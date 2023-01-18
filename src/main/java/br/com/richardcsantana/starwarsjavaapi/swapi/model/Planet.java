package br.com.richardcsantana.starwarsjavaapi.swapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.regex.Pattern;

public class Planet {
    private final Long id;
    private final String name;
    private final String climate;
    private final String terrain;
    private final List<Long> films;

    @JsonCreator
    public Planet(
            @JsonProperty(value = "url") String url,
            @JsonProperty(value = "name") String name,
            @JsonProperty(value = "climate") String climate,
            @JsonProperty(value = "terrain") String terrain,
            @JsonProperty(value = "films") List<String> films
    ) {
        this.id = getPlanetId(url);
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
        this.films = films.stream().map(this::parseFilmId).toList();
    }

    private Long getPlanetId(String url) {
        var matcher = Pattern.compile("planets/([0-9]+)").matcher(url);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }
        return 1L;
    }

    public String getName() {
        return name;
    }

    public String getClimate() {
        return climate;
    }

    public String getTerrain() {
        return terrain;
    }

    public List<Long> getFilms() {
        return films;
    }

    public long getId() {
        return id;
    }

    private Long parseFilmId(String page) {
        if (page == null) {
            return null;
        }
        var matcher = Pattern.compile("/films/(\\d+)").matcher(page);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        } else {
            return null;
        }
    }
}
