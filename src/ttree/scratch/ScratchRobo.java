package ttree.scratch;

import java.io.IOException;
import java.util.logging.Logger;

import ttree.pipin.i2c.LEDPWM;
import ttree.pipin.i2c.MD25Motor;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/*
 * Scratch remote for robot control
 */
public class ScratchRobo implements RemoteCallback {

	final static Logger log = Logger.getLogger("ScratchRobo");
	
	final MD25Motor motors;
	final LEDPWM leds;

	/**
	 * Main for Scratch remote sensor support
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		int address_md25 = -1;
		int address_tcl59116 = -1;
		if (args.length == 2) {
			try {
				address_md25 = Integer.parseInt(args[0]);
				address_tcl59116 = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException nfe) {
				; // ignore
			}
		}
		if (address_md25 == -1 || address_tcl59116 == -1) {
			log.info("Usage: <MD25 address> <TCL59116 address> where addresses are decimal and the I2C address on bus 1 of the device");
			return;
		}
		
		// scratch remote parse and sensor output
		final ScratchRobo scratch = new ScratchRobo(address_md25, address_tcl59116);
		
		log.info("Connecting to scratch remote sensor");
		final RemoteListener listener = new RemoteListener();
		
		final CommandParser command = new CommandParser();
		for (;;) {
			final String line = listener.readLine();
			if (line == null) {
				break;
			}
			
			command.parse(line, scratch);
		}
		log.info("scratch finished");
	}

	public ScratchRobo(int address_md25, int address_tcl59116) throws IOException {
		// RPi external I2C bus
		final I2CBus piExtBus = I2CFactory.getInstance(1);
		
		// Devices
		final I2CDevice device_md25 = piExtBus.getDevice(address_md25);
		final I2CDevice device_tcl59116 = piExtBus.getDevice(address_tcl59116);
		
		this.motors = new MD25Motor(device_md25, 1, (byte)1);
		this.leds = new LEDPWM(device_tcl59116);
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
				leds.pwm(led-1, ledValue);
			} catch (IOException e) {
				log.warning("LED cannot change pwm: " + e.getMessage());
			}
		}

		if (name.startsWith("MOT") == true) {
			int motor;
			try {
				motor = Integer.parseInt(name.substring(3));
			}
			catch (NumberFormatException nfe) {
				log.warning("MOT number format: " + name);
				return;
			}
			
			if (motor < 1 || motor > 2) {
				log.warning("MOT number range 1..2: " + name);
				return;
			}
			
			int speed;
			try {
				speed = Integer.parseInt(value);
			}
			catch (NumberFormatException nfe) {
				log.warning("LED value format: " + value);
				return;
			}
			
			if (speed < -128 || speed > 127) {
				log.warning("MOT speed range -128..127: " + value);
				return;
			}
			
			// everything validated - change the motor speed
			try {
				if (motor == 1) {
					motors.setSpeed1((byte)speed);
				}
				else {
					motors.setSpeed2((byte)speed);
				}
			} catch (IOException e) {
				log.warning("MOT cannot change speed: " + e.getMessage());
			}
		}

	}
	
}
