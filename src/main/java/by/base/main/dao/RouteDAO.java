package by.base.main.dao;


import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.dto.RouteDTO;

import by.base.main.model.Message;
import by.base.main.model.Route;
import by.base.main.model.Truck;
import by.base.main.model.User;

public interface RouteDAO {
	
	List<Route> getRouteList();

	void saveOrUpdateRoute(Route route);
	
	void saveRoute(Route route);
	
	void updateRoute(Route route);

	Route getRouteById(int id);
	
	Route getRouteByDirection(String login);

//	void deleteRouteById(int id);
//	
//	void deleteRouteByDirection(String login);
	
	Route getLastRoute();
	
	List<Route> getRouteListAsDate(Date dateStart, Date dateFinish);
	
	/**
	 * отдаёт маршруты для менеджера международных перевозок
	 * @param dateStart
	 * @param dateFinish
	 * @return
	 */
	List<Route> getRouteListAsDateForInternational(Date dateStart, Date dateFinish);
	
	List<RouteDTO> getRouteListAsDateDTO(Date dateStart, Date dateFinish);
	
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

	/**
	 * Получаем маршруты, в которых участвовал юзер (выйграл и нет!)
	 * @param route
	 * @return
	 */
	List<Route> getRouteListParticipated(User user);
	
	/**
	 * Получаем маршруты по перевозу за период
	 * @param user
	 * @param start
	 * @param end
	 * @return
	 */
	List<Route> getRouteListByUserHasPeriod(User user, LocalDate start, LocalDate end);
	
	/**
	 * Отдаёт лист с маршрутами/заказами АХО по <b>дате доставки (dateLoadPreviously)</b>
	 * @param dateStart
	 * @param dateFinish
	 * @return
	 */
	List<Route> getMaintenanceListAsDate(Date dateStart, Date dateFinish);
	
	/**
	 * Отдаёт лист с маршрутами/заказами АХО по <b>дате доставки (dateLoadPreviously)</b> и логину
	 * @param dateStart
	 * @param dateFinish
	 * @param user
	 * @return
	 */
	List<Route> getMaintenanceListAsDateAndLogin(Date dateStart, Date dateFinish, User user);
	
	/**
	 * Основной метод для выдачи маршрутов которые торгуются.
	 * <br>фильтруется по 1 статусу
	 * <br>потом по комментарию international
	 * <br>начиная с таргетной даты и далее (не показывает прошлые даты)
	 * @param dateStart
	 * @param dateFinish
	 * @param user
	 * @return
	 */
	List<Route> getActualRoute(Date date);
	
	List<Route> getInternationalRoutesByDates(Date dateStart, Date dateFinish);
	
	/**
	 * <br>Возвращает список роутов за указанный интервал дат</br>
	 * @param dateStart
	 * @param dateFinish
	 * @return
	 * @author Ira
	 */
	List<Route> getRouteListByDatesCreate(Date dateStart, Date dateFinish);

	/**
	 * <br>Возвращает мапу пар route id - message по указанными idRoute</br>
	 * @param routesId
	 * @return
	 * @author Ira
	 */
	Map<String, List<Message>> routesWithMessages(List<String> routesId);
}
