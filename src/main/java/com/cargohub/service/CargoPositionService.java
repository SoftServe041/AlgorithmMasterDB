package com.cargohub.service;

import com.cargohub.entities.CargoPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CargoPositionService {

    boolean existsById(Integer id);

    CargoPosition findById(Integer id);

    CargoPosition update(CargoPosition cargoPosition);

    CargoPosition save(CargoPosition cargoPosition);

    Page<CargoPosition> findAll(Pageable pageable);

    void delete(Integer id);

}
