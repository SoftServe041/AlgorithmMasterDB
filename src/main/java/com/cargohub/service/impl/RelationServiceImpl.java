package com.cargohub.service.impl;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RelationEntity;
import com.cargohub.exceptions.HubException;
import com.cargohub.exceptions.RelationException;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.RelationRepository;
import com.cargohub.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationServiceImpl implements RelationService {

    private final RelationRepository repository;
    private final HubRepository hubRepository;

    @Autowired
    public RelationServiceImpl(RelationRepository repository, HubRepository hubRepository) {
        this.repository = repository;
        this.hubRepository = hubRepository;
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public RelationEntity findById(Integer id) {
        RelationEntity result;
        result = repository.findById(id).orElseThrow(() -> new RelationException("Order not found"));
        return result;
    }

    @Override
    public RelationEntity update(RelationEntity relation) {
        if (relation.getId() == null) {
            throw new RelationException("Illegal state for Order");
        }
        if (existsById(relation.getId())) {
            return repository.save(relation);
        }
        throw new RelationException("Order not found");
    }

    @Override
    public RelationEntity save(RelationEntity relation) {
        if (relation.getId() != null) {
            throw new RelationException("Illegal state for Order");
        }
        return repository.save(relation);
    }

    @Override
    public List<RelationEntity> saveAll(List<RelationEntity> relations) {
        return (List<RelationEntity>) repository.saveAll(relations);
    }

    @Override
    public Page<RelationEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (existsById(id)) {
            repository.deleteById(id);
            return;
        }
        throw new RelationException("Cargo not found");
    }

    @Override
    public void deleteByOwnerAndConnectedHubs(String connectedHubName, String ownerHubName) {
        HubEntity connectedHub = hubRepository.findByName(connectedHubName).
                orElseThrow(() -> {
                    throw new HubException("Hub not found by name: " + connectedHubName);
                });
        HubEntity ownerHub = hubRepository.findByName(ownerHubName).
                orElseThrow(() -> {
                    throw new HubException("Hub not found by name: " + ownerHubName);
                });
        repository.deleteAllByConnectedHubAndOwnerHub(connectedHub, ownerHub);
    }
}
