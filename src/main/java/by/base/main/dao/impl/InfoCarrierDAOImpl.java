package by.base.main.dao.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.InfoCarrierDAO;
import by.base.main.model.InfoCarrier;
import by.base.main.model.OrderCalculation;

@Repository
public class InfoCarrierDAOImpl implements InfoCarrierDAO {

	@Autowired
	private SessionFactory sessionFactory;

    private static final String queryGetAll = "from InfoCarrier order by id";

    @Override
    public List<InfoCarrier> getAll() {
    	Session currentSession = sessionFactory.getCurrentSession();
        TypedQuery<InfoCarrier> query = currentSession.createQuery(queryGetAll, InfoCarrier.class);
        return query.getResultList();
    }

    @Override
    public InfoCarrier getById(Integer id) {
    	Session currentSession = sessionFactory.getCurrentSession();
        InfoCarrier entity = currentSession.get(InfoCarrier.class, id);
        currentSession.flush();
        return entity;
    }

    @Override
    public int save(InfoCarrier infoCarrier) {
        Session session = sessionFactory.getCurrentSession();
        session.save(infoCarrier);
        return Integer.parseInt(session.getIdentifier(infoCarrier).toString());
    }

    @Override
    public void update(InfoCarrier infoCarrier) {
        Session session = sessionFactory.getCurrentSession();
        session.update(infoCarrier);
    }

    private static final String queryGetFromDate = "from InfoCarrier where dateTimeCreate BETWEEN :dateStart AND :dateEnd";
	@Override
	public List<InfoCarrier> getFromDate(Date start, Date end) {
		Session currentSession = sessionFactory.getCurrentSession();
        Query<InfoCarrier> theObject = currentSession.createQuery(queryGetFromDate, InfoCarrier.class);
        Timestamp finalStart = Timestamp.valueOf(LocalDateTime.of(start.toLocalDate(), LocalTime.of(00, 00)));
        Timestamp finalFinish = Timestamp.valueOf(LocalDateTime.of(end.toLocalDate(), LocalTime.of(23, 59)));
        theObject.setParameter("dateStart", finalStart, TemporalType.TIMESTAMP);
        theObject.setParameter("dateEnd", finalFinish, TemporalType.TIMESTAMP);
        List <InfoCarrier> orderCalculations = theObject.getResultList();
		return orderCalculations;
	}
}
