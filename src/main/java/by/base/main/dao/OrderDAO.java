package by.base.main.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.dto.OrderDTO;

import by.base.main.dto.OrderDTOForSlot;
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
	 * Метод который отдаёт заказы для логистов.
	 * Фильтрует по статусам (отдаёт те что выше 17)
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	Set<Order> getListOrdersLogist (Date dateStart, Date dateEnd);	
	
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
	
	/**
	 * Возвращает список заявок, проставленных на таргетном складе, согласно дате
	 * Для поиска по складу использует метод LIKE 
	 * @param dateTarget дата перемещения
	 * @param stockTarget номер склада в стринге
	 * @return
	 */
	Set<Order> getOrderListHasDateAndStockFromSlots (Date dateTarget, String stockTarget);
	
	/**
	 * Метод который возращает заказы по дате доставки 
	 * <br>сложный метод, который сначала ищет по столбцу timeDelivery, если он не равен null
	 * <br>а если он равен null -  то ищет по dateDelivery
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	List<Order> getOrderByPeriodDeliveryAndSlots(Date dateStart, Date dateEnd);
	
	
	/**
	 * Метод который возращает заказы по дате доставки 
	 * <br>сложный метод, который сначала ищет по столбцу timeDelivery, если он не равен null
	 * <br>а если он равен null -  то ищет по dateDelivery
	 * <br>Возвращает DTO
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	List<OrderDTO> getOrderDTOByPeriodDeliveryAndSlots(Date dateStart, Date dateEnd);
	
	/**
	 * Метод который возращает заказы по дате доставки 
	 * <br> В основном для вывода DTO для фронта
	 * <br>Возвращает DTO
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	List<OrderDTO> getOrderDTOByPeriodDelivery(Date dateStart, Date dateEnd);
	
	/**
	 * Метод который возращает заказы по <b>дате доставки (timeDelivery)</b>
	 * <br>
	 * @param dateStart
	 * @param dateEnd
	 * @param numContract
	 * @return
	 */
	List<Order> getOrderByPeriodDeliveryAndCodeContract(Date dateStart, Date dateEnd, String numContract);
	
	Integer saveOrder (Order order);
	
	void updateOrder (Order order);
	
	int updateOrderFromStatus (Order order);
	
	List<Order> getOrderByPeriodCreateAndCounterparty(Date dateStart, Date dateEnd, String counterparty);
	
	void deleteOrderById(Integer id);
	
	boolean checkOrderHasMarketCode(String code);
	
	Order getOrderHasMarketCode(String code);
	
}
