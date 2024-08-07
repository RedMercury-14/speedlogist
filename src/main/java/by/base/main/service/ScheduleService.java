package by.base.main.service;

import java.util.List;

import by.base.main.model.Schedule;

public interface ScheduleService {

	Schedule getScheduleById(Integer id);
	
	List<Schedule> getSchedules();
		
	Schedule getScheduleByNumContract(Long num);
		
	Integer saveSchedule (Schedule schedule);
	
	void updateSchedule (Schedule schedule);
	
	void deleteOrderById(Integer id);
}
