package by.base.main.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import by.base.main.controller.ajax.MainRestController;
import by.base.main.model.Act;
import by.base.main.model.Address;
import by.base.main.model.Currency;
import by.base.main.model.Feedback;
import by.base.main.model.Message;
import by.base.main.model.Order;
import by.base.main.model.Rates;
import by.base.main.model.Role;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.Shop;
import by.base.main.model.Tender;
import by.base.main.model.Truck;
import by.base.main.model.User;
import by.base.main.service.ActService;
import by.base.main.service.FeedbackService;
import by.base.main.service.MessageService;
import by.base.main.service.OrderService;
import by.base.main.service.RatesService;
import by.base.main.service.RouteHasShopService;
import by.base.main.service.RouteService;
import by.base.main.service.ServiceException;
import by.base.main.service.ShopService;
import by.base.main.service.TruckService;
import by.base.main.service.UserService;
import by.base.main.service.util.CurrencyService;
import by.base.main.service.util.MailService;
import by.base.main.service.util.PDFWriter;
import by.base.main.service.util.POIExcel;
import by.base.main.service.util.TenderTimer;
import by.base.main.service.util.TimerList;
import by.base.main.util.ChatEnpoint;
import by.base.main.util.MainChat;
import by.base.main.util.GraphHopper.RoutingMachine;
import by.base.main.util.bots.BotInitializer;
import by.base.main.util.bots.TelegramBot;
import by.base.main.util.hcolossus.service.MatrixMachine;

/**
 * 
 * @author Dima Hrushevski
 * изменил формирование актов (даты выгрузко теперь передаются в сессию) 24,01,2023 
 * 220 строка ТЕЛЕГРАММ БОТ!
 */
@Controller
@RequestMapping("/")
public class MainController {	

	public MainController() {
	}
	@Autowired
	private UserService userService;
	
	@Autowired
	private TruckService truckService;
	
	@Autowired
	private RouteHasShopService routeHasShopService;

	@Autowired
	private RouteService routeService;
	
	@Autowired
	private TimerList timerList;

	@Autowired
	private ShopService shopService;

	@Autowired
	private FeedbackService feedbackService;
	
	@Autowired
	private POIExcel poiExcel;
	
	@Autowired
	private RatesService ratesService;
	
	@Autowired
	private ChatEnpoint chatEnpoint;
	
	@Autowired
	private MainChat mainChat;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private ActService actService;
	
	@Autowired
	private MainRestController mainRestController;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private CurrencyService currencyService;
	
	@Autowired
	private ComboPooledDataSource cpds;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private RoutingMachine routingMachine;
	
	@Autowired
	private PDFWriter pdfWriter;
	
	@Autowired
	MatrixMachine matrixMachine;
	
//	@Autowired
//	private TenderCloseProcessor closeProcessor;
	
	@Autowired
	private TelegramBot telegramBot;
	
	public static final Map<String,String> distances = new HashMap<String, String>();
	public static String path = null;
	
	public static final Comparator<Address> comparatorAddressId = (Address e1, Address e2) -> (e1.getIdAddress() - e2.getIdAddress());
	
	
	public static final Comparator<Address> comparatorAddressIdForView = (Address e1, Address e2) -> (e2.getType().charAt(0) - e1.getType().charAt(0));
	
	private static boolean isBlockTender = false;
	private int numOfMessage = 50;
	
	//параметры филттрации маршрутов от большей дате к меньшей, для использования с Collections.sort
	class RouteComparatorForDate implements Comparator<Route>{
		@Override
		public int compare(Route r1, Route r2) {
			return r2.getDateLoadPreviously().compareTo(r1.getDateLoadPreviously());
		}
		
	}	
	//параметры фильтрации маршрутов от меньшей дате к болльшей, для использования с Collections.sort
	class RouteComparatorForAct implements Comparator<Route>{
		@Override
		public int compare(Route r1, Route r2) {
			return r1.getDateUnload().compareTo(r2.getDateUnload());
		}
		
	}

	@GetMapping("/main")
	public String homePage(Model model, HttpSession session, HttpServletRequest request) {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("Зашел user = " + userName);
		System.out.println("Session check status = "+session.getAttribute("check"));
		System.out.println(request.getRemoteAddr());
		
		System.out.println("list before = " + chatEnpoint.internationalMessegeList.size());
		
//		//ловим нарушителей
//		if(userName.equals("catalina!%Ricoh") || userName.equals("catalina!%ricoh")){
//			String text = "На каталину зашли с " + request.getRemoteAddr();
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					mailService.sendSimpleEmail(request, "Вход от catalina", text, "GrushevskiyD@dobronom.by");					
//				}
//			}).start();
//		}
//		//end ловим нарушителей
		if(routingMachine.getGraphHopper() == null) {
			System.out.println("Запуск routingMachine");
			path = request.getServletContext().getRealPath("");
			routingMachine.ini(request);
			//прогреваем кеш после перезапуска
			try {
				FileInputStream fis = new FileInputStream(path + "resources/others/hashmap.ser");
		         ObjectInputStream ois = new ObjectInputStream(fis);
		         chatEnpoint.internationalMessegeList = (ArrayList) ois.readObject();
		         ois.close();
		         fis.close();
			}catch (Exception e) {
				// TODO: handle exception
			}
			System.out.println("list after = " + chatEnpoint.internationalMessegeList.size());			
		}
		
		
		//загружаем матрицу, если её нету
		if(matrixMachine.matrix.size() == 0) {
			new Thread(new Runnable() {			
				@Override
				public void run() {
					matrixMachine.loadMatrixOfDistance();
					System.out.println("Матрица расстояний загружена. Всего: " + matrixMachine.matrix.size() + " значений");
				}
			}).start();
		}
		
		//телеграмм бот!
//		if(telegrAMBOT.ISRUNNING == FALSE) {
//			NEW BOTINITIALIZER(TELEGRAMBOT).INIT();
//		}
		System.err.println("ТЕЛЕГРАММ БОТ ОТКЛЮЧЕН!");
		
		
		try {
			currencyService.loadCurrencyMap(request);
		}catch(Exception e){
			System.out.println("Отстутствует подключение к интернету.");
		}
		
		if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
			return "main";
		}
		User user = userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
		
