package tcp.以分隔符作为码流结束标识解决半包;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

class EchoServer {

	public void bind(int port) {
		// NioEventLoopGroup是个线程组，包含一组NIO线程
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			// Netty用于启动NIO服务器端辅助启动类
			ServerBootstrap bootstrap = new ServerBootstrap();
			// NioServerSockketChannel -> JDK NIO类库中端ServerSocketChannel
			// option配置TCP参数 ,backlog为100
			// ChildHandler绑定事件处理类，ChildChannelHandler主要用于处理I/O事件
			bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
							ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
							ch.pipeline().addLast(new StringDecoder());
							ch.pipeline().addLast(new EchoServerHandler());
						}
					});
			// 绑定端口 sync同步阻塞方法，等待绑定操作完成 完成后netty会返回一个ChannFuture
			ChannelFuture future = bootstrap.bind(port).sync();
			// 等待服务端监听端口关闭
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		int port = 5000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				port = 5000;
			}
		}
		new EchoServer().bind(port);
	}
}
