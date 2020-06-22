package com.cargoHub.neo4jGraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RelationshipEntity
public class NodeRelation {

    @StartNode
    private Location start;
    @EndNode
    private Location end;

}
