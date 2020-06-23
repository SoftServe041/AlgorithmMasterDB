package com.cargoHub.neo4jGraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(value = "path")
public class Route {

    private Location departure;
    private Location arrival;
    //Map<String, Object> details;
    @Relationship (type = "NEXT")
    Collection<NodeRelation> relations;
}
