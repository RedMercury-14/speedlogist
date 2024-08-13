package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.OrderProductDAO;
import by.base.main.model.OrderProduct;
import by.base.main.service.OrderProductService;

@Service
public class OrderProductServiceImpl implements OrderProductService{
	
	@Autowired
	private OrderProductDAO productDAO;

	@Override
	public List<OrderProduct> getAllOrderProductList() {
		return productDAO.getAllOrderProductList();
	}

	@Override
	public Integer saveOrderProduct(OrderProduct orderProduct) {
		return productDAO.saveOrderProduct(orderProduct);
	}

	@Override
	public OrderProduct getOrderProductById(Integer id) {
		return productDAO.getOrderProductById(id);
	}

	@Override
	public void updateOrderProduct(OrderProduct orderProduct) {
		productDAO.updateOrderProduct(orderProduct);
	}

}
