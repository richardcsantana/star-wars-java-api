package br.com.richardcsantana.starwarsjavaapi.domain;

import br.com.richardcsantana.starwarsjavaapi.ApplicationTestsBase;
import br.com.richardcsantana.starwarsjavaapi.api.model.repository.FilmPlanetRepository;
import br.com.richardcsantana.starwarsjavaapi.api.model.repository.FilmRepository;
import br.com.richardcsantana.starwarsjavaapi.api.model.repository.PlanetRepository;
import br.com.richardcsantana.starwarsjavaapi.swapi.SwapiGateway;
import br.com.richardcsantana.starwarsjavaapi.swapi.model.Film;
import br.com.richardcsantana.starwarsjavaapi.swapi.model.Planet;
import br.com.richardcsantana.starwarsjavaapi.swapi.model.PlanetsResponse;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
class ApiReaderServiceTest extends ApplicationTestsBase {

    @MockBean
    private SwapiGateway swapiGateway;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private PlanetRepository planetRepository;
    @Autowired
    private FilmPlanetRepository filmPlanetRepository;
    @Autowired
    private ApiReaderService apiReaderService;

    @BeforeEach
    void setUp() {
        when(swapiGateway.getPlanetPage(1L))
                .thenReturn(
                        Mono.just(
                                new PlanetsResponse(
                                        2,
                                        "https://api_url/planets/?page=2",
                                        null,
                                        List.of(
                                                new Planet(
                                                        "http://api_url/planets/1",
                                                        "Tatooine",
                                                        "arid",
                                                        "desert",
                                                        List.of(
                                                                "http://api_url/films/1",
                                                                "http://api_url/films/3"))))));
        when(swapiGateway.getPlanetPage(2L))
                .thenReturn(
                        Mono.just(
                                new PlanetsResponse(
                                        2,
                                        null,
                                        "https://api_url/planets/?page=1",
                                        List.of(
                                                new Planet(
                                                        "http://api_url/planets/2",
                                                        "Alderaan",
                                                        "temperate",
                                                        "grasslands, mountains",
                                                        List.of(
                                                                "http://api_url/films/1",
                                                                "http://api_url/films/2"))))));
        when(swapiGateway.getFilm(1L))
                .thenReturn(
                        Mono.just(
                                new Film(
                                        "http://api_url/films/1",
                                        "A New Hope",
                                        "George Lucas",
                                        LocalDate.parse("1977-05-25"))));
        when(swapiGateway.getFilm(2L))
                .thenReturn(
                        Mono.just(
                                new Film(
                                        "http://api_url/films/2",
                                        "The Empire Strikes Back",
                                        "Irvin Kershner",
                                        LocalDate.parse("1980-05-17"))));
        when(swapiGateway.getFilm(3L))
                .thenReturn(
                        Mono.just(
                                new Film(
                                        "http://api_url/films/3",
                                        "Return of the Jedi",
                                        "Richard Marquand",
                                        LocalDate.parse("1983-05-25"))));
    }

    @Test
    void shouldConsumeContentUntilLastPage() {
        Flux<Planet> planetResponses = apiReaderService.consumePlanets();

        StepVerifier.create(planetResponses)
                .expectNextMatches(planet -> planet.getName().equals("Tatooine"))
                .expectNextMatches(planet -> planet.getName().equals("Alderaan"))
                .verifyComplete();
    }

    @Test
    void shouldConsumeApi() {
        Mono<Void> consumeApi = apiReaderService.consumeAPI();

        StepVerifier.create(consumeApi)
                .verifyComplete();
        Assertions.assertEquals(3L, filmRepository.count().block());
        Assertions.assertEquals(2L, planetRepository.count().block());
        Assertions.assertEquals(4L, filmPlanetRepository.count().block());
    }

}