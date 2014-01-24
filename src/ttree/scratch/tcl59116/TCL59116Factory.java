package ttree.scratch.tcl59116;

import java.io.IOException;

import ttree.pipin.i2c.LEDPWM;
import ttree.scratch.I2CRemoteSensorFactory;
import ttree.scratch.IncomingMessage;
import ttree.scratch.OutgoingMessage;

import com.pi4j.io.i2c.I2CDevice;

/**
 * Factory for the TCL59116 LED driver as a scratch remote sensor
 * 
 * @author Michael Stevens
 */
public class TCL59116Factory implements I2CRemoteSensorFactory {

	@Override
	public IncomingMessage make(OutgoingMessage messageHandler, I2CDevice device) {
		
		try {
			final LEDPWM leds = new LEDPWM(device);
			return new TCL59116Remote(messageHandler, leds);
		} catch (IOException e) {
			TCL59116Remote.log.severe("TCL59116 not responsing on I2C bus: " + e.getMessage());
			return null;
		}

	}

}
