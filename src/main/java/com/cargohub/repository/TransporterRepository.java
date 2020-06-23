package com.cargohub.repository;

import com.cargohub.entities.transports.Transporter;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransporterRepository extends PagingAndSortingRepository<Transporter, Integer> {

}
