package by.base.main.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.RouteHasShopDAO;
import by.base.main.model.RouteHasShop;
import by.base.main.service.RouteHasShopService;
@Service
public class RouteHasShopImpl implements RouteHasShopService{
	
	@Autowired
	private RouteHasShopDAO daoHasShopDAO;

	@Transactional
	@Override
	public List<RouteHasShop> getRouteHasShopList() {
		return daoHasShopDAO.getRouteHasShopList();
	}

	@Transactional
	@Override
	public void saveOrUpdateRouteHasShop(RouteHasShop obj) {
		daoHasShopDAO.saveOrUpdateRouteHasShop(obj);
		
	}

	@Transactional
	@Override
	public RouteHasShop getRouteHasShopById(int id) {
		return daoHasShopDAO.getRouteHasShopById(id);
	}

	@Transactional
	@Override
	public void deleteRouteHasShopById(int id) {
		daoHasShopDAO.deleteRouteHasShopById(id);
		
	}

	@Transactional
	@Override
	public RouteHasShop getRouteHasShopByNum(int id) {
		return daoHasShopDAO.getRouteHasShopByNum(id);
	}

	@Transactional
	@Override
	public RouteHasShop getRouteHasShopByShop(int idShop) {
		return daoHasShopDAO.getRouteHasShopByShop(idShop);
	}

	@Transactional
	@Override
	public RouteHasShop getRouteHasShopByShopAndRoute(int idShop, int idRoute) {
		return daoHasShopDAO.getRouteHasShopByShopAndRoute(idShop, idRoute);
	}

}
