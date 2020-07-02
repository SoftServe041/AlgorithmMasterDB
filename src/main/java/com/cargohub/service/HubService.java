package com.cargohub.service;

import com.cargohub.entities.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface HubService {

    boolean existsById(Integer id);

    Hub findById(Integer id);

    Hub findByName(String name);

    Hub update(Hub hub);

    Hub save(Hub hub);

    Page<Hub> findAll(Pageable pageable);

    void delete(Integer id);

    void deleteByName(String name);

}
