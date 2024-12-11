package by.base.main.dao.impl;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TemporalType;
import javax.transaction.Transactional;

import org.glassfish.grizzly.utils.ArraySet;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dto.OrderDTO;
import com.dto.RouteDTO;

import by.base.main.dao.RouteDAO;
import by.base.main.model.Address;
import by.base.main.model.Message;
import by.base.main.model.Order;
import by.base.main.model.Route;
import by.base.main.model.Truck;
import by.base.main.model.User;
import by.base.main.util.ChatEnpoint;

@Repository
public class RouteDAOImpl implements RouteDAO {

	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d order by r.idroute";

	@Override
	@Transactional
	public List<Route> getRouteList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetList, Route.class);
		List<Route> objects = theObject.getResultList();
		return objects;
	}

	@Override
	@Transactional
	public void saveOrUpdateRoute(Route route) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(route);

	}

	private static final String queryGetObjById = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.idRoute=:idRoute";

	@Override
	@Transactional
	public Route getRouteById(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetObjById, Route.class);
		theObject.setParameter("idRoute", id);
		List<Route> trucks = theObject.getResultList();
		Route object = trucks.stream().findFirst().get();
		return object;
	}

	private static final String queryGetListObj = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.routeDirection=:setNumTruck";

	@Override
	@Transactional
	public Route getRouteByDirection(String login) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListObj, Route.class);
		theObject.setParameter("setNumTruck", login);
		List<Route> trucks = theObject.getResultList();
		Route object = trucks.stream().findFirst().get();
		return object;
	}

