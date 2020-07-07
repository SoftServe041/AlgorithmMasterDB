package cargoloader;

import java.util.ArrayList;
import java.util.List;

// This class represents a primitive model of route for cargo transport
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
