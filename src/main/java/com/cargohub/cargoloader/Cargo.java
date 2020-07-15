package com.cargohub.cargoloader;

import com.cargohub.entities.CargoEntity;
import lombok.Getter;

/**
 * This class represents cargo box for loading algorithm.
 */
@Getter
public class Cargo {

    // Id of the box
    int id;

    // Size in three dimensions in meters
    private double width;
    private double depth;
    private double height;

    // Position of box in matrix (for visualization)
    private int widthPos;
    private int depthPos;
    private int heightPos;

    // Size in matrix cells
    private int widthInCells;
    private int depthInCells;
    private int heightInCells;

    // Weight and volume in kilograms
    private double weight;
    private double volume;

    // Fragility of cargo
    private int fragility;

    // Destination point
    private String destination;

    // Constructor
    public Cargo(int id, double width, double height, double depth, double weight, int fragility, String destination) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
        this.fragility = fragility;
        this.destination = destination;

        // Calculate volume
        volume = this.width * this.height * this.depth;

        // Calculate size in cells
        // Can be used for any transport type in future
        widthInCells = (int) (this.width / 0.3);
        depthInCells = (int) (this.depth / 0.3);
        heightInCells = (int) (this.height / 0.3);
    }

    public Cargo toCargo(CargoEntity cargoEntity) {
        int id = cargoEntity.getId();
        double width = cargoEntity.getDimensions().getWidth();
        double height = cargoEntity.getDimensions().getHeight();
        double depth = cargoEntity.getDimensions().getLength();
        double weight = cargoEntity.getWeight();
        int fragility = 1; // disable fragility comparator
        String destination = cargoEntity.getFinalDestination();
        Cargo cargo = new Cargo(id, width, height, depth, weight, fragility, destination);
        return cargo;
    }

    // Setters

    public void setWidthPos(int widthPos) {
        this.widthPos = widthPos;
    }

    public void setDepthPos(int depthPos) {
        this.depthPos = depthPos;
    }

    public void setHeightPos(int heightPos) {
        this.heightPos = heightPos;
    }

}
