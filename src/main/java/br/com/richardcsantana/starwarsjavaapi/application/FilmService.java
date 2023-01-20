package br.com.richardcsantana.starwarsjavaapi.application;

import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.FilmEntity;
import br.com.richardcsantana.starwarsjavaapi.gateway.database.model.repository.FilmRepository;
import br.com.richardcsantana.starwarsjavaapi.gateway.api.SwapiGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class FilmService {
    private final FilmRepository filmRepository;
    private final SwapiGateway swapiGateway;

    public FilmService(FilmRepository filmRepository, SwapiGateway swapiGateway) {
        this.filmRepository = filmRepository;
        this.swapiGateway = swapiGateway;
    }

    public Mono<FilmEntity> getOrLoad(Long externalId) {
        return this.filmRepository.findByExternalId(externalId)
                .switchIfEmpty(
                        swapiGateway.getFilm(externalId)
                                .map(FilmEntity::fromFilmResponse).flatMap(filmRepository::save)
                );
    }

    public Flux<FilmEntity> findAllByPlanetId(UUID externalId) {
        return this.filmRepository.findAllByPlanetId(externalId);
    }
}
