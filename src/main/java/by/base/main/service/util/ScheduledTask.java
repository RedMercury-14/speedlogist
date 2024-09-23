package by.base.main.service.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

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
			poiExcel.exportToExcelScheduleList(scheduleService.getSchedulesByStock(1200).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1200);
			poiExcel.exportToExcelScheduleList(scheduleService.getSchedulesByStock(1250).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
					appPath + "resources/others/" + fileName1250);
			poiExcel.exportToExcelScheduleList(scheduleService.getSchedulesByStock(1700).stream().filter(s-> s.getStatus() == 20).collect(Collectors.toList()), 
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
		
		
		mailService.sendEmailWithFilesToUsers(servletContext, "Графики поставок на " + currentTimeString, "Автоматическая отправка", files, emails);
		System.out.println("Finish --- sendSchedulesHasORL");
    }
    
    @Scheduled(cron = "0 00 20 * * ?") // каждый день в 20:00
    public void clearMessageList() {
    	mainChat.messegeList.clear();
    }
}
