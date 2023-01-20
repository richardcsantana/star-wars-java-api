package br.com.richardcsantana.starwarsjavaapi.gateway.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.regex.Pattern;

public class APIPlanetResponse {
    private final Long id;
    private final String name;
    private final String climate;
    private final String terrain;
    private final List<Long> films;

    @JsonCreator
    public APIPlanetResponse(
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
        } else {
            throw new IllegalArgumentException("Invalid planet URL: " + url);
        }
    }

    private Long parseFilmId(String url) {
        var matcher = Pattern.compile("films/(\\d+)").matcher(url);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Invalid film url: " + url);
        }
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

}
