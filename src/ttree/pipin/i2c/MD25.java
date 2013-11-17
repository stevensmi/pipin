package ttree.pipin.i2c;

/**
 * I2C Register definition for the MD25 board
 * 
 * The MD25 is a I2C interfaced dual DC motor control board
 * Supplier: Robot Electonics UK
 * Specification: 12v 2.8A Dual H-Bridge
 *
 * @author Michael Stevens
 */
public class MD25 {
	
	public static final int REG_SPEED1 = 0x00;
	public static final int REG_SPEED2 = 0x01;
	public static final int REG_ENC1A = 0x02;
	public static final int REG_ENC1B = 0x03;
	public static final int REG_ENC1C = 0x04;
	public static final int REG_ENC1D = 0x05;
	public static final int REG_ENC2A = 0x06;
	public static final int REG_ENC2B = 0x07;
	public static final int REG_ENC2C = 0x08;
	public static final int REG_ENC2D = 0x09;
	public static final int REG_BAT_VOLTS = 0x0A;
	public static final int REG_CURRENT1 = 0x0B;
	public static final int REG_CURRENT2 = 0x0C;
	public static final int REG_REVISION = 0x0D;
	public static final int REG_ACC_RATE = 0x0E;
	public static final int REG_MODE = 0x0F;
	public static final int REG_COMMAND = 0x10;

}
