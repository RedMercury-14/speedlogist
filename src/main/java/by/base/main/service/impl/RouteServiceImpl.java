package by.base.main.service.impl;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dto.RouteDTO;

import by.base.main.dao.MessageDAO;
import by.base.main.dao.RouteDAO;
import by.base.main.dao.UserDAO;
import by.base.main.model.Message;
import by.base.main.model.Route;
import by.base.main.model.Tender;
import by.base.main.model.Truck;
import by.base.main.model.User;
import by.base.main.service.RouteService;
import by.base.main.util.ChatEnpoint;
@Service
public class RouteServiceImpl implements RouteService{
	
	@Autowired
	private RouteDAO routeDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private ChatEnpoint chatEnpoint;
	
	@Autowired
	private MessageDAO messageDAO;
	
	@Transactional
	@Override
	public List<Route> getRouteList() {
		return routeDAO.getRouteList();
	}

	@Transactional
	@Override
	public void saveOrUpdateRoute(Route route) {
		routeDAO.saveOrUpdateRoute(route);
		
	}

	@Transactional
	@Override
	public Route getRouteById(int id) {
		return routeDAO.getRouteById(id);
	}

	@Transactional
	@Override
	public Route getRouteByDirection(String login) {
		return routeDAO.getRouteByDirection(login);
	}

//	@Override
//	public void deleteRouteById(int id) {
//		routeDAO.deleteRouteById(id);
//		
//	}
//
//	@Override
//	public void deleteRouteByDirection(String login) {
//		routeDAO.deleteRouteByDirection(login);
//		
//	}

	@Transactional
	@Override
	public Route getLastRoute() {
		return routeDAO.getLastRoute();
	}

	@Transactional
	@Override
	public Tender parseTenderByRourte(Route route) {		
		return new Tender(route.getIdRoute(), route.getNumStock(), route.getDateLoadPreviously(), 
				route.getTimeLoadPreviously(), route.isSanitization(), route.getTemperature(), 
				route.getTotalLoadPall(), route.getTotalCargoWeight(), route.getComments(), 
				route.getRouteDirection(), route.getStartPrice(), route.getTime(),
				route.getStatusRoute(), route.getStatusStock(), route.getUser(), 
				route.getTruck(), route.getRoteHasShop());
	}

	@Transactional
	@Override
	public void unparseTenderAndUpdateByRourteAndUpdate(Tender tender) {
		Route route = new Route();
		route.setIdRoute(tender.getIdRoute());
		route.setNumStock(tender.getNumStock());
		route.setDateLoadPreviously(tender.getDateLoadPreviously()); 
		route.setTimeLoadPreviously(tender.getTimeLoadPreviously());
		route.setSanitization(tender.getIsSanitization());
		route.setTemperature(tender.getTemperature()); 
		route.setTotalLoadPall(tender.getTotalLoadPall());
		route.setTotalCargoWeight(tender.getTotalCargoWeight());
		route.setComments(tender.getComments());
		route.setRouteDirection(tender.getRouteDirection());
		route.setStartPrice(tender.getStartPrice());
		route.setTime(tender.getTime());
		route.setStatusRoute(tender.getStatusRoute());
		route.setStatusStock(tender.getStatusStock());
		route.setUser(tender.getUser()); 
		route.setTruck(tender.getTruck());
		routeDAO.saveOrUpdateRoute(route);
	}

	@Transactional
	@Override
	public List<Route> getRouteListAsDate(Date dateStart, Date dateFinish) {
		return routeDAO.getRouteListAsDate(dateStart, dateFinish);
	}

	@Transactional
	@Override
	public List<Route> getRouteListAsDateAndStatus(Date dateStart, Date dateFinish, String stat1, String stat2) {
		return routeDAO.getRouteListAsDateAndStatus(dateStart, dateFinish, stat1, stat2);
	}

	@Transactional
	@Override
	public List<Route> getRouteListAsStatus(String stat1, String stat2) {
		return routeDAO.getRouteListAsStatus(stat1, stat2);
	}

