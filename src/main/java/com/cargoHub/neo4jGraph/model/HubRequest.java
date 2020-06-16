package com.cargoHub.neo4jGraph.model;

public class HubRequest {

    String newCity;
    String connectedCity;
    String tType;


    public HubRequest() {
    }

    public HubRequest(String newCity, String connectedCity) {
        this.newCity = newCity;
        this.connectedCity = connectedCity;
    }

    public String getNewCity() {
        return newCity;
    }

    public void setNewCity(String newCity) {
        this.newCity = newCity;
    }

    public String getConnectedCity() {
        return connectedCity;
    }

    public void setConnectedCity(String connectedCity) {
        this.connectedCity = connectedCity;
    }

    public String gettType() {
        return tType;
    }

    public void settType(String tType) {
        this.tType = tType;
    }
}
