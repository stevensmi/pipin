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

	private final int firstMotor;
	
	/**
	 * Factory for a MD25Remote
	 * @param firstMotor numeric identifier for the first motor e.g. 1 will result in MOT1 and MOT2 being used
	 */
	public MD25Factory(int firstMotor) {
		this.firstMotor = firstMotor;
	}

	@Override
	public IncomingMessage make(OutgoingMessage messageHandler, I2CDevice device) {
		
		try {
			MD25Remote md25Remote = new MD25Remote(messageHandler, device, firstMotor);
			md25Remote.init();
			
			return md25Remote;
		}
		catch (IOException e) {
			MD25Remote.log.severe("MD25 not responsing on I2C bus: " + e.getMessage());
			return null;
		}

	}
	
}
