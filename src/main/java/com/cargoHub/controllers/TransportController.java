package com.cargohub.controllers;

import com.cargohub.dto.IdTransferDto;
import com.cargohub.dto.TransportDetailsDto;
import com.cargohub.dto.TransporterDto;
import com.cargohub.entities.transports.TransportDetails;
import com.cargohub.entities.transports.Transporter;
import com.cargohub.entities.transports.TransporterType;
import com.cargohub.service.TransportDetailsService;
import com.cargohub.service.TransporterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final TransporterService service;
    private final TransportDetailsService transportDetailsService;

    public TransportController(TransporterService service, TransportDetailsService transportDetailsService) {
        this.service = service;
        this.transportDetailsService = transportDetailsService;
    }

    @GetMapping("{id}")
    ResponseEntity<TransporterDto> getTransporter(@PathVariable Integer id) {
        Transporter result = service.findById(id);
        return ResponseEntity.ok(TransporterDto.toDto(result));
    }

    @PostMapping
    ResponseEntity<?> createTransporter(@RequestBody TransporterDto transporterDto) {
        Transporter transporter = transporterDto.toTransporter();
        service.save(transporter);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    ResponseEntity<?> updateTransporter(@RequestBody TransporterDto transporterDto) {
        Transporter transporter = transporterDto.toTransporter();
        Transporter result = service.update(transporter);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    ResponseEntity<?> deleteTransporter(@RequestBody IdTransferDto idTransferDto) {
        service.delete(idTransferDto.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<Page<TransporterDto>> getAllTransporters(Pageable pageable) {
        Page<Transporter> transporters = service.findAll(pageable);
        Page<TransporterDto> result = transporters.map(TransporterDto::toDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/details/{id}")
    ResponseEntity<TransportDetailsDto> getTransportDetails(@PathVariable Integer id) {
        TransportDetails result = transportDetailsService.findById(id);
        return ResponseEntity.ok(TransportDetailsDto.toDto(result));
    }

    @PostMapping("/details")
    ResponseEntity<?> createTransportDetails(@RequestBody TransportDetailsDto requestDto) {
        TransportDetails details = requestDto.toTransportDetails();
        transportDetailsService.save(details);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/details")
    ResponseEntity<?> updateTransportDetails(@RequestBody TransportDetailsDto requestDto) {
        TransportDetails details = requestDto.toTransportDetails();
        transportDetailsService.update(details);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/details")
    ResponseEntity<?> deleteTransportDetails(@RequestBody TransportDetailsDto requestDto) {
        TransportDetails details = requestDto.toTransportDetails();
        transportDetailsService.findByType(details.getType());
        transportDetailsService.delete(details.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/details")
    ResponseEntity<Page<TransportDetailsDto>> getAllTransportDetails(Pageable pageable) {
        Page<TransportDetails> details = transportDetailsService.findAll(pageable);
        Page<TransportDetailsDto> result = details.map(TransportDetailsDto::toDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @RequestMapping("/types")
    ResponseEntity<List<String>> getAllTypes() {
        List<String> types = new ArrayList<>();
        for (TransporterType type : TransporterType.values()) {
            types.add(type.name());
        }
        return ResponseEntity.of(Optional.of(types));
    }

}
