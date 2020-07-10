package com.cargohub.service.impl;

import com.cargohub.entities.CargoPositionEntity;
import com.cargohub.exceptions.CargoPositionException;
import com.cargohub.repository.CargoPositionRepository;
import com.cargohub.service.CargoPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CargoPositionServiceImpl implements CargoPositionService {

    private final CargoPositionRepository repository;

    @Autowired
    public CargoPositionServiceImpl(CargoPositionRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public CargoPositionEntity findById(Integer id) {
        CargoPositionEntity result;
        result = repository.findById(id).orElseThrow(() -> new CargoPositionException("CargoPosition not found"));
        return result;
    }

    @Override
    public CargoPositionEntity update(CargoPositionEntity cargoPosition) {
        if (cargoPosition.getId() == null) {
            throw new CargoPositionException("Illegal state for CargoPosition");
        }
        if (existsById(cargoPosition.getId())) {
            return repository.save(cargoPosition);
        }
        throw new CargoPositionException("CargoPosition not found");
    }

    @Override
    public CargoPositionEntity save(CargoPositionEntity cargoPosition) {
        if (cargoPosition.getId() != null) {
            throw new CargoPositionException("Illegal state for CargoPosition");
        }
        return repository.save(cargoPosition);
    }

    @Override
    public Page<CargoPositionEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new CargoPositionException("CargoPosition not found");
    }
}
