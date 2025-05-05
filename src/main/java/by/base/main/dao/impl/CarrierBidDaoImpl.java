package by.base.main.dao.impl;

import by.base.main.dao.CarrierBidDao;
import by.base.main.model.CarrierBid;
import by.base.main.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TemporalType;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Repository
public class CarrierBidDaoImpl implements CarrierBidDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Long save(CarrierBid carrierBid) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(carrierBid);
        return Long.parseLong(currentSession.getIdentifier(carrierBid).toString());
    }

    @Override
    public void delete(CarrierBid carrierBid) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.delete(carrierBid);
    }

    @Override
    public void update(CarrierBid carrierBid) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.update(carrierBid);
    }

    private static final String queryGetBidById = "from CarrierBid where idCarrierBid =: bidId";

    @Override
    public CarrierBid getById(Long bidId){
        Session currentSession = sessionFactory.getCurrentSession();
        Query<CarrierBid> theObject = currentSession.createQuery(queryGetBidById, CarrierBid.class);
        theObject.setParameter("bidId", bidId);
        if (theObject.getResultList().isEmpty()) {
            return null;
        } else {
            return theObject.getResultList().get(0);
        }
    }

    private static final String queryGetCarrierBidListByPeriod = "from CarrierBid where dateTime BETWEEN :dateStart and :dateEnd";

    @Override
    public List<CarrierBid> getCarrierBidsByDate(Date dateStart, Date dateEnd){

        Session currentSession = sessionFactory.getCurrentSession();
        Timestamp dateStartFinal = Timestamp.valueOf(LocalDateTime.of(dateStart.toLocalDate(), LocalTime.of(00, 00)));
        Timestamp dateEndFinal = Timestamp.valueOf(LocalDateTime.of(dateEnd.toLocalDate(), LocalTime.of(23, 59)));
        Query<CarrierBid> theObject = currentSession.createQuery(queryGetCarrierBidListByPeriod, CarrierBid.class);
        theObject.setParameter("dateStart", dateStartFinal, TemporalType.TIMESTAMP);
        theObject.setParameter("dateEnd", dateEndFinal, TemporalType.TIMESTAMP);
        Set<CarrierBid> bids = new HashSet<CarrierBid>(theObject.getResultList());
        return new ArrayList<CarrierBid>(bids);

    }

    private static final String queryGetCarrierBidListByRouteId = "from CarrierBid c LEFT JOIN FETCH c.route r where r.idRoute =: routeId";

    @Override
    public List<CarrierBid> getCarrierBidsByRouteId(Integer routeId) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query<CarrierBid> theObject = currentSession.createQuery(queryGetCarrierBidListByRouteId, CarrierBid.class);
        theObject.setParameter("routeId", routeId);
        return theObject.getResultList();

    }

    private static final String queryGetCarrierBidByRouteAndUser = "from CarrierBid c LEFT JOIN FETCH c.route r where r.idRoute =: routeId and c.carrier =: user";
    @Override
    public CarrierBid getCarrierBidByRouteAndUser(Integer routeId, User user) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<CarrierBid> theObject = currentSession.createQuery(queryGetCarrierBidByRouteAndUser, CarrierBid.class);
        theObject.setParameter("routeId", routeId);
        theObject.setParameter("user", user);
        if (theObject.getResultList().isEmpty()) {
            return null;
        } else {
            return theObject.getResultList().get(0);
        }
    }
}
