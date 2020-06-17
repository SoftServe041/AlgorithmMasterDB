package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.model.Route;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface LocationRepository extends Neo4jRepository<Location, Long> {

/**
 * Methods createNewHub() and setGeoData() form the query: create a hub and
 * set longitude and latitude.
 * */
    @Query("MATCH (a:City {name:$connectedCity})\n" +
            "CREATE (l:City {name:$newCity})\n" +
            "CREATE (l) -[:NEXT]-> (a)\n" +
            "CREATE (l) <-[:NEXT]- (a);\n" )
    void createNewHub(String newCity, String connectedCity);

    @Query("MATCH (a:City {name: $newCity})\n" +
            "CALL apoc.spatial.geocodeOnce($newCity) YIELD location\n" +
            "SET a.latitude = location.latitude\n" +
            "SET a.longitude = location.longitude;")
    void setGeoData(String newCity);


    /*ToDo by MV:
    if a city already exists:
    if (getHubByName(name1) != null) // what will be returned here?
    "MATCH (a:City {name: $name1}), (b:City {name: $name2})\n" +
    "CREATE (b) -[:NEXT]-> (a)\n" +
    "CREATE (a) -[:NEXT]-> (b);"
    after that >>> setGeoData();*/

/**
 * This is to return list of all hubs.
 * */
    @Query("MATCH (a) RETURN a;")
    List<Location> getAllLocations();


/**
 * This method is for selecting a single existing hub from the graph.
 * */
    @Query("MATCH (a:City{name: $name}) RETURN a;")
    Location getHubByName(String name);


/**
 * This method updates the name of existing hub
 * */
    @Query("MATCH (n:City {name: $name})\n" +
            "SET n.name = $newName;")
    void updateHub(String name, String newName);


/**
 * This method is for deleting existing hub with relations.
 * */
    @Query("MATCH (n:City {name: $name})\n" +
            "DETACH DELETE n;")
    void deleteHub(String name);

}
