package com.cargohub.cargoloader;

import com.cargohub.entities.*;
import com.cargohub.entities.enums.DeliveryStatus;
import com.cargohub.repository.RelationRepository;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class OrderSimulation {

    private final RelationRepository repository;
    private static long counter = 0;

    public OrderSimulation(RelationRepository relationRepository){
        this.repository = relationRepository;
    }

    @Setter
    Double averageSpeed = 60d;


    public OrderEntity getNewOrder(RouteEntity route, Double volume){
        double distance = 0;
        Random random = new Random();
        OrderEntity order = new OrderEntity();
        order.setDeliveryStatus(DeliveryStatus.PROCESSING);
        List<HubEntity> hubs = route.getHubs();
        order.setRoute(route);
        order.setArrivalHub(hubs.get(0));
        order.setDepartureHub(hubs.get(hubs.size()-1));
        order.setTrackingId("ch" + random.nextInt(100) + counter++ + order.getArrivalHub().getName().hashCode()+ random.nextInt(1000));
        Date date = new Date();
        order.setCreated(date);
        List<HubEntity> hubEntities = route.getHubs();
        for(int i = 0; i < hubEntities.size()-1; i++){
            RelationEntity relationEntity = repository.findByOwnerHubAndConnectedHub(hubEntities.get(i), hubEntities.get(i+1))
                    .orElseThrow(() -> new IllegalArgumentException("no such relation found"));
            distance+=relationEntity.getDistance();
        }
        setCargo(order, volume);
        double hours = distance / averageSpeed;
        int days = (int) hours / 10 + (((int) hours % 10) < 5 ? 0 : 1);
        LocalDate localDate = LocalDate.now().plusDays(days);
        order.setEstimatedDeliveryDate(Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()));

        return order;
    }
    private void setCargo(OrderEntity orderEntity, Double volume) {
        Double currentVolume = 0.0;
        List<CargoEntity> cargoList = new ArrayList<>();
        Random random = new Random();
        int randWeight = 10;
        while(currentVolume < volume) {
            CargoEntity cargo = new CargoEntity();
            cargo.setDeliveryStatus(orderEntity.getDeliveryStatus());
            cargo.setStartingDestination(orderEntity.getDepartureHub().getName());
            cargo.setFinalDestination(orderEntity.getArrivalHub().getName());
            cargo.setWeight((double) random.nextInt(randWeight)+1);
            DimensionsEntity dimensionsEntity = getRandDimensions();
            Double cargoVolume = dimensionsEntity.getHeight()*dimensionsEntity.getLength()*dimensionsEntity.getWidth();
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
                return returnSetDimensions(0.3, 0.3,0.3);
            }
            case 1: {
                return returnSetDimensions(0.6, 0.6,0.6);
            }
            case 2: {
                return returnSetDimensions(0.6, 0.6,0.9);
            }
            case 3: {
                return returnSetDimensions(0.6, 0.9,0.9);
            }
            case 4: {
                return returnSetDimensions(0.9, 0.9,0.9);
            }
        }
        return null;
    }
    private DimensionsEntity returnSetDimensions(double height, double width, double length){
        DimensionsEntity dimensions = new DimensionsEntity();
        dimensions.setHeight(height);
        dimensions.setWidth(width);
        dimensions.setLength(length);
        return dimensions;
    }
}
