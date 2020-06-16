package com.cargoHub.neo4jGraph.web;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.model.HubRequest;
import com.cargoHub.neo4jGraph.model.RouteData;
import com.cargoHub.neo4jGraph.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/hub")
public class NewHubController {

    @Autowired
    private LocationService locationService;
    private Location locaton;

    @GetMapping("/findroute")
    public Iterable<Collection<Location>> getLocations(@RequestParam String departure, @RequestParam String arrival) {
        return locationService.getRoutes(departure, arrival);
    }

    @GetMapping("/select")
    public Location getHubByName(@RequestParam String name) {
        return (locationService.searchHubByName(name));
    }

    @GetMapping("/selectall")
    public Iterable<Location> getLocations() {
        return locationService.getAll();
    }

    @PostMapping("/create")
    public void postNewHub(@RequestBody HubRequest hubRequest) {
        locationService.createNewCity(hubRequest.getNewCity(), hubRequest.getConnectedCity());
    }

    @PatchMapping("/update")
    public void updateHub(@RequestParam String name, @RequestParam String newName) {
        locationService.modifyCity(name, newName);
    }

    @DeleteMapping("/delete")
    public void deleteHub(@RequestParam String name) {
        locationService.deleteCityByName(name);
    }
}
