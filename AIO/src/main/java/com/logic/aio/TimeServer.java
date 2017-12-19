package com.logic.aio;

public class TimeServer {
	public static void main(String[] args) {
		int port = 5000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				port = 5000;
			}
		}
		new Thread(new AsyncTimeServerHandler(port), "AIO-AsyncTimeServerHandler-001").start();
	}
}
