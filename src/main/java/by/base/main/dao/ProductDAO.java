package by.base.main.dao;

import java.util.List;
import java.util.Map;

import by.base.main.model.Product;

public interface ProductDAO {

	List<Product> getAllProductList();

	Integer saveProduct(Product product);

	Product getProductByCode(Integer id);
	
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
