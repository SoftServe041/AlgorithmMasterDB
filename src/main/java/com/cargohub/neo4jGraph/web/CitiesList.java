package com.cargohub.neo4jGraph.web;

import com.cargohub.neo4jGraph.model.Location;
import com.cargohub.service.impl.LocationService;
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
