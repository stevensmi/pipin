package ttree.scratch.md25;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Logger;

import ttree.pipin.i2c.MD25Motor;
import ttree.scratch.IncomingMessage;
import ttree.scratch.OutgoingMessage;

/**
 * Remote sensor for MD25 motor controller
 * 
 * @author Michael Stevens
 */
public class MD25Remote implements IncomingMessage {

	final static Logger log = Logger.getLogger("MD25Remote");

	final OutgoingMessage messageHandler;
	final MD25Motor motors;
	
	final AtomicReferenceArray<Integer> positionDemand = new AtomicReferenceArray<Integer>(2);
	
	final ExecutorService encoderExecutor = Executors.newSingleThreadExecutor();
	Future<?> encoderTask = null;


	public MD25Remote(OutgoingMessage messageHandler, MD25Motor motors) {
		this.messageHandler = messageHandler;
		this.motors = motors;
	}

	@Override
	public void broadcast(String message) {
		
		@SuppressWarnings("resource")
		final Scanner textScanner = new Scanner(message);
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
				boolean sensorUpdates = textScanner.hasNext() && textScanner.next().equals("UPDATE");
				final MD25Encoder md25Encoder = new MD25Encoder((sensorUpdates ? messageHandler : null), motors, poll, positionDemand);
				// cancel previous task
				if (encoderTask != null) {
					encoderTask.cancel(true);
				}
				encoderTask = encoderExecutor.submit(md25Encoder);
				log.info("ENC motor encoders are being polled every " + poll + "ms");
			} else {
				if (textScanner.hasNext() && textScanner.next().equals("OFF")) {
					if (encoderTask != null) {
						boolean canceled = encoderTask.cancel(true);
						log.info("ENC motor encoders are OFF: " + canceled);
					}
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

	@Override
	public void sensorUpdate(String name, String value) {

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
