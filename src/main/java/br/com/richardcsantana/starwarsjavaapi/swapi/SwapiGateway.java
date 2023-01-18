package br.com.richardcsantana.starwarsjavaapi.swapi;

import br.com.richardcsantana.starwarsjavaapi.swapi.model.Film;
import br.com.richardcsantana.starwarsjavaapi.swapi.model.PlanetsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class SwapiGateway {
    private final WebClient webClient;

    public SwapiGateway(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<PlanetsResponse> getPage(Integer page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets/").queryParam("page", page).build())
                .retrieve()
                .bodyToMono(PlanetsResponse.class);
    }

    public Mono<PlanetsResponse> getPage() {
        return getPage(1);
    }

    public Mono<Film> getFilm(int id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/films/{id}").build(id))
                .retrieve()
                .bodyToMono(Film.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getStatusCode().value() == 404 ? Mono.error(new SwapiNotFoundException("Resource Not Found")) : Mono.error(ex));
    }
}