	@Transactional
	@Override
	public List<Route> getRouteListByUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		User user = userDAO.getUserByLogin(name);
		return routeDAO.getRouteListByUser(user);
	}

	@Transactional
	@Override
	public List<Route> getRouteListAsComment(String comment) {
		return routeDAO.getRouteListAsComment(comment);
	}

	@Transactional
	@Override
	public void deleteRouteByIdFromMeneger(Integer idRoute) {
		Route route = routeDAO.getRouteById(idRoute);
		route.setStatusRoute("9");
		routeDAO.saveOrUpdateRoute(route);		
	}

	@Transactional
	@Override
	public int updateRouteInBase(Integer idRoute, Integer finishCost, String currency, User user, String statusRoute) {
		return routeDAO.updateRouteInBase(idRoute, finishCost, currency, user, statusRoute);
	}
	
	@Transactional
	@Override
	public int updateRouteInBase(Integer idRoute, String statusRoute) {
		return routeDAO.updateRouteInBase(idRoute, statusRoute);
	}

	@Transactional
	@Override
	public List<Route> getRouteListAsRouteDirection(Route route) {
		return routeDAO.getRouteListAsRouteDirection(route);
	}

	@Transactional
	@Override
	public List<Route> getRouteListAsDateAndUser(Date dateStart, Date dateFinish) {
		return routeDAO.getRouteListAsDateAndUser(dateStart, dateFinish, getThisUser());
	}
	
	private User getThisUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userDAO.getUserByLogin(name);
		return user;
	}

	@Transactional
	@Override
	public int updateRouteInBase(Integer idRoute, Date dateLoadActually, Time timeLoadActually, Date dateUnloadActually,
			Time timeUnloadActually, Truck truck, User driver) {
		return routeDAO.updateRouteInBase(idRoute, dateLoadActually, timeLoadActually, dateUnloadActually, timeUnloadActually, truck, driver);
	}

	@Transactional
	@Override
	public int updateRouteInBase(Integer idRoute, Date dateLoadPreviously) {
		return routeDAO.updateRouteInBase(idRoute, dateLoadPreviously);
	}

	@Transactional
	@Override
	public int updateDropRouteDateOfCarrier(Integer idRoute) {
		return routeDAO.updateDropRouteDateOfCarrier(idRoute);
	}

	@Transactional
	@Override
	public Integer saveRouteAndReturnId(Route route) {
		return routeDAO.saveRouteAndReturnId(route);
	}

	@Transactional
	@Override
	public List<Route> getRouteListParticipated(User user) {
		// TODO Auto-generated method stub
		return routeDAO.getRouteListParticipated(user);
	}

	@Transactional
	@Override
	public List<Route> getRouteListByUserHasPeriod(User user, LocalDate start, LocalDate end) {
		// TODO Auto-generated method stub
		return routeDAO.getRouteListByUserHasPeriod(user, start, end);
	}
	
	@Transactional
	@Override
	public List<Route> getMaintenanceListAsDate(Date dateStart, Date dateFinish) {
		// TODO Auto-generated method stub
		return routeDAO.getMaintenanceListAsDate(dateStart, dateFinish);
	}

	@Transactional
	@Override
	public List<Route> getMaintenanceListAsDateAndLogin(Date dateStart, Date dateFinish, User user) {
		// TODO Auto-generated method stub
		return routeDAO.getMaintenanceListAsDateAndLogin(dateStart, dateFinish, user);
	}

	@Transactional
	@Override
	public List<RouteDTO> getRouteListAsDateDTO(Date dateStart, Date dateFinish) {
		return routeDAO.getRouteListAsDateDTO(dateStart, dateFinish);
	}

	@Transactional
	@Override
	public void saveRoute(Route route) {
		routeDAO.saveRoute(route);
		
	}

	@Transactional
	@Override
	public void updateRoute(Route route) {
		routeDAO.updateRoute(route);
	}

	@Transactional
	@Override
	public List<Route> getActualRoute(Date date) {
		return routeDAO.getActualRoute(date);
	}

	@Transactional
	@Override
	public Set<Route> getRouteListAsDateForInternational(Date dateStart, Date dateFinish) {
		Set<Route> routes = new HashSet<Route>();
		List<Route>targetRoutes = routeDAO.getRouteListAsDateForInternational(dateStart, dateFinish); 
		
//		List<Route>testRoutes = routeDAO.getRouteListAsDateForInternational(dateStart, dateFinish); 
		targetRoutes.stream()
//			.filter(r-> r.getComments() != null && r.getComments().equals("international") && Integer.parseInt(r.getStatusRoute())<=8)
			.filter(r-> Integer.parseInt(r.getStatusRoute())<=8)
			.forEach(r -> routes.add(r)); // проверяет созданы ли точки вручную, и отдаёт только международные маршруты
		
		//подгрузка кол-ва заявок на тендер
//		List<String> routesId = routes.stream().map(r-> r.getIdRoute().toString()).collect(Collectors.toList());
//		List <Message> messages = messageDAO.getListMessageByIdRouteList(routesId);	
//		
//		for (Route route : routes) {
//			List<Message> messagesList = new ArrayList<Message>();			
//			if(route.getStatusRoute().equals("1")) {
//				chatEnpoint.internationalMessegeList.stream().filter(mes -> mes.getIdRoute().equals(route.getIdRoute().toString()))
//				.forEach(mes -> messagesList.add(mes));
//			}else {							
//				messages.stream().filter(m-> m.getCurrency() != null && m.getIdRoute().equals(route.getIdRoute().toString())).forEach(mes -> messagesList.add(mes));
//			}
//			route.setNumOffer(messagesList.size());
//			
//		}
		return routes;
	}

	 @Transactional
	 @Override
	 public List<Route> getInternationalRoutesByDates(Date dateStart, Date dateFinish) {
	     return routeDAO.getInternationalRoutesByDates(dateStart, dateFinish);
	 }
	
}
