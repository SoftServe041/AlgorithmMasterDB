package com.cargohub.cargoloader;

import com.cargohub.entities.*;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.exceptions.RouteException;
import com.cargohub.models.RouteModel;
import com.cargohub.service.impl.RouteNeo4jServiceImpl;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class OrderSimulation {

    private static long counter = 0;
    private final RouteNeo4jServiceImpl routeNeo4jServiceImpl;

    public OrderSimulation(RouteNeo4jServiceImpl routeNeo4jServiceImpl) {
        this.routeNeo4jServiceImpl = routeNeo4jServiceImpl;
    }

    @Setter
    Double averageSpeed = 60d;

    public OrderEntity getNewOrder(RouteEntity route, Double volume) {
        List<HubEntity> hubs = route.getHubs();
        List<RouteModel> routes = routeNeo4jServiceImpl.getRoute(hubs.get(0).getName(), hubs.get(hubs.size() - 1).getName());
        double distance = findDistanceForRoute(routes, hubs);
        Random random = new Random();
        OrderEntity order = new OrderEntity();
        order.setUserId(1);
        order.setPrice(200.0);
        order.setDeliveryStatus(DeliveryStatus.PROCESSING);
        order.setRoute(route);
        order.setArrivalHub(hubs.get(0));
        order.setDepartureHub(hubs.get(hubs.size() - 1));
        order.setTrackingId("ch" + random.nextInt(100) + counter++ + order.getArrivalHub().getName().hashCode() + random.nextInt(1000));
        Date date = new Date();
        order.setCreated(date);
        setCargo(order, volume);
        double hours = distance / averageSpeed;
        int days = (int) hours / 10 + (((int) hours % 10) < 5 ? 0 : 1);
        LocalDate localDate = LocalDate.now().plusDays(days);
        order.setEstimatedDeliveryDate(Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()));
        return order;
    }

    private double findDistanceForRoute(List<RouteModel> routes, List<HubEntity> hubs) {
        for (RouteModel routeModel : routes) {
            List<String> list = routeModel.getRoutes();
            if (list.size() == hubs.size()) {
                List<String> hubsList = hubs.stream().map(HubEntity::getName).collect(Collectors.toList());
                if (list.equals(hubsList)) {
                    return routeModel.getDistance();
                }
            }
        }
        throw new RouteException("no such route");
    }

    private void setCargo(OrderEntity orderEntity, Double volume) {
        Double currentVolume = 0.0;
        List<CargoEntity> cargoList = new ArrayList<>();
        Random random = new Random();
        int randWeight = 10;
        while (currentVolume < volume) {
            CargoEntity cargo = new CargoEntity();
            cargo.setDeliveryStatus(orderEntity.getDeliveryStatus());
            cargo.setStartingDestination(orderEntity.getDepartureHub().getName());
            cargo.setFinalDestination(orderEntity.getArrivalHub().getName());
            cargo.setWeight((double) random.nextInt(randWeight) + 1);
            DimensionsEntity dimensionsEntity = getRandDimensions();
            Double cargoVolume = dimensionsEntity.getHeight() * dimensionsEntity.getLength() * dimensionsEntity.getWidth();
            currentVolume += cargoVolume;
            cargo.setDimensions(dimensionsEntity);
            cargoList.add(cargo);

        }
        orderEntity.setCargoEntities(cargoList);
    }

    private DimensionsEntity getRandDimensions() {
        Random random = new Random();
        int max = 5;
        int rand = random.nextInt(max);
        switch (rand) {
            case 0: {
                return returnSetDimensions(0.3, 0.3, 0.3);
            }
            case 1: {
                return returnSetDimensions(0.6, 0.6, 0.6);
            }
            case 2: {
                return returnSetDimensions(0.6, 0.6, 0.9);
            }
            case 3: {
                return returnSetDimensions(0.6, 0.9, 0.9);
            }
            case 4: {
                return returnSetDimensions(0.9, 0.9, 0.9);
            }
        }
        return null;
    }

    private DimensionsEntity returnSetDimensions(double height, double width, double length) {
        DimensionsEntity dimensions = new DimensionsEntity();
        dimensions.setHeight(height);
        dimensions.setWidth(width);
        dimensions.setLength(length);
        return dimensions;
    }
}
