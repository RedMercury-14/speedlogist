package by.base.main.dao.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TemporalType;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import by.base.main.dao.OrderDAO;
import by.base.main.model.Address;
import by.base.main.model.Order;
import by.base.main.model.Route;

@Repository
public class OrderDAOImpl implements OrderDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetObjByIdOrder = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs  LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where o.idOrder=:idOrder";
	@Transactional
	@Override
	public Order getOrderById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetObjByIdOrder, Order.class);
		theObject.setParameter("idOrder", id);
		List<Order> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		Order object = trucks.stream().findFirst().get();
		return object;
	}
	
	private static final String queryGetObjByDate = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where o.dateCreate=:date";
	@Transactional
	@Override
	public List<Order> getOrderByDateCreate(Date date) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetObjByDate, Order.class);
		theObject.setParameter("date", date, TemporalType.DATE);
		List<Order> trucks = theObject.getResultList();		
		return trucks;
	}

	private static final String queryGetObjByDateDelivery = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where o.dateDelivery=:date";
	@Transactional
	@Override
	public List<Order> getOrderByDateDelivery(Date date) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetObjByDateDelivery, Order.class);
		theObject.setParameter("date", date, TemporalType.DATE);
		List<Order> trucks = theObject.getResultList();		
		return trucks;
	}

	private static final String queryGetObjByRoute = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where r=:route";
	@Transactional
	@Override
	public Order getOrderByRoute(Route route) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetObjByRoute, Order.class);
		theObject.setParameter("route", route);
		List<Order> trucks = theObject.getResultList();
		if(!trucks.isEmpty()) {
			Order object = trucks.stream().findFirst().get();	
			return object;
		}else {
			return null;
		}		
	}

	private static final String queryGetObjByPeriodDelivery = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where o.dateDelivery BETWEEN :dateStart and :dateEnd";
	@Transactional
	@Override
	public List<Order> getOrderByPeriodDelivery(Date dateStart, Date dateEnd) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetObjByPeriodDelivery, Order.class);
		theObject.setParameter("dateStart", dateStart, TemporalType.DATE);
		theObject.setParameter("dateEnd", dateEnd, TemporalType.DATE);
		List<Order> trucks = theObject.getResultList();
		return trucks;
	}

	private static final String queryGetObjByPeriodCreate = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH r.truck t LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH o.addresses a where o.dateCreate BETWEEN :dateStart and :dateEnd";
	@Transactional
	@Override
	public List<Order> getOrderByPeriodCreate(Date dateStart, Date dateEnd) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetObjByPeriodCreate, Order.class);
		theObject.setParameter("dateStart", dateStart, TemporalType.DATE);
		theObject.setParameter("dateEnd", dateEnd, TemporalType.DATE);
		List<Order> trucks = theObject.getResultList();
		return trucks;
	}
	
	@Transactional
	@Override
	public Integer saveOrder(Order order) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(order);
		return Integer.parseInt(currentSession.getIdentifier(order).toString());
	}
	
	@Transactional
	@Override
	public void updateOrder(Order order) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(order);		
	}

	private static final String queryUpdate = "UPDATE Order SET status =:status where idOrder=:idOrder";
	@Transactional
	@Override
	public int updateOrderFromStatus(Order order) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryUpdate);
		query.setParameter("idOrder", order.getIdOrder());
		query.setParameter("status", order.getStatus());
		int result = query.executeUpdate();
		return result;
	}

	
	@Transactional
	@Override
	public List<Order> getOrderByPeriodCreateAndCounterparty(Date dateStart, Date dateEnd, String counterparty) {
		final String queryGetOrderByPeriodCreateAndCounterparty = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a LEFT JOIN FETCH r.truck t LEFT JOIN FETCH r.roteHasShop rhs where o.dateCreate BETWEEN :dateStart and :dateEnd and o.counterparty LIKE '%"+counterparty+"%'";
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetOrderByPeriodCreateAndCounterparty, Order.class);
		theObject.setParameter("dateStart", dateStart, TemporalType.DATE);
		theObject.setParameter("dateEnd", dateEnd, TemporalType.DATE);
		List<Order> trucks = theObject.getResultList();
		return trucks;
	}

	
	private static final String queryGetObjByIdRoute = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where r.idRoute=:idRoute";
	@Transactional
	@Override
	public Order getOrderByIdRoute(Integer idRoute) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetObjByIdRoute, Order.class);
		theObject.setParameter("idRoute", idRoute);
		List<Order> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}else {
			Order object = trucks.stream().findFirst().get();		
			return object;
		}		
	}

	private static final String queryDeleteById = "delete from Order o where o.idOrder=:idOrder";
	@Transactional
	@Override
	public void deleteOrderById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteById);
		theQuery.setParameter("idOrder", id);
		theQuery.executeUpdate();
	}

	private static final String queryCheckOrderHasMarketCode = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where o.marketNumber=:marketNumber and o.status != 10";
	@Transactional
	@Override
	public boolean checkOrderHasMarketCode(String code) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryCheckOrderHasMarketCode, Order.class);
		theObject.setParameter("marketNumber", code);
		List<Order> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return false;
		}else {	
			return true;
		}
	}

	private static final String queryGetOrderHasMarketCode = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where o.marketNumber=:marketNumber AND o.status != 10";
	@Transactional
	@Override
	public Order getOrderHasMarketCode(String code) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetOrderHasMarketCode, Order.class);
		theObject.setParameter("marketNumber", code);
		List<Order> trucks = theObject.getResultList();
		if(!trucks.isEmpty()) {
			return trucks.stream().findFirst().get();
		}else {
			return null;
		}
	}

	public static final Comparator<Order> comparatorTimeDeliveryBefore = (Order e1, Order e2) -> (e2.getTimeDelivery().hashCode() - e1.getTimeDelivery().hashCode());
	private static final String queryGetOrderBeforeTimeDeliveryHasStockAndRamp = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where o.timeDelivery BETWEEN :dateStart and :dateEnd AND o.idRamp=:idRamp AND o.numStockDelivery=:numStockDelivery AND o.status!=10";
	@Transactional
	@Override
	public Order getOrderBeforeTimeDeliveryHasStockAndRamp(Order order) {
		Session currentSession = sessionFactory.getCurrentSession();
		LocalDateTime localDateTime = order.getTimeDelivery().toLocalDateTime().minusDays(2);
		Timestamp dateStart = Timestamp.valueOf(localDateTime);
		Query<Order> theObject = currentSession.createQuery(queryGetOrderBeforeTimeDeliveryHasStockAndRamp, Order.class);
		theObject.setParameter("idRamp", order.getIdRamp());
		theObject.setParameter("numStockDelivery", order.getNumStockDelivery());
		theObject.setParameter("dateEnd", order.getTimeDelivery(), TemporalType.TIMESTAMP);		
		theObject.setParameter("dateStart", dateStart, TemporalType.TIMESTAMP);
		List<Order> trucks = theObject.getResultList();
		trucks.sort(comparatorTimeDeliveryBefore);
//		trucks.stream().filter(o-> !o.equals(order)).forEach(o-> System.out.println(o));
		if(!trucks.isEmpty()) {
			if(trucks.get(0).equals(order) && trucks.size()==1) {
				return null;
			}else {
				List<Order> orders = trucks.stream().filter(o-> !o.equals(order)).filter(o-> o.getStatus() != 10).collect(Collectors.toList());
				orders.sort(comparatorTimeDeliveryBefore);
				if(orders.isEmpty()) {
					return null;
				}else {
					return orders.get(0);
				}
			}
		}else {
			return null;
		}
	}

	public static final Comparator<Order> comparatorTimeDeliveryAfter = (Order e1, Order e2) -> (e1.getTimeDelivery().hashCode() - e2.getTimeDelivery().hashCode());
	private static final String queryGetOrderAfterTimeDeliveryHasStockAndRamp = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where o.timeDelivery BETWEEN :dateStart and :dateEnd AND o.idRamp=:idRamp AND o.numStockDelivery=:numStockDelivery AND o.status!=10";
	@Transactional
	@Override
	public Order getOrderAfterTimeDeliveryHasStockAndRamp(Order order) {
		Session currentSession = sessionFactory.getCurrentSession();
		LocalDateTime localDateTime = order.getTimeDelivery().toLocalDateTime().plusDays(2);
		Timestamp dateStart = Timestamp.valueOf(localDateTime);
		Query<Order> theObject = currentSession.createQuery(queryGetOrderAfterTimeDeliveryHasStockAndRamp, Order.class);
		theObject.setParameter("idRamp", order.getIdRamp());
		theObject.setParameter("numStockDelivery", order.getNumStockDelivery());
		theObject.setParameter("dateStart", order.getTimeDelivery(), TemporalType.TIMESTAMP);		
		theObject.setParameter("dateEnd", dateStart, TemporalType.TIMESTAMP);
		List<Order> trucks = theObject.getResultList();
		trucks.sort(comparatorTimeDeliveryAfter);
//		trucks.stream().filter(o-> !o.equals(order)).forEach(o-> System.out.println(o));
		if(!trucks.isEmpty()) {
//			trucks.forEach(o-> Sys tem.out.println(o));
			if(trucks.get(0).equals(order) && trucks.size()==1) {
				return null;
			}else {
				List<Order> orders = trucks.stream().filter(o-> !o.equals(order)).filter(o-> o.getStatus() != 10).collect(Collectors.toList());
				orders.sort(comparatorTimeDeliveryAfter);
				if(orders.isEmpty()) {
					return null;
				}else {
					return orders.get(0);
				}
				
			}			
		}else {
			return null;
		}
	}

	private static final String queryGetOrderByMarketNumber = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a where o.marketNumber=:marketNumber AND o.status!=10";
	@Transactional
	@Override
	public Order getOrderByMarketNumber(String number) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetOrderByMarketNumber, Order.class);
		theObject.setParameter("marketNumber", number);
		List<Order> trucks = theObject.getResultList();
		if(!trucks.isEmpty()) {
			return trucks.get(0);
		}else {
			return null;
		}
	}

	private static final String queryGetOrderByPeriodCreateMarket = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH r.truck t LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH o.addresses a where o.dateCreateMarket BETWEEN :dateStart and :dateEnd AND o.dateCreate=NULL";
	@Transactional
	@Override
	public List<Order> getOrderByPeriodCreateMarket(Date dateStart, Date dateEnd) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetOrderByPeriodCreateMarket, Order.class);
		theObject.setParameter("dateStart", dateStart, TemporalType.DATE);
		theObject.setParameter("dateEnd", dateEnd, TemporalType.DATE);
		List<Order> trucks = theObject.getResultList();
		return trucks;
	}

	private static final String queryGetOrderByTimeDelivery = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH r.truck t LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH o.addresses a where o.status !=10 AND o.timeDelivery BETWEEN :dateStart and :dateEnd";
	@Transactional
	@Override
	public List<Order> getOrderByTimeDelivery(Date dateStart, Date dateEnd) {
		Timestamp dateStartFinal = Timestamp.valueOf(LocalDateTime.of(dateStart.toLocalDate(), LocalTime.of(00, 00)));
		Timestamp dateEndFinal = Timestamp.valueOf(LocalDateTime.of(dateEnd.toLocalDate(), LocalTime.of(23, 59)));
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetOrderByTimeDelivery, Order.class);
		theObject.setParameter("dateStart", dateStartFinal, TemporalType.TIMESTAMP);
		theObject.setParameter("dateEnd", dateEndFinal, TemporalType.TIMESTAMP);
		Set<Order> trucks = theObject.getResultList().stream().collect(Collectors.toSet());
		return trucks.stream().collect(Collectors.toList());
	}

	@Transactional
	@Override
	public Set<Order> getOrderListHasDateAndStockFromSlots(Date dateTarget, String stockTarget) {
		final String queryGetSummPallInStock = "from Order o LEFT JOIN FETCH o.routes r LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.user ru LEFT JOIN FETCH r.truck rt LEFT JOIN FETCH r.driver rd LEFT JOIN FETCH o.addresses a LEFT JOIN FETCH r.truck t LEFT JOIN FETCH r.roteHasShop rhs where o.timeDelivery BETWEEN :dateStart and :dateEnd and o.status !=10 AND o.idRamp LIKE '%"+stockTarget+"%'";
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Order> theObject = currentSession.createQuery(queryGetSummPallInStock, Order.class);
		LocalDateTime start = LocalDateTime.of(dateTarget.toLocalDate(), LocalTime.of(00, 00, 00));
		LocalDateTime finish = LocalDateTime.of(dateTarget.toLocalDate(), LocalTime.of(23, 59, 59));
		theObject.setParameter("dateStart", Timestamp.valueOf(start), TemporalType.TIMESTAMP);
		theObject.setParameter("dateEnd", Timestamp.valueOf(finish), TemporalType.TIMESTAMP);
		Set<Order> trucks = theObject.getResultList().stream().collect(Collectors.toSet());
		if(!trucks.isEmpty()) {
			return trucks;
		}else {
			return null;
		}
		
	}


}
