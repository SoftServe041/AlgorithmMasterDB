package com.cargohub.service;

import com.cargohub.entities.HubEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HubService {

    boolean existsById(Integer id);

    HubEntity findById(Integer id);

    HubEntity findByName(String name);

    HubEntity update(HubEntity hub);

    HubEntity save(HubEntity hub);

    Page<HubEntity> findAll(Pageable pageable);

    List<HubEntity> saveAll(List<HubEntity> hubs);

    void delete(Integer id);

    void deleteByName(String name);

    void exportAllFromNeo();
}
