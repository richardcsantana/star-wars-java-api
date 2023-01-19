package br.com.richardcsantana.starwarsjavaapi.common.application;

import br.com.richardcsantana.starwarsjavaapi.common.model.FilmEntity;
import br.com.richardcsantana.starwarsjavaapi.common.model.repository.FilmRepository;
import br.com.richardcsantana.starwarsjavaapi.batch.swapi.SwapiGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FilmService {
    private final FilmRepository filmRepository;
    private final SwapiGateway swapiGateway;

    public FilmService(FilmRepository filmRepository, SwapiGateway swapiGateway) {
        this.filmRepository = filmRepository;
        this.swapiGateway = swapiGateway;
    }

    public Mono<FilmEntity> getOrSave(Long externalId) {
        return this.filmRepository.findByExternalId(externalId)
                .switchIfEmpty(
                        swapiGateway.getFilm(externalId)
                                .map(FilmEntity::fromFilmResponse).flatMap(filmRepository::save)
                );
    }
}
