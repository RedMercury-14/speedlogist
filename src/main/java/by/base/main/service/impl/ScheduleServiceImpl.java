package by.base.main.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dto.CounterpartyDTO;

import by.base.main.aspect.TimedExecution;
import by.base.main.dao.ScheduleDAO;
import by.base.main.dto.ScheduleCountOrderDTO;
import by.base.main.model.Schedule;
import by.base.main.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService{

	@Autowired
	ScheduleDAO scheduleDAO;
	
	@Transactional
	@Override
	public Schedule getScheduleById(Integer id) {
		// TODO Auto-generated method stub
		return scheduleDAO.getScheduleById(id);
	}

	@Transactional
	@Override
	@TimedExecution
	public Schedule getScheduleByNumContract(Long num) {
		// TODO Auto-generated method stub
		return scheduleDAO.getScheduleByNumContract(num);
	}

	@Transactional
	@Override
	public Integer saveSchedule(Schedule schedule) {
		// TODO Auto-generated method stub
		return scheduleDAO.saveOrder(schedule);
	}

	@Transactional
	@Override
	public void updateSchedule(Schedule schedule) {
		scheduleDAO.updateOrder(schedule);
		
	}

	@Transactional
	@Override
	public void deleteOrderById(Integer id) {
		scheduleDAO.deleteOrderById(id);
	}

	@Transactional
	@Override
	public List<Schedule> getSchedulesByStock(Integer numStock) {
		return scheduleDAO.getSchedulesByStock(numStock);
	}

	@Transactional
	@Override
	public List<Schedule> getSchedulesByDateOrder(Date date, Integer numStock) {
		return scheduleDAO.getSchedulesByDateOrder(date, numStock);
	}

	@Transactional
	@Override
	public List<Schedule> getSchedulesListRC() {
		return scheduleDAO.getSchedulesListRC();
	}

	@Transactional
	@Override
	@Deprecated
	public List<Schedule> getSchedulesListTO() {
  		return scheduleDAO.getSchedulesListTO();
	}

	@Transactional
	@Override
	@Deprecated
	public List<Schedule> getSchedulesListTOContract(String contractCode) {
		return scheduleDAO.getSchedulesListTOContract(contractCode);
	}

	@Transactional
	@Override
	public List<Schedule> getSchedulesListTOСounterparty(String counterpartyName) {
		return scheduleDAO.getSchedulesListTOСounterparty(counterpartyName);
	}

	@Transactional
	@Override
	@Deprecated
	public List<Schedule> getSchedulesByTOType(String toType) {
		return scheduleDAO.getSchedulesByTOType(toType);
	}

	@Transactional
	@Override
	public Schedule getScheduleByNumContractAndNUmStock(Long num, Integer numStock) {
		return scheduleDAO.getScheduleByNumContractAndNUmStock(num, numStock);
	}

	@Transactional
	@Override
	public List<CounterpartyDTO> getcounterpartyListRC() {
		return scheduleDAO.getcounterpartyListRC();
	}

	@Transactional
	@Override
	public List<CounterpartyDTO> getcounterpartyListTO() {
		return scheduleDAO.getcounterpartyListTO();
	}

	@Transactional
	@Override
	public Schedule getScheduleByNumContractAndNumStock(Long num, Integer shock) {
		return scheduleDAO.getScheduleByNumContractAndNumStock(num, shock);
	}

	@Transactional
	@Override
	public List<CounterpartyDTO> getUnicCodeContractTO() {
		return scheduleDAO.getUnicCodeContractTO();
	}

	@Transactional
	@Override
	public int updateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty(Long counterpartyCode,
			String codeNameOfQuantumCounterparty) {
		return scheduleDAO.updateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty(counterpartyCode, codeNameOfQuantumCounterparty);
	}

	@Transactional
	@Override
	public List<Schedule> getSchedulesListTOContractWithTemp(Long contract) {
		return scheduleDAO.getSchedulesListTOContractWithTemp(contract);
	}

	@Transactional
	@Override
	public List<Schedule> getSchedulesListTOWithTemp() {
		return scheduleDAO.getSchedulesListTOWithTemp();
	}

	@Transactional
	@Override
	public List<Schedule> getScheduleByNumContractAndNUmStockWithTemp(Long num, Integer numStock) {
		return scheduleDAO.getScheduleByNumContractAndNUmStockWithTemp(num, numStock);
	}

	@Transactional
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

	@Transactional
	@Override
	public List<Schedule> getSchedulesByTOTypeWithTemp(String toType) {
		return scheduleDAO.getSchedulesByTOTypeWithTemp(toType);
	}

	@Transactional
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

	@Transactional
	@Override
	public List<Schedule> getSchedulesListTOContractOnlyTemp(Long num) {
		return scheduleDAO.getSchedulesListTOContractOnlyTemp(num);
	}

	@Transactional
	@Override
	public List<Schedule> getSchedulesListTOAll() {
		return scheduleDAO.getSchedulesListTOAll();
	}

	@Transactional
	@Override
	public List<CounterpartyDTO> getСounterpartyListRCNameOnly() {
		return scheduleDAO.getСounterpartyListRCNameOnly();
	}

	@Transactional
	@Override
	public List<Schedule> getAllSchedulesByNumContractAndNumStock(Long num, Integer shock) {
		return scheduleDAO.getAllSchedulesByNumContractAndNumStock(num, shock);
	}

	@Transactional
	@Override
	public ScheduleCountOrderDTO getCountScheduleOrderHasWeek() {
		return scheduleDAO.getCountScheduleOrderHasWeek();
	}

	@Transactional
	@Override
	public ScheduleCountOrderDTO getCountScheduleDeliveryHasWeek() {
		return scheduleDAO.getCountScheduleDeliveryHasWeek();
	}

	@Transactional
	@Override
	public Map<Long, Schedule> getSchedulesRCbyContractNums(Set<Long> contractNums) {
		return scheduleDAO.getSchedulesRCbyContractNums(contractNums);
	}
}
