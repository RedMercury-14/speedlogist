package by.base.main.service;

import java.util.List;

import by.base.main.model.Shop;

public interface ShopService {
	
	List<Shop> getShopList();

	void saveShop(Shop shop);

	Shop getShopByNum(int id);

	void deleteShopByNum(int id);
	
	void updateShop(Shop shop);

}
