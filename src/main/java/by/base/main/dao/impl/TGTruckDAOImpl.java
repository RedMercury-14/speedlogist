package by.base.main.dao.impl;

import java.sql.Date;
import java.time.LocalDate;
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
import by.base.main.model.TGUser;

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
		List<TGTruck> trucks = theObject.getResultList();			
		if(trucks.isEmpty()) {
			return null;
		}else {
			return new ArrayList<TGTruck>(theObject.getResultList().stream().collect(Collectors.toSet()));			
		}
	}

	private static final String queryGetTGTruckByChatNumTruck = "from TGTruck tr where tr.numTruck=:numTruck AND tr.dateRequisition=:date";
	@Transactional
	@Override
	public TGTruck getTGTruckByChatNumTruck(String numTruck, TGUser tgUser) {
		Date date;
		if(tgUser.getDateOrderTruckOptimization() != null) {
            date = tgUser.getDateOrderTruckOptimization();
        } else {
            date = Date.valueOf(LocalDate.now().plusDays(1));
        }
		System.out.println(date);
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<TGTruck> theObject = currentSession.createQuery(queryGetTGTruckByChatNumTruck, TGTruck.class);
		theObject.setParameter("numTruck", numTruck);	
		theObject.setParameter("date", date);	
		List<TGTruck> tgTrucks = theObject.getResultList();
		if(tgTrucks.isEmpty()) {
			return null;
		}else {
			TGTruck trucks = tgTrucks.stream().findFirst().get();		
			return trucks;
		}
		
	}

	private static final String queryDeleteByNumTruck = "delete from TGTruck where numTruck=:numTruck";
	@Override
	@Transactional
	public void deleteTGTruckByNumTruck(String numTruck) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteByNumTruck);
		theQuery.setParameter("numTruck", numTruck);
		theQuery.executeUpdate();
	}
}
