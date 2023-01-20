package br.com.richardcsantana.starwarsjavaapi.gateway.api;

import br.com.richardcsantana.starwarsjavaapi.gateway.api.dto.APIFilmResponse;
import br.com.richardcsantana.starwarsjavaapi.gateway.api.dto.APIPlanetResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SwapiGateway {
    private static final Logger logger = Logger.getLogger(SwapiGateway.class.getName());
    private final WebClient webClient;

    public SwapiGateway(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<APIFilmResponse> getFilm(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/films/{id}").build(id))
                .retrieve()
                .bodyToMono(APIFilmResponse.class)
                .doOnSuccess(film -> logger.log(Level.FINE, "Film loaded: " + film))
                .doOnError(ex -> logger.log(Level.SEVERE, "Error loading film with id: %d with message: %s".formatted(id, ex.getMessage()), ex));
    }

    public Mono<APIPlanetResponse> getPlanet(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets/{id}").build(id))
                .retrieve()
                .bodyToMono(APIPlanetResponse.class)
                .doOnSuccess(planet -> logger.log(Level.FINE, "Planet loaded: " + planet))
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            logger.log(Level.SEVERE, "Error loading planet with id: %d with message: %s".formatted(id, ex.getMessage()), ex);
                            return ex.getStatusCode().value() == 404 ?
                                    Mono.error(new SwapiNotFoundException("Planet Not Found on the source API")) :
                                    Mono.error(new SwapiException("We can't load your request now, please try again later"));
                        });
    }
}