		System.out.println("Base check status = "+user.getCheck());
		if(user.getCheck() == null) {
			session.setAttribute("sessionCheck", null); // значение опредиляющее региональных юзеров или международных, для чата
			return "main";
		}
		session.setAttribute("YNP", user.getNumYNP());
		if (session.getAttribute("check") == null || user.getCheck() == null || user.getCheck().equals("international") || session.getAttribute("check").equals("international")
				|| user.getCheck().equals("international&new") || session.getAttribute("check").equals("international&new") || user.getCheck().equals("regional&new") || session.getAttribute("check").equals("regional&new")) {
			if (user.getCheck() != null && user.getCheck().equals("international&new") || user.getCheck().equals("regional&new")) {
				session.setAttribute("sessionCheck", user.getCheck()); // значение опредиляющее региональных юзеров или международных, для чата
				System.out.println("Сюда вставить ссыль на приветственную страницу");
				return "welcome";
			}else {
				session.setAttribute("sessionCheck", null); // значение опредиляющее региональных юзеров или международных, для чата
				return "main";
			}
			
		}else {
			//кладу из БД в сессию статус check
			if((session.getAttribute("check")==null && user.getCheck() != null)) {
				User carrier = userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
				session.setAttribute("check", carrier.getCheck());
				return "redirect:/main";
			}else if (session.getAttribute("check").equals("international&new")) {
				
				return "redirect:/main/carrier";
			}
		}		
		return "main";		
	}
	
	@GetMapping("/main/slots")
	public String getSlotsPage(Model model, HttpServletRequest request) {
		return "slots";
	}
	
	@GetMapping("/main/analytics")
	public String getAnalyticsPage(Model model, HttpServletRequest request) {
		return "analytics";
	}
	
	@GetMapping("/main/logistics/analytics")
	public String getAnalyticsLogistPage(Model model, HttpServletRequest request) {
		return "analyticsLogist";
	}
	
	@GetMapping("/main/procurement")
	public String getProcurementPage(Model model, HttpServletRequest request) {
		return "procurement";
	}
	
	@GetMapping("/main/logistics/ordersLogist/order")
	public String getOrderShow(@RequestParam("idOrder") Integer idOrder, Model model, HttpServletRequest request) {
		Order order = orderService.getOrderById(idOrder);
		List<Address> addresses = new ArrayList<Address>();				// не делаю в модели, т.к. логика для разных страниц - разная
		order.getAddresses().stream().filter(a-> a.getIsCorrect())
			.forEach(a-> addresses.add(a));								// не делаю в модели, т.к. логика для разных страниц - разная
		addresses.sort(comparatorAddressIdForView);						// не делаю в модели, т.к. логика для разных страниц - разная
		order.setAddressesToView(addresses);							// не делаю в модели, т.к. логика для разных страниц - разная
		request.setAttribute("order", order);
		return "orderShow";
	}
	
	@GetMapping("/main/procurement/orders/order")
	public String getOrderShowHasProcurement(@RequestParam("idOrder") Integer idOrder, Model model, HttpServletRequest request) {
		Order order = orderService.getOrderById(idOrder);
		List<Address> addresses = new ArrayList<Address>();				// не делаю в модели, т.к. логика для разных страниц - разная
		order.getAddresses().stream().filter(a-> a.getIsCorrect())
			.forEach(a-> addresses.add(a));								// не делаю в модели, т.к. логика для разных страниц - разная
		addresses.sort(comparatorAddressIdForView);						// не делаю в модели, т.к. логика для разных страниц - разная
		order.setAddressesToView(addresses);							// не делаю в модели, т.к. логика для разных страниц - разная
		request.setAttribute("order", order);
		return "orderShow";
	}
	
	@GetMapping("/main/procurement/add-order")
	public String getProcurementFormPage(Model model, HttpServletRequest request) {
		return "procurementForm";
	}
	
	@GetMapping("/main/procurement/orders")
	public String getOrdersPage(Model model, HttpServletRequest request) {
		return "procurementControl";
	}
	
	@GetMapping("/main/procurement/orders/edit")
	public String getOrdersEditPage(Model model, HttpServletRequest request,
			@RequestParam("idOrder") Integer idOrder) {
		Order order = orderService.getOrderById(idOrder);
		List<Address> addressesOld = order.getAddresses().stream().collect(Collectors.toList());
		addressesOld.sort(comparatorAddressId);
		order.setAddressesSort(addressesOld);
		List<Address> addresses = new ArrayList<Address>();
		order.getAddresses().stream().filter(a-> a.getIsCorrect()).forEach(a-> addresses.add(a));
		addresses.sort(comparatorAddressIdForView);
		order.setAddressesToView(addresses);
		request.setAttribute("order", order);
		return "procurementEdit";
	}
	
	@GetMapping("/main/procurement/orders/copy")
	public String getOrdersCopyPage(Model model, HttpServletRequest request,
			@RequestParam("idOrder") Integer idOrder) {
		Order order = orderService.getOrderById(idOrder);
		List<Address> addressesOld = order.getAddresses().stream().collect(Collectors.toList());
		addressesOld.sort(comparatorAddressId);
		order.setAddressesSort(addressesOld);
		List<Address> addresses = new ArrayList<Address>();
		order.getAddresses().stream().filter(a-> a.getIsCorrect()).forEach(a-> addresses.add(a));
		addresses.sort(comparatorAddressIdForView);
		order.setAddressesToView(addresses);
		request.setAttribute("order", order);
//		order.getAddressesToView().forEach(a-> System.out.println(a));
		return "procurementCopy";
	}
	
	@GetMapping("/main/carrier/exchange")
	public String getExchange(Model model, HttpServletRequest request) {
				return "exchange";		
	}
	

	@GetMapping("/main/admin")
	public String adminPage(Model model, HttpServletRequest request) {
		if (distances.isEmpty()) {
			request.setAttribute("errorMessage", "Матрица расстояний не загружена в память!");
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:SS");
		request.setAttribute("time", LocalTime.now().format(formatter));
		request.setAttribute("sizeMainChat", mainChat.messegeList.size());
		request.setAttribute("sizeChatEnpoint", ChatEnpoint.internationalMessegeList.size());
		String poolConnectInfo;
		try {
			poolConnectInfo = cpds.getMinPoolSize() + "  MinPoolSize\n"
					+cpds.getInitialPoolSize()+ "  InitialPoolSize\n"
					+cpds.getNumUserPools() + "  NumUserPools\n"
					+cpds.getMaxPoolSize() + "  MaxPoolSize\n"
					+cpds.getMaxStatements() + "  MaxStatements\n"
					+cpds.getNumConnectionsAllUsers() + "  NumConnectionsAllUsers\n"
					+cpds.getNumConnections() + "  NumConnections\n";
		} catch (SQLException e) {
			poolConnectInfo = "ERROR";
		}
		request.setAttribute("poolConnectInfo", poolConnectInfo);
		return "admin";
	}
	
	@RequestMapping("/main/admin/memory")
	public String clearMemory(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "1", required = false) String clear1,
			@RequestParam(value = "2", required = false) String clear2,
			@RequestParam(value = "3", required = false) String clear3,
			@RequestParam(value = "4", required = false) String clear4) {
		List <Message> forDeliteList = new ArrayList<Message>();
		if(clear1 != null) {
			int targetnum = mainChat.messegeList.size()-numOfMessage;
			for (Message message : mainChat.messegeList) {
				if(forDeliteList.size() < targetnum) {
					forDeliteList.add(message);
				}else {
					break;
				}
			}
			forDeliteList.forEach(m-> mainChat.messegeList.remove(m));
		}else if(clear2 != null) {
			int targetnum = chatEnpoint.internationalMessegeList.size()-numOfMessage;
			for (Message message : chatEnpoint.internationalMessegeList) {
				if(forDeliteList.size() < targetnum) {
					forDeliteList.add(message);
				}else {
					break;
				}
			}
			forDeliteList.forEach(m-> chatEnpoint.internationalMessegeList.remove(m));
		}else if(clear3 != null) {
			mainChat.messegeList.clear();
		}else if(clear4 != null) {
			chatEnpoint.internationalMessegeList.clear();
		}
		return "redirect:/main/admin";
	}

	@GetMapping("/main/admin/userlist")
	public String userListPage(Model model) {
		List<User> userList = new ArrayList<User>();
		List<User> allUsers = userService.getEmployeesList();
//		userList = userService.getUserList();
//		userList.forEach(u->System.out.println(u.toString()));
		allUsers.stream().filter(u->u.getCompanyName() !=null && u.getCompanyName().equals("Доброном"))
			.forEach(u-> userList.add(u));
		model.addAttribute("userlist", userList);
		return "userList";
	}

	@GetMapping("/main/shop")
	public String shopPage(Model model, HttpServletRequest request, HttpSession session) {
		int idShop = getThisUser().getShop().getNumshop();
		getTimeNow(request);
		Date ds = Date.valueOf(LocalDate.now());
		session.setAttribute("dateStart", ds);
//		try {
		Set<Route> routes = new HashSet<Route>();		
		routeService.getRouteListAsDate(ds, ds).stream().forEach(r -> routes.add(r));
		List<Route> arrayRoute = new ArrayList<Route>();
		routes.stream().filter(r-> isShopHasRoute(r, shopService.getShopByNum(idShop))).forEach(r-> arrayRoute.add(r));
		Route route = arrayRoute.stream().findFirst().get();
		RouteHasShop routeHasShop = routeHasShopService.getRouteHasShopByShopAndRoute(idShop, route.getIdRoute());
		model.addAttribute("routeHasShop", routeHasShop);
		model.addAttribute("route", route);
//		} catch (Exception e) {
//			return "shop";
//		}
		return "shop";
	}
	
	@PostMapping("/main/shop")
	public String shopPagePos(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("dateStart") Date ds) {
		int idShop = getThisUser().getShop().getNumshop();		
		session.setAttribute("dateStart", ds);
		request.setAttribute("dateNow", ds);		
		try {
			Set<Route> routes = new HashSet<Route>();		
			routeService.getRouteListAsDate(ds, ds).stream().forEach(r -> routes.add(r));
			List<Route> arrayRoute = new ArrayList<Route>();
			routes.stream().filter(r-> isShopHasRoute(r, shopService.getShopByNum(idShop))).forEach(r-> arrayRoute.add(r));
			Route route = arrayRoute.stream().findFirst().get();
			RouteHasShop routeHasShop = routeHasShopService.getRouteHasShopByShopAndRoute(idShop, route.getIdRoute());
			model.addAttribute("routeHasShop", routeHasShop);
			model.addAttribute("route", route);
		} catch (Exception e) {
			return "shop";
		}		
		return "shop";
	}
	//подтверждения прибытие от магаза
	@RequestMapping("/main/shop/confirm")
	public String shopPageConfirm(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("routeId") int routeId,
			@RequestParam("routeHasShopId") int routeHasShopId) {
		String director = getThisUser().getName() +" " +getThisUser().getSurname();
		String status = LocalDateTime.now().toString() + " | "+director;
		RouteHasShop routeHasShop =  routeHasShopService.getRouteHasShopById(routeHasShopId);
		routeHasShop.setStatus(status);
		routeHasShopService.saveOrUpdateRouteHasShop(routeHasShop);			
		Route route = routeService.getRouteById(routeId);
		Set<RouteHasShop> set = route.getRoteHasShop();
		int fact = 0;
		int feedbackMessage = 0;
		for (RouteHasShop routeHasShop2 : set) {
			if(routeHasShop2.getStatus() != null) {
				fact++;
			}
			List<Feedback> feedback = feedbackService.getFeedbackListByRHS(routeHasShop2.getIdRouteHasShop());//получаю необработанные сообщения
			if(feedback.size() > 0) {
				feedbackMessage++;
			}
		}
		if(fact == set.size()) {
			User driver = route.getDriver();
			Truck truck = route.getTruck();
			int driverStatus = Integer.parseInt(route.getDriver().getStatus()) - 1;
			int truckStatus = Integer.parseInt(route.getTruck().getStatus()) - 1;
			driver.setStatus(driverStatus +"");
			truck.setStatus(truckStatus +"");
			userService.saveOrUpdateUser(driver, 0);
			truckService.saveOrUpdateTruck(truck);
			if(feedbackMessage == 0) {
				route.setStatusRoute("7");
				routeService.saveOrUpdateRoute(route);
			}else {
				route.setStatusRoute("6");
				routeService.saveOrUpdateRoute(route);
				//сюда вставить сообщение о претензии
			}			
			return "redirect:/main/shop";
		}else {
			return "redirect:/main/shop";
		}
		
	}
	//имеется ли магазин в маршруте
	private boolean isShopHasRoute(Route route, Shop shop) {
		Set<RouteHasShop> routesHasShops = route.getRoteHasShop();
		for (RouteHasShop routeHasShop : routesHasShops) {
			if(routeHasShop.getShop().equals(shop)) {				
				return true;				
			}
		}
		return false;		
	}

	@RequestMapping("/main/carrier")
	public String carrierPage(Model model, HttpSession session, HttpServletRequest request,
			@SessionAttribute(name = "errorMessage", required = false) String message) {
		request.setAttribute("errorMessage", message);
		session.removeAttribute("errorMessage");
		return "carrier";
	}

	@RequestMapping("/main/depot")
	public String depotPage(Model model) {
		return "depot";
	}
	
	@RequestMapping("/main/map")
	public String mapPage(Model model) {
		return "depot";
	}

	@RequestMapping("/main/who")
	public String whoTest(Model model, String message) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			User user = userService.getUserByLogin(username);
			model.addAttribute("message", "roles = " + user.getRoles().toString() + " username = " + username);
		} else {
			String username = principal.toString();
			model.addAttribute("message", username);
		}
		return "redirect:/main";
	}

	@RequestMapping("/main/getuser")
	public String userTest(Model model, String message) {
		User user = userService.getUserByLogin("manager");
		model.addAttribute("message", "roles = " + user.getRoles().toString() + " username = " + user.getLogin());

		return "redirect:/main";
	}

	@RequestMapping(value = "/main/signin", method = RequestMethod.GET)
	public String logination(Model model, HttpServletRequest request) {
		User user = new User();
		model.addAttribute("user", user);
		return "signin";
	}
	
	@RequestMapping(value = "/main/registration", method = RequestMethod.GET)
	public String getPreRegistration(Model model) {	

		return "preregistration";
	}
	
	@RequestMapping(value = "/main/registration", method = RequestMethod.POST)
	public String postPreRegistration(Model model, HttpServletRequest request, 
			@RequestParam(value = "but1", required = false) String but1,
			@RequestParam(value = "but2", required = false) String but2,
			@RequestParam(value = "but3", required = false) String but3) {	
		if(but1 != null) {
			User user = new User();
			user.setCheck("regional&new");
			model.addAttribute("user", user);
			return "registrationReg";
		}else if(but3 != null){
			User user = new User();
			user.setCheck("international&new");
			model.addAttribute("user", user);
			return "registrationInt";
		}else {
			request.setAttribute("errorMessage", "в разработке!");
			return "preregistration";
		}
	}
	
	@RequestMapping(value = "/main/registrationReg")
	public String getRegistrationReg(Model model, HttpServletRequest request) {	
		User user = new User();
		user.setCheck("regional&new");
		model.addAttribute("user", user);
		return "registrationReg";
	}
	
	@RequestMapping(value = "/main/registrationInt")
	public String getRegistrationInt(Model model, HttpServletRequest request) {	
		User user = new User();
		user.setCheck("international&new");
		model.addAttribute("user", user);
		return "registrationInt";
	}
	
	// управление кнопкой регистрации для аккаунта директора магазина и юрлица!!!
	@SuppressWarnings("unused")
	@RequestMapping(value = "/main/registration/form", method = RequestMethod.POST)
	public String postRegistration(@ModelAttribute("user") User user, HttpServletRequest request, Model model, HttpSession session,
			@RequestParam(value = "idShop", required = false) Integer idShop,
			@RequestParam(value = "flag", required = false) String flag,
			@RequestParam(value = "international", required = false) String inter,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "dateContract" , required = false) Date dateContract,
			@RequestParam(value = "requisites" , required = false) String requisites) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		if(!userService.getUserByYNP(user.getNumYNP()).isEmpty()) {
			request.setAttribute("errorMessage", "В регистрации отказано! Пользователь с таким УНП существует");
			if (user.getCheck().equals("regional&new")) {
				return "registrationReg";
			}else {
				return "registrationInt";
			}
			
		}else if(idShop !=null) {
			user.setShop(shopService.getShopByNum(idShop));
			userService.saveOrUpdateUser(user, 4); //магазин
			return "redirect:/main/admin/shoplist";
		}else if(flag != null) {
			user.setShop(userService.getUserById(user.getIdUser()).getShop());
			userService.saveOrUpdateUser(user, 0);
			return "redirect:/main/admin/shoplist";
		}else if(user.getCheck().equals("international&new")) {
//			String numContract = user.getNumContract();
//			user.setNumContract(numContract+" от "+dateContract.toLocalDate().format(formatter).toString());
			user.setRequisites(requisites);
			user.setBlock(false);
			userService.saveOrUpdateUser(user, 7); // международник
			session.setAttribute("check", "international&new");
			mailService.sendSimpleEmail(request, "Регистрация в SpeedLogist", "Спасибо что зарегистрировались на товарно-транспортной бирже компании ЗАО \"Доброном\"\n"
					+ "Для того чтобы принять участие в торгах на бирже, необходимо прислать подписанный договор по адресу: 220112 г.Минск ул. Я. Лучины, 5\n"
					+ "Логин для входа: " + user.getLogin(), user.geteMail());
			//mailService.sendEmailWhithFile(request, "Договор " + user.getCompanyName(), "Регистрация нового международного перевозчика", file);// тут косяк ОСТАНОВИЛСЯ ТУТ, слишком долго отправляет
			
			return "redirect:/main/signin";
		}else{
			String numContract = user.getNumContract();
			user.setNumContract(numContract+" от "+dateContract.toLocalDate().format(formatter).toString());
			user.setRequisites(requisites);
			userService.saveOrUpdateUser(user, 7); // перевозчик
			session.setAttribute("check", "step1");
			mailService.sendEmailWhithFile(request, "Договор " + user.getCompanyName(), "Регистрация нового перевозчика", file);
			return "redirect:/main/signin";
		}		
	}
	
	@GetMapping("/main/admin/userlist/showFormForUpdate")
	@Transactional
	public String editUser(Model model, @RequestParam("id") int id) {
		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		Set<Role> roles = user.getRoles();
		Role role = roles.stream().findFirst().get();
		model.addAttribute("role", role);
		return "editUser";
	}
	
	@GetMapping("/main/admin/userlist/add")
	public String addWorkerGet(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "registrationWorker";
	}
	
	@PostMapping("/main/admin/userlist/save")
	public String addWorkerPost(Model model,
			@ModelAttribute ("user") User user,
			@RequestParam ("role") String role) {
		userService.saveOrUpdateUser(user, Integer.parseInt(role));
		return "redirect:/main/admin/userlist";
	}
	
	@RequestMapping("/main/admin/userlist/delete")
	public String deleteWorker(Model model,
			@RequestParam ("idUser") Integer id) {
		userService.deleteUserById(id);
		return "redirect:/main/admin/userlist";
	}

	@PostMapping("/main/admin/userlist/saveUser")
	public String saveUser(@ModelAttribute("user") User user,
			@RequestParam(value = "role", required = false) String role) {
		if (role.length()<=2) {
			userService.saveOrUpdateUser(user , Integer.parseInt(role));
		}else {
			userService.saveOrUpdateUser(user , 0);
		}
		
		return "redirect:/main/admin/userlist";
	}

	@GetMapping("/main/logistics")
	public String getLogistics() {
		return "logistics";
	}
	
	@GetMapping("/main/order-support/control")
	public String getOrderSupportControl () {
		return "orderSupportTimeControlView";
	}

	@PostMapping("/main/logistics/addshop")
	public String addshop(@RequestParam(name = "file", required = false) MultipartFile file, String message)
			throws ServiceException {
		File target = poiExcel.getFileByMultipart(file);
		poiExcel.addDBShops(target);
		return "redirect:/main/logistics";
	}

	@RequestMapping("/main/logistics/upload")
	public String upload(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam(value = "dateStart", required = false) Date dateStart, String message)
			throws ServiceException {
		File target = poiExcel.getFileByMultipart(file);
		poiExcel.dataBaseFromExcel(target, dateStart);
		return "redirect:/main/logistics";
	}

	@GetMapping("/main/logistics/routemanager")
	public String routeManager(Model model, HttpSession session, HttpServletRequest request) throws ServiceException {
		getTimeNow(request);
		getTimeNowPlusDay(request);
		Date ds = Date.valueOf(LocalDate.now());
		Date df = Date.valueOf(LocalDate.now().plusDays(1));
		session.setAttribute("dateStart", ds);
		session.setAttribute("dateFinish", df);
		Set<Route> routes = new HashSet<Route>();
		model.addAttribute("ClassTender", new Tender());
		routeService.getRouteListAsDate(ds, df).stream().filter(r-> r.getRoteHasShop().stream()
				.findFirst().get().getShop() != null).forEach(r -> routes.add(r)); // проверяет созданы ли точки вручную
		
		Set<Route>res = new HashSet<Route>(); //расчёт стоимости!
		for (Route route : routes) { // расчёт стоимости!
			res.add(addCostForRoute(route)); //расчёт стоимости!
		}
		
		
		model.addAttribute("routes", res);
		return "routeManager";
	}
	

	@PostMapping("/main/logistics/routemanager")
	public String routeManagerPost(Model model, HttpServletRequest request, HttpSession session, @RequestParam("dateStart") Date dateStart,
			@RequestParam("dateFinish") Date dateFinish) throws ServiceException {
		request.setAttribute("dateNow", dateStart);
		session.setAttribute("dateStart", dateStart);
		request.setAttribute("dateTomorrow", dateFinish);
		session.setAttribute("dateFinish", dateFinish);
		Set<Route> routes = new HashSet<Route>();
		routeService.getRouteListAsDate(dateStart, dateFinish).stream()
				.filter(r-> r.getRoteHasShop().stream()
				.findFirst().get().getShop() != null)
				.forEach(r -> routes.add(r));
		Set<Route>res = new HashSet<Route>();
		for (Route route : routes) {
			res.add(addCostForRoute(route));
		}
		model.addAttribute("routes", res);
		return "routeManager";
	}
	//интерфейс менеджера измененный
	@RequestMapping("/main/logistics/rouadUpdate")
	public String test1(Model model, HttpServletRequest request, HttpSession session, 
			@RequestParam("id") Integer id,
			@RequestParam(value = "numStock" , required = false) String numStock, 
			@RequestParam(value = "temperature", required = false) String temperature,
			@RequestParam(value ="timeLoadPreviously", required = false) String timeLoadPreviously,
			@RequestParam(value ="time", required = false) String time,
			@RequestParam(value = "statStock", required = false) String statStock,
			@RequestParam(value = "statRoute", required = false) String statRoute,
			@RequestParam(value = "comment", required = false) String comment)	throws ServiceException {
		Route route = routeService.getRouteById(id);
		
		if (numStock != null) {
			route.setNumStock(Integer.parseInt(numStock.trim()));
		}		
		if (timeLoadPreviously != null) {
			route.setTimeLoadPreviously(LocalTime.parse(timeLoadPreviously));
		}
		if (time!= null) {
			route.setTime(LocalTime.parse(time));
		}
		if (statStock !=null) {
			route.setStatusStock(statStock);
		}else {
			System.out.println("Ошибка статуса склада");//on page
		}
		if (statRoute != null) {
			switch (Integer.parseInt(statRoute.trim())) {
			case 1:
				if (Integer.parseInt(route.getStatusRoute().trim()) >= 1) {
					//вставить обработчик 
				}else {
					route.setStatusRoute(statRoute);
					String orderMailStatus = ""; //переменная для письма, указывает создавался ли заказ, или нет
					Set<Order> orders = route.getOrders();
					
					if(orders != null && orders.size() != 0) {
						for (Order o : orders) {
							o.setStatus(50);
							orderService.updateOrderFromStatus(o);
							orderMailStatus = orderMailStatus + "Заказ номер "+ o.getIdOrder() + " от " + o.getManager()+".\n";
						}						
					}else {
						orderMailStatus = "Маршрут создан без заказа.";
					}
					telegramBot.sendMessageHasSubscription("Маршрут " +route.getRouteDirection() + " с загрузкой от " +route.getDateLoadPreviously()+ " стал доступен для торгов!");
					String textStatus = orderMailStatus;
					//отправляем письмо, запускаем его в отдельном потоке, т.к. отправка проходит в среднем 2 секунды
					new Thread(new Runnable() {					
						@Override
						public void run() {
							mailService.sendSimpleEmail(request, "Статус маршрута", "Маршрут "+route.getRouteDirection() + " стал доступен для торгов "
									+LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyy")) 
									+ " в " 
									+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))+"."
									+"\n"+textStatus, "ArtyuhevichO@dobronom.by");
//							mailService.sendSimpleEmailTwiceUsers(request, "Статус маршрута", "Маршрут "+route.getRouteDirection() + " стал доступен для торгов "
//									+LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyy")) 
//									+ " в " 
//									+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))+"."
//							+"\n"+textStatus, "YakubovE@dobronom.by", "ArtyuhevichO@dobronom.by");
							//GrushevskiyD@dobronom.by						
						}
						
					}).start();
				}				
				break;
			case 5:
				route.setStatusRoute(statRoute);
				Set<Order> orders2 = route.getOrders();
				if(orders2 != null && orders2.size() != 0) {
					for (Order o : orders2) {
						if(o.getStatus() != 10) {
							o.setStatus(40);
							orderService.updateOrder(o);
						}
					}						
				}
				break;

			default:
				//вставить обработчик
				break;
			}
			
			
		}else {
			System.out.println("Ошибка статуса тендера");//on page
		}
		if (temperature != null) {
			route.setTemperature(temperature);
		}
		if(route.getTime() == null) {
			route.setTime(LocalTime.parse("00:05"));
		}
		routeService.saveOrUpdateRoute(route);
		if(route.getComments() != null && route.getComments().equals("international")) {
			Set<Route> routes = new HashSet<Route>();
			routeService.getRouteListAsStatus("1", "1").stream()
				.filter(r->r.getComments() != null && r.getComments().equals("international"))
				.forEach(r -> routes.add(r));
			model.addAttribute("routes", routes);
			return "redirect:/main/logistics/international ";
		}
		Set<Route> routes = new HashSet<Route>();
		routeService.getRouteListAsDate((Date)session.getAttribute("dateStart"), (Date)session.getAttribute("dateFinish")).stream()
				.filter(r->r.getComments() == null || !r.getComments().equals("international"))// ой шляпа тут ====================================================== затычка
				.forEach(r -> routes.add(r));		
		request.setAttribute("dateNow", (Date)session.getAttribute("dateStart"));
		request.setAttribute("dateTomorrow", (Date)session.getAttribute("dateFinish"));
		Set<Route>res = new HashSet<Route>();		
		for (Route route2 : routes) {
			res.add(addCostForRoute(route2));
		}
		model.addAttribute("routes", res);
		return "routeManager";
	}

	@GetMapping("/main/depot/stockmanager")
	public String stockManager(Model model, HttpServletRequest request) throws ServiceException {
		getTimeNow(request);
		getTimeNowPlusDay(request);
		Date ds = Date.valueOf(LocalDate.now());
		Date df = Date.valueOf(LocalDate.now().plusDays(1));
		Set<Route> routes = new HashSet<Route>();		
		routeService.getRouteListAsDate(ds, df).stream().filter(r -> r.getStatusStock()!=null)
				.filter(r->!r.getStatusStock().equals("0")).forEach(r -> routes.add(r));
		model.addAttribute("routes", routes);
		return "stockManager";
	}

	@PostMapping("/main/depot/stockmanager")
	public String stockManagerPost(Model model, HttpServletRequest request, @RequestParam("dateStart") Date dateStart,
			@RequestParam("dateFinish") Date dateFinish) throws ServiceException {
		request.setAttribute("dateNow", dateStart);
		request.setAttribute("dateTomorrow", dateFinish);
		Set<Route> routes = new HashSet<Route>();
		routeService.getRouteListAsDate(dateStart, dateFinish).stream().filter(r -> r.getStatusStock()!=null)
				.filter(r->!r.getStatusStock().equals("0")).forEach(r -> routes.add(r));
		model.addAttribute("routes", routes);
		return "stockManager";
	}
	//интерфейс склада
	@PostMapping("/main/depot/stockUpdate")
	public String stockUpdatePost(Model model, HttpServletRequest request,
			@RequestParam("id") int id,
			@RequestParam(value = "timeLoadPreviouslyStock", required = false) LocalTime timeLoadPreviouslyStock,
			@RequestParam(value = "actualTimeArrival", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualTimeArrival,
			@RequestParam(value = "rump", required = false) Integer rump,
			@RequestParam(value = "lines", required = false) String lines) throws ServiceException {
		Route route = routeService.getRouteById(id);
		
		
		String startLoad = request.getParameter("startLoad");
		String finishLoad = request.getParameter("finishLoad");
		String deliveryDocuments = request.getParameter("deliveryDocuments");
		if(startLoad !=null) {
			route.setStartLoad(LocalDateTime.now());
			routeService.saveOrUpdateRoute(route);
		}else if (finishLoad != null) {
			route.setFinishLoad(LocalDateTime.now());
			routeService.saveOrUpdateRoute(route);
		}else if (deliveryDocuments != null) {
			route.setDeliveryDocuments(LocalDateTime.now());
			routeService.saveOrUpdateRoute(route);
		}else if(request.getParameter("update") != null) {
			if(timeLoadPreviouslyStock != null) {
				route.setTimeLoadPreviouslyStock(timeLoadPreviouslyStock);
			}
			if(actualTimeArrival !=null) {
				route.setActualTimeArrival(actualTimeArrival);
			}			
			route.setLines(lines);
			route.setRamp(rump);
			routeService.saveOrUpdateRoute(route);
		}
		return "redirect:/main/depot/stockmanager";
	}
	
	@RequestMapping("/main/carrier/tender")
	public String tenderGetPage(Model model, HttpSession session, HttpServletRequest request) {
		User user = getThisUser();
		LocalDate dateNow = LocalDate.now();
		if(user.isBlock() != null && user.isBlock()) {
			request.setAttribute("blockMessage", "Доступ к тендерам запрещен! Обратитесь к администратору");
			return "blockPage";
		}
		if(isBlockTender) {
			request.setAttribute("blockMessage", "Тендеры завершены. Идёт обработка поступивших предложений.");
			return "blockPage";
		}
		if (user.getRequisites() == null) {
			model.addAttribute("user", new User());
			return "elementRegistration";
		}
		if (user.getCheck() != null && user.getCheck().equals("international")) {
			//ВАЖНО! Само получение тендеров осуществляется через REST
			Set<Route> routes = new HashSet<Route>();
			routeService.getRouteListAsStatus("1", "1").stream()
				.filter(r->r.getComments() !=null && r.getComments().equals("international"))
				.filter(r-> !r.getDateLoadPreviously().isBefore(dateNow)) // не показывает тендеры со вчерашней датой загрузки
				.forEach(r-> routes.add(r));
			model.addAttribute("routes", routes);//отдаёт маршруты для международников в тендер		
//			String appPath = request.getServletContext().getRealPath("");			
//			String urlForDownload = appPath + "resources/others/Speedlogist.apk";
//			request.setAttribute("download", urlForDownload);
			return "tender";
		}else if(user.getCheck() != null && user.getCheck().equals("step2")) {
			session.setAttribute("errorMessage", "В доступе отказано! Необходимо внести автопарк для проверки");
			return "redirect:/main/carrier";
		}else if(user.getCheck() != null && user.getCheck().equals("step3")) {
			session.setAttribute("errorMessage", "В доступе отказано! Проверяется зарегистрированный автопарк");
			return "redirect:/main/carrier";
		}else{
			//обработка внутренних маршрутов
			double maxWeight = 0.0;
			List<Truck> trucks = truckService.getTruckListByUser();		
			for (Truck truck : trucks) {
				if (Double.parseDouble(truck.getCargoCapacity()) > maxWeight) {
					maxWeight = Double.parseDouble(truck.getCargoCapacity());
				}
			}
			Set<Route> routes = new HashSet<Route>();
			routeService.getRouteListAsStatus("1", "1").stream().forEach(r-> routes.add(r));
			Set<Route> finalRoutes = new HashSet<Route>();
			for (Route route : routes) {
				if (Double.parseDouble(route.getTotalCargoWeight()) < maxWeight && route.getComments() == null) {
					finalRoutes.add(route);
				}
			}
			Set<Route>res = new HashSet<Route>();
			for (Route route2 : finalRoutes) {
				res.add(addCostForRoute(route2));
			}		
			model.addAttribute("routes", res);
			model.addAttribute("user", user);
			return "tender";}
		
	}
	
	
	@RequestMapping("/main/carrier/tender/history")
	public String tenderHistoryGetPage(Model model, HttpSession session, HttpServletRequest request) {
		return "tenderHistory";
	}
	
	@PostMapping("/main/carrier/updateUserData")
	public String postSaveDataUser(Model model, HttpServletRequest request,
			@RequestParam(value = "numContract", required = false) String numContract,
			@RequestParam(value = "dateContract", required = false) Date dateContract,
			@RequestParam(value = "requisites", required = false) String requisites,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "eMail", required = false) String eMail,
			@RequestParam(value = "director", required = false) String director) {
		User user = getThisUser();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		user.setNumContract(numContract + " от "+dateContract.toLocalDate().format(formatter).toString());
		user.setRequisites(requisites);
		user.seteMail(eMail);
		user.setDirector(director);
		userService.saveOrUpdateUser(user, 0);
		mailService.sendEmailWhithFile(request, "Внесение информации по договору " + user.getCompanyName(), "Внесение доп. информации", file);
		return "redirect:/main/carrier";
	}
	
	
	@RequestMapping("/main/admin/tender")
	public String tenderGetPageForAdmin(Model model, HttpServletRequest request) {
		Set<Route> routes = new HashSet<Route>();
		routeService.getRouteListAsStatus("1", "1").stream().forEach(r-> routes.add(r));
		Set<Route>res = new HashSet<Route>();
		for (Route route2 : routes) {
			res.add(addCostForRoute(route2));
		}
		model.addAttribute("routes", res);
		return "tender";
	}
	
	@RequestMapping("/main/carrier/tender/tenderpage")
	public String tenderPage(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "routeId", required = false) Integer routeId) {
		User user = getThisUser();
		if(user.getNumYNP() != null && user.getNumYNP().split("&").length >1) {
			request.setAttribute("blockMessage", "Доступ к тендерам запрещен! Обратитесь к администратору");
			return "blockPage";
		}
		if (isBlockTender) {
			request.setAttribute("blockMessage", "Тендеры завершены. Идёт обработка поступивших предложений.");
			return "blockPage";
		}
		if(user.getCheck() != null && user.getCheck().split("&").length >1) {
			request.setAttribute("blockMessage", "В доступе отказано! Необходимо пройти вертификацию. Обратитесь к администратору");
			return "blockPage";
		}
		if (routeId == null) {
			routeId = (Integer) session.getAttribute("idRoute");
		}
		Route route = routeService.getRouteById(routeId);
		boolean flag = false;
		if (routeService.getRouteById(routeId) == null) {
			request.setAttribute("errorMessage", "Маршрут № "+routeId+ " удалён, или не создан.");
			return "errorPage";
		}
		if (route.getComments() != null && route.getComments().equals("international")) {
			for (Message message : chatEnpoint.internationalMessegeList) {
				if (message.getIdRoute().equals(routeId.toString()) && message.getYnp().equals(user.getNumYNP())) { // <-- исправлено тут
					
					flag = true;
					request.setAttribute("userCost", message.getText());
					request.setAttribute("userCurrency", message.getCurrency());
					break;
				}
			}
			model.addAttribute("route", route);
			request.setAttribute("flag", flag);
		}else {
			model.addAttribute("route", addCostForRoute(route));
			request.setAttribute("regionalRoute", true);
		}		
		model.addAttribute("user", user);
		return "tenderPage";
	}
	//интерфейс листа тендера
	@SuppressWarnings("static-access")
	@RequestMapping("/main/carrier/tender/tenderUpdate")
	public String tenderPageUpdate(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("id") Integer routeId,
			@RequestParam("price") String percent) {
		
		Route route = routeService.getRouteById(routeId);
		int finishPercent = (Integer.parseInt(percent.trim()));
		session.setAttribute("idRoute", routeId);
		if (request.getParameter("agree") != null && route.getFinishPrice()==null) { // подтверждение первой цены
			User user = getThisUser();			
			route.setFinishPrice(0);
			routeService.saveOrUpdateRoute(route);//забиваем процент в БД
			route.setStatusRoute("2");
			route.setUser(user);
			int seconds = route.getTime().getMinute()*60 + route.getTime().getSecond();
			TenderTimer tenderTimer = new TenderTimer(seconds, addCostForRoute(route), routeService, chatEnpoint, mainChat);
			timerList.addAndStart(tenderTimer);
		}else if(request.getParameter("agree") != null && finishPercent > route.getFinishPrice()){ //новая цена
			User user = getThisUser();			
			route.setFinishPrice(finishPercent);
			routeService.saveOrUpdateRoute(route);//забиваем процент в БД
			route.setStatusRoute("2");
			route.setUser(user);
			int seconds = route.getTime().getMinute()*60 + route.getTime().getSecond();
			TenderTimer tenderTimer = new TenderTimer(seconds, addCostForRoute(route), routeService, chatEnpoint, mainChat);
			timerList.replace(tenderTimer); // важно! меняем!
		}else {
			System.out.println("pidor"); //валидацию в JS
		}
		return "redirect:/main/carrier/tender";
	}
	
	@RequestMapping("/main/carrier/controlpark")
	public String controlParkGet(Model model, HttpServletRequest request, HttpSession session) {
		return "controlPark";
	}
	@GetMapping("/main/carrier/controlpark/trucklist")
	public String truckListGet(Model model, HttpServletRequest request, HttpSession session) {
		if(getThisUser().getCheck() != null && session.getAttribute("check") == null && getThisUser().getCheck().equals("international")){
			Set<Truck> trucks = new HashSet<Truck>();
			trucks.addAll(truckService.getTruckListByUser());
			model.addAttribute("trucks", trucks);
			request.setAttribute("check", "international");
			return "truckList";
		}else if (session.getAttribute("check") == null || getThisUser().getCheck() == null) {
			List<Truck> trucks = truckService.getTruckListByUser();
			model.addAttribute("trucks", trucks);
			return "truckList";	
		}else if(session.getAttribute("check").equals("step3") || userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName()).getCheck().equals("step3")){
			List<Truck> trucks = truckService.getTruckListByUser();
			model.addAttribute("trucks", trucks);
			request.setAttribute("check", session.getAttribute("check"));
			return "truckList";
		}else {
			List<Truck> trucks = truckService.getTruckListByUser();
			model.addAttribute("trucks", trucks);
			request.setAttribute("check", session.getAttribute("check"));
			return "truckList";			
		}
	}
	
	@GetMapping("/main/logistics/controlpark")
	public String truckListCarrierForAdmin(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("idCarrier") Integer idCarrier) {
		request.setAttribute("idCarrier", idCarrier);
		return "truckListForLogist";			
	}
	
	@PostMapping("/main/carrier/controlpark/trucklist")
	public String truckListPost(Model model, HttpServletRequest request, HttpSession session) {
		if (session.getAttribute("check") == null || userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName()).getCheck() == null) {
			//действие для подтвержденных перневозов
			return "redirect:/main/carrier0/controlpark"; // после, добавить метод post типо отравка списка авто
		}else {	
			session.removeAttribute("check");
			session.setAttribute("check", "step3");
			User user = userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
			user.setCheck("step3");
			userService.saveOrUpdateUser(user, 0);
			return "redirect:/main/carrier";
		}	
	}
	
	@RequestMapping("/main/carrier/controlpark/trucklist/add")
	public String addTruck(Model model, HttpServletRequest request) {
		Truck truck = new Truck();
		model.addAttribute("truck", truck);
		return "truckForm";
	}
	//сохранение или обновление транспорта POST
	@RequestMapping("/main/carrier/controlpark/trucklist/save")
	public String saveTruck(Model model, HttpServletRequest request,
			@ModelAttribute("truck") Truck truck,
			@RequestParam("id") int id,
			@RequestParam("typeTrailer") String typeTrailer,
			@RequestParam("cargoCapacity") String cargoCapacity,
			@RequestParam (value = "file", required = false) MultipartFile file) {
		if (id == 0) {
			User user = getThisUser();
			truck.setCargoCapacity(cargoCapacity);
			truck.setTypeTrailer(typeTrailer);
			truckService.saveOrUpdateTruck(truck);
//			if (owner != null) {
//				truck.setOwnerTruck(owner);
//			}
			if(file != null) {				
				mailService.sendEmailWhithFile(request, "Техапаспорт от " + user.getCompanyName(), "техпаспорт авто", file);
			}
		}else if (id !=0) {
			truck.setCargoCapacity(cargoCapacity);
			truck.setTypeTrailer(typeTrailer);
			truck.setIdTruck(id);
			truckService.saveOrUpdateTruck(truck);
		}
		return "redirect:/main/carrier/controlpark/trucklist";
	}
	
	@RequestMapping("/main/carrier/controlpark/trucklist/update")
	public String updateTruck(Model model, HttpServletRequest request,
			@RequestParam("truckId") int id) {
		Truck truck = truckService.getTruckById(id);
		model.addAttribute("truck", truck);
		return "truckForm";
	}
	
	@RequestMapping("/main/carrier/controlpark/trucklist/delete")
	public String deleteTruck(Model model, HttpServletRequest request,
			@ModelAttribute("truck") Truck truck,
			@RequestParam("truckId") int id) {
		truckService.deleteTruckById(id);
		return "redirect:/main/carrier/controlpark/trucklist";
	}
	
	@RequestMapping("/main/carrier/controlpark/driverlist")
	public String driverListGet(Model model, HttpServletRequest request, HttpSession session) {	
		List<User> drivers = userService.getDriverList(getThisUser().getCompanyName());
		model.addAttribute("drivers", drivers);
		return "driverList";
	}
	
	@RequestMapping("/main/carrier/controlpark/driverlist/add")
	public String addDriver(Model model, HttpServletRequest request) {	
		User driver = new User();
		User user = getThisUser();
		model.addAttribute("user", driver);
		request.setAttribute("check", user.getCheck());
		return "driverForm";
	}
	
	@RequestMapping("/main/carrier/controlpark/driverlist/update")
	public String updateDriver(Model model, HttpServletRequest request,
			@RequestParam("driverId") int id) {		
		User driver = userService.getUserById(id);
		model.addAttribute("user", driver);
		return "driverForm";
	}
	
	@RequestMapping("/main/carrier/controlpark/driverlist/delete")
	public String deleteDriver(Model model, HttpServletRequest request,
			@RequestParam("driverId") int id) {	
		User user = userService.getUserById(id);
		user.setCompanyName(null);
		user.setLogin(null);
		user.setPassword(null);
		userService.saveOrUpdateUser(user, 0);
		return "redirect:/main/carrier/controlpark/driverlist";
	}
	//сохранение или обновление водителей POST
	@RequestMapping("/main/carrier/controlpark/driverlist/save")
	public String saveDriver(Model model, HttpServletRequest request,
			@ModelAttribute("user") User user,
			@RequestParam("id") int id) {	
		if (id !=0) {
			User target = userService.getUserById(id);
			target.setName(user.getName());
			target.setSurname(user.getSurname());
			target.setPatronymic(user.getPatronymic());
			target.setTelephone(user.getTelephone());
			target.setNumPass(user.getNumPass());
			userService.saveOrUpdateUser(target, 0);
		}else if(userService.getUserByDriverCard(user.getNumDriverCard()) != null && userService.getUserByDriverCard(user.getNumDriverCard()).getLogin() == null){	
			User target = userService.getUserByDriverCard(user.getNumDriverCard());
			target.setName(user.getName());
			target.setSurname(user.getSurname());
			target.setPatronymic(user.getPatronymic());
			target.setTelephone(user.getTelephone());
			target.setLogin(user.getLogin());
			target.setPassword(user.getPassword());
			userService.saveOrUpdateUser(target, 8);
		}else if(userService.getUserByDriverCard(user.getNumDriverCard()) != null && !userService.getUserByDriverCard(user.getNumDriverCard()).getLogin().isEmpty()){	
			System.out.println("данный водитель уже зарегистрирован");
			request.setAttribute("errorMessage", "данный водитель уже зарегистрирован");			
			return addDriver(model, request);
		}else {
			user.setStatus("0");
			userService.saveOrUpdateUser(user, 8);
		}
		return "redirect:/main/carrier/controlpark/driverlist";
	}
	
	//получение страницы с маршрутами перевозчика!
	@RequestMapping("/main/carrier/transportation")
	public String transportationGet(Model model, HttpServletRequest request, HttpSession session) {	
		User user = getThisUser();
		model.addAttribute("user", user);
		List<Route> routes = routeService.getRouteListByUser();
		List<Route> resultRoutes = new ArrayList<Route>();
		routes.stream().filter(r-> Integer.parseInt(r.getStatusRoute())<=4).filter(r-> !resultRoutes.contains(r)).forEach(r->resultRoutes.add(r));
		Collections.sort(resultRoutes, new RouteComparatorForDate());
		model.addAttribute("routes", resultRoutes);
		List<Truck> trucks = truckService.getTruckListByUser();
		Set<Truck> freeTrucks = new HashSet<Truck>();
		trucks.stream().filter(t->t.getStatus() == null || t.getStatus().equals("1") || t.getStatus().equals("0")).forEach(t->freeTrucks.add(t));
		// тут остановился. Добавить проверку водителей, подходит ли транспорт по весу и вместимости, отправить на страницу
		model.addAttribute("trucks", freeTrucks);
		List<User> drivers = userService.getDriverList(getCompanyName());
		Set<User> freeDrivers = new HashSet<User>();
		drivers.stream().filter(d-> d.getStatus() == null || d.getStatus().equals("1") || d.getStatus().equals("0")).forEach(d->freeDrivers.add(d));
		model.addAttribute("drivers", freeDrivers);		
		if (session.getAttribute("errorMessage") != null) {
			request.setAttribute("errorMessage", session.getAttribute("errorMessage"));
			session.removeAttribute("errorMessage");
		}
		return "transportation";
	}
	
	@RequestMapping("/main/carrier/transportation/update")
	public String transportationUpdate(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value ="isTruck", required = false) Integer idTruck,
			@RequestParam(value ="isDriver", required = false) Integer idDriver,
			@RequestParam("id") int idRoute,
			@RequestParam(value = "revers", required = false) String revers,
			@RequestParam(value = "dateLoadActually" , required = false) String dateLoad,
			@RequestParam(value = "dateUnloadActually" , required = false) String dateUnload,
			@RequestParam(value = "timeUnloadActually" , required = false) String timeUnload,
			@RequestParam(value = "timeLoadActually" , required = false) String timeLoad) {	
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
		if(timeUnload.length()>5) {
			String[] strArr = timeUnload.split(":");
			timeUnload = strArr[0]+":"+strArr[1];
		}
		Route route = routeService.getRouteById(idRoute);
		route.setDateUnloadActually(LocalDate.parse(dateUnload, formatter));
		route.setTimeUnloadActually(LocalTime.parse(timeUnload, formatterTime));
		route.setDateLoadActually(LocalDate.parse(dateLoad, formatter));
		route.setTimeLoadActually(LocalTime.parse(timeLoad, formatterTime));
		if(revers != null) {
//			route.setDriver(null);
//			route.setTruck(null);
//			routeService.saveOrUpdateRoute(route);
		}else if(idDriver == null && idTruck != null) {
			Truck truck = truckService.getTruckById(idTruck);
			//временно
//			if(truck.getBrandTrailer() == null) {
//				session.setAttribute("errorMessage", "На авто " + truck.getNumTruck() +" отсутствует марка прицепа. Пожалуйста, укажите марку прицепа в настройках авто.");	
//				return "redirect:/main/carrier/transportation";
//			}
			double weightHasTruck = Double.parseDouble(truck.getCargoCapacity());
			double weightHasRoute = Double.parseDouble(route.getTotalCargoWeight());
			double pallHasTruck = Double.parseDouble(truck.getPallCapacity());
			double pallHasRoute = Double.parseDouble(route.getTotalLoadPall());
//			if (weightHasRoute > weightHasTruck || pallHasRoute > pallHasTruck) {				
//				session.setAttribute("errorMessage", "Данная машина не может быть поставлена на этот маршрут. Паллеты или тоннаж машины не позволяют перевезти данный заказ");	
//				return "redirect:/main/carrier/transportation";
//			}else {
//				route.setTruck(truck);
//				route.setStatusRoute("4");
//				routeService.saveOrUpdateRoute(route);
//			}
			route.setTruck(truck);
			route.setStatusRoute("4");
			routeService.saveOrUpdateRoute(route);
		}else if(idDriver != null && idTruck == null){
			User driver = userService.getUserById(idDriver);
			route.setDriver(driver);
			route.setStatusRoute("4");
			routeService.saveOrUpdateRoute(route);			
		}else if (idDriver == null && idTruck == null){
			session.setAttribute("errorMessage", "Не выбран водитель");			
			return "redirect:/main/carrier/transportation";
		}else {			
			User driver = userService.getUserById(idDriver);
			Truck truck = truckService.getTruckById(idTruck);
			//временно
//			if(truck.getBrandTrailer() == null) {
//				session.setAttribute("errorMessage", "На авто " + truck.getNumTruck() +" отсутствует марка прицепа. Пожалуйста, укажите марку прицепа в настройках авто.");
//				return "redirect:/main/carrier/transportation";
//			}
			double weightHasTruck = Double.parseDouble(truck.getCargoCapacity());
			double weightHasRoute = Double.parseDouble(route.getTotalCargoWeight());
			double pallHasTruck = Double.parseDouble(truck.getPallCapacity());
			double pallHasRoute = Double.parseDouble(route.getTotalLoadPall());
//			if (weightHasRoute > weightHasTruck || pallHasRoute > pallHasTruck) {				
//				session.setAttribute("errorMessage", "Данная машина не может быть поставлена на этот маршрут. Паллеты или тоннаж машины не позволяют перевезти данный заказ");
//				return "redirect:/main/carrier/transportation";
//			}else {
////				if (driver.getStatus() == null || driver.getStatus().equals("0")) {
////					driver.setStatus("1");
////				}else {
////					int status = Integer.parseInt(driver.getStatus());
////					status = status + 1;
////					driver.setStatus(status+"");
////				}
////				if(truck.getStatus() == null || truck.getStatus().equals("0")) {
////					truck.setStatus("1");
////				}else {
////					int status = Integer.parseInt(truck.getStatus());
////					status = status + 1;
////					truck.setStatus(status+"");
////				}
//				userService.saveOrUpdateUser(driver, 0);
//				truckService.saveOrUpdateTruck(truck);
//				route.setDriver(driver);
//				route.setTruck(truck);
//				route.setStatusRoute("4");
//				routeService.saveOrUpdateRoute(route);
//			}
			userService.saveOrUpdateUser(driver, 0);
			truckService.saveOrUpdateTruck(truck);
			route.setDriver(driver);
			route.setTruck(truck);
			route.setStatusRoute("4");
			routeService.saveOrUpdateRoute(route);
		}				
		return "redirect:/main/carrier/transportation";
	}
	
	@RequestMapping("/main/carrier/transportation/tenderpage")
	public String tenderPageFromTransportation(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "routeId", required = false) Integer routeId) {
		if (routeId == null) {
			routeId = (Integer) session.getAttribute("idRoute");
		}
		Route route = routeService.getRouteById(routeId);
		model.addAttribute("route", route);	
		model.addAttribute("user", getThisUser());
		return "tenderPage";
	}
	
	@RequestMapping("/main/carrier/transportation/routecontrole")
	public String routecontroleGet(Model model, HttpServletRequest request, HttpSession session) {
		User user = getThisUser();
		if (user.getRequisites() == null) {
			model.addAttribute("user", new User());
			return "elementRegistration";
		}
		model.addAttribute("user", user);
		List<Route> routes = routeService.getRouteListByUser();
		List<Route> resultRoutes = new ArrayList<Route>();
		routes.stream().filter(r-> Integer.parseInt(r.getStatusRoute())<=4).filter(r->!resultRoutes.contains(r)).forEach(r->resultRoutes.add(r));
		Collections.sort(resultRoutes, new RouteComparatorForDate());
		model.addAttribute("routes", resultRoutes);
		List<Truck> trucks = truckService.getTruckListByUser();
		Set<Truck> freeTrucks = new HashSet<Truck>();
		trucks.stream().filter(t->t.getStatus() == null || t.getStatus().equals("1") || t.getStatus().equals("0")).forEach(t->freeTrucks.add(t));
		// тут остановился. Добавить проверку водителей, подходит ли транспорт по весу и вместимости, отправить на страницу
		model.addAttribute("trucks", freeTrucks);	
		List<User> drivers = userService.getDriverList(getCompanyName());
		Set<User> freeDrivers = new HashSet<User>();
		drivers.stream().filter(d-> d.getStatus() == null || d.getStatus().equals("1") || d.getStatus().equals("0")).forEach(d->freeDrivers.add(d));
		model.addAttribute("drivers", freeDrivers);		
		if (session.getAttribute("errorMessage") != null) {
			request.setAttribute("errorMessage", session.getAttribute("errorMessage"));
			session.removeAttribute("errorMessage");
		}
		if(session.getAttribute("errorMessage") != null) {
			request.setAttribute("errorMessage", session.getAttribute("errorMessage"));
			session.removeAttribute("errorMessage");
		}
		return "routeControlList";
	}
	
	
	//переход на страницу с предварительным актом
	@GetMapping("/main/carrier/transportation/routecontrole/getformact")
	public String getActForm(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "targetAct", required = false) Integer[] idRoutes,
			@RequestParam(value = "dateUnload", required = false) String[] datesUnload,
			@RequestParam(value = "isNDS") boolean isNDS) {
		String month = datesUnload[0].split("-")[1];
		for (String string : datesUnload) {
			if(!string.split("-")[1].equals(month)) {
				session.setAttribute("errorMessage", "Акт выполненных работ не может быть составлен из рейсев, выгруженных в разные месяцы!");
				return "redirect:/main/carrier/transportation/routecontrole";
			}
		}
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		User user = getThisUser();
		List<Route> routes = new ArrayList<Route>();
		String currency = null;
		int i = 0;
		for (Integer idRoute : idRoutes) {
			Route route = routeService.getRouteById(idRoute);
			if(currency == null) {
				currency = route.getStartCurrency();
			}else if(route.getStartCurrency() == null || !route.getStartCurrency().equals(currency)) {
				session.setAttribute("errorMessage", "Акт выполненных работ не может быть составлен из разных валют!");
				return "redirect:/main/carrier/transportation/routecontrole";
			}
			
			if(route.getDateLoadPreviously().isAfter(LocalDate.parse(datesUnload[i]))) {
				session.setAttribute("errorMessage", "Дата выгрузки в акте не может быть раньше даты загрузки!");
				return "redirect:/main/carrier/transportation/routecontrole";
			}
			String dateUnload = datesUnload[i].split("-")[2]+"."+datesUnload[i].split("-")[1]+"."+datesUnload[i].split("-")[0];
			route.setDateUnload(dateUnload);
			routes.add(route);
			i++;
		}
		Collections.sort(routes, new RouteComparatorForAct());
		List<String> datesUnloadtest = new ArrayList<String>();
		for (Route route : routes) {
			datesUnloadtest.add(route.getDateUnload());
		}
		session.setAttribute("datesUnload", datesUnloadtest);
		LocalDate dateOfAct = null;
		for (String string : datesUnload) {
			if(dateOfAct == null) {
				dateOfAct = LocalDate.parse(string);
			}
			if(dateOfAct.isBefore(LocalDate.parse(string))) {
				dateOfAct = LocalDate.parse(string);
			}
		}
		
		model.addAttribute("routes", routes);		
		request.setAttribute("dateNow", dateOfAct.format(formatter).toString());
		request.setAttribute("user", user);
		request.setAttribute("isNDS", isNDS);
		
		return "actForm";
	}
	
	
	@PostMapping("/main/carrier/transportation/routecontrole/getformact")
	@ResponseBody
	public String postActForm(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam(value = "numWayList", required = false) String[] numWayLists,
			@RequestParam(value = "idRoute", required = false) Integer[] idRoutes,
			@RequestParam(value = "numTruckAndTrailer", required = false) String[] numTruckAndTrailers,
			@RequestParam(value = "cmr", required = false) String[] cmrArr,
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "sheffName", required = false) String sheffName,
			@RequestParam(value = "numContract", required = false) String numContractTarget,
			@RequestParam(value = "dateContract", required = false) String dateContract,
			@RequestParam(value = "isNDS") boolean isNDS,
			@RequestParam(value = "requisitesCarrier", required = false) String requisitesCarrier,
			@RequestParam(value = "costWay", required = false) String[] costWay,
			@RequestParam(value = "dateUnload", required = false) String[] dateUnload,
			@RequestParam(value = "сargoWeight", required = false) String[] сargoWeight,
			@RequestParam(value = "dateOfAct", required = false) String dateOfAct )throws IOException, DocumentException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		User user = getThisUser();
		List<Route> routes = new ArrayList<Route>();
		
		List<String> datesUnloadtest = (List<String>) session.getAttribute("datesUnload");
		List<Route> routesForOrders = new ArrayList<Route>();
		for (int i =0; i<idRoutes.length; i++) {
			Route route = routeService.getRouteById(idRoutes[i]);
			route.setNumWayList(numWayLists[i]);
			route.setNumTruckAndTrailer(numTruckAndTrailers[i]);
			route.setCmr(cmrArr[i]);
			route.setCostWay(costWay[i]);
			route.setCargoWeightForAct(сargoWeight[i]);
			route.setDateUnload(datesUnloadtest.get(i));
			routes.add(route);			
//			route.setStatusRoute("6"); // перенес ниже, после проверки колличества листов в акте
			routesForOrders.add(route);
//			routeService.saveOrUpdateRoute(route); // перенес ниже, после проверки колличества листов в акте
		}
		Collections.sort(routes, new RouteComparatorForAct()); // тут сортировать?
