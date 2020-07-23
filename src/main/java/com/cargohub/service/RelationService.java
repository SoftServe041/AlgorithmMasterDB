package com.cargohub.service;

import com.cargohub.entities.RelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RelationService {

    boolean existsById(Integer id);

    RelationEntity findById(Integer id);

    RelationEntity update(RelationEntity relation);

    RelationEntity save(RelationEntity relation);

    List<RelationEntity> saveAll(List<RelationEntity> relations);

    Page<RelationEntity> findAll(Pageable pageable);

    void delete(Integer id);

    void deleteByOwnerAndConnectedHubs(String connectedHub, String ownerHub);
}
