package cargoloader;

import java.util.ArrayList;
import java.util.Arrays;
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

	public void sortByFragilityAndVolume() {
		for (Map.Entry<String, List<Box>> entry : sortedCargoMap.entrySet()) {
			List<Box> listBox = entry.getValue();
			Collections.sort(listBox, new CargoSorter.BoxChainedComparator(new CargoSorter.FragilityComparator(),
					new CargoSorter.VolumeComparator()));
		}
	}

	static class FragilityComparator implements Comparator<Box> {
		@Override
		public int compare(Box o1, Box o2) {
			return o1.getFragility() - o2.getFragility();
		}
	}

	static class VolumeComparator implements Comparator<Box> {
		@Override
		public int compare(Box o1, Box o2) {
			if (o1.getVolume() < o2.getVolume())
				return 1;
			if (o1.getVolume() > o2.getVolume())
				return -1;
			return 0;
		}
	}

	static class BoxChainedComparator implements Comparator<Box> {

		private List<Comparator<Box>> listComparators;

		@SafeVarargs
		public BoxChainedComparator(Comparator<Box>... comparators) {
			this.listComparators = Arrays.asList(comparators);
		}

		@Override
		public int compare(Box box1, Box box2) {
			for (Comparator<Box> comparator : listComparators) {
				int result = comparator.compare(box1, box2);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}
	}
}
