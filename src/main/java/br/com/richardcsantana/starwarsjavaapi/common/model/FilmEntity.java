package br.com.richardcsantana.starwarsjavaapi.common.model;

import br.com.richardcsantana.starwarsjavaapi.batch.swapi.model.Film;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("films")
public class FilmEntity {
    @Id
    private UUID id;
    private final String title;
    private final String director;
    private final LocalDate releaseDate;
    private final Long externalId;

    public FilmEntity(String title, String director, LocalDate releaseDate, Long externalId) {
        this.title = title;
        this.director = director;
        this.releaseDate = releaseDate;
        this.externalId = externalId;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Long getExternalId() {
        return externalId;
    }

    public static FilmEntity fromFilmResponse(Film film) {
        return new FilmEntity(film.getTitle(), film.getDirector(), film.getReleaseDate(), film.getId());
    }
}