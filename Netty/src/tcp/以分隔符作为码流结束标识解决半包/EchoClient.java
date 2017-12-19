package tcp.以分隔符作为码流结束标识解决半包;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Hello world!
 *
 */
public class EchoClient {
	public static void main(String[] args) {
		String host = "192.168.31.1";
		int port = 9000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				port = 8000;
			}
		}
		new EchoClient().connect(port, host);
	}

	public void connect(int port, String host) {
		EventLoopGroup eventLoop = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(eventLoop).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ByteBuf delimter = Unpooled.copiedBuffer("$_".getBytes());
							ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimter));
							ch.pipeline().addLast(new StringDecoder());
							ch.pipeline().addLast(new EchoClientHandler());
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
