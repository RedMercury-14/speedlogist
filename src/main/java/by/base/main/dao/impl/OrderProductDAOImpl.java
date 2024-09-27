package by.base.main.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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

	private static final String queryGetOrderProductListHasDate = "from OrderProduct where dateCreate BETWEEN :dateStart and :dateEnd";
	@Transactional
	@Override
	public List<OrderProduct> getOrderProductListHasDate(Date date) {
		Session currentSession = sessionFactory.getCurrentSession();
		Timestamp start = Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(date.toString()), LocalTime.of(00, 00)));
		Timestamp end = Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(date.toString()), LocalTime.of(23, 59)));
		Query<OrderProduct> theObject = currentSession.createQuery(queryGetOrderProductListHasDate, OrderProduct.class);
		theObject.setParameter("dateStart", start, TemporalType.TIMESTAMP);
		theObject.setParameter("dateEnd", end, TemporalType.TIMESTAMP);
		List<OrderProduct> trucks = theObject.getResultList();
		return trucks;
	}

	private static final String queryGetOrderProductListHasCodeProductAndPeriod = "from OrderProduct where codeProduct =:codeProduct AND dateCreate BETWEEN :dateStart and :dateEnd";
	@Transactional
	@Override
	public List<OrderProduct> getOrderProductListHasCodeProductAndPeriod(Integer codeProduct, Date start, Date finish) {
		Session currentSession = sessionFactory.getCurrentSession();
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(start.toString()), LocalTime.of(00, 00)));
		Timestamp endTime = Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(finish.toString()), LocalTime.of(23, 59)));
		Query<OrderProduct> theObject = currentSession.createQuery(queryGetOrderProductListHasCodeProductAndPeriod, OrderProduct.class);
		theObject.setParameter("dateStart", startTime, TemporalType.TIMESTAMP);
		theObject.setParameter("dateEnd", endTime, TemporalType.TIMESTAMP);
		theObject.setParameter("codeProduct", codeProduct);
		Set<OrderProduct> trucks = new HashSet<OrderProduct>(theObject.getResultList());
		return new ArrayList<OrderProduct>(trucks);
	}

}
