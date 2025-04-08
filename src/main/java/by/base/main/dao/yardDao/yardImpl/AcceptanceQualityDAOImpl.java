package by.base.main.dao.yardDao.yardImpl;

import by.base.main.dao.yardDao.AcceptanceQualityDAO;
import by.base.main.model.yard.AcceptanceFoodQuality;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AcceptanceQualityDAOImpl implements AcceptanceQualityDAO {

    @Autowired
    @Qualifier("sessionFactoryYard")
    private SessionFactory sessionFactoryYard;

    private static final String GET_BY_STATUS = "SELECT DISTINCT afq FROM AcceptanceFoodQuality afq " +
            "JOIN FETCH afq.acceptance a " +
            "left JOIN FETCH a.ttnInList " +
            "left JOIN FETCH afq.acceptanceFoodQualityUsers afqUser " +
            "left JOIN FETCH afqUser.userYard " +
//            "JOIN FETCH afq.acceptanceQualityFoodCardSet afqset " +
            "WHERE afq.qualityProcessStatus = :status";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public List<AcceptanceFoodQuality> getAllByStatus(int status) {
        Session session = sessionFactoryYard.getCurrentSession();
        Query<AcceptanceFoodQuality> query = session.createQuery(GET_BY_STATUS, AcceptanceFoodQuality.class);
        query.setParameter("status", status);


        System.out.println("Parameters: status=" + status);

        return query.getResultList();
    }

    private static final String GET_BY_STATUSES = "SELECT DISTINCT afq FROM AcceptanceFoodQuality afq " +
            "JOIN FETCH afq.acceptance a " +
            "left JOIN FETCH a.ttnInList " +
            "left JOIN FETCH afq.acceptanceFoodQualityUsers afqUser " +
            "left JOIN FETCH afqUser.userYard " +
            "where afq.qualityProcessStatus in (:statuses)";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public List<AcceptanceFoodQuality> getAllByStatuses(List<Integer> statuses) {
        Session session = sessionFactoryYard.getCurrentSession();
        Query<AcceptanceFoodQuality> query = session.createQuery(GET_BY_STATUSES, AcceptanceFoodQuality.class);
        query.setParameter("statuses", statuses);
        return query.getResultList();
    }

    private static final String GET_BY_STATUS_AND_DATE = "SELECT DISTINCT afq FROM AcceptanceFoodQuality afq " +
            "JOIN FETCH afq.acceptance a " +
            "left JOIN FETCH a.ttnInList " +
            "left JOIN FETCH afq.acceptanceFoodQualityUsers afqUser " +
            "left JOIN FETCH afqUser.userYard " +
            "where afq.qualityProcessStatus = :status and afq.dateStartProcess between :start and :end";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public List<AcceptanceFoodQuality> getAllByStatusAndDates(int status, LocalDateTime start, LocalDateTime end) {
        Session session = sessionFactoryYard.getCurrentSession();
        Query<AcceptanceFoodQuality> query = session.createQuery(GET_BY_STATUS_AND_DATE, AcceptanceFoodQuality.class);
        query.setParameter("status", status);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    private static final String GET_BY_ID_AND_STATUS = "from AcceptanceFoodQuality where idAcceptanceFoodQuality = :id and qualityProcessStatus = :status";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public AcceptanceFoodQuality getByIdAndStatus(Long id, int status) {
        Session session = sessionFactoryYard.getCurrentSession();
        Query<AcceptanceFoodQuality> query = session.createQuery(GET_BY_ID_AND_STATUS, AcceptanceFoodQuality.class);
        query.setParameter("id", id);
        query.setParameter("status", status);
        return query.uniqueResult();
    }

    private static final String GET_BY_ID_AND_STATUS_LESS = "from AcceptanceFoodQuality where idAcceptanceFoodQuality = :id and qualityProcessStatus < :status";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public AcceptanceFoodQuality getByIdAndStatusLessThan(Long id, int status) {
        Session session = sessionFactoryYard.getCurrentSession();
        Query<AcceptanceFoodQuality> query = session.createQuery(GET_BY_ID_AND_STATUS_LESS, AcceptanceFoodQuality.class);
        query.setParameter("id", id);
        query.setParameter("status", status);
        return query.uniqueResult();
    }

    private static final String GET_BY_ID = "SELECT DISTINCT afq FROM AcceptanceFoodQuality afq " +
            "JOIN FETCH afq.acceptance a " +
            "left JOIN FETCH a.ttnInList " +
            "left JOIN FETCH afq.acceptanceFoodQualityUsers afqUser " +
            "left JOIN FETCH afqUser.userYard " +
            "where afq.idAcceptanceFoodQuality = :id";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public AcceptanceFoodQuality getByIdAcceptanceFoodQuality(Long id) {
        Session session = sessionFactoryYard.getCurrentSession();
        Query<AcceptanceFoodQuality> query = session.createQuery(GET_BY_ID, AcceptanceFoodQuality.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }


}
