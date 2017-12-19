package netty_file;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileServerHandler extends SimpleChannelInboundHandler<String> {

	//换行符。。。。
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
			}
			ctx.write(file + "" + file.length() + CR);
			//随机读文件
			RandomAccessFile randomAccessFile = new RandomAccessFile(msg, "r");
			FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
			ctx.write(region);
			ctx.writeAndFlush(CR);
			randomAccessFile.close();
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
