package nettyProtocloStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

	NettyMarshallingDecoder marshangllingDecoder;

	public NettyMessageDecoder(int maxFrameLength, int lengthFiledOffset, int LengthFieldLength) throws IOException {
		super(maxFrameLength, lengthFiledOffset, LengthFieldLength);
		marshangllingDecoder = new NettyMarshallingDecoder();
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setCrcCode(in.readInt());
		header.setLength(in.readInt());
		header.setSessionID(in.readLong());
		header.setType(in.readByte());
		header.setPriority(in.readByte());
		int size = in.readInt();
		if (size > 0) {
			Map<String, Object> attch = new HashMap<String, Object>(size);
			int keySize = 0;
			byte[] keyArray = null;
			String key = null;
			for (int i = 0; i < size; i++) {
				keySize = in.readInt();
				keyArray = new byte[keySize];
				in.readBytes(keyArray);
				key = new String(keyArray, "utf-8");
				attch.put(key, marshangllingDecoder.decode(in));
			}
			keyArray = null;
			header.setAttachment(attch);
		}
		if (in.readableBytes() > 4) {
			message.setBody(marshangllingDecoder.decode(in));
		}
		message.setHeader(header);
		return message;
	}

}
