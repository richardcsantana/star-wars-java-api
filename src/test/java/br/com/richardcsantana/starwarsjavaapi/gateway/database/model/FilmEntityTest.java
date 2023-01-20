package br.com.richardcsantana.starwarsjavaapi.gateway.database.model;

import br.com.richardcsantana.starwarsjavaapi.gateway.api.dto.APIFilmResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmEntityTest {

    @Test
    void fromFilmResponse() {
        var film = FilmEntity.fromFilmResponse(
                new APIFilmResponse(
                        "https://api_url/films/1/",
                        "A New Hope",
                        "George Lucas",
                        java.time.LocalDate.parse("1977-05-25")
                )
        );

        assertEquals(1L, film.getExternalId());
        assertEquals("A New Hope", film.getTitle());
        assertEquals("George Lucas", film.getDirector());
        assertEquals(LocalDate.parse("1977-05-25"), film.getReleaseDate());
    }
}