package br.com.richardcsantana.starwarsjavaapi.gateway.api.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class APIFilmResponseTest {

    @Test
    void shouldCreateFilm() {
        var film = new APIFilmResponse("https://api_url/films/1/", "A New Hope", "George Lucas", LocalDate.parse("1977-05-25"));

        assertEquals(1L, film.getId());
        assertEquals("A New Hope", film.getTitle());
        assertEquals("George Lucas", film.getDirector());
        assertEquals(LocalDate.parse("1977-05-25"), film.getReleaseDate());
    }

    @Test
    void shouldThrowExceptionWithInvalidUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> new APIFilmResponse("invalid_url", "A New Hope", "George Lucas", LocalDate.parse("1977-05-25")));
    }

}