package br.com.richardcsantana.starwarsjavaapi.swapi.model;

import br.com.richardcsantana.starwarsjavaapi.batch.swapi.model.Film;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmTest {

    @Test
    void shouldCreateFilm() {
        var film = new Film("https://api_url/films/1/", "A New Hope", "George Lucas", LocalDate.parse("1977-05-25"));

        assertEquals(1L, film.getId());
        assertEquals("A New Hope", film.getTitle());
        assertEquals("George Lucas", film.getDirector());
        assertEquals(LocalDate.parse("1977-05-25"), film.getReleaseDate());
    }

}