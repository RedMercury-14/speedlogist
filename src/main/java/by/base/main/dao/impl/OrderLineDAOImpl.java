package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.OrderLineDAO;
import by.base.main.model.Order;
import by.base.main.model.OrderLine;

@Repository
public class OrderLineDAOImpl implements OrderLineDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetList = "from OrderLine ol order by ol.id";

	@Transactional
	@Override
	public List<OrderLine> getAllOrderLineList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<OrderLine> theRole = currentSession.createQuery(queryGetList, OrderLine.class);
		List <OrderLine> roles = theRole.getResultList();
		return roles;
	}

	@Transactional
	@Override
	public Integer saveOrderLine(OrderLine orderLine) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(orderLine);
		return Integer.parseInt(currentSession.getIdentifier(orderLine).toString());
	}

	private static final String queryGetObjByCode = "from OrderLine ol where ol.id=:id";
	@Transactional
	@Override
	public OrderLine getOrderLineById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<OrderLine> theObject = currentSession.createQuery(queryGetObjByCode, OrderLine.class);
		theObject.setParameter("id", id);
		List<OrderLine> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		OrderLine object = trucks.stream().findFirst().get();
		return object;
	}

	@Transactional
	@Override
	public void updateProduct(OrderLine orderLine) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(orderLine);
	}

	private static final String queryDeleteById = "delete from OrderLine where order=:order";
	@Transactional
	@Override
	public void deleteOrderLineByOrder(Order order) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteById);
		theQuery.setParameter("order", order);
		theQuery.executeUpdate();
	}

	
}
