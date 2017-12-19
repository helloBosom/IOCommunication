package nettyProtocloStack;

import java.io.IOException;

import org.jboss.marshalling.ByteOutput;

import io.netty.buffer.ByteBuf;

public class ChannelBufferByteOutput implements ByteOutput {

	private final ByteBuf buffer;

	/**
	 * Create a new instance which use the given {@link ByteBuf}
	 */
	ChannelBufferByteOutput(ByteBuf buffer) {
		this.buffer = buffer;
	}

	public void close() throws IOException {
		// Nothing to do
	}

	public void flush() throws IOException {
		// nothing to do
	}

	public void write(int b) throws IOException {
		buffer.writeByte(b);
	}

	public void write(byte[] bytes) throws IOException {
		buffer.writeBytes(bytes);
	}

	public void write(byte[] bytes, int srcIndex, int length) throws IOException {
		buffer.writeBytes(bytes, srcIndex, length);
	}

	/**
	 * Return the {@link ByteBuf} which contains the written content
	 *
	 */
	ByteBuf getBuffer() {
		return buffer;
	}
}
