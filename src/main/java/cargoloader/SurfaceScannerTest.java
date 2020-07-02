package cargoloader;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.text.Highlighter.Highlight;

public class SurfaceScannerTest {

	// Check if we can fit box
	private boolean checkPlace(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
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
	private boolean checkVolume(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
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
	private boolean checkBottom(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
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
	private boolean checkTop(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
		for (int i = currentWidthPos; i < currentWidthPos + box.getWidthInCells(); i++) {
			if (loadingMatrix[currentHeightPos + box.getHeightInCells() - 1][i] != 0) {
				return false;
			}
		}
		return true;
	}

	// Start surface scanner for loading matrix
	public void initializeSurfaceScanner(List<Box> listBox, int[][] loadingMatrix) {
		int heightPos = loadingMatrix.length - 1;
		int widthPos = 0;

		for (Box box : listBox) {

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

			if (scanSurfaceAndPlaceBox(box, widthPos, heightPos, loadingMatrix)) {
				heightPos = loadingMatrix.length - 1;
				widthPos += box.getWidthInCells() - 1;
			} else {
				widthPos = 0;
				scanSurfaceAndPlaceBox(box, widthPos, heightPos, loadingMatrix);
			}
		}
	}

	// Scan surface and place box
	private boolean scanSurfaceAndPlaceBox(Box box, int currentWidth, int currentHeight, int[][] loadingMatrix) {
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
	private void placeBox(Box box, int[][] loadingMatrix, int currentHeight, int currentWidth) {
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
		int[][] matrix = { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };

		SurfaceScannerTest scannerTest = new SurfaceScannerTest();
		List<Box> listBox = new LinkedList<Box>();

		Box box1 = new Box(1.2, 1.2, 1, 1, 1, "Kyiv");
		Box box2 = new Box(0.9, 0.9, 1, 2, 2, "Kyiv");
		Box box3 = new Box(0.9, 0.9, 1, 2, 3, "Kyiv");
		Box box4 = new Box(0.6, 0.6, 1, 5, 4, "Kyiv");
		Box box5 = new Box(0.9, 0.3, 1, 2, 5, "Kyiv");
		Box box6 = new Box(0.6, 0.9, 1, 2, 6, "Kyiv");
		Box box7 = new Box(0.3, 0.3, 1, 2, 7, "Kyiv");
		Box box8 = new Box(0.3, 0.9, 1, 2, 8, "Kyiv");
		Box box9 = new Box(1.2, 1.2, 1, 2, 9, "Kyiv");

		listBox.add(box1);
		listBox.add(box2);
		listBox.add(box9);
		listBox.add(box3);
		listBox.add(box4);
		listBox.add(box5);
		listBox.add(box6);
		listBox.add(box7);
		listBox.add(box8);

		scannerTest.initializeSurfaceScanner(listBox, matrix);

		scannerTest.printMatrix(matrix);

	}
}
