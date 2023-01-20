package br.com.richardcsantana.starwarsjavaapi.adapter.http.dto;

import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmEntity;

import java.time.LocalDate;

public record FilmResponse(Long id, String title, String director,
                           LocalDate releaseDate) {

    public static FilmResponse fromFilmEntity(FilmEntity filmEntity) {
        return new FilmResponse(filmEntity.getExternalId(), filmEntity.getTitle(), filmEntity.getDirector(), filmEntity.getReleaseDate());
    }
}
