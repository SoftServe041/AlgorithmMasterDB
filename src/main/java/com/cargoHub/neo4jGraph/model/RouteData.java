package com.cargohub.neo4jGraph.model;

import java.util.List;

public class RouteData {

    private int id;
    private Location departure;
    private Location arrival;
    private NodeRelation relationType;
    private double distance;

    List<RouteData> intermediaryHubs;

    public RouteData() {
    }

    public RouteData(int id, Location departure, Location arrival, NodeRelation relationType, double distance, List<RouteData> intermediaryHubs) {
        this.id = id;
        this.departure = departure;
        this.arrival = arrival;
        this.relationType = relationType;
        this.distance = distance;
        this.intermediaryHubs = intermediaryHubs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getDeparture() {
        return departure;
    }

    public void setDeparture(Location departure) {
        this.departure = departure;
    }

    public Location getArrival() {
        return arrival;
    }

    public void setArrival(Location arrival) {
        this.arrival = arrival;
    }

    public NodeRelation getRelationType() {
        return relationType;
    }

    public void setRelationType(NodeRelation relationType) {
        this.relationType = relationType;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<RouteData> getIntermediaryHubs() {
        return intermediaryHubs;
    }

    public void setIntermediaryHubs(List<RouteData> intermediaryHubs) {
        this.intermediaryHubs = intermediaryHubs;
    }
}
