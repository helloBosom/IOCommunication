package netty_protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SubReqServer {

	public static void main(String[] args) {
		int port = 5000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				port = 5000;
			}
		}
		new SubReqServer().bind(port);
	}

	/*
	 * 在基类AbstractBootstrap有handler方法，目的是添加一个handler，监听Bootstrap的动作，
	 * 客户端的Bootstrap中，继承了这一点。
	 * 
	 * 在服务端的ServerBootstrap中增加了一个方法childHandler，它的目的是添加handler，
	 * 用来监听已经连接的客户端的Channel的动作和状态。
	 * 
	 * handler在初始化时就会执行，而childHandler会在客户端成功connect后才执行，这是两者的区别。
	 */
	private void bind(int port) {
		EventLoopGroup workGroup = new NioEventLoopGroup();
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// 用于半包处理
							ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
							/*
							 * ProtobufDecoder的参数是com.google.protobuf.
							 * MessageLite 告诉ProtobufDecoder目标类是什么
							 * ProtubufDecoder只负责解码，不支持读半包
							 * 在ProtobufDecoder之前加入处理半包的解码器,3种方式可选：
							 * 1.使用Netty提供的ProtobufVarint32FrameDecoder。
							 * 2.继承Netty提供的通用半包解码器LengthFieldBasedFrameDecoder
							 * 3.继承ByteToMessageDecoder类，自己处理半包.
							 * 
							 * POJO：POJO(Plain Old Java
							 * Object)这个名字用来强调它是一个普通java对象，而不是一个特殊的对象，
							 * 其主要用来指代那些没有遵从特定的Java对象模型、约定或框架（如EJB）的Java对象。
							 */
							ch.pipeline()
									.addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
							ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
							ch.pipeline().addLast(new ProtobufEncoder());
							ch.pipeline().addLast(new SubReqServerHandler());
						}
					});
			ChannelFuture future = bootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
}
