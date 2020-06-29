package cargoloader;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

public class SurfaceScannerTest {

	// Check if we can fit box
	private boolean checkPlace(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
		if (checkVolume(box, loadingMatrix, currentWidthPos, currentHeightPos) == false
				&& checkBottom(box, loadingMatrix, currentWidthPos, currentHeightPos) == false
				&& checkTop(box, loadingMatrix, currentWidthPos, currentHeightPos) == false)
			return false;
		return true;
	}

	// Check free volume for box
	private boolean checkVolume(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
		if (currentWidthPos + box.getWidthInCells() > loadingMatrix[0].length
				&& currentHeightPos + box.getHeightInCells() > loadingMatrix.length) {
			return false;
		}

		for (int j = currentHeightPos; j < currentHeightPos + box.getHeightInCells(); j++) {
			for (int k = currentWidthPos; k < currentWidthPos + box.getWidthInCells(); k++) {
				if (loadingMatrix[j][k] != 0)
					return false;
			}
		}

		return true;
	}

	// Check cells under box
	private boolean checkBottom(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
		int availableSquare = 0;
		int boxSquare = box.getWidthInCells();
		for (int i = currentWidthPos; i < currentWidthPos + box.getWidthInCells(); i++) {
			if (currentHeightPos - 1 >= 0) {
				availableSquare += loadingMatrix[currentHeightPos - 1][i];
			}
		}
		if (availableSquare < boxSquare) {
			return false;
		}
		return true;
	}

	private boolean checkTop(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
		for (int i = currentWidthPos; i < currentWidthPos + box.getWidthInCells(); i++) {
			if (loadingMatrix[currentHeightPos + box.getHeightInCells()][i] != 0) {
				return false;
			}
		}
		return true;
	}

	// Start surface scanner for loading matrix
	public void initializeSurfaceScanner(Box box, int[][] loadingMatrix) {
		int heightPos = loadingMatrix.length - 1;
		int widthPos = 0;
		while (loadingMatrix[heightPos][widthPos] == 0 && heightPos > 0) {
			heightPos--;
		}
		scanSurface(box, widthPos, ++heightPos, loadingMatrix);
	}

	// Scan surface and place boxА
	private void scanSurface(Box box, int currentWidth, int currentHeight, int[][] loadingMatrix) {
		boolean canClimb = false;
		while (currentWidth < loadingMatrix[0].length) {
			if (checkPlace(box, loadingMatrix, currentWidth, currentHeight)) {
				// Place box
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

						// Move down
						if (loadingMatrix[currentHeight - 1][currentWidth] == 0) {
							currentHeight--;
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

	private void printMatrix(int[][] matrix) {
		for (int i = matrix.length - 1; i >= 0; i--) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (matrix[i][j] != 0) {
					System.out.print("N ");
				} else if (matrix[i][j] == 0) {
					System.out.print(". ");
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
		Box box = new Box(1.2, 1.2, 1, 1, 1, "Kyiv");
		scannerTest.initializeSurfaceScanner(box, matrix);
		scannerTest.printMatrix(matrix);

	}
}
