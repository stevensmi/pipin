package ttree.scratch;

/**
 * Callback for remote events
 * 
 * @author Michael Stevens
 */
public interface RemoteCallback {
	
	void broadcast(String text);

	void sensor_update(String name, String value);
}
