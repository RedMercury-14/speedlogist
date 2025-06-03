package by.base.main.dao;

import java.sql.Date;
import java.util.List;

import by.base.main.model.InfoCarrier;

public interface InfoCarrierDAO {
	
	List<InfoCarrier> getAll();
	List<InfoCarrier> getFromDate(Date start, Date end);
    InfoCarrier getById(Integer id);
    int save(InfoCarrier infoCarrier);
    void update(InfoCarrier infoCarrier);

}
