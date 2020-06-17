package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Route;
import org.neo4j.ogm.cypher.compiler.NodeBuilder;
import org.neo4j.ogm.response.model.NodeModel;
import org.neo4j.ogm.response.model.RelationshipModel;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.sql.ResultSet;
import java.util.List;

public interface RouteRepository extends Neo4jRepository<Route, Long> {

    /**
     * Methods getAllRouts() returns all available routs in ascending order.
     * To avoid SOF pls limit the output. By default this limit value is set to 5.
     * */
    @Query("MATCH (a:City {name:$departure}), (b:City {name:$arrival})\n" +
            "MATCH p=(a)-[*]->(b)\n" +
            "WITH collect(p) as paths\n" +
            "CALL apoc.spatial.sortByDistance(paths) YIELD path, distance\n" +
            "RETURN path, distance LIMIT 5")
    List<RouteData> getAllRoutes(String departure, String arrival);

    /*@Query("MATCH (nod:City)\n" +
            "MATCH ()-[rels:TRUCK]->()\n" +
            "WITH collect(nod) as a, collect(rels) as b\n" +
            "CALL apoc.export.json.data(a, b, null, {stream: true})\n" +
            "YIELD file, nodes, relationships, properties, data\n" +
            "RETURN file, nodes, relationships, properties, data")
    Stream<Location> getAllRouts(String departure, String arrival);*/

    @QueryResult
    public class RouteData {
        public PathObject path;
        public double distance;
    }

    public class PathObject{
        public NodeModel start;
        public NodeModel end;
        public List<SegmentObject> segments;
        public int length;
    }

    public class SegmentObject {
        public NodeModel start;
        public NodeModel end;
        public RelationshipModel relationship;
    }
}
