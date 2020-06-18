package com.cargohub.service.impl;

import com.cargohub.entities.Dimensions;
import com.cargohub.entities.Hub;
import com.cargohub.entities.transports.CarrierCompartment;
import com.cargohub.entities.transports.Truck;
import com.cargohub.exceptions.TruckException;
import com.cargohub.repository.CarrierCompartmentRepository;
import com.cargohub.repository.DimensionsRepository;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.TruckRepository;
import com.cargohub.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckServiceImpl implements TruckService {

    private final TruckRepository repository;
    private final HubRepository hubRepository;
    private final CarrierCompartmentRepository carrierCompartmentRepository;
    private final DimensionsRepository dimensionsRepository;

    @Autowired
    public TruckServiceImpl(TruckRepository repository, HubRepository hubRepository, CarrierCompartmentRepository carrierCompartmentRepository, DimensionsRepository dimensionsRepository) {
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
    public Truck findById(Integer id) {
        Truck result;
        result = repository.findById(id).orElseThrow(() -> new TruckException("Truck not found"));
        return result;
    }

    @Override
    public Truck update(Truck truck) {
        if (truck.getId() == null) {
            throw new TruckException("Illegal state for Truck");
        }
        if (existsById(truck.getId())) {
            return repository.save(truck);

        }
        throw new TruckException("Truck not found");
    }

    @Override
    public Truck save(Truck truck) {
        if (truck.getId() != null) {
            throw new TruckException("Illegal state for Truck");
        }
        Dimensions dimensions = dimensionsRepository.save(truck.getCompartments().get(0).getVolume());
        truck.getCompartments().get(0).setVolume(dimensions);
        CarrierCompartment carrierCompartment = carrierCompartmentRepository.save(truck.getCompartments().get(0));
        Hub hub = hubRepository.save(truck.getCurrentHub());
        truck.setCurrentHub(hub);
        truck.setCompartments(List.of(carrierCompartment));
        return repository.save(truck);
    }

    @Override
    public Page<Truck> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new TruckException("Truck not found");
    }
}
