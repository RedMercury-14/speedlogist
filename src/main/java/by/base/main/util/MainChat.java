package by.base.main.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
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
import by.base.main.service.UserService;

@Component
@ServerEndpoint(value = "/system", decoders = { MessageDecoder.class }, encoders = {
		MessageEncoder.class }, configurator = SpringConfigurator.class)
public class MainChat {

	@Autowired
	UserService userService;

	private Session session = null;

	public static List<Session> sessionList = new LinkedList<>();
	public static Set<Message> messegeList = new HashSet<Message>(); // лист с сообщениями

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		sessionList.add(session);
	}

	@OnClose
	public void onClose(Session session) {
		sessionList.remove(session);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		sessionList.remove(session);
		throwable.printStackTrace();
	}

	@OnMessage
	public void onMessage(Session session, Message message) {
		//отвечает за работу уведомлений и сообщений перевозам
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
			message.setDatetime(LocalDateTime.now().format(formatter1));
			System.out.println(message);
			sessionList.forEach(s -> {
				if (s == this.session) {
					
					if (!message.getFromUser().equals("system")) {
						if(!message.getToUser().equals("international")) {//в доп блоке откоючаю запись сообщений о том что маршрут доступен для торгов для последующего отображения
							messegeList.add(message);
						}
						
					}
				}
				try {
					if(s!= null) {						
						s.getBasicRemote().sendObject(message);
					}
				} catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			});	
	}
	public void setMessageByTimer(Message message) {
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
		message.setDatetime(LocalDateTime.now().format(formatter1));
		messegeList.add(message);
		sessionList.forEach(s->{
			try {	
				s.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
		});
	}

}
