package ttree.scratch.protocol;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Support for WS and quoted text
 *  
 * @author Michael Stevens
 */
public class TextParsing {
	
	private static final Pattern quotePattern = Pattern.compile("\"");
	
	/**
	 * Place double quote around text if it contains a WS
	 * @param text
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
	 * 
	 * @param scanner
	 * @return
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
	 * Determine the next WS delimited text item in a scanner
	 * @param scanner
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
