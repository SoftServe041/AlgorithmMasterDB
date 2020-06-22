package com.cargoHub.neo4jGraph.service;

import com.cargoHub.neo4jGraph.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RouteService {

    @Autowired
    RouteRepository routeRepository;

    public List<RouteRepository.RouteData> getRoute(String departure, String arrival) {
        return routeRepository.getAllRoutes(departure, arrival);
    }
}
