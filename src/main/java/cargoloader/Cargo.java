package cargoloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Cargo {
	// id of the box
	int id;

	// size in three dimensions in meters
	private double width;
	private double depth;
	private double height;

	// position of box (for visualization)
	private int widthPos;
	private int depthPos;
	private int heightPos;

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
	public Cargo(double width, double height, double depth, double weight, int fragility, String destination) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.weight = weight;
		this.fragility = fragility;
		this.destination = destination;
		// calculate volume
		volume = this.width * this.height * this.depth;
		// calculate size int cells
		widthInCells = (int) (this.width / 0.3);// can be related to transport type
		depthInCells = (int) (this.depth / 0.3);
		heightInCells = (int) (this.height / 0.3);
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

//	@Override
//	public String toString() {
//		return "Box [id=" + id + ", width=" + width + ", depth=" + depth + ", height=" + height + ", widthInCells="
//				+ widthInCells + ", depthInCells=" + depthInCells + ", heightInCells=" + heightInCells + ", weight="
//				+ weight + ", volume=" + volume + ", fragility=" + fragility + ", destination=" + destination + "]";
//	}

	@Override
	public String toString() {
		return "Fragility=" + getFragility() + " ,volume=" + getVolume();
	}
}
