package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Location;
import lombok.Getter;
import org.neo4j.ogm.response.model.NodeModel;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RouteRepository extends Neo4jRepository<Location, Long> {


    /*@Query("CALL apoc.export.json.query(\n" +
            "\"MATCH (a:City {name:'Kyiv'}), (b:City {name:'Lviv'}) " +
            "MATCH p=(a)-[*]->(b) WITH collect(p) as paths " +
            "CALL apoc.spatial.sortByDistance(paths) " +
            "YIELD path, distance " +
            "RETURN path, distance LIMIT 5\",\n" +
            "\"route_data.json\", {writeNodeProperties:true})")
    Stream getAllRoutes(String departure, String arrival);*/


    /**
     * Method getAllRouts() returns all available routs in ascending order.
     * To avoid SOF pls limit the output. By default this limit value is set to 5.
     */

    /*
    @Query("MATCH (a:City {name:$departure}), (b:City {name:$arrival})\n" +
            "MATCH p=(a)-[*]->(b)\n" +
            "WITH collect(p) as paths\n" +
            "CALL apoc.spatial.sortByDistance(paths) YIELD path, distance\n" +
            "RETURN path, distance LIMIT 5")

    Iterable<RouteData> getAllRoutes(String departure, String arrival);

    @QueryResult
    public class RouteData {
        public PathData path;
        public double distance;
    }

    @QueryResult
    public class PathData {
        public int length;
        public LocationRelation[] rels;
        public NodeModel[] nodes;
    }

    @QueryResult
    public class StartModel {
        public Integer identity;
        public String[] labels;
        public LocationProperties properties;
    }

    @QueryResult
    public class LocationProperties {
        public float latitude;
        public String name;
        public float longitude;
    }

    @QueryResult
    public class LocationRelation {
        public int id;
        public String type;
        public String label;
        public StartModel start;
        public LocationRelation properties;
        public StartModel end;
    }

     */

    @Query("MATCH (a:City {name: $departure}), (b:City {name: $arrival})\n" +
            "MATCH p=(a)-[*]->(b) WITH collect(p) as paths\n" +
            "CALL apoc.spatial.sortByDistance(paths) \n" +
            "YIELD  distance , path\n" +
            "WITH reduce(output = [] , n IN nodes(path) | output + n.name )  as routes\n" +
            "RETURN  routes\n" +
            "limit 5")

    List<RouteData> getAllRoutes(String departure, String arrival);

    @QueryResult
    public class RouteData {
        public List<String> routes;
    }


    @Query("MATCH (a:City {name: $departure}), (b:City {name: $arrival})\n" +
            "MATCH p=(a)-[*]->(b) WITH collect(p) as paths\n" +
            "CALL apoc.spatial.sortByDistance(paths) \n" +
            "YIELD  distance\n" +
            "RETURN  round(distance) as distance\n" +
            "limit 5")

    List<RouteDataDistance> getAllRoutesDistance(String departure, String arrival);

    @QueryResult
    public class RouteDataDistance {
        public Double distance;
    }

}
