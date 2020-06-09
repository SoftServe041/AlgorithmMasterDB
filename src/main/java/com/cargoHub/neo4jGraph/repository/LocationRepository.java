package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Location;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;
import java.util.Collections;


public interface LocationRepository extends Neo4jRepository<Location, Long> {
    //@Query("MATCH (a:City) -[r:AVIA]-> (b:City) RETURN a, type(r), b LIMIT 2")
    @Query("MATCH (n:City) RETURN n LIMIT 25")
    Collection<Location> getAllLocations();
    Location findByName(String name);


    /*default Collection<Location> getAllLocations() {
        Location l = new Location(1L, "qwerty", 123.00, 4546.23);
        return Collections.singleton(l);
    }*/

}
