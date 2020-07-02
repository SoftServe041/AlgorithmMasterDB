package com.cargohub.service.impl;

import com.cargohub.models.RouteModel;
import com.cargohub.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class RouteService {

    final
    RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

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
