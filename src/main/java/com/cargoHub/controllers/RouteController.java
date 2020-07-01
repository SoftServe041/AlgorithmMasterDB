package com.cargohub.controllers;

import com.cargohub.neo4jGraph.model.RouteModel;
import com.cargohub.service.impl.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/{departure}/{arrival}")
    public ResponseEntity<List<RouteModel>> getRoute(@PathVariable String departure, @PathVariable String arrival) {
        return ResponseEntity.ok(routeService.getRoute(departure, arrival));
    }
}
