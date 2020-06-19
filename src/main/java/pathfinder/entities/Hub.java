package pathfinder.entities;

import java.util.HashMap;
import java.util.Map;

import pathfinder.criteries.Distance;

public class Hub {
	private String name;
	private Map<String, Integer> neighbors;

	public Hub(String name) {
		this.name = name;
		neighbors = new HashMap<>();
	}

	public void addNeighbor(String name,int distance) {
		neighbors.put(name, distance);
	}

	public String getName() {
		return name;
	}

	public Map<String, Integer> getNeighbors() {
		return neighbors;
	}

}
