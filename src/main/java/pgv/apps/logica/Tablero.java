package pgv.apps.logica;

import java.io.Serializable;

/**
 * Representa el tablero del juego, la clase que se movera entre los sockets y
 * permitira jugar
 * 
 */
public class Tablero implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Atributo que define lo que ve el jugador
	 */
	private String[][] campoVista;

	/**
	 * Atributo que define la ubicación de las bombas
	 */
	private String[][] campo;

	/**
	 * Atributos que definen las características del tablero (las proporciones y la
	 * cantidad de minas)
	 */
	private int numMinas, alto, ancho;

	/**
	 * Atributo que define si se ha acabado el juego por diversos motivos (victoria,
	 * derrota,...)
	 */
	private boolean caput = false;

	public boolean isCaput() {
		return caput;
	}

	public void setCaput(boolean caput) {
		this.caput = caput;
	}

	/**
	 * Genera un nuevo tablero con los atributos que se le pasan
	 * 
	 * @param alto  define la longitud vertical del tablero
	 * @param ancho define la longitud horizontal del tablero
	 * @param minas define la cantidad de minas que hay en el tablero
	 */
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

	/**
	 * Funcion que gestiona la seleccion de la casilla
	 * 
	 * @param ver Posicion vertical de la casilla
	 * @param hor Posicion horizontal de la casilla
	 * @return El estado de la casilla, si es -1, fin del juego, si es -2 es que ya
	 *         está pulsada y no hace nada. En caso de ser 0, felicidades, ganaste.
	 *         Con cualquier otro número es la cantidad de minas que hay alrededor
	 *         de la casilla escogida
	 */
	public int clickCasilla(int ver, int hor) {
		if (campoVista[ver][hor].equals("X")) {
			if (campo[ver][hor].equals("*")) {
				campoVista[ver][hor] = "*";
				for (int x = 0; x < alto; x++)
					for (int y = 0; y < ancho; y++)
						if (campo[ver][hor].equals("*")) {
							campoVista[ver][hor] = "*";
						}
				return -1;
			} else {
				int numMinas = CuentaMinas(ver, hor);
				switch (numMinas) {
				case 0:
					campoVista[ver][hor] = Integer.toString(numMinas);
					AutoDescubrir(ver, hor);
					break;
				default:
					campoVista[ver][hor] = Integer.toString(numMinas);
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

	/**
	 * Funcion auxiliar que calcula las minas alrededor de una casilla
	 * 
	 * @param ver posicion vertical de la casilla
	 * @param hor posicion horizontal de la casilla
	 * @return casillas alrededor de la casilla seleccionada
	 */
	int CuentaMinas(int ver, int hor) {
		int nm = 0;
		for (int x = ver - 1; x <= ver + 1; x++)
			for (int y = hor - 1; y <= hor + 1; y++)
				if (!(x == ver && y == hor) && x >= 0 && x < alto && y >= 0 && y < ancho)
					if (campo[x][y].equals("*"))
						nm++;
		return nm;
	}

	/**
	 * 
	 * @param ver posicion vertical de la casilla
	 * @param hor posicion horizontal de la casilla
	 */
	void AutoDescubrir(int ver, int hor) {
		for (int x = ver - 1; x <= ver + 1; x++)
			for (int y = hor - 1; y <= hor + 1; y++) {
				if (x >= 0 && x < alto && y >= 0 && y < ancho && !(x == ver && y == hor)
						&& campoVista[x][y].equals("X")) {
					int numMinas = CuentaMinas(x, y);
					switch (numMinas) {
					case 0:
						campoVista[x][y] = Integer.toString(numMinas);
						AutoDescubrir(x, y);
						break;

					default:
						campoVista[x][y] = Integer.toString(numMinas);
						break;
					}
				}
			}
	}

	/**
	 * Calcula la cantidad de casillas que se han seleccionado y, por tanto, son
	 * diferentes que la "X" inicial
	 * 
	 * @return cantidad de casillas diferentes al estado inicial
	 */
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

	/**
	 * El toString sobreescrito. Este toString permite la visualizacion al jugador
	 * del estado actual del tablero.
	 */
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

	/**
	 * Una especie de toString pero solo muestra la ubicacion de las bombas
	 */
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

	/**
	 * Otra especie de toString pero muestra la ubicacion de las bombas + las
	 * casillas del toString sobreescrito
	 */
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
				if (!campo[i][j].equals("*")) {
					string.append(campoVista[i][j] + "  ");
				} else {
					string.append(campo[i][j] + "  ");
				}
			}
			string.append("\n");
		}

		return string.toString();
	}

}
