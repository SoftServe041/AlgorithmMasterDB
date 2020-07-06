package cargoloader.helperclasses;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

import cargoloader.Cargo;

public class SurfaceScannerTest_2 {
//	@SuppressWarnings("unused")
//	private boolean checkPlace(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
//		if (checkVolume(box, loadingMatrix, currentWidthPos, currentHeightPos) == false
//				&& checkBottom(box, loadingMatrix, currentWidthPos, currentHeightPos) == false
//				&& checkTop(box, loadingMatrix, currentWidthPos, currentHeightPos) == false)
//			return false;
//		return true;
//	}
//
//	private boolean checkVolume(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
//		if (currentWidthPos + box.getWidthInCells() > loadingMatrix[0].length
//				&& currentHeightPos + box.getHeightInCells() > loadingMatrix.length) {
//			return false;
//		}
//
//		for (int j = currentHeightPos; j < currentHeightPos + box.getHeightInCells(); j++) {
//			for (int k = currentWidthPos; k < currentWidthPos + box.getWidthInCells(); k++) {
//				if (loadingMatrix[j][k] == 1)
//					return false;
//			}
//		}
//
//		return true;
//	}
//
//	@SuppressWarnings("unused")
//	private boolean checkBottom(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
//		int availableSquare = 0;
//		int boxSquare = box.getWidthInCells();
//		for (int i = currentWidthPos; i < currentWidthPos + box.getWidthInCells(); i++) {
//
//			if (currentHeightPos - 1 >= 0) {
//				availableSquare += loadingMatrix[currentHeightPos - 1][i];
//			} else if (currentHeightPos == 0) {
//				availableSquare = boxSquare;
//			}
//
//		}
//		if (availableSquare < boxSquare / 2) {
//			return false;
//		}
//		return true;
//	}
//
//	@SuppressWarnings("unused")
//	private boolean checkTop(Box box, int[][] loadingMatrix, int currentWidthPos, int currentHeightPos) {
//		for (int i = currentWidthPos; i < currentWidthPos + box.getWidthInCells(); i++) {
//			if (loadingMatrix[currentHeightPos + box.getHeightInCells()][i] == 1) {
//				return false;
//			}
//		}
//		return true;
//	}

	@SuppressWarnings("unused")
	public void initializeSurfaceScanner(Cargo box, int[][] loadingMatrix) {
		int heightPos = loadingMatrix.length - 1;
		int widthPos = 0;
		while (loadingMatrix[heightPos][widthPos] == 0 && heightPos > 0) {
			printMatrix(loadingMatrix, heightPos, widthPos);
			heightPos--;
		}
		scanSurface(box, widthPos, ++heightPos, loadingMatrix);
	}

