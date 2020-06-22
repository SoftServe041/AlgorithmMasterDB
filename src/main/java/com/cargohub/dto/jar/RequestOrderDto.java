package com.cargohub.dto.jar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RequestOrderDto {
    private String trackingId;
    private Double price;
    private Date estimatedDeliveryDate;
    private String departureHub;
    private String arrivalHub;
    private Double cargoWeight;
    private Integer cargoWidth;
    private Integer cargoHeight;
    private Integer CargoLength;
    private DeliveryStatus deliveryStatus;
}
