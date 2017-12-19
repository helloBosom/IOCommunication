package com.logic.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable {

	@SuppressWarnings("unused")
	private int port;
	CountDownLatch latch;
	AsynchronousServerSocketChannel asynchronousServerSocketChannel;

	public AsyncTimeServerHandler(int port) {
		this.port = port;
		try {
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("the time server is start in port: " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// 在完成一组正在执行的操作之前允许当前线程一直阻塞
		latch = new CountDownLatch(1);
		asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
