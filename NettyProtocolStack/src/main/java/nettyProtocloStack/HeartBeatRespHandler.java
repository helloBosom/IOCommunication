package nettyProtocloStack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage message = (NettyMessage) msg;
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
			System.out.println("receive client heart beat message -->" + message);
			NettyMessage heartBeat = buildHeatBeat();
			System.out.println("send heart beat response message to client : -->" + heartBeat);
			ctx.writeAndFlush(heartBeat);
		} else {
			ctx.fireChannelRead(msg);
		}

	}

	private NettyMessage buildHeatBeat() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.HEARTBEAT_RESP.value());
		message.setHeader(header);
		return message;
	}
}
