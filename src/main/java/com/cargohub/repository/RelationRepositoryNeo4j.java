package com.cargohub.repository;

import com.cargohub.models.Location;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

/**
 * Pls keep in mind that all connections are mutual by default.
 * The type of the relation is hardcoded to :NEXT.
 * */
public interface RelationRepositoryNeo4j extends Neo4jRepository<Location, Long> {

/**
 * This method permits to create a mutual connection between two locations.
 * */
    @Query("MATCH (a:City {name:$connectedCity}), (l:City {name:$newCity})\n" +
            "CREATE (l) -[:NEXT]-> (a)\n" +
            "CREATE (l) <-[:NEXT]- (a);")
    void createNewRelation(String connectedCity, String newCity);

/**
 * This method deletes the existing mutual connection.
 * */
    @Query("MATCH (a:City {name:$connectedCity})-[r]-(b:City {name:$newCity})\n" +
            " DELETE r;")
    void deleteRelation(String connectedCity, String newCity);

/**
 * Use this method to reveal all connected cities
 * */
    @Query("MATCH (a:City {name:$city})-[:NEXT]-(b)\n" +
            " RETURN b;")
    List<Location> getAllLocations(String city);

}
