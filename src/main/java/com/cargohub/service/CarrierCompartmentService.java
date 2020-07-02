package com.cargohub.service;

import com.cargohub.entities.transports.CarrierCompartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarrierCompartmentService {

    boolean existsById(Integer id);

    CarrierCompartment findById(Integer id);

    CarrierCompartment update(CarrierCompartment carrierCompartment);

    CarrierCompartment save(CarrierCompartment carrierCompartment);

    Page<CarrierCompartment> findAll(Pageable pageable);

    void delete(Integer id);

}
