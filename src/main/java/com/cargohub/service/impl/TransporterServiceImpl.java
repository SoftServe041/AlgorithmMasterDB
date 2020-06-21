package com.cargohub.service.impl;

import com.cargohub.entities.Dimensions;
import com.cargohub.entities.Hub;
import com.cargohub.entities.transports.CarrierCompartment;
import com.cargohub.entities.transports.Transporter;
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
            return repository.save(transporter);

        }
        throw new TransporterException("Transporter not found");
    }

    @Override
    public Transporter save(Transporter transporter) {
        if (transporter.getId() != null) {
            throw new TransporterException("Illegal state for Transporter");
        }
        Dimensions dimensions = dimensionsRepository.save(transporter.getCompartments().get(0).getVolume());
        transporter.getCompartments().get(0).setVolume(dimensions);
        CarrierCompartment carrierCompartment = carrierCompartmentRepository.save(transporter.getCompartments().get(0));
        Hub hub = hubRepository.save(transporter.getCurrentHub());
        transporter.setCurrentHub(hub);
        transporter.setCompartments(List.of(carrierCompartment));
        return repository.save(transporter);
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
