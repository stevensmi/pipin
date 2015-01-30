package ttree.scratch.protocol;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Support for white space and quoted text.
 *  
 * @author Michael Stevens
 */
public class TextParsing {
	
	private static final Pattern quotePattern = Pattern.compile("\"");
	
	/**
	 * Place double quote around text if it contains a white space.
	 * @param text text to parse
	 * @return text which is quoted if necessary
	 */
	public static String quoteIfWs(String text) {
		final Scanner scanner = new Scanner(text);
		final String wsText = nextText(scanner);
		if (wsText == null || wsText.equals(text) == false) {
			return "\"" + text + "\"";
		}
		else {
			return text;
		}
	}
	
	/**
	 * Setup scanner to use a quote as pattern.
	 * @param scanner scanner to setup
	 * @return next text or null if there is no more text
	 */
	public static String quotedText(Scanner scanner) {
		scanner.useDelimiter(quotePattern);
		// not quoted
		if (scanner.hasNext() == false) {
			return null;
		}
		return scanner.next();
	}
	
	
	/**
	 * Determine the next white space delimited text item in a scanner.
	 * @param scanner scanner to use
	 * @return the text or null if there is no more text
	 */
	public static String nextText(Scanner scanner) {
		// no text
		if (scanner.hasNext() == false) {
			return null;
		}
		return scanner.next();
	}
	
}
