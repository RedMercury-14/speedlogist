package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.RouteHasShopDAO;
import by.base.main.model.RouteHasShop;
import by.base.main.service.RouteHasShopService;
@Service
public class RouteHasShopImpl implements RouteHasShopService{
	
	@Autowired
	private RouteHasShopDAO daoHasShopDAO;

	@Override
	public List<RouteHasShop> getRouteHasShopList() {
		return daoHasShopDAO.getRouteHasShopList();
	}

	@Override
	public void saveOrUpdateRouteHasShop(RouteHasShop obj) {
		daoHasShopDAO.saveOrUpdateRouteHasShop(obj);
		
	}

	@Override
	public RouteHasShop getRouteHasShopById(int id) {
		return daoHasShopDAO.getRouteHasShopById(id);
	}

	@Override
	public void deleteRouteHasShopById(int id) {
		daoHasShopDAO.deleteRouteHasShopById(id);
		
	}

	@Override
	public RouteHasShop getRouteHasShopByNum(int id) {
		return daoHasShopDAO.getRouteHasShopByNum(id);
	}

	@Override
	public RouteHasShop getRouteHasShopByShop(int idShop) {
		return daoHasShopDAO.getRouteHasShopByShop(idShop);
	}

	@Override
	public RouteHasShop getRouteHasShopByShopAndRoute(int idShop, int idRoute) {
		return daoHasShopDAO.getRouteHasShopByShopAndRoute(idShop, idRoute);
	}

}
