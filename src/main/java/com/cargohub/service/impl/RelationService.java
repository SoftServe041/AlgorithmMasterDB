package com.cargohub.service.impl;

import com.cargohub.models.Location;
import com.cargohub.repository.RelationRepositoryNeo4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationService {

    @Autowired
    RelationRepositoryNeo4j relationRepository;

    public void createNewRelation(String connectedCity, String newCity) {
        relationRepository.createNewRelation(connectedCity, newCity);
    }

    public void deleteMutualRelation(String connectedCity, String newCity) {
        relationRepository.deleteRelation(connectedCity, newCity);
    }

    public List<Location> getAllConnectedLocations(String city) {
        return relationRepository.getAllLocations(city);
    }
}
