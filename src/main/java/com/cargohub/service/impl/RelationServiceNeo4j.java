package com.cargohub.service.impl;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RelationEntity;
import com.cargohub.entities.enums.TransporterType;
import com.cargohub.exceptions.HubException;
import com.cargohub.models.Location;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.RelationRepositoryNeo4j;
import com.cargohub.service.HubService;
import com.cargohub.service.RelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationServiceNeo4j {

    private final RelationRepositoryNeo4j relationRepository;
    private final HubRepository hubRepository;
    private final RelationService relationService;

    public RelationServiceNeo4j(RelationRepositoryNeo4j relationRepository, HubRepository hubRepository, RelationService relationService) {
        this.relationRepository = relationRepository;
        this.hubRepository = hubRepository;
        this.relationService = relationService;
    }

    public void createNewRelation(String connectedCity, String newCity) {
        relationRepository.createNewRelation(connectedCity, newCity);
        RelationEntity relationEntity = new RelationEntity();
        HubEntity connected = hubRepository.findByName(connectedCity).orElseThrow(() -> {
            throw new HubException("Hub not found by name: " + connectedCity);
        });
        HubEntity owner = hubRepository.findByName(newCity).orElseThrow(() -> {
            throw new HubException("Hub not found by name: " + newCity);
        });;
        relationEntity.setConnectedHub(connected);
        relationEntity.setOwnerHub(owner);
        relationEntity.setDistance(0d);
        relationEntity.setRelationType(TransporterType.TRUCK);
        relationService.save(relationEntity);
    }

    public void deleteMutualRelation(String connectedCity, String newCity) {
        relationRepository.deleteRelation(connectedCity, newCity);
        relationService.deleteByOwnerAndConnectedHubs(newCity, connectedCity);
    }

    public List<Location> getAllConnectedLocations(String city) {
        return relationRepository.getAllLocations(city);
    }

    public Double getDistanceBetweenCities(String connectedCity, String newCity) {
        return relationRepository.getDistance(connectedCity, newCity);
    }
}
