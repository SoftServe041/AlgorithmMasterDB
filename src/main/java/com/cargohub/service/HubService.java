package com.cargohub.service;

import com.cargohub.entities.HubEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubService {

    boolean existsById(Integer id);

    HubEntity findById(Integer id);

    HubEntity findByName(String name);

    HubEntity update(HubEntity hub);

    HubEntity save(HubEntity hub);

    Page<HubEntity> findAll(Pageable pageable);

    void delete(Integer id);

    void deleteByName(String name);

}
