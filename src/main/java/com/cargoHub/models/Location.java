package com.cargohub.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(value = "City")
public class Location implements Serializable {

    private Long id;
    private String name;
    private double longitude;
    private double latitude;
    @Relationship
    private Set<Location> connectedLocations;
}
