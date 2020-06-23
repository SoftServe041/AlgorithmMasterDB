package com.cargohub.dto.jar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponseDto {
    private String trackingId;
    private Double price;
    private Date estimatedDeliveryDate;
}
