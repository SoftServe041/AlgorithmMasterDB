package com.cargohub.service.impl;

import com.cargohub.entities.Cargo;
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
    public Cargo findById(Integer id) {
        Cargo result = repository.findById(id).orElseThrow(() -> new CargoException("Cargo not found"));
        return result;
    }

    @Override
    public Cargo update(Cargo cargo) {
        if (cargo.getId() == null) {
            throw new CargoException("Illegal state for Cargo");
        }
        if (existsById(cargo.getId())) {
            return repository.save(cargo);
        }
        throw new CargoException("Cargo not found");
    }

    @Override
    public Cargo save(Cargo cargo) {
        if (cargo.getId() != null) {
            throw new CargoException("Illegal state for Cargo");
        }
        return repository.save(cargo);
    }

    @Override
    public Page<Cargo> findAll(Pageable pageable) {
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