//		poiExcel.getActOfRoute(routes, request, isNDS, dateContract, numContractTarget, user.getDirector(), city, requisitesCarrier, dateOfAct);
		
		//test остановился тут, тут залочены акты
		int numPage = pdfWriter.getActOfRoute(routesForOrders, request, isNDS, dateContract, numContractTarget, user.getDirector(), city, requisitesCarrier, dateOfAct, 0);
		if(numPage != 1) {
			numPage = pdfWriter.getActOfRoute(routesForOrders, request, isNDS, dateContract, numContractTarget, user.getDirector(), city, requisitesCarrier, dateOfAct, numPage);
		}
		if(numPage>2) {
			//тут сгенерить ошибку
			return null;
		}else {
			//тут меняем статусы
			for (int i =0; i<routes.size(); i++) {
				Route route = routes.get(i);
				route.setStatusRoute("6"); 
				routeService.saveOrUpdateRoute(route);
			}
		}

		String appPath = request.getServletContext().getRealPath("");
		String fileName = "Act_N"+routes.get(0).getIdRoute().toString();
		response.setHeader("content-disposition", "attachment;filename="+fileName+".pdf");
		String fileNameForRead = user.getCompanyName() + ".pdf";
		File file = new File(appPath + "resources/others/" + fileNameForRead);
		session.removeAttribute("datesUnload");
		mailService.sendEmailWhithFileToUser(request, "Акт от перевозчика "+user.getCompanyName(), "", file, "apanaschiko@dobronom.by");
		FileInputStream in = null;
		OutputStream out = null;
		try {
			
			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
//			in = new FileInputStream(appPath + "resources/others/act.xlsx");
			in = new FileInputStream(appPath + "resources/others/" + fileNameForRead);
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
		//закрываем заказы, если имеются
		for (Route route : routesForOrders) {
			if(route.getOrders() != null) {
				route.getOrders().forEach(o->{
					o.setStatus(70);
					orderService.updateOrderFromStatus(o);
				});
			}
		}
		return "redirect:/main/carrier/transportation/routecontrole";
	}
	
	@RequestMapping("/main/carrier/downdoad")
	public String downdoadGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		//File file = new File(appPath + "resources/others/Speedlogist.apk");
		response.setHeader("content-disposition", "attachment;filename="+"Speedlogist"+".apk");
		FileInputStream in = null;
		OutputStream out = null;
		try {
			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
			in = new FileInputStream(appPath + "resources/others/Speedlogist.apk");
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
	
	@RequestMapping("/main/logistics/documentflow")
	public String documentflowGet(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		return "documentflow";
	}
	
	@PostMapping("/main/logistics/documentflow")
	public String documentflowPost(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam(value = "numAct", required = false) String numAct,
			@RequestParam( value = "dateStart", required = false) Date dateStart,
			@RequestParam( value = "dateFinish", required = false) Date dateFinish,
			@RequestParam(value = "cancelAct", required = false) String cancelAct,
			@RequestParam(value = "getAct", required = false) String getAct,
			@RequestParam(value = "idAct", required = false) Integer idAct,
			@RequestParam(value = "comment", required = false) String comment) {
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		if(dateStart == null || dateFinish == null) {
			if (session.getAttribute("dateStart") !=null || session.getAttribute("dateFinish")!=null) {
				dateStart = (Date) session.getAttribute("dateStart");
				dateFinish = (Date) session.getAttribute("dateFinish");
				request.setAttribute("dateNow", session.getAttribute("dateStart"));
				request.setAttribute("dateTomorrow", session.getAttribute("dateFinish"));
			}else {
				getTimeNow(request);
				getTimeNowPlusDay(request);
				dateStart = Date.valueOf(LocalDate.now());
				dateFinish = Date.valueOf(LocalDate.now().plusDays(5));
				session.setAttribute("dateStart", dateStart);
				session.setAttribute("dateFinish", dateFinish);
			}			
		}else {
			// тут я заменил метот пост, на метод гет, в форме задание даты отображения
			request.setAttribute("dateNow", dateStart);
			session.setAttribute("dateStart", dateStart);
			request.setAttribute("dateTomorrow", dateFinish);
			session.setAttribute("dateFinish", dateFinish);			
		}
		if(getAct != null) {
			Act act = actService.getActById(idAct);
			act.setStatus(LocalDateTime.now().format(formatter2).toString());
			act.setComment(comment);
			//что-то должно делаться с маршрутом!
			actService.saveOrUpdateAct(act);
			return "redirect:/main/logistics/documentflow/documentlist";
		}else if(cancelAct != null) {
			Act act = actService.getActById(idAct);
			act.setStatus("del");
			act.setComment(comment);
			act.setCancel(LocalDateTime.now().format(formatter2).toString());
			String[] idRoutes = act.getIdRoutes().trim().split(";");
			for (String idRoute : idRoutes) {
				Route route = routeService.getRouteById(Integer.parseInt(idRoute));
				route.setStatusRoute("4");
				routeService.saveOrUpdateRoute(route);
			}
			actService.saveOrUpdateAct(act);
			return "redirect:/main/logistics/documentflow/documentlist";
		}else {
			List<Act> result = new ArrayList<Act>();
			List<Act> acts1 = actService.getActBynumAct(numAct);
			List<Act> acts2 = actService.getActBynumAct("T"+numAct);
			List<Act> acts3 = actService.getActBySecretCode(numAct);
			result.addAll(acts1);
			result.addAll(acts2);
			result.addAll(acts3);
			request.setAttribute("acts", result);
			if(result.isEmpty()) {
				request.setAttribute("errorMessage", "Актов с номерами "+numAct+" или "+"T"+numAct+ " не найдено");
				return "documentflow";
			}
			return "documentlist";
		}
		
	}
	
	@RequestMapping("/main/logistics/documentflow/documentlist")
	public String documentlistGet(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam( value = "dateStart", required = false) Date dateStart,
			@RequestParam( value = "dateFinish", required = false) Date dateFinish) {
		if(dateStart == null || dateFinish == null) {
			if (session.getAttribute("dateStart") !=null || session.getAttribute("dateFinish")!=null) {
				dateStart = (Date) session.getAttribute("dateStart");
				dateFinish = (Date) session.getAttribute("dateFinish");
				request.setAttribute("dateNow", session.getAttribute("dateStart"));
				request.setAttribute("dateTomorrow", session.getAttribute("dateFinish"));
			}else {
				getTimeNow(request);
				getTimeNowPlusDay(request);
				dateStart = Date.valueOf(LocalDate.now());
				dateFinish = Date.valueOf(LocalDate.now().plusDays(5));
				session.setAttribute("dateStart", dateStart);
				session.setAttribute("dateFinish", dateFinish);
			}			
		}else {
			// тут я заменил метот пост, на метод гет, в форме задание даты отображения
			request.setAttribute("dateNow", dateStart);
			session.setAttribute("dateStart", dateStart);
			request.setAttribute("dateTomorrow", dateFinish);
			session.setAttribute("dateFinish", dateFinish);			
		}
		Set<Act> acts = new HashSet<Act>();
		acts.addAll(actService.getActListAsDate(dateStart, dateFinish));
		model.addAttribute("acts", acts);
		return "documentlist";
	}
	
	@PostMapping("/main/logistics/documentflow/documentlist")
	public String documentlistPost(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam( value = "dateStart", required = false) Date dateStart,
			@RequestParam( value = "dateFinish", required = false) Date dateFinish,
			@RequestParam(value = "idAct", required = false) Integer idAct,
			@RequestParam(value = "getAct", required = false) String getAct,
			@RequestParam(value = "cancelAct", required = false) String cancelAct,
			@RequestParam(value = "comment", required = false) String comment) {
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		if(getAct!=null) {
			Act act = actService.getActById(idAct);
			act.setStatus(LocalDateTime.now().format(formatter2).toString());
			act.setComment(comment);
			//что-то должно делаться с маршрутом!
			actService.saveOrUpdateAct(act);
			List<Act> inCansel = actService.getActBynumAct(act.getNumAct());
			for (Act act2 : inCansel) {
				if (act2.getStatus().equals("1")) {
					act2.setCancel(LocalDateTime.now().format(formatter2));
					act2.setStatus("del");
					act2.setComment("подписан другой акт");
					actService.saveOrUpdateAct(act2);
				}
			}
		}else {
			Act act = actService.getActById(idAct);
			act.setStatus("del");
			act.setComment(comment);
			act.setCancel(LocalDateTime.now().format(formatter2).toString());
			String[] idRoutes = act.getIdRoutes().trim().split(";");
			for (String idRoute : idRoutes) {
				Route route = routeService.getRouteById(Integer.parseInt(idRoute));
				route.setStatusRoute("4");
				routeService.saveOrUpdateRoute(route);
			}
			actService.saveOrUpdateAct(act);
		}

		return "redirect:/main/logistics/documentflow/documentlist";
	}
	
	@RequestMapping("/main/logistics/shopControl")
	public String getShopListLogist(Model model, HttpServletRequest request, HttpSession session) {
		List<Shop> shops = shopService.getShopList();		
		model.addAttribute("shops", shops);
		return "shopListForLogist";
	}
	
		
	@RequestMapping("/main/admin/shoplist")
	public String shoplistGet(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "test", required = false) String[] tests) {
		List<Shop> shops = shopService.getShopList();
		model.addAttribute("shops", shops);
		return "shopList";
	}
	
	@RequestMapping("/main/admin/shoplist/addaccount")
	public String addaccountGet(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("id") int idShop) {
		User user = new User();
		Shop shop = shopService.getShopByNum(idShop);
		model.addAttribute("shop", shop);
		model.addAttribute("user", user);
		return "registrationShop";
	}
	
	@RequestMapping("/main/admin/shoplist/update")
	public String shopAccountUpdate(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("shopId") int idShop) {
		User user = shopService.getShopByNum(idShop).getDirector();
		model.addAttribute("user", user);
		return "registrationShop";
	}
	
	@RequestMapping("/main/admin/shoplist/delete")
	public String shopAccountDelete(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("shopId") int idShop) {
		int userId = shopService.getShopByNum(idShop).getDirector().getIdUser();
		userService.deleteUserById(userId);
		return "redirect:/main/admin/shoplist";
	}
	
	@RequestMapping("/main/shop/feedback")
	public String shopFeedback(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("idRouteHasShop") int idRouteHasShop) {
		Feedback feedback = new Feedback();
		model.addAttribute("feedback", feedback);
		model.addAttribute("driver", routeHasShopService.getRouteHasShopById(idRouteHasShop).getRoute().getDriver());
		User user = userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
		model.addAttribute("shop", user.getShop());
		model.addAttribute("idRouteHasShop", idRouteHasShop);
		return "feedbackForm";
	}
	
	@RequestMapping("/main/shop/feedback/form")
	public String shopFeedbackForm(Model model, HttpServletRequest request, HttpSession session,
			@ModelAttribute("driver") int idDriver,
			@ModelAttribute("feedback") Feedback feedback,			
			@RequestParam("radio") String radio) {
		User ShopDirect = userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
		User driver = userService.getUserById(idDriver);
		addFeddbackFromShop(ShopDirect, driver, feedback, radio);
		return "redirect:/main/shop";
	}
	
	@RequestMapping("/main/admin/carrier")
	public String carrierListForAdmin(Model model, HttpServletRequest request, HttpSession session) {
		List<User> carriers = userService.getCarrierListV2();
		Set<User> checkCarriers = new HashSet<User>();
		carriers.stream().filter(c-> c.getCheck() == null).forEach(c-> checkCarriers.add(c));
		model.addAttribute("carriers", checkCarriers);
		request.setAttribute("regionalCarrier", true);
		return "adminCarrier";
	}
	@RequestMapping("/main/logistics/internationalCarrier")
	public String internationalCarrierListForManager(Model model, HttpServletRequest request, HttpSession session) {
		System.out.println("start");
		List<User> carriers = userService.getCarrierListV2();
		System.out.println("end");
		Set<User> checkCarriers = new HashSet<User>();
		carriers.stream().filter(c->c.getCheck() != null &&c.getCheck().equals("international") && !c.isBlock())
			.forEach(c-> checkCarriers.add(c));
		model.addAttribute("carriers", checkCarriers);
		request.setAttribute("internationalCarrier", true);
		return "adminCarrier";
	}
	
	@RequestMapping("/main/logistics/internationalCarrier/block")
	public String internationalCarrierGetBlock(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("carrierYNP") String ynp) {
		List<User> carriers = userService.getUserByYNP(ynp);
//		carriers.forEach(c-> c.setNumYNP(ynp+"&block"));
		carriers.forEach(c-> c.setBlock(true));		
		carriers.forEach(c-> userService.saveOrUpdateUser(c, 0));
		return "redirect:/main/logistics/internationalCarrier";
	}
	@GetMapping("/main/logistics/carrier/update") 
	public String internationalCarrierUpdateGet(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("carrierId") int carrierId) {
		User carrier = userService.getUserById(carrierId);
		model.addAttribute("carrier", carrier);
		return "editCarrier";
	}
	@PostMapping("/main/logistics/carrier/update") 
	public String internationalCarrierUpdatePost(Model model, HttpServletRequest request, HttpSession session,
			@ModelAttribute("carreir") User carrier) {
		User oldCarrier = userService.getUserById(carrier.getIdUser());
		oldCarrier.setAddress(carrier.getAddress());
		oldCarrier.setCompanyName(carrier.getCompanyName());
		oldCarrier.setSurname(carrier.getSurname());
		oldCarrier.setName(carrier.getName());
		oldCarrier.setPatronymic(carrier.getPatronymic());
		oldCarrier.setNumYNP(carrier.getNumYNP());
		oldCarrier.setTelephone(carrier.getTelephone());
		userService.saveOrUpdateUser(oldCarrier, 0);	
		return "redirect:/main/logistics/internationalCarrier";
	}
	@GetMapping("/main/admin/carrier/update") 
	public String carrieUpdateGet(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("carrierId") int carrierId) {
		User carrier = userService.getUserById(carrierId);
		model.addAttribute("carrier", carrier);
		return "editCarrier";
	}
	
	@PostMapping("/main/admin/carrier/update") 
	public String carrieUpdatePost(Model model, HttpServletRequest request, HttpSession session,
			@ModelAttribute("carreir") User carrier) {
		User oldCarrier = userService.getUserById(carrier.getIdUser());
		oldCarrier.setAddress(carrier.getAddress());
		oldCarrier.setCompanyName(carrier.getCompanyName());
		oldCarrier.setSurname(carrier.getSurname());
		oldCarrier.setName(carrier.getName());
		oldCarrier.setPatronymic(carrier.getPatronymic());
		oldCarrier.setNumYNP(carrier.getNumYNP());
		oldCarrier.setTelephone(carrier.getTelephone());
		oldCarrier.setRate(carrier.getRate());
		userService.saveOrUpdateUser(oldCarrier, 0);	
		return "redirect:/main/admin/carrier";
	}
	@GetMapping("/main/admin/carrier/delete") 
	public String carrieDeletePost(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("carrierId") int carrierId) {
		User oldCarrier = userService.getUserById(carrierId);
		oldCarrier.setEnablet(false);
		userService.saveOrUpdateUser(oldCarrier, 0);
		return "redirect:/main/admin/carrier";
	}
	
	@GetMapping("/main/admin/carrier/internationalProof") 
	public String internationalCarrieProofGet(Model model, HttpServletRequest request, HttpSession session) {
		List<User> carriers = userService.getCarrierListV2();
		Set<User> checkCarriers = new HashSet<User>();
		carriers.stream().filter(c->c.getCheck() != null &&c.getCheck().equals("international&new"))
			.forEach(c-> checkCarriers.add(c));
		model.addAttribute("carriers", checkCarriers);
		request.setAttribute("proof", true);
		return "adminCarrierProof";
	}
	@PostMapping("/main/admin/carrier/internationalProof") 
	public String internationalCarrieProofPost(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("carrierId") Integer carrierId){
		User carrier = userService.getUserById(carrierId);
		carrier.setCheck("international");
		userService.saveOrUpdateUser(carrier, 0);
		return "redirect:/main/logistics/internationalCarrier";
	}
	@GetMapping("/main/admin/carrier/proof") 
	public String carrieProofGet(Model model, HttpServletRequest request, HttpSession session) {
		List<User> carriers = userService.getCarrierListV2();
		Set<User> proofCarriers = new HashSet<User>();
		carriers.stream().filter(c->c.getCheck() != null && c.getCheck().equals("step3")).forEach(c->proofCarriers.add(c));
		model.addAttribute("carriers", proofCarriers);
		return "adminCarrierProof";
	}
	@PostMapping("/main/admin/carrier/proof") 
	public String carrieProofPost(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("carrierId") int carrierId,
			@RequestParam("rate") String rate){
		User carrier = userService.getUserById(carrierId);
		carrier.setRate(rate);
		carrier.setCheck(null);
		userService.saveOrUpdateUser(carrier, 0);
		return "redirect:/main/admin/carrier";
	}
	@GetMapping("/main/admin/carrier/block") 
	public String carrieBlockGet(Model model, HttpServletRequest request, HttpSession session) {
		List<User> carriers = userService.getDesableCarrierList();
		Set<User> blockCarriers = new HashSet<User>();
		carriers.stream().filter(c->c.getLoyalty() !=null).forEach(c-> blockCarriers.add(c));
		model.addAttribute("carriers", blockCarriers);
		model.addAttribute("isBlock", true);
		return "adminCarrierProof";
	}
	@GetMapping("/main/admin/carrier/internationalBlock") 
	public String internationalCarrieBlockGet(Model model, HttpServletRequest request, HttpSession session) {
		List<User> carriers = userService.getCarrierListV2();
		Set<User> blockCarriers = new HashSet<User>();
		carriers.stream().filter(c->c.getCheck() !=null && c.getCheck().equals("international") && c.isBlock())
			.forEach(c-> blockCarriers.add(c));
		model.addAttribute("carriers", blockCarriers);
		model.addAttribute("international", true);
		return "adminCarrierProof";
	}
	@PostMapping("/main/admin/carrier/internationalBlock") 
	public String internationalCarrieBlockPost(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("carrierYNP") String carrierYNP) {
		List<User> carriers = userService.getUserByYNP(carrierYNP);
		carriers.forEach(c-> c.setBlock(false));
		carriers.forEach(c-> userService.saveOrUpdateUser(c, 0));
		return "redirect:/main/logistics/internationalCarrier";
	}
	@PostMapping("/main/admin/carrier/block") 
	public String carrieBlockPost(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("carrierId") int carrierId){
		User carrier = userService.getUserById(carrierId);
		carrier.setEnablet(true);
		userService.saveOrUpdateUser(carrier, 0);
		return "redirect:/main/admin/carrier";
	}
	
	@RequestMapping("/main/admin/carrier/park") 
	public String carrieAdminParkGet(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "carrierId", required = false) Integer carrierId){
		if(carrierId == null) {
			carrierId = (Integer) session.getAttribute("carrierId");
		}
		User user = userService.getUserById(carrierId);
		session.setAttribute("carrierId", carrierId);
		session.setAttribute("user", user);
		List<Truck> trucks = truckService.getTruckListByUser(user);
		model.addAttribute("trucks", trucks);
		model.addAttribute("user", user);
		
		return "truckList";	
	}
	
	@RequestMapping("/main/admin/carrier/park/update")
	public String updateTruckforAdmin(Model model, HttpServletRequest request,
			@RequestParam("truckId") int id) {
		Truck truck = truckService.getTruckById(id);
		model.addAttribute("truck", truck);
		return "truckForm";
	}
	
	@RequestMapping("/main/admin/carrier/park/delete")
	public String deleteTruckForAdmin(Model model, HttpServletRequest request,
			@ModelAttribute("truck") Truck truck,
			@RequestParam("truckId") int id) {
		truckService.deleteTruckById(id);
		return "redirect:/main/admin/carrier/park";
	}
	
	@RequestMapping("/main/admin/carrier/park/add")
	public String addTruckForAdmin(Model model, HttpServletRequest request, HttpSession session){
		Truck truck = new Truck();
		model.addAttribute("truck", truck);
		return "truckForm";
	}
	
	@RequestMapping("/main/admin/carrier/park/save")
	public String saveTruckforAdmin(Model model, HttpServletRequest request, HttpSession session,
			@ModelAttribute("truck") Truck truck,
			@RequestParam("id") int id,
			@RequestParam("typeTrailer") String typeTrailer) {
		if (id == 0) {
			truck.setTypeTrailer(typeTrailer);
			truckService.saveOrUpdateTruck(truck, (User)session.getAttribute("user"));			
		}else if (id !=0) {
			truck.setTypeTrailer(typeTrailer);
			truck.setIdTruck(id);
			truckService.saveOrUpdateTruck(truck, (User)session.getAttribute("user"));
		}
		session.removeAttribute("user");
		return "redirect:/main/admin/carrier/park";
	}
	
	@GetMapping("/main/admin/carrier/feedback")
	public String carrierFeedbackForAdminGet(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("carrierId") int idCarrier) {
		User carrier = userService.getUserById(idCarrier);
		List<Feedback> feedbackList = feedbackService.getFeedbackList();
		Set<Feedback> targetList = new HashSet<Feedback>();
		feedbackList.stream().filter(f-> f.getUser().getCompanyName().equals(carrier.getCompanyName()))
		.forEach(f-> targetList.add(f));
		model.addAttribute("feedback", targetList);
		model.addAttribute("carrier", carrier);
		return "adminFeedbackList";
	}
	
	@GetMapping("/main/admin/carrier/feedback/tender")
	public String carrierFeedbackForAdminTenderGet(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam("idRouteHasShop") int idRouteHasShop) {
		Route route = routeHasShopService.getRouteHasShopById(idRouteHasShop).getRoute();
		model.addAttribute("route", route);		
		return "tenderPage";
	}
	
	/*@RequestMapping("/main/carrier/controlpark/trucklist/update")
	public String updateTruck(Model model, HttpServletRequest request,
			@RequestParam("truckId") int id) {
		Truck truck = truckService.getTruckById(id);
		model.addAttribute("truck", truck);
		return "truckForm";
	}
	*/
	
	@RequestMapping("/main/admin/cost")
	public String costControlGet(Model model, HttpServletRequest request) {
		return "costMain";
	}
	
	@PostMapping("/main/admin/cost")
	public String costControlPost(Model model, HttpServletRequest request,
			@RequestParam(name = "but1", required = false) String but1) throws InvalidFormatException, IOException {
		if (but1!=null) {
			System.out.println("Start");
			distances.clear();
			poiExcel.getDistancesToMap(distances, request);
			System.out.println("загрузка в память завершена. Контрольная сумма = " + distances.size());
		}
		return "costMain";
	}
	
	@GetMapping("/main/admin/cost/rates")
	public String rates(Model model, HttpServletRequest request) throws InvalidFormatException, IOException {
		List<Rates> ratesList = ratesService.getRatesList();
		model.addAttribute("rates", ratesList);
		return "ratesList";
	}
	
	@GetMapping("/main/admin/cost/rates/add")
	public String ratesFormGet(Model model, HttpServletRequest request) throws InvalidFormatException, IOException {
		Rates rates = new Rates();
		model.addAttribute("rate", rates);
		return "ratesForm";
	}
	
	@PostMapping("/main/admin/cost/rates/add")
	public String ratesFormPost(Model model, HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("rate") Rates rates) throws InvalidFormatException, IOException {
		ratesService.saveOrUpdateRates(rates);
		return "redirect:/main/admin/cost/rates";
	}

	//Менеджер международных маршрутов NEW GET
	@GetMapping("/main/logistics/internationalNew")
	public String internationalGetNEW(HttpServletRequest request, HttpSession session, Model model,HttpServletResponse response) throws IOException {
			
		return "internationalManagerNew";
	}
	
	
	@GetMapping("/main/logistics/international")
	public String internationalGet(HttpServletRequest request, HttpSession session, Model model,HttpServletResponse response,
			@RequestParam( value = "dateStart", required = false) Date dateStart,
			@RequestParam( value = "dateFinish", required = false) Date dateFinish) throws IOException {
		
		if(dateStart == null || dateFinish == null) {
			if (session.getAttribute("dateStart") !=null || session.getAttribute("dateFinish")!=null) {
				dateStart = (Date) session.getAttribute("dateStart");
				dateFinish = (Date) session.getAttribute("dateFinish");
				request.setAttribute("dateNow", session.getAttribute("dateStart"));
				request.setAttribute("dateTomorrow", session.getAttribute("dateFinish"));
			}else {
				getTimeNow(request);
				getTimeNowPlusDay(request);
				dateStart = Date.valueOf(LocalDate.now());
				dateFinish = Date.valueOf(LocalDate.now().plusDays(5));
				session.setAttribute("dateStart", dateStart);
				session.setAttribute("dateFinish", dateFinish);
			}			
		}else {
			// тут я заменил метот пост, на метод гет, в форме задание даты отображения
			request.setAttribute("dateNow", dateStart);
			session.setAttribute("dateStart", dateStart);
			request.setAttribute("dateTomorrow", dateFinish);
			session.setAttribute("dateFinish", dateFinish);			
		}
		Set<Route> routes = new HashSet<Route>();
		List<Route>targetRoutes = routeService.getRouteListAsDate(dateStart, dateFinish);
		java.util.Date point1 = new java.util.Date(); // POINT
		targetRoutes.stream()
			.filter(r-> r.getComments() != null && r.getComments().equals("international") && Integer.parseInt(r.getStatusRoute())<=8)
			.forEach(r -> routes.add(r)); // проверяет созданы ли точки вручную, и отдаёт только международные маршруты
		model.addAttribute("routes", routes);
		request.setAttribute("isBlockTender", isBlockTender);		
		return "internationalManager";
	}
	
	@GetMapping("/main/logistics/ordersLogist")
	public String internationalOrdersLogist(HttpServletRequest request, HttpSession session, Model model) {
		return "procurementControlLogist";
	}
	
	@GetMapping("/main/order-support/orders")
	public String internationalOrdersSopportLogist(HttpServletRequest request, HttpSession session, Model model) {
		return "orderSupportControlLogist";
	}
	
	@GetMapping("/main/stock-support/orders")
	public String internationalStockSopport(HttpServletRequest request, HttpSession session, Model model) {
		return "internationalStockSopport";
	}
	
	@GetMapping("/main/order-support/orders/order")
	public String getOrderShowForOrderSupport(@RequestParam("idOrder") Integer idOrder, Model model, HttpServletRequest request) {
		Order order = orderService.getOrderById(idOrder);
		List<Address> addresses = new ArrayList<Address>();				// не делаю в модели, т.к. логика для разных страниц - разная
		order.getAddresses().stream().filter(a-> a.getIsCorrect())
			.forEach(a-> addresses.add(a));								// не делаю в модели, т.к. логика для разных страниц - разная
		addresses.sort(comparatorAddressIdForView);						// не делаю в модели, т.к. логика для разных страниц - разная
		order.setAddressesToView(addresses);							// не делаю в модели, т.к. логика для разных страниц - разная
		request.setAttribute("order", order);
		return "orderShow";
	}
	
	@GetMapping("/main/logistics/ordersLogist/routeForm")
	public String internationalrouteForm(HttpServletRequest request, HttpSession session, Model model,
			@RequestParam(value = "idOrder", required = false) Integer idOrder) {		 
		Order order = orderService.getOrderById(idOrder);
		List<Address> addressesOld = order.getAddresses().stream().collect(Collectors.toList());
		addressesOld.sort(comparatorAddressId);
		order.setAddressesSort(addressesOld);
		request.setAttribute("order", order);
		return "procurementFormHasLogist";
	}
	
	@GetMapping("/main/logistics/stoptender")
	public String internationalBlockTender(HttpServletRequest request, HttpSession session, Model model,
			@RequestParam( value = "stop", required = false) String stop,
			@RequestParam( value = "start", required = false) String start) {
		if(stop!=null) {
			isBlockTender = true;
		}else if (start != null) {
			isBlockTender = false;
		}
		return "redirect:/main/logistics/international";
	}
	//на всякий случай
