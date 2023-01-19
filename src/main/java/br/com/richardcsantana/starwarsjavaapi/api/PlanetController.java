package br.com.richardcsantana.starwarsjavaapi.api;

import br.com.richardcsantana.starwarsjavaapi.common.application.PlanetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/planets")
public class PlanetController {

    private final PlanetService planetService;

    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }

    @GetMapping
    public Flux<PlanetResponse> getAll(@RequestParam(value = "name", required = false) String name) {
        return planetService.getAll(name);
    }

    @GetMapping("/{id}")
    public Mono<PlanetResponse> getById(@PathVariable Long id) {
        return planetService.getPlanetById(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PlanetResponse> load(@PathVariable Long id) {
        return planetService.loadPlanetById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return planetService.deletePlanet(id);
    }
}
