package br.com.richardcsantana.starwarsjavaapi.swapi.model;

import br.com.richardcsantana.starwarsjavaapi.batch.swapi.model.Planet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlanetTest {

    @Test
    void shouldCreatePlanet() {
        var planet = new Planet(
                "https://api_url/planets/1/",
                "Tatooine",
                "arid",
                "desert",
                List.of("https://api_url/films/1/", "https://api_url/films/3/")
        );

        assertEquals(1L, planet.getId());
        assertEquals("Tatooine", planet.getName());
        assertEquals("arid", planet.getClimate());
        assertEquals("desert", planet.getTerrain());
        assertEquals(List.of(1L, 3L), planet.getFilms());
    }

}