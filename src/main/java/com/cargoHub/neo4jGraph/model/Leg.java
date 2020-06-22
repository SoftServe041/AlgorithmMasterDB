package com.cargoHub.neo4jGraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class Leg {

    @Property
    private Location departure;
    @Property
    private Location arrival;
    @Relationship(type = "NEXT")
    Collection<Leg> legs;
    private double distance;
}
