package com.logic.aio;

public class TimeClient {
	public static void main(String[] args) {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				port = 5000;
			}
		}
		new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient001").start();
	}
}