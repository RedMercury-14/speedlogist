package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.OrderLineDAO;
import by.base.main.model.Order;
import by.base.main.model.OrderLine;
import by.base.main.service.OrderLineService;

@Service
public class OrderLineServiceImpl implements OrderLineService{

	@Autowired
	private OrderLineDAO orderLineDAO;

	@Override
	public List<OrderLine> getAllOrderLineList() {
		// TODO Auto-generated method stub
		return orderLineDAO.getAllOrderLineList();
	}

	@Override
	public Integer saveOrderLine(OrderLine orderLine) {
		// TODO Auto-generated method stub
		return orderLineDAO.saveOrderLine(orderLine);
	}

	@Override
	public OrderLine getOrderLineById(Integer id) {
		// TODO Auto-generated method stub
		return orderLineDAO.getOrderLineById(id);
	}

	@Override
	public void updateProduct(OrderLine orderLine) {
		orderLineDAO.updateProduct(orderLine);		
	}

	@Override
	public void deleteOrderLineByOrder(Order order) {
		orderLineDAO.deleteOrderLineByOrder(order);
		
	}
	
}
