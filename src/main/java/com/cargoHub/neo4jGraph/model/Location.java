package com.cargoHub.neo4jGraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;


import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(value = "City")
public class Location implements Serializable {

    private Long id;
    public String[] labels;
    private String name;
    private double longitude;
    private double latitude;
    @Relationship(type="NEXT")
    private List<Location> connectedLocations;
}
