package com.cargohub.controllers;

import com.cargohub.models.Location;
import com.cargohub.service.impl.LocationServiceNeo4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CitiesList {

    @Autowired
    private LocationServiceNeo4j locationServiceNeo4j;

    @GetMapping
    public ResponseEntity<List<Location>> selectAll() {
        return ResponseEntity.ok(locationServiceNeo4j.getAll());
    }

}
