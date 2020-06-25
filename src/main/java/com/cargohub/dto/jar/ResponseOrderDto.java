package com.cargohub.dto.jar;

import com.cargohub.entities.Order;
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
    private Double cargoWeight;
    private DeliveryStatus deliveryStatus;
    
    public static ResponseOrderDto orderToResponseOrderDto(Order order){
        ResponseOrderDto rod = new ResponseOrderDto();
        rod.setArrivalHub(order.getArrivalHub().getName());
        rod.setDepartureHub(order.getDepartureHub().getName());
        rod.setDeliveryStatus(order.getDeliveryStatus());
        rod.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        rod.setTrackingId(order.getTrackingId());
        rod.setPrice(order.getPrice());
        rod.setCargoWeight(order.getCargo().getWeight());
        return rod;
    }

}
