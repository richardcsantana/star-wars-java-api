package br.com.richardcsantana.starwarsjavaapi.application;

import br.com.richardcsantana.starwarsjavaapi.api.model.FilmEntity;
import br.com.richardcsantana.starwarsjavaapi.api.model.FilmPlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.api.model.PlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.api.model.repository.FilmPlanetRepository;
import br.com.richardcsantana.starwarsjavaapi.api.model.repository.PlanetRepository;
import br.com.richardcsantana.starwarsjavaapi.swapi.model.Planet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlanetService {
    private final PlanetRepository planetRepository;
    private final FilmService filmService;
    private final FilmPlanetRepository filmPlanetRepository;

    public PlanetService(PlanetRepository planetRepository, FilmService filmService, FilmPlanetRepository filmPlanetRepository) {
        this.planetRepository = planetRepository;
        this.filmService = filmService;
        this.filmPlanetRepository = filmPlanetRepository;
    }

    @Transactional
    public Mono<PlanetEntity> savePlanet(Planet planet) {
        Flux<FilmEntity> filmEntityFlux = Flux.fromIterable(planet.getFilms()).flatMap(filmService::getOrSave);
        return this.planetRepository.findByExternalId(planet.getId())
                .switchIfEmpty(
                        this.planetRepository.save(new PlanetEntity(planet.getName(), planet.getClimate(), planet.getTerrain(), planet.getId()))
                                .flatMap(planetEntity -> createRelationBetweenFilmsAndPlanets(filmEntityFlux, planetEntity).then(Mono.just(planetEntity)))
                );
    }

    private Flux<FilmPlanetEntity> createRelationBetweenFilmsAndPlanets(Flux<FilmEntity> filmEntityFlux, PlanetEntity planetEntity) {
        return filmEntityFlux.flatMap(
                filmEntity ->
                        filmPlanetRepository.save(new FilmPlanetEntity(filmEntity.getId(), planetEntity.getId())));
    }
}
