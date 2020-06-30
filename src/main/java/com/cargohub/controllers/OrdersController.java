package com.cargohub.controllers;

import com.cargohub.dto.jar.RequestOrderDto;
import com.cargohub.dto.jar.ResponseOrderDto;
import com.cargohub.entities.OrderEntity;
import com.cargohub.models.OrderModel;
import com.cargohub.order_builder.FormUnpaidOrders;
import com.cargohub.order_builder.UnpaidOrder;
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

import java.util.List;
import java.util.Map;

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
        OrderEntity orderEntity = RequestOrderDto.reqOrderToEntity(requestOrderDto);
        orderEntity.setUserId(id);
        orderService.save(orderEntity);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @PostMapping("/requestRoutes")
    public Map<String, List<UnpaidOrder>> getOrderVariants(@RequestBody OrderModel reqModel) {
        FormUnpaidOrders formUnpaidOrders = new FormUnpaidOrders();
        Map<String,List<UnpaidOrder>>  map = formUnpaidOrders.formUnpaidOrders(reqModel.getDepartureHub(), reqModel.getArrivalHub());
       return  map;
       // return FazlidinClass(reqModel);
    }

    @GetMapping(path = "/{id}/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ResponseOrderDto> getOrders(@RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "limit", defaultValue = "5") int limit,
                                            @PathVariable Integer id) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        return orderService.findAllByUserId(id, pageableRequest).map(ResponseOrderDto::orderToResponseOrderDto);
    }

    @DeleteMapping(path = "/{id}/deleteOrder")
    ResponseEntity<?> deleteTransporter(@PathVariable Integer id) {
        orderService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
