package com.cargohub.neo4jGraph.web;

import com.cargohub.neo4jGraph.model.Location;
import com.cargohub.neo4jGraph.service.LocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/location")
public class LocationController {

    private LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/getall")
    public Iterable<Location> getLocations() {
        return locationService.getAll();
    }

}
