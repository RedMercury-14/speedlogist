package by.base.main.dao.yardDao.yardImpl;


import by.base.main.dao.yardDao.ShipmentDAO;
import by.base.main.model.yard.Shipment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Repository
public class ShipmentDaoImpl implements ShipmentDAO {

    @Autowired
    @Qualifier("sessionFactoryYard")
    private SessionFactory sessionFactoryYard;

    @Transactional(transactionManager = "myTransactionManagerYard")
    @Override
    public Long saveShipment(Shipment shipment) {
        Session currentSession = sessionFactoryYard.getCurrentSession();
        currentSession.save(shipment);
        System.out.println("Transaction active: " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("Session is joined to transaction: " + currentSession.isJoinedToTransaction());
        System.out.println("Flush mode: " + currentSession.getFlushMode());
        return Long.parseLong(currentSession.getIdentifier(shipment).toString());
    }
}
