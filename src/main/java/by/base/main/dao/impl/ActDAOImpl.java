package by.base.main.dao.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.QueryHint;
import javax.persistence.TemporalType;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.ActDAO;
import by.base.main.model.Act;

@Repository
public class ActDAOImpl implements ActDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from Act order by idAct";
	@Override
	@Transactional
	public List<Act> getActList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Act> theObject = currentSession.createQuery(queryGetList, Act.class);
		List <Act> objects = theObject.getResultList();
		return objects;
	}

	@Override
	@Transactional
	public void saveOrUpdateAct(Act act) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(act);
		
	}

	@Override
	@Transactional
	public Act getActById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Act object = currentSession.get(Act.class, id);
		currentSession.flush();
		return object;
	}

	private static final String queryGetListByRHS = "from Act where numAct=:numAct";
	@Override
	@Transactional
	public List<Act> getActBynumAct(String id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Act> theObject = currentSession.createQuery(queryGetListByRHS, Act.class);
		theObject.setParameter("numAct", id);		
		List<Act> feedbacks = theObject.getResultList();
		return feedbacks;
	}

	private static final String queryGetListAsDate = "from Act where date BETWEEN :frmdate and :todate";
	@Override
	@Transactional
	public List<Act> getActListAsDate(Date dateStart, Date dateFinish) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Act> theObject = currentSession.createQuery(queryGetListAsDate, Act.class);
		theObject.setParameter("frmdate", dateStart, TemporalType.DATE);
		theObject.setParameter("todate", dateFinish, TemporalType.DATE);
		List <Act> objects = theObject.getResultList();
		return objects;
	}

	private static final String getActBySecretCode = "from Act where secretCode=:secretCode";
	@Override
	@Transactional
	public List<Act> getActBySecretCode(String code) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Act> theObject = currentSession.createQuery(getActBySecretCode, Act.class);
		theObject.setParameter("secretCode", code);		
		List<Act> feedbacks = theObject.getResultList();
		return feedbacks;
	}

	private static final String queryGetActsByRouteId = "from Act a where a.date between :dateStart and :dateFinish " +
		       "and a.documentsArrived is not null " +
		       "and a.idRoutes like :idRoute";
		@Override
		public List<Act> getActsByRouteId(String id, LocalDate startDate, LocalDate finishDate) {
		    Session currentSession = sessionFactory.getCurrentSession();
		    Query<Act> theObject = currentSession.createQuery(queryGetActsByRouteId, Act.class);
		    theObject.setParameter("dateStart", startDate);
		    theObject.setParameter("dateFinish", finishDate.plusDays(7));
		    theObject.setParameter("idRoute", id + "%");

		    List<Act> acts = theObject.getResultList();
		    return acts;
		}

	private static final String queryGetActsByDates = "from Act a where a.date between :dateStart and :dateFinish " +
			"and a.documentsArrived is not null ";

	@Override
	public List<Act> getActsByDates(LocalDate startDate, LocalDate finishDate){
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Act> theObject = currentSession.createQuery(queryGetActsByDates, Act.class);
		theObject.setParameter("dateStart", startDate);
		theObject.setParameter("dateFinish", finishDate.plusDays(7));
		return theObject.getResultList();
	}
}
