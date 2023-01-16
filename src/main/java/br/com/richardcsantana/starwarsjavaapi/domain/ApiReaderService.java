package br.com.richardcsantana.starwarsjavaapi.domain;

import br.com.richardcsantana.starwarsjavaapi.swapi.SwapiGateway;
import br.com.richardcsantana.starwarsjavaapi.swapi.model.Planet;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ApiReaderService {

    private final SwapiGateway swapiGateway;

    public ApiReaderService(SwapiGateway swapiGateway) {
        this.swapiGateway = swapiGateway;
    }

    public Flux<Planet> consumePlanets() {
        return consumePlanets("1");
    }

    public Flux<Planet> consumePlanets(String page) {
        return swapiGateway.getPage(Integer.valueOf(page)).flatMapMany(it ->
        {
            var planets = it.results();
            var nextPage = it.next();
            if (nextPage == null) {
                return Flux.fromIterable(planets);
            } else {
                return Flux.fromIterable(planets).concatWith(consumePlanets(nextPage.split("\\?page=")[1]));
            }
        });
    }
}
