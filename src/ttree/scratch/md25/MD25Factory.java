package ttree.scratch.md25;

import java.io.IOException;

import ttree.pipin.i2c.MD25;
import ttree.pipin.i2c.MD25Motor;
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
			final MD25Motor motors = new MD25Motor(device, 1, (byte)MD25.MODE_1);
			motors.setAccelRate((byte)10);
			
			return new MD25Remote(messageHandler, motors);
		} catch (IOException e) {
			MD25Remote.log.severe("MD25 not responsing on I2C bus: " + e.getMessage());
			return null;
		}

	}
	
}
