package com.cargohub.repository;

import com.cargohub.entities.transports.Truck;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TruckRepository extends PagingAndSortingRepository<Truck, Integer> {

}
