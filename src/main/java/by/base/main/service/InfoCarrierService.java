package by.base.main.service;

import java.util.List;

import by.base.main.model.InfoCarrier;

public interface InfoCarrierService {

	List<InfoCarrier> getAll();
    InfoCarrier getById(Integer id);
    int save(InfoCarrier infoCarrier);
    void update(InfoCarrier infoCarrier);
}
