package ttree.scratch;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import ttree.scratch.RemoteSensors.OutgoingMessages;
import ttree.scratch.md25.MD25Factory;
import ttree.scratch.protocol.ScratchConnection;
import ttree.scratch.tcl59116.TCL59116Factory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Scratch remote for robot control with a MD25 motor controller and an TCL59116 KED driver
 * 
 * @author Michael Stevens
 */
public class ScratchRobo  {

	final static Logger log = Logger.getLogger("ScratchRobo");

	/**
	 * Main for Scratch remote sensor support
	 */
	public static void main(String[] args) {

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
		final ScratchConnection scratchRemote;
		try {
			scratchRemote = new ScratchConnection();
		}
		catch (IOException e) {
			log.warning("No connection to scratch remote sensor: " + e.getMessage());
			System.exit(1);	// signal no connection to scratch
			return;
		}

		// build remoteSensors from the command line 
		final List<IncomingMessage> remoteSensors = new LinkedList<IncomingMessage>();
		
		// scratch remote parse and sensor output
		try {
			// RPi external I2C bus
			final I2CBus piExtBus = I2CFactory.getInstance(1);

			// Devices
			final I2CDevice device_md25 = piExtBus.getDevice(address_md25);
			final I2CDevice device_tcl59116 = piExtBus.getDevice(address_tcl59116);

			final OutgoingMessages messageHandler = new OutgoingMessages(scratchRemote);
			
			final IncomingMessage md25 = new MD25Factory(1).make(messageHandler, device_md25);
			if (md25 != null) {
				remoteSensors.add(md25);
			}

			final IncomingMessage leds = new TCL59116Factory(1).make(messageHandler, device_tcl59116);
			if (leds != null) {
				remoteSensors.add(leds);
			}

		} catch (IOException e) {
			log.warning("Cannot connect to i2c sensor: " + e.getMessage());
			System.exit(2);	// signal no connection to i2c
			return;
		}
		
		// start handling incoming remote messages
		final RemoteSensors remoteRunner = new RemoteSensors(scratchRemote, remoteSensors);
		remoteRunner.run();
	}

}
