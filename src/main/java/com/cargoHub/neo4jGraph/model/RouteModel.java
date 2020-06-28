package com.cargoHub.neo4jGraph.model;

import com.cargoHub.neo4jGraph.repository.RouteRepository;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Getter
@Setter
public class RouteModel {

    Double distance;
    List<String> routes;

}
