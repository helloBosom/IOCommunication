package nettyProtocloStack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyServer {

	public void bind() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
	
			/*
			 * boot:靴子 ; strap:拼命工作; bootstrap:引导程序
			 */
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
							ch.pipeline().addLast(new NettyMessageEncoder());
							ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
							ch.pipeline().addLast(new LoginAuthRespHandler());
							ch.pipeline().addLast("HeartBeatHandler", new HeartBeatRespHandler());
						};
					});
			bootstrap.bind(NettyConstant.REMOTEIP, NettyConstant.PORT).sync();
			System.out.println("netty server start ok :" + NettyConstant.REMOTEIP + ":" + NettyConstant.PORT);
		
	}

	public static void main(String[] args) throws InterruptedException {
		new NettyServer().bind();
	}

}
