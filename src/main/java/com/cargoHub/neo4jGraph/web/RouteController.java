package com.cargoHub.neo4jGraph.web;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.repository.RouteRepository;
import com.cargoHub.neo4jGraph.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/find")
    public ResponseEntity<Iterable<RouteRepository.RouteData>> getRoute(@RequestParam String departure, String arrival) {
        return ResponseEntity.ok(routeService.getRoute(departure, arrival));
    }

//    @GetMapping("/{departure}/{arrival}")
//    public ResponseEntity<Iterable<Location>> getRoute(@PathVariable String departure, @PathVariable String arrival) {
//        return ResponseEntity.ok(routeService.getRoute(departure, arrival));
//    }
}
