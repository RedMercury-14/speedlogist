package by.base.main.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.TGTruckDAO;
import by.base.main.model.TGTruck;

@Repository
public class TGTruckDAOImpl  implements TGTruckDAO {

	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from TGTruck tr order by tr.idTGTruck";
	@Override
	@Transactional
	public List<TGTruck> getTGTruckList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<TGTruck> theObject = currentSession.createQuery(queryGetList, TGTruck.class);
		List <TGTruck> objects = theObject.getResultList();
		return objects;
	}

	@Transactional
	@Override
	public Integer saveOrUpdateTGTruck(TGTruck tgTruck) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(tgTruck);
		return Integer.parseInt(currentSession.getIdentifier(tgTruck).toString());
	}

	private static final String queryGetListObjByUser = "from TGTruck tr where tr.chatIdUserTruck=:chatId";
	@Override
	@Transactional
	public List<TGTruck> getTGTruckByChatIdUser(long chatId) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<TGTruck> theObject = currentSession.createQuery(queryGetListObjByUser, TGTruck.class);
		theObject.setParameter("chatId", chatId);		
		Set<TGTruck> trucks = theObject.getResultList().stream().collect(Collectors.toSet());		
		return new ArrayList<TGTruck>(trucks);
	}
}
