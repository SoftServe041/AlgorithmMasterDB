package com.cargoHub.neo4jGraph.service;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class LocationService {

    @Autowired
    LocationRepository locationRepository;

    //public Collection<Location> getAll() {
    public Iterable<Location> getAll() {
        //return locationRepository.getAllLocations(); //findAll will work here as well
        return locationRepository.findAll(); //findAll will work here as well
        //return Collections.singleton(locationRepository.findByName("Kyiv")); //findAll will work here as well
    }
}
