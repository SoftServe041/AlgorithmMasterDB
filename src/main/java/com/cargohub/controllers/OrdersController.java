package com.cargohub.controllers;

import com.cargohub.dto.jar.OrderDto;
import com.cargohub.entities.Order;
import com.cargohub.models.OrderModel;
import com.cargohub.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrdersController {

    private ModelMapper modelMapper;
    private OrderService orderService;

    @Autowired
    public OrdersController(ModelMapper modelMapper, OrderService orderService) {
        this.modelMapper = modelMapper;
        this.orderService = orderService;
    }

    @PostMapping("/")
    public ResponseEntity makeOrder(@RequestBody OrderModel ordersModel) {
        OrderDto ordersDto = modelMapper.map(ordersModel, OrderDto.class);
        ordersDto.setTrackingId(generateTrackingId(
                ordersDto.getDepartureHub(),
                ordersDto.getArrivalHub(),
                ordersDto.getUserId()));

        Order orderEntity = modelMapper.map(ordersDto,Order.class);
        orderService.save(orderEntity);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping(path = "/PROFILE_PAGE", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getOrders(@RequestParam(value = "usedId", defaultValue = "a") int id) {

    }

    private String generateTrackingId(String firstCity, String secondCity, long id) {

        byte[] byteCity1 = firstCity.getBytes();
        byte[] byteCity2 = secondCity.getBytes();

        StringBuffer returnStr = new StringBuffer();
        for (byte a : byteCity1) {
            returnStr.append(a);
        }
        for (byte a : byteCity2) {
            returnStr.append(a);
        }
        returnStr.append(id);

        return returnStr.toString();
    }
}
