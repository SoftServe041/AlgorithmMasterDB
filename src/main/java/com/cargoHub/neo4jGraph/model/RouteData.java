package com.cargoHub.neo4jGraph.model;

import com.cargoHub.neo4jGraph.repository.RouteRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.response.model.NodeModel;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
@Builder
public class RouteData implements Serializable {
    public PathData path;
    public double distance;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class PathData extends InternalPath {
        public StartModel start;
        public LocationRelation relationship;
        public StartModel end;
    }

    public class StartModel extends InternalNode {
        public Integer id;
        public List<?> labels;
        public Map<String, LocationProperties> properties;

        public StartModel(long id) {
            super(id);
        }

        public StartModel(long id, Collection<String> labels, Map<String, Value> properties) {
            super(id, labels, properties);
        }
    }

    public class LocationProperties{
        public float latitude;
        public String name;
        public float longitude;
    }

    @RelationshipEntity
    public class LocationRelation  extends InternalRelationship {
        public int id;
        public String type;
        public String label;
        @StartNode
        public StartModel start;
        public LocationRelation properties;
        @EndNode
        public StartModel end;

        public LocationRelation(long id, long start, long end, String type) {
            super(id, start, end, type);
        }

        public LocationRelation(long id, long start, long end, String type, Map<String, Value> properties) {
            super(id, start, end, type, properties);
        }
    }
}
