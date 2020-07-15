package com.cargohub.repository;

import com.cargohub.entities.CargoPositionEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CargoPositionRepository extends PagingAndSortingRepository<CargoPositionEntity, Integer> {

}
