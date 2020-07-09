package com.cargohub.repository;

import com.cargohub.entities.CargoEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CargoRepository extends PagingAndSortingRepository<CargoEntity, Integer> {

}
