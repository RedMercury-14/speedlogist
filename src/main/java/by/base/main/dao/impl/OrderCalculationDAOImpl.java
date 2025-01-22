package by.base.main.dao.impl;

import by.base.main.dao.OrderCalculationDAO;
import by.base.main.model.OrderCalculation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TemporalType;
import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public class OrderCalculationDAOImpl implements OrderCalculationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private static final String queryGetObjByIdOrderCalculation = "from OrderCalculation oc left join fetch oc.orderProducts op where oc.id=:idOrderCalculation";

    @Override
    public OrderCalculation getOrderCalculationById(Integer id) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<OrderCalculation> theObject = currentSession.createQuery(queryGetObjByIdOrderCalculation, OrderCalculation.class);
        theObject.setParameter("idOrderCalculation", id);
        List<OrderCalculation> trucks = theObject.getResultList();
        if(trucks.isEmpty()) {
            return null;
        }
        OrderCalculation object = trucks.stream().findFirst().get();
        return object;
    }

    @Override
    public Integer saveOrderCalculation(OrderCalculation orderCalculation) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.saveOrUpdate(orderCalculation);
        return Integer.parseInt(currentSession.getIdentifier(orderCalculation).toString());
    }

    private static final String queryGetOrderCalculationByPeriod = "from OrderCalculation oc where oc.deliveryDate BETWEEN :dateStart and :dateEnd";
    @Override
    public List<OrderCalculation> getOrderCalculationsForPeriod(Date dateFrom, Date dateTo) {
        Session currentSession = sessionFactory.getCurrentSession();
        Timestamp dateStartFinal = Timestamp.valueOf(LocalDateTime.of(dateFrom.toLocalDate(), LocalTime.of(00, 00)));
        Timestamp dateEndFinal = Timestamp.valueOf(LocalDateTime.of(dateTo.toLocalDate(), LocalTime.of(23, 59)));
        Query<OrderCalculation> theObject = currentSession.createQuery(queryGetOrderCalculationByPeriod, OrderCalculation.class);
        theObject.setParameter("dateStart", dateStartFinal, TemporalType.TIMESTAMP);
        theObject.setParameter("dateEnd", dateEndFinal, TemporalType.TIMESTAMP);
        List<OrderCalculation> orderCalculations = theObject.getResultList();
        return orderCalculations;
    }

    private static final String queryGetOrderCalculationByContractAndNumStock = "from OrderCalculation oc where oc.counterpartyContractCode = :contract " +
            "and oc.numStock = :numStock and oc.deliveryDate = :date and oc.goodsId = :goodId";
    @Override
    public OrderCalculation getOrderCalculatiionByContractNumStockAndDeliveryDate(Long contract, Integer numStock, Date date, Long goodId) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<OrderCalculation> theObject = currentSession.createQuery(queryGetOrderCalculationByContractAndNumStock, OrderCalculation.class);
        theObject.setParameter("contract", contract);
        theObject.setParameter("numStock", numStock);
        theObject.setParameter("date", date);
        theObject.setParameter("goodId", goodId);
        List <OrderCalculation> orderCalculations = theObject.getResultList();

        return orderCalculations.isEmpty() ? new OrderCalculation() : orderCalculations.get(0);
    }
}
