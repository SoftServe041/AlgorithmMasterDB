package cargoloader;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * This class represents cargo hold in any kind of transport.
 */

public class CargoHold {
	private double width;
	private double height;
	private double depth;

	// Max Volume and weight for checking limits (in future)
	private double maxVolume;
	private double maxWeight;

	// Map of loaded cargo
	// Stack for every destination point in route
	private Map<String, Stack<Cargo>> loadedCargo;
	private int[][][] loadingMatrix;

	public CargoHold() {

		loadedCargo = new LinkedHashMap<String, Stack<Cargo>>();

		// In future can work with any transport type
		// Parameters of cargo hold
		width = 2.4;// m
		height = 2.4;// m
		depth = 12;// m
		maxWeight = 22000;// kilograms

		// Calculate max volume for loading
		maxVolume = width * height * depth;

		// Initialize loading matrix
		loadingMatrix = new int[(int) (depth / 0.3)][(int) (height / 0.3)][(int) (width / 0.3)];
	}

	// Getters
	public Map<String, Stack<Cargo>> getLoadedCargo() {
		return loadedCargo;
	}

	public int[][][] getLoadingMatrix() {
		return loadingMatrix;
	}

}
