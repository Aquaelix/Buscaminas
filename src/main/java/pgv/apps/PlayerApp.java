package pgv.apps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pgv.apps.controllers.PlayerController;

public class PlayerApp extends Application {

	private PlayerController controller;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		controller = new PlayerController();
		
		Scene scene = new Scene(controller.getRoot(), 600, 600);
		
		primaryStage.setTitle("Buscaminas");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
