package com.cargohub.service;

import com.cargohub.entities.transports.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TruckService {

    boolean existsById(Integer id);

    Truck findById(Integer id);

    Truck update(Truck transportDetails);

    Truck save(Truck transportDetails);

    Page<Truck> findAll(Pageable pageable);

    void delete(Integer id);

}
