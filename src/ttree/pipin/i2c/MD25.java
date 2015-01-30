package ttree.pipin.i2c;

/**
 * I2C Register definition for the MD25 board.
 * 
 * The MD25 is a I2C interfaced dual DC motor control board.
 * Supplier: Robot Electronics (Daventech) UK
 * Specification: 12v 2.8A Dual H-Bridge
 *
 * @author Michael Stevens
 */
public class MD25 {
	
	/* REGISTERS */
	public static final int REG_SPEED1				= 0x00;	// R/W  | Motor1 speed (mode 0,1) or speed (mode 2,3)
	public static final int REG_SPEED2				= 0x01;	// R/W  | Motor2 speed (mode 0,1) or turn (mode 2,3)
	public static final int REG_ENC1A				= 0x02;	// R    | Encoder 1 position, 1st byte (highest), capture count when read
	public static final int REG_ENC1B				= 0x03;	// R    | Encoder 1 position, 2nd byte
	public static final int REG_ENC1C				= 0x04;	// R    | Encoder 1 position, 3rd byte
	public static final int REG_ENC1D				= 0x05;	// R    | Encoder 1 position, 4th (lowest byte)
	public static final int REG_ENC2A				= 0x06;	// R    | Encoder 2 position, 1st  byte (highest), capture count when read
	public static final int REG_ENC2B				= 0x07;	// R    | Encoder 2 position, 2nd byte
	public static final int REG_ENC2C				= 0x08;	// R    | Encoder 2 position, 3rd byte
	public static final int REG_ENC2D				= 0x09;	// R    | Encoder 2 position, 4th byte (lowest byte)
	public static final int REG_BATTERY_VOLTS		= 0x0A;	// R    | The supply battery voltage
	public static final int REG_MOTOR1_CURRENT		= 0x0B;	// R    | The current through motor 1
	public static final int REG_MOTOR2_CURRENT		= 0x0C;	// R    | The current through motor 2
	public static final int REG_SOFTWARE_REVISION	= 0x0D;	// R    | Software Revision Number
	public static final int REG_ACCELERATION_RATE	= 0x0E;	// R/W  | Optional Acceleration register
	public static final int REG_MODE				= 0x0F;	// R/W  | Mode of operation (see below)
	public static final int REG_COMMAND				= 0x10;	// R/W  | Used for reset of encoder counts and module address changes

	/* MODES */
	public static final byte MODE_0					= 0x00;	// The meaning of the speed registers is literal speeds in the range of 0 (Full Reverse), 128 (Stop), 255 (Full Forward) (Default Setting).
	public static final byte MODE_1					= 0x01;	// The meaning of the speed registers is literal speeds in the range of -128 (Full Reverse), 0 (Stop), 127 (Full Forward).
	public static final byte MODE_2					= 0x02;	// Speed1 control both motors speed, and speed2 becomes the turn value. Data is in the range of 0 (Full Reverse), 128 (Stop), 255 (Full  Forward).
	public static final byte MODE_3					= 0x03;	// Speed1 control both motors speed, and speed2 becomes the turn value. Data is in the range of -128 (Full Reverse), 0 (Stop), 127 (Full Forward).
	 
	/* COMMANDS */
	public static final byte CMD_ENCODER_RESET		= 0x20;	// Resets the encoder registers to zero
	public static final byte CMD_AUTO_SPEED_DISABLE	= 0x30; // Disables automatic speed regulation
	public static final byte CMD_AUTO_SPEED_ENABLE	= 0x31;	// Enables automatic speed regulation (default)
	public static final byte CMD_TIMEOUT_DISABLE	= 0x32;	// Disables 2 second timeout of motors (Version 2 onwards only)
	public static final byte CMD_TIMEOUT_ENABLE		= 0x33;	// Enables 2 second timeout of motors when no I2C communications (default) (Version 2 onwards only)
	public static final byte CMD_CHANGE_I2C_ADDR_1	= (byte)0xA0;	// 1st in sequence to change I2C address
	public static final byte CMD_CHANGE_I2C_ADDR_2	= (byte)0xAA;	// 2nd in sequence to change I2C address
	public static final byte CMD_CHANGE_I2C_ADDR_3	= (byte)0xA5;	// 3rd in sequence to change I2C address
	 
}
