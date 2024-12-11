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

	List<Product> getProductByCode(Integer id);
	
	Product getProductByCodeAndStock(Integer id, Integer stock);

	void updateProduct(Product product);
	
	/**
	 * Отдаёт мапу где ключ-это конкатинация из кода продуката и склада. (5645645<b>1700</b>)
	 * <br>Значение - это Product
	 * @param codes
	 * @return
	 */
	Map<String, Product> getProductMapHasGroupByCode (List<Integer> codes);
}
