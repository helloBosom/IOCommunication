package netty;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;

import netty_protobuf.SubscribeReqProto;

public class TestSubscribeReqProto {

	private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
		return req.toByteArray();
	}

	private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
		return SubscribeReqProto.SubscribeReq.parseFrom(body);
	}

	private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
		SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
		builder.setSubReqId(1);
		builder.setUserName("welcome");
		builder.setProductName("netty");
		List<String> address = new ArrayList<>();
		address.add("beijing");
		address.add("shanghai");
		address.add("shenzhen");
		builder.addAllAddress(address);
		return builder.build();
	}

	public static void main(String[] args) throws InvalidProtocolBufferException {
		SubscribeReqProto.SubscribeReq req = createSubscribeReq();
		System.out.println("before encode : " + req.toString());
		SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
		System.out.println("After decode : " + req.toString());
		System.out.println("Assert equal : --> " + req2.equals(req));
	}
}
