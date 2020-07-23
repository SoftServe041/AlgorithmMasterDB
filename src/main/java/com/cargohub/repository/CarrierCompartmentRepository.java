package com.cargohub.repository;

import com.cargohub.entities.transports.CarrierCompartmentEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CarrierCompartmentRepository extends PagingAndSortingRepository<CarrierCompartmentEntity, Integer> {
    List<CarrierCompartmentEntity> findAllByTransporterId(int id);
}
