package by.base.main.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import by.base.main.model.Act;

public interface ActDAO {
	
	List<Act> getActList();
	void saveOrUpdateAct (Act act);
	Act getActById(Integer id);
	List<Act> getActBynumAct(String id);
	List<Act> getActBySecretCode(String code);
	List<Act> getActListAsDate(Date dateStart, Date dateFinish);
	
	/**
	 * <br>Возвращает список актов по указанному id и за указанный диапазон времени</br>
	 * @param id
	 * @param startDate
	 * @param finishDate
	 * @return
	 * @author Ira
	 */
	   List<Act> getActsByRouteId(String id, LocalDate startDate, LocalDate finishDate);


	List<Act> getActsByDates(LocalDate startDate, LocalDate finishDate);
}
