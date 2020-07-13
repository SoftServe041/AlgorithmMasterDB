package cargoloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * This is a main class of cargo loading algorithm. Object of this class is like
 * a worker. Give to that class a link to cargo hold, and cargo loader will pack
 * all boxes in it.
 */

public class CargoLoader3D {

	// Map of sorted and prepared cargo for loading
	private Map<String, List<Cargo>> unloadedCargo;

	// Create sorter of cargo
	private CargoSorter cargoSorter;

	// Constructor
	public CargoLoader3D() {
		cargoSorter = new CargoSorter();
	}

	// Main method, takes boxes, route and cargo hold, changes cargo hold state
	public void loadCargo(List<Cargo> boxes, Route route, CargoHold cargohold) {

		// First of all sort all boxes by destination, fragility and volume
		// Initialize unsorted cargo
		unloadedCargo = cargoSorter.sortCargoByDestination(boxes, route);

		// Initialize surface scanner for each destination point, search place for boxes
		for (Map.Entry<String, List<Cargo>> entry : unloadedCargo.entrySet()) {
			if (cargohold.getLoadedCargo().containsKey(entry.getKey())) {
				initializeSurfaceScanner(entry.getValue(), cargohold.getLoadingMatrix(), cargohold.getLoadedCargo());
			} else {
				cargohold.getLoadedCargo().put(entry.getKey(), new Stack<Cargo>());
				initializeSurfaceScanner(entry.getValue(), cargohold.getLoadingMatrix(), cargohold.getLoadedCargo());
			}
		}

		// Print loading matrix (useful for testing)
		printMatrix(cargohold.getLoadingMatrix());
	}

	public void unloadCargo() {

	}

	// Check if we can fit box
	private boolean checkPlace(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepth) {

		// Check borders of cargo hold
		if (currentHeightPos + box.getHeightInCells() - 1 > loadingMatrix[0].length - 1
				|| currentWidthPos + box.getWidthInCells() - 1 > loadingMatrix[0][0].length - 1
				|| currentDepth + box.getDepthInCells() - 1 > loadingMatrix.length - 1) {
			return false;

			// Check free place for box from all sides
		} else if (checkVolume(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepth) == false
				|| checkBottom(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepth) == false
				|| checkTop(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepth) == false) {
			return false;
		}
		return true;
	}

