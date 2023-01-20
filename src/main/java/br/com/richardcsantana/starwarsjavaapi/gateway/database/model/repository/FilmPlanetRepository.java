package br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository;

import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmPlanetEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FilmPlanetRepository extends ReactiveCrudRepository<FilmPlanetEntity, UUID> {
    @Query("delete from films_planets where planet_id = :planetId")
    Mono<Void> deleteAllByPlanetId(UUID id);
}
