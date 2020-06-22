package com.cargoHub.neo4jGraph.service;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.repository.RelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationService {

    @Autowired
    RelationRepository relationRepository;

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
