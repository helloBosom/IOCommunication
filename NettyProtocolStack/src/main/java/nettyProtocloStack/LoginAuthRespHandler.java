package nettyProtocloStack;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

	private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();
	private String[] whiteList = { "127.0.0.1", getAddress() };

	private String getAddress() {
		String localAddress = null;
		try {
			localAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			localAddress = "127.0.0.1";
		}
		return localAddress;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage message = (NettyMessage) msg;
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
			String nodeIndex = ctx.channel().remoteAddress().toString();
			NettyMessage loginResp = null;
			if (nodeCheck.containsKey(nodeIndex)) {
				loginResp = buildResponse((byte) -1);
			} else {
				InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
				String ip = address.getAddress().getHostAddress();
				boolean isOK = false;
				for (String WIP : whiteList) {
					if (WIP.equals(ip)) {
						isOK = true;
						break;
					}
				}

				loginResp = isOK ? buildResponse((byte) 0) : buildResponse((byte) -1);
				if (isOK) {
					nodeCheck.put(nodeIndex, true);
				}
				System.out.println("the login response is : " + loginResp + "body [" + loginResp.getBody() + "]");
				ctx.writeAndFlush(loginResp);
			}
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	private NettyMessage buildResponse(byte result) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_RESP.value());
		message.setHeader(header);
		message.setBody(result);
		return message;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}
}
