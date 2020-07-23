package com.cargohub.service;

import com.cargohub.entities.transports.TransportDetailsEntity;
import com.cargohub.entities.enums.TransporterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransportDetailsService {

    boolean existsById(Integer id);

    TransportDetailsEntity findById(Integer id);

    TransportDetailsEntity findByType(TransporterType type);

    TransportDetailsEntity update(TransportDetailsEntity transportDetails);

    TransportDetailsEntity save(TransportDetailsEntity transportDetails);

    Page<TransportDetailsEntity> findAll(Pageable pageable);

    void delete(Integer id);

}
