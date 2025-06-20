package by.base.main.service.impl;

import by.base.main.dao.WarehouseManagementDataDAO;
import by.base.main.model.WarehouseManagementData;
import by.base.main.service.WarehouseManagementDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;

@Service
public class WarehouseManagementDataServiceImpl implements WarehouseManagementDataService {

    @Autowired
    private WarehouseManagementDataDAO warehouseManagementDataDAO;

    @Transactional
    @Override
    public void saveWarehouseManagementData(WarehouseManagementData data) {
        warehouseManagementDataDAO.saveWarehouseManagementData(data);
    }

    @Transactional
    @Override
    public WarehouseManagementData getWMbyStockAndDate(Integer stock, Date date, Integer warehouse) {
        return warehouseManagementDataDAO.getWMbyStockAndDate(stock, date, warehouse);
    }
}
