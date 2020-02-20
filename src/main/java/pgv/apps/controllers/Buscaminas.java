package pgv.apps.controllers;

import java.io.Serializable;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

public class Buscaminas extends GridPane implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Casilla3 tablero[][];
	boolean minas[][];
	int maxMinas;

	@Override
	public String toString() {
		StringBuffer texto = new StringBuffer();
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero[i].length; j++) {
				texto.append(tablero[i][j].getText() + " | ");
			}
			texto.append("\n");
		}
		return texto.toString();
	}

	public Buscaminas(){
       
    }
	
	public Buscaminas(int ancho, int alto, int numMinas) {
		super();
		int f, c;
		setWidth(300);
		setHeight(300);

		tablero = new Casilla3[alto][ancho];
		minas = new boolean[alto][ancho];
		maxMinas = numMinas;
		// creamos los botones y vaciamos la matriz donde
		// colocaremos las minas
		for (f = 0; f < alto; f++)
			for (c = 0; c < ancho; c++) {
				minas[f][c] = false;
				tablero[f][c] = new Casilla3("");
				add(tablero[f][c], f, c);
				tablero[f][c].setOnAction(new AccionBoton3(this, f, c, ancho, alto));
			}

		// colocamos minas
		for (int n = 0; n < maxMinas; n++) {
			do {
				f = (int) (Math.random() * alto);
				c = (int) (Math.random() * ancho);
			} while (minas[f][c]);
			minas[f][c] = true;
		}
	}

	public Casilla3[][] getCasilla3() {
		return tablero;
	}

	public void setCasilla3(Casilla3[][] tablero) {
		this.tablero = tablero;
	}

	public boolean[][] getMinas() {
		return minas;
	}

	public void setMinas(boolean[][] minas) {
		this.minas = minas;
	}

	public int getMaxMinas() {
		return maxMinas;
	}

	public void setMaxMinas(int maxMinas) {
		this.maxMinas = maxMinas;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}

class Casilla3 extends Button implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean descubierta;
	String valor;

	public Casilla3(String t) {
		super(t);
		setStyle("-fx-background-color: rgb(200,200,200)");
		descubierta = false;
	}

	public Casilla3(String t, boolean b) {
		super(t);
		if (b) {
			setStyle("-fx-background-color: rgb(170,255,255)");
		}else {
			setStyle("-fx-background-color: rgb(200,200,200)");
		}
		
		descubierta = b;
		valor = t;
	}
	
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public boolean isDescubierta() {
		return descubierta;
	}

	public void setDescubierta(boolean descubierta) {
		this.descubierta = descubierta;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

class AccionBoton3 implements Serializable, EventHandler<ActionEvent> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Buscaminas getPb() {
		return pb;
	}

	public void setPb(Buscaminas pb) {
		this.pb = pb;
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	Buscaminas pb;
	int f, c, ancho, alto;

	AccionBoton3(Buscaminas p, int ff, int cc, int ancho, int alto) {
		pb = p;
		f = ff;
		c = cc;
		this.alto = alto;
		this.ancho = ancho;
	}

	void AutoDescubrir(int ff, int cc) {
		pb.tablero[ff][cc].setStyle("-fx-background-color: rgb(170,255,255)");
		pb.tablero[ff][cc].descubierta = true;
		for (int x = ff - 1; x <= ff + 1; x++)
			for (int y = cc - 1; y <= cc + 1; y++) {
				if (x >= 0 && x < alto && y >= 0 && y < ancho && !(x == ff && y == cc)
						&& !pb.tablero[x][y].descubierta) {
					int numMinas = CuentaMinas(x, y);
					switch (numMinas) {
					case 0:
						AutoDescubrir(x, y);
						break;
					default:
						pb.tablero[x][y].descubierta = true;
						pb.tablero[x][y].setStyle("-fx-background-color: rgb(170,255,255)");
						pb.tablero[x][y].setText(Integer.toString(numMinas));
					}
					;
				}
			}
	}

	int numDescubiertas() {
		int nd = 0;
		for (int x = 0; x < alto; x++)
			for (int y = 0; y < ancho; y++)
				if (pb.tablero[x][y].descubierta)
					nd++;
		return nd;
	}

	@Override
	public void handle(ActionEvent event) {
		if (!pb.tablero[f][c].descubierta) {
			pb.tablero[f][c].descubierta = true;
			pb.tablero[f][c].setStyle("-fx-background-color: rgb(170,255,255)");
			if (pb.minas[f][c]) {
				pb.tablero[f][c].setText("*");
				for (int x = 0; x < alto; x++)
					for (int y = 0; y < ancho; y++)
						if (pb.minas[x][y]) {
							pb.tablero[x][y].setStyle("-fx-background-color: rgb(255,50,100)");
							pb.tablero[x][y].setText("*");
						}
//	                JOptionPane.showMessageDialog(pb.getParent(), "Lo siento, has perdido");

				System.out.println("Panel:\n" + pb.toString());

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("FIN");
				alert.setContentText("Has pisado una mina, fin :c");
				alert.showAndWait();
				System.exit(0);
			} else {
				int numMinas = CuentaMinas(f, c);
				switch (numMinas) {
				case 0:
					AutoDescubrir(f, c);
					break;
				default:
					pb.tablero[f][c].setText(Integer.toString(numMinas));
				}
				;
			}
			int nd = numDescubiertas();
//	            pb.getParent().setTitle("Quedan " + Integer.toString((ancho*alto)-nd)+" casillas");            
			if (1500 - nd == pb.maxMinas) {
//	                JOptionPane.showMessageDialog(pb.getParent(), "Muy bien, ya has terminado");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("FIN");
				alert.setContentText("Has ganado, felicidades :D");
				alert.showAndWait();
				System.exit(0);
			}
		}
	}

	int CuentaMinas(int ff, int cc) {
		int nm = 0;
		for (int x = ff - 1; x <= ff + 1; x++)
			for (int y = cc - 1; y <= cc + 1; y++)
				if (!(x == ff && y == cc) && x >= 0 && x < alto && y >= 0 && y < ancho)
					if (pb.minas[x][y])
						nm++;
		return nm;
	}

}
