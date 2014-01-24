package ttree.scratch.tcl59116;

import java.io.IOException;
import java.util.logging.Logger;

import ttree.pipin.i2c.LEDPWM;
import ttree.scratch.IncomingMessage;
import ttree.scratch.OutgoingMessage;

/**
 * Remote sensor for MD25 motor controller
 * 
 * @author Michael Stevens
 */
public class TCL59116Remote implements IncomingMessage {

	final static Logger log = Logger.getLogger("TCL59116Remote");

	final OutgoingMessage messageHandler;
	final LEDPWM leds;
	
	public TCL59116Remote(OutgoingMessage messageHandler, LEDPWM leds) {
		this.messageHandler = messageHandler;
		this.leds = leds;
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

			if (led < 1 || led > 16) {
				log.warning("LED number range 1..16: " + name);
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
				leds.pwm(led-1, ledValue);
			} catch (IOException e) {
				log.warning("LED cannot change pwm: " + e.getMessage());
			}
		}
	}

}
