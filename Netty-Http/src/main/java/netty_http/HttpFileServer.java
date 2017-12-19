package netty_http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

	private final static String DEFAULT_URL = "/src/netty/";

	public static void main(String[] args) throws Exception {
		int port = 5000;
		String url = DEFAULT_URL;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				port = 5000;
			}
		}
		if (args.length > 1) {
			url = args[1];
		}
		new HttpFileServer().run(port, url);
	}

	public void run(final int port, final String url) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(workGroup, bossGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.class)).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							/*
							 * 将多个消息转换为单一的FullHttpRequest或者FullHttpResponse
							 * 原因是HTTP解码器
							 */
							ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
							ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
							ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
							/*
							 * 支持异步发送大的码流，但不占用过多的内存，防止Java内存溢出
							 */
							ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
							ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
						}
					});
			ChannelFuture future = bootstrap.bind("127.0.0.1", port).sync();
			System.out.println("http 文件目录服务器启动，网址是：" + "http://192.168.31.199:" + port + url);
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}

	}
}
