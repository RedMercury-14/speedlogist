package by.base.main.dao;

import by.base.main.model.WarehouseManagementData;

import java.sql.Date;

public interface WarehouseManagementDataDAO {
    void saveWarehouseManagementData(WarehouseManagementData data);

    WarehouseManagementData getWMbyStockAndDate(Integer stock, Date date, Integer warehouse);
}
