package ttree.scratch;

import com.pi4j.io.i2c.I2CDevice;

/**
 * Standarised factory for remote sensors which are a single device on an I2C bus.
 * 
 * @author Michael Stevens
 */
public interface I2CRemoteSensorFactory {
	
	/**
	 * Make a remote sensor instance.
	 * @param messageHandler the outgoing message handler to scratch
	 * @param device the I2C device
	 * @return the incoming message handler for the remote instance or null if none
	 */
	IncomingMessage make(OutgoingMessage messageHandler, I2CDevice device);

}
