package by.base.main.service.impl;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.InfoCarrierDAO;
import by.base.main.model.InfoCarrier;
import by.base.main.service.InfoCarrierService;

@Service
public class InfoCarrierServiceImpl implements InfoCarrierService{
	
	@Autowired
    private InfoCarrierDAO infoCarrierDAO;

	@Override
	@Transactional
	public List<InfoCarrier> getAll() {
		return infoCarrierDAO.getAll();
	}

	@Override
	@Transactional
	public InfoCarrier getById(Integer id) {
		return infoCarrierDAO.getById(id);
	}

	@Override
	@Transactional
	public int save(InfoCarrier infoCarrier) {
		return infoCarrierDAO.save(infoCarrier);
	}

	@Override
	@Transactional
	public void update(InfoCarrier infoCarrier) {
		infoCarrierDAO.update(infoCarrier);
	}

	@Override
	@Transactional
	public List<InfoCarrier> getFromDate(Date start, Date end) {
		return infoCarrierDAO.getFromDate(start, end);
	}

}
