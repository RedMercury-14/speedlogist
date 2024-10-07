package by.base.main.dao.impl;

import java.util.List;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.TGUserDAO;
import by.base.main.model.TGUser;

@Repository
public class TGUserDAOImpl implements TGUserDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from TGUser u order by u.idTGUser";
	@Transactional
	@Override
	public List<TGUser> getUserList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<TGUser> theRole = currentSession.createQuery(queryGetList, TGUser.class);
		List <TGUser> users = theRole.getResultList();
		return users;
	}

	@Transactional
	@Override
	public Integer saveOrUpdateUser(TGUser tgUser) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(tgUser);
		return Integer.parseInt(currentSession.getIdentifier(tgUser).toString());
	}

	private static final String queryGetObjById = "from TGUser u where u.idTGUser=:idUser";
	@Override
	@Transactional
	public TGUser getTGUserById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<TGUser> theObject = currentSession.createQuery(queryGetObjById, TGUser.class);
		theObject.setParameter("idUser", id);
		List<TGUser> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}else {
			TGUser object = trucks.stream().findFirst().get();		
			return object;
		}
		
	}

	private static final String queryGetTGUserByChatId = "from TGUser u where u.chatId=:chatId";
	@Transactional
	@Override
	public TGUser getTGUserByChatId(long chatId) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<TGUser> theObject = currentSession.createQuery(queryGetTGUserByChatId, TGUser.class);
		theObject.setParameter("chatId", chatId);
		List<TGUser> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}else {
			TGUser object = trucks.stream().findFirst().get();
			return object;
		}
		
	}

}
