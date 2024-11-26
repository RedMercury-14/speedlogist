package by.base.main.service.util;

import java.io.File;
import java.io.FileInputStream;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import by.base.main.model.Order;
import by.base.main.model.Schedule;
import by.base.main.service.OrderService;
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
    
    
    
    @Scheduled(cron = "0 00 04 * * ?") // каждый день в 04:00
    public void sendSchedulesTOHasORL() {
    	System.out.println("Start --- sendSchedulesTOHasORL");
    	// Получаем текущую дату для имени файла
        LocalDate currentTime = LocalDate.now();
        String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        
		List<String> emails = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.to");
//		emails.addAll(emailsSupport);
		String appPath = servletContext.getRealPath("/");
		
		String fileName1200 = "1200 (----Холодный----).xlsm";
		String fileName1100 = "1100 График прямой сухой.xlsm";
		String fileNameSample = "График для шаблоново.xlsx";
		
		try {
			poiExcel.exportToExcelScheduleListTOWithMacro(scheduleService.getSchedulesByTOType("холодный").stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1200);
			poiExcel.exportToExcelScheduleListTOWithMacro(scheduleService.getSchedulesByTOType("сухой").stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1100);
			poiExcel.exportToExcelSampleListTO(scheduleService.getSchedulesByTOType("холодный").stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileNameSample);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Ошибка формирование EXCEL");
		}
		
//		response.setHeader("content-disposition", "attachment;filename="+fileName+".xlsx");
		List<File> files = new ArrayList<File>();
		files.add(new File(appPath + "resources/others/" + fileName1200));
		files.add(new File(appPath + "resources/others/" + fileName1100));
		files.add(new File(appPath + "resources/others/" + fileNameSample));
		
		 File zipFile;
		 List<File> filesZip = new ArrayList<File>();
		try {
			zipFile = createZipFile(files, appPath + "resources/others/TO.zip");
			filesZip.add(zipFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		mailService.sendEmailWithFilesToUsers(servletContext, "TEST Графики поставок на TO от TEST" + currentTimeString, "Тестовая отправка сообщения.\nНе обращайте внимания / игнорируте это сообщение", files, emails);
		mailService.sendEmailWithFilesToUsers(servletContext, "Графики поставок на TO" + currentTimeString, "Автоматическая отправка графиков поставок на ТО\nВерсия с макросом выделений (Ctr+t)", filesZip, emails);
    	System.out.println("Finish --- sendSchedulesHasTOORL");
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
