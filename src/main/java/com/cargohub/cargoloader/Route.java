package com.cargohub.cargoloader;

import java.util.ArrayList;
import java.util.List;

// This class represents a primitive model of route for cargo transport
//TODO Should be taken from Order builder or directly from Neo4j
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
