package br.com.richardcsantana.starwarsjavaapi.domain;

import br.com.richardcsantana.starwarsjavaapi.swapi.SwapiGateway;
import br.com.richardcsantana.starwarsjavaapi.swapi.model.Planet;
import br.com.richardcsantana.starwarsjavaapi.swapi.model.PlanetsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
class ApiReaderServiceTest {

    @MockBean
    private SwapiGateway swapiGateway;

    @BeforeEach
    void setUp() {
        when(swapiGateway.getPage(1))
                .thenReturn(
                        Mono.just(
                                new PlanetsResponse(
                                        2,
                                        "https://api_url/planets/?page=2",
                                        null,
                                        List.of(new Planet("Tatooine", "arid", "desert", emptyList()))
                                )
                        )
                );
        when(swapiGateway.getPage(2))
                .thenReturn(
                        Mono.just(
                                new PlanetsResponse(
                                        2,
                                        null,
                                        "https://api_url/planets/?page=1",
                                        List.of(new Planet("Alderaan", "temperate", "grasslands, mountains", emptyList()))
                                )
                        )
                );
    }

    @Test
    void shouldConsumeContentUntilLastPage() {
        //given
        var apiReaderService = new ApiReaderService(swapiGateway);
        //when
        Flux<Planet> planetResponses = apiReaderService.consumePlanets();
        //then
        StepVerifier.create(planetResponses)
                .expectNextMatches(planet -> planet.name().equals("Tatooine"))
                .expectNextMatches(planet -> planet.name().equals("Alderaan"))
                .verifyComplete();
    }

}