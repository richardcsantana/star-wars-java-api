package br.com.richardcsantana.starwarsjavaapi.common.model.repository;

import br.com.richardcsantana.starwarsjavaapi.common.model.PlanetEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PlanetRepository extends ReactiveCrudRepository<PlanetEntity, UUID> {

    Mono<PlanetEntity> findByExternalId(long id);

    Mono<PlanetEntity> findByName(String name);

    Mono<Void> deleteByExternalId(Long id);
}
