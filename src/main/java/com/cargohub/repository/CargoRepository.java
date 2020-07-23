package com.cargohub.repository;

import com.cargohub.entities.CargoEntity;
import com.cargohub.entities.transports.CarrierCompartmentEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CargoRepository extends PagingAndSortingRepository<CargoEntity, Integer> {
    List<CargoEntity> findCargoEntitiesByCarrierCompartment(CarrierCompartmentEntity carrierCompartmentEntity);

}
