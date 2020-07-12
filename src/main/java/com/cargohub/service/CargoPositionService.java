package com.cargohub.service;

import com.cargohub.entities.CargoPositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CargoPositionService {

    boolean existsById(Integer id);

    CargoPositionEntity findById(Integer id);

    CargoPositionEntity update(CargoPositionEntity cargoPosition);

    CargoPositionEntity save(CargoPositionEntity cargoPosition);

    Page<CargoPositionEntity> findAll(Pageable pageable);

    void delete(Integer id);

}
