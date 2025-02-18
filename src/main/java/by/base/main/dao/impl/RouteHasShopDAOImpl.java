package by.base.main.dao.impl;

import java.util.List;

import javax.persistence.Transient;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.RouteHasShopDAO;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;

@Repository
public class RouteHasShopDAOImpl implements RouteHasShopDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from RouteHasShop rhs LEFT JOIN FETCH rhs.route r LEFT JOIN FETCH rhs.shop s order by rhs.idorder";
	@Override	
	public List<RouteHasShop> getRouteHasShopList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<RouteHasShop> theObject = currentSession.createQuery(queryGetList, RouteHasShop.class);
		List <RouteHasShop> objects = theObject.getResultList();
		return objects;
	}

	@Override	
	public void saveOrUpdateRouteHasShop(RouteHasShop obj) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(obj);
		
	}

	@Override	
	public RouteHasShop getRouteHasShopById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		RouteHasShop object = currentSession.get(RouteHasShop.class, id);
		currentSession.flush();
		return object;
	}

	private static final String queryDeleteById = "delete from RouteHasShop where idroute_has_shop=:setId";
	@Override	
	public void deleteRouteHasShopById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteById);
		theQuery.setParameter("setId", id);
		theQuery.executeUpdate();
		
	}
	
	private static final String queryGetListObj = "from RouteHasShop rhs LEFT JOIN FETCH rhs.route r LEFT JOIN FETCH rhs.shop s where rhs.order=:order";
	@Override		
	public RouteHasShop getRouteHasShopByNum(int id) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<RouteHasShop> theObject = currentSession.createQuery(queryGetListObj, RouteHasShop.class);
		theObject.setParameter("order", id);		
		List<RouteHasShop> RhS = theObject.getResultList();	
		RouteHasShop object = RhS.stream().findFirst().get();	
		return object;
	}

	private static final String queryGetListObjShop = "from RouteHasShop rhs LEFT JOIN FETCH rhs.route r LEFT JOIN FETCH rhs.shop s where rhs.shop_numshop=:numshop";
	@Override	
	public RouteHasShop getRouteHasShopByShop(int idShop) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<RouteHasShop> theObject = currentSession.createQuery(queryGetListObjShop, RouteHasShop.class);
		theObject.setParameter("numshop", idShop);		
		List<RouteHasShop> RhS = theObject.getResultList();	
		RouteHasShop object = RhS.stream().findFirst().get();	
		return object;
	}

	private static final String queryGetListObjShopAndRoute = "from RouteHasShop rhs LEFT JOIN FETCH rhs.route r LEFT JOIN FETCH rhs.shop s where rhs.shop_numshop=:numshop and rhs.route_idroute=:idroute";
	@Override	
	public RouteHasShop getRouteHasShopByShopAndRoute(int idShop, int idRoute) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<RouteHasShop> theObject = currentSession.createQuery(queryGetListObjShopAndRoute, RouteHasShop.class);
		theObject.setParameter("numshop", idShop);		
		theObject.setParameter("idroute", idRoute);	
		List<RouteHasShop> RhS = theObject.getResultList();	
		RouteHasShop object = RhS.stream().findFirst().get();	
		return object;
	}

}
