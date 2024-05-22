package by.base.main.service;

import java.util.List;

import by.base.main.model.Product;

public interface ProductService {

	List<Product> getAllProductList();

	Integer saveProduct(Product product);

	Product getProductByCode(Integer id);

	void updateProduct(Product product);
}
