package com.logic.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {
	// 多路复用器
	Selector selector;
	ServerSocketChannel serverSocketChannel;
	volatile boolean stop;

	/*
	 * @param port
	 */
	public MultiplexerTimeServer(int port) {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("the time server is start in port: " + port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	public void stop() {
		this.stop = stop;
	}

	public void run() {
		while (!stop) {
			try {
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				SelectionKey key = null;
				while (iterator.hasNext()) {
					key = iterator.next();
					iterator.remove();
					try {
						handleInput(key);
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			if (key.isAcceptable()) {
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
				SocketChannel socketChannel = serverSocketChannel.accept();
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_READ);
			}
			if (key.isReadable()) {
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer readbuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readbuffer);
				if (readBytes > 0) {
					readbuffer.flip();
					byte[] bytes = new byte[readbuffer.remaining()];
					//将缓冲区可读的字节数组复制到bytes中
					readbuffer.get(bytes);
					String body = new String(bytes, "utf-8");
					System.out.println("the time server receive order:" + body);
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)
							? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
					doWrite(sc, currentTime);
				} else if (readBytes < 0) {
					key.cancel();
					sc.close();
				} else {

				}
			}
		}

	}

	private void doWrite(SocketChannel channel, String response) throws IOException {
		if (response != null && response.trim().length() > 0) {
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer);
		}

	}

}
