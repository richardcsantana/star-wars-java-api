package br.com.richardcsantana.starwarsjavaapi.swapi;

import br.com.richardcsantana.starwarsjavaapi.ApplicationTestsBase;
import br.com.richardcsantana.starwarsjavaapi.batch.swapi.SwapiGateway;
import br.com.richardcsantana.starwarsjavaapi.batch.swapi.SwapiNotFoundException;
import br.com.richardcsantana.starwarsjavaapi.helper.MockServerHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwapiGatewayTest extends ApplicationTestsBase {

    @Autowired
    private SwapiGateway swapiGateway;

    @BeforeEach
    public void setUp() {
        serverUrl = mockServer.getEndpoint();
        new MockServerHelper()
                .prepareMockServer(
                        mockServer.getEndpoint(),
                        new MockServerClient(mockServer.getHost(), mockServer.getServerPort()));
    }

    @Test
    void getFirstPage() {
        var planets = swapiGateway.getPlanetPage();
        StepVerifier
                .create(planets)
                .expectNextMatches(planetsResponse ->
                        planetsResponse.getNext().equals(2L)
                        && planetsResponse.getPrevious() == null
                        && planetsResponse.getCount() == 2
                        && planetsResponse.getResults().size() == 1)
                .verifyComplete();
    }

    @Test
    void getSecondPage() {
        var planets = swapiGateway.getPlanetPage(2L);
        StepVerifier
                .create(planets)
                .expectNextMatches(planetsResponse ->
                        planetsResponse.getNext() == null
                        && planetsResponse.getPrevious().equals(1L)
                        && planetsResponse.getCount() == 2
                        && planetsResponse.getResults().size() == 1)
                .verifyComplete();
    }

    @Test
    void getFilmById() {
        var film = swapiGateway.getFilm(1L);
        StepVerifier
                .create(film)
                .expectNextMatches(filmResponse ->
                        filmResponse.getTitle().equals("A New Hope")
                        && filmResponse.getDirector().equals("George Lucas")
                        && filmResponse.getReleaseDate().equals(LocalDate.parse("1977-05-25")))
                .verifyComplete();
    }

}