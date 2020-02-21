package pgv.apps.controllers;

import java.io.Serializable;

public class Tablero implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[][] campoVista;

	private String[][] campo;

	private int numMinas, alto, ancho;

	public Tablero() {
	}

	public Tablero(int alto, int ancho, int minas) {
		this.numMinas = minas;
		campo = new String[alto][ancho];
		campoVista = new String[alto][ancho];
		this.setAlto(alto);
		this.setAncho(ancho);

		int i, j;

		for (i = 0; i < alto; i++) {
			for (j = 0; j < ancho; j++) {
				campo[i][j] = " ";
				campoVista[i][j] = "X";
			}
		}

		for (int n = 0; n < minas; n++) {
			do {
				i = (int) (Math.random() * alto);
				j = (int) (Math.random() * ancho);
			} while (campo[i][j].equals("*"));
			campo[i][j] = "*";
		}

	}
	
	public Tablero(String[][] campo, String[][] minas, int alto, int ancho, int minaNum) {
		this.alto=alto;
		this.ancho=ancho;
		this.campo=minas;
		this.campoVista=campo;
		this.numMinas=minaNum;
	}

	public int clickCasilla(int hor, int ver) {
		if (!campoVista[hor][ver].equals("X")) {
			if (campo[hor][ver].equals("*")) {
				campoVista[hor][ver] = "*";
				for (int x = 0; x < alto; x++)
					for (int y = 0; y < ancho; y++)
						if (campo[hor][ver].equals("*")) {
							campoVista[hor][ver] = "*";
						}
				return -1;
			} else {
				int numMinas = CuentaMinas(hor, ver);
				switch (numMinas) {
				case 0:
					AutoDescubrir(hor, ver);
					break;
				default:
					campoVista[hor][ver] = Integer.toString(numMinas);
				}
				
			}
			int nd = numDescubiertas();
			if ((alto * ancho) - nd == numMinas) {
				return 0;
			}
			return (ancho * alto) - nd;
		}
		return -2;
	}

	int CuentaMinas(int ff, int cc) {
		int nm = 0;
		for (int x = ff - 1; x <= ff + 1; x++)
			for (int y = cc - 1; y <= cc + 1; y++)
				if (!(x == ff && y == cc) && x >= 0 && x < alto && y >= 0 && y < ancho)
					if (campo[x][y].equals("*"))
						nm++;
		return nm;
	}

	void AutoDescubrir(int ff, int cc) {
		for (int x = ff - 1; x <= ff + 1; x++)
			for (int y = cc - 1; y <= cc + 1; y++) {
				if (x >= 0 && x < alto && y >= 0 && y < ancho && !(x == ff && y == cc)
						&& !campoVista[x][y].equals("X")) {
					int numMinas = CuentaMinas(x, y);
					switch (numMinas) {
					case 0:
						AutoDescubrir(x, y);
						break;
					default:
						campoVista[x][y] = Integer.toString(numMinas);
					}
					;
				}
			}
	}

	int numDescubiertas() {
		int nd = 0;
		for (int x = 0; x < alto; x++)
			for (int y = 0; y < ancho; y++)
				if (!campoVista[x][y].equals("X"))
					nd++;
		return nd;
	}

	public String[][] getCampo() {
		return campo;
	}

	public void setCampo(String[][] campo) {
		this.campo = campo;
	}

	public int getNumMinas() {
		return numMinas;
	}

	public void setNumMinas(int numMinas) {
		this.numMinas = numMinas;
	}

	public String[][] getCampoVista() {
		return campoVista;
	}

	public void setCampoVista(String[][] campoVista) {
		this.campoVista = campoVista;
	}

	public int getAlto() {
		return alto;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}

	public int getAncho() {
		return ancho;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(" ");
		for (int z = 0; z < ancho; z++) {
			if (z < 9) {
				string.append("  " + (z + 1));
			} else {
				string.append("  " + ((char) (55 + z + 1)));
			}

		}
		string.append("\n");
		for (int i = 0; i < campoVista.length; i++) {
			if (i < 9) {
				string.append((i + 1) + "  ");
			} else {
				string.append(((char) (55 + i + 1)) + "  ");
			}

			for (int j = 0; j < campoVista[i].length; j++) {
				string.append(campoVista[i][j] + "  ");

			}
			string.append("\n");
		}

		return string.toString();
	}

	public String showBombs() {
		StringBuilder string = new StringBuilder();
		string.append(" ");
		for (int z = 0; z < ancho; z++) {
			if (z < 9) {
				string.append("  " + (z + 1));
			} else {
				string.append("  " + ((char) (55 + z + 1)));
			}

		}
		string.append("\n");
		for (int i = 0; i < campo.length; i++) {
			if (i < 9) {
				string.append((i + 1) + "  ");
			} else {
				string.append(((char) (55 + i + 1)) + "  ");
			}

			for (int j = 0; j < campo[i].length; j++) {
				string.append(campo[i][j] + "  ");
			}
			string.append("\n");
		}

		return string.toString();
	}
	
	public String showAll() {
		StringBuilder string = new StringBuilder();
		string.append(" ");
		for (int z = 0; z < ancho; z++) {
			if (z < 9) {
				string.append("  " + (z + 1));
			} else {
				string.append("  " + ((char) (55 + z + 1)));
			}

		}
		string.append("\n");
		for (int i = 0; i < campoVista.length; i++) {
			if (i < 9) {
				string.append((i + 1) + "  ");
			} else {
				string.append(((char) (55 + i + 1)) + "  ");
			}

			for (int j = 0; j < campoVista[i].length; j++) {
				if(!campo[i][j].equals("*")) {
					string.append(campoVista[i][j] + "  ");
				}else {
					string.append(campo[i][j] + "  ");
				}
			}
			string.append("\n");
		}

		return string.toString();
	}

}
