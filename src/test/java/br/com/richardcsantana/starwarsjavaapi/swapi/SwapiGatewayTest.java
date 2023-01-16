package br.com.richardcsantana.starwarsjavaapi.swapi;

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

import static org.mockserver.model.HttpRequest.request;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {SwapiGatewayTest.Initializer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwapiGatewayTest {

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName.parse("mockserver/mockserver:mockserver-5.14.0");

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
        var mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
        var responsePlanet = """
                {
                    "count": 2,
                    "next": "api_url/planets/?page=2",
                    "previous": null,
                    "results": [
                        {
                            "name": "Tatooine",
                            "rotation_period": "23",
                            "orbital_period": "304",
                            "diameter": "10465",
                            "climate": "arid",
                            "gravity": "1 standard",
                            "terrain": "desert",
                            "surface_water": "1",
                            "population": "200000",
                            "residents": [
                                "api_url/people/1/",
                                "api_url/people/2/",
                                "api_url/people/4/",
                                "api_url/people/6/",
                                "api_url/people/7/",
                                "api_url/people/8/",
                                "api_url/people/9/",
                                "api_url/people/11/",
                                "api_url/people/43/",
                                "api_url/people/62/"
                            ],
                            "films": [
                                "api_url/films/1/",
                                "api_url/films/3/",
                                "api_url/films/4/",
                                "api_url/films/5/",
                                "api_url/films/6/"
                            ],
                            "created": "2014-12-09T13:50:49.641000Z",
                            "edited": "2014-12-20T20:58:18.411000Z",
                            "url": "api_url/planets/1/"
                        }
                    ]
                }""".replaceAll("api_url", serverUrl);
        mockServerClient.when(request().withPath("/planets.*").withQueryStringParameter("page", "1").withMethod("GET"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(responsePlanet)
                        .withHeader("Content-Type", "application/json"));
        var responsePlanetPage2 = """
                {
                    "count": 2,
                    "next": null,
                    "previous": "api_url/planets/?page=1",
                    "results": [
                        {
                            "name": "Alderaan",
                            "rotation_period": "23",
                            "orbital_period": "304",
                            "diameter": "10465",
                            "climate": "temperate",
                            "gravity": "1 standard",
                            "terrain": "grasslands, mountains",
                            "surface_water": "1",
                            "population": "200000",
                            "residents": [
                                "api_url/people/1/",
                                "api_url/people/2/",
                                "api_url/people/4/",
                                "api_url/people/6/",
                                "api_url/people/7/",
                                "api_url/people/8/",
                                "api_url/people/9/",
                                "api_url/people/11/",
                                "api_url/people/43/",
                                "api_url/people/62/"
                            ],
                            "films": [
                                "api_url/films/1/",
                                "api_url/films/3/",
                                "api_url/films/4/",
                                "api_url/films/5/",
                                "api_url/films/6/"
                            ],
                            "created": "2014-12-09T13:50:49.641000Z",
                            "edited": "2014-12-20T20:58:18.411000Z",
                            "url": "api_url/planets/2/"
                        }
                    ]
                }""".replaceAll("api_url", serverUrl);
        mockServerClient.when(request().withPath("/planets.*").withMethod("GET").withQueryStringParameter("page", "2"))
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(responsePlanetPage2)
                        .withHeader("Content-Type", "application/json"));
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
}