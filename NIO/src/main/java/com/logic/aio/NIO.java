package com.logic.aio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIO {
	public static void main(String[] args) {
		int port = 0;
		try {
			// 1. 打开ServerSocketChannel.监听客户端的链接
			ServerSocketChannel acceptorSvr = ServerSocketChannel.open();
			// 2. 绑定监听端口，设置为非阻塞模式
			acceptorSvr.socket().bind(new InetSocketAddress(InetAddress.getByName("ip"), port));
			acceptorSvr.configureBlocking(false);
			// 3. 创建reactor线程，创建多路复用器启动线程
			Selector selector = Selector.open();
			// new Thread(new ReactorTask()).start();
			// 4. 将ServerSocketChannel注册到Reactor线程到多路复用器Selector上，监听accept事件
			// SelectionKey key = acceptorSvr.register(selector, SelectionKey.OP_ACCEPT,
			// ioHandler);
			// 5. 多路复用器在线程run方法的无限循环体内轮询准备就绪的key
			int num = selector.select();
			Set selectedkeys = selector.selectedKeys();
			Iterator iterator = selectedkeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key2 = (SelectionKey) iterator.next();
			}
			// 6. 多路复用监听器监听到新的客户端接入，处理新的请求，完成TCP三次握手，建立物理链路
			// SocketChannel channel = ServerSocketChannel.accept();
			// 7. 设置客户端链路为非阻塞模式
			// channel.configureBlocking(false);
			// channel.socket().setReuseAddress(true);
			// 8. 将新接入的客户端链接注册到Reactor线路的多路复用器上，监听读操作
			// SelectionKey key3 = SocketChannel.register();
			// 9. 异步读取客户端请求消息到缓冲区
			// int readNumber = channel.read(receivedBuffer);
			// 10.对ByteBuffer进行编解码，如果有半解包消息指针reset，继续读取后续的报文，将解码成功的消息封装成Task，投递到业务线程池中，进行业务逻辑编排
			Object message = null;
			// 将POJO对象encode成ByteBuffer,调用SocketChannel的异步write接口，将消息异步发送给客户端
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
