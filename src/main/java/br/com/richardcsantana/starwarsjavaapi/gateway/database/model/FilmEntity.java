package br.com.richardcsantana.starwarsjavaapi.gateway.database.model;

import br.com.richardcsantana.starwarsjavaapi.gateway.api.dto.APIFilmResponse;
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

    public static FilmEntity fromFilmResponse(APIFilmResponse APIFilmResponse) {
        return new FilmEntity(
                APIFilmResponse.getTitle(),
                APIFilmResponse.getDirector(),
                APIFilmResponse.getReleaseDate(),
                APIFilmResponse.getId());
    }
}
