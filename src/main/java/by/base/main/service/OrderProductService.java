package by.base.main.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
	 * Метод принимает OrderLine и по заданному периоду возвращает заказы по этому продукту
	 * <br>Метод исключает дубликаты (Set<>)
	 * @param OrderLine
	 * @param start начало выборки
	 * @param finish конец выборки
	 * @return <b>отсортирован от самой ранней даты</b>
	 */
	List<OrderProduct> getOrderProductListHasCodeProductAndPeriod(OrderLine orderLine, Date start, Date finish);

	void updateOrderProduct(OrderProduct orderProduct);
}
