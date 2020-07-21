package com.cargohub.controllers;

import com.cargohub.cargoloader.LoadingServiceImpl;
import com.cargohub.cargoloader.SimulationServiceImpl;
import com.cargohub.dto.RequestOrderDto;
import com.cargohub.dto.ResponseOrderDto;
import com.cargohub.entities.OrderEntity;
import com.cargohub.models.OrderModel;
import com.cargohub.order_builder.FormUnpaidOrders;
import com.cargohub.order_builder.UnpaidOrder;
import com.cargohub.service.OrderService;
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
import java.util.stream.Collectors;

@RestController
public class OrdersController {
    private OrderService orderService;
    private FormUnpaidOrders formUnpaidOrders;
    private LoadingServiceImpl loadingService;
    private final SimulationServiceImpl simulationService;

    @Autowired
    public OrdersController(OrderService orderService, FormUnpaidOrders formUnpaidOrders, LoadingServiceImpl loadingService, SimulationServiceImpl simulationService) {
        this.orderService = orderService;
        this.formUnpaidOrders = formUnpaidOrders;
        this.loadingService = loadingService;
        this.simulationService = simulationService;
    }

    @PostMapping("/{id}")
    public ResponseEntity makeOrder(@RequestBody RequestOrderDto requestOrderDto,
                                    @PathVariable Integer id) {
        OrderEntity orderEntity = RequestOrderDto.reqOrderToEntity(requestOrderDto);
        orderEntity.setUserId(id);
        orderService.save(orderEntity);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/simulation")
    public ResponseEntity simulation() {
        simulationService.simulate();
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PostMapping("/requestRoutes")
    public Map<String, List<UnpaidOrder>> getOrderVariants(@RequestBody OrderModel reqModel) {
        Map<String, List<UnpaidOrder>> map = formUnpaidOrders.formUnpaidOrders(reqModel);
        return map;
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

    @GetMapping(path = "/hub/{hubName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ResponseOrderDto> getOrdersByHubName(@PathVariable String hubName) {
        return loadingService.getAllOrdersByHub(hubName).stream().map(ResponseOrderDto::orderToResponseOrderDto).collect(Collectors.toList());
    }
}
