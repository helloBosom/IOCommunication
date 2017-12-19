package netty_websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
	public void run(int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("http-codec", new HttpServerCodec());
							ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
							ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
							ch.pipeline().addLast("handler", new WebSocketServerHandler());
						}
					});
			Channel channel = bootstrap.bind(port).sync().channel();
			System.out.println("web socket server started at port " + port);
			System.out.println("open your browser and navigate to http://localhost:" + port + '/');
			channel.closeFuture().sync();
		} catch (Exception e) {

		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}

	}

	public static void main(String[] args) {
		int port = 5000;
		if (args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		new WebSocketServer().run(port);
	}

}
