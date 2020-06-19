package pathfinder.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pathfinder.criteries.Distance;
import pathfinder.entities.Hub;

public class PathFinder {
	private Map<String, Hub> hubMap;

	public PathFinder() {
		hubMap = new HashMap<String, Hub>();
	}

	public void addHub(Hub hub) {
		if (!hubMap.containsKey(hub.getName())) {
			hubMap.put(hub.getName(), hub);
		}
	}

	public void findPaths(String start, String dest) {
		Map<String, Boolean> isVisited = new HashMap<String, Boolean>();
		ArrayList<String> pathList = new ArrayList<>();
		int distance = 0;

		for (Map.Entry<String, Hub> entry : hubMap.entrySet()) {
			isVisited.put(entry.getKey(), false);
		}

		pathList.add(start);

		findPathsUtil(start, dest, isVisited, pathList, distance);
	}

	private void findPathsUtil(String current, String dest, Map<String, Boolean> isVisited, List<String> localPathList,
			int currentDistance) {
		isVisited.put(current, true);

		if (current.equals(dest)) {
			System.out.println(localPathList + " " + currentDistance + " km");
			isVisited.put(current, false);
			return;
		}

		for (Map.Entry<String, Integer> entry : hubMap.get(current).getNeighbors().entrySet()) {
			if (!isVisited.get(entry.getKey())) {

				localPathList.add(entry.getKey());
				currentDistance += hubMap.get(current).getNeighbors().get(entry.getKey());
				findPathsUtil(entry.getKey(), dest, isVisited, localPathList, currentDistance);

				localPathList.remove(entry.getKey());
			}
		}

		isVisited.put(current, false);
	}

	public static void main(String[] args) {
		// PathFinder as abstract
		PathFinder pathFinder = new PathFinder();
		Hub hub1 = new Hub("Kharkiv");
		hub1.addNeighbor("Kyiv", 987);
		hub1.addNeighbor("Lviv", 326);
		hub1.addNeighbor("Sumy", 315);
		hub1.addNeighbor("Poltava", 167);

		Hub hub2 = new Hub("Kyiv");
		hub2.addNeighbor("Kharkiv", 987);
		hub2.addNeighbor("Lviv", 1572);
		hub2.addNeighbor("Sumy", 443);
		hub2.addNeighbor("Myrgorod", 574);

		Hub hub3 = new Hub("Lviv");
		hub3.addNeighbor("Kyiv", 1572);
		hub3.addNeighbor("Kharkiv", 326);

		Hub hub4 = new Hub("Sumy");
		hub4.addNeighbor("Kyiv", 443);
		hub4.addNeighbor("Kharkiv", 315);

		Hub hub5 = new Hub("Myrgorod");
		hub5.addNeighbor("Kyiv", 574);
		hub5.addNeighbor("Poltava", 124);

		Hub hub6 = new Hub("Poltava");
		hub6.addNeighbor("Myrgorod", 124);
		hub6.addNeighbor("Kharkiv", 167);

		pathFinder.addHub(hub1);
		pathFinder.addHub(hub2);
		pathFinder.addHub(hub3);
		pathFinder.addHub(hub4);
		pathFinder.addHub(hub5);
		pathFinder.addHub(hub6);

		String s = "Kyiv";

		String d = "Kharkiv";

		System.out.println("All different paths from " + s + " to " + d);
		pathFinder.findPaths(s, d);

	}
}
