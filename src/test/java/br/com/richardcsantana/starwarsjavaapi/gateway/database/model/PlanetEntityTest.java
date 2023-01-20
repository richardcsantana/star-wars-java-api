package br.com.richardcsantana.starwarsjavaapi.gateway.database.model;

import br.com.richardcsantana.starwarsjavaapi.gateway.api.dto.APIPlanetResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlanetEntityTest {

    @Test
    void fromPlanet() {
        var planet = PlanetEntity.fromPlanet(
                new APIPlanetResponse(
                        "https://api_url/planets/1/",
                        "Tatooine",
                        "arid",
                        "desert",
                        List.of("https://api_url/films/1/", "https://api_url/films/3/")
                )
        );

        assertEquals(1L, planet.getExternalId());
        assertEquals("Tatooine", planet.getName());
        assertEquals("arid", planet.getClimate());
        assertEquals("desert", planet.getTerrain());
    }
}