package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.OrderProductDAO;
import by.base.main.model.OrderProduct;
@Repository
public class OrderProductDAOImpl implements OrderProductDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from OrderProduct order by idOrderProduct";
	@Transactional
	@Override
	public List<OrderProduct> getAllOrderProductList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<OrderProduct> theRole = currentSession.createQuery(queryGetList, OrderProduct.class);
		List <OrderProduct> roles = theRole.getResultList();
		return roles;
	}

	@Transactional
	@Override
	public Integer saveOrderProduct(OrderProduct orderProduct) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(orderProduct);
		return Integer.parseInt(currentSession.getIdentifier(orderProduct).toString());
	}

	
	private static final String queryGetObjByCode = "from OrderProduct where idOrderProduct=:idOrderProduct";
	@Transactional
	@Override
	public OrderProduct getOrderProductById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<OrderProduct> theObject = currentSession.createQuery(queryGetObjByCode, OrderProduct.class);
		theObject.setParameter("idOrderProduct", id);
		List<OrderProduct> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		OrderProduct object = trucks.stream().findFirst().get();
		return object;
	}

	@Transactional
	@Override
	public void updateOrderProduct(OrderProduct orderProduct) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(orderProduct);	
	}

}
