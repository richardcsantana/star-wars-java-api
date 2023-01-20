package br.com.richardcsantana.starwarsjavaapi.gateway.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class APIFilmResponse {

    private final Long id;
    private final String title;
    private final String director;
    private final LocalDate releaseDate;

    @JsonCreator
    public APIFilmResponse(
            @JsonProperty(value = "url") String url,
            @JsonProperty(value = "title") String title,
            @JsonProperty(value = "director") String director,
            @JsonProperty(value = "release_date") LocalDate releaseDate
    ) {
        this.id = getFilmId(url);
        this.title = title;
        this.director = director;
        this.releaseDate = releaseDate;
    }

    private Long getFilmId(String url) {
        var matcher = Pattern.compile("films/([0-9]+)").matcher(url);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Invalid film URL: " + url);
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
}
