package br.com.richardcsantana.starwarsjavaapi.adapter.http.dto;

import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmResponseTest {

    @Test
    void fromFilmEntity() {
        var film = FilmResponse.fromFilmEntity(
                new FilmEntity(
                        "A New Hope",
                        "George Lucas",
                        LocalDate.parse("1977-05-25"),
                        1L
                )
        );

        assertEquals(1L, film.id());
        assertEquals("A New Hope", film.title());
        assertEquals("George Lucas", film.director());
        assertEquals(LocalDate.parse("1977-05-25"), film.releaseDate());
    }
}