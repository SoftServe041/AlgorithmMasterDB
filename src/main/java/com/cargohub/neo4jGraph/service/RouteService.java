package com.cargohub.neo4jGraph.service;

import com.cargohub.neo4jGraph.model.RouteModel;
import com.cargohub.neo4jGraph.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class RouteService {

    @Autowired
    RouteRepository routeRepository;

    public List<RouteModel> getRoute(String departure, String arrival) {
        List<RouteModel> routeModel = new ArrayList<>();

        List<RouteRepository.RouteData> routeData = new ArrayList<>();
        routeData = routeRepository.getAllRoutes(departure, arrival);

        List<RouteRepository.RouteDataDistance> distanceData = new ArrayList<>();
        distanceData = routeRepository.getAllRoutesDistance(departure, arrival);

        for (int i = 0; i < routeData.size(); i++) {
            RouteModel routeModelTemp = new RouteModel();
            routeModelTemp.setRoutes(routeData.get(i).routes);
            routeModelTemp.setDistance(distanceData.get(i).distance);
            routeModel.add(routeModelTemp);
        }

        return routeModel;
    }
}
