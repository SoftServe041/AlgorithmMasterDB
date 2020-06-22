package com.cargoHub.neo4jGraph.web;

import com.cargoHub.neo4jGraph.ErrorHandler.HubNotFoundException;
import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.model.HubRequest;

import com.cargoHub.neo4jGraph.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/location")
public class NewHubController {

    @Autowired
    private LocationService locationService;
    private Location locaton;

    @GetMapping("/{name}")
    public ResponseEntity<Location> getHubByName(@PathVariable String name) {
        try {
            Location result = locationService.searchHubByName(name);
            return ResponseEntity.ok(result);
        } catch (HubNotFoundException e) {
            return ResponseEntity.notFound().location(URI.create("/location/" + name)).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Location>> slectAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    @PostMapping("/create")
    public void postNewHub(@RequestBody HubRequest hubRequest) {
        locationService.createNewCity(hubRequest.getNewCity());
    }

    @PatchMapping("/{name}")
    public void updateHub(@PathVariable String name, @RequestParam String newName){
        locationService.modifyCity(name, newName);
    }

    @DeleteMapping("/{name}")
    public void deleteHub(@PathVariable String name) {
        locationService.deleteCityByName(name);
    }
}
