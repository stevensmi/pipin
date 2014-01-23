package ttree.scratch;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Logger;

import ttree.pipin.i2c.LEDPWM;
import ttree.pipin.i2c.MD25;
import ttree.pipin.i2c.MD25Motor;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/*
 * Scratch remote for robot control
 */
public class ScratchRobo implements RemoteCallback {

	final static Logger log = Logger.getLogger("ScratchRobo");
	
	ScratchConnection scratchRemote;

	final MD25Motor motors;
	final AtomicReferenceArray<Integer> positionDemand = new AtomicReferenceArray<Integer>(2);
	final LEDPWM leds;
	
	final ExecutorService encoderExecutor = Executors.newSingleThreadExecutor();
	Future<?> encoderTask = null;

	/**
	 * Main for Scratch remote sensor support
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		int address_md25 = -1;
		int address_tcl59116 = -1;
		if (args.length == 2) {
			try {
				address_md25 = Integer.parseInt(args[0], 16);
				address_tcl59116 = Integer.parseInt(args[1], 16);
			}
			catch (NumberFormatException nfe) {
				; // ignore
			}
		}
		if (address_md25 == -1 || address_tcl59116 == -1) {
			log.info("Usage: <MD25 hex address> <TCL59116 hex address> where addresses are decimal and the I2C address on bus 1 of the device");
			return;
		}
		
		log.info("Connecting to scratch remote sensor");
		final ScratchConnection scratchRemote = new ScratchConnection();
		
		// scratch remote parse and sensor output
		final ScratchRobo scratch = new ScratchRobo(address_md25, address_tcl59116);
		scratch.scratchRemote = scratchRemote;

		final ScratchRemoteProtocol command = new ScratchRemoteProtocol();
		for (;;) {
			final String line = scratchRemote.readLine();
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
		
		this.motors = new MD25Motor(device_md25, 1, (byte)MD25.MODE_1);
		this.leds = new LEDPWM(device_tcl59116);
	}
	
	public void broadcast(String text) {

		@SuppressWarnings("resource")
		final Scanner textScanner = new Scanner(text);
		if (textScanner.hasNext() == false) {
			log.warning("empty broadcast");
		}
		
		final String what = textScanner.next();
		switch (what) {
		case "MOT":
			try {
				motors.keepAlive();
			}
			catch (IOException e) {
				log.warning("MOT keep alive: " + e.getMessage());
			}
			break;

		case "ENC":
			final int poll;
			if (textScanner.hasNextInt() == true) {
				 poll = textScanner.nextInt();
					final MD25Encoder md25Encoder = new MD25Encoder(scratchRemote, motors, poll, positionDemand);
					// cancel previous task
					if (encoderTask != null) {
						encoderTask.cancel(true);
					}
					encoderTask = encoderExecutor.submit(md25Encoder);
					log.info("ENC motor encoders are being polled every " + poll + "ms");
			} else {
				if (textScanner.hasNext() && textScanner.next().equals("OFF")) {
					if (encoderTask != null) {
						encoderTask.cancel(true);
						encoderTask = null;
					}
					log.info("ENC motor encoders are OFF");
				}
				else {
					log.warning("ENC ignored, expecting integer poll interval or OFF");
				}
			}
			break;
		default:
			// ignore unknown broadcast
		}
	}

	public void sensor_update(String name, String value) {
		
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

		else if (name.startsWith("MOT") == true) {
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
				log.warning("MOT expecting integer as speed: " + value);
				return;
			}
			
			// saturate speed
			if (speed < -128) {
				speed = 128;
			} else if (speed > 127) {
				speed = 127;
			}
			
			// everything validated - change the motor speed
			try {
				if (motor == 1) {
					motors.setSpeed1((byte)speed);
				}
				else {
					motors.setSpeed2((byte)speed);
				}

				// speed set, disable position demand
				positionDemand.set(motor-1, null);

			}
			catch (IOException e) {
				log.warning("MOT cannot change speed: " + e.getMessage());
			}
		}

		else if (name.startsWith("POS") == true) {
			int motor;
			try {
				motor = Integer.parseInt(name.substring(3));
			}
			catch (NumberFormatException nfe) {
				log.warning("POS number format: " + name);
				return;
			}
			
			if (motor < 1 || motor > 2) {
				log.warning("POS number range 1..2: " + name);
				return;
			}
			
			int position;
			try {
				position = Integer.parseInt(value);
			}
			catch (NumberFormatException nfe) {
				log.warning("MOT expecting integer as position: " + value);
				return;
			}
			
			// everything validated - change the demand position speed
			positionDemand.set(motor-1, position);
		}
	}
	
}
