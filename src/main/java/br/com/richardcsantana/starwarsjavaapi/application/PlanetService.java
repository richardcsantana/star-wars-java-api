package br.com.richardcsantana.starwarsjavaapi.application;

import br.com.richardcsantana.starwarsjavaapi.adapter.http.dto.PlanetResponse;
import br.com.richardcsantana.starwarsjavaapi.application.errors.ResourceNotFoundException;
import br.com.richardcsantana.starwarsjavaapi.gateway.api.SwapiGateway;
import br.com.richardcsantana.starwarsjavaapi.gateway.api.dto.APIPlanetResponse;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmPlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.PlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository.FilmPlanetRepository;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository.PlanetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlanetService {
    private final PlanetRepository planetRepository;
    private final FilmService filmService;
    private final FilmPlanetRepository filmPlanetRepository;
    private final SwapiGateway swapiGateway;

    public PlanetService(PlanetRepository planetRepository,
                         FilmService filmService,
                         FilmPlanetRepository filmPlanetRepository,
                         SwapiGateway swapiGateway) {
        this.planetRepository = planetRepository;
        this.filmService = filmService;
        this.filmPlanetRepository = filmPlanetRepository;
        this.swapiGateway = swapiGateway;
    }

    @Transactional
    public Mono<PlanetEntity> savePlanet(APIPlanetResponse planetResponse) {
        return planetRepository.save(PlanetEntity.fromPlanet(planetResponse))
                .flatMap(planetEntity -> Flux.fromIterable(planetResponse.getFilms())
                        .flatMap(filmService::getOrLoad)
                        .flatMap(filmEntity ->
                                filmPlanetRepository.save(new FilmPlanetEntity(filmEntity.getId(), planetEntity.getId())))
                        .then(Mono.just(planetEntity)));
    }

    public Mono<PlanetResponse> loadPlanetById(Long id) {
        return this.planetRepository.findByExternalId(id)
                .switchIfEmpty(
                        this.swapiGateway.getPlanet(id)
                                .flatMap(this::savePlanet)
                ).flatMap(this::fullfilPlanetResponse);
    }

    public Flux<PlanetResponse> getPlanets(String name) {
        Flux<PlanetEntity> result;
        if (name == null) {
            result = this.planetRepository.findAll();
        } else {
            result = this.planetRepository.findByNameContainingIgnoreCase(name);
        }
        return result.flatMap(this::fullfilPlanetResponse);
    }

    private Mono<PlanetResponse> fullfilPlanetResponse(PlanetEntity planet) {
        return Mono.just(planet)
                .flatMap(planetEntity ->
                        filmService.findAllByPlanetId(planetEntity.getId())
                                .collectList()
                                .map(filmEntities ->
                                        PlanetResponse.fromPlanetEntity(planetEntity, filmEntities)));
    }

    public Mono<PlanetResponse> getPlanetById(Long id) {
        return this.planetRepository.findByExternalId(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Planet not found")))
                .flatMap(this::fullfilPlanetResponse);
    }

    public Mono<Void> deletePlanet(Long id) {
        return this.planetRepository.findByExternalId(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Planet not found")))
                .flatMap(planetEntity ->
                        filmPlanetRepository.deleteAllByPlanetId(planetEntity.getId())
                                .then(planetRepository.delete(planetEntity)));

    }
}
