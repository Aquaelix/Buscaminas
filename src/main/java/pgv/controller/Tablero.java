package pgv.controller;

import java.io.Serializable;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

public class Tablero implements Serializable {

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;

	public static String[][] casillas;

	public static boolean[][] descubiertas;

	public static int maxMinas;

	public Tablero(int alto, int ancho, int numMinas) {
		super();
		Tablero.maxMinas = numMinas;
		Tablero.casillas = new String[alto][ancho];
		Tablero.descubiertas = new boolean[alto][ancho];

		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {
				descubiertas[i][j] = false;
				casillas[i][j] = "";
			}
		}

		// colocamos minas
		int x, y;
		for (int n = 0; n < maxMinas; n++) {
			do {
				x = (int) (Math.random() * alto);
				y = (int) (Math.random() * ancho);
			} while (descubiertas[y][x]);
			descubiertas[y][x] = true;
		}

	}

	public boolean[][] getDescubiertas() {
		return descubiertas;
	}

	public void setDescubiertas(boolean[][] descubiertas) {
		Tablero.descubiertas = descubiertas;
	}

	public int getMaxMinas() {
		return maxMinas;
	}

	public void setMaxMinas(int maxMinas) {
		Tablero.maxMinas = maxMinas;
	}

	public Tablero(String[][] tablero) {
		Tablero.casillas = tablero;
	}

	public String[][] getCasillas() {
		return casillas;
	}

	public void setCasillas(String[][] casillas) {
		Tablero.casillas = casillas;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static GridPane generatePane() {
		GridPane panel = new GridPane();
		for (int i = 0; i < casillas.length; i++) {
			for (int j = 0; j < casillas[i].length; j++) {

				Button boton = new Button(casillas[i][j]);

				if (!descubiertas[i][j]) {
					boton.setStyle("-fx-background-color: rgb(200,200,200)");
				} else if (!casillas[i][j].equals("*")) {
					boton.setStyle("-fx-background-color: rgb(170,255,255)");
				} else {
					boton.setStyle("-fx-background-color: rgb(255,50,100)");
				}
				boton.setOnAction(new DescubrirAction(i, j, casillas.length, casillas[0].length));
				panel.add(boton, i, j);
			}
		}
		return panel;
	}

	/*
	 * private static void onDescubrirAction() { if (!pb.tablero[f][c].descubierta)
	 * { pb.tablero[f][c].descubierta = true;
	 * pb.tablero[f][c].setStyle("-fx-background-color: rgb(170,255,255)"); if
	 * (pb.minas[f][c]) { pb.tablero[f][c].setText("*"); for (int x = 0; x < alto;
	 * x++) for (int y = 0; y < ancho; y++) if (pb.minas[x][y]) {
	 * pb.tablero[x][y].setStyle("-fx-background-color: rgb(255,50,100)");
	 * pb.tablero[x][y].setText("*"); } //
	 * JOptionPane.showMessageDialog(pb.getParent(), "Lo siento, has perdido");
	 * 
	 * System.out.println("Panel:\n"+escribePanel());
	 * 
	 * Alert alert = new Alert(AlertType.ERROR); alert.setTitle("FIN");
	 * alert.setContentText("Has pisado una mina, fin :c"); alert.showAndWait();
	 * System.exit(0); } else { int numMinas = CuentaMinas(f, c); switch (numMinas)
	 * { case 0: AutoDescubrir(f, c); break; default:
	 * pb.tablero[f][c].setText(Integer.toString(numMinas)); } ; } int nd =
	 * numDescubiertas(); if ((casillas.length*casillas[0].length) - nd ==
	 * Tablero.maxMinas) { Alert alert = new Alert(AlertType.ERROR);
	 * alert.setTitle("FIN"); alert.setContentText("Has ganado, felicidades :D");
	 * alert.showAndWait(); System.exit(0); } } }
	 * 
	 * void AutoDescubrir(int ff, int cc) {
	 * pb.tablero[ff][cc].setStyle("-fx-background-color: rgb(170,255,255)");
	 * pb.tablero[ff][cc].descubierta = true; for (int x = ff - 1; x <= ff + 1; x++)
	 * for (int y = cc - 1; y <= cc + 1; y++) { if (x >= 0 && x < alto && y >= 0 &&
	 * y < ancho && !(x == ff && y == cc) && !pb.tablero[x][y].descubierta) { int
	 * numMinas = CuentaMinas(x, y); switch (numMinas) { case 0: AutoDescubrir(x,
	 * y); break; default: pb.tablero[x][y].descubierta = true;
	 * pb.tablero[x][y].setStyle("-fx-background-color: rgb(170,255,255)");
	 * pb.tablero[x][y].setText(Integer.toString(numMinas)); } ; } } }
	 * 
	 * static int numDescubiertas() { int nd = 0; for (int x = 0; x <
	 * Tablero.casillas.length; x++) for (int y = 0; y < Tablero.casillas[0].length;
	 * y++) if (descubiertas[x][y]) nd++; return nd; }
	 * 
	 * int CuentaMinas(int ff, int cc) { int nm = 0; for (int x = ff - 1; x <= ff+1;
	 * x++) for (int y = cc - 1; y <= cc+1; y++) if (!(x == ff && y == cc) && x >= 0
	 * && x < descubiertas.length && y >= 0 && y < descubiertas[y].length) if
	 * (descubiertas[x][y]) nm++; return nm; }
	 */
	public static String escribePanel() {
		StringBuilder string = new StringBuilder();
		for (int i = 0; i < casillas.length; i++) {
			for (int j = 0; j < casillas[i].length; j++)
				string.append(casillas[i][j] + " | ");
			string.append("\n");
		}

		return string.toString();
	}

	@Override
	public String toString() {
		return Tablero.escribePanel();
	}

}

