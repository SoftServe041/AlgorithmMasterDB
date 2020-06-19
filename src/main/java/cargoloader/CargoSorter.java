package cargoloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import pathfinder.entities.Hub;
import pathfinder.entities.Route;

public class CargoSorter {
	Map<String, List<Box>> sortedCargoMap;

	public CargoSorter() {
		sortedCargoMap = new HashMap<String, List<Box>>();
	}

	public void sortCargoByDestination(List<Box> boxes, Route route) {
		ListIterator<Hub> hubIterator = route.getRoute().listIterator(route.getRoute().size());
		while (hubIterator.hasPrevious()) {
			if (!hubIterator.previous().getName().equals(route.getRoute().get(0).getName())) {
				hubIterator.next();
				Hub hub = hubIterator.previous();
				if (sortedCargoMap.containsKey(hub.getName()) == false) {
					sortedCargoMap.put(hub.getName(), new ArrayList<Box>());
					for (Box box : boxes) {
						if (box.getDestination().equals(hub.getName())) {
							sortedCargoMap.get(hub.getName()).add(box);
						}
					}
				} else {
					for (Box box : boxes) {
						if (box.getDestination().equals(hub.getName())) {
							sortedCargoMap.get(hub.getName()).add(box);
						}
					}
				}
			}
		}
	}

	public void sortByFragility() {
		for (Map.Entry<String, List<Box>> entry : sortedCargoMap.entrySet()) {
			List<Box> listBox = entry.getValue();
			Collections.sort(listBox);
		}
	}
}
