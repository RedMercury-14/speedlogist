package by.base.main.service;

import java.util.List;
import java.util.Map;

import by.base.main.model.Shop;

public interface ShopService {
	
	List<Shop> getShopList();
	
	/**
	 * Отдаёт бд всех магазинов в виде мапы где ключ - номер магазина
	 * @return
	 */
	Map<Integer, Shop> getShopMap();

	void saveShop(Shop shop);

	Shop getShopByNum(int id);

	void deleteShopByNum(int id);
	
	void updateShop(Shop shop);

}
