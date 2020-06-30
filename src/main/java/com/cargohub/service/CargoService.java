package com.cargohub.service;

import com.cargohub.entities.Cargo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CargoService {

    boolean existsById(Integer id);

    Cargo findById(Integer id);

    Cargo update(Cargo cargo);

    Cargo save(Cargo cargo);

    Page<Cargo> findAll(Pageable pageable);

    void delete(Integer id);

}
