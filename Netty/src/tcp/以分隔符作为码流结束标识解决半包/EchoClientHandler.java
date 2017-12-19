package tcp.以分隔符作为码流结束标识解决半包;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

	static final String ECHO_REQ = "welcome.$_";
	private int counter;

	public EchoClientHandler() {
	}

	/*
	 * 当客户端和服务器端TCP链接建立成功之后，Netty的NIO线程会调用channelActive方法，发送 查询指令。
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for (int i = 0; i < 10; i++) {
			ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
		}
	}

	/*
	 * 当服务器端返回应答消息时，ChannelRead被调用
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("this is: " + ++counter + "times receive server:[" + msg + "]");
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
