package ttree.pipin.i2c;

import java.io.IOException;

import com.pi4j.io.i2c.I2CDevice;

/**
 * LED PWM control via TLC59116
 * 
 * @author Michael Stevens
 */
public final class LEDPWM {

	private final I2CDevice device;
	
	/**
	 * Initialise the TLC59116 so all LED outputs are in PWM mode with 0 output
	 * @param device on I2C bus
	 * @throws IOException bus or device error
	 */
	public LEDPWM(I2CDevice device) throws IOException {

		this.device = device;

		for (int led = 0; led != 16; ++led) {
			device.write(TLC59116.REG_PWM_x + led, (byte)0);
		}

		// PWM mode for LED
		final byte PWM_mode = (byte)0xAA;
		for (int out_group = 0; out_group != 4; ++out_group) {
			device.write(TLC59116.REG_LEDOUT_x + out_group, PWM_mode);
		}

		device.write(TLC59116.REG_MODE1, (byte)0x0); // OSC On

	}
	
	/**
	 * Set the PWM output value of a LED
	 * @param led
	 * @param value
	 * @throws IOException device error
	 */
	public void pwm(int led, int value) throws IOException {
		if (led < 0 || led > 15) {
			throw new IllegalArgumentException("led range 0..15 " + led);
		}
		if (value < 0 || value > 255) {
			throw new IllegalArgumentException("value range 0..255 " + value);
		}
		device.write(TLC59116.REG_PWM_x + led, (byte)value);
	}
	
}
