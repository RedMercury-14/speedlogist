package by.base.main.controller.ajax;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.mysql.cj.x.protobuf.MysqlxResultset.FetchSuspendedOrBuilder;

import by.base.main.controller.MainController;
import by.base.main.model.Message;
import by.base.main.model.Rates;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.User;
import by.base.main.service.MessageService;
import by.base.main.service.RatesService;
import by.base.main.service.RouteService;
import by.base.main.service.UserService;
import by.base.main.util.ChatEnpoint;
import by.base.main.util.MainChat;

@RestController
@RequestMapping("api")
public class MainRestController {
	
	private Route route;
	
	private Gson gson = new Gson();

	@Autowired
	private RouteService routeService;

	@Autowired
	private RatesService ratesService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ChatEnpoint chatEnpoint;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private MainChat mainChat;
	

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	@GetMapping ("/route")
	public Set<Route> getListRoute() {
		Set<Route> res = new HashSet<Route>(); // расчёт стоимости!
		for (Route route : routeService.getRouteList()) { // расчёт стоимости!
			boolean flag = false;
			Set <RouteHasShop> routeHasShops = route.getRoteHasShop();
			for (RouteHasShop routeHasShop : routeHasShops) {
				if(routeHasShop.getShop() == null) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				res.add(addCostForRoute(route)); // расчёт стоимости!
			}else {
				res.add(route);
			}			
		}
		return res;
	}
	
	@GetMapping ("/simpleroute")
	public List<Route> getListSimpleRoute() {		
		return routeService.getRouteList();
	}

	@GetMapping("/route/{id}")
	public Route getRoute(@PathVariable int id) {
		Route route = routeService.getRouteById(id);
		if (route.getComments() != null && route.getComments().equals("international")) {
			return route;
		}else {
			return addCostForRoute(route);
		}
		
		
	}

	@PostMapping("/route/numStock")
	public JSONObject postNumStock(@RequestBody String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		int id = Integer.parseInt(jsonpObject.get("idRoute").toString().trim());
		Route route = routeService.getRouteById(id);
		route.setNumStock(Integer.parseInt(jsonpObject.get("numStock").toString().trim()));
		routeService.saveOrUpdateRoute(route);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ!");
		return new JSONObject(map);
	}

	@PostMapping("/route/temperature")
	public JSONObject postTemperature(@RequestBody String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		int id = Integer.parseInt(jsonpObject.get("idRoute").toString().trim());
		Route route = routeService.getRouteById(id);
		route.setTemperature(jsonpObject.get("temperature").toString().trim());
		routeService.saveOrUpdateRoute(route);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ!");
		return new JSONObject(map);
	}
	
	@PostMapping("/route/timeLoadPreviously")
	public JSONObject postTimeLoadPreviously(@RequestBody String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		int id = Integer.parseInt(jsonpObject.get("idRoute").toString().trim());
		Route route = routeService.getRouteById(id);
		LocalTime time = LocalTime.parse(jsonpObject.get("timeLoadPreviously").toString().trim());
		route.setTimeLoadPreviously(time);
		routeService.saveOrUpdateRoute(route);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ!");
		return new JSONObject(map);
	}
	
	@PostMapping("/route/time")
	public JSONObject postTime(@RequestBody String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		int id = Integer.parseInt(jsonpObject.get("idRoute").toString().trim());
		Route route = routeService.getRouteById(id);
		LocalTime time = LocalTime.parse(jsonpObject.get("time").toString().trim());
		route.setTime(time);
		routeService.saveOrUpdateRoute(route);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ!");
		return new JSONObject(map);
	}
	
	@GetMapping("/route/admin/{dateStart},{dateFinish}")
	public List<Route> getListRouteHasDate(@PathVariable String dateStart, @PathVariable String dateFinish ) {
		Date targetDateStart = Date.valueOf(dateStart);
		Date targetDateFinish = Date.valueOf(dateFinish);
		return routeService.getRouteListAsDate(targetDateStart, targetDateFinish);
	}
	
	@GetMapping("/route/disposition")
	public List<Route> getListRouteInternationalDispo() {
		List<Route> routes = new ArrayList<Route>();
		routeService.getRouteListAsStatus("4", "4").stream()
			.filter(r-> r.getComments().equals("international"))
			.forEach(r-> routes.add(r));
		return routes;
	}
	
	@GetMapping ("/user")
	public List<User> homePage() {
		return userService.getUserList();		
	}
	
	@GetMapping("/user/{id}")
	public User getUser(@PathVariable int id) {
		return userService.getUserById(id);		
	}
	
	@PostMapping("/user/isexists")
	public JSONObject postRegistration(@RequestBody String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		try {
			if (userService.getUserByLogin(jsonpObject.get("Login").toString()) == null) {
				return null;
			}else {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("message", "юзер с таким именем существует");
				return new JSONObject(map);
			}
		} catch (Exception e) {
			return null;
		}
				
	}
	
	@GetMapping("/user/time")
	public String time() {
		return LocalDateTime.now().toString();		
	}
	
