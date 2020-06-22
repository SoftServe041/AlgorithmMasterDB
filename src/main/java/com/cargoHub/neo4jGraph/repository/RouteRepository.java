package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.model.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface RouteRepository extends Neo4jRepository<Route, Long> {

/**
 * Method getAllRouts() returns all available routs in ascending order.
 * To avoid SOF pls limit the output. By default this limit value is set to 5.
 */
    @Query("MATCH (a:City {name:$departure}), (b:City {name:$arrival})\n" +
            "MATCH p=(a)-[*]->(b)\n" +
            "WITH collect(p) as paths\n" +
            "CALL apoc.spatial.sortByDistance(paths) YIELD path, distance\n" +
            "RETURN path, distance LIMIT 5")
    List<RouteData> getAllRoutes(String departure, String arrival);

    //List<NodeRelation> findAllNodeRelations();




    //Map<String, Object> getAllRoutes(String departure, String arrival);

    /*@QueryResult
    public class RouteData {
        public Path path;
        public double distance;
    }

    public class Path{
        public NodeModel start;
        public NodeModel end;
        public List<SegmentObject> segments;
        public int length;
    }

    public class SegmentObject {
        public NodeModel start;
        public NodeModel end;
        public RelationshipModel relationship;
    }*/

    @QueryResult
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RouteData {
        public Path path;
        public double distance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Path{
        public NodeObject start;
        public NodeObject end;
        public Iterable<SegmentObject> segments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class SegmentObject {
        public NodeObject start;
        public NodeObject end;
        public RelationshipObject relationship;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class NodeObject {
        public long identity;
        public String[] labels;
        public Location properties;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RelationshipObject {
        public long identity;
        public int start;
        public int end;
        public String type;
        public Iterable<Object> properties;
    }
}
