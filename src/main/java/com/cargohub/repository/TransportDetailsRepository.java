package com.cargohub.repository;

import com.cargohub.entities.transports.TransportDetailsEntity;
import com.cargohub.entities.enums.TransporterType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TransportDetailsRepository extends PagingAndSortingRepository<TransportDetailsEntity, Integer> {
    Optional<TransportDetailsEntity> findByType(TransporterType type);
}
