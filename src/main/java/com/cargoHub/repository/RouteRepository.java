package com.cargohub.repository;

import com.cargohub.models.Location;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;


public interface RouteRepository extends Neo4jRepository<Location, Long> {

    /**
     * Method getAllRouts() returns all available routs in ascending order.
     * To avoid SOF pls limit the output. By default this limit value is set to 5.
     */

    @Query("MATCH (a:City {name: $departure}), (b:City {name: $arrival})\n" +
            "MATCH p=(a)-[*]->(b) WITH collect(p) AS paths\n" +
            "CALL apoc.spatial.sortByDistance(paths) \n" +
            "YIELD  path\n" +
            "WITH reduce(output = [] , n IN nodes(path) | output + n.name )  AS routes\n" +
            "RETURN  routes\n" +
            "LIMIT 5")
    List<RouteData> getAllRoutes(String departure, String arrival);

    @QueryResult
    class RouteData {
        public List<String> routes;
    }

    @Query("MATCH (a:City {name: $departure}), (b:City {name: $arrival})\n" +
            "MATCH p=(a)-[*]->(b) WITH collect(p) AS paths\n" +
            "CALL apoc.spatial.sortByDistance(paths) \n" +
            "YIELD  distance\n" +
            "RETURN  round(distance) AS distance\n" +
            "LIMIT 5")
    List<RouteDataDistance> getAllRoutesDistance(String departure, String arrival);

    @QueryResult
    class RouteDataDistance {
        public Double distance;
    }

}
