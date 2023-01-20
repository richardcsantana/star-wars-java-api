package br.com.richardcsantana.starwarsjavaapi.gateway.api;

import br.com.richardcsantana.starwarsjavaapi.gateway.api.dto.APIFilmResponse;
import br.com.richardcsantana.starwarsjavaapi.gateway.api.dto.APIPlanetResponse;
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

    public Mono<APIFilmResponse> getFilm(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/films/{id}").build(id))
                .retrieve()
                .bodyToMono(APIFilmResponse.class);
    }

    public Mono<APIPlanetResponse> getPlanet(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets/{id}").build(id))
                .retrieve()
                .bodyToMono(APIPlanetResponse.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getStatusCode().value() == 404 ? Mono.error(new SwapiNotFoundException("Planet Not Found on the source API")) : Mono.error(ex));
    }
}