//	private static final String queryDeleteById = "delete from Route where idroute=:setId";
//
//	@Override
//	@Transactional
//	@Deprecated
//	public void deleteRouteById(int id) {
//		Session currentSession = sessionFactory.getCurrentSession();
//		Query theQuery = currentSession.createQuery(queryDeleteById);
//		theQuery.setParameter("setId", id);
//		theQuery.executeUpdate();
//	}
//
//	private static final String queryDeleteByLogin = "delete from Route where routeDirection=:setLogin";
//
//	@Override
//	@Transactional
//	public void deleteRouteByDirection(String login) {
//		Session currentSession = sessionFactory.getCurrentSession();
//		Query theQuery = currentSession.createQuery(queryDeleteByLogin);
//		theQuery.setParameter("setLogin", login);
//		theQuery.executeUpdate();
//
//	}

	private static final String queryGetLast = "SELECT * FROM route order BY idroute DESC LIMIT 1";

	@Override
	@Transactional
	public Route getLastRoute() {
		Session currentSession = sessionFactory.getCurrentSession();
		List<Route> theObject = currentSession.createSQLQuery(queryGetLast).addEntity(Route.class).list();
		Route object = theObject.stream().findFirst().get();
		return object;
	}

	private static final String queryGetListAsDate = "from Route r LEFT JOIN FETCH r.orders ord "
			+ "LEFT JOIN FETCH ord.addresses addr "
			+ "LEFT JOIN FETCH r.user u "
			+ "LEFT JOIN FETCH r.truck tr "
			+ "LEFT JOIN FETCH r.roteHasShop rhs "
			+ "LEFT JOIN FETCH r.driver d "
			+ "where r.dateLoadPreviously BETWEEN :frmdate and :todate";

	@Override
	@Transactional
	public List<Route> getRouteListAsDate(Date dateStart, Date dateFinish) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListAsDate, Route.class);
		theObject.setParameter("frmdate", dateStart, TemporalType.DATE);
		theObject.setParameter("todate", dateFinish, TemporalType.DATE);
		List<Route> objects = theObject.getResultList();
		// Преобразуем связанные заказы в DTO
	    for (Route route : objects) {
	        Set<Order> orders = route.getOrders(); // Инициализируем заказы
	        Set<OrderDTO> orderDTOs = orders.stream()
	            .map(order -> new OrderDTO(
	                order.getIdOrder(),
	                order.getCounterparty(),
	                order.getContact(),
	                order.getCargo(),
	                order.getTypeLoad(),
	                order.getMethodLoad(),
	                order.getTypeTruck(),
	                order.getTemperature(),
	                order.getControl(),
	                order.getComment(),
	                order.getStatus(),
	                order.getDateCreate(),
	                order.getDateDelivery(),
	                order.getManager(),
	                order.getTelephoneManager(),
	                order.getStacking(),
	                order.getLogist(),
	                order.getLogistTelephone(),
	                order.getMarketNumber(),
	                order.getOnloadWindowDate(),
	                order.getOnloadWindowTime(),
	                order.getLoadNumber(),
	                order.getNumStockDelivery(),
	                order.getPall(),
	                order.getWay(),
	                order.getOnloadTime(),
	                order.getIncoterms(),
	                order.getChangeStatus(),
	                order.getNeedUnloadPoint(),
	                order.getIdRamp(),
	                order.getTimeDelivery(),
	                order.getTimeUnload(),
	                order.getLoginManager(),
	                order.getSku(),
	                order.getMonoPall(),
	                order.getMixPall(),
	                order.getIsInternalMovement(),
	                order.getMailInfo(),
	                order.getSlotInfo(),
	                order.getDateCreateMarket(),
	                order.getMarketInfo(),
	                order.getMarketContractType(),
	                order.getMarketContractGroupId(),
	                order.getMarketContractNumber(),
	                order.getMarketContractorId(),
	                order.getNumProduct(),
	                order.getStatusYard(),
	                order.getUnloadStartYard(),
	                order.getUnloadFinishYard(),
	                order.getPallFactYard(),
	                order.getWeightFactYard(),
	                order.getMarketOrderSumFirst(),
	                order.getMarketOrderSumFinal(),
	                order.getArrivalFactYard(),
	                order.getRegistrationFactYard(),
	                order.getAddresses().stream()
	                     .findFirst() // Извлекаем первый адрес, если он существует
	                     .map(Address::getBodyAddress)
	                     .orElse(null),
	                order.getLastDatetimePointLoad(),
	                order.getDateOrderOrl(),
	                order.getLink()
	            )).collect(Collectors.toSet());
	        // Если нужно, добавьте OrderDTO обратно в Route или сохраните для дальнейшей обработки
	        route.setOrdersDTO(orderDTOs); // Добавьте это поле в `Route`, если требуется
	    }
		return objects;
	}

	private static final String queryGetListAsDateAndStatus = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.dateLoadPreviously BETWEEN :frmdate and :todate AND where r.statusRoute BETWEEN :frstat and :tostat";

	@Override
	@Transactional
	public List<Route> getRouteListAsDateAndStatus(Date dateStart, Date dateFinish, String stat1, String stat2) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListAsDateAndStatus, Route.class);
		theObject.setParameter("frmdate", dateStart, TemporalType.DATE);
		theObject.setParameter("todate", dateFinish, TemporalType.DATE);
		theObject.setParameter("frstat", stat1);
		theObject.setParameter("tostat", stat2);
		List<Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String queryGetListAsStatus = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.statusRoute BETWEEN :frstat and :tostat";
	private static final String queryGetListAsStatus1And1 = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.roteHasShop rhs where r.statusRoute BETWEEN :frstat and :tostat";

	@Override
	@Transactional
	public List<Route> getRouteListAsStatus(String stat1, String stat2) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject;
		if (stat1.equals("1") && stat2.equals("1")) {
			theObject = currentSession.createQuery(queryGetListAsStatus1And1, Route.class);
			System.out.println("queryGetListAsStatus1And1");
		} else {
			theObject = currentSession.createQuery(queryGetListAsStatus, Route.class);
		}
		theObject.setParameter("frstat", stat1);
		theObject.setParameter("tostat", stat2);
		List<Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String queryGetListObjByUser = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.user=:user";

	@Override
	@Transactional
	public List<Route> getRouteListByUser(User user) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListObjByUser, Route.class);
		theObject.setParameter("user", user);
		List<Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String queryGetListAsComment = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.comments=:comment";

	@Override
	@Transactional
	public List<Route> getRouteListAsComment(String comment) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListAsComment, Route.class);
		theObject.setParameter("comment", comment);
		List<Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String queryUpdate = "UPDATE Route SET finishPrice =: finishPrice, startCurrency=: currency, user=:user, statusRoute=:statusRoute where idRoute=:idRoute";

	@Override
	@Transactional
	public int updateRouteInBase(Integer idRoute, Integer finishCost, String currency, User user, String statusRoute) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryUpdate);
		query.setParameter("idRoute", idRoute);
		query.setParameter("finishPrice", finishCost);
		query.setParameter("currency", currency);
		query.setParameter("user", user);
		query.setParameter("statusRoute", statusRoute);
		int result = query.executeUpdate();
		return result;
	}

	private static final String queryUpdateSimple = "UPDATE Route SET statusRoute=:statusRoute where idRoute=:idRoute";

	@Override
	@Transactional
	public int updateRouteInBase(Integer idRoute, String statusRoute) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryUpdateSimple);
		query.setParameter("idRoute", idRoute);
		query.setParameter("statusRoute", statusRoute);
		int result = query.executeUpdate();
		return result;
	}

	@Override
	@Transactional
	public List<Route> getRouteListAsRouteDirection(Route route) { // 09.10.2023
		Session currentSession = sessionFactory.getCurrentSession();
		String SQL;
		if (route.getRouteDirection().contains("[")) {
			SQL = "SELECT * FROM `route` WHERE `routeDirection` LIKE '" + route.getRouteDirection().split(" \\[")[0]
					+ "%'";
		} else {
			SQL = "SELECT * FROM `route` WHERE `routeDirection` LIKE '" + route.getRouteDirection().split(" N")[0]
					+ "%'";
		}
		List<Route> theObject = currentSession.createSQLQuery(SQL).addEntity(Route.class).list();
		return theObject;
	}

	private static final String queryGetListAsDateAndUser = "from Route r LEFT JOIN FETCH r.orders LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.dateLoadPreviously BETWEEN :frmdate and :todate AND r.user =:user";

	@Override
	@Transactional
	public List<Route> getRouteListAsDateAndUser(Date dateStart, Date dateFinish, User user) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetListAsDateAndUser, Route.class);
		theObject.setParameter("frmdate", dateStart, TemporalType.DATE);
		theObject.setParameter("todate", dateFinish, TemporalType.DATE);
		theObject.setParameter("user", user);
		List<Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String queryUpdateDates = "UPDATE Route r SET r.dateLoadActually =:dateLoadActually, r.dateUnloadActually =:dateUnloadActually, r.timeLoadActually=:timeLoadActually, r.timeUnloadActually=:timeUnloadActually, r.truck=:truck, r.driver =:driver where idRoute=:idRoute";

	@Transactional
	@Override
	@Deprecated
	public int updateRouteInBase(Integer idRoute, Date dateLoadActually, Time timeLoadActually, Date dateUnloadActually,
			Time timeUnloadActually, Truck truck, User driver) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryUpdateDates);
		query.setParameter("idRoute", idRoute);
		query.setParameter("dateLoadActually", dateLoadActually, TemporalType.DATE);
		query.setParameter("dateUnloadActually", dateUnloadActually, TemporalType.DATE);
		query.setParameter("timeLoadActually", timeLoadActually.toLocalTime(), TemporalType.TIME); // стедать стрингом
		query.setParameter("timeUnloadActually", timeUnloadActually.toLocalTime(), TemporalType.TIME); // стедать
																										// стрингом
		query.setParameter("truck", truck);
		query.setParameter("driver", driver);
		int result = query.executeUpdate();
		return result;
	}

	private static final String queryUpdateDateDoute = "UPDATE Route SET dateLoadPreviously =:dateLoadPreviously where idRoute=:idRoute";

	@Transactional
	@Override
	public int updateRouteInBase(Integer idRoute, Date dateLoadPreviously) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryUpdateDateDoute);
		query.setParameter("idRoute", idRoute);
		query.setParameter("dateLoadPreviously", dateLoadPreviously, TemporalType.DATE);
		int result = query.executeUpdate();
		return result;
	}

	private static final String queryUpdateDrop = "UPDATE Route r SET r.dateLoadActually =NULL, r.dateUnloadActually =NULL, r.timeLoadActually=NULL, r.timeUnloadActually=NULL, r.truck=NULL, r.driver =NULL where r.idRoute=:idRoute";

	@Transactional
	@Override
	public int updateDropRouteDateOfCarrier(Integer idRoute) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryUpdateDrop);
		query.setParameter("idRoute", idRoute);
		int result = query.executeUpdate();
		return result;
	}

	@Transactional
	@Override
	public Integer saveRouteAndReturnId(Route route) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(route);
		return Integer.parseInt(currentSession.getIdentifier(route).toString());
	}

	private static final String queryGetObjByCompanyNameMessage = "from Message where status=1 AND companyName=:companuName AND date BETWEEN :frmdate and :todate";

	@Transactional
	@Override
	public List<Route> getRouteListParticipated(User user) {
		Session currentSession = sessionFactory.getCurrentSession();
		Date start = Date.valueOf(LocalDate.now().minusDays(15));
		Date finish = Date.valueOf(LocalDate.now().plusDays(15));
		Query query1 = currentSession.createQuery(queryGetObjByCompanyNameMessage, Message.class);
		query1.setParameter("companuName", user.getCompanyName());
		query1.setParameter("frmdate", start, TemporalType.DATE);
		query1.setParameter("todate", finish, TemporalType.DATE);
		Set<String> idRoutes = new HashSet<String>();
		List<Message> objects = query1.getResultList();
		ChatEnpoint.internationalMessegeList.stream().filter(m -> m.getFromUser().equals(user.getLogin()))
				.forEach(m -> idRoutes.add(m.getIdRoute())); // получаем idRoute что находятся в кеше
		objects.forEach(m -> idRoutes.add(m.getIdRoute()));
		Set<Route> routes = new HashSet<Route>();
		for (String idStr : idRoutes) {
			routes.add(getRouteById(Integer.parseInt(idStr)));
		}
		return routes.stream().collect(Collectors.toList());
	}

	private static final String queryПetRouteListByUserHasPeriod = "from Route r LEFT JOIN FETCH r.orders LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.user =:user AND r.dateLoadPreviously BETWEEN :frmdate and :todate";

	@Transactional
	@Override
	public List<Route> getRouteListByUserHasPeriod(User user, LocalDate start, LocalDate end) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryПetRouteListByUserHasPeriod, Route.class);
		theObject.setParameter("frmdate", Date.valueOf(start), TemporalType.DATE);
		theObject.setParameter("todate", Date.valueOf(end), TemporalType.DATE);
		theObject.setParameter("user", user);
		List<Route> objects = theObject.getResultList();
		Set<Route> objectsSet = new HashSet<Route>();
		objectsSet = objects.stream().collect(Collectors.toSet());
		objects.clear();
		objects.addAll(objectsSet);
		return objects;
	}

	private static final String queryGetMaintenanceListAsDate = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.comments ='maintenance' AND r.dateLoadPreviously BETWEEN :frmdate and :todate";

	@Override
	@Transactional
	public List<Route> getMaintenanceListAsDate(Date dateStart, Date dateFinish) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetMaintenanceListAsDate, Route.class);
		theObject.setParameter("frmdate", dateStart, TemporalType.DATE);
		theObject.setParameter("todate", dateFinish, TemporalType.DATE);
		List<Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String queryGetMaintenanceListAsDateAndLogin = "from Route r LEFT JOIN FETCH r.orders ord LEFT JOIN FETCH ord.addresses addr LEFT JOIN FETCH r.user u LEFT JOIN FETCH r.truck tr LEFT JOIN FETCH r.roteHasShop rhs LEFT JOIN FETCH r.driver d where r.comments ='maintenance' AND r.user =:user AND r.dateLoadPreviously BETWEEN :frmdate and :todate";

	@Transactional
	@Override
	public List<Route> getMaintenanceListAsDateAndLogin(Date dateStart, Date dateFinish, User user) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Route> theObject = currentSession.createQuery(queryGetMaintenanceListAsDateAndLogin, Route.class);
		theObject.setParameter("frmdate", dateStart, TemporalType.DATE);
		theObject.setParameter("todate", dateFinish, TemporalType.DATE);
		theObject.setParameter("user", user);
		List<Route> objects = theObject.getResultList();
		return objects;
	}

	private static final String routeConstruct = "SELECT new com.dto.RouteDTO(r.idRoute, "
			+ "r.numStock, "
			+ "r.dateLoadPreviously, "
			+ "r.timeLoadPreviously, "
			+ "r.timeLoadPreviouslyStock, "
			+ "r.actualTimeArrival, "
			+ "r.startLoad, "
			+ "r.finishLoad, "
			+ "r.deliveryDocuments, "
			+ "r.isSanitization, "
			+ "r.temperature, "
			+ "r.nameLoader,"
			+ "r.ramp, "
			+ "r.totalLoadPall, "
			+ "r.totalCargoWeight, "
			+ "r.lines, "
			+ "r.comments, "
			+ "r.routeDirection, "
			+ "r.startPrice, "
			+ "r.finishPrice, "
			+ "r.time, "
			+ "r.statusRoute, "
			+ "r.statusStock, "
			+ "r.typeTrailer, "
			+ "r.startCurrency, "
			+ "r.userComments, "
			+ "r.stepCost, "
			+ "r.optimalCost, "
			+ "r.customer, "
			+ "r.run, "
			+ "r.logistInfo, "
			+ "r.tnvd, "
			+ "r.way, "
			+ "r.expeditionCost, "
			+ "r.loadNumber, "
			+ "r.dateLoadActually, "
			+ "r.timeLoadActually, "
			+ "r.dateUnloadActually, "
			+ "r.timeUnloadActually, "
			+ "r.timeUnloadPreviouslyStock, "
			+ "r.dateUnloadPreviouslyStock, "
			+ "r.createDate, "
			+ "r.createTime, "
			+ "r.onloadWindowDate, "
			+ "r.onloadWindowTime, "
			+ "r.onloadTime, "
			+ "r.logistComment, "
			+ "r.truckInfo, "
			+ "r.kmInfo, "
			+ "r.cargoInfo, "
			+ "r.routeDirectionInternational, "
			+ "r.typeLoad, "
			+ "r.methodLoad, "
			+ "u.companyName, " //-----------
			+ "tr.numTruck, "
			+ "tr.modelTruck, "
			+ "tr.brandTruck, "
			+ "tr.typeTrailer, "
			+ "tr.brandTrailer, "
			+ "tr.ownerTruck, "
			+ "tr.cargoCapacity, "
			+ "tr.pallCapacity, "
			+ "tr.numTrailer, "
			+ "tr.hitchType, "
			+ "tr.info, "
			+ "tr.volumeTrailer, "
			+ "tr.dimensionsBody, "
			+ "tr.number_axes, "
			+ "tr.technicalCertificate, "
			+ "d.telephone, "
			+ "d.numPass,"
			+ "d.name,"
			+ "d.surname,"
			+ "d.patronymic)";
		
	private static final String queryGetRouteListAsDateDTO = routeConstruct + " from Route r LEFT JOIN r.orders ord LEFT JOIN ord.addresses addr LEFT JOIN r.user u LEFT JOIN r.truck tr LEFT JOIN r.roteHasShop rhs LEFT JOIN r.driver d where r.dateLoadPreviously BETWEEN :frmdate and :todate ";
	
	@Transactional
	@Override
	public List<RouteDTO> getRouteListAsDateDTO(Date dateStart, Date dateFinish) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<RouteDTO> theObject = currentSession.createQuery(queryGetRouteListAsDateDTO, RouteDTO.class);
		theObject.setParameter("frmdate", dateStart, TemporalType.DATE);
		theObject.setParameter("todate", dateFinish, TemporalType.DATE);
		List<RouteDTO> objects = theObject.getResultList();
//		for (RouteDTO routeDTO : objects) {
//			Route route = new Route();
//			route.setIdRoute(routeDTO.getIdRoute());
//			Query<OrderDTO> orders = currentSession.createQuery(queryGetRouteListAsDateDTOFOrOrders, OrderDTO.class);
//			orders.setParameter("route", route);
//			Set<OrderDTO> res = orders.getResultList().stream().collect(Collectors.toSet());
//			routeDTO.setOrders(res);
//		}
		return objects;
	}

	@Override
	@Transactional
	public void saveRoute(Route route) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(route);
	}

	@Override
	@Transactional
	public void updateRoute(Route route) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(route);
		
	}
}
