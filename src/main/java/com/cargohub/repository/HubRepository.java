package com.cargohub.repository;

import com.cargohub.entities.HubEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface HubRepository extends PagingAndSortingRepository<HubEntity, Integer> {
    Optional<HubEntity> findByName(String name);

    void deleteByName(String name);
}
