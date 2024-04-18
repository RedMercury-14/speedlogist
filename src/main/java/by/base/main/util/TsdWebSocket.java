package by.base.main.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import by.base.main.coders.MessageDecoder;
import by.base.main.coders.MessageEncoder;
import by.base.main.controller.MainController;
import by.base.main.model.Message;
import by.base.main.model.Order;
import by.base.main.model.User;
import by.base.main.service.MessageService;
import by.base.main.service.OrderService;
import by.base.main.service.UserService;
@Component
@ServerEndpoint(value = "/tsd", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class},
configurator = SpringConfigurator.class)
public class TsdWebSocket {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private MainController mainController;
	
	@Autowired
	private OrderService orderService;
	
	private Session session = null;
	public static List<Session> sessionList = new LinkedList<>();
	public static List<Message> internationalMessegeList = new ArrayList<Message>(); //лист с сообщениями (предложениями) от перевозчиков (международников)
	
	
	@OnOpen
	public void onOpen(Session session) throws IOException {	
		System.out.println("TsdWebSocket запущен");
		this.session = session;
		if(sessionList.isEmpty()) {
			sessionList.add(session);
		}else {
			for (Session sess : sessionList) {
				sess.close();
			}
			sessionList.clear();
			sessionList.add(session);
		}		
		System.out.println("TsdWebSocket: всего сессий: " + sessionList.size());
	}
	
	@OnClose
	public void onClose(Session session) {
		System.err.println("TsdWebSocket: onClose");
		sessionList.remove(session);
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		System.err.println("TsdWebSocket: onError");
		throwable.printStackTrace();
	}
	
	
	@OnMessage
	public void onMessage(Session session, Message message) {
		//отвечает за работу уведомлений и сообщений перевозам
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
			message.setDatetime(LocalDateTime.now().format(formatter1));
			message.setStatus("Это ответ от сервера!");
			message.setComment("Всего подключенных сессий = " + sessionList.size());
			System.out.println(message);
			if(message.getAction() != null) {
				switch (message.getAction()) {
				case "loadPlan":
					LocalDate dateNow = LocalDate.now().plusDays(1);
					List<Order> orders = orderService.getOrderByTimeDelivery(java.sql.Date.valueOf(dateNow), java.sql.Date.valueOf(dateNow));
					List<Order> result = new ArrayList<Order>();
					orders.forEach(o->{
						o.setChangeStatus(null);
						o.setAddresses(null);
						o.setRoutes(null);
						o.setMailInfo(null);
						o.setSlotInfo(null);
						result.add(o);
					});
					
					break;

				default:
					sessionList.forEach(s -> {
						try {
							if(s!= null) {						
								s.getBasicRemote().sendObject(message);
							}
						} catch (IOException | EncodeException e) {
							e.printStackTrace();
						}
					});	
					break;
				}
			}else {
				sessionList.forEach(s -> {
					try {
						if(s!= null) {						
							s.getBasicRemote().sendObject(message);
						}
					} catch (IOException | EncodeException e) {
						e.printStackTrace();
					}
				});	
			}
			
			
	}
	
	public void sendMessage (Message message) {
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
		message.setDatetime(LocalDateTime.now().format(formatter1));
		sessionList.forEach(s -> {
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
		sessionList.forEach(s->{
			try {	
				s.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
		});
	}
}

