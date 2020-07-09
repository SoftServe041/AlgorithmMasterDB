package com.cargohub.service.impl;

import com.cargohub.models.RouteModel;
import com.cargohub.repository.RouteNeo4jRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class RouteNeo4jServiceImpl {

    final
    RouteNeo4jRepository routeNeo4jRepository;

    public RouteNeo4jServiceImpl(RouteNeo4jRepository routeNeo4jRepository) {
        this.routeNeo4jRepository = routeNeo4jRepository;
    }

    public List<RouteModel> getRoute(String departure, String arrival) {
        List<RouteModel> routeModel = new ArrayList<>();

        List<RouteNeo4jRepository.RouteData> routeData = new ArrayList<>();
        routeData = routeNeo4jRepository.getAllRoutes(departure, arrival);

        List<RouteNeo4jRepository.RouteDataDistance> distanceData = new ArrayList<>();
        distanceData = routeNeo4jRepository.getAllRoutesDistance(departure, arrival);

        for (int i = 0; i < routeData.size(); i++) {
            RouteModel routeModelTemp = new RouteModel();
            routeModelTemp.setRoutes(routeData.get(i).routes);
            routeModelTemp.setDistance(distanceData.get(i).distance);
            routeModel.add(routeModelTemp);
        }

        return routeModel;
    }
}
