package ttree.pipin.i2c;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LEDTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		final I2CBus piExtBus = I2CFactory.getInstance(1);
		
		final I2CDevice ledDevice = piExtBus.getDevice(0x6e);
		
		ledDevice.write(TLC59116.REG_MODE1, (byte)0x0); // OSC On

		// PWM mode for LED
		final byte PWM_mode = (byte)0xAA;
		for (int out_group = 0; out_group != 4; ++out_group) {
			ledDevice.write(TLC59116.REG_LEDOUT_x + out_group, PWM_mode);
		}

		for (int led = 0; led != 4; ++led) {
			ledDevice.write(TLC59116.REG_PWM_x + led, (byte)200);
		}
	}

}
