package com.logic.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {

	private AsynchronousSocketChannel client;
	private String host;
	private int port;
	/*
	 * 一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
	 * 用给定的计数 初始化 CountDownLatch。由于调用了 countDown() 方法，所以在当前计数到达零之前，await
	 * 方法会一直受阻塞。之后，会释放所有等待的线程，await 的所有后续调用都将立即返回。
	 * 
	 */
	private CountDownLatch latch;

	public AsyncTimeClientHandler(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			client = AsynchronousSocketChannel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		latch = new CountDownLatch(1);
		/*
		 * A attachment : AsynchronouSocketChannel的附件，用于回调通知时作为入参被传递
		 * CompletionHandler<Void,?super A>handler : 异步操作回调接口通知
		 */
		client.connect(new InetSocketAddress(host, port), this, this);
		try {
			//CountDownLatch.await()使当前线程在锁存器倒计数至零之前一直等待，除非线程被中断。
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void completed(Void result, AsyncTimeClientHandler attachment) {
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
			public void completed(Integer result, ByteBuffer buffer) {
				if (buffer.hasRemaining()) {
					client.write(buffer, buffer, this);
				} else {
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
						public void completed(Integer result, ByteBuffer buffer) {
							buffer.flip();
							byte[] bytes = new byte[buffer.remaining()];
							buffer.get(bytes);
							String body;
							try {
								body = new String(bytes, "utf-8");
								System.out.println("now is : " + body);
								latch.countDown();
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}

						public void failed(Throwable exc, ByteBuffer attachment) {
							try {
								client.close();
								latch.countDown();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			public void failed(Throwable exc, ByteBuffer attachment) {
				try {
					client.close();
					latch.countDown();
				} catch (IOException e) {

				}

			}
		});
	}

	public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
		try {
			client.close();
			latch.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
