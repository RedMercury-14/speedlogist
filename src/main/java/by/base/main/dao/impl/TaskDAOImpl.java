package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.TaskDAO;
import by.base.main.model.Shop;
import by.base.main.model.Task;

@Repository
public class TaskDAOImpl implements TaskDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetTaskById = "from Task t where t.idTask=:idTask";
	@Transactional
	@Override
	public Task getTaskById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Task> theObject = currentSession.createQuery(queryGetTaskById, Task.class);
		theObject.setParameter("idTask", id);
		List<Task> trucks = theObject.getResultList();
		return trucks.stream().findFirst().get();
	}

	private static final String queryGetLastTaskFor398 = "from Task t where t.comment= '398' ORDER BY t.idTask DESC";
	@Transactional
	@Override
	public Task getLastTaskFor398() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Task> theObject = currentSession.createQuery(queryGetLastTaskFor398, Task.class);
		List<Task> trucks = theObject.getResultList();
		return trucks.stream().findFirst().get();
	}

	@Transactional
	@Override
	public Integer saveTask(Task task) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(task);
		return Integer.parseInt(currentSession.getIdentifier(task).toString());
	}

	@Transactional
	@Override
	public void updateTask(Task task) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(task);	
	}

	private static final String queryGetTaskList = "from Task t order by t.idTask DESC";
	@Override
	@Transactional
	public List<Task> getTaskList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Task> theObject = currentSession.createQuery(queryGetTaskList, Task.class);
		List <Task> objects = theObject.getResultList();
		return objects;
	}
	

}
