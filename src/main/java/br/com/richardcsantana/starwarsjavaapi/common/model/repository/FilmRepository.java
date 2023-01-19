package br.com.richardcsantana.starwarsjavaapi.common.model.repository;

import br.com.richardcsantana.starwarsjavaapi.common.model.FilmEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FilmRepository extends ReactiveCrudRepository<FilmEntity, UUID> {
    Mono<FilmEntity> findByExternalId(Long id);

    @Query("SELECT f.* FROM films as f inner join films_planets fp on f.id = fp.film_id inner join planets p on fp.planet_id = p.id where p.external_id = :planetId")
    Flux<FilmEntity> findAllByPlanetExternalId(Long planetId);

}
