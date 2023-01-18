package br.com.richardcsantana.starwarsjavaapi.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("planet")
public class PlanetEntity {

    @Id
    private UUID id;
    private String name;
    private String climate;
    private String terrain;

}
