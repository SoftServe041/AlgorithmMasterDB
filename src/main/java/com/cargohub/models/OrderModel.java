package com.cargohub.models;

import lombok.Data;

import java.util.List;

@Data
public class OrderModel {

    private String departureHub;
    private String arrivalHub;
    List<CargoSizeModel> sizeList;
}
