package com.cargohub.repository;

import com.cargohub.entities.transports.TransportDetails;
import com.cargohub.entities.transports.TransporterType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TransportDetailsRepository extends PagingAndSortingRepository<TransportDetails, Integer> {
    Optional<TransportDetails> findByType(TransporterType type);
}
