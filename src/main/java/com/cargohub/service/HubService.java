package com.cargohub.service;

import com.cargohub.entities.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubService {

    boolean existsById(Integer id);

    Hub findById(Integer id);

    Hub update(Hub hub);

    Hub save(Hub hub);

    Page<Hub> findAll(Pageable pageable);

    void delete(Integer id);

}
