package ttree.scratch;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Scratch remote sensor support over a TCP/IP socket
 * 
 * @author Michael Stevens
 */
public class ScratchConnection {

	public static final int SENSOR_PORT = 42001;
	
	private static Charset charset = Charset.forName("ISO-8859-1");
	
	/**
	 * The socket connected to Scratch
	 */
	private final SocketChannel sc;

	/**
	 * Construct sensor socket, blocking to accept a connection
	 */
	public ScratchConnection() throws IOException {

		sc = SocketChannel.open();
		
		sc.connect(new InetSocketAddress("localhost", SENSOR_PORT));
		sc.configureBlocking(true);
	}
	
	/**
	 * Write a single line to the sensor socket
	 * @param outLine
	 * @throws IOException 
	 */
	public void writeLine(String outLine) throws IOException {
		final ByteBuffer lineData = charset.encode(outLine);
		final int lineDataSize = lineData.capacity();
		final ByteBuffer message = ByteBuffer.allocate(4 + lineDataSize);
		
		message.putInt(lineDataSize);
		message.put(lineData);
		sc.write(message);
	}
	
	/**
	 * Read a single line from the sensor socket
	 * @return a line or null if socket closed
	 * @throws IOException 
	 */
	public String readLine() throws IOException {
		
		final ByteBuffer header = readBytes(4);
		if (header == null) {
			return null;
		}
		
		header.flip();
		int lineLen = header.asIntBuffer().get();
		final ByteBuffer data = readBytes(lineLen);
		if (data == null) {
			sc.close();
			return null;
		}
		
		data.flip();
		final CharBuffer line = charset.decode(data);
		return new StringBuilder(line).toString();
	}
	
	private ByteBuffer readBytes(int num) throws IOException {
		
		final ByteBuffer buf = ByteBuffer.allocate(num);
		while (num > 0) {
			final int bytesRead = sc.read(buf);
			if (bytesRead == -1) {
				sc.close();
				return null;
			}
			num -= bytesRead;
		}
		return buf;
	}
	
}
