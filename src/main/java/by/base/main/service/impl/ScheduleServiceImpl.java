package by.base.main.service.impl;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dto.CounterpartyDTO;

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
	public List<Schedule> getSchedulesListTO() {
		return scheduleDAO.getSchedulesListTO();
	}

	@Override
	public List<Schedule> getSchedulesListTOContract(String contractCode) {
		return scheduleDAO.getSchedulesListTOContract(contractCode);
	}

	@Override
	public List<Schedule> getSchedulesListTOСounterparty(String counterpartyName) {
		return scheduleDAO.getSchedulesListTOСounterparty(counterpartyName);
	}

	@Override
	public List<Schedule> getSchedulesByTOType(String toType) {
		return scheduleDAO.getSchedulesByTOType(toType);
	}

	@Override
	public Schedule getScheduleByNumContractAndNUmStock(Long num, Integer numStock) {
		return scheduleDAO.getScheduleByNumContractAndNUmStock(num, numStock);
	}

	@Override
	public List<CounterpartyDTO> getcounterpartyList() {
		return scheduleDAO.getcounterpartyList();
	}

}
