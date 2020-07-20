package com.cargohub.dto;

import com.cargohub.entities.*;
import com.cargohub.entities.enums.DeliveryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RequestOrderDto {

    private Double price;
    private String trackingId;
    private Date estimatedDeliveryDate;
    private String departureHub;
    private String arrivalHub;
    private List<CargoDto> cargos;
    private RouteDto route;

    public static OrderEntity reqOrderToEntity(RequestOrderDto reqOrder) {
        OrderEntity orderEntity = new OrderEntity();
        HubEntity arrival = new HubEntity();
        HubEntity departure = new HubEntity();
        arrival.setName(reqOrder.arrivalHub);
        departure.setName(reqOrder.departureHub);
        orderEntity.setArrivalHub(arrival);
        orderEntity.setDepartureHub(departure);
        orderEntity.setDeliveryStatus(DeliveryStatus.PROCESSING);
        orderEntity.setEstimatedDeliveryDate(reqOrder.getEstimatedDeliveryDate());
        orderEntity.setPrice(reqOrder.getPrice());
        orderEntity.setTrackingId(reqOrder.getTrackingId());
        List<CargoEntity> entities = new ArrayList<>();
        orderEntity.setRoute(reqOrder.getRoute().toEntity());
        for (CargoDto dto : reqOrder.getCargos()) {
            CargoEntity entity = new CargoEntity();
            entity.setWeight(dto.getWeight());
            entity.setDeliveryStatus(orderEntity.getDeliveryStatus());
            entity.setStartingDestination(orderEntity.getDepartureHub().getName());
            entity.setFinalDestination(orderEntity.getArrivalHub().getName());
            DimensionsEntity dimensions = new DimensionsEntity();
            dimensions.setHeight(dto.getHeight());
            dimensions.setLength(dto.getLength());
            dimensions.setWidth(dto.getWidth());
            entity.setDimensions(dimensions);
            entities.add(entity);
        }
        orderEntity.setCargoEntities(entities);

        return orderEntity;
    }
}
