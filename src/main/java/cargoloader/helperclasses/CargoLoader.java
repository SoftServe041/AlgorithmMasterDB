package cargoloader.helperclasses;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cargoloader.Cargo;
import cargoloader.CargoHold;
import cargoloader.CargoSorter;
import pathfinder.entities.Hub;
import pathfinder.entities.Route;

public class CargoLoader {
	private Map<String, List<Cargo>> unloadedCargo;
	private CargoSorter cargoSorter;

	public CargoLoader() {
		cargoSorter = new CargoSorter();
	}

	public void loadCargo(List<Cargo> boxes, Route route, CargoHold cargohold) {
		cargoSorter.sortCargoByDestination(boxes, route);
		unloadedCargo = cargoSorter.getSortedCargo();
		for (Map.Entry<String, List<Cargo>> entry : unloadedCargo.entrySet()) {
			if (cargohold.getLoadedCargo().containsKey(entry.getKey())) {
				//initializeSurfaceScanner(entry.getValue(), cargohold.getLoadingMatrix(), cargohold.getLoadedCargo());
			} else {
				cargohold.getLoadedCargo().put(entry.getKey(), new Stack<Cargo>());
				//initializeSurfaceScanner(entry.getValue(), cargohold.getLoadingMatrix(), cargohold.getLoadedCargo());
			}
		}
		//printMatrix(cargohold.getLoadingMatrix());
	}

	// Check if we can fit box
	private boolean checkPlace(Cargo box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
		if (currentHeightPos + box.getHeightInCells() - 1 > loadingMatrix.length - 1
				|| currentWidthPos + box.getWidthInCells() - 1 > loadingMatrix[0].length - 1) {
			return false;
		} else if (checkVolume(box, loadingMatrix, currentWidthPos, currentHeightPos) == false
				|| checkBottom(box, loadingMatrix, currentWidthPos, currentHeightPos) == false
				|| checkTop(box, loadingMatrix, currentWidthPos, currentHeightPos) == false) {
			return false;
		}
		return true;
	}

