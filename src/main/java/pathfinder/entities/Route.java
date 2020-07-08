package pathfinder.entities;

import java.util.ArrayList;
import java.util.List;

public class Route {
	private List<Hub> route;

	public Route(Hub... hubs) {
		route = new ArrayList<Hub>();
		for (Hub hub : hubs) {
			route.add(hub);
		}
	}

	public List<Hub> getRoute() {
		return route;
	}
}
