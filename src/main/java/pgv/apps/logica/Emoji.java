package pgv.apps.logica;

import javafx.util.Pair;

/**
 * Clase emoji que hereda de {@link Pair} para generar emojis con un ID de tipo
 * {@link EmojiEnum} y su equivalente unicode para la impresión de emojis
 * 
 */
public class Emoji extends Pair<EmojiEnum, String> implements Comparable<Emoji>{

	private static final long serialVersionUID = 1L;

	public Emoji(EmojiEnum key, String value) {
		super(key, value);
	}
	
	@Override
	public String toString() {
        return  this.getKey().toString().toLowerCase()+"("+this.getValue()+")";
	}

	@Override
	public int compareTo(Emoji o) {
		return this.getKey().toString().compareToIgnoreCase(o.getKey().toString());
	}
}
