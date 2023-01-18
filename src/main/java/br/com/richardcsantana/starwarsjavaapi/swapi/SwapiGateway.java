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

    public Mono<PlanetsResponse> getPlanetPage(Long page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets/").queryParam("page", page).build())
                .retrieve()
                .bodyToMono(PlanetsResponse.class);
    }

    public Mono<PlanetsResponse> getPlanetPage() {
        return getPlanetPage(1L);
    }

    public Mono<Film> getFilm(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/films/{id}").build(id))
                .retrieve()
                .bodyToMono(Film.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getStatusCode().value() == 404 ? Mono.error(new SwapiNotFoundException("Resource Not Found")) : Mono.error(ex));
    }
}
