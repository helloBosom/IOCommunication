package com.logic.aio;

public class TimeCilent {

	public static void main(String[] args) {
		int port = 5000;
		String host = "127.0.0.1";
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				port = 5000;
			}
		}
		new Thread(new AsyncTimeClientHandler(host, port), "AIO-AsyncTimeClientHandler-001").start();
	}
}
