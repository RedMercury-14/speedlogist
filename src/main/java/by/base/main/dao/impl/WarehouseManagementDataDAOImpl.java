package by.base.main.dao.impl;

import by.base.main.dao.WarehouseManagementDataDAO;
import by.base.main.model.WarehouseManagementData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class WarehouseManagementDataDAOImpl implements WarehouseManagementDataDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void saveWarehouseManagementData(WarehouseManagementData data) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(data);
    }

    private static final String queryGetByStockWarehouseAndDate = "from WarehouseManagementData w " +
            "where numStock =: stock and dateCreate =: date and warehouse =: warehouse";

    @Override
    public WarehouseManagementData getWMbyStockAndDate(Integer stock, Date date, Integer warehouse) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<WarehouseManagementData> theRole = currentSession.createQuery(queryGetByStockWarehouseAndDate, WarehouseManagementData.class);
        theRole.setParameter("stock", stock);
        theRole.setParameter("date", date);
        theRole.setParameter("warehouse", warehouse);
        List<WarehouseManagementData> wmList = theRole.list();
        if(!wmList.isEmpty()) {
            return wmList.get(0);
        } else {
            return null;
        }
    }

}
