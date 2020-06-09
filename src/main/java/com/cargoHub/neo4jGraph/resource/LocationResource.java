package com.cargoHub.neo4jGraph.resource;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/rest/neo4j/location")
public class LocationResource {

    @Autowired
    LocationService locationService;

    @GetMapping
    //public Collection<Location> getAll() {
    public Iterable<Location> getAll() {
        //return locationService.getAll();
        return locationService.getAll();
    }
}
