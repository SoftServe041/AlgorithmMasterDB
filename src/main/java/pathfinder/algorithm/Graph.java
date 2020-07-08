package pathfinder.algorithm;

import java.util.*;

//Create graph class
//with adjacency list
public class Graph {

	// Adjacency list
	private Map<String, List<String>> adjList;

	// Constructor
	public Graph() {
		// initialize adjacency list
		adjList = new HashMap<String, List<String>>();
	}

	// add edge from u to v
	public void addEdge(String u, String v) {
		// Add v to u's list.
		if (!adjList.containsKey(u)) {
			adjList.put(u, new ArrayList<String>());
		}
		if (!adjList.containsKey(v)) {
			adjList.put(v, new ArrayList<String>());
		}
		adjList.get(u).add(v);
	}

	// Prints all paths from
	// 's' to 'd'
	public void printAllPaths(String s, String d) {
		Map<String, Boolean> isVisited = new HashMap<String, Boolean>();
		ArrayList<String> pathList = new ArrayList<>();

		for (Map.Entry<String, List<String>> entry : adjList.entrySet()) {
			isVisited.put(entry.getKey(), false);
		}

		// add source to path
		pathList.add(s);

		// Call recursive method
		printAllPathsUtil(s, d, isVisited, pathList);
	}

	// A recursive function to print
	// all paths from 'u' to 'd'.
	// isVisited keeps track of
	// vertices in current path.
	// localPathList stores actual
	// vertices in the current path
	private void printAllPathsUtil(String u, String d, Map<String, Boolean> isVisited, List<String> localPathList) {
		// Mark the current node
		isVisited.put(u, true);

		if (u.equals(d)) {
			System.out.println(localPathList);
			// if match found then no need to traverse more till depth
			isVisited.put(u, false);
			return;
		}

		// Recursive call for all the vertices
		// adjacent to current vertex
		for (String s : adjList.get(u)) {
			if (!isVisited.get(s)) {
				// store current node
				// in path
				localPathList.add(s);
				printAllPathsUtil(s, d, isVisited, localPathList);

				// remove current node
				// in path
				localPathList.remove(s);
			}
		}

		// Mark the current node
		isVisited.put(u, false);
	}

	// Driver program
	public static void main(String[] args) {
		// Create a sample graph
		Graph g = new Graph();
		g.addEdge("Kharkiv", "Lviv");
		g.addEdge("Kharkiv", "Kyiv");
		g.addEdge("Kharkiv", "Sumy");
		g.addEdge("Kharkiv", "Poltava");
		g.addEdge("Kyiv", "Kharkiv");
		g.addEdge("Kyiv", "Lviv");
		g.addEdge("Kyiv", "Sumy");
		g.addEdge("Kyiv", "Myrgorod");
		g.addEdge("Lviv", "Kyiv");
		g.addEdge("Lviv", "Kharkiv");
		g.addEdge("Sumy", "Kyiv");
		g.addEdge("Sumy", "Kharkiv");
		g.addEdge("Myrgorod", "Kyiv");
		g.addEdge("Myrgorod", "Poltava");
		g.addEdge("Poltava", "Myrgorod");
		g.addEdge("Poltava", "Kharkiv");

		// source
		String s = "Kyiv";

		// destination
		String d = "Kharkiv";

		System.out.println("All different paths from " + s + " to " + d);
		g.printAllPaths(s, d);

	}
}