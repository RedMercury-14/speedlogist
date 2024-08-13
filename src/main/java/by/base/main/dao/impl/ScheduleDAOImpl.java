package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.ScheduleDAO;
import by.base.main.model.Order;
import by.base.main.model.Role;
import by.base.main.model.Schedule;

@Repository
public class ScheduleDAOImpl implements ScheduleDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetObjByIdOrder = "from Schedule where idSchedule=:idSchedule";
	@Transactional
	@Override
	public Schedule getScheduleById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetObjByIdOrder, Schedule.class);
		theObject.setParameter("idSchedule", id);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		Schedule object = trucks.stream().findFirst().get();
		return object;
	}

	
	private static final String queryGetList = "from Schedule order by idSchedule";
	@Transactional
	@Override
	public List<Schedule> getSchedules() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetList, Schedule.class);
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}

	private static final String queryGetObjByNumContract = "from Schedule where counterpartyContractCode=:counterpartyContractCode";
	@Transactional
	@Override
	public Schedule getScheduleByNumContract(Long num) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetObjByNumContract, Schedule.class);
		theObject.setParameter("counterpartyContractCode", num);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		Schedule object = trucks.stream().findFirst().get();
		return object;
	}

	@Transactional
	@Override
	public Integer saveOrder(Schedule schedule) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(schedule);
		return Integer.parseInt(currentSession.getIdentifier(schedule).toString());
	}

	@Transactional
	@Override
	public void updateOrder(Schedule schedule) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(schedule);
	}

	@Transactional
	@Override
	public void deleteOrderById(Integer id) {
		System.err.println("В разработке");
	}

	private static final String queryGetSchedulesByStock = "from Schedule where numStock=:numStock";
	@Transactional
	@Override
	public List<Schedule> getSchedulesByStock(Integer numStock) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetSchedulesByStock, Schedule.class);
		theObject.setParameter("numStock", numStock);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		return trucks;
	}
	
	

}
