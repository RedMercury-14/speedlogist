package by.base.main.dao;

import java.util.List;

import by.base.main.model.Product;

public interface ProductDAO {

	List<Product> getAllProductList();

	Integer saveProduct(Product product);

	List<Product> getProductByCode(Integer id);
	
	Product getProductByCodeAndStock(Integer id, Integer stock);

	void updateProduct(Product product);
}
