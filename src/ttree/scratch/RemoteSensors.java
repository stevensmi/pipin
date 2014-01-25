package ttree.scratch;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import ttree.scratch.protocol.ScratchConnection;
import ttree.scratch.protocol.ScratchRemoteProtocol;

/**
 * Scratch remote sensors
 *
 * Provider support for multiple remote sensors with a single scratch connection
 * 
 * @author Michael Stevens
 */
public class RemoteSensors implements IncomingMessage, Runnable {

	final static Logger log = Logger.getLogger("RemoteSensors");

	final private ScratchConnection scratchRemote;
	final private List<IncomingMessage> remoteSensors;

	/**
	 * Construct with list of remote sensors to use with scratch
	 * @param scratchRemote
	 * @param remoteSensors remote sensors for incoming messages
	 * @throws IOException
	 */
	public RemoteSensors(ScratchConnection scratchRemote, List<IncomingMessage> remoteSensors) {
		
		this.scratchRemote = scratchRemote;
		this.remoteSensors = remoteSensors;
	}
	
	/**
	 * Read incoming messages from scratch and pass it on to the remote sensors
	 */
	@Override
	public void run() {
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
	
			command.parse(line, this);
		}
		log.info("scratch finished");
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

	/**
	 * Provide support for the outgoing messages 
	 *
	 */
	public static class OutgoingMessages implements OutgoingMessage {

		private final ScratchConnection scratchRemote;
		
		public OutgoingMessages(ScratchConnection scratchRemote) {
			this.scratchRemote = scratchRemote;
		}

		final ScratchRemoteProtocol remoteProtocol = new ScratchRemoteProtocol();
		
		@Override
		public synchronized void broadcast(String message) {
			
			final String broadcastLine = remoteProtocol.generateBroadcast(message);
			RemoteSensors.log.info(broadcastLine);
			try {
				scratchRemote.writeLine(broadcastLine);
			} catch (IOException e) {
				log.warning("Scratch remote not recieving messages");
			}
			
		}

		@Override
		public synchronized void sensorUpdate(String... updates) {
			
			final String updateLine = remoteProtocol.generateSensorUpdate(updates);
			RemoteSensors.log.info(updateLine);
			try {
				scratchRemote.writeLine(updateLine);
			} catch (IOException e) {
				log.warning("Scratch remote not recieving messages");
			}
		}
		
	}


}
