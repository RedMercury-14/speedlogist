package by.base.main.dao;

import java.util.List;

import by.base.main.model.Task;

public interface TaskDAO {
	
	List<Task> getTaskList();
	
	Task getTaskById(Integer id);
	
	/**
	 * Отдаёт последнюю таску для 398 отчётов
	 * отличает таски в столбце с комментом
	 * @return
	 */
	Task getLastTaskFor398();
	
	Integer saveTask(Task task);
	
	void updateTask(Task task);

}
