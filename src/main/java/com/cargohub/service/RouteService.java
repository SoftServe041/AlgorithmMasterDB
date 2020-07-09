package com.cargohub.service;

import com.cargohub.entities.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RouteService {

    boolean existsById(Integer id);

    Route findById(Integer id);

    Route update(Route route);

    Route save(Route route);

    Page<Route> findAll(Pageable pageable);

    void delete(Integer id);

}
