package nettyProtocloStack;

import java.io.IOException;

import org.jboss.marshalling.ByteOutput;
import org.jboss.marshalling.Marshaller;

import io.netty.buffer.ByteBuf;

public class NettyMarshallingEncoder {

	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	Marshaller marshaller;

	public NettyMarshallingEncoder() throws IOException {
		marshaller = MarshallingCodeCFactory.buildMarshalling();
	}

	protected void encode(Object msg, ByteBuf out) throws IOException {
		try {
			int lengthPos = out.writerIndex();
			out.writeBytes(LENGTH_PLACEHOLDER);
			ByteOutput output =new ChannelBufferByteOutput(out);
			marshaller.start(output);
			marshaller.writeObject(msg);
			marshaller.finish();
			out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
		} finally {
			marshaller.close();
		}
	}

}
