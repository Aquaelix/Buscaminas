package pgv.apps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pgv.apps.controllers.ServerController;

public class ServerApp extends Application {

	private ServerController controller;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		controller = new ServerController();
		
		Scene scene = new Scene(controller.getRoot(), 300, 500);
		
		primaryStage.setTitle("Buscaminas");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
