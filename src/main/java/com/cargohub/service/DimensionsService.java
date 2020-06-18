package com.cargohub.service;

import com.cargohub.entities.Dimensions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DimensionsService {

    boolean existsById(Integer id);

    Dimensions findById(Integer id);

    Dimensions update(Dimensions dimensions);

    Dimensions save(Dimensions dimensions);

    Page<Dimensions> findAll(Pageable pageable);

    void delete(Integer id);

}
