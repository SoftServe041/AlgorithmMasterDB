package com.cargohub.models;

import lombok.Data;

@Data
public class OrderModel {

    private String departureHub;
    private String arrivalHub;
    private Double cargoWeight;
    private Integer cargoWidth;
    private Integer cargoHeight;
    private Integer CargoLength;
    private Double price;
    private int userid;

}
