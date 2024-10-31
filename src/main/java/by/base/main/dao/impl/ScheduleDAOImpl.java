package by.base.main.dao.impl;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.ScheduleDAO;
import by.base.main.model.Order;
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

	
	private static final String queryGetListRC = "from Schedule where type='РЦ'";
	@Transactional
	@Override
	public List<Schedule> getSchedulesListRC() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListRC, Schedule.class);
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}
	
	private static final String queryGetListTO = "from Schedule where type='ТО'";
	@Transactional
	@Override
	public List<Schedule> getSchedulesListTO() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListTO, Schedule.class);
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


	@Transactional
	@Override
	public List<Schedule> getSchedulesByDateOrder(Date date, Integer numStock) {
		String dayName = DayOfWeek.from(date.toLocalDate()).getDisplayName(java.time.format.TextStyle.FULL, new Locale("en")).toLowerCase();		
		String queryGetSchedulesByDateOrder = "from Schedule where status=20 AND numStock=:numStock AND "+dayName+" LIKE '%з%'";
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetSchedulesByDateOrder, Schedule.class);
		theObject.setParameter("numStock", numStock);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		return trucks;
	}
	
	

}
