package pgv.apps.controllers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class PlayerController implements Initializable {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private DataInputStream is;
	private DataOutputStream os;
	private ChatReceive escuchaChat;
	private ObjectReceive escuchaObj;

	private BooleanProperty envio = new SimpleBooleanProperty(false);

	private Pair<String, String> casilla = null;

	// model
	private Tablero panel;
	private String host;

	@FXML
	private SplitPane view;

	@FXML
	private TextArea textoArea;

	@FXML
	private TextField textoField;

	@FXML
	private TextArea minasArea;

	@FXML
	private Button escogerButton;

	@FXML
	private Button enviarButton;

	@FXML
	public static BorderPane buscaminasPane;

	@FXML
	void onEnviarAction(ActionEvent event) {

		try {
			os.writeUTF(textoField.getText());

			textoArea.setText("Tú: " + textoField.getText() + "\n" + textoArea.getText());

			textoField.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void onSelectAction(ActionEvent event) {

		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("¿Coordenadas?");
		dialog.setHeaderText("Necesito la casilla: ");

		ButtonType insertButton = new ButtonType("Seleccionar", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(insertButton, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField horizontal = new TextField();
		TextField vertical = new TextField();

		grid.add(new Label("Horizontal: "), 0, 0);
		grid.add(horizontal, 1, 0);
		grid.add(new Label("Vertical: "), 0, 1);
		grid.add(vertical, 1, 1);

		Node loginButton = dialog.getDialogPane().lookupButton(insertButton);
		loginButton.setDisable(true);

		horizontal.textProperty().addListener((observable, oldValue, newValue) -> {
			int lado = panel.getAlto();
			if (lado == 5) {
				loginButton.setDisable(
						(!((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 53)) || newValue.trim().isEmpty()));
			} else if (lado == 10) {
				loginButton.setDisable(!(((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 57))
						|| (newValue.charAt(0) == 65) || newValue.trim().isEmpty()));
			} else {
				loginButton.setDisable(!(((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 57))
						|| ((newValue.charAt(0) >= 65) && (newValue.charAt(0) <= 70)) || newValue.trim().isEmpty()));
			}
		});

		vertical.textProperty().addListener((observable, oldValue, newValue) -> {
			int lado = panel.getAlto();
			if (lado == 5) {
				loginButton.setDisable(
						(!((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 53)) || newValue.trim().isEmpty()));
			} else if (lado == 10) {
				loginButton.setDisable(!(((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 57))
						|| (newValue.charAt(0) == 65) || newValue.trim().isEmpty()));
			} else {
				loginButton.setDisable(!(((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 57))
						|| ((newValue.charAt(0) >= 65) && (newValue.charAt(0) <= 70)) || newValue.trim().isEmpty()));
			}
		});

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == insertButton) {
				return new Pair<>(horizontal.getText(), vertical.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
			System.out
					.println("Horizontal= " + usernamePassword.getKey() + ", Vertical= " + usernamePassword.getValue());
			casilla = result.get();
		});

		int resultado = panel.clickCasilla(Integer.valueOf(casilla.getKey()) - 1,
				Integer.valueOf(casilla.getValue()) - 1);
System.out.println(casilla.getKey() + " "+casilla.getValue());
		if (resultado != -2) {
			if (resultado == -1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Vaya...");
				alert.setHeaderText("Fin del juego");
				alert.setContentText("Has encontrado una mina");

				minasArea.textProperty().unbind();
				minasArea.setText(panel.showBombs());

				alert.showAndWait();
				Platform.exit();
			} else if (resultado == 0) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("¡Felicidades!");
				alert.setHeaderText(null);
				alert.setContentText("¡Has ganado!");

				minasArea.textProperty().unbind();
				minasArea.setText(panel.showAll());

				alert.showAndWait();
				Platform.exit();
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("¡Buena!");
				alert.setHeaderText("Sabia elección");
				alert.setContentText("No hay bomba, toca esperar a tu compañero");
				alert.showAndWait();
			}
		}

		envio.set(true);
		try {
			oos.writeObject(panel);
System.out.println("envio");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// resto de codigo
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Necesito IP");
		dialog.setHeaderText("¿Y mi IP?");
		dialog.setContentText("Dame la IP:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
//System.out.println("Mi IP: " + result.get());
			host = result.get();
		}

		Socket clientSocket = new Socket();
		InetSocketAddress addr = new InetSocketAddress(host, 5555);

		Socket clientSocketObject = new Socket();
		InetSocketAddress addrObject = new InetSocketAddress(host, 5556);

		try {
			clientSocket.connect(addr);
			clientSocketObject.connect(addrObject);
//System.out.println("CONECTADO");
			oos = new ObjectOutputStream(clientSocketObject.getOutputStream());
			ois = new ObjectInputStream(clientSocketObject.getInputStream());

			is = new DataInputStream(clientSocket.getInputStream());
			os = new DataOutputStream(clientSocket.getOutputStream());

			escuchaChat = new ChatReceive(is);
			escuchaChat.start();

			escuchaObj = new ObjectReceive(ois);
			new Thread(escuchaObj).start();

			// bindeo
			escogerButton.disableProperty().bind(envio);
			enviarButton.disableProperty().bind(textoField.textProperty().isEmpty());

			minasArea.textProperty().bind(escuchaObj.messageProperty());
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public PlayerController() throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlayerView.fxml"));
		loader.setController(this);
		loader.load();

	}

	public SplitPane getRoot() {
		return view;
	}

	public Tablero getPanel() {
		return panel;
	}

	public void setPanel(Tablero panel) {
		this.panel = panel;
	}

	class ChatReceive extends Thread {

		private DataInputStream lectura;

		public ChatReceive(DataInputStream in) {
			this.lectura = in;
		}

		@Override
		public void run() {
			try {
				System.out.println("Escuchando");

				while (!isInterrupted()) {
					if (lectura.available() > 1) {
						String texto = lectura.readUTF();
						textoArea.setText(">> " + texto + "\n" + textoArea.getText());

//System.out.println("Hablo desde hiloChat, leido");

					}

				}

				lectura.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void interrumpir() {
			interrupt();
		}
	}

	class ObjectReceive extends Task<Void> {

		private ObjectInputStream lectura;

		public ObjectReceive(ObjectInputStream in) {
			this.lectura = in;
		}

		@Override
		protected Void call() throws Exception {
			try {
				System.out.println("Escuchando");
				while(!isCancelled()) {

				System.out.println("Hablo desde hiloObjeto, creando Tablero");
				panel = (Tablero) ois.readObject();

System.out.println(panel);
				envio.set(false);
								
				updateMessage(panel.toString());
				}
				lectura.close();
			} catch (Exception e) {
				e.printStackTrace();
			}			return null;
		}
	}
}
