package netty;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());
	private final ByteBuf bytebuf;

	public TimeClientHandler(String str) {
		byte[] bytes = str.getBytes();
		bytebuf = Unpooled.buffer(bytes.length);
		// 写入buffer
		bytebuf.writeBytes(bytes);
		System.out.println("wirte success");
	}

	public TimeClientHandler() {
		byte[] req = "QUERY TIME ORDER".getBytes();
		bytebuf = Unpooled.buffer();
		bytebuf.writeBytes(req);
	}

	/*
	 * 当客户端和服务器端TCP链接建立成功之后，Netty的NIO线程会调用channelActive方法，发送 查询指令。
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 向服务端发送数据
		ctx.writeAndFlush(bytebuf);
		System.out.println("send success");
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
		System.out.println("Now is: " + message);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.warning("Unexpected exception from downstream" + cause.getMessage());
		ctx.close();
	}
}
