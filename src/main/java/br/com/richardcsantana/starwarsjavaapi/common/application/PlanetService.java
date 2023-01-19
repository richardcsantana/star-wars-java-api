package br.com.richardcsantana.starwarsjavaapi.common.application;

import br.com.richardcsantana.starwarsjavaapi.api.PlanetResponse;
import br.com.richardcsantana.starwarsjavaapi.batch.swapi.SwapiGateway;
import br.com.richardcsantana.starwarsjavaapi.batch.swapi.model.Planet;
import br.com.richardcsantana.starwarsjavaapi.common.application.errors.ResourceNotFoundException;
import br.com.richardcsantana.starwarsjavaapi.common.model.FilmEntity;
import br.com.richardcsantana.starwarsjavaapi.common.model.FilmPlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.common.model.PlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.common.model.repository.FilmPlanetRepository;
import br.com.richardcsantana.starwarsjavaapi.common.model.repository.FilmRepository;
import br.com.richardcsantana.starwarsjavaapi.common.model.repository.PlanetRepository;
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
    public Mono<PlanetEntity> savePlanet(Planet planet) {
        Flux<FilmEntity> filmEntityFlux = Flux.fromIterable(planet.getFilms()).flatMap(filmService::getOrSave);
        return this.planetRepository.findByExternalId(planet.getId())
                .switchIfEmpty(
                        this.planetRepository.save(new PlanetEntity(planet.getName(), planet.getClimate(), planet.getTerrain(), planet.getId()))
                                .flatMap(planetEntity ->
                                        createRelationBetweenFilmsAndPlanets(filmEntityFlux, planetEntity).then(Mono.just(planetEntity)))
                );
    }

    @Transactional
    public Mono<PlanetResponse> loadPlanetById(Long id) {
        return this.planetRepository.findByExternalId(id)
                .switchIfEmpty(
                        this.swapiGateway.getPlanet(id)
                                .flatMap(this::savePlanet)
                ).flatMap(this::fullfilPlanetResponse);
    }

    private Flux<FilmPlanetEntity> createRelationBetweenFilmsAndPlanets(Flux<FilmEntity> filmEntityFlux, PlanetEntity planetEntity) {
        return filmEntityFlux.flatMap(
                filmEntity ->
                        filmPlanetRepository.save(new FilmPlanetEntity(filmEntity.getId(), planetEntity.getId())));
    }

    public Flux<PlanetResponse> getAll() {
        return this.planetRepository.findAll()
                .flatMap(this::fullfilPlanetResponse);
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
        return this.filmPlanetRepository.deleteAllByPlanetExternalId(id)
                .then(this.planetRepository.deleteByExternalId(id));
    }
}
