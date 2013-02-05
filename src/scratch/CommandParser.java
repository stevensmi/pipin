package scratch;

import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Parse the Scratch remote commands
 * The broadcast and sensor-update are parsed
 *  
 * @author Michael Stevens
 */
public class CommandParser {
	
	public static final String BROADCAST = "broadcast ";
	public static final String SENSOR_UPDATE = "sensor-update ";

	private static final int BROADCAST_LEN = BROADCAST.length();
	private static final int SENSOR_UPDATE_LEN = SENSOR_UPDATE.length();
	
	private static final Pattern quotePattern = Pattern.compile("\"");
	
	final Logger log = Logger.getLogger("Scratch remote command parser");

	/**
	 * Construct
	 */
	public CommandParser() {
	}
	
	/**
	 * Parse a command line and call the remoteCallback with all valid commands
	 * @param line
	 * @param remoteCallback
	 */
	public void parse(String line, RemoteCallback remoteCallback) {
		
		log.fine(line);
		
		if (line.startsWith(BROADCAST) == true) {
			// single broadcast
			final Scanner scanner = new Scanner(line.substring(BROADCAST_LEN));

			final String text = quotedText(scanner);
			if (text != null) {
				remoteCallback.broadcast(text);
			}
			else {
				log.warning(line);
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
					log.warning(line);
				}
				
				final Scanner valueScanner = new Scanner(afterName);
				final String value = wsText(valueScanner);
				if (value == null) {
					log.warning(line);
				}
				remoteCallback.sensor_update(name, value);
			}
		}
		else {
			; // ignore
		}
	}

	private String quotedText(Scanner scanner) {
		scanner.useDelimiter(quotePattern);
		// not quoted
		if (scanner.hasNext() == false) {
			return null;
		}
		return scanner.next();
	}
	
	private String wsText(Scanner scanner) {
		scanner.reset();
		// no ws
		if (scanner.hasNext() == false) {
			return null;
		}
		return scanner.next();
	}
	
}
