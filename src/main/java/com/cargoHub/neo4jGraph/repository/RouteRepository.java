package com.cargoHub.neo4jGraph.repository;

import com.cargoHub.neo4jGraph.model.Location;
import org.neo4j.driver.internal.InternalPath;
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

//public interface RouteRepository extends Neo4jRepository<Location, Long> {
//
//
//    /*@Query("CALL apoc.export.json.query(\n" +
//            "\"MATCH (a:City {name:'Kyiv'}), (b:City {name:'Lviv'}) " +
//            "MATCH p=(a)-[*]->(b) WITH collect(p) as paths " +
//            "CALL apoc.spatial.sortByDistance(paths) " +
//            "YIELD path, distance " +
//            "RETURN path, distance LIMIT 5\",\n" +
//            "\"route_data.json\", {writeNodeProperties:true})")
//    Stream getAllRoutes(String departure, String arrival);*/
//
//
//    /**
//     * Method getAllRouts() returns all available routs in ascending order.
//     * To avoid SOF pls limit the output. By default this limit value is set to 5.
//     */
////    @Query("MATCH (a:City {name:$departure}), (b:City {name:$arrival})\n" +
////            "MATCH p=(a)-[*]->(b)\n" +
////            "WITH collect(p) as paths\n" +
////            "CALL apoc.spatial.sortByDistance(paths) YIELD path, distance\n" +
////            "RETURN path, distance LIMIT 5")
//    @Query("MATCH (a:City {name:$departure}), (b:City {name:$arrival})\n" +
//            "MATCH p=(a)-[*]->(b)\n" +
//            "WITH collect(p) as paths\n" +
//            "RETURN a as a, paths LIMIT 5")
////    @Query("MATCH (a:City {name:'london'}), (b:City {name:'reading'})\n" +
////            "            MATCH paths=allShortestPaths((a)-[*]->(b))\n" +
////            "            RETURN paths LIMIT 2")
////    @Query("MATCH (a:City {name:$departure}), (b:City {name:$arrival})\n" +
////            "            MATCH p=allShortestPaths((a)-[*]->(b))\n" +
////            "            WITH collect(p) as paths\n" +
////            "            CALL apoc.spatial.sortByDistance(paths) YIELD path, distance\n" +
////            "            RETURN path, distance LIMIT 5")
//    RouteData getAllRoutes(String departure, String arrival);
////    Iterable<Location> getAllRoutes(String departure, String arrival);
//
//    @QueryResult
//    public class RouteData {
//        public Location a;
//        public Set<Location> paths;
//        public double distance;
//    }
//
////    @QueryResult
////    public class RouteData {
////        public PathData path;
////        public double distance;
////    }
//
//    @QueryResult
//    public class PathData {
//        public int length;
//        public LocationRelation[] rels;
//        public NodeModel[] nodes;
//    }
//
//    @QueryResult
//    public class StartModel {
//        public Integer id;
//        public String[] labels;
//        public LocationProperties properties;
//    }
//
//    @QueryResult
//    public class LocationProperties {
//        public float latitude;
//        public String name;
//        public float longitude;
//    }
//
//    @QueryResult
//    public class LocationRelation {
//        public int id;
//        public String type;
//        public String label;
//        public StartModel start;
//        public LocationRelation properties;
//        public StartModel end;
//    }
//
////    @Data
////    @NoArgsConstructor
////    @AllArgsConstructor
////    @NodeEntity(value = "path")
////    public class Route {
////
////        private Location departure;
////        private Location arrival;
////        //Map<String, Object> details;
////        @Relationship (type = "NEXT")
////        Collection<NodeRelation> relations;
////    }
//}

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
                        "            MATCH p=allShortestPaths((a)-[*]->(b))\n" +
                        "            WITH collect(p) as paths\n" +
                        "            CALL apoc.spatial.sortByDistance(paths) YIELD path, distance\n" +
                        "            RETURN path, distance LIMIT 5",
                parameters
        );
        log.info("RESULT = {}", result);
        List<RouteData> routes = new ArrayList<>();
        for (Map<String, Object> row : result) {
            log.info("RESULT = {}", result);
            InternalPath.SelfContainedSegment[] paths = ((InternalPath.SelfContainedSegment[]) row.get("path"));
            List<Location> locations = new ArrayList<>();
            for (InternalPath.SelfContainedSegment path : paths) {

                Location location = new Location(
                        path.end().id(),
                        path.end().get("name").asString(),
                        path.end().get("latitude").asDouble(),
                        path.end().get("longitude").asDouble(),
                        Collections.emptySet()
                );
                locations.add(location);
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
}
