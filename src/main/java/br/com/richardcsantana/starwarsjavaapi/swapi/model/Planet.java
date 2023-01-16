package br.com.richardcsantana.starwarsjavaapi.swapi.model;

import java.util.List;

public record Planet(
        String name,
        String climate,
        String terrain,
        List<String> films
) {
}
