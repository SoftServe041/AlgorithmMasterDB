package com.cargohub.service;

import com.cargohub.entities.transports.TransportDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransportDetailsService {

    boolean existsById(Integer id);

    TransportDetails findById(Integer id);

    TransportDetails update(TransportDetails transportDetails);

    TransportDetails save(TransportDetails transportDetails);

    Page<TransportDetails> findAll(Pageable pageable);

    void delete(Integer id);

}
