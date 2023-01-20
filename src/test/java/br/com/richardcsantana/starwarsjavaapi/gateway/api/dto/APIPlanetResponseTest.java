package br.com.richardcsantana.starwarsjavaapi.gateway.api.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class APIPlanetResponseTest {

    @Test
    void shouldCreatePlanet() {
        var planet = new APIPlanetResponse(
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

    @Test
    void shouldThrowErrorIfFilmURLisInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new APIPlanetResponse(
                "https://api_url/planets/1/",
                "Tatooine",
                "arid",
                "desert",
                List.of("https://api_url", "https://api_url/")
        ));
    }

    @Test
    void shouldThrowErrorIfPlanetURLisInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new APIPlanetResponse(
                "https://api_url",
                "Tatooine",
                "arid",
                "desert",
                List.of("https://api_url/films/1/", "https://api_url/films/3/")
        ));
    }
}