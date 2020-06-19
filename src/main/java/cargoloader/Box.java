package cargoloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Box implements Comparable<Box> {
	// id of the box
	int id;

	// size in three dimensions in meters
	private double width;
	private double depth;
	private double height;

	// size in minimal cells
	private int widthInCells;
	private int depthInCells;
	private int heightInCells;

	// weight and volume in kg
	private double weight;
	private double volume;

	// fragility from 1 to 5
	private int fragility;

	// destination point
	private String destination;

	// constructor
	public Box(double width, double height, double depth, double weight, int fragility, String destination) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.weight = weight;
		this.fragility = fragility;
		this.destination = destination;
		// calculate volume
		volume = this.width * this.height * this.depth;
		// calculate size int cells
		widthInCells = (int) (this.width / 0.6125);
		depthInCells = (int) (this.depth / 0.68);
		heightInCells = (int) (this.width / 0.6675);
	}

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

	@Override
	public String toString() {
		return "Box [id=" + id + ", width=" + width + ", depth=" + depth + ", height=" + height + ", widthInCells="
				+ widthInCells + ", depthInCells=" + depthInCells + ", heightInCells=" + heightInCells + ", weight="
				+ weight + ", volume=" + volume + ", fragility=" + fragility + ", destination=" + destination + "]";
	}

	public int compareTo(Box box) {
		return this.getFragility() - box.getFragility();
	}
}
