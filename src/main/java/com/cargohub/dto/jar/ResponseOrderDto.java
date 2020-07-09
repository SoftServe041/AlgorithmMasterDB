package com.cargohub.dto.jar;

import com.cargohub.entities.OrderEntity;
import com.cargohub.entities.enums.DeliveryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ResponseOrderDto {

    private String trackingId;
    private Double price;
    private Date estimatedDeliveryDate;
    private String departureHub;
    private String arrivalHub;
    private DeliveryStatus deliveryStatus;

    public static ResponseOrderDto orderToResponseOrderDto(OrderEntity orderEntity) {
        ResponseOrderDto rod = new ResponseOrderDto();
        rod.setArrivalHub(orderEntity.getArrivalHub().getName());
        rod.setDepartureHub(orderEntity.getDepartureHub().getName());
        rod.setDeliveryStatus(orderEntity.getDeliveryStatus());
        rod.setEstimatedDeliveryDate(orderEntity.getEstimatedDeliveryDate());
        rod.setTrackingId(orderEntity.getTrackingId());
        rod.setPrice(orderEntity.getPrice());
        return rod;
    }

}
