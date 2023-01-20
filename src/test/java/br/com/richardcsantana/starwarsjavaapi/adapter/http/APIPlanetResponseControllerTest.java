package br.com.richardcsantana.starwarsjavaapi.adapter.http;

import br.com.richardcsantana.starwarsjavaapi.ApplicationTestsBase;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmPlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.PlanetEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository.FilmPlanetRepository;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository.FilmRepository;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository.PlanetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class APIPlanetResponseControllerTest extends ApplicationTestsBase {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private PlanetRepository planetRepository;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private FilmPlanetRepository filmPlanetRepository;

    private WebTestClient testClient;

    @BeforeEach
    public void setUp() {
        serverUrl = mockServer.getEndpoint();
        testClient = WebTestClient.bindToApplicationContext(context).build();
        prepareAPIsCalls();
        prepareDatabase();
    }


    @AfterEach
    public void tearDown() {
        filmPlanetRepository.deleteAll().block();
        filmRepository.deleteAll().block();
        planetRepository.deleteAll().block();
    }

    @Test
    public void getAll() {
        testClient.get()
                .uri("/planets")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[*].name").value(hasItems("Planet1", "Planet2"));
    }

    @Test
    public void searchByName() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets").queryParam("name", "Planet1").build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[*].name").value(hasItems("Planet1"));
    }

    @Test
    public void searchByNameWithMultipleResults() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets").queryParam("name", "Planet").build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[*].name").value(hasItems("Planet1", "Planet2"));
    }

    @Test
    public void searchByNameWithNoPlanetFound() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets").queryParam("name", "Planet3").build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getPlanetById() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets/{id}").build(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.name").isEqualTo("Planet1")
                .jsonPath("$.climate").isEqualTo("testClimate")
                .jsonPath("$.terrain").isEqualTo("Terrain")
                .jsonPath("$.films").isArray()
                .jsonPath("$.films").value(hasSize(2))
                .jsonPath("$.films[*].title").value(hasItems("Film1", "Film2"));
    }

    @Test
    public void getPlanetByIdNotFound() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planets/{id}").build(3L))
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.message").isEqualTo("Planet not found");
    }

    @Test
    public void loadPlanet() {
        testClient.post()
                .uri(uriBuilder -> uriBuilder.path("/planets/{id}").build(3L))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.name").isEqualTo("Yavin IV")
                .jsonPath("$.climate").isEqualTo("temperate, tropical")
                .jsonPath("$.terrain").isEqualTo("jungle, rainforests")
                .jsonPath("$.films").isArray()
                .jsonPath("$.films").value(hasSize(1))
                .jsonPath("$.films[*].title").value(hasItems("Revenge of the Sith"));
    }

    @Test
    public void loadPlanetNotFound() {
        testClient.post()
                .uri(uriBuilder -> uriBuilder.path("/planets/{id}").build(4L))
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.message").isEqualTo("Planet Not Found on the source API");
    }

    @Test
    public void loadPlanetWithAPIError() {
        testClient.post()
                .uri(uriBuilder -> uriBuilder.path("/planets/{id}").build(5L))
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.message").isEqualTo("Internal server error");
    }

    @Test
    public void deletePlanet() {
        Flux<Void> responseBody = testClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/planets/{id}").build(1L))
                .exchange()
                .expectStatus().isNoContent()
                .returnResult(Void.class)
                .getResponseBody();
        StepVerifier.create(responseBody)
                .expectNext()
                .then(() -> {
                    var planet = planetRepository.findByExternalId(1L).block();
                    Assertions.assertNull(planet);
                })
                .verifyComplete();
    }

    @Test
    public void deletePlanetNotFound() {
        testClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/planets/{id}").build(3L))
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.message").isEqualTo("Planet not found");
    }

    private void prepareDatabase() {
        var planet1 = planetRepository.save(
                new PlanetEntity("Planet1", "testClimate", "Terrain", 1L)
        ).block();
        var planet2 = planetRepository.save(
                new PlanetEntity("Planet2", "testClimate2", "Terrain2", 2L)
        ).block();

        var film1 = this.filmRepository.save(
                new FilmEntity("Film1", "Director1", LocalDate.parse("2020-01-01"), 1L)
        ).block();
        var film2 = this.filmRepository.save(
                new FilmEntity("Film2", "Director2", LocalDate.parse("2020-01-02"), 2L)
        ).block();
        filmPlanetRepository.saveAll(
                List.of(
                        new FilmPlanetEntity(film1.getId(), planet1.getId()),
                        new FilmPlanetEntity(film1.getId(), planet2.getId()),
                        new FilmPlanetEntity(film2.getId(), planet1.getId())
                )).blockLast();
    }

    private void prepareAPIsCalls() {
        MockServerClient mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
        mockServerClient.when(
                request().withPath("/planets/3").withMethod("GET")
        ).respond(
                response().withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "name": "Yavin IV",
                                    "rotation_period": "24",
                                    "orbital_period": "4818",
                                    "diameter": "10200",
                                    "climate": "temperate, tropical",
                                    "gravity": "1 standard",
                                    "terrain": "jungle, rainforests",
                                    "surface_water": "8",
                                    "population": "1000",
                                    "residents": [],
                                    "films": [
                                        "api_url/films/3/"
                                    ],
                                    "created": "2014-12-10T11:37:19.144000Z",
                                    "edited": "2014-12-20T20:58:18.421000Z",
                                    "url": "api_url/planets/3/"
                                }
                                """.replaceAll("api_url", serverUrl)));
        mockServerClient.when(
                request().withPath("/films/3").withMethod("GET")
        ).respond(
                response().withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "title": "Revenge of the Sith",
                                    "episode_id": 3,
                                    "opening_crawl": "War! The Republic is crumbling under attacks by the ruthless Sith Lord, Count Dooku. There are heroes on both sides. Evil is everywhere. In a stunning move, the fiendish droid leader, General Grievous, has swept into the Republic capital and kidnapped Chancellor Palpatine, leader of the Galactic Senate. As the Separatist Droid Army attempts to flee the besieged capital with their valuable hostage, two Jedi Knights lead a desperate mission to rescue the captive Chancellor...",
                                    "director": "George Lucas",
                                    "producer": "Rick McCallum",
                                    "release_date": "2005-05-19",
                                    "characters": [
                                        "api_url/people/1/",
                                        "api_url/people/2/"
                                    ],
                                    "planets": [
                                        "api_url/planets/3/"
                                    ],
                                    "starships": [
                                        "api_url/starships/48/",
                                        "api_url/starships/59/",
                                        "api_url/starships/61/"
                                    ],
                                    "vehicles": [
                                        "api_url/vehicles/33/",
                                        "api_url/vehicles/50/"
                                    ],
                                    "species": [
                                        "api_url/species/32/",
                                        "api_url/species/33/",
                                        "api_url/species/2/"
                                    ],
                                    "created": "2014-12-20T18:49:38.403000Z",
                                    "edited": "2014-12-20T20:47:52.073000Z",
                                    "url": "api_url/films/3/"
                                    }
                                """)
        );
        mockServerClient.when(
                request().withPath("/planets/5").withMethod("GET")
        ).respond(
                response().withStatusCode(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "detail": "Internal server error"
                                }
                                """));
    }
}