package by.base.main.service;

import by.base.main.model.WarehouseManagementData;

import java.sql.Date;

public interface WarehouseManagementDataService {
    void saveWarehouseManagementData(WarehouseManagementData data);

    WarehouseManagementData getWMbyStockAndDate(Integer stock, Date date, Integer warehouse);
}
