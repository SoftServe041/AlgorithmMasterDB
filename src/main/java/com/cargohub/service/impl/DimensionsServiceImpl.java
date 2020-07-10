package com.cargohub.service.impl;

import com.cargohub.entities.DimensionsEntity;
import com.cargohub.exceptions.DimensionsException;
import com.cargohub.repository.DimensionsRepository;
import com.cargohub.service.DimensionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DimensionsServiceImpl implements DimensionsService {

    private final DimensionsRepository repository;

    @Autowired
    public DimensionsServiceImpl(DimensionsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public DimensionsEntity findById(Integer id) {
        DimensionsEntity result;
        result = repository.findById(id).orElseThrow(() -> new DimensionsException("Dimensions not found"));
        return result;
    }

    @Override
    public DimensionsEntity update(DimensionsEntity dimensions) {
        if (dimensions.getId() == null) {
            throw new DimensionsException("Illegal state for Dimensions");
        }
        if (existsById(dimensions.getId())) {
            return repository.save(dimensions);
        }
        throw new DimensionsException("Dimensions not found");
    }

    @Override
    public DimensionsEntity save(DimensionsEntity dimensions) {
        if (dimensions.getId() != null) {
            throw new DimensionsException("Illegal state for Dimensions");
        }
        return repository.save(dimensions);
    }

    @Override
    public Page<DimensionsEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new DimensionsException("Dimensions not found");
    }
}
