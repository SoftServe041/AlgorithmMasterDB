package com.cargohub.service;

import com.cargohub.entities.transports.CarrierCompartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarrierCompartmentService {

    boolean existsById(Integer id);

    CarrierCompartmentEntity findById(Integer id);

    CarrierCompartmentEntity update(CarrierCompartmentEntity carrierCompartment);

    CarrierCompartmentEntity save(CarrierCompartmentEntity carrierCompartment);

    Page<CarrierCompartmentEntity> findAll(Pageable pageable);

    void delete(Integer id);

}
