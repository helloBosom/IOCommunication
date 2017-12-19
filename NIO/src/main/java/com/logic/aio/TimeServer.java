package com.logic.aio;

public class TimeServer {
	public static void main(String[] args) {
		int port = 8080;
		if (args != null && args.length > 0) {

			try {
				port = Integer.valueOf(port);
			} catch (Exception e) {
				port = 5000;
			}
		}
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
	}
}
