package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import by.base.main.dao.ProductDAO;
import by.base.main.model.Product;
import by.base.main.service.ProductService;

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
	public Product getProductByCode(Integer id) {
		return productDAO.getProductByCode(id);
	}

	@Override
	public void updateProduct(Product product) {
		productDAO.updateProduct(product);		
	}

}
