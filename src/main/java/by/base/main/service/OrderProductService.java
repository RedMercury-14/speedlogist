package by.base.main.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import by.base.main.model.OrderLine;
import by.base.main.model.OrderProduct;

public interface OrderProductService {

	List<OrderProduct> getAllOrderProductList();

	Integer saveOrderProduct(OrderProduct orderProduct);
	
	/**
	 * Отдаёт OrderProduct по id
	 * @param id
	 * @return
	 */
	OrderProduct getOrderProductById(Integer id);
	
	/**
	 * Возвращает заказы поставщикам за определенную дату
	 * @param date
	 * @return
	 */
	List<OrderProduct> getOrderProductListHasDate(Date date);
	
	/**
	 * Возвращает Map<кодтовара, количество> заказы поставщикам за определенную дату
	 * @param date
	 * @return Map<Integer, Integer>, где key=код товара; value=кол-во
	 */
	Map<Integer, Integer> getOrderProductMapHasDate(Date date);
	
	/**
	 * Метод принимает OrderLine и по заданному периоду возвращает заказы по этому продукту
	 * <br>Метод исключает дубликаты (Set<>)
	 * @param OrderLine
	 * @param start начало выборки
	 * @param finish конец выборки
	 * @return <b>отсортирован от самой ранней даты</b>
	 */
	@Deprecated
	List<OrderProduct> getOrderProductListHasCodeProductAndPeriod(OrderLine orderLine, Date start, Date finish);
	
	/**
	 * Метод принимает лист с кодами продуктов и по заданному периоду возвращает заказы по этим продуктам (где они есть)
	 * <br>Метод исключает дубликаты (Set<>)
	 * @param codeProduct
	 * @param start
	 * @param finish
	 * @return
	 */	
	List<OrderProduct> getOrderProductListHasCodeProductGroupAndPeriod(List<OrderLine> orderLines , Date start, Date finish);

	void updateOrderProduct(OrderProduct orderProduct);
}
