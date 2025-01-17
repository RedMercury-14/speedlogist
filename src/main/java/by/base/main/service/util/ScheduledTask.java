package by.base.main.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import by.base.main.model.Order;
import by.base.main.model.OrderLine;
import by.base.main.model.OrderProduct;
import by.base.main.model.Product;
import by.base.main.model.Schedule;
import by.base.main.service.OrderProductService;
import by.base.main.service.OrderService;
import by.base.main.service.ProductService;
import by.base.main.service.ScheduleService;
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
	private ServiceLevel serviceLevel;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderProductService orderProductService;
	
	@Autowired
	private ProductService productService;
	
    @Scheduled(cron = "0 00 11 * * ?") // каждый день в 11:00
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
	                   String regEx = " " + draftNumber + ".";

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
    
    /*
	 * 1. + Сначала разрабатываем метод который по дате определяет какие контракты должны быть заказаны в этот день (список Schedule) + 
	 * 2. + Разрабатываем метод, который принимает список кодов контрактов и по ним отдаёт заказы, в указаный период от текущей даты на 7 недель вперед
	 * 3. + Суммируем заказы по каждому коду контракта
	 * 4. + формируем отчёт в excel и отправляем на почту 
	 */
    /**
     * Метод, отвечающий за формирование отчётов serviceLevel
     * @throws IOException 
     */
    @Scheduled(cron = "0 00 08 * * MON-SAT") // каждый день в 08:00 утра кроме воскресенья
    public void sendServiceLevel() throws IOException {
    	Date dateStart = Date.valueOf(LocalDate.now().minusDays(1));
		Date dateFinish7Week = Date.valueOf(LocalDate.now().plusMonths(2));
		List<Schedule> schedules = scheduleService.getSchedulesByDateOrder(dateStart, 1700); // реализация 1 пункта
		List<Order> ordersHas7Week = orderService.getOrderByPeriodDeliveryAndListCodeContract(dateStart, dateFinish7Week, schedules); // реализация 2 пункта
    	List<File> files = new ArrayList<File>();
    	String appPath = servletContext.getRealPath("/");
    	files.add(serviceLevel.checkingOrdersForORLNeeds(ordersHas7Week, dateStart, appPath));
    	
    	//получаем email
    	List<String> emails = propertiesUtils.getValuesByPartialKey(servletContext, "email.slevel");
    	
        LocalDate currentTime = LocalDate.now().minusDays(1);
        String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    	mailService.sendEmailWithFilesToUsers(servletContext, "Service level на " + currentTimeString, "Service level заказов, относительно заказов ОРЛ.\nВключает брони.\nVer 1.1", files, emails);
    	mainChat.messegeList.clear();
    }
}
