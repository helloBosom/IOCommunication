package nettyProtocloStack;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
	private volatile ScheduledFuture<?> heartBeat;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		NettyMessage message = (NettyMessage) msg;
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
			heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000,
					TimeUnit.MILLISECONDS);

		} else if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
			System.out.println("client recevie server heart beat message : -->" + message);
		} else {
			ctx.fireChannelRead(msg);
		}

	}

	private class HeartBeatTask implements Runnable {
		private final ChannelHandlerContext ctx;

		public HeartBeatTask(final ChannelHandlerContext ctx) {
			this.ctx = ctx;
		}

		public void run() {
			NettyMessage heatBeat = buildHeatBeat();
			System.out.println("client send heart beat message to server : -->" + heatBeat);
			ctx.writeAndFlush(heatBeat);
		}

		private NettyMessage buildHeatBeat() {
			NettyMessage nettyMessage = new NettyMessage();
			Header header = new Header();
			header.setType(MessageType.HEARTBEAT_REQ.value());
			nettyMessage.setHeader(header);
			return nettyMessage;
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (heartBeat != null) {
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.fireExceptionCaught(cause);
	}
}
