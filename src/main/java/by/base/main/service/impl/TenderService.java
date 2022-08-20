package by.base.main.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import by.base.main.dao.RouteDAO;
import by.base.main.dao.RouteHasShopDAO;
import by.base.main.model.RouteHasShop;
import by.base.main.model.Tender;

public class TenderService {
	
	@Autowired
	private RouteDAO routeDAO;
	
	@Autowired
	private RouteHasShopDAO routeHasShopDAO;
	
	public Set<Tender> processByTender(Set<Tender> tenders){
		
		while (!tenders.isEmpty()) {
			Tender tender = tenders.stream().findFirst().get();
			tenders.remove(tender);
			Double sumPall = 0.0;
			Double sumWeight = 0.0;
			Set<RouteHasShop> routeHasShops = tender.getRoteHasShop();			
			for (RouteHasShop routeHasShop : routeHasShops) {
				sumPall = sumPall + Double.parseDouble(routeHasShop.getPall());
				sumWeight = sumWeight + Double.parseDouble(routeHasShop.getWeight());
			}
			
		}
		
		return null;
		
	}

}
