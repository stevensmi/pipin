package ttree.scratch.md25;

import ttree.pipin.i2c.MD25Motor;
import ttree.scratch.I2CRemoteSensorFactory;
import ttree.scratch.IncomingMessage;
import ttree.scratch.OutgoingMessage;

import com.pi4j.io.i2c.I2CDevice;

/**
 * Factory for the MD25 motor controller as a scratch remote sensor.
 * 
 * @author Michael Stevens
 */
public class MD25Factory implements I2CRemoteSensorFactory {

	private final int firstMotor;
	
	/**
	 * Factory for a MD25Remote.
	 * @param firstMotor numeric identifier for the first motor e.g. 1 will result in MOT1 and MOT2 being used
	 */
	public MD25Factory(int firstMotor) {
		this.firstMotor = firstMotor;
	}

	@Override
	public IncomingMessage make(OutgoingMessage messageHandler, I2CDevice device) {

        final MD25Motor md25 = new MD25Motor(device);
        MD25Remote md25Remote = new MD25Remote(messageHandler, md25, firstMotor);
        md25Remote.init();

        return md25Remote;
    }
	
}
