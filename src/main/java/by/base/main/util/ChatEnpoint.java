package by.base.main.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.security.core.context.SecurityContextHolder;

import by.base.main.coders.MessageDecoder;
import by.base.main.coders.MessageEncoder;
import by.base.main.model.Message;

@ServerEndpoint(value = "/chat", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatEnpoint {
	
	private Session session = null;
	private static List<Session> sessionList = new LinkedList<>();
	
	
	@OnOpen
	public void onOpen(Session session) {		
		this.session = session;
		sessionList.add(session);
	}
	
	@OnClose
	public void onClose(Session session) {
		sessionList.remove(session);
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		throwable.printStackTrace();
	}
	
	@OnMessage
	public void onMessage(Session session, Message message) {
		System.out.println(message);
		sessionList.forEach(s->{
			if(s == this.session) return;
			try {
				message.setFromUser(this.session.getUserPrincipal().getName());
				System.out.println("post "+message);
				s.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
		});
	}
}
