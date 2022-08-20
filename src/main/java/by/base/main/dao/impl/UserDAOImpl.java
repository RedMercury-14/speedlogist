package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.UserDAO;
import by.base.main.model.User;

@Repository
public class UserDAOImpl implements UserDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from User order by iduser";
	@Override
	@Transactional
	public List<User> getUserList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theRole = currentSession.createQuery(queryGetList, User.class);
		List <User> users = theRole.getResultList();
		return users;
	}

	@Override
	@Transactional
	public void saveOrUpdateUser(User user) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(user);
	}

	@Override
	@Transactional
	public User getUserById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		User user = currentSession.get(User.class, id);
		currentSession.flush(); // а надо ли?!
		return user;
	}

	private static final String queryGetListUser = "from User u where login=:setLogin";
	@Override
	@Transactional
	public User getUserByLogin(String login) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<User> theUser = currentSession.createQuery(queryGetListUser, User.class);
		theUser.setParameter("setLogin", login);		
		List<User> users = theUser.getResultList();	
		User user = users.stream().findFirst().get();	
		return user;
	}

	private static final String queryDeleteUserId = "delete from User where iduser=:setId";
	@Override
	@Transactional
	public void deleteUserById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteUserId);
		theQuery.setParameter("setId", id);
		theQuery.executeUpdate();		
	}
	
	private static final String queryDeleteUserLogin = "delete from User where login=:setLogin";
	@Override
	@Transactional
	public void deleteUserByLogin(String login) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteUserLogin);
		theQuery.setParameter("setLogin", login);
		theQuery.executeUpdate();
		
	}

	private static final String queryGetDriverList = "from User where isDriver=1 and companyName =:companyName";
	@Override
	@Transactional
	public List<User> getDriverList(String companyName) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theRole = currentSession.createQuery(queryGetDriverList, User.class);
		theRole.setParameter("companyName", companyName);
		List <User> users = theRole.getResultList();
		return users;
	}

	private static final String queryGetUserCard = "from User u where numDriverCard=:num";
	@Override
	@Transactional
	public User getUserByNumDriverCard(String num) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<User> theUser = currentSession.createQuery(queryGetUserCard, User.class);
		theUser.setParameter("num", num);		
		List<User> users = theUser.getResultList();	
		User user;
		try {
			user = users.stream().findFirst().get();
		} catch (Exception e) {
			return null;
		}
			
		return user;
	}

	private static final String queryGetCarrierList = "from User where isDriver=0 and numYNP IS NOT NULL and enablet=1";
	@Override
	@Transactional
	public List<User> getCarrierList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theRole = currentSession.createQuery(queryGetCarrierList, User.class);
		List <User> users = theRole.getResultList();
		return users;
	}

	private static final String queryGetDesableCarrierList = "from User where isDriver=0 and numYNP IS NOT NULL and enablet=0";
	@Override
	@Transactional
	public List<User> getDesableCarrierList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theRole = currentSession.createQuery(queryGetDesableCarrierList, User.class);
		List <User> users = theRole.getResultList();
		return users;
	}

}
