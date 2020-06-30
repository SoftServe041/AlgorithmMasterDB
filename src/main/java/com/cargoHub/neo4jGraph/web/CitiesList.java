package com.cargoHub.neo4jGraph.web;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CitiesList {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<List<Location>> selectAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

}
