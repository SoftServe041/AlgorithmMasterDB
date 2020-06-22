package com.cargoHub.neo4jGraph.service;

import com.cargoHub.neo4jGraph.ErrorHandler.HubNotFoundException;
import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    LocationRepository locationRepository;

    public List<Location> getAll() {
        return locationRepository.getAllLocations();
    }

    public void createNewCity(String newCity) {
        locationRepository.createNewHub(newCity);
        locationRepository.setGeoData(newCity);
    }

    public Location searchHubByName(String name) {

        Location result = locationRepository.getHubByName(name);
        if(result == null) {
            throw new HubNotFoundException("There is no hub with the name: " + name);
        }
        return result;
    }

    public void modifyCity(String name, String newName) {
        if(locationRepository.getHubByName(name) == null) {
            throw new HubNotFoundException("There is no hub with the name: " + name);
        }
        locationRepository.updateHub(name, newName);
    }

    public void deleteCityByName(String name) {
        locationRepository.deleteHub(name);
    }

}
