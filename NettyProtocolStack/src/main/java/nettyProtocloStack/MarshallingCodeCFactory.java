package nettyProtocloStack;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class MarshallingCodeCFactory {

	public static Unmarshaller buildUnMarshalling() throws IOException {
		MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		return marshallerFactory.createUnmarshaller(configuration);
	}

	public static Marshaller buildMarshalling() throws IOException {
		MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		return marshallerFactory.createMarshaller(configuration);
	}
}
