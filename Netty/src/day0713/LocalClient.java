package day0713;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalClient {
	public static void main(String[] args) throws UnknownHostException {
		InetAddress addr;
		String ip = null;
		String address = null;
		addr = InetAddress.getLocalHost();
		ip = addr.getHostAddress();
		address = addr.getHostName();
		System.out.println(ip);
		System.out.println(address.toString());
		System.out.println(System.getProperty("java.vm.name"));
	}

}
