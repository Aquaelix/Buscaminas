package pgv.apps.controllers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import javafx.util.Pair;
import pgv.apps.logica.Emoji;
import pgv.apps.logica.EmojisList;
import pgv.apps.logica.Tablero;

public class PlayerController implements Initializable {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private DataInputStream is;
	private DataOutputStream os;
	private ChatReceive escuchaChat;
	private ObjectReceive escuchaObj;

	private Pair<String, String> casilla = null;

	private Popup popupEmoji = new Popup();
	private Popup popupAyuda = new Popup();
	private ListView<Emoji> list = new ListView<Emoji>();

	// model
	private Tablero panel;
	private String host, usuario;
	private BooleanProperty envio = new SimpleBooleanProperty(true);

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
	private Button emojiButton;

	@FXML
	private Button enviarButton;

	@FXML
	public static BorderPane buscaminasPane;

	@FXML
	void onEmojiAction(ActionEvent event) {

		List<Emoji> choices = EmojisList.emojisOrdenados;

		ChoiceDialog<Emoji> dialog = new ChoiceDialog<>(EmojisList.emojisOrdenados.get(0), choices);

		dialog.setTitle("¿Qué emoji deseas?");
		dialog.setContentText("Inserto...");
		Optional<Emoji> result = dialog.showAndWait();
		if (result.isPresent()) {
			textoField.setText(textoField.getText() + result.get().getValue());
		}

	}

