package com.cargohub.neo4jGraph.web;

import com.cargohub.neo4jGraph.ErrorHandler.HubNotFoundException;
import com.cargohub.neo4jGraph.model.HubRequest;
import com.cargohub.neo4jGraph.model.Location;
import com.cargohub.neo4jGraph.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/location")
@CrossOrigin(origins = "*")
public class NewHubController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/{name}")
    public ResponseEntity<Location> getHubByName(@PathVariable String name) {
        try {
            Location result = locationService.searchHubByName(name);
            return ResponseEntity.ok(result);
        } catch (HubNotFoundException e) {
            return ResponseEntity.notFound().location(URI.create("/location/" + name)).build();
        }
    }

    @PostMapping("/create")
    public void postNewHub(@RequestBody HubRequest hubRequest) {
        locationService.createNewCity(hubRequest.getNewCity());
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
