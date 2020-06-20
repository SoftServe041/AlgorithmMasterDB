package com.cargohub.service;

import com.cargohub.entities.transports.Transporter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Transporterervice {

    boolean existsById(Integer id);

    Transporter findById(Integer id);

    Transporter update(Transporter transporter);

    Transporter save(Transporter transporter);

    Page<Transporter> findAll(Pageable pageable);

    void delete(Integer id);

}
