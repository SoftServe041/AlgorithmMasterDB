package cargoloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import pathfinder.entities.Hub;
import pathfinder.entities.Route;

public class CargoLoader3D {
	private Map<String, List<Box>> unloadedCargo;
	private CargoSorter cargoSorter;

	public CargoLoader3D() {
		cargoSorter = new CargoSorter();
	}

	public void loadCargo(List<Box> boxes, Route route, CargoHold cargohold) {
		cargoSorter.sortCargoByDestination(boxes, route);
		unloadedCargo = cargoSorter.getSortedCargo();
		for (Map.Entry<String, List<Box>> entry : unloadedCargo.entrySet()) {
			if (cargohold.getLoadedCargo().containsKey(entry.getKey())) {
				initializeSurfaceScanner(entry.getValue(), cargohold.getLoadingMatrix(), cargohold.getLoadedCargo());
			} else {
				cargohold.getLoadedCargo().put(entry.getKey(), new Stack<Box>());
				initializeSurfaceScanner(entry.getValue(), cargohold.getLoadingMatrix(), cargohold.getLoadedCargo());
			}
		}
		printMatrix(cargohold.getLoadingMatrix());
	}

	// Check if we can fit box
	private boolean checkPlace(Box box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepth) {
		if (currentHeightPos + box.getHeightInCells() - 1 > loadingMatrix[0].length - 1
				|| currentWidthPos + box.getWidthInCells() - 1 > loadingMatrix[0][0].length - 1
				|| currentDepth + box.getDepthInCells() - 1 > loadingMatrix.length) {
			return false;
		} else if (checkVolume(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepth) == false
				|| checkBottom(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepth) == false
				|| checkTop(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepth) == false) {
			return false;
		}
		return true;
	}

	// Check free volume for box
	private boolean checkVolume(Box box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
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
	private boolean checkBottom(Box box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
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
	private boolean checkTop(Box box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepthPos) {
		for (int i = currentDepthPos; i < currentDepthPos + box.getDepthInCells(); i++) {
			for (int j = currentWidthPos; j < currentWidthPos + box.getWidthInCells(); j++) {
				if (loadingMatrix[i][currentHeightPos + box.getHeightInCells()][j] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	// Start surface scanner for loading matrix
	public void initializeSurfaceScanner(List<Box> listBox, int[][][] loadingMatrix,
			Map<String, Stack<Box>> loadedCargo) {
		int heightPos = loadingMatrix[0].length - 1;
		int widthPos = 0;
		int depthPos = 0;
		int unloadedBoxes = listBox.size();

		for (Box box : listBox) {

			// Set starting position
			if (loadingMatrix[depthPos][heightPos][widthPos] != 0) {
				while (loadingMatrix[depthPos][heightPos][widthPos] != 0 & widthPos + 1 < loadingMatrix[0].length) {
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

			// TODO: ASK ABOUT CONCEPT
			if (scanSurfaceAndPlaceBox(box, widthPos, heightPos, depthPos, loadingMatrix)) {
				heightPos = loadingMatrix[0].length - 1;
				widthPos = 0;
				loadedCargo.get(box.getDestination()).push(box);
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

	}

	// Scan surface and place box
	private boolean scanSurfaceAndPlaceBox(Box box, int currentWidth, int currentHeight, int currentDepth,
			int[][][] loadingMatrix) {
		boolean canClimb = false;
		while (currentWidth < loadingMatrix[0].length) {

			if (checkPlace(box, loadingMatrix, currentWidth, currentHeight, currentDepth)) {
				placeBox(box, loadingMatrix, currentHeight, currentWidth, currentDepth);
				return true;

			} else {

				// Check if height < top and height > bottom
				if (currentHeight - 1 >= 0 & currentHeight < loadingMatrix.length - 1) {

					// Check if width < end
					if (currentWidth < loadingMatrix[0].length - 1) {

						// Move forward
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] != 0
								& loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0) {
							currentWidth++;
							continue;
						}

						// Start to move up
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] != 0
								& loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0) {
							currentHeight++;
							canClimb = true;
							continue;
						}

						// Move up
						if (canClimb) {
							if (loadingMatrix[currentDepth][currentHeight + 1][currentWidth] == 0
									& loadingMatrix[currentDepth][currentHeight][currentWidth + 1] != 0) {
								currentHeight++;
								continue;
							}
						}

						// Climb to the edge
						if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0
								& loadingMatrix[currentDepth][currentHeight - 1][currentWidth + 1] != 0) {
							currentWidth++;
							canClimb = false;
							continue;
						}

						// Move down
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}

						// Check if width = end
					} else if (currentWidth == loadingMatrix[0].length - 1) {

						// Move down near the wall
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}
					}

					// Check if height = top
				} else if (currentHeight == loadingMatrix.length - 1) {

					// Check if width < end
					if (currentWidth < loadingMatrix[0].length - 1) {

						// Move forward
						if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0
								& loadingMatrix[currentDepth][currentHeight - 1][currentWidth] != 0) {
							currentWidth++;
							continue;
						}

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
						}

						// Move down
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}

						// Check if width = end
					} else if (currentWidth == loadingMatrix[0].length - 1) {

						// Move down near the wall
						if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}
					}

					// Check if height = bottom
				} else if (currentHeight == 0) {
					// Check if width < end
					if (currentWidth < loadingMatrix[0].length - 1) {

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
					} else if (currentWidth == loadingMatrix[0].length - 1) {
						break;
					}
				}
			}

			// Check if there are no ways
			if (currentHeight == 0 & currentWidth == loadingMatrix[0].length - 1
					| loadingMatrix[currentDepth][currentHeight - 1][currentWidth] != 0
							& currentWidth == loadingMatrix[0].length - 1
					| currentHeight == loadingMatrix.length & currentWidth == loadingMatrix[0].length - 1) {
				break;
			}
		}
		return false;
	}

	// Place box
	private void placeBox(Box box, int[][][] loadingMatrix, int currentHeight, int currentWidth, int currentDepth) {
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
		List<Box> listBox = new LinkedList<Box>();
		Box box1 = new Box(1.2, 1.2, 1, 1, 1, "Kyiv");
		Box box2 = new Box(0.9, 0.9, 1, 2, 2, "Kyiv");
		Box box3 = new Box(0.9, 0.9, 1, 2, 3, "Lviv");
		Box box4 = new Box(0.6, 0.6, 1, 5, 4, "Lviv");
		Box box5 = new Box(0.9, 0.3, 1, 2, 5, "Lviv");
		Box box6 = new Box(0.6, 0.9, 1, 2, 6, "Kyiv");
		Box box7 = new Box(0.3, 0.3, 1, 2, 7, "Lviv");
		Box box8 = new Box(0.6, 0.6, 1, 2, 8, "Kyiv");
		listBox.add(box1);
		listBox.add(box2);
		listBox.add(box3);
		listBox.add(box4);
//		listBox.add(box5);
//		listBox.add(box6);
//		listBox.add(box7);
//		listBox.add(box8);

		Hub hub1 = new Hub("Kharkiv");
		Hub hub2 = new Hub("Kyiv");
		Hub hub3 = new Hub("Lviv");
		Route route = new Route(hub1, hub2, hub3);
		CargoHold cargohold = new CargoHold();
		CargoLoader3D cargoLoader = new CargoLoader3D();
		cargoLoader.loadCargo(listBox, route, cargohold);
	}
}
