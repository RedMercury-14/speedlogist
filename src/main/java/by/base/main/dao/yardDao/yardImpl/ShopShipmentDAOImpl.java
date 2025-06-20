package by.base.main.dao.yardDao.yardImpl;

import by.base.main.dao.yardDao.ShopShipmentDAO;
import by.base.main.model.yard.ShopShipment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Repository
public class ShopShipmentDAOImpl implements ShopShipmentDAO {

    @Autowired
    @Qualifier("sessionFactoryYard")
    private SessionFactory sessionFactoryYard;

    @Transactional(transactionManager = "myTransactionManagerYard")
    @Override
    public Long saveShopShipment(ShopShipment shopShipment) {
        Session currentSession = sessionFactoryYard.getCurrentSession();
        currentSession.save(shopShipment);
        System.out.println("Transaction active: " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("Session is joined to transaction: " + currentSession.isJoinedToTransaction());
        System.out.println("FlushMode: " + currentSession.getHibernateFlushMode());
        return Long.parseLong(currentSession.getIdentifier(shopShipment).toString());
    }
}
