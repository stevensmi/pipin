package ttree.pipin.i2c;

import java.io.IOException;

import com.pi4j.io.i2c.I2CDevice;

/**
 * Dual Motor control via MD25
 * 
 * @author Michael Stevens
 */
public final class MD25Motor {

	private final I2CDevice device;
	
	/**
	 * Initialize the MD25 into the given mode 
	 * @param device on I2C bus
	 * @param revision minimum revision of MD25
	 * @param mode
	 * @throws IOException bus or device error
	 */
	public MD25Motor(I2CDevice device, int revision, byte mode) throws IOException {

		this.device = device;
		
		int actualRevision = device.read(MD25.REG_REVISION);
		if (actualRevision <= revision) {
			throw new IllegalStateException("MD25 actual revision: " + actualRevision + " below required revision:" + revision);
		}

		if (mode < 0 || mode > 3) {
			throw new IllegalArgumentException("MD25 mode must be 0..3");
		}
		device.write(MD25.REG_MODE, mode);
	}

	/**
	 * Kepp the MD25 from stopping the motors automatically by reading the 'mode' register
	 */
	public void keepAlive() throws IOException {

		device.read(MD25.REG_MODE);
	}
	
	/**
	 * Set the motor 1 speed value
	 * @param speed
	 * @throws IOException 
	 */
	public void setSpeed1(byte speed) throws IOException {

		device.write(MD25.REG_SPEED1, speed);
	}
	
	/**
	 * Set the motor 2 speed value
	 * @param speed
	 * @throws IOException 
	 */
	public void setSpeed2(byte speed) throws IOException {

		device.write(MD25.REG_SPEED2, speed);
	}
	
}
