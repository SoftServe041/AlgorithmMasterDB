package com.cargoHub.neo4jGraph.service;

import com.cargoHub.neo4jGraph.ErrorHandler.HubNotFoundException;
import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.model.HubRequest;
import com.cargoHub.neo4jGraph.model.RouteData;
import com.cargoHub.neo4jGraph.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LocationService {
    String newCity;
    String connectedCity;
    String tType;
    String hub;


    @Autowired
    LocationRepository locationRepository;
    HubRequest hubRequest;

    //public Collection<Location> getAll() {
    public Iterable<Location> getAll() {
        return locationRepository.getAllLocations();
    }

    public Iterable<Collection<Location>> getRoutes(String departure, String arrival) {
        return locationRepository.getAllRouts(departure, arrival);
    }

    public void createNewCity(String newCity, String connectedCity) {
        locationRepository.createNewHub(newCity, connectedCity);
        locationRepository.setGeoData(newCity);
    }

    public Location searchHubByName(String name) {

        Location result = locationRepository.getHubByName(name);
        if(result == null) {
            throw new HubNotFoundException("There is no hub with the name: " + name);
        }
        //return locationRepository.getHubByName(name);
        return result;
    }

    public void modifyCity(String name, String newName) {
        locationRepository.updateHub(name, newName);
    }

    public void deleteCityByName(String name) {
        locationRepository.deleteHub(name);
    }
}
