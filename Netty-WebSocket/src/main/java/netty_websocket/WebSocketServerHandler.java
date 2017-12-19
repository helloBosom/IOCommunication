package netty_websocket;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

	private static final Logger LOGGER = Logger.getLogger(WebSocketServerHandler.class.getName());

	private WebSocketServerHandshaker handshaker;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 传统的HTTP接入
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, (FullHttpRequest) msg);
			System.out.println("fullHttpRequest");
		}
		// WebSocket接入
		else if (msg instanceof WebSocketFrame) {
			System.out.println("webSocketFrame");
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		if (frame instanceof CloseWebSocketFrame) {
			System.out.println("close");
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		if (frame instanceof PingWebSocketFrame) {
			System.out.println("write");
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if (!(frame instanceof TextWebSocketFrame)) {
			System.out.println("text");
			throw new UnsupportedOperationException(
					String.format("%s frame types not supported", frame.getClass().getName()));
		}

		String request = ((TextWebSocketFrame) frame).text();
		System.out.println("welcome");
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine(String.format("%s received %s", ctx.channel(), request));
		}
		ctx.channel().write(new TextWebSocketFrame(request + ",欢迎使用Netty WebSocket服务，现在时刻：" + new Date().toString()));
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
		System.out.println("upgrade");
		System.out.println(req.headers().get("Upgrade"));
		if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, req,
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		// 构造握手响应返回，本机测试
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				"ws://localhost:5000/websocket", null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			System.out.println("handshaker is null");
			// 新版netty：sendUnsupportedVersionResponse类取代sendUnsupportedWebSocketVersionResponse
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			System.out.println("future");
			handshaker.handshake(ctx.channel(), req);
		}
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse resp) {
		if (resp.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(resp.status().toString(), CharsetUtil.UTF_8);
			resp.content().writeBytes(buf);
			buf.release();
			HttpUtil.setContentLength(resp, resp.content().readableBytes());
		}
		ChannelFuture future = ctx.channel().writeAndFlush(resp);
		if (!HttpUtil.isKeepAlive(req) || resp.status().code() != 200) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
