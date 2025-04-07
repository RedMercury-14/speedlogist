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
import by.base.main.model.yard.DefectBase;
import by.base.main.model.yard.TtnIn;
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

import by.base.main.model.Act;
import by.base.main.model.Address;
import by.base.main.model.ClientRequest;
import by.base.main.model.Message;
import by.base.main.model.Order;
import by.base.main.model.Route;
import by.base.main.model.User;
import by.base.main.service.OrderService;
import by.base.main.service.TelegramChatQualityService;
import by.base.main.service.UserService;
import by.base.main.service.util.SocketClient;
import by.base.main.util.SlotWebSocket;
import by.base.main.util.bots.TelegramBotRoutingTEST;
import by.base.main.util.bots.TelegrammBotQuantityYard;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
		return response;		
	}
	
	@RequestMapping("/test/{id}")
	public Map<String, Object> test(@PathVariable String id){
		
		Map<String, Object> responce = new HashMap<String, Object>();
		List<AcceptanceQualityFoodCard> cards =	acceptanceQualityFoodCardService.getFoodCardByIdFoodQuality(Long.parseLong(id));
		
		StringBuilder resultBuilder = new StringBuilder();
		
		

        for (AcceptanceQualityFoodCard card : cards) {
            StringBuilder cardBuilder = new StringBuilder();
            cardBuilder.append("Карта ID: ").append(card.getIdAcceptanceQualityFoodCard()).append("\n");
            cardBuilder.append("Продукт: ").append(card.getProductName()).append("\n");

            boolean isImport = Optional.ofNullable(card.getAcceptanceFoodQuality().getAcceptance().getIsImport()).orElse(false);
            boolean withPC = card.getUnit() != null && !"шт".equalsIgnoreCase(card.getUnit());
            double sampleSize = Optional.ofNullable(card.getSampleSize()).orElse(0.0);

            // Обработка трёх типов дефектов
            processDefectGroup("Внутренние дефекты", card.getInternalDefectsQualityCardList(), isImport, withPC, sampleSize, cardBuilder, false);
            processDefectGroup("Некондиция", card.getLightDefectsQualityCardList(), isImport, withPC, sampleSize, cardBuilder, false);
            processDefectGroup("Брак / гниль", card.getTotalDefectQualityCardList(), isImport, withPC, sampleSize, cardBuilder, true);

            resultBuilder.append(cardBuilder).append("\n----------------------------\n");
        }		
        responce.put("object", resultBuilder.toString());
        System.out.println(resultBuilder.toString());
		return responce;		
	}
	

    private void processDefectGroup(String title,
                                           Collection<? extends DefectBase> defects,
                                           boolean isImport,
                                           boolean withPC,
                                           double sampleSize,
                                           StringBuilder builder,
                                           boolean applyPC) {

        final double pcThreshold = 10.0;
        final double percentageFactor = 100.0;
        final double pcFactorBeforeThreshold = isImport ? 160.0 : 140.0;
        final double pcFactorAfterThreshold = 200.0;

        double totalWeight = 0.0;
        double totalPercentage = 0.0;
        double totalPercentageWithPC = 0.0;

        builder.append(title).append(":\n");

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

        builder.append("  Итого вес: ").append(roundNumber(totalWeight, 100)).append("\n");
        builder.append("  Итого %: ").append(String.format("%.2f", totalPercentage)).append("%").append("\n");
        if (applyPC) {
            builder.append("  Итого % с ПК: ").append(String.format("%.2f", totalPercentageWithPC)).append("%").append("\n");
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
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@PostMapping("/aproofQualityFoodCard")
	public Map<String, Object> setActStatus(HttpServletRequest request, @RequestBody String str) throws ParseException, IOException {
		Map<String, Object> response = new HashMap<>();
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(str);
		
		Integer idCard = Integer.parseInt(jsonMainObject.get("idAcceptanceQualityFoodCard").toString());
		String comment = jsonMainObject.get("comment") != null && !jsonMainObject.get("comment").toString().isEmpty() ? jsonMainObject.get("comment").toString() : null;
		String percent = jsonMainObject.get("managerPercent") != null && !jsonMainObject.get("managerPercent").toString().isEmpty() ? jsonMainObject.get("managerPercent").toString() : null;
		Integer status = jsonMainObject.get("status") != null ? Integer.parseInt(jsonMainObject.get("status").toString()) : null;
		
		AcceptanceQualityFoodCard foodCard = acceptanceQualityFoodCardService.getByIdAcceptanceQualityFoodCard(idCard.longValue());
		
		foodCard.setCardStatus(status);
		foodCard.setCommentAproof(comment);	
		
		acceptanceQualityFoodCardService.save(foodCard);
		
		response.put("status", "200");
		response.put("object", foodCard);
		return response;
	}
	
	@GetMapping("/acceptanceQualityBot/{id}")
	public Map<String, String> getAcceptanceQualityBot(HttpServletRequest request,
			@PathVariable("id") String idCar) {
		Map<String, String> responce = new HashMap<String, String>();
		List<AcceptanceQualityFoodCard> acceptanceQualityFoodCardList =	acceptanceQualityFoodCardService.getFoodCardByIdFoodQuality(Long.parseLong(idCar));
		
		List<Long> chatIds = telegramChatQualityService.getChatIdList().stream().map(s-> s.getChatId().longValue()).collect(Collectors.toList()); // список chatId--
		List<String> tags = new ArrayList<String>();
		for (AcceptanceQualityFoodCard acceptanceQualityFoodCard : acceptanceQualityFoodCardList) {
			List<String> photoIds = new ArrayList<String>();
			for (AcceptanceQualityFoodCardImageUrl acceptanceQualityFoodCardImageUrl : acceptanceQualityFoodCard.getAcceptanceQualityFoodCardImageUrls()) {
				System.out.println(acceptanceQualityFoodCard.getIdAcceptanceQualityFoodCard() +" --> "+acceptanceQualityFoodCardImageUrl);	
				photoIds.add(acceptanceQualityFoodCardImageUrl.getIdAcceptanceQualityFoodCardImageUrl().toString());	
			}
			
			
			StringBuilder message = new StringBuilder();
			message.append("Поставщик: " + acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getFirmNameAccept() + ";  авто: " 
					+ acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getCarNumber() + "; продукт: "
					+ acceptanceQualityFoodCard.getProductName() + "; Карточка товара №" + acceptanceQualityFoodCard.getIdAcceptanceQualityFoodCard()
					+ "\n");
//			if(acceptanceQualityFoodCard.getTasteQuality() != null) message.append("Вкусовые качества: " + acceptanceQualityFoodCard.getTasteQuality().trim() + "\n");
			if(acceptanceQualityFoodCard.getCardInfo() != null) message.append("Примечания: " + acceptanceQualityFoodCard.getCardInfo().trim() + "\n");
//			if(acceptanceQualityFoodCard.getCaliber() != null) message.append("Калибр: " + acceptanceQualityFoodCard.getCaliber().trim() + "\n");
//			if(acceptanceQualityFoodCard.getMaturityLevel() != null) message.append("Уровень зрелости: " + acceptanceQualityFoodCard.getMaturityLevel().trim() + "\n");
//			if(acceptanceQualityFoodCard.getAppearanceDefects() != null) message.append("Внешние дефекты: " + acceptanceQualityFoodCard.getAppearanceDefects().trim() + "\n");
			
			
			if(!tags.contains(acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getFirmNameAccept())) {
				tags.add(acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getFirmNameAccept());
			}
			if(!tags.contains(acceptanceQualityFoodCard.getProductName())) {
				tags.add(acceptanceQualityFoodCard.getProductName());
			}	
			
			boolean isImport = Optional.ofNullable(acceptanceQualityFoodCard.getAcceptanceFoodQuality().getAcceptance().getIsImport()).orElse(false);
            boolean withPC = acceptanceQualityFoodCard.getUnit() != null && !"шт".equalsIgnoreCase(acceptanceQualityFoodCard.getUnit());
            double sampleSize = Optional.ofNullable(acceptanceQualityFoodCard.getSampleSize()).orElse(0.0);

            // Обработка трёх типов дефектов
            processDefectGroup("Внутренние дефекты", acceptanceQualityFoodCard.getInternalDefectsQualityCardList(), isImport, withPC, sampleSize, message, false);
            processDefectGroup("Некондиция", acceptanceQualityFoodCard.getLightDefectsQualityCardList(), isImport, withPC, sampleSize, message, false);
            processDefectGroup("Брак / гниль", acceptanceQualityFoodCard.getTotalDefectQualityCardList(), isImport, withPC, sampleSize, message, true);
			
			if(isRunTelegrammBot) {
				telegrammBotQuantityYard.sendMessageWithPhotos(chatIds, message.toString(), photoIds, tags);				
			}else {
				System.err.println(message.toString());
				telegramBotRoutingTEST.sendMessageWithPhotos(chatIds, message.toString(), photoIds, tags);		
				System.out.println("В телегу полетело");
			}
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
