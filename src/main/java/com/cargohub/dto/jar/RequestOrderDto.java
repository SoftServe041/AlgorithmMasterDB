package com.cargohub.dto.jar;

import com.cargohub.entities.Cargo;
import com.cargohub.entities.Dimensions;
import com.cargohub.entities.Hub;
import com.cargohub.entities.Order;
import com.cargohub.entities.enums.DeliveryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.coyote.Request;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RequestOrderDto {
    //private String trackingId;
    private Double price;
    private Date estimatedDeliveryDate;
    private String departureHub;
    private String arrivalHub;
    private Double cargoWeight;
    private Integer cargoWidth;
    private Integer cargoHeight;
    private Integer cargoLength;
    private DeliveryStatus deliveryStatus;

    public static Order reqOrderToEntity(RequestOrderDto reqOrder){
        Order order = new Order();
        Hub arrival = new Hub();
        Hub departure = new Hub();
        arrival.setName(reqOrder.arrivalHub);
        departure.setName(reqOrder.departureHub);
        order.setArrivalHub(arrival);
        order.setDepartureHub(departure);
        order.setDeliveryStatus(reqOrder.deliveryStatus);
        order.setEstimatedDeliveryDate(reqOrder.getEstimatedDeliveryDate());
        //order.setTrackingId(reqOrder.getTrackingId());
        order.setPrice(reqOrder.getPrice());
        Cargo cargo = new Cargo();
        cargo.setDeliveryStatus(order.getDeliveryStatus());
        cargo.setStartingDestination(order.getDepartureHub().getName());
        cargo.setFinalDestination(order.getArrivalHub().getName());
        cargo.setWeight(reqOrder.getCargoWeight());
        Dimensions dimensions = new Dimensions();
        dimensions.setHeight(reqOrder.getCargoHeight());
        dimensions.setLength(reqOrder.getCargoLength());
        dimensions.setWidth(reqOrder.getCargoWidth());
        cargo.setDimensions(dimensions);
        order.setCargo(cargo);

        return order;
    }
}
