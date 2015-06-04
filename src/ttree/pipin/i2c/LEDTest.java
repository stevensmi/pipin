package ttree.pipin.i2c;

import java.io.IOException;
import java.util.Arrays;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Test LEDS driven by TLC59116 attached to I2C bus.
 * 
 * @author Michael Stevens
 */
public class LEDTest {

	/**
	 * Main for LED Test.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("Testing LEDs 0..15");
		final byte leds[] = new byte[16];
		
		// RPi external I2C bus
		final I2CBus piExtBus = I2CFactory.getInstance(1);
		// Default address for ELC LED board
		final I2CDevice ledDevice = piExtBus.getDevice(0x6F);
		
		final LEDPWM ledpwm = new LEDPWM(ledDevice);
		ledpwm.setup();

		int pwm = 0;
		int led = 0;
		boolean up = true;
		while (Thread.currentThread().isInterrupted() == false)  {
			if (up == true) {
				// maximum
				if (pwm == 255) {
					// stay on maximum and change direction
					up = false;
				}
				else {
					++pwm;
				}
			}
			else {
				// minimum
				if (pwm == 0) {
					// stay on minimum and change direction
					up = true;
				}
				else {
					--pwm;
				}
			}
			leds[led] = (byte)pwm;
			led = (led + 1) % leds.length;

			// output pwm to all active leds
			ledpwm.pwmAll(leds);
		}
		
		Arrays.fill(leds, (byte)0);
		ledpwm.pwmAll(leds);
	}

}
