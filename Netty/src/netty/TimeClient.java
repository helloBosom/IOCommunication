package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Hello world!
 *
 */
public class TimeClient {
	public static void main(String[] args) {
		String host = "127.0.0.1";
		int port = 5000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				port = 8080;
			}
		}
		new TimeClient().connect(port, host);

	}

	public void connect(int port, String host) {
		EventLoopGroup eventLoop = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(eventLoop).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new TimeClientHandler());
						}
					});
			// 发起异步连接操作
			ChannelFuture future = bootstrap.connect(host, port).sync();
			// 等待客户端链路关闭
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			eventLoop.shutdownGracefully();
		}
	}

}
