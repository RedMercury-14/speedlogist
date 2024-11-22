package by.base.main.dao;
import java.util.List;
import java.util.Map;

import by.base.main.model.Shop;

public interface ShopDAO {
	
	List<Shop> getShopList();
	
	/**
	 * Возвращает мапу с магазинами где ключ - это номер, значение - магазин
	 * @return
	 */
	Map<Integer, Shop> getShopMap();

	void saveShop(Shop shop);

	Shop getShopByNum(int id);

	void deleteShopByNum(int id);

	void updateShop(Shop shop);
}
