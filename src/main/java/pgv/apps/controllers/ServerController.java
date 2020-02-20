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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class ServerController implements Initializable {

	// model
	private Buscaminas panel;

	public static ArrayList<Jugador> clientesChat = new ArrayList<Jugador>();
	public static ArrayList<Jugador> clientesMinas = new ArrayList<Jugador>();
	
	private ServerSocket servidorChat;
	private ServerSocket servidorObject;

	@FXML
	private BorderPane view;

	@FXML
	private TextArea textoArea;

	@FXML
	private Button byeButton;

	@FXML
	private Button byeByeButton;

	@FXML
	void onByeAction(ActionEvent event) {

	}

	@FXML
	void onByeByeAction(ActionEvent event) {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		int numPuertoChat = 5555, numPuertoObjeto = 5556, jugadores = 2;

		List<String> choices = new ArrayList<String>();
		choices.add("Fácil");
		choices.add("Media");
		choices.add("Difícil");
		choices.add("100% imposible");

		ChoiceDialog<String> dialog = new ChoiceDialog<>("Fácil", choices);

		dialog.setTitle("¿Qué dificultad desean?");
		dialog.setContentText("Escoge el reto:");
		String dificultad = null;
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			dificultad = result.get();
			if (dificultad.equals("Difícil")) {
				panel = new Buscaminas(15, 15, 110);
			} else if (dificultad.equals("Media")) {
				panel = new Buscaminas(10, 10, 40);
			} else if (dificultad.equals("Fácil")) {
				panel = new Buscaminas(5, 5, 5);
			} else {
				panel = new Buscaminas(15, 15, 225);
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
				System.out.println("Jugador aceptado ;D "+jugadores);
			} while (jugadores != 0);


			ServerController.clientesMinas.get(0).outObjeto.writeObject(panel.getMinas());
			System.out.println("ENVIO AL 1 Minas");

			ServerController.clientesMinas.get(0).outObjeto.writeObject(panel.getMaxMinas());
			System.out.println("ENVIO AL 1 int minasMaximas");
			
//			ServerController.clientesMinas.get(0).outObjeto.writeObject(panel.getCasilla3());
//			System.out.println("ENVIO AL 1 Casilla3");
			
			String[][] texto;
			boolean[][] booleanButton;
			if (dificultad.equals("Media")) {
				texto = new String[10][10];
				booleanButton = new boolean[10][10];
			} else if (dificultad.equals("Fácil")) {
				texto = new String[5][5];
				System.out.println("Soy texto: "+texto.length);
				System.out.println("Soy texto[0]: "+texto[0].length);
				booleanButton = new boolean[5][5];
			} else {
				texto = new String[15][15];
				booleanButton = new boolean[15][15];
			}
			
			for(int i=0; i<texto.length;i++) {
				for(int j=0; j<texto[i].length; j++) {
					texto[i][j]=
							panel.getCasilla3()[i][j].getText();
				}
			}
			
			for(int i=0; i<texto.length;i++) {
				for(int j=0; j<texto[i].length; j++) {
					booleanButton[i][j]= panel.getCasilla3()[i][j].isDescubierta();
				}
			}
			
			ServerController.clientesMinas.get(0).outObjeto.writeObject(texto);
			System.out.println("ENVIO AL 1 textoMinas");
			
			ServerController.clientesMinas.get(0).outObjeto.writeObject(booleanButton);
			System.out.println("ENVIO AL 1 booleanButton");
			
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
					if (cliente.inObjeto.available() > 1) {
						try {
							int jugador = ServerController.clientesMinas.indexOf(this.cliente) + 1;

							if (jugador >= ServerController.clientesMinas.size())
								jugador = 0;
							Buscaminas buscaminas = (Buscaminas) cliente.inObjeto.readObject();
							ServerController.clientesMinas.get(jugador).outObjeto.writeObject(buscaminas);
							
System.out.println("Hablo desde hiloMina, mando Buscaminas");

						} catch (Exception e) {
							System.out.println("Vaya... un error: " + e.getMessage());
							interrumpir();
						}
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
				System.out.println("Escuchando chat");

				while (!isInterrupted()) {
					if (cliente.dis.available() > 1) {
						String texto = cliente.dis.readUTF();
						System.out.println(texto);

						for (int i = 0; i < ServerController.clientesChat.size(); i++) {
							if (!ServerController.clientesChat.get(i).equals(this.cliente))
								ServerController.clientesChat.get(i).dos.writeUTF(texto);
						}
						
System.out.println("Hablo desde hiloChat, mando Buscaminas");
						
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
			interrupt();
		}

	}
}
