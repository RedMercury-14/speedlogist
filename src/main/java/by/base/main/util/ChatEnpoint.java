package by.base.main.util;

import java.io.IOException;
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
import by.base.main.controller.MainController;
import by.base.main.model.Message;
import by.base.main.service.UserService;
@Component
@ServerEndpoint(value = "/chat", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class},
configurator = SpringConfigurator.class)
public class ChatEnpoint {
	
	@Autowired
	UserService userService;
	
	private Session session = null;
	public static List<Session> sessionList = new LinkedList<>();
	public static List<Message> internationalMessegeList = new ArrayList<Message>(); //лист с сообщениями от перевозчиков (международников)
	
	
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
		if(message.getIdRoute() != null) {
			String login = this.session.getUserPrincipal().getName();
			message.setFromUser(login);
			message.setCompanyName(userService.getUserByLogin(login).getCompanyName());
		}else {
			message.setFromUser(this.session.getUserPrincipal().getName());
		}		
		sessionList.forEach(s->{
			if(s == this.session) {
				internationalMessegeList.add(message);
				return;}
			try {			
				s.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
		});
	}
}
