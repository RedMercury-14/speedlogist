package by.base.main.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import by.base.main.model.Order;
import by.base.main.model.Route;

public interface OrderDAO {
	
	Order getOrderById(Integer id);
	
	List<Order> getOrderByDateCreate(Date date);
	
	List<Order> getOrderByDateDelivery(Date date);
	
	Order getOrderByRoute(Route route);
	
	Order getOrderByIdRoute(Integer idRoute);
	
	List<Order> getOrderByPeriodDelivery(Date dateStart, Date dateEnd);
	
	List<Order> getOrderByPeriodCreate(Date dateStart, Date dateEnd);	
	
	/**
	 * Возвращает заказы по периуду создания из маркета
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	List<Order> getOrderByPeriodCreateMarket(Date dateStart, Date dateEnd);
	
	/**
	 * отдаёт ордеры по дате доставки ()без учёта времени)
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	List<Order> getOrderByTimeDelivery(Date dateStart, Date dateEnd);
	
	/**
	 * Отдаёт заказы по номеру из маркета (должен быть один)
	 * @param number
	 * @return
	 */
	Order getOrderByMarketNumber(String number);
	
	/**
	 * Отдаёт ближайший заказ который идёт <b>до<b> даты доставки 
	 * @param timeDelivery - 
	 * @param stock - номер склада
	 * @param ramp - номер рампы 
	 * @return
	 */
	Order getOrderBeforeTimeDeliveryHasStockAndRamp(Order order);	
	
	/**
	 * Отдаёт ближайший заказ который идёт <b>после<b> даты доставки 
	 * @param timeDelivery - 
	 * @param stock - номер склада
	 * @param ramp - номер рампы 
	 * @return
	 */
	Order getOrderAfterTimeDeliveryHasStockAndRamp(Order order);
	
	Integer saveOrder (Order order);
	
	void updateOrder (Order order);
	
	int updateOrderFromStatus (Order order);
	
	List<Order> getOrderByPeriodCreateAndCounterparty(Date dateStart, Date dateEnd, String counterparty);
	
	void deleteOrderById(Integer id);
	
	boolean checkOrderHasMarketCode(String code);
	
	Order getOrderHasMarketCode(String code);
	
}
