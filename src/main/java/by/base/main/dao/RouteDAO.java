package by.base.main.dao;


import java.sql.Date;
import java.util.List;

import by.base.main.model.Route;
import by.base.main.model.User;

public interface RouteDAO {
	
	List<Route> getRouteList();

	void saveOrUpdateRoute(Route route);

	Route getRouteById(int id);
	
	Route getRouteByDirection(String login);

	void deleteRouteById(int id);
	
	void deleteRouteByDirection(String login);
	
	Route getLastRoute();
	
	List<Route> getRouteListAsDate(Date dateStart, Date dateFinish);
	
	List<Route> getRouteListAsDateAndStatus(Date dateStart, Date dateFinish, String stat1, String stat2);
	
	List<Route> getRouteListAsStatus(String stat1, String stat2);
	
	List<Route> getRouteListByUser(User user);

}
