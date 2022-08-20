package by.base.main.dao.impl;

import java.sql.Date;
import java.util.List;

import javax.persistence.TemporalType;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.RouteDAO;
import by.base.main.model.Route;
import by.base.main.model.User;

@Repository
public class RouteDAOImpl implements RouteDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from Route order by idroute";
	@Override
	@Transactional
	public List<Route> getRouteList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetList, Route.class);
		List <Route> objects = theObject.getResultList();
		return objects;
	}

	@Override
	@Transactional
	public void saveOrUpdateRoute(Route route) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(route);
		
	}

	@Override
	@Transactional
	public Route getRouteById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Route object = currentSession.get(Route.class, id);
		currentSession.flush();
		return object;
	}
	private static final String queryGetListObj = "from Route r where routeDirection=:setNumTruck";
	@Override
	@Transactional
	public Route getRouteByDirection(String login) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<Route> theObject = currentSession.createQuery(queryGetListObj, Route.class);
		theObject.setParameter("setNumTruck", login);		
		List<Route> trucks = theObject.getResultList();	
		Route object = trucks.stream().findFirst().get();	
		return object;
	}

	private static final String queryDeleteById = "delete from Route where idroute=:setId";
	@Override
	@Transactional
	public void deleteRouteById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteById);
		theQuery.setParameter("setId", id);
		theQuery.executeUpdate();	
		
	}

	private static final String queryDeleteByLogin = "delete from Route where routeDirection=:setLogin";
	@Override
	@Transactional
	public void deleteRouteByDirection(String login) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteByLogin);
		theQuery.setParameter("setLogin", login);
		theQuery.executeUpdate();
		
	}

	private static final String queryGetLast = "SELECT * FROM route ORDER BY idroute DESC LIMIT 1";
	@Override
	@Transactional
	public Route getLastRoute() {
		Session currentSession = sessionFactory.getCurrentSession();		
		List<Route> theObject = currentSession.createSQLQuery(queryGetLast).addEntity(Route.class).list();
		Route object = theObject.stream().findFirst().get();	
		return object;
	}

	private static final String queryGetListAsDate = "from Route where dateLoadPreviously BETWEEN :frmdate and :todate";
	@Override
	@Transactional
	public List<Route> getRouteListAsDate(Date dateStart, Date dateFinish) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListAsDate, Route.class);
		theObject.setParameter("frmdate", dateStart, TemporalType.DATE);
		theObject.setParameter("todate", dateFinish, TemporalType.DATE);	
		List <Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String queryGetListAsDateAndStatus = "from Route where dateLoadPreviously BETWEEN :frmdate and :todate AND where statusRoute BETWEEN :frstat and :tostat";
	@Override
	@Transactional
	public List<Route> getRouteListAsDateAndStatus(Date dateStart, Date dateFinish, String stat1, String stat2) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListAsDateAndStatus, Route.class);
		theObject.setParameter("frmdate", dateStart, TemporalType.DATE);
		theObject.setParameter("todate", dateFinish, TemporalType.DATE);
		theObject.setParameter("frstat", stat1);
		theObject.setParameter("tostat", stat2);
		List <Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String queryGetListAsStatus = "from Route where statusRoute BETWEEN :frstat and :tostat";
	@Override
	@Transactional
	public List<Route> getRouteListAsStatus(String stat1, String stat2) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListAsStatus, Route.class);
		theObject.setParameter("frstat", stat1);
		theObject.setParameter("tostat", stat2);
		List <Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String queryGetListObjByUser = "from Route where user_iduser_manager=:user";
	@Override
	@Transactional
	public List<Route> getRouteListByUser(User user) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListObjByUser, Route.class);
		theObject.setParameter("user", user.getIdUser());	
		List <Route> objects = theObject.getResultList();
		return objects;
	}

}