	@GetMapping("/message/disposition/{idRoute}")
	public List<Message> getListMEssageInternationalDispo(@PathVariable String idRoute) {
		List<Message> messages = new ArrayList<Message>();
		messageService.getListMessageByIdRoute(idRoute).stream()
			.filter(m-> m.getToUser() != null && m.getToUser().equals("disposition")).forEach(m-> messages.add(m));
		return messages;
	}
	
	//переписать с использованием gson!
	@PostMapping("/route/addpoints")
	public JSONObject postRoadPoints(@RequestBody String str) throws ParseException {
		StringBuilder sb = new StringBuilder(str);
		sb.deleteCharAt(0);
		sb.deleteCharAt(str.length()-2);		
		StringBuilder nsb = new StringBuilder(sb.toString());
		char[]task = sb.toString().toCharArray();
		for (int i = 0; i < task.length; i++) {			
			if (task[i] == '}' && i !=task.length-1) {
				nsb.setCharAt(i+1, '&');				
			}			
		}
		String[] points = nsb.toString().split("&");		
		route = new Route();
		Set<RouteHasShop> routeHasShops = new HashSet<RouteHasShop>();		
		for (String string : points) {
			JSONParser parser = new JSONParser();
			JSONObject jsonpObject = (JSONObject) parser.parse(string);
			RouteHasShop routeHasShop = new RouteHasShop();
			String text = jsonpObject.get("order").toString();
			routeHasShop.setOrder(Integer.parseInt(text));
			routeHasShop.setPall((String) jsonpObject.get("pall"));
			routeHasShop.setWeight((String) jsonpObject.get("weight"));
			routeHasShop.setAddress((String) jsonpObject.get("address"));
			routeHasShop.setCargo((String)jsonpObject.get("cargo"));
			routeHasShop.setPosition((String)jsonpObject.get("position"));
			routeHasShops.add(routeHasShop);
		}
		route.setRoteHasShop(routeHasShops);	
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ");
		return new JSONObject(map);
	}
	
	
	@GetMapping("/info/message/numroute/{idRoute}") // отдаёт колличество предложений по id маршруту
	public String getSizeMessageByRoute(@PathVariable String idRoute) {
		List <Message> messagesList = new ArrayList<Message>();		
		chatEnpoint.internationalMessegeList.stream()
			.filter(mes->mes.getIdRoute().equals(idRoute+"")).forEach(mes-> messagesList.add(mes));		
		return messagesList.size()+"";		
	}
	
	@GetMapping("/info/message/routes") // отдаёт все сообщения, которые имеются в кеше, по маршрутам
	public List <Message> getListMessegRoute() {
		List <Message> messagesList = new ArrayList<Message>();		
		chatEnpoint.internationalMessegeList.stream()
			.filter(mes->mes.getIdRoute()!= null).forEach(mes-> messagesList.add(mes));		
		return messagesList;		
	}
	
	@GetMapping("/info/message/routes/{idRoute}") // отдаёт сообщения где есть id маршрута из кеша!!!
	public List<Message> getListMessegRouteById(@PathVariable String idRoute) {
		List <Message> messagesList = new ArrayList<Message>();		
		ChatEnpoint.internationalMessegeList.stream()
			.filter(mes->mes.getIdRoute().equals(idRoute+"")).forEach(mes-> messagesList.add(mes));		
		return messagesList;		
	}
	
	@GetMapping("/info/message/routes/from_me") // отдаёт сообщения, на которые есть предложения от данного юзера из кеша
	public List<Message> getIdRouteByTargetCarrier() {
		List <Message> result = new ArrayList<Message>();
		String login = SecurityContextHolder.getContext().getAuthentication().getName();	
		ChatEnpoint.internationalMessegeList.stream()
			.filter(mes->mes.getFromUser().equals(login)).forEach(mes-> result.add(mes));	
		return result;
	}
	
	@GetMapping("/memory/message/routes/{idRoute}") // отдаёт сообщения где есть id маршрута из БД!!!
	public List<Message> getListMessegRouteByIdFromBase(@PathVariable String idRoute) {
		List <Message> messagesList = new ArrayList<Message>();		
		messagesList = messageService.getListMessageByIdRoute(idRoute);
		return messagesList;		
	}
	
	@GetMapping("/info/message/participants/{idRoute}") // отдаёт колличество участников торгов по id маршрута
	public String getParticipantsByRoute(@PathVariable String idRoute) {
		Set <String> messagesList = new HashSet<String>();		
		chatEnpoint.internationalMessegeList.stream()
			.filter(mes->mes.getIdRoute().equals(idRoute+"")).forEach(mes-> messagesList.add(mes.getCompanyName()));		
		return messagesList.size()+"";		
	}
	
	@GetMapping("/mainchat/messages") // отдаёт число сообщений
	public String getNumMessage() {		
		return mainChat.messegeList.size()+"";		
	}
	
