package netty_serializable;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		SubscribeReq req = (SubscribeReq) msg;
		if ("welcome".equalsIgnoreCase(req.getUserName())) {
			System.out.println("service accept client subscribe req : [" + req.toString() + "]");
			ctx.writeAndFlush(resp(req.getSubReqId()));
		}
	}

	private SubscribeResp resp(int subReqId) {
		SubscribeResp resp = new SubscribeResp();
		resp.setSubReqId(subReqId);
		resp.setRespCode(0);
		resp.setDesc("Netty work order succeed , 3 day later, sent to the designated address");
		return resp;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
