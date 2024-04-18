package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.RatesDAO;
import by.base.main.model.Rates;
@Repository
public class RatesDAOImpl implements RatesDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from Rates order by idrates";
	@Override
	@Transactional
	public List<Rates> getRatesList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Rates> theObject = currentSession.createQuery(queryGetList, Rates.class);
		List <Rates> objects = theObject.getResultList();
		return objects;
	}

	@Override
	@Transactional
	public void saveOrUpdateRates(Rates rates) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(rates);
		
	}

	@Override
	@Transactional
	public Rates getRatesById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Rates object = currentSession.get(Rates.class, id);
		currentSession.flush();
		return object;
	}

	private static final String queryGetListObj = "from Rates where caste=:caste and type=изотерма";
	@Override
	@Transactional
	public List<Rates> getListRatesByCasteForIsoterm(String caste) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<Rates> theObject = currentSession.createQuery(queryGetListObj, Rates.class);
		theObject.setParameter("caste", caste);		
		List<Rates> trucks = theObject.getResultList();
		return trucks;
	}

	private static final String queryGetListObjRef = "from Rates where caste=:caste and type=рефрижератор";
	@Override
	@Transactional
	public List<Rates> getListRatesByCasteForRef(String caste) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Rates> theObject = currentSession.createQuery(queryGetListObjRef, Rates.class);
		theObject.setParameter("caste", caste);		
		List<Rates> trucks = theObject.getResultList();
		return trucks;
	}

	private static final String queryDeleteByLogin = "delete from Rates where idRates=:id";
	@Override
	@Transactional
	public void deleteRatesById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteByLogin);
		theQuery.setParameter("id", id);
		theQuery.executeUpdate();
		
	}

}
