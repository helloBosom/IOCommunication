package nettyProtocloStack;

import java.io.IOException;

import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import io.netty.buffer.ByteBuf;

public class NettyMarshallingDecoder {

	private final Unmarshaller unmarshaller;

	public NettyMarshallingDecoder() throws IOException {
		unmarshaller = MarshallingCodeCFactory.buildUnMarshalling();
	}

	protected Object decode(ByteBuf in) throws IOException, ClassNotFoundException {
		int objectSize = in.readInt();
		ByteBuf buf = in.slice(in.readerIndex(), objectSize);
		ByteInput input = new ChannelBufferByteInput(buf);
		try {
			unmarshaller.start(input);
			Object object = unmarshaller.readObject();
			unmarshaller.finish();
			in.readerIndex(in.readerIndex() + objectSize);
			return object;
		} finally {
			unmarshaller.close();
		}
	}

}
