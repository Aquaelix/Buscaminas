package pgv.controller;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class BuscaminasServidor {

	public static ArrayList<Cliente> clientesChat = new ArrayList<Cliente>();
	public static ArrayList<Cliente> clientesMinas = new ArrayList<Cliente>();

	@SuppressWarnings("resource")
	public static void main(String[] arg) throws IOException, ClassNotFoundException {
		int numPuertoChat = 5555, numPuertoObjeto = 5556, jugadores = 2;
		char dificultad;

		Scanner in = new Scanner(System.in);

		ServerSocket servidorChat = new ServerSocket(numPuertoChat);
		ServerSocket servidorObject = new ServerSocket(numPuertoObjeto);

		do {
			System.out.println("¿Dificultad de la partida? ¿Difícil, medio, fácil? (D/M/F)");
			dificultad = in.nextLine().charAt(0);
		} while (dificultad != 'D' && dificultad != 'M' && dificultad != 'F');

		do {
			System.out.println("Esperando jugadores...");
			try {
				Socket socketChat = servidorChat.accept();
				Socket socketObjeto = servidorObject.accept();

				clientesChat.add(new Cliente(socketChat, true));
				clientesMinas.add(new Cliente(socketObjeto, false));

			} catch (UnknownHostException ex) {

				System.out.println("Server not found: " + ex.getMessage());

			} catch (IOException ex) {

				System.out.println("I/O error: " + ex.getMessage());
			}
			jugadores--;
		} while (jugadores != 0);

		PanelBuscaminas2 panel;
		if (dificultad == 'D') {
			panel = new PanelBuscaminas2(15, 15, 110);
		} else if (dificultad == 'M') {
			panel = new PanelBuscaminas2(10, 10, 40);
		} else {
			panel = new PanelBuscaminas2(5, 5, 5);
		}

		System.out.println("ENVIO AL 1");
		BuscaminasServidor.clientesMinas.get(0).outObjeto.writeObject(panel);

//		in.close();
//		servidorChat.close();
//		servidorObject.close();
	}
}

class Cliente {

	Socket socketCliente;
	HiloObjeto hiloObject;
	HiloChat hiloChat;
	ObjectOutputStream outObjeto;
	ObjectInputStream inObjeto;

	DataInputStream dis;
	DataOutputStream dos;

	public Cliente(Socket cliente, boolean isChat) {
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
			this.hiloObject = new HiloObjeto(this);
			hiloObject.start();
		} else {
			this.hiloChat = new HiloChat(this);
			hiloChat.start();
		}
	}

}

class HiloObjeto extends Thread {

	Cliente cliente;

	public HiloObjeto(Cliente cliente) {
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
						int jugador = BuscaminasServidor.clientesMinas.indexOf(this.cliente) + 1;

						if (jugador >= BuscaminasServidor.clientesMinas.size())
							jugador = 0;
						PanelBuscaminas2 buscaminas = (PanelBuscaminas2) cliente.inObjeto.readObject();
						BuscaminasServidor.clientesMinas.get(jugador).outObjeto.writeObject(buscaminas);

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
		BuscaminasServidor.clientesMinas.remove(cliente);
		interrupt();
	}

}

class HiloChat extends Thread {

	Cliente cliente;

	public HiloChat(Cliente cliente) {
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
//					String texto = new String(cliente.dis.readAllBytes());
					String texto = cliente.dis.readUTF();
					if (!texto.equals("salir!")) {
						System.out.println(texto);

						for (int i = 0; i < BuscaminasServidor.clientesChat.size(); i++) {
							if (!BuscaminasServidor.clientesChat.get(i).equals(this.cliente))
								BuscaminasServidor.clientesChat.get(i).dos.writeUTF(texto);
						}
					} else
						interrumpir();
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
		BuscaminasServidor.clientesChat.remove(cliente);
		interrupt();
	}

}
