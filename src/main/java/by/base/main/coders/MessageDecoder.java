package by.base.main.coders;


import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import by.base.main.model.Message;

public class MessageDecoder implements Decoder.Text<Message> {
	
	private static Gson gson = new Gson();

	@Override
	public void init(EndpointConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public Message decode(String s) throws DecodeException {
		return gson.fromJson(s, Message.class);
	}

	@Override
	public boolean willDecode(String s) {
		return s != null;
	}

}
