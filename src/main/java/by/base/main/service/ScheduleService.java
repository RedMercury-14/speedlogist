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
	 * Метод возвращает все графики поставок и временные и удалённые и т.д.
	 * @return
	 */
	List<Schedule> getSchedulesListTOAll();
	
	/**
	 * Метод изменяет название кванта по коду контрагента.
	 * Возвращает кол-во измененных строк.
	 * @param counterpartyCode
	 * @param codeNameOfQuantumCounterparty
	 * @return
	 */
	int updateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty(Long counterpartyCode, String codeNameOfQuantumCounterparty);
	
	/**
	 * выдаёт лист Schedule с уникальными кодами контракта
	 * @return
	 */
	List<CounterpartyDTO> getUnicCodeContractTO();
	
	/**
	 * Возвращает лист с контрагентами РЦ <b>DTO класс</b>
	 * @return
	 */
	List<CounterpartyDTO> getcounterpartyListRC();
	
	/**
	 * Возвращает лист с контрагентами ТО <b>DTO класс</b>
	 * @return
	 */
	List<CounterpartyDTO> getcounterpartyListTO();
	
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
		
	Integer saveSchedule (Schedule schedule);
	
	void updateSchedule (Schedule schedule);
	
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
	 * @return
	 */
	List<Schedule> getSchedulesListTOContractWithTemp(Long contract);


	/**
	 * @author Ira
	 * <br>Возвращает список только актуальных графиков на ТО - либо временных, либо постоянных</br>
	 * @return
	 */
	List<Schedule> getSchedulesListTOWithTemp();

	/**
	 * @author Ira
	 * <br>Возвращает список графиков на ТО по номеру контракта и номеру ТО - и временных, и постоянных</br>
	 * @return
	 */
	List<Schedule> getScheduleByNumContractAndNUmStockWithTemp(Long num, Integer numStock);

	/**
	 * @author Ira
	 * <br>Проверяет создаваемый график на пересечение временных границ с существующими графиками</br>
	 * @return
	 */
	boolean checkScheduleIntersection(List<Schedule> schedules, Schedule schedule);

	/**
	 * <br>Возвращает список графиков по типу ТО - и временных, и постоянных</br>
	 * <br><b>Возвращает только графики с 20 статусом</b>
	 * @param toType
	 * @return
	 * @author Ira
	 */
	List<Schedule> getSchedulesByTOTypeWithTemp(String toType);

	/**
	 * @author Ira
	 * <br>Получает на вход лист графиков, фильтрует, возвращает лист графиков, действующих на текущий момент</br>
	 * @param allSchedules
	 * @return
	 */
	List<Schedule> getSchedulesListTOOnlyActual(List<Schedule> allSchedules);
	
	/**
	 * @author Dima
	 * <br>Возвращает список всех графиков на ТО по номеру контракта - <b>только временных!</b> </br>
	 * @param num
	 * @return
	 */
	public List<Schedule> getSchedulesListTOContractOnlyTemp(Long num);
}

