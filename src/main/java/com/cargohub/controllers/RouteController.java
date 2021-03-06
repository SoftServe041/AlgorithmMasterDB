package com.cargohub.controllers;

import com.cargohub.dto.RouteDto;
import com.cargohub.entities.RouteEntity;
import com.cargohub.models.RouteModel;
import com.cargohub.service.RouteService;
import com.cargohub.service.impl.RouteNeo4jServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteNeo4jServiceImpl routeNeo4jServiceImpl;
    private final RouteService service;

    public RouteController(RouteNeo4jServiceImpl routeNeo4jServiceImpl, RouteService service) {
        this.routeNeo4jServiceImpl = routeNeo4jServiceImpl;
        this.service = service;
    }

    @GetMapping("/find")
    public ResponseEntity<List<RouteModel>> getRoute(@RequestParam String departure, String arrival) {
        return ResponseEntity.ok(routeNeo4jServiceImpl.getRoute(departure, arrival));
    }

    @PostMapping
    public ResponseEntity saveRoute(@RequestBody RouteDto dto){
        RouteEntity route = dto.toEntity();
        service.save(route);
        return new ResponseEntity(HttpStatus.OK);
    }
}
