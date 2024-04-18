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

import by.base.main.dao.AddressDAO;
import by.base.main.model.Address;

@Repository
public class AddressDAOImpl implements AddressDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetAddressById = "from Address a LEFT JOIN FETCH a.order o where a.idAddress=:idAddress";
	@Transactional
	@Override
	public Address getAddressById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Address> theObject = currentSession.createQuery(queryGetAddressById, Address.class);
		theObject.setParameter("idAddress", id);
		List<Address> trucks = theObject.getResultList();
		return trucks.stream().findFirst().get();
	}

	private static final String queryGetObjByDate = "from Address a LEFT JOIN FETCH a.order o where o.date=:date";
	@Transactional
	@Override
	public List<Address> getAddressByDate(Date date) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Address> theObject = currentSession.createQuery(queryGetObjByDate, Address.class);
		theObject.setParameter("date", date, TemporalType.DATE);
		List<Address> trucks = theObject.getResultList();		
		return trucks;
	}
	
	@Transactional
	@Override
	public Integer saveAddress(Address address) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(address);
		return Integer.parseInt(currentSession.getIdentifier(address).toString());
	}
	
	@Transactional
	@Override
	public void updateAddress(Address address) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(address);		
	}

}
