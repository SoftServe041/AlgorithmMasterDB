package cargoloader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import pathfinder.entities.Route;

public class CargoLoader {
	private Map<String, List<Box>> unloadedCargo;
	private CargoSorter cargoSorter;

	public CargoLoader() {
		cargoSorter = new CargoSorter();
	}

	public void loadCargo(List<Box> boxes, Route route, CargoHold cargohold) {
		cargoSorter.sortCargoByDestination(boxes, route);
		unloadedCargo = cargoSorter.getSortedCargo();
		for (Map.Entry<String, List<Box>> entry : unloadedCargo.entrySet()) {

		}
	}
}
