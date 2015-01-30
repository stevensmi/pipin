package ttree.scratch.tlc59116;

import java.io.IOException;
import java.util.logging.Logger;

import ttree.pipin.i2c.LEDPWM;
import ttree.scratch.IncomingMessage;

/**
 * Remote sensor for TLC59116 LED driver,
 * 
 * @author Michael Stevens
 */
public class TLC59116Remote implements IncomingMessage {

	final private Logger log = Logger.getLogger("TCL59116Remote");

	private final int firstLED;
	private final LEDPWM leds;
	
	public TLC59116Remote(LEDPWM leds, final int firstLED) {
		this.leds = leds;
		this.firstLED = firstLED;
	}

	/**
	 * Initialise the device.
	 */
	void init() {
        try {
            leds.setup();
        } catch (IOException e) {
            log.severe("TCL59116 not responsing on I2C bus: " + e.getMessage());
        }
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
				leds.pwm(led-firstLED, (byte)ledValue);
			} catch (IOException e) {
				log.warning("LED cannot change pwm: " + e.getMessage());
			}
		}
	}

}
