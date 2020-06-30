package cargoloader;

import java.util.Arrays;
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
	// Needs to check fragility too!
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
	public void initializeSurfaceScanner(Box box, int[][] loadingMatrix) {
		int heightPos = loadingMatrix.length - 1;
		int widthPos = 0;
		while (heightPos > 0) {
			if (loadingMatrix[heightPos][widthPos] != 0 | heightPos == 0) {
				heightPos++;
				break;
			} else {
				heightPos--;
			}
		}
		scanSurface(box, widthPos, heightPos, loadingMatrix);
	}

	// Scan surface and place box
	private void scanSurface(Box box, int currentWidth, int currentHeight, int[][] loadingMatrix) {
		boolean canClimb = false;
		while (currentWidth < loadingMatrix[0].length) {
			if (checkPlace(box, loadingMatrix, currentWidth, currentHeight)) {
				placeBox(box, loadingMatrix, currentHeight, currentWidth);
				break;
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
						if (canClimb) {
							if (loadingMatrix[currentHeight][currentWidth + 1] == 0
									& loadingMatrix[currentHeight - 1][currentWidth + 1] != 0) {
								currentWidth++;
								canClimb = false;
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
						if (loadingMatrix[currentHeight - 1][currentWidth] == 0
								& loadingMatrix[currentHeight][currentWidth - 1] != 0) {
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
								if (loadingMatrix[currentHeight][currentWidth] == 0) {
									break;
								} else {
									currentWidth++;
								}
							}
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
						if (loadingMatrix[currentHeight - 1][currentWidth] == 0
								& loadingMatrix[currentHeight][currentWidth - 1] != 0) {
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
					| loadingMatrix[currentHeight - 1][currentWidth] != 0
							& currentWidth == loadingMatrix[0].length - 1) {
				break;
			}
		}
	}

	private void placeBox(Box box, int[][] loadingMatrix, int currentHeight, int currentWidth) {
		for (int i = currentHeight; i < currentHeight + box.getHeightInCells(); i++) {
			for (int j = currentWidth; j < currentWidth + box.getWidthInCells(); j++) {
				loadingMatrix[i][j] = box.getFragility();
			}
		}
	}

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
		Box box = new Box(0.9, 0.9, 1, 2, 2, "Kyiv");
		Box box2 = new Box(0.6, 0.6, 1, 5, 5, "Kyiv");
		Box box3 = new Box(1.2, 1.2, 1, 1, 1, "Kyiv");
		Box box4 = new Box(0.9, 0.3, 1, 2, 7, "Kyiv");
		Box box5 = new Box(0.9, 0.9, 1, 2, 4, "Kyiv");
		Box box6 = new Box(0.9, 0.9, 1, 2, 8, "Kyiv");
		scannerTest.initializeSurfaceScanner(box, matrix);
		scannerTest.initializeSurfaceScanner(box2, matrix);
		scannerTest.initializeSurfaceScanner(box3, matrix);
		scannerTest.initializeSurfaceScanner(box4, matrix);
		scannerTest.initializeSurfaceScanner(box5, matrix);
		scannerTest.initializeSurfaceScanner(box6, matrix);
		scannerTest.printMatrix(matrix);

	}
}
