package com.cargohub.service.impl;

import com.cargohub.entities.Dimensions;
import com.cargohub.entities.Hub;
import com.cargohub.entities.transports.CarrierCompartment;
import com.cargohub.entities.transports.Transporter;
import com.cargohub.entities.transports.TransporterStatus;
import com.cargohub.exceptions.HubException;
import com.cargohub.exceptions.TransporterException;
import com.cargohub.repository.CarrierCompartmentRepository;
import com.cargohub.repository.DimensionsRepository;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.TransporterRepository;
import com.cargohub.service.Transporterervice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransporterServiceImpl implements Transporterervice {

    private final TransporterRepository repository;
    private final HubRepository hubRepository;
    private final CarrierCompartmentRepository carrierCompartmentRepository;
    private final DimensionsRepository dimensionsRepository;

    @Autowired
    public TransporterServiceImpl(TransporterRepository repository, HubRepository hubRepository, CarrierCompartmentRepository carrierCompartmentRepository, DimensionsRepository dimensionsRepository) {
        this.repository = repository;
        this.hubRepository = hubRepository;
        this.carrierCompartmentRepository = carrierCompartmentRepository;
        this.dimensionsRepository = dimensionsRepository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public Transporter findById(Integer id) {
        Transporter result;
        result = repository.findById(id).orElseThrow(() -> new TransporterException("Transporter not found"));
        return result;
    }

    @Override
    public Transporter update(Transporter transporter) {
        if (transporter.getId() == null) {
            throw new TransporterException("Illegal state for Transporter");
        }
        if (existsById(transporter.getId())) {
            Transporter existing = repository.findById(transporter.getId()).get();
            Hub hub = hubRepository.findByName(transporter.getCurrentHub().getName()).orElseThrow(
                    () -> new HubException("Hub not found by name: " + transporter.getCurrentHub().getName()));
            transporter.setCurrentHub(hub);
            int existingCompartmentsSize = existing.getCompartments().size();
            int currentCompartmentsSize = transporter.getCompartments().size();
            if (existingCompartmentsSize != currentCompartmentsSize) {
                if (existingCompartmentsSize < currentCompartmentsSize) {
                    for (int i = 0; i < existingCompartmentsSize; i++) {
                        existing.getCompartments().set(i, transporter.getCompartments().get(i));
                    }
                    for (int i = 0; i < currentCompartmentsSize - existingCompartmentsSize; i++) {
                        existing.getCompartments().add(transporter.getCompartments().get(existingCompartmentsSize + i));
                    }
                } else {
                    List<CarrierCompartment> newCompartments = new ArrayList<>();
                    for (int i = 0; i < currentCompartmentsSize; i++) {
                        transporter.getCompartments().get(i).setId(existing.getCompartments().get(i).getId());
                        newCompartments.add(transporter.getCompartments().get(i));
                    }
                    transporter.setCompartments(newCompartments);
                }
            }
            transporter.setStatus(existing.getStatus());
            transporter.setRoute(existing.getRoute());
            for (CarrierCompartment c : transporter.getCompartments()) {
                carrierCompartmentRepository.save(c);
            }
            return repository.save(transporter);

        }
        throw new TransporterException("Transporter not found");
    }

    @Override
    public Transporter save(Transporter transporter) {
        if (transporter.getId() != null) {
            throw new TransporterException("Illegal state for Transporter");
        }
        for (CarrierCompartment compartment : transporter.getCompartments()) {
            Dimensions dimensions = dimensionsRepository.save(compartment.getVolume());
            compartment.setVolume(dimensions);
            compartment.setFreeSpace(100d);
            compartment = carrierCompartmentRepository.save(compartment);
        }
        Hub hub = hubRepository.findByName(transporter.getCurrentHub().getName()).orElseThrow(
                () -> new HubException("Hub not found by name: " + transporter.getCurrentHub().getName()));
        transporter.setCurrentHub(hub);
        transporter.setStatus(TransporterStatus.WAITING);
        Transporter result = repository.save(transporter);
        for (CarrierCompartment compartment : transporter.getCompartments()) {
            compartment.setTransporter(result);
            carrierCompartmentRepository.save(compartment);
        }
        return result;
    }

    @Override
    public Page<Transporter> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new TransporterException("Transporter not found");
    }
}
