package com.cargohub.cargoloader;

import java.util.*;

/**
 * This class represents cargo hold in any kind of transport.
 */
//TODO CarrierCompartmentEntity
public class CargoHold {
    private double width;
    private double height;
    private double depth;

    // Max Volume and weight for checking limits (in future)
    private double maxVolume;
    private double maxWeight;

    // Map of loaded cargo
    // Stack for every destination point in route
    private Map<String, List<Cargo>> loadedCargo;
    private int[][][] loadingMatrix;

    public CargoHold(double width, double height, double depth, int maxWeight, int[][][] loadingMatrix) {

        loadedCargo = new LinkedHashMap<>();

        // In future can work with any transport type
        // Parameters of cargo hold
        this.width = width;// m
        this.height = height;// m
        this.depth = depth;// m
        this.maxWeight = maxWeight;// kilograms

        // Calculate max volume for loading
        maxVolume = width * height * depth;

        // Initialize loading matrix
        this.loadingMatrix = loadingMatrix;
    }

    public List<Cargo> getCargoList() {
        List<Cargo> cargos = new ArrayList<>();
        for (Map.Entry<String, List<Cargo>> entry : loadedCargo.entrySet()) {
            cargos.addAll(entry.getValue());
        }
        return cargos;
    }

    // Getters
    public Map<String, List<Cargo>> getLoadedCargo() {
        return loadedCargo;
    }

    public int[][][] getLoadingMatrix() {
        return loadingMatrix;
    }

}
