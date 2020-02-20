package pgv.controller;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

import javax.swing.JOptionPane;

public class Buscaminas {
//    public static void main(String[] args) {
//        Frame f = new Frame();
//        PanelBuscaminas panel = new PanelBuscaminas();
//        f.add(panel);
//        f.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent we) {
//                if(we.equals(we.WINDOW_CLOSING))
//            	System.exit(0);
//            }
//        });
//        f.setSize(900, 540);
//        f.show();
//    }
}

class PanelBuscaminas extends Panel implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Casilla tablero[][];
    boolean minas[][];
    int maxMinas;
//    PanelBuscaminas(){
//        int f,c;
//        setLayout(new GridLayout(30,50));
//        tablero = new Casilla[30][50];
//        minas = new boolean[30][50];
//        maxMinas = 250;
//        // creamos los botones y vaciamos la matriz donde
//        // colocaremos las minas
//        for(f=0; f<30; f++)
//            for(c=0; c<50; c++)
//            {
//                minas[f][c] = false;
//                tablero[f][c] = new Casilla("");
//                add(tablero[f][c]);
//                tablero[f][c].addActionListener(new AccionBoton(this,f,c));
//            }
//        
//        // colocamos minas
//        for(int n=0; n<maxMinas; n++)
//        {
//            do {
//                f = (int)(Math.random()*30);
//                c = (int)(Math.random()*50);
//            }while(minas[f][c]);
//            minas[f][c] = true;
//        }
//    }
    PanelBuscaminas(int ancho, int alto, int numMinas){
        int f,c;
        setLayout(new GridLayout(alto, ancho));
        tablero = new Casilla[alto][ancho];
        minas = new boolean[alto][ancho];
        maxMinas = numMinas;
        // creamos los botones y vaciamos la matriz donde
        // colocaremos las minas
        for(f=0; f<30; f++)
            for(c=0; c<50; c++)
            {
                minas[f][c] = false;
                tablero[f][c] = new Casilla("");
                add(tablero[f][c]);
                tablero[f][c].addActionListener(new AccionBoton(this,f,c, ancho, alto));
            }
        
        // colocamos minas
        for(int n=0; n<maxMinas; n++)
        {
            do {
                f = (int)(Math.random()*alto);
                c = (int)(Math.random()*ancho);
            }while(minas[f][c]);
            minas[f][c] = true;
        }
    }
	public Casilla[][] getTablero() {
		return tablero;
	}
	public void setTablero(Casilla[][] tablero) {
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

class Casilla extends Button implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean descubierta;
    Casilla(String t){
        super.setLabel(t);
        setBackground(new Color(200,200,200));
        setFont(new Font("Comic Sans Ms", Font.BOLD, 20));
        descubierta = false;
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

class AccionBoton implements ActionListener, Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public PanelBuscaminas getPb() {
		return pb;
	}
	public void setPb(PanelBuscaminas pb) {
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
	PanelBuscaminas pb;
    int f, c, ancho, alto;
    AccionBoton(PanelBuscaminas p,int ff, int cc, int ancho, int alto){
        pb = p;
        f = ff;
        c = cc;
        this.alto = alto;
        this.ancho = ancho;
    }
    public void actionPerformed(ActionEvent ae) {
        if(!pb.tablero[f][c].descubierta)
        {
            pb.tablero[f][c].descubierta = true;
            pb.tablero[f][c].setBackground(new Color(170,255,255));
            if(pb.minas[f][c])
            {
                pb.tablero[f][c].setLabel("*");
                for(int x=0; x<30; x++)
                    for(int y=0; y<50; y++)
                        if(pb.minas[x][y])
                        {
                            pb.tablero[x][y].setBackground(new Color(255,50,100));
                            pb.tablero[x][y].setLabel("*");
                        }
                JOptionPane.showMessageDialog(pb.getParent(), "Lo siento, has perdido");
                System.exit(0);
            }
            else
            {
                int numMinas = CuentaMinas(f,c);
                switch(numMinas){
                    case 0:
                        AutoDescubrir(f,c);
                        break;
                    default:
                        pb.tablero[f][c].setLabel(Integer.toString(numMinas));
                };
            }
            int nd = numDescubiertas();
            ((Frame)pb.getParent()).setTitle("Quedan " + Integer.toString((ancho*alto)-nd)+" casillas");            
            if(1500-nd == pb.maxMinas)
            {
                JOptionPane.showMessageDialog(pb.getParent(), "Muy bien, ya has terminado");
                System.exit(0);
            }
        }
    }
    int CuentaMinas(int ff, int cc){
        int nm = 0;
        for(int x=ff-1; x<=ff+1; x++)
            for(int y=cc-1; y<=cc+1; y++)
                if(!(x==ff && y==cc) &&
                    x>=0 && x<30 &&
                    y>=0 && y<50)
                    if(pb.minas[x][y])
                        nm++;
        return nm;
    }
    void AutoDescubrir(int ff, int cc){
        pb.tablero[ff][cc].setBackground(new Color(170,255,255));
        pb.tablero[ff][cc].descubierta = true;
        for(int x=ff-1; x<=ff+1; x++)
            for(int y=cc-1; y<=cc+1; y++)
            {
                if(x>=0 && x<30 && y>=0 && y<50 &&
                !(x==ff && y==cc) &&
                !pb.tablero[x][y].descubierta)
                {
                    int numMinas = CuentaMinas(x,y);
                    switch(numMinas){
                        case 0:
                            AutoDescubrir(x,y);
                            break;
                        default:
                            pb.tablero[x][y].descubierta = true;
                            pb.tablero[x][y].setBackground(new Color(170,255,255));
                            pb.tablero[x][y].setLabel(Integer.toString(numMinas));
                    };
                }
            }
    }
    int numDescubiertas(){
        int nd=0;
        for(int x=0; x<30; x++)
            for(int y=0; y<50; y++)
                if(pb.tablero[x][y].descubierta)
                    nd++;
        return nd;
    }
}
