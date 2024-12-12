package by.base.main.dao;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.dto.OrderDTO;

import by.base.main.dto.OrderDTOForSlot;
import by.base.main.model.Order;
import by.base.main.model.Product;
import by.base.main.model.Route;
import by.base.main.model.Schedule;

public interface OrderDAO {
	
	Order getOrderById(Integer id);
	
	List<Order> getOrderByDateCreate(Date date);
	
	List<Order> getOrderByDateDelivery(Date date);
	
	List<Order> getOrderByLink(Integer link);
	
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
	 * отдаёт ордеры по дате доставки (без учёта времени)
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	List<Order> getOrderByTimeDelivery(Date dateStart, Date dateEnd);
	
	/**
	 * отдаёт ордеры по дате доставки и времени, где время это time начиная от окончания выгрузки таргетного заказа
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	List<Order> getOrderByTimeAfterUnload(Order order, Time time);
	
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
	 * Метод который возращает заказы по <b>дате доставки (timeDelivery) и номеру контракта </b>
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
	List<Order> getOrderByPeriodDeliveryAndListCodeContract(Date dateStart, Date dateEnd, List<String> numContracts);
	
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
	
	/**
	 * прямой наследний <b>List<Order> getOrderGroupByPeriodSlotsAndProduct(Date dateStart, Date dateFinish, List<Long> goodsIds)</b>
	 * <br> Возвращаем заказы за период, в которые входит <b>группа</b> таргетных продуктов
	 * <br> Не подтягивает ненужные связи, а инициирует только <b>OrderLines!</b>
	 * <br> <b>Не использовать для фронта</b>
	 * 
	 * @param dateStart
	 * @param Finish
	 * @param product
	 * @return
	 */
	List<Order> getOrderGroupByPeriodSlotsAndProductNotJOIN(Date dateStart, Date dateFinish, List<Long> goodsIds);
	
	Integer saveOrder (Order order);
	
	void updateOrder (Order order);
	
	int updateOrderFromStatus (Order order);
	
	List<Order> getOrderByPeriodCreateAndCounterparty(Date dateStart, Date dateEnd, String counterparty);
	
	void deleteOrderById(Integer id);
	
	boolean checkOrderHasMarketCode(String code);
	
	Order getOrderHasMarketCode(String code);
	
	/**
	 * Метод который возращает заказы по <b>дате доставки (timeDelivery) и номеру контракта </b>
	 * <br>Без использования join ов. Подтягивается только <b>OrderLines</b>
	 * @param dateStart
	 * @param dateEnd
	 * @param numContract
	 * @return
	 */
	List<Order> getOrderByPeriodDeliveryAndCodeContractNotJOIN(Date dateStart, Date dateEnd, String numContract);
	
}
