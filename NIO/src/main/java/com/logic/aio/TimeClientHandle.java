package com.logic.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable {
	String host;
	int port;
	Selector selector;
	SocketChannel socketChannel;
	volatile boolean stop;

	public TimeClientHandle(String host, int port) {
		this.host = host == null ? "127.0.0.1" : host;
		this.port = port;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void run() {
		try {
			doConnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (!stop) {
			try {
				selector.select(1000);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
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
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
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
		SocketChannel sc = (SocketChannel) key.channel();
		if (key.isConnectable()) {
			if (sc.finishConnect()) {
				sc.register(selector, SelectionKey.OP_READ);
				doWrite(sc);
			} else {
				System.exit(1);
			}
		}
		if (key.isReadable()) {
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);
			int readBytes = sc.read(readBuffer);
			if (readBytes > 0) {
				readBuffer.flip();
				byte[] bytes = new byte[readBuffer.remaining()];
				readBuffer.get(bytes);
				String body = new String(bytes, "utf8");
				System.out.println("Now is" + body);
				this.stop = true;
			} else if (readBytes < 0) {
				key.cancel();
				sc.close();
			} else {

			}
		}
	}

	private void doConnect() throws IOException {
		if (socketChannel.connect(new InetSocketAddress(host, port))) {
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		} else {
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

	private void doWrite(SocketChannel sc) throws IOException {
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writerBuffer = ByteBuffer.allocate(req.length);
		writerBuffer.put(req);
		writerBuffer.flip();
		sc.write(writerBuffer);
		if (!writerBuffer.hasRemaining()) {
			System.out.println("send order to server succeed.");
		}
	}

}
