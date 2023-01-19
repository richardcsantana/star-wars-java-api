package br.com.richardcsantana.starwarsjavaapi.swapi.model;

import br.com.richardcsantana.starwarsjavaapi.batch.swapi.model.Planet;
import br.com.richardcsantana.starwarsjavaapi.batch.swapi.model.PlanetsResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlanetsResponseTest {

    @Test
    void shouldCreatePlanetsResponse() {
        var planetsResponse = new PlanetsResponse(
                1,
                "https://api_url/planets/?page=3",
                "https://api_url/planets/?page=1",
                List.of(
                        new Planet(
                                "http://api_url/planets/1",
                                "Tatooine",
                                "arid",
                                "desert",
                                List.of("https://api_url/films/1/", "https://api_url/films/3/")
                        )
                )
        );

        assertEquals(1, planetsResponse.getCount());
        assertEquals(3, planetsResponse.getNext());
        assertEquals(1, planetsResponse.getPrevious());
        assertEquals(1, planetsResponse.getResults().size());
    }

    @Test
    void shouldCreatePlanetsResponseWithNextNull() {
        var planetsResponse = new PlanetsResponse(
                1,
                null,
                "https://api_url/planets/?page=1",
                List.of(
                        new Planet(
                                "http://api_url/planets/1",
                                "Tatooine",
                                "arid",
                                "desert",
                                List.of("https://api_url/films/1/", "https://api_url/films/3/")
                        )
                )
        );

        assertEquals(1, planetsResponse.getCount());
        assertNull(planetsResponse.getNext());
        assertEquals(1, planetsResponse.getPrevious());
        assertEquals(1, planetsResponse.getResults().size());
    }

    @Test
    void shouldCreatePlanetsResponseWithPreviousNull() {
        var planetsResponse = new PlanetsResponse(
                1,
                "https://api_url/planets/?page=3",
                null,
                List.of(
                        new Planet(
                                "http://api_url/planets/1",
                                "Tatooine",
                                "arid",
                                "desert",
                                List.of("https://api_url/films/1/", "https://api_url/films/3/")
                        )
                )
        );

        assertEquals(1, planetsResponse.getCount());
        assertEquals(3, planetsResponse.getNext());
        assertNull(planetsResponse.getPrevious());
        assertEquals(1, planetsResponse.getResults().size());
    }

}