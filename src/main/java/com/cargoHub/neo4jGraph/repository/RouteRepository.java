package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Connection;
import com.cargoHub.neo4jGraph.model.Location;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.repository.support.SimpleNeo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RouteRepository extends SimpleNeo4jRepository<Location, Long> {

    private static final Logger log = LoggerFactory.getLogger(RouteRepository.class);
    private final Session session;

    public RouteRepository(Session session) {
        super(Location.class, session);
        this.session = session;
    }

    public Iterable<RouteData> getAllRoutes(String departure, String arrival) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("departure", departure);
        parameters.put("arrival", arrival);
        Result result = session.query(
                "MATCH (a:City {name:$departure}), (b:City {name:$arrival})\n" +
                        "            MATCH p=(a)-[*]->(b)\n" +
                        "            WITH collect(p) as paths\n" +
                        "            CALL apoc.spatial.sortByDistance(paths) " +
                        "            YIELD path, distance\n" +
                        "            RETURN path, distance LIMIT 5"

                /*"MATCH (a:City {name:'Lviv'}), (b:City {name:'Kharkiv'})
                            MATCH p=(a)-[*]->(b) WITH collect(p) as paths
                            CALL apoc.spatial.sortByDistance(paths)
                            YIELD path, distance
                            RETURN path, distance LIMIT 5"*/
                /*"MATCH (a:City {name:$departure}), (b:City {name:$arrival})\n" +
                        "            MATCH p=allShortestPaths((a)-[*]->(b))\n" +
                        "            WITH collect(p) as paths\n" +
                        "            CALL apoc.spatial.sortByDistance(paths) YIELD path, distance\n" +
                        "            RETURN path, distance LIMIT 5"*/,
                parameters
        );
        log.info("RESULT = {}", result);
        List<RouteData> routes = new ArrayList<>();
        for (Map<String, Object> row : result) {
            log.info("RESULT = {}", result);
            InternalPath.SelfContainedSegment[] paths = ((InternalPath.SelfContainedSegment[]) row.get("path"));
            List<Location> locations = new ArrayList<>();
            for (InternalPath.SelfContainedSegment path : paths) {

                Location start = new Location(
                        path.start().id(),
                        path.start().get("name").asString(),
                        path.start().get("latitude").asDouble(),
                        path.start().get("longitude").asDouble(),
                        path.relationship().type()
                        //Collections.singleton(new Connection())
                );
                locations.add(start);

                Location end = new Location(
                        path.end().id(),
                        path.end().get("name").asString(),
                        path.end().get("latitude").asDouble(),
                        path.end().get("longitude").asDouble(),
                        Collections.singleton(new Connection())
                );
                locations.add(end);
            }


            double distance = (Double) row.get("distance");
            routes.add(new RouteData(locations, distance));
        }
        return routes;
    }

    public class RouteData {
        public List<Location> path;
        public double distance;

        public RouteData(List<Location> path, double distance) {
            this.path = path;
            this.distance = distance;
        }

        public List<Location> getPath() {
            return path;
        }

        public void setPath(List<Location> path) {
            this.path = path;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }
    }

    public class RouteRelationship extends InternalRelationship {

        public RouteRelationship(long id, long start, long end, String type) {
            super(id, start, end, type);
        }

        public RouteRelationship(long id, long start, long end, String type, Map<String, Value> properties) {
            super(id, start, end, type, properties);
        }
    }

    /*List<Connection> connections = new ArrayList<>();
            for (InternalPath.SelfContainedSegment path : paths) {
                //long id, Location start, Location end, String type
                Connection connection = new Connection(
                     -1, //ToDo by MV: reveal from the db, to get connection order
                     path.start().id(),
                     path.end().id(),
                     path.relationship().type()
                );
            }
            double distance = (Double) row.get("distance");
            routes.add(new RouteData(connections, distance));*/
}
