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

public class PlayerController implements Initializable {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private DataInputStream is;
	private DataOutputStream os;
	private ChatReceive escuchaChat;
	private ObjectReceive escuchaObj;

	private BooleanProperty envio = new SimpleBooleanProperty(false);

	// model
	private Buscaminas panel;
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
	public static BorderPane buscaminasPane;

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

		// bindeo
		liberButton.disableProperty().bind(envio);

//		System.out.println("Panel:\n"+panel.toString());
//		buscaminasPane.setCenter(panel);

		// resto de codigo
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
			System.out.println("CONECTADO");
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

	public PlayerController() throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlayerView.fxml"));
		loader.setController(this);
		loader.load();

	}

	public SplitPane getRoot() {
		return view;
	}

	public Buscaminas getPanel() {
		return panel;
	}

	public void setPanel(Buscaminas panel) {
		this.panel = panel;
	}
	public static void seteoPanel(Buscaminas pane) {
		buscaminasPane.setCenter(pane);
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

						System.out.println("Hablo desde hiloChat, leido");

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
				int i = 0, maxMinas = 0;
				boolean[][] minas = null;
				boolean[][] botones = null;
				String[][] texto = null;

				while (!isInterrupted()) {
//					if (lectura.available() > 1) {
						try {
							System.out.println("Hablo desde hiloObjeto, recibido");
							if (i == 0) {

								minas = (boolean[][]) ois.readObject();

								System.out.println("Lectura 1");
								i++;
							} else if (i == 1) {

								maxMinas = (int) ois.readObject();

								System.out.println("Lectura 2");
								i++;
							} else if (i == 3) {

								botones = (boolean[][]) ois.readObject();

								System.out.println("Lectura 4");
								i++;
							} else if (i == 2) {

								texto = (String[][]) ois.readObject();

								System.out.println("Lectura 3");
								i++;
								
							}else if(i==4) {
								Buscaminas panelito = new Buscaminas();
								panelito.setMinas(minas);
								panelito.setMaxMinas(maxMinas);
								
								Casilla3[][] casillas = new Casilla3[botones.length][botones[0].length];
								
								for (int l = 0; l < casillas.length; l++) {
									for (int m = 0; m < casillas.length; m++) {
										System.out.print("Nueva casillita ");
										if(texto[l][m].equals(""))
											texto[l][m]= " ";
										System.out.println("Mi string "+texto[l][m]+ " boolean "+botones[l][m]);
										casillas[l][m]= new Casilla3(texto[l][m], botones[l][m]);
									}

									System.out.println("Nueva linea de casillitas");
								}
								
								panelito.setCasilla3(casillas);
	System.out.println(panelito);
								seteoPanel(panelito);
								envio.set(false);
								i = 0;
							}

							System.out.println("Hablo desde hiloObjeto, recibido Todo");

						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						textoArea.setText("Minas recibidas\n" + textoArea.getText());
					}

//				}

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
