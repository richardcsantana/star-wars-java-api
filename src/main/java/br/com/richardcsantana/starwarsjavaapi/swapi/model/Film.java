package br.com.richardcsantana.starwarsjavaapi.swapi.model;

import java.time.LocalDate;

public record Film(
        String title,
        String director,
        LocalDate release_date
) {
}
