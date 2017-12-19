package netty;

public class Test {

	int port;
	String hoString;

	public Test(int port, String hoString) {
		this.port = port;
		this.hoString = hoString;
	}

	public Test(String hoString, int port) {
		this.port = port;
		this.hoString = hoString;
	}
}
