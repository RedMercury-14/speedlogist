package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public List<Schedule> getSchedules() {
		// TODO Auto-generated method stub
		return scheduleDAO.getSchedules();
	}

	@Override
	public Schedule getScheduleByNumContract(Integer num) {
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

}
