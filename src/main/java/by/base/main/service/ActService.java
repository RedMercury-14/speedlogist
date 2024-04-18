package by.base.main.service;

import java.sql.Date;
import java.util.List;

import by.base.main.model.Act;

public interface ActService {
	
	List<Act> getActList();
	void saveOrUpdateAct (Act act);
	Act getActById(Integer id);
	List<Act> getActBynumAct(String id);
	List<Act> getActBySecretCode(String code);
	List<Act> getActListAsDate(Date dateStart, Date dateFinish);

}
