package br.com.richardcsantana.starwarsjavaapi.api.model.repository;

import br.com.richardcsantana.starwarsjavaapi.api.model.FilmPlanetEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface FilmPlanetRepository extends ReactiveCrudRepository<FilmPlanetEntity, UUID> {
}
