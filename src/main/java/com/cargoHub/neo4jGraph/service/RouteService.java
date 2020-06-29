package com.cargoHub.neo4jGraph.service;

import com.cargoHub.neo4jGraph.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RouteService {

    @Autowired
    RouteRepository routeRepository;

    public Iterable<RouteRepository.RouteData> getRoute(String departure, String arrival) {
        return routeRepository.getAllRoutes(departure, arrival);
    }

//    public Iterable<Location> getRoute(String departure, String arrival) {
//        return routeRepository.getAllRoutes(departure, arrival);
//    }
}
