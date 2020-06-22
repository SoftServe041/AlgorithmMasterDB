package com.cargoHub.neo4jGraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;


import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(value = "City")
public class Location implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private double longitude;
    private double latitude;
}
