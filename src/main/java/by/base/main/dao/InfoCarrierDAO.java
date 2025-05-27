package by.base.main.dao;

import java.util.List;

import by.base.main.model.InfoCarrier;

public interface InfoCarrierDAO {
	
	List<InfoCarrier> getAll();
    InfoCarrier getById(Integer id);
    int save(InfoCarrier infoCarrier);
    void update(InfoCarrier infoCarrier);

}
