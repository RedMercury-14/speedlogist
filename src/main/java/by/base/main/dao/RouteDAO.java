package by.base.main.dao;


import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import by.base.main.model.Route;
import by.base.main.model.Truck;
import by.base.main.model.User;

public interface RouteDAO {
	
	List<Route> getRouteList();

	void saveOrUpdateRoute(Route route);

	Route getRouteById(int id);
	
	Route getRouteByDirection(String login);

//	void deleteRouteById(int id);
//	
//	void deleteRouteByDirection(String login);
	
	Route getLastRoute();
	
	List<Route> getRouteListAsDate(Date dateStart, Date dateFinish);
	
	List<Route> getRouteListAsDateAndStatus(Date dateStart, Date dateFinish, String stat1, String stat2);
	
	List<Route> getRouteListAsStatus(String stat1, String stat2);
	
	List<Route> getRouteListByUser(User user);
	
	List<Route> getRouteListAsComment(String comment);
	
	int updateRouteInBase(Integer idRoute, Integer finishCost, String currency, User user, String statusRoute);
	
	/**
	 * Обновляет маршрут по датам
	 * @param idRoute
	 * @param dateLoadActually дата загрузки от перевоза
	 * @param timeLoadActually время загрузки от перевоза
	 * @param dateUnloadActually дата выгрузки от перевоза
	 * @param timeUnloadActually время загрузки от перевоза
	 * @return
	 */
	int updateRouteInBase(Integer idRoute, Date dateLoadActually, Time timeLoadActually, Date dateUnloadActually, Time timeUnloadActually, Truck truck, User driver);
	
	
	/**
	 * меняет дату рейса
	 * @param idRoute
	 * @param dateLoadPreviously дата рейса
	 * @return
	 */
	int updateRouteInBase(Integer idRoute, Date dateLoadPreviously);
	
	/**
	 * удаляет даты , машину и водителя с маршрута
	 * @param idRoute
	 * @return
	 */
	int updateDropRouteDateOfCarrier(Integer idRoute);
	
	int updateRouteInBase(Integer idRoute, String statusRoute);
	
	List<Route> getRouteListAsRouteDirection(Route route);
	
	List<Route> getRouteListAsDateAndUser(Date dateStart, Date dateFinish, User user);
	
	/**
	 * Сохраняет маршрут и возвращает id маршрута
	 * @param route
	 * @return
	 */
	Integer saveRouteAndReturnId(Route route);

}
