package netty_file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;


import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.SimpleChannelInboundHandler;

public class FileServerHandler extends SimpleChannelInboundHandler<String> {

	// 换行符。。。。
	private static final String CR = System.getProperty("line.separator");

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("message arrive");
		System.out.println(msg);
		File file = new File(msg);
		if (file.exists()) {
			if (!file.isFile()) {
				ctx.writeAndFlush("not a file : " + file + CR);
				return;
			} else {
				
				DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(msg)));
				byte[] bytes = new byte[1024];
				int read = 0;
				while (true) {
					read = in.read(bytes);
					ctx.writeAndFlush(read);
				}
			}
		} else {
			ctx.writeAndFlush("file not found : " + file + CR);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
