package by.base.main.service.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import by.base.main.aspect.TimedExecution;
import by.base.main.controller.ajax.MainRestController;
import by.base.main.dto.MarketDataArrayForRequestDto;
import by.base.main.dto.MarketDataFor398Request;
import by.base.main.dto.MarketDataForLoginDto;
import by.base.main.dto.MarketErrorDto;
import by.base.main.dto.MarketPacketDto;
import by.base.main.dto.MarketRequestDto;
import by.base.main.dto.MarketTableDto;
import by.base.main.dto.OrderBuyGroupDTO;
import by.base.main.dto.OrderCheckPalletsDto;
import by.base.main.model.Order;
import by.base.main.model.OrderLine;
import by.base.main.model.OrderProduct;
import by.base.main.model.Product;
import by.base.main.model.Schedule;
import by.base.main.model.Task;
import by.base.main.model.User;
import by.base.main.service.OrderProductService;
import by.base.main.service.OrderService;
import by.base.main.service.ProductService;
import by.base.main.service.ScheduleService;
import by.base.main.service.TaskService;
import by.base.main.service.UserService;
import by.base.main.util.MainChat;

/**
 * Главный метод с заданиями. 
 * отдельно прописывается в applicationContext. 
 */
public class ScheduledTask {

	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private POIExcel poiExcel;
	
	@Autowired
	private PropertiesUtils propertiesUtils;
	
	@Autowired
	private MailService mailService;
		
	@Autowired
    private ServletContext servletContext;
	
	@Autowired
	private MainChat mainChat;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderProductService orderProductService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private MainRestController mainRestController;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderCreater orderCreater;
	
	@Value("${rat.run}")
	public boolean isRatRun;
	
