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
import pathfinder.entities.Hub;
import pathfinder.entities.Route;

public class CameraRotationApp extends Application {

	private Parent createContent() throws Exception {

		// Load cargo for visualization
		List<Cargo> listCargo = new LinkedList<Cargo>();
		Cargo box1 = new Cargo(1.2, 1.2, 1.2, 1, 1, "Kyiv");// 4x4x4
		Cargo box2 = new Cargo(0.9, 0.9, 0.9, 2, 2, "Kyiv");// 3x3x3
		Cargo box3 = new Cargo(0.9, 0.9, 0.6, 2, 3, "Lviv");// 3x3x2
		Cargo box4 = new Cargo(0.6, 0.6, 2.4, 5, 4, "Lviv");// 2x2x8
		Cargo box5 = new Cargo(0.9, 0.3, 1.2, 2, 5, "Lviv");// 3x1x4
		Cargo box6 = new Cargo(0.6, 0.9, 1.2, 2, 6, "Kyiv");// 2x3x4
		Cargo box7 = new Cargo(0.3, 0.3, 1.2, 2, 7, "Lviv");// 1x1x4
		Cargo box8 = new Cargo(0.6, 0.6, 1.2, 2, 8, "Kyiv");// 2x2x4
		listCargo.add(box1);
		listCargo.add(box2);
		listCargo.add(box3);
		listCargo.add(box4);
		listCargo.add(box5);
		listCargo.add(box6);
		listCargo.add(box7);
		listCargo.add(box8);

		Hub hub1 = new Hub("Kharkiv");
		Hub hub2 = new Hub("Kyiv");
		Hub hub3 = new Hub("Lviv");
		Route route = new Route(hub1, hub2, hub3);
		CargoHold cargohold = new CargoHold();
		CargoLoader3D cargoLoader = new CargoLoader3D();
		cargoLoader.loadCargo(listCargo, route, cargohold);

		// Initialize CargoHold
		Box cargoHold = new Box(40, 8, 8); // (DEPTH, WIDTH, HEIGHT)
		cargoHold.setMaterial(new PhongMaterial(Color.DARKGRAY));
		cargoHold.setDrawMode(DrawMode.LINE);

		// Initialize zero for coordinates
		double zeroDepth = -cargoHold.getWidth() / 2;
		double zeroWidth = -cargoHold.getHeight() / 2;
		double zeroHeight = -cargoHold.getDepth() / 2;

		// --------------------------------------------------------------------------------------------
		// Create and position camera
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.getTransforms().addAll(new Rotate(-60, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS),
				new Rotate(0, Rotate.Z_AXIS), new Translate(0, 0, -60));
		// --------------------------------------------------------------------------------------------

		List<Box> boxList = new LinkedList<Box>();
		for (Map.Entry<String, Stack<Cargo>> entry : cargohold.getLoadedCargo().entrySet()) {
			ListIterator<Cargo> iterator = entry.getValue().listIterator();
			while (iterator.hasNext()) {
				Cargo c = iterator.next();
				Box cargobox = new Box(c.getWidthInCells(), c.getHeightInCells(), c.getDepthInCells());
				cargobox.getTransforms()
						.addAll(new Translate(zeroDepth + (double) c.getDepthInCells() / 2 + c.getDepthPos(),
								zeroWidth + (double) c.getWidthInCells() / 2 + c.getWidthPos(),
								zeroHeight + (double) c.getHeightInCells() / 2 + c.getHeightPos()));
				boxList.add(cargobox);
			}
		}
		// Build the Scene Graph
		Group root = new Group();
		root.getChildren().add(camera);
		root.getChildren().add(cargoHold);
		for (Box box : boxList) {
			root.getChildren().add(box);
		}

		// Use a SubScene
		SubScene subScene = new SubScene(root, 1600, 900, true, SceneAntialiasing.BALANCED);
		subScene.setFill(Color.WHITE);
		subScene.setCamera(camera);
		Group group = new Group();
		group.getChildren().add(subScene);

		return group;
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setResizable(false);
		Scene scene = new Scene(createContent());
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		CameraRotationApp app = new CameraRotationApp();
		app.launch(args);
	}
}