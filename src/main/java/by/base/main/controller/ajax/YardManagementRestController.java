package by.base.main.controller.ajax;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import by.base.main.dto.yard.AcceptanceQualityFoodCardDTO;
import by.base.main.model.yard.AcceptanceFoodQuality;
import by.base.main.model.yard.AcceptanceQualityFoodCard;
import by.base.main.model.yard.AcceptanceQualityFoodCardImageUrl;
import by.base.main.model.yard.TtnIn;
import by.base.main.service.yardService.AcceptanceFoodQualityService;
import by.base.main.service.yardService.AcceptanceQualityFoodCardImageUrlService;
import by.base.main.service.yardService.AcceptanceQualityFoodCardService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.*;

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
	@Qualifier("csrfTokenRepository") // <-- Указываем нужный бин
	CsrfTokenRepository csrfTokenRepository;
	
	@Autowired
	OrderService orderService;

	@Autowired
	private SlotWebSocket slotWebSocket;

	@Autowired
	private AcceptanceFoodQualityService acceptanceFoodQualityService;

	@Autowired
	private AcceptanceQualityFoodCardService acceptanceQualityFoodCardService;

	@Autowired
	private AcceptanceQualityFoodCardImageUrlService acceptanceQualityFoodCardImageUrlService;

	private static final String staticToken = "3d075c53-4fd3-41c3-89fc-a5e5c4a0b25b";


	
	/*
	 * тут данные которые определяют приоритетную рампу и время
	 */
	private static final Integer idRumpPriority1700 = 170001;
	private static final Integer idRumpPriority1800 = 180001;
	private static final LocalTime startTimePriority = LocalTime.of(9, 0, 0);
	private static final LocalTime finishTimePriority = LocalTime.of(20, 0, 0);

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
	
	/**
	 * Метод который принимает информацию о статусе авто во дворе
	 * @param request
	 * @param marketNum
	 * @param status
	 * @param timeStart
	 * @param timeEnd
	 * @param pall
	 * @param weight
	 * @return
	 * @throws ParseException
	 */
	@GetMapping("/SetOrderStatus/{marketNum}&{status}&{timeStart}&{timeEnd}&{pall}&{weight}&{timeArrival}&{timeRegistration}")
	public Map<String, String> getAddressForImport(HttpServletRequest request, @PathVariable String marketNum, @PathVariable String status, 
			@PathVariable String timeStart, @PathVariable String timeEnd, @PathVariable String pall, @PathVariable String weight, @PathVariable String timeArrival, @PathVariable String timeRegistration) throws ParseException {
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
		order.setArrivalFactYard(timeArrival != null && !timeArrival.equals("null") ? new Timestamp(Long.parseLong(timeArrival)) : null);
		order.setRegistrationFactYard(timeRegistration != null && !timeRegistration.equals("null") ? new Timestamp(Long.parseLong(timeRegistration)) : null);
		orderService.updateOrder(order);
		//тправляем сообщение в WS
		Message message = new Message("slot", "yard", null, "200", order.toJsonForYard(), idOrder.toString(), "changeStatusYard");
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
			o.setMailInfo(null);
			o.setSlotInfo(null);
			//доп логика где указываю для двора приоритетные машины
			if(o.getIdRamp().intValue() == idRumpPriority1700.intValue() || o.getIdRamp().intValue() == idRumpPriority1800.intValue()) {
				if(o.getTimeDelivery().toLocalDateTime().toLocalTime().isAfter(startTimePriority) && o.getTimeDelivery().toLocalDateTime().toLocalTime().isBefore(finishTimePriority)) {
					o.setIsPriority(true);
				}
			}
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
			o.setMailInfo(null);
			o.setSlotInfo(null);
			//доп логика где указываю для двора приоритетные машины
			if(o.getIdRamp().intValue() == idRumpPriority1700.intValue() || o.getIdRamp().intValue() == idRumpPriority1800.intValue()) {
				if(o.getTimeDelivery().toLocalDateTime().toLocalTime().isAfter(startTimePriority) && o.getTimeDelivery().toLocalDateTime().toLocalTime().isBefore(finishTimePriority)) {
					o.setIsPriority(true);
				}
			}
			result.add(o);
		});
		return orders;
	}


	/**
	 * Получение всех записей контроля качества с неподтвержденным статусом.
	 *
	 * @author Lesha
	 * @return Список объектов AcceptanceFoodQuality с статусом 0.
	 */
	@GetMapping("/unprocessedAcceptanceQuality")
	public ResponseEntity<List<AcceptanceFoodQuality>> getAllAcceptanceFoodQualities() {
		List<AcceptanceFoodQuality> allData = acceptanceFoodQualityService.getAllByStatus(0);
		return ResponseEntity.ok(allData);
	}


	/**
	 * Получение всех записей контроля качества в процессе обработки.
	 *
	 * @author Lesha
	 * @return Список объектов AcceptanceFoodQuality с статусами 10 и 50.
	 */
	@GetMapping("/inProcessAcceptanceQuality")
	public ResponseEntity<List<AcceptanceFoodQuality>> getInProcessAcceptanceFoodQualities() {
		List<AcceptanceFoodQuality> allData = acceptanceFoodQualityService.getAllByStatuses(Arrays.asList(10, 50));
		return ResponseEntity.ok(allData);
	}

	/**
	 * Получение всех закрытых записей контроля качества за указанный период.
	 *
	 * @author Lesha
	 * @param startDate Начальная дата периода (в формате ISO).
	 * @param endDate Конечная дата периода (в формате ISO).
	 * @return Список объектов AcceptanceFoodQuality с статусом 100.
	 */
	@GetMapping("/closedAcceptanceQuality")
	public ResponseEntity<List<AcceptanceFoodQuality>> getClosedAcceptanceFoodQualities(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		List<AcceptanceFoodQuality> allData = acceptanceFoodQualityService
				.getAllByStatusAndDates(100, startDate, endDate);

		return ResponseEntity.ok(allData);
	}


	/**
	 * Получение всех карточек контроля качества по идентификатору качества
	 *
	 * @author Lesha
	 * @param idAcceptanceFoodQuality Идентификатор контроля качества питания.
	 * @return Список объектов AcceptanceQualityFoodCardDTO.
	 */
	@GetMapping("/getAllAcceptanceQualityFoodCard")
	public ResponseEntity<List<AcceptanceQualityFoodCardDTO>> getAllAcceptanceQualityFoodCard(HttpServletRequest request,
			@RequestParam("idAcceptanceFoodQuality") Long idAcceptanceFoodQuality) {

		List<AcceptanceQualityFoodCardDTO> acceptanceQualityFoodCardDTOList =
				acceptanceQualityFoodCardService.getAllAcceptanceQualityFoodCard(idAcceptanceFoodQuality,request);

		return ResponseEntity.ok(acceptanceQualityFoodCardDTOList);
	}


	/**
	 * Загрузка файла по его идентификатору.
	 *
	 * @author Lesha
	 * @param idFile Идентификатор файла.
	 * @return Файл в формате Resource, если найден, иначе 404 Not Found.
	 * @throws IOException В случае ошибки при работе с файлом.
	 */
	@GetMapping("files/{idFile}")
	public ResponseEntity<Resource> getFile(@PathVariable Long idFile) throws IOException {
		Resource resource = acceptanceQualityFoodCardImageUrlService.getFile(idFile);

		if (resource == null || !resource.exists() || !resource.isReadable()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
				.contentType(MediaType.IMAGE_JPEG)
				.body(resource);
	}

	 
//	@GetMapping("/files/{idFile}")
//	public ResponseEntity<Resource> getFile(@PathVariable Long idFile) throws IOException {
//		AcceptanceQualityFoodCardImageUrl acceptanceQualityFoodCardImageUrl = acceptanceQualityFoodCardImageUrlService.getByIdAcceptanceQualityFoodCardImageUrl(idFile);
//
//		if (acceptanceQualityFoodCardImageUrl == null) {
//			return ResponseEntity.notFound().build();
//		}
//
//		String urlPart = acceptanceQualityFoodCardImageUrl.getUrl();
//
//		if (urlPart == null || urlPart.isEmpty()) {
//			return ResponseEntity.notFound().build();
//		}
//
//		Path filePath = Paths.get(ROOT_DIRECTORY + urlPart);
//
//		if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
//			return ResponseEntity.notFound().build();
//		}
//
//		// Информация о файле до сжатия
//		long originalSize = Files.size(filePath);
//
//		BufferedImage image = ImageIO.read(filePath.toFile());
//		if (image != null) {
//		}
//
//		ByteArrayOutputStream compressedOutputStream = new ByteArrayOutputStream();
//		Resource resource;
//
//		// Сжимаем, если размер больше 1 МБ (1_048_576 байт)
//		if (originalSize > 1_048_576 && image != null) {
//			try {
//				ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
//				ImageOutputStream ios = ImageIO.createImageOutputStream(compressedOutputStream);
//				writer.setOutput(ios);
//
//				ImageWriteParam param = writer.getDefaultWriteParam();
//				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//				param.setCompressionQuality(0.7f); // 70% качество
//
//				writer.write(null, new IIOImage(image, null, null), param);
//
//				writer.dispose();
//				ios.close();
//
//				byte[] compressedBytes = compressedOutputStream.toByteArray();
//				resource = new ByteArrayResource(compressedBytes);
//
//			} catch (IOException e) {
//				resource = new UrlResource(filePath.toUri());
//			}
//		} else {
//			resource = new UrlResource(filePath.toUri());
//		}
//
//		if (!resource.exists() || !resource.isReadable()) {
//			return ResponseEntity.notFound().build();
//		}
//
//		return ResponseEntity.ok()
//				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
//				.contentType(MediaType.IMAGE_JPEG)
//				.body(resource);
//	}





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
