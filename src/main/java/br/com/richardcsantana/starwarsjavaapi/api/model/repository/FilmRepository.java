package br.com.richardcsantana.starwarsjavaapi.api.model.repository;

import br.com.richardcsantana.starwarsjavaapi.api.model.FilmEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FilmRepository extends ReactiveCrudRepository<FilmEntity, UUID> {
    Mono<FilmEntity> findByExternalId(Long id);
}
