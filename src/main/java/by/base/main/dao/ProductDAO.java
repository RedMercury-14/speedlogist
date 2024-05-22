package by.base.main.dao;

import java.util.List;

import by.base.main.model.Product;

public interface ProductDAO {

	List<Product> getAllProductList();

	Integer saveProduct(Product product);

	Product getProductByCode(Integer id);

	void updateProduct(Product product);
}
