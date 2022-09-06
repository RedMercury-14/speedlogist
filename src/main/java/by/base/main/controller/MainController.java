package by.base.main.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import by.base.main.controller.ajax.MainRestController;
import by.base.main.model.Feedback;
import by.base.main.model.Message;
import by.base.main.model.Rates;
import by.base.main.model.Role;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.Shop;
import by.base.main.model.Tender;
import by.base.main.model.Truck;
import by.base.main.model.User;
import by.base.main.service.FeedbackService;
import by.base.main.service.MessageService;
import by.base.main.service.RatesService;
import by.base.main.service.RouteHasShopService;
import by.base.main.service.RouteService;
import by.base.main.service.ServiceException;
import by.base.main.service.ShopService;
import by.base.main.service.TruckService;
import by.base.main.service.UserService;
import by.base.main.service.util.POIExcel;
import by.base.main.service.util.TenderTimer;
import by.base.main.service.util.TimerList;
import by.base.main.util.ChatEnpoint;

@Controller
@RequestMapping("/")
public class MainController {

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
	private MessageService messageService;
	
	@Autowired
	private MainRestController mainRestController;
	
	public static final Map<String,String> distances = new HashMap<String, String>();

	@GetMapping("/main")
	public String homePage(Model model, HttpSession session) {
		if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
			return "main";
		}
		User user = userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
		if (session.getAttribute("check") == null || user.getCheck() == null || user.getCheck().equals("international") || session.getAttribute("check").equals("international")) {
			return "main";
		}else {
			//кладу из БД в сессию статус check
			if((session.getAttribute("check")==null && user.getCheck() != null)) {
				User carrier = userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
				session.setAttribute("check", carrier.getCheck());
				return "redirect:/main";
			}else if (session.getAttribute("check").equals("step1")) {
				session.removeAttribute("check");
				session.setAttribute("check", "step2");				
				user.setCheck("step2");
				userService.saveOrUpdateUser(user, 0);
				return "redirect:/main/carrier/controlpark/trucklist";
			}else if(session.getAttribute("check").equals("step2")) {
				return "redirect:/main/carrier/controlpark/trucklist";
			}
		}
		return "main";		
	}

	@GetMapping("/main/admin")
	public String adminPage(Model model, HttpServletRequest request) {
		if (distances.isEmpty()) {
			request.setAttribute("errorMessage", "Матрица расстояний не загружена в память!");
		}
		
		return "admin";
	}

	@GetMapping("/main/admin/userlist")
	public String userListPage(Model model) {
		List<User> userList = new ArrayList<User>();
		userService.getUserList().stream().filter(u->u.getCompanyName().equals("Доброном"))
			.forEach(u-> userList.add(u));
		model.addAttribute("userlist", userList);
		return "userList";
	}

	@GetMapping("/main/shop")
	public String shopPage(Model model, HttpServletRequest request, HttpSession session) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		int idShop = userService.getUserByLogin(name).getShop().getNumshop();
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
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		int idShop = userService.getUserByLogin(name).getShop().getNumshop();		
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
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		String director = userService.getUserByLogin(name).getName() +" " +userService.getUserByLogin(name).getSurname();
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

	@RequestMapping("/main/who")
	public String whoTest(Model model, String message) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			User user = userService.getUserByLogin(username);
			System.out.println(user.getRoles().toString());
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
		System.out.println(user.getRoles().toString());
		model.addAttribute("message", "roles = " + user.getRoles().toString() + " username = " + user.getLogin());

		return "redirect:/main";
	}

	@RequestMapping(value = "/main/signin", method = RequestMethod.GET)
	public String logination(Model model) {
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
			model.addAttribute("user", user);
			return "registration";
		}else if(but3 != null){
			User user = new User();
			user.setCheck("international");
			model.addAttribute("user", user);
			return "registration";
		}else {
			request.setAttribute("errorMessage", "в разработке!");
			return "preregistration";
		}
	}
	// управление кнопкой регистрации для аккаунта директора магазина и юрлица!!!
	@SuppressWarnings("unused")
	@RequestMapping(value = "/main/registration/form", method = RequestMethod.POST)
	public String postRegistration(@ModelAttribute("user") User user, HttpServletRequest request, Model model, HttpSession session,
			@RequestParam(value = "idShop", required = false) Integer idShop,
			@RequestParam(value = "flag", required = false) String flag,
			@RequestParam(value = "international", required = false) String inter) {
		if (!user.getPassword().equals(user.getConfirmPassword())) {
			request.setAttribute("errorMessage", "Пароли не совпадают"); // JS!
			User target = new User();
			model.addAttribute("user", target);
			return "registration";
		}else if(idShop !=null) {
			user.setShop(shopService.getShopByNum(idShop));
			userService.saveOrUpdateUser(user, 4); //магазин
			return "redirect:/main/admin/shoplist";
		}else if(flag != null) {
			user.setShop(userService.getUserById(user.getIdUser()).getShop());
			userService.saveOrUpdateUser(user, 0);
			return "redirect:/main/admin/shoplist";
		}else if(inter != null) {			
			userService.saveOrUpdateUser(user, 7); // международник
			session.setAttribute("check", "international");
			return "redirect:/main/signin";
		}else{
			userService.saveOrUpdateUser(user, 7); // перевозчик
			session.setAttribute("check", "step1");
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

	@PostMapping("/main/admin/userlist/saveUser")
	public String saveUser(@ModelAttribute("user") User user) {
		userService.saveOrUpdateUser(user , 1);
		return "redirect:/main/admin/userlist";
	}

	@GetMapping("/main/logistics")
	public String getLogistics() {
		return "logistics";
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
		}else {
			route.setTime(LocalTime.parse("00:05"));
		}
		if (statStock !=null) {
			route.setStatusStock(statStock);
		}else {
			System.out.println("Ошибка статуса склада");//on page
		}
		if (statRoute != null) {
			if (Integer.parseInt(route.getStatusRoute().trim()) >= 1) {
				//вставить обработчик
			}else {
				route.setStatusRoute(statRoute);
			}
			
		}else {
			System.out.println("Ошибка статуса тендера");//on page
		}
		if (temperature != null) {
			route.setTemperature(temperature);
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
	public String tenderGetPage(Model model, HttpSession session) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		User user = userService.getUserByLogin(name);
		if (user.getCheck() != null && user.getCheck().equals("international")) {
			Set<Route> routes = new HashSet<Route>();
			routeService.getRouteListAsStatus("1", "1").stream()
				.filter(r->r.getComments() !=null && r.getComments().equals("international"))
				.forEach(r-> routes.add(r));
			model.addAttribute("routes", routes);//отдаёт маршруты для международников в тендер		
			return "tender";
		}else if(user.getCheck() != null) {
			session.setAttribute("errorMessage", "В доступе отказано! необходимо пройти вертификацию.");
			return "redirect:/main/carrier";
		}else{
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
				if (Double.parseDouble(route.getTotalCargoWeight()) < maxWeight) {
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
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		User user = userService.getUserByLogin(name);
		if (routeId == null) {
			routeId = (Integer) session.getAttribute("idRoute");
		}
		Route route = routeService.getRouteById(routeId);
		boolean flag = false;		
		if (route.getComments() != null && route.getComments().equals("international")) {
			for (Message message : chatEnpoint.internationalMessegeList) {
				if (message.getIdRoute().equals(routeId.toString()) && message.getFromUser().equals(name)) {
					flag = true;
					request.setAttribute("userCost", message.getText());
					break;
				}
			}
			model.addAttribute("route", route);
			request.setAttribute("flag", flag);
		}else {
			model.addAttribute("route", addCostForRoute(route));
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
			String name = SecurityContextHolder.getContext().getAuthentication().getName();	
			User user = userService.getUserByLogin(name);			
			route.setFinishPrice(0);
			routeService.saveOrUpdateRoute(route);//забиваем процент в БД
			route.setStatusRoute("2");
			route.setUser(user);
			int seconds = route.getTime().getMinute()*60 + route.getTime().getSecond();
			TenderTimer tenderTimer = new TenderTimer(seconds, addCostForRoute(route), routeService);
			timerList.addAndStart(tenderTimer);
			System.out.println(timerList.size());
		}else if(request.getParameter("agree") != null && finishPercent > route.getFinishPrice()){ //новая цена
			String name = SecurityContextHolder.getContext().getAuthentication().getName();	
			User user = userService.getUserByLogin(name);			
			route.setFinishPrice(finishPercent);
			routeService.saveOrUpdateRoute(route);//забиваем процент в БД
			route.setStatusRoute("2");
			route.setUser(user);
			int seconds = route.getTime().getMinute()*60 + route.getTime().getSecond();
			TenderTimer tenderTimer = new TenderTimer(seconds, addCostForRoute(route), routeService);
			timerList.replace(tenderTimer); // важно! меняем!
			System.out.println(timerList.size());
		}else {
			System.out.println("pidor"); //валидацию в JS
		}
		return "redirect:/main/carrier/tender/tenderpage";
	}
	
	@RequestMapping("/main/carrier/controlpark")
	public String controlParkGet(Model model, HttpServletRequest request, HttpSession session) {
		return "controlPark";
	}
	@GetMapping("/main/carrier/controlpark/trucklist")
	public String truckListGet(Model model, HttpServletRequest request, HttpSession session) {
		if(session.getAttribute("check") == null && userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName()).getCheck().equals("international")){
			List<Truck> trucks = truckService.getTruckListByUser();
			model.addAttribute("trucks", trucks);
			request.setAttribute("check", "international");
			return "truckList";
		}else if (session.getAttribute("check") == null || userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName()).getCheck() == null) {
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
	
	@PostMapping("/main/carrier/controlpark/trucklist")
	public String truckListPost(Model model, HttpServletRequest request, HttpSession session) {
		if (session.getAttribute("check") == null || userService.getUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName()).getCheck() == null) {
			//действие для подтвержденных перневозов
			return "redirect:/main/carrier/controlpark"; // после, добавить метод post типо отравка списка авто
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
			@RequestParam("typeTrailer") String typeTrailer) {
		if (id == 0) {
			truck.setTypeTrailer(typeTrailer);
			truckService.saveOrUpdateTruck(truck);			
		}else if (id !=0) {
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
		String name = SecurityContextHolder.getContext().getAuthentication().getName();		
		List<User> drivers = userService.getDriverList(userService.getUserByLogin(name).getCompanyName());
		model.addAttribute("drivers", drivers);
		return "driverList";
	}
	
	@RequestMapping("/main/carrier/controlpark/driverlist/add")
	public String addDriver(Model model, HttpServletRequest request) {	
		User driver = new User();
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByLogin(name);
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
			userService.saveOrUpdateUser(target, 0);
		}else if(userService.getUserByDriverCard(user.getNumDriverCard()) != null && userService.getUserByDriverCard(user.getNumDriverCard()).getLogin() == null){	
			User target = userService.getUserByDriverCard(user.getNumDriverCard());
			target.setName(user.getName());
			target.setSurname(user.getSurname());
			target.setPatronymic(user.getPatronymic());
			target.setTelephone(user.getTelephone());
			target.setLogin(user.getLogin());
			target.setPassword(user.getPassword());
			userService.saveOrUpdateUser(target, 6);
			System.out.println("fsdjofofaijopisfjaisfjoijohjfaoijoasifhopfsh");
		}else if(userService.getUserByDriverCard(user.getNumDriverCard()) != null && !userService.getUserByDriverCard(user.getNumDriverCard()).getLogin().isEmpty()){	
			System.out.println("данный водитель уже зарегистрирован");
			request.setAttribute("errorMessage", "данный водитель уже зарегистрирован");			
			return addDriver(model, request);
		}else {
			user.setStatus("0");
			userService.saveOrUpdateUser(user, 6);
		}
		return "redirect:/main/carrier/controlpark/driverlist";
	}
	
	//получение страницы с маршрутами перевозчика!
	@RequestMapping("/main/carrier/transportation")
	public String transportationGet(Model model, HttpServletRequest request, HttpSession session) {	
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByLogin(name);
		model.addAttribute("user", user);
		List<Route> routes = routeService.getRouteListByUser();
		List<Route> resultRoutes = new ArrayList<Route>();
		routes.stream().filter(r-> Integer.parseInt(r.getStatusRoute())<=4).forEach(r->resultRoutes.add(r));
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
			@RequestParam(value = "revers", required = false) String revers) {	
		Route route = routeService.getRouteById(idRoute);
		if(revers != null) {
			User driver = route.getDriver();
			Truck truck = route.getTruck();
			int statusNew = Integer.parseInt(driver.getStatus())-1;
			driver.setStatus(statusNew+"");
			int statusTrNew = Integer.parseInt(truck.getStatus())-1;
			truck.setStatus(statusTrNew+"");
			userService.saveOrUpdateUser(driver, 0);
			truckService.saveOrUpdateTruck(truck);
			route.setDriver(null);
			route.setTruck(null);
			routeService.saveOrUpdateRoute(route);
		}else {			
			User driver = userService.getUserById(idDriver);
			Truck truck = truckService.getTruckById(idTruck);
			double weightHasTruck = Double.parseDouble(truck.getCargoCapacity());
			double weightHasRoute = Double.parseDouble(route.getTotalCargoWeight());
			double pallHasTruck = Double.parseDouble(truck.getPallCapacity());
			double pallHasRoute = Double.parseDouble(route.getTotalLoadPall());
			if (weightHasRoute > weightHasTruck || pallHasRoute >= pallHasTruck) {
				System.out.println(weightHasRoute+" <=  "+weightHasTruck);
				System.out.println(pallHasRoute+" >=  "+pallHasTruck);
				
				session.setAttribute("errorMessage", "данная машина не может быть поставлена на этот маршрут");			
				return "redirect:/main/carrier/transportation";
			}else {
				if (driver.getStatus() == null || driver.getStatus().equals("0")) {
					driver.setStatus("1");
				}else {
					int status = Integer.parseInt(driver.getStatus());
					status = status + 1;
					driver.setStatus(status+"");
				}
				if(truck.getStatus() == null || truck.getStatus().equals("0")) {
					truck.setStatus("1");
				}else {
					int status = Integer.parseInt(truck.getStatus());
					status = status + 1;
					truck.setStatus(status+"");
				}
				userService.saveOrUpdateUser(driver, 0);
				truckService.saveOrUpdateTruck(truck);
				route.setDriver(driver);
				route.setTruck(truck);
				route.setStatusRoute("4");
				routeService.saveOrUpdateRoute(route);
			}
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
		return "tenderPage";
	}
	
	@RequestMapping("/main/admin/shoplist")
	public String shoplistGet(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "routeId", required = false) Integer routeId) {
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
		List<User> carriers = userService.getCarrierList();
		Set<User> checkCarriers = new HashSet<User>();
		carriers.stream().filter(c-> c.getCheck() == null).forEach(c-> checkCarriers.add(c));
		model.addAttribute("carriers", checkCarriers);
		return "adminCarrier";
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
	
	@GetMapping("/main/admin/carrier/proof") 
	public String carrieProofGet(Model model, HttpServletRequest request, HttpSession session) {
		List<User> carriers = userService.getCarrierList();
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
		carrier.setCheck(null); // типо подтверждение
		carrier.setLoyalty("100"); // вынести в глоб. переменную
		carrier.setRate(rate);
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
			poiExcel.getDistancesToMap(distances);
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
	public String ratesFormPost(Model model, HttpServletRequest request,
			@ModelAttribute("rate") Rates rates) throws InvalidFormatException, IOException {
		ratesService.saveOrUpdateRates(rates);
		return "redirect:/main/admin/cost/rates";
	}
	
	//Менеджер международных маршрутов GET
	@GetMapping("/main/logistics/international")
	public String internationalGet(HttpServletRequest request, HttpSession session, Model model,
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
		Set<Route> routes = new HashSet<Route>();
		routeService.getRouteListAsDate(dateStart, dateFinish).stream()
			.filter(r-> r.getComments() != null && r.getComments().equals("international"))
			.forEach(r -> routes.add(r)); // проверяет созданы ли точки вручную, и отдаёт только международные маршруты
		model.addAttribute("routes", routes);
		return "internationalManager";
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
	public String internationalAddRouteGet(Model model) {
		model.addAttribute("route", mainRestController.getRoute());
		return "routeForm";
	}
	
	
	@PostMapping("/main/logistics/international/addRoute")
	public String internationalAddRoutePost(Model model,
			@ModelAttribute("route") Route target,
			@RequestParam(value = "date", required = false) Date dateStart,
			@RequestParam(value = "timeOfLoad", required = false) LocalTime time) {
		
		Route route = poiExcel.creatureEmptyRoute(dateStart);
		if (time != null) {
			route.setTimeLoadPreviously(time);
		}		
		route.setStatusRoute("0");
		route.setStatusStock("0");
		route.setComments("international");	
		route.setTemperature(target.getTemperature());
		route.setTotalLoadPall(target.getTotalLoadPall());
		route.setTotalCargoWeight(target.getTotalCargoWeight());
		route.setRouteDirection(target.getRouteDirection());
		route.setStartPrice(target.getStartPrice());
		route.setTypeTrailer(target.getTypeTrailer());
		mainRestController.getRoute().getRoteHasShop().stream().forEach(s-> s.setRoute(route));
		mainRestController.getRoute().getRoteHasShop().stream().forEach(s-> routeHasShopService.saveOrUpdateRouteHasShop(s));
			
		return "redirect:/main/logistics/international";
	}
	
	@GetMapping("/main/logistics/international/editRoute")
	public String internationalEditRouteGet(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		model.addAttribute("route", route);
		request.setAttribute("edit", true);
		return "routeForm";
	}
	@PostMapping("/main/logistics/international/editRoute")
	public String internationalEditRoutePost(Model model, HttpServletRequest request,
			@ModelAttribute("route") Route route,
			@RequestParam(value = "edit", required = false) String edit,
			@RequestParam(value = "delite", required = false) String delite,
			@RequestParam(value = "date", required = false) Date dateStart,
			@RequestParam(value = "timeOfLoad", required = false) LocalTime time) {
		if (edit != null) {
			Route oldRoute = routeService.getRouteById(route.getIdRoute());
			route.setUser(oldRoute.getUser());
			route.setTruck(oldRoute.getTruck());
			route.setDriver(oldRoute.getDriver());
			route.setTimeLoadPreviously(time);
			route.setDateLoadPreviously(dateStart);
			routeService.saveOrUpdateRoute(route);
		}else if(delite != null) {
			routeService.deleteRouteById(route.getIdRoute());
		}
		
		return "redirect:/main/logistics/international";
	}
	
	@GetMapping("/main/logistics/international/tenderOffer")
	public String internationalTenderOfferGet(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {
		request.setAttribute("idRoute", idRoute);
		return "tenderOffer";
	}
	
	@GetMapping("/main/carrier/tender/tenderOffer")
	public String carrierTenderOfferGet(Model model, HttpServletRequest request) {
		return "redirect:/main/carrier/tender";
	}
	
	@GetMapping("/main/logistics/international/routeShow")
	public String internationalRouteShowGet(Model model, HttpServletRequest request,
			@RequestParam(value = "idRoute", required = false) Integer idRoute) {		
		request.setAttribute("route", routeService.getRouteById(idRoute));
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
		User driver = route.getDriver();
		int statusNew = Integer.parseInt(driver.getStatus())-1;
		driver.setStatus(statusNew+"");
		Truck truck = route.getTruck();
		int statusTrNew = Integer.parseInt(truck.getStatus())-1;
		truck.setStatus(statusTrNew+"");
		userService.saveOrUpdateUser(driver, 0);
		truckService.saveOrUpdateTruck(truck);
		routeService.saveOrUpdateRoute(route);
		return "redirect:/main/logistics/international";
	}
	
	
	@RequestMapping("/main/message")
	public String chat(HttpServletRequest request, Model driver, HttpSession session) {
		return "chat";
	}
	
	
	@RequestMapping("/main/logistics/international/confrom")
	public String confromCost(Model model,
			@RequestParam("login") String login,
			@RequestParam("cost") Integer cost,
			@RequestParam("idRoute") Integer idRoute) {
		Route route = routeService.getRouteById(idRoute);
		User user = userService.getUserByLogin(login);
		route.setFinishPrice(cost);
		route.setUser(user);
		route.setStatusRoute("4");
		routeService.saveOrUpdateRoute(route);
		List<Message> messages = new ArrayList<Message>();		
		chatEnpoint.internationalMessegeList.stream()
			.filter(mes->mes.getIdRoute().equals(idRoute.toString()) && !mes.getFromUser().equals("system"))
			.forEach(mes-> messages.add(mes));
		messages.stream().forEach(mes->{
			chatEnpoint.internationalMessegeList.remove(mes);
			messageService.saveOrUpdateMessage(mes);
		});
		return "redirect:/main/logistics/international";
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
	
	@RequestMapping("/main/test")
	public String test() {
		System.out.println(chatEnpoint.sessionList.size());
		return "redirect:/main";
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
	
	
	@RequestMapping("/main/js")
	public String javaScript(Model model, HttpSession session, HttpServletRequest request) {	
		return "jsPage";
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
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		String companyName = userService.getUserByLogin(name).getCompanyName();
		return companyName;
		
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
