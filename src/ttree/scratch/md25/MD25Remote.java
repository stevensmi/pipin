package ttree.scratch.md25;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Logger;

import ttree.pipin.i2c.MD25;
import ttree.pipin.i2c.MD25Motor;
import ttree.scratch.IncomingMessage;
import ttree.scratch.OutgoingMessage;

/**
 * Remote sensor for MD25 motor controller.
 * 
 * @author Michael Stevens
 */
public class MD25Remote implements IncomingMessage {

	final private Logger log = Logger.getLogger("MD25Remote");

	private final OutgoingMessage messageHandler;
	private final int firstMotor;
	private final MD25Motor md25;
	
	private final AtomicReferenceArray<Integer> positionDemand = new AtomicReferenceArray<>(2);
    private final AtomicInteger watchdog = new AtomicInteger(0);

    private final ExecutorService encoderExecutor = Executors.newSingleThreadExecutor();
    private Future<?> encoderTask = null;


    /**
     * Construct remote sensor.
     * @param messageHandler handler for outgoing messages
     * @param md25 MD25 motor controller
     * @param firstMotor numeric identifier for the first motor e.g. 1 will result in MOT1 and MOT2 being used
     */
	public MD25Remote(OutgoingMessage messageHandler, MD25Motor md25, int firstMotor) {
        this.messageHandler = messageHandler;
		this.md25 = md25;
		this.firstMotor = firstMotor;
	}
	
	/**
	 * Initialise the mode and MD25 parameters.
	 */
	void init() {
        try {
            md25.setup(1, MD25.MODE_1);
            md25.setAccelRate((byte) 10);
        }
        catch (IOException e) {
            log.severe("MD25 not responding on I2C bus: " + e.getMessage());
        }

    }

	@Override
	public void broadcast(String message) {

        try (Scanner textScanner = new Scanner(message)) {
            if (textScanner.hasNext() == false) {
                log.warning("empty broadcast");
            }

            final String what = textScanner.next();
            switch (what) {
                case "MOT":
                    // reset the position control watchdog
                    watchdog.set(0);
                    try {
                        // Keep the MD25 from stopping the motors automatically by reading the 'mode' register
                        md25.readMode();
                    } catch (IOException e) {
                        log.warning("MOT keep alive: " + e.getMessage());
                    }
                    break;

                case "ENC":
                    final int poll;
                    if (textScanner.hasNextInt() == true) {
                        poll = textScanner.nextInt();
                        boolean sensorUpdates = textScanner.hasNext() && textScanner.next().equals("UPDATE");

                        // disable position demand
                        positionDemand.set(0, null);
                        positionDemand.set(1, null);

                        // cancel previous task
                        if (encoderTask != null) {
                            log.info("canceling");
                            encoderTask.cancel(true);
                            log.info("canceled");
                        }

                        // new task
                        final MD25Encoder md25Encoder = new MD25Encoder(poll, watchdog, (sensorUpdates ? messageHandler : null), firstMotor, md25, positionDemand);
                        encoderTask = encoderExecutor.submit(md25Encoder);
                        log.info("ENC motor encoders are being polled every " + poll + "ms, task=" + encoderTask);
                    } else {
                        if (textScanner.hasNext() && textScanner.next().equals("OFF")) {
                            if (encoderTask != null) {
                                boolean canceled = encoderTask.cancel(true);
                                log.info("ENC motor encoders are OFF: " + canceled);
                            }
                        } else {
                            log.warning("ENC ignored, expecting integer poll interval or OFF");
                        }
                    }
                    break;
                default:
                    // ignore unknown broadcast
            }
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

			if (motor < firstMotor || motor > (firstMotor + 1)) {
				log.warning("MOT number range " + firstMotor + ".." + (firstMotor + 1) + ": " + name);
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
				// validate the MD25 has not been reset
				if (md25.readMode() != MD25.MODE_1) {
					throw new IOException("MD25 was reset");
				}
				
				if (motor == 1) {
					md25.setSpeed1((byte) speed);
				}
				else {
					md25.setSpeed2((byte) speed);
				}

				// speed set, disable position demand
				positionDemand.set(motor-1, null);

			}
			catch (IOException e) {
				log.warning("MOT cannot change speed: " + e.getMessage());
			}
			watchdog.set(0);
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
			watchdog.set(0);
		}		
	}

}
