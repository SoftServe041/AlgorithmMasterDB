package com.cargohub.service;

import com.cargohub.entities.transports.TransporterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransporterService {

    boolean existsById(Integer id);

    TransporterEntity findById(Integer id);

    TransporterEntity update(TransporterEntity transporter);

    TransporterEntity save(TransporterEntity transporter);

    Page<TransporterEntity> findAll(Pageable pageable);

    void delete(Integer id);

}
