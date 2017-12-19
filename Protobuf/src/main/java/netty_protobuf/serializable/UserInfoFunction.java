package netty_protobuf.serializable;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Hello world!
 *
 */
public class UserInfoFunction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userName;
	private int userId;

	public UserInfoFunction buildUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public UserInfoFunction buildUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public final String getUserName() {
		return userName;
	}

	public final void setUserName(String userName) {
		this.userName = userName;
	}

	public final int getUserId() {
		return userId;
	}

	public final void setUserId(int userId) {
		this.userId = userId;
	}

	public byte[] codeC(ByteBuffer buffer) {
		buffer.clear();
		byte[] value = this.userName.getBytes();
		buffer.putInt(this.userId);
		buffer.put(value);
		buffer.putInt(value.length);
		buffer.flip();
		value = null;
		byte[] result = new byte[buffer.remaining()];
		buffer.get(result);
		return result;
	}

	public static void main(String[] args) {
		System.out.println("Hello World!");
	}
}
