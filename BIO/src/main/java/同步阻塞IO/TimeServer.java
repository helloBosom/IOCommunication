package 同步阻塞IO;

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
			while (true) {
				socket = server.accept();
				new Thread(new TimeServerHandler(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				server.close();
				server = null;
			}
		}
	}
}
