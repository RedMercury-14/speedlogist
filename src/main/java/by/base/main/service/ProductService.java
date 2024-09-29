package by.base.main.service;

import java.util.List;
import java.util.Map;

import by.base.main.model.Product;

public interface ProductService {

	List<Product> getAllProductList();
	
	/**
	 * Возвращает Map<Integer, Product>, где ключ - это код продукта
	 * @return
	 */
	Map<Integer, Product> getAllProductMap();

	Integer saveProduct(Product product);

	Product getProductByCode(Integer id);
	
	Product getProductByCodeAndStock(Integer id, Integer stock);

	void updateProduct(Product product);
}
