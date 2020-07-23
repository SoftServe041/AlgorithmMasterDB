package com.cargohub.service.impl;

import com.cargohub.entities.HubEntity;
import com.cargohub.exceptions.HubException;
import com.cargohub.exceptions.HubNotFoundException;
import com.cargohub.models.Location;
import com.cargohub.repository.HubRepository;
import com.cargohub.repository.LocationRepository;
import com.cargohub.service.HubService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceNeo4j {

    private final LocationRepository locationRepository;
    private final HubRepository hubRepository;

    public LocationServiceNeo4j(LocationRepository locationRepository, HubRepository hubRepository) {
        this.locationRepository = locationRepository;
        this.hubRepository = hubRepository;
    }

    public List<Location> getAll() {
        return locationRepository.getAllLocations();
    }

    public void createNewCity(String newCity) {
        locationRepository.createNewHub(newCity);
        locationRepository.setGeoData(newCity);
        HubEntity hub = new HubEntity();
        hub.setName(newCity);
        hubRepository.save(hub);
    }

    public Location searchHubByName(String name) {

        Location result = locationRepository.getHubByName(name);
        if (result == null) {
            throw new HubNotFoundException("There is no hub with the name: " + name);
        }
        return result;
    }

    public void modifyCity(String name, String newName) {
        if (locationRepository.getHubByName(name) == null) {
            throw new HubNotFoundException("There is no hub with the name: " + name);
        }
        locationRepository.updateHub(name, newName);
        HubEntity hub = hubRepository.findByName(name).orElseThrow(() -> {
            throw new HubException("Hub not found by name: " + name);
        });
        hub.setName(newName);
        hubRepository.save(hub);
    }

    public void deleteCityByName(String name) {
        locationRepository.deleteHub(name);
        hubRepository.deleteByName(name);
    }

}
