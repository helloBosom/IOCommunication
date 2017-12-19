package netty;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			ByteBuf in = (ByteBuf) msg;
			byte[] bytes = new byte[in.readableBytes()];
			in.readBytes(bytes);
			String message = new String(bytes, "utf-8");
			System.out.println("the tine server receive order: " + message);
			String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(message)
					? new Date(System.currentTimeMillis()).toString()
					: "BAD ORDER";
			ByteBuf buffer = Unpooled.copiedBuffer(currentTime.getBytes());
			ctx.write(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("exception");
		ctx.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
}
