package pgv.controller;

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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;

public class Controller implements Initializable {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private DataInputStream is;
	private DataOutputStream os;
	private ChatReceive escuchaChat;
	private ObjectReceive escuchaObj;
	
	private BooleanProperty envio = new SimpleBooleanProperty(false);

	// model
	private PanelBuscaminas2 panel;
	private String host;

	@FXML
	private SplitPane view;

	@FXML
	private TextArea textoArea;

	@FXML
	private TextField textoField;

	@FXML
	private Button liberButton;

	@FXML
	private Button enviarButton;

	@FXML
	private BorderPane buscaminasPane;

	@FXML
	void onEnviarAction(ActionEvent event) {

		try {
			if (!textoField.getText().trim().equals("")) {
				os.writeUTF(textoField.getText());

				textoArea.setText("Tú: " + textoField.getText() + "\n" + textoArea.getText());
			}
			textoField.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void onLiberAction(ActionEvent event) {
	
		envio.set(true);
		try {
			oos.writeObject(panel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//bindeo
		liberButton.disableProperty().bind(envio);
		
		//resto de codigo
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Necesito IP");
		dialog.setHeaderText("¿Y mi IP?");
		dialog.setContentText("Dame la IP:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			System.out.println("Mi IP: " + result.get());
			host = result.get();
		}

		Socket clientSocket = new Socket();
		InetSocketAddress addr = new InetSocketAddress(host, 5555);

		Socket clientSocketObject = new Socket();
		InetSocketAddress addrObject = new InetSocketAddress(host, 5556);
		
		try {
			clientSocket.connect(addr);
			clientSocketObject.connect(addrObject);

			oos = new ObjectOutputStream(clientSocketObject.getOutputStream());
			ois = new ObjectInputStream(clientSocketObject.getInputStream());

			is = new DataInputStream(clientSocket.getInputStream());
			os = new DataOutputStream(clientSocket.getOutputStream());

			escuchaChat = new ChatReceive(is);
			escuchaChat.start();

			escuchaObj = new ObjectReceive(ois);
			escuchaObj.start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Controller() throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/buscaminasView.fxml"));
		loader.setController(this);
		loader.load();

	}

	public SplitPane getRoot() {
		return view;
	}

	public PanelBuscaminas2 getPanel() {
		return panel;
	}

	public void setPanel(PanelBuscaminas2 panel) {
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
						textoArea.setText(">>>> " + texto + "\n" + textoArea.getText());
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

	class ObjectReceive extends Thread {

		private ObjectInputStream lectura;

		public ObjectReceive(ObjectInputStream in) {
			this.lectura = in;
		}

		@Override
		public void run() {
			try {
				System.out.println("Escuchando");

				while (!isInterrupted()) {
					if (lectura.available() > 1) {
						try {
							panel = (PanelBuscaminas2) ois.readObject();
							buscaminasPane.setCenter(panel);
							envio.set(false);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						textoArea.setText("Minas recibidas\n" + textoArea.getText());
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
}
