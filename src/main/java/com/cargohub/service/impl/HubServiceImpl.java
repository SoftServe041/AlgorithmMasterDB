package com.cargohub.service.impl;

import com.cargohub.entities.HubEntity;
import com.cargohub.exceptions.HubException;
import com.cargohub.repository.HubRepository;
import com.cargohub.service.HubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class HubServiceImpl implements HubService {

    private final HubRepository repository;

    @Autowired
    public HubServiceImpl(HubRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public HubEntity findById(Integer id) {
        HubEntity result;
        result = repository.findById(id).orElseThrow(() -> new HubException("Hub not found"));
        return result;
    }

    @Override
    public HubEntity findByName(String name) {
        Optional<HubEntity> result = repository.findByName(name);
        return result.orElseThrow(() -> new HubException("Hub not found"));
    }

    @Override
    public HubEntity update(HubEntity cargo) {
        if (cargo.getId() == null) {
            throw new HubException("Illegal state for Hub");
        }
        if (existsById(cargo.getId())) {
            return repository.save(cargo);
        }
        throw new HubException("Hub not found");
    }

    @Override
    public HubEntity save(HubEntity cargo) {
        if (cargo.getId() != null) {
            throw new HubException("Illegal state for Hub");
        }
        return repository.save(cargo);
    }

    @Override
    public Page<HubEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new HubException("Hub not found");
    }

    @Override
    @Transactional
    public void deleteByName(String name) {
        if(repository.findByName(name).isPresent()){
            repository.deleteByName(name);
            return;
        }
        throw new HubException("Hub not found");
    }
}
