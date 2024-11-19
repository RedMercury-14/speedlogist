package by.base.main.service.util;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        
		List<String> emails = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl");
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
    @Scheduled(cron = "0 00 04 * * ?") // каждый день в 11:00
    public void sendSchedulesTOHasORL() {
    	System.out.println("Start --- sendSchedulesTOHasORL");
    	Map<String, Object> responseMap = new HashMap<>();
		// Получаем текущую дату для имени файла
        LocalDate currentTime = LocalDate.now();
        String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        
		List<String> emails = propertiesUtils.getValuesByPartialKey(servletContext, "email.orl.to");
//		List<String> emailsSupport = propertiesUtils.getValuesByPartialKey(servletContext, "email.orderSupport");
//		emails.addAll(emailsSupport);
		String appPath = servletContext.getRealPath("/");
		
		String fileName1200 = "1200 (----Холодный----).xlsx";
		String fileName1100 = "1100 График прямой сухой.xlsx";
		
		try {
			poiExcel.exportToExcelScheduleListTO(scheduleService.getSchedulesByTOType("холодный").stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1200);
			poiExcel.exportToExcelScheduleListTO(scheduleService.getSchedulesByTOType("сухой").stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1100);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Ошибка формирование EXCEL");
		}
		
//		response.setHeader("content-disposition", "attachment;filename="+fileName+".xlsx");
		List<File> files = new ArrayList<File>();
		files.add(new File(appPath + "resources/others/" + fileName1200));
		files.add(new File(appPath + "resources/others/" + fileName1100));
		
		
		mailService.sendEmailWithFilesToUsers(servletContext, "Графики поставок на TO от " + currentTimeString, "Автоматическая отправка", files, emails);
    	System.out.println("Finish --- sendSchedulesHasTOORL");
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
