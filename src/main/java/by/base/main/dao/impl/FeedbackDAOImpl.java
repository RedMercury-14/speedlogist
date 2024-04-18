package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.FeedbackDAO;
import by.base.main.model.Feedback;
import by.base.main.model.Shop;
import by.base.main.model.User;

@Repository
public class FeedbackDAOImpl implements FeedbackDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from Feedback order by idFeedback";
	@Override
	@Transactional
	public List<Feedback> getFeedbackList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Feedback> theObject = currentSession.createQuery(queryGetList, Feedback.class);
		List <Feedback> objects = theObject.getResultList();
		return objects;
	}

	@Override
	@Transactional
	public void saveOrUpdateFeedback(Feedback feedback) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(feedback);
		
	}

	private static final String queryGetListDriver = "from Feedback where user_iduser=:id";
	@Override
	@Transactional
	public List<Feedback> getFeedbackAsDriver(User driver) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Feedback> theObject = currentSession.createQuery(queryGetListDriver, Feedback.class);
		theObject.setParameter("id", driver.getIdUser());		
		List<Feedback> feedbacks = theObject.getResultList();
		return feedbacks;
	}

	private static final String queryGetListShop = "from Feedback where shop_numshop=:id";
	@Override
	@Transactional
	public List<Feedback> getFeedbackAsShop(Shop shop) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Feedback> theObject = currentSession.createQuery(queryGetListShop, Feedback.class);
		theObject.setParameter("id", shop.getNumshop());		
		List<Feedback> feedbacks = theObject.getResultList();
		return feedbacks;
	}

	@Override
	@Transactional
	public Feedback getFeedbackById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Feedback object = currentSession.get(Feedback.class, id);
		currentSession.flush();
		return object;
	}

	private static final String queryGetListFROM = "from Feedback where from=:id";
	@Override
	@Transactional
	public List<Feedback> getFeedbackFROM(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Feedback> theObject = currentSession.createQuery(queryGetListFROM, Feedback.class);
		theObject.setParameter("id", id);		
		List<Feedback> feedbacks = theObject.getResultList();
		return feedbacks;
	}

	private static final String queryDeleteById = "delete from Feedback where idFeedback=:id";
	@SuppressWarnings("rawtypes")
	@Override
	@Transactional
	public void deleteFeedbackById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteById);
		theQuery.setParameter("id", id);
		theQuery.executeUpdate();		
	}

	private static final String queryGetListByRHS = "from Feedback where idRouteHasShop=:id and status IS NULL";
	@Override
	@Transactional
	public List<Feedback> getFeedbackListByRHS(int idRHS) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Feedback> theObject = currentSession.createQuery(queryGetListByRHS, Feedback.class);
		theObject.setParameter("id", idRHS);		
		List<Feedback> feedbacks = theObject.getResultList();
		return feedbacks;
	}

}
