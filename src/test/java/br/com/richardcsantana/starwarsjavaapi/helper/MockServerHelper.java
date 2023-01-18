package br.com.richardcsantana.starwarsjavaapi.helper;

import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;

import static org.mockserver.model.HttpRequest.request;

public class MockServerHelper {

    public static final String PLANETS_PAGE_1 = """
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
            }""";

    public static final String PLANETS_PAGE_2 = """
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
                            "api_url/films/2/"
                        ],
                        "created": "2014-12-09T13:50:49.641000Z",
                        "edited": "2014-12-20T20:58:18.411000Z",
                        "url": "api_url/planets/2/"
                    }
                ]
            }""";
    public static final String FILM_1 = """
            {
                "title": "A New Hope",
                "episode_id": 4,
                "opening_crawl": "It is a period of civil war.",
                "director": "George Lucas",
                "producer": "Gary Kurtz, Rick McCallum",
                "release_date": "1977-05-25",
                "characters": [
                    "api_url/people/1/",
                    "api_url/people/2/",
                    "api_url/people/3/",
                    "api_url/people/4/",
                    "api_url/people/5/",
                    "api_url/people/6/",
                    "api_url/people/7/",
                    "api_url/people/8/",
                    "api_url/people/9/",
                    "api_url/people/10/",
                    "api_url/people/12/",
                    "api_url/people/13/",
                    "api_url/people/14/",
                    "api_url/people/15/",
                    "api_url/people/16/",
                    "api_url/people/18/",
                    "api_url/people/19/",
                    "api_url/people/81/"
                ],
                "planets": [
                    "api_url/planets/2/",
                    "api_url/planets/3/",
                    "api_url/planets/1/"
                ],
                "starships": [
                    "api_url/starships/2/",
                    "api_url/starships/3/",
                    "api_url/starships/5/",
                    "api_url/starships/9/",
                    "api_url/starships/10/",
                    "api_url/starships/11/",
                    "api_url/starships/12/",
                    "api_url/starships/13/"
                ],
                "vehicles": [
                    "api_url/vehicles/4/",
                    "api_url/vehicles/6/",
                    "api_url/vehicles/7/",
                    "api_url/vehicles/8/"
                ],
                "species": [
                    "api_url/species/5/",
                    "api_url/species/3/",
                    "api_url/species/2/",
                    "api_url/species/1/",
                    "api_url/species/4/"
                ],
                "created": "2014-12-10T14:23:31.880000Z",
                "edited": "2015-04-11T09:46:52.774897Z",
                "url": "api_url/films/1/"
            }""";

    public void prepareMockServer(String serverUrl, MockServerClient mockServerClient) {
        createSwapiResponse(PLANETS_PAGE_1, serverUrl, mockServerClient, request().withPath("/planets.*").withMethod("GET").withQueryStringParameter("page", "1"));
        createSwapiResponse(PLANETS_PAGE_2, serverUrl, mockServerClient, request().withPath("/planets.*").withMethod("GET").withQueryStringParameter("page", "2"));
        createSwapiResponse(FILM_1, serverUrl, mockServerClient, request().withPath("/films/{id}").withPathParameter("id", "1").withMethod("GET"));
    }

    private void createSwapiResponse(String response, String serverUrl, MockServerClient mockServerClient, HttpRequest requestDefinition) {
        var responseWithCorrectURI = response.replaceAll("api_url", serverUrl);
        mockServerClient.when(requestDefinition)
                .respond(org.mockserver.model.HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(responseWithCorrectURI)
                        .withHeader("Content-Type", "application/json"));
    }
}
