package com.cargohub.controllers;

import com.cargohub.dto.jar.RequestOrderDto;
import com.cargohub.dto.jar.ResponseOrderDto;
import com.cargohub.entities.Order;
import com.cargohub.models.OrderModel;
import com.cargohub.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @PostMapping("/{id}")
    public ResponseEntity makeOrder(@RequestBody RequestOrderDto requestOrderDto,
                                    @PathVariable Integer id) {
        //RequestOrderDto ordersDto = modelMapper.map(ordersModel, RequestOrderDto.class);

        requestOrderDto.setTrackingId(generateTrackingId(
                requestOrderDto.getDepartureHub(),
                requestOrderDto.getArrivalHub(),
                id));

        Order orderEntity = RequestOrderDto.reqOrderToEntity(requestOrderDto);
        orderEntity.setUserId(id);
        orderService.save(orderEntity);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ResponseOrderDto> getOrders(@RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "limit", defaultValue = "5") int limit,
                                            @PathVariable Integer id) {

        Pageable pageableRequest = PageRequest.of(page, limit);
        return orderService.findAllByUserId(id, pageableRequest).map(ResponseOrderDto::orderToResponseOrderDto);
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
