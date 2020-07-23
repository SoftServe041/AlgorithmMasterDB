package com.cargohub.controllers;

import com.cargohub.cargoloader.SimulationServiceImpl;
import com.cargohub.dto.CargoPositionAndDimensionDto;
import com.cargohub.dto.UpdateHubDto;
import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import com.cargohub.exceptions.LogsClearException;
import com.cargohub.models.HubRequest;
import com.cargohub.models.Location;
import com.cargohub.repository.CarrierCompartmentRepository;
import com.cargohub.service.HubService;
import com.cargohub.service.impl.LocationServiceNeo4j;
import com.cargohub.service.impl.RelationServiceNeo4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin")
public class AdminController {

    private final RelationServiceNeo4j relationServiceNeo4j;
    private final LocationServiceNeo4j locationServiceNeo4j;
    private final SimulationServiceImpl simulationService;
    private final HubService hubService;
    private final CarrierCompartmentRepository carrierCompartmentRepository;

    public AdminController(LocationServiceNeo4j locationServiceNeo4j,
                           RelationServiceNeo4j relationServiceNeo4j,
                           SimulationServiceImpl simulationService,
                           HubService hubService,
                           CarrierCompartmentRepository carrierCompartmentRepository) {
        this.locationServiceNeo4j = locationServiceNeo4j;
        this.relationServiceNeo4j = relationServiceNeo4j;
        this.simulationService = simulationService;
        this.hubService = hubService;
        this.carrierCompartmentRepository = carrierCompartmentRepository;
    }

    @GetMapping("/hub")
    public ResponseEntity<List<Location>> getAll() {
        return ResponseEntity.ok(locationServiceNeo4j.getAll());
    }

    @GetMapping("/cargosByTransporter")
    public List<CargoPositionAndDimensionDto> getCargosByTransporter(@RequestParam int id) {
        List<CargoEntity> cargoEntities = new ArrayList<>();
        List<CarrierCompartmentEntity> compartments = carrierCompartmentRepository.findAllByTransporterId(id);
        for (CarrierCompartmentEntity car : compartments) {
            cargoEntities.addAll(car.getCargoEntities());
        }
        return cargoEntities.stream().map(CargoPositionAndDimensionDto::cargoToCarPos).collect(Collectors.toList());
    }


    @PostMapping("/hub/relation")
    public ResponseEntity postNewRelation(@RequestBody HubRequest hubRequest) {
        relationServiceNeo4j.createNewRelation(hubRequest.getConnectedCity(), hubRequest.getNewCity());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/hub")
    public ResponseEntity postNewHub(@RequestBody HubRequest hubRequest) {
        locationServiceNeo4j.createNewCity(hubRequest.getNewCity());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping("/hub/{hubName}")
    public void deleteHub(@PathVariable String hubName) {
        locationServiceNeo4j.deleteCityByName(hubName);
    }

    @PatchMapping("/hub/{hubName}")
    public ResponseEntity updateHub(@PathVariable String hubName, @RequestBody UpdateHubDto dto) {
        locationServiceNeo4j.modifyCity(hubName, dto.getNewName());
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/hub/relation")
    public ResponseEntity deleteRelation(@RequestBody HubRequest hubRequest) {
        relationServiceNeo4j.deleteMutualRelation(hubRequest.getConnectedCity(), hubRequest.getNewCity());
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/hub/relation/{hubName}")
    public ResponseEntity<List<Location>> getAllConnectedHubs(@PathVariable String hubName) {
        return ResponseEntity.ok(relationServiceNeo4j.getAllConnectedLocations(hubName));
    }

    @PostMapping("/neo4j/to/mysql")
    public ResponseEntity importAllHubsFromNeoToMysql() {
        hubService.exportAllFromNeo();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/hub/transports/{hubName}")
    public ResponseEntity initTransportersInHub(@PathVariable String hubName) {
        simulationService.initTransportersInHub(hubName);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/simulation")
    public ResponseEntity simulation() {
        simulationService.simulate();
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @GetMapping("/clear/db")
    public ResponseEntity clearAllSimulationData() {
        simulationService.clearDatabase();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/clear")
    public ResponseEntity clearLogs() {
        try (PrintWriter writer = new PrintWriter("demoLog.log")) {
            writer.print("");
        } catch (FileNotFoundException e) {
            throw new LogsClearException("Couldn't find log file");
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
