package cargoloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import pathfinder.entities.Hub;
import pathfinder.entities.Route;

public class CargoSorter {
	private Map<String, List<Cargo>> sortedCargoMap;

	public CargoSorter() {
		sortedCargoMap = new LinkedHashMap<String, List<Cargo>>();
	}

	public void sortCargoByDestination(List<Cargo> boxes, Route route) {
		ListIterator<Hub> hubIterator = route.getRoute().listIterator(route.getRoute().size());
		while (hubIterator.hasPrevious()) {
			if (!hubIterator.previous().getName().equals(route.getRoute().get(0).getName())) {
				hubIterator.next();
				Hub hub = hubIterator.previous();
				if (sortedCargoMap.containsKey(hub.getName()) == false) {
					sortedCargoMap.put(hub.getName(), new ArrayList<Cargo>());
					for (Cargo box : boxes) {
						if (box.getDestination().equals(hub.getName())) {
							sortedCargoMap.get(hub.getName()).add(box);
						}
					}
				} else {
					for (Cargo box : boxes) {
						if (box.getDestination().equals(hub.getName())) {
							sortedCargoMap.get(hub.getName()).add(box);
						}
					}
				}
			}
		}
		sortByFragilityAndVolume();
	}

	private void sortByFragilityAndVolume() {
		for (Map.Entry<String, List<Cargo>> entry : sortedCargoMap.entrySet()) {
			List<Cargo> listBox = entry.getValue();
			Collections.sort(listBox, new CargoSorter.BoxChainedComparator(new CargoSorter.FragilityComparator(),
					new CargoSorter.VolumeComparator()));
		}
	}

	static class FragilityComparator implements Comparator<Cargo> {
		@Override
		public int compare(Cargo o1, Cargo o2) {
			return o1.getFragility() - o2.getFragility();
		}
	}

	static class VolumeComparator implements Comparator<Cargo> {
		@Override
		public int compare(Cargo o1, Cargo o2) {
			if (o1.getVolume() < o2.getVolume())
				return 1;
			if (o1.getVolume() > o2.getVolume())
				return -1;
			return 0;
		}
	}

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

	public Map<String, List<Cargo>> getSortedCargo() {
		return sortedCargoMap;
	}
}
