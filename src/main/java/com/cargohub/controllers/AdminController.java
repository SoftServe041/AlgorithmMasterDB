package com.cargohub.controllers;

import com.cargohub.dto.UpdateHubDto;
import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.HubEntity;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import com.cargohub.models.HubRequest;
import com.cargohub.models.Location;
import com.cargohub.service.HubService;
import com.cargohub.service.impl.LocationService;
import com.cargohub.service.impl.RelationService;
import com.cargohub.service.impl.TransporterServiceImpl;
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
    private final TransporterServiceImpl transporterService;
    private final HubService hubService;

    public AdminController(LocationService locationService, RelationService relationService, HubService hubService,TransporterServiceImpl transporterService) {
        this.locationService = locationService;
        this.relationService = relationService;
        this.hubService = hubService;
        this.transporterService = transporterService;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    @GetMapping("cargosByTransporter")
    public List<CargoEntity> getCargosByTransporter(@RequestParam int id) {
        List<CargoEntity> cargoEntities = new ArrayList<>();
        List<CarrierCompartmentEntity> compartments = transporterService.findById(id).getCompartments();
        for(CarrierCompartmentEntity car : compartments){
            for(CargoEntity cargoEntity : car.getCargoEntities()){
                cargoEntities.add(cargoEntity);
            }
        }
        return cargoEntities;
    }
    @PostMapping("relation")
    public void postNewRelation(@RequestBody HubRequest hubRequest) {
        relationService.createNewRelation(hubRequest.getConnectedCity(), hubRequest.getNewCity());
    }

    @PostMapping
    public ResponseEntity postNewHub(@RequestBody HubRequest hubRequest) {
        locationService.createNewCity(hubRequest.getNewCity());
        HubEntity hub = new HubEntity();
        hub.setName(hubRequest.getNewCity());
        hubService.save(hub);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping("/{name}")
    public void deleteHub(@PathVariable String name) {
        locationService.deleteCityByName(name);
        hubService.deleteByName(name);
    }

    @PatchMapping("/{name}")
    public void updateHub(@PathVariable String name, @RequestBody UpdateHubDto dto) {
        locationService.modifyCity(name, dto.getNewName());
        HubEntity hub = hubService.findByName(name);
        hub.setName(dto.getNewName());
        hubService.update(hub);
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
