package by.base.main.service;

import java.util.List;

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

	void updateOrderProduct(OrderProduct orderProduct);
}
