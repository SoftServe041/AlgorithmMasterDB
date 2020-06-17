package com.cargoHub.neo4jGraph.model;

public class Leg {

    private Location departure;
    private Location arrival;
    private NodeRelation type;
    private double distance;

    public Leg() {
    }

    public Leg(Location departure, Location arrival, NodeRelation type, double distance) {
        this.departure = departure;
        this.arrival = arrival;
        this.type = type;
        this.distance = distance;
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

    public NodeRelation getType() {
        return type;
    }

    public void setType(NodeRelation type) {
        this.type = type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
