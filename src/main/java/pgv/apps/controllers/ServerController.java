package pgv.apps.controllers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import pgv.apps.logica.Tablero;

public class ServerController implements Initializable {

	// model
	private Tablero panel;
	private Popup popup = new Popup();

	public static ArrayList<Jugador> clientesChat = new ArrayList<Jugador>();
	public static ArrayList<Jugador> clientesMinas = new ArrayList<Jugador>();

	private ServerSocket servidorChat;
	private ServerSocket servidorObject;

	@FXML
	private BorderPane view;

	@FXML
	private TextArea textoArea;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		popup.setAutoHide(true);
		popup.getContent().add(new Label("Solo carácteres numéricos."));

		int numPuertoChat = 5555, numPuertoObjeto = 5556;

		TextInputDialog dialogJ = new TextInputDialog("2");

		dialogJ.setTitle("¿Cuántos jugadores son?");
		dialogJ.setContentText("Número de jugadores:");
		int jugadores = 0;
		Optional<String> resultJ = dialogJ.showAndWait();
		if (resultJ.isPresent()) {
			jugadores = Integer.valueOf(resultJ.get());

		}

		List<String> choices = new ArrayList<String>();
		choices.add("Fácil");
		choices.add("Media");
		choices.add("Difícil");
		choices.add("Personalizado");
		choices.add("99% imposible");

		ChoiceDialog<String> dialog = new ChoiceDialog<>("Fácil", choices);

