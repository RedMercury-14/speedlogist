package by.base.main.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import by.base.main.aspect.TimedExecution;
import by.base.main.controller.ajax.MainRestController;
import by.base.main.dto.MarketDataFor330Request;
import by.base.main.dto.MarketDataFor330Responce;
import by.base.main.dto.MarketPacketDto;
import by.base.main.dto.MarketRequestDto;
import by.base.main.dto.ReportRow;
import by.base.main.model.Order;
import by.base.main.model.Route;
import by.base.main.model.Truck;
import by.base.main.service.OrderService;
import by.base.main.service.RouteService;
import by.base.main.service.ServiceException;
import by.base.main.service.TruckService;
import by.base.main.service.util.MailService;
import by.base.main.service.util.POIExcel;

//@Controller
@RestController
@RequestMapping(path = "file")
public class MainFileController {
	
	@Autowired
	private TruckService truckService;
	
	@Autowired
	private POIExcel poiExcel;
	
	@Autowired
	private RouteService routeService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private MainRestController mainRestController;
	
	@Autowired
	private OrderService orderService;
	
	private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
				@Override
				public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					return context.serialize(src.getTime());  // Сериализация даты в миллисекундах
				}
            })
            .create();
	
    @RequestMapping(value="/echo", method=RequestMethod.GET)
    public @ResponseBody String handleFileUpload(HttpServletRequest request) throws IOException{
    	System.out.println("MainFileController ECHO");
    	 return "echo";
    }
	
    @RequestMapping(value="/sendFileAgree", method=RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public @ResponseBody String handleFileUpload(@RequestParam(value = "agreePersonalData", required = false) MultipartFile mulFile2,  HttpServletRequest request) throws IOException, ServletException{
    	String name = request.getHeader("ynp");
    	saveFile(mulFile2, request, name);
    	return "Good!";
    }   
    
    @RequestMapping(value="/sendContract", method=RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public @ResponseBody String sendContract(@RequestParam(value = "contract", required = false) MultipartFile mulFile2,  HttpServletRequest request) throws IOException, ServletException, ServiceException{
    	String name = request.getHeader("companyName");
    	File file = poiExcel.getFileByMultipart(mulFile2);
    	new Thread(new Runnable() {			
			@Override
			public void run() {
				mailService.sendEmailWhithFileToUser(request, "Договор от перевозчика "+name, "", file, "ArtyuhevichO@dobronom.by");    //GrushevskiyD@dobronom.by	 
			}
		}).start();
    	
    	return "Good!";
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
		List<MarketDataFor330Responce> dataList330 = new ArrayList<MarketDataFor330Responce>();
		List<ReportRow> reportRows = new ArrayList<ReportRow>();
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
		JSONArray goodsIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("GoodsId").toString());
		
		String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
		String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
		Object[] warehouseId = warehouseIdArray.toArray();
		Object[] goodsId = goodsIdArray.toArray();
		
		MarketDataFor330Request for330Request = new MarketDataFor330Request(dateForm, dateTo, warehouseId, goodsId);		
		MarketPacketDto marketPacketDto = new MarketPacketDto(mainRestController.marketJWT, "SpeedLogist.GetReport330", mainRestController.serviceNumber, for330Request);		
		MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);
		
		String marketOrder2 = mainRestController.postRequest(mainRestController.marketUrl, gson.toJson(requestDto));
		System.out.println(gson.toJson(requestDto));
		
		if(marketOrder2.equals("503")) { // означает что связь с маркетом потеряна
			//в этом случае проверяем бд
			System.err.println("Связь с маркетом потеряна");
			response.put("status", "503");
			response.put("payload responce", marketOrder2);
			response.put("message", "Связь с маркетом потеряна");
			return response;
			
		}else{//если есть связь с маркетом
			JSONObject jsonResponceMainObject = (JSONObject) parser.parse(marketOrder2);
			JSONArray jsonResponceTable = (JSONArray) parser.parse(jsonResponceMainObject.get("Table").toString());			
			for (Object obj : jsonResponceTable) {
	        	dataList330.add(new MarketDataFor330Responce(obj.toString())); // парсин json засунул в конструктор
	        }
			
		}
		
//		for (MarketDataFor330Responce object : responces) {
//			System.out.println(object);
//		}
		
		// Получаем номера заказов
		List<String> uniqueOrderBuyGroupIds = dataList330.stream()
	            .map(MarketDataFor330Responce::getOrderBuyGroupId) // Получаем значения
	            .filter(id -> id != null) // Убираем null значения
	            .map(String::valueOf) // Преобразуем Long в String
	            .distinct() // Убираем дубликаты
	            .collect(Collectors.toList()); // Преобразуем обратно в список
		
		//получаем заказы по списку        
		Map<String, Order> orders = orderService.getOrdersByListMarketNumber(uniqueOrderBuyGroupIds);
		
		//подгатавливаем строку (собираем все нужные столбцы)
		for (MarketDataFor330Responce data330 : dataList330) {
			ReportRow reportRow = new ReportRow();
			reportRow.setProductName(data330.getGoodsName());
			reportRow.setProductCode(data330.getGoodsId());
			
			String period = Date.valueOf(from).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " + Date.valueOf(to).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			reportRow.setPeriodOrderDelivery(period);
			reportRow.setOrderedUnitsORL(null); //сколько заказано ОРЛ	
			Order order = orders.get(data330.getOrderBuyGroupId().toString());
			if(order == null) {
				System.err.println("Заказа с номером " + data330.getOrderBuyGroupId() + " не найдено!");
			}
			Map<Long, Double> productHasOrder = order.getOrderLinesMap();
//			System.out.println("---> Хочу взять: " + data330.getGoodsId() + " из заказа " + data330.getOrderBuyGroupId());
			if(!productHasOrder.containsKey(data330.getGoodsId())) {
				System.err.println("Отсутствует товар " + data330.getGoodsId() + " ("+ data330.getGoodsName()+ ") в заказе " + data330.getOrderBuyGroupId());
			}
			Integer orderProductHasOrderManager = productHasOrder.get(data330.getGoodsId()).intValue();
			reportRow.setOrderedUnitsManager(orderProductHasOrderManager); // сколько заказано менеджером
			reportRow.setMarketNumber(data330.getOrderBuyGroupId().toString());
			reportRow.setDateStart(Date.valueOf(from));
			reportRow.setDateFinish(Date.valueOf(to));
			reportRow.setCounterpartyName(data330.getContractorNameShort());
			reportRow.setAcceptedUnits(data330.getQuantity().intValue());
			reportRows.add(reportRow);						
		}
		
		//записываем строки в ексель
		String appPath = request.getServletContext().getRealPath("");
        String folderPath = appPath + "resources/others/report330.xlsx";
        
        try {
			poiExcel.generateExcelReport(reportRows, folderPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
		return null;
	}
    
    /**
	 * Метод отвечает за скачивание документа инструкции для графика поставок для ТО
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/delivery-schedule-to/downdoad/instruction-trading-objects")
	public void downdoadIncotermsInsuranceGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		//File file = new File(appPath + "resources/others/Speedlogist.apk");
		response.setHeader("content-disposition", "attachment;filename="+"instruction-trading-objects.docx");
		response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		FileInputStream in = null;
		OutputStream out = null;
		try {
			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
			in = new FileInputStream(appPath + "resources/others/docs/instruction-trading-objects.docx");
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
	}
	
	@RequestMapping("/orl/download/zip398")
	public void downloadInstructionArchive(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    String appPath = request.getServletContext().getRealPath("");
	    String targetDirectoryPath = appPath + "resources/others/398/";
	    String archivePath = targetDirectoryPath + "398.zip";

	    File targetDirectory = new File(targetDirectoryPath);
	    File archiveFile = new File(archivePath);

	    // Если архив не существует, создаем его
	    if (!archiveFile.exists()) {
	        try (FileOutputStream fos = new FileOutputStream(archiveFile);
	             ZipOutputStream zos = new ZipOutputStream(fos)) {

	            // Получаем все файлы из целевой директории
	            File[] filesToInclude = targetDirectory.listFiles();
	            if (filesToInclude != null) {
	                for (File file : filesToInclude) {
	                    if (file.isFile() && !file.getName().equals("398.zip")) { // Исключаем сам архив
	                        try (FileInputStream fis = new FileInputStream(file)) {
	                            // Добавляем файл в архив
	                            ZipEntry zipEntry = new ZipEntry(file.getName());
	                            zos.putNextEntry(zipEntry);

	                            byte[] buffer = new byte[1024];
	                            int len;
	                            while ((len = fis.read(buffer)) > 0) {
	                                zos.write(buffer, 0, len);
	                            }
	                            zos.closeEntry();
	                        }
	                    }
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new IOException("Error creating the archive file");
	        }
	    }

	    // Устанавливаем заголовки для скачивания
	    response.setHeader("content-disposition", "attachment;filename=398.zip");
	    response.setContentType("application/zip");

	    // Передаем архив в поток
	    try (FileInputStream in = new FileInputStream(archiveFile);
	         OutputStream out = response.getOutputStream()) {

	        byte[] buffer = new byte[1024];
	        int len;
	        while ((len = in.read(buffer)) > 0) {
	            out.write(buffer, 0, len);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new IOException("Error downloading the archive file");
	    }
	}


	
	private File convertMultiPartToFile(MultipartFile file, HttpServletRequest request ) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
	    File convFile = new File(appPath + "resources/others/"+file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream( convFile );
	    fos.write( file.getBytes() );
	    fos.close();
	    return convFile;
	}
	
	
	private void saveFile(MultipartFile file, HttpServletRequest request, String name) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		File directory = new File(appPath + "resources/others/fileAgree/");
		if (directory.mkdir()) {
            System.out.println("папка создана");
        }
		System.out.println(appPath);
		String[] findType = file.getOriginalFilename().split("\\.");
		String type = "." + findType[findType.length-1];
	    File convFile = new File(appPath + "resources/others/fileAgree/"+name+type);
	    FileOutputStream fos = new FileOutputStream( convFile );
	    fos.write( file.getBytes() );
	    fos.close();
//	    return convFile;
	}

}
