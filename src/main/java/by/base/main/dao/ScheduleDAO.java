package by.base.main.dao;

import java.util.List;

import by.base.main.model.Schedule;

public interface ScheduleDAO {

	Schedule getScheduleById(Integer id);
	
	List<Schedule> getSchedules();
		
	Schedule getScheduleByNumContract(Integer num);
		
	Integer saveOrder (Schedule schedule);
	
	void updateOrder (Schedule schedule);
	
	void deleteOrderById(Integer id);
	
}
