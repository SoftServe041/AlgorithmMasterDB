package com.cargohub.service;

import com.cargohub.entities.RouteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RouteService {

    boolean existsById(Integer id);

    RouteEntity findById(Integer id);

    RouteEntity update(RouteEntity route);

    RouteEntity save(RouteEntity route);

    Page<RouteEntity> findAll(Pageable pageable);

    void delete(Integer id);

}
