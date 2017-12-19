package netty.marshalling;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SubReqClient {
	public static void main(String[] args) {
		int port = 5000;
		String host = "127.0.0.1";
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				port = 5000;
			}
		}
		new SubReqClient().connect(host, port);
	}

	public void connect(String host, int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
							ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
							ch.pipeline().addLast(new SubReqClientHandler());
						}
					});
			ChannelFuture future = bootstrap.connect(host, port).sync();
			future.channel().closeFuture().sync();
		} catch (Exception e) {

		} finally {
			group.shutdownGracefully();
		}
	}
}
