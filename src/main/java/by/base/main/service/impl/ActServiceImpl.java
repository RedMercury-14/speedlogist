package by.base.main.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.ActDAO;
import by.base.main.model.Act;
import by.base.main.service.ActService;

@Service
public class ActServiceImpl implements ActService{
	
	@Autowired
	ActDAO actDAO;

	@Override
	public List<Act> getActList() {
		return actDAO.getActList();
	}

	@Override
	public void saveOrUpdateAct(Act act) {
		actDAO.saveOrUpdateAct(act);
		
	}

	@Override
	public Act getActById(Integer id) {
		return actDAO.getActById(id);
	}

	@Override
	public List<Act> getActBynumAct(String id) {
		return actDAO.getActBynumAct(id);
	}

	@Override
	public List<Act> getActListAsDate(Date dateStart, Date dateFinish) {
		return actDAO.getActListAsDate(dateStart, dateFinish);
	}

	@Override
	public List<Act> getActBySecretCode(String code) {
		return actDAO.getActBySecretCode(code);
	}
	
	@Override
	@Transactional
	public List<Act> getActsByRouteId(String id, LocalDate startDate, LocalDate finishDate) {
	    return actDAO.getActsByRouteId(id,startDate, finishDate);
	}

}
