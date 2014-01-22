package ttree.scratch;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Parse and generate the Scratch remote protocol
 * The broadcast and sensor-update are parsed
 *  
 * @author Michael Stevens
 */
public class ScratchRemoteProtocol {
	
	public static final String BROADCAST = "broadcast ";
	public static final String SENSOR_UPDATE = "sensor-update ";

	private static final int BROADCAST_LEN = BROADCAST.length();
	private static final int SENSOR_UPDATE_LEN = SENSOR_UPDATE.length();
	
	private static final Pattern quotePattern = Pattern.compile("\"");
	
	final Logger logIn = Logger.getLogger("FromScratch");
	final Logger logOut = Logger.getLogger("ToScratch");

	/**
	 * Construct
	 */
	public ScratchRemoteProtocol() {
	}
	
	/**
	 * Parse a command line and call the remoteCallback with all valid commands
	 * @param line
	 * @param remoteCallback
	 */
	public void parse(String line, RemoteCallback remoteCallback) {
		
		logIn.info(line);
		
		if (line.startsWith(BROADCAST) == true) {
			// single broadcast
			final Scanner scanner = new Scanner(line.substring(BROADCAST_LEN));

			final String text = quotedText(scanner);
			if (text != null) {
				remoteCallback.broadcast(text);
			}
			else {
				logIn.warning(BROADCAST + " expecting string in: " + line);
			}
		}
		else if (line.startsWith(SENSOR_UPDATE) == true) {
			
			final String changes = line.substring(SENSOR_UPDATE_LEN);
			// multiple sensor update
			final Scanner scanner = new Scanner(changes);
			
			while (true) {
				final String name = quotedText(scanner);
				if (name == null) {
					break;
				}
				String afterName = quotedText(scanner);
				if (afterName == null) {
					logIn.warning(SENSOR_UPDATE + " expecting string after: " + name + " in: " + line);
					break;
				}
				
				final Scanner valueScanner = new Scanner(afterName);
				final String value = wsDelimitedText(valueScanner);
				if (value == null) {
					logIn.warning(SENSOR_UPDATE + " expecting value after: " + name + " in: " + line);
				}
				remoteCallback.sensor_update(name, value);
			}
		}
		else {
			; // ignore
		}
	}

	/**
	 * Generate the line for a single broadcast value
	 * @param broadcast
	 * @return broadcast line
	 */
	public String generateBroadcast(String broadcast) {
		return BROADCAST + quoteIfWs(broadcast);
	}
	
	/**
	 * Generate the line for a list of sensor updates
	 * @param updates - assumed to be pairs of name, values
	 * @return update line
	 */
	public String generateSensorUpdate(String... updates) {
		final StringBuilder sb = new StringBuilder(SENSOR_UPDATE);
		final ListIterator<String> upIt = Arrays.asList(updates).listIterator();
		if (upIt.hasNext() == true) {
			while (true) {
				sb.append(quoteIfWs(upIt.next()));
				if (upIt.hasNext() == false)
					break;
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	private String quoteIfWs(String text) {
		final Scanner scanner = new Scanner(text);
		if (wsDelimitedText(scanner) == null) {
			return "\"" + text + "\"";
		}
		else {
			return text;		}
		
	}
	
	private String quotedText(Scanner scanner) {
		scanner.useDelimiter(quotePattern);
		// not quoted
		if (scanner.hasNext() == false) {
			return null;
		}
		return scanner.next();
	}
	
	private String wsDelimitedText(Scanner scanner) {
		scanner.reset();
		// no ws
		if (scanner.hasNext() == false) {
			return null;
		}
		return scanner.next();
	}
	
}
