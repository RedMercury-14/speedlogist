package by.base.main.controller.ajax;

import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.LIMIT;
import static com.graphhopper.json.Statement.Op.MULTIPLY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.j2objc.annotations.AutoreleasePool;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.util.CustomModel;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.JsonFeature;
import com.graphhopper.util.PointList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import by.base.main.controller.MainController;
import by.base.main.dao.OrderDAO;
import by.base.main.dto.MarketDataForClear;
import by.base.main.dto.MarketDataForLoginDto;
import by.base.main.dto.MarketDataForRequestDto;
import by.base.main.dto.MarketErrorDto;
import by.base.main.dto.MarketPacketDto;
import by.base.main.dto.MarketRequestDto;
import by.base.main.dto.MarketTableDto;
import by.base.main.dto.OrderBuyGroupDTO;
import by.base.main.model.Address;
import by.base.main.model.GeometryResponse;
import by.base.main.model.JsonResponsePolygon;
import by.base.main.model.MapResponse;
import by.base.main.model.Message;
import by.base.main.model.Order;
import by.base.main.model.Product;
import by.base.main.model.Rates;
import by.base.main.model.Role;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.Shop;
import by.base.main.model.SimpleRoute;
import by.base.main.model.Truck;
import by.base.main.model.User;
import by.base.main.service.AddressService;
import by.base.main.service.MessageService;
import by.base.main.service.OrderService;
import by.base.main.service.ProductService;
import by.base.main.service.RatesService;
import by.base.main.service.RoleService;
import by.base.main.service.RouteHasShopService;
import by.base.main.service.RouteService;
import by.base.main.service.ServiceException;
import by.base.main.service.ShopService;
import by.base.main.service.TruckService;
import by.base.main.service.UserService;
import by.base.main.service.util.CustomJSONParser;
import by.base.main.service.util.MailService;
import by.base.main.service.util.OrderCreater;
import by.base.main.service.util.POIExcel;
import by.base.main.util.ChatEnpoint;
import by.base.main.util.MainChat;
import by.base.main.util.SlotWebSocket;
import by.base.main.util.TsdWebSocket;
import by.base.main.util.GraphHopper.CustomJsonFeature;
import by.base.main.util.GraphHopper.JSpiritMachine;
import by.base.main.util.GraphHopper.RoutingMachine;
import by.base.main.util.bots.TelegramBot;
import by.base.main.util.hcolossus.ColossusProcessorANDRestrictions2;
import by.base.main.util.hcolossus.pojo.Solution;
import by.base.main.util.hcolossus.pojo.VehicleWay;
import by.base.main.util.hcolossus.service.LogicAnalyzer;
import by.base.main.util.hcolossus.service.MatrixMachine;

@RestController
@RequestMapping(path = "api", produces = "application/json")
public class MainRestController {
	
	private static FileInputStream fileInputStream = null;
	private static Properties properties = null;
	
	private static Map<Integer, Integer> stockLimits = null;

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

	@Autowired
	private RoleService roleService;

	@Autowired
	private TruckService truckService;

	@Autowired
	private MailService mailService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private RouteHasShopService routeHasShopService;

	@Autowired
	private RoutingMachine routingMachine;

	@Autowired
	private ShopService shopService;

	@Autowired
	private POIExcel poiExcel;

	@Autowired
	private TelegramBot telegramBot;

	@Autowired
	private JSpiritMachine jSpiritMachine;

	@Autowired
	private ColossusProcessorANDRestrictions2 colossusProcessorRad;

	@Autowired
	private MatrixMachine matrixMachine;

	@Autowired
	private TsdWebSocket tsdWebSocket;
	
	@Autowired
	private SlotWebSocket slotWebSocket;
	
	@Autowired
	private OrderCreater orderCreater;
	
	@Autowired
	private ProductService productService;
	
	private static String classLog;
	private static String marketJWT;
	//в отдельный файл
//	private static final String marketUrl = "https://api.dobronom.by:10806/Json";
//	private static final String serviceNumber = "BB7617FD-D103-4724-B634-D655970C7EC0";
//	private static final String loginMarket = "191178504_SpeedLogist";
//	private static final String passwordMarket = "SL!2024D@2005";
	private static final String marketUrl = "https://api.dobronom.by:10896/Json";
	private static final String serviceNumber = "CD6AE87C-2477-4852-A4E7-8BA5BD01C156";
	private static final String loginMarket = "SpeedLogist";
	private static final String passwordMarket = "12345678";


	public static final Comparator<Address> comparatorAddressId = (Address e1, Address e2) -> (e1.getIdAddress() - e2.getIdAddress());
	public static final Comparator<Address> comparatorAddressIdForView = (Address e1, Address e2) -> (e2.getType().charAt(0) - e1.getType().charAt(0));
	
	
	
	@PostMapping("/carrier/cost")
	public Map<String, String> postSetCarrierCost(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
//		User user = getThisUser();
		Map<String, String> response = new HashMap<String, String>();
		if(str == null) {
			response.put("status", "100");
			response.put("message", "Тело запроса = null");
			return response;
		}
		
		Message message = new Message();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		message.setFromUser(jsonMainObject.get("fromUser") == null ? null : jsonMainObject.get("fromUser").toString());
		message.setText(jsonMainObject.get("text") == null ? null : jsonMainObject.get("text").toString());
		message.setIdRoute(jsonMainObject.get("idRoute") == null ? null : jsonMainObject.get("idRoute").toString());
		message.setCurrency(jsonMainObject.get("currency") == null ? null : jsonMainObject.get("currency").toString());
		message.setFullName(jsonMainObject.get("fullName") == null ? null : jsonMainObject.get("fullName").toString());
		message.setStatus(jsonMainObject.get("status") == null ? null : jsonMainObject.get("status").toString());
		message.setComment(jsonMainObject.get("comment") == null ? null : jsonMainObject.get("comment").toString());
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
		message.setDatetime(LocalDateTime.now().format(formatter1));
		chatEnpoint.onMessageFromRest(message);
		response.put("status", "200");
		return response;
		
	}
	
	/**
	 * Метод меняет остатки на складах Ост на РЦ + запасники в днях
	 * @param request
	 * @param code
	 * @param stock
	 * @return
	 */
	@GetMapping("/order-support/setNewBalance/{code}&{stock}")
	public Map<String, Object> setNewBalance(HttpServletRequest request, @PathVariable String code, @PathVariable String stock) {
		Map<String, Object> response = new HashMap<String, Object>();
		Product product = productService.getProductByCode(Integer.parseInt(code.trim()));
		product.setBalanceStockAndReserves(Double.parseDouble(stock));
		productService.updateProduct(product);
		response.put("status", "200");
		response.put("message", "Данные по товaру обновлены");
		return response;		
	}
	
	/**
	 * Метод меняет Exception
	 * @param request
	 * @param code
	 * @return
	 */
	@GetMapping("/order-support/changeException/{idProduct}")
	public Map<String, Object> changeException(HttpServletRequest request, @PathVariable String idProduct) {
		Map<String, Object> response = new HashMap<String, Object>();
		Product product = productService.getProductByCode(Integer.parseInt(idProduct.trim()));
		product.setIsException(!product.getIsException());
		productService.updateProduct(product);
		response.put("status", "200");
		response.put("message", "Данные по товaру обновлены.");
		return response;		
	}
	
	/**
	 * Редактор максимального кол-ва дней для Product 
	 * @param request
	 * @param param
	 * @return
	 */
	@GetMapping("/order-support/setMaxDay/{code}&{day}")
	public Map<String, Object> setMaxDay(HttpServletRequest request, @PathVariable String code, @PathVariable String day) {
		Map<String, Object> response = new HashMap<String, Object>();
		Product product = productService.getProductByCode(Integer.parseInt(code.trim()));
		product.setDayMax(Integer.parseInt(day));
		productService.updateProduct(product);
		response.put("status", "200");
		response.put("message", "Данные по товaру обновлены");
		return response;		
	}
	
	/**
	 * проверяет статус заказа по id маршрута.
	 * @param request
	 * @return
	 */
	@GetMapping("/logistics/checkOrderForStatus/{idRoute}")
	public Map<String, String> checkOrderForStatus(HttpServletRequest request, @PathVariable String idRoute) {
		Map<String, String> response = new HashMap<String, String>();		
		Order order = orderService.getOrderByIdRoute(Integer.parseInt(idRoute));
		if(order != null) {
			response.put("status", "200");
			response.put("message", order.getStatus().toString());
		}else {
			response.put("status", "200");
			response.put("message", "0");
		}
		return response;
	}
	
	/**
	 * возвращает все SКU  manager
	 * @param request
	 * @return
	 */
	@GetMapping("/manager/getStockRemainder")
	public List<Product> getStockRemainder(HttpServletRequest request) {
		List<Product> targetRoutes = productService.getAllProductList();		
		return targetRoutes;
	}
	
	/**
	 * возвращает все SКU  manager
	 * @param request
	 * @return
	 */
	@GetMapping("/order-support/getStockRemainder")
	public List<Product> getStockRemainderSupport(HttpServletRequest request) {
		List<Product> targetRoutes = productService.getAllProductList();		
		return targetRoutes;
	}
	
	@GetMapping("/carrier/test")
	public String test(HttpServletRequest request) {
		return MainChat.messegeList.size() + "";		
	}
	
	@GetMapping("/market/clearjwt/{param}")
	public Map<String, Object> getJWTnull(HttpServletRequest request, @PathVariable String param) {
		Map<String, Object> response = new HashMap<String, Object>();
		MarketDataForClear dataDto = new MarketDataForClear(Integer.parseInt(param));
		MarketPacketDto packetDto = new MarketPacketDto(marketJWT, "CleanToken", serviceNumber, dataDto);
		MarketRequestDto requestDto = new MarketRequestDto("", packetDto);
		String str = postRequest(marketUrl, gson.toJson(requestDto));
		response.put("status", "200");
		response.put("message", str);
		return response;		
	}
	
	@GetMapping("/market/nulljwt")
	public Map<String, Object> getJWTNull(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		marketJWT = null;		
		response.put("status", "200");
		response.put("jwt", marketJWT);
		response.put("message", "JWT равен null");
		return response;		
	}
	
	@GetMapping("/market/getjwt")
	public Map<String, Object> getJWT(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		MarketDataForLoginDto dataDto = new MarketDataForLoginDto(loginMarket, passwordMarket, "101");
//		MarketDataForLoginDtoTEST dataDto = new MarketDataForLoginDtoTEST("SpeedLogist", "12345678", 101);
		MarketPacketDto packetDto = new MarketPacketDto("null", "GetJWT", serviceNumber, dataDto);
		MarketRequestDto requestDto = new MarketRequestDto("", packetDto);
		//запрашиваем jwt
		String str = postRequest(marketUrl, gson.toJson(requestDto));
		MarketTableDto marketRequestDto = gson.fromJson(str, MarketTableDto.class);
		marketJWT = marketRequestDto.getTable()[0].toString().split("=")[1].split("}")[0];		
		response.put("status", "200");
		response.put("jwt", marketJWT);
		response.put("message", "JWT обновлён");
		return response;		
	}
	
	/**
	 * Главный метод запроса ордера из маркета.
	 * Если в маркете есть - он обнавляет его в бд.
	 * Если связи с маркетом нет - берет из бд.
	 * Если нет в бд и связи с маркетом нет - выдаёт ошибку
	 * Если нет в 
	 * @param request
	 * @param idMarket
	 * @return
	 */
	@GetMapping("/manager/getMarketOrder/{idMarket}")
	public Map<String, Object> getMarketOrder(HttpServletRequest request, @PathVariable String idMarket) {
		try {			
			checkJWT(marketUrl);			
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		
		Map<String, Object> response = new HashMap<String, Object>();
		MarketDataForRequestDto dataDto3 = new MarketDataForRequestDto(idMarket);
		MarketPacketDto packetDto3 = new MarketPacketDto(marketJWT, "SpeedLogist.GetOrderBuyInfo", serviceNumber, dataDto3);
		MarketRequestDto requestDto3 = new MarketRequestDto("", packetDto3);
		String marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto3));
		
