package tcp.以分隔符作为码流结束标识解决半包;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	int counter = 0;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String message = (String) msg;
		System.out.println("this is " + ++counter + "times receive client:[" + message + "]");
		message += "$_";
		ByteBuf buffer = Unpooled.copiedBuffer(message.getBytes());
		ctx.writeAndFlush(buffer);
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
