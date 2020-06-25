/*
package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Location;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.stream.Stream;

public interface RouteExtractorRepository extends Neo4jRepository<Location, Long> {




    @Query("CALL apoc.export.json.query(\n" +
            "\"MATCH (a:City {name:$departure}), (b:City {name:$arrival}) " +
            "MATCH p=(a)-[*]->(b) WITH collect(p) as paths " +
            "CALL apoc.spatial.sortByDistance(paths) " +
            "YIELD path, distance " +
            "RETURN path, distance LIMIT 5\",\n" +
            "\"data.json\", " +
            "{writeNodeProperties:true})")
    void getAllRoutes(String departure, String arrival);

}
*/