		dialog.setTitle("¿Qué dificultad desean?");
		dialog.setContentText("Escoge el reto:");
		String dificultad = null;
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			dificultad = result.get();
			if (dificultad.equals("Difícil")) {
				panel = new Tablero(15, 15, 110);
			} else if (dificultad.equals("Media")) {
				panel = new Tablero(10, 10, 40);
			} else if (dificultad.equals("Fácil")) {
				panel = new Tablero(5, 5, 5);
			} else if (dificultad.equals("99% imposible")) {
				panel = new Tablero(15, 15, 224);
			} else {
				Dialog<String> dialog2 = new Dialog<>();
				dialog2.setTitle("Establezca su dificultad");
				dialog2.setHeaderText("Tenga en cuenta que hay valores mínimos: ");

				ButtonType insertButton = new ButtonType("Seleccionar", ButtonData.OK_DONE);
				dialog2.getDialogPane().getButtonTypes().addAll(insertButton, ButtonType.CANCEL);

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 150, 10, 10));

				TextField ancho = new TextField();
				TextField alto = new TextField();
				TextField minas = new TextField();

				grid.add(new Label("Ancho: "), 0, 0);
				grid.add(ancho, 1, 0);
				grid.add(new Label("Alto: "), 0, 1);
				grid.add(alto, 1, 1);
				grid.add(new Label("Minas"), 0, 2);
				grid.add(minas, 1, 2);

				dialog2.getDialogPane().setContent(grid);

				Node loginButton = dialog2.getDialogPane().lookupButton(insertButton);
				loginButton.setDisable(true);

				ancho.textProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue.matches("[0-9]*")) {
						loginButton.setDisable(Integer.valueOf(newValue) >= 1 && Integer.valueOf(newValue) <= 15);
					} else {
						popup.show(view.getScene().getWindow());
					}
				});

				alto.textProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue.matches("[0-9]*")) {
						loginButton.setDisable(Integer.valueOf(newValue) >= 1 && Integer.valueOf(newValue) <= 15);
					} else {
						popup.show(view.getScene().getWindow());
					}
				});

				minas.textProperty().addListener((o, ov, nv) -> {
					if (nv.matches("[0-9]*")) {
						if (!alto.getText().isEmpty() && !ancho.getText().isEmpty()) {
							int numMinas = Integer.valueOf(alto.getText()) * Integer.valueOf(ancho.getText());
System.out.println(numMinas);
							loginButton.setDisable(Integer.valueOf(nv) > numMinas*0.75 || Integer.valueOf(nv) < numMinas*0.25);
						}
					} else {
						popup.show(view.getScene().getWindow());
					}
				});
				
				dialog2.setResultConverter(dialogButton -> {
					if (dialogButton == insertButton) {
						return ancho.getText() + ":" + alto.getText() + ":" + minas.getText();
					}
					return null;
				});

				Optional<String> result2 = dialog2.showAndWait();

				result2.ifPresent(tablero -> {
					String[] datos = tablero.split(":");
					panel = new Tablero(Integer.valueOf(datos[0]), Integer.valueOf(datos[1]),
							Integer.valueOf(datos[2]));
				});
			}
		}

		try {
			servidorChat = new ServerSocket(numPuertoChat);
			servidorObject = new ServerSocket(numPuertoObjeto);

			do {
				Socket socketChat = servidorChat.accept();
				Socket socketObjeto = servidorObject.accept();

				clientesChat.add(new Jugador(socketChat, true));
				clientesMinas.add(new Jugador(socketObjeto, false));

				jugadores--;
//				System.out.println("Jugador aceptado ;D "+jugadores);
			} while (jugadores != 0);

			ServerController.clientesMinas.get(0).outObjeto.writeObject(panel);
//			System.out.println("ENVIO AL 1 tablero");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public ServerController() throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ServerView.fxml"));
		loader.setController(this);
		loader.load();

	}

	public BorderPane getRoot() {
		return view;
	}

	class Jugador {

		Socket socketCliente;
		HiloMina hiloObject;
		HiloChat hiloChat;
		ObjectOutputStream outObjeto;
		ObjectInputStream inObjeto;

		DataInputStream dis;
		DataOutputStream dos;

		String nombre;

		public Jugador(Socket cliente, boolean isChat) {
			this.socketCliente = cliente;
			try {

				if (!isChat) {
					outObjeto = new ObjectOutputStream(this.socketCliente.getOutputStream());
					inObjeto = new ObjectInputStream(this.socketCliente.getInputStream());
				} else {
					dis = new DataInputStream(this.socketCliente.getInputStream());
					dos = new DataOutputStream(this.socketCliente.getOutputStream());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (!isChat) {
				this.hiloObject = new HiloMina(this);
				hiloObject.start();
			} else {
				this.hiloChat = new HiloChat(this);
				hiloChat.start();
			}
		}

	}

	class HiloMina extends Thread {

		Jugador cliente;

		public HiloMina(Jugador cliente) {
			this.cliente = cliente;
		}

		@Override
		public void run() {
			// leer del socket
			// recorrer la lista escribiendo por output para el resto de los clientes
			try {
				System.out.println("Escuchando minas");

				while (!isInterrupted()) {
					try {
						Tablero buscaminas = (Tablero) cliente.inObjeto.readObject();

						int jugador = ServerController.clientesMinas.indexOf(this.cliente) + 1;

						if (jugador >= ServerController.clientesMinas.size())
							jugador = 0;
						ServerController.clientesMinas.get(jugador).outObjeto.writeObject(buscaminas);

						if (buscaminas.isCaput())
							this.interrumpir();

//System.out.println("Hablo desde hiloMina, mando Buscaminas");

					} catch (Exception e) {
						System.out.println("Vaya... un error: " + e.getMessage());
						interrumpir();
					}
				}

				System.out.println("Cerrando el socket de escucha");
				System.out.println("Terminado");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void interrumpir() {
			try {
				cliente.inObjeto.close();
				cliente.outObjeto.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ServerController.clientesMinas.remove(cliente);
			interrupt();
		}

	}

	class HiloChat extends Thread {

		Jugador cliente;

		public HiloChat(Jugador cliente) {
			this.cliente = cliente;
		}

		@Override
		public void run() {
			// leer del socket
			// recorrer la lista escribiendo por output para el resto de los clientes
			try {
				boolean sinNombre = true;
				while (!isInterrupted()) {
					if (cliente.dis.available() > 1) {
						String texto = cliente.dis.readUTF();

						if (sinNombre) {
							cliente.nombre = texto;
							sinNombre = false;
							textoArea.setText("Jugador: " + cliente.nombre + " conectado.\n" + textoArea.getText());
						} else {
							System.out.println(texto);
							if (texto.equals("!hola")) {
								this.cliente.dos.writeUTF("¿Qué pasó " + this.cliente.nombre + "?");
							} else if (texto.equals("!adios")) {
								this.cliente.dos.writeUTF("Hasta la vista " + this.cliente.nombre);
							} else if (texto.equals("!estado")) {
								this.cliente.dos.writeUTF(
										"Pues aquí, ayudando a que jueguen, comunicando mensajes y tableros.");
							} else if (texto.equals("!salir")) {
								interrumpir();
							} else if (texto.matches("(!\\\"([À-ÿ\\w\\s])*\\\")([À-ÿ\\w\\s.,:-])*")) {
								System.out.println(texto);
								String envio = "", receptor = "";
								int comillas = 0;
								Jugador privado = null;
								for (int i = 0; i < texto.length(); i++) {
									if (texto.charAt(i) == '\"') {
										comillas++;
									}
									if (comillas == 1) {
										receptor = texto.substring(i + 1);
										break;
									}
								}

								comillas = 0;
								for (int i = 0; i < receptor.length(); i++) {
									if (receptor.charAt(i) == '\"') {
										comillas++;
									}
									if (comillas == 1) {
										receptor = receptor.substring(0, i);
										break;
									}

								}

								System.out.println("Receptor: " + receptor);
								for (int i = 0; i < clientesChat.size(); i++) {
									if (clientesChat.get(i).nombre.equals(receptor)) {
										privado = clientesChat.get(i);
										break;
									}
								}

								comillas = 0;
								if (privado != null) {
									for (int i = 0; i < texto.length(); i++) {
										if (texto.charAt(i) == '\"') {
											comillas++;
										}
										if (comillas == 2) {
											envio = texto.substring(i + 2);
											break;
										}
									}
									privado.dos.writeUTF(this.cliente.nombre + "(P): " + envio);
								} else {
									textoArea.setText("No existe el jugador especificado\n" + textoArea.getText());
								}

								System.out.println("Envio: " + envio);
							} else {
								for (int i = 0; i < ServerController.clientesChat.size(); i++) {
									if (!ServerController.clientesChat.get(i).equals(this.cliente))
										ServerController.clientesChat.get(i).dos
												.writeUTF(this.cliente.nombre + ": " + texto);
								}

								textoArea.setText(texto + "\n" + textoArea.getText());
							}
						}
					}

				}

				System.out.println("Cerrando el socket de escucha");
				cliente.dis.close();
				System.out.println("Terminado");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void interrumpir() {
			ServerController.clientesChat.remove(cliente);
			ServerController.clientesMinas.remove(cliente);
			interrupt();
		}

	}
}
