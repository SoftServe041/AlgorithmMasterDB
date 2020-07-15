package com.cargohub.service.impl;

import com.cargohub.entities.CargoEntity;
import com.cargohub.exceptions.CargoException;
import com.cargohub.repository.CargoRepository;
import com.cargohub.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CargoServiceImpl implements CargoService {

    private final CargoRepository repository;

    @Autowired
    public CargoServiceImpl(CargoRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public CargoEntity findById(Integer id) {
        CargoEntity result = repository.findById(id).orElseThrow(() -> new CargoException("Cargo not found"));
        return result;
    }

    @Override
    public CargoEntity update(CargoEntity cargoEntity) {
        if (cargoEntity.getId() == null) {
            throw new CargoException("Illegal state for Cargo");
        }
        if (existsById(cargoEntity.getId())) {
            return repository.save(cargoEntity);
        }
        throw new CargoException("Cargo not found");
    }

    @Override
    public CargoEntity save(CargoEntity cargoEntity) {
        if (cargoEntity.getId() != null) {
            throw new CargoException("Illegal state for Cargo");
        }
        return repository.save(cargoEntity);
    }

    @Override
    public Page<CargoEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new CargoException("Cargo not found");
    }
}
