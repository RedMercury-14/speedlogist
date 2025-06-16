package by.base.main.controller.ajax;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import by.base.main.dto.yard.AcceptanceQualityFoodCardDTO;
import by.base.main.model.yard.AcceptanceFoodQuality;
import by.base.main.model.yard.AcceptanceQualityFoodCard;
import by.base.main.model.yard.AcceptanceQualityFoodCardImageUrl;
import by.base.main.model.yard.DefectBase;
import by.base.main.service.yardService.AcceptanceFoodQualityService;
import by.base.main.service.yardService.AcceptanceQualityFoodCardImageUrlService;
import by.base.main.service.yardService.AcceptanceQualityFoodCardService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.*;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;

import by.base.main.model.ClientRequest;
import by.base.main.model.MapResponse;
import by.base.main.model.Message;
import by.base.main.model.Order;
import by.base.main.model.Shop;
import by.base.main.model.User;
import by.base.main.service.OrderService;
import by.base.main.service.TelegramChatQualityService;
import by.base.main.service.UserService;
import by.base.main.service.util.SocketClient;
import by.base.main.util.SlotWebSocket;
import by.base.main.util.GraphHopper.RoutingMachine;
import by.base.main.util.bots.TelegramBotRoutingTEST;
import by.base.main.util.bots.TelegrammBotQuantityYard;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
	
	@Autowired
	private TelegrammBotQuantityYard telegrammBotQuantityYard;
	
	@Autowired
	private TelegramChatQualityService telegramChatQualityService;
	
	@Autowired
	private TelegramBotRoutingTEST telegramBotRoutingTEST;
	
	@Autowired
	private RoutingMachine routingMachine;
	
	@Value("${telegramm.bot.run}")
	private boolean isRunTelegrammBot;

	private static final String staticToken = "3d075c53-4fd3-41c3-89fc-a5e5c4a0b25b";
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	@Value("${yard.web.urlPart}")
	public String urlPart;
	

	/**
	 * Метод который запрашивае фото у двора и отдаёт клиенту
	 * @param id
	 * @return
	 */
	@GetMapping("/files/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable("id") String id) {
        // URL на внешний сервер
        String externalUrl = urlPart + id;

        // Выполняем GET-запрос
        ResponseEntity<byte[]> response = restTemplate.exchange(
                externalUrl,
                HttpMethod.GET,
                null,
                byte[].class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            ByteArrayResource resource = new ByteArrayResource(response.getBody());

            // Возвращаем фото клиенту
            return ResponseEntity.ok()
                    .contentLength(response.getBody().length)
                    .contentType(response.getHeaders().getContentType() != null ?
                            response.getHeaders().getContentType() :
                            MediaType.IMAGE_JPEG)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
	
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
		response.put("user", SecurityContextHolder.getContext().getAuthentication().getName());
		return response;		
	}
	

    private void processDefectGroup(String title,
                                           Collection<? extends DefectBase> defects,
                                           boolean isImport,
                                           boolean withPC,
                                           double sampleSize,
                                           StringBuilder builder,
                                           StringBuilder finalMessage,
                                           boolean applyPC) {

        final double pcThreshold = 10.0;
        final double percentageFactor = 100.0;
        final double pcFactorBeforeThreshold = isImport ? 160.0 : 140.0;
        final double pcFactorAfterThreshold = 200.0;

        double totalWeight = 0.0;
        double totalPercentage = 0.0;
        double totalPercentageWithPC = 0.0;

        builder.append(title).append(":\n");
        finalMessage.append(title).append(":\n");

        for (DefectBase defect : defects) {
            double weight = Optional.ofNullable(defect.getWeight()).orElse(0.0);
            totalWeight += weight;

            double percentage = sampleSize > 0 ? (weight / sampleSize) * percentageFactor : 0.0;
            totalPercentage += percentage;

            double percentageWithPC = 0.0;
            if (applyPC && withPC && sampleSize > 0) {
                percentageWithPC = percentage <= pcThreshold
                        ? (weight / sampleSize) * pcFactorBeforeThreshold
                        : (weight / sampleSize) * pcFactorAfterThreshold;
                totalPercentageWithPC += percentageWithPC;
            }

//            builder.append("  - ")
//                    .append(defect.getDescription()).append(" | вес: ").append(weight)
//                    .append(" | %: ").append(String.format("%.2f", percentage));

//            if (applyPC) {
//                builder.append(" | % c ПК: ").append(String.format("%.2f", percentageWithPC));
//            }
//            builder.append("\n");
        }

//        builder.append("  Итого вес: ").append(roundNumber(totalWeight, 100)).append("\n");
        builder.append("  Итого %: ").append(String.format("%.2f", totalPercentage)).append("%").append("\n");
        finalMessage.append("  Итого %: ").append(String.format("%.2f", totalPercentage)).append("%").append("\n");
        if (applyPC) {
            builder.append("  Итого % с ПК: ").append(String.format("%.2f", totalPercentageWithPC)).append("%").append("\n");
            finalMessage.append("  Итого % с ПК: ").append(String.format("%.2f", totalPercentageWithPC)).append("%").append("\n");
        }
    }

    private static double roundNumber(double value, int factor) {
        return Math.round(value * factor) / (double) factor;
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
//			listResult.add(new MapResponse(resultPoints, path.getDistance(), path.getTime(), shopPoints.get(index)[0],
//					shopPoints.get(index)[1]));
			listResult.add(new MapResponse(null, path.getDistance(), path.getTime(), shopPoints.get(index)[0],
			shopPoints.get(index)[1])); // важно. Здесь нет точек!!!
		}
		return listResult;
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
				acceptanceQualityFoodCardService.getAllAcceptanceQualityFoodCard(idAcceptanceFoodQuality,request); // этим методом можно получить урлы к фото

		return ResponseEntity.ok(acceptanceQualityFoodCardDTOList);
	}
	
	/**
	 * Реализация подтверждения карточки товара.
	 * Записывает сразу в базу данных
	 * <b></b>
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws TelegramApiException 
	 */
	@PostMapping("/aproofQualityFoodCard")
	public Map<String, Object> setActStatus(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException, TelegramApiException {
		Map<String, Object> response = new HashMap<>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		User user = getThisUser();
		
		Integer idCard = Integer.parseInt(jsonMainObject.get("idAcceptanceQualityFoodCard").toString());
		String comment = jsonMainObject.get("comment") != null && !jsonMainObject.get("comment").toString().isEmpty() ? jsonMainObject.get("comment").toString() : null;
		String percent = jsonMainObject.get("managerPercent") != null && !jsonMainObject.get("managerPercent").toString().isEmpty() ? jsonMainObject.get("managerPercent").toString() : null;
		Integer status = jsonMainObject.get("status") != null ? Integer.parseInt(jsonMainObject.get("status").toString()) : null;
		
		AcceptanceQualityFoodCard foodCard = acceptanceQualityFoodCardService.getByIdAcceptanceQualityFoodCard(idCard.longValue());
		AcceptanceFoodQuality acceptanceFoodQuality = foodCard.getAcceptanceFoodQuality();
		
		foodCard.setCardStatus(status);
		foodCard.setCommentAproof(comment);	
		foodCard.setManagerPercent(percent);
		
		foodCard.setLoginManagerAproof(user.getLogin());
		foodCard.setIdManagerAproof(user.getIdUser());
		foodCard.setFullnameManagerAproof(user.getSurname() +" "+ user.getName());
		
		
		
		acceptanceQualityFoodCardService.update(foodCard);		
		
		/*
		 * Интересует только доп выборка и переборка
		 */
		//тут отпраляем сообщение в бот.
		String statusStr = null;
		switch (status) {
		case 150:
			statusStr = "Принимаем.";
			break;
		case 152:
			statusStr = "Оправляем на переборку";
			
			acceptanceFoodQuality.setQualityProcessStatus(70); //70 статус доп работа
			acceptanceFoodQuality.setDateStopProcess(null); 
			acceptanceFoodQualityService.update(acceptanceFoodQuality);
			
			AcceptanceQualityFoodCard foodCard2 = new AcceptanceQualityFoodCard();
			foodCard2.setAcceptanceFoodQuality(acceptanceFoodQuality);
			foodCard2.setProductName(foodCard.getProductName());
			foodCard2.setProductType(foodCard.getProductType());
			foodCard2.setClassType(foodCard.getClassType());
			foodCard2.setNumberOfBrands(foodCard.getNumberOfBrands());
			foodCard2.setQualityOfProductPackaging(foodCard.getQualityOfProductPackaging());
			foodCard2.setThermogram(foodCard.getThermogram());
			foodCard2.setBodyTemp(foodCard.getBodyTemp());
			foodCard2.setFruitTemp(foodCard.getFruitTemp());
			foodCard2.setAppearanceEvaluation(foodCard.getAppearanceEvaluation());
			foodCard2.setTasteQuality(foodCard.getTasteQuality());
//			foodCard2.setLoginManagerAproof(user.getSurname() + " " + user.getName());			
			foodCard2.setIdAcceptanceQualityFoodCard(null);
			foodCard2.setIdMotherCard(foodCard.getIdMotherCard() != null ? foodCard.getIdMotherCard() : foodCard.getIdAcceptanceQualityFoodCard());
			foodCard2.setCardStatus(10);
			foodCard2.setType("Переборка");
			foodCard2.setDateTimeCreate(Timestamp.valueOf(LocalDateTime.now()));
			foodCard2.setDateCard(foodCard.getDateCard());
			foodCard2.setInternalDefectsQualityCardList(null);
			foodCard2.setLightDefectsQualityCardList(null);
			foodCard2.setTotalDefectQualityCardList(null);
			foodCard2.setAcceptanceQualityFoodCardImageUrls(null);
			acceptanceQualityFoodCardService.save(foodCard2);
			break;
		case 154:
			statusStr = "Принимаем с процентом брака.";
			break;
		case 156:
			statusStr = "Принимаем под реализацию";
			break;
		case 158:
			statusStr = "Требуется дополнительная выборка (своими силами)";
			
			acceptanceFoodQuality.setQualityProcessStatus(70); //70 статус доп работа
			acceptanceFoodQuality.setDateStopProcess(null); 
			acceptanceFoodQualityService.update(acceptanceFoodQuality);
			
			AcceptanceQualityFoodCard foodCard3 = new AcceptanceQualityFoodCard();
			foodCard3.setAcceptanceFoodQuality(acceptanceFoodQuality);
			foodCard3.setProductName(foodCard.getProductName());
			foodCard3.setProductType(foodCard.getProductType());
			foodCard3.setClassType(foodCard.getClassType());
			foodCard3.setNumberOfBrands(foodCard.getNumberOfBrands());
			foodCard3.setQualityOfProductPackaging(foodCard.getQualityOfProductPackaging());
			foodCard3.setThermogram(foodCard.getThermogram());
			foodCard3.setBodyTemp(foodCard.getBodyTemp());
			foodCard3.setFruitTemp(foodCard.getFruitTemp());
			foodCard3.setAppearanceEvaluation(foodCard.getAppearanceEvaluation());
			foodCard3.setTasteQuality(foodCard.getTasteQuality());
//			foodCard2.setLoginManagerAproof(user.getSurname() + " " + user.getName());			
			foodCard3.setIdAcceptanceQualityFoodCard(null);
			foodCard3.setIdMotherCard(foodCard.getIdMotherCard() != null ? foodCard.getIdMotherCard() : foodCard.getIdAcceptanceQualityFoodCard());
			foodCard3.setCardStatus(10);
			foodCard3.setType("Дополнительная выборка");
			foodCard3.setDateTimeCreate(Timestamp.valueOf(LocalDateTime.now()));
			foodCard3.setDateCard(foodCard.getDateCard());
			foodCard3.setInternalDefectsQualityCardList(null);
			foodCard3.setLightDefectsQualityCardList(null);
			foodCard3.setTotalDefectQualityCardList(null);
			foodCard3.setAcceptanceQualityFoodCardImageUrls(null);
			acceptanceQualityFoodCardService.save(foodCard3);
			break;
		case 160:
			statusStr = "Принимаем с дополнительной выборкой (силами поставщика)";
			break;
		case 140:
			statusStr = "Не принимаем";
			break;
		default:
			statusStr = "Ошибка чтения статуса";
			break;
		}		
		
		StringBuilder message = new StringBuilder();
		message.append("Поставщик: " + acceptanceFoodQuality.getAcceptance().getFirmNameAccept() + ";  авто: " 
				+ acceptanceFoodQuality.getAcceptance().getCarNumber() + "; продукт: "
				+ foodCard.getProductName() + "; Карточка товара №" + foodCard.getIdAcceptanceQualityFoodCard()
				+ "\n");
		message.append("<b>Статус по карточке: " + statusStr);
		if((status == 150 || status == 152 || status == 156 || status == 140) && comment != null) message.append(" Комментарий: " + comment);
		if(status == 154 && comment != null) message.append("Информация по процентам: " + percent + "\nКомментарий: " + comment);		
		if(status == 154) message.append("Информация по процентам: " + percent);		
		message.append("</b>\n");
		message.append("Принял решение: " + user.getSurname() +" "+ user.getName() + ". Тел: " + user.getTelephone());
		
		
		if(isRunTelegrammBot) {
			telegrammBotQuantityYard.sendMessageInBot(message.toString(), null);				
		}else {
			telegramBotRoutingTEST.sendMessageInBot(message.toString(), null);	
		}
		
		response.put("status", "200");
		response.put("object", foodCard);
		response.put("telegrammMessage", message.toString());
		return response;
	}
	
	/**
	 * Главынй метод отправки сообщения телеграмм боту
	 * <b></b>
	 * @param request
	 * @param idCar
	 * @return
	 * @throws TelegramApiException
	 */
	@GetMapping("/acceptanceQualityBot/{id}")
	public Map<String, String> getAcceptanceQualityBot(HttpServletRequest request,
			@PathVariable("id") String idCar) throws TelegramApiException {
		Map<String, String> responce = new HashMap<String, String>();
		List<AcceptanceQualityFoodCard> acceptanceQualityFoodCardList =	acceptanceQualityFoodCardService.getFoodCardByIdFoodQuality(Long.parseLong(idCar));
		
		List<Long> chatIds = telegramChatQualityService.getChatIdList().stream().map(s-> s.getChatId().longValue()).collect(Collectors.toList()); // список chatId--
		
		String finalName = null;
		String finalCar = null;
		StringBuilder finalProductCard = new StringBuilder();
		List<String> tagsAll = new ArrayList<String>();
		
		for (AcceptanceQualityFoodCard acceptanceQualityFoodCard : acceptanceQualityFoodCardList) {
			List<String> tags = new ArrayList<String>();
			List<String> photoIds = new ArrayList<String>();
			for (AcceptanceQualityFoodCardImageUrl acceptanceQualityFoodCardImageUrl : acceptanceQualityFoodCard.getAcceptanceQualityFoodCardImageUrls()) {
				System.out.println(acceptanceQualityFoodCard.getIdAcceptanceQualityFoodCard() +" --> "+acceptanceQualityFoodCardImageUrl);	
				photoIds.add(acceptanceQualityFoodCardImageUrl.getIdAcceptanceQualityFoodCardImageUrl().toString());	
			}
			
			if(finalName == null) {
				finalName = acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getFirmNameAccept();
				finalCar = acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getCarNumber();
				finalProductCard.append("Поставщик: " + finalName + ";  авто: " 
					+ finalCar + "; \n");				
			}
			
			
			StringBuilder message = new StringBuilder();
			message.append("Поставщик: " + finalName + ";  авто: " 
					+ finalCar + "; продукт: "
					+ acceptanceQualityFoodCard.getProductName() + "; Карточка товара №" + acceptanceQualityFoodCard.getIdAcceptanceQualityFoodCard()
					+ "\n");
			message.append("Внутренние дефекты: \n	  Итого: " + acceptanceQualityFoodCard.getTotalInternalDefectPercentage() + "%\n");
			message.append("Некондиция: \n	  Итого: " + acceptanceQualityFoodCard.getTotalLightDefectPercentage() + "%;\n	  Итого: "+acceptanceQualityFoodCard.getTotalLightDefectWeight() + "кг\n");
			message.append("Брак/гниль: \n	  Итого: " + acceptanceQualityFoodCard.getTotalDefectPercentage() + "%;\n	  Итого: "+acceptanceQualityFoodCard.getTotalDefectWeight() + "кг;\n	  Итого % с ПК: "+acceptanceQualityFoodCard.getTotalDefectPercentageWithPC() + "кг;\n");
			if(acceptanceQualityFoodCard.getCardInfo() != null) message.append("Примечания: " + acceptanceQualityFoodCard.getCardInfo().trim() + "\n");
			
			tags.add(acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getFirmNameAccept());
			if(!tagsAll.contains(acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getFirmNameAccept())) {				
				tagsAll.add(acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getFirmNameAccept());
			}
			tags.add(acceptanceQualityFoodCard.getProductName());
			if(!tagsAll.contains(acceptanceQualityFoodCard.getProductName())) {				
				tagsAll.add(acceptanceQualityFoodCard.getProductName());
			}	
			
			tags.add(finalCar);
			if(!tagsAll.contains(finalCar)) {
				tagsAll.add(finalCar);
			}
			
			boolean isImport = Optional.ofNullable(acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getIsImport()).orElse(false);
            boolean withPC = acceptanceQualityFoodCard.getUnit() != null && !"шт".equalsIgnoreCase(acceptanceQualityFoodCard.getUnit());
            double sampleSize = Optional.ofNullable(acceptanceQualityFoodCard.getSampleSize()).orElse(0.0);

            finalProductCard.append("<b>Карточка товара №" + acceptanceQualityFoodCard.getIdAcceptanceQualityFoodCard() +
            		" -> " + acceptanceQualityFoodCard.getProductName() + ":</b>\n");            
            // Обработка трёх типов дефектов
//            processDefectGroup("Внутренние дефекты", acceptanceQualityFoodCard.getInternalDefectsQualityCardList(), isImport, withPC, sampleSize, message, finalProductCard, false);
//            processDefectGroup("Некондиция", acceptanceQualityFoodCard.getLightDefectsQualityCardList(), isImport, withPC, sampleSize, message, finalProductCard, false);
//            processDefectGroup("Брак / гниль", acceptanceQualityFoodCard.getTotalDefectQualityCardList(), isImport, withPC, sampleSize, message, finalProductCard, true);
            finalProductCard.append("Внутренние дефекты: \n	  Итого: " + acceptanceQualityFoodCard.getTotalInternalDefectPercentage() + "%\n");
            finalProductCard.append("Некондиция: \n	  Итого: " + acceptanceQualityFoodCard.getTotalLightDefectPercentage() + "%;\n	  Итого: "+acceptanceQualityFoodCard.getTotalLightDefectWeight() + "кг\n");
            finalProductCard.append("Брак/гниль: \n	  Итого: " + acceptanceQualityFoodCard.getTotalDefectPercentage() + "%;\n	  Итого: "+acceptanceQualityFoodCard.getTotalDefectWeight() + "кг;\n	  Итого % с ПК: "+acceptanceQualityFoodCard.getTotalDefectPercentageWithPC() + "кг;\n");
            if(acceptanceQualityFoodCard.getCardInfo() != null) finalProductCard.append("Примечания: " + acceptanceQualityFoodCard.getCardInfo().trim() + "\n");
            finalProductCard.append("\n");
			if(isRunTelegrammBot) {
				telegrammBotQuantityYard.sendMessageWithPhotos(chatIds, message.toString(), photoIds, tags);				
			}else {
				telegramBotRoutingTEST.sendMessageWithPhotos(chatIds, message.toString(), photoIds, tags);		
			}
		}
		
		if(isRunTelegrammBot) {
			telegrammBotQuantityYard.sendMessageInBot(finalProductCard.toString(), tagsAll);				
		}else {
			telegramBotRoutingTEST.sendMessageInBot(finalProductCard.toString(), tagsAll);	
		}
		responce.put("status", "200");		
		responce.put("message", "ообщение отправлено в телеграмм бот");	

		return responce;
	}


//	/**
//	 * Загрузка файла по его идентификатору.
//	 *
//	 * @author Lesha
//	 * @param idFile Идентификатор файла.
//	 * @return Файл в формате Resource, если найден, иначе 404 Not Found.
//	 * @throws IOException В случае ошибки при работе с файлом.
//	 */
//	@GetMapping("files/{idFile}")
//	public ResponseEntity<Resource> getFile(@PathVariable Long idFile) throws IOException {
//		Resource resource = acceptanceQualityFoodCardImageUrlService.getFile(idFile);
//
//		if (resource == null || !resource.exists() || !resource.isReadable()) {
//			return ResponseEntity.notFound().build();
//		}
//
//		return ResponseEntity.ok()
//				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
//				.contentType(MediaType.IMAGE_JPEG)
//				.body(resource);
//	}

	 
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
