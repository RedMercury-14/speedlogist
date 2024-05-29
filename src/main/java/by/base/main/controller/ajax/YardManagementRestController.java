package by.base.main.controller.ajax;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import by.base.main.model.Address;
import by.base.main.model.ClientRequest;
import by.base.main.model.Message;
import by.base.main.model.Order;
import by.base.main.model.User;
import by.base.main.service.OrderService;
import by.base.main.service.UserService;
import by.base.main.service.util.SocketClient;
import by.base.main.util.SlotWebSocket;

@RestController
@RequestMapping(path = "tsd", produces = "application/json")
public class YardManagementRestController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	CsrfTokenRepository csrfTokenRepository;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	private SlotWebSocket slotWebSocket;
	
	private static final String staticToken = "3d075c53-4fd3-41c3-89fc-a5e5c4a0b25b";

	@RequestMapping("/echo")
	public Map<String, String> getEcho (HttpServletRequest request) {
		String text = "Speedlogist";
		Map<String, String> response = new HashMap<String, String>();
		response.put("status", "200");
		response.put("message", text);
		response.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy; hh:MM:ss")));
		return response;		
	}
	
	@RequestMapping("/server")
	public Map<String, String> getEchoServer(HttpServletRequest request) throws UnknownHostException, ClassNotFoundException, IOException {
		ClientRequest clientRequest = new ClientRequest("echo", "this is data");
		Object responce = SocketClient.send(request, clientRequest);
		System.out.println(responce);
		Map<String, String> response = new HashMap<String, String>();
		response.put("status", "200");
		response.put("message", responce.toString());
		return response;		
	}
	
	@GetMapping("/SetOrderStatus/{marketNum}&{status}&{timeStart}&{timeEnd}&{pall}&{weight}")
	public Map<String, String> getAddressForImport(HttpServletRequest request, @PathVariable String marketNum, @PathVariable String status, 
			@PathVariable String timeStart, @PathVariable String timeEnd, @PathVariable String pall, @PathVariable String weight) throws ParseException {
		Map<String, String> response = new HashMap<String, String>();
		String headerFlag = request.getHeader("Flag");
		
		if(!staticToken.equals(headerFlag)) {
			response.put("status", "403");
			response.put("message", "Доступ запрещен");
			return response;
		}
		
		String idOrder = marketNum != null && !marketNum.equals("null") ? marketNum : null;
		if(idOrder == null) {
			response.put("status", "100");
			response.put("message", "Ошибка. Не пришел marketNum");
			return response;
		}
		Order order = orderService.getOrderByMarketNumber(idOrder);
		if(order == null) {
			response.put("status", "100");
			response.put("message", "Заказ с номером из Маркета " + idOrder + " не найден в базе данных SL");
			return response;
		}
		
		order.setStatusYard(status != null && !status.equals("null") ? Integer.parseInt(status) : null);
		order.setUnloadStartYard(timeStart != null && !timeStart.equals("null") ? new Timestamp(Long.parseLong(timeStart)) : null);
		order.setUnloadFinishYard(timeEnd != null && !timeEnd.equals("null") ? new Timestamp(Long.parseLong(timeEnd)) : null);
		order.setPallFactYard(pall != null && !pall.equals("null")? Integer.parseInt(pall) : null);
		order.setWeightFactYard(weight != null && !weight.equals("null") ? Double.parseDouble(weight) : null);
		orderService.updateOrder(order);
		//тправляем сообщение в WS
		Message message = new Message("yard", null, "200", order.toJsonForYard(), idOrder.toString(), "changeStatusYard");
		slotWebSocket.sendMessage(message);	
		response.put("status", "200");
		response.put("object", order.toJsonForYard());
		return response;		
	}
	

	
	@GetMapping("/loadPlan")
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
	
	@GetMapping("/loadPlanNow")
	public List<Order> loadPlanNow(HttpServletRequest request) {
		LocalDate dateNow = LocalDate.now();
		
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
 
	/**
	 * Метод отдаёт csrf токен для пост методов от двора
	 * важно что он принимает ключ от двора
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping("/csrf")
	public Map<String, String> downdoadIncotermsGet (HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		Map<String, String> result = new HashMap<String, String>();		
		
		String headerFlag = request.getHeader("Flag");
		
		System.out.println(request.getRemoteAddr());
		System.out.println(request.getRemotePort());
		System.out.println(request.getRemoteHost());
		System.out.println();
		
		if(!staticToken.equals(headerFlag)) {
			result.put("status", "403");
			result.put("message", "Доступ запрещен");
			return result;
		}
		
		CsrfToken token = csrfTokenRepository.generateToken(request);
		csrfTokenRepository.saveToken(token, request, response);
		
		result.put("status", "200");
		result.put("token", token.getToken());
		result.put("JSESSIONID", session.getId());
		
		return result;		
	}
	
	// округляем числа до 2-х знаков после запятой
	private static double roundВouble(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}
	
	public static String removeCharAt(String s, int pos) {
		return s.substring(0, pos) + s.substring(pos + 1); // Возвращаем подстроку s, которая начиная с нулевой позиции
															// переданной строки (0) и заканчивается позицией символа
															// (pos), который мы хотим удалить, соединенную с другой
															// подстрокой s, которая начинается со следующей позиции
															// после позиции символа (pos + 1), который мы удаляем, и
															// заканчивается последней позицией переданной строки.
	}
	
	private User getThisUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByLogin(name);
		return user;
	}
	
	 private String postRequest(String url, String payload, HttpServletRequest request, HttpServletResponse responseOne, HttpSession session) {
		 
		 CsrfToken token = csrfTokenRepository.generateToken(request);
			csrfTokenRepository.saveToken(token, request, responseOne);
			
//			result.put("status", "200");
//			result.put("token", token.getToken());
//			result.put("JSESSIONID", session.getId());
		 
		 
	        try {
	            URL urlForPost = new URL(url);
	            HttpURLConnection connection = (HttpURLConnection) urlForPost.openConnection();
	            connection.setRequestMethod("POST");
//	            connection.setRequestProperty("Content-Type", "application/json");
	            connection.setRequestProperty("X-Csrf-Token", token.getToken());
	            connection.setRequestProperty("Cookie", "JSESSIONID=" + session.getId());
	            connection.setDoOutput(true);
	           
	            byte[] postData = payload.getBytes(StandardCharsets.UTF_8);
	            
	            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
	                wr.write(postData);
	            }
	            
	            int getResponseCode = connection.getResponseCode();
	            System.out.println("POST Response Code: " + getResponseCode);

	            if (getResponseCode == HttpURLConnection.HTTP_OK) {
	            	StringBuilder response = new StringBuilder();
	            	try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
	                    String inputLine;	 
	                    while ((inputLine = in.readLine()) != null) {
	                    	response.append(inputLine.trim());
	                    }
	                    in.close();
	                }	           
//	            	System.out.println(connection.getContentType());
	                return response.toString();
	            } else {
	                return null;
	            }
	        } catch (IOException e) {
	            System.out.println("Подключение недоступно");
	            return "error";
	        }
	    }
}
