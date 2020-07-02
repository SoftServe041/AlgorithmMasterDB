package com.cargohub.repository;


import com.cargohub.models.Location;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface LocationRepository extends Neo4jRepository<Location, Long> {

/**
 * Methods createNewHub() and setGeoData() form the query: create a hub and
 * set longitude and latitude.
 * */
    @Query("CREATE (l:City {name:$newCity})" )
    void createNewHub(String newCity);

    @Query("MATCH (a:City {name: $newCity})\n" +
            "CALL apoc.spatial.geocodeOnce($newCity) YIELD location\n" +
            "SET a.latitude = location.latitude\n" +
            "SET a.longitude = location.longitude;")
    void setGeoData(String newCity);

    /**
     * This is to return list of all hubs.
     */
    @Query("MATCH (a) RETURN a;")
    List<Location> getAllLocations();


    /**
     * This method is for selecting a single existing hub from the graph.
     */
    @Query("MATCH (a:City{name: $name}) RETURN a;")
    Location getHubByName(String name);


    /**
     * This method updates the name of existing hub
     */
    @Query("MATCH (n:City {name: $name})\n" +
            "SET n.name = $newName;")
    void updateHub(String name, String newName);


    /**
     * This method is for deleting existing hub with relations.
     */
    @Query("MATCH (n:City {name: $name})\n" +
            "DETACH DELETE n;")
    void deleteHub(String name);

}
