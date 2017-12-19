package tcp.按行读取解决半包;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

class TimeServer {

	public void bind(int port) {
		// NioEventLoopGroup是个线程组，包含一组NIO线程
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			// Netty用于启动NIO服务器端辅助启动类
			ServerBootstrap bootstrap = new ServerBootstrap();
			// NioServerSockketChannel -> JDK NIO类库中端ServerSocketChannel
			// option配置TCP参数 backlog为1024 存储1M
			// ChildHandler绑定事件处理类，ChildChannelHandler主要用于处理I/O事件
			bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChildChannelHandler());
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

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new TimeServerHandler());
		}
	}

	public static void main(String[] args) {
		int port;
		if (args != null && args.length > 0) {
			port = Integer.valueOf(args[0]);
		} else {
			port = 5000;
		}
		new TimeServer().bind(port);
	}

}
