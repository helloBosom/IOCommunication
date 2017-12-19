package netty_file;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class FileClient {
	EventLoopGroup group = new NioEventLoopGroup();

	public void connect(int port, String host) throws InterruptedException {
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("FileClientHandler", new FileClientHandler());
						}
					});
			ChannelFuture future = bootstrap.connect(host, port).sync();
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new FileClient().connect(5000, "10.10.174.254");
	}

}
