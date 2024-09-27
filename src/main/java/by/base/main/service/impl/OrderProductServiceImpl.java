package by.base.main.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.OrderProductDAO;
import by.base.main.model.OrderLine;
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

	@Override
	public List<OrderProduct> getOrderProductListHasDate(Date date) {
		return productDAO.getOrderProductListHasDate(date);
	}

	@Override
	public List<OrderProduct> getOrderProductListHasCodeProductAndPeriod(OrderLine orderLine, Date start, Date finish) {
		Integer code = orderLine.getGoodsId().intValue();
		List<OrderProduct> result = productDAO.getOrderProductListHasCodeProductAndPeriod(code, start, finish);
		if(result != null && !result.isEmpty()) {
			result.sort((o1, o2) -> o2.getDateCreate().compareTo(o1.getDateCreate()));// сортируемся от самой ранней даты
			return result;
		}else {
			return null;
		}
	}

}
