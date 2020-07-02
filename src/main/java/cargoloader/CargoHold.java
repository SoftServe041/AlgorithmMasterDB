package cargoloader;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class CargoHold {
	private double width;
	private double height;
	private double depth;
	private double maxVolume;
	private double maxWeight;
	private Map<String, Stack<Cargo>> loadedCargo;
	private int[][][] loadingMatrix;

	public CargoHold() {
		loadedCargo = new LinkedHashMap<String, Stack<Cargo>>();
		// in future can work with transport type
		width = 2.4;// m
		height = 2.4;// m
		depth = 12;// m
		maxWeight = 22000;// kg
		maxVolume = width * height * depth;
		loadingMatrix = new int[(int) (depth / 0.3)][(int) (height / 0.3)][(int) (width / 0.3)];
	}

	public Map<String, Stack<Cargo>> getLoadedCargo() {
		return loadedCargo;
	}

	public int[][][] getLoadingMatrix() {
		return loadingMatrix;
	}

}
