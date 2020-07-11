package com.cargohub.repository;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.transports.TransporterEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TransporterRepository extends PagingAndSortingRepository<TransporterEntity, Integer> {

    List<TransporterEntity> findAllByCurrentHub(HubEntity hub);
}
