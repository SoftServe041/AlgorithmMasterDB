package com.cargohub.controllers;

import com.cargohub.dto.TruckRequestDto;
import com.cargohub.entities.transports.TransportType;
import com.cargohub.entities.transports.Truck;
import com.cargohub.service.TruckService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin/transport")
public class TransportController {

    private final ModelMapper mapper;
    private final TruckService service;

    public TransportController(ModelMapper mapper, TruckService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping
    ResponseEntity<?> createTruck(@RequestBody TruckRequestDto truckRequestDto) {

        Truck truck = truckRequestDto.toTruck();
        service.save(truck);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    @RequestMapping("/types")
    ResponseEntity<List<String>> getAllTypes() {
        List<String> types = new ArrayList<>();
        for (TransportType type : TransportType.values()) {
            types.add(type.name());
        }
        return ResponseEntity.of(Optional.of(types));
    }

}