//	@PostMapping("/main/logistics/international")
//	public String internationalPost(Model model,HttpServletResponse response, HttpServletRequest request, HttpSession session, @RequestParam("dateStart") Date dateStart,
//			@RequestParam("dateFinish") Date dateFinish) throws ServiceException {
//		request.setAttribute("dateNow", dateStart);
//		session.setAttribute("dateStart", dateStart);
//		request.setAttribute("dateTomorrow", dateFinish);
//		session.setAttribute("dateFinish", dateFinish);
//		Set<Route> routes = new HashSet<Route>();
//		routeService.getRouteListAsDate(dateStart, dateFinish).stream()
//			.filter(r-> r.getComments() != null && r.getComments().equals("international"))
//			.forEach(r -> routes.add(r)); // проверяет созданы ли точки вручную, и отдаёт только международные маршруты
//		model.addAttribute("routes", routes);
//		return "internationalManager";
//	}
	
	@RequestMapping("/main/logistics/international/add")
	public String internationalAddGet() {
		return "internationalForm";
	}
	
	
	@RequestMapping("/main/logistics/international/addRoute")
	public String internationalAddRouteGet(Model model, HttpServletRequest request,
			@RequestParam(value = "routeDirection", required = false) String routeDirection,
			@RequestParam(value = "idRoute", required = false) Integer idRoute,
			HttpSession session) {
		User user = getThisUser();
		if (routeDirection != null) {
			Route route = (Route) session.getAttribute(user.getIdUser()+"route");
			route.setRouteDirection(routeDirection);
			
			//блок определения оптимальной цены
			//получаем маршруты по названию
			List<Route> routes = routeService.getRouteListAsRouteDirection(route);
			List<Route> step1 = new ArrayList<>();
			routes.stream().filter(r-> r.getFinishPrice() != null && r.getStartCurrency() != null && r.getStartCurrency().equals("BYN")).forEach(r->step1.add(r)); // пока что ижем только в белках	
			Integer optimalCost = 0;			
			if (step1.size() != 0 && step1.size()<5) {
				Integer summ=0;
				for (Route route2 : step1) {
					summ = summ + route2.getFinishPrice();
				}
				optimalCost = (summ/step1.size());
				if (route.getStartPrice() == null) {					
					route.setOptimalCost(optimalCost.toString());
				}				
			}else if(step1.size() == 0) {
				System.out.println("Маршрут " + routeDirection + " в базе данных не найден");//обработка ручного ввода
				route.setOptimalCost("1000");
			}else {
				Integer summ=0;
				summ = summ + step1.get(step1.size()-1).getFinishPrice();
				summ = summ + step1.get(step1.size()-2).getFinishPrice();
				summ = summ + step1.get(step1.size()-3).getFinishPrice();
				summ = summ + step1.get(step1.size()-4).getFinishPrice();
				summ = summ + step1.get(step1.size()-5).getFinishPrice();
				optimalCost = summ/5;
				if (route.getStartPrice() == null) {
					route.setOptimalCost(optimalCost.toString());
				}	
			}
			//конец блока
			request.setAttribute("listCosts", step1);
			model.addAttribute("route", route);
			RouteHasShop routeHasShop = route.getRoteHasShop().stream().findFirst().get();
			request.setAttribute("pall", routeHasShop.getPall());
			request.setAttribute("weight", routeHasShop.getWeight());
		}else if (idRoute != null) {
			Route route = routeService.getRouteById(idRoute);
			//блок определения оптимальной цены
			//получаем маршруты по названию
			List<Route> routes = routeService.getRouteListAsRouteDirection(route);
			List<Route> step1 = new ArrayList<>();
			routes.stream().filter(r-> r.getFinishPrice() != null && r.getStartCurrency() != null && r.getStartCurrency().equals("BYN")).forEach(r->step1.add(r)); // пока что ижем только в белках		
			Integer optimalCost = 0;			
			if (step1.size() != 0 && step1.size()<5) {
				Integer summ=0;
				for (Route route2 : step1) {
					summ = summ + route2.getFinishPrice();
				}
				optimalCost = (summ/step1.size());
				if (route.getStartPrice() == null) {					
					route.setOptimalCost(optimalCost.toString());
				}				
			}else if(step1.size() == 0) {
				System.out.println("Маршрут " + routeDirection + " в базе данных не найден");//обработка ручного ввода
				route.setOptimalCost("1000");
			}else {
				Integer summ=0;
				summ = summ + step1.get(step1.size()-1).getFinishPrice();
				summ = summ + step1.get(step1.size()-2).getFinishPrice();
				summ = summ + step1.get(step1.size()-3).getFinishPrice();
				summ = summ + step1.get(step1.size()-4).getFinishPrice();
				summ = summ + step1.get(step1.size()-5).getFinishPrice();
				optimalCost = summ/5;
				if (route.getStartPrice() == null) {
					route.setOptimalCost(optimalCost.toString());
				}	
			}
			//конец блока
			route.setIdRoute(null);
			route.setTimeLoadPreviously(null);
			model.addAttribute("route", route);
			RouteHasShop routeHasShop = route.getRoteHasShop().stream().findFirst().get();
			request.setAttribute("pall", routeHasShop.getPall());
			request.setAttribute("weight", routeHasShop.getWeight());
			request.setAttribute("volume", routeHasShop.getVolume());
			request.setAttribute("listCosts", step1);
			model.addAttribute("idRouteCopy", idRoute);
		}		
		return "routeForm";
	}
	
	@RequestMapping("/main/logistics/international/addRoutePattern")
	public String internationalAddPatternRouteGet(Model model, HttpServletRequest request,
			@RequestParam(value = "routeDirection", required = false) String routeDirection,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {	
		model.addAttribute("route", new Route());
		request.setAttribute("patternRoutes", routeService.getRouteListAsComment("pattern"));
		return "routeForm";
	}
	
	@RequestMapping("/main/logistics/international/addRoutePattern/confrom") //09.10.2023
	public String internationalAddPatternRouteConfromGet(Model model, HttpServletRequest request,
			@RequestParam(value = "date", required = false) Date dateStart,
			@RequestParam(value = "timeOfLoad", required = false) LocalTime time,
			@RequestParam(value = "idRoute", required = false) Integer idRoute,
			@RequestParam(value = "way", required = false) String way) {	
		Route route = routeService.getRouteById(idRoute);
		route.setIdRoute(null);
		Set<RouteHasShop>routeHasShops = route.getRoteHasShop();
		routeHasShops.forEach(rhs->{
			rhs.setIdRouteHasShop(null);
			rhs.setRoute(null);
		});
		route.setWay(way);
		route.setTimeLoadPreviously(time);		
		route.setComments("international");	
		route.setDateLoadPreviously(dateStart);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
		route.setRouteDirection(route.getRouteDirection().split(",")[0]);
		route.setRoteHasShop(null);
		routeService.saveOrUpdateRoute(route);
		Route routeGet = routeService.getLastRoute();
		routeHasShops.forEach(rhs->rhs.setRoute(routeGet));
		routeHasShops.forEach(rhs->routeHasShopService.saveOrUpdateRouteHasShop(rhs));
		routeGet.setRouteDirection(routeGet.getRouteDirection()+" N"+routeGet.getIdRoute());
		routeService.saveOrUpdateRoute(routeGet);
		return "redirect:/main/logistics/international";
	}

@PostMapping("/main/logistics/international/addRoute")  //09.10.2023
	public String internationalAddRoutePost(Model model,
			@ModelAttribute("route") Route target,
			@RequestParam(value = "date", required = false) Date dateStart,
			@RequestParam(value = "timeOfLoad", required = false) LocalTime time,
			@RequestParam(value = "count", required = false) Integer count,
			@RequestParam(value = "volume", required = false) String volume,
			@RequestParam(value = "idRouteCopy", required = false) Integer idRouteCopy,
			HttpSession session) {
	User user = getThisUser();
		if(idRouteCopy != null) {
			if(count == 1) {
				Route route = poiExcel.creatureEmptyRoute(dateStart);
				if (time != null) {
					target.setTimeLoadPreviously(time);
				}	
				target.setIdRoute(route.getIdRoute());
				target.setStatusRoute("0");
				target.setStatusStock("0");
				target.setComments("international");
				target.setDateLoadPreviously(dateStart);
				Route routeCopyOld = routeService.getRouteById(idRouteCopy);
				target.setStepCost(routeCopyOld.getStepCost());			
				if(routeCopyOld.getRouteDirection().contains("[")) { //===================================================================
					// обработка старых маршрутов, где имелась дата. 
					// в этом случае отсекае всё, начиная с даты 
					target.setRouteDirection(routeCopyOld.getRouteDirection().split("\\[")[0]+" N"+route.getIdRoute());
				}else {
					target.setRouteDirection(routeCopyOld.getRouteDirection().split("N")[0]+"N"+route.getIdRoute());
				}
				Set<RouteHasShop> routeHasShops = new HashSet<>();
				routeHasShops.addAll(routeCopyOld.getRoteHasShop());
				routeHasShops.stream().forEach(s-> s.setIdRouteHasShop(null));
				routeHasShops.stream().forEach(s-> s.setVolume(volume));
				routeHasShops.stream().forEach(s-> s.setIdRouteHasShop(null));
				routeHasShops.stream().forEach(s-> s.setRoute(target));
				routeHasShops.stream().forEach(s-> routeHasShopService.saveOrUpdateRouteHasShop(s));
				routeService.saveOrUpdateRoute(target);
				return "redirect:/main/logistics/international";
			}else {
				for(int i = 1; i<=count; i++) {
					Route route = poiExcel.creatureEmptyRoute(dateStart);
					if (time != null) {
						target.setTimeLoadPreviously(time);
					}	
					Route routeCopyOld = routeService.getRouteById(idRouteCopy);
					target.setIdRoute(route.getIdRoute());
					target.setStatusRoute("0");
					target.setStatusStock("0");
					target.setComments("international");
					target.setDateLoadPreviously(dateStart);
					target.setStepCost(routeCopyOld.getStepCost());					
					if(routeCopyOld.getRouteDirection().contains("[")) { //===================================================================
						// обработка старых маршрутов, где имелась дата. 
						// в этом случае отсекае всё, начиная с даты 
						target.setRouteDirection(routeCopyOld.getRouteDirection().split("\\[")[0]+" N"+route.getIdRoute());
					}else {
						target.setRouteDirection(routeCopyOld.getRouteDirection().split("N")[0]+"N"+route.getIdRoute());
					}
					Set<RouteHasShop> routeHasShops = new HashSet<>();
					routeHasShops.addAll(routeCopyOld.getRoteHasShop());
					routeHasShops.stream().forEach(s-> s.setIdRouteHasShop(null));
					routeHasShops.stream().forEach(s-> s.setVolume(volume));
					routeHasShops.stream().forEach(s-> s.setRoute(target));
					routeHasShops.stream().forEach(s-> routeHasShopService.saveOrUpdateRouteHasShop(s));
					routeService.saveOrUpdateRoute(target);
				}
				return "redirect:/main/logistics/international";
			}
			
		}else {	
			if(count == 1) {	
				Route route = poiExcel.creatureEmptyRoute(dateStart);
				Route sessionRoute = (Route) session.getAttribute(user.getIdUser()+"route");
				if (time != null) {
					route.setTimeLoadPreviously(time);
				}		
				route.setStatusRoute("0");
				route.setStatusStock("0");
				route.setComments("international");	
				route.setWay(target.getWay());
				route.setOptimalCost(target.getOptimalCost());
//				System.out.println(route.getOptimalCost()); //===================================================================
				route.setUserComments(target.getUserComments());
				route.setTemperature(target.getTemperature());
				route.setTotalLoadPall(target.getTotalLoadPall());
				route.setTotalCargoWeight(target.getTotalCargoWeight());
				route.setCustomer(target.getCustomer());
				route.setRouteDirection(target.getRouteDirection() + " N"+route.getIdRoute());
				route.setStartPrice(target.getStartPrice());
				route.setTypeTrailer(target.getTypeTrailer());
				sessionRoute.getRoteHasShop().stream().forEach(s-> s.setRoute(route));
				sessionRoute.getRoteHasShop().stream().forEach(s-> routeHasShopService.saveOrUpdateRouteHasShop(s));				
				return "redirect:/main/logistics/international";
			}else {
				for(int i = 1; i<=count; i++) {
					Route routeI = poiExcel.creatureEmptyRoute(dateStart);
					Route sessionRoute = (Route) session.getAttribute(user.getIdUser()+"route");
					if (time != null) {
						routeI.setTimeLoadPreviously(time);
					}	
					routeI.setStatusRoute("0");
					routeI.setStatusStock("0");
					routeI.setComments("international");	
					routeI.setWay(target.getWay());
					routeI.setOptimalCost(target.getOptimalCost());
					routeI.setUserComments(target.getUserComments());
					routeI.setTemperature(target.getTemperature());
					routeI.setTotalLoadPall(target.getTotalLoadPall());
					routeI.setTotalCargoWeight(target.getTotalCargoWeight());
					routeI.setCustomer(target.getCustomer());
					System.out.println(routeI.getOptimalCost()); //===================================================================
					routeI.setRouteDirection(target.getRouteDirection()+" N"+routeI.getIdRoute());
					routeI.setStartPrice(target.getStartPrice());
					routeI.setTypeTrailer(target.getTypeTrailer());
					sessionRoute.getRoteHasShop().forEach(s->s.setIdRouteHasShop(null));
					sessionRoute.getRoteHasShop().stream().forEach(s-> s.setRoute(routeI));
					sessionRoute.getRoteHasShop().stream().forEach(s-> routeHasShopService.saveOrUpdateRouteHasShop(s));
				}
				return "redirect:/main/logistics/international";
			}			
		}
	}
	
	@GetMapping("/main/logistics/international/editRoute")
	public String internationalEditRouteGet(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		model.addAttribute("route", route);
		request.setAttribute("edit", true);
		return "routeForm";
	}
	@PostMapping("/main/logistics/international/editRoute")//редактор маршрута
	public String internationalEditRoutePost(Model model, HttpServletRequest request,
			@ModelAttribute("route") Route route,
			@RequestParam(value = "edit", required = false) String edit,
			@RequestParam(value = "delite", required = false) String delite,
			@RequestParam(value = "offCar", required = false) String offCar,
			@RequestParam(value = "date", required = false) String dateStart,
			@RequestParam(value = "timeOfLoad", required = false) LocalTime time,
			@RequestParam(value = "dateUnloadPreviouslyStock", required = false) String dateUnload) {
		if(edit != null) {
			Route routeTarget = routeService.getRouteById(route.getIdRoute());
			if(!dateStart.isEmpty()) {
				routeTarget.setDateLoadPreviously(dateStart);
			}
			if(dateUnload != null || !dateUnload.isEmpty()) {
				routeTarget.setDateUnloadPreviouslyStock(dateUnload);
			}
			routeTarget.setUserComments(route.getUserComments());
			routeTarget.setTimeUnloadPreviouslyStock(route.getTimeUnloadPreviouslyStock()!=null ? route.getTimeUnloadPreviouslyStock().toString() : null);
			routeService.saveOrUpdateRoute(routeTarget);
//			routeService.updateRouteInBase(route.getIdRoute(), dateStart);
		}
		if(offCar != null) {
			routeService.updateDropRouteDateOfCarrier(route.getIdRoute());
		}
		if(delite != null) {			
			routeService.deleteRouteByIdFromMeneger(route.getIdRoute());
			Set<Order> orders = route.getOrders();			
			if(orders != null && orders.size() != 0) {
				orders.forEach(o->{
					o.setStatus(20);
					orderService.updateOrderFromStatus(o);
				});				
			}			
			Route routeTarget = routeService.getRouteById(route.getIdRoute());
			routeTarget.setOrders(null);
			routeService.saveOrUpdateRoute(routeTarget);			
		}
//		if (edit != null) {
//			Route oldRoute = routeService.getRouteById(route.getIdRoute());			
//			route.setUser(oldRoute.getUser());
//			route.setTruck(oldRoute.getTruck());
//			route.setDriver(oldRoute.getDriver());
//			route.setTimeLoadPreviously(time);
//			route.setDateLoadPreviously(dateStart);
//			route.setStartCurrency(oldRoute.getStartCurrency());
//			routeService.saveOrUpdateRoute(route);
//		}else if(delite != null) {
//			routeService.deleteRouteByIdFromMeneger(route.getIdRoute());
//		}
		
		return "redirect:/main/logistics/international";
	}
	
	@GetMapping("/main/logistics/international/tenderOffer")
	public String internationalTenderOfferGet(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		request.setAttribute("idRoute", idRoute);
		request.setAttribute("routeDirection", route.getRouteDirection());
		return "tenderOffer";
	}
	
	//лист тендеров для подтверждения админа
	@GetMapping("/main/admin/international/tenderOffer")
	public String internationalTenderOfferForAdminGet(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		request.setAttribute("idRoute", idRoute);
		request.setAttribute("routeDirection", route.getRouteDirection());
		request.setAttribute("isAdmin", true);
		request.setAttribute("loginUser", route.getUser().getLogin());
		request.setAttribute("thisUser", getThisUser().getLogin());
		
		return "tenderOffer";
	}
	
	@GetMapping("/main/logistics/internationalNew/tenderOffer")
	public String internationalTenderOfferGetNew(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		request.setAttribute("idRoute", idRoute);
		request.setAttribute("routeDirection", route.getRouteDirection());
		return "tenderOffer";
	}
	
	//лист тендеров для подтверждения админа
	@GetMapping("/main/admin/internationalNew/tenderOffer")
	public String internationalTenderOfferForAdminGetNew(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		request.setAttribute("idRoute", idRoute);
		request.setAttribute("routeDirection", route.getRouteDirection());
		request.setAttribute("isAdmin", true);
		request.setAttribute("loginUser", route.getUser().getLogin());
		request.setAttribute("thisUser", getThisUser().getLogin());
		
		return "tenderOffer";
	}
	
	@GetMapping("/main/carrier/tender/tenderOffer")
	public String carrierTenderOfferGet(Model model, HttpServletRequest request,
			@RequestParam(value = "notagree", required = false) String notagree) {
		return "redirect:/main/carrier/tender";		
	}
	
	@GetMapping("/main/logistics/international/routeShow")
	public String internationalRouteShowGet(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		request.setAttribute("route", route);		
		//блок определения оптимальной цены
		//получаем маршруты по названию
		List<Route> routes = routeService.getRouteListAsRouteDirection(route);
		List<Route> step1 = new ArrayList<>();
		routes.stream().filter(r-> r.getFinishPrice() != null && r.getStartCurrency() != null && r.getStartCurrency().equals("BYN")).forEach(r->step1.add(r)); // пока что ижем только в белках		
		request.setAttribute("listCosts", step1);			
		//конец блока
				
		return "routeForm";
	}
	
	@GetMapping("/main/logistics/international/routeEnd")
	public String internationalRouteEndGet(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		if (Integer.parseInt(route.getStatusRoute())>=5) {
			return "redirect:/main/logistics/international";
		}
		route.setStatusRoute("6");
		Truck truck = route.getTruck();
		User driver = route.getDriver();
		if(driver != null && truck != null) {
			int statusNew = Integer.parseInt(driver.getStatus())-1;
			driver.setStatus(statusNew+"");
			int statusTrNew = Integer.parseInt(truck.getStatus())-1;
			truck.setStatus(statusTrNew+"");
			userService.saveOrUpdateUser(driver, 0);
			truckService.saveOrUpdateTruck(truck);			
		}
		routeService.saveOrUpdateRoute(route);
		return "redirect:/main/logistics/international";
	}
	
	
	@RequestMapping("/main/message")
	public String chat(HttpServletRequest request, Model driver, HttpSession session) {
		request.setAttribute("sessionCheck", session.getAttribute("sessionCheck"));
		return "chat";
	}
	
	//подтверждение цены маршрута
	@RequestMapping("/main/logistics/international/confrom")
	public String confromCost(Model model, HttpServletRequest request,
			@RequestParam("login") String login,
			@RequestParam("cost") Integer cost,
			@RequestParam("idRoute") Integer idRoute,
			@RequestParam("currency") String currency,
			@RequestParam(name = "status", required = false) String status) {
		User user = userService.getUserByLogin(login);
		if (status == null) {
			status = "4";
			routeService.updateRouteInBase(idRoute, cost, currency, user, status);
		}else {
			routeService.updateRouteInBase(idRoute, cost, currency, user, status);
			Route route = routeService.getRouteById(idRoute);
			User userHasCarrier = userService.getUserByLogin(login);
			java.util.Date t2 = new java.util.Date();
			String message = "На маршрут "+route.getRouteDirection()+" принят единственный заявившийся перевозчик: " + userHasCarrier.getCompanyName() 
					+ ". Заявленная стоимость перевозки составляет "+ route.getFinishPrice() + " "+ route.getStartCurrency() + ". \nОптимальная стоимость составляет " + route.getOptimalCost()+" BYN";
			mailService.sendSimpleEmail(request, "Подтверждение единственного перевозчика", message, "YakubovE@dobronom.by");
		}
		List<Message> messages = new ArrayList<Message>();		
		chatEnpoint.internationalMessegeList.stream()
			.filter(mes->mes.getIdRoute().equals(idRoute.toString()))
			.forEach(mes-> messages.add(mes));
		messages.stream().forEach(mes->{
			chatEnpoint.internationalMessegeList.remove(mes);
			messageService.saveOrUpdateMessage(mes);
		});
		//аварийная сериализация кеша, на случай если сервер упадёт
		try {
			FileOutputStream fos =
                     new FileOutputStream(path + "resources/others/hashmap.ser");
                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                  oos.writeObject(chatEnpoint.internationalMessegeList);
                  oos.close();
                  fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//меняем статус у Order если имеется
		Route routeTarget = routeService.getRouteById(idRoute);
		Set <Order> orders = routeTarget.getOrders();
		if(orders != null && orders.size() != 0) {
			orders.forEach(o->{
				o.setStatus(60);
				o.setChangeStatus(o.getChangeStatus() + "\nМаршрут "+idRoute+" выйгран  " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:MM:ss")));
				orderService.updateOrder(o);
//				orderService.updateOrderFromStatus(o);
			});			
		}
		
		Set<Message> messagesJustDelete = new HashSet<Message>();
		mainChat.messegeList.stream()
			.filter(m-> m.getIdRoute() != null && m.getIdRoute().equals(idRoute) && m.getToUser().equals("international"))
			.forEach(m-> messagesJustDelete.add(m));
		messagesJustDelete.stream().forEach(m->mainChat.messegeList.remove(m));
		return "redirect:/main/logistics/international";
	}
	
	//подтверждение цены маршрута new
		@RequestMapping("/main/logistics/internationalNew/confrom")
		public String confromCostNew(Model model, HttpServletRequest request,
				@RequestParam("login") String login,
				@RequestParam("cost") Integer cost,
				@RequestParam("idRoute") Integer idRoute,
				@RequestParam("currency") String currency,
				@RequestParam(name = "status", required = false) String status) {
			User user = userService.getUserByLogin(login);
			if (status == null) {
				status = "4";
				routeService.updateRouteInBase(idRoute, cost, currency, user, status);
			}else {
				routeService.updateRouteInBase(idRoute, cost, currency, user, status);
				Route route = routeService.getRouteById(idRoute);
				User userHasCarrier = userService.getUserByLogin(login);
				java.util.Date t2 = new java.util.Date();
				String message = "На маршрут "+route.getRouteDirection()+" принят единственный заявившийся перевозчик: " + userHasCarrier.getCompanyName() 
						+ ". Заявленная стоимость перевозки составляет "+ route.getFinishPrice() + " "+ route.getStartCurrency() + ". \nОптимальная стоимость составляет " + route.getOptimalCost()+" BYN";
				mailService.sendSimpleEmail(request, "Подтверждение единственного перевозчика", message, "YakubovE@dobronom.by");
			}
			List<Message> messages = new ArrayList<Message>();		
			chatEnpoint.internationalMessegeList.stream()
				.filter(mes->mes.getIdRoute().equals(idRoute.toString()))
				.forEach(mes-> messages.add(mes));
			messages.stream().forEach(mes->{
				chatEnpoint.internationalMessegeList.remove(mes);
				messageService.saveOrUpdateMessage(mes);
			});
			//аварийная сериализация кеша, на случай если сервер упадёт
			try {
				FileOutputStream fos =
	                     new FileOutputStream(path + "resources/others/hashmap.ser");
	                  ObjectOutputStream oos = new ObjectOutputStream(fos);
	                  oos.writeObject(chatEnpoint.internationalMessegeList);
	                  oos.close();
	                  fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//меняем статус у Order если имеется
			Route routeTarget = routeService.getRouteById(idRoute);
			Set <Order> orders = routeTarget.getOrders();
			if(orders != null && orders.size() != 0) {
				orders.forEach(o->{
					o.setStatus(60);
					o.setChangeStatus(o.getChangeStatus() + "\nМаршрут "+idRoute+" выйгран  " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:MM:ss")));
					orderService.updateOrder(o);
//					orderService.updateOrderFromStatus(o);
				});			
			}
			
			Set<Message> messagesJustDelete = new HashSet<Message>();
			mainChat.messegeList.stream()
				.filter(m-> m.getIdRoute() != null && m.getIdRoute().equals(idRoute) && m.getToUser().equals("international"))
				.forEach(m-> messagesJustDelete.add(m));
			messagesJustDelete.stream().forEach(m->mainChat.messegeList.remove(m));
			return "redirect:/main/logistics/internationalNew";
		}
	
	//подтверждение цены маршрута ДЛЯ АДМИНА
		@RequestMapping("/main/admin/international/confrom")
		public String confromCostAdmin(Model model,
				@RequestParam("idRoute") Integer idRoute) {			
			routeService.updateRouteInBase(idRoute, "4");
			Route routeTarget = routeService.getRouteById(idRoute);
			Set <Order> orders = routeTarget.getOrders();
			if(orders != null && orders.size() != 0) {
				orders.forEach(o->{
					o.setStatus(60);
					orderService.updateOrderFromStatus(o);
				});			
			}
			return "redirect:/main/logistics/international";
		}
		
		@RequestMapping("/main/admin/internationalNew/confrom")
		public String confromCostAdminNew(Model model,
				@RequestParam("idRoute") Integer idRoute) {			
			routeService.updateRouteInBase(idRoute, "4");
			Route routeTarget = routeService.getRouteById(idRoute);
			Set <Order> orders = routeTarget.getOrders();
			if(orders != null && orders.size() != 0) {
				orders.forEach(o->{
					o.setStatus(60);
					orderService.updateOrderFromStatus(o);
				});			
			}
			return "redirect:/main/logistics/internationalNew";
		}
	
	@GetMapping("/main/logistics/international/disposition")
	public String dispositionGet(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {	
		List<Route> routes = new ArrayList<Route>();
		routeService.getRouteListAsStatus("4", "4").stream()
			.filter(r-> r.getComments().equals("international"))
			.forEach(r-> routes.add(r));
		request.setAttribute("routes", routes);
		return "disposition";
	}
	
	
	@GetMapping("/main/admin/routePattern")
	public String routePatternGet(HttpServletRequest request) {
		request.setAttribute("routes", routeService.getRouteListAsComment("pattern"));
		return "routePatternList";
	}
	@GetMapping("/main/admin/routePattern/add")
	public String routePatternAddGet(HttpServletRequest request) {
		request.setAttribute("flag", "flag");
		return "internationalForm";
	}
	
	@RequestMapping("/main/admin/routePattern/addRoute")
	public String patternAddRouteGet(Model model, HttpServletRequest request,
			@RequestParam(value = "routeDirection", required = false) String routeDirection,
			@RequestParam(value = "idRoute", required = false) Integer idRoute, HttpSession session) {
		User user = getThisUser();
		if (routeDirection != null) {
			Route route = (Route) session.getAttribute(user.getIdUser()+"route");
			route.setRouteDirection(routeDirection);
			model.addAttribute("route", route);
			RouteHasShop routeHasShop = route.getRoteHasShop().stream().findFirst().get();
			request.setAttribute("pall", routeHasShop.getPall());
			request.setAttribute("weight", routeHasShop.getWeight());
		}else if (idRoute != null) {
			Route route = routeService.getRouteById(idRoute);
			route.setIdRoute(null);
			route.setTimeLoadPreviously(null);
			model.addAttribute("route", route);
			RouteHasShop routeHasShop = route.getRoteHasShop().stream().findFirst().get();
			request.setAttribute("pall", routeHasShop.getPall());
			request.setAttribute("weight", routeHasShop.getWeight());
		}
		request.setAttribute("flag", "flag");
		return "routeForm";
	}
	
	@PostMapping("/main/admin/routePattern/addRoute")
	public String patternAddRoutePost(Model model,
			@ModelAttribute("route") Route target,
			@RequestParam(value = "date", required = false) Date dateStart,
			@RequestParam(value = "timeOfLoad", required = false) LocalTime time, HttpSession session) {
		User user = getThisUser();
		Route route = poiExcel.creatureEmptyRoute(dateStart);
		Route sessionRoute = (Route) session.getAttribute(user.getIdUser()+"route");
		if (time != null) {
			route.setTimeLoadPreviously(time);
		}		
		route.setStatusRoute("0");
		route.setStatusStock("0");
		route.setComments("pattern");
		route.setUserComments(target.getUserComments());
		route.setOptimalCost(target.getOptimalCost());
		route.setTemperature(target.getTemperature());
		route.setTotalLoadPall(target.getTotalLoadPall());
		route.setTotalCargoWeight(target.getTotalCargoWeight());
		route.setStepCost(target.getStepCost());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
		route.setRouteDirection(target.getRouteDirection());
		route.setStartPrice(target.getStartPrice());
		route.setTypeTrailer(target.getTypeTrailer());
		sessionRoute.getRoteHasShop().stream().forEach(s-> s.setRoute(route));
		sessionRoute.getRoteHasShop().stream().forEach(s-> routeHasShopService.saveOrUpdateRouteHasShop(s));
			
		return "redirect:/main/admin/routePattern";
	}
	
	@RequestMapping("/main/admin/routePattern/showRoute")
	public String showPatternRouteForAdmin(Model model, HttpServletRequest request,
			@RequestParam("idRoute") Integer idRoute) {
		request.setAttribute("route", routeService.getRouteById(idRoute));
		return "routeForm";
	}
	
	@RequestMapping("/main/admin/routePattern/delete")
	public String patternDeleteRoute(Model model, HttpServletRequest request,
			@RequestParam("idRoute") Integer idRoute) {
//		routeService.deleteRouteById(idRoute);
		System.out.println("временно заблокированно");
		return "redirect:/main/admin/routePattern";
	}
	
	@RequestMapping("/main/userpage")
	public String getUserPage(Model model, HttpServletRequest request) {
		if (getThisUser().getDepartment() != null && getThisUser().getDepartment().equals("Директор")) {
			request.setAttribute("department", true);
		}else {
			request.setAttribute("department", false);
		}
		return "userPage";
	}
	@RequestMapping("/main/userpage/userlist")
	public String getUserListHasCarrier(Model model, HttpServletRequest request) {
		User boss = getThisUser();
		List <User> workers = new ArrayList<User>();
		userService.getCarrierList().stream()
			.filter(u-> u.getNumYNP().equals(boss.getNumYNP()) && !u.getDepartment().equals("Директор"))
			.forEach(u-> workers.add(u));
		request.setAttribute("userlist", workers);
		request.setAttribute("department", true);
		return "userList";
	}
	
	@GetMapping("/main/userpage/userlist/add")
	public String addWorkerCarrierGet(Model model, HttpServletRequest request) {
		User user = new User();
		model.addAttribute("user", user);
		request.setAttribute("department", true);
		return "registrationWorker";
	}
	
	@PostMapping("/main/userpage/userlist/save")
	public String addWorkerCarrierPost(Model model, HttpServletRequest request,
			@ModelAttribute ("user") User user) {
		if(userService.getUserByLogin(user.getLogin())!=null) {
			model.addAttribute("user", user);
			request.setAttribute("errorMessage", "Юзер с таким логином уже зарегистрирован");
			request.setAttribute("department", true);
			return "registrationWorker";
		}		
		userService.saveOrUpdateUser(user, 9);
		return "redirect:/main/userpage/userlist";
	}
	
	@GetMapping("/main/userpage/edit")
	public String editUserForYourself(Model model) {
		User user = getThisUser();
		user.setPassword(null);
		model.addAttribute("user", user);
		Set<Role> roles = user.getRoles();
		Role role = roles.stream().findFirst().get();
		model.addAttribute("role", role);
		return "editUser";
		}
	
	@PostMapping("/main/userpage/edit")
	public String editUserForYourselfPost(Model model,
			@ModelAttribute ("user") User user,
			@RequestParam(value = "numContract", required = false) String numContract,
			@RequestParam(value = "dateContract", required = false) String dateContract) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		User target = userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
		if (!user.getPassword().equals("")) {
			target.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		}
		target.setNumContract(numContract + " от " + dateContract);
		target.setLogin(user.getLogin());
		target.setName(user.getName());
		target.setSurname(user.getSurname());
		target.setPatronymic(user.getPatronymic());
		target.seteMail(user.geteMail());
		target.setTelephone(user.getTelephone());
		target.setAddress(user.getAddress());
		target.setRequisites(user.getRequisites());
		target.setDirector(user.getDirector());
		userService.saveOrUpdateUser(target, 0);
		return "redirect:/main/userpage";
		}
	@GetMapping("/main/admin/dashboard")
	public String dashboardGet(Model model,HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam( value = "dateStart", required = false) Date dateStart,
			@RequestParam( value = "dateFinish", required = false) Date dateFinish) {
		if(dateStart == null || dateFinish == null) {
			if (session.getAttribute("dateStart") !=null || session.getAttribute("dateFinish")!=null) {
				dateStart = (Date) session.getAttribute("dateStart");
				dateFinish = (Date) session.getAttribute("dateFinish");
				request.setAttribute("dateNow", session.getAttribute("dateStart"));
				request.setAttribute("dateTomorrow", session.getAttribute("dateFinish"));
			}else {
				getTimeNow(request);
				getTimeNowPlusDay(request);
				dateStart = Date.valueOf(LocalDate.now());
				dateFinish = Date.valueOf(LocalDate.now().plusDays(5));
				session.setAttribute("dateStart", dateStart);
				session.setAttribute("dateFinish", dateFinish);
			}			
		}else {
			request.setAttribute("dateNow", dateStart);
			session.setAttribute("dateStart", dateStart);
			request.setAttribute("dateTomorrow", dateFinish);
			session.setAttribute("dateFinish", dateFinish);			
		}
		int carrierTotal = 0;
		int carrierInternational = 0;
		int carrierInternationalNew = 0;
		int carrierRegionalNew = 0;
		
		request.setAttribute("carrierTotal", carrierTotal);
		request.setAttribute("carrierInternational", carrierInternational);
		request.setAttribute("carrierInternationalNew", carrierInternationalNew);
		request.setAttribute("carrierRegionalNew", carrierRegionalNew);
		Set<User> usersHasRoute = new HashSet<User>();
		
		List<Route> routes = routeService.getRouteListAsDate(dateStart, dateFinish);
		int routeTotal = routes.size();
		int routeCollInternational = 0;
		double costTotalInternational = 0;
		double economyInternational = 0;
		int routeNotOptimalCost = 0;
		for (Route route : routes) {
			if (route.getFinishPrice() == null || route.getStatusRoute().equals("9") || route.getStatusRoute().equals("1")) {
				continue;
			}
			usersHasRoute.add(route.getUser());
			if(route.getStartCurrency() != null && route.getStartCurrency().equals("BYN") && route.getComments()!= null && route.getComments().equals("international")) {
				routeCollInternational++;
				costTotalInternational=costTotalInternational+route.getFinishPrice();
				if(route.getStartPrice() != null) {
					economyInternational = economyInternational+route.getStartPrice()-route.getFinishPrice();
					if(route.getStartPrice()-route.getFinishPrice() <0) {
						routeNotOptimalCost++;
					}
				}else {
					if(route.getOptimalCost() != null) {
						economyInternational = economyInternational+Double.parseDouble(route.getOptimalCost())-route.getFinishPrice();
						if(Double.parseDouble(route.getOptimalCost())-route.getFinishPrice() <0) {
							routeNotOptimalCost++;
						}
					}
					
				}
			}else if(route.getStartCurrency() != null && route.getStartCurrency().equals("USD") && route.getComments()!= null && route.getComments().equals("international")) {
				routeCollInternational++;
				Currency USD = currencyService.getCurrencyMap().get("USD");
				Double BYNparse = route.getFinishPrice()*USD.getCur_OfficialRate()/USD.getCur_Scale();
				costTotalInternational=costTotalInternational+BYNparse;
				if(route.getStartPrice() != null) {
					economyInternational = economyInternational+route.getStartPrice()-BYNparse;
					if(route.getStartPrice()-BYNparse <0) {
						routeNotOptimalCost++;
					}
				}else {
					economyInternational = economyInternational+Double.parseDouble(route.getOptimalCost())-BYNparse;
					if(Double.parseDouble(route.getOptimalCost())-BYNparse <0) {
						routeNotOptimalCost++;
					}
				}
				
			}else if(route.getStartCurrency() != null && route.getStartCurrency().equals("RUB") && route.getComments()!= null && route.getComments().equals("international")) {
				routeCollInternational++;
				Currency RUB = currencyService.getCurrencyMap().get("RUB");
				Double BYNparse = route.getFinishPrice()*RUB.getCur_OfficialRate()/RUB.getCur_Scale();
				costTotalInternational=costTotalInternational+BYNparse;
				if(route.getStartPrice() != null) {
					economyInternational = economyInternational+route.getStartPrice()-BYNparse;
					if(route.getStartPrice()-BYNparse <0) {
						routeNotOptimalCost++;
					}
				}else {
					economyInternational = economyInternational+Double.parseDouble(route.getOptimalCost())-BYNparse;
					if(Double.parseDouble(route.getOptimalCost())-BYNparse <0) {
						routeNotOptimalCost++;
					}
				}
				
			}else if(route.getStartCurrency() != null && route.getStartCurrency().equals("EUR") && route.getComments()!= null && route.getComments().equals("international")) {
				routeCollInternational++;
				Currency EUR = currencyService.getCurrencyMap().get("EUR");
				Double BYNparse = route.getFinishPrice()*EUR.getCur_OfficialRate()/EUR.getCur_Scale();
				costTotalInternational=costTotalInternational+BYNparse;
				if(route.getStartPrice() != null) {
					economyInternational = economyInternational+route.getStartPrice()-BYNparse;
					if(route.getStartPrice()-BYNparse <0) {
						routeNotOptimalCost++;
					}
				}else {
					economyInternational = economyInternational+Double.parseDouble(route.getOptimalCost())-BYNparse;
					if(Double.parseDouble(route.getOptimalCost())-BYNparse <0) {
						routeNotOptimalCost++;
					}
				}
				
			}else if(route.getStartCurrency() != null && route.getStartCurrency().equals("KZT") && route.getComments()!= null && route.getComments().equals("international")) {
				routeCollInternational++;
				Currency KZT = currencyService.getCurrencyMap().get("KZT");
				Double BYNparse = route.getFinishPrice()*KZT.getCur_OfficialRate()/KZT.getCur_Scale();
				costTotalInternational=costTotalInternational+BYNparse;
				if(route.getStartPrice() != null) {
					economyInternational = economyInternational+route.getStartPrice()-BYNparse;
					if(route.getStartPrice()-BYNparse <0) {
						routeNotOptimalCost++;
					}
				}else {
					economyInternational = economyInternational+Double.parseDouble(route.getOptimalCost())-BYNparse;
					if(Double.parseDouble(route.getOptimalCost())-BYNparse <0) {
						routeNotOptimalCost++;
					}
				}
				
			}
		}
		Integer activity = usersHasRoute.size()*100/carrierInternational;
		request.setAttribute("economyInternational", economyInternational);
		request.setAttribute("routeTotal", routeTotal);
		request.setAttribute("costTotalInternational", costTotalInternational);
		request.setAttribute("routeNotOptimalCost", routeNotOptimalCost);
		request.setAttribute("routeCollInternational", routeCollInternational);
		request.setAttribute("activity", activity);
		return "dashboard";
		}
	
	
	@RequestMapping("/main/test")
	public String test(HttpServletRequest request) {
		User user = userService.getUserById(2);
		System.out.println(user.toString());
		System.out.println(user.getRoles());
		
		return "map";
	}
	
	@RequestMapping("/main/test2")
	public String test2() {
		distances.put("1", "1488");
		return "redirect:/main";
	}
	@RequestMapping("/main/test3")
	public String test3() {
		distances.clear();
		distances.put("1", "9874987");
		return "redirect:/main";
	}
	
	
	@RequestMapping("/main/logistics/sendEmail")
	public String sendEmailFile(HttpServletRequest request,
			@RequestParam(name = "subject", required = false) String subject,
			@RequestParam(name = "text", required = false) String text,
			@RequestParam(name = "file", required = false) MultipartFile file) {
		mailService.sendEmailWhithFile(request, subject, text, file);		
		return "logistics";
	}
	
	@RequestMapping("/main/js")
	public String javaScript(Model model, HttpSession session, HttpServletRequest request) {	
		return "jsPage";
	}
	
	@GetMapping("/main/admin/extracontrol")
	public String getExtracontrol() {			
		return "extracontrol";
	}
	
//	@PostMapping("/main/admin/extracontrol")
//	public String addCostProduct(@RequestParam(name = "file", required = false) MultipartFile file, String message)
//			throws ServiceException {
//		File target = poiExcel.getFileByMultipart(file);
//		poiExcel.addCostProductConst(target);
//		return "redirect:/main/admin/extracontrol";
//	}
//	
//	@PostMapping("/main/admin/extracontrol2")
//	public String addCostProductRetail(@RequestParam(name = "file", required = false) MultipartFile file, String message)
//			throws ServiceException {
//		File target = poiExcel.getFileByMultipart(file);
//		poiExcel.addCostProductRetailConst(target);
//		return "redirect:/main/admin/extracontrol";
//	}
//	
//	@PostMapping("/main/admin/extracontrol3")
//	public String addTest(@RequestParam(name = "file", required = false) MultipartFile file, String message)
//			throws ServiceException {
//		File target = poiExcel.getFileByMultipart(file);
//		poiExcel.testCollFile(target);
//		return "redirect:/main/admin/extracontrol";
//	}
	
	@GetMapping("/main/carrier/transportation/archive")
	public String getArchive() {	
		
		return "archive";
	}
	
	private void getTimeNow(HttpServletRequest request) {
		LocalDate date = LocalDate.now();
		request.setAttribute("dateNow", date);
	}

	private void getTimeNowPlusDay(HttpServletRequest request) {
		LocalDate date = LocalDate.now().plusDays(1);
		request.setAttribute("dateTomorrow", date);
	}
	
	private String getCompanyName() {
		String companyName = getThisUser().getCompanyName();
		return companyName;
		
	}
	
	private User getThisUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByLogin(name);
		return user;
	}
	
	private void addFeddbackFromShop(User shopDirect, User driver, Feedback feedback, String radio) {
		feedback.setDate(LocalDate.now());
		feedback.setFrom(shopDirect);
		feedback.setShop(shopDirect.getShop());
		feedback.setUser(driver);
		String finalMessag = "*"+radio+".\n Отзыв от: "+ shopDirect.getDepartment()+" "+shopDirect.getSurname()+" "+shopDirect.getName()+", магазин №"+shopDirect.getShop().getNumshop()+" к водителю "+
				driver.getCompanyName()+" "+driver.getSurname()+" "+driver.getName()+". \n Текс отзыва: "+ feedback.getMessage()+
				"\n\n"+"Составлен: "+LocalDate.now().toString()+".\n"+"idRouteHasShop="+feedback.getIdRouteHasShop();
		feedback.setMessage(finalMessag);
		feedbackService.saveOrUpdateFeedback(feedback);
	}
	//расчёт ставок ждя каждого маршрута
	private Route addCostForRoute(Route route) {
		Set <RouteHasShop> routeHasShops = new HashSet<RouteHasShop>();
				for (RouteHasShop routeHasShop : route.getRoteHasShop()) {
					routeHasShops.add(routeHasShop);
				}
		double km = 0.0;
		for(int i = 0; i<=50; i++) {
			RouteHasShop target1 = routeHasShops.stream().findFirst().get();
			routeHasShops.remove(target1);
			if(!routeHasShops.isEmpty()) {
				String key = target1.getShop().getNumshop()+"-"+routeHasShops.stream().findFirst().get().getShop().getNumshop();
				km = km + Double.parseDouble(distances.get(key).replace(',', '.'));
			}else{
				break;
			}
			
		}	
		Map <String,String>costMap = new HashMap<String, String>();
		List <Rates> rates = new ArrayList<Rates>();
		if(route.getTemperature() == null || route.getTemperature().equals("")) {
			for (Rates rate : ratesService.getRatesList()) {
				if (rate.getType().equals("изотерма")) {
					rates.add(rate);
				}
			}
		}else {
			for (Rates rate : ratesService.getRatesList()) {
				if (rate.getType().equals("рефрижератор")) {
					rates.add(rate);
				}
			}
		}
		int i = 1;		
		for (Rates rate : rates) {
			if(Double.parseDouble(rate.getPall()) >= Double.parseDouble(route.getTotalLoadPall()) && Integer.parseInt(rate.getCaste()) == i) {
				if (Double.parseDouble(rate.getWeight())>=Double.parseDouble(route.getTotalCargoWeight())) {
					double tariff = 0.0;
					if (km<400) {
						tariff = Double.parseDouble(rate.getBefore400());
					}else {
						tariff = Double.parseDouble(rate.getAfter400());
					}
					double finalCost = km*tariff;
					costMap.put(i+"", Math.ceil(finalCost)+"");
					System.out.println(i+" = "+Math.ceil(finalCost)+" для маршрута " + route.getRouteDirection()+", где общий киллометраж - " + Math.ceil(km));
					i++;					
					continue;
				}
			}
		}
		route.setCost(costMap);
		return route;
	}	
}
