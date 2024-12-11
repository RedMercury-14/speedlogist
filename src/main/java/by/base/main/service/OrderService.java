package by.base.main.service;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import com.dto.OrderDTO;

import by.base.main.dto.OrderDTOForSlot;
import by.base.main.model.Order;
import by.base.main.model.Product;
import by.base.main.model.Route;
import by.base.main.model.Schedule;

public interface OrderService {

	Order getOrderById(Integer id);
	
	List<Order> getOrderByDateCreate(Date date);
	
	List<Order> getOrderByDateDelivery(Date date);
	
	List<Order> getOrderByLink(Integer link);
	
	Order getOrderByRoute(Route route);
	
	Order getOrderByIdRoute(Integer idRoute);
	
	List<Order> getOrderByPeriodDelivery(Date dateStart, Date dateEnd);
	
	/**
	 * Метод который отдаёт заказы для логистов.
	 * Фильтрует по статусам (отдаёт те что выше 17)
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	Set<Order> getListOrdersLogist (Date dateStart, Date dateEnd);	
	
	/**
	 * Отдаёт заказы по номеру из маркета (должен быть один)
	 * @param number
	 * @return
	 */
	Order getOrderByMarketNumber(String number);
	
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
	 * Общий метод сохранения.
	 * НЕ ИСПОЛЬЗОВАТЬ ДЛЯ 5 СТАТУСОВ
	 * @param order
	 * @return
	 */
	Integer saveOrder (Order order);
	
	/**
	 * Метод для сохранения, или выведения сообщения, заказов после обработки excel (для 5 статусов и 6 статусов)
	 * @param order
	 * @return
	 */
	String saveOrderFromExcel (Order order);
	
	/**
	 * Метод для сохранения, или выведения сообщения, заказов после ообщения из маркета (для 5 статусов и 6 статусов)
	 * @param order
	 * @return
	 */
	String saveOrderFromMarket (Order order);
	
	void updateOrder (Order order);
	
	/**
	 * Специальный метод обновления ордера, который проверяет реально ли поставить слот на выгрузку.
	 * Проверяет не перекрывает ли этот слот другие слоты на уровне базы данных
	 * @param order
	 * @return
	 */
	String updateOrderForSlots (Order order);
	
	/**
	 * Возвращает суммарное кол-во паллет на текущий момент времени на складе
	 * Это общее колл-во паллет! 
	 * @param order
	 * @return
	 */
	Integer getSummPallInStock (Order order);
	
	/**
	 * Возвращает суммарное кол-во паллет <b>заказов внутренних перемещений</b> на текущий момент времени на складе
	 * @param order
	 * @return
	 */
	Integer getSummPallInStockInternal (Order order);
	
	/**
	 * Возвращает суммарное кол-во паллет <b>обычных заказов</b> на текущий момент времени на складе
	 * @param order
	 * @return
	 */
	Integer getSummPallInStockExternal (Order order);
	
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
	 * Метод который возращает заказы по дате доставки <b>(OrderDTOForSlot)</b>
	 * <br>сложный метод, который сначала ищет по столбцу timeDelivery, если он не равен null
	 * <br>а если он равен null -  то ищет по dateDelivery
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
	
	/**
	 * Метод который возращает заказы по <b>дате доставки (timeDelivery) и списку из номеров  контратов</b>
	 * <br>
	 * @param dateStart
	 * @param dateEnd
	 * @param schedules
	 * @return
	 */
	List<Order> getOrderByPeriodDeliveryAndListCodeContract(Date dateStart, Date dateEnd, List<Schedule> schedules);
	
	/**
	 * Возвращаем заказы за период, в которые входит таргетный продукт
	 * @param dateStart
	 * @param Finish
	 * @param product
	 * @return
	 */
	List<Order> getOrderByPeriodSlotsAndProduct(Date dateStart, Date dateFinish, Product product);
	
	/**
	 * прямой наследний <b>List<Order> getOrderByPeriodSlotsAndProduct(Date dateStart, Date dateFinish, Product product)</b>
	 * <br> Возвращаем заказы за период, в которые входит <b>группа</b> таргетных продуктов
	 * 
	 * @param dateStart
	 * @param Finish
	 * @param product
	 * @return
	 */
	List<Order> getOrderGroupByPeriodSlotsAndProduct(Date dateStart, Date dateFinish, List<Long> goodsIds);
	
	int updateOrderFromStatus (Order order);
	
	List<Order> getOrderByPeriodCreateAndCounterparty(Date dateStart, Date dateEnd, String counterparty);
	
	void deleteOrderById(Integer id);
	
	boolean checkOrderHasMarketCode(String code);
}
