package cargoloader;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This class represents 3D visualization of cargo loading algorithm. Program
 * uses JavaFX library. This program can be used for testing of cargo load
 * algorithm.
 */

// Create main class of application
public class CargoLoading3DAnimation extends Application {

	// Method for creating cargo boxes and visual components
	private Parent createContent() throws Exception {

		// Create and load cargo for visualization
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
		Cargo box10 = new Cargo(1.2, 1.2, 1.2, 2, 9, "Lviv");// 2x2x4
		Cargo box11 = new Cargo(0.9, 0.9, 0.9, 2, 1, "Lviv");// 3x3x3
		Cargo box12 = new Cargo(0.9, 0.9, 0.9, 2, 1, "Lviv");// 3x3x3
		Cargo box13 = new Cargo(0.9, 0.9, 0.9, 2, 3, "Lviv");// 3x3x3
		Cargo box14 = new Cargo(0.9, 0.9, 0.9, 2, 2, "Kyiv");// 2x2x4
		Cargo box15 = new Cargo(0.9, 0.9, 0.9, 2, 4, "Kyiv");// 2x2x4
		Cargo box16 = new Cargo(0.9, 0.9, 0.9, 2, 5, "Kyiv");// 2x2x4
		Cargo box17 = new Cargo(0.9, 0.9, 0.9, 2, 7, "Kyiv");// 2x2x4
		Cargo box18 = new Cargo(0.9, 0.3, 1.2, 2, 6, "Lviv");// 3x1x4
		Cargo box19 = new Cargo(0.9, 0.3, 1.2, 2, 8, "Lviv");// 3x1x4
		Cargo box20 = new Cargo(0.9, 0.3, 1.2, 2, 9, "Lviv");// 3x1x4

		listCargo.add(box1);
		listCargo.add(box2);
		listCargo.add(box3);
		listCargo.add(box4);
		listCargo.add(box5);
		listCargo.add(box6);
		listCargo.add(box7);
		listCargo.add(box8);
		listCargo.add(box9);
		listCargo.add(box10);
		listCargo.add(box11);
		listCargo.add(box12);
		listCargo.add(box13);
		listCargo.add(box14);
		listCargo.add(box15);
		listCargo.add(box16);
		listCargo.add(box17);
		listCargo.add(box18);
		listCargo.add(box19);
		listCargo.add(box20);

		// Create Hubs for route
		Hub hub1 = new Hub("Kharkiv");
		Hub hub2 = new Hub("Kyiv");
		Hub hub3 = new Hub("Lviv");

		// Create new route
		Route route = new Route(hub1, hub2, hub3);

		// Create cargo hold as part of transport
		CargoHold cargohold = new CargoHold();

		// Create cargo loader (contains algorithm)
		CargoLoader3D cargoLoader = new CargoLoader3D();

		// Load all cargo
		cargoLoader.loadCargo(listCargo, route, cargohold);

		// Initialize cargo hold as javafx object
		Box cargoHold = new Box(8, 8, 40); // (WIDTH,HEIGHT,DEPTH)
		cargoHold.setMaterial(new PhongMaterial(Color.DARKGRAY));
		cargoHold.setDrawMode(DrawMode.LINE);

		// Create time line for animation
		Timeline timeline = new Timeline();
		Duration timepoint = Duration.ZERO;
		Duration pause = Duration.seconds(0.5);

		// Initialize zero for coordinates
		double zeroWidth = -cargoHold.getWidth() / 2;
		double zeroHeight = cargoHold.getHeight() / 2;
		double zeroDepth = -cargoHold.getDepth() / 2;

		// --------------------------------------------------------------------------------------------
		// Create and position camera
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.getTransforms().addAll(new Rotate(60, Rotate.X_AXIS), new Rotate(-120, Rotate.Y_AXIS),
				new Rotate(60, Rotate.Z_AXIS), new Translate(0, 0, -60));
		// --------------------------------------------------------------------------------------------

		// Create list of 3D boxes for visualization and fill it
		List<Box> boxList = new LinkedList<Box>();
		for (Map.Entry<String, Stack<Cargo>> entry : cargohold.getLoadedCargo().entrySet()) {
			ListIterator<Cargo> iterator = entry.getValue().listIterator();
			while (iterator.hasNext()) {
				Cargo c = iterator.next();
				Box cargobox = new Box(c.getWidthInCells(), c.getHeightInCells(), c.getDepthInCells());
				switch (c.getFragility()) {
				case 1:
					cargobox.setMaterial(new PhongMaterial(Color.BROWN));
					break;
				case 2:
					cargobox.setMaterial(new PhongMaterial(Color.DARKRED));
					break;
				case 3:
					cargobox.setMaterial(new PhongMaterial(Color.RED));
					break;
				case 4:
					cargobox.setMaterial(new PhongMaterial(Color.ORANGERED));
					break;
				case 5:
					cargobox.setMaterial(new PhongMaterial(Color.DARKORANGE));
					break;
				case 6:
					cargobox.setMaterial(new PhongMaterial(Color.ORANGE));
					break;
				case 7:
					cargobox.setMaterial(new PhongMaterial(Color.DARKGOLDENROD));
					break;
				case 8:
					cargobox.setMaterial(new PhongMaterial(Color.GOLD));
					break;
				case 9:
					cargobox.setMaterial(new PhongMaterial(Color.LIGHTYELLOW));
					break;
				}

				// Set position for every cargo box
				cargobox.getTransforms()
						.addAll(new Translate(zeroWidth + c.getWidthPos() + (double) c.getWidthInCells() / 2,
								zeroHeight - c.getHeightPos() - (double) c.getHeightInCells() / 2,
								zeroDepth + c.getDepthPos() + (double) c.getDepthInCells() / 2));

				// Add box
				boxList.add(cargobox);
			}
		}

		// Build the Scene Graph
		Group root = new Group();
		root.getChildren().add(camera);
		root.getChildren().add(cargoHold);

		// Create loop with time pauses for animation of loading
		for (Box box : boxList) {
			timepoint = timepoint.add(pause);
			KeyFrame keyFrame = new KeyFrame(timepoint, e -> root.getChildren().add(box));
			timeline.getKeyFrames().add(keyFrame);
			// root.getChildren().add(box); // Add box to scene without time pauses
		}

		// Use a SubScene
		SubScene subScene = new SubScene(root, 1600, 900, true, SceneAntialiasing.BALANCED);
		subScene.setFill(Color.WHITE);
		subScene.setCamera(camera);
		
		Group group = new Group();
		group.getChildren().add(subScene);
		
		// Start timer
		timeline.play();
		
		return group;
	}

	// Method for application starting
	@Override
	public void start(Stage stage) throws Exception {
		stage.setResizable(false);
		Scene scene = new Scene(createContent());
		stage.setScene(scene);
		stage.show();
	}

	// Create and launch application
	public static void main(String[] args) {
		CargoLoading3DAnimation app = new CargoLoading3DAnimation();
		app.launch(args);
	}
}