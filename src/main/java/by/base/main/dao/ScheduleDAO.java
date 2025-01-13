package by.base.main.dao;

import java.sql.Date;
import java.util.List;

import com.dto.CounterpartyDTO;

import by.base.main.model.Schedule;

import javax.transaction.Transactional;

public interface ScheduleDAO {

	Schedule getScheduleById(Integer id);
	
	/**
	 * Метод изменяет название кванта по коду контрагента.
	 * Возвращает кол-во измененных строк.
	 * @param counterpartyCode
	 * @param codeNameOfQuantumCounterparty
	 * @return
	 */
	int updateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty(Long counterpartyCode, String codeNameOfQuantumCounterparty);
	
	List<Schedule> getSchedulesListRC();
	
	List<Schedule> getSchedulesListTO();
	
	/**
	 * Метод возвращает все графики поставок и временные и удалённые и т.д.
	 * @return
	 */
	List<Schedule> getSchedulesListTOAll();
	
	/**
	 * Возвращает лист с контрагентами РЦ <b>DTO класс</b>
	 * @return
	 */
	List<CounterpartyDTO> getcounterpartyListRC();
	
	/**
	 * Возвращает лист с контрагентами РЦ <b>DTO класс</b>
	 * <br><b>Важно то что он не подтягивает коды контрактов, что ускоряет работу</b>
	 * @return
	 */
	List<CounterpartyDTO> getСounterpartyListRCNameOnly();
	
	/**
	 * Возвращает лист с контрагентами ТО <b>DTO класс</b>
	 * @return
	 */
	List<CounterpartyDTO> getcounterpartyListTO();
	
	/**
	 * выдаёт лист Schedule с уникальными кодами контракта
	 * @return
	 */
	List<CounterpartyDTO> getUnicCodeContractTO();
	
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
	 * <br><b>Возвращает только графики с 20 статусом</b>
	 * @param numStock
	 * @return
	 */
	List<Schedule> getSchedulesByTOType(String toType);
		
	Schedule getScheduleByNumContract(Long num);
	
	Schedule getScheduleByNumContractAndNumStock(Long num, Integer shock);
	
	/**
	 * Отдаёт график поставок по номеру контракта и номеру магазина / склада
	 * @param num
	 * @param numStock
	 * @return
	 */
	Schedule getScheduleByNumContractAndNUmStock(Long num, Integer numStock);
		
	Integer saveOrder (Schedule schedule);
	
	void updateOrder (Schedule schedule);
	
	void deleteOrderById(Integer id);
	
	/**
	 * Метод, который определяем список из графикоф поставок, дата которого соответствует дню недели заказа
	 * @param date
	 * @return
	 */
	List<Schedule> getSchedulesByDateOrder(Date date, Integer numStock);

	/**
	 * @author Ira
	 * <br>Возвращает список всех графиков на ТО по номеру контракта - и временных, и постоянных</br>
	 * @param num
	 * @return
	 */

	public List<Schedule> getSchedulesListTOContractWithTemp(Long num);

	/**
	 * @author Ira
	 * <br>Возвращает список всех графиков на ТО - и временных, и постоянных</br>
	 * <br><b>Возвращает только графики с 20 статусом</b>
	 * @return
	 */
	public List<Schedule> getSchedulesListTOWithTemp();

	/**
	 * @author Ira
	 * <br>Возвращает список графиков на ТО по номеру контракта и номеру ТО - и временных, и постоянных</br>
	 * @return
	 */
	List<Schedule> getScheduleByNumContractAndNUmStockWithTemp(Long num, Integer numStock);

	/**
	 * <br>Возвращает список графиков по типу ТО - и временных, и постоянных
	 * <br><b>Возвращает только графики с 20 статусом</b>
	 * @param toType
	 * @return
	 * @author Ira
	 */
	List<Schedule> getSchedulesByTOTypeWithTemp(String toType);
	
	/**
	 * @author Dima
	 * <br>Возвращает список всех графиков на ТО по номеру контракта - <b>только временных!</b> </br>
	 * @param num
	 * @return
	 */
	public List<Schedule> getSchedulesListTOContractOnlyTemp(Long num);
	
	

}
