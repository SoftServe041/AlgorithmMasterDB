package com.cargoHub.neo4jGraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;


//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@NodeEntity(value = "City")
//public class Location implements Serializable {
//
//    private Long id;
//    private String name;
//    private double longitude;
//    private double latitude;
//    @Relationship
//    private Set<Location> connectedLocations;
//}

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@NodeEntity(value = "City")
public class Location {

    @Id @GeneratedValue
    private Long id;
    private String name;
    private double longitude;
    private double latitude;
    @Relationship(type="NEXT")
    private Set<Connection> connections;
//    private Set<Location> connections;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Location location = (Location) o;
//        return Double.compare(location.longitude, longitude) == 0 &&
//                Double.compare(location.latitude, latitude) == 0 &&
//                Objects.equals(id, location.id) &&
//                Objects.equals(name, location.name);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name, longitude, latitude);
//    }

    public Location(
            Long id,
            String name,
            double longitude,
            double latitude,
            Set<Connection> connections
    ) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.connections = connections;
    }

    public Location() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

//    public Set<Location> getConnections() {
//        return connections;
//    }
//
//    public void setConnections(Set<Location> connections) {
//        this.connections = connections;
//    }

    public Set<Connection> getConnections() {
        return connections;
    }

    public void setConnections(Set<Connection> connections) {
        this.connections = connections;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", connections=" + connections +
                '}';
    }
}
