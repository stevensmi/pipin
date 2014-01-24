package ttree.scratch.tcl59116;

import java.io.IOException;
import java.util.logging.Logger;

import ttree.pipin.i2c.LEDPWM;
import ttree.scratch.IncomingMessage;
import ttree.scratch.OutgoingMessage;

import com.pi4j.io.i2c.I2CDevice;

/**
 * Remote sensor for MD25 motor controller
 * 
 * @author Michael Stevens
 */
public class TCL59116Remote implements IncomingMessage {

	final static Logger log = Logger.getLogger("TCL59116Remote");

	@SuppressWarnings("unused")
	private final OutgoingMessage messageHandler;
	private final I2CDevice device;
	private final int firstLED;

	private LEDPWM leds = null;		// uninitialised
	
	public TCL59116Remote(OutgoingMessage messageHandler, I2CDevice device, final int firstLED) {
		this.messageHandler = messageHandler;
		this.device = device;
		this.firstLED = firstLED;
	}

	/**
	 * Initialise device
	 * @throws IOException 
	 */
	void init() throws IOException {
		leds = new LEDPWM(device);
	}

	@Override
	public void broadcast(String message) {
			// ignore all broadcast
	}

	@Override
	public void sensorUpdate(String name, String value) {

		if (name.startsWith("LED") == true) {
			int led;
			try {
				led = Integer.parseInt(name.substring(3));
			}
			catch (NumberFormatException nfe) {
				log.warning("LED number format: " + name);
				return;
			}

			if (led < firstLED || led > (firstLED + 15)) {
				log.warning("LED number range " + firstLED + ".." + (firstLED + 15) + ": " + name);
				return;
			}

			int ledValue;
			try {
				ledValue = Integer.parseInt(value);
			}
			catch (NumberFormatException nfe) {
				log.warning("LED value format: " + value);
				return;
			}

			// saturate led value
			if (ledValue < 0) {
				ledValue = 0;
			} else {
				if (ledValue > 255) {
					ledValue = 255;
				}
			}

			// everything validated - change the led PWM value
			try {
				leds.pwm(led-firstLED, ledValue);
			} catch (IOException e) {
				log.warning("LED cannot change pwm: " + e.getMessage());
			}
		}
	}

}
