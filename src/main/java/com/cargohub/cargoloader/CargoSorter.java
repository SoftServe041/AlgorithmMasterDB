package com.cargohub.cargoloader;

import com.cargohub.entities.HubEntity;
import com.cargohub.entities.RouteEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * This class sorts cargo by destination point, fragility and volume
 */

public class CargoSorter {

	// Map for sorted cargo
	private Map<String, List<Cargo>> sortedCargoMap;

	// Initialize map for sorted cargo in constructor
	// This map keeps values in the given order (from distant points to near)
	// First value in map is a list of cargo for the farthest destination point
	public CargoSorter() {
		sortedCargoMap = new LinkedHashMap<String, List<Cargo>>();
	}

	// Sort cargo by destination, form distant route point to near (and by fragility
	// and volume)
	public void sortCargoByDestination(List<Cargo> boxes, RouteEntity route) {
		ListIterator<HubEntity> hubIterator = route.getHubs().listIterator(route.getHubs().size());
		while (hubIterator.hasPrevious()) {
			if (!hubIterator.previous().getName().equals(route.getHubs().get(0).getName())) {
				hubIterator.next();
				HubEntity hub = hubIterator.previous();
				if (!sortedCargoMap.containsKey(hub.getName())) {
					sortedCargoMap.put(hub.getName(), new ArrayList<Cargo>());
				}
				for (Cargo box : boxes) {
					if (box.getDestination().equals(hub.getName())) {
						sortedCargoMap.get(hub.getName()).add(box);
					}
				}
			}
		}
		sortByFragilityAndVolume();
	}

	// Sort cargo by fragility and volume from biggest and hard boxes to smallest
	// and fragile
	private void sortByFragilityAndVolume() {
		for (Map.Entry<String, List<Cargo>> entry : sortedCargoMap.entrySet()) {
			List<Cargo> listBox = entry.getValue();
			listBox.sort(new BoxChainedComparator(new FragilityComparator(),
					new VolumeComparator()));
		}
	}

	// Create comparators
	static class FragilityComparator implements Comparator<Cargo> {
		@Override
		public int compare(Cargo o1, Cargo o2) {
			return o1.getFragility() - o2.getFragility();
		}
	}

	static class VolumeComparator implements Comparator<Cargo> {
		@Override
		public int compare(Cargo o1, Cargo o2) {
			return Double.compare(o2.getVolume(), o1.getVolume());
		}
	}

	// Create multiple comparator
	static class BoxChainedComparator implements Comparator<Cargo> {

		private List<Comparator<Cargo>> listComparators;

		@SafeVarargs
		public BoxChainedComparator(Comparator<Cargo>... comparators) {
			this.listComparators = Arrays.asList(comparators);
		}

		@Override
		public int compare(Cargo box1, Cargo box2) {
			for (Comparator<Cargo> comparator : listComparators) {
				int result = comparator.compare(box1, box2);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}
	}

	// Return sorted map of cargo
	public Map<String, List<Cargo>> getSortedCargo() {
		return sortedCargoMap;
	}
}
