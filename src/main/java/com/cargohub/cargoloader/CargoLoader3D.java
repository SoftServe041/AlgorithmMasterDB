package com.cargohub.cargoloader;

import com.cargohub.entities.RouteEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is a main class of cargo loading algorithm. Object of this class is like
 * a worker. Give to that class a link to cargo hold, and cargo loader will pack
 * all boxes in it.
 */

@Component
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
    public void loadCargo(List<Cargo> boxes, RouteEntity route, CargoHold cargohold) {

        // First of all sort all boxes by destination, fragility and volume
        cargoSorter.sortCargoByDestination(boxes, route);

        // Initialize unsorted cargo
        unloadedCargo = cargoSorter.getSortedCargo();

        // Initialize surface scanner for each destination point, search place for boxes
        for (Map.Entry<String, List<Cargo>> entry : unloadedCargo.entrySet()) {
            if (!cargohold.getLoadedCargo().containsKey(entry.getKey())) {
                cargohold.getLoadedCargo().put(entry.getKey(), new ArrayList<>());
            }
            initializeSurfaceScanner(entry.getValue(), cargohold.getLoadingMatrix(), cargohold.getLoadedCargo());
        }

        // Print loading matrix (useful for testing)
        printMatrix(cargohold.getLoadingMatrix());
    }

    public void unloadCargo() {

    }

    // TODO: Restore loadingMatrix in CargoHold from entities
    // Method to check level of loading into compartment
    public boolean checkLoadingForOrder(List<Cargo> listBox, int[][][] loadingMatrix) {

        // Initialize starting position
        int heightPos = loadingMatrix[0].length - 1;
        int widthPos = 0;
        int depthPos = 0;

        List<Cargo> unloadedBoxes = new LinkedList<Cargo>(listBox);

        while (unloadedBoxes.size() > 0 & depthPos < loadingMatrix.length) {
            boolean canLoad = false;
            for (Cargo box : unloadedBoxes) {
                if (scanSurfaceAndPlaceBox(box, widthPos, heightPos, depthPos, loadingMatrix)) {
                    heightPos = loadingMatrix[0].length - 1;
                    widthPos = 0;
                    unloadedBoxes.remove(box);
                    canLoad = true;
                    break;
                }
            }
            if (!canLoad) {
                depthPos++;
            }
        }
        if(unloadedBoxes.size() > 0){
            return false;
        }
        return true;
    }

    // Check if we can fit box
    private boolean checkPlace(Cargo box, int[][][] loadingMatrix, int currentWidthPos, int currentHeightPos,
                               int currentDepth) {

        // Check borders of cargo hold
        if (currentHeightPos + box.getHeightInCells() > loadingMatrix[0].length
                || currentWidthPos + box.getWidthInCells() > loadingMatrix[0][0].length
                || currentDepth + box.getDepthInCells() > loadingMatrix.length) {
            return false;

            // Check free place for box from all sides
        } else if (!checkVolume(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepth)
                || !checkBottom(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepth)
                || !checkTop(box, loadingMatrix, currentWidthPos, currentHeightPos, currentDepth)) {
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
                                         Map<String, List<Cargo>> loadedCargo) {

        // Initialize starting position
        int heightPos = loadingMatrix[0].length - 1;
        int widthPos = 0;
        int depthPos = 0;

        List<Cargo> unloadedBoxes = new LinkedList<Cargo>();
        unloadedBoxes.addAll(listBox);

        while (unloadedBoxes.size() > 0 & depthPos < loadingMatrix.length) {
            boolean canLoad = false;
            for (Cargo box : unloadedBoxes) {
                if (scanSurfaceAndPlaceBox(box, widthPos, heightPos, depthPos, loadingMatrix)) {
                    heightPos = loadingMatrix[0].length - 1;
                    widthPos = 0;
                    loadedCargo.get(box.getDestination()).add(box);
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
}
