package com.cargohub.controllers;

import com.cargohub.dto.TransporterRequestDto;
import com.cargohub.dto.jar.OrderResponseDto;
import com.cargohub.entities.transports.TransportType;
import com.cargohub.entities.transports.Transporter;
import com.cargohub.service.Transporterervice;
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
    private final Transporterervice service;

    public TransportController(ModelMapper mapper, Transporterervice service) {
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping
    ResponseEntity<?> createTruck(@RequestBody TransporterRequestDto transporterRequestDto) {

        Transporter transporter = transporterRequestDto.toTruck();
        service.save(transporter);
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
