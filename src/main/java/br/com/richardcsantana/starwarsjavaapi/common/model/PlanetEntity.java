package br.com.richardcsantana.starwarsjavaapi.common.model;

import br.com.richardcsantana.starwarsjavaapi.batch.swapi.model.Planet;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("planets")
public class PlanetEntity {

    @Id
    private UUID id;
    private final String name;
    private final String climate;
    private final String terrain;
    private final Long externalId;

    public PlanetEntity(String name, String climate, String terrain, Long externalId) {
        this.externalId = externalId;
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
    }

    public static PlanetEntity fromPlanet(Planet planet) {
        return new PlanetEntity(planet.getName(), planet.getClimate(), planet.getTerrain(), planet.getId());
    }

    public UUID getId() {
        return id;
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

    public Long getExternalId() {
        return externalId;
    }
}
