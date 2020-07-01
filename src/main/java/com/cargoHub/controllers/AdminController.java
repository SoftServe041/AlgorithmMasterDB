package com.cargohub.controllers;

import com.cargohub.dto.UpdateHubDto;
import com.cargohub.neo4jGraph.model.HubRequest;
import com.cargohub.neo4jGraph.model.Location;
import com.cargohub.service.impl.LocationService;
import com.cargohub.service.impl.RelationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin/hub")
public class AdminController {

    private final RelationService relationService;
    private final LocationService locationService;

    public AdminController(LocationService locationService, RelationService relationService) {
        this.locationService = locationService;
        this.relationService = relationService;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    @PostMapping("relation")
    public void postNewRelation(@RequestBody HubRequest hubRequest) {
        relationService.createNewRelation(hubRequest.getConnectedCity(), hubRequest.getNewCity());
    }

    @PostMapping
    public ResponseEntity postNewHub(@RequestBody HubRequest hubRequest) {
        locationService.createNewCity(hubRequest.getNewCity());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping("/{name}")
    public void deleteHub(@PathVariable String name) {
        locationService.deleteCityByName(name);
    }

    @PatchMapping("/{name}")
    public void updateHub(@PathVariable String name, @RequestBody UpdateHubDto dto) {
        locationService.modifyCity(name, dto.getNewName());
    }

    @DeleteMapping("relation")
    public void deleteRelation(@RequestBody HubRequest hubRequest) {
        relationService.deleteMutualRelation(hubRequest.getConnectedCity(), hubRequest.getNewCity());
    }

    @GetMapping("relation/{name}")
    public ResponseEntity<List<Location>> getAllConnectedHubs(@PathVariable String name) {
        return ResponseEntity.ok(relationService.getAllConnectedLocations(name));
    }
}
