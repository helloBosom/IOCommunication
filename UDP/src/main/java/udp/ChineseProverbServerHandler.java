package udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

public class ChineseProverbServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	private static final String[] DICTIONARY = { "hello ", "world ", "welcome ", "linux ", "unix ", "i am coming" };

	// netty对UDP进行封装，接收到的是netty封装后的io.netty.channel.socket.DatagramPacket
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		// bytebuf的toString（charset）方法
		String req = msg.content().toString(CharsetUtil.UTF_8);
		System.out.println(req);
		if ("hello?".equals(req)) {
			ctx.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer("hello:" + nextQuote(), CharsetUtil.UTF_8), msg.sender()));
		}
	}

	private String nextQuote() {
		int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
		return DICTIONARY[quoteId];
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		cause.printStackTrace();
	}
}
