package by.base.main.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.Shop;


public class MainTest2 {

	public static void main(String[] args) throws InvalidFormatException, IOException {
		Route route = new Route();
		
		Set <RouteHasShop> routeHasShops = new HashSet<RouteHasShop>();
		Shop shop1 = new Shop(111, "asd");
		Shop shop2 = new Shop(222, "asd");
		Shop shop3 = new Shop(333, "asd");
		Shop shop4 = new Shop(444, "asd");
		RouteHasShop routeHasShop1 = new RouteHasShop(1, null, null, shop1);
		RouteHasShop routeHasShop2 = new RouteHasShop(2, null, null, shop2);
		RouteHasShop routeHasShop3 = new RouteHasShop(3, null, null, shop3);
		RouteHasShop routeHasShop4 = new RouteHasShop(4, null, null, shop4);
		routeHasShops.add(routeHasShop1);
		routeHasShops.add(routeHasShop2);
		routeHasShops.add(routeHasShop3);
		routeHasShops.add(routeHasShop4);
		route.setRoteHasShop(routeHasShops);
		
		
		double km = 0.0;
		//RouteHasShop[] array = (RouteHasShop[]) routeHasShops.toArray();
		System.out.println(routeHasShops.size());
		for(int i = 0; i<=routeHasShops.size()+3; i++) {
			RouteHasShop target1 = routeHasShops.stream().findFirst().get();
			routeHasShops.remove(target1);
			if(!routeHasShops.isEmpty()) {
				System.out.println(target1.getShop().getNumshop()+"-"+routeHasShops.stream().findFirst().get().getShop().getNumshop());				
			}else {
				System.out.println("dsa");
			}
		}
	}
}