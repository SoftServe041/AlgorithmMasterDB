package com.cargohub.service;

import com.cargohub.entities.DimensionsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DimensionsService {

    boolean existsById(Integer id);

    DimensionsEntity findById(Integer id);

    DimensionsEntity update(DimensionsEntity dimensions);

    DimensionsEntity save(DimensionsEntity dimensions);

    Page<DimensionsEntity> findAll(Pageable pageable);

    void delete(Integer id);

}
