package br.com.richardcsantana.starwarsjavaapi.adapter.http;

import br.com.richardcsantana.starwarsjavaapi.adapter.http.dto.PlanetResponse;
import br.com.richardcsantana.starwarsjavaapi.application.PlanetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The planet related endpoints
 */

@RestController
@RequestMapping("/planets")
public class PlanetController {

    private final PlanetService planetService;

    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }

    /**
     * List all planets
     *
     * @param name Name to filter planets (optional)
     * @return List of planets
     */
    @GetMapping
    public Flux<PlanetResponse> getPlanets(@RequestParam(value = "name", required = false) String name) {
        if (name != null) {
            return planetService.getPlanetsByName(name);
        }
        return planetService.getPlanets();
    }

    /**
     * Get a planet by id
     *
     * @param id the id of the planet to search for
     * @return Planet
     */
    @GetMapping("/{id}")
    public Mono<PlanetResponse> getById(@PathVariable Long id) {
        return planetService.getPlanetById(id);
    }

    /**
     * Load a planet in the database
     *
     * @param id id of the planet to be loaded
     * @return Loaded planet
     */
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PlanetResponse> load(@PathVariable Long id) {
        return planetService.loadPlanetById(id);
    }

    /**
     * Delete a planet by id
     *
     * @param id the id of the planet to be deleted
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return planetService.deletePlanet(id);
    }
}
