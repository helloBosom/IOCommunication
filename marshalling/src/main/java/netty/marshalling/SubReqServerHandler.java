package netty.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class SubReqServerHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
		if ("welcome".equalsIgnoreCase(req.getUserName())) {
			System.out.println("service accept client subscribe req : [" + req.toString() + "]");
		}
		ctx.writeAndFlush(resp(req.getSubReqId()));
	}

	private SubscribeRespProto.SubscribeResp resp(int subReqId) {
		SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
		builder.setSubReqId(subReqId);
		builder.setRespCode(0);
		builder.setDesc("netty book order succeed,3 days later,sent to the designated address");
		return builder.build();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
