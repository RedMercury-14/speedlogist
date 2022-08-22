package by.base.main.controller.ajax;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import by.base.main.controller.MainController;
import by.base.main.model.Message;
import by.base.main.model.Rates;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.User;
import by.base.main.service.RatesService;
import by.base.main.service.RouteService;
import by.base.main.service.UserService;
import by.base.main.util.ChatEnpoint;

@RestController
@RequestMapping("api")
public class MainRestController {
	
	public Route route;
	
	Gson gson = new Gson();

	@Autowired
	RouteService routeService;

	@Autowired
	private RatesService ratesService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ChatEnpoint chatEnpoint;

	@GetMapping ("/route")
	public Set<Route> getListRoute() {
		Set<Route> res = new HashSet<Route>(); // расчёт стоимости!
		for (Route route : routeService.getRouteList()) { // расчёт стоимости!
			res.add(addCostForRoute(route)); // расчёт стоимости!
		}
		return res;
	}

	@GetMapping("/route/{id}")
	public Route getRoute(@PathVariable int id) {
		return addCostForRoute(routeService.getRouteById(id));
		
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
	
	@PostMapping("/route/addpoints")
	public JSONObject postRoadPoints(@RequestBody String str) throws ParseException {
		StringBuilder sb = new StringBuilder(str);
		sb.deleteCharAt(0);
		sb.deleteCharAt(str.length()-2);
		StringBuilder nsb = new StringBuilder(sb.toString());
		char[]task = sb.toString().toCharArray();
		int j = 0;
		for (int i = 0; i < task.length; i++) {			
			if (task[i] == ',') {
				j++;
				if (j%4 == 0) {
					nsb.setCharAt(i, '&');
				}				
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
			routeHasShops.add(routeHasShop);
		}
		route.setRoteHasShop(routeHasShops);	
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ");
		return new JSONObject(map);
	}
	
	
	@GetMapping("/info/message/numroute/{idRoute}")
	public String getSizeMessageByRoute(@PathVariable String idRoute) {
		List <Message> messagesList = new ArrayList<Message>();		
		chatEnpoint.internationalMessegeList.stream()
			.filter(mes->mes.getIdRoute().equals(idRoute+"")).forEach(mes-> messagesList.add(mes));		
		return messagesList.size()+"";		
	}
	
	@GetMapping("/info/message/routes/{idRoute}")
	public List<Message> getListMessegRouteById(@PathVariable String idRoute) {
		List <Message> messagesList = new ArrayList<Message>();		
		ChatEnpoint.internationalMessegeList.stream()
			.filter(mes->mes.getIdRoute().equals(idRoute+"")).forEach(mes-> messagesList.add(mes));		
		return messagesList;		
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
