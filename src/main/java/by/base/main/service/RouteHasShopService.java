package by.base.main.service;

import java.util.List;

import by.base.main.model.RouteHasShop;

public interface RouteHasShopService {
	
	List<RouteHasShop> getRouteHasShopList();

	void saveOrUpdateRouteHasShop(RouteHasShop obj);

	RouteHasShop getRouteHasShopById(int id);
	
	RouteHasShop getRouteHasShopByNum(int id);

	void deleteRouteHasShopById(int id);
	
	RouteHasShop getRouteHasShopByShop(int idShop);
	
	RouteHasShop getRouteHasShopByShopAndRoute(int idShop, int idRoute);

}
