package by.base.main.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.poi.util.SystemOutLogger;
import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import by.base.main.coders.MessageDecoder;
import by.base.main.coders.MessageEncoder;
import by.base.main.controller.MainController;
import by.base.main.model.Message;
import by.base.main.model.User;
import by.base.main.service.MessageService;
import by.base.main.service.UserService;
@Component
@ServerEndpoint(value = "/chat", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class},
configurator = SpringConfigurator.class)
public class ChatEnpoint {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	MainController mainController;
	
	private Session session = null;
	public static List<Session> sessionList = new LinkedList<>();
	public static List<Message> internationalMessegeList = new ArrayList<Message>(); //лист с сообщениями (предложениями) от перевозчиков (международников)
	
	
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
		System.err.println(session.getUserPrincipal());
		
		String appPath = mainController.path;
		User user = userService.getUserByLogin(message.getFromUser());		
		if (user != null && user.getNumYNP() != null) {
			message.setYnp(user.getNumYNP());
		}		
		if (message.getComment() !=null && message.getComment().equals("delete")) {
			Set<Message> messageSet = new HashSet<Message>();			
			internationalMessegeList.stream()
				.filter(m-> m.getText().equals(message.getText()) && m.getIdRoute().equals(message.getIdRoute()) && m.getYnp().equals(message.getYnp()))
				.forEach(m-> messageSet.add(m));
//			System.out.println(messageSet.size());
//			internationalMessegeList.forEach(m->System.out.println(m));
			internationalMessegeList.remove(messageSet.stream().findFirst().get());
			sessionList.forEach(s->{	
				try {
					if(s != null) {						
						s.getBasicRemote().sendObject(message);
					}					
				} catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			});
		}else {
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
			message.setDatetime(LocalDateTime.now().format(formatter1));
			
			if (message.getIdRoute() != null && message.getToUser() != null && message.getToUser().equals("disposition")) {
				message.setCompanyName(userService.getUserByLogin(message.getFromUser()).getCompanyName());
				messageService.singleSaveMessage(message);
			}else if(message.getIdRoute() != null) {
				if (message.getFromUser() == null) {
					String login = this.session.getUserPrincipal().getName();
					message.setFromUser(login);
					message.setCompanyName(userService.getUserByLogin(login).getCompanyName());
				}else if (!message.getFromUser().equals("system")){				 
						message.setCompanyName(userService.getUserByLogin(message.getFromUser()).getCompanyName());						
				}
			}else {
				if (message.getFromUser() == null) {
					message.setFromUser(this.session.getUserPrincipal().getName());
				}			
			}		
			sessionList.forEach(s->{
				if(s == this.session) {
					if (message.getFromUser().equals("system")) {
						//не записываем сообщение от системы
					}else if (message.getToUser() != null && message.getToUser().equals("disposition")) {
						
					}
					else{
						internationalMessegeList.add(message);
						//аварийная сериализация кеша, на случай если сервер упадёт
						try {
							FileOutputStream fos =
				                     new FileOutputStream(appPath + "resources/others/hashmap.ser");
				                  ObjectOutputStream oos = new ObjectOutputStream(fos);
				                  oos.writeObject(internationalMessegeList);
				                  oos.close();
				                  fos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					//return;
					}
				try {	
					if(s != null) {
						s.getBasicRemote().sendObject(message);
					}					
				} catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	/**
	 * этот метод дублирует предыдущий метод.
	 * Разница в том что сообщение приходит по TCP/IP а не через WS
	 */
	public void onMessageFromRest(Message message) {
		String appPath = mainController.path;
		User user = userService.getUserByLogin(message.getFromUser());		
		if (user != null && user.getNumYNP() != null) {
			message.setYnp(user.getNumYNP());
		}		
		if (message.getComment() !=null && message.getComment().equals("delete")) {
			Set<Message> messageSet = new HashSet<Message>();			
			internationalMessegeList.stream()
				.filter(m-> m.getText().equals(message.getText()) && m.getIdRoute().equals(message.getIdRoute()) && m.getYnp().equals(message.getYnp()))
				.forEach(m-> messageSet.add(m));
//			System.out.println(messageSet.size());
//			internationalMessegeList.forEach(m->System.out.println(m));
			internationalMessegeList.remove(messageSet.stream().findFirst().get());
			sessionList.forEach(s->{	
//				UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) s.getUserPrincipal();
				try {
					if(s != null) {
						s.getBasicRemote().sendObject(message);
					}					
				} catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			});
		}else {
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
			message.setDatetime(LocalDateTime.now().format(formatter1));
			
			if (message.getIdRoute() != null && message.getToUser() != null && message.getToUser().equals("disposition")) {
				message.setCompanyName(userService.getUserByLogin(message.getFromUser()).getCompanyName());
				messageService.singleSaveMessage(message);
			}else if(message.getIdRoute() != null) {
				if (message.getFromUser() == null) {
					String login = user.getLogin();
					message.setFromUser(login);
					message.setCompanyName(userService.getUserByLogin(login).getCompanyName());
				}else if (!message.getFromUser().equals("system")){				 
						message.setCompanyName(userService.getUserByLogin(message.getFromUser()).getCompanyName());						
				}
			}else {
				if (message.getFromUser() == null) {
					message.setFromUser(user.getLogin());
				}			
			}		
			sessionList.forEach(s->{
//				UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) s.getUserPrincipal();
					if (message.getFromUser().equals("system")) {
						//не записываем сообщение от системы
					}else if (message.getToUser() != null && message.getToUser().equals("disposition")) {
						
					}
					else{
						if(!internationalMessegeList.contains(message)) {
							internationalMessegeList.add(message);
						}						
						//аварийная сериализация кеша, на случай если сервер упадёт
						try {
							FileOutputStream fos =
				                     new FileOutputStream(appPath + "resources/others/hashmap.ser");
				                  ObjectOutputStream oos = new ObjectOutputStream(fos);
				                  oos.writeObject(internationalMessegeList);
				                  oos.close();
				                  fos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					//return;
				try {	
					if(s != null) {
						s.getBasicRemote().sendObject(message);
					}					
				} catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			});
		}
	}
	public void setMessageByTimer(Message message) {
		sessionList.forEach(s->{
			try {	
				s.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
		});
	}
}

