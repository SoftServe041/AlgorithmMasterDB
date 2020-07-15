package com.cargohub.service;

import com.cargohub.entities.CargoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CargoService {

    boolean existsById(Integer id);

    CargoEntity findById(Integer id);

    CargoEntity update(CargoEntity cargoEntity);

    CargoEntity save(CargoEntity cargoEntity);

    Page<CargoEntity> findAll(Pageable pageable);

    void delete(Integer id);

}