	// Check free volume for box
	private boolean checkVolume(Cargo box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
		for (int j = currentHeightPos; j < currentHeightPos + box.getHeightInCells(); j++) {
			for (int k = currentWidthPos; k < currentWidthPos + box.getWidthInCells(); k++) {
				if (loadingMatrix[j][k] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	// Check cells under box
	// Need to check fragility too!
	private boolean checkBottom(Cargo box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
		int boxSquare = box.getWidthInCells();
		int availableSquare = 0;
		boolean isInAir = true;
		if (currentHeightPos - 1 >= 0) {
			for (int i = currentWidthPos; i < currentWidthPos + box.getWidthInCells(); i++) {
				if (loadingMatrix[currentHeightPos - 1][i] != 0) {
					availableSquare++;
					isInAir = false;
				}
			}
			if ((availableSquare < boxSquare) | isInAir == true) {
				return false;
			}
		}
		return true;
	}

	// Check for box from above
	private boolean checkTop(Cargo box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
		for (int i = currentWidthPos; i < currentWidthPos + box.getWidthInCells(); i++) {
			if (loadingMatrix[currentHeightPos + box.getHeightInCells() - 1][i] != 0) {
				return false;
			}
		}
		return true;
	}

	// Start surface scanner for loading matrix
	public void initializeSurfaceScanner(List<Cargo> listBox, int[][] loadingMatrix,
			Map<String, Stack<Cargo>> loadedCargo) {
		int heightPos = loadingMatrix.length - 1;
		int widthPos = 0;

		for (Cargo box : listBox) {

			// Set starting position
			if (loadingMatrix[heightPos][widthPos] != 0) {
				while (loadingMatrix[heightPos][widthPos] != 0 & widthPos + 1 < loadingMatrix[0].length) {
					widthPos++;
				}
			}

			while (heightPos > 0) {
				if (loadingMatrix[heightPos - 1][widthPos] != 0) {
					break;
				} else {
					heightPos--;
				}
			}

			// TODO: ASK ABOUT CONCEPT
			if (scanSurfaceAndPlaceBox(box, widthPos, heightPos, loadingMatrix)) {
				heightPos = loadingMatrix.length - 1;
				widthPos = 0;
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
	private boolean scanSurfaceAndPlaceBox(Cargo box, int currentWidth, int currentHeight, int[][] loadingMatrix) {
		boolean canClimb = false;
		while (currentWidth < loadingMatrix[0].length) {

			if (checkPlace(box, loadingMatrix, currentWidth, currentHeight)) {
				placeBox(box, loadingMatrix, currentHeight, currentWidth);
				return true;

			} else {

				// Check if height < top and height > bottom
				if (currentHeight - 1 >= 0 & currentHeight < loadingMatrix.length - 1) {

					// Check if width < end
					if (currentWidth < loadingMatrix[0].length - 1) {

						// Move forward
						if (loadingMatrix[currentHeight - 1][currentWidth] != 0
								& loadingMatrix[currentHeight][currentWidth + 1] == 0) {
							currentWidth++;
							continue;
						}

						// Start to move up
						if (loadingMatrix[currentHeight - 1][currentWidth] != 0
								& loadingMatrix[currentHeight][currentWidth + 1] != 0) {
							currentHeight++;
							canClimb = true;
							continue;
						}

						// Move up
						if (canClimb) {
							if (loadingMatrix[currentHeight + 1][currentWidth] == 0
									& loadingMatrix[currentHeight][currentWidth + 1] != 0) {
								currentHeight++;
								continue;
							}
						}

						// Climb to the edge
						if (loadingMatrix[currentHeight][currentWidth + 1] == 0
								& loadingMatrix[currentHeight - 1][currentWidth + 1] != 0) {
							currentWidth++;
							canClimb = false;
							continue;
						}

						// Move down
						if (loadingMatrix[currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}

						// Check if width = end
					} else if (currentWidth == loadingMatrix[0].length - 1) {

						// Move down near the wall
						if (loadingMatrix[currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}
					}

					// Check if height = top
				} else if (currentHeight == loadingMatrix.length - 1) {

					// Check if width < end
					if (currentWidth < loadingMatrix[0].length - 1) {

						// Move forward
						if (loadingMatrix[currentHeight][currentWidth + 1] == 0
								& loadingMatrix[currentHeight - 1][currentWidth] != 0) {
							currentWidth++;
							continue;
						}

						// Go around an obstacle
						if (loadingMatrix[currentHeight][currentWidth + 1] != 0
								& loadingMatrix[currentHeight - 1][currentWidth] != 0) {
							while (true) {
								if (loadingMatrix[currentHeight][currentWidth + 1] != 0) {
									currentWidth++;
								} else {
									canClimb = false;
									break;
								}
							}
							continue;
						}

						if (canClimb) {
							if (loadingMatrix[currentHeight][currentWidth + 1] != 0) {
								while (true) {
									if (loadingMatrix[currentHeight][currentWidth + 1] != 0) {
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
						if (loadingMatrix[currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}

						// Check if width = end
					} else if (currentWidth == loadingMatrix[0].length - 1) {

						// Move down near the wall
						if (loadingMatrix[currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
							continue;
						}
					}

					// Check if height = bottom
				} else if (currentHeight == 0) {
					// Check if width < end
					if (currentWidth < loadingMatrix[0].length - 1) {

						// Move forward
						if (loadingMatrix[currentHeight][currentWidth + 1] == 0) {
							currentWidth++;
							continue;
						}

						// Start to move up
						if (loadingMatrix[currentHeight][currentWidth + 1] != 0) {
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
					| loadingMatrix[currentHeight - 1][currentWidth] != 0 & currentWidth == loadingMatrix[0].length - 1
					| currentHeight == loadingMatrix.length & currentWidth == loadingMatrix[0].length - 1) {
				break;
			}
		}
		return false;
	}

	// Place box
	private void placeBox(Cargo box, int[][] loadingMatrix, int currentHeight, int currentWidth) {
		for (int i = currentHeight; i < currentHeight + box.getHeightInCells(); i++) {
			for (int j = currentWidth; j < currentWidth + box.getWidthInCells(); j++) {
				loadingMatrix[i][j] = box.getFragility();
			}
		}
	}

	// Print all matrix
	private void printMatrix(int[][] matrix) {
		for (int i = matrix.length - 1; i >= 0; i--) {
			for (int j = 0; j < matrix[0].length; j++) {
//				if (matrix[i][j] != 0) {
//					System.out.print("N ");
//				} else if (matrix[i][j] == 0) {
//					System.out.print(". ");
//				}
				if (matrix[i][j] == 0) {
					System.out.print(". ");
				} else {
					System.out.print(matrix[i][j] + " ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void main(String[] args) {
		List<Cargo> listBox = new LinkedList<Cargo>();
		Cargo box1 = new Cargo(1.2, 1.2, 1, 1, 1, "Kyiv");
		Cargo box2 = new Cargo(0.9, 0.9, 1, 2, 2, "Kyiv");
		Cargo box3 = new Cargo(0.9, 0.9, 1, 2, 3, "Lviv");
		Cargo box4 = new Cargo(0.6, 0.6, 1, 5, 4, "Lviv");
		Cargo box5 = new Cargo(0.9, 0.3, 1, 2, 5, "Lviv");
		Cargo box6 = new Cargo(0.6, 0.9, 1, 2, 6, "Kyiv");
		Cargo box7 = new Cargo(0.3, 0.3, 1, 2, 7, "Lviv");
		Cargo box8 = new Cargo(0.6, 0.6, 1, 2, 8, "Kyiv");
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
		CargoLoader cargoLoader = new CargoLoader();
		cargoLoader.loadCargo(listBox, route, cargohold);
	}
}
