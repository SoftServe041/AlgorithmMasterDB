package com.cargohub.service.impl;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RelationEntity;
import com.cargohub.entities.enums.TransporterType;
import com.cargohub.exceptions.HubException;
import com.cargohub.models.Location;
import com.cargohub.models.RouteModel;
import com.cargohub.repository.HubRepository;
import com.cargohub.service.HubService;
import com.cargohub.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class HubServiceImpl implements HubService {

    private final HubRepository repository;
    private final RelationService relationService;
    private final LocationServiceNeo4j locationServiceNeo4j;
    private final RelationServiceNeo4j relationServiceNeo4j;

    @Autowired
    public HubServiceImpl(HubRepository repository,
                          RelationService relationService,
                          LocationServiceNeo4j locationServiceNeo4j,
                          RelationServiceNeo4j relationServiceNeo4j) {
        this.repository = repository;
        this.relationService = relationService;
        this.locationServiceNeo4j = locationServiceNeo4j;
        this.relationServiceNeo4j = relationServiceNeo4j;
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
    public List<HubEntity> saveAll(List<HubEntity> hubs) {
        Iterable<HubEntity> hubEntities = repository.saveAll(hubs);
        return (List<HubEntity>) hubEntities;
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
        if (repository.findByName(name).isPresent()) {
            repository.deleteByName(name);
            return;
        }
        throw new HubException("Hub not found");
    }

    @Override
    public void exportAllFromNeo() {
        List<Location> allLocations = locationServiceNeo4j.getAll();
        List<HubEntity> hubs = new ArrayList<>();
        for (Location location : allLocations) {
            HubEntity hub = new HubEntity();
            hub.setName(location.getName());
            hubs.add(hub);
        }
        List<HubEntity> hubEntities = saveAll(hubs);
        Map<String, HubEntity> hubByName = new HashMap<>();
        hubEntities.forEach(hub -> hubByName.put(hub.getName(), hub));
        List<RelationEntity> relations = new ArrayList<>();
        for (HubEntity hubEntity : hubEntities) {
            List<Location> allConnectedLocations = relationServiceNeo4j.getAllConnectedLocations(hubEntity.getName());
            for (Location location : allConnectedLocations) {
                RelationEntity relationEntity = new RelationEntity();
                relationEntity.setOwnerHub(hubEntity);
                relationEntity.setConnectedHub(hubByName.get(location.getName()));
                double distanceBetweenCities = relationServiceNeo4j.
                        getDistanceBetweenCities(hubEntity.getName(), location.getName());
                relationEntity.setDistance(distanceBetweenCities);
                relationEntity.setRelationType(TransporterType.TRUCK);
                relations.add(relationEntity);
            }
        }
        relationService.saveAll(relations);
    }
}
