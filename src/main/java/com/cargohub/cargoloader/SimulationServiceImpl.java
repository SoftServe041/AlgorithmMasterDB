package com.cargohub.cargoloader;

import com.cargohub.entities.*;
import com.cargohub.entities.enums.TransporterStatus;
import com.cargohub.entities.enums.TransporterType;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import com.cargohub.entities.transports.TransporterEntity;
import com.cargohub.repository.HubRepository;
import com.cargohub.service.OrderService;
import com.cargohub.service.TransporterService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimulationServiceImpl {


    private final OrderSimulation orderSimulation;
    private final HubRepository hubRepository;
    private final OrderService orderService;
    private final TransporterService transporterService;
    private final LoadingServiceImpl loadingService;

    public SimulationServiceImpl(OrderSimulation orderSimulation,
                                 HubRepository hubRepository,
                                 OrderService orderService,
                                 TransporterService transporterService,
                                 LoadingServiceImpl loadingService) {
        this.orderSimulation = orderSimulation;
        this.hubRepository = hubRepository;
        this.orderService = orderService;
        this.transporterService = transporterService;
        this.loadingService = loadingService;
    }

    public void simulate() {
        //List<HubEntity> hubs = (List<HubEntity>) hubRepository.findAll();
        //fillHubs(hubs);

        //here also orders added
        initTransportersInHub("Berlin");
        loadingService.loadAllTransportersInHub("Berlin");
    }

    public void initTransportersInHub(String hubName) {
        HubEntity hub = hubRepository.findByName(hubName).orElseThrow();
        fillHubWithTransporters(hub);
        fillHubWithOrders(hub);
    }

    private void fillHubWithTransporters(HubEntity hub) {
        Map<String, List<RouteEntity>> routesWithDirections = getRoutesWithDirections(hub);
        List<TransporterEntity> transporters = new ArrayList<>();
        for (int i = 0; i < routesWithDirections.size() + 1; i++) {
            TransporterEntity transporter = initializeTransporter(hub);
            transporters.add(transporter);
        }
        transporterService.saveAll(transporters);
    }


//    public void fillHubs(List<HubEntity> hubs) {
//        for (HubEntity hub : hubs) {
//            fillHubWithOrders(hub);
//        }
//    }

    private void fillHubWithOrders(HubEntity hub) {
        //There will be as many entries in the Map as many relations have current Hub
        Map<String, List<RouteEntity>> routesWithDirections = getRoutesWithDirections(hub);
        for (Map.Entry<String, List<RouteEntity>> entry : routesWithDirections.entrySet()) {
            List<RouteEntity> routes = entry.getValue();
            //only longest variants of routes for Berlin Should be 5 routes
            RouteEntity route = null;
            int size = 0;
            for (RouteEntity routeEntity : routes) {
                if(size == 0){
                    route = routeEntity;
                } else {
                    if(route.getHubs().size() < routeEntity.getHubs().size()){
                        route = routeEntity;
                    }
                }
                size = routeEntity.getHubs().size();
            }
            TransporterEntity transporter = initializeTransporter(hub);
            double compartmentVolume = computeCompartmentVolume(transporter.getCompartments().get(0));
            // "-1" here because route segments count is hubs - 1
            double ordersVolume = compartmentVolume * 0.6;
            List<OrderEntity> orders = formOrders(route, ordersVolume);
//            RouteEntity subRoute = new RouteEntity();
//            subRoute.setHubs(route.getHubs().subList(1, route.getHubs().size()));
//            orders.addAll(formOrders(subRoute, ordersVolume / 2));
            orderService.saveAll(orders);
        }
    }

    private Map<String, List<RouteEntity>> getRoutesWithDirections(HubEntity hub) {
        Map<String, List<RouteEntity>> routesWithDirections = new HashMap<>();
        List<RelationEntity> relations = hub.getRelations();
        for (RelationEntity relation : relations) {
            String name = relation.getConnectedHub().getName();
            HubEntity connectedHub = relation.getConnectedHub();
            List<RouteEntity> routes = getAllPossibleRoutes(hub, connectedHub);
            routesWithDirections.put(name, routes);
        }
        return routesWithDirections;
    }

    private List<OrderEntity> formOrders(RouteEntity route, double ordersVolume) {
        List<OrderEntity> result = new ArrayList<>();
        RouteEntity formingRoute;
        List<HubEntity> hubs;
        OrderEntity order;
        for (int i = 0; i < 10; i++) {
            formingRoute = new RouteEntity();
            hubs = new ArrayList<>(route.getHubs());
            formingRoute.setHubs(hubs);
            order = orderSimulation.getNewOrder(formingRoute, ordersVolume);
            result.add(order);
        }
        if (route.getHubs().size() == 2) {
            result.remove(result.size() - 1);
            result.remove(result.size() - 1);
            formingRoute = new RouteEntity();
            hubs = new ArrayList<>(route.getHubs());
            formingRoute.setHubs(hubs);
            order = orderSimulation.getNewOrder(formingRoute, ordersVolume / 2);
            result.add(order);
        }
        for (int i = 1; i < route.getHubs().size(); i++) {
            for (int j = 0; j < 5; j++) {
                formingRoute = new RouteEntity();
                hubs = new ArrayList<>(route.getHubs().subList(i - 1, i + 1));
                formingRoute.setHubs(hubs);
                order = orderSimulation.getNewOrder(formingRoute, ordersVolume / 4);
                result.add(order);
            }
        }
        return result;
    }

    private TransporterEntity initializeTransporter(HubEntity hub) {
        TransporterEntity transporter = new TransporterEntity();
        List<CarrierCompartmentEntity> compartments = new ArrayList<>();
        CarrierCompartmentEntity compartment = new CarrierCompartmentEntity();
        compartment.setFreeSpace(100d);
        compartment.setMaximumWeight(22d);
        DimensionsEntity dimensions = new DimensionsEntity();
        dimensions.setHeight(2.4);
        dimensions.setWidth(2.4);
        dimensions.setLength(12d);
        dimensions.setCarrierCompartment(compartment);
        compartment.setVolume(dimensions);
        compartment.setTransporter(transporter);
        compartments.add(compartment);
        transporter.setCompartments(compartments);
        transporter.setType(TransporterType.TRUCK);
        transporter.setStatus(TransporterStatus.WAITING);
        transporter.setCurrentHub(hub);
        return transporter;
    }

    private double computeCompartmentVolume(CarrierCompartmentEntity compartment) {
        DimensionsEntity dimensions = compartment.getVolume();
        return dimensions.getHeight() * dimensions.getWidth() * dimensions.getLength();
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

    public void clearDatabase() {
        loadingService.clearDatabaseAfterSimulation();
    }
}
