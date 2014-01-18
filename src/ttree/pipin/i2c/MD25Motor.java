package ttree.pipin.i2c;

import java.io.IOException;

import com.pi4j.io.i2c.I2CDevice;
import static ttree.pipin.i2c.MD25.*;

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
		
		int actualRevision = device.read(REG_SOFTWARE_REVISION);
		if (actualRevision <= revision) {
			throw new IllegalStateException("MD25 actual revision: " + actualRevision + " below required revision:" + revision);
		}

		if (mode < 0 || mode > 3) {
			throw new IllegalArgumentException("MD25 mode must be 0..3");
		}
		device.write(REG_MODE, mode);
	}

	/**
	 * Kepp the MD25 from stopping the motors automatically by reading the 'mode' register
	 */
	public void keepAlive() throws IOException {

		device.read(REG_MODE);
	}
	
	/**
	 * Set the motor 1 speed value
	 * @param speed
	 * @throws IOException 
	 */
	public void setSpeed1(byte speed) throws IOException {

		device.write(REG_SPEED1, speed);
	}
	
	/**
	 * Set the motor 2 speed value
	 * @param speed
	 * @throws IOException 
	 */
	public void setSpeed2(byte speed) throws IOException {

		device.write(REG_SPEED2, speed);
	}
	
	/**
	 * Encoder for the motor 1
	 * @return encoder count
	 * @throws IOException
	 */
	public int encoder1() throws IOException {
		int enc1a = device.read(REG_ENC1A);	// capture encoder
		int enc1b = device.read(REG_ENC1B);
		int enc1c = device.read(REG_ENC1C);
		int enc1d = device.read(REG_ENC1D);
		return ((enc1a << 8 | enc1b) << 8 | enc1c) << 8 | enc1d;
	}

	/**
	 * Encoder for the motor 2
	 * @return encoder count
	 * @throws IOException
	 */
	public int encoder2() throws IOException {
		int enc2a = device.read(REG_ENC2A);	// capture encoder
		int enc2b = device.read(REG_ENC2B);
		int enc2c = device.read(REG_ENC2C);
		int enc2d = device.read(REG_ENC2D);
		return ((enc2a << 8 | enc2b) << 8 | enc2c) << 8 | enc2d;
	}

}
