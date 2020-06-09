package com.cargoHub.neo4jGraph.web;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@RestController
//@RequestMapping("/location")
@RequestMapping("/location")
public class LocationController {

    private LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    //public Collection<Location> getLocations() {
    public Iterable<Location> getLocations() {
        return locationService.getAll();
    }
}