		if(marketOrder2.equals("503")) { // означает что связь с маркетом потеряна
			//в этом случае проверяем бд
			System.err.println("Связь с маркетом потеряна");
			Order order = orderService.getOrderByMarketNumber(idMarket);
			marketJWT = null; // сразу говорим что jwt устарел
			if(order != null) {
				response.put("status", "200");
				response.put("message", "Заказ загружен из локальной базы данных SL. Связь с маркетом отсутствует");
				response.put("order", order);
				return response;
			}else {
				response.put("status", "100");
				response.put("message", "Заказ с номером " + idMarket + " в базе данных SL не найден. Связь с Маркетом отсутствует. Обратитесь в отдел ОСиУЗ");
				return response;
			}
			
		}else{//если есть связь с маркетом
			//проверяем на наличие сообщений об ошибке со стороны маркета
			if(marketOrder2.contains("Error")) {
				MarketErrorDto errorMarket = gson.fromJson(marketOrder2, MarketErrorDto.class);
//				System.out.println(errorMarket);
				if(errorMarket.getError().equals("99")) {//обработка случая, когда в маркете номера нет, а в бд есть.
					Order orderFromDB = orderService.getOrderByMarketNumber(idMarket);
					if(orderFromDB !=null) {
						response.put("status", "100");
						response.put("message", "Заказ " + idMarket + " не найден в маркете. Данные из SL устаревшие. Обновите данные в Маркете");
						return response;
					}else {
						response.put("status", "100");
						response.put("message", errorMarket.getErrorDescription());
						return response;
					}
				}
				response.put("status", "100");
				response.put("message", errorMarket.getErrorDescription());
				return response;
			}
			
			//тут избавляемся от мусора в json
			String str2 = marketOrder2.split("\\[", 2)[1];
			String str3 = str2.substring(0, str2.length()-2);
			
			//создаём свой парсер и парсим json в объекты, с которыми будем работать.
			CustomJSONParser customJSONParser = new CustomJSONParser();
			OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(str3);
			
			//создаём Order, записываем в бд и возвращаем или сам ордер или ошибку (тот же ордер, только с отрицательным id)
			Order order = orderCreater.create(orderBuyGroupDTO);
			if(order.getIdOrder() < 0) {
				response.put("status", "100");
				response.put("message", order.getMessage());
				return response;
			}else {
				response.put("status", "200");
				response.put("message", order.getMessage());
				response.put("order", order);
				return response;
			}
		}		
				
	}
	
	/**
	 * Метод проверки наличия jwt токена. Должен находится перед каждём запросом в маркет. 
	 * @return
	 */
	private void checkJWT(String url) {
		MarketDataForLoginDto dataDto = new MarketDataForLoginDto(loginMarket, passwordMarket, "101");
//		MarketDataForLoginDtoTEST dataDto = new MarketDataForLoginDtoTEST("SpeedLogist", "12345678", 101);
		MarketPacketDto packetDto = new MarketPacketDto("null", "GetJWT", serviceNumber, dataDto);
		MarketRequestDto requestDto = new MarketRequestDto("", packetDto);
		if(marketJWT == null){
			//запрашиваем jwt
			String str = postRequest(url, gson.toJson(requestDto));
			MarketTableDto marketRequestDto = gson.fromJson(str, MarketTableDto.class);
			marketJWT = marketRequestDto.getTable()[0].toString().split("=")[1].split("}")[0];
		}
	}
	
	/**
	 * Метод редактирования поля "Информация" в заявке, для таблицы слотов.
	 * @param request
	 * @param idOrder
	 * @param text
	 * @return
	 */
	@GetMapping("/manager/editMarketInfo/{idOrder}&{text}")
	public Map<String, String> getEditMarketInfo(HttpServletRequest request, @PathVariable String idOrder, @PathVariable String text) {
		Map<String, String> response = new HashMap<String, String>();
		Order order = orderService.getOrderById(Integer.parseInt(idOrder.trim()));
		if(order == null) {
			response.put("status", "100");
			response.put("message", "Заказ не найден");
			return response;
		}
		order.setMarketInfo(cleanXSS(text));
		orderService.updateOrder(order);
		response.put("status", "200");
		response.put("message", "Комментарий изменен");
		return response;
	}
	
	/**
	 * отдаёт все маршруты для новой страницы менеджер международных маршрутов
	 * @param request
	 * @param dateStart 2024-03-15
	 * @param dateFinish 2024-03-15
	 * @return
	 */
	@GetMapping("/manager/getRouteForInternational/{dateStart}&{dateFinish}")
	public Set<Route> getRouteForInternational(HttpServletRequest request, @PathVariable Date dateStart, @PathVariable Date dateFinish) {
		Set<Route> routes = new HashSet<Route>();
		List<Route>targetRoutes = routeService.getRouteListAsDate(dateStart, dateFinish);
		targetRoutes.stream()
			.filter(r-> r.getComments() != null && r.getComments().equals("international") && Integer.parseInt(r.getStatusRoute())<=8)
			.forEach(r -> routes.add(r)); // проверяет созданы ли точки вручную, и отдаёт только международные маршруты		
		return routes;
	}
	
	@GetMapping("/carrier/getStatusTenderForMe")
	public Set<Route> getStatusTenderForMe(HttpServletRequest request) {
		User user = getThisUser();
		Set<Route> result = new HashSet<Route>();
		List<Route> vin = routeService.getRouteListByUserHasPeriod(user, LocalDate.now().minusDays(15), LocalDate.now().plusDays(15)); // получаем выйгранные тендеры
		List<Route> routeListParticipated = routeService.getRouteListParticipated(user); // получаем список всех тендеров, выгранных и не выйгранных
		vin.forEach(r->{
			r.setOptimalCost(null);
			r.setCostWay(null);
			r.setRoteHasShop(null);
			r.setTruck(null);
			r.setCost(null);
			r.setStatusRoute("green");
			r.setCustomer(null);
			r.setStartCurrency(null);
			r.setFinishPrice(0);
			result.add(r);
		});
		routeListParticipated.forEach(r->{
			if(r.getUser() != null && !r.getUser().equals(user)) {
				r.setOptimalCost(null);
				r.setCostWay(null);
				r.setRoteHasShop(null);
				r.setTruck(null);
				r.setCost(null);
				r.setStatusRoute("red");
				r.setCustomer(null);
				r.setStartCurrency(null);
				r.setFinishPrice(0);
				result.add(r);
			}else if(r.getUser() == null){
				r.setOptimalCost(null);
				r.setCostWay(null);
				r.setRoteHasShop(null);
				r.setTruck(null);
				r.setCost(null);
				r.setStatusRoute("white");
				r.setCustomer(null);
				r.setStartCurrency(null);
				r.setFinishPrice(0);
				result.add(r);
			}
		});
		return result;
	}
	
	@GetMapping("/message")
	public List<Order> message(HttpServletRequest request) {
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
		return orders;
	}
	
	@PostMapping("/slot/save")
	public Map<String, String> postSlotSave(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		java.util.Date t1 = new java.util.Date();
		User user = getThisUser();
		String role = user.getRoles().stream().findFirst().get().getAuthority();
		Map<String, String> response = new HashMap<String, String>();
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
		properties = new Properties();
		properties.load(fileInputStream);
		if(role.equals("ROLE_TOPMANAGER") || role.equals("ROLE_MANAGER")) {
			response.put("status", "100");
			response.put("message", "Неправомерный запрос от роли логиста");
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idOrder = jsonMainObject.get("idOrder") != null ? Integer.parseInt(jsonMainObject.get("idOrder").toString()) : null;
		if(idOrder == null) {
			response.put("status", "100");
			response.put("message", "Ошибка. Не пришел idOrder");
			return response;
		}
		Order order = orderService.getOrderById(idOrder);
//		//проверка на лимиты товара
//				String checkMessage = checkNumProductHasStock(order, order.getTimeDelivery());		
//				if(checkMessage != null) {
//					response.put("status", "105");
//					response.put("message", checkMessage);
//					System.err.println("Не прошла проверку по лимитам товара");
//					System.out.println(checkMessage);
//					return response;
//				}
		
		switch (order.getStatus()) {
		case 8: // от поставщиков
			order.setStatus(100);
			orderService.updateOrder(order);
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "save");
			Message message = new Message(user.getLogin(), null, "200", str, idOrder.toString(), "save");
			slotWebSocket.sendMessage(message);	
			java.util.Date t2 = new java.util.Date();
			System.out.println(t2.getTime()-t1.getTime() + " ms - save" );
			response.put("status", "200");
			response.put("message", str);			
			return response;
		case 7: // сакмовывоз
			order.setStatus(20);
			order.setChangeStatus("Создал: " + user.getSurname() + " " + user.getName() + " " + user.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
			orderService.updateOrder(order);
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "save");
			Message message7 = new Message(user.getLogin(), null, "200", str, idOrder.toString(), "save");
			slotWebSocket.sendMessage(message7);	
			java.util.Date t3 = new java.util.Date();
			System.out.println(t3.getTime()-t1.getTime() + " ms - save" );
			String text = "Создана заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+"; \nСлот на выгркузку: "+ order.getTimeDelivery() +"; " +
					"\nНаправление: " + order.getWay();
			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", text, properties.getProperty("email.addNewProcurement.rb.1"), properties.getProperty("email.addNewProcurement.rb.2"));
			response.put("status", "200");
			response.put("message", str);			
			return response;			
		case 100: // сакмовывоз
			order.setStatus(8);
			orderService.updateOrder(order);
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "unsave");
			Message message100 = new Message(user.getLogin(), null, "200", str, idOrder.toString(), "unsave");
			slotWebSocket.sendMessage(message100);	
			java.util.Date t4 = new java.util.Date();
			System.out.println(t4.getTime()-t1.getTime() + " ms - save" );
			response.put("status", "200");
			response.put("message", str);			
			return response;

		default:
			response.put("status", "100");
			response.put("message", "Ошибка в статусах. Ожидается статусы 7, 8, или 100");			
			return response;
		}
	}
	
	/**
	 * Метод удаления ивента / слота на рампах
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@PostMapping("/slot/delete")
	public Map<String, String> postSlotDelete(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		java.util.Date t1 = new java.util.Date();
		User user = getThisUser();
		Map<String, String> response = new HashMap<String, String>();
		String role = user.getRoles().stream().findFirst().get().getAuthority();
		if(role.equals("ROLE_TOPMANAGER") || role.equals("ROLE_MANAGER")) {
			response.put("status", "100");
			response.put("message", "Неправомерный запрос от роли логиста");
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idOrder = jsonMainObject.get("idOrder") != null ? Integer.parseInt(jsonMainObject.get("idOrder").toString()) : null;
		if(idOrder == null) {
			response.put("status", "100");
			response.put("message", "Ошибка. Не пришел idOrder");
			return response;
		}
		Order order = orderService.getOrderById(idOrder);
//		System.out.println(order.toString());
		Integer oldIdRamp = order.getIdRamp();
		Timestamp oldTimeDelivery = order.getTimeDelivery();
		
		if(order.getStatus() <= 7) {
			order.setTimeDelivery(null);
			order.setIdRamp(null);
			order.setLoginManager(null);
			order.setStatus(6);	
			order.setChangeStatus("\nОтменил в слотах: " + user.getSurname() + " " + user.getName() + " " + user.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
			orderService.updateOrder(order);
			java.util.Date t2 = new java.util.Date();
			Message message = new Message(user.getLogin(), null, "200", str, idOrder.toString(), "delete");
			slotWebSocket.sendMessage(message);	
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), oldIdRamp, null, oldTimeDelivery, null, user.getLogin(), "delete");
			System.out.println(t2.getTime()-t1.getTime() + " ms - del" );
			response.put("status", "200");
			response.put("message", str);
			return response;
		}else if(order.getStatus() == 8){
			order.setTimeDelivery(null);
			order.setIdRamp(null);
			order.setLoginManager(null);
			order.setStatus(5);		
			orderService.updateOrder(order);
			java.util.Date t2 = new java.util.Date();
			Message message = new Message(user.getLogin(), null, "200", str, idOrder.toString(), "delete");
			slotWebSocket.sendMessage(message);	
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), oldIdRamp, null, oldTimeDelivery, null, user.getLogin(), "delete");
			System.out.println(t2.getTime()-t1.getTime() + " ms - del" );
			response.put("status", "200");
			response.put("message", str);
			return response;
		}
		else if(order.getStatus() == 100){
			order.setTimeDelivery(null);
			order.setIdRamp(null);
			order.setLoginManager(null);
			order.setStatus(5);		
			orderService.updateOrder(order);
			java.util.Date t2 = new java.util.Date();
			Message message = new Message(user.getLogin(), null, "200", str, idOrder.toString(), "delete");
			slotWebSocket.sendMessage(message);	
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), oldIdRamp, null, oldTimeDelivery, null, user.getLogin(), "delete");
			System.out.println(t2.getTime()-t1.getTime() + " ms - del" );
			response.put("status", "200");
			response.put("message", str);
			return response;
		}else {
			response.put("status", "100");
			response.put("message", "Невозможно удалить заказ из плана выгрузки, т.к. оформлена заявка на поиск транспорта");
			return response;
		}				
	}
	
	/**
	 * Метод обновления ивента / слота на рампах
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@PostMapping("/slot/update")
	public Map<String, String> postSlotUpdate(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		java.util.Date t1 = new java.util.Date();
		
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/stock.properties");
		Properties propertiesStock = new Properties();
		propertiesStock.load(fileInputStream);
		
		User user = getThisUser();
		String role = user.getRoles().stream().findFirst().get().getAuthority();
		Map<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idOrder = jsonMainObject.get("idOrder") != null ? Integer.parseInt(jsonMainObject.get("idOrder").toString()) : null;
		if(idOrder == null) {
			response.put("status", "100");
			response.put("message", "Ошибка. Не пришел idOrder");
			return response;
		}
		Order order = orderService.getOrderById(idOrder);
		Integer oldIdRamp = order.getIdRamp();
		Timestamp oldTimeDelivery = order.getTimeDelivery();
		Timestamp timestamp = Timestamp.valueOf(jsonMainObject.get("timeDelivery").toString());
		Integer idRamp = Integer.parseInt(jsonMainObject.get("idRamp").toString());
		order.setTimeDelivery(timestamp);
		order.setIdRamp(idRamp);
		//указываем какие роли перезаписывают LoginManager
		if(role.equals("ROLE_PROCUREMENT") || role.equals("ROLE_ORDERSUPPORT")) {
			order.setLoginManager(user.getLogin());
		}		
		order.setStatus(order.getStatus());			
		switch (role) {
		case "ROLE_MANAGER":
			String messageManager = jsonMainObject.get("messageLogist") == null ? null : jsonMainObject.get("messageLogist").toString();
			String fullMessageManager = "Слот перемещен с рампы " + oldIdRamp +" на рампу " + order.getIdRamp() + " со времени " + oldTimeDelivery + " на новое время " + order.getTimeDelivery() + 
					" сотрудником " + user.getSurname() + " " + user.getName() + " по причине: " + messageManager + "\n";
			order.setSlotInfo(fullMessageManager);
			break;
		case "ROLE_TOPMANAGER":
			String messageTopManager = jsonMainObject.get("messageLogist") == null ? null : jsonMainObject.get("messageLogist").toString();
			String fullMessageTopManager = "Слот перемещен с рампы " + oldIdRamp +" на рампу " + order.getIdRamp() + " со времени " + oldTimeDelivery + " на новое время " + order.getTimeDelivery() + 
					" сотрудником " + user.getSurname() + " " + user.getName() + " по причине: " + messageTopManager + "\n";
			order.setSlotInfo(fullMessageTopManager);
			break;
		}
		//главные проверки
		//проверка на лимит приемки паллет
		if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false")) { // проверяем всё кроме вн перемещений
			Integer summPall = orderService.getSummPallInStockExternal(order);
			Integer summPallNew =  summPall + Integer.parseInt(order.getPall().trim());
			String propKey = "limit." + getTrueStock(order);
			if(summPallNew > Integer.parseInt(propertiesStock.getProperty(propKey))) {						
				response.put("status", "100");
				response.put("message", "Ошибка. Превышен лимит по паллетам на текущую дату");
				System.err.println("Не прошла проверку по лимитам паллет склада");
				return response;
			}			
		}
		
		
		
//		//отдельно проверяем на внутренние перемещение и все остальные
//		if(order.getIsInternalMovement() != null && order.getIsInternalMovement().equals("true")) {
//			Integer summPall = orderService.getSummPallInStockInternal(order);
//			Integer summPallNew =  summPall + Integer.parseInt(order.getPall().trim());
//			String propKey = "limit.movment." + getTrueStock(order);
//			if(summPallNew > Integer.parseInt(propertiesStock.getProperty(propKey))) {
//				response.put("status", "100");
//				response.put("message", "Ошибка. Превышен лимит по паллетам на текущую дату");
//				return response;
//			}
//		}else {
//			Integer summPall = orderService.getSummPallInStockExternal(order);
//			Integer summPallNew =  summPall + Integer.parseInt(order.getPall().trim());
//			String propKey = "limit." + getTrueStock(order);
//			if(summPallNew > Integer.parseInt(propertiesStock.getProperty(propKey))) {						
//				response.put("status", "100");
//				response.put("message", "Ошибка. Превышен лимит по паллетам на текущую дату");
//				return response;
//			}
//		}
		//проверка на лимиты товара
		String checkMessage = checkNumProductHasStock(order, timestamp);		
		if(checkMessage != null) {
			response.put("status", "105");
			response.put("message", checkMessage);
			System.err.println("Не прошла проверку по лимитам товара");
			return response;
		}
		
		String errorMessage = orderService.updateOrderForSlots(order);//проверка на пересечение со временим других слотов и лимит складов
		java.util.Date t2 = new java.util.Date();
		System.out.println(t2.getTime()-t1.getTime() + " ms - update" );
		if(errorMessage!=null) {
			response.put("status", "100");
			response.put("message", errorMessage);
			System.err.println("Не прошла проверку по пересечениям слотов");
			return response;
		}else {
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), oldIdRamp, order.getIdRamp(), oldTimeDelivery, order.getTimeDelivery(), user.getLogin(), "update");
			if(order.getRoutes() != null) {
				order.getRoutes().forEach(r->{
					r.setDateUnloadPreviouslyStock(order.getTimeDelivery().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
					r.setTimeUnloadPreviouslyStock(order.getTimeDelivery().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
					routeService.saveOrUpdateRoute(r);
				});
			}
			Message message = new Message(user.getLogin(), null, "200", str, idOrder.toString(), "update");
			slotWebSocket.sendMessage(message);	
			response.put("status", "200");
			response.put("message", str);
			return response;	
		}			
	}
	
	
	/**
	 * Метод для проверки кол-ва товара на текущий день
	 * если всё ок, возвращает null, если что то не то - сообщение
	 * @return
	 */
	private String checkNumProductHasStock(Order order, Timestamp timeDelivery) {
		String message = null;
		User user = getThisUser();
		Role role = user.getRoles().stream().findFirst().get();
		if(role.getIdRole() == 1 || role.getIdRole() == 2 || role.getIdRole() == 3) { // тут мы говорим что если это логист или админ - в проверке не нуждаемся
			return null;
		}
		if(order.getIsInternalMovement() != null && order.getIsInternalMovement().equals("true")) {
			return null;
		}
		if(order.getNumProduct() == null) {
			message = "Данные по заказу " + order.getMarketNumber() + " устарели! Обновите заказ.";
//			return message; временно отключил
			return null;
		}
		
		String [] numProductMass = order.getNumProduct().split("\\^");
		
		
		for (String string : numProductMass) {
			Product product = productService.getProductByCode(Integer.parseInt(string));
			
			if(product != null) {
				if(product.getBalanceStockAndReserves() == 9999.0) {
					continue;
				}
				if(product.getRemainderStockInPall() < 33.0) { //если в паллетах товара меньшге чем 33 - то пропускаем
					continue;
				}
				//считаем разницу в днях сегодняшнеего дня и непосредственно записи
				LocalDateTime start = timeDelivery.toLocalDateTime();
				LocalDateTime end = LocalDateTime.of(product.getDateUnload().toLocalDate(), LocalTime.now());

				Duration duration = Duration.between(start, end);
				Double currentDate = (double) duration.toDays();
				// считаем правильный остаток на текущий день
				Double trueBalance = product.getBalanceStockAndReserves() + currentDate;
				
				
				if(!product.getIsException()) {
					if(trueBalance > product.getDayMax()) {
						//считаем сколько дней нужно прибавить, чтобы заказать товар
						Long deltDate = (long) (trueBalance - product.getDayMax() + 1);
						if(message == null) {
							message = "Товара " + product.getCodeProduct() + " ("+product.getName()+")" + " на складе хранится на " + trueBalance + " дней. Ограничение стока по данному товару: " + product.getDayMax() + " дней."
									+"Ближайшая дата на которую можно доставить данный товар: " + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy")) + "\n";
						}else {
							message = message + "\nТовара " + product.getCodeProduct() + " ("+product.getName()+")" + " на складе хранится на " + trueBalance + " дней. Ограничение стока по данному товару: " + product.getDayMax() + " дней. "
									+"Ближайшая дата на которую можно доставить данный товар: " + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy"))+ "\n";
						}
						 
					}
				}
				
			}
		}
		return message;
	}
	
	/**
	 * Метод возвращает номер склада из idRump
	 * @param order
	 * @return
	 */
	private String getTrueStock(Order order) {
		String numStock = null;
		if(order.getIdRamp().toString().length() < 5) {
			System.err.println("Ошибка в названии склада. Склад не может быть двухзначным");
		}
		if(order.getIdRamp().toString().length() < 6) { // проверка на будующее если будет учавстовать склад с трехзначным индексом
			numStock = order.getIdRamp().toString().substring(0, 3);
		}else {
			numStock = order.getIdRamp().toString().substring(0, 4);
		}
		return numStock;		
	}
	
	/**
	 * Метод добавления ивента / слота на рампу
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@PostMapping("/slot/load")
	public Map<String, String> postSlotLoad(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		java.util.Date t1 = new java.util.Date();
		
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/stock.properties");
		Properties propertiesStock = new Properties();
		propertiesStock.load(fileInputStream);
		
		User user = getThisUser();	
		Map<String, String> response = new HashMap<String, String>();
		String role = user.getRoles().stream().findFirst().get().getAuthority();
		if(role.equals("ROLE_TOPMANAGER") || role.equals("ROLE_MANAGER")) {
			response.put("status", "100");
			response.put("message", "Неправомерный запрос от роли логиста");
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idOrder = jsonMainObject.get("idOrder") != null ? Integer.parseInt(jsonMainObject.get("idOrder").toString()) : null;
		if(idOrder == null) {
			response.put("status", "100");
			response.put("message", "Ошибка. Не пришел idOrder");
			return response;
		}
		Order order = orderService.getOrderById(idOrder);
		if(order.getLoginManager() != null) {//обработка одновременного вытягивания объекта из дроп зоны
			response.put("status", "100");
			response.put("message", "Ошибка доступа. Заказ не зафиксирован. Данный заказ уже поставлен другим пользователем");
			return response;
		}
		if(order.getStatus() == 6 && Integer.parseInt(jsonMainObject.get("status").toString()) == 8) {
			//означает что манагер заранее создал маршрут с 8 статусом а потом создал заявку на него
			response.put("status", "100");
			response.put("message", "Вы пытаетесь установить слот от поставщика как слот на самовывоз.");
			return response;
		}
		Timestamp timestamp = Timestamp.valueOf(jsonMainObject.get("timeDelivery").toString());
		Integer idRamp = Integer.parseInt(jsonMainObject.get("idRamp").toString());
		order.setTimeDelivery(timestamp);
		order.setIdRamp(idRamp);
		order.setLoginManager(user.getLogin());
		order.setStatus(jsonMainObject.get("status") == null ? 7 : Integer.parseInt(jsonMainObject.get("status").toString()));
		//главные проверки
		//проверка на лимит приемки паллет	
		if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false")) { // проверяем всё кроме вн перемещений
			Integer summPall = orderService.getSummPallInStockExternal(order);
//			System.out.println("Сумма паллет обычного заказа = " + summPall);
			Integer summPallNew =  summPall + Integer.parseInt(order.getPall().trim());
			String propKey = "limit." + getTrueStock(order);
			if(summPallNew > Integer.parseInt(propertiesStock.getProperty(propKey))) {
				response.put("status", "100");
				response.put("message", "Ошибка. Превышен лимит по паллетам на текущую дату");
				System.err.println("Не прошла проверку по лимитам паллет склада");
				return response;
			}
		}
		
		
		//отдельно проверяем на внутренние перемещение и все остальные
//		if(order.getIsInternalMovement() != null && order.getIsInternalMovement().equals("true")) {
//			Integer summPall = orderService.getSummPallInStockInternal(order);
//			System.out.println("Сумма паллет перемещение = " + summPall);
//			Integer summPallNew =  summPall + Integer.parseInt(order.getPall().trim());
//			String propKey = "limit.movment." + getTrueStock(order);
//			if(summPallNew > Integer.parseInt(propertiesStock.getProperty(propKey))) {
//				response.put("status", "100");
//				response.put("message", "Ошибка. Превышен лимит по паллетам на текущую дату");
//				return response;
//			}
//		}else {
//			Integer summPall = orderService.getSummPallInStockExternal(order);
//			System.out.println("Сумма паллет обычного заказа = " + summPall);
//			Integer summPallNew =  summPall + Integer.parseInt(order.getPall().trim());
//			String propKey = "limit." + getTrueStock(order);
//			if(summPallNew > Integer.parseInt(propertiesStock.getProperty(propKey))) {
//				response.put("status", "100");
//				response.put("message", "Ошибка. Превышен лимит по паллетам на текущую дату");
//				return response;
//			}
//		}
		
		//конец проверки на лимит приемки
		
		//проверка на лимиты товара
				String checkMessage = checkNumProductHasStock(order, timestamp);		
				if(checkMessage != null) {
					response.put("status", "105");
					response.put("message", checkMessage);
					System.err.println("Не прошла проверку по лимитам товара");
					return response;
				}
		
		String errorMessage = orderService.updateOrderForSlots(order);//проверка на пересечение со временим других слотов
		java.util.Date t2 = new java.util.Date();
		System.out.println(t2.getTime()-t1.getTime() + " ms - load" );
		if(errorMessage != null) {
			response.put("status", "100");
			response.put("message", errorMessage);
			System.err.println("Не прошла проверку по лимитам паллет склада");
			return response;
		}else {
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), null, order.getIdRamp(), null, order.getTimeDelivery(), user.getLogin(), "load");
			Message message = new Message(user.getLogin(), null, "200", str, idOrder.toString(), "load");
			slotWebSocket.sendMessage(message);	
			response.put("status", "200");
			response.put("message", str);
			return response;	
		}			
	}
	/**
	 * Метод отвечает за загрузку остатков на складах
	 * @param model
	 * @param request
	 * @param session
	 * @param excel
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/order-support/control/490", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, String> postOrderSupportLoad490(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "excel", required = false) MultipartFile excel)
			throws InvalidFormatException, IOException, ServiceException {
		Map<String, String> response = new HashMap<String, String>();	
		File file1 = poiExcel.getFileByMultipartTarget(excel, request, "490.xlsx");
//		String text = poiExcel.testHeaderOrderHasExcel(file1);
		String text;
//		if(text != null) {
//			response.put("150", text);
//			return response;
//		}
		//основной метод загрузки в БД
		text = poiExcel.loadBalanceStock(file1, request);
		
		
		response.put("200", text);
		return response;
	}
	
	/**
	 * Метод отвечает за загрузку и создание Заказов
	 * @param model
	 * @param request
	 * @param session
	 * @param excel
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/order-support/control/487", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, String> postOrderSupportLoad487(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "excel", required = false) MultipartFile excel)
			throws InvalidFormatException, IOException, ServiceException {
		Map<String, String> response = new HashMap<String, String>();	
		File file1 = poiExcel.getFileByMultipartTarget(excel, request, "487.xlsx");
//		String text = poiExcel.testHeaderOrderHasExcel(file1);
		String text;
//		if(text != null) {
//			response.put("150", text);
//			return response;
//		}
		//основной метод создания заказов со статусом 5
		text = poiExcel.loadOrderHasExcelV2(file1, request);
//		text = poiExcel.loadBalanceStock(file1, request);
		
		
		response.put("200", text);
//		System.out.println(text);
		return response;
	}
	
	/**
	 * Метод отвечает за проверку кода из маркета: если маршрут с таким кодом в бд и не со статусом 10 то возвращает true
	 * @param request
	 * @param number
	 * @return
	 */
	@GetMapping("/procurement/checkMarketCode/{number}")
	public Map<String, String> checkMarketCode(HttpServletRequest request, @PathVariable String number) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("status", "200");
		response.put("message", orderService.checkOrderHasMarketCode(number)+"");
		return response;
	}
	
	/**
	 * Отдаёт заявки по номеру из маркета
	 * @param request
	 * @param number
	 * @return
	 */
	@GetMapping("/procurement/getOrderHasMarketNumber/{number}")
	public Order getOrderHasMarketNumber(HttpServletRequest request, @PathVariable String number) {
//		Map<String, String> response = new HashMap<String, String>();
		return orderService.getOrderByMarketNumber(number);
	}
	
	@PostMapping("/map/myoptimization3")
	public Solution myOptimization3(@RequestBody String str) throws Exception {
		Double maxKoef = 1.45;
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("shops");
		JSONArray pallHasShopsJSON = (JSONArray) jsonMainObject.get("palls");
		JSONArray tonnageHasShopsJSON = (JSONArray) jsonMainObject.get("tonnage");

		List<Integer> numShops = new ArrayList<Integer>();
		List<Integer> pallHasShops = new ArrayList<Integer>();
		List<Integer> tonnageHasShops = new ArrayList<Integer>();

		Integer stock = Integer.parseInt(jsonMainObject.get("stock").toString());

		numShopsJSON.forEach(s -> numShops.add(Integer.parseInt(s.toString())));
		pallHasShopsJSON.forEach(p -> pallHasShops.add(Integer.parseInt(p.toString())));
		tonnageHasShopsJSON.forEach(t-> tonnageHasShops.add(Integer.parseInt(t.toString())));
		
		List<Solution> solutions = new ArrayList<Solution>();
		
		//реализация перебора первого порядка
		for (double i = 1.45; i <= maxKoef; i = i + 0.02) {
			Double koeff = i;
//			System.out.println("Коэфф = " + koeff);
			Solution solution = colossusProcessorRad.run(jsonMainObject, numShops, pallHasShops, tonnageHasShops, stock, koeff, "fullLoad");

			// строим маршруты для отправки клиенту

			// в этой мате ключ это id самого маршрута, т.е. WhiteWay, а значение это сам
			// маршрут
	
			solution.getWhiteWay().forEach(w -> {
				List<Shop> newPoints = logicAnalyzer.correctRouteMaker(w.getWay());				
				VehicleWay way = w;
				way.setWay(newPoints);
			});
			solution.setKoef(koeff);
			solutions.add(solution);
		}
		//второй порядок
//		for (double i = 1.01; i <= maxKoef; i = i + 0.02) {
//			Double koeff = i;
////			System.out.println("Коэфф = " + koeff);
//			Solution solution = colossusProcessorRad.run(jsonMainObject, numShops, pallHasShops, stock, koeff, "noFullLoad");
//
//			// строим маршруты для отправки клиенту
//
//			// в этой мате ключ это id самого маршрута, т.е. WhiteWay, а значение это сам
//			// маршрут
//
//			solution.getWhiteWay().forEach(w -> {
//				List<Shop> newPoints = logicAnalyzer.correctRouteMaker(w.getWay());
//				VehicleWay way = w;
//				way.setWay(newPoints);
//			});
//			solutions.add(solution);
//		}
		
//		System.err.println(solutions.size());
//		solutions.forEach(s-> System.out.println(s.getTotalRunSolution()));
		Double minOwerrun = 999999999999999999.0;
		int emptyShop = 9999;
		Solution finalSolution = null;
		for (Solution solution2 : solutions) {
			
			//определяем и записываем суммарный пробег маршрута
			//!!!!!!записываем внутри процессора!
//			Double totalRunHasMatrix = 0.0;
//			for (VehicleWay way : solution2.getWhiteWay()) {
//				//заменяем просчёт расстояний из GH на матричный метод			
//				for (int j = 0; j < way.getWay().size()-1; j++) {
//					String key = way.getWay().get(j).getNumshop()+"-"+way.getWay().get(j+1).getNumshop();
//					totalRunHasMatrix = totalRunHasMatrix + matrixMachine.matrix.get(key);
//				}
//			}
//			solution2.setTotalRunKM(totalRunHasMatrix);
			int summpall = 0;
			for (VehicleWay way : solution2.getWhiteWay()) {
				Shop stock123 = way.getWay().get(0);
				summpall = summpall + calcPallHashHsop(way.getWay(), stock123);
				way.setSummPall(summpall);
			}
			System.err.println("Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef() + "; Паллеты: " + summpall);
			if(solution2.getEmptyShop().size() <= emptyShop) {
				if(solution2.getEmptyShop().size() < emptyShop && minOwerrun < solution2.getTotalRunKM()) {
					System.out.println("Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef()+ "; Паллеты: " + summpall);
					minOwerrun = solution2.getTotalRunKM();
					emptyShop = solution2.getEmptyShop().size();
					finalSolution = solution2;
				}
				
				if(solution2.getTotalRunKM() < minOwerrun) {
					System.out.println("Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef()+ "; Паллеты: " + summpall);
					minOwerrun = solution2.getTotalRunKM();
					emptyShop = solution2.getEmptyShop().size();
					finalSolution = solution2;
				}
			}
		}
		Map<String, List<MapResponse>> wayHasMap = new HashMap<String, List<MapResponse>>();
		finalSolution.getWhiteWay().forEach(way -> {
			List<GHRequest> ghRequests = null;
			try {
				ghRequests = routingMachine.createrListGHRequest(way.getWay());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<Shop[]> shopPoints = null;
			try {
				shopPoints = routingMachine.getShopAsWay(way.getWay());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GraphHopper hopper = routingMachine.getGraphHopper();
//			ghRequests.forEach(r->System.out.println(r.getCustomModel()));
			List<MapResponse> listResult = new ArrayList<MapResponse>();
			for (GHRequest req : ghRequests) {
				int index = ghRequests.indexOf(req);

				GHResponse rsp = hopper.route(req);
				if (rsp.getAll().isEmpty()) {
					rsp.getErrors().forEach(e -> System.out.println(e));
					rsp.getErrors().forEach(e -> e.printStackTrace());
					listResult.add(new MapResponse(null, null, null, 500.0, 500));
				}
//				System.err.println(rsp.getAll().size());
				if (rsp.getAll().size() > 1) {
					rsp.getAll().forEach(p -> System.out.println(p.getDistance() + "    " + p.getTime()));
				}
				ResponsePath path = rsp.getBest();
				List<ResponsePath> listPath = rsp.getAll();
				for (ResponsePath pathI : listPath) {
					if (pathI.getDistance() < path.getDistance()) {
						path = pathI;
					}
				}
//				System.out.println(roundВouble(path.getDistance()/1000, 2) + "km, " + path.getTime() + " time");
				PointList pointList = path.getPoints();
				path.getPathDetails();
				List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
				pointList.forEach(p -> result.add(p.toGeoJson()));
				List<Double[]> resultPoints = new ArrayList<Double[]>();
				double cash = 0.0;
				for (Double[] point : result) {
					cash = point[0];
					point[0] = point[1];
					point[1] = cash;
					resultPoints.add(point);
				}
				listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
						shopPoints.get(index)[0], shopPoints.get(index)[1]));
			}
			wayHasMap.put(way.getId(), listResult);
		});
		finalSolution.setMapResponses(wayHasMap);
		
		return finalSolution;


	}
	
	/**
	 * Метод добавляет адрес к заявкам, которые создаются без точек выгрузок
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	@PostMapping("/stock-support/addAdress")
	public Map<String, String> getAddressForImport(HttpServletRequest request, @RequestBody String str) throws ParseException {
		Map<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idOrder = jsonMainObject.get("idOrder") != null ? Integer.parseInt(jsonMainObject.get("idOrder").toString()) : null;
		if(idOrder == null) {
			response.put("status", "100");
			response.put("message", "Ошибка. Не пришел idOrder");
			return response;
		}
		Order order = orderService.getOrderById(idOrder);
		String unloadPoint = jsonMainObject.get("unloadPoint").toString();
		JSONObject jsonpObject = (JSONObject) parser.parse(unloadPoint);
		Address address = new Address((String) jsonpObject.get("bodyAdress"),
				jsonpObject.get("date").toString().isEmpty() ? null
						: Date.valueOf((String) jsonpObject.get("date")),
				(String) jsonpObject.get("type"),
				jsonpObject.get("pall").toString().isEmpty() ? null : (String) jsonpObject.get("pall"),
				jsonpObject.get("weight").toString().isEmpty() ? null : (String) jsonpObject.get("weight"),
				jsonpObject.get("volume").toString().isEmpty() ? null : (String) jsonpObject.get("volume"),
				jsonpObject.get("timeFrame").toString().isEmpty() ? null : (String) jsonpObject.get("timeFrame"),
				jsonpObject.get("contact").toString().isEmpty() ? null : (String) jsonpObject.get("contact"),
				jsonpObject.get("cargo").toString().isEmpty() ? null : (String) jsonpObject.get("cargo"));
		address.setCustomsAddress(jsonpObject.get("customsAddress").toString().isEmpty() ? null
				: (String) jsonpObject.get("customsAddress"));
		address.setTnvd(jsonpObject.get("tnvd") != null ? jsonpObject.get("tnvd").toString() : null);
		address.setTime(Time.valueOf((String) jsonpObject.get("time") + ":00"));
		address.setIsCorrect(true);
		address.setOrder(order);
		addressService.saveAddress(address);
		order.setStatus(20);
		orderService.updateOrder(order);
		if(address != null) {
			response.put("status", "200");
			response.put("message", "Сохранен адрес. Маршрут " + order.getIdOrder() + "; Адрес выгрузки: " + address.getBodyAddress() + "; Адрес таможни: " + address.getCustomsAddress());
		}
		return response;		
	}
	
	/**
	 * Метод отвечает за скачивание документа incotermsInsurance
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/procurement/downdoad/incotermsInsurance")
	public String downdoadIncotermsInsuranceGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		//File file = new File(appPath + "resources/others/Speedlogist.apk");
		response.setHeader("content-disposition", "attachment;filename="+"Incoterms insurance.docx");
		FileInputStream in = null;
		OutputStream out = null;
		try {
			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
			in = new FileInputStream(appPath + "resources/others/docs/incotermsInsurance.docx");
			//  Создать выходной поток
			out = response.getOutputStream();
			//  Создать буфер
			byte buffer[] = new byte[1024];
			int len = 0;
			//  Прочитать содержимое входного потока в буфер в цикле
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			in.close();
			out.close();
		}
		return "complited";
	}
	
	/**
	 * Метод отвечает за скачивание документа incoterms
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/procurement/downdoad/incoterms")
	public String downdoadIncotermsGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		//File file = new File(appPath + "resources/others/Speedlogist.apk");
		response.setHeader("content-disposition", "attachment;filename="+"Incoterms.docx");
		FileInputStream in = null;
		OutputStream out = null;
		try {
			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
			in = new FileInputStream(appPath + "resources/others/docs/Incoterms.docx");
			//  Создать выходной поток
			out = response.getOutputStream();
			//  Создать буфер
			byte buffer[] = new byte[1024];
			int len = 0;
			//  Прочитать содержимое входного потока в буфер в цикле
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			in.close();
			out.close();
		}
		return "redirect:/main/carrier/tender";
	}
	
	/**
	 * Метод для простановки времени разгрузки одного заказа
	 * @param request
	 * @param idOrder
	 * @param time
	 * @return
	 */
	@GetMapping("/manager/setTimeUnload/{idOrder}&{time}")
	public Map<String, String> setTimeUnload(HttpServletRequest request, @PathVariable Integer idOrder, @PathVariable String time) {
		Time timetarget = Time.valueOf(time+":00");
		Map<String, String> response = new HashMap<String, String>();
		try {			
			Order order = orderService.getOrderById(idOrder);
			order.setOnloadTime(timetarget);
			orderService.updateOrder(order);
			response.put("status", "200");
			response.put("message", "Данные обновлены");
		} catch (Exception e) {
			response.put("status", "500");
			response.put("message", e.toString());
		}		
		return response;
	}
	
	/**
	 * Метод для постановки времени на выгрузку (окна на выгрузку)
	 * метод Карины
	 * УСТАРЕВШИЙ
	 * НА УДАЛЕНИЕ
	 * @param request
	 * @param idOrder
	 * @param date
	 * @param time
	 * @return
	 * @throws IOException 
	 */
	@Deprecated
	@GetMapping("/manager/setWindowUnload/{idAddress}&{time}")
	public Map<String, String> setWindowUnload(HttpServletRequest request, @PathVariable Integer idAddress, @PathVariable String time) throws IOException {
//		Date datetarget = Date.valueOf(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
		properties = new Properties();
		properties.load(fileInputStream);
		Time timetarget = Time.valueOf(time+":00");
		Map<String, String> response = new HashMap<String, String>();
		try {			
			Address address = addressService.getAddressById(idAddress);
			address.setTime(timetarget);
			addressService.updateAddress(address);
			Order order = address.getOrder();
			order.setOnloadWindowDate(order.getDateDelivery());
			order.setOnloadTime(timetarget);
			order.setStatus(20);
//			order.setMailInfo("");
			orderService.updateOrder(order);
			String text = "Создана заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+"; \nСлот на выгркузку: "+ order.getDateDelivery() + " в "+ 
					timetarget.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "; " +
					"\nНаправление: " + order.getWay();
			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", text, properties.getProperty("email.addNewProcurement.rb.1"), properties.getProperty("email.addNewProcurement.rb.2"));
			response.put("status", "200");
			response.put("message", "Данные обновлены");
		} catch (Exception e) {
			response.put("status", "500");
			response.put("message", e.toString());
			e.printStackTrace();
		}		
		return response;
	}
	
	/**
	 * Метод для постановки даты на выгрузку (окна на выгрузку) с отправкой уведомления об изменении даты
	 * УСТАРЕВШИЙ
	 * НА УДАЛЕНИЕ
	 * @param request
	 * @param idAddress
	 * @param date
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/manager/setWindowUnloadDate/{idAddress}&{date}")
	public Map<String, String> setWindowUnloadHasDate(HttpServletRequest request, @PathVariable Integer idAddress, @PathVariable String date) throws IOException {
		Date datetarget = Date.valueOf(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));		
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
		properties = new Properties();
		properties.load(fileInputStream);
		Map<String, String> response = new HashMap<String, String>();
		try {			
			Address address = addressService.getAddressById(idAddress);
			address.setDate(datetarget);
			addressService.updateAddress(address);
			Order order = address.getOrder();
			Date oldDate = order.getDateDelivery();
			order.setOnloadWindowDate(datetarget);
			order.setDateDelivery(datetarget);
			orderService.updateOrder(order);
			
			String text = "Дата поставки по заявке №"+ order.getIdOrder() + " " + order.getCounterparty() + " изменена специалистaми отдела ОСиУЗ: \nСтарая дата: " + 
					oldDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyy"))+"; \nНовая дата выгрузки: " + 
					order.getDateDelivery().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyy")) + ";";
			String managerEmail = order.getManager().split("; ")[1];
			managerEmail = managerEmail.split(".by")[0];
			managerEmail = managerEmail+".by";
			final String finalManagerEmail = managerEmail;
			mailService.sendSimpleEmail(request, "Изменение заявки ОСиУЗ", text, finalManagerEmail);
			response.put("status", "200");
			response.put("message", "Данные обновлены");
		} catch (Exception e) {
			response.put("status", "500");
			response.put("message", e.toString());
			e.printStackTrace();
		}		
		return response;
	}
	
	/**
	 * УСТАРЕВШИЙ
	 * НА УДАЛЕНИЕ
	 * @param request
	 * @param idOrder
	 * @param date
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/manager/deleteOrderHasOrderSupport/{idOrder}&{date}")
	public Map<String, String> deleteOrderHasOrderSupport(HttpServletRequest request, @PathVariable Integer idOrder, @PathVariable String date) throws IOException {
		Date datetarget = Date.valueOf(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));		
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
		properties = new Properties();
		properties.load(fileInputStream);
		Map<String, String> response = new HashMap<String, String>();
		User thisUser = getThisUser();
		String thisUserFullName = thisUser.getSurname() + " " + thisUser.getName();
		try {		
			Order order = orderService.getOrderById(idOrder);
			Date oldDate = order.getDateDelivery();
			order.setStatus(10);
			order.setChangeStatus(order.getChangeStatus() + "\nУдалил заявку специалист ОСиУЗ: " + thisUserFullName + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:SS")));
			orderService.updateOrder(order);
			
			String text = "Заявка №"+ order.getIdOrder() + " " + order.getCounterparty() + " отменена специалистaми ОСиУЗ: установленная дата выгрузки ("+
					oldDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyy"))+") не согласована специалистaми ОСиУЗ. \nВозможная дата выгрузки: " + 
					datetarget.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyy"))+"; \nЗаявку необходимо пересоздать с учётом новой даты выгрузки.";
			String managerEmail = order.getManager().split("; ")[1];
			managerEmail = managerEmail.split(".by")[0];
			managerEmail = managerEmail+".by";
			final String finalManagerEmail = managerEmail;
			mailService.sendSimpleEmail(request, "Отмена заявки ОСиУЗ", text, finalManagerEmail);
			response.put("status", "200");
			response.put("message", "Данные обновлены");
		} catch (Exception e) {
			response.put("status", "500");
			response.put("message", e.toString());
			e.printStackTrace();
		}		
		return response;
	}
	
	
	/**
	 * Метод отвечает за отправку email сообщения менеджеру от логиста о данных по машине и водителю, когда тендер сыграл
	 * Новый метод! Используется отдельный поток для отправки сообщений!
	 * @param request
	 * @param id
	 * @return
	 */
	@GetMapping("/manager/getDataHasOrder2/{id}")
	public Map<String, String> getDataHasOrder2(HttpServletRequest request, @PathVariable Integer id) {
		Map<String, String> response = new HashMap<String, String>();
		Order order = orderService.getOrderById(id);
		String managerEmail = order.getManager().split("; ")[1];
		managerEmail = managerEmail.split(".by")[0];
		managerEmail = managerEmail+".by";
		User logist = getThisUser();
		final String finalManagerEmail = managerEmail;
		if(managerEmail == null || managerEmail.equals("")) {
			response.put("status", "100");
			response.put("message", "Ошибка: не назначена почта у менеджера");
			return response;
		}
		String text = "";
		List<Route> routes = new ArrayList<Route>(order.getRoutes());
		String driverStr = "";
		for (int i = 0; i < routes.size(); i++) {
			Route r = routes.get(i);
			if(r.getStatusRoute().equals("5")) {
				continue;
			}
			if(r.getUser() == null) {
				response.put("status", "100");
				response.put("message", "Ошибка: не назначен перевозчик на маршрут");
				return response;
			}
			if(r.getTruck() == null) {
				response.put("status", "100");
				response.put("message", "Ошибка: перевозчик не назначил машину на маршрут");
				return response;
			}
			if(r.getDriver() == null) {
				response.put("status", "100");
				response.put("message", "Ошибка: перевозчик не назначил водителя на маршрут");
				return response;
			}
			driverStr = r.getDriver().getSurname() + " " + r.getDriver().getName() + " " + r.getDriver().getPatronymic()+"\n";
			text = text + "Данные по маршруту " + r.getRouteDirection() + "; " + "заказа: " + order.getIdOrder() + " " + order.getCounterparty()+"\n";
			text = text + "Перевозчик: " + r.getUser().getCompanyName()+"\n";
			text = text + "Подвижной состав: " + r.getTruck().getNumTruck() + "/" + r.getTruck().getNumTrailer()+"\n";
			text = text + "Марка машины / прицепа: " + r.getTruck().getBrandTruck() + "/" + r.getTruck().getBrandTrailer()+"\n";
			text = text + "Водитель: " + r.getDriver().getSurname() + " " + r.getDriver().getName() + " " + r.getDriver().getPatronymic()+"\n";
			text = text + "Телефон: " + r.getDriver().getTelephone()+"\n";
			text = text + "Паспортные данные водителя: " +r.getDriver().getNumPass() + "; водительское удостоверение:" + r.getDriver().getNumDriverCard() +"\n";
			text = text + "Цена за перевозку составила: " +r.getFinishPrice() + " " + r.getStartCurrency() +"\n";			
			text = text + "Дата подачи машины на загрузку: " +r.getDateLoadActually().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " в " + r.getTimeLoadActually().format(DateTimeFormatter.ofPattern("HH:mm")) +"\n";			
			text = text + "Дата подачи машины на выгрузку: " +r.getDateUnloadActually().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " в " + r.getTimeUnloadActually().format(DateTimeFormatter.ofPattern("HH:mm")) +"\n";	
		}
		
		final String message = text;
		String mailInfo = "Сообщение было отправлено " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + ". Водитель: " + driverStr;
		order.setMailInfo(order.getMailInfo() + mailInfo);
		orderService.updateOrder(order);
//		mailService.sendSimpleEmail(request, "Данные по заявке №"+order.getIdOrder(), message, finalManagerEmail);
		mailService.sendSimpleEmailTwiceUsers(request, "Данные по заявке №"+order.getIdOrder(), message, finalManagerEmail, logist.geteMail());
		response.put("status", "200");
		response.put("message", "Данные отправлены на почту: " + managerEmail);
		return response;
	}
	
	/**
	 * Метод отвечает за отправку email сообщения менеджеру от логиста о данных по машине и водителю, когда тендер сыграл
	 * УСТАРЕВШИЙ МЕТОД! ИСПОЛЬЗОВАТЬ В АВАРИЙНОМ СЛУЧАЕ!
	 * если маршрутов несколько - первый маршрут как правило без перевоза - не отправляет сообщение из-за цикла!
	 * @param request
	 * @param id
	 * @return
	 */
	@GetMapping("/manager/getDataHasOrder/{id}")
	public Map<String, String> getDataHasOrder(HttpServletRequest request, @PathVariable Integer id) {
		Map<String, String> response = new HashMap<String, String>();
		Order order = orderService.getOrderById(id);
		String managerEmail = order.getManager().split("; ")[1];
		managerEmail = managerEmail.split(".by")[0];
		managerEmail = managerEmail+".by";
		final String finalManagerEmail = managerEmail;
		if(managerEmail == null || managerEmail.equals("")) {
			response.put("status", "100");
			response.put("message", "Ошибка: не назначена почта у менеджера");
			return response;
		}
		String text = "";
		String driverStr = null;
		for (Route r : order.getRoutes()) {
			if(r.getUser() == null) {
				response.put("status", "100");
				response.put("message", "Ошибка: не назначен перевозчик на маршрут");
				return response;
			}
			if(r.getTruck() == null) {
				response.put("status", "100");
				response.put("message", "Ошибка: перевозчик не назначил машину на маршрут");
				return response;
			}
			if(r.getDriver() == null) {
				response.put("status", "100");
				response.put("message", "Ошибка: перевозчик не назначил водителя на маршрут");
				return response;
			}
			driverStr = r.getDriver().getSurname() + " " + r.getDriver().getName() + " " + r.getDriver().getPatronymic()+"\n";
			text = text + "Данные по маршруту " + r.getRouteDirection() + "; " + "заказа: " + order.getIdOrder() + " " + order.getCounterparty()+"\n";
			text = text + "Перевозчик: " + r.getUser().getCompanyName()+"\n";
			text = text + "Подвижной состав: " + r.getTruck().getNumTruck() + "/" + r.getTruck().getNumTrailer()+"\n";
			text = text + "Марка машины / прицепа: " + r.getTruck().getBrandTruck() + "/" + r.getTruck().getBrandTrailer()+"\n";
			text = text + "Водитель: " + r.getDriver().getSurname() + " " + r.getDriver().getName() + " " + r.getDriver().getPatronymic()+"\n";
			text = text + "Паспортные данные водителя: " +r.getDriver().getNumPass() + "; водительское удостоверение:" + r.getDriver().getNumDriverCard() +"\n";
			text = text + "Цена за перевозку составила: " +r.getFinishPrice() + " " + r.getStartCurrency() +"\n";			
			text = text + "Дата подачи машины на загрузку: " +r.getDateLoadActually().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " в " + r.getTimeLoadActually().format(DateTimeFormatter.ofPattern("HH:mm")) +"\n";			
			text = text + "Дата подачи машины на выгрузку: " +r.getDateUnloadActually().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " в " + r.getTimeUnloadActually().format(DateTimeFormatter.ofPattern("HH:mm")) +"\n";			
		}
		
		final String message = text;
		String mailInfo = "Сообщение было отправлено " + LocalDateTime.now() + ". Водитель: " + driverStr;
		order.setMailInfo(mailInfo);
		new Thread(new Runnable() {			
			@Override
			public void run() {
				mailService.sendSimpleEmail(request, "Данные по заявке №"+order.getIdOrder(), message, finalManagerEmail);				
			}
		}).start();
		response.put("status", "200");
		response.put("message", "Данные отправлены на почту: " + managerEmail);
		return response;
	}
	
	@GetMapping("/manager/getRoutesHasOrder/{id}")
	public Set<Route> getRoutesHasOrder(HttpServletRequest request, @PathVariable Integer id) {		
		return orderService.getOrderById(id).getRoutes();
	}
	
	// загрузка и проверка матрицы

	@GetMapping("/map/matrix")
	public Map<String, Double> getMatrix(HttpServletRequest request) {
		return matrixMachine.matrix;
	}

	@GetMapping("/map/loadmatrix")
	public Integer getCalcMatrix(HttpServletRequest request) {
		return matrixMachine.loadMatrixOfDistance().size();
	}

	@GetMapping("/map/sizematrix")
	public Map<String, String> getSizeMatrix(HttpServletRequest request) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("size", matrixMachine.matrix.size() + "");
		List<Shop> shops = shopService.getShopList();
		Integer size = shops.size() * shops.size() - shops.size();
		response.put("размер согласно базе данных", size + "");
		return response;
	}

	@GetMapping("/map/help")
	public Map<String, String> getHelpMatrix(HttpServletRequest request) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("/map/matrix/", "Возвращает матрицу расстояний (все значения)");
		response.put("/map/loadmatrix", "загрузить матрицу из файла сериализации");
		response.put("/map/sizematrix",
				"Возвращает колличество элементов матрицы и выводит в консоль, все ли элементы прогружены на текущий момент");
		response.put("/map/calcmatrix/{i}", "рассчитать матрицу и создать фай1л сериализации (0 - если всю)");
		return response;
	}

	@GetMapping("/map/calcmatrix/{i}")
	public Integer getCalcMatrix(HttpServletRequest request, @PathVariable Integer i) {
		System.out.println("на вход получил " + i);
		if (i == 0) {
			return matrixMachine.calculationDistance(null);
		} else {
			return matrixMachine.calculationDistance(i);
		}
	}

	// конец загрузки и проверка матрицы
	
	@Autowired
	private LogicAnalyzer logicAnalyzer;
	
	@PostMapping("/map/myoptimization2")
	public Solution myOptimization2(@RequestBody String str) throws Exception {
		Double maxKoef = 2.0;
		Map<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("shops");
		JSONArray pallHasShopsJSON = (JSONArray) jsonMainObject.get("palls");

		List<Integer> numShops = new ArrayList<Integer>();
		List<Integer> pallHasShops = new ArrayList<Integer>();

		Integer stock = Integer.parseInt(jsonMainObject.get("stock").toString());

		numShopsJSON.forEach(s -> numShops.add(Integer.parseInt(s.toString())));
		pallHasShopsJSON.forEach(p -> pallHasShops.add(Integer.parseInt(p.toString())));
		List<Solution> solutions = new ArrayList<Solution>();
		
		//реализация перебора первого порядка
		for (double i = 1.00; i <= maxKoef; i = i + 0.02) {
			Double koeff = i;
//			System.out.println("Коэфф = " + koeff);
			Solution solution = colossusProcessorRad.run(jsonMainObject, numShops, pallHasShops, null, stock, koeff, "fullLoad");

			// строим маршруты для отправки клиенту

			// в этой мате ключ это id самого маршрута, т.е. WhiteWay, а значение это сам
			// маршрут

			solution.getWhiteWay().forEach(w -> {
				List<Shop> newPoints = logicAnalyzer.correctRouteMaker(w.getWay());				
				VehicleWay way = w;
				way.setWay(newPoints);
			});
			solution.setKoef(koeff);
			solutions.add(solution);
		}
		//второй порядок
//		for (double i = 1.01; i <= maxKoef; i = i + 0.02) {
//			Double koeff = i;
////			System.out.println("Коэфф = " + koeff);
//			Solution solution = colossusProcessorRad.run(jsonMainObject, numShops, pallHasShops, stock, koeff, "noFullLoad");
//
//			// строим маршруты для отправки клиенту
//
//			// в этой мате ключ это id самого маршрута, т.е. WhiteWay, а значение это сам
//			// маршрут
//
//			solution.getWhiteWay().forEach(w -> {
//				List<Shop> newPoints = logicAnalyzer.correctRouteMaker(w.getWay());
//				VehicleWay way = w;
//				way.setWay(newPoints);
//			});
//			solutions.add(solution);
//		}
		
//		System.err.println(solutions.size());
//		solutions.forEach(s-> System.out.println(s.getTotalRunSolution()));
		Double minOwerrun = 999999999999999999.0;
		int emptyShop = 9999;
		Solution finalSolution = null;
		for (Solution solution2 : solutions) {
			
			//определяем и записываем суммарный пробег маршрута
			//!!!!!!записываем внутри процессора!
//			Double totalRunHasMatrix = 0.0;
//			for (VehicleWay way : solution2.getWhiteWay()) {
//				//заменяем просчёт расстояний из GH на матричный метод			
//				for (int j = 0; j < way.getWay().size()-1; j++) {
//					String key = way.getWay().get(j).getNumshop()+"-"+way.getWay().get(j+1).getNumshop();
//					totalRunHasMatrix = totalRunHasMatrix + matrixMachine.matrix.get(key);
//				}
//			}
//			solution2.setTotalRunKM(totalRunHasMatrix);
			int summpall = 0;
			for (VehicleWay way : solution2.getWhiteWay()) {
				Shop stock123 = way.getWay().get(0);
				summpall = summpall + calcPallHashHsop(way.getWay(), stock123);
				way.setSummPall(summpall);
			}
			System.err.println("Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef() + "; Паллеты: " + summpall);
			if(solution2.getEmptyShop().size() <= emptyShop) {
				if(solution2.getEmptyShop().size() < emptyShop && minOwerrun < solution2.getTotalRunKM()) {
					System.out.println("Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef()+ "; Паллеты: " + summpall);
					minOwerrun = solution2.getTotalRunKM();
					emptyShop = solution2.getEmptyShop().size();
					finalSolution = solution2;
				}
				
				if(solution2.getTotalRunKM() < minOwerrun) {
					System.out.println("Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef()+ "; Паллеты: " + summpall);
					minOwerrun = solution2.getTotalRunKM();
					emptyShop = solution2.getEmptyShop().size();
					finalSolution = solution2;
				}
			}
		}
		Map<String, List<MapResponse>> wayHasMap = new HashMap<String, List<MapResponse>>();
		finalSolution.getWhiteWay().forEach(way -> {
			List<GHRequest> ghRequests = null;
			try {
				ghRequests = routingMachine.createrListGHRequest(way.getWay());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<Shop[]> shopPoints = null;
			try {
				shopPoints = routingMachine.getShopAsWay(way.getWay());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GraphHopper hopper = routingMachine.getGraphHopper();
//			ghRequests.forEach(r->System.out.println(r.getCustomModel()));
			List<MapResponse> listResult = new ArrayList<MapResponse>();
			for (GHRequest req : ghRequests) {
				int index = ghRequests.indexOf(req);

				GHResponse rsp = hopper.route(req);
				if (rsp.getAll().isEmpty()) {
					rsp.getErrors().forEach(e -> System.out.println(e));
					rsp.getErrors().forEach(e -> e.printStackTrace());
					listResult.add(new MapResponse(null, null, null, 500.0, 500));
				}
//				System.err.println(rsp.getAll().size());
				if (rsp.getAll().size() > 1) {
					rsp.getAll().forEach(p -> System.out.println(p.getDistance() + "    " + p.getTime()));
				}
				ResponsePath path = rsp.getBest();
				List<ResponsePath> listPath = rsp.getAll();
				for (ResponsePath pathI : listPath) {
					if (pathI.getDistance() < path.getDistance()) {
						path = pathI;
					}
				}
//				System.out.println(roundВouble(path.getDistance()/1000, 2) + "km, " + path.getTime() + " time");
				PointList pointList = path.getPoints();
				path.getPathDetails();
				List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
				pointList.forEach(p -> result.add(p.toGeoJson()));
				List<Double[]> resultPoints = new ArrayList<Double[]>();
				double cash = 0.0;
				for (Double[] point : result) {
					cash = point[0];
					point[0] = point[1];
					point[1] = cash;
					resultPoints.add(point);
				}
				listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
						shopPoints.get(index)[0], shopPoints.get(index)[1]));
			}
			wayHasMap.put(way.getId(), listResult);
		});
		finalSolution.setMapResponses(wayHasMap);
		
		return finalSolution;
	}

	@PostMapping("/map/myoptimization")
	public Solution myOptimization(@RequestBody String str) throws Exception {
		Map<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("shops");
		JSONArray pallHasShopsJSON = (JSONArray) jsonMainObject.get("palls");

		List<Integer> numShops = new ArrayList<Integer>();
		List<Integer> pallHasShops = new ArrayList<Integer>();

		Integer stock = Integer.parseInt(jsonMainObject.get("stock").toString());

		numShopsJSON.forEach(s -> numShops.add(Integer.parseInt(s.toString())));
		pallHasShopsJSON.forEach(p -> pallHasShops.add(Integer.parseInt(p.toString())));

		Solution solution = colossusProcessorRad.run(jsonMainObject, numShops, pallHasShops, null, stock, 1.2, "fullLoad");

//		response.put("status", "200");
//		response.put("message", solution.toString());
//		System.out.println("MainRestController.myOptimization: Оптимизация закончена.");

		// строим маршруты для отправки клиенту

		// в этой мате ключ это id самого маршрута, т.е. WhiteWay, а значение это сам
		// маршрут
		Map<String, List<MapResponse>> wayHasMap = new HashMap<String, List<MapResponse>>();

		solution.getWhiteWay().forEach(way -> {
			List<GHRequest> ghRequests = null;
			try {
				ghRequests = routingMachine.createrListGHRequest(way.getWay());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<Shop[]> shopPoints = null;
			try {
				shopPoints = routingMachine.getShopAsWay(way.getWay());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GraphHopper hopper = routingMachine.getGraphHopper();
//			ghRequests.forEach(r->System.out.println(r.getCustomModel()));
			List<MapResponse> listResult = new ArrayList<MapResponse>();
			for (GHRequest req : ghRequests) {
				int index = ghRequests.indexOf(req);

				GHResponse rsp = hopper.route(req);
				if (rsp.getAll().isEmpty()) {
					rsp.getErrors().forEach(e -> System.out.println(e));
					rsp.getErrors().forEach(e -> e.printStackTrace());
					listResult.add(new MapResponse(null, null, null, 500.0, 500));
				}
//				System.err.println(rsp.getAll().size());
				if (rsp.getAll().size() > 1) {
					rsp.getAll().forEach(p -> System.out.println(p.getDistance() + "    " + p.getTime()));
				}
				ResponsePath path = rsp.getBest();
				List<ResponsePath> listPath = rsp.getAll();
				for (ResponsePath pathI : listPath) {
					if (pathI.getDistance() < path.getDistance()) {
						path = pathI;
					}
				}
//				System.out.println(roundВouble(path.getDistance()/1000, 2) + "km, " + path.getTime() + " time");
				PointList pointList = path.getPoints();
				path.getPathDetails();
				List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
				pointList.forEach(p -> result.add(p.toGeoJson()));
				List<Double[]> resultPoints = new ArrayList<Double[]>();
				double cash = 0.0;
				for (Double[] point : result) {
					cash = point[0];
					point[0] = point[1];
					point[1] = cash;
					resultPoints.add(point);
				}
				listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
						shopPoints.get(index)[0], shopPoints.get(index)[1]));
			}
			wayHasMap.put(way.getId(), listResult);
		});

		solution.setMapResponses(wayHasMap);
		return solution;

	}

	@PostMapping("/map/optimization")
	public Map<String, String> optimization(@RequestBody String str) throws ParseException {
		Map<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("shops");
		JSONArray pallHasShopsJSON = (JSONArray) jsonMainObject.get("palls");

		List<Integer> numShops = new ArrayList<Integer>();
		List<Integer> pallHasShops = new ArrayList<Integer>();
		List<VehicleImpl> vehicleImpls = new ArrayList<VehicleImpl>();

		Integer stock = Integer.parseInt(jsonMainObject.get("stock").toString());
		Integer iteration = jsonMainObject.get("iteration") == null ? 30
				: Integer.parseInt(jsonMainObject.get("iteration").toString());

		numShopsJSON.forEach(s -> numShops.add(Integer.parseInt(s.toString())));
		pallHasShopsJSON.forEach(p -> pallHasShops.add(Integer.parseInt(p.toString())));

		// создаём машины
		// фуры
		JSONObject big = (JSONObject) parser.parse(jsonMainObject.get("big").toString());
		int nuOfBig = Integer.parseInt(big.get("count").toString());
		int capacityBig = Integer.parseInt(big.get("tonnage").toString());
		double maxDurationBig = Double.parseDouble(big.get("maxMileage").toString());
		int depotCounter = 1;
		for (int i = 0; i < nuOfBig; i++) {
			VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
					.addCapacityDimension(0, capacityBig).setCostPerDistance(1.0).build();
			String vehicleId = depotCounter + "_" + (i + 1) + "_фура";
			VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
			vehicleBuilder.setStartLocation(Location.Builder.newInstance().setId(stock.toString()).build());
			vehicleBuilder.setType(vehicleType);
			vehicleBuilder.setLatestArrival(maxDurationBig);
			VehicleImpl vehicle = vehicleBuilder.build();
			vehicleImpls.add(vehicle);
		}
		depotCounter++;
		// средние
		JSONObject middle = (JSONObject) parser.parse(jsonMainObject.get("middle").toString());
		int nuOfVehiclesMiddle = Integer.parseInt(middle.get("count").toString());
		int capacityMiddle = Integer.parseInt(middle.get("tonnage").toString());
		double maxDurationMiddle = Double.parseDouble(middle.get("maxMileage").toString());
		for (int i = 0; i < nuOfVehiclesMiddle; i++) {
			VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
					.addCapacityDimension(0, capacityMiddle).setCostPerDistance(1.0).build();
			String vehicleId = depotCounter + "_" + (i + 1) + "_средняя";
			VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
			vehicleBuilder.setStartLocation(Location.Builder.newInstance().setId(stock.toString()).build());
			vehicleBuilder.setType(vehicleType);
			vehicleBuilder.setLatestArrival(maxDurationMiddle);
			VehicleImpl vehicle = vehicleBuilder.build();
			vehicleImpls.add(vehicle);
		}
		depotCounter++;
		// маленькие
		JSONObject little = (JSONObject) parser.parse(jsonMainObject.get("little").toString());
		int nuOfVehiclesLittle = Integer.parseInt(little.get("count").toString());
		int capacityLittle = Integer.parseInt(little.get("tonnage").toString());
		double maxDurationLittle = Double.parseDouble(little.get("maxMileage").toString());
		for (int i = 0; i < nuOfVehiclesLittle; i++) {
			VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
					.addCapacityDimension(0, capacityLittle).setCostPerDistance(1.0).build();
			String vehicleId = depotCounter + "_" + (i + 1) + "_маленькая";
			VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
			vehicleBuilder.setStartLocation(Location.Builder.newInstance().setId(stock.toString()).build());
			vehicleBuilder.setType(vehicleType);
			vehicleBuilder.setLatestArrival(maxDurationLittle);
			VehicleImpl vehicle = vehicleBuilder.build();
			vehicleImpls.add(vehicle);
		}

		String res = jSpiritMachine.optimization50Points(numShops, pallHasShops, vehicleImpls, stock, iteration);
//		System.err.println(res);
		response.put("status", "200");
		response.put("message", res);
		return response;
	}

	@GetMapping("/carrier/getMyDrivers")
	public List<User> getMyDrivers(HttpServletRequest request) {
		return userService.getDriverList(getThisUser().getCompanyName());

	}

	/**
	 * метод отвечает за удаление полигона по имени
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/map/delPolygon/{name}")
	public Map<String, String> delPolygon(@PathVariable String name) {
		JsonFeature feature = routingMachine.deletePolygon(name);
		Map<String, String> result = new HashMap<String, String>();
		if (feature != null) {
			result.put("status", "200");
			result.put("message", "Полигон с именем " + feature.getId() + " удалён");
		} else {
			result.put("status", "11");
			result.put("message", "Полигон с именем " + feature.getId() + " не найден!");
		}
		return result;

	}

	@PostMapping("/map/savePolygon")
	public Map<String, String> savePolygon(@RequestBody String str) throws ParseException {
		System.out.println(str);
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		JSONObject propertiesJSON = (JSONObject) parser.parse(jsonMainObject.get("properties").toString());
		JSONObject geometryJSON = (JSONObject) parser.parse(jsonMainObject.get("geometry").toString());
		JSONArray coordinatesArrayJson = (JSONArray) parser.parse(geometryJSON.get("coordinates").toString());
		String nameFirst = propertiesJSON.get("name").toString();
		String action = propertiesJSON.get("action").toString();
		String textMass1 = coordinatesArrayJson.get(0).toString();
		textMass1 = textMass1.substring(1, textMass1.length() - 1);
		String[] textMass2 = textMass1.split(",");
		Coordinate[] area = new Coordinate[textMass2.length / 2];
		int j = 0;
		for (int i = 0; i < textMass2.length; i = i + 2) {
			double lat = Double.parseDouble(textMass2[i].substring(1, textMass2[i].length()).trim());
			double lng = Double.parseDouble(textMass2[i + 1].substring(0, textMass2[i + 1].length() - 1).trim());
//			System.out.println(lat + "   " + lng + "   j=" + j);
			area[j] = new Coordinate(lat, lng);
			j++;
		}

//		for (Coordinate string : area) {
//			System.out.println(string);
//		}

		CustomJsonFeature feature = new CustomJsonFeature(action, nameFirst, "Feature", null,
				new GeometryFactory().createPolygon(area), new HashMap<>());
		Map<String, String> result = new HashMap<String, String>();
		if (routingMachine.polygons.containsKey(feature.getId())) {
			result.put("status", "100");
			result.put("message", "Полигон с названием " + feature.getId() + " существует");
		} else {
			routingMachine.savePolygon(feature);
			result.put("status", "200");
			result.put("message", "Полигон сохранён");
		}
		return result;

	}

	@GetMapping("/map/getAllPolygons")
	public List<JsonResponsePolygon> getAllPolygons() {
		List<JsonResponsePolygon> result = new ArrayList<JsonResponsePolygon>();
		routingMachine.polygons.forEach((k, v) -> {
			result.add(ResponsePolygonCreater(v));
		});
		return result;
	}

	/**
	 * Проверяет, есть ли такой полигон по названию
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/map/checkNamePolygon/{name}")
	public boolean checkNamePolygon(@PathVariable String name) {
		if (routingMachine.polygons.containsKey(name)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Создаёт объекты для ответа на фронт из JsonFeature
	 * 
	 * @param feature
	 * @return
	 */
	private JsonResponsePolygon ResponsePolygonCreater(CustomJsonFeature feature) {
		List<double[]> coordinates = new ArrayList<double[]>();
		for (Coordinate d : feature.getGeometry().getCoordinates()) {
			double[] coordinat = new double[2];
			coordinat[0] = d.getX();
			coordinat[1] = d.getY();
			coordinates.add(coordinat);
		}
		feature.getProperties().forEach((k, v) -> System.out.println(k + "  " + v));
		GeometryResponse geometryResponse = new GeometryResponse(coordinates, "Polygon");
		JsonResponsePolygon jsonResponsePolygon = new JsonResponsePolygon();
		jsonResponsePolygon.setGeometry(geometryResponse);
		Map<String, String> prop = new HashMap<String, String>();
		prop.put("type", "polygone");
		prop.put("name", feature.getId());
		prop.put("action", feature.getAction());
		jsonResponsePolygon.setProperties(prop);
		jsonResponsePolygon.setType("Feature");
		return jsonResponsePolygon;
	}

	@GetMapping("/manager/process")
	public void getReport() {
		System.out.println("start");
		List<Message> messages = messageService.getMEssageList();
		System.out.println("Выгрузка завершена, колличество сообщений: " + messages.size());
		DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		double precent = 0.0;
		for (int i = 0; i < messages.size(); i++) {
			Message message = messages.get(i);
			if (message.getDate() != null) {
				continue;
			}
			String dateStr = message.getDatetime().split(";")[0];
			if (dateStr.length() < 10) {
				dateStr = "0" + dateStr;
			}
			Date date = Date.valueOf(LocalDate.parse(dateStr, myFormatter));
			messageService.updateDate(message.getIdMessage(), date);
			precent = i * 100 / messages.size();
			System.out.println(roundВouble(precent, 1) + "%");
		}
		System.out.println("Конец программы");
	}

	@GetMapping("/manager/getReport/{dateStart}&{dateFinish}")
	public Map<String, Object> getReport(@PathVariable Date dateStart, @PathVariable Date dateFinish,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		Set<Route> routes = new HashSet<Route>();
		List<Route> targetRoutes = routeService.getRouteListAsDate(dateStart, dateFinish);
		targetRoutes.stream().filter(r -> r.getComments() != null && r.getComments().equals("international")
				&& Integer.parseInt(r.getStatusRoute()) <= 8).forEach(r -> routes.add(r)); // проверяет созданы ли точки
																							// вручную, и отдаёт только
																							// международные маршруты
		File file = poiExcel.getReportFromInternationalManager(routes, request, dateStart, dateFinish);

		result.put("status", 200);
		result.put("body", file);
		result.put("extension", ".xlsx");
		return result;
	}

	@GetMapping("/dev/getError")
	public void getHeapSpaceError() {
		StringBuffer buffer = new StringBuffer();
		while (true) {
			buffer.append("d");
		}
	}

	@GetMapping("/dev/heap")
	public Map<String, String> getHeapSpace() {
		Map<String, String> response = new HashMap<String, String>();
		// Get current size of heap in bytes
		long heapSize = Runtime.getRuntime().totalMemory();

		// Get maximum size of heap in bytes. The heap cannot grow beyond this size.//
		// Any attempt will result in an OutOfMemoryException.
		long heapMaxSize = Runtime.getRuntime().maxMemory();

		// Get amount of free memory within the heap in bytes. This size will increase
		// // after garbage collection and decrease as new objects are created.
		long heapFreeSize = Runtime.getRuntime().freeMemory();

		response.put("status", "200");
		response.put("heapSize", heapSize / 1000000 + " mb");
		response.put("heapMaxSize", heapMaxSize / 1000000 + " mb");
		response.put("heapFreeSize", heapFreeSize / 1000000 + " mb");
		return response;
	}

	@RequestMapping(value = "/map/6", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<Long, List<MapResponse>> getRouteHasExcelV2(
			@RequestParam(value = "excel", required = false) MultipartFile excel, HttpServletRequest request,
			HttpServletResponse response) throws Throwable {
		poiExcel.classLog = null;
		routingMachine.classLog = null;
		classLog = null;
		File target = poiExcel.getFileByMultipartTarget(excel, request, "routes.xlsx");
		java.util.Date t2 = new java.util.Date();
		Map<Long, List<Double[]>> pointsMap = poiExcel.readExcelForWays(target);
		if (pointsMap == null) {
			classLog = classLog + "\n Controller -- Отмена построения маршрутов!";
			return null;
		}
		java.util.Date t3 = new java.util.Date();
		Map<Long, List<GHRequest>> ghRequests = routingMachine.generateGHRequestHasMap(pointsMap);
		java.util.Date t4 = new java.util.Date();
//		ghRequests.forEach((k,v)-> System.out.println(k + "  ---  " + v));

		Map<Long, List<Shop[]>> shopPointsMap = poiExcel.getShopAsPointExcel(target);
		java.util.Date t5 = new java.util.Date();
//		shopPointsMap.forEach((k,v)-> v.forEach(sh->{
//			System.out.println(k + "  ---  " + sh[0].getNumshop() +" ----> " + sh[1].getNumshop());
//		}));
		GraphHopper hopper = routingMachine.getGraphHopper();
////		ghRequests.forEach(r->System.out.println(r.getCustomModel()));
		Map<Long, List<MapResponse>> mapResult = new HashMap<Long, List<MapResponse>>();
		ghRequests.forEach((k, v) -> {
			List<GHRequest> ghRequestsList = v;
			if (ghRequestsList == null) {
				mapResult.put(k, null);
			} else {
				List<MapResponse> listResult = new ArrayList<MapResponse>();
				List<Shop[]> shopPoints = shopPointsMap.get(k);
				for (GHRequest req : ghRequestsList) {
					int index = ghRequestsList.indexOf(req);
					GHResponse rsp = hopper.route(req);// прокладываем маршрут
					if (rsp.getAll().isEmpty()) {
						rsp.getErrors().forEach(e -> System.out.println(e));
						rsp.getErrors().forEach(e -> e.printStackTrace());
						listResult.add(new MapResponse(null, null, null, 500.0, 500));
					}
					ResponsePath path = rsp.getBest();
					List<ResponsePath> listPath = rsp.getAll();
					for (ResponsePath pathI : listPath) {
						if (pathI.getDistance() < path.getDistance()) {
							path = pathI;
						}
					}
					PointList pointList = path.getPoints();
					List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
					pointList.forEach(p -> result.add(p.toGeoJson()));
					List<Double[]> resultPoints = new ArrayList<Double[]>();
					double cash = 0.0;
					for (Double[] point : result) {
						cash = point[0];
						point[0] = point[1];
						point[1] = cash;
						resultPoints.add(point);
					}
					listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
							shopPoints.get(index)[0], shopPoints.get(index)[1]));
				}
				mapResult.put(k, listResult);
			}
		});
		java.util.Date t6 = new java.util.Date();
		// сюда вставить метод, для формирования листа ексель
		// план такой: опять читаем лист ексель и переформируем его в горизонталь.
		// ворым этапом по id мы находим лист с расстояниями и вписываем в ексель.

		poiExcel.getRouteExcelForLogist(mapResult, target, request);
		java.util.Date t7 = new java.util.Date();
		classLog = classLog + "\n Controller -- " + (t3.getTime() - t2.getTime())
				+ "ms poiExcel.readExcelForWays(target)";
		classLog = classLog + "\n Controller -- " + (t4.getTime() - t3.getTime())
				+ "ms generateGHRequestHasMap(pointsMap)";
		classLog = classLog + "\n Controller -- " + (t5.getTime() - t4.getTime())
				+ "ms poiExcel.getShopAsPointExcel(target))";
		classLog = classLog + "\n Controller -- " + (t6.getTime() - t5.getTime()) + "ms routingMachine";
		classLog = classLog + "\n Controller -- " + (t7.getTime() - t6.getTime()) + "ms createExcel";
		classLog = classLog + "\n Controller -- " + (t7.getTime() - t2.getTime()) + "ms allTime";
		return mapResult;
	}

	@RequestMapping(value = "/carrier/editTruck", method = RequestMethod.POST)
	public Truck editTruck(@RequestParam(value = "technical_certificate_file", required = false) MultipartFile mulFile2,
			HttpServletRequest request,
			@RequestParam(value = "technical_certificate", required = false) String technicalCertificate,
			@RequestParam(value = "info", required = false) String infoData,
			@RequestParam(value = "dimensions", required = false) String dimensions,
			@RequestParam(value = "modelTruck", required = false) String modelTruck,
			@RequestParam(value = "numTruck", required = false) String numTruck,
			@RequestParam(value = "ownerTruck", required = false) String ownerTruck,
			@RequestParam(value = "number_axes", required = false) String number_axes,
			@RequestParam(value = "typeTrailer", required = false) String typeTrailer,
			@RequestParam(value = "hitch_type", required = false) String hitch_type,
			@RequestParam(value = "type_of_load", required = false) String type_of_load,
			@RequestParam(value = "cargoCapacity", required = false) String cargoCapacity,
			@RequestParam(value = "volume_trailer", required = false) String volume_trailer,
			@RequestParam(value = "pallCapacity", required = false) String pallCapacity,
			@RequestParam(value = "brandTruck", required = false) String brandTruck,
			@RequestParam(value = "numTrailer", required = false) String numTrailer,
			@RequestParam(value = "brandTrailer", required = false) String brandTrailer,
			@RequestParam(value = "idTruck", required = false) Integer idTruck) throws IOException, ServletException {
		Truck truckOld = truckService.getTruckById(idTruck);
		if (truckOld == null) {
			return null;
		}
		Truck truck = new Truck();
		truck.setIdTruck(truckOld.getIdTruck());
		truck.setNumTruck(stripXSS(numTruck));
		truck.setTechnicalCertificate(stripXSS(technicalCertificate));
		truck.setInfo(infoData);
		truck.setDimensionsBody(dimensions);
		truck.setModelTruck(stripXSS(modelTruck));
		truck.setOwnerTruck(stripXSS(ownerTruck));
		truck.setNumber_axes(stripXSS(number_axes));
		truck.setTypeTrailer(stripXSS(typeTrailer));
		truck.setHitchType(stripXSS(hitch_type));
		truck.setTypeLoad(stripXSS(type_of_load));
		truck.setCargoCapacity(stripXSS(cargoCapacity));
		truck.setVolumeTrailer(Integer.parseInt(volume_trailer));
		truck.setPallCapacity(stripXSS(pallCapacity));
		truck.setBrandTruck(stripXSS(brandTruck));
		truck.setUser(truckOld.getUser());
		truck.setVerify(truckOld.getVerify());
		truck.setNumTrailer(numTrailer);
		truck.setBrandTrailer(brandTrailer);
		truckService.updateTruck(truck);
		return truck;
	}

	@GetMapping("/manager/changeVertCar/{idCar}")
	public Map<String, String> getChangeVertCar(@PathVariable Integer idCar) {
		Truck truck = truckService.getTruckById(idCar);
		if (truck.getVerify() == null) {
			truck.setVerify(true);
		} else {
			truck.setVerify(!truck.getVerify());
		}
		truckService.updateTruck(truck);
		Map<String, String> response = new HashMap<String, String>();
		response.put("status", "200");
		response.put("message", "Проверка машины " + truck.getNumTruck() + "изменена");
		return response;
	}

	/**
	 * проверяет, есть ли такой магазин по номеру магаза
	 * 
	 * @param numShop
	 * @return
	 */
	@GetMapping("/manager/existShop/{numShop}")
	public boolean getListOrders(@PathVariable String numShop) {
		Shop shop = shopService.getShopByNum(Integer.parseInt(numShop));
		if (shop == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * удаляет магаз по номеру магаза
	 * 
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/manager/deleteShop", method = RequestMethod.POST)
	public Map<String, String> deleteShop(@RequestBody String str) throws ParseException {
		Map<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		shopService.deleteShopByNum(Integer.parseInt(jsonMainObject.get("numshop").toString()));
		response.put("status", "200");
		response.put("message", "Магазин " + jsonMainObject.get("numshop").toString() + " удалён!");
		return response;
	}

	/**
	 * добавляет магаз
	 * 
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/manager/addShop", method = RequestMethod.POST)
	public Map<String, String> saveNewShop(@RequestBody String str) throws ParseException {
		System.err.println(str);
		Map<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Shop shop = new Shop();
		if (jsonMainObject.get("numshop").toString() == null) {
			response.put("status", "100");
			response.put("message", "Отсутствует номер магазина!");
			return response;
		}
		shop.setNumshop(Integer.parseInt(jsonMainObject.get("numshop").toString()));
		shop.setAddress(jsonMainObject.get("address") != null ? jsonMainObject.get("address").toString() : null);
		shop.setLat(jsonMainObject.get("lat") != null ? jsonMainObject.get("lat").toString() : null);
		shop.setLng(jsonMainObject.get("lng") != null ? jsonMainObject.get("lng").toString() : null);
		shop.setType(jsonMainObject.get("type") != null ? jsonMainObject.get("type").toString() : null);
		shop.setLength(!jsonMainObject.get("length").toString().isEmpty() ? Double.parseDouble(jsonMainObject.get("length").toString()) : null);
		shop.setWidth(!jsonMainObject.get("width").toString().isEmpty() ? Double.parseDouble(jsonMainObject.get("width").toString()) : null);
		shop.setHeight(!jsonMainObject.get("height").toString().isEmpty() ? Double.parseDouble(jsonMainObject.get("height").toString()) : null);
		shop.setMaxPall(!jsonMainObject.get("maxPall").toString().isEmpty() ? Integer.parseInt(jsonMainObject.get("maxPall").toString()) : null);
		if(jsonMainObject.get("isTailLift") != null) {
			shop.setIsTailLift(jsonMainObject.get("isTailLift").toString().equals("true") ? true : false);
		}else {
			shop.setIsTailLift(null);
		}
		
		if(jsonMainObject.get("isInternalMovement") != null) {
			shop.setIsInternalMovement(jsonMainObject.get("isInternalMovement").toString().equals("true") ? true : false);
		}else {
			shop.setIsInternalMovement(null);
		}
		shopService.saveShop(shop);
		response.put("status", "200");
		response.put("message", "Магазин " + shop.getNumshop() + " сохранен!");
		return response;
	}
	
	@RequestMapping(value = "/manager/editShop", method = RequestMethod.POST)
	public Map<String, String> editShop(@RequestBody String str) throws ParseException {
		Map<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Shop shop = shopService.getShopByNum(Integer.parseInt(jsonMainObject.get("numshop").toString()));
		if (jsonMainObject.get("numshop").toString() == null) {
			response.put("status", "100");
			response.put("message", "Отсутствует номер магазина!");
			return response;
		}
		shop.setNumshop(Integer.parseInt(jsonMainObject.get("numshop").toString()));
		shop.setAddress(jsonMainObject.get("address") != null ? jsonMainObject.get("address").toString() : null);
		shop.setLat(jsonMainObject.get("lat") != null ? jsonMainObject.get("lat").toString() : null);
		shop.setLng(jsonMainObject.get("lng") != null ? jsonMainObject.get("lng").toString() : null);
		shop.setType(jsonMainObject.get("type") != null ? jsonMainObject.get("type").toString() : null);
		shop.setLength(!jsonMainObject.get("length").toString().isEmpty() ? Double.parseDouble(jsonMainObject.get("length").toString()) : null);
		shop.setWidth(!jsonMainObject.get("width").toString().isEmpty() ? Double.parseDouble(jsonMainObject.get("width").toString()) : null);
		shop.setHeight(!jsonMainObject.get("height").toString().isEmpty() ? Double.parseDouble(jsonMainObject.get("height").toString()) : null);
		shop.setMaxPall(!jsonMainObject.get("maxPall").toString().isEmpty() ? Integer.parseInt(jsonMainObject.get("maxPall").toString()) : null);		
		if(jsonMainObject.get("isTailLift") != null) {
			shop.setIsTailLift(jsonMainObject.get("isTailLift").toString().equals("true") ? true : false);
		}else {
			shop.setIsTailLift(null);
		}		
		if(jsonMainObject.get("isInternalMovement") != null) {
			shop.setIsInternalMovement(jsonMainObject.get("isInternalMovement").toString().equals("true") ? true : false);
		}else {
			shop.setIsInternalMovement(null);
		}
		shopService.updateShop(shop);
		response.put("status", "200");
		response.put("message", "Магазин " + shop.getNumshop() + " обновлён!");
		return response;
	}

	/**
	 * отдаёт весе магазины
	 * 
	 * @return
	 */
	@GetMapping("/manager/getAllShops")
	public List<Shop> getAllShops() {
		return shopService.getShopList();
	}
	
	/**
	 * Отдаёт все магазы с пометкой для внутреннего перемещения
	 * @return
	 */
	@GetMapping("/manager/getInternalMovementShops")
	public List<Shop> getInternalMovementShops() {
		return shopService.getShopList().stream().filter(s-> s.getIsInternalMovement() != null && s.getIsInternalMovement()).collect(Collectors.toList());
	}

	/**
	 * загружает все магазины из фавйла excel
	 * 
	 * @param model
	 * @param request
	 * @param session
	 * @param excel
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/map/loadShop", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, String> postShopListLogist(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "excel", required = false) MultipartFile excel)
			throws InvalidFormatException, IOException, ServiceException {
		poiExcel.loadShopFromMarket(poiExcel.getFileByMultipartTarget(excel, request, "shop.xlxs"));
		Map<String, String> response = new HashMap<String, String>();
		response.put("200", "Список магазинов обновлён");
		return response;
	}

	@GetMapping("/map/getStackTrace")
	public Message getMessageAfterRouting() {
		Message message = new Message();
		String body = poiExcel.classLog + routingMachine.classLog + classLog;
//		System.out.println(body);
		message.setComment(body);
		return message;
	}

	/**
	 * Запрос проверки расстояний. <br>
	 * Принимает excel
	 * 
	 * @param excel
	 * @return
	 * @throws ParseException
	 * @throws ServiceException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	@RequestMapping(value = "/map/5", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<Long, List<MapResponse>> getRouteHasExcel(
			@RequestParam(value = "excel", required = false) MultipartFile excel, HttpServletRequest request)
			throws ParseException, ServiceException, InvalidFormatException, IOException {
		poiExcel.classLog = null;
		routingMachine.classLog = null;
		classLog = null;
		File target = poiExcel.getFileByMultipartTarget(excel, request, "routes.xlsx");
		java.util.Date t2 = new java.util.Date();
		Map<Long, List<Double[]>> pointsMap = poiExcel.readExcelForWays(target);
		if (pointsMap == null) {
			classLog = classLog + "\n Controller -- Отмена построения маршрутов!";
			return null;
		}
		java.util.Date t3 = new java.util.Date();
		Map<Long, List<GHRequest>> ghRequests = routingMachine.generateGHRequestHasMap(pointsMap);
		java.util.Date t4 = new java.util.Date();
//		ghRequests.forEach((k,v)-> System.out.println(k + "  ---  " + v));

		Map<Long, List<Shop[]>> shopPointsMap = poiExcel.getShopAsPointExcel(target);
		java.util.Date t5 = new java.util.Date();
//		shopPointsMap.forEach((k,v)-> v.forEach(sh->{
//			System.out.println(k + "  ---  " + sh[0].getNumshop() +" ----> " + sh[1].getNumshop());
//		}));
		GraphHopper hopper = routingMachine.getGraphHopper();
////		ghRequests.forEach(r->System.out.println(r.getCustomModel()));
		Map<Long, List<MapResponse>> mapResult = new HashMap<Long, List<MapResponse>>();
		ghRequests.forEach((k, v) -> {
			List<GHRequest> ghRequestsList = v;
			if (ghRequestsList == null) {
				mapResult.put(k, null);
			} else {
				List<MapResponse> listResult = new ArrayList<MapResponse>();
				List<Shop[]> shopPoints = shopPointsMap.get(k);
				for (GHRequest req : ghRequestsList) {
					int index = ghRequestsList.indexOf(req);
					GHResponse rsp = hopper.route(req);
					if (rsp.getAll().isEmpty()) {
						rsp.getErrors().forEach(e -> System.out.println(e));
						rsp.getErrors().forEach(e -> e.printStackTrace());
						listResult.add(new MapResponse(null, null, null, 500.0, 500));
					}
					ResponsePath path = rsp.getBest();
					List<ResponsePath> listPath = rsp.getAll();
					for (ResponsePath pathI : listPath) {
						if (pathI.getDistance() < path.getDistance()) {
							path = pathI;
						}
					}
					PointList pointList = path.getPoints();
					path.getPathDetails();
					List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
					pointList.forEach(p -> result.add(p.toGeoJson()));
					List<Double[]> resultPoints = new ArrayList<Double[]>();
					double cash = 0.0;
					for (Double[] point : result) {
						cash = point[0];
						point[0] = point[1];
						point[1] = cash;
						resultPoints.add(point);
					}
					listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
							shopPoints.get(index)[0], shopPoints.get(index)[1]));
				}
				mapResult.put(k, listResult);
			}
		});
		java.util.Date t6 = new java.util.Date();
//		System.out.println(t2.getTime() - t1.getTime() + "ms poiExcel.getFileByMultipart(excel)");
//		System.out.println(t3.getTime() - t2.getTime() + "ms poiExcel.readExcelForWays(target)");
//		System.out.println(t4.getTime() - t3.getTime() + "ms generateGHRequestHasMap(pointsMap)");
//		System.out.println(t5.getTime() - t4.getTime() + "ms poiExcel.getShopAsPointExcel(target))");
//		System.out.println(t6.getTime() - t5.getTime() + "ms routingMachine");
//		System.out.println(t6.getTime() - t1.getTime() + "ms allTime");
		classLog = classLog + "\n Controller -- " + (t3.getTime() - t2.getTime())
				+ "ms poiExcel.readExcelForWays(target)";
		classLog = classLog + "\n Controller -- " + (t4.getTime() - t3.getTime())
				+ "ms generateGHRequestHasMap(pointsMap)";
		classLog = classLog + "\n Controller -- " + (t5.getTime() - t4.getTime())
				+ "ms poiExcel.getShopAsPointExcel(target))";
		classLog = classLog + "\n Controller -- " + (t6.getTime() - t5.getTime()) + "ms routingMachine";
		classLog = classLog + "\n Controller -- " + (t6.getTime() - t2.getTime()) + "ms allTime";
//		mapResult.forEach((k,v) -> {
//			if(v == null) {
//				System.out.println(k + " --  null");
//			}else {
//				v.forEach(m-> System.out.println(k + " -- " + m.getStartShop().getNumshop() + " --> " + m.getEndShop().getNumshop()));
//			}			
//		});
		return mapResult;
	}

	/**
	 * тестовый метод, для выгрузки расстояний по одному магазину относительно всех
	 * магазов
	 * 
	 * @param excel
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/map/666/{num}", method = RequestMethod.GET)
	public Map<Long, MapResponse> getSplitWayHasNumShop666(HttpServletRequest request, @PathVariable Integer num)
			throws ParseException {
		Map<Long, MapResponse> mapResult = new HashMap<Long, MapResponse>();
		Shop stock = shopService.getShopByNum(num);
		List<Shop> shops = shopService.getShopList();
		Map<Integer, GHRequest> ghRequests = new HashMap<Integer, GHRequest>();

		CustomModel model = routingMachine.parseJSONFromClientCustomModel(null);
		shops.forEach(s -> {
			GHRequest stockGhRequest = new GHRequest()
					.addPoint(new GHPoint(Double.parseDouble(stock.getLat()), Double.parseDouble(stock.getLng())))
					.addPoint(new GHPoint(Double.parseDouble(s.getLat()), Double.parseDouble(s.getLng())))
					.setProfile("car_custom").setCustomModel(model);
			ghRequests.put(s.getNumshop(), stockGhRequest);
		});
//		System.out.println(ghRequests.size() + " - Всего запросов");
		GraphHopper hopper = routingMachine.getGraphHopper();
		ghRequests.forEach((k, v) -> {
			GHResponse rsp = hopper.route(v);
			ResponsePath path = rsp.getBest();
			List<ResponsePath> listPath = rsp.getAll();
			for (ResponsePath pathI : listPath) {
				if (pathI.getDistance() < path.getDistance()) {
					path = pathI;
				}
			}
			PointList pointList = path.getPoints();
			path.getPathDetails();
			List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
			pointList.forEach(p -> result.add(p.toGeoJson()));
			List<Double[]> resultPoints = new ArrayList<Double[]>();
			double cash = 0.0;
			for (Double[] point : result) {
				cash = point[0];
				point[0] = point[1];
				point[1] = cash;
				resultPoints.add(point);
			}
			MapResponse listResult = new MapResponse(resultPoints, path.getDistance(), path.getTime(), null, null);
			mapResult.put(Long.parseLong(k + ""), listResult);
			System.out.println("Рассчитан " + k + " магазин");
		});
		poiExcel.createRazvozForJa(mapResult, request);
		return null;
	}

	/**
	 * Основной метод постройки и отладки отдельных маршрутов по точкам принимает
	 * номер маршрута, берет координаты из БД отдаёт информацию по магазинам в
	 * ответе
	 * 
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/map/way/4", method = RequestMethod.POST)
	public List<MapResponse> getSplitWayHasNumShop(@RequestBody String str) throws ParseException {
		List<GHRequest> ghRequests = routingMachine.parseJSONFromClientRequestSplit(str);
		List<Shop[]> shopPoints = routingMachine.getShopAsPoint(str);
		GraphHopper hopper = routingMachine.getGraphHopper();
//		ghRequests.forEach(r->System.out.println(r.getCustomModel()));
		List<MapResponse> listResult = new ArrayList<MapResponse>();
		for (GHRequest req : ghRequests) {
			int index = ghRequests.indexOf(req);

			GHResponse rsp = hopper.route(req);
			if (rsp.getAll().isEmpty()) {
				rsp.getErrors().forEach(e -> System.out.println(e));
				rsp.getErrors().forEach(e -> e.printStackTrace());
				listResult.add(new MapResponse(null, null, null, 500.0, 500));
			}
//			System.err.println(rsp.getAll().size());
			if (rsp.getAll().size() > 1) {
//				rsp.getAll().forEach(p -> System.out.println(p.getDistance() + "    " + p.getTime()));
			}
			ResponsePath path = rsp.getBest();
			List<ResponsePath> listPath = rsp.getAll();
			for (ResponsePath pathI : listPath) {
				if (pathI.getDistance() < path.getDistance()) {
					path = pathI;
				}
			}
//			System.out.println(roundВouble(path.getDistance()/1000, 2) + "km, " + path.getTime() + " time");
			PointList pointList = path.getPoints();
			path.getPathDetails();
			List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
			pointList.forEach(p -> result.add(p.toGeoJson()));
			List<Double[]> resultPoints = new ArrayList<Double[]>();
			double cash = 0.0;
			for (Double[] point : result) {
				cash = point[0];
				point[0] = point[1];
				point[1] = cash;
				resultPoints.add(point);
			}
			listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(), shopPoints.get(index)[0],
					shopPoints.get(index)[1]));
		}
		return listResult;
	}

	@RequestMapping(value = "/map/addShop", method = RequestMethod.POST)
	public List<MapResponse> getAddShop(@RequestBody String str) throws ParseException {
		Map<String, String> shops = new HashMap<String, String>();
		String[] mass = str.split("},");
		for (int i = 0; i < mass.length; i++) {
			if (i == 0) {
				String json = removeCharAt(mass[0], 0) + "}";
				shops.put(json.split(":", 2)[0].substring(1, json.split(":", 2)[0].length() - 1),
						json.split(":", 2)[1]);
			}
			if (i == mass.length - 1) {
				String json = removeCharAt(mass[mass.length - 1], mass[mass.length - 1].length() - 1);
				shops.put(json.split(":", 2)[0].substring(1, json.split(":", 2)[0].length() - 1),
						json.split(":", 2)[1]);
			}
			String json = mass[i] + "}";
			shops.put(json.split(":", 2)[0].substring(1, json.split(":", 2)[0].length() - 1), json.split(":", 2)[1]);

		}
		JSONParser parser = new JSONParser();
		shops.entrySet().forEach(s -> {
			Shop shop = null;
			if (s.getKey().equals("\"100")) {
				shop = shopService.getShopByNum(Integer.parseInt("100"));
			} else {
				shop = shopService.getShopByNum(Integer.parseInt(s.getKey().toString()));
			}

			JSONObject object = null;
			try {
				object = (JSONObject) parser.parse(s.getValue());
				if (shop != null) {
					shop.setAddress(object.get("address").toString());
					JSONObject coord = (JSONObject) parser.parse(object.get("coord").toString());
					shop.setLat(coord.get("lat").toString());
					shop.setLng(coord.get("lng").toString());
					shopService.updateShop(shop);
//					System.out.println(shop);
				} else {
					JSONObject coord = (JSONObject) parser.parse(object.get("coord").toString());
					if (s.getKey().equals("\"100")) {
						shop = new Shop(Integer.parseInt("100"), object.get("address").toString(),
								coord.get("lat").toString(), coord.get("lng").toString());
					} else {
						shop = new Shop(Integer.parseInt(s.getKey()), object.get("address").toString(),
								coord.get("lat").toString(), coord.get("lng").toString());
					}
					shopService.saveShop(shop);
					System.err.println(shop);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
//		
////		JSONArray lang = (JSONArray) parser.parse(str);
//		JSONArray jsonMainObject = (JSONArray) parser.parse(str);
//		System.out.println(jsonMainObject.size());
		return null;
	}

	/**
	 * Основной метод постройки и отладки отдельных маршрутов по точкам
	 * 
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/map/way/3", method = RequestMethod.POST)
	public List<MapResponse> getSplitWay(@RequestBody String str) throws ParseException {
//		System.out.println(str);
		List<GHRequest> ghRequests = routingMachine.parseJSONFromClientRequestSplit(str);
		GraphHopper hopper = routingMachine.getGraphHopper();
//		ghRequests.forEach(r->System.out.println(r.getCustomModel()));
		List<MapResponse> listResult = new ArrayList<MapResponse>();
		for (GHRequest req : ghRequests) {
			GHResponse rsp = hopper.route(req);
			if (rsp.getAll().isEmpty()) {
				rsp.getErrors().forEach(e -> System.out.println(e));
				rsp.getErrors().forEach(e -> e.printStackTrace());
				listResult.add(new MapResponse(null, null, null, 500.0, 500));
			}
			ResponsePath path = rsp.getBest();
			List<ResponsePath> listPath = rsp.getAll();
			for (ResponsePath pathI : listPath) {
				if (pathI.getDistance() < path.getDistance()) {
					path = pathI;
				}
			}
			PointList pointList = path.getPoints();
			path.getPathDetails();
			List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
			pointList.forEach(p -> result.add(p.toGeoJson()));
			List<Double[]> resultPoints = new ArrayList<Double[]>();
			double cash = 0.0;
			for (Double[] point : result) {
				cash = point[0];
				point[0] = point[1];
				point[1] = cash;
				resultPoints.add(point);
			}
			listResult.add(new MapResponse(null, resultPoints, null, path.getDistance(), path.getTime()));
		}
		return listResult;
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@RequestMapping(value = "/map/setDefaultParameters", method = RequestMethod.POST)
	public Map<String, String> setDefaultParameters(@RequestBody String str, HttpServletRequest request)
			throws ParseException {
		Map<String, String> response = new HashMap();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		routingMachine.roadClassPRIMARY = jsonMainObject.get("roadClassPRIMARY") != null
				? jsonMainObject.get("roadClassPRIMARY").toString()
				: routingMachine.roadClassPRIMARY;
		routingMachine.roadClassTERTIARY = jsonMainObject.get("roadClassTERTIARY") != null
				? jsonMainObject.get("roadClassTERTIARY").toString()
				: routingMachine.roadClassTERTIARY;
		routingMachine.roadClassRESIDENTIAL = jsonMainObject.get("roadClassRESIDENTIAL") != null
				? jsonMainObject.get("roadClassRESIDENTIAL").toString()
				: routingMachine.roadClassRESIDENTIAL;
		routingMachine.roadClassSECONDARY = jsonMainObject.get("roadClassSECONDARY") != null
				? jsonMainObject.get("roadClassSECONDARY").toString()
				: routingMachine.roadClassSECONDARY;
		routingMachine.roadEnvironmentFERRY = jsonMainObject.get("roadEnvironmentFERRY") != null
				? jsonMainObject.get("roadEnvironmentFERRY").toString()
				: routingMachine.roadEnvironmentFERRY;
		routingMachine.maxAxleLoad = jsonMainObject.get("maxAxleLoad") != null
				? jsonMainObject.get("maxAxleLoad").toString()
				: routingMachine.maxAxleLoad;
		routingMachine.maxAxleLoadCoeff = jsonMainObject.get("maxAxleLoadCoeff") != null
				? jsonMainObject.get("maxAxleLoadCoeff").toString()
				: routingMachine.maxAxleLoadCoeff;
		routingMachine.surfaceMISSING = jsonMainObject.get("surfaceMISSING") != null
				? jsonMainObject.get("surfaceMISSING").toString()
				: routingMachine.surfaceMISSING;
		routingMachine.surfaceGRAVEL = jsonMainObject.get("surfaceGRAVEL") != null
				? jsonMainObject.get("surfaceGRAVEL").toString()
				: routingMachine.surfaceGRAVEL;
		routingMachine.surfaceCOMPACTED = jsonMainObject.get("surfaceCOMPACTED") != null
				? jsonMainObject.get("surfaceCOMPACTED").toString()
				: routingMachine.surfaceCOMPACTED;
		routingMachine.surfaceASPHALT = jsonMainObject.get("surfaceASPHALT") != null
				? jsonMainObject.get("surfaceASPHALT").toString()
				: routingMachine.surfaceASPHALT;
		routingMachine.distanceInfluence = jsonMainObject.get("distanceInfluence") != null
				? jsonMainObject.get("distanceInfluence").toString()
				: routingMachine.distanceInfluence;
		routingMachine.roadClassUNCLASSIFIED = jsonMainObject.get("roadClassUNCLASSIFIED") != null
				? jsonMainObject.get("roadClassUNCLASSIFIED").toString()
				: routingMachine.roadClassUNCLASSIFIED;
		routingMachine.roadClassMOTORWAYTOLL = jsonMainObject.get("roadClassMOTORWAYTOLL") != null
				? jsonMainObject.get("roadClassMOTORWAYTOLL").toString()
				: routingMachine.roadClassMOTORWAYTOLL;
		response.put("distanceInfluence", routingMachine.distanceInfluence);
		response.put("roadClassPRIMARY", routingMachine.roadClassPRIMARY);
		response.put("roadClassTERTIARY", routingMachine.roadClassTERTIARY);
		response.put("roadClassRESIDENTIAL", routingMachine.roadClassRESIDENTIAL);
		response.put("roadClassSECONDARY", routingMachine.roadClassSECONDARY);
		response.put("roadEnvironmentFERRY", routingMachine.roadEnvironmentFERRY);
		response.put("maxAxleLoad", routingMachine.maxAxleLoad);
		response.put("maxAxleLoadCoeff", routingMachine.maxAxleLoadCoeff);
		response.put("surfaceMISSING", routingMachine.surfaceMISSING);
		response.put("surfaceGRAVEL", routingMachine.surfaceGRAVEL);
		response.put("surfaceCOMPACTED", routingMachine.surfaceCOMPACTED);
		response.put("surfaceASPHALT", routingMachine.surfaceASPHALT);
		response.put("roadClassUNCLASSIFIED", routingMachine.roadClassUNCLASSIFIED);
		response.put("roadClassMOTORWAYTOLL", routingMachine.roadClassMOTORWAYTOLL);
		User thisUser = getThisUser();
		String text = "Настройки маршрутизатора были изменены пользователем " + thisUser.getLogin() + " / "
				+ thisUser.getSurname() + " " + thisUser.getName();
		text = text + "\ndistanceInfluence - " + routingMachine.distanceInfluence;
		text = text + "\nroadClassPRIMARY - " + routingMachine.roadClassPRIMARY;
		text = text + "\nroadClassRESIDENTIAL - " + routingMachine.roadClassRESIDENTIAL;
		text = text + "\nroadClassSECONDARY - " + routingMachine.roadClassSECONDARY;
		text = text + "\nroadEnvironmentFERRY - " + routingMachine.roadEnvironmentFERRY;
		text = text + "\nmaxAxleLoad - " + routingMachine.maxAxleLoad;
		text = text + "\nmaxAxleLoadCoeff - " + routingMachine.maxAxleLoadCoeff;
		text = text + "\nsurfaceMISSING - " + routingMachine.surfaceMISSING;
		text = text + "\nsurfaceGRAVEL - " + routingMachine.surfaceGRAVEL;
		text = text + "\nsurfaceCOMPACTED - " + routingMachine.surfaceCOMPACTED;
		text = text + "\nsurfaceASPHALT - " + routingMachine.surfaceASPHALT;
		text = text + "\nroadClassUNCLASSIFIED - " + routingMachine.roadClassUNCLASSIFIED;
		text = text + "\nroadClassMOTORWAYTOLL - " + routingMachine.roadClassMOTORWAYTOLL;
		final String string = text;
		mailService.sendSimpleEmailTwiceUsers(request, "Изменение настроек маршрутизатора", string,
				"YakubovE@dobronom.by", "GrushevskiyD@dobronom.by");
		return response;
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@RequestMapping(value = "/map/getDefaultParameters", method = RequestMethod.GET)
	public Map<String, String> getDefaultParameters() {
		Map<String, String> response = new HashMap();
		response.put("distanceInfluence", routingMachine.distanceInfluence);
		response.put("roadClassPRIMARY", routingMachine.roadClassPRIMARY);
		response.put("roadClassTERTIARY", routingMachine.roadClassTERTIARY);
		response.put("roadClassRESIDENTIAL", routingMachine.roadClassRESIDENTIAL);
		response.put("roadClassSECONDARY", routingMachine.roadClassSECONDARY);
		response.put("roadEnvironmentFERRY", routingMachine.roadEnvironmentFERRY);
		response.put("maxAxleLoad", routingMachine.maxAxleLoad);
		response.put("maxAxleLoadCoeff", routingMachine.maxAxleLoadCoeff);
		response.put("surfaceMISSING", routingMachine.surfaceMISSING);
		response.put("surfaceGRAVEL", routingMachine.surfaceGRAVEL);
		response.put("surfaceCOMPACTED", routingMachine.surfaceCOMPACTED);
		response.put("surfaceASPHALT", routingMachine.surfaceASPHALT);
		response.put("roadClassUNCLASSIFIED", routingMachine.roadClassUNCLASSIFIED);
		response.put("roadClassMOTORWAYTOLL", routingMachine.roadClassMOTORWAYTOLL);
		return response;
	}

	/**
	 * Метод построения маршрута по дополнительным параметрам параметрам и
	 * нескольким точкам. Возвращает один маршрут целиком Если параметры в запросе
	 * отсутствуют - применяет дефолтные параметры
	 * 
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/map/way/2", method = RequestMethod.POST)
	public MapResponse getWayAndParameters(@RequestBody String str) throws ParseException {
		GHRequest req = routingMachine.parseJSONFromClientRequest(str);
		GraphHopper hopper = routingMachine.getGraphHopper();
		CustomModel model = routingMachine.parseJSONFromClientCustomModel(str);
		System.out.println(model.toString());
		req.setCustomModel(model);

		GHResponse rsp = hopper.route(req);

		if (rsp.getAll().isEmpty()) {
			rsp.getErrors().forEach(e -> System.out.println(e));
			rsp.getErrors().forEach(e -> e.printStackTrace());
			return new MapResponse(null, null, null, 500.0, 500);
		}

		ResponsePath path = rsp.getBest();
		List<ResponsePath> listPath = rsp.getAll();
		for (ResponsePath pathI : listPath) {
			if (pathI.getDistance() < path.getDistance()) {
				path = pathI;
			}
		}

		PointList pointList = path.getPoints();
		path.getPathDetails();

		List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
		pointList.forEach(p -> result.add(p.toGeoJson()));
		List<Double[]> resultPoints = new ArrayList<Double[]>();
		double cash = 0.0;
		for (Double[] point : result) {
			cash = point[0];
			point[0] = point[1];
			point[1] = cash;
			resultPoints.add(point);
		}
		return new MapResponse(null, resultPoints, null, path.getDistance(), path.getTime());
	}

	/**
	 * Метод построения маршрута по дефолтным параметрам и нескольким точкам.
	 * Возвращает один маршрут целиком
	 * 
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/map/way/1", method = RequestMethod.POST)
	public MapResponse getWay(@RequestBody String str) throws ParseException {
		GHRequest req = routingMachine.parseJSONFromClientRequest(str);
//		StopWatch sw = new StopWatch().start();
		GraphHopper hopper = routingMachine.getGraphHopper();
		CustomModel model = new CustomModel();
		model.addToPriority(If("road_class == PRIMARY", MULTIPLY, routingMachine.roadClassPRIMARY));
		model.addToPriority(If("road_class == TERTIARY", MULTIPLY, routingMachine.roadClassTERTIARY));
		model.addToPriority(If("road_class == SECONDARY", MULTIPLY, routingMachine.roadClassSECONDARY));
		model.addToPriority(If("road_environment == FERRY", MULTIPLY, routingMachine.roadEnvironmentFERRY));
		model.addToPriority(If("road_class == RESIDENTIAL", MULTIPLY, routingMachine.roadClassRESIDENTIAL));
		model.addToPriority(
				If("max_axle_load < " + routingMachine.maxAxleLoad, MULTIPLY, routingMachine.maxAxleLoadCoeff));
		model.addToPriority(If("surface == MISSING", MULTIPLY, routingMachine.surfaceMISSING));
		model.addToPriority(If("surface == GRAVEL", MULTIPLY, routingMachine.surfaceGRAVEL));
		model.addToPriority(If("surface == COMPACTED", MULTIPLY, routingMachine.surfaceCOMPACTED));
		model.addToPriority(If("true", LIMIT, "100"));
		req.setCustomModel(model);

		GHResponse rsp = hopper.route(req);

		if (rsp.getAll().isEmpty()) {
			rsp.getErrors().forEach(e -> System.out.println(e));
			rsp.getErrors().forEach(e -> e.printStackTrace());
			return new MapResponse(null, null, null, 500.0, 500);
		}

		ResponsePath path = rsp.getBest();
		List<ResponsePath> listPath = rsp.getAll();
		for (ResponsePath pathI : listPath) {
			if (pathI.getDistance() < path.getDistance()) {
				path = pathI;
			}
		}

//		long took = sw.stop().getNanos() / 1_000_000;
		// points, distance in meters and time in millis of the full path
		PointList pointList = path.getPoints();
		path.getPathDetails();

		Translation tr = hopper.getTranslationMap().getWithFallBack(Locale.UK);
		InstructionList il = path.getInstructions();
		// iterate over all turn instructions
//	            for (Instruction instruction : il) {
//	                 System.out.println("distance " + instruction.getDistance() + " for instruction: " + instruction.getTurnDescription(tr));
//	            }
		List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
		pointList.forEach(p -> result.add(p.toGeoJson()));
		List<Double[]> resultPoints = new ArrayList<Double[]>();
		double cash = 0.0;
		for (Double[] point : result) {
			cash = point[0];
			point[0] = point[1];
			point[1] = cash;
			resultPoints.add(point);
		}
		return new MapResponse(null, resultPoints, null, path.getDistance(), path.getTime());
	}

	/**
	 * Редактирование заявки POST
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/manager/editProcurement", method = RequestMethod.POST)
	public Map<String, String> editProcurement(@RequestBody String str)
			throws IOException, ServletException, ParseException {
		HashMap<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		String points = jsonMainObject.get("points").toString();
		String way = (String) jsonMainObject.get("way");
		Order order = orderService.getOrderById(Integer.parseInt(jsonMainObject.get("idOrder").toString()));
		if (order.getStatus() == 20 || order.getStatus() == 17 || order.getStatus() == 15) { // общее редактирование, пока маршрут не создан
			order.setCounterparty((String) jsonMainObject.get("contertparty"));
			order.setContact((String) jsonMainObject.get("contact"));
			order.setCargo((String) jsonMainObject.get("cargo"));
			order.setTypeLoad((String) jsonMainObject.get("typeLoad"));
			order.setMethodLoad((String) jsonMainObject.get("methodLoad"));
//			order.setMarketInfo(jsonMainObject.get("marketInfo") != null ? jsonMainObject.get("marketInfo").toString() : null);
			order.setTypeTruck((String) jsonMainObject.get("typeTruck"));
			order.setTemperature((String) jsonMainObject.get("temperature"));
			order.setLoadNumber(jsonMainObject.get("loadNumber") != null ? jsonMainObject.get("loadNumber").toString() : null);
			order.setControl((boolean) jsonMainObject.get("control").toString().equals("true") ? true : false);
			order.setComment((String) jsonMainObject.get("comment"));
//			order.setStatus(order);
			order.setDateDelivery(jsonMainObject.get("dateDelivery").toString().isEmpty() ? null
					: Date.valueOf((String) jsonMainObject.get("dateDelivery")));
			order.setWay(way);
			order.setStacking(jsonMainObject.get("stacking").toString().equals("true") ? true : false);
			order.setIncoterms(jsonMainObject.get("incoterms") == null ? null : jsonMainObject.get("incoterms").toString());
			order.setMarketNumber(jsonMainObject.get("marketNumber").toString().isEmpty() ? null : jsonMainObject.get("marketNumber").toString());
			order.setIsInternalMovement(jsonMainObject.get("isInternalMovement") == null ? null : jsonMainObject.get("isInternalMovement").toString());

			User thisUser = getThisUser();
//			order.setManager(order.getManager() + "\n отредактировал: " + thisUser.getSurname() + " "
//					+ thisUser.getName() + " " + thisUser.getPatronymic() + " " + Date.valueOf(LocalDate.now()));
			order.setChangeStatus(order.getChangeStatus() + "\nОтредактировал: " + thisUser.getSurname() + " " + thisUser.getName() + " " + thisUser.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:SS")));
			String firstJsonRequest = points.substring(1, points.length() - 1);
			List<Address> addressesOld = order.getAddresses().stream().collect(Collectors.toList());
			addressesOld.sort(comparatorAddressId);
			String[] mass = firstJsonRequest.split("},");
			for (int i = 0; i < mass.length; i++) {
				String string = mass[i];
				if (string.charAt(string.length() - 1) != '}') {
					string = string + "}";
				}
				JSONObject jsonpObject = (JSONObject) parser.parse(string);
				Address address = addressesOld.get(i);
				address.setDate(jsonpObject.get("date").toString().isEmpty() ? null
						: Date.valueOf((String) jsonpObject.get("date")));
				address.setBodyAddress((String) jsonpObject.get("bodyAdress"));
				address.setType((String) jsonpObject.get("type"));
				address.setPall(jsonpObject.get("pall").toString().isEmpty() ? null : (String) jsonpObject.get("pall"));
				address.setWeight(
						jsonpObject.get("weight").toString().isEmpty() ? null : (String) jsonpObject.get("weight"));
				address.setVolume(
						jsonpObject.get("volume").toString().isEmpty() ? null : (String) jsonpObject.get("volume"));
				address.setTimeFrame(jsonpObject.get("timeFrame").toString().isEmpty() ? null
						: (String) jsonpObject.get("timeFrame"));
				address.setContact(
						jsonpObject.get("contact").toString().isEmpty() ? null : (String) jsonpObject.get("contact"));
				address.setCargo(
						jsonpObject.get("cargo").toString().isEmpty() ? null : (String) jsonpObject.get("cargo"));
				address.setCustomsAddress(jsonpObject.get("customsAddress").toString().isEmpty() ? null
						: (String) jsonpObject.get("customsAddress"));
				address.setTnvd(jsonpObject.get("tnvd") != null ? jsonpObject.get("tnvd").toString() : null);
				address.setPointNumber(jsonpObject.get("pointNumber") != null ? Integer.parseInt(jsonpObject.get("pointNumber").toString()) : null);
				if (!jsonpObject.get("time").toString().isEmpty()) {
					address.setTime(Time.valueOf(
							LocalTime.of(Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[0]),
									Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[1]))));
				} else {
					address.setTime(null);
				}
				addressService.updateAddress(address);
			}
			orderService.updateOrder(order);
			response.put("status", "200");
			response.put("message", "Заявка отредактирована");
			System.out.println("Заявка отредактирована");
		} else {
			User thisUser = getThisUser();
			order.setChangeStatus(order.getChangeStatus() + "\nОтредактировал: " + thisUser.getSurname() + " " + thisUser.getName() + " " + thisUser.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:SS")));
			String firstJsonRequest = points.substring(1, points.length() - 1);
			List<Address> addressesOld = order.getAddresses().stream().collect(Collectors.toList());
			addressesOld.sort(comparatorAddressId);
			orderService.updateOrder(order);
			String[] mass = firstJsonRequest.split("},");
			for (int i = 0; i < mass.length; i++) {
				String string = mass[i];
				if (string.charAt(string.length() - 1) != '}') {
					string = string + "}";
				}
				JSONObject jsonpObject = (JSONObject) parser.parse(string);
				Address address = addressesOld.get(i);
				// начинается обработка сравнения: если вдрес изменился - то создаётся
				// корректировочный адрес
				if (!jsonpObject.get("bodyAdress").toString().equals(address.getBodyAddress())) {
					Integer oldId = jsonpObject.get("idAddress") == null ? null
							: Integer.parseInt(jsonpObject.get("idAddress").toString());
					if (oldId == null) {
						response.put("status", "100");
						response.put("message", "Отсутствует idAddress в теле запроса");
						System.err.println("Отсутствует idAddress в теле запроса");
						return response;
					}
					if (!jsonpObject.get("oldIdaddress").toString().equals("0")) { // если опять корректируется
																					// корректный адрес - он просто
																					// редактируется
						
						Address addressNewCorrect = addressService
								.getAddressById(Integer.parseInt(jsonpObject.get("idAddress").toString()));
						addressNewCorrect.setDate(jsonpObject.get("date").toString().isEmpty() ? null
								: Date.valueOf((String) jsonpObject.get("date")));
						addressNewCorrect.setBodyAddress((String) jsonpObject.get("bodyAdress"));
						addressNewCorrect.setType((String) jsonpObject.get("type"));
						addressNewCorrect.setPall(
								jsonpObject.get("pall").toString().isEmpty() ? null : (String) jsonpObject.get("pall"));
						addressNewCorrect.setWeight(jsonpObject.get("weight").toString().isEmpty() ? null
								: (String) jsonpObject.get("weight"));
						addressNewCorrect.setVolume(jsonpObject.get("volume").toString().isEmpty() ? null
								: (String) jsonpObject.get("volume"));
						addressNewCorrect.setTimeFrame(jsonpObject.get("timeFrame").toString().isEmpty() ? null
								: (String) jsonpObject.get("timeFrame"));
						addressNewCorrect.setContact(jsonpObject.get("contact").toString().isEmpty() ? null
								: (String) jsonpObject.get("contact"));
						addressNewCorrect.setTnvd(jsonpObject.get("tnvd") != null ? jsonpObject.get("tnvd").toString() : null);
						addressNewCorrect.setCargo(jsonpObject.get("cargo").toString().isEmpty() ? null
								: (String) jsonpObject.get("cargo"));
						addressNewCorrect
								.setCustomsAddress(jsonpObject.get("customsAddress").toString().isEmpty() ? null
										: (String) jsonpObject.get("customsAddress"));
						if (!jsonpObject.get("time").toString().isEmpty()) {
							addressNewCorrect.setTime(Time.valueOf(LocalTime.of(
									Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[0]),
									Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[1]))));
						} else {
							addressNewCorrect.setTime(null);
						}
						addressService.updateAddress(addressNewCorrect);

						response.put("status", "200");
						response.put("message", "Корректировка обновлена");
//						System.out.println("Корректировка обновлена");
					} else {
						Address oldAddress = addressesOld.get(i);
						oldAddress.setIsCorrect(false);
						addressService.updateAddress(oldAddress);
						Address correctAddress = new Address((String) jsonpObject.get("bodyAdress"),
								jsonpObject.get("date").toString().isEmpty() ? null
										: Date.valueOf((String) jsonpObject.get("date")),
								(String) jsonpObject.get("type"),
								jsonpObject.get("pall").toString().isEmpty() ? null : (String) jsonpObject.get("pall"),
								jsonpObject.get("weight").toString().isEmpty() ? null
										: (String) jsonpObject.get("weight"),
								jsonpObject.get("volume").toString().isEmpty() ? null
										: (String) jsonpObject.get("volume"),
								jsonpObject.get("timeFrame").toString().isEmpty() ? null
										: (String) jsonpObject.get("timeFrame"),
								jsonpObject.get("contact").toString().isEmpty() ? null
										: (String) jsonpObject.get("contact"),
								jsonpObject.get("cargo").toString().isEmpty() ? null
										: (String) jsonpObject.get("cargo"));
						correctAddress.setCustomsAddress(jsonpObject.get("customsAddress").toString().isEmpty() ? null
								: (String) jsonpObject.get("customsAddress"));
						if (!jsonpObject.get("time").toString().isEmpty()) {
							correctAddress.setTime(Time.valueOf(LocalTime.of(
									Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[0]),
									Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[1]))));
						} else {
							correctAddress.setTime(null);
						}
						correctAddress.setIsCorrect(true);
						correctAddress.setOldIdaddress(oldId);
						correctAddress.setOrder(order);
						addressService.saveAddress(correctAddress);
						response.put("status", "200");
						response.put("message", "Внесена корректировка");
//						System.out.println("Внесена корректировка");
					}
				}
			}
		}
		return response;
	}

	/**
	 * создание маршрута через заявку
	 * @param str
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/manager/createNewRoute", method = RequestMethod.POST)
	public Map<String, String> createNewRoute(@RequestBody String str)
			throws IOException, ServletException, ParseException {
		HashMap<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		String points = jsonMainObject.get("points").toString();
		JSONArray idOrdersJSON = (JSONArray) parser.parse(jsonMainObject.get("idOrders").toString());
		Set<Order> orders = new HashSet<Order>();
		
		for (Object string : idOrdersJSON) {
			Order order = orderService.getOrderById(Integer.parseInt(string.toString()));
			//блок определеящий обязательность постановки окна на выгруцзку от Карины (временно отключен)
			if(order.getOnloadWindowDate() == null && order.getWay().equals("РБ") && order.getStatus().equals("17")) {
				response.put("status", "100");
				response.put("message", "Невозможно создать маршрут без окна на выгрузку");
				System.out.println("Невозможно создать маршрут без окна на выгрузку");
				return response;
			}else if(order.getOnloadWindowDate() != null && order.getOnloadWindowDate().toLocalDate().isBefore(LocalDate.now()) && order.getWay().equals("РБ") && !order.getWay().equals("Экспорт")) {
				response.put("status", "100");
				response.put("message", "Окно на выгрузку просрочено! Необходимо назначить новое коно на выгрузку!");
				System.out.println("Окно на выгрузку просрочено! Необходимо назначить новое коно на выгрузку!");
				//возможна доп обработка самого ордера
				return response;
			}
			orders.add(order);			
		}
		Order order = orders.stream().findFirst().get();	
		//проверяем не отменена ли заявка
		if(order.getStatus() == 10) {
			response.put("status", "100");
			response.put("message", "Заявка " + order.getCounterparty() + " отменена!");
			return response;
		}

		Route route = new Route();
		User thisUser = getThisUser();
		route.setStatusRoute("0");
		route.setStatusStock("0");
		route.setComments("international");
		route.setTime(LocalTime.of(0, 5));
		route.setWay((String) jsonMainObject.get("way"));
		route.setTypeTrailer((String) jsonMainObject.get("typeTruck"));
		route.setUserComments((String) jsonMainObject.get("comment"));
		route.setTemperature((String) jsonMainObject.get("temperature"));
		route.setCustomer(order.getManager());
		route.setLogistInfo(thisUser.getSurname() +" " + thisUser.getName() + " " + thisUser.getPatronymic() + "; "+thisUser.getTelephone());
		route.setOnloadWindowDate(order.getOnloadWindowDate());
		route.setOnloadWindowTime(order.getOnloadWindowTime());
		route.setLoadNumber(order.getLoadNumber());
		String tnvd="";
		

		route.setOrders(orders);
//		route.setStartPrice(target.getStartPrice());
		route.setIdRoute(routeService.saveRouteAndReturnId(route));

		
//		orders.forEach(o -> {
//			List<Route> routes = o.getRoutes();
//			System.err.println(routes.size() + " - кол-во routes");
//			routes.add(route);
//			o.setRoutes(routes);
//			o.setStatus(30);
////			orderService.updateOrderFromStatus(order);		
//			o.setLogist(thisUser.getSurname() +" " + thisUser.getName() + " " + thisUser.getPatronymic() + "; ");
//			o.setLogistTelephone(thisUser.getTelephone());
//			orderService.updateOrder(o);
//		});
		
		orders.forEach(o -> {
			o.setStatus(30);
			o.setLogist(thisUser.getSurname() +" " + thisUser.getName() + " " + thisUser.getPatronymic() + "; ");
			o.setLogistTelephone(thisUser.getTelephone());
			o.setChangeStatus(o.getChangeStatus() + "\nМаршрут создал: " + thisUser.getSurname() + thisUser.getName() + thisUser.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:SS")));
			orderService.updateOrder(o);
		});

		String firstJsonRequest = points.substring(1, points.length() - 1);
		List<RouteHasShop> routeHasShopsArray = new ArrayList<RouteHasShop>();
		List<JSONObject> arrayJSON = new ArrayList<>();
		String[] mass = firstJsonRequest.split("},");
		JSONObject jsonpFirstObject = (JSONObject) parser.parse(mass[0] + "}");
		JSONObject jsonpLastObject = (JSONObject) parser.parse(mass[mass.length - 1]);
		route.setDateLoadPreviously(jsonpFirstObject.get("date").toString().isEmpty() ? null
				: Date.valueOf(jsonpFirstObject.get("date").toString()));
		if (!jsonpFirstObject.get("time").toString().isEmpty()) {
			route.setTimeLoadPreviously(
					LocalTime.of(Integer.parseInt(jsonpFirstObject.get("time").toString().split(":")[0]),
							Integer.parseInt(jsonpFirstObject.get("time").toString().split(":")[1])));
		} else {
			route.setTimeLoadPreviously(null);
		}

		if(order.getTimeDelivery() == null) {
			route.setDateUnloadPreviouslyStock(order.getOnloadWindowDate() == null ? null : order.getOnloadWindowDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			route.setTimeUnloadPreviouslyStock(order.getOnloadWindowTime() == null ? null : order.getOnloadTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
			}else {
			route.setDateUnloadPreviouslyStock(order.getTimeDelivery().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			route.setTimeUnloadPreviouslyStock(order.getTimeDelivery().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
			}
		Integer summPall = 0;
		Integer summWeight = 0;
		for (String string : mass) {
			if (string.charAt(string.length() - 1) != '}') {
				string = string + "}";
			}
			JSONObject jsonpObject = (JSONObject) parser.parse(string);
			RouteHasShop routeHasShop = new RouteHasShop();
			routeHasShop.setRoute(route);
			String header = jsonpObject.get("type").toString() + " " + jsonpObject.get("number").toString();
			if (!jsonpObject.get("customsAddress").toString().isEmpty()) { // добавление таможни в комментарий
				route.setUserComments(route.getUserComments() + "\n" + header + " - Таможня: "
						+ (String) jsonpObject.get("customsAddress"));
				routeHasShop.setCustomsAddress((String) jsonpObject.get("customsAddress")); // добавление таможни в объект!
			}
			if (!jsonpObject.get("timeFrame").toString().isEmpty()) {
				route.setUserComments(route.getUserComments() + "\n" + header + " - Время работы: "
						+ (String) jsonpObject.get("timeFrame"));
			}
			if (!jsonpObject.get("contact").toString().isEmpty()) {
				route.setUserComments(route.getUserComments() + "\n" + header + " - Контакт : "
						+ (String) jsonpObject.get("contact"));
			}
			String tnvdI = jsonpObject.get("tnvd") == null ? "" : (String) jsonpObject.get("tnvd");
			if(!tnvdI.isEmpty() || !tnvdI.equals("")) {
				String out = Pattern.compile("\r\n").matcher(tnvdI).replaceAll(" ");
				String out2 = Pattern.compile("\n").matcher(out).replaceAll(" ");
				tnvd = tnvd + out2;
			}
			
			routeHasShop.setPosition((String) jsonpObject.get("type"));
			routeHasShop.setOrder(Integer.parseInt(jsonpObject.get("number").toString()));
			routeHasShop.setAddress((String) jsonpObject.get("bodyAdress"));
			routeHasShop.setCargo((String) jsonpObject.get("cargo"));
			routeHasShop
					.setPall(jsonpObject.get("pall").toString().isEmpty() ? null : (String) jsonpObject.get("pall"));
			Integer targetPall = jsonpObject.get("pall").toString().isEmpty() ? 0
					: Integer.parseInt(jsonpObject.get("pall").toString());
			routeHasShop.setWeight(
					jsonpObject.get("weight").toString().isEmpty() ? null : (String) jsonpObject.get("weight"));
			Integer targetWeigth = jsonpObject.get("weight").toString().isEmpty() ? 0
					: Integer.parseInt((String) jsonpObject.get("weight"));
			if (jsonpObject.get("type").toString().equals("Загрузка")) {
				summPall = summPall + targetPall;
				summWeight = summWeight + targetWeigth;
			}
			routeHasShop.setVolume(
					jsonpObject.get("volume").toString().isEmpty() ? null : (String) jsonpObject.get("volume"));
			routeHasShopsArray.add(routeHasShop);
		}

		route.setTnvd(tnvd);
		route.setTotalCargoWeight(summWeight.toString());
		route.setTotalLoadPall(summPall.toString());
//		route.setRouteDirection(order.getCounterparty() + " - "
//				+ routeHasShopsArray.get(routeHasShopsArray.size() - 1).getAddress().split("; ")[1] + " N"
//				+ route.getIdRoute());
		
		//тут ставим название маршрута
		String routeDirection = "";
		for (int i = 0; i < routeHasShopsArray.size(); i++) {
			RouteHasShop rhs = routeHasShopsArray.get(i);
			if(routeDirection.isEmpty()) {
				routeDirection = "<" + order.getCounterparty() + "> " + rhs.getAddress().split("; ")[1];
			}else {
				if(i == routeHasShopsArray.size()-1) {
					routeDirection = routeDirection +" - "+ rhs.getAddress().split("; ")[1] + " N" + route.getIdRoute();
				}else {
					routeDirection = routeDirection +" - "+ rhs.getAddress().split("; ")[1];
				}				
			}
		}
		route.setRouteDirection(routeDirection);
		
		route.setOptimalCost(getOptimalCost(route)); // ===============================================ПРОВЕРИТЬ=======================================
		
		routeService.saveOrUpdateRoute(route);
		routeHasShopsArray.forEach(rhs -> routeHasShopService.saveOrUpdateRouteHasShop(rhs));

		response.put("status", "200");
		response.put("message", "метод отработал");
		return response;
	}
	
	/**
	 * отдаёт заявки по дате только 5 статуса
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@GetMapping("/manager/getOrdersForSlots/{dateStart}&{dateEnd}")
	public Set<Order> getOrdersForSlots(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		return orderService.getOrderByPeriodDelivery(dateStart, dateEnd).stream().filter(o -> o.getStatus() != 10)
				.collect(Collectors.toSet());
	}
	
	/**
	 * отдаёт заявки для админов (с 8 и 100 статусами)
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@GetMapping("/manager/getOrdersForAdmin/{dateStart}&{dateEnd}")
	public Set<Order> getOrdersForAdmin(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		Set<Order> orders1 = orderService.getOrderByPeriodCreate(dateStart, dateEnd).stream().filter(o -> o.getStatus() >= 17)
				.collect(Collectors.toSet());
		Set<Order> orders2 = orderService.getOrderByPeriodCreateMarket(dateStart, dateEnd).stream().filter(o -> o.getStatus() == 8)
				.collect(Collectors.toSet());
		orders1.addAll(orders2);
		return orders1;
	}
	

	/**
	 * отдаёт заявки по дате и контрагенту. контрагента ищет по вхождению любых
	 * символов, методом LIKE!
	 * @param dateStart
	 * @param dateEnd
	 * @param counterparty
	 * @return
	 */
	@GetMapping("/manager/getOrdersHasCounterparty/{dateStart}&{dateEnd}&{counterparty}")
	public Set<Order> getListOrders(@PathVariable Date dateStart, @PathVariable Date dateEnd,
			@PathVariable String counterparty) {
		return orderService.getOrderByPeriodCreateAndCounterparty(dateStart, dateEnd, counterparty).stream()
				.collect(Collectors.toSet());
	}
	
	/**
	 * Отдаёт заявки для логистов (нет 10 статуса и ниже) по периоду создания заявки.
	 * pattern = "yyyy-MM-dd"
	 * Использует специальный запрос
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@GetMapping("/manager/getOrdersForLogist2/{dateStart}&{dateEnd}")
	public Set<Order> getListOrdersLogistV2(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		java.util.Date t1 = new java.util.Date();
		Set<Order> result = orderService.getListOrdersLogist(dateStart, dateEnd);
		java.util.Date t2 = new java.util.Date();
		System.out.println(t2.getTime() - t1.getTime() + " ms. getOrdersForLogist2");
		return result;
	}

	/**
	 * Отдаёт заявки для логистов (нет 10 статуса и ниже) по периоду создания заявки.
	 * pattern = "yyyy-MM-dd"
	 * @param dateStart 2023-10-01
	 * @param dateEnd   2023-10-01
	 * @return
	 */
	@GetMapping("/manager/getOrdersForLogist/{dateStart}&{dateEnd}")
	public Set<Order> getListOrdersLogist(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		java.util.Date t1 = new java.util.Date();
		Set<Order> result = orderService.getOrderByPeriodCreate(dateStart, dateEnd).stream().filter(o -> o.getStatus() >= 17)
				.collect(Collectors.toSet());
		java.util.Date t2 = new java.util.Date();
		System.out.println(t2.getTime() - t1.getTime() + " ms. getOrdersForLogist");
		return result;
	}

	/**
	 * Метод отвечает за смену статуса ордера
	 * 
	 * @param idOrder
	 * @param status
	 * @return
	 * @throws IOException 
	 */
	@GetMapping("/manager/changeOrderStatus/{idOrder}&{status}")
	public Map<String, String> changeOrderStatus(@PathVariable Integer idOrder, @PathVariable Integer status, HttpServletRequest request) throws IOException {
		//загружаем почтовые ящики из файлов .properties
				String appPath = request.getServletContext().getRealPath("");
				FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
				properties = new Properties();
				properties.load(fileInputStream);
		
		HashMap<String, String> response = new HashMap<String, String>();
		User user = getThisUser();
		Order order = orderService.getOrderById(idOrder);
		switch (status) {
		case 10:
			
			order.setChangeStatus(order.getChangeStatus() + "\nУдалил заявку: " + order.getManager().split(";")[0] + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:SS")));
			//отправляем на почту к логистам
			String text = "Заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+" отменена! ";
			if(!order.getRoutes().isEmpty()) {
				text = text + "\nБыли созданы следующие маршруты:";
				for (Route route : order.getRoutes()) {
					text = text +"\n" +route.getRouteDirection();
					if(route.getUser() != null) {
						text = text + ", на который назначен перевозчик: " + route.getUser().getCompanyName()+";";
					}
				}
			}else {
				text = text + "\n Маршрутов на данную заявку создано не было.";
			}
			final String message = text;
			
			if(order.getWay().equals("РБ")) {
				mailService.sendSimpleEmailTwiceUsers(request, "Отмена заявки", message, properties.getProperty("email.addNewProcurement.rb.1"), properties.getProperty("email.addNewProcurement.rb.2"));
			}else{
				mailService.sendSimpleEmailTwiceUsers(request, "Отмена заявки", message, properties.getProperty("email.addNewProcurement.import.1"), properties.getProperty("email.addNewProcurement.import.2"));
			}
			if(!order.getWay().equals("Экспорт") ||!order.getIsInternalMovement().equals("true")) {
				saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "delete");
				Message messageWS = new Message(user.getLogin(), null, "200", null, idOrder.toString(), "delete from table");
				messageWS.setPayload(order.toJsonForDelete());
				slotWebSocket.sendMessage(messageWS);
				Order orderNew = new Order();
				orderNew.setCounterparty(order.getCounterparty());
				orderNew.setCargo(order.getCargo());
				orderNew.setDateDelivery(order.getDateDelivery());
				orderNew.setMarketNumber(order.getMarketNumber());
				orderNew.setTimeUnload(order.getTimeUnload());
				orderNew.setChangeStatus("Пересоздан про прошлому заказу № " + order.getIdOrder() +" в " + LocalDate.now());
				orderNew.setNumStockDelivery(order.getNumStockDelivery());
				orderNew.setPall(order.getPall());
				orderNew.setSku(order.getSku());
				orderNew.setMonoPall(order.getMonoPall());
				orderNew.setMixPall(order.getMixPall());
				orderNew.setStatus(5);
				orderService.saveOrder(orderNew);
			}
			break;
		default:
			System.out.println("default");
			break;
		}
		
		order.setStatus(status);
		orderService.updateOrder(order);
		response.put("status", "200");
		response.put("message", "статус Order " + idOrder + " изменен на " + status); 
//		response.put("payload", order.toJsonForDelete());
		return response;
	}

	/**
	 * Отдаёт все заявки по периоду создания заявки. pattern = "yyyy-MM-dd"
	 * 
	 * @param dateStart 2023-10-01
	 * @param dateEnd   2023-10-01
	 * @return
	 */
	@GetMapping("/manager/getOrders/{dateStart}&{dateEnd}")
	public Set<Order> getListOrders(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		Set<Order> orders = new HashSet<Order>();
		for (Order order : orderService.getOrderByPeriodCreate(dateStart, dateEnd).stream()
				.collect(Collectors.toSet())) {
			List<Address> addresses = new ArrayList<Address>();
			order.getAddresses().stream().filter(a -> a.getIsCorrect()).forEach(a -> addresses.add(a));
			addresses.sort(comparatorAddressIdForView);
			order.setAddressesToView(addresses);
			orders.add(order);
		}
		return orders;
	}
	
	/**
	 *  Отдаёт заявки только на внутреннние перемещения по периоду создания заявки. pattern = "yyyy-MM-dd"
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@GetMapping("/manager/getOrdersForStockProcurement/{dateStart}&{dateEnd}")
	public Set<Order> getOrdersForStockProcurement(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		Set<Order> orders = new HashSet<Order>();
		for (Order order : orderService.getOrderByPeriodCreate(dateStart, dateEnd).stream()
				.filter(o-> o.getIsInternalMovement().equals("true"))
				.collect(Collectors.toSet())) {
			List<Address> addresses = new ArrayList<Address>();
			order.getAddresses().stream().filter(a -> a.getIsCorrect()).forEach(a -> addresses.add(a));
			addresses.sort(comparatorAddressIdForView);
			order.setAddressesToView(addresses);
			orders.add(order);
		}
		return orders;
	}
	
	/**
	 * Отдаёт все заявки по периоду создания заявки. pattern = "yyyy-MM-dd"
	 * Только импорт
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@GetMapping("/stock-support/getOrders/{dateStart}&{dateEnd}")
	public Set<Order> getListOrdersForStockSupport(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		Set<Order> orders = new HashSet<Order>();
		for (Order order : orderService.getOrderByPeriodCreate(dateStart, dateEnd).stream()
				.filter(o-> o.getWay().equals("Импорт"))
				.collect(Collectors.toSet())) {
			List<Address> addresses = new ArrayList<Address>();
			order.getAddresses().stream().filter(a -> a.getIsCorrect()).forEach(a -> addresses.add(a));
			addresses.sort(comparatorAddressIdForView);
			order.setAddressesToView(addresses);
			orders.add(order);
		}
		return orders;
	}
	
	/**
	 * Отдаёт все заявки по периоду создания заявки. pattern = "yyyy-MM-dd"
	 * НО ТОЛЬКО ТЕ, ЧТО СОЗДАЛ ОПРЕДЕЛЕННЫЙ МЕНЕДЖЕР
	 * 
	 * @param dateStart 2023-10-01
	 * @param dateEnd   2023-10-01
	 * @return
	 */
	@GetMapping("/manager/getMyOrders/{dateStart}&{dateEnd}")
	public Set<Order> getMyListOrders(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		Set<Order> orders = new HashSet<Order>();
		User thisUser = getThisUser();
		String FIO = thisUser.getSurname() + " " + thisUser.getName() + " " + thisUser.getPatronymic();
		for (Order order : orderService.getOrderByPeriodCreate(dateStart, dateEnd).stream()
				.filter(o-> o.getManager().split(";")[0].equals(FIO))
				.collect(Collectors.toSet())) {
			List<Address> addresses = new ArrayList<Address>();
			order.getAddresses().stream().filter(a -> a.getIsCorrect()).forEach(a -> addresses.add(a));
			addresses.sort(comparatorAddressIdForView);
			order.setAddressesToView(addresses);
			orders.add(order);
		}
		return orders;
	}
	
	/**
	 * Метод сохраняет заявку заявки создание заявки ИМПОРТ И РБ
	 * ожидает статус и idOrder!!
	 * @param str
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/manager/addNewProcurementHasMarket", method = RequestMethod.POST)
	public Map<String, String> addNewProcurement2(@RequestBody String str, HttpServletRequest request)
			throws IOException, ServletException, ParseException {
		//загружаем почтовые ящики из файлов .properties
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
		properties = new Properties();
		properties.load(fileInputStream);
		
		HashMap<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		String points = jsonMainObject.get("points").toString();
		String way = (String) jsonMainObject.get("way");
		String needUnloadPoint =  jsonMainObject.get("needUnloadPoint") == null ? null : (String) jsonMainObject.get("needUnloadPoint");
		Integer count = Integer.parseInt((String) jsonMainObject.get("orderCount"));
		
		Order order = orderService.getOrderById(Integer.parseInt(jsonMainObject.get("idOrder").toString()));
		if(order == null) {
			response.put("status", "100");
			response.put("message", "Отсутствует заявка с id = " + jsonMainObject.get("idOrder").toString());
			return response;
		}
		order.setCounterparty((String) jsonMainObject.get("contertparty"));
		order.setCargo((String) jsonMainObject.get("cargo"));
		order.setContact((String) jsonMainObject.get("contact"));
		order.setTypeLoad((String) jsonMainObject.get("typeLoad"));
		order.setMethodLoad((String) jsonMainObject.get("methodLoad"));
		order.setTypeTruck((String) jsonMainObject.get("typeTruck"));
		order.setTemperature((String) jsonMainObject.get("temperature"));
//		order.setMarketInfo(jsonMainObject.get("marketInfo") != null ? jsonMainObject.get("marketInfo").toString() : null);
		order.setControl((boolean) jsonMainObject.get("control").toString().equals("true") ? true : false);
		order.setComment((String) jsonMainObject.get("comment"));
		order.setDateCreate(Date.valueOf(LocalDate.now()));
		if(jsonMainObject.get("dateDelivery") != null) {
			if(jsonMainObject.get("dateDelivery").toString().contains("-")) { // значит 10-10-2024
				order.setDateDelivery(Date.valueOf((String) jsonMainObject.get("dateDelivery")));
			}else {// значит миллисекунды
				order.setDateDelivery(new Date(Long.parseLong(jsonMainObject.get("dateDelivery").toString())));
			}
		}else {
			order.setDateDelivery(null);	
		}
		User thisUser = getThisUser();
		order.setMarketNumber((String) jsonMainObject.get("marketNumber"));
		order.setManager(thisUser.getSurname() + " " + thisUser.getName() + " " + thisUser.getPatronymic() + "; " + thisUser.geteMail());
		order.setTelephoneManager(thisUser.getTelephone());
		order.setWay(way);
		order.setLoadNumber(jsonMainObject.get("loadNumber") != null ? jsonMainObject.get("loadNumber").toString() : null);
		order.setStacking(jsonMainObject.get("stacking").toString().equals("true") ? true : false);
		order.setIncoterms(jsonMainObject.get("incoterms") == null ? null : jsonMainObject.get("incoterms").toString());
		order.setIsInternalMovement(jsonMainObject.get("isInternalMovement") == null ? null : jsonMainObject.get("isInternalMovement").toString());
		if(jsonMainObject.get("numStockDelivery") != null) {
			order.setNumStockDelivery(jsonMainObject.get("numStockDelivery").toString());
		}
		
		if(jsonMainObject.get("status") != null) {
			order.setStatus(Integer.parseInt(jsonMainObject.get("status").toString()));
		}else {
			response.put("status", "100");
			response.put("message", "Отсутствует статус в заявке");
			return response;
		}
		order.setChangeStatus("Создал: " + thisUser.getSurname() + " " + thisUser.getName() + " " + thisUser.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
		
		//костыль для фруктов
		boolean flag = true;
		System.err.println(order.getManager());
		if(order.getManager().split(";")[1].trim().equals("ShelestovA@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("MarkevichK@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("SickoO@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("PozdnyakovR@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("KuzmickayaE@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("VegeroK@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("YakzhikE@dobronom.by") // нет такого?
				|| order.getManager().split(";")[1].trim().equals("TishalovichA@dobronom.by")){
			flag = false;
		}
		
		if(needUnloadPoint != null) {
			if(needUnloadPoint.equals("true")) {
				order.setNeedUnloadPoint("true");
				order.setStatus(15); // статус башкирова
			}else {
	//			order.setStatus(20);
				if(!flag) {
					order.setStatus(20);
					System.out.println("Фрукты!");
				}else {
					System.out.println("Ожидаем слоты");
				}
			}
		}
		
		LocalDateTime dateTimeNow = LocalDateTime.now();
		LocalDate dateNow = LocalDate.now();
		
		String firstJsonRequest = points.substring(1, points.length() - 1);
		Set<Address> addresses = new HashSet<Address>();
		List<JSONObject> arrayJSON = new ArrayList<>();
		String[] mass = firstJsonRequest.split("},");
		for (String string : mass) {
			if (string.charAt(string.length() - 1) != '}') {
				string = string + "}";
			}
			JSONObject jsonpObject = (JSONObject) parser.parse(string);
			Address address = new Address((String) jsonpObject.get("bodyAdress"),
					jsonpObject.get("date").toString().isEmpty() ? null
							: Date.valueOf((String) jsonpObject.get("date")),
					(String) jsonpObject.get("type"),
					jsonpObject.get("pall").toString().isEmpty() ? null : (String) jsonpObject.get("pall"),
					jsonpObject.get("weight").toString().isEmpty() ? null : (String) jsonpObject.get("weight"),
					jsonpObject.get("volume").toString().isEmpty() ? null : (String) jsonpObject.get("volume"),
					jsonpObject.get("timeFrame").toString().isEmpty() ? null : (String) jsonpObject.get("timeFrame"),
					jsonpObject.get("contact").toString().isEmpty() ? null : (String) jsonpObject.get("contact"),
					jsonpObject.get("cargo").toString().isEmpty() ? null : (String) jsonpObject.get("cargo"));
			address.setCustomsAddress(jsonpObject.get("customsAddress").toString().isEmpty() ? null
					: (String) jsonpObject.get("customsAddress"));
			address.setTnvd(jsonpObject.get("tnvd") != null ? jsonpObject.get("tnvd").toString() : null);
			address.setPointNumber(jsonpObject.get("pointNumber") != null ? Integer.parseInt(jsonpObject.get("pointNumber").toString()) : null);
			
			
			if (!jsonpObject.get("time").toString().isEmpty()) {
				address.setTime(Time.valueOf(
						LocalTime.of(Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[0]),
								Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[1]))));

//				LocalDate targetDate = address.getDate().toLocalDate();

				// реализация проверки по валидности даты ввода загрузки
//				if (dateTimeNow.getHour() >= 12) {
//					if (dateNow.plusDays(1) == targetDate || dateNow.plusDays(1).equals(targetDate)) {
//						orderService.deleteOrderById(order.getIdOrder());
//						response.put("status", "150");
//						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
//						response.put("message",
//								"Недопустимая дата загрузки!\nТак как Вы пытаетесь создать заявку после 12:00, допустимая дата подачи машины :"
//										+ dateNow.plusDays(2).format(formatter));
//						System.err.println(
//								"Недопустимая дата загрузки!\nТак как Вы пытаетесь создать заявку после 12:00, допустимая дата подачи машины :"
//										+ dateNow.plusDays(2).format(formatter));
//						return response;
//					}
//				}
				// ОКОНЧАНИЕ реализации проверки по валидности даты ввода загрузки
			} else {
				address.setTime(null);
			}
			address.setIsCorrect(true);
			address.setOrder(order);
			addressService.saveAddress(address);
			addresses.add(address);
		}
		response.put("status", "200");
		response.put("message", "Заявка создана");
		//отправляем на почту к логистам в отдельных потоках
		String text = "Создана заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+"\nНаправление: " + order.getWay();
		if(order.getWay().equals("РБ") && !flag && order.getStatus() == 20 ) {
			//отправляем не в отдельном потоке!
			String textForSupport = "Создана заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+"\nНаправление: " + order.getWay() + 
					"\nНеобходимо назначить слот на выгрузку";
			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", text, properties.getProperty("email.addNewProcurement.rb.1"), properties.getProperty("email.addNewProcurement.rb.2"));
//			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", textForSupport, properties.getProperty("email.orderSupport.1"), properties.getProperty("email.orderSupport.2"));
		}else if(order.getWay().equals("Импорт") && order.getStatus() == 20 || order.getWay().equals("Экспорт") && order.getStatus() == 20 ){
			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", text, properties.getProperty("email.addNewProcurement.import.1"), properties.getProperty("email.addNewProcurement.import.2"));
		}			
		return response;
		}
	
	/**
	 * Метод сохраняет заявку заявки создание заявки ДЛЯ ЭКСПОРТА и заказов без слотов
	 * @param str
	 * @return
	 * 
	 * @throws IOException
	 * @throws ServletException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/manager/addNewProcurement", method = RequestMethod.POST)
	public Map<String, String> addNewProcurement(@RequestBody String str, HttpServletRequest request)
			throws IOException, ServletException, ParseException {	
		//загружаем почтовые ящики из файлов .properties
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
		properties = new Properties();
		properties.load(fileInputStream);
		
		HashMap<String, String> response = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		String points = jsonMainObject.get("points").toString();
		String way = (String) jsonMainObject.get("way");
		String needUnloadPoint =  jsonMainObject.get("needUnloadPoint") == null ? null : (String) jsonMainObject.get("needUnloadPoint");
		Integer count = Integer.parseInt((String) jsonMainObject.get("orderCount"));
		
		Order order = null;
		
		if(jsonMainObject.get("idOrder") != null) {
			order = orderService.getOrderById(Integer.parseInt(jsonMainObject.get("idOrder").toString()));
		}
		
		if(order == null) {
			order = new Order((String) jsonMainObject.get("contertparty"), (String) jsonMainObject.get("contact"),
					(String) jsonMainObject.get("cargo"), (String) jsonMainObject.get("typeLoad"),
					(String) jsonMainObject.get("methodLoad"), (String) jsonMainObject.get("typeTruck"),
					(String) jsonMainObject.get("temperature"),
					(boolean) jsonMainObject.get("control").toString().equals("true") ? true : false,
					(String) jsonMainObject.get("comment"), 20, Date.valueOf(LocalDate.now()),
					null); // тут вместо null джолжно было стоять dateDelivery
		}else {
			order.setCounterparty((String) jsonMainObject.get("contertparty"));
			order.setCargo((String) jsonMainObject.get("cargo"));
			order.setContact((String) jsonMainObject.get("contact"));
			order.setTypeLoad((String) jsonMainObject.get("typeLoad"));
			order.setMethodLoad((String) jsonMainObject.get("methodLoad"));
			order.setTypeTruck((String) jsonMainObject.get("typeTruck"));
			order.setTemperature((String) jsonMainObject.get("temperature"));
//			order.setMarketInfo(jsonMainObject.get("marketInfo") != null ? jsonMainObject.get("marketInfo").toString() : null);
			order.setControl((boolean) jsonMainObject.get("control").toString().equals("true") ? true : false);
			order.setComment((String) jsonMainObject.get("comment"));
			order.setDateCreate(Date.valueOf(LocalDate.now()));
		}
		
		if(jsonMainObject.get("dateDelivery") != null) {
			if(jsonMainObject.get("dateDelivery").toString().contains("-")) { // значит 10-10-2024
				order.setDateDelivery(Date.valueOf((String) jsonMainObject.get("dateDelivery")));
			}else {// значит миллисекунды
				order.setDateDelivery(new Date(Long.parseLong(jsonMainObject.get("dateDelivery").toString())));
			}
		}else {
			order.setDateDelivery(null);	
		}
		
		User thisUser = getThisUser();
		order.setMarketNumber((String) jsonMainObject.get("marketNumber"));
		order.setManager(thisUser.getSurname() + " " + thisUser.getName() + " " + thisUser.getPatronymic() + "; " + thisUser.geteMail());
		order.setTelephoneManager(thisUser.getTelephone());
		order.setWay(way);
		order.setLoadNumber(jsonMainObject.get("loadNumber") != null ? jsonMainObject.get("loadNumber").toString() : null);
		order.setStacking(jsonMainObject.get("stacking").toString().equals("true") ? true : false);
		order.setIncoterms(jsonMainObject.get("incoterms") == null ? null : jsonMainObject.get("incoterms").toString());
		order.setIsInternalMovement(jsonMainObject.get("isInternalMovement") == null ? null : jsonMainObject.get("isInternalMovement").toString());
//		order.setMarketInfo(jsonMainObject.get("marketInfo") != null ? jsonMainObject.get("marketInfo").toString() : null);
		
		if(order.getIsInternalMovement().equals("true")) {// костыльно ставим время выгрузки для перемещения 1 час
			order.setTimeUnload(Time.valueOf(LocalTime.of(1, 0)));
		}
		
		if(jsonMainObject.get("numStockDelivery") != null) {
			order.setNumStockDelivery(jsonMainObject.get("numStockDelivery").toString());
		}
		if(jsonMainObject.get("status") != null) {
			order.setStatus(Integer.parseInt(jsonMainObject.get("status").toString()));
		}else {
			response.put("status", "100");
			response.put("message", "Отсутствует статус в заявке");
			return response;
		}
		order.setChangeStatus("Создал: " + thisUser.getSurname() + " " + thisUser.getName() + " " + thisUser.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
		
		//костыль для фруктов
		boolean flag = true;
		
		if(order.getManager().split(";")[1].trim().equals("ShelestovA@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("MarkevichK@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("SickoO@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("PozdnyakovR@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("KuzmickayaE@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("VegeroK@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("YakzhikE@dobronom.by") // нет такого?
				|| order.getManager().split(";")[1].trim().equals("TishalovichA@dobronom.by")){
			flag = false;
		}
		
		if(needUnloadPoint != null) {
			if(needUnloadPoint.equals("true")) {
				order.setNeedUnloadPoint("true");
				order.setStatus(15); // статус башкирова
			}else {
	//			order.setStatus(20);
				if(!flag) {
					order.setStatus(20);
					System.out.println("Фрукты!");
				}else {
					System.out.println("Ожидаем слоты");
				}
			}
		}
		
//		if(needUnloadPoint != null) {
//			if(needUnloadPoint.equals("true")) {
//				order.setNeedUnloadPoint("true");
//				order.setStatus(15); // статус башкирова
//			}else {
////				order.setStatus(20);
//				if(order.getWay().equals("РБ") && flag) {
//					System.err.println("17 STATUS!!!!");
//					order.setStatus(17); // статус карины
//				}else {
//					order.setStatus(20);
//				}				
//			}
//		}
		

		LocalDateTime dateTimeNow = LocalDateTime.now();
		LocalDate dateNow = LocalDate.now();
		
		
		String firstJsonRequest = points.substring(1, points.length() - 1);
		Set<Address> addresses = new HashSet<Address>();
		List<JSONObject> arrayJSON = new ArrayList<>();
		String[] mass = firstJsonRequest.split("},");
		
		//вытягиваем паллеты из поинтов
		String pall = null;
		for (String string : mass) {
			if (string.charAt(string.length() - 1) != '}') {
				string = string + "}";
			}
			JSONObject jsonpObject = (JSONObject) parser.parse(string);
			pall = (String) jsonpObject.get("pall");	
			if(pall != null) {
				break;
			}

		}
		order.setPall(pall);
		if(order.getIdOrder() == null) {
			order.setIdOrder(orderService.saveOrder(order));
		}
		
		
		for (String string : mass) {
			if (string.charAt(string.length() - 1) != '}') {
				string = string + "}";
			}
			JSONObject jsonpObject = (JSONObject) parser.parse(string);
			Address address = new Address((String) jsonpObject.get("bodyAdress"),
					jsonpObject.get("date").toString().isEmpty() ? null
							: Date.valueOf((String) jsonpObject.get("date")),
					(String) jsonpObject.get("type"),
					jsonpObject.get("pall").toString().isEmpty() ? null : (String) jsonpObject.get("pall"),
					jsonpObject.get("weight").toString().isEmpty() ? null : (String) jsonpObject.get("weight"),
					jsonpObject.get("volume").toString().isEmpty() ? null : (String) jsonpObject.get("volume"),
					jsonpObject.get("timeFrame").toString().isEmpty() ? null : (String) jsonpObject.get("timeFrame"),
					jsonpObject.get("contact").toString().isEmpty() ? null : (String) jsonpObject.get("contact"),
					jsonpObject.get("cargo").toString().isEmpty() ? null : (String) jsonpObject.get("cargo"));
			address.setCustomsAddress(jsonpObject.get("customsAddress").toString().isEmpty() ? null
					: (String) jsonpObject.get("customsAddress"));
			address.setTnvd(jsonpObject.get("tnvd") != null ? jsonpObject.get("tnvd").toString() : null);
			address.setPointNumber(jsonpObject.get("pointNumber") != null ? Integer.parseInt(jsonpObject.get("pointNumber").toString()) : null);
			if (!jsonpObject.get("time").toString().isEmpty()) {
				address.setTime(Time.valueOf(
						LocalTime.of(Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[0]),
								Integer.parseInt((String) jsonpObject.get("time").toString().split(":")[1]))));

				LocalDate targetDate = address.getDate().toLocalDate();

				// реализация проверки по валидности даты ввода загрузки
				if (dateTimeNow.getHour() >= 12) {
					if (dateNow.plusDays(1) == targetDate || dateNow.plusDays(1).equals(targetDate)) {
						orderService.deleteOrderById(order.getIdOrder());
						response.put("status", "150");
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
						response.put("message",
								"Недопустимая дата загрузки!\nТак как Вы пытаетесь создать заявку после 12:00, допустимая дата подачи машины :"
										+ dateNow.plusDays(2).format(formatter));
						System.err.println(
								"Недопустимая дата загрузки!\nТак как Вы пытаетесь создать заявку после 12:00, допустимая дата подачи машины :"
										+ dateNow.plusDays(2).format(formatter));
						return response;
					}
				}
				// ОКОНЧАНИЕ реализации проверки по валидности даты ввода загрузки
			} else {
				address.setTime(null);
			}
			address.setIsCorrect(true);
			address.setOrder(order);
			addressService.saveAddress(address);
			addresses.add(address);
		}
		response.put("status", "200");
		response.put("message", "Заявка создана");
		//отправляем на почту к логистам в отдельных потоках
		String text = "Создана заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+"\nНаправление: " + order.getWay();
		if(order.getWay().equals("РБ") && !flag && order.getStatus() == 20) {
			//отправляем не в отдельном потоке!
			String textForSupport = "Создана заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+"\nНаправление: " + order.getWay() + 
					"\nНеобходимо назначить слот на выгрузку";
			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", text, properties.getProperty("email.addNewProcurement.rb.1"), properties.getProperty("email.addNewProcurement.rb.2"));
//			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", textForSupport, properties.getProperty("email.orderSupport.1"), properties.getProperty("email.orderSupport.2"));
		}else if(order.getWay().equals("Импорт") || order.getWay().equals("Экспорт") && order.getStatus() == 20 ){
			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", text, properties.getProperty("email.addNewProcurement.import.1"), properties.getProperty("email.addNewProcurement.import.2"));
		}			
		return response;
	}

	@GetMapping("/getThisUser")
	public User getThisUserGet() {
		return getThisUser();
	}

	@RequestMapping(value = "/carrier/saveNewDriver", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public User handleSaveNewDriver(@RequestParam(value = "drivercard_file", required = false) MultipartFile mulFile2,
			HttpServletRequest request, @RequestParam(value = "numpass", required = false) String numpass,
			@RequestParam(value = "numdrivercard", required = false) String numdrivercard,
			@RequestParam(value = "surname", required = false) String surname,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "patronymic", required = false) String patronymic,
			@RequestParam(value = "tel", required = false) String telephone) throws IOException, ServletException {
		User driver = new User();
		User user = getThisUser();
		driver.setEnablet(true);
		driver.setIsDriver(true);
		driver.setBlock(false);
		driver.setStatus("0");
		Role role = roleService.getRole(8);
		Set<Role> rolest = new HashSet<Role>();
		rolest.add(role);
		driver.setRoles(rolest);
		driver.setCompanyName(user.getCompanyName());
		driver.setNumPass(stripXSS(numpass));
		driver.setNumDriverCard(stripXSS(numdrivercard));
		driver.setSurname(stripXSS(surname));
		driver.setName(stripXSS(name));
		driver.setPatronymic(stripXSS(patronymic));
		driver.setTelephone(telephone);
		try {
			return userService.saveNewDriver(driver);
		} catch (Exception e) {
			return null; // временный костыль
		}

	}

	/**
	 * создаёт новую машину
	 * 
	 * @param mulFile2
	 * @param request
	 * @param technicalCertificate
	 * @param infoData
	 * @param dimensions
	 * @param modelTruck
	 * @param numTruck
	 * @param ownerTruck
	 * @param number_axes
	 * @param typeTrailer
	 * @param hitch_type
	 * @param type_of_load
	 * @param cargoCapacity
	 * @param volume_trailer
	 * @param pallCapacity
	 * @param brandTruck
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@RequestMapping(value = "/carrier/saveNewTruck", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public Truck handleSaveNewTruck(
			@RequestParam(value = "technical_certificate_file", required = false) MultipartFile mulFile2,
			HttpServletRequest request,
			@RequestParam(value = "technical_certificate", required = false) String technicalCertificate,
			@RequestParam(value = "info", required = false) String infoData,
			@RequestParam(value = "dimensions", required = false) String dimensions,
			@RequestParam(value = "modelTruck", required = false) String modelTruck,
			@RequestParam(value = "numTruck", required = false) String numTruck,
			@RequestParam(value = "ownerTruck", required = false) String ownerTruck,
			@RequestParam(value = "number_axes", required = false) String number_axes,
			@RequestParam(value = "typeTrailer", required = false) String typeTrailer,
			@RequestParam(value = "hitch_type", required = false) String hitch_type,
			@RequestParam(value = "type_of_load", required = false) String type_of_load,
			@RequestParam(value = "cargoCapacity", required = false) String cargoCapacity,
			@RequestParam(value = "volume_trailer", required = false) String volume_trailer,
			@RequestParam(value = "pallCapacity", required = false) String pallCapacity,
			@RequestParam(value = "brandTruck", required = false) String brandTruck,
			@RequestParam(value = "numTrailer", required = false) String numTrailer,
			@RequestParam(value = "brandTrailer", required = false) String brandTrailer)
			throws IOException, ServletException {
//		Truck truck = truckService.getTruckByNum(numTruck);
		User user = getThisUser();
//		if (truck != null) {
//			return null;
//		}
		Truck truck = new Truck();
		truck.setNumTruck(stripXSS(numTruck));
		truck.setTechnicalCertificate(stripXSS(technicalCertificate));
		truck.setInfo(infoData);
		truck.setDimensionsBody(dimensions);
		truck.setModelTruck(stripXSS(modelTruck));
		truck.setOwnerTruck(stripXSS(ownerTruck));
		truck.setNumber_axes(stripXSS(number_axes));
		truck.setTypeTrailer(stripXSS(typeTrailer));
		truck.setHitchType(stripXSS(hitch_type));
		truck.setTypeLoad(stripXSS(type_of_load));
		truck.setCargoCapacity(stripXSS(cargoCapacity));
		truck.setVolumeTrailer(Integer.parseInt(volume_trailer));
		truck.setPallCapacity(stripXSS(pallCapacity));
		truck.setBrandTruck(stripXSS(brandTruck));
		truck.setUser(user);
		truck.setVerify(false);
		truck.setNumTrailer(numTrailer);
		truck.setBrandTrailer(brandTrailer);
		if (mulFile2 != null) {
//			mailService.sendEmailWhithFile(request, "Техапаспорт от " + user.getCompanyName(), "техпаспорт авто",
//					mulFile2);
			final String num = numTruck;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mailService.sendEmailWhithFileToAnyUsers(request,
								user.getCompanyName() + " - Техпаспорт авто " + num,
								user.getCompanyName() + " - Техпаспорт авто " + num,
								convertMultiPartToFile(mulFile2, request), "ArtyuhevichO@dobronom.by",
								"StrizhakA@dobronom.by");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();

		}
		return truckService.saveNewTruck(truck);
	}

	@PostMapping("manager/changeNumDocument")
	public Map<String, String> getchangeNumDocument(@RequestBody String str) throws ParseException {
		// {idUser}&{text}
		HashMap<String, String> map = new HashMap<String, String>();
		str = stripXSS(str);
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		Integer idUser = Integer.parseInt(jsonpObject.get("idUser").toString());
		String numcontract = jsonpObject.get("numcontract") == null ? null : jsonpObject.get("numcontract").toString();
		User user = userService.getUserById(idUser);
		if (numcontract == null) {
			if (user.getCheck().contains("&")) {
				map.put("status", "200");
				map.put("message", "Аккаунт переведен в неподтвержденные");
				return map;
			} else {
				user.setCheck(user.getCheck() + "&new");
				user.setNumContract(null);
				userService.saveOrUpdateUser(user, 0);
				map.put("status", "200");
				map.put("message", "Аккаунт переведен в неподтвержденные");
				return map;
			}
		}

		if (userService.updateUserInBaseDocuments(idUser, numcontract) > 0) {
			String check = user.getCheck();
			user.setCheck(check.split("&")[0]);
			user.setNumContract(numcontract);
			userService.saveOrUpdateUser(user, 0);
			map.put("status", "200");
			map.put("message", "Номер договора изменен");
			return map;
		} else {
			map.put("status", "200");
			map.put("message", "Номер договора изменен");
			return map;
		}
	}

	@PostMapping("/user/registration")
	public Map<String, String> postRegistration(@RequestBody String str, HttpSession session,
			HttpServletRequest request) throws ParseException {
		HashMap<String, String> map = new HashMap<String, String>();
		str = stripXSS(str);
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		User user = new User();
		user.setCheck(jsonpObject.get("check").toString());
		user.setLogin(jsonpObject.get("login").toString());
		user.setPassword(jsonpObject.get("password").toString());
		user.setName(jsonpObject.get("name") == null ? null : jsonpObject.get("name").toString());
		user.setSurname(jsonpObject.get("surname") == null ? null : jsonpObject.get("surname").toString());
		user.setPatronymic(jsonpObject.get("patronymic") == null ? null : jsonpObject.get("patronymic").toString());
		user.setTelephone(jsonpObject.get("tel").toString());
		user.seteMail(jsonpObject.get("mail").toString());
		user.setCompanyName(jsonpObject.get("companyName").toString());
		user.setPropertySize(jsonpObject.get("propertySize").toString());
		user.setCountryOfRegistration(jsonpObject.get("countryOfRegistration").toString());
		user.setDirector(jsonpObject.get("director").toString());
		user.setNumYNP(jsonpObject.get("numYNP").toString());
		user.setRegistrationCertificate(jsonpObject.get("registrationCertificate").toString());
		user.setCharacteristicsOfTruks(jsonpObject.get("characteristicsOfTruks") == null ? null
				: jsonpObject.get("characteristicsOfTruks").toString());
		user.setAffiliatedCompanies(jsonpObject.get("affiliatedCompanies") == null ? null
				: jsonpObject.get("affiliatedCompanies").toString());
		user.setTIR(jsonpObject.get("TIR") == null ? false : Boolean.parseBoolean(jsonpObject.get("TIR").toString()));
		user.setDirectionOfTransportation(jsonpObject.get("directionOfTransportation") == null ? null
				: jsonpObject.get("directionOfTransportation").toString());
		user.setNumberOfTruks(jsonpObject.get("numberOfTruks") == null ? null
				: Integer.parseInt(jsonpObject.get("numberOfTruks").toString()));
		user.setBlock(false);
		user.setIsDriver(false);
		user.setDepartment("Директор");
		Date date = Date.valueOf(LocalDate.now());
		user.setDateRegistration(date);
		user.setRequisites(jsonpObject.get("requisites").toString());

		if (!userService.getUserByYNP(user.getNumYNP()).isEmpty()) {
			if (user.getCheck().equals("regional&new")) {
				map.put("status", "403");
				map.put("message", "В регистрации отказано! Пользователь с таким УНП существует");
				map.put("check", "regional");
				return map;
			} else {
				map.put("status", "403");
				map.put("message", "В регистрации отказано! Пользователь с таким УНП существует");
				map.put("check", "international");
				return map;
			}

		}
		if (user.getCheck() != null && user.getCheck().split("&")[0].equals("international")) {
			userService.saveOrUpdateUser(user, 7); // международник
			session.setAttribute("check", "international&new");
			mailService.sendSimpleEmail(request, "Регистрация в SpeedLogist",
					"Спасибо что зарегистрировались на товарно-транспортной бирже компании ЗАО \"Доброном\"\n"
							+ "Для того чтобы принять участие в торгах на бирже, необходимо прислать подписанный договор по адресу: 220112 г.Минск ул. Я. Лучины, 5\n"
							+ "Логин для входа: " + user.getLogin(),
					user.geteMail());
//			System.out.println("это международник!");
//			System.out.println(user);
			map.put("status", "200");
			map.put("message", "новый международник создан");
			map.put("url", "/main/signin");
			return map;
		}
		if (user.getCheck() != null && user.getCheck().split("&")[0].equals("regional")) {
			userService.saveOrUpdateUser(user, 7);
			session.setAttribute("check", "regional&new");
			mailService.sendSimpleEmail(request, "Регистрация в SpeedLogist",
					"Спасибо что зарегистрировались на товарно-транспортной бирже компании ЗАО \"Доброном\"\n"
							+ "Для того чтобы принять участие в торгах на бирже, необходимо прислать подписанный договор по адресу: 220112 г.Минск ул. Я. Лучины, 5\n"
							+ "Логин для входа: " + user.getLogin(),
					user.geteMail());
//			System.out.println("это региональник!");
//			System.out.println(user);
			map.put("status", "200");
			map.put("message", "новый международник создан ТЕСТ");
			map.put("url", "/main/signin");
			return map;
		}
		map.put("status", "100");
		map.put("message", "не удалось сохранить");
		return map;
	}

	@GetMapping("/route")
	public Set<Route> getListRoute() {
		Set<Route> res = new HashSet<Route>(); // расчёт стоимости!
		for (Route route : routeService.getRouteList()) { // расчёт стоимости!
			boolean flag = false;
			Set<RouteHasShop> routeHasShops = route.getRoteHasShop();
			for (RouteHasShop routeHasShop : routeHasShops) {
				if (routeHasShop.getShop() == null) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				res.add(addCostForRoute(route)); // расчёт стоимости!
			} else {
				res.add(route);
			}
		}
		return res;
	}

	@GetMapping("/simpleroute")
	public List<Route> getListSimpleRoute() {
//		return routeService.getRouteList();
		return null; // test
	}

	@GetMapping("/route/{id}")
	public Route getRoute(@PathVariable int id) {
		Route route;
		User usertarget = getThisUser();
		if (usertarget == null) {
			return null;
		}
		try {
			route = routeService.getRouteById(id);
		} catch (Exception e) {
			return null;
		}

		if (usertarget.getRoles().stream().findFirst().get().getIdRole() < 5) {
			if (route.getComments() != null && route.getComments().equals("international")) {
				return route;
			} else if (route.getComments() != null && route.getComments().equals("pattern")) {
				return route;
			} else {
				return addCostForRoute(route);
			}
		} else {
			if (route.getComments() != null && route.getComments().equals("international")
					&& Integer.parseInt(route.getStatusRoute()) <= 1) {
				return route;
			} else if (route.getComments() != null && route.getComments().equals("international")
					&& route.getUser().equals(usertarget)) {
				return route;
			}
			System.out.println("Неправомерный запрос от " + usertarget.getCompanyName());
//			SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
//			usertarget.setEnablet(false);
			usertarget.setLoyalty("Неправомерный запрос к /route/" + id);
			userService.saveOrUpdateUser(usertarget, 0);
			return null;
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

	@PostMapping("/route/temperature") // задаёт температуру тендера
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

	@PostMapping("/route/timeLoadPreviously") // задаёт время начала загрузки тендера
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

	@PostMapping("/route/userComments") // задаёт комментарий
	public JSONObject postUserComments(@RequestBody String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		int id = Integer.parseInt(jsonpObject.get("idRoute").toString().trim());
		Route route = routeService.getRouteById(id);
		route.setUserComments(jsonpObject.get("userComments").toString().trim());
		routeService.saveOrUpdateRoute(route);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ!");
		return new JSONObject(map);
	}

	@PostMapping("/route/typeTrailer") // задаёт тип прицепа
	public JSONObject postTypeTrailer(@RequestBody String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		int id = Integer.parseInt(jsonpObject.get("idRoute").toString().trim());
		Route route = routeService.getRouteById(id);
		route.setTypeTrailer(jsonpObject.get("typeTrailer").toString().trim());
		routeService.saveOrUpdateRoute(route);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ!");
		return new JSONObject(map);
	}

	@PostMapping("/route/time") // задаёт время тендера
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
	public List<Route> getListRouteHasDate(@PathVariable String dateStart, @PathVariable String dateFinish) {
		Date targetDateStart = Date.valueOf(dateStart);
		Date targetDateFinish = Date.valueOf(dateFinish);
		return routeService.getRouteListAsDate(targetDateStart, targetDateFinish);
	}

	@GetMapping("/route/disposition")
	public List<Route> getListRouteInternationalDispo() {
		List<Route> routes = new ArrayList<Route>();
		routeService.getRouteListAsStatus("4", "4").stream().filter(r -> r.getComments().equals("international"))
				.forEach(r -> routes.add(r));
//		System.out.println(routes.size());
		return routes;
	}

	@GetMapping("/user")
	public List<User> homePage() {
//		return userService.getUserList();
		System.err.println("Заблокированный метод! /user");
		return null;
	}

	/**
	 * Отдаёт машины юзера
	 * 
	 * @return
	 */
	@GetMapping("/carrier/getMyCar")
	public Set<Truck> getMyCar() {
		return truckService.getTruckListByUser().stream().collect(Collectors.toSet());
	}

	/**
	 * Отдаёт машины перевозчика по id этого перевозчика
	 * 
	 * @param idCarrier
	 * @return
	 */
	@GetMapping("/carrier/getCarByIdUser/{idCarrier}")
	public Set<Truck> getCarByIdUser(@PathVariable Integer idCarrier) {
		return userService.getUserById(idCarrier).getTrucks();
	}

	/**
	 * проверяет, есть ли такая машина в базе данных если есть, возвращает true
	 * 
	 * @param numCar
	 * @return
	 */
	@GetMapping("/carrier/isContainCar/{numCar}")
	public boolean getIsContainCar(@PathVariable String numCar) {
		Truck truck = truckService.getTruckByNum(numCar);
		if (truck != null) {
//			return true;
			return false; // отключил проверку на уникальность авто. Сама проверка в js осталась
		} else {
			return false;
		}
	}

	/**
	 * Отдаёт всех перевозчиков
	 * 
	 * @return
	 */
	@GetMapping("/manager/getAllCarrier")
	public Set<User> getAllCarrier() {
		Set<User> carriers = userService.getCarrierListV2().stream().collect(Collectors.toSet());
//		carriers.forEach(c -> System.out.println(c.getTrucks().size()));
		return userService.getCarrierListV2().stream().collect(Collectors.toSet());
	}

	/**
	 * Блокирует и разблокирует перевозчиков GET запрос
	 * 
	 * @param idUser
	 * @return
	 */
	@GetMapping("/manager/blockCarrier/{idUser}")
	public Map<String, String> getblockCarrier(@PathVariable int idUser) {
		Map<String, String> response = new HashMap<String, String>();
		if (getThisUserRole().getIdRole() <= 3) {
			User user = userService.getUserById(idUser);
			user.setBlock(!user.isBlock());
			userService.saveOrUpdateUser(user, 0);
			response.put("status", "200");
			response.put("message", "block user change on " + user.isBlock() + "");
		} else {
			response.put("status", "403");
			response.put("message", "Ошибка доступа");
		}
		return response;
	}

	/**
	 * post метод, принимает json на добавление или изминение номера контакта.
	 * 
	 * @param {"idUser": "111", "numContract": "155894", "date": "2023-09-18"}
	 * @return
	 * @throws ParseException
	 */
	@PostMapping("/manager/addNumContract")
	public Map<String, String> postAddNumContract(@RequestBody String str) throws ParseException {
		Map<String, String> response = new HashMap<String, String>();
		if (getThisUserRole().getIdRole() <= 3) {
			JSONParser parser = new JSONParser();
			JSONObject jsonpObject = (JSONObject) parser.parse(str);
			Integer idUser = Integer.parseInt(jsonpObject.get("idUser").toString());
			User carrier = userService.getUserById(idUser);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			String date = Date.valueOf(jsonpObject.get("date").toString()).toLocalDate().format(formatter);
			carrier.setNumContract(jsonpObject.get("numContract").toString() + " от " + date);
			userService.saveOrUpdateUser(carrier, 0);
			response.put("status", "200");
			response.put("message", carrier.getNumContract());
		} else {
			response.put("status", "403");
			response.put("message", "Ошибка доступа");
		}
		return response;
	}

	/**
	 * отдаёт активные тендеры если перевоз заблокирован или не подтвержедн, то
	 * возвращает NULL
	 * 
	 * @return
	 */
	@GetMapping("/carrier/getActiveInternationalTenders")
	public Set<Route> getActiveTenders() {
		User target = getThisUser();
		LocalDate dateNow = LocalDate.now();
		if (getThisUserRole() == null) {
			return null;
		}
		if (target.isBlock() || target.getCheck().split("&").length > 1) {
			return null;
		} else {
			Set<Route> routes = new HashSet<Route>();
			routeService.getRouteListAsStatus("1", "1").stream()
					.filter(r -> r.getComments() != null && r.getComments().equals("international"))
					.filter(r-> !r.getDateLoadPreviously().isBefore(dateNow)) // не показывает тендеры со вчерашней датой загрузки
					.forEach(r -> routes.add(r));
//			System.out.println(routes.size());
			return routes;
		}
	}

	/**
	 * УСТАРЕВШИЙ МЕТОД Отдаёт юзера по id
	 * закрыл метод
	 * @param id
	 * @return
	 */
	@GetMapping("/user/{id}")
	public User getUser(@PathVariable int id) {
//		return userService.getUserById(id);
		System.err.println("Заблокированный метод! /user/{id}");
		return null;
	}

	/**
	 * УСТАРЕВШИЙ МЕТОД Отдаёт юзера по login
	 * 
	 * @param login
	 * @return
	 */
	@GetMapping("/userByLogin/{login}")
	public User getUserByLogin(@PathVariable String login) {
		System.err.println("Заблокированный метод! /userByLogin/{login}");
//		return userService.getUserByLogin(login);
		return null;
	}

	@PostMapping("/user/isexists") // проверяет есть ли юзер с логином
	public JSONObject postRegistrationHasUser(@RequestBody String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		try {
			if (userService.getUserByLogin(jsonpObject.get("Login").toString()) == null) {
				return null;
			} else {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("message", "Юзер с таким именем существует");
				map.put("status", "200");
				return new JSONObject(map);
			}
		} catch (Exception e) {
			return null;
		}

	}

	@PostMapping("/user/isexistsUNP") // проверяет есть ли юзер с унп
	public JSONObject postRegistrationHasUNP(@RequestBody String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonpObject = (JSONObject) parser.parse(str);
		String ynp = jsonpObject.get("Login").toString().split("&")[0];
		try {
			if (userService.getUserByYNP(ynp).isEmpty() && userService.getUserByYNP(ynp + "&block").isEmpty()) {
				return null;
			} else {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("message", "Юзер с таким УНП зарегистрирован. Обратитесь к администратору");
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
				.filter(m -> m.getToUser() != null && m.getToUser().equals("disposition"))
				.forEach(m -> messages.add(m));
		return messages;
	}

	// переписать с использованием gson!
	@PostMapping("/route/addpoints")
	public JSONObject postRoadPoints(@RequestBody String str, HttpSession session) throws ParseException {
		StringBuilder sb = new StringBuilder(str);
		sb.deleteCharAt(0);
		sb.deleteCharAt(str.length() - 2);
		StringBuilder nsb = new StringBuilder(sb.toString());
		char[] task = sb.toString().toCharArray();
		for (int i = 0; i < task.length; i++) {
			if (task[i] == '}' && i != task.length - 1) {
				nsb.setCharAt(i + 1, '&');
			}
		}
		String[] points = nsb.toString().split("&");
		Route route = new Route(); // 1111
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
			routeHasShop.setCargo((String) jsonpObject.get("cargo"));
			routeHasShop.setPosition((String) jsonpObject.get("position"));
			routeHasShop.setVolume((String) jsonpObject.get("volume"));
			routeHasShops.add(routeHasShop);
		}
		route.setRoteHasShop(routeHasShops);
		HashMap<String, String> map = new HashMap<String, String>();
		User user = getThisUser();
		session.setAttribute(user.getIdUser()+"route", route);
		map.put("message", "УСПЕХ");
		return new JSONObject(map);
	}

	@GetMapping("/info/message/numroute/{idRoute}") // отдаёт колличество предложений по id маршруту
	public String getSizeMessageByRoute(@PathVariable String idRoute) {
		List<Message> messagesList = new ArrayList<Message>();
		chatEnpoint.internationalMessegeList.stream().filter(mes -> mes.getIdRoute().equals(idRoute + ""))
				.forEach(mes -> messagesList.add(mes));
		return messagesList.size() + "";
	}

	@GetMapping("/info/message/routes") // отдаёт все сообщения, которые имеются в кеше, по маршрутам
	public List<Message> getListMessegRoute() {
		List<Message> messagesList = new ArrayList<Message>();
		chatEnpoint.internationalMessegeList.stream().filter(mes -> mes.getIdRoute() != null)
				.forEach(mes -> messagesList.add(mes));
		return messagesList;
	}

	@GetMapping("/info/message/routes/{idRoute}") // отдаёт сообщения где есть id маршрута из кеша!!!
	public List<Message> getListMessegRouteById(@PathVariable String idRoute) {
		List<Message> messagesList = new ArrayList<Message>();
		List<Message> result = new ArrayList<Message>();
		ChatEnpoint.internationalMessegeList.stream().filter(mes -> mes.getIdRoute().equals(idRoute + ""))
				.forEach(mes -> messagesList.add(mes));

		Role role = getThisUserRole();
		User user = getThisUser();
		if (role != null && role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_TOPMANAGER") || role.getAuthority().equals("ROLE_MANAGER")) {
			return messagesList;
		} else {
			result = messagesList.stream().filter(m -> m.getFromUser().equals(user.getLogin()))
					.collect(Collectors.toList());
			return result;
		}

	}

	@GetMapping("/info/message/routes/from_me") // отдаёт сообщения, на которые есть предложения от данного юзера
												// (сравнине по УНП) из кеша
	public List<Message> getIdRouteByTargetCarrier() {
		List<Message> result = new ArrayList<Message>();
		User user = userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
		String ynp = user.getNumYNP();
		ChatEnpoint.internationalMessegeList.stream().filter(mes -> mes.getYnp() != null && mes.getYnp().equals(ynp))
				.forEach(mes -> result.add(mes));
		return result;
	}

	@GetMapping("/memory/message/routes/{idRoute}") // отдаёт сообщения где есть id маршрута из БД!!!
	public List<Message> getListMessegRouteByIdFromBase(@PathVariable String idRoute) {
		List<Message> messagesList = new ArrayList<Message>();
		messagesList = messageService.getListMessageByIdRoute(idRoute);
		return messagesList;
	}

	@GetMapping("/info/message/participants/{idRoute}") // отдаёт колличество участников торгов по id маршрута
	public String getParticipantsByRoute(@PathVariable String idRoute) {
		Set<String> messagesList = new HashSet<String>();
		chatEnpoint.internationalMessegeList.stream().filter(mes -> mes.getIdRoute().equals(idRoute + ""))
				.forEach(mes -> messagesList.add(mes.getCompanyName()));
		return messagesList.size() + "";
	}

	@GetMapping("/mainchat/messages") // отдаёт число сообщений
	public String getNumMessage() {
		return mainChat.messegeList.size() + "";
	}

	@GetMapping("/mainchat/messagesList") // отдаёт лист сообщений из mainChat
	public Set<Message> getNumMessageList() {
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
				.filter(mes -> mes.getDatetime().split(";")[0].equals(now)
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(1).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(2).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(3).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(4).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(5).format(dateFormatter)))
				.forEach(mes -> result.add(mes));
		for (Message message : result) {
			message.setIdMessage(null);
			message.setStatus("1");
			message.setComment(null);
			messages.remove(message);
		}
		List<Message> finalResult = new ArrayList<Message>();
		finalResult = messages.stream()
				.filter(m -> m.getToUser().equals(login) || m.getToUser().equals("international"))
				.collect(Collectors.toList());
		// тут возвращался тупо messages, причём всех юзеров
		Role role = getThisUserRole();
		if (role == null) {
			return finalResult;
		}
		if (role != null && role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_TOPMANAGER")) {
			return messages;
		} else {
			return finalResult;
		}

	}

	@GetMapping("/mainchat/messagesListYNP&{login}") // отдаёт лист непрочитанных сообщений из mainChat по УНП
	public List<Message> getNumMessageListByLoginAndYNP(@PathVariable String login, HttpSession session) {
		List<Message> messages = new ArrayList<Message>();
		mainChat.messegeList.stream().filter(m -> m.getYnp() != null && m.getYnp().equals(session.getAttribute("YNP")))
				.forEach(m -> messages.add(m));
		List<Message> result = new ArrayList<Message>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
		String now = LocalDate.now().format(dateFormatter);
		messageService.getListMessageByComment(login).stream()
				.filter(mes -> mes.getDatetime().split(";")[0].equals(now)
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(1).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(2).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(3).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(4).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(5).format(dateFormatter)))
				.forEach(mes -> result.add(mes));
		for (Message message : result) {
			message.setIdMessage(null);
			message.setStatus("1");
			message.setComment(null);
			messages.remove(message);
		}
		return messages;
	}

	@GetMapping("/mainchat/messagesList/{fromUser}&{toUser}") // отдаёт list с сообщениями отопредиленного юзера к
																// опредиленному юзеру
	public List<Message> getMessagesListFromTo(@PathVariable String fromUser, @PathVariable String toUser) {
		List<Message> messagesList = new ArrayList<Message>();
		mainChat.messegeList.stream()
				.filter(mes -> mes.getFromUser().equals(fromUser) && mes.getToUser().equals(toUser))
				.forEach(mes -> messagesList.add(mes));
		return messagesList;
	}

	@PostMapping("/mainchat/massage/add") // сохраняет сообщение в бд, если есть сообщение, то не сохзраняет
	public JSONObject postSaveDBMessage(@RequestBody String str) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");
		Message message = gson.fromJson(str, Message.class);
		message.setStatus(LocalDateTime.now().format(formatter));
		messageService.singleSaveMessage(message);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", "УСПЕХ");
		map.put("status", "200");
		return new JSONObject(map);
	}

	@GetMapping("/chatenpoint/getoffers") // отдаёт list с предложениями
	public List<Message> getOffers() {
		return ChatEnpoint.internationalMessegeList;
	}

	@GetMapping("/logistics/international/routeShow&{idRoute}")
	public List<SimpleRoute> getListRouteAsCost(@PathVariable Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		List<Route> routes = routeService.getRouteListAsRouteDirection(route);
		List<Route> step1 = new ArrayList<>();
		routes.stream().filter(
				r -> r.getFinishPrice() != null && r.getStartCurrency() != null && r.getStartCurrency().equals("BYN"))
				.forEach(r -> step1.add(r)); // пока что ижем только в белках
		List<SimpleRoute> result = new ArrayList<>();
		step1.forEach(r -> result.add(r.getSimpleRoute()));
		return result;
	}

	@GetMapping("/mainchat/massages/getfromdb&{login}") // отдаёт сообщения к системе за последние 5 дней
	public List<Message> getDBMessage(@PathVariable String login) throws ParseException {
		List<Message> result = new ArrayList<Message>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
		String now = LocalDate.now().format(dateFormatter);
		messageService.getListMessageByComment(login).stream()
				.filter(mes -> mes.getDatetime().split(";")[0].equals(now)
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(1).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(2).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(3).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(4).format(dateFormatter))
						|| mes.getDatetime().split(";")[0].equals(LocalDate.now().minusDays(5).format(dateFormatter)))
				.forEach(mes -> result.add(mes));
		return result;
	}

	/**
	 * Принимает на вход дату старта и дату финиша. Отдаёт закрытые и завершенные
	 * маршруты перевозчику Идея в том, чтобы каждый раз при смене даты он показывал
	 * маршруты
	 * 
	 * @param dateStart
	 * @param dateFinish
	 * @return
	 * @throws ParseException
	 */
	@GetMapping("/carrier/getroutes&{dateStart}&{dateFinish}")
	public List<Route> getRoutesAsDate(@PathVariable String dateStart, @PathVariable String dateFinish)
			throws ParseException {
		Date dateStartSQL = Date.valueOf(LocalDate.parse(dateStart));
		Date dateFinishSQL = Date.valueOf(LocalDate.parse(dateFinish));
		List<Route> result = routeService.getRouteListAsDateAndUser(dateStartSQL, dateFinishSQL).stream()
				.filter(r -> r.getStatusRoute().equals("6") || r.getStatusRoute().equals("7"))
				.collect(Collectors.toList());
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

	private User getThisUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByLogin(name);
		return user;
	}

	private File convertMultiPartToFile(MultipartFile file, HttpServletRequest request) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		File convFile = new File(appPath + "resources/others/" + file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private Role getThisUserRole() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!name.equals("anonymousUser")) {
			Role role = userService.getUserByLogin(name).getRoles().stream().findFirst().get();
			return role;
		} else {
			return null;
		}
	}

	private String cleanXSS(String value) {
		value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
		value = value.replaceAll("'", "& #39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		return value;
	}

	private String getOptimalCost(Route route) {
		String routeDirection = route.getRouteDirection();
		List<Route> routes = routeService.getRouteListAsRouteDirection(route);
		List<Route> step1 = new ArrayList<>();
		routes.stream().filter(
				r -> r.getFinishPrice() != null && r.getStartCurrency() != null && r.getStartCurrency().equals("BYN"))
				.forEach(r -> step1.add(r)); // пока что ижем только в белках
		Integer optimalCost = 0;
		if (step1.size() != 0 && step1.size() < 5) {
			Integer summ = 0;
			for (Route route2 : step1) {
				summ = summ + route2.getFinishPrice();
			}
			optimalCost = (summ / step1.size());
			return optimalCost.toString();
		} else if (step1.size() == 0) {
			System.out.println("Маршрут " + routeDirection + " в базе данных не найден");// обработка ручного ввода
			return "1000";
		} else {
			Integer summ = 0;
			summ = summ + step1.get(step1.size() - 1).getFinishPrice();
			summ = summ + step1.get(step1.size() - 2).getFinishPrice();
			summ = summ + step1.get(step1.size() - 3).getFinishPrice();
			summ = summ + step1.get(step1.size() - 4).getFinishPrice();
			summ = summ + step1.get(step1.size() - 5).getFinishPrice();
			optimalCost = summ / 5;
			return optimalCost.toString();
		}

	}

	private String stripXSS(String value) {
		if (value != null) {
			// NOTE: It's highly recommended to use the ESAPI library and uncomment the
			// following line to
			// avoid encoded attacks.
			// value = ESAPI.encoder().canonicalize(value);

			// Avoid null characters
			value = value.replaceAll("", "");

			// Avoid anything between script tags
			Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid anything in a src='...' type of expression
			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Remove any lonesome </script> tag
			scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Remove any lonesome <script ...> tag
			scriptPattern = Pattern.compile("<script(.*?)>",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid eval(...) expressions
			scriptPattern = Pattern.compile("eval\\((.*?)\\)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid expression(...) expressions
			scriptPattern = Pattern.compile("expression\\((.*?)\\)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid javascript:... expressions
			scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid vbscript:... expressions
			scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid onload= expressions
			scriptPattern = Pattern.compile("onload(.*?)=",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
		}
		return value;
	}

	public static String removeCharAt(String s, int pos) {
		return s.substring(0, pos) + s.substring(pos + 1); // Возвращаем подстроку s, которая начиная с нулевой позиции
															// переданной строки (0) и заканчивается позицией символа
															// (pos), который мы хотим удалить, соединенную с другой
															// подстрокой s, которая начинается со следующей позиции
															// после позиции символа (pos + 1), который мы удаляем, и
															// заканчивается последней позицией переданной строки.
	}

	// округляем числа до 2-х знаков после запятой
	private static double roundВouble(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}
	
	/**
	 * Отдаёт колличество паллет загруженных в машину
	 * @param shops
	 * @param targetStock
	 * @return
	 */
	public Integer calcPallHashHsop(List<Shop> shops, Shop targetStock) {
		Integer summ = 0;
		for (Shop shop : shops) {
			if(targetStock.getNumshop() !=shop.getNumshop()) {
				summ = summ + shop.getNeedPall();
			}
		}
		return summ;
	}
	
	/**
	 * Метод отправки POST запросов 
	 * сделан для запросов в маркет
	 * @param url
	 * @param payload
	 * @return
	 */
	private String postRequest(String url, String payload) {
        try {
            URL urlForPost = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlForPost.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
           
            byte[] postData = payload.getBytes(StandardCharsets.UTF_8);
            
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postData);
            }
            
            int getResponseCode = connection.getResponseCode();
//            System.out.println("POST Response Code: " + getResponseCode);

            if (getResponseCode == HttpURLConnection.HTTP_OK) {
            	StringBuilder response = new StringBuilder();
            	try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    String inputLine;	 
                    while ((inputLine = in.readLine()) != null) {
                    	response.append(inputLine.trim());
                    }
                    in.close();
                }	           
//            	System.out.println(connection.getContentType());
                return response.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            System.out.println("MainRestController.postRequest: Подключение недоступно - 503");
            return "503";
        }
    }
	
	/**
	 * Метод для сохранения действия в файл.
	 * @param idOrder Уникальный идентификатор заказа.
	 * @param marketNumber Номер в маркете.
	 * @param numStockDelivery Номер склада.
	 * @param idRampOld Старый идентификатор рампы.
	 * @param idRampNew Новый идентификатор рампы.
	 * @param timeDeliveryOld Время поставки (старое).
	 * @param timeDeliveryNew Время поставки (новое).
	 * @param loginManager Логин менеджера.
	 * @param action Действие.
	 */
	public void saveActionInFile(HttpServletRequest request, String localPath, Integer idOrder, String marketNumber, String numStockDelivery,
	                             Integer idRampOld, Integer idRampNew,
	                             Timestamp timeDeliveryOld, Timestamp timeDeliveryNew,
	                             String loginManager, String action) {
		String appPath = request.getServletContext().getRealPath("");
		String currentDir = appPath+localPath; //(resources/others/...)
	    try {	        
	    	//получаем ip
	    	String ip = request.getRemoteAddr();

	        // Получаем текущее время и форматируем его
	        LocalDateTime timeAction = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String timeActionString = timeAction.format(formatter);

	        // Получаем текущую дату для имени файла
	        LocalDate currentTime = LocalDate.now();
	        String currentTimeString = String.valueOf(currentTime);
	        String fileName = currentDir + "/" + currentTimeString + ".txt";
	        
	        //проверка директории
	        File fileTest= new File(appPath + "resources/others/blackBox/");
	        if (!fileTest.exists()) {
	            fileTest.mkdir();
	            File fileTest2= new File(appPath + "resources/others/blackBox/slot/");
		        if (!fileTest2.exists()) {
		            fileTest2.mkdir();
		        }
	        }

	        // Проверяем, существует ли файл с текущей датой, и создаем его, если не существует
	        File file = new File(fileName);
	        if (!file.exists()) {
	            try {
	                file.createNewFile();
	                try {
	    	            BufferedWriter writerHeader = new BufferedWriter(new FileWriter(fileName, true));
	    	            writerHeader.write("idOrder" + ";" + "marketNumber" + ";" + "numStockDelivery" + ";" +
	    	                    "idRampOld" + ";" + "idRampNew" + ";" +
	    	                    "timeDeliveryOld" + ";" + "timeDeliveryNew" + ";" +
	    	                    "loginManager" + ";" + "action" + ";" + "timeActionString;ip");
	    	            writerHeader.newLine();
	    	            writerHeader.close();
	    	        } catch (IOException e) {
	    	            e.printStackTrace();
	    	        }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }

	        // Записываем информацию о действии в файл
	        try {
	            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
	            writer.write(idOrder + ";" + marketNumber + ";" + numStockDelivery + ";" +
	                    idRampOld + ";" + idRampNew + ";" +
	                    timeDeliveryOld + ";" + timeDeliveryNew + ";" +
	                    loginManager + ";" + action + ";" + timeActionString+";"+ip);
	            writer.newLine();
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
