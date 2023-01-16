package br.com.richardcsantana.starwarsjavaapi.swapi.model;

import java.util.List;

public record PlanetsResponse(
        Integer count,
        String next,
        String previous,
        List<Planet> results
) {

}
