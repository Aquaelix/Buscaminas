package pgv.apps.logica;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;;

/**
 * Clase que contiene la lista completa de los emojis, empleando la clase
 * {@link Emoji}.
 * 
 */
public class EmojisList {

	/**
	 * Aquí inicializamos desde el inicio los emojis que queremos que tenga nuestra
	 * aplicación.
	 */
	public static List<Emoji> listaEmojis = new ArrayList<Emoji>(

			Arrays.asList(new Emoji(EmojiEnum.BOMBA, "\uD83D\uDCA3"), new Emoji(EmojiEnum.FELIZ, "\uD83D\uDE01"),
					new Emoji(EmojiEnum.RISA, "\uD83D\uDE02"), new Emoji(EmojiEnum.UPS, "\uD83D\uDE05"),
					new Emoji(EmojiEnum.DIABLILLO, "\uD83D\uDE08"), new Emoji(EmojiEnum.ANGEL, "\uD83D\uDE07"),
					new Emoji(EmojiEnum.GAFAS_SOL, "\uD83D\uDE0E"), new Emoji(EmojiEnum.DESPRECIO, "\uD83D\uDE12"),
					new Emoji(EmojiEnum.BESO, "\uD83D\uDE18"), new Emoji(EmojiEnum.TRISTE, "\uD83D\uDE1E"),
					new Emoji(EmojiEnum.ENFADADO, "\uD83D\uDE20"), new Emoji(EmojiEnum.LLORAR, "\uD83D\uDE22"),
					new Emoji(EmojiEnum.CIRCULO, "\u20DD"), new Emoji(EmojiEnum.CUADRADO, "\u20DE"),
					new Emoji(EmojiEnum.DIAMANTE, "\u20DF"), new Emoji(EmojiEnum.PROHIBIDO, "\u20E0"),
					new Emoji(EmojiEnum.RELOJ, "\u231A"), new Emoji(EmojiEnum.RELOJ_ARENA, "\u231B"),
					new Emoji(EmojiEnum.SIN_PALBRAS, "\uD83D\uDE36"), new Emoji(EmojiEnum.DORMIR, "\uD83D\uDE34"),
					new Emoji(EmojiEnum.COPITO, "\u2744"), new Emoji(EmojiEnum.BRILLO, "\u2728"),
					new Emoji(EmojiEnum.PAJARO, "\uD83D\uDC26"), new Emoji(EmojiEnum.UNICORNIO, "\uD83E\uDD84"),
					new Emoji(EmojiEnum.SOL, "\u2600"), new Emoji(EmojiEnum.NUBE, "\u2601"),
					new Emoji(EmojiEnum.PARAGUAS, "\u2602"), new Emoji(EmojiEnum.ESTRELLA, "\u2606")

			));

	public static List<Emoji> sortEmojis() {
		Collections.sort(listaEmojis);
		return listaEmojis;
	}

	public static final List<Emoji> emojisOrdenados = sortEmojis();

}
