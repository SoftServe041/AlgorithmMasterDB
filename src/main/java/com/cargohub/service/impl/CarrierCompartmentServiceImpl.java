package com.cargohub.service.impl;

import com.cargohub.entities.transports.CarrierCompartmentEntity;
import com.cargohub.exceptions.CarrierCompartmentException;
import com.cargohub.repository.CarrierCompartmentRepository;
import com.cargohub.service.CarrierCompartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarrierCompartmentServiceImpl implements CarrierCompartmentService {

    private final CarrierCompartmentRepository repository;

    @Autowired
    public CarrierCompartmentServiceImpl(CarrierCompartmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public CarrierCompartmentEntity findById(Integer id) {
        CarrierCompartmentEntity result;
        result = repository.findById(id).orElseThrow(() -> new CarrierCompartmentException("CarrierCompartment not found"));
        return result;

    }

    @Override
    public CarrierCompartmentEntity update(CarrierCompartmentEntity carrierCompartment) {
        if (carrierCompartment.getId() == null) {
            throw new CarrierCompartmentException("Illegal state for CarrierCompartment");
        }
        if (existsById(carrierCompartment.getId())) {
            return repository.save(carrierCompartment);
        }
        throw new CarrierCompartmentException("CarrierCompartment not found");
    }

    @Override
    public CarrierCompartmentEntity save(CarrierCompartmentEntity carrierCompartment) {
        if (carrierCompartment.getId() != null) {
            throw new CarrierCompartmentException("Illegal state for CarrierCompartment");
        }
        return repository.save(carrierCompartment);
    }

    @Override
    public Page<CarrierCompartmentEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new CarrierCompartmentException("CarrierCompartment not found");
    }
}
