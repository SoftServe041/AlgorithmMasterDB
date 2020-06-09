package com.cargoHub.neo4jGraph.model;



import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@NodeEntity(value = "City")
public class Location implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private double longitude;
    private double latitude;

    public Location() {
    }

    public Location(String name) {
        this.name = name;
    }

    public Location(Long id, String name, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
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

    /*public Set<Location> getConnectedLocations() {
        return connectedLocations;
    }

    public void setConnectedLocations(Set<Location> connectedLocations) {
        this.connectedLocations = connectedLocations;
    }*/

    /**
     * ToDo by MV: how to set several relationship types? As parameter -- kinda solution
     * */
    @Relationship(type = "AVIA", direction = Relationship.OUTGOING)
    public Set<Location> connectedLocations;

    public void connectedTo(Location connectedLocation) {
        if (connectedLocations == null) {
            connectedLocations = new HashSet<>();
        }
        connectedLocations.add(connectedLocation);
    }

    public String toString() {

        return this.name + "Connected Locations => "
                + Optional.ofNullable(this.connectedLocations).orElse(
                Collections.emptySet()).stream()
                .map(Location::getName)
                .collect(Collectors.toList());
    }
}