	@GetMapping("/mainchat/messagesList") // отдаёт лист сообщений из mainChat
	public List<Message> getNumMessageList() {			
		return mainChat.messegeList;
	}
	
	@GetMapping("/mainchat/messagesList&{login}") // отдаёт лист непрочитанных сообщений из mainChat
	public List<Message> getNumMessageListByLogin(@PathVariable String login) {
		List<Message> messages = new ArrayList<Message>();
		messages.addAll(mainChat.messegeList);
		List<Message> result = new ArrayList<Message>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
		String now = LocalDate.now().format(dateFormatter);
		messageService.getListMessageByComment(login).stream()
		.filter(mes-> mes.getDatetime().split(";")[0].equals(now) ||
				mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(1).format(dateFormatter)) ||
				mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(2).format(dateFormatter)) ||
				mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(3).format(dateFormatter)) ||
				mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(4).format(dateFormatter)) ||
				mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(5).format(dateFormatter))).
			forEach(mes->result.add(mes));
		for (Message message : result) {
			message.setIdMessage(null);
			message.setStatus("1");
			message.setComment(null);			
			messages.remove(message);
		}
		return messages;
	}
	
	@GetMapping("/mainchat/messagesList/{fromUser}&{toUser}") // отдаёт list с сообщениями отопредиленного юзера к опредиленному юзеру 
	public List <Message> getMessagesListFromTo(@PathVariable String fromUser, @PathVariable String toUser) {
		List <Message> messagesList = new ArrayList<Message>();		
		mainChat.messegeList.stream()
			.filter(mes->mes.getFromUser().equals(fromUser) && mes.getToUser().equals(toUser))
			.forEach(mes-> messagesList.add(mes));		
		return messagesList;		
	}

	@PostMapping("/mainchat/massage/add")// сохраняет сообщение в бд, если есть сообщение, то не сохзраняет
	public JSONObject postSaveDBMessage(@RequestBody String str) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");		
		Message message = gson.fromJson(str, Message.class);
		message.setStatus(LocalDateTime.now().format(formatter));
		messageService.singleSaveMessage(message);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ");
		return new JSONObject(map);
	}
	
	@GetMapping("/mainchat/massages/getfromdb&{login}") // отдаёт сообщения к системе за последние 5 дней
	public List<Message> getDBMessage(@PathVariable String login) throws ParseException {		
		List<Message> result = new ArrayList<Message>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
		String now = LocalDate.now().format(dateFormatter);
		messageService.getListMessageByComment(login).stream()
			.filter(mes-> mes.getDatetime().split(";")[0].equals(now) ||
					mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(1).format(dateFormatter)) ||
					mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(2).format(dateFormatter)) ||
					mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(3).format(dateFormatter)) ||
					mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(4).format(dateFormatter)) ||
					mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(5).format(dateFormatter))).
				forEach(mes->result.add(mes));
		return result;
	}
	
	private Route addCostForRoute(Route route) {
		Set<RouteHasShop> routeHasShops = new HashSet<RouteHasShop>();
		for (RouteHasShop routeHasShop : route.getRoteHasShop()) {
			routeHasShops.add(routeHasShop);
		}
		double km = 0.0;
		for (int i = 0; i <= 50; i++) {
			RouteHasShop target1 = routeHasShops.stream().findFirst().get();
			routeHasShops.remove(target1);
			if (!routeHasShops.isEmpty()) {
				String key = target1.getShop().getNumshop() + "-"
						+ routeHasShops.stream().findFirst().get().getShop().getNumshop();
				km = km + Double.parseDouble(MainController.distances.get(key).replace(',', '.'));
			} else {
				break;
			}

		}
		Map<String, String> costMap = new HashMap<String, String>();
		List<Rates> rates = new ArrayList<Rates>();
		if (route.getTemperature() == null || route.getTemperature().equals("")) {
			for (Rates rate : ratesService.getRatesList()) {
				if (rate.getType().equals("изотерма")) {
					rates.add(rate);
				}
			}
		} else {
			for (Rates rate : ratesService.getRatesList()) {
				if (rate.getType().equals("рефрижератор")) {
					rates.add(rate);
				}
			}
		}
		int i = 1;
		for (Rates rate : rates) {
			if (Double.parseDouble(rate.getPall()) >= Double.parseDouble(route.getTotalLoadPall())
					&& Integer.parseInt(rate.getCaste()) == i) {
				if (Double.parseDouble(rate.getWeight()) >= Double.parseDouble(route.getTotalCargoWeight())) {
					double tariff = 0.0;
					if (km < 400) {
						tariff = Double.parseDouble(rate.getBefore400());
					} else {
						tariff = Double.parseDouble(rate.getAfter400());
					}
					double finalCost = km * tariff;
					costMap.put(i + "", Math.ceil(finalCost) + "");
					System.out.println(i + " = " + Math.ceil(finalCost) + " для маршрута " + route.getRouteDirection()
							+ ", где общий киллометраж - " + Math.ceil(km));
					i++;
					continue;
				}
			}
		}
		route.setCost(costMap);
		return route;
	}
}
