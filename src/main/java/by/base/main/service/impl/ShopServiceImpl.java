package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.ShopDAO;
import by.base.main.model.Shop;
import by.base.main.service.ShopService;
@Service
public class ShopServiceImpl implements ShopService{
	
	@Autowired
	private ShopDAO shopDAO;

	@Override
	public List<Shop> getShopList() {
		return shopDAO.getShopList();
	}

	@Override
	public void saveShop(Shop shop) {
		shopDAO.saveShop(shop);
		
	}

	@Override
	public Shop getShopByNum(int id) {
		return shopDAO.getShopByNum(id);
	}

	@Override
	public void deleteShopByNum(int id) {
		shopDAO.deleteShopByNum(id);
		
	}

	@Override
	public void updateShop(Shop shop) {
		shopDAO.updateShop(shop);
		
	}

}
