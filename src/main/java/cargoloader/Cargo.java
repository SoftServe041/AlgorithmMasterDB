package cargoloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents cargo box for loading algorithm.
 */
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
	public Cargo(double width, double height, double depth, double weight, int fragility, String destination) {
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

	// Getters
	public int getId() {
		return id;
	}

	public double getWidth() {
		return width;
	}

	public double getDepth() {
		return depth;
	}

	public double getHeight() {
		return height;
	}

	public int getWidthInCells() {
		return widthInCells;
	}

	public int getDepthInCells() {
		return depthInCells;
	}

	public int getHeightInCells() {
		return heightInCells;
	}

	public double getWeight() {
		return weight;
	}

	public double getVolume() {
		return volume;
	}

	public int getFragility() {
		return fragility;
	}

	public String getDestination() {
		return destination;
	}

	public int getWidthPos() {
		return widthPos;
	}

	public void setWidthPos(int widthPos) {
		this.widthPos = widthPos;
	}

	public int getDepthPos() {
		return depthPos;
	}

	public void setDepthPos(int depthPos) {
		this.depthPos = depthPos;
	}

	public int getHeightPos() {
		return heightPos;
	}

	public void setHeightPos(int heightPos) {
		this.heightPos = heightPos;
	}

// Basic version of toString	
//	@Override
//	public String toString() {
//		return "Box [id=" + id + ", width=" + width + ", depth=" + depth + ", height=" + height + ", widthInCells="
//				+ widthInCells + ", depthInCells=" + depthInCells + ", heightInCells=" + heightInCells + ", weight="
//				+ weight + ", volume=" + volume + ", fragility=" + fragility + ", destination=" + destination + "]";
//	}

	// For testing
	@Override
	public String toString() {
		return "Fragility=" + getFragility() + " ,volume=" + getVolume();
	}
}
