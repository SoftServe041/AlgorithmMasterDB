package com.cargoHub.neo4jGraph.model;

import java.util.LinkedList;
import java.util.List;

public class Route {
    private Location departure;
    private Location arrival;
    LinkedList<Leg> legs;

    public Route() {
    }

    public Route(Location departure, Location arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }

    public void addLeg(Location departure, Location arrival, NodeRelation type, double distance) {
        if(this.legs == null) {
            this.legs = new LinkedList<>();
        }
        this.legs.add(new Leg(departure, arrival, type, distance));
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

    public LinkedList<Leg> getLegs() {
        return legs;
    }

    public void setLegs(LinkedList<Leg> legs) {
        this.legs = legs;
    }
}
