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

    public Location createNewCity(String newCity) {
        //ToDo create a check for existing location
//        locationRepository.createNewHub(newCity);
        Location location = new Location();
        location.setName(newCity);
        locationRepository.save(location);
        locationRepository.setGeoData(newCity);
        return locationRepository.findByName(newCity);
    }

    public void createNewCityBulk() { //ToDo return list of new cities
        //ToDo create a check for existing location
//        locationRepository.createNewHub(newCity);
        Location location = new Location();
        String[] newCities = {
                "Berlin",
                "Hamburg",
                "Munich",
                "Cologne",
                "Frankfurt",
                "Essen",
                "Dortmund",
                "Stuttgart",
                "Zurich",
                "Geneva",
                "Basel",
                "Bern",
                "Lausanne",
                "Lucerne",
                "Paris",
                "Marseille",
                "Lyon",
                "Toulouse",
                "Nice",
                "Nantes",
                "Strasbourg"};

        for (String city: newCities) {
            createNewCity(city);

        }
        //return locationRepository.findByName(newCity);
    }

    public Location searchHubByName(String name) {

//        Location result = locationRepository.getHubByName(name);
        Location result = locationRepository.findByName(name);
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
