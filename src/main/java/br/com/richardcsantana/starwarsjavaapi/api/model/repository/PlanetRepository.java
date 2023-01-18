package br.com.richardcsantana.starwarsjavaapi.api.model.repository;

import br.com.richardcsantana.starwarsjavaapi.api.model.PlanetEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PlanetRepository extends ReactiveCrudRepository<PlanetEntity, UUID> {

    Mono<PlanetEntity> findByExternalId(long id);
}