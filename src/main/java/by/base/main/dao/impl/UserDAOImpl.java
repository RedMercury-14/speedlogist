package by.base.main.dao.impl;

import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.TemporalType;
import javax.transaction.Transactional;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.UserDAO;
import by.base.main.model.Route;
import by.base.main.model.User;

@Repository
public class UserDAOImpl implements UserDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from User u LEFT JOIN FETCH u.route r LEFT JOIN FETCH u.roles rol LEFT JOIN FETCH u.trucks tr LEFT JOIN FETCH u.shop sh LEFT JOIN FETCH u.feedbackList f LEFT JOIN FETCH u.singleRoute SR order by u.idUser";
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

	private static final String queryGetObjById = "from User u LEFT JOIN FETCH u.route r LEFT JOIN FETCH u.roles rol LEFT JOIN FETCH u.trucks tr LEFT JOIN FETCH u.shop sh LEFT JOIN FETCH u.feedbackList f where u.idUser=:idUser";
	@Override
	@Transactional
	public User getUserById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theObject = currentSession.createQuery(queryGetObjById, User.class);
		theObject.setParameter("idUser", id);
		List<User> trucks = theObject.getResultList();
		User object = trucks.stream().findFirst().get();
		return object;
	}

	private static final String queryGetListUser = "from User u LEFT JOIN FETCH u.roles rol LEFT JOIN FETCH u.trucks tr where u.login=:setLogin";
	@Override
	@Transactional
	public User getUserByLogin(String login) {
		try {
			Session currentSession = sessionFactory.getCurrentSession();		
			Query<User> theUser = currentSession.createQuery(queryGetListUser, User.class);
			theUser.setParameter("setLogin", login);		
			List<User> users = theUser.getResultList();	
			User user = users.stream().findFirst().get();
//			System.out.println(currentSession.getStatistics());
//			currentSession.getStatistics().getCollectionKeys().forEach(k-> System.out.println(k));
//			currentSession.getStatistics().getEntityKeys().forEach(k-> System.out.println(k));
			currentSession.clear(); // тест! в этом методе пиздобратия происходит.
			
//			System.out.println("==============");
//			System.out.println(currentSession.getStatistics());
//			currentSession.getStatistics().getCollectionKeys().forEach(k-> System.out.println(k));
//			currentSession.getStatistics().getEntityKeys().forEach(k-> System.out.println(k));
			
			return user;	
		} catch (NoSuchElementException e) {
			return null;
		}
		
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

	private static final String queryGetDriverList = "from User u LEFT JOIN FETCH u.route r LEFT JOIN FETCH u.roles rol LEFT JOIN FETCH u.trucks tr LEFT JOIN FETCH u.shop sh LEFT JOIN FETCH u.feedbackList f where u.isDriver=1 and u.companyName =:companyName";
	@Override
	@Transactional
	public List<User> getDriverList(String companyName) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theRole = currentSession.createQuery(queryGetDriverList, User.class);
		theRole.setParameter("companyName", companyName);
		List <User> users = theRole.getResultList();
		return users;
	}

	private static final String queryGetUserCard = "from User u LEFT JOIN FETCH u.route r LEFT JOIN FETCH u.roles rol LEFT JOIN FETCH u.trucks tr LEFT JOIN FETCH u.shop sh LEFT JOIN FETCH u.feedbackList f where u.numDriverCard=:num";
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

	private static final String queryGetCarrierList = "from User u LEFT JOIN FETCH u.route r LEFT JOIN FETCH u.roles rol LEFT JOIN FETCH u.trucks tr LEFT JOIN FETCH u.shop sh LEFT JOIN FETCH u.feedbackList f where u.isDriver=0 and u.numYNP IS NOT NULL and u.enablet=1";
	@Override
	@Transactional
	public List<User> getCarrierList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theRole = currentSession.createQuery(queryGetCarrierList, User.class);
		List <User> users = theRole.getResultList();
		return users;
	}

	private static final String queryGetDesableCarrierList = "from User u LEFT JOIN FETCH u.route r LEFT JOIN FETCH u.roles rol LEFT JOIN FETCH u.trucks tr LEFT JOIN FETCH u.shop sh LEFT JOIN FETCH u.feedbackList f where u.isDriver=0 and u.numYNP IS NOT NULL and u.enablet=0";
	@Override
	@Transactional
	public List<User> getDesableCarrierList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theRole = currentSession.createQuery(queryGetDesableCarrierList, User.class);
		List <User> users = theRole.getResultList();
		return users;
	}

	private static final String queryGetListUserYNP = "from User u LEFT JOIN FETCH u.route r LEFT JOIN FETCH u.roles rol LEFT JOIN FETCH u.trucks tr LEFT JOIN FETCH u.shop sh LEFT JOIN FETCH u.feedbackList f where u.numYNP=:setNumYNP";
	@Override
	@Transactional
	public List<User> getUserByYNP(String YNP) {
		try {
			Session currentSession = sessionFactory.getCurrentSession();		
			Query<User> theUser = currentSession.createQuery(queryGetListUserYNP, User.class);
			theUser.setParameter("setNumYNP", YNP);		
			List<User> users = theUser.getResultList();		
			return users;
		} catch (NoSuchElementException e) {
			return null;
		}
		
	}

	private static final String queryGetCountUser = "select count(*) from User";
	@Override
	@Transactional
	public Integer getCountUserInDB() {
		Session currentSession = sessionFactory.getCurrentSession();	
		Query query = currentSession.createQuery(queryGetCountUser);
		Integer res = ((Number) query.uniqueResult()).intValue();
		return res;
	}

	private static final String queryGetCarrierListV2 = "from User u LEFT JOIN FETCH u.roles rol LEFT JOIN FETCH u.trucks tr where u.isDriver=0 and u.numYNP IS NOT NULL and u.enablet=1";
	@Override
	@Transactional
	public List<User> getCarrierListV2() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theRole = currentSession.createQuery(queryGetCarrierListV2, User.class);
		List <User> users = theRole.getResultList();
		return users;
	}

	private static final String queryUpdateDateDoute = "UPDATE User SET numContract =:text where idUser=:idUser";
	@Transactional
	@Override
	public int updateUserInBaseDocuments(int idUser, String text) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryUpdateDateDoute);
		query.setParameter("idUser", idUser);
		query.setParameter("text", text);
		int result = query.executeUpdate();
		return result;
	}

	@Transactional
	@Override
	public User saveNewDriver(User user) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(user);
		user.setIdUser(Integer.parseInt(currentSession.getIdentifier(user).toString()));
		return user;
	}

	private static final String queryGetEmployeesList = "from User u LEFT JOIN FETCH u.roles rol WHERE u.companyName=:text";
	@Override
	@Transactional
	public List<User> getEmployeesList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<User> theRole = currentSession.createQuery(queryGetEmployeesList, User.class);
		theRole.setParameter("text", "Доброном");
		List <User> users = theRole.getResultList();
		return users;
	}

}
