package com.cargohub.repository;

import com.cargohub.entities.Cargo;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CargoRepository extends PagingAndSortingRepository<Cargo, Integer> {

}