	@SuppressWarnings("unused")
	private void scanSurface(Cargo box, int currentWidth, int currentHeight, int[][] loadingMatrix) {
		while (currentWidth < loadingMatrix[0].length) {
//			if (checkPlace(box, loadingMatrix, currentWidth, currentHeight)) {
//				System.out.println("Can place the Box");
//				// place box
//				break;
			if (false) {
			} else {
				if (currentHeight - 1 >= 0 & currentHeight < loadingMatrix.length) {
					if (currentWidth < loadingMatrix[0].length - 1) {
						if (loadingMatrix[currentHeight - 1][currentWidth] != 0
								& loadingMatrix[currentHeight][currentWidth + 1] == 0) {
							currentWidth++;
							printMatrix(loadingMatrix, currentHeight, currentWidth);
							continue;
						}

						if (loadingMatrix[currentHeight - 1][currentWidth] == 0
								& loadingMatrix[currentHeight - 1][currentWidth - 1] != 0) {
							currentHeight--;
							printMatrix(loadingMatrix, currentHeight, currentWidth);
							continue;
						}

						if (loadingMatrix[currentHeight - 1][currentWidth] == 0
								& loadingMatrix[currentHeight][currentWidth - 1] != 0) {
							currentHeight--;
							printMatrix(loadingMatrix, currentHeight, currentWidth);
							continue;
						}

						if (loadingMatrix[currentHeight][currentWidth + 1] == 0
								& loadingMatrix[currentHeight - 1][currentWidth + 1] != 0) {
							currentWidth++;
							printMatrix(loadingMatrix, currentHeight, currentWidth);
							continue;
						}

						if (currentHeight == loadingMatrix.length - 1) {
							if (loadingMatrix[currentHeight][currentWidth + 1] == 0) {
								currentWidth++;
								printMatrix(loadingMatrix, currentHeight, currentWidth);
								continue;
							}

							if (loadingMatrix[currentHeight][currentWidth + 1] != 0) {
								while (true) {
									if (loadingMatrix[currentHeight][currentWidth] == 0) {
										break;
									} else {
										currentWidth++;
									}
								}
								printMatrix(loadingMatrix, currentHeight, currentWidth);
								continue;
							}
						}

						if (loadingMatrix[currentHeight - 1][currentWidth] != 0
								& loadingMatrix[currentHeight][currentWidth + 1] != 0) {
							currentHeight++;
							printMatrix(loadingMatrix, currentHeight, currentWidth);
							continue;
						}

						if (loadingMatrix[currentHeight][currentWidth + 1] != 0) {
							currentHeight++;
							printMatrix(loadingMatrix, currentHeight, currentWidth);
							continue;
						}

						if (loadingMatrix[currentHeight + 1][currentWidth] == 0
								& loadingMatrix[currentHeight][currentWidth + 1] != 0) {
							currentHeight++;
							printMatrix(loadingMatrix, currentHeight, currentWidth);
							continue;
						}
					} else if (currentWidth == loadingMatrix[0].length - 1) {
						if (loadingMatrix[currentHeight - 1][currentWidth] == 0
								& loadingMatrix[currentHeight - 1][currentWidth - 1] != 0) {
							currentHeight--;
							printMatrix(loadingMatrix, currentHeight, currentWidth);
							continue;
						}

						if (loadingMatrix[currentHeight - 1][currentWidth] == 0
								& loadingMatrix[currentHeight][currentWidth - 1] != 0) {
							currentHeight--;
							printMatrix(loadingMatrix, currentHeight, currentWidth);
							continue;
						}
					}

				} else if (currentHeight == 0) {
					if (loadingMatrix[currentHeight][currentWidth + 1] == 0) {
						currentWidth++;
						printMatrix(loadingMatrix, currentHeight, currentWidth);
						continue;
					}

					if (loadingMatrix[currentHeight][currentWidth + 1] != 0) {
						currentHeight++;
						printMatrix(loadingMatrix, currentHeight, currentWidth);
						continue;
					}
				}
			}

			// when we are don`t have any ways
			if (currentHeight == 0 & currentWidth == loadingMatrix[0].length - 1
					| loadingMatrix[currentHeight - 1][currentWidth] != 0
							& currentWidth == loadingMatrix[0].length - 1) {
				break;
			}
		}
	}

	private void printMatrix(int[][] matrix, int h, int w) {
		matrix[h][w] = 9;
		for (int i = matrix.length - 1; i >= 0; i--) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (matrix[i][j] != 0 & matrix[i][j] != 9) {
					System.out.print("| ");
				} else if (matrix[i][j] == 0) {
					System.out.print(". ");
				} else if (matrix[i][j] == 9) {
					System.out.print("O ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void main(String[] args) {
		int[][] matrix = { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1 },
				{ 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1 },
				{ 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1 },
				{ 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0 },
				{ 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, };
		SurfaceScannerTest_2 scannerTest = new SurfaceScannerTest_2();
		Cargo box = new Cargo(1.2, 1.2, 1, 1, 1, "Kyiv");
		scannerTest.initializeSurfaceScanner(box, matrix);

	}
}
