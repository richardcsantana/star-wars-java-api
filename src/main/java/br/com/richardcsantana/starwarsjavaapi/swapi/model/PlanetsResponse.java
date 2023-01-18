package br.com.richardcsantana.starwarsjavaapi.swapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.regex.Pattern;

public class PlanetsResponse {

    private final Integer count;
    private final Long next;
    private final Long previous;
    private final List<Planet> results;

    @JsonCreator
    public PlanetsResponse(@JsonProperty(value = "count") Integer count,
                           @JsonProperty(value = "next") String next,
                           @JsonProperty(value = "previous") String previous,
                           @JsonProperty(value = "planets") List<Planet> results
    ) {
        this.count = count;
        this.next = parsePageNumber(next);
        this.previous = parsePageNumber(previous);
        this.results = results;
    }

    public Integer getCount() {
        return count;
    }

    public Long getNext() {
        return next;
    }

    public Long getPrevious() {
        return previous;
    }

    public List<Planet> getResults() {
        return results;
    }

    private Long parsePageNumber(String page) {
        if (page == null) {
            return null;
        }
        var matcher = Pattern.compile("page=(\\d+)").matcher(page);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        } else {
            return null;
        }
    }
}
