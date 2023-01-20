package br.com.richardcsantana.starwarsjavaapi.application;

import br.com.richardcsantana.starwarsjavaapi.adapter.http.dto.PlanetResponse;
import br.com.richardcsantana.starwarsjavaapi.application.errors.ResourceNotFoundException;
import br.com.richardcsantana.starwarsjavaapi.gateway.api.SwapiGateway;
import br.com.richardcsantana.starwarsjavaapi.gateway.api.dto.APIPlanetResponse;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmPlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.PlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository.FilmPlanetRepository;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository.FilmRepository;
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
    private final FilmRepository filmRepository;

    public PlanetService(PlanetRepository planetRepository, FilmService filmService, FilmPlanetRepository filmPlanetRepository, SwapiGateway swapiGateway, FilmRepository filmRepository) {
        this.planetRepository = planetRepository;
        this.filmService = filmService;
        this.filmPlanetRepository = filmPlanetRepository;
        this.swapiGateway = swapiGateway;
        this.filmRepository = filmRepository;
    }

    @Transactional
    public Mono<PlanetEntity> savePlanet(APIPlanetResponse APIPlanetResponse) {
        return planetRepository.save(PlanetEntity.fromPlanet(APIPlanetResponse))
                .flatMap(planetEntity -> Flux.fromIterable(APIPlanetResponse.getFilms())
                        .flatMap(filmService::getOrLoad)
                        .flatMap(filmEntity -> filmPlanetRepository.save(new FilmPlanetEntity(filmEntity.getId(), planetEntity.getId())))
                        .then(Mono.just(planetEntity)));
    }

    @Transactional
    public Mono<PlanetResponse> loadPlanetById(Long id) {
        return this.planetRepository.findByExternalId(id)
                .switchIfEmpty(
                        this.swapiGateway.getPlanet(id)
                                .flatMap(this::savePlanet)
                ).flatMap(this::fullfilPlanetResponse);
    }

    public Flux<PlanetResponse> getAll(String name) {
        Flux<PlanetEntity> result;
        if (name != null) {
            result = this.planetRepository.findByNameContainingIgnoreCase(name);
        } else {
            result = this.planetRepository.findAll();
        }
        return result.flatMap(this::fullfilPlanetResponse);
    }

    private Mono<PlanetResponse> fullfilPlanetResponse(PlanetEntity planet) {
        return Mono.just(planet)
                .flatMap(planetEntity ->
                        filmRepository.findAllByPlanetExternalId(planetEntity.getExternalId())
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
