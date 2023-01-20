package br.com.richardcsantana.starwarsjavaapi.adapter.http.dto;

import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.PlanetEntity;

import java.util.List;

/**
 * Planet response
 *
 * @param id      Planet id
 * @param name    Planet name
 * @param climate Planet climate
 * @param terrain Planet terrain
 * @param films   List of films
 */
public record PlanetResponse(Long id, String name, String climate,
                             String terrain,
                             List<FilmResponse> films) {

    public static PlanetResponse fromPlanetEntity(PlanetEntity planetEntity, List<FilmEntity> films) {
        return new PlanetResponse(
                planetEntity.getExternalId(),
                planetEntity.getName(),
                planetEntity.getClimate(),
                planetEntity.getTerrain(),
                films.stream().map(
                        FilmResponse::fromFilmEntity).toList());
    }
}
