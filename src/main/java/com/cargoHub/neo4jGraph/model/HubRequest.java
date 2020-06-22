package com.cargoHub.neo4jGraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubRequest {

    String newCity;
    String connectedCity;
}
