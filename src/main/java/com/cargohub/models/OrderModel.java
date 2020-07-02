package com.cargohub.models;

import lombok.Data;

@Data
public class OrderModel {

    private String departureHub;
    private String arrivalHub;
    private Double cargoWeight;
    private Double cargoWidth;
    private Double cargoHeight;
    private Double cargoLength;
}
