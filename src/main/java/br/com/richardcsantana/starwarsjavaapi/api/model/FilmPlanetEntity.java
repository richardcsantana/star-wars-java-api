package br.com.richardcsantana.starwarsjavaapi.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("films_planets")
public class FilmPlanetEntity {
    private final UUID filmId;
    private final UUID planetId;

    public FilmPlanetEntity(UUID filmId, UUID planetId) {
        this.filmId = filmId;
        this.planetId = planetId;
    }
}
