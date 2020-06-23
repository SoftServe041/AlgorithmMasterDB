package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Location;
import com.cargoHub.neo4jGraph.model.NodeRelation;
import com.cargoHub.neo4jGraph.model.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.response.model.NodeModel;
import org.neo4j.ogm.response.model.RelationshipModel;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RouteRepository extends Neo4jRepository<Route, Long> {

/**
 * Method getAllRouts() returns all available routs in ascending order.
 * To avoid SOF pls limit the output. By default this limit value is set to 5.
 */
    @Query("MATCH (a:City {name:$departure}), (b:City {name:$arrival})\n" +
            "MATCH p=(a)-[*]->(b)\n" +
            "WITH collect(p) as paths\n" +
            "CALL apoc.spatial.sortByDistance(paths) YIELD path, distance\n" +
            "RETURN path as path, distance as distance LIMIT 5")
    List<RouteData> getAllRoutes(String departure, String arrival);

    //List<NodeRelation> findAllNodeRelations();

    @QueryResult
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RouteData {
        public Object[] path;
        public double distance;
    }
    /*@QueryResult
    @Data
    @AllArgsConstructor
    public class Path{
        public Location start;
        public Location end;
        public SegmentObject segments;
    }
    @QueryResult
    @Data
    @AllArgsConstructor
    public class SegmentObject {
        public Location start;
        public Location end;
        public NodeRelation relationship;
    }*/
    /*@QueryResult
    @Data
    @AllArgsConstructor
    public class LocationNode {
        public long identity;
        public String[] labels;
        public Location properties;
    }*/

    /*@QueryResult
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
        public NodeModel start;
        public NodeModel end;
        public Iterable<NodeModel> segments;
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
    }*/
}
