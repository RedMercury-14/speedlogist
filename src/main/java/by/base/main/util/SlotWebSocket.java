package by.base.main.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
@ServerEndpoint(value = "/slot", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class},
configurator = SpringConfigurator.class)
public class SlotWebSocket {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MainController mainController;
	
	@Autowired
	OrderService orderService;
	
	private Session session = null;
	public static List<Session> sessionList = new LinkedList<>();
//	public static List<Message> internationalMessegeList = new ArrayList<Message>(); //лист с сообщениями (предложениями) от перевозчиков (международников)
	
	
	@OnOpen
	public void onOpen(Session session) throws IOException {	
//		System.out.println("SlotTsdWebSocket подключение пользователя: " + session.getUserPrincipal().getName());
		this.session = session;
		if(sessionList.isEmpty()) {
			sessionList.add(session);
			sendSimpleMessage(session, "120", null);
		}else if(!sessionList.contains(session)){
			sessionList.add(session);
			sendSimpleMessage(session, "120", null);
		}
		System.out.println("SlotTsdWebSocket: всего сессий: " + sessionList.size());
	}
	
	@OnClose
	public void onClose(Session session) {
		System.err.println("SlotTsdWebSocket: onClose");
		sessionList.remove(session);
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
//		System.err.println("SlotTsdWebSocket: onError login: " + session.getUserPrincipal().getName());
		onClose(session);
		throwable.printStackTrace();
	}
	
	
	@OnMessage
	public void onMessage(Session session, Message message) throws ParseException, IOException, EncodeException {
		if(message.getStatus() != null) {
			switch (message.getStatus()) {
			case "110": // подключение нового пользователя
				sendSimpleMessage(session, "505", "Статус 110 отменен");
				break;
			case "130": // echo метод
				session.getBasicRemote().sendObject(message);
				break;
			case "300": // сообщения от клиента к серверу.
				if(message.getAction() != null) {
					switch (message.getAction()) {
					case "load":						
						Integer idOrder = Integer.parseInt(message.getIdOrder().trim());
						JSONParser parser = new JSONParser();
						JSONObject jsonMainObject = (JSONObject) parser.parse(message.getPayload());
						Timestamp timestamp = Timestamp.valueOf(jsonMainObject.get("timeDelivery").toString());
						Integer idRamp = Integer.parseInt(jsonMainObject.get("idRamp").toString());
						Order order = orderService.getOrderById(idOrder);
						order.setTimeDelivery(timestamp);
						order.setIdRamp(idRamp);
						order.setLoginManager(session.getUserPrincipal().getName());
						order.setStatus(6);
						//тут будет проверка на возможность вставки слота!
						orderService.updateOrder(order);
						sendSimpleMessage(session, "222", null);
						sessionList.forEach(s->{
							if(!s.equals(session)) {
								Message messageOther = new Message();
								messageOther.setIdOrder(idOrder.toString());
								messageOther.setStatus("200");
								messageOther.setAction("load");
								messageOther.setPayload(message.getPayload());
								try {
									s.getBasicRemote().sendObject(messageOther);
								} catch (IOException | EncodeException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});						
						break;					
						
					case "update":
						Integer idOrderUpdate = Integer.parseInt(message.getIdOrder().trim());
						JSONParser parserUpdate = new JSONParser();
						JSONObject jsonMainObjectUpdate = (JSONObject) parserUpdate.parse(message.getPayload());
						Timestamp timestampUpdate = Timestamp.valueOf(jsonMainObjectUpdate.get("timeDelivery").toString());
						Integer idRampUpdate = Integer.parseInt(jsonMainObjectUpdate.get("idRamp").toString());
						Order orderUpdate = orderService.getOrderById(idOrderUpdate);
						orderUpdate.setTimeDelivery(timestampUpdate);
						orderUpdate.setIdRamp(idRampUpdate);
						//тут будет проверка на возможность вставки слота!
						orderService.updateOrder(orderUpdate);
						sendSimpleMessage(session, "222", null);
						sessionList.forEach(s->{
							if(!s.equals(session)) {
								Message messageOther = new Message();
								messageOther.setIdOrder(idOrderUpdate.toString());
								messageOther.setStatus("200");
								messageOther.setAction("update");
								messageOther.setPayload(message.getPayload());
								try {
									s.getBasicRemote().sendObject(messageOther);
								} catch (IOException | EncodeException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						break;
						
					case "delete":						
						Integer idOrderDelete = Integer.parseInt(message.getIdOrder().trim());
						Order orderDelete= orderService.getOrderById(idOrderDelete);
						if(orderDelete.getStatus() >= 6) {
							orderDelete.setStatus(5);
							orderDelete.setLoginManager(null);
							orderDelete.setTimeDelivery(null);
							orderDelete.setAddresses(null);
							orderService.updateOrder(orderDelete);
							sendSimpleMessage(session, "222", null);
							sessionList.forEach(s->{
								if(!s.equals(session)) {
									Message messageOther = new Message();
									messageOther.setIdOrder(orderDelete.toString());
									messageOther.setStatus("200");
									messageOther.setAction("delete");
									try {
										s.getBasicRemote().sendObject(messageOther);
									} catch (IOException | EncodeException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
						}else {
							sendSimpleMessage(session, "200", "Невозможно удалить заказ из плата выгрузки, т.к. оформлена заявка на поиск транспорта");
						}
						break;

					default:
						sendSimpleMessage(session, "500", "неизвестная коменда action");
						break;
					}
				}else {
					sendSimpleMessage(session, "500", "action = null");
				}
				break;
//			case value:
//				
//				break;
			

			default:
				sendSimpleMessage(session, "505", "Ошибка статуса");
				break;
			}
		}else {
			sendSimpleMessage(session, "505", "Ошибка статуса: status = null");
		}
		
		
		
		
		//отвечает за работу уведомлений и сообщений перевозам
//			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
//			message.setDatetime(LocalDateTime.now().format(formatter1));
//			message.setStatus("Это ответ от сервера!");
//			message.setComment("Всего подключенных сессий = " + sessionList.size());
//			System.out.println(message);
//			sessionList.forEach(s -> {
//				try {
//					if(s!= null) {						
//						s.getBasicRemote().sendObject(message);
//					}
//				} catch (IOException | EncodeException e) {
//					e.printStackTrace();
//				}
//			});	
	}
	
	public void sendMessage (Message message) {
//		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
//		message.setDatetime(LocalDateTime.now().format(formatter1));
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
	
	private void sendSimpleMessage(Session session, String status, String message) {
		Message errorMessage = new Message();
		errorMessage.setStatus(status);
		errorMessage.setText(message);
		try {
			session.getBasicRemote().sendObject(errorMessage);
		} catch (IOException | EncodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

