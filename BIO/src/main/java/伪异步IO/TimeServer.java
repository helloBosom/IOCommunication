package 伪异步IO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Hello world!
 *
 */
public class TimeServer {
	public static void main(String[] args) throws IOException {
		int port = 8080;
		if (args != null && args.length > 0) {
			port = Integer.valueOf(port);
		}
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("the timeServer is start in port:" + port);
			Socket socket = null;
			TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);
			while (true) {
				socket = server.accept();
				singleExecutor.execute(new TimeServerHandler(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				System.out.println("the time server close");
				server.close();
				server = null;
			}
		}
	}
}
