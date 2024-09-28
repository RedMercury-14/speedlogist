package by.base.main.service;

import java.sql.Date;
import java.util.List;

import by.base.main.model.Schedule;

public interface ScheduleService {

	Schedule getScheduleById(Integer id);
	
	List<Schedule> getSchedules();
	
	/**
	 * Возвращает лист Schedule по складу
	 * @param numStock
	 * @return
	 */
	List<Schedule> getSchedulesByStock(Integer numStock);
		
	Schedule getScheduleByNumContract(Long num);
		
	Integer saveSchedule (Schedule schedule);
	
	void updateSchedule (Schedule schedule);
	
	void deleteOrderById(Integer id);
	
	/**
	 * Метод, который определяем список из графикоф поставок, дата которого соответствует дню недели заказа
	 * @param date
	 * @return
	 */
	List<Schedule> getSchedulesByDateOrder(Date date, Integer numStock);
}
