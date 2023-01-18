package br.com.richardcsantana.starwarsjavaapi.domain;

import br.com.richardcsantana.starwarsjavaapi.application.FilmService;
import br.com.richardcsantana.starwarsjavaapi.application.PlanetService;
import br.com.richardcsantana.starwarsjavaapi.swapi.SwapiGateway;
import br.com.richardcsantana.starwarsjavaapi.swapi.model.Planet;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetrySpec;

import java.time.Duration;
import java.util.logging.Level;

@Service
public class ApiReaderService {

    private final SwapiGateway swapiGateway;
    private final PlanetService planetService;
    private final FilmService filmService;

    public ApiReaderService(SwapiGateway swapiGateway, PlanetService planetService, FilmService filmService) {
        this.swapiGateway = swapiGateway;
        this.planetService = planetService;
        this.filmService = filmService;
    }

    public Flux<Planet> consumePlanets() {
        return consumePlanets(1L);
    }

    public Flux<Planet> consumePlanets(Long page) {
        return swapiGateway.getPlanetPage(page).flatMapMany(it ->
        {
            var planets = it.getResults();
            var nextPage = it.getNext();
            if (nextPage == null) {
                return Flux.fromIterable(planets);
            } else {
                return Flux.fromIterable(planets).concatWith(consumePlanets(nextPage));
            }
        });
    }

    public Mono<Void> consumeAPI() {
        return consumePlanets().flatMap(planetService::savePlanet).retryWhen(RetrySpec.fixedDelay(3, Duration.ofSeconds(2))).log("category", Level.INFO).then();
    }
}
