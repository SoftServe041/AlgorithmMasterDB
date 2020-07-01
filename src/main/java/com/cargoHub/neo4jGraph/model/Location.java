package com.cargoHub.neo4jGraph.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

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
    @Relationship
    private String connection;
    @Relationship
    private Set<Connection> connections;

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

    public Location(Long id, String name, double longitude,
                    double latitude, String connection) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.connection = connection;
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

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

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
