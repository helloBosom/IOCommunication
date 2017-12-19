package tcp.未考虑粘包;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());
	private int counter;
	private byte[] req;

	public TimeClientHandler() {
		req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
	}

	/*
	 * 当客户端和服务器端TCP链接建立成功之后，Netty的NIO线程会调用channelActive方法，发送 查询指令。
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf message = null;
		for (int i = 0; i < 100; i++) {
			message = Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
	}

	/*
	 * 当服务器端返回应答消息时，ChannelRead被调用
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 读取服务端发过来的数据
		ByteBuf buf = (ByteBuf) msg;
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		String message = new String(bytes, "utf-8");
		System.out.println("Now is: " + message + "; the counter is " + ++counter);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.warning("Unexpected exception from downstream" + cause.getMessage());
		ctx.close();
	}
}
