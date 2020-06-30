package com.cargohub.repository;

import com.cargohub.entities.CargoPosition;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CargoPositionRepository extends PagingAndSortingRepository<CargoPosition, Integer> {

}
