package by.base.main.controller.ajax;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import by.base.main.aspect.TimedExecution;
import by.base.main.dto.AuthRequest;
import by.base.main.dto.AuthResponse;
import by.base.main.dto.MarketDataFor398Request;
import by.base.main.dto.MarketDataFor398Responce;
import by.base.main.dto.MarketPacketDto;
import by.base.main.dto.MarketRequestDto;
import by.base.main.security.JwtTokenProvider;
import by.base.main.service.ScheduleService;

@RestController
@RequestMapping(path = "fusion", produces = "application/json")
public class FusionController {
	
	private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
				@Override
				public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					return context.serialize(src.getTime());  // Сериализация даты в миллисекундах
				}
            })
            .create();
    
    @Autowired
    private ScheduleService scheduleService; 
    
    @Autowired
    private MainRestController mainRestController; 
    
    public FusionController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

	@GetMapping("/echo")
    public Map<String, Object> getTastList(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "200");
		responseMap.put("message", "echo");
		responseMap.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
		return responseMap;
    }
	
	@PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
		
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenProvider.generateToken(userDetails.getUsername());

        return new AuthResponse(token);
    }
	
	@GetMapping("/schedule/getListTOAll")
	public Map<String, Object> getListDeliveryScheduleTO(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("status", "200");
		response.put("body", scheduleService.getSchedulesListTOAll());
		return response;		
	}
	
	@GetMapping("/help")
	public Map<String, Object> getHelp(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("status", "200");
		response.put("/schedule/getListTOAll", "Метод возвращает все графики поставок и временные и удалённые и т.д. (status: 0 - удалён; 10 - создан, ожидает подтверждения; 20 - в работе)");
		response.put("/398/{stock}&{from}&{to}", "Метод возвращает 398 отчёт для расчётов (stock - склады, через запятую не более 50; from - дата начала выборки в SQL формате (2024-03-24); to - дата окончания выборки в SQL формате (2024-03-24))");
		return response;		
	}
	
	@TimedExecution
	@GetMapping("/398/{stock}&{from}&{to}")
	public Map<String, Object> get398AndMoreStock(HttpServletRequest request, @PathVariable String stock,
			 @PathVariable String from,
			 @PathVariable String to) throws ParseException {
		Integer maxShopCoint = 50; // максимальное кол-во магазинов в запросе
		Map<String, Object> response = new HashMap<>();

		//сначала определяыем кол-во магазов и делим их на массивы запросов
		 String [] mass = stock.split(",");
		 Integer shopAllCoint = mass.length;

		 System.out.println("Всего магазинов: " + shopAllCoint);
		 
		 if(shopAllCoint > maxShopCoint) {
			 response.put("status", "100");
			 response.put("message", "Превышено максимальное число магазинов в запросе");
			 response.put("description", "Магазино в запросе = " + shopAllCoint+"; Максимальное значение = " + maxShopCoint);
			 return response;
		 }
		 
		 String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport398\", \"Data\": {\"DateFrom\": \""+from+"\", \"DateTo\": \""+to+"\", \"WarehouseId\": "
					+ "["+stock+"],"
					+ " \"WhatBase\": [11,12]}}}";
			try {
				mainRestController.checkJWT(mainRestController.marketUrl);
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
//			String warehouseId = jsonMainObjectTarget.get("WarehouseId") == null ? null : jsonMainObjectTarget.get("WarehouseId").toString();
//			String whatBase = jsonMainObjectTarget.get("WhatBase") == null ? null : jsonMainObjectTarget.get("WhatBase").toString();

			MarketDataFor398Request for398Request = new MarketDataFor398Request(dateForm, dateTo, warehouseId, whatBase);
			MarketPacketDto marketPacketDto = new MarketPacketDto(mainRestController.marketJWT, "SpeedLogist.GetReport398", mainRestController.serviceNumber, for398Request);
			MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);

			String marketOrder2;
			try {
				marketOrder2 = mainRestController.postRequest(mainRestController.marketUrl, gson.toJson(requestDto));
			} catch (Exception e) {
				e.printStackTrace();
				response.put("status", "100");
				response.put("exception", e.toString());
				response.put("message", "Ошибка запроса к Маркету");
				response.put("info", "Ошибка запроса к Маркету");
				return response;
			}
//			System.out.println(gson.toJson(requestDto));

			JSONObject jsonTable = (JSONObject) parser.parse(marketOrder2);	
			
			List<MarketDataFor398Responce> dataFor398Responces = new ArrayList<MarketDataFor398Responce>();
			JSONArray jsonArray = (JSONArray) parser.parse(jsonTable.get("Table").toString());
			for (Object object : jsonArray) {
				MarketDataFor398Responce dataFor398Responce = gson.fromJson(object.toString(), MarketDataFor398Responce.class);
				dataFor398Responces.add(dataFor398Responce);
			}
			

		response.put("status", "200");
		response.put("payload", dataFor398Responces);
		return response;
	}
	
	
}
