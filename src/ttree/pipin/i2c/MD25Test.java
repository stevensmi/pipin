package ttree.pipin.i2c;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Test MD 25 device attached to I2C bus
 * 
 * @author michael
 */
public class MD25Test {

	/**
	 * Main for Motor Test
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		// RPi external I2C bus
		final I2CBus piExtBus = I2CFactory.getInstance(1);
		// Default address for MD25 board
		final I2CDevice md25 = piExtBus.getDevice(0x58);
	
		int revision = md25.read(MD25.REG_REVISION);
		int mode = md25.read(MD25.REG_MODE);
		
		System.out.println("MD25 revision" + revision + " in mode " + mode);
	}
	
}
