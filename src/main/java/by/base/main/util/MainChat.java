package by.base.main.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import by.base.main.coders.MessageDecoder;
import by.base.main.coders.MessageEncoder;
import by.base.main.model.Message;
import by.base.main.service.MessageService;
import by.base.main.service.UserService;

@Component
@ServerEndpoint(value = "/system", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class},
configurator = SpringConfigurator.class)
public class MainChat {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MessageService messageService;
	
	
	private Session session = null;
	
	public static List<Session> sessionList = new LinkedList<>();
	public static List<Message> messegeList = new ArrayList<Message>(); //лист с сообщениями
	
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
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
		message.setDatetime(LocalDateTime.now().format(formatter1));	
		sessionList.forEach(s->{
			if(s == this.session) {
				if (!message.getFromUser().equals("system")) {
					messegeList.add(message);
				}				
				}
			try {
				s.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
		});
	}

}
