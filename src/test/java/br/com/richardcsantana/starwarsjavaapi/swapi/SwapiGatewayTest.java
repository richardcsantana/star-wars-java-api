package br.com.richardcsantana.starwarsjavaapi.swapi;

import br.com.richardcsantana.starwarsjavaapi.ApplicationTestsBase;
import br.com.richardcsantana.starwarsjavaapi.batch.swapi.SwapiGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwapiGatewayTest extends ApplicationTestsBase {

    @Autowired
    private SwapiGateway swapiGateway;

    @BeforeEach
    public void setUp() {
        serverUrl = mockServer.getEndpoint();
        var mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
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
    }

    @Test
    void getPlanetById() {
        var planet = swapiGateway.getPlanet(3L);
        StepVerifier
                .create(planet)
                .expectNextMatches(planetResponse ->
                        planetResponse.getName().equals("Yavin IV") &&
                        planetResponse.getClimate().equals("temperate, tropical") &&
                        planetResponse.getTerrain().equals("jungle, rainforests") &&
                        planetResponse.getFilms().size() == 1 &&
                        planetResponse.getFilms().get(0) == 3L
                ).verifyComplete();
    }

    @Test
    void getFilmById() {
        var film = swapiGateway.getFilm(3L);
        StepVerifier
                .create(film)
                .expectNextMatches(filmResponse ->
                        filmResponse.getTitle().equals("Revenge of the Sith") &&
                        filmResponse.getDirector().equals("George Lucas") &&
                        filmResponse.getReleaseDate().equals(LocalDate.parse("2005-05-19")))
                .verifyComplete();

    }

}