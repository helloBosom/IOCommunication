package netty_protobuf.serializable;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Hello world!
 *
 */
public class UserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userName;
	private int userId;

	public UserInfo buildUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public UserInfo buildUserId(int userId) {
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

	public byte[] codeC() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
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

}
