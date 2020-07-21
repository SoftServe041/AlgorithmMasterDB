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

    public SimulationServiceImpl(OrderSimulation orderSimulation, HubRepository hubRepository, OrderService orderService, TransporterService transporterService) {
        this.orderSimulation = orderSimulation;
        this.hubRepository = hubRepository;
        this.orderService = orderService;
        this.transporterService = transporterService;
    }

    public void simulate() {
        HubEntity hub = new HubEntity();
        hub.setName("Berlin");
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
        for (Map.Entry<String, List<RouteEntity>> entry : routesWithDirections.entrySet()) {
            List<RouteEntity> routes = entry.getValue();
            for (RouteEntity route : routes) {
                TransporterEntity transporter = initializeTransporter(route.getHubs().get(0));
                double compartmentVolume = computeCompartmentVolume(transporter.getCompartments().get(0)) - 4;
                // in Alexey`s simulation the upper range is limited by last added box volume,
                // so the biggest box volume is 1.728 and i am decreasing order`s volume by 1.5
                double ordersVolume = compartmentVolume / (route.getHubs().size() - 1);
                List<OrderEntity> orders = formOrders(route, ordersVolume);
                saveAllGeneratedEntities(orders, transporter);
            }
        }
        //TODO remove after testing
        routesWithDirections.forEach((name, list) -> {
            System.out.println("name = " + name);
            list.forEach(routeEntity -> {
                System.out.println("routeEntity = " + routeEntity);
            });
        });

    }

    private void saveAllGeneratedEntities(List<OrderEntity> orders, TransporterEntity transporter) {
        transporterService.save(transporter);
        orderService.saveAll(orders);
    }

    private List<OrderEntity> formOrders(RouteEntity route, double ordersVolume) {
        List<OrderEntity> result = new ArrayList<>();
        for (int i = 1; i < route.getHubs().size(); i++) {
            RouteEntity formingRoute1 = new RouteEntity();
            RouteEntity formingRoute2 = new RouteEntity();
            List<HubEntity> hubs1 = route.getHubs().subList(0, i + 1);
            List<HubEntity> hubs2 = new ArrayList<>(hubs1);
            formingRoute1.setHubs(hubs1);
            formingRoute2.setHubs(hubs2);
            OrderEntity order1 = orderSimulation.getNewOrder(formingRoute1,ordersVolume / 2);
            OrderEntity order2 = orderSimulation.getNewOrder(formingRoute2,ordersVolume / 2);
            result.add(order1);
            result.add(order2);
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
}
