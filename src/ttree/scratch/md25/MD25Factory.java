package ttree.scratch.md25;

import java.io.IOException;

import ttree.scratch.I2CRemoteSensorFactory;
import ttree.scratch.IncomingMessage;
import ttree.scratch.OutgoingMessage;

import com.pi4j.io.i2c.I2CDevice;

/**
 * Factory for the MD25 motor controller as a scratch remote sensor
 * 
 * @author Michael Stevens
 */
public class MD25Factory implements I2CRemoteSensorFactory {

	@Override
	public IncomingMessage make(OutgoingMessage messageHandler, I2CDevice device) {
		
		try {
			MD25Remote md25Remote = new MD25Remote(messageHandler, device);
			md25Remote.init();
			
			return md25Remote;
		}
		catch (IOException e) {
			MD25Remote.log.severe("MD25 not responsing on I2C bus: " + e.getMessage());
			return null;
		}

	}
	
}
