package com.cargohub.repository;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RelationEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface RelationRepository extends PagingAndSortingRepository<RelationEntity, Integer> {
Optional<RelationEntity> findByOwnerHubAndConnectedHub(HubEntity owner, HubEntity connected);
}
