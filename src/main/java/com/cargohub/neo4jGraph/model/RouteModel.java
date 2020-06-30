package com.cargohub.neo4jGraph.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public class RouteModel {

    Double distance;
    List<String> routes;

}
