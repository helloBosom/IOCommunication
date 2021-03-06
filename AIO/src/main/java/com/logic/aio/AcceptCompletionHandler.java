package com.logic.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {
	public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
		attachment.asynchronousServerSocketChannel.accept(attachment, this);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		/*
		 * public final <A> void read(ByteBuffer dst, A attachment,
		 * CompletionHandler<Integer,? super A> handler) ByteBuffer dst :
		 * 接收缓冲区，用于从异步Channel中读取数据包 A attachment: 异步Channel携带的附件， 通知回调的时候作为入参使用
		 * ComppletionHandler<Integer,?super A>: 接收通知回调的业务handler
		 */
		result.read(buffer, buffer, new ReadCompletionHandler(result));
	}

	public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
		exc.printStackTrace();
		attachment.latch.countDown();
	}

}
