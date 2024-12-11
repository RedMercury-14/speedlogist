package by.base.main.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.aspect.TimedExecution;
import by.base.main.dao.ProductDAO;
import by.base.main.model.OrderProduct;
import by.base.main.model.Product;
import by.base.main.service.ProductService;
@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	private ProductDAO productDAO; 

	@Override
	public List<Product> getAllProductList() {
		return productDAO.getAllProductList();
	}

	@Override
	public Integer saveProduct(Product product) {
		return productDAO.saveProduct(product);
	}

	@Override
	public List<Product> getProductByCode(Integer id) {
		return productDAO.getProductByCode(id);
	}

	@Override
	public void updateProduct(Product product) {
		productDAO.updateProduct(product);		
	}

	@Override
	public Product getProductByCodeAndStock(Integer id, Integer stock) {
		return productDAO.getProductByCodeAndStock(id, stock);
	}

	@Override
	public Map<Integer, Product> getAllProductMap() {
		 List<Product> products = productDAO.getAllProductList();
		 Map<Integer, Product> response = new HashMap<Integer, Product>();

		 for (Product product : products) {
		     response.put(product.getCodeProduct(), product);
		 }
		return response;
	}

}
