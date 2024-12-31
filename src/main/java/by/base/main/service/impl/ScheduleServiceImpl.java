package by.base.main.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dto.CounterpartyDTO;

import by.base.main.aspect.TimedExecution;
import by.base.main.dao.ScheduleDAO;
import by.base.main.model.Schedule;
import by.base.main.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService{

	@Autowired
	ScheduleDAO scheduleDAO;
	
	@Override
	public Schedule getScheduleById(Integer id) {
		// TODO Auto-generated method stub
		return scheduleDAO.getScheduleById(id);
	}

	@Override
	@TimedExecution
	public Schedule getScheduleByNumContract(Long num) {
		// TODO Auto-generated method stub
		return scheduleDAO.getScheduleByNumContract(num);
	}

	@Override
	public Integer saveSchedule(Schedule schedule) {
		// TODO Auto-generated method stub
		return scheduleDAO.saveOrder(schedule);
	}

	@Override
	public void updateSchedule(Schedule schedule) {
		scheduleDAO.updateOrder(schedule);
		
	}

	@Override
	public void deleteOrderById(Integer id) {
		scheduleDAO.deleteOrderById(id);
	}

	@Override
	public List<Schedule> getSchedulesByStock(Integer numStock) {
		return scheduleDAO.getSchedulesByStock(numStock);
	}

	@Override
	public List<Schedule> getSchedulesByDateOrder(Date date, Integer numStock) {
		return scheduleDAO.getSchedulesByDateOrder(date, numStock);
	}

	@Override
	public List<Schedule> getSchedulesListRC() {
		return scheduleDAO.getSchedulesListRC();
	}

	@Override
	@Deprecated
	public List<Schedule> getSchedulesListTO() {
  		return scheduleDAO.getSchedulesListTO();
	}

	@Override
	@Deprecated
	public List<Schedule> getSchedulesListTOContract(String contractCode) {
		return scheduleDAO.getSchedulesListTOContract(contractCode);
	}

	@Override
	public List<Schedule> getSchedulesListTOСounterparty(String counterpartyName) {
		return scheduleDAO.getSchedulesListTOСounterparty(counterpartyName);
	}

	@Override
	@Deprecated
	public List<Schedule> getSchedulesByTOType(String toType) {
		return scheduleDAO.getSchedulesByTOType(toType);
	}

	@Override
	public Schedule getScheduleByNumContractAndNUmStock(Long num, Integer numStock) {
		return scheduleDAO.getScheduleByNumContractAndNUmStock(num, numStock);
	}

	@Override
	public List<CounterpartyDTO> getcounterpartyListRC() {
		return scheduleDAO.getcounterpartyListRC();
	}

	@Override
	public List<CounterpartyDTO> getcounterpartyListTO() {
		return scheduleDAO.getcounterpartyListTO();
	}

	@Override
	public Schedule getScheduleByNumContractAndNumStock(Long num, Integer shock) {
		return scheduleDAO.getScheduleByNumContractAndNumStock(num, shock);
	}

	@Override
	public List<CounterpartyDTO> getUnicCodeContractTO() {
		return scheduleDAO.getUnicCodeContractTO();
	}

	@Override
	public int updateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty(Long counterpartyCode,
			String codeNameOfQuantumCounterparty) {
		return scheduleDAO.updateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty(counterpartyCode, codeNameOfQuantumCounterparty);
	}

	/**
	 * @author Ira
	 * @param contract
	 * <br>Возвращает список всех графиков на ТО по номеру контракта - и временных, и постоянных</br>
	 * @return
	 */
	@Override
	public List<Schedule> getSchedulesListTOContractWithTemp(Long contract) {
		return scheduleDAO.getSchedulesListTOContractWithTemp(contract);

	}

	/**
	 * @author Ira
	 * <br>Возвращает список только актуальных графиков на ТО - либо временных, либо постоянных</br>
	 * @return
	 */
	@Override
	public List<Schedule> getSchedulesListTOWithTemp() {
		return scheduleDAO.getSchedulesListTOWithTemp();
	}

	/**
	 * @author Ira
	 * <br>Возвращает список графиков на ТО по номеру контракта и номеру ТО - и временных, и постоянных</br>
	 * @return
	 */
	@Override
	public List<Schedule> getScheduleByNumContractAndNUmStockWithTemp(Long num, Integer numStock) {
		return scheduleDAO.getScheduleByNumContractAndNUmStockWithTemp(num, numStock);
	}

	/**
	 * @author Ira
	 * <br>Проверяет создаваемый график на пересечение временных границ с существующими графиками</br>
	 * @return
	 */
	@Override
	public boolean checkScheduleIntersection(List<Schedule> existingSchedules, Schedule newSchedule){
		for (Schedule sch: existingSchedules) {
			if (sch.getStartDateTemp() != null && sch.getEndDateTemp() != null) {
				Date currentStartDate = sch.getStartDateTemp();
				Date currentEndDate = sch.getEndDateTemp();
				Date newScheduleStartDate = newSchedule.getStartDateTemp();
				Date newScheduleEndDate = newSchedule.getEndDateTemp();

				if(!((newScheduleStartDate.after(currentEndDate) && newScheduleEndDate.after(currentEndDate))
						|| (newScheduleStartDate.before(currentStartDate) && newScheduleEndDate.before(currentStartDate)))){
					return false;
				}
			}
		}
		return true;

	}

	/**
	 * @author Ira
	 * <br>Возвращает список графиков по типу ТО - и временных, и постоянных</br>
	 * @param toType
	 * @return
	 */
	@Override
	public List<Schedule> getSchedulesByTOTypeWithTemp(String toType) {
		return scheduleDAO.getSchedulesByTOTypeWithTemp(toType);
	}

	/**
	 * @author Ira
	 * <br>Получает на вход лист графиков, фильтрует, возвращает только графики, действующие на текущий момент</br>
	 * @param allSchedules
	 * @return
	 */
	@Override
	public List<Schedule> getSchedulesListTOOnlyActual(List<Schedule> allSchedules){
		List<Schedule> actualSchedules = new ArrayList<>(allSchedules);
		for (Schedule schedule: allSchedules){
			if (schedule.getStartDateTemp() != null) {
				long counterpartyContractNumber = schedule.getCounterpartyContractCode();
				int numStock = schedule.getNumStock();

				actualSchedules.removeIf(sch -> sch.getCounterpartyContractCode() == counterpartyContractNumber
						&& sch.getNumStock() == numStock
						&& sch.getStartDateTemp() == null);

			}
		}
		return actualSchedules;
	}

	@Override
	public List<Schedule> getSchedulesListTOContractOnlyTemp(Long num) {
		return scheduleDAO.getSchedulesListTOContractOnlyTemp(num);
	}

	@Override
	public List<Schedule> getSchedulesListTOAll() {
		return scheduleDAO.getSchedulesListTOAll();
	}
}
