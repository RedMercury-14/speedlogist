package by.base.main.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.TaskDAO;
import by.base.main.model.Task;
import by.base.main.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService{

	@Autowired
	private TaskDAO taskDAO;
	
	@Override
	public Task getTaskById(Integer id) {
		return taskDAO.getTaskById(id);
	}

	@Override
	public Task getLastTaskFor398() {
		return taskDAO.getLastTaskFor398();
	}

	@Override
	public Integer saveTask(Task task) {
		return taskDAO.saveTask(task);
	}

	@Override
	public void updateTask(Task task) {
		taskDAO.updateTask(task);
		
	}

}
