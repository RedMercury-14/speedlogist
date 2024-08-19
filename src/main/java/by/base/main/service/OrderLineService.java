package by.base.main.service;

import java.util.List;

import by.base.main.model.Order;
import by.base.main.model.OrderLine;

public interface OrderLineService {

	List<OrderLine> getAllOrderLineList();

	Integer saveOrderLine(OrderLine orderLine);

	OrderLine getOrderLineById(Integer id);

	void updateProduct(OrderLine orderLine);
	
	void deleteOrderLineByOrder(Order order);
}
