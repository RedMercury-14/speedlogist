package by.base.main.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import by.base.main.model.OrderProduct;

public interface OrderProductDAO {

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

	void updateOrderProduct(OrderProduct orderProduct);
	
}
