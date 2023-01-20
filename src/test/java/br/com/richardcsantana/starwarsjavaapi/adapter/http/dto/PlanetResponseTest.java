package br.com.richardcsantana.starwarsjavaapi.adapter.http.dto;

import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.PlanetEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlanetResponseTest {

    @Test
    void fromPlanetEntity() {
        var planet = PlanetResponse.fromPlanetEntity(
                new PlanetEntity(
                        "Tatooine",
                        "arid",
                        "desert",
                        1L
                ),
                List.of(
                        new FilmEntity("A New Hope", "George Lucas", LocalDate.parse("1977-05-25"), 1L),
                        new FilmEntity("The Empire Strikes Back", "Irvin Kershner", LocalDate.parse("1980-05-17"), 2L)
                )
        );

        assertEquals(1L, planet.id());
        assertEquals("Tatooine", planet.name());
        assertEquals("arid", planet.climate());
        assertEquals("desert", planet.terrain());
        assertEquals(2, planet.films().size());
    }
}