	// Check free volume for box
	private boolean checkVolume(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepthPos) {
		for (int i = currentDepthPos; i < currentDepthPos + box.getDepthInCells(); i++) {
			for (int j = currentHeightPos; j < currentHeightPos + box.getHeightInCells(); j++) {
				for (int k = currentWidthPos; k < currentWidthPos + box.getWidthInCells(); k++) {
					if (loadingMatrix[i][j][k] != 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// Check cells under box
	// Fragility checking can be simply added in future.
	private boolean checkBottom(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepthPos) {
		int boxSquare = box.getWidthInCells() * box.getDepthInCells();
		int availableSquare = 0;
		boolean isInAir = true;
		if (currentHeightPos - 1 >= 0) {
			for (int i = currentDepthPos; i < currentDepthPos + box.getDepthInCells(); i++) {
				for (int j = currentWidthPos; j < currentWidthPos + box.getWidthInCells(); j++) {
					if (loadingMatrix[i][currentHeightPos - 1][j] != 0) {
						availableSquare++;
						isInAir = false;
					}
				}
			}
			if ((availableSquare < boxSquare) | isInAir == true) {
				return false;
			}
		}
		return true;
	}

	// Check for box from above
	private boolean checkTop(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepthPos) {
		if (currentHeightPos + box.getHeightInCells() < loadingMatrix[0].length) {
			for (int i = currentDepthPos; i < currentDepthPos + box.getDepthInCells(); i++) {
				for (int j = currentWidthPos; j < currentWidthPos + box.getWidthInCells(); j++) {
					if (loadingMatrix[i][currentHeightPos + box.getHeightInCells()][j] != 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// Start surface scanner for loading matrix
	public void initializeSurfaceScanner(List<Cargo> listBox, int[][][] loadingMatrix,
			Map<String, Stack<Cargo>> loadedCargo) {

		// Initialize starting position
		int heightPos = loadingMatrix[0].length - 1;
		int widthPos = 0;
		int depthPos = 0;

		List<Cargo> unloadedBoxes = new LinkedList<Cargo>();
		unloadedBoxes.addAll(listBox);

		while (unloadedBoxes.size() > 0 & depthPos < loadingMatrix.length) {
			boolean canLoad = false;
			for (Cargo box : unloadedBoxes) {

				// Set starting position
				// TODO Проблема в это куске кода, который ищет стартовую позицию, без него
				// вроде пашет, надо будет переделать
//				if (loadingMatrix[depthPos][heightPos][widthPos] != 0) {
//					while (loadingMatrix[depthPos][heightPos][widthPos] != 0
//							& widthPos + 1 < loadingMatrix[0][0].length) {
//						widthPos++;
//					}
//				}
//
//				while (heightPos > 0) {
//					if (loadingMatrix[depthPos][heightPos - 1][widthPos] != 0) {
//						break;
//					} else {
//						heightPos--;
//					}
//				}

				if (scanSurfaceAndPlaceBox(box, widthPos, heightPos, depthPos, loadingMatrix)) {
					heightPos = loadingMatrix[0].length - 1;
					widthPos = 0;
					loadedCargo.get(box.getDestination()).push(box);
					unloadedBoxes.remove(box);
					canLoad = true;
					break;
				}
			}
			if (!canLoad) {
				depthPos++;
			}
		}

	}

	// Scan surface and find place for box, this method works only with loading
	// matrix
	private boolean scanSurfaceAndPlaceBox(Cargo box, int currentWidth, int currentHeight, int currentDepth,
			int[][][] loadingMatrix) {
		boolean canClimb = false;
		while (currentWidth < loadingMatrix[0][0].length) {

			// Check place for box and if place is free, save its position in loading matrix
			if (checkPlace(box, loadingMatrix, currentWidth, currentHeight, currentDepth)) {
				placeBox(box, loadingMatrix, currentHeight, currentWidth, currentDepth);

				// Set position of box in loading matrix
				box.setDepthPos(currentDepth);
				box.setHeightPos(currentHeight);
				box.setWidthPos(currentWidth);
				return true;

			} else {

				// If we don`t have place for box, scan surface from side to side step by step
				// Check if height < top and height > bottom
				if (currentHeight - 1 >= 0 & currentHeight < loadingMatrix[0].length - 1) {

					// Check if width < end
					if (currentWidth < loadingMatrix[0][0].length - 1) {

						// Move forward
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] != 0
								& loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0) {
							currentWidth++;
							continue;
						}

						// Start to climb
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] != 0
								& loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0) {
							canClimb = true;
							currentHeight++;
							continue;
						}

						// Climb to the edge
						if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0
								& loadingMatrix[currentDepth][currentHeight - 1][currentWidth + 1] != 0) {
							canClimb = false;
							currentWidth++;
							continue;
						}

						// Move up
						if (canClimb) {
							if (loadingMatrix[currentDepth][currentHeight + 1][currentWidth] == 0
									& loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0
									& loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0) {
								currentHeight++;
								continue;
							}
						}

						// Move down
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}

						// Check if width = end
					} else if (currentWidth == loadingMatrix[0][0].length - 1) {

						// Move down near the wall
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}
					}

					// Check if height = top
				} else if (currentHeight == loadingMatrix[0].length - 1) {

					// Check if width < end
					if (currentWidth < loadingMatrix[0][0].length - 1) {

						// Climb to the edge
						if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0
								& loadingMatrix[currentDepth][currentHeight - 1][currentWidth + 1] != 0) {
							canClimb = false;
							currentWidth++;
							continue;
						}

						// Move forward
						if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0
								& loadingMatrix[currentDepth][currentHeight - 1][currentWidth] != 0) {
							currentWidth++;
							continue;
						}

						// Go around an obstacle
						if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0
								& loadingMatrix[currentDepth][currentHeight][currentWidth] != 0) {
							while (currentWidth + 1 < loadingMatrix[0][0].length) {
								if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0) {
									currentWidth++;
								} else {
									canClimb = false;
									break;
								}
							}
							continue;
						}

						if (canClimb) {
							if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0) {
								while (currentWidth + 1 < loadingMatrix[0][0].length) {
									if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0) {
										currentWidth++;
									} else {
										canClimb = false;
										break;
									}
								}
								continue;
							}
						}

						// Move down
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}

						// Check if width = end
					} else if (currentWidth == loadingMatrix[0][0].length - 1) {

						// Move down near the wall
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}
					}

					// Check if height = bottom
				} else if (currentHeight == 0) {

					// Check if width < end
					if (currentWidth < loadingMatrix[0][0].length - 1) {

						// Move forward
						if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0) {
							currentWidth++;
							continue;
						}

						// Start to move up
						if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0) {
							currentHeight++;
							canClimb = true;
							continue;
						}
					} else if (currentWidth == loadingMatrix[0][0].length - 1) {
						break;
					}
				}
			}

			// Check if there are no ways
			if (currentHeight == 0 & currentWidth == loadingMatrix[0][0].length - 1
					| loadingMatrix[currentDepth][currentHeight - 1][currentWidth] != 0
							& currentWidth == loadingMatrix[0][0].length - 1
					| currentHeight == loadingMatrix.length & currentWidth == loadingMatrix[0][0].length - 1) {
				break;
			}
		}
		return false;
	}

	// Place box in matrix
	private void placeBox(Cargo box, int[][][] loadingMatrix, int currentHeight, int currentWidth, int currentDepth) {
		for (int i = currentDepth; i < currentDepth + box.getDepthInCells(); i++) {
			for (int j = currentHeight; j < currentHeight + box.getHeightInCells(); j++) {
				for (int k = currentWidth; k < currentWidth + box.getWidthInCells(); k++) {
					loadingMatrix[i][j][k] = box.getFragility();
				}
			}
		}
	}

	// Print all matrix
	private void printMatrix(int[][][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = matrix[0].length - 1; j >= 0; j--) {
				for (int k = 0; k < matrix[0][0].length; k++) {
					if (matrix[i][j][k] == 0) {
						System.out.print(". ");
					} else {
						System.out.print(matrix[i][j][k] + " ");
					}
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	// Test algorithm
	public static void main(String[] args) {
		List<Cargo> listCargo = new LinkedList<Cargo>();
//		Cargo box1 = new Cargo(1.2, 1.2, 1.2, 1, 1, "Kyiv");// 4x4x4
//		Cargo box2 = new Cargo(0.9, 0.9, 0.9, 2, 2, "Kyiv");// 3x3x3
//		Cargo box3 = new Cargo(0.9, 0.9, 0.6, 2, 3, "Lviv");// 3x3x2
//		Cargo box4 = new Cargo(0.6, 0.6, 2.4, 5, 4, "Lviv");// 2x2x8
//		Cargo box5 = new Cargo(0.9, 0.3, 1.2, 2, 5, "Lviv");// 3x1x4
//		Cargo box6 = new Cargo(0.6, 0.9, 1.2, 2, 6, "Kyiv");// 2x3x4
//		Cargo box7 = new Cargo(0.3, 0.3, 1.2, 2, 7, "Lviv");// 1x1x4
//		Cargo box8 = new Cargo(0.6, 0.6, 1.2, 2, 8, "Kyiv");// 2x2x4
//		Cargo box9 = new Cargo(0.9, 0.9, 0.9, 2, 9, "Kyiv");// 2x2x4
//
//		listCargo.add(box1);
//		listCargo.add(box2);
//		listCargo.add(box3);
//		listCargo.add(box4);
//		listCargo.add(box5);
//		listCargo.add(box6);
//		listCargo.add(box7);
//		listCargo.add(box8);
//		listCargo.add(box9);
//		listCargo.add(box1);
//		listCargo.add(box2);
//		listCargo.add(box3);
//		listCargo.add(box4);
//		listCargo.add(box5);
//		listCargo.add(box6);
//		listCargo.add(box7);
//		listCargo.add(box8);
//		listCargo.add(box9);

//		Cargo box1 = new Cargo(2.4, 2.4, 2.4, 1, 1, "Kyiv");
//		Cargo box2 = new Cargo(2.4, 2.4, 2.4, 1, 1, "Kyiv");
//		listCargo.add(box1);
//		listCargo.add(box2);

//		Cargo box1 = new Cargo(1.2, 1.2, 1.2, 1, 1, "Kyiv");
//		Cargo box2 = new Cargo(0.6, 0.6, 1.2, 2, 2, "Kyiv");
//		Cargo box3 = new Cargo(1.2, 1.2, 1.2, 2, 3, "Lviv");
//		Cargo box4 = new Cargo(0.6, 0.6, 1.2, 5, 4, "Lviv");
//		Cargo box5 = new Cargo(1.2, 1.2, 1.2, 2, 5, "Lviv");
//		Cargo box6 = new Cargo(0.6, 1.2, 1.2, 2, 6, "Kyiv");
//		Cargo box7 = new Cargo(0.3, 0.3, 0.6, 2, 7, "Lviv");
//		Cargo box8 = new Cargo(0.6, 0.6, 1.2, 2, 8, "Kyiv");
//		Cargo box9 = new Cargo(1.2, 1.2, 1.2, 2, 9, "Kyiv");
//		Cargo box11 = new Cargo(1.2, 1.2, 1.2, 1, 1, "Kyiv");
//		Cargo box12 = new Cargo(0.6, 0.6, 1.2, 2, 2, "Kyiv");
//		Cargo box13 = new Cargo(1.2, 1.2, 1.2, 2, 3, "Lviv");
//		Cargo box14 = new Cargo(0.6, 0.6, 1.2, 5, 4, "Lviv");
//		Cargo box15 = new Cargo(1.2, 1.2, 1.2, 2, 5, "Lviv");
//		Cargo box16 = new Cargo(0.6, 1.2, 1.2, 2, 6, "Kyiv");
//		Cargo box17 = new Cargo(0.3, 0.3, 0.6, 2, 7, "Lviv");
//		Cargo box18 = new Cargo(0.6, 0.6, 1.2, 2, 8, "Kyiv");
//		Cargo box19 = new Cargo(2.4, 2.4, 2.4, 2, 9, "Kyiv");
//
//		listCargo.add(box1);
//		listCargo.add(box2);
//		listCargo.add(box3);
//		listCargo.add(box4);
//		listCargo.add(box5);
//		listCargo.add(box6);
//		listCargo.add(box7);
//		listCargo.add(box8);
//		listCargo.add(box9);
//		listCargo.add(box11);
//		listCargo.add(box12);
//		listCargo.add(box13);
//		listCargo.add(box14);
//		listCargo.add(box15);
//		listCargo.add(box16);
//		listCargo.add(box17);
//		listCargo.add(box18);
//		listCargo.add(box19);

//		Cargo box1 = new Cargo(1.2, 1.2, 2.4, 1, 1, "Kyiv");
//		Cargo box2 = new Cargo(1.2, 1.2, 1.2, 2, 2, "Kyiv");
//		Cargo box3 = new Cargo(1.2, 1.2, 1.2, 3, 3, "Kyiv");
//		Cargo box4 = new Cargo(1.2, 1.2, 1.2, 4, 4, "Kyiv");
//		Cargo box5 = new Cargo(1.2, 1.2, 1.2, 5, 5, "Kyiv");
//
//		listCargo.add(box1);
//		listCargo.add(box2);
//		listCargo.add(box3);
//		listCargo.add(box4);
//		listCargo.add(box5);

//		for (int i = 0; i < 2563; i++) {
//			listCargo.add(new Cargo(0.3, 0.3, 0.3, 1, 1, "Kyiv"));
//		}

		Hub hub1 = new Hub("Kharkiv");
		Hub hub2 = new Hub("Kyiv");
		Hub hub3 = new Hub("Lviv");

		Route route = new Route(hub1, hub2, hub3);

		CargoHold cargohold = new CargoHold();

		CargoLoader3D cargoLoader = new CargoLoader3D();

		cargoLoader.loadCargo(listCargo, route, cargohold);

		// System.out.println(cargohold.getLoadedCargo());
	}
}
