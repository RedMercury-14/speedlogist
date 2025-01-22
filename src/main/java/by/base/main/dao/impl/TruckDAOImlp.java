package by.base.main.dao.impl;

import java.time.temporal.Temporal;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.TruckDAO;
import by.base.main.model.Route;
import by.base.main.model.Truck;
import by.base.main.model.User;

@Repository
public class TruckDAOImlp implements TruckDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from Truck tr LEFT JOIN FETCH tr.user u LEFT JOIN FETCH tr.routes r order by tr.idTruck";
	@Override
	public List<Truck> getTruckList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Truck> theObject = currentSession.createQuery(queryGetList, Truck.class);
		List <Truck> objects = theObject.getResultList();
		return objects;
	}

	@Override
	public void saveOrUpdateTruck(Truck truck) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(truck);
		
	}

	private static final String queryGetObjById = "from Truck tr LEFT JOIN FETCH tr.user u LEFT JOIN FETCH tr.routes r where tr.idTruck=:idTruck";
	@Override
	public Truck getTruckById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Truck> theObject = currentSession.createQuery(queryGetObjById, Truck.class);
		theObject.setParameter("idTruck", id);
		List<Truck> trucks = theObject.getResultList();
		Truck object = trucks.stream().findFirst().get();
		return object;
	}

	private static final String queryGetListObj = "from Truck tr LEFT JOIN FETCH tr.user u LEFT JOIN FETCH tr.routes r where tr.numTruck=:setNumTruck";
	@Override
	public Truck getTruckByNum(String login) {
		try {
			Session currentSession = sessionFactory.getCurrentSession();		
			Query<Truck> theObject = currentSession.createQuery(queryGetListObj, Truck.class);
			theObject.setParameter("setNumTruck", login);		
			List<Truck> trucks = theObject.getResultList();	
			Truck object = trucks.stream().findFirst().get();	
			return object;
		} catch (Exception e) {
			return null;
		}
		
	}

	private static final String queryDeleteById = "delete from Truck where idTruck=:setId";
	@Override
	public void deleteTruckById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteById);
		theQuery.setParameter("setId", id);
		theQuery.executeUpdate();	
		
	}

	private static final String queryDeleteByLogin = "delete from Truck where numTruck=:setLogin";
	@Override
	public void deleteTruckByNum(String login) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteByLogin);
		theQuery.setParameter("setLogin", login);
		theQuery.executeUpdate();
		
	}
	
	private static final String queryGetListObjByUser = "from Truck tr LEFT JOIN FETCH tr.routes r where tr.user=:user";
	@Override
	public List<Truck> getTruckListByUser(User user) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<Truck> theObject = currentSession.createQuery(queryGetListObjByUser, Truck.class);
		theObject.setParameter("user", user);		
		List<Truck> trucks = theObject.getResultList();		
		return trucks;
	}

	@Override
	public Truck saveNewTruck(Truck truck) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(truck);
		truck.setIdTruck(Integer.parseInt(currentSession.getIdentifier(truck).toString()));
		return truck;
	}

	@Override
	public void updateTruck(Truck truck) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(truck);		
	}

}
