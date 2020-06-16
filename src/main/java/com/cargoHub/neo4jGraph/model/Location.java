package com.cargoHub.neo4jGraph.model;



import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.io.Serializable;
import java.util.Collection;


@NodeEntity(value = "City")
public class Location implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private double longitude;
    private double latitude;
    //AvailableTransport availableTransport;

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
    /*public Location(Long id, String name, double longitude, double latitude, AvailableTransport availableTransport) {
        this.id = id;
        this.name = name;
        //this.longitude = longitude;
        //this.latitude = latitude;
        //this.availableTransport = availableTransport;
    }*/

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

    /*public AvailableTransport getAvailableTransport() {
        return availableTransport;
    }

    public void setAvailableTransport(AvailableTransport availableTransport) {
        this.availableTransport = availableTransport;
    }*/
}