class DescubrirAction implements Serializable, EventHandler<ActionEvent> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	int posY, posX, ancho, alto;

	public DescubrirAction(int ff, int cc, int ancho, int alto) {
		posY = ff;
		posX = cc;
		this.alto = alto;
		this.ancho = ancho;
	}

	@Override
	public void handle(ActionEvent event) {
		if (!Tablero.descubiertas[posY][posX]) {
			Tablero.descubiertas[posY][posX] = true;
//			pb.tablero[posY][posX].setStyle("-fx-background-color: rgb(170,255,255)");
			if (Tablero.casillas[posY][posX].equals("*")) {

				System.out.println("Panel:\n" + Tablero.escribePanel());

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("FIN");
				alert.setContentText("Has pisado una mina, fin :c");
				alert.showAndWait();
				System.exit(0);
			} else {
				int numMinas = CuentaMinas(posY, posX);
				switch (numMinas) {
				case 0:
					AutoDescubrir(posY, posX);
					break;
				default:
					Tablero.casillas[posY][posX]=String.valueOf(numMinas);
				}
				;
			}
			int nd = numDescubiertas();
			if ((Tablero.casillas.length * Tablero.casillas[0].length) - nd == Tablero.maxMinas) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("FIN");
				alert.setContentText("Has ganado, felicidades :D");
				alert.showAndWait();
				System.exit(0);
			}
		}
	}

	void AutoDescubrir(int ff, int cc) {
		Tablero.descubiertas[ff][cc] = true;
		for (int x = ff - 1; x <= ff + 1; x++)
			for (int y = cc - 1; y <= cc + 1; y++) {
				if (x >= 0 && x < alto && y >= 0 && y < ancho && !(x == ff && y == cc)
						&& !Tablero.descubiertas[x][y]) {
					int numMinas = CuentaMinas(x, y);
					switch (numMinas) {
					case 0:
						AutoDescubrir(x, y);
						break;
					default:
						Tablero.descubiertas[x][y] = true;
						Tablero.casillas[x][y] = String.valueOf(numMinas);
					}
					;
				}
			}
	}

	static int numDescubiertas() {
		int nd = 0;
		for (int x = 0; x < Tablero.casillas.length; x++)
			for (int y = 0; y < Tablero.casillas[0].length; y++)
				if (Tablero.descubiertas[x][y])
					nd++;
		return nd;
	}

	int CuentaMinas(int ff, int cc) {
		int nm = 0;
		for (int x = ff - 1; x <= ff + 1; x++)
			for (int y = cc - 1; y <= cc + 1; y++)
				if (!(x == ff && y == cc) && x >= 0 && x < Tablero.descubiertas.length && y >= 0
						&& y < Tablero.descubiertas[y].length)
					if (Tablero.descubiertas[x][y])
						nm++;
		return nm;

	}
}