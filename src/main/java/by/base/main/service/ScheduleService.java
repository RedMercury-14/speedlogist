package by.base.main.service;

import java.sql.Date;
import java.util.List;

import com.dto.CounterpartyDTO;

import by.base.main.model.Schedule;

public interface ScheduleService {

	Schedule getScheduleById(Integer id);
	
	List<Schedule> getSchedulesListRC();
	
	List<Schedule> getSchedulesListTO();
	
	/**
	 * Возвращает лист с контрагентами <b>DTO класс</b>
	 * @return
	 */
	List<CounterpartyDTO> getcounterpartyList();
	
	/**
	 * Возвращает графики поставок на ТО по коду контрактов
	 * @param contractCode
	 * @return
	 */
	List<Schedule> getSchedulesListTOContract(String contractCode);
	
	/**
	 * Возвращает графики поставо на ТО по <b>наиминованию контрагента (через LIKE)</b>
	 * @param counterpartyName
	 * @return
	 */
	List<Schedule> getSchedulesListTOСounterparty(String counterpartyName);
	
	/**
	 * Возвращает лист Schedule по складу
	 * @param numStock
	 * @return
	 */
	List<Schedule> getSchedulesByStock(Integer numStock);
	
	/**
	 * Возвращает лист Schedule по toType (сухой, холодный)
	 * @param numStock
	 * @return
	 */
	List<Schedule> getSchedulesByTOType(String toType);
		
	Schedule getScheduleByNumContract(Long num);
	
	/**
	 * Отдаёт график поставок по номеру контракта и номеру магазина / склада
	 * @param num
	 * @param numStock
	 * @return
	 */
	Schedule getScheduleByNumContractAndNUmStock(Long num, Integer numStock);
		
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
