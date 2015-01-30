package ttree.pipin.i2c;

import static ttree.pipin.i2c.MD25.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.pi4j.io.i2c.I2CDevice;

/**
 * Dual Motor control via MD25.
 * 
 * I2C reads and writes are synchronized to allow thread safe access.
 * 
 * @author Michael Stevens
 */
public final class MD25Motor {

	private final I2CDevice device;
	
	/**
	 * Construct form I2C device..
	 * @param device on I2C bus
	 */
	public MD25Motor(I2CDevice device) {

		this.device = device;
	}

    /**
     * Setup the MD25 into the given mode.
     * @param revision minimum revision of MD25
     * @param mode MD25 mode
     * @throws IOException bus or device error
     */
    public synchronized void setup(int revision, byte mode) throws IOException {

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
	 * Read the mode register.
	 * @return the byte value of the mode register
	 * @throws IOException
	 */
	public synchronized	byte readMode() throws IOException {
		
		return (byte)device.read(REG_MODE);
	}
	
	/**
	 * Set the motor 1 speed value.
	 * @param speed speed value
	 * @throws IOException 
	 */
	public synchronized void setSpeed1(byte speed) throws IOException {

		device.write(REG_SPEED1, speed);
	}
	
	/**
	 * Set the motor 2 speed value.
	 * @param speed speed value
	 * @throws IOException 
	 */
	public synchronized void setSpeed2(byte speed) throws IOException {

		device.write(REG_SPEED2, speed);
	}
	
	/**
	 * Set the maximum accelerationRate.
	 * @param accelerationRate acceleration rate value
	 * @throws IOException 
	 */
	public synchronized void setAccelRate(byte accelerationRate) throws IOException {

		device.write(REG_ACCELERATION_RATE, accelerationRate);
	}
	
	/**
	 * Encoder for the motor 1.
	 * @return encoder count
	 * @throws IOException
	 */
	public synchronized int encoder1() throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(4);
		device.read(REG_ENC1A, buffer.array(), 0, 4);	// capture encoder
		buffer.position(4);
		buffer.flip();
		return buffer.getInt();
	}

	/**
	 * Encoder for the motor 2.
	 * @return encoder count
	 * @throws IOException
	 */
	public synchronized int encoder2() throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(4);
		device.read(REG_ENC2A, buffer.array(), 0, 4);	// capture encoder
		buffer.position(4);
		buffer.flip();
		return buffer.getInt();
	}

}
