package pgv.apps.controllers;
import java.lang.System;
public class P {
	public static void main(String[] args) {
//		String shoe = "\uD83D\uDC61";
//		String bomb ="\uD83D\uDCA3";
//		
//		System.out.println(shoe+"\n"+bomb);
//		
//		Tablero tablero = new Tablero(5, 5, 10);
//		System.out.println(tablero.showAll());
//		
//		boolean b = 97=='a';
//		System.out.println(b);
		
		String hor="F";
		int iHor;
		if(hor.charAt(0)>=65)
			iHor = (int) hor.charAt(0) -55 ;
		else
			iHor = Integer.valueOf(hor);
		System.out.println(iHor);
	}
}
