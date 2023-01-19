package br.com.richardcsantana.starwarsjavaapi.batch.domain;

import br.com.richardcsantana.starwarsjavaapi.batch.swapi.SwapiGateway;
import br.com.richardcsantana.starwarsjavaapi.batch.swapi.model.Planet;
import br.com.richardcsantana.starwarsjavaapi.common.application.PlanetService;
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

    public ApiReaderService(SwapiGateway swapiGateway, PlanetService planetService) {
        this.swapiGateway = swapiGateway;
        this.planetService = planetService;
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
