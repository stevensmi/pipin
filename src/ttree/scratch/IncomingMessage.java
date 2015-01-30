package ttree.scratch;

/**
 * Handler for remote sensor messages coming from scratch.
 * 
 * @author Michael Stevens
 */
public interface IncomingMessage {
	
	/**
	 * A broadcast.
	 * @param message the broadcast message
	 */
	void broadcast(String message);

	/**
	 * A single sensor update.
	 * @param name sensor name
	 * @param value sensor value
	 */
	void sensorUpdate(String name, String value);
}
