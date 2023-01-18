package br.com.richardcsantana.starwarsjavaapi.application;

import br.com.richardcsantana.starwarsjavaapi.api.model.FilmEntity;
import br.com.richardcsantana.starwarsjavaapi.api.model.repository.FilmRepository;
import br.com.richardcsantana.starwarsjavaapi.swapi.SwapiGateway;
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