	private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
				@Override
				public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					return context.serialize(src.getTime());  // Сериализация даты в миллисекундах
				}
            })
            .create();
	
    @Scheduled(cron = "0 30 06 * * ?") // каждый день в 06:30
    public void sendSchedulesHasORL() {
    	System.out.println("Start --- sendSchedulesHasORL");
    	
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
		
		try {
			poiExcel.exportToExcelScheduleListRC(scheduleService.getSchedulesByStock(1200).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1200);
			poiExcel.exportToExcelScheduleListRC(scheduleService.getSchedulesByStock(1250).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1250);
			poiExcel.exportToExcelScheduleListRC(scheduleService.getSchedulesByStock(1700).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1700);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Ошибка формирование EXCEL");
		}
		
//		response.setHeader("content-disposition", "attachment;filename="+fileName+".xlsx");
		List<File> files = new ArrayList<File>();
		files.add(new File(appPath + "resources/others/" + fileName1200));
		files.add(new File(appPath + "resources/others/" + fileName1250));
		files.add(new File(appPath + "resources/others/" + fileName1700));
		
		
		mailService.sendEmailWithFilesToUsers(servletContext, "Графики поставок на РЦ от " + currentTimeString, "Автоматическая отправка", files, emails);
		System.out.println("Finish --- sendSchedulesHasORL");
    }
    
    @Scheduled(cron = "0 00 05 * * ?") // каждый день в 05:00
    public void sendProblemsWithOrders() {
    	XSSFWorkbook book = new XSSFWorkbook();
		XSSFSheet sheet = book.createSheet("Несоответствия");
		XSSFSheet checkSheet = book.createSheet("Проверка");
		XSSFCellStyle cellStyle = book.createCellStyle();

		String[] headers = {
				"Код товара", "Наименование товара", "Заказ (остальные склады)", "Заказано (остальные склады)", "Заказ 1700", "Заказано для 1700",
				"Заказ 1800", "Заказано для 1800", "Увеличенный заказ 1700", "Увеличенный заказ 1800"
		};


		String[] checkHeaders = {
				"Код товара", "название товара", "дата", "Количество"
		};



		// Создаем строку заголовков
		Row headerRow = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
		}

		Row checkHeaderRow = checkSheet.createRow(0);
		for (int i = 0; i < checkHeaders.length; i++) {
			Cell cell = checkHeaderRow.createCell(i);
			cell.setCellValue(checkHeaders[i]);
		}

		boolean isSheetEmpty = true;
		int rowNum = 1;
		//TODO Ira test

		LocalDate currentTime = LocalDate.now().minusDays(1);
		LocalDate currentTimeDayBefore = currentTime.minusDays(1);
		String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		String currentTimeDayBeforeString = currentTimeDayBefore.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		Date dateForSearch = Date.valueOf(currentTime);
		Date dateForSearchBefore = Date.valueOf(currentTime.minusDays(1));

		String fileName = "Несоответствия потребностей и слотов за " + currentTimeString + ".xlsx";
		String appPath = servletContext.getRealPath("/");

		List<OrderProduct> orderProducts = orderProductService.getOrderProductListHasDate(dateForSearchBefore);

		List<Long> orderProductsIds = orderProducts.stream().map(p -> p.getCodeProduct().longValue()).collect(Collectors.toList());

		long amount1700 = orderProducts.stream().filter(p -> p.getQuantity1700() != null).count();
		long amount1800 = orderProducts.stream().filter(p -> p.getQuantity1800() != null).count();
		long amountOthers = orderProducts.stream().filter(p -> p.getQuantity() != null).count();

		List<Order> orders = orderService.getOrderByFirstLoadSlotAndDateOrderOrl(dateForSearchBefore, dateForSearch, dateForSearch);

		int amountOrdered1700 = 0;
		int amountOrdered1800 = 0;
		int amountOrderedOthers = 0;

		for (OrderProduct orderProduct : orderProducts) {
			double quantityFromOrders = 0;
			double quantityFromOrders1700 = 0;
			double quantityFromOrders1800 = 0;
			for (Order order : orders) {
				String numStock = getTrueStockWithNullCheck(order);
				for (OrderLine orderLine : order.getOrderLines()) {

					if (orderProduct.getCodeProduct().longValue() == orderLine.getGoodsId()) {
						double quantity = orderLine.getQuantityOrder() == null ? 0 : orderLine.getQuantityOrder();
						if (numStock.equals("1700")) {
							quantityFromOrders1700 += quantity;
						} else if (numStock.equals("1800")) {
							quantityFromOrders1800 += quantity;

						} else if (numStock.equals("1200") || numStock.equals("1250") || numStock.equals("1100")) {
							quantityFromOrders += quantity;
						}
					}

				}
			}
			int summaryQuantityOtherStocks = orderProduct.getQuantity() == null ? 0 : orderProduct.getQuantity();
			int summaryQuantityOther1700 = orderProduct.getQuantity1700() == null ? 0 : orderProduct.getQuantity1700();
			int summaryQuantityOther1800 = orderProduct.getQuantity1800() == null ? 0 : orderProduct.getQuantity1800();
			List<Order> ordersForInfo = orderService.getOrdersByGoodId(orderProduct.getCodeProduct().longValue()); //.stream().filter(o -> !o.getOrderLines().isEmpty()).collect(Collectors.toList());

			String counterpartyName = "";
			String category = "";

			if (!ordersForInfo.isEmpty()){
				counterpartyName = ordersForInfo.get(0).getCounterparty();
				category = ordersForInfo.get(0).getOrderLines().stream().findFirst().get().getGoodsGroupName();
			}

			if (quantityFromOrders < summaryQuantityOtherStocks || quantityFromOrders1700 < summaryQuantityOther1700 || quantityFromOrders1800 < summaryQuantityOther1800) {
				isSheetEmpty = false;
				poiExcel.fillExcelAboutNeeds(orderProduct, quantityFromOrders, quantityFromOrders1700, quantityFromOrders1800, counterpartyName, category,  sheet, rowNum);
				rowNum++;
			}


			if (quantityFromOrders < summaryQuantityOtherStocks) {
				amountOrderedOthers++;
			}
			if (quantityFromOrders1700 < summaryQuantityOther1700) {
				amountOrdered1700++;
			}
			if (quantityFromOrders1800 < summaryQuantityOther1800){
				amountOrdered1800++;
			}
		}

		double percent1700 = (double) amountOrdered1700 / (double) amount1700 * 100;
		double percent1800 = (double)  amountOrdered1800/ (double) amount1800 * 100;
		double percentOthers = (double)  amountOrderedOthers/ (double) amountOthers * 100;
		double percentOfcoverage = ((double) rowNum - 1) / (double) orderProducts.size() * 100;

		String percent1700str = String.format("%.2f",percent1700);
		String percent1800str = String.format("%.2f",percent1800);
		String percentOthersStr = String.format("%.2f",percentOthers);

		String result = String.format("%.2f",percentOfcoverage);


		int checkRowNum = 1;

		for (Order order : orders) {
			for (OrderLine orderLine : order.getOrderLines()) {
				int goodId = orderLine.getGoodsId().intValue();

				Product pr = productService.getProductByCode(goodId);

				if (pr == null) {
					poiExcel.fillExcelToCheckNeeds(checkSheet, checkRowNum, "нет заказа ОРЛ", orderLine);
				} else {

					List <OrderProduct> listOP  = pr.getOrderProductsListHasDateTarget(order.getDateOrderOrl());
					if (listOP == null) {
						poiExcel.fillExcelToCheckNeeds(checkSheet, checkRowNum, "нет заказа ОРЛ", orderLine);
					} else {
						OrderProduct item = listOP.get(0);
						poiExcel.fillExcelToCheckNeeds(checkSheet, checkRowNum, item.getDateCreate().toString(), orderLine);

					}
				}

				checkRowNum++;
			}
		}
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}
		for (int i = 0; i < checkHeaders.length; i++) {
			checkSheet.autoSizeColumn(i);
		}

       /*
       1. по ордерам получить все продукты
       2 по продуктам методом product.getOrderProductsListHasDateTarget(order.getDateOrderOrl()) получить лист заказов по каждому продукту от орл
       3 product.getOrderProductsListHasDateTarget(order.getDateOrderOrl()).get(0) получить последний заказ ор по фактически заказанному продукту и записать в эксель
       взять дату
        */

		if (!isSheetEmpty) {
			try {
				File file = new File(appPath + "resources/others/" + fileName);
				book.write(new FileOutputStream(file));
				book.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String str = "По данным потребностям не были созданы слоты в установленное время. " +
					"Процент не поставленных заказов на " + currentTimeString + " относительно заказа ОРЛ - " + result +"%." +
					"\nПроцент для 1700 склада - " + percent1700str + "%." +
					"\nПроцент для 1800 склада - " + percent1800str + "%." +
					"\nПроцент для остальных складов - " + percentOthersStr + "%.";

			List<File> files = new ArrayList<File>();
			files.add(new File(appPath + "resources/others/" + fileName));

//			System.out.println(appPath + "resources/others/" + fileName);

			List<String> emails = propertiesUtils.getValuesByPartialKey(servletContext, "email.problemsWithOrders");
			mailService.sendEmailWithFilesToUsers(servletContext, "Незакрытые потребности " + currentTimeDayBeforeString, str, files, emails);

		}
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
    
    @Scheduled(cron = "0 00 04 * * ?") // каждый день в 04:00
    public void sendSchedulesTOHasORL() {
    	System.out.println("Start --- sendSchedulesTOHasORL");
		// Получаем текущую дату для имени файла
		LocalDate currentTime = LocalDate.now();
		String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

		List<String> emailsORL = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.to.ORL");
		List<String> emailsSupportDepartment = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.to.supportDepartment");

		Map<String, List<String>> draftLists = propertiesUtils.getListForDraftFolders(servletContext);

		System.out.println(emailsORL);
//      emails.addAll(emailsSupport);
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

		mailService.sendEmailWithFilesToUsers(servletContext, "Графики поставок на TO " + currentTimeString, "Автоматическая отправка графиков поставок на ТО\nВерсия с макросом выделений (Ctr+t)", filesZipORL, emailsORL);
		mailService.sendEmailWithFilesToUsers(servletContext, "Графики поставок на TO " + currentTimeString, "Автоматическая отправка графиков поставок на ТО\nВерсия с макросом выделений (Ctr+t)", filesZipSupportDepartment, emailsSupportDepartment);

		System.out.println("Finish --- sendSchedulesHasTOORL");
    }
    
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
    
    public File createZipFile(List<File> files, String zipFilePath) throws IOException {
        File zipFile = new File(zipFilePath);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : files) {
                if (file.exists() && !file.isDirectory()) {
                    addFileToZip(file, zos);
                } else {
                    System.err.println("File not found or is a directory: " + file.getAbsolutePath());
                }
            }
        }
        return zipFile; // Возвращаем объект File архива
    }

    private void addFileToZip(File file, ZipOutputStream zos) throws IOException {
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
    
    @Scheduled(cron = "0 00 20 * * ?") // каждый день в 20:00
    public void clearMessageList() {
    	mainChat.messegeList.clear();
    }
    
    
    
    @Scheduled(cron = "0 00 06 * * ?") // каждый день в 06:00
    public void get398() throws ParseException {
		try {
			Task task = taskService.getLastTaskFor398();
			String stock = task.getStocks();
//			String from = task.getFromDate().toString();
//			String to = task.getToDate().toString();
			
			String from = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String to = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			
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
						checkJWT(mainRestController.marketUrl);			
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
					
					MarketDataFor398Request for398Request = new MarketDataFor398Request(dateForm, dateTo, warehouseId, whatBase);		
					MarketPacketDto marketPacketDto = new MarketPacketDto(mainRestController.marketJWT, "SpeedLogist.GetReport398", mainRestController.serviceNumber, for398Request);		
					MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);
					
					String marketOrder2 = postRequest(mainRestController.marketUrl, gson.toJson(requestDto));
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
			
			mailService.sendEmailToUsers(servletContext, "Автоматическая выгрузка : 398", text, emailsORL);
		} catch (Exception e) {
			List<String> emailsAdmins = propertiesUtils.getValuesByPartialKey(servletContext, "email.admin");
			mailService.sendEmailToUsers(servletContext, "Ошибка автоматической выгрузки : 398", e.toString(), emailsAdmins);
		}
		
    }
    
    /**
     * Метод проверяет актуальность JWT токена
     * @param url
     */
    private void checkJWT(String url) {
		MarketDataForLoginDto dataDto = new MarketDataForLoginDto(mainRestController.loginMarket, mainRestController.passwordMarket, "101");
//		MarketDataForLoginDtoTEST dataDto = new MarketDataForLoginDtoTEST("SpeedLogist", "12345678", 101);
		MarketPacketDto packetDto = new MarketPacketDto("null", "GetJWT", mainRestController.serviceNumber, dataDto);
		MarketRequestDto requestDto = new MarketRequestDto("", packetDto);
		if(mainRestController.marketJWT == null){
			//запрашиваем jwt
			String str = postRequest(url, gson.toJson(requestDto));
			MarketTableDto marketRequestDto = gson.fromJson(str, MarketTableDto.class);
			mainRestController.marketJWT = marketRequestDto.getTable()[0].toString().split("=")[1].split("}")[0];
		}
	}
    
    /**
     * Переводит байты в мегабайты
     * @param input
     * @return
     */
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

    
    /*
	 * 1. + Сначала разрабатываем метод который по дате определяет какие контракты должны быть заказаны в этот день (список Schedule) + 
	 * 2. + Разрабатываем метод, который принимает список кодов контрактов и по ним отдаёт заказы, в указаный период от текущей даты на 7 недель вперед
	 * 3. + Суммируем заказы по каждому коду контракта
	 * 4. + формируем отчёт в excel и отправляем на почту 
	 */
    /**
     * Метод, отвечающий за формирование отчётов serviceLevel
     * Отключил, т.к. логика по складам изменилась
     * @throws IOException 
     */
    @Scheduled(cron = "0 00 08 * * MON-SAT") // каждый день в 08:00 утра кроме воскресенья
    public void sendServiceLevel() throws IOException {
//    	Date dateStart = Date.valueOf(LocalDate.now().minusDays(1));
//		Date dateFinish7Week = Date.valueOf(LocalDate.now().plusMonths(2));
//		List<Schedule> schedules = scheduleService.getSchedulesByDateOrder(dateStart, 1700); // реализация 1 пункта
//		List<Order> ordersHas7Week = orderService.getOrderByPeriodDeliveryAndListCodeContract(dateStart, dateFinish7Week, schedules); // реализация 2 пункта
//    	List<File> files = new ArrayList<File>();
//    	String appPath = servletContext.getRealPath("/");
//    	files.add(serviceLevel.checkingOrdersForORLNeeds(ordersHas7Week, dateStart, appPath));
//    	
//    	//получаем email
//    	List<String> emails = propertiesUtils.getValuesByPartialKey(servletContext, "email.slevel");
//    	
//        LocalDate currentTime = LocalDate.now().minusDays(1);
//        String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
//    	mailService.sendEmailWithFilesToUsers(servletContext, "Service level на " + currentTimeString, "Service level заказов, относительно заказов ОРЛ.\nВключает брони.\nVer 1.1", files, emails);
    	mainChat.messegeList.clear();
    }
    
    @Scheduled(cron = "0 0,30 8-21 * * MON-SAT")
	public void checkPalletsDifference() throws ParseException, IOException {
    	if(isRatRun) {
    		Map<String, Order> responseMap = new HashMap<>();
            java.util.Date t1 = new java.util.Date();
            System.err.println("ТАРАКАААААН");
            Date startDate = Date.valueOf(LocalDate.now());
            Date finishDate = Date.valueOf(LocalDate.now().plusDays(14));
            String appPath = servletContext.getRealPath("/");

            List<Order> orders = orderService.getOrderByTimeDelivery(startDate, finishDate);

//           orders.forEach(o-> System.out.println("ТАРАКАААААН - 1 - " + o.getMarketNumber()));
//           System.out.println("ТАРАКАААААН - 1 - " + orders.size());
//           System.out.println("");

            List<Order> filteredOrders = orders.stream()
                  .filter(o -> o.getMarketNumber() != null)
                  .filter(o -> Optional.ofNullable(o.getWay()).stream().noneMatch("АХО"::equals))
                  .distinct().collect(Collectors.toList()); // .toList()

            //создаём мапу marketNumber - order и заполняем лист с marketNumber
            Map<String, Order> ordersFromDB = new HashMap<>();
            for (Order order : filteredOrders) {
               ordersFromDB.put(order.getMarketNumber(), order);
            }

            ordersFromDB.forEach((k, v) -> System.out.println("ТАРАКАААААН - 2 - " + k + " " + v));
            System.out.println("ТАРАКАААААН - 2 - " + ordersFromDB.size());
            System.out.println("");

            String result = String.join(",", ordersFromDB.keySet());
            try {
               checkJWT(MainRestController.marketUrl);
            } catch (Exception e) {
               System.err.println("Ошибка получения jwt токена");
            }

            Object[] goodsId = result.split(",");

            Map<String, Order> response = new HashMap<String, Order>();
            MarketDataArrayForRequestDto dataDto3 = new MarketDataArrayForRequestDto(goodsId);
            MarketPacketDto packetDto3 = new MarketPacketDto(mainRestController.marketJWT, "SpeedLogist.OrderBuyArrayInfoGet", mainRestController.serviceNumber, dataDto3);
            MarketRequestDto requestDto3 = new MarketRequestDto("", packetDto3);
            String marketOrder2 = null;
            try {
               marketOrder2 = postRequest(mainRestController.marketUrl, gson.toJson(requestDto3));
            } catch (Exception e) {
               e.printStackTrace();
               System.err.println("ERROR : Ошибка запроса к Маркету");
               System.err.println("marketOrder2 : " + marketOrder2);
               System.err.println("ERROR DESCRIPTION - " + e.toString());
            }

            System.out.println("request -> " + gson.toJson(requestDto3));
            //проверяем на наличие сообщений об ошибке со стороны маркета
            if (marketOrder2.contains("Error")) {
               //тут избавляемся от мусора в json
               System.out.println(marketOrder2);
//            String str2 = marketOrder2.split("\\[", 2)[1];
//            String str3 = str2.substring(0, str2.length()-2);
               MarketErrorDto errorMarket = gson.fromJson(marketOrder2, MarketErrorDto.class);
//            System.out.println("JSON -> "+str3);
//            System.out.println(errorMarket);
               if (errorMarket.getError().equals("99")) {//обработка случая, когда в маркете номера нет, а в бд есть.

               }
               System.err.println("ERROR");
               System.err.println(marketOrder2);
               System.err.println("ERROR DESCRIPTION - " + errorMarket.getErrorDescription());
            }

            System.out.println("Пришло из маркета: " + marketOrder2);

            //создаём свой парсер и парсим json в объекты, с которыми будем работать.
            CustomJSONParser customJSONParser = new CustomJSONParser();

            //создаём лист OrderBuyGroup
//         Map<Long, OrderBuyGroupDTO> OrderBuyGroupDTOMap = new HashMap<Long, OrderBuyGroupDTO>();
//         Map<String, Order> ordersFromMarket = new HashMap<String, Order>();
            JSONParser parser = new JSONParser();
            JSONObject jsonMainObject = (JSONObject) parser.parse(marketOrder2);
            JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("OrderBuyGroup");
            Map<String, List<Order>> ordersWithStatus0 = new HashMap<>();
            Map<String, List<Order>> ordersWithWrongPallets = new HashMap<>();

            for (Object object : numShopsJSON) {
               //создаём OrderBuyGroup
               OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(object.toString());
               int checkx = orderBuyGroupDTO.getCheckx();
               if (checkx == 0) {
                  Order order = orderCreater.createSimpleOrder(orderBuyGroupDTO);
                  Order currentOrderFromDB = ordersFromDB.get(order.getMarketNumber());
                  List<Order> list;
                  String loginManager = currentOrderFromDB.getLoginManager();
                  User user = userService.getUserByLogin(loginManager);

                  String emailManager = user.geteMail();
                  if (ordersWithStatus0.containsKey(emailManager)) {
                     list = ordersWithStatus0.get(emailManager);
                  } else {
                     list = new ArrayList<>();
                  }
                  list.add(currentOrderFromDB);
                  ordersWithStatus0.put(emailManager, list);

               } else if (checkx == 50 || checkx == 51) {
                  Order order = orderCreater.createSimpleOrder(orderBuyGroupDTO);
                  responseMap.put(order.getMarketNumber(), order);
               }
//            OrderBuyGroupDTOMap.put(orderBuyGroupDTO.getOrderBuyGroupId(), orderBuyGroupDTO);

            }

            responseMap.forEach((k, v) -> System.out.println("ТАРАКАААААН - 3 - " + k + " " + v));
            System.out.println("ТАРАКАААААН - 3 - " + responseMap.size());
            System.out.println("");

            System.out.println("EMAIL TEST = " + propertiesUtils.getValuesByPartialKey(servletContext, "email.test").get(0));

            //вычисляем косячные ордеры и раскладываем по соответствующим мапам

            for (String key : responseMap.keySet()) {

               double marketAmountOfPallets = 0;
               Order marketOrder = responseMap.get(key);
               Set<OrderLine> orderLines = marketOrder.getOrderLines();

               for (OrderLine orderLine : orderLines) {
                  System.out.println("ТАРАКАААААН - 3.3 - Orderlines pallets " + orderLine.getQuantityPallet());
                  System.out.println("");
                  if ((orderLine.getQuantityPallet() != null && orderLine.getQuantityPallet() > 0) && orderLine.getQuantityOrder() != null) {
                     marketAmountOfPallets += Math.ceil(orderLine.getQuantityOrder() / orderLine.getQuantityPallet());
                  }
               }
               Order currentOrderFromBD = ordersFromDB.get(key);
               List<Order> list;

               String loginManager = currentOrderFromBD.getLoginManager();
               User user = userService.getUserByLogin(loginManager);

               String emailManager = user.geteMail();
               System.out.println("ТАРАКАААААН - 3.4 - DBOrder pallets " + currentOrderFromBD.getPall());
               System.out.println("");
               int DBAmountOfPallets = Integer.parseInt(currentOrderFromBD.getPall());

               if (DBAmountOfPallets < marketAmountOfPallets) {
                  if (ordersWithWrongPallets.containsKey(emailManager)) {
                     list = ordersWithWrongPallets.get(emailManager);
                  } else {
                     list = new ArrayList<>();
                  }
                  list.add(currentOrderFromBD);
                  ordersWithWrongPallets.put(emailManager, list);
               }
            }

            System.out.println("ТАРАКАААААН - 4 - сформированы мапы = " + ordersWithWrongPallets.size());
            System.out.println("");

            //обрабатываем полученные мапы: формируем рассылку и чистим слоты
            for (String emailManager : ordersWithStatus0.keySet()) {
               List<Order> orderCheckPalletsDtos = ordersWithStatus0.get(emailManager);

               StringBuilder orderIds = new StringBuilder();
               for (Order order : orderCheckPalletsDtos) {
                  orderIds.append(order.getIdOrder());
                  orderIds.append(", ");
               }
               orderIds.deleteCharAt(orderIds.length() - 1);
               orderIds.deleteCharAt(orderIds.length() - 1);

               System.out.println("ТАРАКАААААН - 5 - перед рассылкой статусов 0");
               System.out.println("");

//                 List<String> emailsAdmins = propertiesUtils.getValuesByPartialKey(servletContext, "email.test");
               List<String> emails = new ArrayList<>();
               emails.add(emailManager);
//                 String messageText = "Добрый день.\nЗаказы " + orderIds + " находились в статусе 0. Заказы удалены из слотов.";
               String messageText = "Добрый день.\nЗаказы " + orderIds + " находились в статусе 0. В следующем обновлении такие заказы будут удаляться.";

//             mailService.sendEmailToUsers(appPath, "Информация по заказам", messageText, emailsAdmins);
               mailService.sendEmailToUsers(appPath, "Информация по заказам", messageText, emails);
               for (Order orderCheckPalletsDto : orderCheckPalletsDtos) {
//                   orderService.deleteSlot(orderCheckPalletsDto.getIdOrder());
               }

            }

            for (String emailManager : ordersWithWrongPallets.keySet()) {
               List<Order> orderCheckPalletsDtos = ordersWithWrongPallets.get(emailManager);


               StringBuilder orderIds = new StringBuilder();
               for (Order order : orderCheckPalletsDtos) {
                  orderIds.append(order.getIdOrder());
                  orderIds.append(", ");
               }
               orderIds.deleteCharAt(orderIds.length() - 1);
               orderIds.deleteCharAt(orderIds.length() - 1);

               System.out.println("ТАРАКАААААН - 5 - перед рассылкой косяков паллет");
               System.out.println("");

//                 List<String> emailsAdmins = propertiesUtils.getValuesByPartialKey(servletContext, "email.test");
               List<String> emails = new ArrayList<>();
               emails.add(emailManager);

               //пока что тут мой имейл, потом надо подставить emailManager

//                 String messageText = "Добрый день.\n" +
//                       "В заказах " + orderIds + " количество паллет в маркете больше чем количество паллет в Speedlogist. Заказы удалены из слотов.";
               String messageText = "Добрый день.\n" +
                     "В заказах " + orderIds + " количество паллет в маркете больше чем количество паллет в Speedlogist. В следующем обновлении такие заказы будут удаляться.";
//                 mailService.sendEmailToUsers(appPath, "Информация по заказам", messageText, emailsAdmins);
               mailService.sendEmailToUsers(appPath, "Информация по заказам", messageText, emails);


               for (Order order : orderCheckPalletsDtos) {
//                   orderService.deleteSlot(orderCheckPalletsDto.getIdOrder());
               }
            }

            ordersWithStatus0.forEach((k, v) -> System.out.println("ТАРАКАААААН - ordersWithStatus0 - " + k + " " + v));
            System.out.println("ТАРАКАААААН - ordersWithStatus0 - " + ordersWithStatus0.size());
            System.out.println("");

            ordersWithWrongPallets.forEach((k, v) -> System.out.println("ТАРАКАААААН - ordersWithStatus0 - " + k + " " + v));
            System.out.println("ТАРАКАААААН - ordersWithWrongPallets - " + ordersWithWrongPallets.size());
            System.out.println("");

            java.util.Date t3 = new java.util.Date();
            System.err.println(t3.getTime() - t1.getTime() + " ms");
         }
	}    

    
}
