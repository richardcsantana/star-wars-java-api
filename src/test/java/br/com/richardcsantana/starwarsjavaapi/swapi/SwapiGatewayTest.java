package br.com.richardcsantana.starwarsjavaapi.swapi;

import br.com.richardcsantana.starwarsjavaapi.helper.MockServerHelper;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {SwapiGatewayTest.Initializer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwapiGatewayTest {

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName
            .parse("mockserver/mockserver:mockserver-5.14.0");

    @Rule
    public static MockServerContainer mockServer = new MockServerContainer(MOCKSERVER_IMAGE);
    private static String serverUrl;
    @Autowired
    private SwapiGateway swapiGateway;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            mockServer.start();
            mockServer.getEndpoint();
            serverUrl = mockServer.getEndpoint();
            TestPropertyValues.of(
                    "swapi.url=" + serverUrl
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

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
        var planets = swapiGateway.getPage();
        StepVerifier
                .create(planets)
                .expectNextMatches(planetsResponse ->
                        planetsResponse.next().equals(String.format("%s/planets/?page=2", serverUrl))
                        && planetsResponse.previous() == null
                        && planetsResponse.count() == 2
                        && planetsResponse.results().size() == 1)
                .verifyComplete();
    }

    @Test
    void getSecondPage() {
        var planets = swapiGateway.getPage(2);
        StepVerifier
                .create(planets)
                .expectNextMatches(planetsResponse ->
                        planetsResponse.next() == null
                        && planetsResponse.previous().equals(String.format("%s/planets/?page=1", serverUrl))
                        && planetsResponse.count() == 2
                        && planetsResponse.results().size() == 1)
                .verifyComplete();
    }

    @Test
    void getFilmById() {
        var film = swapiGateway.getFilm(1);
        StepVerifier
                .create(film)
                .expectNextMatches(filmResponse ->
                        filmResponse.title().equals("A New Hope")
                        && filmResponse.director().equals("George Lucas")
                        && filmResponse.release_date().equals(LocalDate.parse("1977-05-25")))
                .verifyComplete();
    }

    @Test
    void getFilmByIdNotFound() {
        var film = swapiGateway.getFilm(404);
        StepVerifier
                .create(film)
                .expectErrorMatches(throwable ->
                        throwable.getClass().equals(SwapiNotFoundException.class) &&
                        throwable.getMessage().equals("Resource Not Found"))
                .verify();
    }
}