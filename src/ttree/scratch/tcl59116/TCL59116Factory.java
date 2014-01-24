package ttree.scratch.tcl59116;

import java.io.IOException;

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

	private final int firstLED;
	
	/**
	 * Factory for a TCL59116Remote
	 * @param firstLED numeric identifier for the first LED e.g. 1 will result in LED1 to MOT16 being used
	 */
	public TCL59116Factory(int firstLED) {
		this.firstLED = firstLED;
	}

	@Override
	public IncomingMessage make(OutgoingMessage messageHandler, I2CDevice device) {
		
		try {
			final TCL59116Remote tcl59116Remote = new TCL59116Remote(messageHandler, device, firstLED);
			tcl59116Remote.init();
			
			return tcl59116Remote;
		}
		catch (IOException e) {
			TCL59116Remote.log.severe("TCL59116 not responsing on I2C bus: " + e.getMessage());
			return null;
		}

	}

}
