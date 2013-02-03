package ttree.pipin.i2c;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LEDTest {

	/**
	 * Main for LED Test
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("START");
		final int active_leds = 4;
		
		final I2CBus piExtBus = I2CFactory.getInstance(1);
		
		final I2CDevice ledDevice = piExtBus.getDevice(0x6e);
		
		ledDevice.write(TLC59116.REG_MODE1, (byte)0x0); // OSC On

		// PWM mode for LED
		final byte PWM_mode = (byte)0xAA;
		for (int out_group = 0; out_group != 4; ++out_group) {
			ledDevice.write(TLC59116.REG_LEDOUT_x + out_group, PWM_mode);
		}

		int pwm = 0;
		boolean up = true;
		for (;;) {
			if (up == true) {
				// maximum
				if (pwm == 255) {
					// stay on maximum and change direction
					up = !up;
				}
				else {
					++pwm;
				}
			}
			else {
				// minimum
				if (pwm == 0) {
					// stay on minimum and change direction
					up = !up;
				}
				else {
					--pwm;
				}
			}

			// output pwm to all active leds
			for (int led = 0; led != active_leds; ++led) {
				ledDevice.write(TLC59116.REG_PWM_x + led, (byte)pwm);
			}
		}
	}

}
