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
import by.base.main.model.User;

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

	private static final String queryGetTGUserByMainUser = "from TGUser u where u.telephone LIKE :telephone";
	@Transactional
	@Override
	public TGUser getTGUserByMainUser(User user) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<TGUser> theObject = currentSession.createQuery(queryGetTGUserByMainUser, TGUser.class);
		String telephone = user.getTelephone().replaceAll("[^\\d]", ""); // используем только цифры!!!
		if (telephone.length() > 12) {
		    telephone = telephone.substring(0, 12); // Обрезаем до первых 12 символов
		}
		theObject.setParameter("telephone", "%" + telephone + "%");
		List<TGUser> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}else {
			TGUser object = trucks.stream().findFirst().get();
			return object;
		}
	}

	private static final String queryGetTGUserByIdUser = "from TGUser u where u.idUser=:idUser";
	@Transactional
	@Override
	public TGUser getTGUserByIdUser(Integer idUser) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<TGUser> theObject = currentSession.createQuery(queryGetTGUserByIdUser, TGUser.class);
		theObject.setParameter("idUser", idUser);
		List<TGUser> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}else {
			TGUser object = trucks.stream().findFirst().get();
			return object;
		}
	}

	private static final String queryGetTGUserByTelephone = "from TGUser u where u.telephone LIKE :telephone";
	@Transactional
	@Override
	public TGUser getTGUserByTelephone(String telephone) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<TGUser> theObject = currentSession.createQuery(queryGetTGUserByMainUser, TGUser.class);
		String telephone2 = telephone.replaceAll("[^\\d]", ""); // используем только цифры!!!
		if (telephone2.length() > 12) {
		    telephone2 = telephone2.substring(0, 12); // Обрезаем до первых 12 символов
		}
		theObject.setParameter("telephone", "%" + telephone + "%");
		List<TGUser> trucks = theObject.getResultList();
		
		if(trucks.isEmpty()) {
			return null;
		}else {
			if(trucks.size() > 1) {
				throw new DTOException("Результат вернулся > 1.");
			}else {
				TGUser object = trucks.stream().findFirst().get();
				return object;				
			}
		}
		
		
		
	}

}
