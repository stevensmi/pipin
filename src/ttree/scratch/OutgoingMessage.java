package ttree.scratch;


/**
 * Handler for remote sensor messages going to scratch.
 * 
 * @author Michael Stevens
 */
public interface OutgoingMessage {

	/**
	 * A broadcast.
	 * @param message the broadcast message
	 */
	public void broadcast(String message);
	
	/**
	 * A list of sensor updates.
	 * @param updates - assumed to be pairs of name, values
	 */
	public void sensorUpdate(String... updates);
		
}