	@FXML
	void onEnviarAction(ActionEvent event) {

		String texto = textoField.getText();
		if (texto.equals("!limpiar")) {
			textoArea.setText("");
		} else if (texto.equals("!ayuda")) {
			if (!popupAyuda.isShowing())
				popupAyuda.show(view.getScene().getWindow());
		} else if (texto.equals("!emojis")) {
			if (!popupEmoji.isShowing())
				popupEmoji.show(view.getScene().getWindow());
		} else if (texto.equals("!salir")) {
			try {
				os.writeUTF(texto);
				enviarButton.setDisable(true);
				textoArea.setText("Has salido del juego, no podrás hablar ni seguir jugando.\n" + textoArea.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				os.writeUTF(texto);
				textoArea.setText("Tú: " + textoField.getText() + "\n" + textoArea.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		textoField.setText("");
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
			if (!newValue.isEmpty()) {
				if (lado == 5) {
					loginButton.setDisable(
							(!((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 53)) || newValue.trim().isEmpty()));
				} else if (lado == 10) {
					loginButton.setDisable(!(((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 57))
							|| (newValue.charAt(0) == 65) || newValue.trim().isEmpty()));
				} else {
					loginButton.setDisable(!(((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 57))
							|| ((newValue.charAt(0) >= 65) && (newValue.charAt(0) <= 70))
							|| newValue.trim().isEmpty()));
				}
			}
		});

		vertical.textProperty().addListener((observable, oldValue, newValue) -> {
			int lado = panel.getAlto();
			if (!newValue.isEmpty()) {
				if (lado == 5) {
					loginButton.setDisable(
							(!((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 53)) || newValue.trim().isEmpty()));
				} else if (lado == 10) {
					loginButton.setDisable(!(((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 57))
							|| (newValue.charAt(0) == 65) || newValue.trim().isEmpty()));
				} else {
					loginButton.setDisable(!(((newValue.charAt(0) >= 49) && (newValue.charAt(0) <= 57))
							|| ((newValue.charAt(0) >= 65) && (newValue.charAt(0) <= 70))
							|| newValue.trim().isEmpty()));
				}
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

		String hor = casilla.getValue(), ver = casilla.getKey();
		int iHor, iVer;
		if (hor.charAt(0) >= 65)
			iHor = (int) hor.charAt(0) - 55;
		else
			iHor = Integer.valueOf(hor);

		if (ver.charAt(0) >= 65)
			iVer = (int) ver.charAt(0) - 55;
		else
			iVer = Integer.valueOf(ver);

		int resultado = panel.clickCasilla(iHor - 1, iVer - 1);
//		System.out.println("Panelsinbombas");
//		System.out.println(panel);
//		System.out.println("Panelbombas");
//		System.out.println(panel.showAll());
//System.out.println(casilla.getKey() + " "+casilla.getValue());
		if (resultado != -2) {
			if (resultado == -1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Vaya...");
				alert.setHeaderText("Fin del juego");
				alert.setContentText("Has encontrado una mina");

				minasArea.textProperty().unbind();
				minasArea.setText(panel.showBombs());

				panel.setCaput(true);
				alert.showAndWait();
//				Platform.exit();
			} else if (resultado == 0) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("¡Felicidades!");
				alert.setHeaderText(null);
				alert.setContentText("¡Has ganado!");

				minasArea.textProperty().unbind();
				minasArea.setText(panel.showAll());

				panel.setCaput(true);
				alert.showAndWait();
//				Platform.exit();
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

		TextInputDialog dialogJ = new TextInputDialog(System.getProperty("user.name"));

		dialogJ.setTitle("¿Nombre?");
		dialogJ.setContentText("Me reconocerán como... ");
		Optional<String> resultJ = dialogJ.showAndWait();
		if (resultJ.isPresent()) {
			usuario = resultJ.get();
		}

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

			os.writeUTF(usuario);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// bindeo
		escogerButton.disableProperty().bind(envio);
		enviarButton.disableProperty().bind(textoField.textProperty().isEmpty());

		minasArea.textProperty().bind(escuchaObj.messageProperty());

		list.getItems().addAll(EmojisList.emojisOrdenados);

		EventHandler<MouseEvent> evento;

		evento = new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				Emoji emoji = list.getSelectionModel().getSelectedItem();
				textoField.setText(textoField.getText() + emoji.getValue());
			}
		};
		list.setOnMouseClicked(evento);

		popupEmoji.getContent().add(list);
		popupEmoji.setAutoHide(true);

		TextArea textoAyuda = new TextArea("Lista de comandos:\n!hola\n" + "Saluda usando el nombre del usuario\n"
				+ "!adios\n" + "Despide usando el nombre del usuario\n" + "!ayuda\n"
				+ "Muestra esta ayuda de todos los comandos\n" + "!estado\n"
				+ "Muestra \"pues aquí, ayudando que ustedes jueguen, comunicando mensajes y tableros\"\n" + "!salir\n"
				+ "Permite la salida del juego al usuario que lo solicitó\n" + "!emojis\n"
				+ "Muestra una lista completa de todos los emojis que hay para usar (en formato popup)\n" + "!limpiar\n"
				+ "Permite limpiar el textarea del chat\r\n" + "!usuarios\n"
				+ "Permite visualizar los nombres de las personas que están jugando\n" + "!\"(nombre de un usuario)\"\n"
				+ "Permite mandar todo el texto siguiente al usuario como si de un chat privado se tratase. Ej: !\"Juan José\" Hola Juan, preparado para perder? ;D\n"
				+ "No funciona con caracteres que no sean alfanumericos. Se permiten las tildes");
		textoAyuda.setEditable(false);
		textoAyuda.setWrapText(true);

		popupAyuda.getContent().add(textoAyuda);
		popupAyuda.setAutoHide(true);

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Y si...");
		alert.setContentText("Prueba a usar \"!ayuda\" en el chat, a ver qué pasa");
		alert.showAndWait();

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
						textoArea.setText(texto + "\n" + textoArea.getText());

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
				while (!isCancelled()) {

					System.out.println("Hablo desde hiloObjeto, creando Tablero");
					panel = (Tablero) ois.readObject();

					if (panel.isCaput()) {

						updateMessage("Fin del juego, revisa con tu compañero los resultados.");
						this.cancel();
						break;
					} else {
//System.out.println(panel);
						envio.set(false);

						updateMessage(panel.toString());
					}
				}
				lectura.close();
			} catch (EOFException e) {

			} catch (SocketException e) {
				System.out.println(
						"Error con los sockets, probablemente se dedsconectó primero el servidor:\n" + e.getMessage());
			}
			return null;
		}
	}
}
