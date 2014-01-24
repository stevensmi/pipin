package ttree.scratch;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import ttree.scratch.md25.MD25Factory;
import ttree.scratch.protocol.ScratchConnection;
import ttree.scratch.protocol.ScratchRemoteProtocol;
import ttree.scratch.tcl59116.TCL59116Factory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/*
 * Scratch remote for robot control
 */
public class ScratchRobo implements IncomingMessage {

	final static Logger log = Logger.getLogger("ScratchRobo");

	private ScratchConnection scratchRemote;
	
	final private List<IncomingMessage> remoteSensors = new LinkedList<IncomingMessage>();
	
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

		// scratch remote parse and sensor output
		final ScratchRobo self;
		try {
			self = new ScratchRobo(address_md25, address_tcl59116);
		} catch (IOException e) {
			log.warning("Cannot connect to i2c sensor: " + e.getMessage());
			System.exit(2);	// signal no connection to i2c
			return;
		}
		self.scratchRemote = scratchRemote;

		final ScratchRemoteProtocol command = new ScratchRemoteProtocol();
		for (;;) {
			String line;
			try {
				line = scratchRemote.readLine();
			} catch (IOException e) {
				log.warning("Connection to scratch remote sensor lost: " + e.getMessage());
				break;
			}
			if (line == null) {
				break;
			}

			command.parse(line, self);
		}
		log.info("scratch finished");
	}

	public ScratchRobo(int address_md25, int address_tcl59116) throws IOException {
		// RPi external I2C bus
		final I2CBus piExtBus = I2CFactory.getInstance(1);

		// Devices
		final I2CDevice device_md25 = piExtBus.getDevice(address_md25);
		final I2CDevice device_tcl59116 = piExtBus.getDevice(address_tcl59116);

		final OutgoingMessages messageHandler = new OutgoingMessages();
		
		final IncomingMessage md25 = new MD25Factory().make(messageHandler, device_md25);
		remoteSensors.add(md25);

		final IncomingMessage leds = new TCL59116Factory().make(messageHandler, device_tcl59116);
		remoteSensors.add(leds);
	}

	@Override
	public void broadcast(String message) {
		
		// simply let all the remote sensors handle the broadcast in turn
		for (IncomingMessage remote : remoteSensors) {
			remote.broadcast(message);
		}
	}

	@Override
	public void sensorUpdate(String name, String value) {

		// simply let all the remote sensors handle the sensor update in turn
		for (IncomingMessage remote : remoteSensors) {
			remote.sensorUpdate(name, value);
		}
	}

	class OutgoingMessages implements OutgoingMessage {

		final ScratchRemoteProtocol remoteProtocol = new ScratchRemoteProtocol();
		
		@Override
		public synchronized void broadcast(String message) {
			final String broadcastLine = remoteProtocol.generateBroadcast(message);
			try {
				scratchRemote.writeLine(broadcastLine);
			} catch (IOException e) {
				log.warning("Scratch remote not recieving messages");
			}
			
		}

		@Override
		public synchronized void sensorUpdate(String... updates) {
			final String updateLine = remoteProtocol.generateSensorUpdate(updates);
			try {
				scratchRemote.writeLine(updateLine);
			} catch (IOException e) {
				log.warning("Scratch remote not recieving messages");
			}
		}
		
	}


}
