package by.base.main.dao;

import java.sql.Date;
import java.util.List;

import by.base.main.model.Schedule;

public interface ScheduleDAO {

	Schedule getScheduleById(Integer id);
	
	List<Schedule> getSchedulesListRC();
	
	List<Schedule> getSchedulesListTO();
	
	/**
	 * Возвращает лист Schedule по складу
	 * @param numStock
	 * @return
	 */
	List<Schedule> getSchedulesByStock(Integer numStock);
		
	Schedule getScheduleByNumContract(Long num);
		
	Integer saveOrder (Schedule schedule);
	
	void updateOrder (Schedule schedule);
	
	void deleteOrderById(Integer id);
	
	/**
	 * Метод, который определяем список из графикоф поставок, дата которого соответствует дню недели заказа
	 * @param date
	 * @return
	 */
	List<Schedule> getSchedulesByDateOrder(Date date, Integer numStock);
	
}
