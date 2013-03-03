package ttree.scratch;

import java.io.IOException;
import java.util.logging.Logger;

import ttree.pipin.i2c.LEDPWM;

public class ScratchLED implements RemoteCallback {

	final static Logger log = Logger.getLogger("ScratchLED");
	
	final LEDPWM ledpwm;

	/**
	 * Main for LED Test
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		int address = -1;
		if (args.length == 1) {
			try {
				address = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException nfe) {
				; // ignore
			}
		}
		if (address == -1) {
			log.info("Usage: <address>    where address is decimal and the I2C address on bus 1 of the TCL59116");
			return;
		}
		
		log.info("Accessing TCL59115 on I2C bus 1 address "+ address);
		final ScratchLED scratchLED = new ScratchLED(address);
		
		log.info("Connecting to scratch remote sensor");
		final RemoteListener listner = new RemoteListener();
		
		final CommandParser command = new CommandParser();
		for (;;) {
			final String line = listner.readLine();
			if (line == null) {
				break;
			}
			
			command.parse(line, scratchLED);
		}
		log.info("scratch finished");
	}

	public ScratchLED(int address) throws IOException {
		ledpwm = new LEDPWM(address);
	}
	
	public void broadcast(String text) {

		log.info("broadcast: " + text);
	}

	public void sensor_update(String name, String value) {
		
		log.info("sensor-update: " + name + "=" + value);
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
			
			if (ledValue < 0 || ledValue > 255) {
				log.warning("LED value range 0..255: " + value);
				return;
			}
			
			// everything validated - change the led PWM value
			try {
				ledpwm.pwm(led-1, ledValue);
			} catch (IOException e) {
				log.warning("LED cannot change pwm: " + e.getMessage());
			}
		}
	}
	
}
