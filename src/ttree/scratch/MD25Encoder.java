package ttree.scratch;

import java.io.IOException;

import ttree.pipin.i2c.MD25Motor;

/**
 * Poll the MD25 encoders and send sensor_update
 * 
 * @author Michael Stevens
 */
public class MD25Encoder implements Runnable {

	private final ScratchConnection scratch;
	private final MD25Motor md25Motor;
	private final int pollMillis;
	
	private final ScratchRemoteProtocol remoteProtocol = new ScratchRemoteProtocol();
	
	/**
	 * Construct with polling delay
	 * @param scratch
	 * @param md25Motor
	 * @param poleMillis
	 */
	public MD25Encoder(ScratchConnection scratch, MD25Motor md25Motor, int pollMillis) {
		this.scratch = scratch;
		this.md25Motor = md25Motor;
		this.pollMillis = pollMillis;
	}

	@Override
	public void run() {
		while (true) {
			
			try {
				final int encoder1 = md25Motor.encoder1();
				final int encoder2 = md25Motor.encoder2();
				
				final String updateLine = remoteProtocol.generateSensorUpdate("encoder1", String.valueOf(encoder1), "encoder2", String.valueOf(encoder2));
				System.out.println(updateLine);
				scratch.writeLine(updateLine);

				Thread.sleep(pollMillis);
			} catch (InterruptedException | IOException e) {
				; // stop execution
			}
		}
	}

}
