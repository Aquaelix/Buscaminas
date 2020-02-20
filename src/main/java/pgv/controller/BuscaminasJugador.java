package pgv.controller;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BuscaminasJugador {
	@SuppressWarnings("resource")
	public static void main(String[] arg) throws IOException, ClassNotFoundException {

		Scanner in = new Scanner(System.in);
		String Host = "localhost";
		int Puerto = 5555;
		System.out.println("IP?");
		Host = in.nextLine();

		System.out.println("PROGRAMA CLIENTE INICIADO....");

		Socket clientSocket = new Socket();
		InetSocketAddress addr = new InetSocketAddress(Host, Puerto);
		clientSocket.connect(addr);

		ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

		Frame f = new Frame();
		f.setSize(600, 400);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		while (true) {
			try {
				PanelBuscaminas panel2 = (PanelBuscaminas) ois.readObject();

				Button c = new Button("Me libre de esta ;D");
				c.setBounds(50, 100, 60, 30);
				c.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							System.out.println("Escribo ");

							oos.writeObject(panel2);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});

				FlowLayout layout2 = new FlowLayout();
				f.removeAll();
				f.setLayout(layout2);
				f.add(panel2);
				f.add(c);
				f.show();
			} catch (Exception e) {
				oos.writeObject(new Exception("Tas muerto"));
				 ois.close();
				 oos.close();
				 clientSocket.close();
				f.dispose();
			}
		}
	}
}
