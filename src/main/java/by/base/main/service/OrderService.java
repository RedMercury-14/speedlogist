package by.base.main.service;

import java.sql.Date;
import java.util.List;

import by.base.main.model.Order;
import by.base.main.model.Route;

public interface OrderService {

	Order getOrderById(Integer id);
	
	List<Order> getOrderByDateCreate(Date date);
	
	List<Order> getOrderByDateDelivery(Date date);
	
	Order getOrderByRoute(Route route);
	
	Order getOrderByIdRoute(Integer idRoute);
	
	List<Order> getOrderByPeriodDelivery(Date dateStart, Date dateEnd);
	
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
	
	void updateOrder (Order order);
	
	/**
	 * Специальный метод обновления ордера, который проверяет реально ли поставить слот на выгрузку.
	 * Проверяет не перекрывает ли этот слот другие слоты на уровне базы данных
	 * @param order
	 * @return
	 */
	String updateOrderForSlots (Order order);
	
	int updateOrderFromStatus (Order order);
	
	List<Order> getOrderByPeriodCreateAndCounterparty(Date dateStart, Date dateEnd, String counterparty);
	
	void deleteOrderById(Integer id);
	
	boolean checkOrderHasMarketCode(String code);
}
