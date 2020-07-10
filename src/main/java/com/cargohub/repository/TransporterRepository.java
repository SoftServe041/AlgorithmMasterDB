package com.cargohub.repository;

import com.cargohub.entities.transports.TransporterEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransporterRepository extends PagingAndSortingRepository<TransporterEntity, Integer> {

}
