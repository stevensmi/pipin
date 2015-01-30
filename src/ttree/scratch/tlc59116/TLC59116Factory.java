package ttree.scratch.tlc59116;

import com.pi4j.io.i2c.I2CDevice;
import ttree.pipin.i2c.LEDPWM;
import ttree.scratch.I2CRemoteSensorFactory;
import ttree.scratch.IncomingMessage;
import ttree.scratch.OutgoingMessage;

/**
 * Factory for the TLC59116 LED driver as a scratch remote sensor.
 * 
 * @author Michael Stevens
 */
public class TLC59116Factory implements I2CRemoteSensorFactory {

	private final int firstLED;
	
	/**
	 * Factory for a TCL59116Remote.
	 * @param firstLED numeric identifier for the first LED e.g. 1 will result in LED1 to MOT16 being used
	 */
	public TLC59116Factory(int firstLED) {
		this.firstLED = firstLED;
	}

	@Override
	public IncomingMessage make(OutgoingMessage messageHandler, I2CDevice device) {

        final LEDPWM leds = new LEDPWM(device);
		final TLC59116Remote tcl59116Remote = new TLC59116Remote(leds, firstLED);
		tcl59116Remote.init();

        return tcl59116Remote;
	}

}
