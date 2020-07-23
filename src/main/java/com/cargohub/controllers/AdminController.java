package com.cargohub.controllers;

import com.cargohub.dto.CargoPositionAndDimensionDto;
import com.cargohub.dto.UpdateHubDto;
import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.HubEntity;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import com.cargohub.models.HubRequest;
import com.cargohub.models.Location;
import com.cargohub.repository.CargoRepository;
import com.cargohub.repository.CarrierCompartmentRepository;
import com.cargohub.service.HubService;
import com.cargohub.service.impl.LocationService;
import com.cargohub.service.impl.RelationService;
import com.cargohub.service.impl.TransporterServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin/hub")
public class AdminController {

    private final RelationService relationService;
    private final LocationService locationService;
    private final TransporterServiceImpl transporterService;
    private final HubService hubService;
    private final CargoRepository cargoRepository;
    private final CarrierCompartmentRepository carrierCompartmentRepository;

    public AdminController(LocationService locationService, RelationService relationService, HubService hubService, TransporterServiceImpl transporterService, CargoRepository cargoRepository, CarrierCompartmentRepository carrierCompartmentRepository) {
        this.locationService = locationService;
        this.relationService = relationService;
        this.hubService = hubService;
        this.transporterService = transporterService;
        this.cargoRepository = cargoRepository;
        this.carrierCompartmentRepository = carrierCompartmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
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
