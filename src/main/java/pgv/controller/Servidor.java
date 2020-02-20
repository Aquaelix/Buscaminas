package pgv.controller;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import pgv.apps.controllers.Buscaminas;

public class Servidor {

	public static ArrayList<Cliente2> clientesChat = new ArrayList<Cliente2>();
	public static ArrayList<Cliente2> clientesMinas = new ArrayList<Cliente2>();

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

				clientesChat.add(new Cliente2(socketChat, true));
				clientesMinas.add(new Cliente2(socketObjeto, false));

			} catch (UnknownHostException ex) {

				System.out.println("Server not found: " + ex.getMessage());

			} catch (IOException ex) {

				System.out.println("I/O error: " + ex.getMessage());
			}
			jugadores--;
		} while (jugadores != 0);

		Buscaminas panel;
		if (dificultad == 'D') {
			panel = new Buscaminas(15, 15, 110);
		} else if (dificultad == 'M') {
			panel = new Buscaminas(10, 10, 40);
		} else {
			panel = new Buscaminas(5, 5, 5);
		}

		System.out.println("ENVIO AL 1");
		Servidor.clientesMinas.get(0).outObjeto.writeObject(panel);

//		in.close();
//		servidorChat.close();
//		servidorObject.close();
	}
}

class Cliente2 {

	Socket socketCliente;
	HiloObjeto2 hiloObject;
	HiloChat2 hiloChat;
	ObjectOutputStream outObjeto;
	ObjectInputStream inObjeto;

	DataInputStream dis;
	DataOutputStream dos;

	public Cliente2(Socket cliente, boolean isChat) {
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
			this.hiloObject = new HiloObjeto2(this);
			hiloObject.start();
		} else {
			this.hiloChat = new HiloChat2(this);
			hiloChat.start();
		}
	}

}

class HiloObjeto2 extends Thread {

	Cliente2 cliente;

	public HiloObjeto2(Cliente2 cliente) {
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
						int jugador = Servidor.clientesMinas.indexOf(this.cliente) + 1;

						if (jugador >= Servidor.clientesMinas.size())
							jugador = 0;
						Buscaminas buscaminas = (Buscaminas) cliente.inObjeto.readObject();
						Servidor.clientesMinas.get(jugador).outObjeto.writeObject(buscaminas);

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
		Servidor.clientesMinas.remove(cliente);
		interrupt();
	}

}

class HiloChat2 extends Thread {

	Cliente2 cliente;

	public HiloChat2(Cliente2 cliente) {
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

						for (int i = 0; i < Servidor.clientesChat.size(); i++) {
							if (!Servidor.clientesChat.get(i).equals(this.cliente))
								Servidor.clientesChat.get(i).dos.writeUTF(texto);
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
		Servidor.clientesChat.remove(cliente);
		interrupt();
	}

}
