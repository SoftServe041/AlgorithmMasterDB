package cargoloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import pathfinder.entities.Hub;
import pathfinder.entities.Route;

public class CargoLoader3D {
	private Map<String, List<Cargo>> unloadedCargo;
	private CargoSorter cargoSorter;

	public CargoLoader3D() {
		cargoSorter = new CargoSorter();
	}

	public void loadCargo(List<Cargo> boxes, Route route, CargoHold cargohold) {
		cargoSorter.sortCargoByDestination(boxes, route);
		unloadedCargo = cargoSorter.getSortedCargo();
		for (Map.Entry<String, List<Cargo>> entry : unloadedCargo.entrySet()) {
			if (cargohold.getLoadedCargo().containsKey(entry.getKey())) {
				initializeSurfaceScanner(entry.getValue(), cargohold.getLoadingMatrix(), cargohold.getLoadedCargo());
			} else {
				cargohold.getLoadedCargo().put(entry.getKey(), new Stack<Cargo>());
				initializeSurfaceScanner(entry.getValue(), cargohold.getLoadingMatrix(), cargohold.getLoadedCargo());
			}
		}
		printMatrix(cargohold.getLoadingMatrix());
	}

	// Check if we can fit box
	private boolean checkPlace(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepth) {

		if (currentHeightPos + box.getHeightInCells() - 1 > loadingMatrix[0].length - 1
				|| currentWidthPos + box.getWidthInCells() - 1 > loadingMatrix[0][0].length - 1
				|| currentDepth + box.getDepthInCells() - 1 > loadingMatrix.length - 1) {
			return false;
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
	// Need to check fragility too!
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
		int heightPos = loadingMatrix[0].length - 1;
		int widthPos = 0;
		int depthPos = 0;
		List<Cargo> unloadedBoxes = new LinkedList<Cargo>();
		unloadedBoxes.addAll(listBox);
		while (unloadedBoxes.size() > 0) {
			boolean canLoad = false;
			for (Cargo box : unloadedBoxes) {

				// Set starting position
				if (loadingMatrix[depthPos][heightPos][widthPos] != 0) {
					while (loadingMatrix[depthPos][heightPos][widthPos] != 0
							& widthPos + 1 < loadingMatrix[0][0].length) {
						widthPos++;
					}
				}

				while (heightPos > 0) {
					if (loadingMatrix[depthPos][heightPos - 1][widthPos] != 0) {
						break;
					} else {
						heightPos--;
					}
				}

				if (scanSurfaceAndPlaceBox(box, widthPos, heightPos, depthPos, loadingMatrix)) {
					heightPos = loadingMatrix[0].length - 1;
					widthPos = 0;
					loadedCargo.get(box.getDestination()).push(box);
					unloadedBoxes.remove(box);
					canLoad = true;
					break;
				}

//			if (scanSurfaceAndPlaceBox(box, widthPos, heightPos, loadingMatrix)) {
//				widthPos += box.getWidthInCells()-1;
//				loadedCargo.get(box.getDestination()).push(box);
//			} else {
//				widthPos = 0;
//				if (scanSurfaceAndPlaceBox(box, widthPos, heightPos, loadingMatrix)) {
//					loadedCargo.get(box.getDestination()).push(box);
//				}
//			}

			}
			if (!canLoad & depthPos < loadingMatrix.length) {
				depthPos++;
			}
		}

	}

	// Scan surface and place box
	private boolean scanSurfaceAndPlaceBox(Cargo box, int currentWidth, int currentHeight, int currentDepth,
			int[][][] loadingMatrix) {
		boolean canClimb = false;
		while (currentWidth < loadingMatrix[0][0].length) {
			if (checkPlace(box, loadingMatrix, currentWidth, currentHeight, currentDepth)) {
				placeBox(box, loadingMatrix, currentHeight, currentWidth, currentDepth);
				box.setDepthPos(currentDepth);
				box.setHeightPos(currentHeight);
				box.setWidthPos(currentWidth);
				return true;

			} else {

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

						// TODO: Change moving around an obstacle
						// Go around an obstacle
						if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0
								& loadingMatrix[currentDepth][currentHeight - 1][currentWidth] != 0) {
							while (true) {
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

	// Place box
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

	public static void main(String[] args) {
		List<Cargo> listCargo = new LinkedList<Cargo>();
		Cargo box1 = new Cargo(1.2, 1.2, 1.2, 1, 1, "Kyiv");// 4x4x4
		Cargo box2 = new Cargo(0.9, 0.9, 0.9, 2, 2, "Kyiv");// 3x3x3
		Cargo box3 = new Cargo(0.9, 0.9, 0.6, 2, 3, "Lviv");// 3x3x2
		Cargo box4 = new Cargo(0.6, 0.6, 2.4, 5, 4, "Lviv");// 2x2x8
		Cargo box5 = new Cargo(0.9, 0.3, 1.2, 2, 5, "Lviv");// 3x1x4
		Cargo box6 = new Cargo(0.6, 0.9, 1.2, 2, 6, "Kyiv");// 2x3x4
		Cargo box7 = new Cargo(0.3, 0.3, 1.2, 2, 7, "Lviv");// 1x1x4
		Cargo box8 = new Cargo(0.6, 0.6, 1.2, 2, 8, "Kyiv");// 2x2x4
		Cargo box9 = new Cargo(0.9, 0.9, 0.9, 2, 9, "Kyiv");// 2x2x4
		listCargo.add(box1);
		listCargo.add(box2);
		listCargo.add(box3);
		listCargo.add(box4);
		listCargo.add(box5);
		listCargo.add(box6);
		listCargo.add(box7);
		listCargo.add(box8);
		listCargo.add(box9);
		listCargo.add(box1);
		listCargo.add(box2);
		listCargo.add(box3);
		listCargo.add(box4);
		listCargo.add(box5);
		listCargo.add(box6);
		listCargo.add(box7);
		listCargo.add(box8);
		listCargo.add(box9);

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
