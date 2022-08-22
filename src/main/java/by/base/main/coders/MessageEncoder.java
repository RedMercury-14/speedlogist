package by.base.main.coders;

import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import by.base.main.model.Message;

public class MessageEncoder implements Encoder.Text<Message>{
	private static Gson gson = new Gson();
	@Override
	public String encode(Message object) throws EncodeException {
		return gson.toJson(object);
	}

	@Override
	public void init(EndpointConfig config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
