package pgv.apps.logica;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Clase que contiene la lista completa de los emojis, empleando la clase {@link Emoji}.
 * 
 */
public class EmojisList extends ArrayList<Emoji> {

	private static final long serialVersionUID = 1L;

	/**
	 * Aquí inicializamos desde el inicio los emojis que queremos que tenga nuestra aplicación.
	 */
	public static final ArrayList<Emoji> emojis = new ArrayList<Emoji>(
			Arrays.asList(
						new Emoji(EmojiEnum.BOMBA, "\uD83D\uDCA3")
					));
}
