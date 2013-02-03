package ttree.pipin.i2c;

/**
 * I2C Register definition for the TLC59116 Device
 * 
 * The TLC59116 is a I2C interfaced integrated driver for 16 low current LEDs from TI
 *
 * @author Michael Stevens
 */
public class TLC59116 {
	
	public static final int REG_MODE1 = 0x00;
	public static final int REG_MODE2 = 0x01;
	public static final int REG_PWM_x = 0x02;
	public static final int REG_LEDOUT_x = 0x14;
	
	public static final int LEDS = 16;

}
