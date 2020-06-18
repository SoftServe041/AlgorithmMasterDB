package com.cargohub.dto.jar;

import com.cargohub.entities.Cargo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderDto {
    private Integer id;
    private String trackingId;
    private Integer userId;
    private Double price;
    private Date estimatedDeliveryDate;
    private String departureHub;
    private String arrivalHub;
    private Cargo cargo;
    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;
}
