package br.com.richardcsantana.starwarsjavaapi.swapi;

import br.com.richardcsantana.starwarsjavaapi.swapi.model.PlanetsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SwapiGateway {
    private final WebClient webClient;

    public SwapiGateway(WebClient webClient) {
        this.webClient = webClient;
    }

    // TODO Remodel page model
    public Mono<PlanetsResponse> getPage(Integer page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets/").queryParam("page", page).build())
                .retrieve()
                .bodyToMono(PlanetsResponse.class);
    }

    public Mono<PlanetsResponse> getPage() {
        return getPage(1);
    }
}
