package cargoloader;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class CargoManager {
	@SuppressWarnings("unused")
	private boolean checkPlace(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepthPos) {
		if (checkVolume(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepthPos) == false
				&& checkBottom(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepthPos) == false
				&& checkTop(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepthPos) == false)
			return false;
		return true;
	}

	private boolean checkVolume(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepthPos) {
		if (currentWidthPos + box.getWidthInCells() - 1 > loadingMatrix[0][0].length
				&& currentHeightPos + box.getHeightInCells() - 1 > loadingMatrix[0].length
				&& currentDepthPos + box.getDepthInCells() - 1 > loadingMatrix.length) {
			return false;
		}
		for (int i = currentDepthPos; i < currentDepthPos + box.getDepthInCells(); i++) {
			for (int j = currentHeightPos; j < currentHeightPos + box.getHeightInCells(); j++) {
				for (int k = currentWidthPos; k < currentWidthPos + box.getWidthInCells(); k++) {
					if (loadingMatrix[i][j][k] == 1)
						return false;
				}
			}
		}

		return true;
	}

	@SuppressWarnings("unused")
	private boolean checkBottom(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepthPos) {
		int availableSquare = 0;
		int boxSquare = box.getDepthInCells() * box.getWidthInCells();
		for (int i = currentWidthPos; i < currentWidthPos + box.getWidthInCells(); i++) {
			for (int j = currentDepthPos; j < currentDepthPos + box.getDepthInCells(); j++) {
				if (currentHeightPos - 1 > 0) {
					availableSquare += loadingMatrix[j][currentHeightPos - 1][i];
				} else {
					availableSquare = boxSquare;
				}
			}
		}
		if (availableSquare < boxSquare / 2) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private boolean checkTop(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
			int currentDepthPos) {
		for (int i = currentWidthPos; i < currentWidthPos + box.getWidthInCells(); i++) {
			for (int j = currentDepthPos; j < currentDepthPos + box.getDepthInCells(); j++) {
				if (loadingMatrix[j][currentHeightPos + box.getHeightInCells()][i] == 1) {
					return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void initializeSurfaceScanner(Cargo box, int[][][] loadingMatrix, Map<String, Stack<Cargo>> loadedCargo) {
		for (int depth = 0; depth < loadingMatrix.length; depth++) {
			int heightPos = loadingMatrix[0].length - 1;
			int widthPos = 0;
			while (loadingMatrix[depth][heightPos][widthPos] != 1 && heightPos > 0) {
				heightPos--;
			}

		}
	}

	@SuppressWarnings("unused")
	private void scanSurface(int currentWidth, int currentHeight, int currentDepth, Cargo box, int[][][] loadingMatrix,
			Map<String, Stack<Cargo>> loadedCargo) {
		while (currentWidth < loadingMatrix[0][0].length) {
			if (checkPlace(box, loadingMatrix, currentWidth, currentHeight, currentDepth) == true) {
				// place box
				break;
			} else {
				if (currentHeight - 1 > 0 & currentHeight < loadingMatrix[0][0].length) {
					if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 1
							& loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0) {
						currentWidth++;
						continue;
					}

					if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0
							& loadingMatrix[currentDepth][currentHeight - 1][currentWidth - 1] == 1) {
						while (loadingMatrix[currentDepth][currentHeight][currentWidth] == 0 & currentHeight > 0) {
							currentHeight--;
							continue;
						}
					}

					if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 1
							& loadingMatrix[currentDepth][currentHeight][currentWidth - 1] == 1) {
						currentHeight--;
						continue;
					}

					if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 1
							& loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 1) {
						currentHeight++;
						continue;
					}

					if (loadingMatrix[currentDepth][currentHeight - 1][currentWidth] == 0
							& loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 1) {
						currentHeight++;
						continue;
					}

					if (loadingMatrix[currentDepth][currentHeight][currentWidth] == 0
							& loadingMatrix[currentDepth][currentHeight - 1][currentWidth + 1] == 1) {
						currentWidth++;
						continue;
					}
				} else if (currentHeight == 0) {
					if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 0) {
						currentWidth++;
						continue;
					}

					if (loadingMatrix[currentDepth][currentHeight][currentWidth + 1] == 1) {
						currentHeight++;
						continue;
					}
				}
			}
		}

	}
}
