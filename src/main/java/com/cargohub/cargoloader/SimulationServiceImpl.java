package com.cargohub.cargoloader;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.OrderEntity;
import com.cargohub.entities.RelationEntity;
import com.cargohub.entities.RouteEntity;
import com.cargohub.repository.HubRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimulationServiceImpl {


    private final OrderSimulation orderSimulation;
    private final HubRepository hubRepository;

    public SimulationServiceImpl(OrderSimulation orderSimulation, HubRepository hubRepository) {
        this.orderSimulation = orderSimulation;
        this.hubRepository = hubRepository;
    }

    public void simulate() {
        RouteEntity route = new RouteEntity();
        RouteEntity route1 = new RouteEntity();
        List<HubEntity> hubList = new ArrayList<>();
        HubEntity hub = new HubEntity();
        hub.setName("Stuttgart");
        hubList.add(hub);
        hub = new HubEntity();
        hub.setName("Frankfurt");
        hubList.add(hub);
        hub = new HubEntity();
        hub.setName("Berlin");
        hubList.add(hub);
        route.setHubs(hubList);
        hubList.remove(hubList.size() - 1);
        route1.setHubs(hubList);
        OrderEntity orderEntity = orderSimulation.getNewOrder(route, 30.0);
        orderSimulation.getNewOrder(route, 30.0);
        hub = hubRepository.findByName(hub.getName()).orElseThrow();
        fillHub(hub);
    }

    public void fillHubs(List<HubEntity> hubs) {
        for (HubEntity hub : hubs) {
            fillHub(hub);
        }
    }

    private void fillHub(HubEntity hub) {
        //There will be as many entries in the Map as many relations have current Hub
        Map<String, List<RouteEntity>> routesWithDirections = new HashMap<>();
        List<RelationEntity> relations = hub.getRelations();
        for (RelationEntity relation : relations) {
            String name = relation.getConnectedHub().getName();
            HubEntity connectedHub = relation.getConnectedHub();
            List<RouteEntity> routes = getAllPossibleRoutes(hub, connectedHub);
            routesWithDirections.put(name, routes);
        }
        //TODO remove after testing
        routesWithDirections.forEach((name, list) -> {
            System.out.println("name = " + name);
            list.forEach(routeEntity -> {
                System.out.println("routeEntity = " + routeEntity);
            });
        });
        
    }

    private List<RouteEntity> getAllPossibleRoutes(HubEntity hub, HubEntity connectedHub) {
        List<RouteEntity> routes = new ArrayList<>();
        RouteEntity rawRoute = new RouteEntity();
        rawRoute.setHubs(new ArrayList<>());
        rawRoute.getHubs().addAll(Arrays.asList(hub, connectedHub));
        routes.add(rawRoute);
        findAllRoutesOnDirection(routes);
        return routes;
    }

    private void findAllRoutesOnDirection(List<RouteEntity> routes) {
        RouteEntity route = routes.get(routes.size() - 1);
        List<HubEntity> existingHubs = new ArrayList<>(route.getHubs());
        HubEntity lastInRoute = existingHubs.get(existingHubs.size() - 1);
        List<HubEntity> notVisitedHubs = lastInRoute.getRelations().stream().
                map(RelationEntity::getConnectedHub).
                filter(hubEntity -> !existingHubs.contains(hubEntity)).
                collect(Collectors.toList());
        for (int i = 0; i < notVisitedHubs.size(); i++) {
            HubEntity notVisitedHub = notVisitedHubs.get(i);
            if (i != 0) {
                RouteEntity routeEntity = new RouteEntity();
                List<HubEntity> hubEntities = new ArrayList<>(existingHubs);
                routeEntity.setHubs(hubEntities);
                routes.add(routeEntity);
                routeEntity.getHubs().add(notVisitedHub);
            } else {
                route.getHubs().add(notVisitedHub);
            }
            findAllRoutesOnDirection(routes);
        }
    }
}
