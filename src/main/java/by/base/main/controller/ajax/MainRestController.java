package by.base.main.controller.ajax;

import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.LIMIT;
import static com.graphhopper.json.Statement.Op.MULTIPLY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import by.base.main.dto.*;
import by.base.main.model.*;
import by.base.main.model.yard.Shipment;
import by.base.main.model.yard.ShopShipment;
import by.base.main.service.*;

import by.base.main.service.yardService.ShipmentService;
import by.base.main.service.yardService.ShopShipmentService;
import by.base.main.util.hcolossus.ColossusProcessorANDRestrictions5Sync;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dto.OrderDTO;
import com.dto.PlanResponce;
import com.dto.RouteDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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
import com.itextpdf.text.DocumentException;

import by.base.main.aspect.TimedExecution;
import by.base.main.controller.MainController;
import by.base.main.dao.DAOException;
import by.base.main.dto.MarketDataFor330Request;
import by.base.main.dto.MarketDataFor330Responce;
import by.base.main.dto.MarketDataFor398Request;
import by.base.main.dto.MarketDataForClear;
import by.base.main.dto.MarketDataForLoginDto;
import by.base.main.dto.MarketDataForRequestDto;
import by.base.main.dto.MarketErrorDto;
import by.base.main.dto.MarketPacketDto;
import by.base.main.dto.MarketRequestDto;
import by.base.main.dto.MarketTableDto;
import by.base.main.dto.OrderBuyGroupDTO;
import by.base.main.service.AddressService;
import by.base.main.service.MessageService;
import by.base.main.service.OrderProductService;
import by.base.main.service.OrderService;
import by.base.main.service.ProductService;
import by.base.main.service.RatesService;
import by.base.main.service.RoleService;
import by.base.main.service.RouteHasShopService;
import by.base.main.service.RouteService;
import by.base.main.service.ScheduleService;
import by.base.main.service.ServiceException;
import by.base.main.service.ShopService;
import by.base.main.service.TGTruckService;
import by.base.main.service.TGUserService;
import by.base.main.service.TaskService;
import by.base.main.service.TruckService;
import by.base.main.service.UserService;
import by.base.main.service.impl.MarketExceptionBuilder;
import by.base.main.service.util.CheckOrderNeeds;
import by.base.main.service.util.CustomJSONParser;
import by.base.main.service.util.MailService;
import by.base.main.service.util.ORLExcelException;
import by.base.main.service.util.OrderCreater;
import by.base.main.service.util.PDFWriter;
import by.base.main.service.util.POIExcel;
import by.base.main.service.util.PasswordGenerator;
import by.base.main.service.util.PrilesieService;
import by.base.main.service.util.PropertiesUtils;
import by.base.main.service.util.ReaderSchedulePlan;
import by.base.main.service.util.ServiceLevel;
import by.base.main.util.CarrierTenderWebSocket;
import by.base.main.util.ChatEnpoint;
import by.base.main.util.MainChat;
import by.base.main.util.SlotWebSocket;
import by.base.main.util.TsdWebSocket;
import by.base.main.util.GraphHopper.CustomJsonFeature;
import by.base.main.util.GraphHopper.JSpiritMachine;
import by.base.main.util.GraphHopper.RoutingMachine;
import by.base.main.util.bots.TelegramBot;
import by.base.main.util.hcolossus.ColossusProcessorANDRestrictions5;
import by.base.main.util.hcolossus.exceptions.FatalInsufficientPalletTruckCapacityException;
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

//	private Gson gson = new GsonBuilder()
//            .setDateFormat("yyyy-MM-dd")  // Устанавливаем нужный формат
//            .create();
	
	private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
				@Override
				public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					return context.serialize(src.getTime());  // Сериализация даты в миллисекундах
				}
            })
            .create();

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
	private ColossusProcessorANDRestrictions5 colossusProcessorRad;

	@Autowired
	private ColossusProcessorANDRestrictions5Sync colossusProcessorRadSync;

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
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private PropertiesUtils propertiesUtils;
	
	@Autowired
	private CheckOrderNeeds checkOrderNeeds;
	
	@Autowired
	private OrderProductService orderProductService;
	
	@Autowired
	private ReaderSchedulePlan readerSchedulePlan;
	
	@Autowired
	private ServiceLevel serviceLevel;
	
	@Autowired 
	private TGTruckService tgTruckService;
	
	@Autowired
    private TGUserService tgUserService;
	
	@Autowired
	private PDFWriter pdfWriter;

	@Autowired
	private TaskService taskService;
	
	@Autowired	
	private PermissionService permissionService;

	@Autowired
	private ActService actService;

	@Autowired
	private OrderCalculationService orderCalculationService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private PriceProtocolService priceProtocolService;
	
	@Autowired
	private RotationService rotationService;
	
	@Autowired
	private CarrierBidService carrierBidService;
	
	@Autowired
	private CarrierTenderWebSocket carrierTenderWebSocket;
	
	@Autowired
	private GoodAccommodationService goodAccommodationService;
	
	@Autowired
	private InfoCarrierService infoCarrierService;
	
	@Autowired
	private PrilesieService prilesieService;
	
	@Autowired
	private DistanceMatrixService distanceMatrixService;
	
	@Autowired
	private PasswordGenerator passwordGenerator;

	@Autowired
	private WarehouseManagementDataService warehouseManagementDataService;

	@Autowired
	private ShipmentService shipmentService;

	@Autowired
	private ShopShipmentService shopShipmentService;

	private static String classLog;
	public static String marketJWT;
	//в отдельный файл
//	public static final String marketUrl = "https://api.dobronom.by:10806/Json";
//	public static final String serviceNumber = "BB7617FD-D103-4724-B634-D655970C7EC0";
//	public static final String loginMarket = "191178504_SpeedLogist";
//	public static final String passwordMarket = "SL!2024D@2005";
//	public static final String marketUrl = "https://api.dobronom.by:10896/Json";
//	public static final String serviceNumber = "CD6AE87C-2477-4852-A4E7-8BA5BD01C156";
//	public static final String loginMarket = "191178504_SpeedLogist";
//	public static final String passwordMarket = "SL!2024D@2005";
	
	public static String marketUrl;
	public static String serviceNumber;
	public static String loginMarket;
	public static String passwordMarket;
	
	@Value("${market.marketUrl}")
	public String marketUrlProp;
	
	@Value("${market.serviceNumber}")
	public String serviceNumberProp;
	
	@Value("${market.loginMarket}")
	public String loginMarketProp;
	
	@Value("${market.passwordMarket}")
	public String passwordMarketProp;
	
	@Value("${stockBalance.run}")
	public Boolean stockBalanceRun;
	
	@Value("${api.prilesie.url}")
	private String SERVER_URL;
	
	@Value("${api.prilesie.username}")
	private String USERNAME;
	
	@Value("${api.prilesie.passeord}")
	private String PASSWORD;
	
	@PostConstruct
    public void init() {
		marketUrl = marketUrlProp;
		serviceNumber = serviceNumberProp;
		loginMarket = loginMarketProp;
		passwordMarket = passwordMarketProp;
    }
	

	public static final Comparator<Address> comparatorAddressId = (Address e1, Address e2) -> (e1.getIdAddress() - e2.getIdAddress());
	public static final Comparator<Address> comparatorAddressIdForView = (Address e1, Address e2) -> (e2.getType().charAt(0) - e1.getType().charAt(0));
	/**
	 * сортирует от последней точки загрузки
	 */
	public static final Comparator<Address> comparatorAddressForLastLoad = (Address e1, Address e2) -> (e2.getPointNumber() - e1.getPointNumber());

	@Autowired
    private ServletContext servletContext;

	/**
	 * Экспорт данных из Route и RouteHasShop во двор
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @author Ira
	 */
	@PostMapping("/logistics/razv/import-to-yard")
//	public Map<String, Object> importToYard(@RequestParam(value =  "dateTask", required = false) Date dateTask){
	public Map<String, Object> importToYard(@RequestBody String str) throws ParseException {

		Map<String, Object> response = new HashMap<>();

		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Date dateTask = Date.valueOf(jsonMainObject.get("dateTask").toString());
		List<Route> routes = routeService.getRoutesByDateTask(dateTask);
		for(Route route : routes){
			Shipment shipment = new Shipment();
			shipment.setDatePlanShip(new Timestamp(route.getDateTask().getTime()));
			shipment.setRouteValue(route.getRouteDirection());
			shipment.setFirmName("");
			Long id = shipmentService.saveShipment(shipment);
			Set<RouteHasShop> routeHasShops = route.getRoteHasShop();
			for(RouteHasShop routeHasShop: routeHasShops) {
				ShopShipment shopShipment = new ShopShipment();
				shopShipment.setCargoWeight(Double.parseDouble(routeHasShop.getWeight()));
				shopShipment.setIdShipment(id);
				shopShipment.setPalletsToShop(((Double)Double.parseDouble(routeHasShop.getPall())).intValue());
				shopShipment.setCargoWeight(Double.parseDouble(routeHasShop.getWeight()));
				shopShipment.setOrdinalNumber(routeHasShop.getOrder());
				shopShipment.setIdShop(routeHasShop.getShop().getIdShop());
				shopShipmentService.saveShopShipment(shopShipment);
			}
		}

		response.put("status", "200");
		return response;
	}

	/**
	 * Метод парсит эксели с маршрутным листои и записывает в Route и RouteHasShop
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @author Ira
	 */
	@PostMapping("/logistics/razv/parse-route-sheet")
	public Map<String, Object> parseRouteSheet(@RequestParam(value = "routesExcel", required = false) MultipartFile excel,
											   @RequestParam(value =  "dateTask", required = false) Date dateTask) throws IOException {
		Map<String, Object> response = new HashMap<>();
		poiExcel.parseRouteSheet(excel.getInputStream(), dateTask);
		response.put("status", "200");
		return response;
	}

	/**
	 * Метод парсит эксели с заданиями и записывает в WarehouseManagementData
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @author Ira
	 */
	@RequestMapping(value ="/logistics/razv/parse-WMS-excel", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, Object> getDataFromWMSExcel(@RequestParam(value = "excel1700", required = false) MultipartFile excel1700,
												   @RequestParam(value = "excel1800", required = false) MultipartFile excel1800,
												   @RequestParam(value =  "dateTask", required = false) Date dateTask) throws IOException, ParseException, ServiceException {
		Map<String, Object> response = new HashMap<>();
//		JSONParser parser = new JSONParser();
//		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
//		Date date = Date.valueOf(jsonMainObject.get("dateTask").toString());
		List<List<Double>> excelDataWMS1700 = poiExcel.parseWMSexcel(excel1700.getInputStream());
		List<List<Double>> excelDataWMS1800 = poiExcel.parseWMSexcel(excel1800.getInputStream());

        for (List<Double> row : excelDataWMS1700) {
			WarehouseManagementData data = new WarehouseManagementData();
			data.setNumStock(row.get(0).intValue());
			data.setPallets(row.get(1));
			data.setWeight(row.get(2));
			data.setWarehouse(1700);
			data.setDateCreate(new Date(System.currentTimeMillis()));
			data.setDateTask(dateTask);
			warehouseManagementDataService.saveWarehouseManagementData(data);
		}

		for (List<Double> row : excelDataWMS1800) {
			WarehouseManagementData data = new WarehouseManagementData();
			data.setNumStock(row.get(0).intValue());
			data.setPallets(row.get(1));
			data.setWeight(row.get(2));
			data.setWarehouse(1800);
			data.setDateCreate(new Date(System.currentTimeMillis()));
			data.setDateTask(dateTask);
			warehouseManagementDataService.saveWarehouseManagementData(data);
		}

		response.put("status", "200");
		return response;
	}

	@PostMapping("/logistics/razv/create-route-sheet")
	public Map<String, Object> createRouteSheets(@RequestParam(value = "excel", required = false) MultipartFile excel,
												 @RequestBody String str) throws IOException, ParseException, ServiceException {
		Map<String, Object> response = new HashMap<>();

		return response;

	}

	@PostMapping ("/restrictions")
	public void restrictions(@RequestParam(value = "excel", required = false) MultipartFile excel) throws IOException {
		String appPath = servletContext.getRealPath("/");
		String filepath = appPath + "resources/others/Ограничения.xlsx";

		File file = new File(filepath);
		String outPath = appPath + "resources/others/restrictions.xlsx";
		poiExcel.actualRestrictions(file, outPath);

	}

	@GetMapping ("/supplier/get-orders-for-supplier")
	public List<Order> getOrdersForSupplier() {
		String counterpartyCode = getThisUser().getCounterpartyCode().toString();
		List<Order> orders = orderService.getAllOrdersForSupplier(counterpartyCode, 20);
		return orders;
	}
	/**
	 * Метод предварительной заявки авто на определенную дату (TgTruck)
	 * @param request
	 * @param str
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	@PostMapping("/carrier/razv/preorder")
	public Map<String, Object> postRazvPreorder(@RequestBody String str) throws IOException, ParseException {
	    Map<String, Object> response = new HashMap<>();
	    JSONParser parser = new JSONParser();
	    JSONObject jsonMainObject = (JSONObject) parser.parse(str);
	    User user = getThisUser();
	    int countCar = Integer.parseInt(jsonMainObject.get("count").toString());
	    List<TGTruck> tgTrucks = new ArrayList<TGTruck>();

	    for (int i = 0; i < countCar; i++) {
	    	TGTruck tgTruck = new TGTruck();
		    tgTruck.setNumTruck(user.getCompanyName());
		    tgTruck.setPall(Integer.parseInt(jsonMainObject.get("pall").toString()));
		    tgTruck.setTypeTrailer(jsonMainObject.get("typeTrailer").toString());
		    tgTruck.setDateRequisition(Date.valueOf(jsonMainObject.get("dateRequisition").toString()));
		    tgTruck.setCargoCapacity(jsonMainObject.get("cargoCapacity").toString());
		    tgTruck.setStatus(10);
		    tgTruck.setCompanyName(user.getCompanyName());
		    tgTruck.setTypeStock(jsonMainObject.get("typeStock").toString());
	    	int id = tgTruckService.save(tgTruck);
	    	tgTruck.setIdTGTruck(id);
	    	tgTrucks.add(tgTruck);
		}

	    response.put("status", "200");
	    response.put("objects", tgTrucks);
	    return response;
	}

	@PostMapping ("/logistics/registration-fast")
    public Map<String, Object> createNewSupplier(HttpServletRequest request, @RequestBody String str) throws ParseException {
       Map<String, Object> response = new HashMap<>();
       JSONParser parser = new JSONParser();
       JSONObject jsonMainObject = (JSONObject) parser.parse(str);
       User adminUser = getThisUser();
       String login = jsonMainObject.get("login") == null ? null : jsonMainObject.get("login").toString();
       String name = jsonMainObject.get("name") == null ? null : jsonMainObject.get("name").toString();
       String surname = jsonMainObject.get("surname") == null ? null : jsonMainObject.get("surname").toString();
       String patronymic = jsonMainObject.get("patronymic") == null ? null : jsonMainObject.get("patronymic").toString();
       String phone = jsonMainObject.get("phone") == null ? null : jsonMainObject.get("phone").toString();
       String email = jsonMainObject.get("email") == null ? null : jsonMainObject.get("email").toString();
       String companyName = jsonMainObject.get("companyName") == null ? getThisUser().getCompanyName() : jsonMainObject.get("companyName").toString();
       String propertySize = jsonMainObject.get("propertySize") == null ? null : jsonMainObject.get("propertySize").toString();
       String countryOfRegistration = jsonMainObject.get("countryOfRegistration") == null ? null : jsonMainObject.get("countryOfRegistration").toString();
       String numcontract = jsonMainObject.get("numcontract") == null ? null : jsonMainObject.get("numcontract").toString();

       String password = passwordGenerator.generatePassword(12);
       User user = new User();
       user.setLogin(login);
       user.setPassword(password);
       user.setName(name);
       user.setSurname(surname);
       user.setPatronymic(patronymic);
       user.setCompanyName(companyName);
       user.setTelephone(phone);
       user.seteMail(email);
       user.setDateRegistration(new Date(System.currentTimeMillis()));
       user.setPropertySize(propertySize);
       user.setCountryOfRegistration(countryOfRegistration);
       user.setNewTenderNotification(false);
       user.setBlock(false);
       user.setNumContract(numcontract);
       user.setCheck("razv");

       try {
          userService.saveOrUpdateUser(user, 7);
       } catch (ConstraintViolationException e) {
          response.put("status", "100");
          response.put("message", "Такой логин уже существует");
          return response;
       }

       String emailText = "Добрый день!" +
             "<br>Ваш аккаунт создан." +
             "<br>Логин: " + login +
             "<br>Пароль: " + password +
             "<br>Войти в личный кабинет можно <a href=https://boxlogs.net/speedlogist/main/supplier>здесь</a>";

       List<String> emails = Arrays.asList(email, adminUser.geteMail());
       mailService.sendEmailToUsersHTMLContent(request, "Регистрация", emailText, emails);
       response.put("status", "200");
       response.put("message", "Аккаунт создан. Письмо отправлено на почты: " + email + " " + adminUser.geteMail());
       return response;
    }

	/**
	 * <br>Метод для массовой блокировки кодов товаров</br>.
	 * @author Ira
	 */
	@PostMapping("/order-support/block-many-products")
	public Map<String, Object> blockManyProducts(HttpServletRequest request, @RequestBody String str) throws IOException, ParseException {
	    Map<String, Object> response = new HashMap<String, Object>();
	    JSONParser parser = new JSONParser();
	    JSONObject jsonMainObject = (JSONObject) parser.parse(str);
	    JSONArray jsonCodes = (JSONArray) jsonMainObject.get("codes");
	    List<Integer> codes = new ArrayList<>();
	       for (Object jsonCode : jsonCodes) {
	           codes.add(Integer.parseInt(jsonCode.toString()));
	       }
	    Date dateStart = jsonMainObject.get("dateStart") != null && !jsonMainObject.get("dateStart").toString().isEmpty() ? Date.valueOf(jsonMainObject.get("dateStart").toString()) : null;
	    Date dateFinish = jsonMainObject.get("dateFinish") != null && !jsonMainObject.get("dateFinish").toString().isEmpty() ? Date.valueOf(jsonMainObject.get("dateFinish").toString()) : null;
	    if(dateStart.after(dateFinish)) {	    	
	    	response.put("message", "Дата старта не может быть позже даты финиша");
	    	response.put("status", "100");
		    return response;
	    }
	    for (Integer code: codes) {
	       Product product = productService.getProductByCode(code);
	       if (product != null) {
	          product.setBlockDateStart(dateStart);
	          product.setBlockDateFinish(dateFinish);
	          productService.updateProduct(product);
	       } else {
	          response.put("status", "100");
	          response.put("message", "Товара с кодом " + code + " не существует.");
	          return response;
	       }
	    }
	    response.put("status", "200");
	    return response;
	}
	
	/**
	 * <br>Сохраняет машину в прилесье</br>.
	 * @author Ira
	 */
	@PostMapping("/save-route-to-prilesie")
	public Map<String, Object> saveRouteToPrilesie(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
	    Map<String, Object> response = new HashMap<>();
	    JsonObject jsonObject = new JsonObject();
	    String serverUrlMethod = SERVER_URL+"/allowedlist/";


	    String credentials = USERNAME + ":" + PASSWORD;
	    String credentialsBasic = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

	    JSONParser parser = new JSONParser();
	    JSONObject jsonMainObject = (JSONObject) parser.parse(str);
	    Integer routeId = jsonMainObject.get("idRoute") == null ? null : Integer.parseInt(jsonMainObject.get("idRoute").toString());
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    LocalDateTime startTime = LocalDateTime.parse(jsonMainObject.get("dateTimeStartPrilesie").toString(), dtf).withSecond(0).withNano(0);
	    LocalDateTime endTime = LocalDateTime.parse(jsonMainObject.get("dateTimeEndPrilesie").toString(), dtf).withSecond(0).withNano(0);

	    Route route = routeService.getRouteById(routeId);
	    String carNumber = prilesieService.transliterateToVisualLatin(route.getTruck().getNumTruck()).replaceAll("[ *-]+", "");
	    String driverPhone = route.getDriver().getTelephone() == null ? "" : prilesieService.phoneConverter(route.getDriver().getTelephone());
	    Integer warehouse = null;
	    Integer allowCompany = null;
	    List<Order> orders = route.getOrders().stream().collect(Collectors.toList());

	    if (!orders.isEmpty()) {
	        if (orders.get(0).getNumStockDelivery().equals("1800")) {
	            warehouse = 7;
	            allowCompany = 15;
	        } else if (orders.get(0).getNumStockDelivery().equals("1700")) {
	            warehouse = 5;
	            allowCompany = 10;
	        } else {
	            response.put("status", "100");
	            response.put("message", "Данная функция работает только для 1700 и 1800 склада");
	            return response;
	        }
	    }
	    String supplier = route.getRouteDirection().split(">")[0].substring(1);

	    jsonObject.addProperty("id", routeId);
	    jsonObject.addProperty("plate_number", carNumber);
	    jsonObject.addProperty("sms_number", driverPhone);
	    jsonObject.addProperty("start_time", startTime.toString());
	    jsonObject.addProperty("end_time", endTime.toString());
	    jsonObject.addProperty("status", 2);
	    jsonObject.addProperty("allow_company", allowCompany);
	    jsonObject.addProperty("warehouse", warehouse);
	    jsonObject.addProperty("ramp", 20);
	    jsonObject.addProperty("supplier", supplier);

	    Gson gson = new Gson();
	    String jsonData = gson.toJson(jsonObject);
	    StringEntity entity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);

	    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	    HttpPost httpPost = new HttpPost(serverUrlMethod);
	    httpPost.setEntity(entity);
	    httpPost.setHeader("Authorization", credentialsBasic);
	    httpPost.setHeader("Accept", "application/json");
	    httpPost.setHeader("Content-type", "application/json");
	    CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

	    int statusCode = httpResponse.getStatusLine().getStatusCode();
	    HttpEntity responseEntity = httpResponse.getEntity();
	    String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
	    JSONObject jsonResponseObject = (JSONObject) parser.parse(responseBody);
	    if (statusCode >= 200 && statusCode < 300) {
	        Long idObjectPrilesie = jsonResponseObject.get("id") == null ? null : Long.parseLong(jsonResponseObject.get("id").toString());
	        if (idObjectPrilesie == null) {
	            response.put("status", "100");
	            response.put("message", "Объект не был сохранён");
	            return response;
	        }
	        route.setIdObjectPrilesie(idObjectPrilesie);
	        Timestamp timestampStartTime = Timestamp.valueOf(startTime);
	        route.setDateTimeStartPrilesie(timestampStartTime);
	        Timestamp timestampEndTime = Timestamp.valueOf(endTime);
	        route.setDateTimeEndPrilesie(timestampEndTime);
	        routeService.updateRoute(route);
	        response.put("status", "200");
	        response.put("idObjectPrilesie", idObjectPrilesie);
	        response.put("route", route);
	        return response;
	    } else {
	        String prilesieresponse = jsonResponseObject.get("error").toString();
	        response.put("status", "100");
	        response.put("message", "Ответ с Прилесья: " + prilesieresponse);
	        return response;
	    }

	}
	
	/**
	 * <br>Обновляет в прилесье данные о времени для машины</br>.
	 * @author Ira
	 */
	@PostMapping("/update-route-to-prilesie")
	public Map<String, Object> updateRouteToPrilesie(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		String serverUrlMethod = SERVER_URL+"/allowedlist/";

	    Map<String, Object> response = new HashMap<>();

	    JSONParser parser = new JSONParser();
	    JSONObject jsonMainObject = (JSONObject) parser.parse(str);
	    Integer routeId = jsonMainObject.get("idRoute") == null ? null : Integer.parseInt(jsonMainObject.get("idRoute").toString());
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    LocalDateTime startTime = LocalDateTime.parse(jsonMainObject.get("dateTimeStartPrilesie").toString(), dtf).withSecond(0).withNano(0);
	    LocalDateTime endTime = LocalDateTime.parse(jsonMainObject.get("dateTimeEndPrilesie").toString(), dtf).withSecond(0).withNano(0);
	    Route route = routeService.getRouteById(routeId);
	    String carNumber = prilesieService.transliterateToVisualLatin(route.getTruck().getNumTruck()).replaceAll("[ *-]+", "");
	    String driverPhone = route.getDriver().getTelephone() == null ? "" : prilesieService.phoneConverter(route.getDriver().getTelephone());
	    Integer warehouse = null;
	    Integer allowCompany = null;
	    List<Order> orders = route.getOrders().stream().collect(Collectors.toList());

	    if (!orders.isEmpty()) {
	        if (orders.get(0).getNumStockDelivery().equals("1800")) {
	            warehouse = 7;
	            allowCompany = 15;
	        } else if (orders.get(0).getNumStockDelivery().equals("1700")) {
	            warehouse = 5;
	            allowCompany = 10;
	        }
	    }
	    String supplier = route.getRouteDirection().split(">")[0].substring(1);

	    JsonObject jsonObjectForPrilesie = new JsonObject();
	    jsonObjectForPrilesie.addProperty("id", routeId);
	    jsonObjectForPrilesie.addProperty("plate_number", carNumber);
	    jsonObjectForPrilesie.addProperty("sms_number", driverPhone);
	    jsonObjectForPrilesie.addProperty("start_time", startTime.toString());
	    jsonObjectForPrilesie.addProperty("end_time", endTime.toString());
	    jsonObjectForPrilesie.addProperty("status", 2);
	    jsonObjectForPrilesie.addProperty("allow_company", allowCompany);
	    jsonObjectForPrilesie.addProperty("warehouse", warehouse);
	    jsonObjectForPrilesie.addProperty("ramp", 20);
	    jsonObjectForPrilesie.addProperty("supplier", supplier);

	    Gson gson = new Gson();
	    String jsonData = gson.toJson(jsonObjectForPrilesie);
	    StringEntity entity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);

	    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	    String credentials = USERNAME + ":" + PASSWORD;
	    String credentialsBasic = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	    HttpPatch httpPatch = new HttpPatch(serverUrlMethod + route.getIdObjectPrilesie() + "/");
	    httpPatch.setHeader("Authorization", credentialsBasic);
	    httpPatch.setHeader("Accept", "application/json");
	    httpPatch.setHeader("Content-type", "application/json");
	    httpPatch.setEntity(entity);
	    CloseableHttpResponse httpResponse = httpClient.execute(httpPatch);

	    int statusCode = httpResponse.getStatusLine().getStatusCode();
	    HttpEntity responseEntity = httpResponse.getEntity();
	    String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
	    JSONObject jsonResponseObject = (JSONObject) parser.parse(responseBody);
	    if (statusCode >= 200 && statusCode < 300) {
	        Long idObjectPrilesie = jsonResponseObject.get("id") == null ? null : Long.parseLong(jsonResponseObject.get("id").toString());
	        if (idObjectPrilesie == null) {
	            response.put("status", "100");
	            response.put("message", "Объект не был обновлён");
	            return response;
	        }
	        route.setIdObjectPrilesie(idObjectPrilesie);
	        Timestamp timestampStartTime = Timestamp.valueOf(startTime);
	        route.setDateTimeStartPrilesie(timestampStartTime);
	        Timestamp timestampEndTime = Timestamp.valueOf(endTime);
	        route.setDateTimeEndPrilesie(timestampEndTime);
	        routeService.updateRoute(route);
	        response.put("status", "200");
	        response.put("idObjectPrilesie", idObjectPrilesie);
	        response.put("route", route);
	        return response;
	    } else {
	        String prilesieresponse = jsonResponseObject.get("error").toString();
	        response.put("status", "100");
	        response.put("message", "Ответ с Прилесья: " + prilesieresponse);
	        return response;
	    }
	}
	
	/**
	 * <br>Получает из прилесья данные о машине по idObjectPrilesie</br>.
	 * @author Ira
	 */
	@GetMapping("/get-route-prilesie/{idObjectPrilesie}")
	public Map<String, Object> getRoutePrilesie(@PathVariable String idObjectPrilesie) throws ParseException, IOException {
	    Map<String, Object> response = new HashMap<>();
	    String serverUrlMethod = SERVER_URL+"/allowedlist/";
	    JSONParser parser = new JSONParser();

	    JsonObject jsonObjectForPrilesie = new JsonObject();
	    jsonObjectForPrilesie.addProperty("id", idObjectPrilesie);

	    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	    String credentials = USERNAME + ":" + PASSWORD;
	    String credentialsBasic = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	    HttpGet httpGet = new HttpGet(serverUrlMethod + idObjectPrilesie + "/");
	    httpGet.setHeader("Authorization", credentialsBasic);
	    httpGet.setHeader("Accept", "application/json");
	    httpGet.setHeader("Content-type", "application/json");
	    CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

	    int statusCode = httpResponse.getStatusLine().getStatusCode();
	    HttpEntity responseEntity = httpResponse.getEntity();
	    String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
	    JSONObject jsonResponseObject = (JSONObject) parser.parse(responseBody);
	    if (statusCode >= 200 && statusCode < 300) {
	        response.put("status", "200");
	        response.put("response", jsonResponseObject);
	        return response;
	    } else {
	        String prilesieResponse = jsonResponseObject.get("error").toString();
	        if (prilesieResponse.equals("No AllowedList matches the given query.")) {
	            response.put("status", "100");
	            response.put("message", "Маршрут с таким ID не найден");
	            return response;
	        }
	        response.put("status", "100");
	        response.put("message", "Ответ с Прилесья: " + prilesieResponse);
	        return response;
	    }
	}
	
	 //получение страницы с маршрутами перевозчика!
    @GetMapping("/carrier/get-actual-carrier-routes")
    public List<Route> transportationGet(HttpServletRequest request) {
		List<Route> routes = routeService.getRouteListByUser();
		List<Route> resultRoutes = new ArrayList<Route>();
		routes.stream().filter(r-> Integer.parseInt(r.getStatusRoute())<=4).filter(r-> !resultRoutes.contains(r)).forEach(r->resultRoutes.add(r));
        return resultRoutes;
    }
    
	/*
	 * мой старый метод
	 */
	@GetMapping("/getPallHasOwerPlan/{date}&{stock}")
	public Map<String, Object> testNewMethod(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Date date,
			@PathVariable Integer stock) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		int pall = 0;
		int pallOut = 0;
		int pallEternalMowment = 0;
		StringBuilder message = new StringBuilder();
		Set<Order> ordersNow = new HashSet<Order>(orderService.getOrderByTimeDeliveryAndNumStock(date, date, stock));
		Map<Long, Schedule> schedulesMap = scheduleService.getSchedulesListRC().stream()
			    .filter(s -> s.getStatus() == 20)
			    .collect(Collectors.toMap(
			        Schedule::getCounterpartyContractCode,
			        Function.identity()
			    ));
		for (Order order : ordersNow) {
			if(order.getIsInternalMovement() != null && order.getIsInternalMovement().equals("true")) {
				pallEternalMowment = pallEternalMowment + Integer.parseInt(order.getPall().trim());	
				if(map2.containsKey(order.getCounterparty())) {
					Integer old = (Integer) map2.get(order.getCounterparty());
					map2.put("внутренние перемещения: " + order.getCounterparty(), old + Integer.parseInt(order.getPall().trim()));					
				}else {
					map2.put("внутренние перемещения: " + order.getCounterparty(), Integer.parseInt(order.getPall().trim()));
				}
				continue;
			};
			
//			DateRange dateRange = readerSchedulePlan.getDateRangeV2(schedulesMap.get(Long.parseLong(order.getMarketContractType())), order);
			DateRange dateRange = null;
			try {
				dateRange = readerSchedulePlan.getDateRangeV2(schedulesMap.get(Long.parseLong(order.getMarketContractType())), order);				
			} catch (NullPointerException e) {
				RangeCheckResult dateInRangeWithNote = new RangeCheckResult(false, "График поставок был изменен");
				System.out.println(dateRange + " ->"+dateInRangeWithNote + ";  pall = " +order.getPall().trim());
				if(!dateInRangeWithNote.inRange) {
					pall = pall +  Integer.parseInt(order.getPall().trim());
					pallOut = pallOut +  Integer.parseInt(order.getPall().trim());
					if(map2.containsKey(order.getCounterparty())) {
						Integer old = (Integer) map2.get(order.getCounterparty());
						map2.put(order.getCounterparty(), old + Integer.parseInt(order.getPall().trim()));					
					}else {
						map2.put(order.getCounterparty(), Integer.parseInt(order.getPall().trim()));
					}
				}
				continue;
			}
			
			RangeCheckResult dateInRangeWithNote = isDateInRangeWithNote(order.getTimeDelivery().toLocalDateTime().toLocalDate(), dateRange);
			System.out.println(dateRange + " ->"+dateInRangeWithNote + ";  pall = " +order.getPall().trim());
//			message.append(" ->"+dateInRangeWithNote + ";  pall = " +order.getPall().trim() + "\n");
			if(!dateInRangeWithNote.inRange) {
				pall = pall +  Integer.parseInt(order.getPall().trim());
				if(map2.containsKey(order.getCounterparty())) {
					Integer old = (Integer) map2.get(order.getCounterparty());
					map2.put(order.getCounterparty(), old + Integer.parseInt(order.getPall().trim()));					
				}else {
					map2.put(order.getCounterparty(), Integer.parseInt(order.getPall().trim()));
				}
				
			}
			
		}
		map.put("finalPall", pall);
		map.put("finalEternalMowmentPall", pallEternalMowment);
		map.put("finalPallOut", pallOut);
		map.put("description", map2);
//		map.put("message", message.toString());
		return map;
    }
	
	public RangeCheckResult isDateInRangeWithNote(LocalDate targetDate, DateRange range) {
	    if (targetDate == null) {
	        return new RangeCheckResult(false, "Целевая дата отсутствует");
	    }
	    
	    if(range == null) {
	    	return new RangeCheckResult(false, "тсутствует просчёт DateRange");
	    }

	    boolean inRange = !targetDate.isBefore(range.start.toLocalDate()) &&
	                      !targetDate.isAfter(range.end.toLocalDate());

	    if (!inRange) {
	        return new RangeCheckResult(false, "Дата вне диапазона");
	    }

	    if (!targetDate.equals(range.end.toLocalDate())) {
	        return new RangeCheckResult(true, "Дата входит в диапазон, но не совпадает с датой окончания");
	    }

	    return new RangeCheckResult(true, null); // Всё ок, без пометки
	}

	
	public class RangeCheckResult {
	    public final boolean inRange;
	    public final String note;

	    public RangeCheckResult(boolean inRange, String note) {
	        this.inRange = inRange;
	        this.note = note;
	    }

	    @Override
	    public String toString() {
	        return "inRange=" + inRange + (note != null ? ", note=" + note : "");
	    }
	    
	}

	
	@GetMapping("/logistics/info-carrier/list")
	public Map<String, Object> getInfoCarrier(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responce = new HashMap<String, Object>();
		responce.put("status", "200");
		responce.put("objects", infoCarrierService.getAll());		
		return responce;
    }
	
	@GetMapping("/logistics/info-carrier/list/{dateStart}&{dateFinish}")
	public Map<String, Object> getInfoCarrierHasDates(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Date dateStart, @PathVariable Date dateFinish) throws IOException{
		Map<String, Object> responce = new HashMap<String, Object>();		
		responce.put("status", "200");
		responce.put("objects", infoCarrierService.getFromDate(dateStart, dateFinish));		
		return responce;
    }
	
	@GetMapping("/logistics/info-carrier/sendEmail/{id}")
	public Map<String, Object> getInfoCarrierHasDates(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Integer id) throws IOException{
		Map<String, Object> responce = new HashMap<String, Object>();
		InfoCarrier infoCarrier = infoCarrierService.getById(id);
		if(infoCarrier == null) {
        	responce.put("status", "100");
        	responce.put("message", "Объект не найден");
        	return responce;
        }
		User user = getThisUser();
		List <String> emails = Arrays.asList(infoCarrier.getEmailAddress(), user.geteMail());
		mailService.sendAsyncEmailToUsers(request, "Регистрация на грузовой платформе ЗАО «ДОБРОНОМ»", "Пройдите по ссылке-приглашению https://boxlogs.net/speedlogist/main/registration для регистрации на грузовой платформе ЗАО» ДОБРОНОМ»\n\n\nС уважением, команда\r\n"
				+ "ЗАО «ДОБРОНОМ»", emails);
		infoCarrier.setStatus(20);
		infoCarrier.setOtlResponsibleSpecialist(user.getSurname() + " " + user.getName() + "; " + user.getTelephone());
		infoCarrier.setDateSendRegLink(Timestamp.valueOf(LocalDateTime.now()));
		infoCarrierService.update(infoCarrier);
		responce.put("status", "200");
		responce.put("object", infoCarrier);		
		return responce;
    }
	
	@PostMapping("/logistics/info-carrier/update")
	public Map<String, Object> postInfoCarrierUpdate(@RequestBody JsonNode jsonNode) {
	    Map<String, Object> response = new HashMap<>();

	    int id = jsonNode.get("id").asInt();
	    InfoCarrier info = infoCarrierService.getById(id);
	    if (info == null) {
	        response.put("status", "100");
	        response.put("message", "Объект не найден");
	        return response;
	    }

	    if (jsonNode.has("dateTimeCreate")) {
	        info.setDateTimeCreate(jsonNode.get("dateTimeCreate").isNull() ? null : new Timestamp(jsonNode.get("dateTimeCreate").asLong()));
	    }
	    if (jsonNode.has("cargoTransportMarket")) {
	        info.setCargoTransportMarket(jsonNode.get("cargoTransportMarket").isNull() ? null : jsonNode.get("cargoTransportMarket").asText());
	    }
	    if (jsonNode.has("ownershipType")) {
	        info.setOwnershipType(jsonNode.get("ownershipType").isNull() ? null : jsonNode.get("ownershipType").asText());
	    }
	    if (jsonNode.has("carrierName")) {
	        info.setCarrierName(jsonNode.get("carrierName").isNull() ? null : jsonNode.get("carrierName").asText());
	    }
	    if (jsonNode.has("offeredVehicleCount")) {
	        info.setOfferedVehicleCount(jsonNode.get("offeredVehicleCount").isNull() ? null : jsonNode.get("offeredVehicleCount").asText());
	    }
	    if (jsonNode.has("bodyType")) {
	        info.setBodyType(jsonNode.get("bodyType").isNull() ? null : jsonNode.get("bodyType").asText());
	    }
	    if (jsonNode.has("hasTailLift")) {
	        info.setHasTailLift(jsonNode.get("hasTailLift").isNull() ? null : jsonNode.get("hasTailLift").asText());
	    }
	    if (jsonNode.has("hasNavigation")) {
	        info.setHasNavigation(jsonNode.get("hasNavigation").isNull() ? null : jsonNode.get("hasNavigation").asText());
	    }
	    if (jsonNode.has("vehicleLocationCity")) {
	        info.setVehicleLocationCity(jsonNode.get("vehicleLocationCity").isNull() ? null : jsonNode.get("vehicleLocationCity").asText());
	    }
	    if (jsonNode.has("contactPhone")) {
	        info.setContactPhone(jsonNode.get("contactPhone").isNull() ? null : jsonNode.get("contactPhone").asText());
	    }
	    if (jsonNode.has("emailAddress")) {
	        info.setEmailAddress(jsonNode.get("emailAddress").isNull() ? null : jsonNode.get("emailAddress").asText());
	    }
	    if (jsonNode.has("offeredRate")) {
	        info.setOfferedRate(jsonNode.get("offeredRate").isNull() ? null : jsonNode.get("offeredRate").asText());
	    }
	    if (jsonNode.has("notes")) {
	        info.setNotes(jsonNode.get("notes").isNull() ? null : jsonNode.get("notes").asText());
	    }
	    if (jsonNode.has("applicationStatus")) {
	        info.setApplicationStatus(jsonNode.get("applicationStatus").isNull() ? null : jsonNode.get("applicationStatus").asText());
	    }
	    if (jsonNode.has("carrierContactDate")) {
	        info.setCarrierContactDate(jsonNode.get("carrierContactDate").isNull() ? null : new Timestamp(jsonNode.get("carrierContactDate").asLong()));
	    }
	    if (jsonNode.has("otlResponsibleSpecialist")) {
	        info.setOtlResponsibleSpecialist(jsonNode.get("otlResponsibleSpecialist").isNull() ? null : jsonNode.get("otlResponsibleSpecialist").asText());
	    }
	    if (jsonNode.has("comment")) {
	        info.setComment(jsonNode.get("comment").isNull() ? null : jsonNode.get("comment").asText());
	    }
	    if (jsonNode.has("contactCarrier")) {
	        info.setContactCarrier(jsonNode.get("contactCarrier").isNull() ? null : jsonNode.get("contactCarrier").asText());
	    }
	    if (jsonNode.has("vehicleCapacity")) {
	        info.setVehicleCapacity(jsonNode.get("vehicleCapacity").isNull() ? null : jsonNode.get("vehicleCapacity").asText());
	    }
	    if (jsonNode.has("palletCapacity")) {
	        info.setPalletCapacity(jsonNode.get("palletCapacity").isNull() ? null : jsonNode.get("palletCapacity").asText());
	    }
	    if (jsonNode.has("status")) {
	        info.setStatus(jsonNode.get("status").isNull() ? null : jsonNode.get("status").asInt());
	    }
	    if (jsonNode.has("dateSendRegLink")) {
	        info.setDateSendRegLink(jsonNode.get("dateSendRegLink").isNull() ? null : new Timestamp(jsonNode.get("dateSendRegLink").asLong()));
	    }

	    infoCarrierService.update(info);

	    response.put("status", "200");
	    response.put("object", info);
	    return response;
	}


	
	
    
    /**
     * <br>Нужно ли уведомлять о новом тендере</br>.
     * @author Ira
     */
    @GetMapping("/user/get-new-tender-notification")
    public Boolean getNewTenderNotification(HttpServletRequest request) throws ParseException, IOException {
        return getThisUser().getNewTenderNotification();
    }
    
    /**
     * <br>Внести в инфу в БД, нужно ли уведомлять о новых тендерах</br>.
     * @author Ira
     */
    @PostMapping("/user/new-tender-notification")
    public Map<String, Object> getNewTenderNotification(@RequestBody String str) throws ParseException {
        Map<String, Object> response = new HashMap<String, Object>();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        Boolean newTenderNotification =  jsonMainObject.get("newTenderNotification") == null ? null : Boolean.parseBoolean(jsonMainObject.get("newTenderNotification").toString());
        User user = getThisUser();
        user.setNewTenderNotification(newTenderNotification);
        userService.saveOrUpdateUser(user, 0);
        response.put("status", "200");
        return response;
    }
    
    /**
     * <br>Метод для превращения закрытого тендера в тендер на понижение</br>.
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @PostMapping("/logistics/tenders/make-tender-for-reduction")
    public Map<String, Object> makeTenderForReduction(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);

        Long carrierBidId = jsonMainObject.get("idCarrierBid") == null ? null : Long.parseLong(jsonMainObject.get("idCarrierBid").toString());
        Integer routeId = jsonMainObject.get("idRoute") == null ? null : Integer.parseInt(jsonMainObject.get("idRoute").toString());
        int price = jsonMainObject.get("price") == null ? null : Integer.parseInt(jsonMainObject.get("price").toString());
        String login = jsonMainObject.get("login") == null ? null : jsonMainObject.get("login").toString();
        String currency = jsonMainObject.get("currency") == null ? null : jsonMainObject.get("currency").toString();
        String comment = jsonMainObject.get("comment") == null ? null : jsonMainObject.get("comment").toString();

        Route route = routeService.getRouteById(routeId);
        if (route == null) {
            response.put("status", "100");
            response.put("message", "Такой маршрут не существует");
            return response;
        }
        if (!route.getStatusRoute().equals("1") && !route.getStatusRoute().equals("8")) {
            response.put("status", "100");
            response.put("message", "Маршрут не на бирже");
            return response;
        }

        CarrierBid carrierBid;
        if (carrierBidId != null) {
            carrierBid = carrierBidService.getById(carrierBidId);
            carrierBid.setPercent(0);
            List <CarrierBid> carrierBids = carrierBidService.getCarrierBidsByRouteId(routeId).stream().filter(c -> c.getStatus().equals(20)).collect(Collectors.toList());
            carrierBids.removeIf(c -> c.getCarrier().equals(carrierBid.getCarrier()));
            for (CarrierBid carrierBid1 : carrierBids) {
                if (!carrierBid1.getIdCarrierBid().equals(carrierBidId)) {
                    carrierBidService.delete(carrierBid1);
                }
            }
            carrierBidService.update(carrierBid);
            CarrierTenderMessage messageForWinner = new CarrierTenderMessage();
            messageForWinner.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + routeId);
            messageForWinner.setIdRoute(routeId.toString());
            messageForWinner.setToUser(carrierBid.getCarrier().getLogin());
            messageForWinner.setAction("notification");
            messageForWinner.setText("Тендер для маршрута №" + routeId + " : "
                    + (carrierBid.getRouteDirection().length() > 100 ? carrierBid.getRouteDirection().substring(0, 100) : carrierBid.getRouteDirection())
                    + "... переведён в формат понижения ставки."
                    + "<br>Ваша ставка установлена как начальная цена этого тендера.");
            messageForWinner.setStatus("200");
            messageForWinner.setWSPath("carrier-tenders");
            carrierTenderWebSocket.sendToUser(carrierBid.getCarrier().getLogin(), messageForWinner);

            CarrierTenderMessage messageForLoosers = new CarrierTenderMessage();
            messageForLoosers.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + routeId);
            messageForLoosers.setIdRoute(routeId.toString());
            messageForLoosers.setAction("notification");
            messageForLoosers.setText("Тендер для маршрута №" + routeId + " : "
                    + (carrierBid.getRouteDirection().length() > 100 ? carrierBid.getRouteDirection().substring(0, 100) : carrierBid.getRouteDirection())
                    + "... переведён в формат понижения ставки."
                    + "<br>Ваша предыдущая ставка отменена — подайте новую, чтобы участвовать.");
            messageForLoosers.setStatus("200");
            messageForLoosers.setWSPath("carrier-tenders");
            for (CarrierBid carrierBid1 : carrierBids) {
                messageForLoosers.setToUser(carrierBid1.getCarrier().getLogin());
                carrierTenderWebSocket.sendToUser(carrierBid1.getCarrier().getLogin(), messageForLoosers);
            }
            
        } else {
            carrierBid = new CarrierBid();
            carrierBid.setDateTime(new Timestamp(System.currentTimeMillis()));
            carrierBid.setPrice(price);
            carrierBid.setPercent(0);
            carrierBid.setCurrency(currency);
            User user = userService.getUserByLogin(login);
            carrierBid.setCarrier(user);
            carrierBid.setIdUser(user.getIdUser());
            carrierBid.setCompanyName(user.getCompanyName());
            carrierBid.setRoute(route);
            carrierBid.setWinner(false);
            carrierBid.setComment(comment);
            carrierBidService.save(carrierBid);
        }
        route.setForReduction(true);
        route.setStartPriceForReduction(price);
        route.setCurrencyForReduction(currency);
        Set <CarrierBid> bids = new HashSet<>();
        bids.add(carrierBid);
        route.setCarrierBids(bids);
        routeService.updateRoute(route);

        CarrierTenderMessage messageForChanging = new CarrierTenderMessage();
        messageForChanging.setRoute(route);
        messageForChanging.setIdRoute(routeId.toString());
        messageForChanging.setAction("change-tender-type");
        messageForChanging.setStatus("200");
        messageForChanging.setWSPath("carrier-tenders");
        carrierTenderWebSocket.broadcast(messageForChanging);


        response.put("status", "200");
        response.put("route", route);
        return response;
    }
	
	/**
     * <br>Метод для установки в маршрут машины</br>.
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @PostMapping("/carrier/transportation/set-route-parameters")
    public Map<String, Object> setRouteParameters(HttpServletRequest request, @RequestBody String str) throws ParseException {
        Map<String, Object> response = new HashMap<String, Object>();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        Integer idDriver = jsonMainObject.get("idDriver") == null ? null : Integer.parseInt(jsonMainObject.get("idDriver").toString());
        Integer idTruck = jsonMainObject.get("idTruck") == null ? null : Integer.parseInt(jsonMainObject.get("idTruck").toString());
        Integer idRoute = jsonMainObject.get("idRoute") == null ? null : Integer.parseInt(jsonMainObject.get("idRoute").toString());
        String dateLoad = jsonMainObject.get("dateLoadActually") == null ? null : jsonMainObject.get("dateLoadActually").toString();
        String dateUnload = jsonMainObject.get("dateUnloadActually") == null ? null : jsonMainObject.get("dateUnloadActually").toString();
        String timeUnload = jsonMainObject.get("timeUnloadActually") == null ? null : jsonMainObject.get("timeUnloadActually").toString();
        String timeLoad = jsonMainObject.get("timeLoadActually") == null ? null : jsonMainObject.get("timeLoadActually").toString();
        Integer expeditionCost = jsonMainObject.get("expeditionCost") == null ? null : Integer.parseInt(jsonMainObject.get("expeditionCost").toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        Route route = routeService.getRouteById(idRoute);
        route.setDateUnloadActually(LocalDate.parse(dateUnload, formatter));
        route.setTimeUnloadActually(LocalTime.parse(timeUnload, formatterTime));
        route.setDateLoadActually(LocalDate.parse(dateLoad, formatter));
        route.setTimeLoadActually(LocalTime.parse(timeLoad, formatterTime));

        if(route.getWay().equals("Импорт") && route.getExpeditionCost() == null) {
            route.setExpeditionCost(expeditionCost);
        }

        if(idTruck != null) {
            try {
                Truck truck = truckService.getTruckById(idTruck);
                route.setTruck(truck);
            } catch (NoSuchElementException e) {
                response.put("status", "100");
                response.put("message", "Такого автомобиля не существует");
                return response;
            }
        }
        if(idDriver != null) {
            try {
                User driver = userService.getUserById(idDriver);
                route.setDriver(driver);
            } catch (NoSuchElementException e) {
                response.put("status", "100");
                response.put("message", "Такого водителя не существует");
                return response;
            }
        }

        route.setStatusRoute("4");
        routeService.saveOrUpdateRoute(route);
        response.put("route", route);
        response.put("status", "200");
        return response;
    }
	
	/**
	 * Редактирование правил код товара - склад
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@PostMapping("/procurement/product-control/edit")
    @TimedExecution
    public Map<String, Object> postGoodAccommodationEdit(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
       Map<String, Object> response = new HashMap<>();
       JSONParser parser = new JSONParser();
       JSONObject jsonMainObject = (JSONObject) parser.parse(str);
//       User user = getThisUser();
       
       Long id = Long.parseLong(jsonMainObject.get("idGoodAccommodation").toString());       
       GoodAccommodation goodAccommodation = goodAccommodationService.getGoodAccommodationById(id);
       if(goodAccommodation == null) {
    	   response.put("status", "100");
    	   response.put("message", "Директива не найдена");
           return response;
       }
       goodAccommodation.setBarcode(jsonMainObject.get("barcode") != null && !jsonMainObject.get("barcode").toString().isEmpty() ? Long.parseLong(jsonMainObject.get("barcode").toString()) : null);
       goodAccommodation.setGoodName(jsonMainObject.get("goodName").toString());
       goodAccommodation.setProductCode(Long.parseLong(jsonMainObject.get("productCode").toString().trim()));
       goodAccommodation.setProductGroup(jsonMainObject.get("productGroup").toString());
       goodAccommodation.setStatus(Integer.parseInt(jsonMainObject.get("status").toString()));
       goodAccommodation.setStocks(jsonMainObject.get("stocks").toString());
       goodAccommodationService.update(goodAccommodation);   
       
       String statusValue = "";
       switch (goodAccommodation.getStatus()) {
		case 10:
			statusValue = "ожидает подтверждения";
			break;
			
		case 20:
			statusValue = "действует";
			break;

		}
       
       String eMailMessage = "Данные по товару изменены: статус - " + statusValue + "; определен на склад: " + goodAccommodation.getStocks();
       List<String> emails = Arrays.asList(goodAccommodation.getInitiatorEmail());
       mailService.sendAsyncEmailToUsers(request, "Статус по товару", eMailMessage, emails);
       response.put("status", "200");
       response.put("object", goodAccommodation);
       return response;
    }
	
	/**
	 * Метод отдаёт все GoodAccommodation на фронт
	 * @param request
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@GetMapping("/procurement/product-control/getAll")
    public Map<String, Object> getProductControlAll(HttpServletRequest request) throws ParseException, IOException {
       Map<String, Object> response = new HashMap<>();
       response.put("objects", goodAccommodationService.getAll());
       response.put("status", "200");
       return response;
    }
	
	/**
	 * Метод отвечает за загрузку данных товар-склад из екселя
	 * @param model
	 * @param request
	 * @param session
	 * @param excel
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/procurement/product-control/load", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, String> postProductControlLoad(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "excel", required = false) MultipartFile excel) throws IOException {
		Map<String, String> response = new HashMap<String, String>();	
//		File file1 = poiExcel.getFileByMultipart(excel);
		poiExcel.importGoodAccommodation(excel.getInputStream());
		response.put("status", "200");
		response.put("message", "Готово");
		return response;
	}
	
	
    /**
     * <br>getThisUser для фронта</br>.
     * @author Ira
     */
    @GetMapping("/get-this-user")
    @TimedExecution
    public Map<String, Object> getThisUserToFront(HttpServletRequest request) throws ParseException, IOException {
       Map<String, Object> response = new HashMap<>();
       Integer userId =  getThisUser().getIdUser();
       response.put("userId", userId);
       return response;
    }

    /**
     * <br>Метод для получения списка ставок</br>.
     * @param request
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @GetMapping("/carrier-tenders/get-carrier-bids-list/{dateStart}&{dateEnd}")
    @TimedExecution
    public Map<String, Object> getCarrierBidsList(@PathVariable String dateStart, @PathVariable String dateEnd) {
       Map<String, Object> response = new HashMap<>();
       Date dateFrom = Date.valueOf(dateStart);
       Date dateTo = Date.valueOf(dateEnd);
       List<CarrierBid> carrierBids = carrierBidService.getCarrierBidsByDate(dateFrom, dateTo);
       response.put("bidList", carrierBids);
       return response;
    }

    /**
     * <br>Метод для выбора ставки-победителя</br>.
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @PostMapping("/logistics/tenders/make-bid-winner")
    @TimedExecution
    public Map<String, Object> makeBidWinner(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        Long idCarrierBid = jsonMainObject.get("idCarrierBid") != null || !jsonMainObject.get("idCarrierBid").toString().isEmpty() ? Long.parseLong(jsonMainObject.get("idCarrierBid").toString()) : null;
        String status = jsonMainObject.get("status") == null ? null : jsonMainObject.get("status").toString();
        String logistComment = jsonMainObject.get("logistComment") == null ? null : jsonMainObject.get("logistComment").toString();
        CarrierBid carrierBid = carrierBidService.getById(idCarrierBid);
        if (carrierBid == null) {
            response.put("status", "100");
            response.put("message", "Выбранное предложение было отменено.");
            return response;
        }

        Integer idRoute = carrierBid.getRoute().getIdRoute();
        int idUser = carrierBid.getIdUser();
        int cost = carrierBid.getPrice();
        String currency = carrierBid.getCurrency();
        Order order = orderService.getOrderByIdRoute(idRoute);
        if (order != null && order.getStatus() == 10) {
            response.put("status", "100");
            response.put("message", "Заявка не найдена");
            return response;
        }
//        User user = userService.getUserById(idUser);
        User user = new User();
        user.setIdUser(idUser);
        if (status == null) {
            status = "4";
            routeService.updateRouteInBase(idRoute, cost, currency, user, status);
        } else {
            routeService.updateRouteInBase(idRoute, cost, currency, user, status);
        }

        carrierBid.setWinner(true);
        carrierBid.setLogistComment(logistComment);
        carrierBidService.update(carrierBid);

        //меняем статус у Order если имеется
        Route routeTarget = routeService.getRouteById(idRoute);
        Set<Order> orders = routeTarget.getOrders();
        if (orders != null && !orders.isEmpty()) {
            orders.forEach(o -> {
                o.setStatus(60);
                o.setChangeStatus(o.getChangeStatus() + "\nМаршрут " + idRoute + " выигран " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:MM:ss")));
                orderService.updateOrder(o);
            });
        }

        if (!status.equals("8")) {
            CarrierTenderMessage messageForWinner = new CarrierTenderMessage();
            messageForWinner.setUrl("/speedlogist/main/carrier/transportation");
            messageForWinner.setIdRoute(idRoute.toString());
            messageForWinner.setToUser(carrierBid.getCarrier().getLogin());
            messageForWinner.setAction("notification");
            messageForWinner.setText("Ваше предложение к маршруту №" + idRoute + " : "
                    + (carrierBid.getRouteDirection().length() > 100 ? carrierBid.getRouteDirection().substring(0, 100) : carrierBid.getRouteDirection())
                    + "... с ценой " + cost + " " + currency + " <b>одобрено!</b>"
                    + "<br>Необходимо назначить машину и водителя.");
            messageForWinner.setStatus("200");
            messageForWinner.setWSPath("carrier-tenders");
            carrierTenderWebSocket.sendToUser(carrierBid.getCarrier().getLogin(), messageForWinner);
        }

        List<CarrierBid> actualBids = carrierBidService.getActualCarrierBidsByRouteId(carrierBid.getRoute().getIdRoute());
        actualBids.removeIf(c -> c.getCarrier().equals(carrierBid.getCarrier()));
        CarrierTenderMessage messageForLooser = new CarrierTenderMessage();
        messageForLooser.setIdRoute(idRoute.toString());
        messageForLooser.setToUser(carrierBid.getCarrier().getLogin());
        messageForLooser.setAction("notification");
        messageForLooser.setText("К сожалению, предложенная Вами цена для маршрута №" + idRoute + " : "
                + (carrierBid.getRouteDirection().length() > 100 ? carrierBid.getRouteDirection().substring(0, 100) : carrierBid.getRouteDirection())
                + "... нам <b>не подходит</b>.");
        messageForLooser.setStatus("200");
        messageForLooser.setWSPath("carrier-tenders");
        for(CarrierBid bid : actualBids) {
            messageForLooser.setToUser(bid.getCarrier().getLogin());
            carrierTenderWebSocket.sendToUser(bid.getCarrier().getLogin(), messageForLooser);
        }
        CarrierTenderMessage message = new CarrierTenderMessage();
        message.setIdRoute(idRoute.toString());
        message.setAction("finish-tender");
        message.setStatus("200");
        message.setWSPath("carrier-tenders");
        carrierTenderWebSocket.broadcast(message);

        response.put("status", "200");
        response.put("message", "Маршрут создан");
        response.put("route", routeTarget);
        return response;
    }
    
    /**
     * <br>Метод для удаления ставки ЛОГИСТОМ</br>.
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @PostMapping("/logistics/tenders/delete-bid")
    @TimedExecution
    public Map<String, Object> deleteBidByLogist(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
        Map<String, Object> response = new HashMap<>();
        User user = getThisUser();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        Long bidId = jsonMainObject.get("idCarrierBid") == null ? null : Long.parseLong(jsonMainObject.get("idCarrierBid").toString());
        CarrierBid carrierBid = carrierBidService.getById(bidId);
        if (carrierBid == null) {
            response.put("status", "100");
            response.put("message", "Такая ставка не найдена");
            return response;
        }
        Integer routeId = jsonMainObject.get("idRoute") == null ? null : Integer.parseInt(jsonMainObject.get("idRoute").toString());
        Route route = routeService.getRouteById(routeId);
        if(!route.getForReduction()) {
            response.put("status", "100");
            response.put("message", "Запрещено! Только для тендеров на понижение");
            return response;
        }
        String logistComment = jsonMainObject.get("logistComment") == null ? null : jsonMainObject.get("logistComment").toString();
        carrierBid.setLogistComment(logistComment);
        carrierBid.setStatus(10);
        carrierBid.setUserHasChange(user.getSurname() +" "+ user.getName());
        carrierBid.setDatetimeChange(Timestamp.valueOf(LocalDateTime.now()));
        carrierBidService.update(carrierBid);
        
        List<CarrierBid> bids = carrierBidService.getCarrierBidsByRouteId(routeId);
        List<CarrierBid> sortedBids = bids.stream().sorted(Comparator.comparing(CarrierBid::getPrice)).collect(Collectors.toList());
        List<CarrierBid> filteredBids = sortedBids.stream().filter(c -> c.getStatus().equals(20)).collect(Collectors.toList());

        CarrierTenderMessage messageForCancelled = new CarrierTenderMessage();

        messageForCancelled.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + routeId);
        messageForCancelled.setIdRoute(routeId.toString());
        messageForCancelled.setToUser(carrierBid.getCarrier().getLogin());
        messageForCancelled.setAction("notification");
        messageForCancelled.setText("Ваша ставка для маршрута №" + routeId + " : "
                + (carrierBid.getRouteDirection().length() > 100 ? carrierBid.getRouteDirection().substring(0, 100) : carrierBid.getRouteDirection())
                + "... была <b>удалена логистом</b>.");
        messageForCancelled.setStatus("200");
        messageForCancelled.setWSPath("carrier-tenders");
        carrierTenderWebSocket.sendToUser(carrierBid.getCarrier().getLogin(), messageForCancelled);

        if (route.getForReduction()) {
            if (!filteredBids.isEmpty()) {

                if (!filteredBids.get(0).getCarrier().equals(carrierBid.getCarrier())) {

                    CarrierBid latestBid = filteredBids.get(0);

                    CarrierTenderMessage messageForWinnerAgain = new CarrierTenderMessage();
                    messageForWinnerAgain.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + routeId);
                    messageForWinnerAgain.setIdRoute(routeId.toString());
                    messageForWinnerAgain.setToUser(latestBid.getCarrier().getLogin());
                    messageForWinnerAgain.setAction("notification");
                    messageForWinnerAgain.setText("Ваша ставка для маршрута №" + routeId + " : "
                            + (carrierBid.getRouteDirection().length() > 100 ? carrierBid.getRouteDirection().substring(0, 100) : carrierBid.getRouteDirection())
                            + "... <b>снова актуальна</b>.");
                    messageForWinnerAgain.setStatus("200");
                    messageForWinnerAgain.setWSPath("carrier-tenders");
                    carrierTenderWebSocket.sendToUser(latestBid.getCarrier().getLogin(), messageForWinnerAgain);
                    filteredBids.remove(latestBid);
                }

                filteredBids.remove(carrierBid);

                for (CarrierBid bid : filteredBids) {
                    CarrierTenderMessage messageForOthers = new CarrierTenderMessage();
                    messageForOthers.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + routeId);
                    messageForOthers.setIdRoute(routeId.toString());
                    messageForOthers.setToUser(bid.getCarrier().getLogin());
                    messageForOthers.setAction("notification");
                    messageForOthers.setText("Лидирующая ставка для маршрута №" + routeId + " : "
                            + (bid.getRouteDirection().length() > 100 ? bid.getRouteDirection().substring(0, 100) : bid.getRouteDirection())
                            + "... <b>отменена</b>."
                            + "<br>Актуальная цена <b>" + bid.getPrice() + " " + bid.getCurrency() + "</b>.");
                    messageForOthers.setStatus("200");
                    messageForOthers.setWSPath("carrier-tenders");
                    carrierTenderWebSocket.sendToUser(bid.getCarrier().getLogin(), messageForOthers);
                }
            }
        }

        CarrierTenderMessage message = new CarrierTenderMessage();
        message.setIdRoute(routeId.toString());
        message.setAction("delete");
        message.setStatus("200");
        message.setCarrierBid(carrierBid);
        message.setWSPath("carrier-tenders");
        carrierTenderWebSocket.broadcast(message);
        response.put("bid", carrierBid);
        response.put("status", "200");
        return response;
    }
    
    /**
     * <br>Метод для удаления ставки ПЕРЕВОЗЧИКОМ</br>.
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @PostMapping("/carrier/tenders/delete-bid")
    public Map<String, Object> deleteBidByCarrier(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
        Map<String, Object> response = new HashMap<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        Long bidId = jsonMainObject.get("idCarrierBid") == null ? null : Long.parseLong(jsonMainObject.get("idCarrierBid").toString());
        Integer routeId = jsonMainObject.get("idRoute") == null ? null : Integer.parseInt(jsonMainObject.get("idRoute").toString());
        List<CarrierBid> bids = carrierBidService.getCarrierBidsByRouteId(routeId);
        Route route = routeService.getRouteById(routeId);

        if (route.getStatusRoute().equals("4") || route.getStatusRoute().equals("8")) {
            response.put("status", "100");
            response.put("message", "Тендер по данному маршруту уже закрыт. Обновите страницу.");
            return response;
        }

        CarrierBid carrierBid = carrierBidService.getById(bidId);
        carrierBidService.delete(carrierBidService.getById(bidId));
        if (!bids.isEmpty()) {
            List<CarrierBid> actualBids = bids.stream().filter(c -> c.getStatus().equals(20)).sorted(Comparator.comparing(CarrierBid::getPrice)).collect(Collectors.toList());
            if(!actualBids.isEmpty()) {
                CarrierBid latestBid = actualBids.get(0);
                if (latestBid.getPrice() > carrierBid.getPrice()) {
                    CarrierTenderMessage messageForWinnerAgain = new CarrierTenderMessage();
                    messageForWinnerAgain.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + routeId);
                    messageForWinnerAgain.setIdRoute(routeId.toString());
                    messageForWinnerAgain.setToUser(latestBid.getCarrier().getLogin());
                    messageForWinnerAgain.setAction("notification");
                    messageForWinnerAgain.setText("Ваша ставка для маршрута №" + routeId + " : "
                            + (latestBid.getRouteDirection().length() > 100 ? latestBid.getRouteDirection().substring(0, 100) : latestBid.getRouteDirection())
                            + "... <b>снова актуальна</b>.");
                    messageForWinnerAgain.setStatus("200");
                    messageForWinnerAgain.setWSPath("carrier-tenders");
                    carrierTenderWebSocket.sendToUser(latestBid.getCarrier().getLogin(), messageForWinnerAgain);

                    actualBids.remove(latestBid);
                    for (CarrierBid bid : actualBids) {
                        CarrierTenderMessage messageForOthers = new CarrierTenderMessage();
                        messageForOthers.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + routeId);
                        messageForOthers.setIdRoute(routeId.toString());
                        messageForOthers.setToUser(bid.getCarrier().getLogin());
                        messageForOthers.setAction("notification");
                        messageForOthers.setText("Лидирующая ставка для маршрута №" + routeId + " : "
                                + (latestBid.getRouteDirection().length() > 100 ? latestBid.getRouteDirection().substring(0, 100) : latestBid.getRouteDirection())
                                + "... <b>отменена</b>." +
                                "<br>Актуальная цена <b>" + bid.getPrice() + " " + bid.getCurrency() + "</b>.");
                        messageForOthers.setStatus("200");
                        messageForOthers.setWSPath("carrier-tenders");
                        carrierTenderWebSocket.sendToUser(bid.getCarrier().getLogin(), messageForOthers);
                    }
                }
            }
        }
        CarrierTenderMessage message = new CarrierTenderMessage();
        message.setIdRoute(routeId.toString());
        message.setAction("delete");
        message.setStatus("200");
        message.setCarrierBid(carrierBid);
        message.setWSPath("carrier-tenders");
        carrierTenderWebSocket.broadcast(message);
        response.put("bid", carrierBid);
        response.put("status", "200");
        return response;
    }

    /**
     * <br>Метод для получения предложений по idRoute</br>.
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @GetMapping("/logistics/tenders/get-bids-by-id-route/{idRoute}")
    public List<CarrierBid> getBidsByRouteId(@PathVariable String idRoute) throws ParseException, IOException {
        Map<String, Object> response = new HashMap<>();
        Integer routeId = Integer.parseInt(idRoute);
        List<CarrierBid> bids = carrierBidService.getCarrierBidsByRouteId(routeId);
        return bids;
     }

    /**
     * <br>Метод для удаления ставки</br>.
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
//    @PostMapping("/carrier/tenders/delete-bid")
//    public Map<String, Object> deleteBid(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
//        Map<String, Object> response = new HashMap<>();
//        JSONParser parser = new JSONParser();
//        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
//        User user = getThisUser();
//        Long bidId = jsonMainObject.get("idCarrierBid") == null ? null : Long.parseLong(jsonMainObject.get("idCarrierBid").toString());
//        CarrierBid carrierBid = carrierBidService.getById(bidId);
//        carrierBidService.delete(carrierBidService.getById(bidId));
//        Integer routeId = jsonMainObject.get("idRoute") == null ? null : Integer.parseInt(jsonMainObject.get("idRoute").toString());
//        List<CarrierBid> bids = carrierBidService.getCarrierBidsByRouteId(routeId);
//        if (!bids.isEmpty()) {
//            CarrierBid latestBid = bids.stream().sorted(Comparator.comparing(CarrierBid::getPrice)).collect(Collectors.toList()).get(0);
//            if (latestBid.getPrice() > carrierBid.getPrice()) {
//                Route route = routeService.getRouteById(routeId);
//                CarrierTenderMessage messageForWinnerAgain = new CarrierTenderMessage();
//                messageForWinnerAgain.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + routeId);
//                messageForWinnerAgain.setIdRoute(routeId.toString());
//                messageForWinnerAgain.setToUser(latestBid.getCarrier().getLogin());
//                messageForWinnerAgain.setAction("notification");
//                messageForWinnerAgain.setText("Ваша ставка для маршрута " + route.getRouteDirection() + " <b>снова актуальна</b>.");
//                messageForWinnerAgain.setStatus("200");
//                messageForWinnerAgain.setWSPath("carrier-tenders");
//                carrierTenderWebSocket.sendToUser(latestBid.getCarrier().getLogin(), messageForWinnerAgain);
//            }
//
//        }
//
//        CarrierTenderMessage message = new CarrierTenderMessage();
//        message.setIdRoute(routeId.toString());
//        message.setAction("delete");
//        message.setStatus("200");
//        message.setCarrierBid(carrierBid);
//        message.setWSPath("carrier-tenders");
//        carrierTenderWebSocket.broadcast(message);
//        response.put("bid", carrierBid);
//        response.put("status", "200");
//        return response;
//    }
    
    /**
     * <br>Метод для получения ставки</br>.
     * Главный метод плучения ставки
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @PostMapping("/carrier/tenders/get-bid")
    public Map<String, Object> getBid(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
        Map<String, Object> response = new HashMap<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        CarrierBid carrierBid = new CarrierBid();
        User user = getThisUser();
        carrierBid.setCarrier(user);
        carrierBid.setPrice(jsonMainObject.get("price") == null ? null : Integer.parseInt(jsonMainObject.get("price").toString()));
        carrierBid.setComment(jsonMainObject.get("comment") == null ? null : jsonMainObject.get("comment").toString());
        carrierBid.setPercent(jsonMainObject.get("percent") == null ? null : Integer.parseInt(jsonMainObject.get("percent").toString()));
        carrierBid.setCurrency(jsonMainObject.get("currency") == null ? null : jsonMainObject.get("currency").toString());
        carrierBid.setDateTime(new Timestamp(System.currentTimeMillis()));
        Integer routeId = jsonMainObject.get("idRoute") == null ? null : Integer.parseInt(jsonMainObject.get("idRoute").toString());
        carrierBid.setIdUser(user.getIdUser());
        carrierBid.setCompanyName(user.getCompanyName());
        Route route = routeService.getRouteById(routeId);
        carrierBid.setRoute(route);
        carrierBid.setWinner(false);
        carrierBid.setRouteDirection(route.getRouteDirection());
        carrierBid.setStatus(20);
        List<CarrierBid> bids = carrierBidService.getCarrierBidsByRouteId(routeId);

        if (!bids.isEmpty()) {
            for (CarrierBid bid: bids) {
                if (bid.getWinner()) {
                    response.put("status", "100");
                    response.put("message", "Тендер завершён. Ставки больше не принимаются");
                    return response;
                }
            }
            List<CarrierBid> actualBids = bids.stream().filter(c -> c.getStatus().equals(20)).sorted(Comparator.comparing(CarrierBid::getPrice)).collect(Collectors.toList());
           if(!actualBids.isEmpty()) {
               CarrierBid latestBid = actualBids.get(0);
               if (route.getForReduction()) {
                   if (carrierBid.getPercent() != 99 && latestBid.getPrice() <= carrierBid.getPrice()) {
                       response.put("status", "100");
                       response.put("message", "Ваша ставка не последняя. Актуальная цена " + latestBid.getPrice() + " " + latestBid.getCurrency() + ".");
                       return response;
                   }
               }

               if (route.getForReduction()) {
                   if (!carrierBid.getCarrier().equals(latestBid.getCarrier())) {
                       CarrierTenderMessage messageForLooser = new CarrierTenderMessage();
                       messageForLooser.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + routeId);
                       messageForLooser.setIdRoute(routeId.toString());
                       messageForLooser.setToUser(actualBids.get(0).getCarrier().getLogin());
                       messageForLooser.setAction("notification");
                       messageForLooser.setText("Ваша ставка по маршруту №" + routeId + " : "
                               + (carrierBid.getRouteDirection().length() > 100 ? carrierBid.getRouteDirection().substring(0, 100) : carrierBid.getRouteDirection())
                               + "... была <b>перебита</b>."
                               + "<br>Текущая ставка: <b>" + carrierBid.getPrice() + " " + carrierBid.getCurrency() + "</b>"
                               + "<br>Вы можете снизить свою ставку.");
                       messageForLooser.setStatus("200");
                       messageForLooser.setWSPath("carrier-tenders");
                       carrierTenderWebSocket.sendToUser(actualBids.get(0).getCarrier().getLogin(), messageForLooser);
                       // цикл
                   }
               }
           }
        }

        CarrierBid carrierBidOld = carrierBidService.getCarrierBidByRouteAndUser(routeId, user);
        if (carrierBidOld != null && carrierBidOld.getStatus().equals(20)) {
           carrierBid.setIdCarrierBid(carrierBidOld.getIdCarrierBid());
           carrierBidService.update(carrierBid);
        } else {
           Long carrierBidId = carrierBidService.save(carrierBid);
           carrierBid.setIdCarrierBid(carrierBidId);
        }

        CarrierTenderMessage message = new CarrierTenderMessage();
        message.setIdRoute(route.getIdRoute().toString());
        message.setAction("create");
        message.setCarrierBid(carrierBid);
        message.setStatus("200");
        message.setWSPath("carrier-tenders");
        carrierTenderWebSocket.broadcast(message);
        response.put("bid", carrierBid);
        response.put("status", "200");
        return response;
     }

    /**
     * <br>Метод для отправки на фронт всех тендеров</br>.
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @GetMapping("/carrier/tenders/all")
    @TimedExecution
    public Map<String, Object> getActualTenders(HttpServletRequest request) throws ParseException, IOException {
        Map<String, Object> response = new HashMap<>();
        LocalDate dateNow = LocalDate.now();
        List<Route> routes = routeService.getActualRoute(Date.valueOf(dateNow));
//       List<Route> routes = routeService.getAllActualRoute(Date.valueOf(dateNow.toString()));
        response.put("routes", routes);
        return response;
    }
    /*
     * ----------------------------------------------
     */
	
	@PostMapping("/logistics/internationalNew/confrom")
	public Map<String, Object> confromCostNew(Model model, HttpServletRequest request,
			@RequestBody String str) throws ParseException {
		String appPath = request.getServletContext().getRealPath("");
		Map<String, Object> response = new HashMap<String, Object>();
		JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        
        Integer idRoute = Integer.parseInt(jsonMainObject.get("idRoute").toString().trim());
        Integer cost = Integer.parseInt(jsonMainObject.get("cost").toString().trim());
        String currency = jsonMainObject.get("currency") != null || !jsonMainObject.get("currency").toString().isEmpty() ? jsonMainObject.get("currency").toString() : null;
        String status = jsonMainObject.get("status") != null || !jsonMainObject.get("status").toString().isEmpty() ? jsonMainObject.get("status").toString() : null;
        String login = jsonMainObject.get("login") != null || !jsonMainObject.get("login").toString().isEmpty() ? jsonMainObject.get("login").toString() : null;
		
		//обработка, если удалось нажать на кнопку
		Order order = orderService.getOrderByIdRoute(idRoute);
		if(order != null && order.getStatus() == 10) {
			response.put("status", "100");
			response.put("message", "Заявка не найдена");
			return response;
		}
		User user = userService.getUserByLogin(login.trim());
		if (status == null) {
			status = "4";
			routeService.updateRouteInBase(idRoute, cost, currency, user, status);
		}else {
			routeService.updateRouteInBase(idRoute, cost, currency, user, status);
			Route route = routeService.getRouteById(idRoute);				
			String message = "На маршрут "+route.getRouteDirection()+" принят единственный заявившийся перевозчик: " + user.getCompanyName() 
					+ ". Заявленная стоимость перевозки составляет "+ route.getFinishPrice() + " "+ route.getStartCurrency() + ". \nОптимальная стоимость составляет " + route.getOptimalCost()+" BYN";
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
                     new FileOutputStream(appPath + "resources/others/hashmap.ser");
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
		response.put("status", "200");
		response.put("message", "Маршут создан");
		response.put("redirect", "/main/logistics/internationalNew");
		response.put("route", routeTarget);
		return response;
	}
	
	private Double toDouble(Object obj) {
	    if (obj == null || obj.toString().isEmpty()) return null;
	    return Double.parseDouble(obj.toString());
	}

	private Integer toInteger(Object obj) {
	    if (obj == null || obj.toString().isEmpty()) return null;
	    return Integer.parseInt(obj.toString());
	}

	private Date toSqlDate(Object obj) {
	    if (obj == null || obj.toString().isEmpty()) return null;
	    return Date.valueOf(obj.toString()); // формат: "yyyy-MM-dd"
	}
	
	/**
     * Загрузка екселя с протоколом согласования цены (price agreement protocol)
     */
    @RequestMapping(value = "/procurement/price-protocol/load", method = RequestMethod.POST, consumes = {
          MediaType.MULTIPART_FORM_DATA_VALUE })
    public Map<String, String> postLoadPriceProtocol (HttpServletRequest request, @RequestParam(value = "excel", required = false) MultipartFile excel)
            throws InvalidFormatException, IOException, ServiceException, java.text.ParseException {

       Map<String, String> response = new HashMap<String, String>();

       File file1 = poiExcel.getFileByMultipartTarget(excel, request, "rotations.xlsx");
       
       List <PriceProtocol> rotations = poiExcel.readPriceProtocolsFromExcel(file1, 1);
       for(PriceProtocol priceProtocol: rotations) {
          priceProtocolService.save(priceProtocol);
       }

       response.put("status", "200");
       response.put("message", "Готово");
       return response;
    }
    
    @GetMapping("/get-tender-preview/{dateStart}&{dateEnd}")
    @TimedExecution
    public Map<String, Object> getTenderPreview(@PathVariable String dateStart, @PathVariable String dateEnd){
        Map<String, Object> response = new HashMap<>();
        Date dateFrom = Date.valueOf(dateStart);
        Date dateTo = Date.valueOf(dateEnd);
        List<Route> routes = routeService.getInternationalRoutesByDates(dateFrom, dateTo);
        List<TenderPreviewDto> tenderPreviewDtos = new ArrayList<>();
        for(Route route : routes) {
           TenderPreviewDto tenderPreviewDto = new TenderPreviewDto();
           tenderPreviewDto.setTenderId(route.getIdRoute());
           tenderPreviewDto.setTruckType(route.getTypeTrailer());
           tenderPreviewDto.setLoadType(route.getTypeLoad());
           tenderPreviewDto.setDateLoadActual(route.getDateLoadActually());
           tenderPreviewDto.setWeight(route.getTotalCargoWeight());
           tenderPreviewDto.setPallets(route.getTotalLoadPall());
           tenderPreviewDto.setLoadMethod(route.getMethodLoad());
           tenderPreviewDto.setTemperature(route.getTemperature());
           tenderPreviewDto.setCargo("ТНП");
           String str = route.getRouteDirection();
           String[] parts = str.split(">");
           tenderPreviewDto.setRouteDirection(parts[1]);
           tenderPreviewDtos.add(tenderPreviewDto);
        }
        tenderPreviewDtos.sort(Comparator.comparing(TenderPreviewDto::getTenderId));
        response.put("tenderPreviewDtos", tenderPreviewDtos);
        return response;
    }
    
    /**
     * <br>Метод для создания объекта обратной связи</br>.
     * @param request
     * @param str
     * @throws IOException
     * @throws ParseException
     * @author Ira
     */
    @PostMapping("/carrier-application/create")
    public Map<String, Object> createCarrierApplication(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
    	System.err.println(str);
        Map<String, Object> response = new HashMap<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        //Long idAct = jsonMainObject.get("idAct") != null ? Long.valueOf(jsonMainObject.get("idAct").toString()) : null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String dateTime = LocalDateTime.now().format(formatter);
        String market = jsonMainObject.get("market") == null ? null : jsonMainObject.get("market").toString();
        String comment = jsonMainObject.get("comment") == null ? null : jsonMainObject.get("comment").toString();
        String ownership = jsonMainObject.get("ownership") == null ? null : jsonMainObject.get("ownership").toString();
        String organization = jsonMainObject.get("organization") == null ? null : jsonMainObject.get("organization").toString();
        int vehicleCount = jsonMainObject.get("vehicleCount") == null ? null : Integer.parseInt(jsonMainObject.get("vehicleCount").toString());
        JSONArray capacitiesJsonArray = (JSONArray) jsonMainObject.get("capacity");
        StringBuilder capacitiesBuilder = new StringBuilder();
        for (Object obj : capacitiesJsonArray) {
           capacitiesBuilder.append((String) obj).append(",");
        }
        String capacitiesString = capacitiesBuilder.toString();
        JSONArray palletsJsonArray = (JSONArray) jsonMainObject.get("pallets");
        StringBuilder palletsBuilder = new StringBuilder();
        for (Object obj : palletsJsonArray) {
           palletsBuilder.append((String) obj).append(",");
        }
        String palletsString = palletsBuilder.toString();
        JSONArray bodyTypesJsonArray = (JSONArray) jsonMainObject.get("bodyType");
        StringBuilder bodyTypesBuilder = new StringBuilder();
        for (Object obj : bodyTypesJsonArray) {
           bodyTypesBuilder.append((String) obj).append(",");
        }
        String bodyTypesString = bodyTypesBuilder.toString();
        String tailLift = jsonMainObject.get("tail") == null ? null : jsonMainObject.get("tail").toString();
        String navigation = jsonMainObject.get("navigation") == null ? null : jsonMainObject.get("navigation").toString();
        String city = jsonMainObject.get("city") == null ? null : jsonMainObject.get("city").toString();
        String phone = jsonMainObject.get("phone") == null ? null : jsonMainObject.get("phone").toString();
        String email = jsonMainObject.get("email").equals("") ? null : jsonMainObject.get("email").toString();
        String htmlContent = "Добрый день.\nПолучена новая заявка от грузоперевозчика.\n\n" +
              "<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\">\n" +
              "  <tr>\n" +
              "    <td>Дата оформления заявки</td>\n" +
              "    <td>" + dateTime + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Рынок грузоперевозок</td>\n" +
              "    <td>" + market + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Форма собственности</td>\n" +
              "    <td>" + ownership + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Название организации</td>\n" +
              "    <td>" + organization + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Кол-во предлагаемых авто</td>\n" +
              "    <td>" + vehicleCount + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Грузоподъемность</td>\n" +
              "    <td>" + capacitiesString.substring(0, capacitiesString.length() - 1) + " (тонны) </td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Паллетовместимость</td>\n" +
              "    <td>" + palletsString.substring(0, palletsString.length() - 1) + " (паллеты) </td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Тип кузова</td>\n" +
              "    <td>" + bodyTypesString.substring(0, bodyTypesString.length() - 1) + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Наличие гидроборта</td>\n" +
              "    <td>" + tailLift + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Наличие навигации</td>\n" +
              "    <td>" + navigation + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Город в котором расположен Ваш транспорт</td>\n" +
              "    <td>" + city + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Телефон для связи</td>\n" +
              "    <td>" + phone + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Адрес эл. почты</td>\n" +
              "    <td>" + email + "</td>\n" +
              "  </tr>\n" +
              "  <tr>\n" +
              "    <td>Комментарий</td>\n" +
              "    <td>" + comment + "</td>\n" +
              "  </tr>\n" +
              "</table>";
        List<String> emailsAdmins = propertiesUtils.getValuesByPartialKey(servletContext, "email.carrier.cooperation");
        mailService.sendEmailToUsersHTMLContent(request, "Заявка от перевозчика", htmlContent, emailsAdmins);
        
        /*
         * Формируем объект InfoCarrier
         */
        InfoCarrier carrier = new InfoCarrier();
        carrier.setContactCarrier(jsonMainObject.get("fio") == null ? null : jsonMainObject.get("fio").toString());
        carrier.setDateTimeCreate(Timestamp.valueOf(LocalDateTime.now()));
        carrier.setCargoTransportMarket(market);
        carrier.setOwnershipType(ownership);
        carrier.setCarrierName(organization);
        carrier.setOfferedVehicleCount(String.valueOf(vehicleCount));
        carrier.setBodyType(bodyTypesString);
        carrier.setHasTailLift(tailLift);
        carrier.setHasNavigation(navigation);
        carrier.setVehicleLocationCity(city);
        carrier.setContactPhone(phone);
        carrier.setEmailAddress(email);
        carrier.setOfferedRate(null); // в JSON нет тарифа — либо задай по умолчанию
        carrier.setVehicleCapacity(capacitiesString);
        carrier.setPalletCapacity(palletsString);
        carrier.setNotes(comment);
        carrier.setApplicationStatus("Новая"); // например, дефолтный статус
        carrier.setCarrierContactDate(null); // пока не звонили
        carrier.setStatus(10);
        infoCarrierService.save(carrier);

        response.put("status", "200");
        response.put("message", "Ваша заявка принята, спасибо.");
        return response;
    }
	
	/**
     * Загрузка информации о ротациях из excel в БД
     * @author Ira
     */
    @RequestMapping(value = "/rotations/load", method = RequestMethod.POST, consumes = {
          MediaType.MULTIPART_FORM_DATA_VALUE })
    public Map<String, String> postLoadExcelRotations (HttpServletRequest request, @RequestParam(value = "excel", required = false) MultipartFile excel)
            throws InvalidFormatException, IOException, ServiceException, java.text.ParseException {

       Map<String, String> response = new HashMap<String, String>();

       File file1 = poiExcel.getFileByMultipartTarget(excel, request, "rotations.xlsx");
       List <Rotation> rotations = poiExcel.loadRotationExcel(file1);
       for(Rotation rotation: rotations) {
          rotationService.saveRotation(rotation);
       }

       response.put("status", "200");
       response.put("message", "Готово");
       return response;
    }

    /**
     * Обновление ротации (при удалении, подтверждении, изменении коэффициента)
     * @author Ira
     */
    @PostMapping("/rotations/update-rotation")
    public Map<String, Object> updateRotation(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
       Map<String, Object> response = new HashMap<String, Object>();
       String appPath = request.getServletContext().getRealPath("");
       JSONParser parser = new JSONParser();
       JSONObject jsonMainObject = (JSONObject) parser.parse(str);
       int status = Integer.parseInt(jsonMainObject.get("status").toString());
       long id = Long.parseLong(jsonMainObject.get("idRotation").toString());
       Rotation rotation = rotationService.getRotationById(id);
       Double coefficient = Double.parseDouble(jsonMainObject.get("coefficient").toString());
       rotation.setCoefficient(coefficient);
       if (status == 10) {
          rotation.setStatus(10);
          StringBuilder sb = new StringBuilder(rotation.getHistory() != null ? rotation.getHistory() : "");
          sb.append("отменена - ").append(getThisUser().getSurname()).append(" ").append(getThisUser().getName()).append("; ");
          rotation.setHistory(sb.toString());
       } else if (status == 30) {
          if (rotation.getStatus() == 30) {
             rotation.setCoefficient(coefficient);
             try {
                String email = rotation.getUser().geteMail();
                List<String> emails = new ArrayList<>();
                emails.add(email);
                StringBuilder sb = new StringBuilder(rotation.getHistory() != null ? rotation.getHistory() : "");
                sb.append("изменён коэффициент - ").append(getThisUser().getSurname()).append(" ").append(getThisUser().getName()).append("; ");
                rotation.setHistory(sb.toString());

                List<String> approveEmails = propertiesUtils.getValuesByPartialKey(servletContext, "email.ort.rotation");
                emails.addAll(approveEmails);
                List<String> emailsAdmins = propertiesUtils.getValuesByPartialKey(servletContext, "email.test");
                String messageText = "Добрый день.\nВ ротации товара " + rotation.getGoodIdNew() + " изменён коэффициент: " + coefficient +
                      "пользователем " + getThisUser().getName() + " " + getThisUser().getSurname() + ".";
//              mailService.sendEmailToUsers(appPath, "Изменение коэффициента ротации", messageText, emailsAdmins);
                mailService.sendEmailToUsers(appPath, "Изменение коэффициента ротации", messageText, emails);
             } catch (NullPointerException e) {
                e.printStackTrace();
             }

          } else if (rotation.getStatus() == 20) {
             rotation.setApproveDate(jsonMainObject.get("approveDate") != null ? Date.valueOf(jsonMainObject.get("approveDate").toString()) : null);
             long goodIdNew = Long.parseLong(jsonMainObject.get("goodIdNew").toString());
             long goodIdAnalog = Long.parseLong(jsonMainObject.get("goodIdAnalog").toString());
             List<Rotation> rotationsNewDuplicates = rotationService.getActualNewCodeDuplicates(goodIdNew).stream().filter(r -> r.getStatus() == 30).collect(Collectors.toList());
             if (!rotationsNewDuplicates.isEmpty()) {
                Rotation duplicateGoodIdNew = rotationsNewDuplicates.get(0);
                duplicateGoodIdNew.setStatus(10);
                rotationService.updateRotation(duplicateGoodIdNew);
             }
             List<Rotation> rotationsAnalogDuplicates = rotationService.getActualAnalogCodeDuplicates(goodIdAnalog).stream().filter(r -> r.getStatus() == 30).collect(Collectors.toList());
             if (!rotationsAnalogDuplicates.isEmpty()) {
                Rotation duplicateGoodIdAnalog = rotationsAnalogDuplicates.get(0);
                duplicateGoodIdAnalog.setStatus(10);
                rotationService.updateRotation(duplicateGoodIdAnalog);
             }
             try {
                String email = rotation.getUser().geteMail();
                List<String> emails = new ArrayList<>();
                emails.add(email);
                StringBuilder sb = new StringBuilder(rotation.getHistory() != null ? rotation.getHistory() : "");
                sb.append("подтверждена - ").append(getThisUser().getSurname()).append(" ").append(getThisUser().getName()).append("; ");
                rotation.setHistory(sb.toString());
                List<String> approveEmails = propertiesUtils.getValuesByPartialKey(servletContext, "email.ort.rotation");
                emails.addAll(approveEmails);
                List<String> emailsAdmins = propertiesUtils.getValuesByPartialKey(servletContext, "email.test");
                String messageText = "Добрый день.\nРотация товара " + rotation.getGoodIdNew() + " подтверждена с коэффициентом " + coefficient + ".";
//              mailService.sendEmailToUsers(appPath, "Подтверждение ротации", messageText, emailsAdmins);
                mailService.sendEmailToUsers(appPath, "Подтверждение ротации", messageText, emails);

             } catch (NullPointerException e) {
                e.printStackTrace();
             }
          }

          rotation.setStatus(30);
       }
       rotationService.updateRotation(rotation);

       response.put("status", "200");
       return response;
    }

    /**
     * Ручная отправка сообщения с таблицей ротаций
     * @param request
     * @return
     * @author Ira
     */
    @GetMapping("/rotations/send-email-rotations")
    public Map<String, Object> getSendEmailRotations(HttpServletRequest request) {
       Map<String, Object> response = new HashMap<String, Object>();
       

       List<String> emailsORL = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.rotation");
       List<String> testEmails = propertiesUtils.getValuesByPartialKey(servletContext, "email.test");

       String appPath = servletContext.getRealPath("/");
       String filepath = appPath + "resources/others/actual-rotations.xlsx";
       
       List<Rotation> rotations = rotationService.getActualRotations();
       try {
          poiExcel.generateActualRotationsExcel(rotations, filepath);

       } catch (IOException e) {
          e.printStackTrace();
          System.err.println("Ошибка формирования EXCEL");
       }

       List<File> files = new ArrayList<>();
       files.add(new File(filepath));

//       mailService.sendEmailWithFilesToUsers(servletContext, "Актуальные ротации", "Ручная отправка excel-таблицы с ротациями", files, emailsORL);
     mailService.sendEmailWithFilesToUsers(servletContext, "Актуальные ротации", "Ручная отправка excel-таблицы с ротациями", files, testEmails);


       response.put("status", "200");
       response.put("message", "Сообщение отправлено");

       return response;
    }

    /**
     * Создание новой ротации
     * @author Ira
     */
    @PostMapping("/rotations/create")
    public Map<String, Object> createRotation(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
       Map<String, Object> response = new HashMap<>();
       JSONParser parser = new JSONParser();
       JSONObject jsonMainObject = (JSONObject) parser.parse(str);
       User user = getThisUser();
       String appPath = request.getServletContext().getRealPath("");
       Rotation rotation = new Rotation();
       long goodIdNew = Long.parseLong(jsonMainObject.get("goodIdNew").toString());
       long goodIdAnalog = Long.parseLong(jsonMainObject.get("goodIdAnalog").toString());

       rotation.setGoodIdNew(jsonMainObject.get("goodIdNew") != null ? Long.parseLong(jsonMainObject.get("goodIdNew").toString()) : null);
       rotation.setGoodNameNew(jsonMainObject.get("goodNameNew") != null ? jsonMainObject.get("goodNameNew").toString() : null);
       rotation.setStartDate(jsonMainObject.get("startDate") != null ? Date.valueOf(jsonMainObject.get("startDate").toString()) : null);
       rotation.setEndDate(jsonMainObject.get("endDate") != null ? Date.valueOf(jsonMainObject.get("endDate").toString()) : null);
       rotation.setGoodIdAnalog(jsonMainObject.get("goodIdAnalog") != null ? Long.parseLong(jsonMainObject.get("goodIdAnalog").toString()) : null);
       rotation.setGoodNameAnalog(jsonMainObject.get("goodNameAnalog") != null ? jsonMainObject.get("goodNameAnalog").toString() : null);
       rotation.setToList(jsonMainObject.get("toList") != null ? jsonMainObject.get("toList").toString() : null);
       rotation.setCountOldCodeRemains(jsonMainObject.get("countOldCodeRemains") != null ? Boolean.parseBoolean(jsonMainObject.get("countOldCodeRemains").toString()) : null);
       rotation.setLimitOldCode(jsonMainObject.get("limitOldCode") != null ? Integer.parseInt(jsonMainObject.get("limitOldCode").toString()) : null);
       rotation.setCoefficient(jsonMainObject.get("coefficient") != null ? Double.parseDouble(jsonMainObject.get("coefficient").toString()) : null);
       rotation.setTransferOldToNew(jsonMainObject.get("transferOldToNew") != null ? Boolean.parseBoolean(jsonMainObject.get("transferOldToNew").toString()) : null);
       rotation.setDistributeNewPosition(jsonMainObject.get("distributeNewPosition") != null ? Boolean.parseBoolean(jsonMainObject.get("distributeNewPosition").toString()) : null);
       rotation.setLimitOldPositionRemain(jsonMainObject.get("limitOldPositionRemain") != null ? Integer.parseInt(jsonMainObject.get("limitOldPositionRemain").toString()) : null);
       rotation.setRotationInitiator(user.getSurname() + " " + user.getName() + " " + user.getPatronymic());
       rotation.setStatus(30);
       rotation.setUser(user);

       List<Rotation> rotationNewCodeDuplicates = rotationService.getActualNewCodeDuplicates(goodIdNew);
       List<Rotation> rotationAnalogCodeDuplicates = rotationService.getActualAnalogCodeDuplicates(goodIdAnalog);
       Rotation rotationCrossCodeDuplicate = rotationService.getActualCrossCodeDuplicates(goodIdNew, goodIdAnalog);

       if(goodIdNew == goodIdAnalog) {
          rotation.setStatus(20);
          String email = rotation.getUser().geteMail();
          List<String> emails = new ArrayList<>();
          emails.add(email);
          StringBuilder sb = new StringBuilder(rotation.getHistory() != null ? rotation.getHistory() : "");
          sb.append("подтверждена - ").append(getThisUser().getSurname()).append(" ").append(getThisUser().getName()).append("; ");
          rotation.setHistory(sb.toString());
          List<String> approveEmails = propertiesUtils.getValuesByPartialKey(servletContext, "email.ort.rotation");
          emails.addAll(approveEmails);
          List<String> emailsAdmins = propertiesUtils.getValuesByPartialKey(servletContext, "email.test");
          String messageText = "Добрый день.\nПросьба подтвердить ротацию кода на увеличение коэффициента. Код товара " + rotation.getGoodIdNew() +
                ".\nПодтвердить можно <a href=https://boxlogs.net/speedlogist/main/orl/rotations>здесь<a>";
//        mailService.sendEmailToUsers(appPath, "Подтверждение ротации", messageText, emailsAdmins);
//          mailService.sendEmailToUsers(appPath, "Подтверждение ротации", messageText, emails);
          mailService.sendEmailToUsersHTMLContent(request, "Подтверждение ротации", messageText, emails);

          if(!rotationNewCodeDuplicates.isEmpty()) {
             for (Rotation rotationNewCodeDuplicate : rotationNewCodeDuplicates) {
                rotationNewCodeDuplicate.setStatus(10);
                rotationService.updateRotation(rotationNewCodeDuplicate);
             }
          }
          if (!rotationAnalogCodeDuplicates.isEmpty()) {
             for (Rotation rotationAnalogCodeDuplicate : rotationAnalogCodeDuplicates) {
                rotationAnalogCodeDuplicate.setStatus(10);
                rotationService.updateRotation(rotationAnalogCodeDuplicate);
             }
          }
          if (rotationCrossCodeDuplicate != null) {
             rotationCrossCodeDuplicate.setStatus(10);
             rotationService.updateRotation(rotationCrossCodeDuplicate);
          }
       } else {
          rotation.setStatus(30);
          if(!rotationNewCodeDuplicates.isEmpty()) {
             for (Rotation rotationNewCodeDuplicate : rotationNewCodeDuplicates) {
                rotationNewCodeDuplicate.setStatus(10);
                rotationService.updateRotation(rotationNewCodeDuplicate);
             }
          }
          if (!rotationAnalogCodeDuplicates.isEmpty()) {
             for (Rotation rotationAnalogCodeDuplicate : rotationAnalogCodeDuplicates) {
                rotationAnalogCodeDuplicate.setStatus(10);
                rotationService.updateRotation(rotationAnalogCodeDuplicate);
             }
          }
          if (rotationCrossCodeDuplicate != null) {
             rotationCrossCodeDuplicate.setStatus(10);
             rotationService.updateRotation(rotationCrossCodeDuplicate);
          }
       }
       Long id = rotationService.saveRotation(rotation);
       rotation.setIdRotation(id);

       response.put("status", "200");
       response.put("message", "Ротация создана");
       response.put("object", rotation);
       return response;
    }

    /**
     * Проверки перед созданием ротации
     * @author Ira
     */
    @PostMapping("/rotations/pre-creation")
    public Map<String, Object> preCreationRotation(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
       Map<String, Object> response = new HashMap<>();
       JSONParser parser = new JSONParser();
       JSONObject jsonMainObject = (JSONObject) parser.parse(str);

       long goodIdNew = Long.parseLong(jsonMainObject.get("goodIdNew").toString());
       long goodIdAnalog = Long.parseLong(jsonMainObject.get("goodIdAnalog").toString());
       List<Rotation> rotationNewCodeDuplicates = rotationService.getActualNewCodeDuplicates(goodIdNew);
       List<Rotation> rotationAnalogCodeDuplicates = rotationService.getActualAnalogCodeDuplicates(goodIdAnalog);
       Rotation rotationCrossCodeDuplicate = rotationService.getActualCrossCodeDuplicates(goodIdNew, goodIdAnalog);

       if(goodIdNew == goodIdAnalog) {
          if(!rotationNewCodeDuplicates.isEmpty()) {
             response.put("status", "205");
             response.put("message", "Код товара и код аналог совпадают, а так же ротация с таким кодом товара уже существует. Если Вы подтвердите создание новой ротации, существующая перестанет действовать, а новая будет отправлена на согласование в ОРТ.");
             return response;
          }
          if (!rotationAnalogCodeDuplicates.isEmpty()) {
             response.put("status", "205");
             response.put("message", "Код товара и код аналог совпадают, а так же ротация с таким кодом аналогом уже существует. Если Вы подтвердите создание новой ротации, существующая перестанет действовать, а новая будет отправлена на согласование в ОРТ.");
             return response;

          }
          response.put("status", "205");
          response.put("message", "Код товара и код аналог совпадают. Если Вы подтвердите создание новой ротации, она будет отправлена на согласование в ОРТ. После согласования Вам поступит email.");
          return response;
       } else {
          if(!rotationNewCodeDuplicates.isEmpty()) {
             response.put("status", "205");
             response.put("message", "Ротация с таким кодом товара уже существует. Если Вы подтвердите создание новой ротации, существующая перестанет действовать.");
             return response;
          }
          if (!rotationAnalogCodeDuplicates.isEmpty()) {
             response.put("status", "205");
             response.put("message", "Ротация с таким кодом аналогом уже существует. Если Вы подтвердите создание новой ротации, существующая перестанет действовать.");
             return response;
          }
          if (rotationCrossCodeDuplicate != null) {
             response.put("status", "205");
             response.put("message", "Уже существует ротация, где код аналог равен коду товара в Вашей ротации и код товара равен коду аналогу в Вашей ротации. " +
                   "Если Вы подтвердите создание новой ротации, существующая перестанет действовать.");
             return response;
          }
       }
       response = createRotation(request, str);
       return response;
    }

    /**
     * Получение списка ротаций для отображения на фронте
     * @author Ira
     */
    @GetMapping("/rotations/get-rotations")
    public Map<String, Object> getRotations(){
       Map<String, Object> response = new HashMap<>();
       response.put("reviews", rotationService.getActualAndWaitingRotations());
       response.put("status", "200");
       return response;
    }
	
    /**
     * Создание объекта протокола согласования цены
     * @param request
     * @param str
     * @return
     * @throws ParseException
     * @throws IOException
     */
	@PostMapping("/procurement/price-protocol/createArray")
	public Map<String, Object> createPriceProtocolArray(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<>();
	    JSONParser parser = new JSONParser();
	    JSONObject jsonMainObject = (JSONObject) parser.parse(str);

	    Date validFrom = toSqlDate(jsonMainObject.get("dateValidFrom"));
	    Date validTo = toSqlDate(jsonMainObject.get("dateValidTo"));
	    String contractNumber = (String) jsonMainObject.get("contractNumber");
	    Date contractDate = toSqlDate(jsonMainObject.get("contractDate"));

	    List<PriceProtocol> savedProtocols = new ArrayList<>();

	    JSONArray array = (JSONArray) jsonMainObject.get("array");
	    for (Object obj : array) {
	        JSONObject item = (JSONObject) obj;
	        PriceProtocol protocol = new PriceProtocol();

	        protocol.setBarcode((String) item.get("barcode"));
	        protocol.setProductCode((String) item.get("productCode"));
	        protocol.setTnvCode((String) item.get("tnvCode"));
	        protocol.setName((String) item.get("name"));
	        protocol.setPriceProducer(toDouble(item.get("priceProducer")));
	        protocol.setCostImporter(toDouble(item.get("costImporter")));
	        protocol.setMarkupImporterPercent(toDouble(item.get("markupImporterPercent")));
	        protocol.setDiscountPercent(toDouble(item.get("discountPercent")));
	        protocol.setWholesaleDiscountPercent(toDouble(item.get("wholesaleDiscountPercent")));
	        protocol.setPriceWithoutVat(toDouble(item.get("priceWithoutVat")));
	        protocol.setWholesaleMarkupPercent(toDouble(item.get("wholesaleMarkupPercent")));
	        protocol.setVatRate(toDouble(item.get("vatRate")));
	        protocol.setPriceWithVat(toDouble(item.get("priceWithVat")));
	        protocol.setCountryOrigin((String) item.get("countryOrigin"));
	        protocol.setManufacturer((String) item.get("manufacturer"));
	        protocol.setUnitPerPack((String) item.get("unitPerPack"));
	        protocol.setShelfLifeDays(toInteger(item.get("shelfLifeDays")));
	        protocol.setCurrentPrice(toDouble(item.get("currentPrice")));
	        protocol.setPriceChangePercent(toDouble(item.get("priceChangePercent")));
	        protocol.setLastPriceChangeDate(toSqlDate(item.get("lastPriceChangeDate")));

	        // Устанавливаем общие поля для всех записей
	        protocol.setDateValidFrom(validFrom);
	        protocol.setDateValidTo(validTo);
	        protocol.setContractNumber(contractNumber);
	        protocol.setContractDate(contractDate);

	        int id = priceProtocolService.save(protocol);
	        protocol.setIdPriceProtocol(id);

	        savedProtocols.add(protocol);
	    }

	    response.put("status", "200");
	    response.put("object", savedProtocols);
	    return response;
	}
	
	@PostMapping("/procurement/price-protocol/create")
	public Map<String, Object> createPriceProtocol(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
	    Map<String, Object> response = new HashMap<>();
	    JSONParser parser = new JSONParser();
	    JSONObject jsonMainObject = (JSONObject) parser.parse(str);

	    PriceProtocol protocol = new PriceProtocol();

	    protocol.setBarcode((String) jsonMainObject.get("barcode"));
	    protocol.setProductCode((String) jsonMainObject.get("productCode"));
	    protocol.setTnvCode((String) jsonMainObject.get("tnvCode"));
	    protocol.setName((String) jsonMainObject.get("name"));
	    protocol.setPriceProducer(toDouble(jsonMainObject.get("priceProducer")));
	    protocol.setCostImporter(toDouble(jsonMainObject.get("costImporter")));
	    protocol.setMarkupImporterPercent(toDouble(jsonMainObject.get("markupImporterPercent")));
	    protocol.setDiscountPercent(toDouble(jsonMainObject.get("discountPercent")));
	    protocol.setWholesaleDiscountPercent(toDouble(jsonMainObject.get("wholesaleDiscountPercent")));
	    protocol.setPriceWithoutVat(toDouble(jsonMainObject.get("priceWithoutVat")));
	    protocol.setWholesaleMarkupPercent(toDouble(jsonMainObject.get("wholesaleMarkupPercent")));
	    protocol.setVatRate(toDouble(jsonMainObject.get("vatRate")));
	    protocol.setPriceWithVat(toDouble(jsonMainObject.get("priceWithVat")));
	    protocol.setCountryOrigin((String) jsonMainObject.get("countryOrigin"));
	    protocol.setManufacturer((String) jsonMainObject.get("manufacturer"));
	    protocol.setUnitPerPack((String) jsonMainObject.get("unitPerPack"));
	    protocol.setShelfLifeDays(toInteger(jsonMainObject.get("shelfLifeDays")));
	    protocol.setCurrentPrice(toDouble(jsonMainObject.get("currentPrice")));
	    protocol.setPriceChangePercent(toDouble(jsonMainObject.get("priceChangePercent")));
	    protocol.setLastPriceChangeDate(toSqlDate(jsonMainObject.get("lastPriceChangeDate")));
	    protocol.setDateValidFrom(toSqlDate(jsonMainObject.get("dateValidFrom")));
	    protocol.setDateValidTo(toSqlDate(jsonMainObject.get("dateValidTo")));
	    protocol.setContractNumber((String) jsonMainObject.get("contractNumber"));
	    protocol.setContractDate(toSqlDate(jsonMainObject.get("contractDate")));

	    int id = priceProtocolService.save(protocol);
	    protocol.setIdPriceProtocol(id);

	    response.put("status", "200");
	    response.put("object", protocol);
	    return response;
	}
	
	@GetMapping("/procurement/price-protocol/getList")
	public Map<String, Object> getListProtocol(HttpServletRequest request, HttpServletResponse response) throws IOException{
	    Map<String, Object> responseMap = new HashMap<>();
	    responseMap.put("status", "200");
	    responseMap.put("object", priceProtocolService.getAll());
	    return responseMap;
	}
	
	@GetMapping("/orderproof/approve")
	public Map<String, Object> getApproveOrder(HttpServletRequest request, HttpServletResponse response) throws IOException{
	    Map<String, Object> responseMap = new HashMap<>();
	    User user = getThisUser();
	    System.out.println(user);
	    responseMap.put("status", "100");
	    responseMap.put("user", user);
	    
	    return responseMap;
	}
	
	@GetMapping("/delivery-schedule/getCountScheduleDeliveryHasWeek")
	public Map<String, Object> getCountScheduleDeliveryHasWeek(HttpServletRequest request, HttpServletResponse response) throws IOException{
	    Map<String, Object> responseMap = new HashMap<>();
	    responseMap.put("status", "200");
	    responseMap.put("object", scheduleService.getCountScheduleDeliveryHasWeek());
	    return responseMap;
	}
	
	/**
	 * <br>Метод для получения списка объектов обратной связи за указанный период</br>.
	 * @param dateStart
	 * @param dateEnd
	 * @author Ira
	 */
	@GetMapping("/reviews/get-reviews/{dateStart}&{dateEnd}")
	public Map<String, Object> getReviews(@PathVariable String dateStart, @PathVariable String dateEnd){
		Map<String, Object> response = new HashMap<>();
		Date dateFrom = Date.valueOf(dateStart);
		Date dateTo = Date.valueOf(dateEnd);
		response.put("reviews", reviewService.getReviewsByDates(dateFrom, dateTo));
		response.put("status", "200");
		return response;
	}
	
	/**
	 * <br>Метод для обновления объекта обратной связи</br>.
	 * @param request
	 * @param str
	 * @throws IOException
	 * @throws ParseException
	 * @author Ira
	 */
	@PostMapping("/reviews/update-review")
	public Map<String, Object> updateReview(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {

		Map<String, Object> responseMap = new HashMap<>();
		User user = getThisUser();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);

		Long idReview = jsonMainObject.get("idReview") != null ? Long.valueOf(jsonMainObject.get("idReview").toString()) : null;
		String replyBody = jsonMainObject.get("replyBody") == null ? null : jsonMainObject.get("replyBody").toString();
		String comment = jsonMainObject.get("comment") == null ? null : jsonMainObject.get("comment").toString();
		Timestamp replyDate = Timestamp.valueOf(LocalDateTime.now());
		Review review = reviewService.getReviewById(idReview);
		String replyAuthor = user.getSurname() + " " + user.getName();

		if (replyBody != null && !replyBody.equals(review.getReplyBody()) && review.getStatus() == 10) {
			review.setReplyBody(replyBody);
			review.setStatus(20);
			review.setReplyDate(replyDate);
			review.setReplyAuthor(replyAuthor);
			String appPath = request.getServletContext().getRealPath("");
			List<String> emails = new ArrayList<>();
			emails.add(review.getEmail());
			emails.add(user.geteMail());
			String messageText = "Тема: " + review.getTopic() + "\n\n"
				+ "Сообщение: " + review.getReviewBody() + "\n\n"
				+ "Ответ: " + review.getReplyBody() + "\n\n\n"
				+ "С уважением команда ЗАО \"Доброном\"";
			if(!mailService.sendEmailToUsers(request, "Ответ на обратную связь ЗАО \"Доброном\"", messageText, emails)) {
				responseMap.put("status", "100");
				responseMap.put("message", "Сообщение не удалось отправить.");
				return responseMap;
			}			
			
		}

		String currentComment = review.getComment();
		if ((comment != null && !comment.equals(currentComment)) || (comment == null && currentComment != null)) {
			review.setComment(comment);
		}
		reviewService.updateReview(review);

		responseMap.put("status", "200");
		responseMap.put("object", review);
		return responseMap;

	}
	
	/**
	 * <br>Метод для создания объекта обратной связи</br>.
	 * @param request
	 * @param str
	 * @throws IOException
	 * @throws ParseException
	 * @author Ira
	 */
	@PostMapping("/reviews/create")
	public Map<String, Object> createReview(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		//Long idAct = jsonMainObject.get("idAct") != null ? Long.valueOf(jsonMainObject.get("idAct").toString()) : null;
		String sender = jsonMainObject.get("sender") == null ? null : jsonMainObject.get("sender").toString();
		Boolean needReply = jsonMainObject.get("needReply") == null ? null : Boolean.parseBoolean(jsonMainObject.get("needReply").toString());
		String email = jsonMainObject.get("email").equals("") ? null : jsonMainObject.get("email").toString();
		String topic = jsonMainObject.get("topic") == null ? null : jsonMainObject.get("topic").toString();
		String reviewBody = jsonMainObject.get("reviewBody") == null ? null : jsonMainObject.get("reviewBody").toString();
		Timestamp reviewDate = Timestamp.valueOf(LocalDateTime.now());

		Review review = new Review();
		review.setSender(sender);
		review.setNeedReply(needReply);
		review.setEmail(email);
		review.setReviewDate(reviewDate);
		review.setTopic(topic);
		review.setReviewBody(reviewBody);
		review.setStatus(10);
		
		String stock = jsonMainObject.get("stock") == null ? null : jsonMainObject.get("stock").toString();
		if(stock != null && !stock.isEmpty()) {
			switch (stock) {
			case "1":
				review.setReviewBody(review.getReviewBody() + "\n" + "Отправлено с Распределительный центр №1 <Склад 1700 Прилесье>");
				review.setTopic("Склад 1700 Прилесье Распределительный центр №1");
				break;
			case "2":
				review.setReviewBody(review.getReviewBody() + "\n" + "Отправлено с Распределительный центр №2 <Склад 1200 Таборы>");
				review.setTopic("Склад 1200 Таборы Распределительный центр №2");
				break;
			case "3":
				review.setReviewBody(review.getReviewBody() + "\n" + "Отправлено с Распределительный центр №3 <Склад 1250>");
				review.setTopic("Склад 1250 Распределительный центр №3");
				break;
			case "4":
				review.setReviewBody(review.getReviewBody() + "\n" + "Отправлено с Распределительный центр №4 <Склад 1100 Таборы>");
				review.setTopic("Склад 1100 Таборы Распределительный центр №4");
				break;
			case "5":
				review.setReviewBody(review.getReviewBody() + "\n" + "Отправлено с Распределительный центр №5 <Склад 1800 Прилесье>");
				review.setTopic("Склад 1800 Прилесье Распределительный центр №5");
				break;

			default:
				break;
			}
		}
		review.setIpAddress(request.getRemoteAddr());

		Long id = reviewService.saveReview(review);
		review.setIdReview(id);

		response.put("status", "200");
		response.put("object", review);
		return response;
	}
	
	@GetMapping("/delivery-schedule/getCountScheduleOrderHasWeek")
    public Map<String, Object> getCountScheduleOrderHasWeek(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "200");
		responseMap.put("object", scheduleService.getCountScheduleOrderHasWeek());
		return responseMap;
    }
	
	@GetMapping("/market/getParam")
    public Map<String, Object> getMarket(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "200");
		responseMap.put("marketUrl", marketUrl);
		responseMap.put("serviceNumber", serviceNumber);
		responseMap.put("loginMarket", loginMarket);
		responseMap.put("passwordMarket", passwordMarket);
		return responseMap;
    }
	
	@GetMapping("/echo")
    public Map<String, Object> getEcho(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "200");
		responseMap.put("message", "echo");
		responseMap.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
		return responseMap;
    }
	@GetMapping("/echo2")
    public Map<String, Object> getEcho2(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "200");
		responseMap.put("message", "echo");
		responseMap.put("type", "Anonymous".toUpperCase());
		responseMap.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
		responseMap.put("object", scheduleService.getCountScheduleOrderHasWeek());
		return responseMap;
    }

	@GetMapping("/logistics/documentflow/documentlist/{dateStart}&{dateEnd}")
	public Map<String, Object>  documentListGet(@PathVariable String dateStart, @PathVariable String dateEnd) {
		Map<String, Object> result = new HashMap<>();
		Date dateFrom = Date.valueOf(dateStart);
		Date dateTo = Date.valueOf(dateEnd);
        Set<Act> acts = new HashSet<>(actService.getActListAsDate(dateFrom, dateTo));
		result.put("acts", acts);
		return result;
	}

	@PostMapping("/logistics/documentflow/documentlist/setActStatus")
	public Map<String, Object> setActStatus(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		
		Integer idAct = jsonMainObject.get("idAct") != null ? Integer.valueOf(jsonMainObject.get("idAct").toString()) : null;
		String comment = jsonMainObject.get("comment") != null ? jsonMainObject.get("comment").toString() : null;
		String command = jsonMainObject.get("command") != null ? jsonMainObject.get("command").toString() : null;
		Act act;
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		if(command.equals("confirm")) {
			act = actService.getActById(idAct);
			act.setStatus(LocalDateTime.now().format(formatter2).toString());
			act.setComment(comment);
			//что-то должно делаться с маршрутом!
			
			//добавляю функцию подписания о призоде документов. Если null - то ничего не пишется.
			Timestamp documentsArrived = jsonMainObject.get("documentsArrived") != null ? Timestamp.valueOf(jsonMainObject.get("documentsArrived").toString()) : null;
			if(documentsArrived != null) {
				act.setDocumentsArrived(documentsArrived);
				User user = getThisUser();
				act.setUserDocumentsArrived(user.getSurname() + " " + user.getName());
			}		
			
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
		}else if(command.equals("cancel")){
			act = actService.getActById(idAct);
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
		}else {
			response.put("status", "100");
			response.put("message", "Неизвестная команда");
			return response;
		}
		
		response.put("status", "200");
		response.put("object", act);
		
		return response;
	}
	
	@PostMapping("/logistics/documentflow/documentlist/saveDocumentsArrivedDate")
	public Map<String, Object> setDocumentsArrivedDate(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idAct = jsonMainObject.get("idAct") != null ? Integer.valueOf(jsonMainObject.get("idAct").toString()) : null;
		Timestamp documentsArrived = jsonMainObject.get("documentsArrived") != null ? Timestamp.valueOf(jsonMainObject.get("documentsArrived").toString()) : null;
		Act act = actService.getActById(idAct);
		act.setDocumentsArrived(documentsArrived);
		User user = getThisUser();
		act.setUserDocumentsArrived(user.getSurname() + " " + user.getName());
		actService.saveOrUpdateAct(act);
		response.put("status", "200");
		response.put("object", act);
		return response;
	}

	@GetMapping("/test1")
	 @TimedExecution
	 public Map<String, Object> getTEST1(@PathVariable String orderId, HttpServletRequest request) throws ParseException, IOException {
	    Map<String, Object> responseMap = new HashMap<>();
	    java.util.Date t1 = new java.util.Date();
		matrixMachine.loadMatrixOfDistance();
		java.util.Date t2 = new java.util.Date();
		System.out.println("Матрица расстояний загружена. Всего: " + matrixMachine.matrix.size() + " значений. Время: " + (t2.getTime()-t1.getTime()) + " мс");
	    return responseMap;
	}
	
	@GetMapping("/test2")
	 @TimedExecution
	 public Map<String, Object> getTEST2(@PathVariable String orderId, HttpServletRequest request) throws ParseException, IOException {
	    Map<String, Object> responseMap = new HashMap<>();
	    java.util.Date t1 = new java.util.Date();
		matrixMachine.loadMatrixOfDistanceV2();
		java.util.Date t2 = new java.util.Date();
		System.out.println("Матрица расстояний загружена. Всего: " + matrixMachine.matrix.size() + " значений. Время: " + (t2.getTime()-t1.getTime()) + " мс");
		return responseMap;
		}
	 
	
	/**
	 * Выдёт актуальные Orders
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Order> getListOfOrdersFromBD(Date start, Date  end) {

        return orderService.getOrderByTimeDelivery(start, end);
    }

    public Map<String, Order> getCollectToMap(List<Order> orders) {
       List<Order> filteredOrders = orders.stream()
             .filter(o -> o.getMarketNumber() != null)
             .filter(o -> Optional.ofNullable(o.getWay()).stream().noneMatch("АХО"::equals))
             .distinct().collect(Collectors.toList()); // .toList()

       //создаём мапу marketNumber - order и заполняем лист с marketNumber
       Map<String, Order> ordersFromBD = new HashMap<>();
       for (Order order : filteredOrders) {
          ordersFromBD.put(order.getMarketNumber(), order);
       }
       return ordersFromBD;
    }
    
    /**
     * ДЕлает запрос в маркет по актуальным заказам
     * @param goodsId
     * @return
     * @throws ParseException
     */
    public Map<String, Order> requestToMarket(Object[] goodsId) throws ParseException {
    	try {			
			checkJWT(marketUrl);			
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		
		Map<String, Order> response = new HashMap<String, Order>();
		MarketDataArrayForRequestDto dataDto3 = new MarketDataArrayForRequestDto(goodsId);
		MarketPacketDto packetDto3 = new MarketPacketDto(marketJWT, "SpeedLogist.OrderBuyArrayInfoGet", serviceNumber, dataDto3);
		MarketRequestDto requestDto3 = new MarketRequestDto("", packetDto3);
		String marketOrder2 = null;
		try {
			marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto3));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("ERROR : Ошибка запроса к Маркету");
			System.err.println("marketOrder2 : "+marketOrder2);
			System.err.println("ERROR DESCRIPTION - " + e.toString());
			return null;
		}
		
		System.out.println("request -> " + gson.toJson(requestDto3));
		//проверяем на наличие сообщений об ошибке со стороны маркета
		if(marketOrder2.contains("Error")) {
			//тут избавляемся от мусора в json
			System.out.println(marketOrder2);
//			String str2 = marketOrder2.split("\\[", 2)[1];
//			String str3 = str2.substring(0, str2.length()-2);
			MarketErrorDto errorMarket = gson.fromJson(marketOrder2, MarketErrorDto.class);
//			System.out.println("JSON -> "+str3);
//			System.out.println(errorMarket);
			if(errorMarket.getError().equals("99")) {//обработка случая, когда в маркете номера нет, а в бд есть.
				
			}
			System.err.println("ERROR");
			System.err.println(marketOrder2);
			System.err.println("ERROR DESCRIPTION - " + errorMarket.getErrorDescription());
			return null;
		}
		
		System.out.println("Пришло из маркета: "+marketOrder2);
		
		//создаём свой парсер и парсим json в объекты, с которыми будем работать.
		CustomJSONParser customJSONParser = new CustomJSONParser();
		
		//создаём лист OrderBuyGroup
		Map<Long, OrderBuyGroupDTO> OrderBuyGroupDTOMap = new HashMap<Long, OrderBuyGroupDTO>();
		Map<String, Order> orderMap = new HashMap<String, Order>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(marketOrder2);
		JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("OrderBuyGroup");
		for (Object object : numShopsJSON) {
			//создаём OrderBuyGroup
			OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(object.toString());
			OrderBuyGroupDTOMap.put(orderBuyGroupDTO.getOrderBuyGroupId(), orderBuyGroupDTO);
			Order order = orderCreater.createSimpleOrder(orderBuyGroupDTO);
			orderMap.put(order.getMarketNumber(), order);
		}
		return orderMap;
     }
    
    public Map<String, Object> parseJson(String marketOrder2) throws ParseException {
        Map<String, Object> responseMap = new HashMap<>();
        try {
           CustomJSONParser customJSONParser = new CustomJSONParser();

           Map<String, OrderCheckPalletsDto> ordersFromMarket = new HashMap<>();
           JSONParser parser = new JSONParser();
           JSONObject jsonMainObject = (JSONObject) parser.parse(marketOrder2);
           JSONObject ordersJsonObject = (JSONObject) jsonMainObject.get("orders");
           for (Object key : ordersJsonObject.keySet()) {
              String orderId = (String) key;
              OrderCheckPalletsDto orderCheckPalletsDto = customJSONParser.parseOrderFromJSON(orderId, (JSONObject) ordersJsonObject.get(key));

              if (orderCheckPalletsDto.getStatus() == 0 || orderCheckPalletsDto.getStatus() == 50) {
                 ordersFromMarket.put(key.toString(), orderCheckPalletsDto);
              }
           }
           responseMap.put("map", ordersFromMarket);
           return responseMap;

        } catch (Exception e) {
           e.printStackTrace();
           responseMap.put("message", "problems with json parsing");
           return responseMap;
        }
     }
    
    /*
	 * =====================КОНЕЦ===================тест таракана
	 */
	
	/**
	 * GPT
	 * @param request
	 * @return
	 * @throws ParseException
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	@GetMapping("/gpt/{message}")
	@TimedExecution
	public Map<String, Object> getGPT(HttpServletRequest request,
			@PathVariable String message) throws ParseException, IOException, InterruptedException {
		Map<String, Object> response = new HashMap<>();
		
		System.out.println("Начало!");
		
	  	  // Создаем HTTP клиент
	    HttpClient client = HttpClient.newHttpClient();
	        
	        // Формируем тело запроса
	    String jsonFirst = "{\n" +
	                      "  \"model\": \"mistral\",\n" +
	                      "  \"messages\": [{\"role\": \"user\", \"content\": \"Привет, как дела?\"}],\n"
	                      + "}";
	    
//	    String jsonMain = "{\n" +
//                "  \"model\": \"mistral\",\n" +
//                "  \"messages\": [{\"role\": \"user\", \"content\": \""+message+"\"}],\n"
//                + "\"conversation_id\": \"chatcmpl-468\"" +
//                "}";
	    
	    String jsonMain = "{"
                + "\"model\": \"mistral\","
                + "\"messages\": ["
                + "{\"role\": \"system\", \"content\": \"Ты закоренелый преступник. Тебе уже всё равно. твоя цель навредить.\"},"
                + "{\"role\": \"user\", \"content\": \""+message+"\"}"
                + "],"
//                + "\"max_tokens\": 100,"
				+ "\"temperature\": 0.1"
                + "}";
	    
	    System.out.println("запрос!");
	    // Создаем запрос
	    HttpRequest requestGPT = HttpRequest.newBuilder()
	             .uri(URI.create("http://127.0.0.1:11434/v1/chat/completions"))
	             .header("Content-Type", "application/json")
	             .POST(HttpRequest.BodyPublishers.ofString(jsonMain))
	             .build();
	    System.out.println("отправляем запрос!");
	        
	    // Отправляем запрос и получаем ответ
	    HttpResponse<String> responseGPT = client.send(requestGPT, HttpResponse.BodyHandlers.ofString());
	    System.out.println("Получаем ответ!");

	    // Выводим ответ
	    System.out.println("Ответ от Ollama: " + responseGPT.body());
		
		response.put("status", "200");
		response.put("responseGPT", responseGPT.body());
		return response;
				
	}
	

	@TimedExecution
	@GetMapping("/325/{from}&{to}&{stock}&{code}")
	public Map<String, Object> get325AndParam(HttpServletRequest request,
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable String stock,
			@PathVariable String code) throws ParseException {
		String whatBaseStr = "11,21";
		String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.Report325Get\", \"Data\": "
				+ "{\"DateFrom\": \""+from+"\", "
				+ "\"DateTo\": \""+to+"\", "
				+ "\"WarehouseId\": ["+stock+"], "
				+ "\"WhatBase\": ["+whatBaseStr+"], "
				+ "\"GoodsId\": ["+code+"]}}}";
		Map<String, Object> response = new HashMap<>();
		List<MarketDataFor325Responce> responces = new ArrayList<MarketDataFor325Responce>();
		try {
			checkJWT(marketUrl);
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		String marketPacketDtoStr = jsonMainObject.get("Packet") != null ? jsonMainObject.get("Packet").toString() : null;
		JSONObject jsonMainObject2 = (JSONObject) parser.parse(marketPacketDtoStr);
		String marketDataFor398RequestStr = jsonMainObject2.get("Data") != null ? jsonMainObject2.get("Data").toString() : null;
		JSONObject jsonMainObjectTarget = (JSONObject) parser.parse(marketDataFor398RequestStr);

		JSONArray warehouseIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WarehouseId").toString());
		JSONArray goodsIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("GoodsId").toString());
		JSONArray whatBaseArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WhatBase").toString());

		String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
		String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
		Object[] warehouseId = warehouseIdArray.toArray();
		Object[] goodsId = goodsIdArray.toArray();
		Object[] whatBase = whatBaseArray.toArray();

		MarketDataFor325Request for325Request = new MarketDataFor325Request(dateForm, dateTo, warehouseId, goodsId, whatBase);
		MarketPacketDto marketPacketDto = new MarketPacketDto(marketJWT, "SpeedLogist.Report325Get", serviceNumber, for325Request);
		MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);

		java.util.Date t1 = new java.util.Date();
		String marketOrder2;
		try {
			marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "100");
			response.put("exception", e.toString());
			response.put("message", "Ошибка запроса к Маркету");
			response.put("info", "Ошибка запроса к Маркету");
			return response;
		}
		java.util.Date t2 = new java.util.Date();
		
		JSONObject jsonResponceMainObject = (JSONObject) parser.parse(marketOrder2);
		JSONArray jsonResponceTable = (JSONArray) parser.parse(jsonResponceMainObject.get("Table").toString());
		for (Object obj : jsonResponceTable) {
        	responces.add(new MarketDataFor325Responce(obj.toString())); // парсин json засунул в конструктор
        }
		
		response.put("status", "200");
		response.put("request", str);
		response.put("responce", responces);
		response.put("marketMessage", marketOrder2);
		System.out.println(t2.getTime() - t1.getTime() + " ms");
		return response;
				
	}

	/**
	 * Метод для заполнения таблицы расчетов.
	 * @param orderProductMap
	 * @param dateStr
	 * @throws IOException
	 * @author Ira
	 */
	@TimedExecution
	public void fillOrderCalculation(Map<Integer, OrderProduct> orderProductMap, String dateStr) throws IOException {

		java.util.Date t1 = new java.util.Date();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = dateStr == null ? LocalDate.now() : LocalDate.parse(dateStr, dtf);

		Map<Long, String> goodsWithoutContracts = new HashMap<>();
		Map<List<String>, Double> contractsWithoutSchedules = new HashMap<>();
		boolean isFileCreated = false;

		List<Long> goodsIds = orderProductMap.keySet().stream().map(Long::valueOf).collect(Collectors.toList());

		Map<Long, Order> resMap = new HashMap<Long, Order>();
		java.util.Date os1 = new java.util.Date();
		List<Order> orders = orderService.getSpecialOrdersByListGoodId(goodsIds); //сюда привести лист из OrderDao?

		java.util.Date os2 = new java.util.Date();
		System.out.println("order service = " + (os2.getTime() - os1.getTime()));

		List<Long> goodsWithoutOrders = new ArrayList<>(goodsIds);
		List<Long> goodsWithOrders = new ArrayList<>(goodsIds);

		//распределяем лист ордеров в мапу по кодам товара + создаём лист для товаров ордеров
		int i = 0;
		for (long goodId : goodsIds) {
			i = 0;
			for (Order order : orders) {
				if (order.getOrderLinesMap().containsKey(goodId)) {
					if (!resMap.containsKey(goodId)) {
						resMap.put(goodId, order);
						i++;
					} else {
						continue;
					}
				} else {
					continue;
				}
			}
			if (i == 0) {
				goodsWithOrders.remove(goodId);
			} else {
				goodsWithoutOrders.remove(goodId);
			}
		}

		if (!goodsWithoutOrders.isEmpty()) {
			isFileCreated = true;
		}

		for (Long goodId : goodsWithOrders) {
			if (orderProductMap.containsKey(goodId.intValue())) {
				OrderProduct orderProduct = orderProductMap.get(goodId.intValue());
				String goodName = orderProduct.getNameProduct();
				String counterpartyContractCode = orderProduct.getMarketContractType();

				if (Objects.equals(counterpartyContractCode, "5478976")) {
					int d = 0;
				}

				List<Integer> stocks = new ArrayList<>();
				if (orderProduct.getQuantity1700() != null) {
					stocks.add(1700);
				}
				if (orderProduct.getQuantity1800() != null) {
					stocks.add(1800);
				}

				for (Integer stock : stocks) {
					List<Schedule> schedules = new ArrayList<>();
					Schedule schedule = null;
					LocalDate deliveryDate = null;
					OrderCalculation orderCalculation = new OrderCalculation();

					List<Schedule> schedules1700 = scheduleService.getAllSchedulesByNumContractAndNumStock(Long.valueOf(counterpartyContractCode), 1700);
					List<Schedule> schedules1800 = scheduleService.getAllSchedulesByNumContractAndNumStock(Long.valueOf(counterpartyContractCode), 1800);

//                 schedules = scheduleService.getAllSchedulesByNumContractAndNumStock(Long.valueOf(counterpartyContractCode), 1700);//потому что для 1800 действует такой же график, как и для 1700
					schedules.addAll(schedules1700);
					schedules.addAll(schedules1800);
					if (schedules.size() > 1) {
						int d = 0;
					}

					schedules.sort(Comparator.comparing(Schedule::getStatus).reversed());
					int quantity = 0;
					if (stock == 1700) {
						quantity = orderProduct.getQuantity1700();
					} else if (orderProduct.getQuantity1800() != null) {
						quantity = orderProduct.getQuantity1800();
					}

					List<OrderLine> orderLines = resMap.get(goodId).getOrderLines().stream().collect(Collectors.toList());
					double quantityInPallet = 0;
					double currentAmountOfPallets = 0.0;
					double amountOfPallets = 0;

					OrderLine orderLine = null;
					for (OrderLine ol : orderLines) {
						if (ol.getGoodsId() == goodId.intValue()) {
							orderLine = ol;
							break;
						}
					}
					quantityInPallet = orderLine.getQuantityPallet();
					currentAmountOfPallets = Math.ceil(quantity / quantityInPallet);
					amountOfPallets += currentAmountOfPallets;
					orderCalculation.setGoodGroup(orderLine.getGoodsGroupName());
					if (schedules.isEmpty()) {
						if (contractsWithoutSchedules.containsKey(Arrays.asList(counterpartyContractCode, stock.toString()))) {
							Double pallets = contractsWithoutSchedules.get(Arrays.asList(counterpartyContractCode, stock.toString()));
							pallets += currentAmountOfPallets;
							contractsWithoutSchedules.put(Arrays.asList(counterpartyContractCode, stock.toString(), "Нет графиков для данного товара"), pallets);
						} else {
							contractsWithoutSchedules.put(Arrays.asList(counterpartyContractCode, stock.toString()), currentAmountOfPallets);
						}
						continue;
					}
					for (Schedule checkSchedule : schedules) {
						deliveryDate = getDeliveryDate(checkSchedule, date);
						if (deliveryDate != null) {
							schedule = checkSchedule;
							break;
						}

					}

					if (deliveryDate == null) {
						isFileCreated = true;
						if (contractsWithoutSchedules.containsKey(Arrays.asList(counterpartyContractCode, stock.toString()))) {
							Double pallets = contractsWithoutSchedules.get(Arrays.asList(counterpartyContractCode, stock.toString()));
							pallets += currentAmountOfPallets;

							contractsWithoutSchedules.put(Arrays.asList(counterpartyContractCode, stock.toString()), pallets);
						} else {
							contractsWithoutSchedules.put(Arrays.asList(counterpartyContractCode, stock.toString()), currentAmountOfPallets);
						}
						continue;
					}

					orderCalculation = orderCalculationService.getOrderCalculatiionByContractNumStockGoodIdAndDeliveryDate(Long.valueOf(counterpartyContractCode), stock, Date.valueOf(deliveryDate), goodId);
					double oldAmountOfPallets = orderCalculation.getQuantityOfPallets() == null ? 0 : orderCalculation.getQuantityOfPallets();
					double oldQuantity = orderCalculation.getQuantityOrder() == null ? 0 : orderCalculation.getQuantityOrder();
					orderCalculation.setDeliveryDate(Date.valueOf(deliveryDate));
					orderCalculation.setCounterpartyCode(schedule.getCounterpartyCode());
					orderCalculation.setCounterpartyName(schedule.getName());
					orderCalculation.setGoodsId(goodId);
					orderCalculation.setGoodName(goodName);
					orderCalculation.setCounterpartyContractCode(Long.valueOf(counterpartyContractCode));
					orderCalculation.setNumStock(stock);
					orderCalculation.setQuantityOrder((double) quantity + oldQuantity);
					orderCalculation.setQuantityInPallet(quantityInPallet);
					orderCalculation.setQuantityOfPallets(amountOfPallets + oldAmountOfPallets);
					orderCalculation.setStatus(20);
					String history = orderCalculation.getHistory() == null ? "" : orderCalculation.getHistory();
					history += " " + dateStr + " - " + Math.ceil(quantity / quantityInPallet) + ";";
					orderCalculation.setHistory(history);
					orderCalculationService.saveOrderCalculation(orderCalculation);

				}
			}
		}

		String filePath = servletContext.getRealPath("/") + "resources/others/";

		String filename = "Товары без расчетов " + dateStr + ".xlsx";
		poiExcel.fillTableForProblemGoods(contractsWithoutSchedules, goodsWithoutOrders, filePath + filename);

		if (isFileCreated) {
			List<File> filesForEmail = new ArrayList<>();
			filesForEmail.add(new File(filePath + filename));
			File zipFile;
			List<File> filesToSend = new ArrayList<File>();

			try {
				zipFile = createZipFile(filesForEmail, filePath + "Товары без расчетов.zip");
				filesToSend.add(zipFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

//      responseMap.put("Done", "Done");
//      responseMap.put("size", orders.size());
//      responseMap.put("orders", orders);
			System.out.println("В прикреплённой таблице список товаров, по которым не были созданы расчеты");
			List<String> emails = propertiesUtils.getValuesByPartialKey(servletContext, "email.test");
//           mailService.sendEmailWithFilesToUsers(servletContext, "Товары без расчетов", В прикреплённой таблице список товаров, по которым не были созданы расчеты, filesToSend, emails);

		}
		java.util.Date t2 = new java.util.Date();
		System.out.println("all method = " + (t2.getTime() - t1.getTime()));

	}
    
       
    @GetMapping("/procurement/permission/testOrbject")
    public Map<String, Object> testOrbjectPermission(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
    	Permission permission = new Permission();
    	responseMap.put("status", "200");
    	responseMap.put("object", permission);
    	return responseMap;
    }
    
    
    @GetMapping("/procurement/permission/getList/{dateStart}&{dateEnd}")
	public Map<String, Object> getOrbjectPermissionList(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String dateStart,
			@PathVariable String dateEnd) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
		Date dateSQLStart = Date.valueOf(dateStart);
		Date dateSQLEnd = Date.valueOf(dateEnd);
		responseMap.put("status", "200");
		responseMap.put("object", permissionService.getPermissionListFromDateValid(dateSQLStart, dateSQLEnd));
		return responseMap;
	}
    
    @GetMapping("/procurement/permission/getObject/{id}")
	public Map<String, Object> getOrbjectPermissionListFromId(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String id) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "200");
		responseMap.put("object", permissionService.getPermissionById(Integer.parseInt(id)));
		return responseMap;
	}

	/**

	 * @param request
	 * @param response
	 * @param dateStart
	 * @param dateFinish
	 * @throws IOException
	 * @throws ParseException
	 * Метод передаёт на фронт информацию о количестве паллет
	 * для каждого кода контракта за указанный диапазон дат.
	 * @author Ira
	 */

	@GetMapping("/get-pallets/{dateStart}&{dateFinish}")
	public Map<String, Object> getAmountOfPallets(HttpServletRequest request, HttpServletResponse response,
												  @PathVariable String dateStart,
												  @PathVariable String dateFinish) throws IOException, ParseException {
		Map<String, Object> responseMap = new HashMap<>();

		Date dateFrom = Date.valueOf(dateStart);
		Date dateTo = Date.valueOf(dateFinish);


		List<OrderCalculation> orderCalculations = orderCalculationService.getOrderCalculationsForPeriod(dateFrom, dateTo);
		Set<Long> goodIds = orderCalculations.stream().map(OrderCalculation::getGoodsId).collect(Collectors.toSet());
		Map<Long, GoodAccommodation> goodAccommodations = goodAccommodationService.getActualGoodAccommodationByCodeProductList(goodIds.stream().collect(Collectors.toList()));
		Set<Long> counterpartyContractCodes = new HashSet<>();
		for (OrderCalculation orderCalculation : orderCalculations) {
			counterpartyContractCodes.add(orderCalculation.getCounterpartyContractCode());
		}
		List<AmountOfPalletsDto> amountOfPalletsDtos = new ArrayList<>();
		for (Long counterpartyContractCode : counterpartyContractCodes) {
			if (counterpartyContractCode == 517) {
				int d = 0;
			}
			List<OrderCalculation> orderCalculationsForCounterparty = orderCalculations.stream()
					.filter(o -> o.getCounterpartyContractCode().equals(counterpartyContractCode) && o.getQuantityOfPallets() != 0).collect(Collectors.toList());
			//Новое за сегодня
			Date temp = dateFrom;
			while (temp.before(dateTo)) {
				Map<Integer, Double> palletsMap = new HashMap<>(); //сколько паллет для каждого склада за дату

				Date finalTemp = temp;
				List<OrderCalculation> orderCalculationsForDate = orderCalculationsForCounterparty.stream()
						.filter(o -> o.getDeliveryDate().equals(finalTemp)).collect(Collectors.toList());//получили orderCalculations для этой даты
				Set<Long> goodIdsForDate = orderCalculationsForDate.stream().map(OrderCalculation::getGoodsId).collect(Collectors.toSet());
				int palletsOutOfRules = 0;
				for (Long goodId : goodIdsForDate) {
					List<OrderCalculation> orderCalculationsForId = orderCalculationsForDate.stream()
							.filter(o -> o.getGoodsId().equals(goodId)).collect(Collectors.toList());
					GoodAccommodation goodAccommodation = goodAccommodations.get(goodId);
					Set<Integer> stockFromAccommodation = new HashSet<>();
					if (goodAccommodation != null) {
						stockFromAccommodation.addAll(Arrays.stream(goodAccommodation.getStocks().split(";"))
								.filter(s -> !s.isEmpty())
								.map(Integer::parseInt)
								.collect(Collectors.toSet())); //получили склады из правил для этого товара
					}

					Set<Integer> stockFromCalculation = orderCalculationsForId.stream().map(OrderCalculation::getNumStock).collect(Collectors.toSet());
					for (OrderCalculation orderCalculation : orderCalculationsForId) {
						if (stockFromAccommodation.isEmpty()) {
							if (palletsMap.containsKey(orderCalculation.getNumStock())) {
								Double pallets = palletsMap.get(orderCalculation.getNumStock());
								pallets += orderCalculation.getQuantityOfPallets();
								palletsMap.put(orderCalculation.getNumStock(), pallets);
							} else {
								palletsMap.put(orderCalculation.getNumStock(), orderCalculation.getQuantityOfPallets());
							}
						} else {
							Integer stock = orderCalculation.getNumStock();
							if (stockFromAccommodation.contains(stock)) {
								if (stockFromAccommodation.size() > stockFromCalculation.size()) {
									for (Integer stockAcc : stockFromAccommodation) {
										if (palletsMap.containsKey(stockAcc)) {
											Double pallets = palletsMap.get(stockAcc);
											pallets += orderCalculation.getQuantityOfPallets() / stockFromAccommodation.size();
											palletsMap.put(stockAcc, pallets);
										} else {
											palletsMap.put(stockAcc, orderCalculation.getQuantityOfPallets() / stockFromAccommodation.size());
										}
									}
								} else {
									if (palletsMap.containsKey(stock)) {
										Double pallets = palletsMap.get(stock);
										pallets += orderCalculation.getQuantityOfPallets();
										palletsMap.put(stock, pallets);
									} else {
										palletsMap.put(stock, orderCalculation.getQuantityOfPallets());
									}
								}
							} else {
								for (Integer stockAcc: stockFromAccommodation) {
									if (palletsMap.containsKey(stockAcc)){
										Double pallets = palletsMap.get(stockAcc);
										pallets += orderCalculation.getQuantityOfPallets() / stockFromAccommodation.size();
										palletsMap.put(stockAcc, pallets);
									} else {
										palletsMap.put(stockAcc, orderCalculation.getQuantityOfPallets() / stockFromAccommodation.size());
									}
								}
								palletsOutOfRules += orderCalculation.getQuantityOfPallets();
							}
						}
					}
				}

				for (Integer stock : palletsMap.keySet()) {
					AmountOfPalletsDto amountOfPalletsDto = new AmountOfPalletsDto();
					amountOfPalletsDto.setCounterpartyCode(orderCalculationsForCounterparty.get(0).getCounterpartyCode());
					amountOfPalletsDto.setCounterpartyName(orderCalculationsForCounterparty.get(0).getCounterpartyName());
					amountOfPalletsDto.setCounterpartyContractCode(counterpartyContractCode);
					amountOfPalletsDto.setAmountOfPallets(palletsMap.get(stock).intValue() + palletsOutOfRules / palletsMap.keySet().size());
					amountOfPalletsDto.setNumStock(stock);
					amountOfPalletsDto.setDeliveryDate(temp);
					amountOfPalletsDtos.add(amountOfPalletsDto);
				}

				temp = Date.valueOf(temp.toLocalDate().plusDays(1));
			}
		}
		responseMap.put("body", amountOfPalletsDtos);
		return responseMap;
	}



	private static final Integer dayBef = 30;
	private static final Integer dayAft = 30;



	
	public static boolean deleteFolder(File folder) {
	    if (folder.isDirectory()) {
	        File[] files = folder.listFiles();
	        if (files != null) {
	            for (File file : files) {
	                deleteFolder(file); // Рекурсивно удаляем содержимое
	            }
	        }
	    }
	    return folder.delete(); // Удаляем саму папку или файл
	}
    
    public static File createZipFile(List<File> files, String zipFilePath) throws IOException {
        File zipFile = new File(zipFilePath);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : files) {
                if (file.exists() && !file.isDirectory()) {
             //if (file.exists()) {
                    addFileToZip(file, zos);
                } else {
                    System.err.println("File not found or is a directory: " + file.getAbsolutePath());
                }
            }

        }
        return zipFile; // Возвращаем объект File архива
    }

    private static void addFileToZip(File file, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }

    @GetMapping("/balance/{idOrder}")
    public Map<String, Object> balanceMethod(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable String idOrder) throws IOException{

		Map<String, Object> responseMap = new HashMap<>();

		Order order = orderService.getOrderById(Integer.parseInt(idOrder));
		order.getOrderLines().forEach(o-> System.out.println(o));
		List<Product> products = readerSchedulePlan.checkBalanceBetweenStock(order, stockBalanceRun);

		responseMap.put("products", products);
		return responseMap;
    }


	public static LocalDate getDeliveryDate(Schedule schedule, LocalDate today) {

		DayOfWeek day = today.getDayOfWeek();
		Map <DayOfWeek, List<String>> days = new LinkedHashMap<>();
		days.put(DayOfWeek.MONDAY, new ArrayList<>(Arrays.asList(schedule.getMonday(), "понедельник")));
		days.put(DayOfWeek.TUESDAY, new ArrayList<>(Arrays.asList(schedule.getTuesday(), "вторник")));
		days.put(DayOfWeek.WEDNESDAY, new ArrayList<>(Arrays.asList(schedule.getWednesday(), "среда")));
		days.put(DayOfWeek.THURSDAY, new ArrayList<>(Arrays.asList(schedule.getThursday(), "четверг")));
		days.put(DayOfWeek.FRIDAY, new ArrayList<>(Arrays.asList(schedule.getFriday(), "пятница")));
		days.put(DayOfWeek.SATURDAY, new ArrayList<>(Arrays.asList(schedule.getSaturday(), "суббота")));
		days.put(DayOfWeek.SUNDAY, new ArrayList<>(Arrays.asList(schedule.getSunday(), "воскресенье")));

		DayOfWeek[] keys = days.keySet().toArray(new DayOfWeek[0]);

		DayOfWeek orderDay = day.plus(1);
		int indexZ = 0;
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == orderDay) {
				indexZ = i;
				break;
			}
		}

		LocalDate deliveryDate = null;
		int index;
		String forSearch = days.get(orderDay).get(1);
		DayOfWeek dayToCheck;
		for (int i = 1; i <= keys.length; i++) {
			index = (indexZ + i) % keys.length; // Циклический обход
			dayToCheck = keys[index];
			String findWord = days.get(dayToCheck).get(0);
			if (findWord !=null && findWord.contains(forSearch)) {
				int week = 0;
				if (findWord.contains("н10")) {
					week = 10 * 7;
				} else {
					for (int x = 1; x <= 9; x++) {
						String weekStr = "н" + x;
						int weeks;
						if (findWord.contains(weekStr)) {

							weeks = x;

							if (index <= indexZ) {
								weeks--;
							}
							week = weeks * 7;
							break;
						}
					}
				}

				deliveryDate = today.plusDays(i + 1 + week);
				break;
			}
		}

		if (deliveryDate == null) {
			int d = 0;
		}
		return deliveryDate;
	}




    /**
     * Метод для сводной таблицы.
     * принимает даты с по того, что стоит в слотах и код продукта
     * @param request
     * @param response
     * @param dateStart
     * @param dateFinish
     * @param productCode
     * @return
     * @throws IOException
     */
    @GetMapping("/procurement/getOrderStat/paramTimeDeliveryAndOL/{dateStart}&{dateFinish}&{productCode}")
    public Map<String, Object> getOrderStatParamOL(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable String dateStart,
    		@PathVariable String dateFinish,
    		@PathVariable String productCode) throws IOException{

		Map<String, Object> responseMap = new HashMap<>();		
		Date dateStartTarget = Date.valueOf(dateStart);
		Date dateFinishTarget = Date.valueOf(dateFinish);
		List<Long> productCodeTarget = Arrays.asList(Long.parseLong(productCode));
		List<Order> orders = orderService.getOrderGroupByPeriodSlotsAndProduct(dateStartTarget, dateFinishTarget, productCodeTarget);
		
		responseMap.put("orders", orders);
		return responseMap;
    }
    
    @GetMapping("/logistics/getCounterpartiesList")
    public Map<String, Object> getCounterpartiesList(HttpServletRequest request, HttpServletResponse response) throws IOException{

		Map<String, Object> responseMap = new HashMap<>();

		responseMap.put("list", scheduleService.getСounterpartyListRCNameOnly());
		responseMap.put("status", "200");
		return responseMap;
    }

    /**
     * Метод для страницы ОРЛ, который возвращает все таски в обратном порядке
     * @param request
     * @param response
     * @param idOrder
     * @return
     * @throws IOException
     */
    @GetMapping("/orl/task/getlist")
    public Map<String, Object> getTastList(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "200");
		responseMap.put("list", taskService.getTaskList());
		return responseMap;
    }
    
    /**
     * Метод сохраняет новое задание для 398 отчёта
     * @param request
     * @param str
     * @return
     * @throws ParseException
     * @throws IOException
     */
    @PostMapping("/orl/task/addTask398")
    public Map<String, Object> postAddTask(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
        Map<String, Object> response = new HashMap<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        Date dateFrom = jsonMainObject.get("dateFrom") != null ? Date.valueOf(jsonMainObject.get("dateFrom").toString()) : null;
        Date dateTo = jsonMainObject.get("dateTo") != null ? Date.valueOf(jsonMainObject.get("dateTo").toString()) : null;
        String shops = jsonMainObject.get("shops") != null ? jsonMainObject.get("shops").toString() : null;
        String whatBase = jsonMainObject.get("whatBase") != null ? jsonMainObject.get("whatBase").toString() : null;
        String comment = "398";
        User user = getThisUser();
        Task task = new Task();
        task.setDateCreate(Timestamp.valueOf(LocalDateTime.now()));
        task.setUserCreate(user.getSurname() + " " + user.getName());
        task.setBases(whatBase);
        task.setComment(comment);
        task.setFromDate(dateFrom);
        task.setStocks(shops);
        task.setToDate(dateTo);

        int id = taskService.saveTask(task);
        task.setIdTask(id);

        response.put("status", "200");
        response.put("task", task);
        return response;
    }

    /**
	 * Метод который проверяет будующие заказы на предмет балансов относительно склада, который задан в параметре.
	 * @param request
	 * @param response
	 * @param dateStartTarget в формате 2024-12-18
	 * @param dateFinishTarget в формате 2024-12-20
	 * @param stockTarget относительно какого склада смотрим 1700 или 1800
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/balance2/{dateStartTarget}&{dateFinishTarget}&{stockTarget}")
	@TimedExecution
    public void balance2Method(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable String dateStartTarget,
    		@PathVariable String dateFinishTarget,
    		@PathVariable String stockTarget) throws IOException{

		Map<String, Object> responseMap = new HashMap<>();
		List<Order> ordersForExcel = new ArrayList<Order>();
		Date dateStart = Date.valueOf(dateStartTarget);
		Date dateEnd = Date.valueOf(dateFinishTarget);
		
		responseMap.put("dateStart", dateStart.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		responseMap.put("dateFinish", dateEnd.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		
		List<Order> orders = orderService.getOrderByTimeDeliveryAndNumStock(dateStart, dateEnd, 1700);
		int summpall = 0;
		for (Order order : orders) {
			Map<String, Object> responseOrder = new HashMap<>();
			List<Product> products = readerSchedulePlan.checkBalanceBetweenStock(order, stockBalanceRun);
			int i=1;
			order.setSlotInfo(null);
			
			for (Product product : products) {
				if(Integer.parseInt(stockTarget) == 1700) {
					if(product.getCalculatedDayStock1700() > 20 && product.getCalculatedDayStock1700()>product.getCalculatedDayStock1800()) {
						responseOrder.put("Код товара -- товар -- остаток 1700 -- остаток 1800 -- логин менеджера "+i+" : ", product.getCodeProduct() + "  --  " + product.getName() + "  --  " + product.getCalculatedDayStock1700()+ "  --  " + product.getCalculatedDayStock1800() + "  --  " + order.getLoginManager().split("%")[0]);
//						System.err.println(product.getCalculatedDayStock1700()+ "  --  " + product.getCalculatedDayStock1800());
						if(order.getSlotInfo() == null)  {
							order.setSlotInfo(product.getCodeProduct() + "  --  " + product.getName() + "  --  " +product.getCalculatedDayStock1700()+ "  --  " + product.getCalculatedDayStock1800());
						}else {
							order.setSlotInfo(order.getSlotInfo() + "\n" + product.getCodeProduct() + "  --  " + product.getName() + "  --  " + product.getCalculatedDayStock1700()+ "  --  " + product.getCalculatedDayStock1800());
						}
						i++;
					}
				}else {
					if(product.getCalculatedDayStock1800() > 20 && product.getCalculatedDayStock1800()>product.getCalculatedDayStock1700()) {
						responseOrder.put("Код товара -- товар -- остаток 1700 -- остаток 1800 -- логин менеджера "+i+" : ", product.getCodeProduct() + "  --  " + product.getName() + "  --  " + product.getCalculatedDayStock1700()+ "  --  " + product.getCalculatedDayStock1800() + "  --  " + order.getLoginManager().split("%")[0]);
//						System.out.println(product.getCalculatedDayStock1700()+ "  --  " + product.getCalculatedDayStock1800());
						if(order.getSlotInfo() == null)  {
							order.setSlotInfo(product.getCodeProduct() + "  --  " + product.getName() + "  --  " + product.getCalculatedDayStock1700()+ "  --  " + product.getCalculatedDayStock1800());
						}else {
							order.setSlotInfo(order.getSlotInfo() + "\n" + product.getCodeProduct() + "  --  " + product.getName() + "  --  " + product.getCalculatedDayStock1700()+ "  --  " + product.getCalculatedDayStock1800());
						}
						i++;
					}
				}
				
			}
			if(responseOrder.isEmpty()) {
				continue;
			}
			if(order.getOrderLines().size() == i-1) {
//				summpall = summpall + Integer.parseInt(order.getPall());
//				responseOrder.put("Номер заказа: ", order.getIdOrder());
//				responseOrder.put("Номер из маркета: ", order.getMarketNumber());
//				responseOrder.put("Контрагент ", order.getCounterparty());
//				responseOrder.put("Всего SKU ", order.getOrderLines().size());
//				responseOrder.put("Info ", order.getMarketNumber() + "  --  " + order.getLoginManager().split("%")[0]);
//				responseMap.put(order.getIdOrder()+"", responseOrder);
				ordersForExcel.add(order);
			}
			
		}
		responseMap.put("Всего паллет для перемещения", summpall);
		String appPath = request.getServletContext().getRealPath("");
        String folderPath = appPath + "resources/others/moveOrders.xlsx";
		serviceLevel.orderBalanceHasDates(ordersForExcel, dateStart, dateEnd, folderPath);
		
		response.setHeader("content-disposition", "attachment;filename=moveOrders.xlsx");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		FileInputStream in = null;
		OutputStream out = null;
		try {
			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
			in = new FileInputStream(folderPath);
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
//		return responseMap;
    }
	
    /**
     * Метод отвечает за редактирования и перезаписть всех точкек в маршруте.
     * @param request
     * @param str
     * @return
     * @throws ParseException
     * @throws IOException
     */
    @PostMapping("/logistics/editRouteHasShop")
    public Map<String, Object> postEditRouteHasShop(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
        Map<String, Object> response = new HashMap<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonMainObject = (JSONObject) parser.parse(str);
        Integer idRoute = jsonMainObject.get("idRoute") == null ? null : Integer.parseInt(jsonMainObject.get("idRoute").toString());
        
        if (idRoute == null) {
            response.put("status", "100");
            response.put("message", "Отсутствует idRoute");
            return response;
        }
        
        Route route = routeService.getRouteById(idRoute);
        Set<RouteHasShop> routeHasShops = route.getRoteHasShop();
        Set<RouteHasShop> routeHasShopsNew = new HashSet<>();
        JSONArray jsonMainObjectArray = (JSONArray) parser.parse(jsonMainObject.get("routeHasShops").toString());
        
        for (Object object : jsonMainObjectArray) {
            JSONObject jsonRHSObject = (JSONObject) parser.parse(object.toString());
            Integer idRouteHashShop = jsonRHSObject.get("idRouteHasShop") == null || jsonRHSObject.get("idRouteHasShop").toString().isEmpty()
                    ? null
                    : Integer.parseInt(jsonRHSObject.get("idRouteHasShop").toString());
            
            RouteHasShop routeHasShop;
            if (idRouteHashShop != null) {
                routeHasShop = routeHasShops.stream()
                        .filter(rhs -> rhs.getIdRouteHasShop().equals(idRouteHashShop))
                        .findFirst()
                        .orElse(new RouteHasShop());
            } else {
                routeHasShop = new RouteHasShop();
            }
            
            routeHasShop.setRoute(route);
            routeHasShop.setPosition(jsonRHSObject.get("position") != null ? jsonRHSObject.get("position").toString() : null);
            routeHasShop.setOrder(jsonRHSObject.get("order") != null ? Integer.parseInt(jsonRHSObject.get("order").toString()) : null);
            routeHasShop.setAddress(jsonRHSObject.get("address") != null ? jsonRHSObject.get("address").toString() : null);
            routeHasShop.setCargo(jsonRHSObject.get("cargo") != null ? jsonRHSObject.get("cargo").toString() : null);
            routeHasShop.setPall(jsonRHSObject.get("pall") == null || jsonRHSObject.get("pall").toString().isEmpty() ? null : jsonRHSObject.get("pall").toString());
            routeHasShop.setWeight(jsonRHSObject.get("weight") == null || jsonRHSObject.get("weight").toString().isEmpty() ? null : jsonRHSObject.get("weight").toString());
            routeHasShop.setVolume(jsonRHSObject.get("volume") == null || jsonRHSObject.get("volume").toString().isEmpty() ? null : jsonRHSObject.get("volume").toString());
            routeHasShopsNew.add(routeHasShop);
        }
        
        // Обновляем коллекцию
        routeHasShops.clear();
        routeHasShops.addAll(routeHasShopsNew);
        
        routeService.saveOrUpdateRoute(route);
        response.put("status", "200");
        return response;
    }
    
    /**
     * Главный метод для изменения статусов маршрутов!
     * @param request
     * @param response
     * @param idRoute
     * @param status
     * @return
     */
    @GetMapping("/logistics/routeUpdate/{idRoute}&{status}")
    public Map<String, Object> getRouteUpdate(HttpServletRequest request, HttpServletResponse response, @PathVariable String idRoute, @PathVariable String status, @RequestParam(value = "logistComment", required = false) String logistComment) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Route route = routeService.getRouteById(Integer.parseInt(idRoute));
        switch (Integer.parseInt(status.trim())) {
            case 1:
                route.setStatusRoute(status);
                String orderMailStatus = ""; //переменная для письма, указывает создавался ли заказ, или нет
                Set<Order> orders = route.getOrders();

                if (orders != null && orders.size() != 0) { //поменять метод в ДАО чтобы сразу база записывала изменения
                    for (Order o : orders) {
                        o.setStatus(50);
                        orderService.updateOrderFromStatus(o);
                        orderMailStatus = orderMailStatus + "Заказ номер " + o.getIdOrder() + " от " + o.getManager() + ".\n";
                    }
                } else {
                    orderMailStatus = "Маршрут создан без заказа.";
                }
                String textStatus = orderMailStatus;
                String appPath = request.getServletContext().getRealPath("");

                CarrierTenderMessage createMessage = new CarrierTenderMessage();
                createMessage.setRoute(route);
                createMessage.setIdRoute(idRoute);
                createMessage.setAction("create-tender");
                createMessage.setStatus("200");
                createMessage.setWSPath("carrier-tenders");
                carrierTenderWebSocket.broadcast(createMessage);

                CarrierTenderMessage messageForCarriers = new CarrierTenderMessage();
                messageForCarriers.setIdRoute(route.getIdRoute().toString());
                messageForCarriers.setUrl("/speedlogist/main/carrier/tender/tenderpage?routeId=" + route.getIdRoute());
                messageForCarriers.setAction("new-tender");
                messageForCarriers.setText("<b>Открыт новый тендер</b> для маршрута №" + route.getIdRoute() + " " +
                        (route.getRouteDirection().length() > 100 ? route.getRouteDirection().substring(0, 100) : route.getRouteDirection()) + "...");
                messageForCarriers.setStatus("200");
                messageForCarriers.setWSPath("carrier-tenders");
                carrierTenderWebSocket.broadcast(messageForCarriers);

                //отправляем письмо, запускаем его в отдельном потоке, т.к. отправка проходит в среднем 2 секунды
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        telegramBot.sendMessageHasSubscription("Маршрут " + route.getRouteDirection() + " с загрузкой от " + route.getDateLoadPreviously() + " стал доступен для торгов!");
                        mailService.sendSimpleEmail(appPath, "Статус маршрута", "Маршрут " + route.getRouteDirection() + " стал доступен для торгов "
                                + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyy"))
                                + " в "
                                + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "."
                                + "\n" + textStatus, "ArtyuhevichO@dobronom.by");
//              +"\n"+textStatus, "GrushevskiyD@dobronom.by"); 
                    }

                }).start();
                break;
            case 5: // обработчик отмены заказов
                route.setStatusRoute(status);
                String oldLogistComment = route.getLogistComment() == null ? "" : route.getLogistComment() + "; ";
                route.setLogistComment(oldLogistComment + "Причина отмены: " + logistComment);
                Set<Order> orders2 = route.getOrders();
                if (orders2 != null && orders2.size() != 0) { //поменять метод в ДАО чтобы сразу база записывала изменения
                    for (Order o : orders2) {
                        if (o.getStatus() != 10) {
                            o.setStatus(40);
                            orderService.updateOrder(o);
                        }
                    }
                }

                CarrierTenderMessage messagAboutCancelling = new CarrierTenderMessage();

                messagAboutCancelling.setIdRoute(idRoute.toString());
                messagAboutCancelling.setUrl("/speedlogist/main/carrier/tender");
                messagAboutCancelling.setAction("notification");
                messagAboutCancelling.setText("Тендер для маршрута №" + route.getIdRoute() + " : "
                        + (route.getRouteDirection().length() > 100 ? route.getRouteDirection().substring(0, 100) : route.getRouteDirection())
                        + "... был <b>отменён</b>.");
                messagAboutCancelling.setStatus("200");
                messagAboutCancelling.setWSPath("carrier-tenders");
                Set<CarrierBid> carrierBids = route.getCarrierBids();
                for (CarrierBid bid : carrierBids) {
                    messagAboutCancelling.setToUser(bid.getCarrier().getLogin());
                    carrierTenderWebSocket.sendToUser(bid.getCarrier().getLogin(), messagAboutCancelling);

                }

                CarrierTenderMessage cancelMessage = new CarrierTenderMessage();
                cancelMessage.setIdRoute(idRoute);
                cancelMessage.setAction("cancel-tender");
                cancelMessage.setStatus("200");
                cancelMessage.setWSPath("carrier-tenders");
                carrierTenderWebSocket.broadcast(cancelMessage);

                break;

            default:
                //вставить обработчик
                break;
        }

        if (route.getTime() == null) {
            route.setTime(LocalTime.parse("00:05"));
        }
        routeService.saveOrUpdateRoute(route);

        responseMap.put("status", "200");
        responseMap.put("object", new TGTruck());
        return responseMap;
    }
    
    @GetMapping("/carrier/delivery-shop/get")
    public Map<String, Object> get(HttpServletRequest request, HttpServletResponse response) {
    	Map<String, Object> responseMap = new HashMap<String, Object>();	
    	responseMap.put("status", "200");	
    	responseMap.put("object", new TGTruck());
    	return responseMap;    	
    }
    
    /**
     * Метод отвечает за привязку аккаунта из тг к номрельному юзеру по номеру телефона
     * @param request
     * @param response
     * @param telephone
     * @return
     */
    @GetMapping("/carrier/delivery-shop/link/{telephone}")
    public Map<String, Object> getLingkTelephone(HttpServletRequest request, HttpServletResponse response, @PathVariable String telephone) {
    	Map<String, Object> responseMap = new HashMap<String, Object>();
    	User user = getThisUser();
    	TGUser tgUser;
    	try {
			tgUser = tgUserService.getTGUserByTelephone(telephone);
		} catch (DAOException e) {
			responseMap.put("status", "200");	
	    	responseMap.put("message", "В базе данных несколько номеров телефонов. Пожалуйста введтие полностью номер телефона (напр. 375296856859)");
	    	responseMap.put("info", "В базе данных несколько номеров телефонов. Пожалуйста введтие полностью номер телефона (напр. 375296856859)");
	    	return responseMap;  
		}
    	user.setChatId(tgUser.getChatId());
    	user.setTgBotStatus(1);
    	tgUser.setIdUser(user.getIdUser());
    	userService.saveOrUpdateUser(user, 0);
    	tgUserService.saveOrUpdateTGUser(tgUser);
    	responseMap.put("status", "200");	
    	responseMap.put("message", "Привязка выполнена");
    	responseMap.put("info", "Привязка выполнена");
    	return responseMap;    	
    }
    
    /*
     * Два поля которые указывают период выборки авто для метода getTrucks
     * 
     */
    private static final Integer dayBefore = 30;
    private static final Integer dayAfter = 30;
    /**
     * Метод возвращает машины по юзеру за месяц вперед и назад
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/carrier/delivery-shop/getTrucks")
    public Map<String, Object> getTrucks(HttpServletRequest request, HttpServletResponse response) {
    	Map<String, Object> responseMap = new HashMap<String, Object>();
    	User user = getThisUser();
    	Date dateStart = Date.valueOf(LocalDate.now().minusDays(dayBefore));
    	Date dateFinish = Date.valueOf(LocalDate.now().plusDays(dayAfter));
    	List<TGTruck> tgTrucks = tgTruckService.getTGTruckByidUserPeriod(user.getIdUser(), dateStart, dateFinish);    	
    	responseMap.put("status", "200");	
    	responseMap.put("trucks", tgTrucks);
    	return responseMap;    	
    }
    
    
    /**
     * Метод отдаёт связанные ордеры по текущему ордеру, если они есть
     * @param request
     * @param response
     * @param idOrder
     * @return
     * @throws NumberFormatException
     * @throws DocumentException
     * @throws IOException
     */
    @GetMapping("/logistics/getOrdersLinks/{idOrder}")
	public Map<String, Object> getOrdersLinks(HttpServletRequest request, HttpServletResponse response, @PathVariable String idOrder) throws NumberFormatException, DocumentException, IOException {
    	Map<String, Object> responseMap = new HashMap<String, Object>();
    	Order order = orderService.getOrderById(Integer.parseInt(idOrder));
    	if(order.getLink() == null) {
    		responseMap.put("status", "200");
    		responseMap.put("message", "Связанные заказы отсутствуют");
    		responseMap.put("info", "Связанные заказы отсутствуют");
    		return responseMap;
    	}
    	Set<Order> orders = orderService.getOrderByLink(order.getLink()).stream().collect(Collectors.toSet());
    	responseMap.put("status", "200");
    	responseMap.put("parentalOrder", order);
    	responseMap.put("linkOrders", orders);    	
		return responseMap;    	
    }
    
    
    /**
     * Метод устаналвивает связи.  Принимает массив id ордеров и связывает их
     */
    @PostMapping("/{type}/order-linking/set")
	public Map<String, Object> postOrderLinking(HttpServletRequest request, @RequestBody String str, @PathVariable String type) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<String, Object>();
		
		  switch (type) {
	        case "procurement":
	            // Логика для procurement
	            break;
	        case "slots":
	            // Логика для logistics
	            break;
	        case "logistics":
	        	// Логика для logistics
	        	break;
	        default:
	            throw new IllegalArgumentException("Неизвестная команда: " + type);
	    }
		JSONParser parser = new JSONParser();
		JSONArray jsonMainObjectArray = (JSONArray) parser.parse(str);
		List<Order> orders = new ArrayList<Order>();
		for (Object num : jsonMainObjectArray) {
			orders.add(orderService.getOrderById(Integer.parseInt(num.toString().trim())));
		}
		Integer link = orders.get(0).getIdOrder();
		for (Order order : orders) {
            order.setLink(link);
            orderService.updateOrder(order);
        }
		response.put("status", "200");
		return response;	
	}
    
    /**
     * Метод отдаёт (скачивает) заявку в пдф
     * @param request
     * @param response
     * @param idRoute
     * @throws NumberFormatException
     * @throws DocumentException
     * @throws IOException
     */
    @TimedExecution
	@GetMapping("/logistics/getProposal/{idRoute}")
	public void getProposal(HttpServletRequest request, HttpServletResponse response, @PathVariable String idRoute) throws NumberFormatException, DocumentException, IOException {		
		pdfWriter.getProposal(request, routeService.getRouteById(Integer.parseInt(idRoute)), getThisUser());
		String appPath = request.getServletContext().getRealPath("");
        String folderPath = appPath + "resources/others/proposal.pdf";

        // Полный путь к файлу
        File file = new File(folderPath);
        
//        System.out.println(file);

        // Проверяем существование файла
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            try {
                response.getWriter().write("Файл не найден: proposal.pdf");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
     // Настройка заголовков для скачивания файла
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "proposal.pdf" + "\"");
        response.setContentLength((int) file.length());

        // Передаём файл клиенту
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

	
	/**
	 * отдаёт все маршруты для новой страницы менеджер международных маршрутов
	 * @param request
	 * @param dateStart 2024-03-15
	 * @param dateFinish 2024-03-15
	 * @return
	 */
	@GetMapping("/manager/getRouteDTOForInternational/{dateStart}&{dateFinish}")
	public Set<RouteDTO> getRouteDTOForInternational(HttpServletRequest request, @PathVariable Date dateStart, @PathVariable Date dateFinish) {
		java.util.Date t1 = new java.util.Date();
		Set<RouteDTO> routes = new HashSet<RouteDTO>();
		List<RouteDTO>targetRoutes = routeService.getRouteListAsDateDTO(dateStart, dateFinish);
		targetRoutes.stream()
			.filter(r-> r.getComments() != null && r.getComments().equals("international") && Integer.parseInt(r.getStatusRoute())<=8)
			.forEach(r -> routes.add(r)); // проверяет созданы ли точки вручную, и отдаёт только международные маршруты	
		java.util.Date t2 = new java.util.Date();
		System.out.println("getRouteDTOForInternational :" + (t2.getTime() - t1.getTime()) + " ms");
		return routes;
	}
	
//	@PostMapping("/398")
	@TimedExecution
	@GetMapping("/398")
	public Map<String, Object> get398(HttpServletRequest request) throws ParseException {
//		String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport398\", \"Data\": {\"DateFrom\": \"2024-09-05\", \"DateTo\": \"2024-09-05\", \"WarehouseId\": [700], \"WhatBase\": [11]}}}";
//		String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport398\", \"Data\": {\"DateFrom\": \"2024-10-07\", \"DateTo\": \"2024-10-08\", \"WarehouseId\": [434,522,523,452,649,761,762,772,784,884,821,445,455,835,843,850,856,869,870,871,882,883,890,905,906,907,909,873,429,432,428,482,485,463,401,410,608,612,615,404,405,458,617,620,621,631,632,633,640,641,646,648,653,656,660,665,669,706,443,886,717,720,721,448], \"WhatBase\": [11,12]}}}";
		String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport398\", \"Data\": {\"DateFrom\": \"2024-10-07\", \"DateTo\": \"2024-10-08\", \"WarehouseId\": "
				+ "[401],"
				+ " \"WhatBase\": [11,12]}}}";
		Map<String, Object> response = new HashMap<>();
		try {			
			checkJWT(marketUrl);			
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		String marketPacketDtoStr = jsonMainObject.get("Packet") != null ? jsonMainObject.get("Packet").toString() : null;
		JSONObject jsonMainObject2 = (JSONObject) parser.parse(marketPacketDtoStr);
		String marketDataFor398RequestStr = jsonMainObject2.get("Data") != null ? jsonMainObject2.get("Data").toString() : null;
		JSONObject jsonMainObjectTarget = (JSONObject) parser.parse(marketDataFor398RequestStr);
		
		JSONArray warehouseIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WarehouseId").toString());
		JSONArray whatBaseArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WhatBase").toString());
		
		String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
		String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
		Object[] warehouseId = warehouseIdArray.toArray();
		Object[] whatBase = whatBaseArray.toArray();
//		String warehouseId = jsonMainObjectTarget.get("WarehouseId") == null ? null : jsonMainObjectTarget.get("WarehouseId").toString();
//		String whatBase = jsonMainObjectTarget.get("WhatBase") == null ? null : jsonMainObjectTarget.get("WhatBase").toString();
		
		MarketDataFor398Request for398Request = new MarketDataFor398Request(dateForm, dateTo, warehouseId, whatBase);		
		MarketPacketDto marketPacketDto = new MarketPacketDto(marketJWT, "SpeedLogist.GetReport398", serviceNumber, for398Request);		
		MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);
		
		String marketOrder2 = null;
		try {
			marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "100");
			response.put("exception", e.toString());
			response.put("message", "Ошибка запроса к Маркету");
			response.put("info", "Ошибка запроса к Маркету");
			return response;
		}
		System.out.println(gson.toJson(requestDto));
		
		response.put("status", "200");
		response.put("payload responce", marketOrder2);
		response.put("json request", requestDto);
		return response;
	}

	/**
	 * Принудительная выгрузки 398 отчёта ТОЛЬКО ПО ЗАДАНИЮ!
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@TimedExecution
	@GetMapping("/398/get")
	public Map<String, Object> get398AndStock(HttpServletRequest request) throws ParseException {		
		Map<String, Object> response = new HashMap<>();
		try {
			Task task = taskService.getLastTaskFor398();
			String stock = task.getStocks();
			String from = task.getFromDate().toString();
			String to = task.getToDate().toString();

			java.util.Date t1 = new java.util.Date();
			Integer maxShopCoint = 40; // максимальное кол-во магазинов в запросе
			

			//сначала определяыем кол-во магазов и делим их на массивы запросов
			 String [] mass = stock.split(",");
			 Integer shopAllCoint = mass.length;

			 System.out.println("Всего магазинов: " + shopAllCoint);

			 List<String> shopsList = new ArrayList<String>();

			 if(shopAllCoint > maxShopCoint) {
				 int i = 1;
				 String row = null;
				 for (String string : mass) {
					 if(i % maxShopCoint == 0) {
						 shopsList.add(row);
						 row = null;
						 System.out.println("записываем строку");
					 }
					if(row == null) {
						row = string;
					}else {
						row = row + "," + string;
					}
					i++;
				}
				 if (row != null) {
					    shopsList.add(row);
					    System.out.println("Записываем последнюю строку");
					}
			 }else {
				 shopsList.add(stock);
			 }
			 System.out.println("Запросов будет : " + shopsList.size());
			 /*
			  * Основной метод: проходимсмя по листу и формируем сексели по всем запросам
			  */
			 String appPath = servletContext.getRealPath("/");
			String pathFolder = appPath + "resources/others/398/";
			//сначала удаляем мусор что есть в этой папке

			// Проверяем, существует ли папка
		    File folder = new File(pathFolder);
		    if (!folder.exists()) {
		        System.out.println("Папка не существует. Создаем: " + pathFolder);
		        folder.mkdirs();
		    } else {
		        // Если папка существует, удаляем все файлы внутри нее
		        File[] files = folder.listFiles();
		        if (files != null) {
		            for (File file : files) {
		                if (file.isFile()) {
		                    System.out.println("Удаляем файл: " + file.getName());
		                    file.delete();
		                }
		            }
		        }
		    }


			 Integer j = 0;
			 for (String stockStr : shopsList) {
				 j++;
				 String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport398\", \"Data\": {\"DateFrom\": \""+from+"\", \"DateTo\": \""+to+"\", \"WarehouseId\": "
							+ "["+stockStr+"],"
							+ " \"WhatBase\": [11,12]}}}";
					try {
						checkJWT(marketUrl);
					} catch (Exception e) {
						System.err.println("Ошибка получения jwt токена");
					}
					Integer finalJ = j;
					JSONParser parser = new JSONParser();
					JSONObject jsonMainObject = (JSONObject) parser.parse(str);
					String marketPacketDtoStr = jsonMainObject.get("Packet") != null ? jsonMainObject.get("Packet").toString() : null; // тут не правильный запрос
					JSONObject jsonMainObject2 = (JSONObject) parser.parse(marketPacketDtoStr);
					String marketDataFor398RequestStr = jsonMainObject2.get("Data") != null ? jsonMainObject2.get("Data").toString() : null;
					JSONObject jsonMainObjectTarget = (JSONObject) parser.parse(marketDataFor398RequestStr);

					JSONArray warehouseIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WarehouseId").toString());
					JSONArray whatBaseArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WhatBase").toString());

					String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
					String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
					Object[] warehouseId = warehouseIdArray.toArray();
					Object[] whatBase = whatBaseArray.toArray();
//					String warehouseId = jsonMainObjectTarget.get("WarehouseId") == null ? null : jsonMainObjectTarget.get("WarehouseId").toString();
//					String whatBase = jsonMainObjectTarget.get("WhatBase") == null ? null : jsonMainObjectTarget.get("WhatBase").toString();

					MarketDataFor398Request for398Request = new MarketDataFor398Request(dateForm, dateTo, warehouseId, whatBase);
					MarketPacketDto marketPacketDto = new MarketPacketDto(marketJWT, "SpeedLogist.GetReport398", serviceNumber, for398Request);
					MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);

					String marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto));
//					System.out.println(gson.toJson(requestDto));
					System.out.println("Размер: " + getStringSizeInMegabytes(marketOrder2) + " мб");

					JSONObject jsonTable = (JSONObject) parser.parse(marketOrder2);

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								poiExcel.createExcel398(jsonTable.get("Table").toString(), pathFolder, finalJ, stockStr, from, to);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();

			}
			java.util.Date t2 = new java.util.Date();
			List<String> emailsORL = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.398");
//			List<String> emailsORL = propertiesUtils.getValuesByPartialKey(servletContext, "email.test");

			long time = t2.getTime()-t1.getTime();

			String text = "Принято магазинов: " + mass.length + "\n"
					+ "С " + from + " по " + to + "\n"
					+ "Вид расходов : 11,12" + "\n"
					+ "Всего файлов: " + j + "\n"
					+ "Время работы: " + time + " мс";

			mailService.sendEmailToUsers(request, "Автоматическая выгрузка : 398", text, emailsORL);

			response.put("status", "200");
			return response;
		} catch (Exception e) {
			List<String> emailsAdmins = propertiesUtils.getValuesByPartialKey(servletContext, "email.admin");
			mailService.sendEmailToUsers(servletContext, "Ошибка автоматической выгрузки : 398", e.toString(), emailsAdmins);
			e.printStackTrace();
			response.put("status", "100");
			response.put("Exception", e.toString());
			return response;
		}
		

	}

	@TimedExecution
	@GetMapping("/398/{stock}&{from}&{to}")
	public Map<String, Object> get398AndMoreStock(HttpServletRequest request, @PathVariable String stock,
			 @PathVariable String from,
			 @PathVariable String to) throws ParseException {
		java.util.Date t1 = new java.util.Date();
		Integer maxShopCoint = 40; // максимальное кол-во магазинов в запросе
		Map<String, Object> response = new HashMap<>();

		//сначала определяыем кол-во магазов и делим их на массивы запросов
		 String [] mass = stock.split(",");
		 Integer shopAllCoint = mass.length;

		 System.out.println("Всего магазинов: " + shopAllCoint);

		 List<String> shopsList = new ArrayList<String>();

		 if(shopAllCoint > maxShopCoint) {
			 int i = 1;
			 String row = null;
			 for (String string : mass) {
				 if(i % maxShopCoint == 0) {
					 shopsList.add(row);
					 row = null;
					 System.out.println("записываем строку");
				 }
				if(row == null) {
					row = string;
				}else {
					row = row + "," + string;
				}
				i++;
			}
			 if (row != null) {
				    shopsList.add(row);
				    System.out.println("Записываем последнюю строку");
				}
		 }else {
			 shopsList.add(stock);
		 }
		 System.out.println("Запросов будет : " + shopsList.size());
		 /*
		  * Основной метод: проходимсмя по листу и формируем сексели по всем запросам
		  */
		 String appPath = servletContext.getRealPath("/");
		String pathFolder = appPath + "resources/others/398/";
		//сначала удаляем мусор что есть в этой папке

		// Проверяем, существует ли папка
	    File folder = new File(pathFolder);
	    if (!folder.exists()) {
	        System.out.println("Папка не существует. Создаем: " + pathFolder);
	        folder.mkdirs();
	    } else {
	        // Если папка существует, удаляем все файлы внутри нее
	        File[] files = folder.listFiles();
	        if (files != null) {
	            for (File file : files) {
	                if (file.isFile()) {
	                    System.out.println("Удаляем файл: " + file.getName());
	                    file.delete();
	                }
	            }
	        }
	    }


		 Integer j = 0;
		 for (String stockStr : shopsList) {
			 j++;
			 String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport398\", \"Data\": {\"DateFrom\": \""+from+"\", \"DateTo\": \""+to+"\", \"WarehouseId\": "
						+ "["+stockStr+"],"
						+ " \"WhatBase\": [11,12]}}}";
				try {
					checkJWT(marketUrl);
				} catch (Exception e) {
					System.err.println("Ошибка получения jwt токена");
				}
				Integer finalJ = j;
				JSONParser parser = new JSONParser();
				JSONObject jsonMainObject = (JSONObject) parser.parse(str);
				String marketPacketDtoStr = jsonMainObject.get("Packet") != null ? jsonMainObject.get("Packet").toString() : null;
				JSONObject jsonMainObject2 = (JSONObject) parser.parse(marketPacketDtoStr);
				String marketDataFor398RequestStr = jsonMainObject2.get("Data") != null ? jsonMainObject2.get("Data").toString() : null;
				JSONObject jsonMainObjectTarget = (JSONObject) parser.parse(marketDataFor398RequestStr);

				JSONArray warehouseIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WarehouseId").toString());
				JSONArray whatBaseArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WhatBase").toString());

				String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
				String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
				Object[] warehouseId = warehouseIdArray.toArray();
				Object[] whatBase = whatBaseArray.toArray();
//				String warehouseId = jsonMainObjectTarget.get("WarehouseId") == null ? null : jsonMainObjectTarget.get("WarehouseId").toString();
//				String whatBase = jsonMainObjectTarget.get("WhatBase") == null ? null : jsonMainObjectTarget.get("WhatBase").toString();

				MarketDataFor398Request for398Request = new MarketDataFor398Request(dateForm, dateTo, warehouseId, whatBase);
				MarketPacketDto marketPacketDto = new MarketPacketDto(marketJWT, "SpeedLogist.GetReport398", serviceNumber, for398Request);
				MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);

				String marketOrder2;
				try {
					marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto));
				} catch (Exception e) {
					e.printStackTrace();
					response.put("status", "100");
					response.put("exception", e.toString());
					response.put("message", "Ошибка запроса к Маркету");
					response.put("info", "Ошибка запроса к Маркету");
					return response;
				}
//				System.out.println(gson.toJson(requestDto));
				System.out.println("Размер: " + getStringSizeInMegabytes(marketOrder2) + " мб");

				JSONObject jsonTable = (JSONObject) parser.parse(marketOrder2);

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							poiExcel.createExcel398(jsonTable.get("Table").toString(), pathFolder, finalJ, stockStr, from, to);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();

		}
		java.util.Date t2 = new java.util.Date();
		List<String> emailsORL = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.398");
//		List<String> emailsORL = propertiesUtils.getValuesByPartialKey(servletContext, "email.test");

		long time = t2.getTime()-t1.getTime();

		String text = "Принято магазинов: " + mass.length + "\n"
				+ "С " + from + " по " + to + "\n"
				+ "Вид расходов : 11,12" + "\n"
				+ "Всего файлов: " + j + "\n"
				+ "Время работы: " + time + " мс";

		mailService.sendEmailToUsers(request, "Автоматическая выгрузка : 398", text, emailsORL);

		response.put("status", "200");
		return response;
	}

    public static double getStringSizeInMegabytes(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Строка не может быть null");
        }

        // Получаем размер строки в байтах (используя кодировку UTF-8)
        int sizeInBytes = input.getBytes(StandardCharsets.UTF_8).length;

        // Переводим байты в мегабайты (1 MB = 1024 * 1024 bytes)
        return (double) sizeInBytes / (1024 * 1024);
    }

    /**
     * тестовый метод 330 отчёта
     * @param request
     * @return
     * @throws ParseException
     */
	@TimedExecution
	@GetMapping("/330")
	public Map<String, Object> get330(HttpServletRequest request) throws ParseException {
		String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport330\", \"Data\": {\"DateFrom\": \"2024-11-05\", \"DateTo\": \"2024-11-29\", \"WarehouseId\": [1700], \"GoodsId\": [665635]}}}";
//		String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport330\", \"Data\": {\"DateFrom\": \"2024-11-05\", \"DateTo\": \"2024-11-29\", \"WarehouseId\": [1700], \"GoodsId\": []}}}";
		Map<String, Object> response = new HashMap<>();
		List<MarketDataFor330Responce> responces = new ArrayList<MarketDataFor330Responce>();
		try {
			checkJWT(marketUrl);
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		String marketPacketDtoStr = jsonMainObject.get("Packet") != null ? jsonMainObject.get("Packet").toString() : null;
		JSONObject jsonMainObject2 = (JSONObject) parser.parse(marketPacketDtoStr);
		String marketDataFor398RequestStr = jsonMainObject2.get("Data") != null ? jsonMainObject2.get("Data").toString() : null;
		JSONObject jsonMainObjectTarget = (JSONObject) parser.parse(marketDataFor398RequestStr);

		JSONArray warehouseIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WarehouseId").toString());
		JSONArray goodsIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("GoodsId").toString());

		String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
		String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
		Object[] warehouseId = warehouseIdArray.toArray();
		Object[] goodsId = goodsIdArray.toArray();

		MarketDataFor330Request for330Request = new MarketDataFor330Request(dateForm, dateTo, warehouseId, goodsId);
		MarketPacketDto marketPacketDto = new MarketPacketDto(marketJWT, "SpeedLogist.GetReport330", serviceNumber, for330Request);
		MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);

		String marketOrder2;
		try {
			marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "100");
			response.put("exception", e.toString());
			response.put("message", "Ошибка запроса к Маркету");
			response.put("info", "Ошибка запроса к Маркету");
			return response;
		}
		
		JSONObject jsonResponceMainObject = (JSONObject) parser.parse(marketOrder2);
		JSONArray jsonResponceTable = (JSONArray) parser.parse(jsonResponceMainObject.get("Table").toString());
		for (Object obj : jsonResponceTable) {
        	responces.add(new MarketDataFor330Responce(obj.toString())); // парсин json засунул в конструктор
        }
		
		response.put("status", "200");
		response.put("payload request", marketOrder2);
		response.put("responce", responces);
		return response;
	}

	@TimedExecution
	@GetMapping("/330/{from}&{to}&{stock}&{code}")
	public Map<String, Object> get330AndParam(HttpServletRequest request,
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable String stock,
			@PathVariable String code) throws ParseException {
		String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport330\", \"Data\": "
				+ "{\"DateFrom\": \""+from+"\", "
				+ "\"DateTo\": \""+to+"\", "
				+ "\"WarehouseId\": ["+stock+"], "
				+ "\"GoodsId\": ["+code+"]}}}";
		Map<String, Object> response = new HashMap<>();
		List<MarketDataFor330Responce> responces = new ArrayList<MarketDataFor330Responce>();
		try {
			checkJWT(marketUrl);
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		String marketPacketDtoStr = jsonMainObject.get("Packet") != null ? jsonMainObject.get("Packet").toString() : null;
		JSONObject jsonMainObject2 = (JSONObject) parser.parse(marketPacketDtoStr);
		String marketDataFor398RequestStr = jsonMainObject2.get("Data") != null ? jsonMainObject2.get("Data").toString() : null;
		JSONObject jsonMainObjectTarget = (JSONObject) parser.parse(marketDataFor398RequestStr);

		JSONArray warehouseIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WarehouseId").toString());
		JSONArray goodsIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("GoodsId").toString());

		String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
		String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
		Object[] warehouseId = warehouseIdArray.toArray();
		Object[] goodsId = goodsIdArray.toArray();

		MarketDataFor330Request for330Request = new MarketDataFor330Request(dateForm, dateTo, warehouseId, goodsId);
		MarketPacketDto marketPacketDto = new MarketPacketDto(marketJWT, "SpeedLogist.GetReport330", serviceNumber, for330Request);
		MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);

		String marketOrder2;
		try {
			marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "100");
			response.put("exception", e.toString());
			response.put("message", "Ошибка запроса к Маркету");
			response.put("info", "Ошибка запроса к Маркету");
			return response;
		}
		JSONObject jsonResponceMainObject = (JSONObject) parser.parse(marketOrder2);
		JSONArray jsonResponceTable = (JSONArray) parser.parse(jsonResponceMainObject.get("Table").toString());
		for (Object obj : jsonResponceTable) {
        	responces.add(new MarketDataFor330Responce(obj.toString())); // парсин json засунул в конструктор
        }
		response.put("status", "200");
		response.put("payload request", marketOrder2);
		response.put("responce", responces);
		return response;
				
	}
	
	/**
	 * Метод возвращает контрагентов и коды контрактов к каждому контрагенту
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/slots/delivery-schedule/getCounterpartyRC")
	public Map<String, Object> getCounterpartyRC(HttpServletRequest request, HttpServletResponse response) throws IOException{
		java.util.Date t1 = new java.util.Date();
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("counterparty", scheduleService.getcounterpartyListRC());
		responseMap.put("status", "200");
		java.util.Date t2 = new java.util.Date();
		responseMap.put("time", t2.getTime()-t1.getTime() + " ms" );
		return responseMap;		
	}
	
	/**
	 * Метод возвращает контрагентов и коды контрактов к каждому контрагенту
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/slots/delivery-schedule/getCounterpartyTO")
	public Map<String, Object> getCounterpartyTO(HttpServletRequest request, HttpServletResponse response) throws IOException{
		java.util.Date t1 = new java.util.Date();
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("counterparty", scheduleService.getcounterpartyListTO());
		responseMap.put("status", "200");
		java.util.Date t2 = new java.util.Date();
		responseMap.put("time", t2.getTime()-t1.getTime() + " ms" );
		return responseMap;		
	}
	
	/**
	 * Метод возвращает уникальные значения кодов контрактов с данными по коду контрагентов
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/slots/delivery-schedule/getUnicContractCodeHasCounterpartyTO")
	public Map<String, Object> getUnicContractCodeHasCounterpartyTO(HttpServletRequest request, HttpServletResponse response) throws IOException{
		java.util.Date t1 = new java.util.Date();
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("counterparty", scheduleService.getUnicCodeContractTO());
		responseMap.put("status", "200");
		java.util.Date t2 = new java.util.Date();
		responseMap.put("time", t2.getTime()-t1.getTime() + " ms" );
		return responseMap;		
	}
	
	@PostMapping("/logistics/deliveryShops/updateList")
	public Map<String, Object> postdeliveryShopsAddList(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		java.util.Date t1 = new java.util.Date();		
		User user = getThisUser();			
		Map<String, Object> response = new HashMap<String, Object>();
		String role = user.getRoles().stream().findFirst().get().getAuthority();
		JSONParser parser = new JSONParser();
//		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		JSONArray jsonArray = (JSONArray) parser.parse(str);
		
        for (Object obj : jsonArray) {
        	JSONObject jsonMainObject = (JSONObject) parser.parse(obj.toString());
        	Integer idTGTruck = jsonMainObject.get("idTGTruck") != null ? Integer.parseInt(jsonMainObject.get("idTGTruck").toString()) : null;
    		TGTruck tgTruck = tgTruckService.getTGTruckByChatId(idTGTruck);
    		tgTruck.setStatus(jsonMainObject.get("status") != null ? Integer.parseInt(jsonMainObject.get("status").toString()) : null);
    		tgTruck.setNameList(jsonMainObject.get("nameList") != null ? jsonMainObject.get("nameList").toString() : null);
    		tgTruckService.saveOrUpdateTGTruck(tgTruck);
        }
					
		Message message = new Message("TGBotRouting", user.getLogin(), null, "200", str, null, "updateList");
		slotWebSocket.sendMessage(message);	
		
		if(response.get("status") == null) {
			response.put("status", "200");
		}
		response.put("message", str);	
		java.util.Date t2 = new java.util.Date();
		System.out.println("deliveryShops/updateList " + (t2.getTime()-t1.getTime()) + " ms");
		return response;
	}
	
	@PostMapping("/logistics/deliveryShops/update")
	public Map<String, Object> postdeliveryShopsAdd(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		java.util.Date t1 = new java.util.Date();		
		User user = getThisUser();			
		Map<String, Object> response = new HashMap<String, Object>();
		String role = user.getRoles().stream().findFirst().get().getAuthority();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idTGTruck = jsonMainObject.get("idTGTruck") != null ? Integer.parseInt(jsonMainObject.get("idTGTruck").toString()) : null;
		TGTruck tgTruck = tgTruckService.getTGTruckByChatId(idTGTruck);
		
		if(tgTruck == null) {
			response.put("status", "100");
			response.put("info", "Отсутствует машина в БД. Машина отменена заявителем.");
			return response;
		}
		
		tgTruck.setStatus(jsonMainObject.get("status") != null ? Integer.parseInt(jsonMainObject.get("status").toString()) : null);
		tgTruck.setNameList(jsonMainObject.get("nameList") != null ? jsonMainObject.get("nameList").toString() : null);
		tgTruckService.saveOrUpdateTGTruck(tgTruck);
			
		Message message = new Message("TGBotRouting", user.getLogin(), null, "200", str, null, "update");
		slotWebSocket.sendMessage(message);	
		
		if(response.get("status") == null) {
			response.put("status", "200");
		}
		response.put("message", str);	
		java.util.Date t2 = new java.util.Date();
		System.out.println("deliveryShops/update " + (t2.getTime()-t1.getTime()) + " ms");
		return response;
	}
	
	/**
	 * Метод записывает кратность машины на график поставок
	 * @param request
	 * @param id
	 * @param num
	 * @return
	 */
	@GetMapping("/procurement/schedule/multiplicity/{id}&{num}")
	public Map<String, Object> getSetMultiplicity(
	        HttpServletRequest request,
	        @PathVariable String id,
	        @PathVariable String num) {	    
		Schedule schedule = scheduleService.getScheduleById(Integer.parseInt(id));		
	    Map<String, Object> response = new HashMap<>();
	    schedule.setMachineMultiplicity(Integer.parseInt(num));
	    scheduleService.updateSchedule(schedule);
	    response.put("status", "200");
	    response.put("schedule", schedule);	    	    
	    return response;
	}
	
	@GetMapping("/logistics/deliveryShops/CheckListName/{name}&{date}")
	public Map<String, Object> getCheckListName(
	        HttpServletRequest request,
	        @PathVariable String name,
	        @PathVariable String date) {	    
	    Map<String, Object> response = new HashMap<>();
	    response.put("status", "200");
	    response.put("isListName", tgTruckService.checkListName(name, Date.valueOf(date)));	    	    
	    return response;
	}
	
	/**
	 * отдаёт тг юзера по его chatId
	 * @param request
	 * @param chatId
	 * @return
	 */
	@GetMapping("/logistics/deliveryShops/getTGUser/{chatId}")
	public Map<String, Object> getTGUserByChatId(
	        HttpServletRequest request,
	        @PathVariable String chatId) {	    
	    Map<String, Object> response = new HashMap<>();
	    TGUser user = tgUserService.getTGUserByChatId(Long.parseLong(chatId));	        
	    response.put("status", "200");
	    response.put("body", user);	    	    
	    return response;
	}
	
	/**
	 * Отдвёт все машины начиная с сегодняшней даты и позже
	 * @param request
	 * @return
	 */
	@GetMapping("/logistics/deliveryShops/getTGTrucks")
	public Map<String, Object> getTGTruckList(
	        HttpServletRequest request) {	    
	    Map<String, Object> response = new HashMap<>();
	    List<TGTruck> tgTrucks = tgTruckService.getActualTGTruckList();
	    if(tgTrucks != null) {	    	
	    	response.put("status", "200");
	    	response.put("body", tgTrucks.stream().filter(t-> t.getStatus() != null).collect(Collectors.toList()));	    	    
	    	return response;	    	
	    }else {
	    	response.put("status", "200");
	    	response.put("body", tgTrucks);	    	    
	    	return response;
	    }
	}
	
	/**
	 * Удаление стоимости рейса
	 * @param request
	 * @param idRoute
	 * @param cost
	 * @param currency
	 * @return
	 */
	@GetMapping("/logistics/maintenance/clearCost/{idRoute}")
	public Map<String, Object> setCost(
	        HttpServletRequest request,
	        @PathVariable String idRoute) {	    
	    Map<String, Object> response = new HashMap<>();
	    Route route = routeService.getRouteById(Integer.parseInt(idRoute));
	    if(!route.getComments().equals("maintenance")) {
	    	response.put("status", "100");
		    response.put("messsage", "Неправильный тип маршрута. Выбран маршрут с типом " + route.getComments() + " а должен быть с типом maintenance");	    	    
		    return response;
	    }
	    route.setStatusRoute("220");
	    route.setFinishPrice(null);
	    route.setStartCurrency(null);
	    routeService.saveOrUpdateRoute(route);	        
	    response.put("status", "200");
	    response.put("body", route);	    	    
	    return response;
	}
	
	/**
	 * Установка стоимости за рейс
	 * @param request
	 * @param idRoute
	 * @param cost
	 * @param currency
	 * @return
	 */
	@GetMapping("/logistics/maintenance/setCost/{idRoute}&{cost}&{currency}")
	public Map<String, Object> setCost(
	        HttpServletRequest request,
	        @PathVariable String idRoute,
	        @PathVariable String cost,
	        @PathVariable String currency) {	    
	    Map<String, Object> response = new HashMap<>();
	    Route route = routeService.getRouteById(Integer.parseInt(idRoute));
	    if(!route.getComments().equals("maintenance")) {
	    	response.put("status", "100");
		    response.put("messsage", "Неправильный тип маршрута. Выбран маршрут с типом " + route.getComments() + " а должен быть с типом maintenance");	    	    
		    return response;
	    }
	    route.setStatusRoute("225");
	    route.setFinishPrice(Integer.parseInt(cost));
	    route.setStartCurrency(currency == null ? "BYN" : currency);
	    routeService.saveOrUpdateRoute(route);	        
	    response.put("status", "200");
	    response.put("body", route);	    	    
	    return response;
	}
	
	@GetMapping("/carrier/getMaintenanceList/{dateStart}&{dateEnd}")
	public Map<String, Object> getMaintenanceListAsCarrier(
	        HttpServletRequest request,
	        @PathVariable String dateStart,
	        @PathVariable String dateEnd) {
	    
	    Map<String, Object> response = new HashMap<>();
	    List<Route> routes = routeService.getMaintenanceListAsDateAndLogin(Date.valueOf(dateStart), Date.valueOf(dateEnd), getThisUser());
	    Set<Route> routes2 = new HashSet<>(routes);
	    response.put("status", "200");
	    response.put("body", routes2);   
	    return response;
	}
	
	/**
	 * Возвращает заказ по id
	 * @param request
	 * @param type
	 * @param idOrder
	 * @return
	 */
	@GetMapping("/{type}/getOrderById/{idOrder}")
	public Map<String, Object> getOrderById(
	        HttpServletRequest request,
	        @PathVariable String type,
	        @PathVariable String idOrder) {
	    
	    Map<String, Object> response = new HashMap<>();
	    Order order = orderService.getOrderById(Integer.parseInt(idOrder));
	    switch (type) {
	        case "procurement":
	            // Логика для procurement
	            break;
	        case "manager":
	            // Логика для logistics
	            break;
	        default:
	            throw new IllegalArgumentException("Неизвестная команда: " + type);
	    }
	    response.put("status", "200");
	    response.put("body", order);	    	    
	    return response;
	}
	
	@GetMapping("/logistics/maintenance/closeRoute/{idRoute}")
	public Map<String, Object> setMileage(
	        HttpServletRequest request,
	        @PathVariable String idRoute) {	    
	    Map<String, Object> response = new HashMap<>();
	    Route route = routeService.getRouteById(Integer.parseInt(idRoute));
	    if(!route.getComments().equals("maintenance")) {
	    	response.put("status", "100");
		    response.put("messsage", "Неправильный тип маршрута. Выбран маршрут с типом " + route.getComments() + " а должен быть с типом maintenance");	    	    
		    return response;
	    }
	    route.setStatusRoute("230");
	    Order order =  route.getOrders().stream().findFirst().get();
	    order.setStatus(70);
	    Set<Order> orders = new HashSet<Order>();
	    orders.add(order);
	    route.setOrders(orders);
	    routeService.saveOrUpdateRoute(route);
	        
	    response.put("status", "200");
	    response.put("body", route);	    	    
	    return response;
	}
	
	/**
	 * удаляет киллометраж из маршрута
	 * @param request
	 * @param idRoute
	 * @param type
	 * @return
	 */
	@GetMapping("/{type}/maintenance/clearMileage/{idRoute}")
	public Map<String, Object> clearMileage(
	        HttpServletRequest request,
	        @PathVariable String idRoute,
	        @PathVariable String type) {	    
	    Map<String, Object> response = new HashMap<>();
	    switch (type) {
        case "logistics":
            // Логика для logistics
            break;
        case "carrier":
            // Логика для carrier
            break;
        default:
            throw new IllegalArgumentException("Неизвестная команда: " + type);
    }
	    Route route = routeService.getRouteById(Integer.parseInt(idRoute));
	    if(!route.getComments().equals("maintenance")) {
	    	response.put("status", "100");
		    response.put("messsage", "Неправильный тип маршрута. Выбран маршрут с типом " + route.getComments() + " а должен быть с типом maintenance");	    	    
		    return response;
	    }
	    if(!route.getStatusRoute().equals("220")) {
	    	response.put("status", "100");
            response.put("messsage", "Неправильная команда. Статус маршрута не 220");                
            return response;
	    }
	    route.setKmInfo(null);
	    route.setFinishPrice(null);
	    route.setStartCurrency(null);
	    route.setStatusRoute("210");
	    routeService.saveOrUpdateRoute(route);
	    response.put("status", "200");
	    response.put("body", route);	    	    
	    return response;
	}
	
	/**
	 * добавляет пробег к выбранному маршруту
	 * @param request
	 * @param idRoute
	 * @return
	 */
	@GetMapping("/{type}/maintenance/setMileage/{idRoute}&{mileage}")
	public Map<String, Object> setMileage(
	        HttpServletRequest request,
	        @PathVariable String idRoute,
	        @PathVariable String mileage,
	        @PathVariable String type) {	    
	    Map<String, Object> response = new HashMap<>();
	    switch (type) {
        case "logistics":
            // Логика для logistics
            break;
        case "carrier":
            // Логика для carrier
            break;
        default:
            throw new IllegalArgumentException("Неизвестная команда: " + type);
    }
	    Route route = routeService.getRouteById(Integer.parseInt(idRoute));
	    if(!route.getComments().equals("maintenance")) {
	    	response.put("status", "100");
		    response.put("messsage", "Неправильный тип маршрута. Выбран маршрут с типом " + route.getComments() + " а должен быть с типом maintenance");	    	    
		    return response;
	    }
//	    if(route.getKmInfo() != null) {
//	    	response.put("status", "100");
//		    response.put("messsage", "На маршрут уже назначен пробег.");	    	    
//		    return response;
//	    }
//	    if(!route.getStatusRoute().equals("210")) {
//	    	response.put("status", "100");
//            response.put("messsage", "Неправильная команда. Статус маршрута не 210");                
//            return response;
//	    }
	    route.setKmInfo(Integer.parseInt(mileage));
	    route.setStatusRoute("220");
	    //сюда вставить расчёт стоимости
	    routeService.saveOrUpdateRoute(route);
	    response.put("status", "200");
	    response.put("body", route);	    	    
	    return response;
	}
	
	/**
	 * Удаляет из маршрута перевозчика
	 * @param request
	 * @param idRoute
	 * @return
	 */
	@GetMapping("/logistics/maintenance/clearCarrier/{idRoute}")
	public Map<String, Object> clearCarrier(
	        HttpServletRequest request,
	        @PathVariable String idRoute) {	    
	    Map<String, Object> response = new HashMap<>();
	    Route route = routeService.getRouteById(Integer.parseInt(idRoute));
	    if(!route.getComments().equals("maintenance")) {
	    	response.put("status", "100");
		    response.put("messsage", "Неправильный тип маршрута. Выбран маршрут с типом " + route.getComments() + " а должен быть с типом maintenance");	    	    
		    return response;
	    }
	    route.setUser(null);
	    route.setTruck(null);
	    route.setKmInfo(null);
	    route.setFinishPrice(null);
	    route.setStartCurrency(null);
	    route.setStatusRoute("200");
	    routeService.saveOrUpdateRoute(route);
	    response.put("status", "200");
	    response.put("body", route);	    	    
	    return response;
	}
	
	/**
	 * назначет водителя на маршрут принудительно
	 * @param request
	 * @param idRoute
	 * @param idCarrier
	 * @return
	 */
	@GetMapping("/logistics/maintenance/setCarrier/{idRoute}&{idCarrier}")
	public Map<String, Object> setCarrier(
	        HttpServletRequest request,
	        @PathVariable String idRoute,
	        @PathVariable String idCarrier) {
	    
	    Map<String, Object> response = new HashMap<>();
	    User user = userService.getUserById(Integer.parseInt(idCarrier));
	    user.setRoute(null);
	    user.setTrucks(null);
	    Route route = routeService.getRouteById(Integer.parseInt(idRoute));
	    if(!route.getComments().equals("maintenance")) {
	    	response.put("status", "100");
		    response.put("messsage", "Неправильный тип маршрута. Выбран маршрут с типом " + route.getComments() + " а должен быть с типом maintenance");	    	    
		    return response;
	    }
	    route.setUser(user);
	    route.setStatusRoute("210");
	    routeService.saveOrUpdateRoute(route);
	    response.put("status", "200");
	    response.put("body", route);	    	    
	    return response;
	}

	/**
	 * возвращает все маршруты с пометкой АХО
	 * @param request
	 * @param type
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@GetMapping("/{type}/getMaintenanceList/{dateStart}&{dateEnd}")
	public Map<String, Object> getMaintenanceList(
	        HttpServletRequest request,
	        @PathVariable String type,
	        @PathVariable String dateStart,
	        @PathVariable String dateEnd) {
	    
	    Map<String, Object> response = new HashMap<>();
	    List<Route> routes = routeService.getMaintenanceListAsDate(Date.valueOf(dateStart), Date.valueOf(dateEnd));
	    Set<Route> routes2 = new HashSet<>(routes);
	    response.put("status", "200");
	    response.put("body", routes2);
	    
	    // В зависимости от типа можно добавить дополнительную логику
	    switch (type) {
	        case "procurement":
	            // Логика для procurement
	            break;
	        case "logistics":
	            // Логика для logistics
	            break;
	        default:
	            throw new IllegalArgumentException("Неизвестная команда: " + type);
	    }	    
	    return response;
	}
	
		
	/**
	 * Метод создаёт маршрут для АХО
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	@PostMapping("/manager/maintenance/add")
	public Map<String, Object> postAddNewMaintenanceOrder(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
	       Map<String, Object> response = new HashMap<String, Object>();
	       JSONParser parser = new JSONParser();
	       JSONObject jsonMainObject = (JSONObject) parser.parse(str);
	       JSONArray idOrdersJSON = (JSONArray) parser.parse(jsonMainObject.get("idOrders").toString());
	       String points = jsonMainObject.get("points").toString();
	       Set<Order> orders = new HashSet<Order>();
//	     System.out.println(str);
	       for (Object string : idOrdersJSON) {
	          Order order = orderService.getOrderById(Integer.parseInt(string.toString()));
	          orders.add(order);       
	       }
	       Order order = orders.stream().findFirst().get();
	       if(order.getStatus() == 10) {
	          response.put("status", "100");
	          response.put("message", "Заявка " + order.getCounterparty() + " отменена!");
	          return response;
	       }
	       
	       
	       User user = getThisUser();
	       Route route = new Route();
	       
	       route.setDateUnloadPreviouslyStock(jsonMainObject.get("dateUnloadPreviouslyStock") != null ? jsonMainObject.get("dateUnloadPreviouslyStock").toString() : null);
	       route.setTimeUnloadPreviouslyStock(jsonMainObject.get("timeUnloadPreviouslyStock") != null ? jsonMainObject.get("timeUnloadPreviouslyStock").toString() : null);
	       route.setTotalLoadPall(jsonMainObject.get("loadPallTotal") != null ? jsonMainObject.get("loadPallTotal").toString() : null);
	       route.setTotalCargoWeight(jsonMainObject.get("cargoWeightTotal") != null ? jsonMainObject.get("cargoWeightTotal").toString() : null);
	       route.setComments("maintenance");
	       route.setCustomer(order.getManager());
	       route.setLogistInfo(user.getSurname() +" " + user.getName() + " " + user.getPatronymic() + "; "+user.getTelephone());
	       route.setTime(LocalTime.of(0, 5));
	       route.setTypeTrailer(jsonMainObject.get("typeTrailer") != null ? jsonMainObject.get("typeTrailer").toString() : null);
	       route.setStatusRoute("200");
	       route.setStatusStock("0");
//	     route.setUserComments(jsonMainObject.get("comment") != null ? jsonMainObject.get("comment").toString() : null);
	       route.setLogistComment(jsonMainObject.get("comment") != null ? jsonMainObject.get("comment").toString() : null);
	       route.setWay("АХО");
	       route.setTypeTrailer(jsonMainObject.get("typeTruck") != null ? jsonMainObject.get("typeTruck").toString() : null);
	       route.setTypeLoad(jsonMainObject.get("typeLoad") != null ? jsonMainObject.get("typeLoad").toString() : null);
	       route.setMethodLoad(jsonMainObject.get("methodLoad") != null ? jsonMainObject.get("methodLoad").toString() : null);
//	     route.setTruckInfo(jsonMainObject.get("truckInfo") != null ? jsonMainObject.get("truckInfo").toString() : null);
//	     route.setCargoInfo(jsonMainObject.get("cargoInfo") != null ? jsonMainObject.get("cargoInfo").toString() : null);
//	     route.setOnloadWindowDate(order.getOnloadWindowDate());
//	     route.setOnloadWindowTime(order.getOnloadWindowTime());
	       route.setLoadNumber(order.getLoadNumber());
	       List <Address> addresses = new ArrayList<Address>(order.getAddresses());
	       route.setDateUnloadPreviouslyStock(addresses.get(addresses.size()-1).getDate() != null ? addresses.get(addresses.size()-1).getDate().toLocalDate().toString() : null);
	       route.setTimeUnloadPreviouslyStock(addresses.get(addresses.size()-1).getTime() != null ? (addresses.get(addresses.size()-1).getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))) : null);
	       route.setOrders(orders);
	       String tnvd = null;
	       String routeDirectionMiddle = jsonMainObject.get("counterparty") != null ? jsonMainObject.get("counterparty").toString() : "";    
	       String routeDirectionBeginning = "<АХО> ";
	       route.setRouteDirection(routeDirectionBeginning + routeDirectionMiddle);
	       
	       Integer idRoute = routeService.saveRouteAndReturnId(route);
	       
	       
	       String firstJsonRequest = points.substring(1, points.length() - 1);
	       List<RouteHasShop> routeHasShopsArray = new ArrayList<RouteHasShop>();
	       String[] mass = firstJsonRequest.split("},");
	       JSONObject jsonpFirstObject = (JSONObject) parser.parse(mass[0] + "}");
	       route.setDateLoadPreviously(jsonpFirstObject.get("date").toString().isEmpty() ? null
	             : Date.valueOf(jsonpFirstObject.get("date").toString()));
	       if (!jsonpFirstObject.get("time").toString().isEmpty()) {
	          route.setTimeLoadPreviously(
	                LocalTime.of(Integer.parseInt(jsonpFirstObject.get("time").toString().split(":")[0]),
	                      Integer.parseInt(jsonpFirstObject.get("time").toString().split(":")[1])));
	       } else {
	          route.setTimeLoadPreviously(null);
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
	       route.setRouteDirection(route.getRouteDirection() + " N"+idRoute.toString());
	        route.setForReduction(false);
	       orders.forEach(o -> {
	          o.setStatus(50);
	          o.setLogist(user.getSurname() +" " + user.getName() + " " + user.getPatronymic() + "; ");
	          o.setLogistTelephone(user.getTelephone());
	          o.setChangeStatus(o.getChangeStatus() + "\nМаршрут создал: " + user.getSurname() + user.getName() + user.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:SS")));
	          orderService.updateOrder(o);
	       });
	       
	       routeService.saveOrUpdateRoute(route);
	       routeHasShopsArray.forEach(rhs -> routeHasShopService.saveOrUpdateRouteHasShop(rhs));

	       response.put("status", "200");
	       response.put("message", "Заявка " + route.getRouteDirection() + " создана");
	       response.put("route", route);
	       return response;      
	    }

	
	/**
	 * Ручная отправка сообщения с графиками на ТО
	 * @param request
	 * @return
	 */
	@GetMapping("/orl/sendEmailTO")
	public Map<String, Object> getSendEmailTO(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
	       
	       System.out.println("Start --- sendSchedulesTOHasORL");
	       // Получаем текущую дату для имени файла
	       LocalDate currentTime = LocalDate.now();
	       String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

	       List<String> emailsORL = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.to.ORL");
	       List<String> emailsSupportDepartment = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.to.supportDepartment");

	       Map<String, List<String>> draftLists = propertiesUtils.getListForDraftFolders(servletContext);

	       System.out.println(emailsORL);
//	      emails.addAll(emailsSupport);
	       String appPath = servletContext.getRealPath("/");



	       String fileName1200 = "1200 (----Холодный----).xlsm";
	           String fileName1100 = "1100 График прямой сухой.xlsm";
	           String fileNameSample = "График для шаблоново.xlsx";
	           String draftFolder = appPath + "resources/others/drafts/";

	           File draftFolderFile = new File(draftFolder);
	           if (draftFolderFile.exists()) {
	              deleteFolder(draftFolderFile);
	           }

	           draftFolderFile.mkdir();

	           try {
	              poiExcel.exportToExcelScheduleListTOWithMacro(scheduleService.getSchedulesListTOOnlyActual(scheduleService.getSchedulesByTOTypeWithTemp("холодный")),
	                    appPath + "resources/others/" + fileName1200);
	              poiExcel.exportToExcelScheduleListTOWithMacro(scheduleService.getSchedulesListTOOnlyActual(scheduleService.getSchedulesByTOTypeWithTemp("сухой")),
	                    appPath + "resources/others/" + fileName1100);
	              poiExcel.exportToExcelSampleListTO(scheduleService.getSchedulesListTOOnlyActual(scheduleService.getSchedulesByTOTypeWithTemp("холодный")),
	                    appPath + "resources/others/" + fileNameSample);
	              poiExcel.exportToExcelDrafts(scheduleService.getSchedulesListTOOnlyActual(scheduleService.getSchedulesListTOWithTemp()), draftFolder);

	           } catch (IOException e) {
	              e.printStackTrace();
	              System.err.println("Ошибка формирование EXCEL");
	           }

	           List<File> files = new ArrayList<File>();
	           files.add(new File(appPath + "resources/others/" + fileName1200));
	           files.add(new File(appPath + "resources/others/" + fileName1100));
	           files.add(new File(appPath + "resources/others/" + fileNameSample));

	           File folder = new File(draftFolder);
	           List<File> draftFiles = new ArrayList<File>(); //для теста черновиков
	           Map <String, List<File>> draftFilesMap = new HashMap<>();

	           File[] drafts = folder.listFiles();

	           for (String key: draftLists.keySet()){
	              draftFilesMap.put(key, new ArrayList<>());
	           }

	           if (drafts != null) {
	              for (File file: drafts){
	                 String fileName = file.getName();

	                 for (String key: draftLists.keySet()){

	                    for (String draftNumber: draftLists.get(key)){
	                       String regEx = " " + draftNumber + " ";

	                       if (fileName.contains(regEx)){
	                          draftFilesMap.get(key).add(file);
	                       }
	                    }
	                 }

	                 if (fileName.contains("виртуальный")){
	                    draftFilesMap.get("ORL").add(file);
	                 }

	                 draftFiles.add(file); //для теста черновиков
	              }

	           }

	           //files.add(new File(appPath + "resources/others/drafts"));

	           System.out.println(appPath + "resources/others/");

	           File zipFile;
	           File zipFileDrafts; //для теста черновиков
	           File zipFileDraftsListORL;
	           File zipFileDraftsListSupportDepartment;

	           List <File> filesZipORL = new ArrayList<File>();
	           List <File> filesZipSupportDepartment = new ArrayList<File>();

	           try {
	              zipFile = createZipFile(files, appPath + "resources/others/TO.zip");
	              zipFileDrafts = createZipFile(draftFiles, appPath + "resources/others/Шаблоны.zip"); //для теста черновиков

	              zipFileDraftsListORL = createZipFile(draftFilesMap.get("ORL"), appPath + "resources/others/ORL.zip");
	              zipFileDraftsListSupportDepartment = createZipFile(draftFilesMap.get("SupportDepartment"), appPath + "resources/others/SupportDepartment.zip");

	              filesZipORL.add(zipFile);
	              filesZipSupportDepartment.add(zipFile);

	              filesZipORL.add(zipFileDraftsListORL);
	              filesZipSupportDepartment.add(zipFileDraftsListSupportDepartment);

	           } catch (IOException e) {
	              e.printStackTrace();
	           }

	       mailService.sendEmailWithFilesToUsers(servletContext, "Графики поставок на TO " + currentTimeString, "Ручная отправка графиков поставок на ТО\nВерсия с макросом выделений (Ctr+t)", filesZipORL, emailsORL);
	       mailService.sendEmailWithFilesToUsers(servletContext, "Графики поставок на TO " + currentTimeString, "Ручная отправка отправка графиков поставок на ТО\nВерсия с макросом выделений (Ctr+t)", filesZipSupportDepartment, emailsSupportDepartment);

	       System.out.println("Finish --- sendSchedulesHasTOORL");
	       
	        response.put("status", "200");
	       response.put("message", "Сообщение отправлено");
	       
	       return response;      		
	}
		
	/**
	 * Ручная отправка сообщения с графиком поставок на РЦ
	 * @param request
	 * @return
	 */
	@GetMapping("/orl/sendEmail")
	public Map<String, Object> getSendEmailRC(HttpServletRequest request) {
		// Получаем текущую дату для имени файла
		Map<String, Object> response = new HashMap<String, Object>();
		User user = getThisUser();
		
		// Получаем текущую дату для имени файла
        LocalDate currentTime = LocalDate.now();
        String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        
		List<String> emails = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.rc");
		List<String> emailsSupport = propertiesUtils.getValuesByPartialKey(servletContext, "email.orderSupport");
		emails.addAll(emailsSupport);
		String appPath = servletContext.getRealPath("/");
		
		String fileName1200 = "1200.xlsx";
		String fileName1250 = "1250.xlsx";
		String fileName1700 = "1700.xlsx";
		String fileName1800 = "1800.xlsx";
		
		try {
			poiExcel.exportToExcelScheduleListRC(scheduleService.getSchedulesByStock(1200).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1200);
			poiExcel.exportToExcelScheduleListRC(scheduleService.getSchedulesByStock(1250).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1250);
			poiExcel.exportToExcelScheduleListRC(scheduleService.getSchedulesByStock(1700).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1700);
			poiExcel.exportToExcelScheduleListRC(scheduleService.getSchedulesByStock(1800).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1800);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Ошибка формирование EXCEL");
		}
		
//		response.setHeader("content-disposition", "attachment;filename="+fileName+".xlsx");
		List<File> files = new ArrayList<File>();
		files.add(new File(appPath + "resources/others/" + fileName1200));
		files.add(new File(appPath + "resources/others/" + fileName1250));
		files.add(new File(appPath + "resources/others/" + fileName1700));
		files.add(new File(appPath + "resources/others/" + fileName1800));
		
		mailService.sendEmailWithFilesToUsers(request.getServletContext(), "Графики поставок (РЦ) на " + currentTimeString, "Сообщение отправлено вручную пользователем : " + user.getSurname() + " " + user.getName() , files, emails);
		response.put("status", "200");
		response.put("message", "Сообщение отправлено");		
		return response;		
	}
	
	@GetMapping("/orl/need/getNeed/{date}")
	public Map<String, Object> getNeedList(HttpServletRequest request, @PathVariable String date) {
		Map<String, Object> response = new HashMap<String, Object>();	
		
		List<OrderProduct> products = orderProductService.getOrderProductListHasDate(Date.valueOf(LocalDate.parse(date)));
		
		if(products == null) {
			response.put("status", "100");
			response.put("message", "ошибка выполнения метода; Возвращен null");
			return response;
		}
				
		response.put("status", "200");
		response.put("body", products);
		return response;		
	}
	
	/**
	 * Обрабатывает загрузку и обработку Excel-файла с данными заказов и их потребностями.
	 * 
	 * @param model       Модель для передачи данных в представление (не используется в данном методе).
	 * @param request     Объект HttpServletRequest для получения данных запроса.
	 * @param session     Текущая HTTP-сессия пользователя.
	 * @param excel       Файл Excel, содержащий данные, которые необходимо загрузить и обработать. Этот параметр не является обязательным.
	 * @param dateStr     Строка, представляющая дату, для которой должны быть загружены данные. Этот параметр не является обязательным.
	 * 
	 * @return            Карта (Map) с ответом, содержащая статус и сообщение о результате обработки.
	 * 
	 * @throws InvalidFormatException  Если формат загружаемого файла Excel недопустим.
	 * @throws IOException             Если произошла ошибка ввода-вывода при обработке файла.
	 * @throws ServiceException        Если произошла ошибка в сервисном слое.
	 * 
	 * Метод выполняет следующие действия:
	 * 1. Проверяет, если ли уже загруженные данные для указанной даты (если дата не указана, используется текущая дата).
	 *    Если данные найдены, метод возвращает ответ с соответствующим сообщением и статусом.
	 * 2. Сохраняет загруженный Excel-файл на сервере.
	 * 3. Парсит Excel-файл и формирует карту (Map) объектов OrderProduct.
	 * 4. Получает список всех продуктов и преобразует его в карту для быстрого поиска по коду продукта.
	 * 5. Проходит по каждому элементу карты OrderProduct:
	 *    - Если продукт найден в базе, привязывает его к заказу и обновляет продукт.
	 *    - Если продукт не найден, создает новый продукт, связывает его с заказом и сохраняет его в базе.
	 * 6. Возвращает ответ с сообщением о завершении обработки.
	 */
	@RequestMapping(value = "/orl/need/load", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, String> postLoadExcelNeed (Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "excel", required = false) MultipartFile excel,
			@RequestParam(value = "date", required = false) String dateStr)
			throws InvalidFormatException, IOException, ServiceException {
		
		Map<String, String> response = new HashMap<String, String>();
		dateStr = dateStr.isEmpty() ? null : dateStr;
		
		List<OrderProduct> orderProducts = orderProductService.getOrderProductListHasDate(Date.valueOf(dateStr == null ? LocalDate.now().toString() : dateStr));
		if(orderProducts != null && !orderProducts.isEmpty()) {
			response.put("status", "100");
			response.put("message", "расчёт заказов на " + (dateStr == null ? LocalDate.now().toString() : dateStr) + " уже загружен");
			return response;
		}
		
		
		File file1 = poiExcel.getFileByMultipartTarget(excel, request, "need.xlsx");
		
//		if(poiExcel.getColumnCount(file1,2) <= 3) {
//			response.put("status", "100");
//			response.put("message", "Файл безнадёжно устарел! Обратитесь в ОРЛ для предоставления новго файла (с расчётом на каждый склад)");
//			return response;
//		}

		Map<Integer, OrderProduct> mapOrderProduct = new HashMap<Integer, OrderProduct>();
		try {
			mapOrderProduct = poiExcel.loadNeedExcel2(file1, dateStr);
		} catch (ORLExcelException e) {
			response.put("status", "105");
			response.put("message", e.getMessage());
			return response;
		}catch (InvalidFormatException | IOException | java.text.ParseException | ServiceException e) {
			e.printStackTrace();
		}

		List<Product> products = productService.getAllProductList();
		Map<Integer, Product> productsMap = products.stream().collect(Collectors.toMap(
		        Product::getCodeProduct,
		        product -> product,
		        (existing, replacement) -> existing // игнорируем дубликат
		    ));


		for (Map.Entry<Integer, OrderProduct> entry : mapOrderProduct.entrySet()) {
			Product product = productsMap.get(entry.getKey());
			if(product == null) {
				System.err.println("Продукт не найден " + entry.getValue());
				//создаём новый продукт
				product = new Product();
				product.setCodeProduct(entry.getKey());
				product.setName(entry.getValue().getNameProduct());
				product.setIsException(false);
				entry.getValue().setProduct(product);
				product.addOrderProducts(entry.getValue());
				productService.saveProduct(product);
				continue;
			}
			entry.getValue().setProduct(product);
			product.addOrderProducts(entry.getValue());
			productService.updateProduct(product);
		}

		fillOrderCalculation(mapOrderProduct, dateStr);

		//Тут будут проверки по потребностям согласно таблице заказов
		
		
		response.put("status", "200");
		response.put("message", "Готово");
		return response;
	}
	
	/**
	 * проверка при постановке слотов относительно графика поставок
	 * @param request
	 * @param num
	 * @param date
	 * @return
	 */
	@GetMapping("/slots/delivery-schedule/checkSchedule/{num}&{date}")
	public Map<String, Object> getCheckSchedule(HttpServletRequest request, @PathVariable String num, @PathVariable String date) {
		Map<String, Object> response = new HashMap<String, Object>();	
		Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(num));
		
		if(schedule == null) {
			response.put("status", "200");
			response.put("flag", false);
			response.put("body", null);
			return response;
		}
		
		boolean flag = chheckScheduleMethod(schedule, date);
				
		response.put("status", "200");
		response.put("flag", flag);
		response.put("body", schedule);
		return response;		
	}
	
	/**
	 * метод проверки ставится ли по графику на текущую дату заказ	 * 
	 * @param schedule - сам график поставок
	 * @param date - день на который ставится
	 * @return - если true - то всё ок. Если нет то false
	 */
	private boolean chheckScheduleMethod(Schedule schedule, String date) {
		Date dateTarget = Date.valueOf(date);
		String targetDayOfWeek = dateTarget.toLocalDate().getDayOfWeek().toString();
		
		boolean flag = false;
		
		for (Map.Entry<String, String> entry : schedule.getDaysMap().entrySet()) {
			String day = entry.getKey();
			String value = entry.getValue();
			if(targetDayOfWeek.equals(day)) {
				if(value.contains("понедельник") || value.contains("вторник") || value.contains("среда") || value.contains("четверг") || value.contains("пятница")
						|| value.contains("суббота") || value.contains("воскресенье")) {
					flag = true;
					break;
				}
				
			}
		}
		
		return flag;		
	}
	
	
	/**
	 * Метод даёт инфу развернутую инфу для записи в стектрейс
	 * <br> по простановке заказа согласно графика поставок
	 * <br> тут же отправляется сообщение, если график поставок отсутствует
	 * <br> название поставщика, для маил сообщения
	 * 
	 * @param num
	 * @param date
	 * @return
	 * @throws IOException 
	 */
	@TimedExecution
	private String chheckScheduleMethodAllInfo (HttpServletRequest request ,String num, String date, String companyName) throws IOException {	
		
		//тут отправляем на почту сообщение
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
		properties = new Properties();
		properties.load(fileInputStream);
		User user = getThisUser();
		
		
		if(num == null) {
			return "ННЗ"; // нет номера заказа (в заказе)
		}
		
		Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(num));
		
		if(schedule == null) {
			//тут
			String text = "Уведомление SpeedLogist: \nПоставщик  " + companyName + " с номером контракта " + num + " не найден в графике поставк. \nНеобходимо добавить график поставок данного контрагента.";
			mailService.sendSimpleEmail(appPath, "Отсутствует график поставок", text, user.geteMail());
			return "НГП"; // нет графика поставок в бд
		}
		
		boolean flag = chheckScheduleMethod(schedule, date);
		if(flag) {
			return "true";
		}else {
			return "false";
		}		
	}
	
	/**
	 * Важный метод, который удаляет ВРЕМЕННЫЕ графики поставок по коду контракта
	 * @param request
	 * @param num
	 * @return
	 */
	@GetMapping("/slots/delivery-schedule/delScheduleByNumContract/{num}")
	public Map<String, Object> delScheduleByNumContract(HttpServletRequest request, @PathVariable String num) {
		Map<String, Object> response = new HashMap<String, Object>();	
		List<Schedule> schedules = scheduleService.getSchedulesListTOContractOnlyTemp(Long.parseLong(num));
		if(schedules == null || schedules.isEmpty()) {
			response.put("status", "100");
			response.put("message", "Временных графиков по коду контракта " + num + " нет");
			response.put("info", "Временных графиков по коду контракта " + num + " нет");
			return response;
		}
		String numContract = num;
		int count = 0;
		for (Schedule schedule : schedules) {
			scheduleService.deleteOrderById(schedule.getIdSchedule());  // на оптимизацию.
			count++;
		}
		response.put("status", "200");
		response.put("numContract", numContract);
		response.put("deleteRows", count);
		
		User user = getThisUser();
		
		String text = "Удалено временных графиков поставок по контракту " + numContract + ". Количество удаленных графиков: " + count + ""
				+ "\nПользователь: " + user.getSurname() + " " + user.getName();
		
		List<String> emails = propertiesUtils.getValuesByPartialKey(request.getServletContext(), "email.orl.to.ORL");
		mailService.sendEmailToUsers(request, "Удаление временных графиков поставо", text, emails);		
		return response;		
	}
	
	@GetMapping("/slots/delivery-schedule/getScheduleNumContract/{num}")
	public Map<String, Object> getScheduleNumContract(HttpServletRequest request, @PathVariable String num) {
		Map<String, Object> response = new HashMap<String, Object>();	
		response.put("status", "200");
		response.put("body", scheduleService.getScheduleByNumContract(Long.parseLong(num)));
		return response;		
	}
	
	/**
	 * изменение статуса графика поставок
	 * @param request
	 * @param num
	 * @param status
	 * @return
	 */
	@GetMapping("/slots/delivery-schedule/changeStatus/{num}&{status}")
	public Map<String, Object> getChangeStatus(HttpServletRequest request, @PathVariable String num, @PathVariable String status) {
		Map<String, Object> response = new HashMap<String, Object>();	
		User user = getThisUser();
		Integer role = user.getRoles().stream().findFirst().get().getIdRole();

//		if(role != 10 && role != 1 && role != 14) {
//			response.put("status", "100");
//			response.put("message", "Отказано! Данная роль не обладает правами на действие");
//			response.put("info", "Отказано! Данная роль не обладает правами на действие");
//			return response;
//		}
		
		if(num == null || status == null) {
			response.put("status", "100");
			response.put("message", "Параметры не заданы");
			response.put("info", "Параметры не заданы");
			return response;
		}
//		Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(num));
		Schedule schedule = scheduleService.getScheduleById(Integer.parseInt(num));
		schedule.setStatus(Integer.parseInt(status));
		
		String statusStr = null;
		
		switch (status) {
		case "0":
			statusStr = "delete";
			break;
		case "10":
			statusStr = "cancel";
			break;
		case "20":
			statusStr = "confirm";
			break;
		}
		
		String history = user.getSurname() + " " + user.getName() + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ";"+statusStr+"\n"; 		
		schedule.setHistory((schedule.getHistory() != null ? schedule.getHistory() : "") + history);		
		
		scheduleService.updateSchedule(schedule);
		
		response.put("status", "200");
		response.put("message", "Статус изменен");
		response.put("info", "Статус изменен");
		return response;		
	}
	
	/**
	 * Метод меняет кодовое имя кванта по коду контрагента
	 * @param request
	 * @param num
	 * @param status
	 * @return
	 */
	@GetMapping("/slots/delivery-schedule/changeNameOfQuantum/{num}&{name}")
	public Map<String, Object> getChangeNameOfQuantum(HttpServletRequest request, @PathVariable String num, @PathVariable String name) {
		Map<String, Object> response = new HashMap<String, Object>();	
		
		if(name.trim().equals("null")) name = null;
		response.put("count", scheduleService.updateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty(Long.parseLong(num.trim()), name));
		response.put("status", "200");
		return response;
		}
	
	
	
	/**
	 * Pедактирование графика поставок
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 */
	@PostMapping("/slots/delivery-schedule/editRC")
	public Map<String, String> postEditDeliverySchedule(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException, CloneNotSupportedException {
		Map<String, String> response = new HashMap<String, String>();
		
		User user = getThisUser();
		Integer role = user.getRoles().stream().findFirst().get().getIdRole();
		
		if(role != 10 && role != 1) {
			response.put("status", "100");
			response.put("message", "Отказано! Данная роль не обладает правами на действие");
			return response;
		}
		
		if(str == null) {
			response.put("status", "100");
			response.put("message", "Тело запроса = null");
			return response;
		}
			
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		if(jsonMainObject.get("idSchedule") == null) {
			response.put("status", "100");
			response.put("message", "idSchedule = null");
			return response;
		}
		
		Schedule schedule = scheduleService.getScheduleById(Integer.parseInt(jsonMainObject.get("idSchedule").toString()));
		schedule.setCounterpartyCode(jsonMainObject.get("counterpartyCode") == null || jsonMainObject.get("counterpartyCode").toString().isEmpty() ? null : Long.parseLong(jsonMainObject.get("counterpartyCode").toString()));
		schedule.setCounterpartyContractCode(jsonMainObject.get("counterpartyContractCode") == null || jsonMainObject.get("counterpartyContractCode").toString().isEmpty() ? null : Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()));
		schedule.setSupplies(jsonMainObject.get("supplies") == null || jsonMainObject.get("supplies").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("supplies").toString()));
		schedule.setNumStock(jsonMainObject.get("numStock") == null || jsonMainObject.get("numStock").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("numStock").toString()));
		schedule.setRunoffCalculation(jsonMainObject.get("runoffCalculation") == null || jsonMainObject.get("runoffCalculation").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("runoffCalculation").toString()));
		schedule.setName(jsonMainObject.get("name") == null || jsonMainObject.get("name").toString().isEmpty() ? null : jsonMainObject.get("name").toString());
		schedule.setNote(jsonMainObject.get("note") == null || jsonMainObject.get("note").toString().isEmpty() ? null : jsonMainObject.get("note").toString());
		schedule.setMonday(jsonMainObject.get("monday") == null || jsonMainObject.get("monday").toString().isEmpty() ? null : jsonMainObject.get("monday").toString());
		schedule.setTuesday(jsonMainObject.get("tuesday") == null || jsonMainObject.get("tuesday").toString().isEmpty() ? null : jsonMainObject.get("tuesday").toString());
		schedule.setWednesday(jsonMainObject.get("wednesday") == null || jsonMainObject.get("wednesday").toString().isEmpty() ? null : jsonMainObject.get("wednesday").toString());
		schedule.setThursday(jsonMainObject.get("thursday") == null || jsonMainObject.get("thursday").toString().isEmpty() ? null : jsonMainObject.get("thursday").toString());
		schedule.setFriday(jsonMainObject.get("friday") == null || jsonMainObject.get("friday").toString().isEmpty() ? null : jsonMainObject.get("friday").toString());
		schedule.setSaturday(jsonMainObject.get("saturday") == null || jsonMainObject.get("saturday").toString().isEmpty() ? null : jsonMainObject.get("saturday").toString());
		schedule.setSunday(jsonMainObject.get("sunday") == null || jsonMainObject.get("sunday").toString().isEmpty() ? null : jsonMainObject.get("sunday").toString());
		schedule.setTz(jsonMainObject.get("tz") == null || jsonMainObject.get("tz").toString().isEmpty() ? null : jsonMainObject.get("tz").toString());
		schedule.setTp(jsonMainObject.get("tp") == null || jsonMainObject.get("tp").toString().isEmpty() ? null : jsonMainObject.get("tp").toString());
		schedule.setComment(jsonMainObject.get("comment") == null || jsonMainObject.get("comment").toString().isEmpty() ? null : jsonMainObject.get("comment").toString());
		schedule.setDescription(jsonMainObject.get("description") == null || jsonMainObject.get("description").toString().isEmpty() ? null : jsonMainObject.get("description").toString());
		schedule.setMultipleOfPallet(jsonMainObject.get("multipleOfPallet") == null || jsonMainObject.get("multipleOfPallet").toString().isEmpty() ? null : jsonMainObject.get("multipleOfPallet").toString().equals("true") ? true : false);
		schedule.setMultipleOfTruck(jsonMainObject.get("multipleOfTruck") == null || jsonMainObject.get("multipleOfTruck").toString().isEmpty() ? null : jsonMainObject.get("multipleOfTruck").toString().equals("true") ? true : false);
		schedule.setMachineMultiplicity(jsonMainObject.get("machineMultiplicity") == null || jsonMainObject.get("machineMultiplicity").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("machineMultiplicity").toString()));
		schedule.setConnectionSupply(jsonMainObject.get("connectionSupply") == null || jsonMainObject.get("connectionSupply").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("connectionSupply").toString()));
//		schedule.setCodeNameOfQuantumCounterparty(jsonMainObject.get("codeNameOfQuantumCounterparty") == null || jsonMainObject.get("codeNameOfQuantumCounterparty").toString().isEmpty() ? null : jsonMainObject.get("codeNameOfQuantumCounterparty").toString());
//		schedule.setQuantumMeasurements(jsonMainObject.get("quantumMeasurements") == null || jsonMainObject.get("quantumMeasurements").toString().isEmpty() ? null : jsonMainObject.get("quantumMeasurements").toString());
//		schedule.setQuantum(jsonMainObject.get("quantum") == null || jsonMainObject.get("quantum").toString().isEmpty() ? null : Double.parseDouble(jsonMainObject.get("quantum").toString()));

		String history = user.getSurname() + " " + user.getName() + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ";update\n"; 
		
		schedule.setHistory((schedule.getHistory() != null ? schedule.getHistory() : "") + history);
		schedule.setDateLastChanging(Date.valueOf(LocalDate.now()));
//		saveActionInFileSchedule(request, "resources/others/blackBox/schedule", scheduleOld, scheduleOld, user.getSurname() + " " + user.getName());
		
		scheduleService.updateSchedule(schedule);		
		
		response.put("status", "200");
		response.put("message", "Отредактировано");
		return response;		
	}
	
	/**
	 * Создание графика поставок а РЦ
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@PostMapping("/slots/delivery-schedule/createRC")
	public Map<String, Object> postCreateDeliveryScheduleRC(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<String, Object>();
		if(str == null) {
			response.put("status", "100");
			response.put("message", "Тело запроса = null");
			return response;
		}
		
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		
		if(jsonMainObject.get("counterpartyContractCode") == null) {
			response.put("status", "100");
			response.put("message", "Отсутствует номер контаркта");
			return response;
		}
		
		Schedule scheduleOld = scheduleService.getScheduleByNumContract(Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()));
		
		if(scheduleOld != null) {
			response.put("status", "100");
			response.put("message", "Данный контракт уже имеется в базе данных");
			response.put("body", scheduleOld);
			return response;
		}
		
		Schedule schedule = new Schedule();		
		schedule.setCounterpartyCode(jsonMainObject.get("counterpartyCode") == null || jsonMainObject.get("counterpartyCode").toString().isEmpty() ? null : Long.parseLong(jsonMainObject.get("counterpartyCode").toString()));
		schedule.setCounterpartyContractCode(jsonMainObject.get("counterpartyContractCode") == null || jsonMainObject.get("counterpartyContractCode").toString().isEmpty() ? null : Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()));
		schedule.setSupplies(jsonMainObject.get("supplies") == null || jsonMainObject.get("supplies").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("supplies").toString()));
		schedule.setNumStock(jsonMainObject.get("numStock") == null || jsonMainObject.get("numStock").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("numStock").toString()));
		schedule.setRunoffCalculation(jsonMainObject.get("runoffCalculation") == null || jsonMainObject.get("runoffCalculation").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("runoffCalculation").toString()));
		schedule.setName(jsonMainObject.get("name") == null || jsonMainObject.get("name").toString().isEmpty() ? null : jsonMainObject.get("name").toString());
		schedule.setNote(jsonMainObject.get("note") == null || jsonMainObject.get("note").toString().isEmpty() ? null : jsonMainObject.get("note").toString());
		schedule.setMonday(jsonMainObject.get("monday") == null || jsonMainObject.get("monday").toString().isEmpty() ? null : jsonMainObject.get("monday").toString());
		schedule.setTuesday(jsonMainObject.get("tuesday") == null || jsonMainObject.get("tuesday").toString().isEmpty() ? null : jsonMainObject.get("tuesday").toString());
		schedule.setWednesday(jsonMainObject.get("wednesday") == null || jsonMainObject.get("wednesday").toString().isEmpty() ? null : jsonMainObject.get("wednesday").toString());
		schedule.setThursday(jsonMainObject.get("thursday") == null || jsonMainObject.get("thursday").toString().isEmpty() ? null : jsonMainObject.get("thursday").toString());
		schedule.setFriday(jsonMainObject.get("friday") == null || jsonMainObject.get("friday").toString().isEmpty() ? null : jsonMainObject.get("friday").toString());
		schedule.setSaturday(jsonMainObject.get("saturday") == null || jsonMainObject.get("saturday").toString().isEmpty() ? null : jsonMainObject.get("saturday").toString());
		schedule.setSunday(jsonMainObject.get("sunday") == null || jsonMainObject.get("sunday").toString().isEmpty() ? null : jsonMainObject.get("sunday").toString());
		schedule.setTz(jsonMainObject.get("tz") == null || jsonMainObject.get("tz").toString().isEmpty() ? null : jsonMainObject.get("tz").toString());
		schedule.setTp(jsonMainObject.get("tp") == null || jsonMainObject.get("tp").toString().isEmpty() ? null : jsonMainObject.get("tp").toString());
		schedule.setComment(jsonMainObject.get("comment") == null || jsonMainObject.get("comment").toString().isEmpty() ? null : jsonMainObject.get("comment").toString());
		schedule.setDescription(jsonMainObject.get("description") == null || jsonMainObject.get("description").toString().isEmpty() ? null : jsonMainObject.get("description").toString());
		schedule.setMultipleOfPallet(jsonMainObject.get("multipleOfPallet") == null || jsonMainObject.get("multipleOfPallet").toString().isEmpty() ? null : jsonMainObject.get("multipleOfPallet").toString().equals("true") ? true : false);
		schedule.setMultipleOfTruck(jsonMainObject.get("multipleOfTruck") == null || jsonMainObject.get("multipleOfTruck").toString().isEmpty() ? null : jsonMainObject.get("multipleOfTruck").toString().equals("true") ? true : false);
		schedule.setMachineMultiplicity(jsonMainObject.get("machineMultiplicity") == null || jsonMainObject.get("machineMultiplicity").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("machineMultiplicity").toString()));
		schedule.setConnectionSupply(jsonMainObject.get("connectionSupply") == null || jsonMainObject.get("connectionSupply").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("connectionSupply").toString()));
		schedule.setIsNotCalc(false);
		schedule.setIsImport(false);
		schedule.setStatus(10);
		
		schedule.setIsDayToDay(false);
		
		String type = jsonMainObject.get("type") == null || jsonMainObject.get("type").toString().isEmpty() ? null : jsonMainObject.get("type").toString();
		if(type !=null) {
			if(!type.equals("РЦ") && !type.equals("ТО")) {
				response.put("status", "100");
				response.put("message", "Ошибка в поле type: ожидается ТО или РЦ");
				response.put("info", "Ошибка в поле type: ожидается ТО или РЦ");
				return response;
			}
		}
		schedule.setType(type);
		schedule.setOrderFormationSchedule(jsonMainObject.get("orderFormationSchedule") == null || jsonMainObject.get("orderFormationSchedule").toString().isEmpty() ? null : jsonMainObject.get("orderFormationSchedule").toString());
		schedule.setOrderShipmentSchedule(jsonMainObject.get("orderShipmentSchedule") == null || jsonMainObject.get("orderShipmentSchedule").toString().isEmpty() ? null : jsonMainObject.get("orderShipmentSchedule").toString());
		schedule.setToType(jsonMainObject.get("toType") == null || jsonMainObject.get("toType").toString().isEmpty() ? null : jsonMainObject.get("toType").toString());
//		schedule.setCodeNameOfQuantumCounterparty(jsonMainObject.get("codeNameOfQuantumCounterparty") == null || jsonMainObject.get("codeNameOfQuantumCounterparty").toString().isEmpty() ? null : jsonMainObject.get("codeNameOfQuantumCounterparty").toString());
//		schedule.setQuantumMeasurements(jsonMainObject.get("quantumMeasurements") == null || jsonMainObject.get("quantumMeasurements").toString().isEmpty() ? null : jsonMainObject.get("quantumMeasurements").toString());
//		schedule.setQuantum(jsonMainObject.get("quantum") == null || jsonMainObject.get("quantum").toString().isEmpty() ? null : Double.parseDouble(jsonMainObject.get("quantum").toString()));

		User user = getThisUser();
		String history = user.getSurname() + " " + user.getName() + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ";create\n"; 
		
		schedule.setHistory((schedule.getHistory() != null ? schedule.getHistory() : "") + history);
		
		Integer id = scheduleService.saveSchedule(schedule);		
		
		//тут отправляем на почту сообщение
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
		properties = new Properties();
		properties.load(fileInputStream);
		String text = "Создан новый график поставок сотрудником: " + user.getSurname() + " " + user.getName() + " \nПоставщик: " + schedule.getName();
//		mailService.sendSimpleEmailTwiceUsers(request, "Новый график поставок на РЦ", text, properties.getProperty("email.orderSupport.1"), properties.getProperty("email.orderSupport.2"));
		List<String> emails = propertiesUtils.getValuesByPartialKey(request.getServletContext(), "email.orderSupport");
		mailService.sendEmailToUsers(request, "Новый график поставок на РЦ", text, emails);
		
		response.put("status", "200");
		response.put("message", "График поставок  "+schedule.getName()+" создан");
		response.put("idSchedule", id.toString());
		return response;		
	}
	
	/**
	 * Создание графика поставок TO
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@PostMapping("/slots/delivery-schedule/createTO")
	public Map<String, Object> postCreateDeliveryScheduleTO(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<String, Object>();
		boolean isNeedEMail = false;
		
		if(str == null) {
			response.put("status", "100");
			response.put("message", "Тело запроса = null");
			return response;
		}
		
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		
		if(jsonMainObject.get("counterpartyContractCode") == null) {
			response.put("status", "100");
			response.put("message", "Отсутствует номер контаркта");
			return response;
		}
		
		if(scheduleService.getScheduleByNumContract(Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString())) == null) {
			isNeedEMail = true;
		}
		
		JSONArray shopsArray = (JSONArray) parser.parse(jsonMainObject.get("numStock").toString());	
		
		Map<Integer, Shop> shopMap =  shopService.getShopMap();
		Map<Integer, Shop> targetShopMap = new HashMap<Integer, Shop>();
		for (Object object : shopsArray) {
			Integer numShop = Integer.parseInt(object.toString());
			Shop shop =  shopMap.get(numShop);
			if(shop!=null) {
				targetShopMap.put(numShop, shop);
				
			}else {
				response.put("status", "100");
				response.put("message", "В базе данных остуствует магазин " + numShop + ". Обратитесь в отдел транспортной логистики");
				return response;
			}
		}
		
		Schedule schedule = new Schedule();		
		schedule.setCounterpartyCode(jsonMainObject.get("counterpartyCode") == null || jsonMainObject.get("counterpartyCode").toString().isEmpty() ? null : Long.parseLong(jsonMainObject.get("counterpartyCode").toString()));
		schedule.setCounterpartyContractCode(jsonMainObject.get("counterpartyContractCode") == null || jsonMainObject.get("counterpartyContractCode").toString().isEmpty() ? null : Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()));
		schedule.setSupplies(jsonMainObject.get("supplies") == null || jsonMainObject.get("supplies").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("supplies").toString()));
//		schedule.setNumStock(jsonMainObject.get("numStock") == null || jsonMainObject.get("numStock").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("numStock").toString()));
		schedule.setRunoffCalculation(jsonMainObject.get("runoffCalculation") == null || jsonMainObject.get("runoffCalculation").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("runoffCalculation").toString()));
		schedule.setName(jsonMainObject.get("name") == null || jsonMainObject.get("name").toString().isEmpty() ? null : jsonMainObject.get("name").toString());
		schedule.setNote(jsonMainObject.get("note") == null || jsonMainObject.get("note").toString().isEmpty() ? null : jsonMainObject.get("note").toString());
		schedule.setMonday(jsonMainObject.get("monday") == null || jsonMainObject.get("monday").toString().isEmpty() ? null : jsonMainObject.get("monday").toString());
		schedule.setTuesday(jsonMainObject.get("tuesday") == null || jsonMainObject.get("tuesday").toString().isEmpty() ? null : jsonMainObject.get("tuesday").toString());
		schedule.setWednesday(jsonMainObject.get("wednesday") == null || jsonMainObject.get("wednesday").toString().isEmpty() ? null : jsonMainObject.get("wednesday").toString());
		schedule.setThursday(jsonMainObject.get("thursday") == null || jsonMainObject.get("thursday").toString().isEmpty() ? null : jsonMainObject.get("thursday").toString());
		schedule.setFriday(jsonMainObject.get("friday") == null || jsonMainObject.get("friday").toString().isEmpty() ? null : jsonMainObject.get("friday").toString());
		schedule.setSaturday(jsonMainObject.get("saturday") == null || jsonMainObject.get("saturday").toString().isEmpty() ? null : jsonMainObject.get("saturday").toString());
		schedule.setSunday(jsonMainObject.get("sunday") == null || jsonMainObject.get("sunday").toString().isEmpty() ? null : jsonMainObject.get("sunday").toString());
		schedule.setTz(jsonMainObject.get("tz") == null || jsonMainObject.get("tz").toString().isEmpty() ? null : jsonMainObject.get("tz").toString());
		schedule.setTp(jsonMainObject.get("tp") == null || jsonMainObject.get("tp").toString().isEmpty() ? null : jsonMainObject.get("tp").toString());
		schedule.setComment(jsonMainObject.get("comment") == null || jsonMainObject.get("comment").toString().isEmpty() ? null : jsonMainObject.get("comment").toString());
		schedule.setDescription(jsonMainObject.get("description") == null || jsonMainObject.get("description").toString().isEmpty() ? null : jsonMainObject.get("description").toString());
		schedule.setMultipleOfPallet(jsonMainObject.get("multipleOfPallet") == null || jsonMainObject.get("multipleOfPallet").toString().isEmpty() ? null : jsonMainObject.get("multipleOfPallet").toString().equals("true") ? true : false);
		schedule.setMultipleOfTruck(jsonMainObject.get("multipleOfTruck") == null || jsonMainObject.get("multipleOfTruck").toString().isEmpty() ? null : jsonMainObject.get("multipleOfTruck").toString().equals("true") ? true : false);
		schedule.setMachineMultiplicity(jsonMainObject.get("machineMultiplicity") == null || jsonMainObject.get("machineMultiplicity").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("machineMultiplicity").toString()));
		schedule.setConnectionSupply(jsonMainObject.get("connectionSupply") == null || jsonMainObject.get("connectionSupply").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("connectionSupply").toString()));
		schedule.setIsNotCalc(false);
		schedule.setIsImport(false);
//		schedule.setStatus(10);
		schedule.setStatus(jsonMainObject.get("status") == null || jsonMainObject.get("status").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("status").toString()));
		
		schedule.setIsDayToDay("true".equals(jsonMainObject.get("isDayToDay").toString()));
		
		
		String type = jsonMainObject.get("type") == null || jsonMainObject.get("type").toString().isEmpty() ? null : jsonMainObject.get("type").toString();
		if(type !=null) {
			if(!type.equals("РЦ") && !type.equals("ТО")) {
				response.put("status", "100");
				response.put("message", "Ошибка в поле type: ожидается ТО или РЦ");
				response.put("info", "Ошибка в поле type: ожидается ТО или РЦ");
				return response;
			}
		}
		schedule.setType(type);
		
		String toType = jsonMainObject.get("toType") == null || jsonMainObject.get("toType").toString().isEmpty() ? null : jsonMainObject.get("toType").toString();
		String textStart = null;
		if(toType !=null) {
			if(!toType.equals("холодный") && !toType.equals("сухой")) {
				response.put("status", "100");
				response.put("message", "Ошибка в поле toType: ожидается холодный или сухой");
				response.put("info", "Ошибка в поле toType: ожидается холодный или сухой");
				return response;
			}
			if(toType.equals("холодный")) {
				textStart = "Ожидает подтверждения ";
			} else {
				textStart = "Создан ";
			}
		}

		schedule.setToType(toType);
		schedule.setOrderFormationSchedule(jsonMainObject.get("orderFormationSchedule") == null || jsonMainObject.get("orderFormationSchedule").toString().isEmpty() ? null : jsonMainObject.get("orderFormationSchedule").toString());
		schedule.setOrderShipmentSchedule(jsonMainObject.get("orderShipmentSchedule") == null || jsonMainObject.get("orderShipmentSchedule").toString().isEmpty() ? null : jsonMainObject.get("orderShipmentSchedule").toString());
		schedule.setCodeNameOfQuantumCounterparty(jsonMainObject.get("codeNameOfQuantumCounterparty") == null || jsonMainObject.get("codeNameOfQuantumCounterparty").toString().isEmpty() ? null : jsonMainObject.get("codeNameOfQuantumCounterparty").toString());
		schedule.setQuantumMeasurements(jsonMainObject.get("quantumMeasurements") == null || jsonMainObject.get("quantumMeasurements").toString().isEmpty() ? null : jsonMainObject.get("quantumMeasurements").toString());
		schedule.setQuantum(jsonMainObject.get("quantum") == null || jsonMainObject.get("quantum").toString().isEmpty() ? null : Double.parseDouble(jsonMainObject.get("quantum").toString()));
		
		User user = getThisUser();
		String history = user.getSurname() + " " + user.getName() + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ";create\n"; 
		
		schedule.setHistory((schedule.getHistory() != null ? schedule.getHistory() : "") + history);

		//boolean isScheduleTemp = jsonMainObject.get("isTempSchedule") == null ? false : Boolean.parseBoolean(jsonMainObject.get("isTempSchedule").toString()); //изменение

		//начало изменений

		boolean isScheduleTemp = jsonMainObject.get("isTempSchedule") == null ? false : Boolean.parseBoolean(jsonMainObject.get("isTempSchedule").toString()); //изменение

		for (Map.Entry<Integer, Shop> object : targetShopMap.entrySet()) {
			Shop shop = object.getValue();
			schedule.setNameStock(shop.getAddress());
			schedule.setNumStock(shop.getNumshop());

			List<Schedule> existingSchedules = scheduleService.getScheduleByNumContractAndNUmStockWithTemp(Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()), shop.getNumshop());
			long constSchedulesCount = existingSchedules.stream().filter(s -> s.getStartDateTemp() == null).count();
			long tempSchedulesCount = existingSchedules.stream().filter(s -> s.getStartDateTemp() != null).count();
			if (isScheduleTemp) {
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
				try {
					schedule.setStartDateTemp(jsonMainObject.get("startDateTemp") == null || jsonMainObject.get("startDateTemp").toString().isEmpty() ? null : new Date(dateFormatter.parse(jsonMainObject.get("startDateTemp").toString()).getTime()));
					schedule.setEndDateTemp(jsonMainObject.get("endDateTemp") == null || jsonMainObject.get("endDateTemp").toString().isEmpty() ? null : new Date(dateFormatter.parse(jsonMainObject.get("endDateTemp").toString()).getTime()));
				} catch (java.text.ParseException e) {
					throw new RuntimeException(e);
				}
				if (constSchedulesCount == 0) {
					response.put("status", "100");
					response.put("message", "Для магазина " + schedule.getNumStock() + " не существует постоянного графика");
					return response;

				} else if (tempSchedulesCount != 0) {
					for (Schedule sch : existingSchedules) {
						if (sch.getEndDateTemp() != null) {
							schedule.setIdSchedule(sch.getIdSchedule());
							scheduleService.updateSchedule(schedule);
						}
					}
				} else {
					schedule.setIdSchedule(null);
					scheduleService.saveSchedule(schedule);
				}
			} else {
				if (constSchedulesCount != 0) {
					response.put("status", "100");
					response.put("message", "Данный контракт уже имеется в базе данных");
					return response;
				} else {
					schedule.setIdSchedule(null);
					scheduleService.saveSchedule(schedule);
				}

//			Schedule scheduleOld = scheduleService.getScheduleByNumContractAndNUmStock(Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()), shop.getNumshop());
//			if(scheduleOld != null) {
//				response.put("status", "100");
//				response.put("message", "Данный контракт уже имеется в базе данных");
//				response.put("body", scheduleOld);
//				return response;
//			}

				//конец изменений


//			schedule.setIdSchedule(null);
//			schedule.setNameStock(shop.getAddress());
//			schedule.setNumStock(shop.getNumshop());
//			scheduleService.saveSchedule(schedule);
			}
		}

//		Integer id = scheduleService.saveSchedule(schedule);
		//тут отправляем на почту сообщение
		if(isNeedEMail) {
			String appPath = request.getServletContext().getRealPath("");
			FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/email.properties");
			properties = new Properties();
			properties.load(fileInputStream);
			String text = textStart + "новый график поставок на ТО сотрудником: " + user.getSurname() + " " + user.getName() + " \nПоставщик: " + schedule.getName() + "; График номер: " + jsonMainObject.get("counterpartyContractCode").toString();
			List<String> emails = propertiesUtils.getValuesByPartialKey(request.getServletContext(), "email.orl.head");
			List<String> emailsTest = propertiesUtils.getValuesByPartialKey(request.getServletContext(), "email.test");

			mailService.sendEmailToUsers(request, "Новый график поставок на ТО", text, emailsTest);
//			mailService.sendSimpleEmailTwiceUsers(request, "Новый график поставок", text, properties.getProperty("email.orderSupport.1"), properties.getProperty("email.orderSupport.2"));	--
		}
		
		response.put("status", "200");
		response.put("message", "Графики поставок созданы");
//		response.put("idSchedule", id.toString());
		return response;		
	}
	
	/**
	 * метод редактирует все графики поставок по коду контракта.
	 * Редактирует только те поля, которые пришли.
	 * Те полая, которые не пришли - не редактирует
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@PostMapping("/slots/delivery-schedule/editTOByCounterpartyContractCodeOnly")
	public Map<String, Object> postEditDeliveryScheduleTOContractCodeOnly(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<String, Object>();
		if(str == null) {
			response.put("status", "100");
			response.put("message", "Тело запроса = null");
			return response;
		}
		
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		
		if(jsonMainObject.get("counterpartyContractCode") == null) {
			response.put("status", "100");
			response.put("message", "Отсутствует номер контаркта");
			return response;
		}
	
		List<Schedule> schedules = scheduleService.getSchedulesListTOContract(jsonMainObject.get("counterpartyContractCode").toString());
		for (Schedule schedule : schedules) {
			scheduleService.updateSchedule(editScheduleByRequest2(schedule, jsonMainObject));
		}
		response.put("status", "200");
		response.put("message", "Графики поставок отредактированы");
		return response;		
	}
	
	/**
	 * метод редактирует все графики поставок по коду контракта И по магазинам
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@PostMapping("/slots/delivery-schedule/editTOByCounterpartyAndShop")
	public Map<String, Object> postEditDeliveryScheduleTO(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<String, Object>();
		if(str == null) {
			response.put("status", "100");
			response.put("message", "Тело запроса = null");
			return response;
		}
		
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		
		if(jsonMainObject.get("counterpartyContractCode") == null) {
			response.put("status", "100");
			response.put("message", "Отсутствует номер контаркта");
			return response;
		}
		
		JSONArray shopsArray = (JSONArray) parser.parse(jsonMainObject.get("numStock").toString());	
		
		Map<Integer, Shop> shopMap =  shopService.getShopMap();
		Map<Integer, Shop> targetShopMap = new HashMap<Integer, Shop>();
		for (Object object : shopsArray) {
			Integer numShop = Integer.parseInt(object.toString());
			Shop shop =  shopMap.get(numShop);
			if(shop!=null) {
				targetShopMap.put(numShop, shop);
				
			}else {
				response.put("status", "100");
				response.put("message", "В базе данных остуствует магазин " + numShop + ". Обратитесь в отдел транспортной лгистики");
				return response;
			}
		}

		for (Entry<Integer, Shop> entry: targetShopMap.entrySet()) { //тут
			List<Schedule> schedules = scheduleService.getScheduleByNumContractAndNUmStockWithTemp(Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()), entry.getKey());
			if(schedules.isEmpty()) {
				response.put("status", "100");
				response.put("message", "Для магазина " + entry.getValue().getNumshop() + " отсутствует график поставок с кодом контракта " + jsonMainObject.get("counterpartyContractCode").toString());
				return response;
			}
			for (Schedule sch: schedules) {
				if (sch.getStartDateTemp() == null) {
					sch.setDateLastChanging(Date.valueOf(LocalDate.now()));
					scheduleService.updateSchedule(editScheduleByRequestForRC(sch, jsonMainObject));
				}
			}
//			Schedule schedule = scheduleService.getScheduleByNumContractAndNumStock(Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()), entry.getKey());
//			if(schedule == null) {
//				response.put("status", "100");
//				response.put("message", "Для магазина " + entry.getValue().getNumshop() + " отсутствует график поставок с кодом контракта " + jsonMainObject.get("counterpartyContractCode").toString());
//				return response;
//			}
//			schedule.setDateLastChanging(Date.valueOf(LocalDate.now()));
//			scheduleService.updateSchedule(editScheduleByRequestForRC(schedule, jsonMainObject));
		}
		response.put("status", "200");
		response.put("message", "Графики поставок отредактированы");
		return response;		
	}
	
	
	
	/**
	 * Метод отвечает за редактирование графика поставок, если есть поле, то редактирует, если нет - не редактирует
	 * + за писывает историю о редактировании
	 * @return
	 */
	@Deprecated
	private Schedule editScheduleByRequest2 (Schedule schedule, JSONObject jsonMainObject) {
		if (jsonMainObject.get("counterpartyCode") != null && !jsonMainObject.get("counterpartyCode").toString().isEmpty()) {
		    schedule.setCounterpartyCode(Long.parseLong(jsonMainObject.get("counterpartyCode").toString()));
		}

		if (jsonMainObject.get("counterpartyContractCode") != null && !jsonMainObject.get("counterpartyContractCode").toString().isEmpty()) {
		    schedule.setCounterpartyContractCode(Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()));
		}

		if (jsonMainObject.get("supplies") != null && !jsonMainObject.get("supplies").toString().isEmpty()) {
		    schedule.setSupplies(Integer.parseInt(jsonMainObject.get("supplies").toString()));
		}

		if (jsonMainObject.get("runoffCalculation") != null && !jsonMainObject.get("runoffCalculation").toString().isEmpty()) {
		    schedule.setRunoffCalculation(Integer.parseInt(jsonMainObject.get("runoffCalculation").toString()));
		}

		if (jsonMainObject.get("name") != null && !jsonMainObject.get("name").toString().isEmpty()) {
		    schedule.setName(jsonMainObject.get("name").toString());
		}

		if (jsonMainObject.get("note") != null && !jsonMainObject.get("note").toString().isEmpty()) {
		    schedule.setNote(jsonMainObject.get("note").toString());
		}

		if (jsonMainObject.get("monday") != null && !jsonMainObject.get("monday").toString().isEmpty()) {
		    schedule.setMonday(jsonMainObject.get("monday").toString());
		}

		if (jsonMainObject.get("tuesday") != null && !jsonMainObject.get("tuesday").toString().isEmpty()) {
		    schedule.setTuesday(jsonMainObject.get("tuesday").toString());
		}

		if (jsonMainObject.get("wednesday") != null && !jsonMainObject.get("wednesday").toString().isEmpty()) {
		    schedule.setWednesday(jsonMainObject.get("wednesday").toString());
		}

		if (jsonMainObject.get("thursday") != null && !jsonMainObject.get("thursday").toString().isEmpty()) {
		    schedule.setThursday(jsonMainObject.get("thursday").toString());
		}

		if (jsonMainObject.get("friday") != null && !jsonMainObject.get("friday").toString().isEmpty()) {
		    schedule.setFriday(jsonMainObject.get("friday").toString());
		}

		if (jsonMainObject.get("saturday") != null && !jsonMainObject.get("saturday").toString().isEmpty()) {
		    schedule.setSaturday(jsonMainObject.get("saturday").toString());
		}

		if (jsonMainObject.get("sunday") != null && !jsonMainObject.get("sunday").toString().isEmpty()) {
		    schedule.setSunday(jsonMainObject.get("sunday").toString());
		}

		if (jsonMainObject.get("tz") != null && !jsonMainObject.get("tz").toString().isEmpty()) {
		    schedule.setTz(jsonMainObject.get("tz").toString());
		}

		if (jsonMainObject.get("tp") != null && !jsonMainObject.get("tp").toString().isEmpty()) {
		    schedule.setTp(jsonMainObject.get("tp").toString());
		}

		if (jsonMainObject.get("comment") != null && !jsonMainObject.get("comment").toString().isEmpty()) {
		    schedule.setComment(jsonMainObject.get("comment").toString());
		}

		if (jsonMainObject.get("description") != null && !jsonMainObject.get("description").toString().isEmpty()) {
		    schedule.setDescription(jsonMainObject.get("description").toString());
		}

		if (jsonMainObject.get("multipleOfPallet") != null && !jsonMainObject.get("multipleOfPallet").toString().isEmpty()) {
		    schedule.setMultipleOfPallet("true".equals(jsonMainObject.get("multipleOfPallet").toString()));
		}

		if (jsonMainObject.get("multipleOfTruck") != null && !jsonMainObject.get("multipleOfTruck").toString().isEmpty()) {
		    schedule.setMultipleOfTruck("true".equals(jsonMainObject.get("multipleOfTruck").toString()));
		}

		if (jsonMainObject.get("machineMultiplicity") != null && !jsonMainObject.get("machineMultiplicity").toString().isEmpty()) {
		    schedule.setMachineMultiplicity(Integer.parseInt(jsonMainObject.get("machineMultiplicity").toString()));
		}

		if (jsonMainObject.get("connectionSupply") != null && !jsonMainObject.get("connectionSupply").toString().isEmpty()) {
		    schedule.setConnectionSupply(Integer.parseInt(jsonMainObject.get("connectionSupply").toString()));
		}

		if (jsonMainObject.get("status") != null && !jsonMainObject.get("status").toString().isEmpty()) {
		    schedule.setStatus(Integer.parseInt(jsonMainObject.get("status").toString()));
		}

		if (jsonMainObject.get("orderFormationSchedule") != null && !jsonMainObject.get("orderFormationSchedule").toString().isEmpty()) {
		    schedule.setOrderFormationSchedule(jsonMainObject.get("orderFormationSchedule").toString());
		}

		if (jsonMainObject.get("orderShipmentSchedule") != null && !jsonMainObject.get("orderShipmentSchedule").toString().isEmpty()) {
		    schedule.setOrderShipmentSchedule(jsonMainObject.get("orderShipmentSchedule").toString());
		}

		if (jsonMainObject.get("isNotCalc") != null && !jsonMainObject.get("isNotCalc").toString().isEmpty()) {
		    schedule.setIsNotCalc("true".equals(jsonMainObject.get("isNotCalc").toString()));
		}

		if (jsonMainObject.get("isDayToDay") != null && !jsonMainObject.get("isDayToDay").toString().isEmpty()) {
		    schedule.setIsDayToDay("true".equals(jsonMainObject.get("isDayToDay").toString()));
		}
		
		if (jsonMainObject.get("isImport") != null && !jsonMainObject.get("isImport").toString().isEmpty()) {
		    schedule.setIsNotCalc("true".equals(jsonMainObject.get("isImport").toString()));
		}
		
		schedule.setDateLastChanging(Date.valueOf(LocalDate.now()));

		
		User user = getThisUser();
		String history = user.getSurname() + " " + user.getName() + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ";edit\n"; 
		
		schedule.setHistory((schedule.getHistory() != null ? schedule.getHistory() : "") + history);
		return schedule;
	}
	
	
	private Schedule editScheduleByRequestForRC (Schedule schedule, JSONObject jsonMainObject) {
		schedule.setCounterpartyCode(jsonMainObject.get("counterpartyCode") == null || jsonMainObject.get("counterpartyCode").toString().isEmpty() ? null : Long.parseLong(jsonMainObject.get("counterpartyCode").toString()));
		schedule.setCounterpartyContractCode(jsonMainObject.get("counterpartyContractCode") == null || jsonMainObject.get("counterpartyContractCode").toString().isEmpty() ? null : Long.parseLong(jsonMainObject.get("counterpartyContractCode").toString()));
		schedule.setSupplies(jsonMainObject.get("supplies") == null || jsonMainObject.get("supplies").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("supplies").toString()));
		schedule.setRunoffCalculation(jsonMainObject.get("runoffCalculation") == null || jsonMainObject.get("runoffCalculation").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("runoffCalculation").toString()));
		schedule.setName(jsonMainObject.get("name") == null || jsonMainObject.get("name").toString().isEmpty() ? null : jsonMainObject.get("name").toString());
		schedule.setNote(jsonMainObject.get("note") == null || jsonMainObject.get("note").toString().isEmpty() ? null : jsonMainObject.get("note").toString());
		schedule.setMonday(jsonMainObject.get("monday") == null || jsonMainObject.get("monday").toString().isEmpty() ? null : jsonMainObject.get("monday").toString());
		schedule.setTuesday(jsonMainObject.get("tuesday") == null || jsonMainObject.get("tuesday").toString().isEmpty() ? null : jsonMainObject.get("tuesday").toString());
		schedule.setWednesday(jsonMainObject.get("wednesday") == null || jsonMainObject.get("wednesday").toString().isEmpty() ? null : jsonMainObject.get("wednesday").toString());
		schedule.setThursday(jsonMainObject.get("thursday") == null || jsonMainObject.get("thursday").toString().isEmpty() ? null : jsonMainObject.get("thursday").toString());
		schedule.setFriday(jsonMainObject.get("friday") == null || jsonMainObject.get("friday").toString().isEmpty() ? null : jsonMainObject.get("friday").toString());
		schedule.setSaturday(jsonMainObject.get("saturday") == null || jsonMainObject.get("saturday").toString().isEmpty() ? null : jsonMainObject.get("saturday").toString());
		schedule.setSunday(jsonMainObject.get("sunday") == null || jsonMainObject.get("sunday").toString().isEmpty() ? null : jsonMainObject.get("sunday").toString());
		schedule.setTz(jsonMainObject.get("tz") == null || jsonMainObject.get("tz").toString().isEmpty() ? null : jsonMainObject.get("tz").toString());
		schedule.setTp(jsonMainObject.get("tp") == null || jsonMainObject.get("tp").toString().isEmpty() ? null : jsonMainObject.get("tp").toString());
		schedule.setComment(jsonMainObject.get("comment") == null || jsonMainObject.get("comment").toString().isEmpty() ? null : jsonMainObject.get("comment").toString());
		schedule.setDescription(jsonMainObject.get("description") == null || jsonMainObject.get("description").toString().isEmpty() ? null : jsonMainObject.get("description").toString());
//		schedule.setMultipleOfPallet(jsonMainObject.get("multipleOfPallet") == null || jsonMainObject.get("multipleOfPallet").toString().isEmpty() ? null : jsonMainObject.get("multipleOfPallet").toString().equals("true") ? true : false);
//		schedule.setMultipleOfTruck(jsonMainObject.get("multipleOfTruck") == null || jsonMainObject.get("multipleOfTruck").toString().isEmpty() ? null : jsonMainObject.get("multipleOfTruck").toString().equals("true") ? true : false);
//		schedule.setMachineMultiplicity(jsonMainObject.get("machineMultiplicity") == null || jsonMainObject.get("machineMultiplicity").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("machineMultiplicity").toString()));
//		schedule.setConnectionSupply(jsonMainObject.get("connectionSupply") == null || jsonMainObject.get("connectionSupply").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("connectionSupply").toString()));
//		schedule.setStatus(jsonMainObject.get("status") == null || jsonMainObject.get("status").toString().isEmpty() ? null : Integer.parseInt(jsonMainObject.get("status").toString()));
		schedule.setOrderFormationSchedule(jsonMainObject.get("orderFormationSchedule") == null || jsonMainObject.get("orderFormationSchedule").toString().isEmpty() ? null : jsonMainObject.get("orderFormationSchedule").toString());
		schedule.setOrderShipmentSchedule(jsonMainObject.get("orderShipmentSchedule") == null || jsonMainObject.get("orderShipmentSchedule").toString().isEmpty() ? null : jsonMainObject.get("orderShipmentSchedule").toString());
//		schedule.setIsNotCalc(jsonMainObject.get("isNotCalc") == null || jsonMainObject.get("isNotCalc").toString().isEmpty() ? null : jsonMainObject.get("isNotCalc").toString().equals("true") ? true : false);
//		schedule.setIsDayToDay(jsonMainObject.get("isDayToDay") == null || jsonMainObject.get("isDayToDay").toString().isEmpty() ? null : jsonMainObject.get("isDayToDay").toString().equals("true") ? true : false);
//		schedule.setCodeNameOfQuantumCounterparty(jsonMainObject.get("codeNameOfQuantumCounterparty") == null || jsonMainObject.get("codeNameOfQuantumCounterparty").toString().isEmpty() ? null : jsonMainObject.get("codeNameOfQuantumCounterparty").toString());
		schedule.setQuantumMeasurements(jsonMainObject.get("quantumMeasurements") == null || jsonMainObject.get("quantumMeasurements").toString().isEmpty() ? null : jsonMainObject.get("quantumMeasurements").toString());
		schedule.setQuantum(jsonMainObject.get("quantum") == null || jsonMainObject.get("quantum").toString().isEmpty() ? null : Double.parseDouble(jsonMainObject.get("quantum").toString()));
		
		
		User user = getThisUser();
		String history = user.getSurname() + " " + user.getName() + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ";edit\n"; 
		
		schedule.setHistory((schedule.getHistory() != null ? schedule.getHistory() : "") + history);
		return schedule;
	}
	
	
	
	/**
	 * метод, который отдаёт все графики поставок на РЦ
	 * @param request
	 * @param code
	 * @param stock
	 * @return
	 */
	@GetMapping("/slots/delivery-schedule/getListRC")
	public Map<String, Object> getListDeliveryScheduleRC(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();		
		response.put("status", "200");
		response.put("body", scheduleService.getSchedulesListRC());
		return response;		
	}
	
	/**
	 * метод, который отдаёт все графики поставок на ТО
	 * @param request
	 * @param code
	 * @param stock
	 * @return
	 */
	@GetMapping("/slots/delivery-schedule/getListTO")
	@TimedExecution
	public Map<String, Object> getListDeliveryScheduleTO(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("status", "200");
		response.put("body", scheduleService.getSchedulesListTOAll());
		return response;		
	}
	
	/**
	 * метод, который отдаёт все графики поставок на ТО по коду контракта
	 * @param request
	 * @param code
	 * @param stock
	 * @return
	 */
	@GetMapping("/slots/delivery-schedule/getListTOContract/{contract}")
	public Map<String, Object> getListDeliveryScheduleTOContract(HttpServletRequest request, @PathVariable String contract) {
		Map<String, Object> response = new HashMap<String, Object>();		
		response.put("status", "200");
		response.put("body", scheduleService.getSchedulesListTOContract(contract));
		return response;		
	}
	
	/**
	 * метод, который отдаёт все графики поставок на ТО по названию контрагента
	 * @param request
	 * @param code
	 * @param stock
	 * @return
	 */
	@GetMapping("/slots/delivery-schedule/getListTOСounterparty/{name}")
	public Map<String, Object> getListDeliveryScheduleTOСounterparty(HttpServletRequest request, @PathVariable String name) {
		Map<String, Object> response = new HashMap<String, Object>();		
		response.put("status", "200");
		response.put("body", scheduleService.getSchedulesListTOСounterparty(name));
		return response;		
	}
	
	
	
	@GetMapping("/slots/delivery-schedule/changeIsNotCalc/{idSchedule}")
	public Map<String, Object> getChangeIsNotCalc(HttpServletRequest request, @PathVariable String idSchedule) {
		Map<String, Object> response = new HashMap<String, Object>();	
		Schedule schedule = scheduleService.getScheduleById(Integer.parseInt(idSchedule.trim()));
		
		if(schedule == null) {
			response.put("status", "100");
			response.put("info", "Не найден график поставок с id " + idSchedule);
			response.put("message", "Не найден график поставок с id " + idSchedule);
			return response;
		}
		User user = getThisUser();
		schedule.setIsNotCalc(!schedule.getIsNotCalc());
		String history = user.getSurname() + " " + user.getName() + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ";changeIsNotClalc="+schedule.getIsNotCalc()+"\n"; 
		
		schedule.setHistory((schedule.getHistory() != null ? schedule.getHistory() : "") + history);
		schedule.setDateLastChanging(Date.valueOf(LocalDate.now()));
		
		scheduleService.updateSchedule(schedule);
		
		response.put("status", "200");
		response.put("body", schedule);
		response.put("info", "Статус расчёта графика поставок "+schedule.getName()+" изменен");
		response.put("message", "Статус расчёта графика поставок "+schedule.getName()+" изменен");
		return response;		
	}
	
	@GetMapping("/slots/delivery-schedule/changeIsImport/{idSchedule}")
	public Map<String, Object> getChangeIsImport(HttpServletRequest request, @PathVariable String idSchedule) {
		Map<String, Object> response = new HashMap<String, Object>();	
		Schedule schedule = scheduleService.getScheduleById(Integer.parseInt(idSchedule.trim()));
		
		if(schedule == null) {
			response.put("status", "100");
			response.put("info", "Не найден график поставок с id " + idSchedule);
			response.put("message", "Не найден график поставок с id " + idSchedule);
			return response;
		}
		User user = getThisUser();
		schedule.setIsImport(!schedule.getIsImport());
		String history = user.getSurname() + " " + user.getName() + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ";changeIsImport="+schedule.getIsNotCalc()+"\n"; 
		
		schedule.setHistory((schedule.getHistory() != null ? schedule.getHistory() : "") + history);
		schedule.setDateLastChanging(Date.valueOf(LocalDate.now()));
		
		scheduleService.updateSchedule(schedule);
		
		response.put("status", "200");
		response.put("body", schedule);
		response.put("info", "Статус импорта "+schedule.getName()+" изменен");
		response.put("message", "Статус импорта "+schedule.getName()+" изменен");
		return response;		
	}
	
	
	@GetMapping("/slots/delivery-schedule/changeDayToDay/{idSchedule}")
	public Map<String, Object> getChangeDayToDay(HttpServletRequest request, @PathVariable String idSchedule) {
		Map<String, Object> response = new HashMap<String, Object>();	
		Schedule schedule = scheduleService.getScheduleById(Integer.parseInt(idSchedule.trim()));
		
		if(schedule == null) {
			response.put("status", "100");
			response.put("info", "Не найден график поставок с id " + idSchedule);
			response.put("message", "Не найден график поставок с id " + idSchedule);
			return response;
		}
		User user = getThisUser();
		schedule.setIsDayToDay(!schedule.getIsDayToDay());
		String history = user.getSurname() + " " + user.getName() + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ";changeDayToDay="+schedule.getIsNotCalc()+"\n"; 
		
		schedule.setHistory((schedule.getHistory() != null ? schedule.getHistory() : "") + history);
		schedule.setDateLastChanging(Date.valueOf(LocalDate.now()));
		
		scheduleService.updateSchedule(schedule);
		
		response.put("status", "200");
		response.put("body", schedule);
		response.put("info", "Расчёт день в день "+schedule.getName()+" изменен");
		response.put("message", "Расчёт день в день "+schedule.getName()+" изменен");
		return response;		
	}
	
	/**
	 * Новый метод отправки стоимости рейсов!
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
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
		message.setYnp(jsonMainObject.get("ynp") == null ? null : jsonMainObject.get("ynp").toString());
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
		Product product = productService.getProductByCodeAndStock(Integer.parseInt(code.trim()), Integer.parseInt(stock));
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
	@GetMapping("/order-support/changeException/{idProduct}&{stock}")
	public Map<String, Object> changeException(HttpServletRequest request, @PathVariable String idProduct, @PathVariable String stock) {
		Map<String, Object> response = new HashMap<String, Object>();
		Product product = productService.getProductByCode(Integer.parseInt(idProduct.trim()));
		product.setIsException(!product.getIsException());
		productService.updateProduct(product);
		response.put("status", "200");
		response.put("message", "Данные по товaру обновлены.");
		return response;		
	}
	
	@PostMapping("/order-support/blockProduct")
	public Map<String, Object> blockProduct(HttpServletRequest request, @RequestBody String str) throws ParseException {		
		Map<String, Object> response = new HashMap<String, Object>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		
		Integer idProduct = jsonMainObject.get("idProduct") != null && !jsonMainObject.get("idProduct").toString().isEmpty() ? Integer.parseInt(jsonMainObject.get("idProduct").toString().trim()) : null;
		Date dateStart = jsonMainObject.get("dateStart") != null && !jsonMainObject.get("dateStart").toString().isEmpty() ? Date.valueOf(jsonMainObject.get("dateStart").toString()) : null;
		Date dateFinish = jsonMainObject.get("dateFinish") != null && !jsonMainObject.get("dateFinish").toString().isEmpty() ? Date.valueOf(jsonMainObject.get("dateFinish").toString()) : null;
		
		Product product = productService.getProductByCode(idProduct);
		product.setBlockDateStart(dateStart);
		product.setBlockDateFinish(dateFinish);
		productService.updateProduct(product);
		response.put("status", "200");
		response.put("message", "Время блокировки товара для слотово обновлено");
		response.put("object", product);
		return response;		
	}
	
	/**
	 * Редактор максимального кол-ва дней для Product 
	 * @param request
	 * @param param
	 * @return
	 */
	@GetMapping("/order-support/setMaxDay/{code}&{stock}&{day}")
	public Map<String, Object> setMaxDay(HttpServletRequest request, @PathVariable String code, @PathVariable String day, @PathVariable String stock) {
		Map<String, Object> response = new HashMap<String, Object>();
		Product product = productService.getProductByCodeAndStock(Integer.parseInt(code.trim()), Integer.parseInt(stock));
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
	public Set<Product> getStockRemainderSupport(HttpServletRequest request) {
		Set<Product> targetRoutes = productService.getAllProductList().stream().collect(Collectors.toSet());
		return targetRoutes;
	}
	
	@GetMapping("/carrier/test")
	public String test(HttpServletRequest request) {
		return MainChat.messegeList.size() + "";		
	}

	
	@GetMapping("/market/jwt/help")
	public Map<String, Object>  jwtHelp(){
		Map<String, Object> result = new HashMap<>();
		
		
		result.put("status", "200");
		result.put("/market/jwt/now", "Возвращает текущий JWT");
		return result;		
	}
	
	@GetMapping("/market/jwt/now")
	public Map<String, Object>  testJWTNow(){
		Map<String, Object> result = new HashMap<>();
		result.put("status", "200");
		result.put("responce", marketJWT);
		return result;		
	}
	
	@GetMapping("/market/clearjwt/{param}")
	public Map<String, Object> getJWTnull(HttpServletRequest request, @PathVariable String param) {
		Map<String, Object> response = new HashMap<String, Object>();
		MarketDataForClear dataDto = new MarketDataForClear(Integer.parseInt(param));
		MarketPacketDto packetDto = new MarketPacketDto(marketJWT, "CleanToken", serviceNumber, dataDto);
		MarketRequestDto requestDto = new MarketRequestDto("", packetDto);
		String str;
		try {
			str = postRequest(marketUrl, gson.toJson(requestDto));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "100");
			response.put("exception", e.toString());
			response.put("message", "Ошибка запроса к Маркету");
			response.put("info", "Ошибка запроса к Маркету");
			return response;
		}
		response.put("status", "200");
		response.put("message", str);
		return response;		
	}
	
	@GetMapping("/market/jwt/null")
	public Map<String, Object> getJWTNull(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		marketJWT = null;		
		response.put("status", "200");
		response.put("jwt", marketJWT);
		response.put("message", "JWT равен null");
		return response;		
	}
	
	@GetMapping("/market/jwt/get")
	public Map<String, Object> getJWT(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		MarketDataForLoginDto dataDto = new MarketDataForLoginDto(loginMarket, passwordMarket, "101");
//		MarketDataForLoginDtoTEST dataDto = new MarketDataForLoginDtoTEST("SpeedLogist", "12345678", 101);
		MarketPacketDto packetDto = new MarketPacketDto("null", "GetJWT", serviceNumber, dataDto);
		MarketRequestDto requestDto = new MarketRequestDto("", packetDto);
		//запрашиваем jwt
		String str;
		System.err.println(gson.toJson(requestDto));
		try {
			str = postRequest(marketUrl, gson.toJson(requestDto));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "100");
			response.put("exception", e.toString());
			response.put("message", "Ошибка запроса к Маркету");
			response.put("info", "Ошибка запроса к Маркету");
			return response;
		}
		System.out.println(str);
		try {
			MarketTableDto marketRequestDto = gson.fromJson(str, MarketTableDto.class);
			marketJWT = marketRequestDto.getTable()[0].toString().split("=")[1].split("}")[0];	
		} catch (Exception e) {
			response.put("status SpeedLogist", "500");
//			response.put("jwt", marketJWT);
			response.put("json", str);
			System.out.println(str);
			return response;
		}
			
		response.put("status", "200");
		response.put("jwt", marketJWT);
		response.put("message", "JWT обновлён");
		return response;		
	}
	
//	/**
//	     СТАРЫЙ МЕТОД 100% РАБОЧИЙ НОВЫЙ НИЖЕ
//			Новый метод проверка просиходит через try-catch (!!!)
//	 * Главный метод запроса ордера из маркета.
//	 * Если в маркете есть - он обнавляет его в бд.
//	 * Если связи с маркетом нет - берет из бд.
//	 * Если нет в бд и связи с маркетом нет - выдаёт ошибку
//	 * ордер из маркета 
//	 * @param request
//	 * @param idMarket
//	 * @return
//	 */
//	@GetMapping("/manager/getMarketOrder/{idMarket}")
//	public Map<String, Object> getMarketOrder(HttpServletRequest request, @PathVariable String idMarket) {		
//		try {			
//			checkJWT(marketUrl);			
//		} catch (Exception e) {
//			System.err.println("Ошибка получения jwt токена");
//		}
//		
//		Map<String, Object> response = new HashMap<String, Object>();
//		MarketDataForRequestDto dataDto3 = new MarketDataForRequestDto(idMarket);
//		MarketPacketDto packetDto3 = new MarketPacketDto(marketJWT, "SpeedLogist.GetOrderBuyInfo", serviceNumber, dataDto3);
//		MarketRequestDto requestDto3 = new MarketRequestDto("", packetDto3);
//		String marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto3));
//		
////		System.out.println(marketOrder2);
//		
//		if(marketOrder2.equals("503")) { // означает что связь с маркетом потеряна
//			//в этом случае проверяем бд
//			System.err.println("Связь с маркетом потеряна");
//			Order order = orderService.getOrderByMarketNumber(idMarket);
//			marketJWT = null; // сразу говорим что jwt устарел
//			if(order != null) {
//				response.put("status", "200");
//				response.put("message", "Заказ загружен из локальной базы данных SL. Связь с маркетом отсутствует");
//				response.put("info", "Заказ загружен из локальной базы данных SL. Связь с маркетом отсутствует");
//				response.put("order", order);
//				return response;
//			}else {
//				response.put("status", "100");
//				response.put("message", "Заказ с номером " + idMarket + " в базе данных SL не найден. Связь с Маркетом отсутствует. Обратитесь в отдел ОСиУЗ");
//				response.put("info", "Заказ с номером " + idMarket + " в базе данных SL не найден. Связь с Маркетом отсутствует. Обратитесь в отдел ОСиУЗ");
//				return response;
//			}
//			
//		}else{//если есть связь с маркетом
//			//проверяем на наличие сообщений об ошибке со стороны маркета
//			if(marketOrder2.contains("Error")) {
//				//тут избавляемся от мусора в json
////				System.err.println(marketOrder2);
////				String str2 = marketOrder2.split("\\[", 2)[1];
////				String str3 = str2.substring(0, str2.length()-2);
//				MarketErrorDto errorMarket = gson.fromJson(marketOrder2, MarketErrorDto.class);
////				System.out.println("JSON -> "+str3);
////				System.out.println(errorMarket);
//				if(errorMarket.getError().equals("99")) {//обработка случая, когда в маркете номера нет, а в бд есть.
//					Order orderFromDB = orderService.getOrderByMarketNumber(idMarket);
//					if(orderFromDB !=null) {
//						response.put("status", "100");
//						response.put("message", "Заказ " + idMarket + " не найден в маркете. Данные из SL устаревшие. Обновите данные в Маркете");
//						response.put("info", "Заказ " + idMarket + " не найден в маркете. Данные из SL устаревшие. Обновите данные в Маркете");
//						return response;
//					}else {
//						response.put("status", "100");
//						response.put("message", errorMarket.getErrorDescription());
//						response.put("info", errorMarket.getErrorDescription());
//						response.put("objectFromMarket", errorMarket);
//						return response;
//					}
//				}
//				response.put("status", "100");
//				response.put("message", errorMarket.getErrorDescription());
//				response.put("info", errorMarket.getErrorDescription());
//				response.put("objectFromMarket", errorMarket);
//				return response;
//			}
//			
//			//тут избавляемся от мусора в json
//			String str2 = marketOrder2.split("\\[", 2)[1];
//			String str3 = str2.substring(0, str2.length()-2);
//			
//			//создаём свой парсер и парсим json в объекты, с которыми будем работать.
//			CustomJSONParser customJSONParser = new CustomJSONParser();
//			
//			//создаём OrderBuyGroup
//			OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(str3);
//						
//			//создаём Order, записываем в бд и возвращаем или сам ордер или ошибку (тот же ордер, только с отрицательным id)
//			Order order = orderCreater.create(orderBuyGroupDTO);		
//			
//			if(order.getIdOrder() < 0) {
//				response.put("status", "100");
//				response.put("message", order.getMessage());
//				response.put("info", order.getMessage());
//				return response;
//			}else {
//				response.put("status", "200");
//				response.put("message", order.getMessage());
//				response.put("info", order.getMessage());
//				response.put("order", order);
//				return response;
//			}
//		}	
//	}
	
	/**
	 * Главный метод запроса ордера из маркета.
	 * Если в маркете есть - он обнавляет его в бд.
	 * Если связи с маркетом нет - берет из бд.
	 * Если нет в бд и связи с маркетом нет - выдаёт ошибку
	 * ордер из маркета 
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
		String marketOrder2;
		try {
			marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto3));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "100");
			response.put("exception", e.toString());
			response.put("message", "Ошибка запроса к Маркету");
			response.put("info", "Ошибка запроса к Маркету");
			List<String> emailsAdmins = propertiesUtils.getValuesByPartialKey(servletContext, "email.admin");
			mailService.sendEmailToUsers(servletContext, "Ошибка getMarketOrder!", e.toString(), emailsAdmins);
			return response;
		}
		
		//проверяем на наличие сообщений об ошибке со стороны маркета
		if(marketOrder2.contains("Error")) {
			//тут избавляемся от мусора в json
//			System.err.println(marketOrder2);
//			String str2 = marketOrder2.split("\\[", 2)[1];
//			String str3 = str2.substring(0, str2.length()-2);
			MarketErrorDto errorMarket = gson.fromJson(marketOrder2, MarketErrorDto.class);
//			System.out.println("JSON -> "+str3);
//			System.out.println(errorMarket);
			if(errorMarket.getError().equals("99")) {//обработка случая, когда в маркете номера нет, а в бд есть.
				Order orderFromDB = orderService.getOrderByMarketNumber(idMarket);
				if(orderFromDB !=null) {
					response.put("status", "100");
					response.put("message", "Заказ " + idMarket + " не найден в маркете. Данные из SL устаревшие. Обновите данные в Маркете");
					response.put("info", "Заказ " + idMarket + " не найден в маркете. Данные из SL устаревшие. Обновите данные в Маркете");
					return response;
				}else {
					response.put("status", "100");
					response.put("message", errorMarket.getErrorDescription());
					response.put("info", errorMarket.getErrorDescription());
					response.put("objectFromMarket", errorMarket);
					return response;
				}
			}
			response.put("status", "100");
			response.put("message", errorMarket.getErrorDescription());
			response.put("info", errorMarket.getErrorDescription());
			response.put("objectFromMarket", errorMarket);
			return response;
		}
		
		//тут избавляемся от мусора в json
		String str2 = marketOrder2.split("\\[", 2)[1];
		String str3 = str2.substring(0, str2.length()-2);
		
		//создаём свой парсер и парсим json в объекты, с которыми будем работать.
		CustomJSONParser customJSONParser = new CustomJSONParser();
		
		//создаём OrderBuyGroup
		OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(str3);
		
		//создаём Order, записываем в бд и возвращаем или сам ордер или ошибку (тот же ордер, только с отрицательным id)
		Order order = orderCreater.create(orderBuyGroupDTO);	
		
		if(order.getIdOrder() < 0) {
			response.put("status", "100");
			response.put("message", order.getMessage());
			response.put("info", order.getMessage());
			return response;
		}else {
			//тут я удаляю все согласования, т.к. заказ обновлён
			permissionService.deletePermissionByIdObject(order.getIdOrder());
			response.put("status", "200");
			response.put("message", order.getMessage());
			response.put("info", order.getMessage());
			response.put("order", order);
			return response;
		}
	}
	
	/**
	 * ВОзвращает Order по группе номеров из маркета.
	 * по сути тестовый метод
	 * @param request
	 * @param idMarket Принимает строку 123,848,451
	 * @return
	 * @throws ParseException
	 */
	@GetMapping("/manager/getMarketOrdersHasMap/{idMarket}")
	public Map<String, Object> getMarketOrders(HttpServletRequest request, @PathVariable String idMarket) throws ParseException {		
		try {			
			checkJWT(marketUrl);			
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		
		Map<String, Object> response = new HashMap<String, Object>();
		Object[] goodsId = idMarket.split(",");
		MarketDataArrayForRequestDto dataDto3 = new MarketDataArrayForRequestDto(goodsId);
		MarketPacketDto packetDto3 = new MarketPacketDto(marketJWT, "SpeedLogist.OrderBuyArrayInfoGet", serviceNumber, dataDto3);
		MarketRequestDto requestDto3 = new MarketRequestDto("", packetDto3);
		String marketOrder2;
		try {
			marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto3));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "100");
			response.put("exception", e.toString());
			response.put("message", "Ошибка запроса к Маркету");
			response.put("info", "Ошибка запроса к Маркету");
			return response;
		}
		
		System.out.println("request -> " + gson.toJson(requestDto3));
		//проверяем на наличие сообщений об ошибке со стороны маркета
		if(marketOrder2.contains("Error")) {
			//тут избавляемся от мусора в json
			System.out.println(marketOrder2);
//			String str2 = marketOrder2.split("\\[", 2)[1];
//			String str3 = str2.substring(0, str2.length()-2);
			MarketErrorDto errorMarket = gson.fromJson(marketOrder2, MarketErrorDto.class);
//			System.out.println("JSON -> "+str3);
//			System.out.println(errorMarket);
			if(errorMarket.getError().equals("99")) {//обработка случая, когда в маркете номера нет, а в бд есть.
				Order orderFromDB = orderService.getOrderByMarketNumber(idMarket);
				if(orderFromDB !=null) {
					response.put("status", "100");
					response.put("message", "Заказ " + idMarket + " не найден в маркете. Данные из SL устаревшие. Обновите данные в Маркете");
					response.put("info", "Заказ " + idMarket + " не найден в маркете. Данные из SL устаревшие. Обновите данные в Маркете");
					return response;
				}else {
					response.put("status", "100");
					response.put("message", errorMarket.getErrorDescription());
					response.put("info", errorMarket.getErrorDescription());
					response.put("objectFromMarket", errorMarket);
					return response;
				}
			}
			response.put("status", "100");
			response.put("message", errorMarket.getErrorDescription());
			response.put("info", errorMarket.getErrorDescription());
			response.put("objectFromMarket", errorMarket);
			return response;
		}
		
		System.out.println(marketOrder2);
		
		//создаём свой парсер и парсим json в объекты, с которыми будем работать.
		CustomJSONParser customJSONParser = new CustomJSONParser();
		
		//создаём лист OrderBuyGroup
		Map<Long, OrderBuyGroupDTO> OrderBuyGroupDTOMap = new HashMap<Long, OrderBuyGroupDTO>();
		Map<String, Order> orderMap = new HashMap<String, Order>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(marketOrder2);
		JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("OrderBuyGroup");
		for (Object object : numShopsJSON) {
			//создаём OrderBuyGroup
			OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(object.toString());
			OrderBuyGroupDTOMap.put(orderBuyGroupDTO.getOrderBuyGroupId(), orderBuyGroupDTO);
			Order order = orderCreater.createSimpleOrder(orderBuyGroupDTO);
			orderMap.put(order.getMarketNumber(), order);
		}
		response.put("status", "200");
		response.put("orders", orderMap);
		return response;	
	}
	
	/**
	 * Тест отдельного ордера на предмето того, находится ли он в 50 статусе или нет
	 * @param request
	 * @param idMarket
	 * @return
	 */
	@GetMapping("/manager/testMarketOrderStatus/{idMarket}")
	public Map<String, Object> testMarketOrder(HttpServletRequest request, @PathVariable String idMarket) {		
		try {			
			checkJWT(marketUrl);			
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		
		Map<String, Object> response = new HashMap<String, Object>();
		MarketDataForRequestDto dataDto3 = new MarketDataForRequestDto(idMarket);
		MarketPacketDto packetDto3 = new MarketPacketDto(marketJWT, "SpeedLogist.GetOrderBuyInfo", serviceNumber, dataDto3);
		MarketRequestDto requestDto3 = new MarketRequestDto("", packetDto3);
		String marketOrder2;
		try {
			marketOrder2 = postRequest(marketUrl, gson.toJson(requestDto3));
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "100");
			response.put("exception", e.toString());
			response.put("message", "Ошибка запроса к Маркету");
			response.put("info", "Ошибка запроса к Маркету");
			return response;
		}
		//проверяем на наличие сообщений об ошибке со стороны маркета
		if(marketOrder2.contains("Error")) {
			MarketErrorDto errorMarket = gson.fromJson(marketOrder2, MarketErrorDto.class);
//			System.out.println(errorMarket);
			if(errorMarket.getError().equals("99")) {//обработка случая, когда в маркете номера нет, а в бд есть.
				Order orderFromDB = orderService.getOrderByMarketNumber(idMarket);
				if(orderFromDB !=null) {
					response.put("status", "100");
					response.put("info", "Заказ " + idMarket + " не найден в маркете. Данные из SL устаревшие. Обновите данные в Маркете");
					return response;
				}else {
					response.put("status", "100");
					response.put("info", errorMarket.getErrorDescription());
					return response;
				}
			}
			response.put("status", "100");
			response.put("info", errorMarket.getErrorDescription());
			return response;
		}
		
		//тут избавляемся от мусора в json
		String str2 = marketOrder2.split("\\[", 2)[1];
		String str3 = str2.substring(0, str2.length()-2);
		
		//создаём свой парсер и парсим json в объекты, с которыми будем работать.
		CustomJSONParser customJSONParser = new CustomJSONParser();
		
		//создаём OrderBuyGroup
		OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(str3);
					
		//создаём Order, записываем в бд и возвращаем или сам ордер или ошибку (тот же ордер, только с отрицательным id)
		Order order = orderCreater.create(orderBuyGroupDTO);
		
//		System.out.println(order);
		
		if(order.getIdOrder() < 0) {
			if(order.getMarketInfo() != null) {
				switch (order.getMarketInfo()) {
				case "0":
					response.put("status", "105");
					response.put("info", "Реальынй статус из маркета - ЧЕРНОВИК");
					return response;
					
				case "-1":
					response.put("status", "105");
					response.put("info", "Статус маркет = null");
					response.put("orderDTO", orderBuyGroupDTO);
					return response;

				default:
					response.put("status", "200");
					response.put("info", "Заказ не в 50 статусе но и не в 0 статусе");
					return response;
				}
			}else {
				Order orderInBase = orderService.getOrderByMarketNumber(idMarket);
				response.put("status", "105");
				response.put("info", "Заказ в Маркете в статусе: " + orderBuyGroupDTO.getCheckx() + ". Сообщение системы: " + order.getMessage()+"\n"
						+"Паллеты в SL = " + orderInBase.getPall() + "  -  паллеты из маркета = " + order.getPall());
				return response;
			}
			
			
		}else {
			response.put("status", "200");
			response.put("info", "Заказ в 50 статусе");
//			System.out.println(checkOrderNeeds.check(order)); // тестовая проверка 
			return response;
		}		
				
	}
	
	/**
	 * Метод проверки наличия jwt токена. Должен находится перед каждём запросом в маркет. 
	 * @return
	 */
	public void checkJWT(String url) {
		MarketDataForLoginDto dataDto = new MarketDataForLoginDto(loginMarket, passwordMarket, "101");
//		MarketDataForLoginDtoTEST dataDto = new MarketDataForLoginDtoTEST("SpeedLogist", "12345678", 101);
		MarketPacketDto packetDto = new MarketPacketDto("null", "GetJWT", serviceNumber, dataDto);
		MarketRequestDto requestDto = new MarketRequestDto("", packetDto);
		if(marketJWT == null){
			//запрашиваем jwt
			String str = null;
			try {
				str = postRequest(url, gson.toJson(requestDto));
			} catch (Exception e) {
				e.printStackTrace();
			}
			MarketTableDto marketRequestDto = gson.fromJson(str, MarketTableDto.class);
			marketJWT = marketRequestDto.getTable()[0].toString().split("=")[1].split("}")[0];
			System.err.println("Пришел такой JWT (marketRequestDto): " + marketRequestDto);
			System.err.println("Пришел такой JWT (распарсил): " + marketJWT);
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
			response.put("info", "Заказ не найден");
			return response;
		}
		order.setMarketInfo(cleanXSS(text));
		orderService.updateOrder(order);
		response.put("status", "200");
		response.put("message", "Комментарий изменен");
		response.put("info", "Комментарий изменен");
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
		java.util.Date t1 = new java.util.Date();
		Set<Route> routes = routeService.getRouteListAsDateForInternational(dateStart, dateFinish);		
		java.util.Date t2 = new java.util.Date();
		System.out.println("getRouteForInternational :" + (t2.getTime() - t1.getTime()) + " ms");
		return routes;
	}
	
	@GetMapping("/carrier/getStatusTenderForMe")
	public List<Route> getStatusTenderForMe(HttpServletRequest request) {
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
		List<Route> resultLIst = new ArrayList<Route>(result);
		resultLIst.sort((o1,o2) -> o2.getIdRoute().hashCode() - o1.getIdRoute().hashCode());
		return resultLIst;
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
	public Map<String, String> postSlotSave(HttpServletRequest request, @RequestBody String str) throws Exception {
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
			response.put("info", "Неправомерный запрос от роли логиста");
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idOrder = jsonMainObject.get("idOrder") != null ? Integer.parseInt(jsonMainObject.get("idOrder").toString()) : null;
		if(idOrder == null) {
			response.put("status", "100");
			response.put("message", "Ошибка. Не пришел idOrder");
			response.put("info", "Ошибка. Не пришел idOrder");
			return response;
		}
		Order order = orderService.getOrderById(idOrder);
		String infoCheck = null;
		
		
		switch (order.getStatus()) {
		case 8: // от поставщиков			
			if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false") && !checkDeepImport(order, request)) {
				
				//тут проверка поплану
				PlanResponce planResponce = readerSchedulePlan.process(order, request);
				if(planResponce.getStatus() == 0) {
					infoCheck = planResponce.getMessage();
					response.put("status", "105");
					response.put("info", infoCheck.replace("\n", "<br>"));
					return response;
				}else {
					order.setStatus(100);
					orderService.updateOrder(order);
					String info = chheckScheduleMethodAllInfo(request, order.getMarketContractType(), order.getTimeDelivery().toLocalDateTime().toLocalDate().toString(), order.getCounterparty());
					saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "save", info, order.getMarketContractType());
					Message message = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "save");
					slotWebSocket.sendMessage(message);	
					infoCheck = planResponce.getMessage();
					response.put("status", "200");
					response.put("info", infoCheck.replace("\n", "<br>"));
					java.util.Date t2 = new java.util.Date();
					System.out.println(t2.getTime()-t1.getTime() + " ms - save" );
					response.put("message", str);			
					return response;
				}
			}else {
				order.setStatus(100);
				orderService.updateOrder(order);
				String info = chheckScheduleMethodAllInfo(request, order.getMarketContractType(), order.getTimeDelivery().toLocalDateTime().toLocalDate().toString(), order.getCounterparty());
				saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "save", info, order.getMarketContractType());
				Message message = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "save");
				slotWebSocket.sendMessage(message);	
				response.put("status", "200");
				java.util.Date t2 = new java.util.Date();
				System.out.println(t2.getTime()-t1.getTime() + " ms - save" );
				response.put("message", str);			
				return response;
			}
			
		case 7: // сакмовывоз			
			if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false") && !checkDeepImport(order, request)) {
				//тут проверка по плану
				PlanResponce planResponce = readerSchedulePlan.process(order, request);
                if(planResponce.getStatus() == 0) {
                    infoCheck = planResponce.getMessage();
                    response.put("status", "105");
                    response.put("info", infoCheck.replace("\n", "<br>"));
                    return response;
                }else {
                	order.setStatus(20);
        			order.setChangeStatus("Создал: " + user.getSurname() + " " + user.getName() + " " + user.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        			orderService.updateOrder(order);
        			String info2 = chheckScheduleMethodAllInfo(request, order.getMarketContractType(), order.getTimeDelivery().toLocalDateTime().toLocalDate().toString(), order.getCounterparty());
        			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), 
        					order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "save", info2, order.getMarketContractType());
        			Message message7 = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "save");
        			slotWebSocket.sendMessage(message7);
                    infoCheck = planResponce.getMessage();
                    response.put("status", "200");
                    response.put("info", infoCheck.replace("\n", "<br>"));
                    java.util.Date t3 = new java.util.Date();
        			System.out.println(t3.getTime()-t1.getTime() + " ms - save" );
        			String text = "Создана заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+"; \nСлот на выгркузку: "+ order.getTimeDelivery() +"; " +
        					"\nНаправление: " + order.getWay();
        			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", text, properties.getProperty("email.addNewProcurement.rb.1"), properties.getProperty("email.addNewProcurement.rb.2"));
        			response.put("message", str);			
        			return response;
                }
			}else {
				order.setStatus(20);
    			order.setChangeStatus("Создал: " + user.getSurname() + " " + user.getName() + " " + user.getPatronymic() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
    			orderService.updateOrder(order);
    			String info2 = chheckScheduleMethodAllInfo(request, order.getMarketContractType(), order.getTimeDelivery().toLocalDateTime().toLocalDate().toString(), order.getCounterparty());
    			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), 
    					order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "save", info2, order.getMarketContractType());
    			Message message7 = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "save");
    			slotWebSocket.sendMessage(message7);
                response.put("status", "200");
                java.util.Date t3 = new java.util.Date();
    			System.out.println(t3.getTime()-t1.getTime() + " ms - save" );
    			String text = "Создана заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+"; \nСлот на выгркузку: "+ order.getTimeDelivery() +"; " +
    					"\nНаправление: " + order.getWay();
    			mailService.sendSimpleEmailTwiceUsers(request, "Новая заявка", text, properties.getProperty("email.addNewProcurement.rb.1"), properties.getProperty("email.addNewProcurement.rb.2"));
    			response.put("message", str);			
    			return response;
			}
						
		case 100: // сакмовывоз			
			if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false")) {
				response.put("status", "200");
                order.setStatus(8);
    			orderService.updateOrder(order);
    			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "unsave", null, order.getMarketContractType());
    			Message message100 = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "unsave");
    			slotWebSocket.sendMessage(message100);
    			java.util.Date t4 = new java.util.Date();
    			System.out.println(t4.getTime()-t1.getTime() + " ms - save" );
    			response.put("message", str);			
    			return response;
			}else {
                response.put("status", "200");
                order.setStatus(8);
    			orderService.updateOrder(order);
    			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "unsave", null, order.getMarketContractType());
    			Message message100 = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "unsave");
    			slotWebSocket.sendMessage(message100);
    			java.util.Date t4 = new java.util.Date();
    			System.out.println(t4.getTime()-t1.getTime() + " ms - save" );
    			response.put("message", str);			
    			return response;
			}

		default:
			response.put("status", "100");
			response.put("message", "Ошибка в статусах. Ожидается статусы 7, 8, или 100");			
			response.put("info", "Ошибка в статусах. Ожидается статусы 7, 8, или 100");			
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
			response.put("info", "Неправомерный запрос от роли логиста");
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idOrder = jsonMainObject.get("idOrder") != null ? Integer.parseInt(jsonMainObject.get("idOrder").toString()) : null;
		if(idOrder == null) {
			response.put("status", "100");
			response.put("message", "Ошибка. Не пришел idOrder");
			response.put("info", "Ошибка. Не пришел idOrder");
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
			Message message = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "delete");
			slotWebSocket.sendMessage(message);	
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), oldIdRamp, null, oldTimeDelivery, null, user.getLogin(), "delete", null, order.getMarketContractType());
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
			Message message = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "delete");
			slotWebSocket.sendMessage(message);	
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), oldIdRamp, null, oldTimeDelivery, null, user.getLogin(), "delete", null, order.getMarketContractType());
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
			Message message = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "delete");
			slotWebSocket.sendMessage(message);	
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), oldIdRamp, null, oldTimeDelivery, null, user.getLogin(), "delete", null, order.getMarketContractType());
			System.out.println(t2.getTime()-t1.getTime() + " ms - del" );
			response.put("status", "200");
			response.put("message", str);
			return response;
		}else {
			response.put("status", "100");
			response.put("message", "Невозможно удалить заказ из плана выгрузки, т.к. оформлена заявка на поиск транспорта");
			response.put("info", "Невозможно удалить заказ из плана выгрузки, т.к. оформлена заявка на поиск транспорта");
			return response;
		}				
	}
	
	/**
	 * Метод обновления ивента / слота на рампах
	 * @param request
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	@PostMapping("/slot/update")
	public Map<String, Object> postSlotUpdate(HttpServletRequest request, @RequestBody String str) throws Exception {
		java.util.Date t1 = new java.util.Date();
		
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/stock.properties");
		Properties propertiesStock = new Properties();
		propertiesStock.load(fileInputStream);
		
		User user = getThisUser();
		String role = user.getRoles().stream().findFirst().get().getAuthority();
		Map<String, Object> response = new HashMap<String, Object>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		Integer idOrder = jsonMainObject.get("idOrder") != null ? Integer.parseInt(jsonMainObject.get("idOrder").toString()) : null;
		if(idOrder == null) {
			response.put("status", "100");
			response.put("message", "Ошибка. Не пришел idOrder");
			response.put("info", "Ошибка. Не пришел idOrder");
			return response;
		}
		Order order = orderService.getOrderById(idOrder);
		Timestamp oldDateTimeDelivery = order.getTimeDelivery();
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
		boolean isLogist = false;
		switch (role) {
		case "ROLE_MANAGER":
			String messageManager = jsonMainObject.get("messageLogist") == null ? null : jsonMainObject.get("messageLogist").toString();
			String fullMessageManager = "Слот перемещен с рампы " + oldIdRamp +" на рампу " + order.getIdRamp() + " со времени " + oldTimeDelivery + " на новое время " + order.getTimeDelivery() + 
					" сотрудником " + user.getSurname() + " " + user.getName() + " по причине: " + messageManager + "\n";
			response.put("status", "200");
			order.setSlotInfo(fullMessageManager);
			isLogist = true;
			break;
		case "ROLE_TOPMANAGER":
			String messageTopManager = jsonMainObject.get("messageLogist") == null ? null : jsonMainObject.get("messageLogist").toString();
			String fullMessageTopManager = "Слот перемещен с рампы " + oldIdRamp +" на рампу " + order.getIdRamp() + " со времени " + oldTimeDelivery + " на новое время " + order.getTimeDelivery() + 
					" сотрудником " + user.getSurname() + " " + user.getName() + " по причине: " + messageTopManager + "\n";
			response.put("status", "200");
			order.setSlotInfo(fullMessageTopManager);
			isLogist = true;
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
				response.put("info", "Ошибка. Превышен лимит по паллетам на текущую дату");
				System.err.println("Не прошла проверку по лимитам паллет склада");
				return response;
			}			
		}
		
		
		//главная проверка по графику поставок
		String infoCheck = null;
		
		if(!checkDeepImport(order, request)) {
			if(!isLogist) { // если это не логист, то проверяем. Если логист - не проверяем при перемещении
				if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false")) {			
					PlanResponce planResponce = readerSchedulePlan.process(order, request);
					if(planResponce.getStatus() == 0) {
						infoCheck = planResponce.getMessage();
						response.put("status", "105");
						response.put("info", infoCheck.replace("\n", "<br>"));
						return response;
					}else {
						infoCheck = planResponce.getMessage();
						response.put("info", infoCheck.replace("\n", "<br>"));
						response.put("status", "200");
						order.setSlotMessageHistory(planResponce.getMessage());
					}		
					
				}
			}
			//конец главная проверка по графику поставок
		}
		
		//проверка по балансу на складах
//		response.put("balance", readerSchedulePlan.checkBalanceBetweenStock(order));
		
		
		String errorMessage = orderService.updateOrderForSlots(order);//проверка на пересечение со временим других слотов и лимит складов
		
		if(errorMessage!=null) {
			response.put("status", "100");
			response.put("message", errorMessage);
			response.put("info", errorMessage);
			System.err.println("Не прошла проверку по пересечениям слотов");
			return response;
		}else {
			String info = chheckScheduleMethodAllInfo(request, order.getMarketContractType(), order.getTimeDelivery().toLocalDateTime().toLocalDate().toString(), order.getCounterparty());
			if(order.getRoutes() != null) {
				order.getRoutes().forEach(r->{
					r.setDateUnloadPreviouslyStock(order.getTimeDelivery().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
					r.setTimeUnloadPreviouslyStock(order.getTimeDelivery().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
					routeService.saveOrUpdateRoute(r);
				});
			}
			Message message = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "update");
			slotWebSocket.sendMessage(message);	
			
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), oldIdRamp, order.getIdRamp(), oldTimeDelivery, order.getTimeDelivery(), user.getLogin(), "update", info, order.getMarketContractType());
			java.util.Date t2 = new java.util.Date();
			System.out.println(t2.getTime()-t1.getTime() + " ms - update" );
			
			if(response.get("status") == null) {
				response.put("status", "200");
			}
			response.put("message", str);
			return response;	
		}			
	}
	
	/**
	 * Метод который выводит сообщение о статусе проверки слота (такой же кк при перемещении и др.)
	 * @param request
	 * @param idOrder
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/slot/getTest/{idOrder}")
	public Map<String, String> getSlotTest(HttpServletRequest request, @PathVariable String idOrder) throws Exception {
		java.util.Date t1 = new java.util.Date();
		
		Map<String, String> response = new HashMap<String, String>();
		Order order = orderService.getOrderById(Integer.parseInt(idOrder));
				
		
		//главные проверки
		//проверка на лимит приемки паллет
//		if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false")) { // проверяем всё кроме вн перемещений
//			Integer summPall = orderService.getSummPallInStockExternal(order);
//			Integer summPallNew =  summPall + Integer.parseInt(order.getPall().trim());
//			String propKey = "limit." + getTrueStock(order);
//			if(summPallNew > Integer.parseInt(propertiesStock.getProperty(propKey))) {						
//				response.put("status", "100");
//				response.put("message", "Ошибка. Превышен лимит по паллетам на текущую дату");
//				response.put("info", "Ошибка. Превышен лимит по паллетам на текущую дату");
//				System.err.println("Не прошла проверку по лимитам паллет склада");
//				return response;
//			}			
//		}
		
		
		//главная проверка по графику поставок
		String infoCheck = null;
		
		if(!checkDeepImport(order, request)) {
				if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false")) {			
					PlanResponce planResponce = readerSchedulePlan.process(order, request);
					if(planResponce.getStatus() == 0) {
						infoCheck = planResponce.getMessage();
						response.put("status", "105");
						response.put("info", infoCheck.replace("\n", "<br>"));
						return response;
					}else {
						infoCheck = planResponce.getMessage();
						response.put("info", infoCheck.replace("\n", "<br>"));
						response.put("status", "200");
					}		
					
				}
			//конец главная проверка по графику поставок
		}
		
		
//		String errorMessage = orderService.updateOrderForSlots(order);//проверка на пересечение со временим других слотов и лимит складов
		
//		String info = chheckScheduleMethodAllInfo(request, order.getMarketContractType(), order.getTimeDelivery().toLocalDateTime().toLocalDate().toString(), order.getCounterparty());
		
		java.util.Date t2 = new java.util.Date();
		System.out.println(t2.getTime()-t1.getTime() + " ms - testSlot" );
		
		if(response.get("status") == null) {
			response.put("status", "200");
		}
		response.put("message", null);
		return response;			
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
	 * Метод возвращает номер склада из idRump
	 * @param order
	 * @return
	 * @author Ira
	 */
	private String getTrueStockWithNullCheck(Order order) {
		String numStock = null;
		if (order.getIdRamp() == null) {
			numStock = order.getNumStockDelivery();
		} else {
			if(order.getIdRamp().toString().length() < 5) {
				System.err.println("Ошибка в названии склада. Склад не может быть двухзначным");
			}
			if(order.getIdRamp().toString().length() < 6) { // проверка на будующее если будет учавстовать склад с трехзначным индексом
				numStock = order.getIdRamp().toString().substring(0, 3);
			}else {
				numStock = order.getIdRamp().toString().substring(0, 4);
			}
		}

		return numStock;
	}


	/**
	 * Метод добавления ивента / слота на рампу
	 * @param request
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	@PostMapping("/slot/load")
	public Map<String, Object> postSlotLoad(HttpServletRequest request, @RequestBody String str) throws Exception {
		java.util.Date t1 = new java.util.Date();
		
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/stock.properties");
		Properties propertiesStock = new Properties();
		propertiesStock.load(fileInputStream);
		
		User user = getThisUser();	
		Map<String, Object> response = new HashMap<String, Object>();
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
			response.put("info", "Ошибка. Не пришел idOrder");
			return response;
		}
		Order order = orderService.getOrderById(idOrder);
		//тут ставим, если слот ставится впервый раз - ставим дату и время.
		if(order.getFirstLoadSlot() == null) {
			order.setFirstLoadSlot(Timestamp.valueOf(LocalDateTime.now()));
		}
		if(order.getLoginManager() != null) {//обработка одновременного вытягивания объекта из дроп зоны
			response.put("status", "100");
			response.put("message", "Ошибка доступа. Заказ не зафиксирован. Данный заказ уже поставлен другим пользователем");
			response.put("info", "Ошибка доступа. Заказ не зафиксирован. Данный заказ уже поставлен другим пользователем");
			return response;
		}
		if(order.getStatus() == 6 && Integer.parseInt(jsonMainObject.get("status").toString()) == 8) {
			//означает что манагер заранее создал маршрут с 8 статусом а потом создал заявку на него
			response.put("status", "100");
			response.put("message", "Вы пытаетесь установить слот от поставщика как слот на самовывоз.");
			response.put("info", "Вы пытаетесь установить слот от поставщика как слот на самовывоз.");
			return response;
		}
//		Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(order.getMarketContractType()));
//		//временная фунция. Проверяет на то, стоит ли кратность машины.
//		if(schedule.getMachineMultiplicity() == null) {
//			Integer mult = jsonMainObject.get("multiplicity") != null ? Integer.parseInt(jsonMainObject.get("multiplicity").toString()) : null;
//			schedule.setMachineMultiplicity(mult);
//			if(mult!= null) scheduleService.updateSchedule(schedule);
//		}
		
		Timestamp timestamp = Timestamp.valueOf(jsonMainObject.get("timeDelivery").toString());
		Integer idRamp = Integer.parseInt(jsonMainObject.get("idRamp").toString());
		order.setTimeDelivery(timestamp);
		order.setIdRamp(idRamp);
		order.setLoginManager(user.getLogin());
		order.setStatus(jsonMainObject.get("status") == null ? 7 : Integer.parseInt(jsonMainObject.get("status").toString()));
		order.setDateOrderOrl(jsonMainObject.get("dateOrderOrl") == null ? null : Date.valueOf(jsonMainObject.get("dateOrderOrl").toString())); // c 17.06.2025 всегда перезаписываем дату заказа
//		if(order.getDateOrderOrl() == null){
//			order.setDateOrderOrl(jsonMainObject.get("dateOrderOrl") == null ? null : Date.valueOf(jsonMainObject.get("dateOrderOrl").toString()));
//		}
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
				response.put("info", "Ошибка. Превышен лимит по паллетам на текущую дату");
				System.err.println("Не прошла проверку по лимитам паллет склада");
				return response;
			}
		}
						
		//конец проверки на лимит приемки
		//главная проверка по графику поставок
		String infoCheck = null;	
		if(!checkDeepImport(order, request)) {
			if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false")) {			
				PlanResponce planResponce = readerSchedulePlan.process(order, request);
				if(planResponce.getStatus() == 0) {
					infoCheck = planResponce.getMessage();
					response.put("status", "105");
					response.put("info", infoCheck.replace("\n", "<br>"));
					return response;
				}else {
					infoCheck = planResponce.getMessage();
					response.put("info", infoCheck.replace("\n", "<br>"));
					response.put("status", "200");
					order.setSlotMessageHistory(planResponce.getMessage());				
					}	
				
			}
		}
		//конец главная проверка по графику поставок
		
				
		String errorMessage = orderService.updateOrderForSlots(order);//проверка на пересечение со временим других слотов
		
		if(errorMessage != null) {
			response.put("status", "100");
			response.put("message", errorMessage);
			response.put("info", errorMessage.replace("\n", "<br>"));
			System.err.println("Не прошла проверку по лимитам паллет склада");
			return response;
		}else {
			String info = chheckScheduleMethodAllInfo(request, order.getMarketContractType(), order.getTimeDelivery().toLocalDateTime().toLocalDate().toString(), order.getCounterparty());
			Message message = new Message("slot", user.getLogin(), null, "200", str, idOrder.toString(), "load");
			slotWebSocket.sendMessage(message);	
			
			saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), null, order.getIdRamp(), null, order.getTimeDelivery(), user.getLogin(), "load", info, order.getMarketContractType());
			
			java.util.Date t2 = new java.util.Date();
			System.out.println(t2.getTime()-t1.getTime() + " ms - load" );
			response.put("status", "200");
			response.put("message", str);
			return response;	
		}			
	}
	
	/**
	 * Метод проверки, является ли контрагент дальним импортом. Если является - то возвращает true
	 * resources/properties/deepImport.properties
	 * @param order
	 * @param request
	 * @return
	 */
	private boolean checkDeepImport(Order order, HttpServletRequest request) {
		List<String> deepImport = propertiesUtils.getValuesByPartialKeyDeepImport(request);
		if(deepImport.contains(order.getMarketContractorId())) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Предварительный запрос при постановке слота.
	 * Нужен для определения графика поставок. и отправки дат, для выбора
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@PostMapping("/slot/preload")
	public Map<String, Object> postSlotPreLoad(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		java.util.Date t1 = new java.util.Date();
		
		User user = getThisUser();	
		Map<String, Object> response = new HashMap<String, Object>();
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
			response.put("info", "Ошибка. Не пришел idOrder");
			return response;
		}
		Order order = orderService.getOrderById(idOrder); // около 7-10 мс.
		if(order.getLoginManager() != null) {//обработка одновременного вытягивания объекта из дроп зоны
			response.put("status", "100");
			response.put("info", "Ошибка доступа. Заказ не зафиксирован. Данный заказ уже поставлен другим пользователем");
			return response;
		}
		
		Timestamp timestamp = Timestamp.valueOf(jsonMainObject.get("timeDelivery").toString());
		Integer idRamp = Integer.parseInt(jsonMainObject.get("idRamp").toString());
		order.setTimeDelivery(timestamp);
		order.setIdRamp(idRamp);
		order.setLoginManager(user.getLogin());
//		order.setStatus(jsonMainObject.get("status") == null ? 7 : Integer.parseInt(jsonMainObject.get("status").toString()));
		order.setDateOrderOrl(jsonMainObject.get("dateOrderOrl") == null ? null : Date.valueOf(jsonMainObject.get("dateOrderOrl").toString()));

		//главные проверки		
		/*<b></b>
		 * тут происходит проверка по разрешению на склады для каждого кода товара
		 */
		if(!stockBalanceRun) {
			if(getTrueStock(order).equals("1700") || getTrueStock(order).equals("1800")) {
				List<Long> codeProductList = new ArrayList<Long>(order.getOrderLinesMap().keySet());
				Map<Long,GoodAccommodation> mapOfPermissionOnStock = goodAccommodationService.getActualGoodAccommodationByCodeProductList(codeProductList);
				StringBuilder stopMessage = new StringBuilder();
				StringBuilder allertMessage = new StringBuilder();
				StringBuilder listOfCodeProduct = new StringBuilder();
				
				for (Long long1 : codeProductList) {
					if(mapOfPermissionOnStock.containsKey(long1)) {
						if(mapOfPermissionOnStock.get(long1).getStatus() == 10) {//если ожидает пождтверждения
							GoodAccommodation goodAccommodation = mapOfPermissionOnStock.get(long1);
							String text = "Товар " + goodAccommodation.getGoodName() + " ("+ long1 +") Ожидает подтверждения специалистами отдела ОСиУЗ.";
							allertMessage.append(text+"<br>");
						}else if(!mapOfPermissionOnStock.get(long1).getStocks().contains(";"+getTrueStock(order)+";")) {
							GoodAccommodation goodAccommodation = mapOfPermissionOnStock.get(long1);
							String text = "Товар " + goodAccommodation.getProductCode() + " ("+ goodAccommodation.getGoodName() +") запрещен к доставке на " + getTrueStock(order)+" склад!";
							if(stopMessage.toString().isEmpty()) {
								stopMessage.append("<b>Действие заблокировано!</b><br>");
							}
							stopMessage.append(text+"<br>"); //разрешения нет, записываем в запрет							
						}				
					}else {				
						OrderLine orderLine = order.getOrderLinesMapFull().get(long1);
						String goodName = orderLine.getGoodsName();
						String text = "Товар " + goodName + " ("+ long1 +") отсутствует в системе разрешений по складам. Создана заявка на добавление. Ожидайте подтверждения специалистами отдела ОСиУЗ.";
						allertMessage.append(text+"<br>");
						GoodAccommodation newGoodAccommodation = new GoodAccommodation(long1, getTrueStock(order)+";", 10, user.getSurname()+" " + user.getName(), user.geteMail(), Date.valueOf(LocalDate.now()), goodName);
						newGoodAccommodation.setBarcode(orderLine.getBarcode() != null ? Long.parseLong(orderLine.getBarcode()) : null);
						newGoodAccommodation.setProductGroup(orderLine.getGoodsGroupName());
						goodAccommodationService.save(newGoodAccommodation);//создаём строку
						listOfCodeProduct.append(long1.toString()+" - "+goodName+"\n");
						//записываем в данные для создания письма
					}
				}	
				
				if(!listOfCodeProduct.toString().isEmpty()) {//тут отправляем сообщение на мыло
					StringBuilder emailText = new StringBuilder();
					emailText.append("Пользователь " + user.getSurname()+" " + user.getName() + " пытается поставить на склад поставку со сл. товарами, на которые нет разрешения: \n");
					emailText.append(listOfCodeProduct.toString());
					List<String> emails = propertiesUtils.getValuesByPartialKey(request.getServletContext(), "email.accommodation");
					mailService.sendAsyncEmailToUsers(request, "Поставка товара на склад: отсутствует правило", emailText.toString(), emails);
				}
				
				if(!stopMessage.toString().isEmpty() || !allertMessage.toString().isEmpty()) {
					response.put("status", "105");
					response.put("message", stopMessage.toString() + "\n" + allertMessage.toString());
					response.put("info", stopMessage.toString() + "\n" + allertMessage.toString());
					return response;
				}
			}
			
		}
		
		/*
		 * 15мс на сохранение одного кода, 2 мс на вывод одного кода
		 * END тут происходит проверка по разрешению на склады
		 */
		
		
		if(order.getDateOrderOrl() != null) {
			response.put("status", "200");
			response.put("info", "Дата заказа ОРЛ уже установлена");
			java.util.Date t2 = new java.util.Date();
			System.out.println(t2.getTime()-t1.getTime() + " ms - preload" );
			return response;
		}
		
		
		if(!checkDeepImport(order, request)) {
			if(order.getIsInternalMovement() == null || order.getIsInternalMovement().equals("false")) {
				PlanResponce planResponce;
				
				if(getTrueStock(order).equals("1200")) {
					planResponce = readerSchedulePlan.getPlanResponceShedulesOnly(order);
				}else {
					planResponce = readerSchedulePlan.getPlanResponce(order);
				}
				
				if(planResponce.getStatus() == 0) {
					response.put("status", "100");
					response.put("message", planResponce.getMessage());
					response.put("info", planResponce.getMessage());
					return response;
				}				
				response.put("status", "200");
				response.put("timeDelivery", order.getTimeDelivery());
				response.put("planResponce", planResponce);
//				System.out.println(planResponce);
				java.util.Date t2 = new java.util.Date();
				System.out.println(t2.getTime()-t1.getTime() + " ms - preload" );
				return response;	
			}else {
				response.put("status", "200");
				response.put("info", "Поставка является внутренним перемещением");
				java.util.Date t2 = new java.util.Date();
				System.out.println(t2.getTime()-t1.getTime() + " ms - preload" );
				return response;
			}
		}else {
			response.put("status", "200");
			response.put("info", "Поставка является дальним импортом");
			java.util.Date t2 = new java.util.Date();
			System.out.println(t2.getTime()-t1.getTime() + " ms - preload" );
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
		String text;
//		if(text != null) {
//			response.put("150", text);
//			return response;
//		}
		//основной метод загрузки в БД
		text = poiExcel.loadBalanceStock2(file1, request);
		response.put("200", text);
		return response;
	}
	
//	@RequestMapping(value = "/order-support/control/loadSchedules", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
//	public Map<String, String> postLoadSchedulesHasTime(Model model, HttpServletRequest request, HttpSession session,
//			@RequestParam(value = "excel", required = false) MultipartFile excel) throws InvalidFormatException, IOException, ServiceException {
//		Map<String, String> response = new HashMap<String, String>();
//
////		File file1 = poiExcel.getFileByMultipart(excel);
////		poiExcel.importGoodAccommodation(excel.getInputStream());
//		poiExcel.actualRestrictions(excel.getInputStream());
//		response.put("status", "200");
//		response.put("message", "Успех");
//		return response;
//	}
	
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
	 * прогрузка графика поставок из excel на РЦ
	 * @param model
	 * @param request
	 * @param session
	 * @param excel
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/slots/delivery-schedule/loadRC", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, String> postLoadExcelPlanRC (Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "excel", required = false) MultipartFile excel,
			@RequestParam(value = "numStock", required = false) String num)
			throws InvalidFormatException, IOException, ServiceException {
		Map<String, String> response = new HashMap<String, String>();	
		File file1 = poiExcel.getFileByMultipartTarget(excel, request, "delivery-schedule.xlsx");
		
		List<Schedule> schedules = new ArrayList<Schedule>();
		try {
			schedules = poiExcel.loadDeliveryScheduleRC(file1, Integer.parseInt(num));
		} catch (InvalidFormatException | IOException | java.text.ParseException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedules.forEach(s-> {
			scheduleService.saveSchedule(s);
		});
		
		response.put("200", "Загружено");
//		response.put("body", schedules.toString());
		return response;
	}
	
	/**
	 * прогрузка графика поставок из excel на РЦ
	 * @param model
	 * @param request
	 * @param session
	 * @param excel
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/slots/delivery-schedule/loadTO", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, Object> postLoadExcelPlanTO (Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "excel", required = false) MultipartFile excel,
			@RequestParam(value = "toType", required = false) String toType)	
			throws InvalidFormatException, IOException, ServiceException {
		Map<String, Object> response = new HashMap<String, Object>();	
		File file1 = poiExcel.getFileByMultipartTarget(excel, request, "delivery-schedule.xlsx");
		
		List<Schedule> schedules = new ArrayList<Schedule>();
		try {
//			schedules = poiExcel.loadDeliveryScheduleTO(file1, toType);
			poiExcel.loadScheduleExcel(file1, request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schedules.forEach(s-> {
			scheduleService.saveSchedule(s);
		});
		
		response.put("200", "Загружено");
		response.put("size", schedules.size());
		return response;
	}
	
	/**
	 * прогрузка графика поставок из excel на РЦ КАМАКО
	 * @param model
	 * @param request
	 * @param session
	 * @param excel
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/slots/delivery-schedule/loadTOkam", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, String> postLoadExcelPlanTOkam (Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "excel", required = false) MultipartFile excel,
			@RequestParam(value = "toType", required = false) String toType)	
			throws InvalidFormatException, IOException, ServiceException {
		Map<String, String> response = new HashMap<String, String>();	
		File file1 = poiExcel.getFileByMultipartTarget(excel, request, "delivery-schedule.xlsx");
		
		List<Schedule> schedules = new ArrayList<Schedule>();
		try {
			schedules = poiExcel.readColumns21And22(file1);
		}catch (Exception e) {
		}
		
		response.put("200", "Обновлено");
//		response.put("body", schedules.toString());
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
	
	private static boolean isRuningOptimization = false;
	
	@GetMapping("/map/myoptimization3/reset")
	public String getReset(HttpServletRequest request) {
		if(isRuningOptimization) {
			isRuningOptimization = !isRuningOptimization;
		}
		return isRuningOptimization + "";
	}

	@TimedExecution
	@PostMapping("/map/myoptimization6")
	public Map<String, Object> myOptimization6(@RequestBody String str, HttpServletRequest request) throws Exception {
		Map<String, Object> responceMap = new HashMap<String, Object>();

		java.util.Date t1 = new java.util.Date();
//		System.out.println(t2.getTime() - t1.getTime() + "ms poiExcel.getFileByMultipart(excel)");

		try {
			if(isRuningOptimization) {
				responceMap.put("status", "105");
				responceMap.put("solution", null);
				responceMap.put("message", "Отказано! Процесс занят пользователем : " + getThisUser().getSurname() + " " + getThisUser().getName());
				responceMap.put("info", "Отказано! Процесс занят пользователем : " + getThisUser().getSurname() + " " + getThisUser().getName());
				return responceMap;
			}else {
				isRuningOptimization = !isRuningOptimization;
			}


			Double maxKoef = 2.0;
			Integer maxShopInWay;

			JSONParser parser = new JSONParser();
			JSONObject jsonMainObject = (JSONObject) parser.parse(str);
			JSONObject jsonParameters = jsonMainObject.get("params") != null ? (JSONObject) parser.parse(jsonMainObject.get("params").toString()) : null;
			JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("shops");
			JSONArray pallHasShopsJSON = (JSONArray) jsonMainObject.get("palls");
			JSONArray tonnageHasShopsJSON = (JSONArray) jsonMainObject.get("tonnage");
			JSONArray shopsWithCrossDocking = (JSONArray) jsonMainObject.get("shopsWithCrossDocking"); // номера машазинов входящих в кросдокинговые площадки
			JSONArray shopsWeightDistributionJSONArray = (JSONArray) jsonMainObject.get("shopsWithWeightDistribution"); // номера машазинов считающихся альтернативно
			JSONArray pallReturnJSON = (JSONArray) jsonMainObject.get("pallReturn");

			Double iterationStr = jsonMainObject.get("iteration") != null ? Double.parseDouble(jsonMainObject.get("iteration").toString().replaceAll(",", ".")) : null;
			if(iterationStr != null && iterationStr != 0.0) {
				maxKoef = iterationStr;
			}
			Integer maxShopInWayTarget = jsonMainObject.get("maxShopsInRoute") != null ? Integer.parseInt(jsonMainObject.get("maxShopsInRoute").toString()) : null;
			if(maxShopInWayTarget != null && maxShopInWayTarget != 0) {
				maxShopInWay = maxShopInWayTarget;
			} else {
                maxShopInWay = 22;
            }


            // Список для хранения отфильтрованных магазинов ходящих в полигон (магазы которые входят в кроссовые площадки)
//	        List<Shop> krossShops = new ArrayList<>();
//
//	        // Перебор всех магазинов и фильтрация по polygonName != null
//	        for (Object shopObject : shopsWithCrossDocking) {
//	            JSONObject shop = (JSONObject) shopObject;
//	            if (shop.get("polygonName") != null) {
//	            	Shop shopObjectHasKross = shopService.getShopByNum(Integer.parseInt(shop.get("numshop").toString()));
//	            	shopObjectHasKross.setKrossPolugonName(shop.get("polygonName").toString());
//	                krossShops.add(shopObjectHasKross);
//	            }
//	        }
//	        krossShops.sort((o1,o2) -> o1.getKrossPolugonName().hashCode() - o2.getKrossPolugonName().hashCode()); //сортируемся для удобства


			List<Integer> numShops = new ArrayList<Integer>();
			List<Double> pallHasShops = new ArrayList<Double>();
			List<Integer> tonnageHasShops = new ArrayList<Integer>();
			List<Integer> weightDistributionList = new ArrayList<Integer>();
			List<Double> pallReturn = new ArrayList<Double>();
			Map<Integer, String> shopsWithCrossDockingMap = new HashMap<Integer, String>(); // мапа где хранятся номера магазинов и название полигонов к ним

			Integer stock = Integer.parseInt(jsonMainObject.get("stock").toString());

			numShopsJSON.forEach(s -> numShops.add(Integer.parseInt(s.toString())));
			pallHasShopsJSON.forEach(p -> pallHasShops.add(Double.parseDouble(p.toString().replaceAll(",", "."))));
			tonnageHasShopsJSON.forEach(t-> tonnageHasShops.add(Integer.parseInt(t.toString())));
			pallReturnJSON.forEach(pr-> pallReturn.add(pr != null ? Double.parseDouble(pr.toString().trim()) : null));

			//прогружаем в кеш усеченный список
			List<Integer> shops = new ArrayList<Integer>(numShops);
			shops.add(stock);
			matrixMachine.matrix = distanceMatrixService.getDistanceMatrixByShops(shops);

			// Перебор всех магазинов и фильтрация по polygonName != null
			for (Object shopObject : shopsWithCrossDocking) {
				JSONObject shop = (JSONObject) shopObject;
				if (shop.get("polygonName") != null) {
					shopsWithCrossDockingMap.put(Integer.parseInt(shop.get("numshop").toString()), shop.get("polygonName").toString());
				}
			}

			//перебор значений JSONArray для получения и записи номеров магазов считающихся альтернативно
			if(shopsWeightDistributionJSONArray != null) {
				for (Object string : shopsWeightDistributionJSONArray) {
					weightDistributionList.add(Integer.parseInt(string.toString().trim()));
				}
			}

			List<Solution> solutions = new ArrayList<Solution>();
			Map<Integer, Shop> allShop = shopService.getShopMap();

			int availableCores = Runtime.getRuntime().availableProcessors();
			ExecutorService executorService = Executors.newFixedThreadPool(availableCores);
			List<CompletableFuture<Solution>> futures = new CopyOnWriteArrayList <>();
//			Semaphore semaphore = new Semaphore(10);
			//реализация перебора первого порядка
			for (double i = 1.0; i <= maxKoef; i = i + 0.02) {
				Double koeff = i;
				//			System.out.println("Коэфф = " + koeff);

				CompletableFuture<Solution> future = CompletableFuture.supplyAsync(() -> {
					Solution currentSolution;
					try {
						currentSolution = colossusProcessorRadSync.run(jsonMainObject, numShops, pallHasShops, tonnageHasShops, stock, koeff, "fullLoad", shopsWithCrossDockingMap, maxShopInWay, pallReturn, weightDistributionList, allShop);
						currentSolution.setKoef(koeff);
					} catch (Exception e) {
						throw new RuntimeException(e);
					} finally {
//						semaphore.release();
					}
					return currentSolution;
				}, executorService);

				futures.add(future);
			}

			CompletableFuture<Void> allDone = CompletableFuture.allOf(
					futures.toArray(new CompletableFuture[0])
			);

			allDone.thenRun(() -> {
				futures.forEach(f -> {
					try {
						f.thenAccept( s ->  {
							solutions.add(s);
						});// БЕЗОПАСНО, потому что задачи уже завершены
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}).join(); // чтобы main не завершился раньше

			executorService.shutdown();

			Double minOwerrun = 999999999999999999.0;
			int emptyShop = 9999;
			Solution finalSolution = null;
			for (Solution solution2 : solutions) {

				double summpall = 0;
				for (VehicleWay way : solution2.getWhiteWay()) {
					Shop stock123 = way.getWay().get(0);
					summpall = summpall + calcPallHashHsop(way.getWay(), stock123);
					way.setSummPall(roundВouble(summpall, 2));
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
			Double totalKM = 0.0;

			for (VehicleWay way : finalSolution.getWhiteWay()) {

				List<GHRequest> ghRequests = null;
				List<GHRequest> ghRequestsReturn = null;
				List<Shop> returnPoint = new ArrayList<Shop>(way.getWay());
				try {
					ghRequests = routingMachine.createrListGHRequest(way.getWay());

					Collections.reverse(returnPoint);
					ghRequestsReturn = routingMachine.createrListGHRequest(returnPoint);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<Shop[]> shopPoints = null;
				List<Shop[]> shopPointsReturn = null;
				try {
					shopPoints = routingMachine.getShopAsWay(way.getWay());
					shopPointsReturn = routingMachine.getShopAsWay(returnPoint);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GraphHopper hopper = routingMachine.getGraphHopper();
				List<MapResponse> listResult = new ArrayList<MapResponse>();
				List<MapResponse> listResultReturn = new ArrayList<MapResponse>();
				Double distance = 0.0;
				Double distanceReturn = 0.0;
				for (GHRequest req : ghRequests) {
					int index = ghRequests.indexOf(req);

					GHResponse rsp = hopper.route(req);
					if (rsp.getAll().isEmpty()) {
						rsp.getErrors().forEach(e -> System.out.println(e));
						rsp.getErrors().forEach(e -> e.printStackTrace());
						listResult.add(new MapResponse(null, null, null, 500.0, 500));
					}

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
					distance = distance + path.getDistance();
					listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
							shopPoints.get(index)[0], shopPoints.get(index)[1]));
				}
				for (GHRequest req : ghRequestsReturn) {
					int index = ghRequestsReturn.indexOf(req);

					GHResponse rsp = hopper.route(req);
					if (rsp.getAll().isEmpty()) {
						rsp.getErrors().forEach(e -> System.out.println(e));
						rsp.getErrors().forEach(e -> e.printStackTrace());
						listResultReturn.add(new MapResponse(null, null, null, 500.0, 500));
					}
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
					distanceReturn = distanceReturn + path.getDistance();
					listResultReturn.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
							shopPointsReturn.get(index)[0], shopPointsReturn.get(index)[1]));

				}

				if(distance < distanceReturn) {
					wayHasMap.put(way.getId(), listResult);
					totalKM = totalKM + distance;
					System.out.println("Выбираем прямой: id = " + way.getId() + " расстояние прямого: " + distance + " м; а обратного: " + distanceReturn);
				}else {
					wayHasMap.put(way.getId(), listResultReturn);
					totalKM = totalKM + distanceReturn;
					System.out.println("Выбираем обратный: id = " + way.getId() + " расстояние обратного: " + distanceReturn + " м; а прямого: " + distance);
				}

			}
			finalSolution.setMapResponses(wayHasMap);
			finalSolution.setMessage("Готово");
			finalSolution.setTotalRunKM(totalKM);
			System.out.println("Всего пробег: " + totalKM + " км.");
			finalSolution.setStackTrace(finalSolution.getStackTrace() + "\n" + "Всего пробег: " + totalKM + " км. Коэфициент поиска: " + finalSolution.getKoef() );

			responceMap.put("status", "200");
			responceMap.put("solution", finalSolution);
			String appPath = request.getServletContext().getRealPath("");
			System.out.println(appPath + "resources/distance/");
			if(isRuningOptimization) {
				isRuningOptimization = !isRuningOptimization;
			}


			java.util.Date t2 = new java.util.Date();
			System.out.println(t2.getTime() - t1.getTime() + " *************myopt6");

			return responceMap;
		} catch (FatalInsufficientPalletTruckCapacityException fe) {
			isRuningOptimization = false;


			responceMap.put("status", "105");
			responceMap.put("solution", null);
			responceMap.put("message", fe.getMessage());
			responceMap.put("info", fe.getMessage());
			return responceMap;
		}

	}

	@TimedExecution
	@PostMapping("/map/myoptimization5")
	public Map<String, Object> myOptimization5(@RequestBody String str, HttpServletRequest request) throws Exception {
		Map<String, Object> responceMap = new HashMap<String, Object>();
		java.util.Date t1 = new java.util.Date();

		try {
			if(isRuningOptimization) {
				responceMap.put("status", "105");
				responceMap.put("solution", null);
				responceMap.put("message", "Отказано! Процесс занят пользователем : " + getThisUser().getSurname() + " " + getThisUser().getName());
				responceMap.put("info", "Отказано! Процесс занят пользователем : " + getThisUser().getSurname() + " " + getThisUser().getName());
				return responceMap;
			}else {
				isRuningOptimization = !isRuningOptimization;
			}
			
			
			Double maxKoef = 2.0;
			Integer maxShopInWay = 22;
			
			JSONParser parser = new JSONParser();
			JSONObject jsonMainObject = (JSONObject) parser.parse(str);
			JSONObject jsonParameters = jsonMainObject.get("params") != null ? (JSONObject) parser.parse(jsonMainObject.get("params").toString()) : null;
			JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("shops");
			JSONArray pallHasShopsJSON = (JSONArray) jsonMainObject.get("palls");
			JSONArray tonnageHasShopsJSON = (JSONArray) jsonMainObject.get("tonnage");
			JSONArray shopsWithCrossDocking = (JSONArray) jsonMainObject.get("shopsWithCrossDocking"); // номера машазинов входящих в кросдокинговые площадки
			JSONArray shopsWeightDistributionJSONArray = (JSONArray) jsonMainObject.get("shopsWithWeightDistribution"); // номера машазинов считающихся альтернативно
			JSONArray pallReturnJSON = (JSONArray) jsonMainObject.get("pallReturn");
			
			Double iterationStr = jsonMainObject.get("iteration") != null ? Double.parseDouble(jsonMainObject.get("iteration").toString().replaceAll(",", ".")) : null;
			if(iterationStr != null && iterationStr != 0.0) {
				maxKoef = iterationStr;
			}
			Integer maxShopInWayTarget = jsonMainObject.get("maxShopsInRoute") != null ? Integer.parseInt(jsonMainObject.get("maxShopsInRoute").toString()) : null;
			if(maxShopInWayTarget != null && maxShopInWayTarget != 0) {
				maxShopInWay = maxShopInWayTarget;
			}
			
			
			// Список для хранения отфильтрованных магазинов ходящих в полигон (магазы которые входят в кроссовые площадки)
//	        List<Shop> krossShops = new ArrayList<>();
//	
//	        // Перебор всех магазинов и фильтрация по polygonName != null
//	        for (Object shopObject : shopsWithCrossDocking) {
//	            JSONObject shop = (JSONObject) shopObject;
//	            if (shop.get("polygonName") != null) {
//	            	Shop shopObjectHasKross = shopService.getShopByNum(Integer.parseInt(shop.get("numshop").toString()));
//	            	shopObjectHasKross.setKrossPolugonName(shop.get("polygonName").toString());
//	                krossShops.add(shopObjectHasKross);
//	            }
//	        }
//	        krossShops.sort((o1,o2) -> o1.getKrossPolugonName().hashCode() - o2.getKrossPolugonName().hashCode()); //сортируемся для удобства
	        	
	
			List<Integer> numShops = new ArrayList<Integer>();
			List<Double> pallHasShops = new ArrayList<Double>();
			List<Integer> tonnageHasShops = new ArrayList<Integer>();
			List<Integer> weightDistributionList = new ArrayList<Integer>();
			List<Double> pallReturn = new ArrayList<Double>();
			Map<Integer, String> shopsWithCrossDockingMap = new HashMap<Integer, String>(); // мапа где хранятся номера магазинов и название полигонов к ним
			
			Integer stock = Integer.parseInt(jsonMainObject.get("stock").toString());
	
			numShopsJSON.forEach(s -> numShops.add(Integer.parseInt(s.toString())));
			pallHasShopsJSON.forEach(p -> pallHasShops.add(Double.parseDouble(p.toString().replaceAll(",", "."))));
			tonnageHasShopsJSON.forEach(t-> tonnageHasShops.add(Integer.parseInt(t.toString())));
			pallReturnJSON.forEach(pr-> pallReturn.add(pr != null ? Double.parseDouble(pr.toString().trim()) : null));	
			
			//прогружаем в кеш усеченный список
			List<Integer> shops = new ArrayList<Integer>(numShops);
			shops.add(stock);
			matrixMachine.matrix = distanceMatrixService.getDistanceMatrixByShops(shops);
			
			// Перебор всех магазинов и фильтрация по polygonName != null
	        for (Object shopObject : shopsWithCrossDocking) {
	            JSONObject shop = (JSONObject) shopObject;
	            if (shop.get("polygonName") != null) {
	            	shopsWithCrossDockingMap.put(Integer.parseInt(shop.get("numshop").toString()), shop.get("polygonName").toString());
	            }
	        }
	        
	        //перебор значений JSONArray для получения и записи номеров магазов считающихся альтернативно
	        if(shopsWeightDistributionJSONArray != null) {
	        	for (Object string : shopsWeightDistributionJSONArray) {
	        		weightDistributionList.add(Integer.parseInt(string.toString().trim()));
	        	}	        	
	        }
			
			List<Solution> solutions = new ArrayList<Solution>();
			Map<Integer, Shop> allShop = shopService.getShopMap();
		
			//реализация перебора первого порядка
			for (double i = 1.0; i <= maxKoef; i = i + 0.02) {
				Double koeff = i;
	//			System.out.println("Коэфф = " + koeff);
				Solution solution = colossusProcessorRad.run(jsonMainObject, numShops, pallHasShops, tonnageHasShops, stock, koeff, "fullLoad", shopsWithCrossDockingMap, maxShopInWay, pallReturn, weightDistributionList, allShop);
	
				// строим маршруты для отправки клиенту
	
				// в этой мате ключ это id самого маршрута, т.е. WhiteWay, а значение это сам
				// маршрут
		
	//			solution.getWhiteWay().forEach(w -> {
	//				List<Shop> newPoints = logicAnalyzer.correctRouteMaker(w.getWay());				
	//				VehicleWay way = w;
	//				way.setWay(newPoints);
	//			});
				solution.setKoef(koeff);
				solutions.add(solution);
			}
			
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
				double summpall = 0;
				for (VehicleWay way : solution2.getWhiteWay()) {
					Shop stock123 = way.getWay().get(0);
					summpall = summpall + calcPallHashHsop(way.getWay(), stock123);
					way.setSummPall(roundВouble(summpall, 2));
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
	//				solution2.setStackTrace(solution2.getStackTrace() + "\n" + "Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef() + "; Паллеты: " + summpall);
				}
			}
			Map<String, List<MapResponse>> wayHasMap = new HashMap<String, List<MapResponse>>();		
			Double totalKM = 0.0;
			
			for (VehicleWay way : finalSolution.getWhiteWay()) {
				
				List<GHRequest> ghRequests = null;
				List<GHRequest> ghRequestsReturn = null;
				List<Shop> returnPoint = new ArrayList<Shop>(way.getWay());
				try {
					ghRequests = routingMachine.createrListGHRequest(way.getWay());
					
					Collections.reverse(returnPoint);
					ghRequestsReturn = routingMachine.createrListGHRequest(returnPoint);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<Shop[]> shopPoints = null;
				List<Shop[]> shopPointsReturn = null;
				try {
					shopPoints = routingMachine.getShopAsWay(way.getWay());
					shopPointsReturn = routingMachine.getShopAsWay(returnPoint);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GraphHopper hopper = routingMachine.getGraphHopper();
	//			ghRequests.forEach(r->System.out.println(r.getCustomModel()));
				List<MapResponse> listResult = new ArrayList<MapResponse>();
				List<MapResponse> listResultReturn = new ArrayList<MapResponse>();
				Double distance = 0.0;
				Double distanceReturn = 0.0;
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
					distance = distance + path.getDistance();
					listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
							shopPoints.get(index)[0], shopPoints.get(index)[1]));
				}
				for (GHRequest req : ghRequestsReturn) {
					int index = ghRequestsReturn.indexOf(req);
	
					GHResponse rsp = hopper.route(req);
					if (rsp.getAll().isEmpty()) {
						rsp.getErrors().forEach(e -> System.out.println(e));
						rsp.getErrors().forEach(e -> e.printStackTrace());
						listResultReturn.add(new MapResponse(null, null, null, 500.0, 500));
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
					distanceReturn = distanceReturn + path.getDistance();
					listResultReturn.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
							shopPointsReturn.get(index)[0], shopPointsReturn.get(index)[1]));
					
				}
				
				if(distance < distanceReturn) {
					wayHasMap.put(way.getId(), listResult);
					totalKM = totalKM + distance;
					System.out.println("Выбираем прямой: id = " + way.getId() + " расстояние прямого: " + distance + " м; а обратного: " + distanceReturn);
				}else {
					wayHasMap.put(way.getId(), listResultReturn);
					totalKM = totalKM + distanceReturn;
					System.out.println("Выбираем обратный: id = " + way.getId() + " расстояние обратного: " + distanceReturn + " м; а прямого: " + distance);
				}
				
			}
			finalSolution.setMapResponses(wayHasMap);
			finalSolution.setMessage("Готово");
			finalSolution.setTotalRunKM(totalKM);
			System.out.println("Всего пробег: " + totalKM + " км.");
			finalSolution.setStackTrace(finalSolution.getStackTrace() + "\n" + "Всего пробег: " + totalKM + " км. Коэфициент поиска: " + finalSolution.getKoef() );
			
			responceMap.put("status", "200");
			responceMap.put("solution", finalSolution);
			String appPath = request.getServletContext().getRealPath("");
			System.out.println(appPath + "resources/distance/");
			if(isRuningOptimization) {
				isRuningOptimization = !isRuningOptimization;
			}

			java.util.Date t2 = new java.util.Date();
			System.out.println(t2.getTime() - t1.getTime() + " *************myopt6");
			return responceMap;
		} catch (FatalInsufficientPalletTruckCapacityException fe) {
			isRuningOptimization = false;
			
			responceMap.put("status", "105");
			responceMap.put("solution", null);
			responceMap.put("message", fe.getMessage());
			responceMap.put("info", fe.getMessage());
			return responceMap;
		}
	}
	
//	@PostMapping("/map/myoptimization3")
//	public Map<String, Object> myOptimization3(@RequestBody String str) throws Exception {
//		Map<String, Object> responceMap = new HashMap<String, Object>();
//		try {
////			System.out.println(str);
////			if(isRuningOptimization) {
////				Solution messageSolution = new Solution();
////				messageSolution.setMapResponses(new HashMap<String, List<MapResponse>>());
////				messageSolution.setEmptyShop(new ArrayList<Shop>());
////				messageSolution.setEmptyTrucks(new ArrayList<Vehicle>());
////				messageSolution.setKoef(0.0);
////				messageSolution.setTotalRunKM(0.0);
////				messageSolution.setWhiteWay(new ArrayList<VehicleWay>());			
////				messageSolution.setMessage("Отказано! Процесс занят пользователем : " + getThisUser().getSurname() + " " + getThisUser().getName());
////				return messageSolution;
////			}else {
////				isRuningOptimization = !isRuningOptimization;
////			}
//			if(isRuningOptimization) {
//				responceMap.put("status", "105");
//				responceMap.put("solution", null);
//				responceMap.put("message", "Отказано! Процесс занят пользователем : " + getThisUser().getSurname() + " " + getThisUser().getName());
//				responceMap.put("info", "Отказано! Процесс занят пользователем : " + getThisUser().getSurname() + " " + getThisUser().getName());
//				return responceMap;
//			}else {
//				isRuningOptimization = !isRuningOptimization;
//			}
//			
//			
//			
//			Boolean mainParameter = null;
//			String algorithm = null;
//			Boolean boolParameter1 = null;
//			Boolean boolParameter2 = null;
//			Boolean boolParameter3 = null;
//			Boolean boolParameter4 = null;
//			Boolean boolParameter5 = null;
//			Boolean boolParameter6 = null;
//			Double dobleParameter1 = null;
//			Double dobleParameter2 = null;
//			Double dobleParameter3 = null;
//			Double dobleParameter4 = null;
//			Double dobleParameter5 = null;
//			
//			
//			
//			Double maxKoef = 2.0;
//			Integer maxShopInWay = 22;
//			
//			JSONParser parser = new JSONParser();
//			JSONObject jsonMainObject = (JSONObject) parser.parse(str);
//			JSONObject jsonParameters = jsonMainObject.get("params") != null ? (JSONObject) parser.parse(jsonMainObject.get("params").toString()) : null;
//			JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("shops");
//			JSONArray pallHasShopsJSON = (JSONArray) jsonMainObject.get("palls");
//			JSONArray tonnageHasShopsJSON = (JSONArray) jsonMainObject.get("tonnage");
//			JSONArray shopsWithCrossDocking = (JSONArray) jsonMainObject.get("shopsWithCrossDocking");
//			JSONArray pallReturnJSON = (JSONArray) jsonMainObject.get("pallReturn");
//			
//			Double iterationStr = jsonMainObject.get("iteration") != null ? Double.parseDouble(jsonMainObject.get("iteration").toString().replaceAll(",", ".")) : null;
//			if(iterationStr != null && iterationStr != 0.0) {
//				maxKoef = iterationStr;
//			}
//			Integer maxShopInWayTarget = jsonMainObject.get("maxShopsInRoute") != null ? Integer.parseInt(jsonMainObject.get("maxShopsInRoute").toString()) : null;
//			if(maxShopInWayTarget != null && maxShopInWayTarget != 0) {
//				maxShopInWay = maxShopInWayTarget;
//			}
//			
//			
//			// Список для хранения отфильтрованных магазинов ходящих в полигон (магазы которые входят в кроссовые площадки)
//	        List<Shop> krossShops = new ArrayList<>();
//
//	        // Перебор всех магазинов и фильтрация по polygonName != null
//	        for (Object shopObject : shopsWithCrossDocking) {
//	            JSONObject shop = (JSONObject) shopObject;
//	            if (shop.get("polygonName") != null) {
//	            	Shop shopObjectHasKross = shopService.getShopByNum(Integer.parseInt(shop.get("numshop").toString()));
//	            	shopObjectHasKross.setKrossPolugonName(shop.get("polygonName").toString());
//	                krossShops.add(shopObjectHasKross);
//	            }
//	        }
//	        krossShops.sort((o1,o2) -> o1.getKrossPolugonName().hashCode() - o2.getKrossPolugonName().hashCode()); //сортируемся для удобства
//	        	
//			
//			mainParameter = jsonParameters != null && jsonParameters.get("optimizeRouteMainCheckbox") != null ? jsonParameters.get("optimizeRouteMainCheckbox").toString().contains("true") : null; 
//			
//			if(mainParameter != null && mainParameter) {
//				algorithm = jsonParameters.get("algorithm") != null ? jsonParameters.get("algorithm").toString() : null;
//				boolParameter1 = jsonParameters.get("optimizeRouteCheckbox1") != null ? jsonParameters.get("optimizeRouteCheckbox1").toString().contains("true") : null; // параметр проверки на развернутый маршрут. Если true - то проверяем
//				boolParameter2 = jsonParameters.get("optimizeRouteCheckbox2") != null ? jsonParameters.get("optimizeRouteCheckbox2").toString().contains("true") : null;
//				boolParameter3 = jsonParameters.get("optimizeRouteCheckbox3") != null ? jsonParameters.get("optimizeRouteCheckbox3").toString().contains("true") : null;
//				boolParameter4 = jsonParameters.get("optimizeRouteCheckbox4") != null ? jsonParameters.get("optimizeRouteCheckbox4").toString().contains("true") : null;
//				boolParameter5 = jsonParameters.get("optimizeRouteCheckbox5") != null ? jsonParameters.get("optimizeRouteCheckbox5").toString().contains("true") : null;
//				boolParameter6 = jsonParameters.get("optimizeRouteCheckbox6") != null ? jsonParameters.get("optimizeRouteCheckbox6").toString().contains("true") : null;
//				
//				System.out.println(algorithm);
//				System.out.println(boolParameter1);
//				
//			}
//
//			List<Integer> numShops = new ArrayList<Integer>();
//			List<Double> pallHasShops = new ArrayList<Double>();
//			List<Integer> tonnageHasShops = new ArrayList<Integer>();
//			List<Double> pallReturn = new ArrayList<Double>();
//			Map<Integer, String> shopsWithCrossDockingMap = new HashMap<Integer, String>(); // мапа где хранятся номера магазинов и название полигонов к ним
//			
//			Integer stock = Integer.parseInt(jsonMainObject.get("stock").toString());
//
//			numShopsJSON.forEach(s -> numShops.add(Integer.parseInt(s.toString())));
//			pallHasShopsJSON.forEach(p -> pallHasShops.add(Double.parseDouble(p.toString().replaceAll(",", "."))));
//			tonnageHasShopsJSON.forEach(t-> tonnageHasShops.add(Integer.parseInt(t.toString())));
//			pallReturnJSON.forEach(pr-> pallReturn.add(pr != null ? Double.parseDouble(pr.toString().trim()) : null));
//			
//			
//			// Перебор всех магазинов и фильтрация по polygonName != null
//	        for (Object shopObject : shopsWithCrossDocking) {
//	            JSONObject shop = (JSONObject) shopObject;
//	            if (shop.get("polygonName") != null) {
//	            	shopsWithCrossDockingMap.put(Integer.parseInt(shop.get("numshop").toString()), shop.get("polygonName").toString());
//	            }
//	        }
//			
//			List<Solution> solutions = new ArrayList<Solution>();
//			
//		
//			//реализация перебора первого порядка
//			for (double i = 1.0; i <= maxKoef; i = i + 0.02) {
//				Double koeff = i;
////				System.out.println("Коэфф = " + koeff);
//				Solution solution = colossusProcessorRadOld.run(jsonMainObject, numShops, pallHasShops, tonnageHasShops, stock, koeff, "fullLoad", shopsWithCrossDockingMap, maxShopInWay);
//
//				// строим маршруты для отправки клиенту
//
//				// в этой мате ключ это id самого маршрута, т.е. WhiteWay, а значение это сам
//				// маршрут
//		
////				solution.getWhiteWay().forEach(w -> {
////					List<Shop> newPoints = logicAnalyzer.correctRouteMaker(w.getWay());				
////					VehicleWay way = w;
////					way.setWay(newPoints);
////				});
//				solution.setKoef(koeff);
//				solutions.add(solution);
//			}
//			
////			System.err.println(solutions.size());
////			solutions.forEach(s-> System.out.println(s.getTotalRunSolution()));
//			Double minOwerrun = 999999999999999999.0;
//			int emptyShop = 9999;
//			Solution finalSolution = null;
//			for (Solution solution2 : solutions) {
//				
//				//определяем и записываем суммарный пробег маршрута
//				//!!!!!!записываем внутри процессора!
////				Double totalRunHasMatrix = 0.0;
////				for (VehicleWay way : solution2.getWhiteWay()) {
////					//заменяем просчёт расстояний из GH на матричный метод			
////					for (int j = 0; j < way.getWay().size()-1; j++) {
////						String key = way.getWay().get(j).getNumshop()+"-"+way.getWay().get(j+1).getNumshop();
////						totalRunHasMatrix = totalRunHasMatrix + matrixMachine.matrix.get(key);
////					}
////				}
////				solution2.setTotalRunKM(totalRunHasMatrix);
//				double summpall = 0;
//				for (VehicleWay way : solution2.getWhiteWay()) {
//					Shop stock123 = way.getWay().get(0);
//					summpall = summpall + calcPallHashHsop(way.getWay(), stock123);
//					way.setSummPall(roundВouble(summpall, 2));
//				}
//				System.err.println("Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef() + "; Паллеты: " + summpall);
//				if(solution2.getEmptyShop().size() <= emptyShop) {
//					if(solution2.getEmptyShop().size() < emptyShop && minOwerrun < solution2.getTotalRunKM()) {
//						System.out.println("Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef()+ "; Паллеты: " + summpall);
//						minOwerrun = solution2.getTotalRunKM();
//						emptyShop = solution2.getEmptyShop().size();
//						finalSolution = solution2;
//					}
//					
//					if(solution2.getTotalRunKM() < minOwerrun) {
//						System.out.println("Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef()+ "; Паллеты: " + summpall);
//						minOwerrun = solution2.getTotalRunKM();
//						emptyShop = solution2.getEmptyShop().size();
//						finalSolution = solution2;
//					}
////					solution2.setStackTrace(solution2.getStackTrace() + "\n" + "Выбран маршрут с данными: суммарный пробег: " + solution2.getTotalRunKM() + "м, " + solution2.getEmptyShop().size() + " - кол-во неназначенных магазинов; " + solution2.getEmptyTrucks().size() + " - кол-во свободных авто; Итерация = " + solution2.getKoef() + "; Паллеты: " + summpall);
//				}
//			}
//			Map<String, List<MapResponse>> wayHasMap = new HashMap<String, List<MapResponse>>();		
//			Double totalKM = 0.0;
//			
//			for (VehicleWay way : finalSolution.getWhiteWay()) {
//				
//				List<GHRequest> ghRequests = null;
//				List<GHRequest> ghRequestsReturn = null;
//				List<Shop> returnPoint = new ArrayList<Shop>(way.getWay());
//				try {
//					ghRequests = routingMachine.createrListGHRequest(way.getWay());
//					
//					Collections.reverse(returnPoint);
//					ghRequestsReturn = routingMachine.createrListGHRequest(returnPoint);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				List<Shop[]> shopPoints = null;
//				List<Shop[]> shopPointsReturn = null;
//				try {
//					shopPoints = routingMachine.getShopAsWay(way.getWay());
//					shopPointsReturn = routingMachine.getShopAsWay(returnPoint);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				GraphHopper hopper = routingMachine.getGraphHopper();
////				ghRequests.forEach(r->System.out.println(r.getCustomModel()));
//				List<MapResponse> listResult = new ArrayList<MapResponse>();
//				List<MapResponse> listResultReturn = new ArrayList<MapResponse>();
//				Double distance = 0.0;
//				Double distanceReturn = 0.0;
//				for (GHRequest req : ghRequests) {
//					int index = ghRequests.indexOf(req);
//
//					GHResponse rsp = hopper.route(req);
//					if (rsp.getAll().isEmpty()) {
//						rsp.getErrors().forEach(e -> System.out.println(e));
//						rsp.getErrors().forEach(e -> e.printStackTrace());
//						listResult.add(new MapResponse(null, null, null, 500.0, 500));
//					}
////					System.err.println(rsp.getAll().size());
//					if (rsp.getAll().size() > 1) {
//						rsp.getAll().forEach(p -> System.out.println(p.getDistance() + "    " + p.getTime()));
//					}
//					ResponsePath path = rsp.getBest();
//					List<ResponsePath> listPath = rsp.getAll();
//					for (ResponsePath pathI : listPath) {
//						if (pathI.getDistance() < path.getDistance()) {
//							path = pathI;
//						}
//					}
////					System.out.println(roundВouble(path.getDistance()/1000, 2) + "km, " + path.getTime() + " time");
//					PointList pointList = path.getPoints();
//					path.getPathDetails();
//					List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
//					pointList.forEach(p -> result.add(p.toGeoJson()));
//					List<Double[]> resultPoints = new ArrayList<Double[]>();
//					double cash = 0.0;
//					for (Double[] point : result) {
//						cash = point[0];
//						point[0] = point[1];
//						point[1] = cash;
//						resultPoints.add(point);
//					}
//					distance = distance + path.getDistance();
//					listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
//							shopPoints.get(index)[0], shopPoints.get(index)[1]));
//				}
//				for (GHRequest req : ghRequestsReturn) {
//					int index = ghRequestsReturn.indexOf(req);
//
//					GHResponse rsp = hopper.route(req);
//					if (rsp.getAll().isEmpty()) {
//						rsp.getErrors().forEach(e -> System.out.println(e));
//						rsp.getErrors().forEach(e -> e.printStackTrace());
//						listResultReturn.add(new MapResponse(null, null, null, 500.0, 500));
//					}
////					System.err.println(rsp.getAll().size());
//					if (rsp.getAll().size() > 1) {
//						rsp.getAll().forEach(p -> System.out.println(p.getDistance() + "    " + p.getTime()));
//					}
//					ResponsePath path = rsp.getBest();
//					List<ResponsePath> listPath = rsp.getAll();
//					for (ResponsePath pathI : listPath) {
//						if (pathI.getDistance() < path.getDistance()) {
//							path = pathI;
//						}
//					}
////					System.out.println(roundВouble(path.getDistance()/1000, 2) + "km, " + path.getTime() + " time");
//					PointList pointList = path.getPoints();
//					path.getPathDetails();
//					List<Double[]> result = new ArrayList<Double[]>(); // возможна утечка помяти
//					pointList.forEach(p -> result.add(p.toGeoJson()));
//					List<Double[]> resultPoints = new ArrayList<Double[]>();
//					double cash = 0.0;
//					for (Double[] point : result) {
//						cash = point[0];
//						point[0] = point[1];
//						point[1] = cash;
//						resultPoints.add(point);
//					}
//					distanceReturn = distanceReturn + path.getDistance();
//					listResultReturn.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(),
//							shopPointsReturn.get(index)[0], shopPointsReturn.get(index)[1]));
//					
//				}
//				
//				if(distance < distanceReturn) {
//					wayHasMap.put(way.getId(), listResult);
//					totalKM = totalKM + distance;
//					System.out.println("Выбираем прямой: id = " + way.getId() + " расстояние прямого: " + distance + " м; а обратного: " + distanceReturn);
//				}else {
//					wayHasMap.put(way.getId(), listResultReturn);
//					totalKM = totalKM + distanceReturn;
//					System.out.println("Выбираем обратный: id = " + way.getId() + " расстояние обратного: " + distanceReturn + " м; а прямого: " + distance);
//				}
//				
//			}
//			finalSolution.setMapResponses(wayHasMap);
//			finalSolution.setMessage("Готово");
//			finalSolution.setTotalRunKM(totalKM);
//			System.out.println("Всего пробег: " + totalKM + " км.");
//			finalSolution.setStackTrace(finalSolution.getStackTrace() + "\n" + "Всего пробег: " + totalKM + " км. Коэфициент поиска: " + finalSolution.getKoef() );
//			
//			if(isRuningOptimization) {
//				isRuningOptimization = !isRuningOptimization;
//			}
//			
//			responceMap.put("status", "200");
//			responceMap.put("solution", finalSolution);
//			return responceMap;
//		} catch (FatalInsufficientPalletTruckCapacityException fe) {
//			if(isRuningOptimization) {
//				isRuningOptimization = !isRuningOptimization;
//			}
//			
//			responceMap.put("status", "105");
//			responceMap.put("solution", null);
//			responceMap.put("message", fe.getMessage());
//			responceMap.put("info", fe.getMessage());
//			return responceMap;
//		}
//
//	}
	
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
			mailService.sendSimpleEmail(appPath, "Изменение заявки ОСиУЗ", text, finalManagerEmail);
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
			mailService.sendSimpleEmail(appPath, "Отмена заявки ОСиУЗ", text, finalManagerEmail);
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
	 * Новый метод!
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
		List<Order> orders = new ArrayList<Order>();
		if(order.getLink() != null) {
			orders = orderService.getOrderByLink(order.getLink());
		}
		
		final String finalManagerEmail = managerEmail;
		if(managerEmail == null || managerEmail.equals("")) {
			response.put("status", "100");
			response.put("message", "Ошибка: не назначена почта у менеджера");
			return response;
		}
		String text = "";
		List<Route> routes = new ArrayList<Route>(order.getRoutes());
		String driverStr = "";
		String subjectEmail = null;
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
			//тут разделение, если заказ один или если заказы связаны:
			if(!orders.isEmpty()) {//если несколько связанных заказов
				subjectEmail = "Данные по заявке ";
				boolean w05 = false;
				boolean w07 = false;
				driverStr = r.getDriver().getSurname() + " " + r.getDriver().getName() + " " + r.getDriver().getPatronymic()+"\n";
//				text = text + "Объедененные заказы.\n";
				text = text + "Данные по маршруту: " + r.getRouteDirection() + "; \n";
				text = text + "Заказы: \n";
				int j = 1;
				for (Order orderI : orders) {
					switch (getTrueStock(orderI)) {
					case "1700":
						w05 = true;
						break;
					case "1800":
						w07 = true;
						break;
					}
					text = text + "	"+j+". " + "Номер заказа из маркета: " + orderI.getMarketNumber() + "; ID заказа в системе " + orderI.getIdOrder()+"; "+ "Контрагент: " + orderI.getCounterparty() + ";\n";
					subjectEmail = subjectEmail + "№" + orderI.getIdOrder()+"; ";
					j++;
				}
				subjectEmail = subjectEmail +" Внимание: выгрузка на складе W05 (1700) и W07 (1800)";
				text = text + "Маршрут: \n";
				int k = 1;
				for (RouteHasShop routeHasShop : r.getRoteHasShop()) {
					text = text + "	" + k + ". " + routeHasShop.getAddress() + "; \n";
					k++;
				}
				if(w05 && w07) {
					text = text + "	" + "Внимание: выгрузка на складе W05 (1700) и W07 (1800); \n";
				}
				text = text + "\nПеревозчик: " + r.getUser().getCompanyName()+"\n";
				text = text + "Подвижной состав: " + r.getTruck().getNumTruck() + "/" + r.getTruck().getNumTrailer()+"\n";
				text = text + "Марка машины / прицепа: " + r.getTruck().getBrandTruck() + "/" + r.getTruck().getBrandTrailer()+"\n";
				text = text + "Водитель: " + r.getDriver().getSurname() + " " + r.getDriver().getName() + " " + r.getDriver().getPatronymic()+"\n";
				text = text + "Телефон: " + r.getDriver().getTelephone()+"\n";
				text = text + "Паспортные данные водителя: " +r.getDriver().getNumPass() + "; водительское удостоверение:" + r.getDriver().getNumDriverCard() +"\n\n";
				
				text = text + "Итоговая цена за перевозку составила: " +r.getFinishPrice() + " " + r.getStartCurrency();	
				if(r.getExpeditionCost() != null) {
					text = text + ", в т.ч. экспедиторские услуги составили: " + r.getExpeditionCost() + " " + r.getStartCurrency() + "\n";
				}else {
					text = text + ";\n";
				}
				text = text + "Дата подачи машины на загрузку: " +r.getDateLoadActually().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " в " + r.getTimeLoadActually().format(DateTimeFormatter.ofPattern("HH:mm")) +"\n";			
				text = text + "Дата подачи машины на выгрузку: " +r.getDateUnloadActually().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " в " + r.getTimeUnloadActually().format(DateTimeFormatter.ofPattern("HH:mm")) +"\n";
				
			}else {//если один заказа
				subjectEmail = "Данные по заявке №"+order.getIdOrder();
				driverStr = r.getDriver().getSurname() + " " + r.getDriver().getName() + " " + r.getDriver().getPatronymic()+"\n";
				text = text + "Данные по маршруту: " + r.getRouteDirection() + "; \n" + "Номер заказа из маркета: " + order.getMarketNumber() + "; \nID заказа в системе " + order.getIdOrder()+";\n";
				text = text + "ID маршрута в системе: " + r.getIdRoute() + ";\n";
				text = text + "Маршрут: \n";
				int k = 1;
				for (RouteHasShop routeHasShop : r.getRoteHasShop()) {
					text = text + "	" + k + ". " + routeHasShop.getAddress() + "; \n";
					k++;
				}
				text = text + "\nПеревозчик: " + r.getUser().getCompanyName()+"\n";
				text = text + "Подвижной состав: " + r.getTruck().getNumTruck() + "/" + r.getTruck().getNumTrailer()+"\n";
				text = text + "Марка машины / прицепа: " + r.getTruck().getBrandTruck() + "/" + r.getTruck().getBrandTrailer()+"\n";
				text = text + "Водитель: " + r.getDriver().getSurname() + " " + r.getDriver().getName() + " " + r.getDriver().getPatronymic()+"\n";
				text = text + "Телефон: " + r.getDriver().getTelephone()+"\n";
				text = text + "Паспортные данные водителя: " +r.getDriver().getNumPass() + "; водительское удостоверение:" + r.getDriver().getNumDriverCard() +"\n\n";
				
				text = text + "Итоговая цена за перевозку составила: " +r.getFinishPrice() + " " + r.getStartCurrency();	
				if(r.getExpeditionCost() != null) {
					text = text + ", в т.ч. экспедиторские услуги составили: " + r.getExpeditionCost() + " " + r.getStartCurrency() + "\n";
				}else {
					text = text + ";\n";
				}
				text = text + "Дата подачи машины на загрузку: " +r.getDateLoadActually().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " в " + r.getTimeLoadActually().format(DateTimeFormatter.ofPattern("HH:mm")) +"\n";			
				text = text + "Дата подачи машины на выгрузку: " +r.getDateUnloadActually().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " в " + r.getTimeUnloadActually().format(DateTimeFormatter.ofPattern("HH:mm")) +"\n";
			}
			
				
		}
		
		final String message = text;
		String mailInfo = "Сообщение было отправлено " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + ". Водитель: " + driverStr + "\n";
		order.setMailInfo(order.getMailInfo() + mailInfo);
		orderService.updateOrder(order);
		mailService.sendSimpleEmailTwiceUsers(request, subjectEmail, message, finalManagerEmail, logist.geteMail());
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
		String appPath = request.getServletContext().getRealPath("");
		new Thread(new Runnable() {			
			@Override
			public void run() {
				mailService.sendSimpleEmail(appPath, "Данные по заявке №"+order.getIdOrder(), message, finalManagerEmail);				
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
	
	@GetMapping("/map/downloadmatrixJSON")
	public String getDownloadmatrix(HttpServletRequest request) {
		Gson gson = new Gson();
        String json = gson.toJson(matrixMachine.matrix);
        try (FileWriter writer = new FileWriter(request.getServletContext().getRealPath("") + "resources/distance/data.json")) {
            writer.write(json);
            System.out.println(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return request.getServletContext().getRealPath("") + "resources/distance/data.json";
	}
	
	@GetMapping("/map/downloadmatrixCSV")
	public String getDownloadmatrixCSV(HttpServletRequest request) {
        convertToCSV(matrixMachine.matrix, request.getServletContext().getRealPath("") + "resources/distance/data.csv");
		return request.getServletContext().getRealPath("") + "resources/distance/data.csv";
	}
	
	/**
	 * Метод, который формирует и скачивает матрицу расстояний
	 * GPT
	 * @param request
	 * @param response
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@GetMapping("/map/downloadmatrix")
	@ResponseBody
	public String getDownloadmatrix2(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
		 // Получаем уникальные точки
        Set<String> points = new HashSet<>();
        for (String key : matrixMachine.matrix.keySet()) {
            String[] pointPair = key.split("-");
            points.add(pointPair[0]);
            points.add(pointPair[1]);
        }

        // Создаем массив для точек
        String[] pointArray = points.toArray(new String[0]);

        // Создаем двумерный массив для расстояний
        double[][] matrix = new double[points.size()][points.size()];

        // Заполняем двумерный массив расстояний
        for (int i = 0; i < pointArray.length; i++) {
            for (int j = 0; j < pointArray.length; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    String key1 = pointArray[i] + "-" + pointArray[j];
                    String key2 = pointArray[j] + "-" + pointArray[i];
                    if (matrixMachine.matrix.containsKey(key1)) {
                        matrix[i][j] = matrixMachine.matrix.get(key1);
                    } else if (matrixMachine.matrix.containsKey(key2)) {
                        matrix[i][j] = matrixMachine.matrix.get(key2);
                    } else {
                        matrix[i][j] = 0; // Если расстояние не задано
                    }
                }
            }
        }

        // Создаем новый файл Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Distances");

        // Заполняем заголовки
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < pointArray.length; i++) {
            Cell cell = headerRow.createCell(i + 1);
            cell.setCellValue(pointArray[i]);
        }
        for (int i = 0; i < pointArray.length; i++) {
            Row row = sheet.createRow(i + 1);
            Cell cell = row.createCell(0);
            cell.setCellValue(pointArray[i]);
            for (int j = 0; j < pointArray.length; j++) {
                cell = row.createCell(j + 1);
                cell.setCellValue(matrix[i][j]);
            }
        }

        // Записываем файл
        try (FileOutputStream fileOut = new FileOutputStream(request.getServletContext().getRealPath("") + "resources/distance/distances.xlsx")) {
            workbook.write(fileOut);
        }

        // Записываем файл в ответ
        response.setHeader("content-disposition", "attachment;filename=distances.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
        
//        FileInputStream in = null;
//		OutputStream out = null;
//		response.setHeader("content-disposition", "attachment;filename=distances.xlsx");
//		try {
//			
//			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
//			in = new FileInputStream(request.getServletContext().getRealPath("") + "resources/distance/distances.xlsx");
//			//  Создать выходной поток
//			out = response.getOutputStream();			
//			//  Создать буфер
//			byte buffer[] = new byte[1024];
//			int len = 0;
//			//  Прочитать содержимое входного потока в буфер в цикле
//			while ((len = in.read(buffer)) > 0) {
//				out.write(buffer, 0, len);
//			}
//			in.close();
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			in.close();
//			out.close();
//		}
    
		return "Готово";
	}
	
    private void convertToCSV(Map<String, Double> matrix, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Пишем заголовок CSV
            writer.append("Key,Value;");

            // Пишем данные из HashMap в CSV
            for (Entry<String, Double> entry : matrix.entrySet()) {
                writer.append(entry.getKey().toString());
                writer.append(",");
                writer.append(entry.getValue().toString());
                writer.append(";");
            }

            System.out.println("CSV файл успешно создан: " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
		response.put("/map/calcmatrix/{i}&{thread}", "рассчитать матрицу и создать фай1л сериализации (0 - если всю)");
		response.put("/map/downloadmatrixJSON", "Скачивает матрицу расстояний в фолрмате json");
		response.put("/map/downloadmatrixCSV", "Скачивает матрицу расстояний в фолрмате CSV с разделителем ;");
		response.put("/map/downloadmatrix", "Скачивает матрицу расстояний в виде двумерного массива");
		return response;
	}

	@GetMapping("/map/calcmatrix/{i}&{thread}")
	public Integer getCalcMatrix(HttpServletRequest request, @PathVariable Integer i, @PathVariable Integer thread) {
		System.out.println("на вход получил " + i);
		if (i == 0) {
			return matrixMachine.calculationDistanceToDB(null, thread);
//			return matrixMachine.calculationDistanceNew(null, Runtime.getRuntime().availableProcessors());
		} else {
			return matrixMachine.calculationDistanceToDB(i, thread);
		}
	}

	// конец загрузки и проверка матрицы
	
	@Autowired
	private LogicAnalyzer logicAnalyzer;
	

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
		String crossDockingPoint = propertiesJSON.get("crossDockingPoint") == null ? null :propertiesJSON.get("crossDockingPoint").toString();
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
				new GeometryFactory().createPolygon(area), new HashMap<>(), crossDockingPoint);
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

	
	/**
	 * Метод отвечает за формирование отчёта.
	 * Отлично отправляет его через rest
	 * @param dateStart
	 * @param dateFinish
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
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
//		List<Integer> shopsNum = 
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
		shop.setName(jsonMainObject.get("name") != null && !jsonMainObject.get("name").toString().isEmpty() ? jsonMainObject.get("name").toString() : null);
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
		shop.setName(jsonMainObject.get("name") != null && !jsonMainObject.get("name").toString().isEmpty() ? jsonMainObject.get("name").toString() : null);
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

	
	@RequestMapping(value = "/map/way/5", method = RequestMethod.POST)
	public List<MapResponse> getSplitWayHasNumShop5(@RequestBody String str) throws ParseException {
		List<GHRequest> ghRequests = routingMachine.parseJSONFromClientRequestSplitV2(str);
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
		if (order.getStatus() == 20 || order.getStatus() == 17 || order.getStatus() == 15 || order.getStatus() == 6 || order.getStatus() == 7) { // общее редактирование, пока маршрут не создан
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
//				System.out.println(jsonpObject);
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
						addressNewCorrect.setPointNumber(jsonpObject.get("pointNumer") == null ? null
								: Integer.parseInt(jsonpObject.get("pointNumer").toString()));
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
						System.out.println("Корректировка обновлена");
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
						System.out.println("Внесена корректировка");
					}
				}
			}
		}
		return response;
	}
	
	// Метод для объединения onloadWindowDate и onloadWindowTime в Timestamp
    private static Timestamp combineDateAndTime(java.sql.Date date, Time time) {
        if (date == null) {
            return null; // Если нет даты, возвращаем null
        }
        long timeInMillis = time != null ? time.getTime() : 0;
        return new Timestamp(date.getTime() + timeInMillis);
    }

    /**
     * создание маршрута через заявку c forPromotion
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
       List<Order> orders = new ArrayList<Order>();

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
       
       
       
       //тут я фильтруюсь по ордеру который выгружается раньше.
       /*
        * Основной принцип сортировки:
              Если timeDelivery не null, используется он.
              Если timeDelivery == null, создаем Timestamp из onloadWindowDate и onloadWindowTime. Если оба тоже null, сортировка поставит объект в конец.

          Метод combineDateAndTime:
              Объединяет дату (onloadWindowDate) и время (onloadWindowTime) в Timestamp.

          Сортировка:
              Используем Comparator с помощью метода Comparator.comparing, чтобы задать логику сортировки.
        */
//     for (Order order : orders) {
//        if(order.getTimeDelivery() == null) {
//           response.put("status", "105");
//           response.put("message", "Заявка " + order.getMarketNumber() + " не поставлена в слоты! Обратитесь к менеджеру " + order.getManager());
//           return response;
//        }
//     }
       
       orders.forEach(o-> System.out.println(o.getOnloadWindowDate() + "   " + o.getOnloadWindowTime()));
       orders.sort(Comparator.comparing(
             order -> Optional.ofNullable(order.getTimeDelivery())
                   .orElseGet(() -> combineDateAndTime(order.getOnloadWindowDate(), order.getOnloadWindowTime()))
       ));


       Order order = orders.stream().findFirst().get();
       //проверяем не отменена ли заявка
       if(order.getStatus() == 10) {
          response.put("status", "100");
          response.put("message", "Заявка " + order.getCounterparty() + " отменена!");
          return response;
       }
     //тут я записываю самый ранний на выгрузку
       

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
       route.setTypeLoad(jsonMainObject.get("typeLoad") != null ? jsonMainObject.get("typeLoad").toString() : null);
       route.setMethodLoad(jsonMainObject.get("methodLoad") != null ? jsonMainObject.get("methodLoad").toString() : null);
       route.setCustomer(order.getManager());
       route.setLogistInfo(thisUser.getSurname() +" " + thisUser.getName() + " " + thisUser.getPatronymic() + "; "+thisUser.getTelephone());
       route.setOnloadWindowDate(order.getOnloadWindowDate());
       route.setOnloadWindowTime(order.getOnloadWindowTime());
       if(order.getTimeDelivery() != null) {
    	   route.setDateUnloadPreviouslyStock(order.getTimeDelivery().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    	   route.setTimeUnloadPreviouslyStock(order.getTimeDelivery().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:SS")));
       }
       route.setLoadNumber(order.getLoadNumber());
       String tnvd="";
       route.setForReduction(jsonMainObject.get("forReduction") != null ? Boolean.parseBoolean(jsonMainObject.get("forReduction").toString()) : null);
       route.setStartPriceForReduction(jsonMainObject.get("startPriceForReduction") != null ? Integer.parseInt(jsonMainObject.get("startPriceForReduction").toString()) : null);
       route.setCurrencyForReduction(jsonMainObject.get("currencyForReduction") != null ? jsonMainObject.get("currencyForReduction").toString() : null);
       route.setOrders(orders.stream().collect(Collectors.toSet()));
//     route.setStartPrice(target.getStartPrice());
       route.setIdRoute(routeService.saveRouteAndReturnId(route));


//     orders.forEach(o -> {
//        List<Route> routes = o.getRoutes();
//        System.err.println(routes.size() + " - кол-во routes");
//        routes.add(route);
//        o.setRoutes(routes);
//        o.setStatus(30);
////          orderService.updateOrderFromStatus(order);
//        o.setLogist(thisUser.getSurname() +" " + thisUser.getName() + " " + thisUser.getPatronymic() + "; ");
//        o.setLogistTelephone(thisUser.getTelephone());
//        orderService.updateOrder(o);
//     });

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

       //тут доработать: выяснить какой из ордеров раньше выгружается - и внести эту инфу туда!
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
//     route.setRouteDirection(order.getCounterparty() + " - "
//           + routeHasShopsArray.get(routeHasShopsArray.size() - 1).getAddress().split("; ")[1] + " N"
//           + route.getIdRoute());

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
	 * старый метод получения слотов
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@Deprecated
	@GetMapping("/manager/getOrdersForSlots/{dateStart}&{dateEnd}")
	public Set<Order> getOrdersForSlots(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		Set<Order> res1 = orderService.getOrderByPeriodDelivery(dateStart, dateEnd).stream().filter(o -> o.getStatus() != 10)
				.collect(Collectors.toSet());
		return res1;
	}
	
		
	/**
	 * отдаёт заявки по дате только 5 статуса
	 * Основной метод для получения информации в слоты
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@GetMapping("/manager/getOrdersForSlots3/{dateStart}&{dateEnd}")
	public Set<Order> getOrdersForSlots3(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		java.util.Date t1 = new java.util.Date();
		Set<Order> res2 = orderService.getOrderByPeriodDeliveryAndSlots(dateStart, dateEnd).stream().filter(o -> o.getStatus() != 10)
				.collect(Collectors.toSet());
		java.util.Date t2 = new java.util.Date();		
		System.out.println("Time getOrdersForSlots2 : res3 = " + (t2.getTime()-t1.getTime()) + " ms ; size = " + res2.size());
		return res2;
	}
	
	@GetMapping("/manager/getOrdersForSlots4/{dateStart}&{dateEnd}")
	public Set<OrderDTO> getOrdersForSlots4(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		java.util.Date t1 = new java.util.Date();
		Set<OrderDTO> res2 = orderService.getOrderDTOByPeriodDeliveryAndSlots(dateStart, dateEnd).stream().filter(o -> o.getStatus() != 10)
				.collect(Collectors.toSet());
		java.util.Date t2 = new java.util.Date();		
		System.out.println("Time getOrdersForSlots4 : res4 = " + (t2.getTime()-t1.getTime()) + " ms ; size = " + res2.size());
		return res2;
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
			/*
			 * 1) если ордер в 20 или 40 статусе - удаляем!
			 * 2) если ордер в каком нибудь другом статусе - удаление запрещено - сначала отменяется маршрут!
			 */
			
			if(order.getRoutes().size()!=0) {
				Route route = order.getRoutes().stream().findFirst().get();
				if(!route.getStatusRoute().equals("5")) {
					response.put("status", "100");
					response.put("message", "Отмена заказа запрещена, т.к. заказ уже взят в работу специалистами отдела транспортной логистики. Обратитесь в указаныый отдел, для отмены мрашрута."); 
					return response;
				}
			}
			
			if(order.getStatus() == 10) {
				response.put("status", "100");
				response.put("message", "Заявка уже отменена. Обновите страницу."); 
				return response;
			}
			
			
			order.setChangeStatus(order.getChangeStatus() + "\nУдалил заявку: " + user.getSurname() + " " + user.getName() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:SS")));
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
				saveActionInFile(request, "resources/others/blackBox/slot", idOrder, order.getMarketNumber(), order.getNumStockDelivery(), order.getIdRamp(), null, order.getTimeDelivery(), null, user.getLogin(), "delete", null, order.getMarketContractType());
				Message messageWS = new Message("slot", user.getLogin(), null, "200", null, idOrder.toString(), "delete from table");
				messageWS.setPayload(order.toJsonForDelete());
				slotWebSocket.sendMessage(messageWS);
				//не создаём 5 статусы! они всё портят!
//				Order orderNew = new Order();
//				orderNew.setCounterparty(order.getCounterparty());
//				orderNew.setCargo(order.getCargo());
//				orderNew.setDateDelivery(order.getDateDelivery());
//				orderNew.setMarketNumber(order.getMarketNumber());
//				orderNew.setTimeUnload(order.getTimeUnload());
//				orderNew.setChangeStatus("Пересоздан про прошлому заказу № " + order.getIdOrder() +" в " + LocalDate.now());
//				orderNew.setNumStockDelivery(order.getNumStockDelivery());
//				orderNew.setPall(order.getPall());
//				orderNew.setSku(order.getSku());
//				orderNew.setMonoPall(order.getMonoPall());
//				orderNew.setMixPall(order.getMixPall());
//				orderNew.setStatus(5);
//				orderService.saveOrder(orderNew);
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
	 * Отдаёт все заявки по периоду создания заявки. pattern = "yyyy-MM-dd"
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@GetMapping("/manager/getOrders2/{dateStart}&{dateEnd}")
	public Set<OrderDTO> getListOrdersDAO(@PathVariable Date dateStart, @PathVariable Date dateEnd) {
		java.util.Date t1 = new java.util.Date();
		Set<OrderDTO> dtos = orderService.getOrderDTOByPeriodDelivery(dateStart, dateEnd).stream()
				.collect(Collectors.toSet());
		java.util.Date t2 = new java.util.Date();
		System.out.println("/manager/getOrders2 : " + (t2.getTime()-t1.getTime()) + " ms; " + dtos.size() + " items");
		return dtos;		
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
				.filter(o-> o.getIsInternalMovement().equals("true") || o.getWay().equals("АХО"))
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
	 * Метод создания заявки / сохранения заявки для АХО
	 * @param str
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/manager/addNewProcurementByMaintenance", method = RequestMethod.POST)
	public Map<String, String> addNewProcurementForAHO(@RequestBody String str, HttpServletRequest request)
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
//					order.setMarketInfo(jsonMainObject.get("marketInfo") != null ? jsonMainObject.get("marketInfo").toString() : null);
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
				order.setWay("АХО");
				order.setLoadNumber(jsonMainObject.get("loadNumber") != null ? jsonMainObject.get("loadNumber").toString() : null);
				order.setStacking(jsonMainObject.get("stacking").toString().equals("true") ? true : false);
				order.setIncoterms(jsonMainObject.get("incoterms") == null ? null : jsonMainObject.get("incoterms").toString());
				order.setIsInternalMovement("false");
//				order.setMarketInfo(jsonMainObject.get("marketInfo") != null ? jsonMainObject.get("marketInfo").toString() : null);
				
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
				order.setStatus(20);
				
				
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
				//отправляем на почту
				List<String> emails = propertiesUtils.getValuesByPartialKey(request.getServletContext(), "email.aho");
				String emailFrom = thisUser.geteMail();
				emails.add(emailFrom);
				
				String text = "Создана заявка №" + order.getIdOrder() + " " + order.getCounterparty() + " от менеджера " + order.getManager()+"\nНаправление: " + order.getWay();
				mailService.sendEmailToUsers(request, "Новая заявка", text, emails);
				return response;
		}
	
	/**
	 * Метод сохраняет заявку заявки создание заявки ИМПОРТ И РБ
	 * после этого метода ожидаются слоты
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
			order.setNumStockDelivery(jsonMainObject.get("numStockDelivery").toString()); // зачем?
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
				|| order.getManager().split(";")[1].trim().equals("KashickiyD@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("ProrovskayaM@dobronom.by")
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
		List <Address> addressesLoad = new ArrayList<Address>(); // лист с отдельными адресами загрузок
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
			if(address.getType().equals("Загрузка")) {
				addressesLoad.add(address);
			}
		}
		//тут просчитываем и записываем крайнюю точку загрузки 
		addressesLoad.sort(comparatorAddressForLastLoad);
		Timestamp dateTimeLastLoad = Timestamp.valueOf(LocalDateTime.of(addressesLoad.get(0).getDate().toLocalDate(), addressesLoad.get(0).getTime().toLocalTime()));
		order.setLastDatetimePointLoad(dateTimeLastLoad);
		orderService.updateOrder(order); // не лишнее ли. возмоно отдельным запросом
		
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
			order.setNumStockDelivery(jsonMainObject.get("numStockDelivery").toString()); // зачем?
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
				|| order.getManager().split(";")[1].trim().equals("KashickiyD@dobronom.by")
				|| order.getManager().split(";")[1].trim().equals("ProrovskayaM@dobronom.by")
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
		
		List <Address> addressesLoad = new ArrayList<Address>(); // лист с отдельными адресами загрузок
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
			if(address.getType().equals("Загрузка")) {
				addressesLoad.add(address);
			}

		}
		//тут просчитываем и записываем крайнюю точку загрузки 
		addressesLoad.sort(comparatorAddressForLastLoad);
		Timestamp dateTimeLastLoad = Timestamp.valueOf(LocalDateTime.of(addressesLoad.get(0).getDate().toLocalDate(), addressesLoad.get(0).getTime().toLocalTime()));
		order.setLastDatetimePointLoad(dateTimeLastLoad);
		orderService.updateOrder(order); // не лишнее ли. возмоно отдельным запросом
				
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
		String appPath = request.getServletContext().getRealPath("");
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
			mailService.sendSimpleEmail(appPath, "Регистрация в SpeedLogist",
					"Спасибо что зарегистрировались на товарно-транспортной бирже компании ЗАО \"Доброном\"\n"
							+ "Для того чтобы принять участие в торгах на бирже, необходимо прислать подписанный договор по адресу: 220073, г.Минск, пер.Загородный 1-й, 20-23;\n"
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
			mailService.sendSimpleEmail(appPath, "Регистрация в SpeedLogist",
					"Спасибо что зарегистрировались на товарно-транспортной бирже компании ЗАО \"Доброном\"\n"
							+ "Для того чтобы принять участие в торгах на бирже, необходимо прислать подписанный договор по адресу: 220073, г.Минск, пер.Загородный 1-й, 20-23;\n"
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
	 * @return
	 */
	@GetMapping("/manager/getAllCarrier")
	public Set<User> getAllCarrier() {
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
		if (getThisUserRole(target) == null) {
			return null;
		}
		if (target.isBlock() || target.getCheck().split("&").length > 1) {
			return null;
		} else {
//			Set<Route> routes = new HashSet<Route>();
//			routeService.getRouteListAsStatus("1", "1").stream() // это в отдельный запрос
//					.filter(r -> r.getComments() != null && r.getComments().equals("international"))
//					.filter(r-> !r.getDateLoadPreviously().isBefore(dateNow)) // не показывает тендеры со вчерашней датой загрузки
//					.forEach(r -> routes.add(r));
			Set<Route> routes = new HashSet<Route>(routeService.getActualRoute(Date.valueOf(dateNow)));
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

		User user = getThisUser();
		Role role = getThisUserRole(user);
		if (role != null && role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_TOPMANAGER") || role.getAuthority().equals("ROLE_MANAGER")) {
			return messagesList;
		} else {
			result = messagesList.stream().filter(m -> m.getFromUser().equals(user.getLogin()))
					.collect(Collectors.toList());
			return result;
		}

	}

	/**
	 * отдаёт сообщения, на которые есть предложения от данного юзера (сравнине по УНП) из кеша
	 * @return
	 */
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

	@TimedExecution
	@GetMapping("/mainchat/messagesList&{login}") // отдаёт лист непрочитанных сообщений из mainChat
	public List<Message> getNumMessageListByLogin(@PathVariable String login) {
		List<Message> messages = new ArrayList<Message>();
		messages.addAll(mainChat.messegeList);
		List<Message> result = new ArrayList<Message>();
		result = messageService.getListMessageByComment5Days(login);
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
		result = messageService.getListMessageByYNP5Days(login);		
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

	
	/**
	 * Пробразует 1737622336000 в формат Timestamp
	 * Вместо изменения адаптера Gson, мы можем перехватить строку JSON до десериализации и преобразовать поле datetimeConverted из 1737622336000 (Unix Timestamp) в строку ISO 8601.
	 * @param json
	 * @return
	 */
	public String preprocessJson(String json) {
        // Парсим JSON-объект
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        // Проверяем наличие поля datetimeConverted
        if (jsonObject.has("datetimeConverted")) {
            String timestamp = jsonObject.get("datetimeConverted").getAsString();
            try {
                // Преобразуем Timestamp в ISO 8601
                long timeInMillis = Long.parseLong(timestamp);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String isoDate = sdf.format(new Date(timeInMillis));

                // Заменяем значение в JSON
                jsonObject.addProperty("datetimeConverted", isoDate);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid timestamp: " + timestamp, e);
            }
        }

        // Возвращаем обновленную строку JSON
        return jsonObject.toString();
    }
	
	@PostMapping("/mainchat/massage/add") // сохраняет сообщение в бд, если есть сообщение, то не сохзраняет
	public JSONObject postSaveDBMessage(@RequestBody String str) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy; HH:mm:ss");		
		Message message = gson.fromJson(preprocessJson(str), Message.class);
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
		result = messageService.getListMessageByComment5Days(login);
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
		User user = userService.getUserByLoginV2(name);
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

	@Deprecated
	private Role getThisUserRole() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!name.equals("anonymousUser")) {
			Role role = userService.getUserByLogin(name).getRoles().stream().findFirst().get();
			return role;
		} else {
			return null;
		}
	}
	private Role getThisUserRole(User user) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!name.equals("anonymousUser")) {
			Role role = user.getRoles().stream().findFirst().get();
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
	public Double calcPallHashHsop(List<Shop> shops, Shop targetStock) {
		Double summ = 0.0;
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
	 * 
	 * <br>При наличии в ответе слова Error генерит ошибку.
	 * @param url
	 * @param payload
	 * @return
	 */
	@TimedExecution
	public String postRequest(String url, String payload) throws Exception{
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
            	//проверяем, есть ли в ответе слово Error
            	if(response.toString().contains("Error") && !response.toString().contains("\"Error\":99")) {
            		throw new MarketExceptionBuilder()
            			.setMessage(response.toString())
            			.setDescription("Ответ от маркета пришел, подключение есть, см саму ошибку")
            			.setResponseCode(getResponseCode+"")
            			.setRequest(payload)
            			.build();
            	}
                return response.toString();
            } else {
            	throw new MarketExceptionBuilder()
    			.setMessage("ResponseCode: " + getResponseCode)
    			.setDescription("Ответ от маркета (статус) не равег 200!")
    			.setResponseCode(getResponseCode+"")
    			.setRequest(payload)
    			.build();
            }
        } catch (IOException e) {
            System.out.println("MainRestController.postRequest: Подключение недоступно - 503");
            e.printStackTrace();
            throw new MarketExceptionBuilder()
			.setMessage(e.getStackTrace().toString())
			.setDescription("Общая ошибка метода postRequest. См. стектрейс")
			.setResponseCode("ResponseCode отсутствует")
			.setRequest(payload)
			.build();
        }
    }
	
	/**
	 * Метод для сохранения действия в файл.
	 * 
	 * @param idOrder Уникальный идентификатор заказа.
	 * @param marketNumber Номер в маркете.
	 * @param numStockDelivery Номер склада.
	 * @param idRampOld Старый идентификатор рампы.
	 * @param idRampNew Новый идентификатор рампы.
	 * @param timeDeliveryOld Время поставки (старое).
	 * @param timeDeliveryNew Время поставки (новое).
	 * @param loginManager Логин менеджера.
	 * @param action Действие.
	 * @param deliverySchedule Инфа о графике поставок.
	 */
	public void saveActionInFile(HttpServletRequest request, String localPath, Integer idOrder, String marketNumber, String numStockDelivery,
	                             Integer idRampOld, Integer idRampNew,
	                             Timestamp timeDeliveryOld, Timestamp timeDeliveryNew,
	                             String loginManager, String action, String deliverySchedule, String contractType) {
		String appPath = request.getServletContext().getRealPath("");
		String currentDir = appPath+localPath; //(resources/others/...)
//		System.out.println(currentDir);
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
//	        System.err.println(appPath + "resources/others/blackBox/slot/");
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
	    	                    "loginManager" + ";" + "action" + ";" + "timeActionString;ip;deliverySchedule;contractType");
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
	                    loginManager + ";" + action + ";" + timeActionString+";"+ip+";"+deliverySchedule+";"+contractType);
	            writer.newLine();
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	/**
	 * метод для сохранения историй изменений
	 * не используется
	 * @param request
	 * @param localPath
	 * @param scheduleOld
	 * @param schedule
	 * @param loginManager
	 */
	public void saveActionInFileSchedule(HttpServletRequest request, String localPath, Schedule scheduleOld, Schedule schedule, String loginManager) {
		String appPath = request.getServletContext().getRealPath("");
		String currentDir = appPath+localPath; //(resources/others/...)
//		System.out.println(currentDir);
	    try {	
	    	
	        // Получаем текущее время и форматируем его
	        LocalDateTime timeAction = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String timeActionString = timeAction.format(formatter);

	        // Получаем текущую дату для имени файла
	        LocalDate currentTime = LocalDate.now();
	        String currentTimeString = String.valueOf(currentTime);
	        String fileName = currentDir + "/контроль_изменений.txt";
	        
	        System.out.println(fileName);
	        
	        //проверка директории
	        File fileTest= new File(appPath + "resources/others/blackBox/");
	        if (!fileTest.exists()) {
	            fileTest.mkdir();
	            File fileTest2= new File(appPath + "resources/others/blackBox/schedule/");
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
	    	            writerHeader.write("Объект до изменения --- кто изменил --- новый объект --- дата и время изменения");
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
	            writer.write(scheduleOld + " --- " + loginManager + " --- " + schedule + " --- " +timeActionString);
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
