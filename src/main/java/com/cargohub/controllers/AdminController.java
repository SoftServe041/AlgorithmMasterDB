package com.cargohub.controllers;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import com.cargohub.cargoloader.SimulationServiceImpl;
import com.cargohub.dto.UpdateHubDto;
import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RelationEntity;
import com.cargohub.entities.enums.TransporterType;
import com.cargohub.models.HubRequest;
import com.cargohub.models.Location;
import com.cargohub.service.HubService;
import com.cargohub.service.RelationService;
import com.cargohub.service.impl.LocationServiceNeo4j;
import com.cargohub.service.impl.RelationServiceNeo4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin")
public class AdminController {

    private final RelationServiceNeo4j relationServiceNeo4j;
    private final LocationServiceNeo4j locationServiceNeo4j;
    private final SimulationServiceImpl simulationService;
    private final HubService hubService;

    public AdminController(LocationServiceNeo4j locationServiceNeo4j,
                           RelationServiceNeo4j relationServiceNeo4j,
                           SimulationServiceImpl simulationService,
                           HubService hubService) {
        this.locationServiceNeo4j = locationServiceNeo4j;
        this.relationServiceNeo4j = relationServiceNeo4j;
        this.simulationService = simulationService;
        this.hubService = hubService;
    }

    @GetMapping("/hub")
    public ResponseEntity<List<Location>> getAll() {
        return ResponseEntity.ok(locationServiceNeo4j.getAll());
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

    @GetMapping("/simulation-result")
    public ResponseEntity<List<String>> getSimulationResult() throws IOException {
        List<String> transporterLogs = getTransporterInfo("demoLog.log");
        clearFile("demoLog.log");
        return ResponseEntity.ok(transporterLogs);
    }

    private static List<String> getTransporterInfo(String fileName) throws IOException {
        List<String> transportersStrings = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            while ((strLine = br.readLine()) != null)  {
                if (strLine.startsWith(" Transporter")) {
                    transportersStrings.add(strLine);
                }
            }
        }
        return transportersStrings;
    }

    private void clearFile(String fileName) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.print("");
        }
    }
}
