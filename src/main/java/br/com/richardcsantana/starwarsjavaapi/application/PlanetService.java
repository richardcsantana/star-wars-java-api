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

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PlanetService {
    private final PlanetRepository planetRepository;
    private final FilmService filmService;
    private final FilmPlanetRepository filmPlanetRepository;
    private final SwapiGateway swapiGateway;

    private final static Logger logger = Logger.getLogger(PlanetService.class.getName());

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
        logger.log(Level.FINE, "Saving planet: " + planetResponse.getName());
        return planetRepository.save(PlanetEntity.fromPlanet(planetResponse))
                .flatMap(planetEntity -> Flux.fromIterable(planetResponse.getFilms())
                        .flatMap(filmService::getOrLoad)
                        .flatMap(filmEntity ->
                                filmPlanetRepository.save(new FilmPlanetEntity(filmEntity.getId(), planetEntity.getId())))
                        .then(Mono.just(planetEntity)))
                .doOnError(e -> logger.log(Level.SEVERE, "Error saving planet: %s".formatted(planetResponse.getName()), e))
                .doOnSuccess(planetEntity -> logger.log(Level.FINE, "Planet saved: %s".formatted(planetEntity.getName())));
    }

    public Mono<PlanetResponse> loadPlanetById(Long id) {
        logger.log(Level.FINE, "Loading planet by id: " + id);
        return this.planetRepository.findByExternalId(id)
                .switchIfEmpty(
                        this.swapiGateway.getPlanet(id)
                                .flatMap(this::savePlanet)
                ).flatMap(this::fullfilPlanetResponse)
                .doOnSuccess(planetResponse -> logger.log(Level.FINE, "Planet loaded by id: %d".formatted(id)))
                .doOnError(e -> logger.log(Level.SEVERE, "Error loading planet by id: %d".formatted(id), e));
    }

    public Flux<PlanetResponse> getPlanetsByName(String name) {
        logger.log(Level.FINE, "Getting planets by name: " + name);
        return this.planetRepository.findByNameContainingIgnoreCase(name)
                .flatMap(this::fullfilPlanetResponse)
                .doOnComplete(() -> logger.log(Level.FINE, "Planets returned by name: %s".formatted(name)));
    }

    public Flux<PlanetResponse> getPlanets() {
        logger.log(Level.FINE, "Getting planets");
        return this.planetRepository.findAll().flatMap(this::fullfilPlanetResponse)
                .doOnComplete(() -> logger.log(Level.FINE, "Planets loaded"));
    }

    private Mono<PlanetResponse> fullfilPlanetResponse(PlanetEntity planet) {
        logger.log(Level.FINE, "Loading films for planet: " + planet.getName());
        return Mono.just(planet)
                .flatMap(planetEntity ->
                        filmService.findAllByPlanetId(planetEntity.getId())
                                .collectList()
                                .map(filmEntities ->
                                        PlanetResponse.fromPlanetEntity(planetEntity, filmEntities)))
                .doOnSuccess(planetResponse -> logger.log(Level.FINE, "%d Films loaded for planet: %s".formatted(planetResponse.films().size(), planet.getName())))
                .doOnError(e -> logger.log(Level.SEVERE, "Error loading films for planet: " + planet.getName(), e));
    }

    public Mono<PlanetResponse> getPlanetById(Long id) {
        logger.log(Level.FINE, "Loading planet by id: " + id);
        return this.planetRepository.findByExternalId(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Planet not found")))
                .flatMap(this::fullfilPlanetResponse)
                .doOnSuccess(planetResponse -> logger.log(Level.FINE, "Planet loaded by id: " + id))
                .doOnError(e -> logger.log(Level.SEVERE, "Error loading planet by id: " + id, e));
    }

    public Mono<Void> deletePlanet(Long id) {
        logger.log(Level.FINE, "Deleting planet by id: " + id);
        return this.planetRepository.findByExternalId(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Planet not found")))
                .flatMap(planetEntity ->
                        filmPlanetRepository.deleteAllByPlanetId(planetEntity.getId())
                                .then(planetRepository.delete(planetEntity)))
                .doOnSuccess(aVoid -> logger.log(Level.FINE, "Planet deleted by id: " + id))
                .doOnError(e -> logger.log(Level.SEVERE, "Error deleting planet by id: " + id, e));

    }
}
