package com.cargohub.service.impl;

import com.cargohub.entities.transports.TransportDetailsEntity;
import com.cargohub.entities.enums.TransporterType;
import com.cargohub.exceptions.TransportDetailsException;
import com.cargohub.repository.TransportDetailsRepository;
import com.cargohub.service.TransportDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransportDetailsServiceImpl implements TransportDetailsService {

    private final TransportDetailsRepository repository;

    @Autowired
    public TransportDetailsServiceImpl(TransportDetailsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public TransportDetailsEntity findById(Integer id) {
        TransportDetailsEntity result;
        result = repository.findById(id).orElseThrow(() -> new TransportDetailsException("TransportDetails not found"));
        return result;
    }

    @Override
    public TransportDetailsEntity findByType(TransporterType type) {
        TransportDetailsEntity result;
        result = repository.findByType(type).orElseThrow(() -> new TransportDetailsException("TransportDetails not found"));
        return result;
    }

    @Override
    public TransportDetailsEntity update(TransportDetailsEntity relation) {
        if (relation.getId() == null) {
            throw new TransportDetailsException("Illegal state for TransportDetails");
        }
        if (existsById(relation.getId())) {
            return repository.save(relation);
        }
        throw new TransportDetailsException("TransportDetails not found");
    }

    @Override
    public TransportDetailsEntity save(TransportDetailsEntity relation) {
        if (relation.getId() != null) {
            throw new TransportDetailsException("Illegal state for TransportDetails");
        }
        return repository.save(relation);
    }

    @Override
    public Page<TransportDetailsEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new TransportDetailsException("TransportDetails not found");
    }
}
