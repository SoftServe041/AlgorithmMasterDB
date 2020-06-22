package com.cargohub.neo4jGraph.web;

import com.cargohub.neo4jGraph.ErrorHandler.HubNotFoundException;
import com.cargohub.neo4jGraph.model.HubRequest;
import com.cargohub.neo4jGraph.model.Location;
import com.cargohub.neo4jGraph.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/location")
@CrossOrigin(origins = "*")
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
        locationService.createNewCity(hubRequest.getNewCity(), hubRequest.getConnectedCity());
    }

    @PatchMapping("/{name}")
    public void updateHub(@PathVariable String name, @RequestParam String newName) {
        locationService.modifyCity(name, newName);
    }

    @DeleteMapping("/{name}")
    public void deleteHub(@PathVariable String name) {
        locationService.deleteCityByName(name);
    }
}
