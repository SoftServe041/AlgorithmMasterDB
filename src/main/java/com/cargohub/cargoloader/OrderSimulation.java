package com.cargohub.cargoloader;

import com.cargohub.entities.*;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.exceptions.RouteException;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class OrderSimulation {

    private static long counter = 0;

    @Setter
    Double averageSpeed = 600000d;

    public OrderEntity getNewOrder(RouteEntity route, Double volume) {
        List<HubEntity> hubs = route.getHubs();
        double distance = findDistanceForRoute(route);
        Random random = new Random();
        OrderEntity order = new OrderEntity();
        order.setUserId(1);
        order.setPrice(200.0);
        order.setDeliveryStatus(DeliveryStatus.PROCESSING);
        order.setRoute(route);
        order.setArrivalHub(hubs.get(hubs.size() - 1));
        order.setDepartureHub(hubs.get(0));
        order.setTrackingId("ch" + random.nextInt(100) + counter++ + order.getArrivalHub().getName().hashCode() + random.nextInt(1000));
        Date date = new Date();
        order.setCreated(date);
        setCargo(order, volume);
        long seconds = (long) (distance / averageSpeed * 36000);
        LocalDateTime localDate = LocalDateTime.now().plusSeconds(seconds);
        order.setEstimatedDeliveryDate(Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant()));
        return order;
    }

    private double findDistanceForRoute(RouteEntity route) {
        List<HubEntity> hubs = route.getHubs();
        double distance = 0;
        for (int i = 0; i < hubs.size() - 1; i++) {
            List<RelationEntity> relations = hubs.get(i).getRelations();
            for (RelationEntity relation : relations) {
                if (relation.getConnectedHub().getName().equals(hubs.get(i + 1).getName())) {
                    distance += relation.getDistance();
                    break;
                }
            }
        }
        if (distance == 0) {
            throw new RouteException("no such route");
        }
        return distance / 1000;
    }

    private void setCargo(OrderEntity orderEntity, Double volume) {
        double currentVolume = 0.0;
        List<CargoEntity> cargoList = new ArrayList<>();
        Random random = new Random();
        int randWeight = 10;
        while (currentVolume < volume) {
            CargoEntity cargo = new CargoEntity();
            cargo.setOrderEntity(orderEntity);
            cargo.setDeliveryStatus(orderEntity.getDeliveryStatus());
            cargo.setStartingDestination(orderEntity.getDepartureHub().getName());
            cargo.setFinalDestination(orderEntity.getArrivalHub().getName());
            cargo.setWeight((double) random.nextInt(randWeight) + 1);
            DimensionsEntity dimensionsEntity = getRandDimensions();
            double cargoVolume = dimensionsEntity.getHeight() * dimensionsEntity.getLength() * dimensionsEntity.getWidth();
            currentVolume += cargoVolume;
            if(currentVolume < volume) {
                cargo.setDimensions(dimensionsEntity);
                cargoList.add(cargo);
            }
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
                return returnSetDimensions(0.6, 0.6, 1.2);
            }
            case 3: {
                return returnSetDimensions(0.6, 1.2, 1.2);
            }
            case 4: {
                return returnSetDimensions(1.2, 1.2, 1.2);
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
