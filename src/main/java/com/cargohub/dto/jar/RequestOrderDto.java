package com.cargohub.dto.jar;

import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.Dimensions;
import com.cargohub.entities.Hub;
import com.cargohub.entities.OrderEntity;
import com.cargohub.entities.enums.DeliveryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RequestOrderDto {

    private Double price;
    private Date estimatedDeliveryDate;
    private String departureHub;
    private String arrivalHub;
    private Double cargoWeight;
    private Integer cargoWidth;
    private Integer cargoHeight;
    private Integer cargoLength;


    public static OrderEntity reqOrderToEntity(RequestOrderDto reqOrder) {
        OrderEntity orderEntity = new OrderEntity();
        Hub arrival = new Hub();
        Hub departure = new Hub();
        arrival.setName(reqOrder.arrivalHub);
        departure.setName(reqOrder.departureHub);
        orderEntity.setArrivalHub(arrival);
        orderEntity.setDepartureHub(departure);
        orderEntity.setDeliveryStatus(DeliveryStatus.PROCESSING);
        orderEntity.setEstimatedDeliveryDate(reqOrder.getEstimatedDeliveryDate());
        orderEntity.setPrice(reqOrder.getPrice());
        CargoEntity cargoEntity = new CargoEntity();
        cargoEntity.setDeliveryStatus(orderEntity.getDeliveryStatus());
        cargoEntity.setStartingDestination(orderEntity.getDepartureHub().getName());
        cargoEntity.setFinalDestination(orderEntity.getArrivalHub().getName());
        cargoEntity.setWeight(reqOrder.getCargoWeight());
        Dimensions dimensions = new Dimensions();
        dimensions.setHeight(reqOrder.getCargoHeight());
        dimensions.setLength(reqOrder.getCargoLength());
        dimensions.setWidth(reqOrder.getCargoWidth());
        cargoEntity.setDimensions(dimensions);
        orderEntity.setCargoEntity(cargoEntity);

        return orderEntity;
    }
}
