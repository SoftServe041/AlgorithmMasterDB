package com.cargohub.controllers;

import com.cargohub.dto.UpdateHubDto;
import com.cargohub.entities.HubEntity;
import com.cargohub.models.HubRequest;
import com.cargohub.models.Location;
import com.cargohub.service.HubService;
import com.cargohub.service.impl.LocationService;
import com.cargohub.service.impl.RelationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin/hub")
public class AdminController {

    private final RelationService relationService;
    private final LocationService locationService;
    private final HubService hubService;

    public AdminController(LocationService locationService, RelationService relationService, HubService hubService) {
        this.locationService = locationService;
        this.relationService = relationService;
        this.hubService = hubService;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    @PostMapping("relation")
    public ResponseEntity postNewRelation(@RequestBody HubRequest hubRequest) {
        relationService.createNewRelation(hubRequest.getConnectedCity(), hubRequest.getNewCity());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity postNewHub(@RequestBody HubRequest hubRequest) {
        locationService.createNewCity(hubRequest.getNewCity());
        HubEntity hub = new HubEntity();
        hub.setName(hubRequest.getNewCity());
        hubService.save(hub);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping("/{hubName}")
    public void deleteHub(@PathVariable String hubName) {
        locationService.deleteCityByName(hubName);
        hubService.deleteByName(hubName);
    }

    @PatchMapping("/{hubName}")
    public ResponseEntity updateHub(@PathVariable String hubName, @RequestBody UpdateHubDto dto) {
        locationService.modifyCity(hubName, dto.getNewName());
        HubEntity hub = hubService.findByName(hubName);
        hub.setName(dto.getNewName());
        hubService.update(hub);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("relation")
    public ResponseEntity deleteRelation(@RequestBody HubRequest hubRequest) {
        relationService.deleteMutualRelation(hubRequest.getConnectedCity(), hubRequest.getNewCity());
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("relation/{hubName}")
    public ResponseEntity<List<Location>> getAllConnectedHubs(@PathVariable String hubName) {
        return ResponseEntity.ok(relationService.getAllConnectedLocations(hubName));
    }

    @PostMapping("/neo4j/to/mysql")
    public ResponseEntity importAllHubsFromNeoToMysql() {
        List<Location> allLocations = locationService.getAll();
        List<HubEntity> hubs = new ArrayList<>();
        for (Location location : allLocations) {
            HubEntity hub = new HubEntity();
            hub.setName(location.getName());
            hubs.add(hub);
        }
        hubService.saveAll(hubs);
        return new ResponseEntity(HttpStatus.OK);
    }
}
