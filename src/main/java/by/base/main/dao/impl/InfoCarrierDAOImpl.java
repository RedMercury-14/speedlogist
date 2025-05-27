package by.base.main.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.InfoCarrierDAO;
import by.base.main.model.InfoCarrier;

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
}
