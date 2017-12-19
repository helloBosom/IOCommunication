package udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class ChineseProverbServer {

	public void run(int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
					.handler(new ChineseProverbServerHandler());
			bootstrap.bind(port).sync().channel().closeFuture().await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		int port = 5000;
		if (args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		new ChineseProverbServer().run(port);
	}
}
