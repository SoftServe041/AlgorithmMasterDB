package com.cargoHub.neo4jGraph.service;

import com.cargoHub.neo4jGraph.model.RouteModel;
import com.cargoHub.neo4jGraph.repository.RouteRepository;
import com.fasterxml.jackson.databind.util.ArrayIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Service
public class RouteService {

    @Autowired
    RouteRepository routeRepository;

    public List<RouteModel> getRoute(String departure, String arrival) {
        List<RouteModel> routeModel = new ArrayList<>();

        List<RouteRepository.RouteData> routeData = new ArrayList<>();
        routeData = routeRepository.getAllRoutes(departure, arrival);

        List<RouteRepository.RouteDataDistance> distanceData = new ArrayList<>();
        distanceData =  routeRepository.getAllRoutesDistance(departure, arrival);

        for (int i = 0; i < routeData.size(); i++) {
            routeModel.add(new RouteModel(distanceData.get(i).distance, routeData.get(i).routes));
        }

        return routeModel;
    }
}
