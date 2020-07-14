package com.cargoHub.neo4jGraph.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.*;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@RelationshipEntity("NEXT")
public class Connection {

    @Id @GeneratedValue
    private long id;
    @StartNode
    @JsonIgnore
    private Location start;
    @EndNode
    private Location end;
    public String type;
    

    public Connection(long id, Location start, Location end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public Connection() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Connection that = (Connection) o;
//        return id == that.id;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
}
