package br.com.richardcsantana.starwarsjavaapi.common.model.repository;

import br.com.richardcsantana.starwarsjavaapi.common.model.FilmPlanetEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FilmPlanetRepository extends ReactiveCrudRepository<FilmPlanetEntity, UUID> {
    @Query("delete from films_planets where planet_id = (select id from planets where external_id = :planetId)")
    Mono<Void> deleteAllByPlanetExternalId(Long id);
}
