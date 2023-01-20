package br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository;

import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.PlanetEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PlanetRepository extends ReactiveCrudRepository<PlanetEntity, UUID> {

    Mono<PlanetEntity> findByExternalId(long id);

    Flux<PlanetEntity> findByNameContainingIgnoreCase(String name);
}
