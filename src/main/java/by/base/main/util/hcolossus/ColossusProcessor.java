package by.base.main.util.hcolossus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.base.main.model.Shop;
import by.base.main.service.ShopService;
import by.base.main.util.hcolossus.pojo.Vehicle;
import by.base.main.util.hcolossus.pojo.VehicleWay;
import by.base.main.util.hcolossus.service.LogicAnalyzer;
import by.base.main.util.hcolossus.service.MatrixMachine;
import by.base.main.util.hcolossus.service.ShopMachine;
import by.base.main.util.hcolossus.service.VehicleMachine;

/**
 * Главный класс
 * тут будут находится все методы и алгоритмы 
 */
@Component
public class ColossusProcessor {
	
	private JSONObject jsonMainObject; // временно. Это нужно для получения машин.
	
	@Autowired
	private VehicleMachine vehicleMachine;
	
	@Autowired
	private ShopMachine shopMachine;
	
	@Autowired
	private MatrixMachine matrixMachine;
	
	@Autowired
	private ShopService shopService; 
	
	@Autowired
	private LogicAnalyzer logicAnalyzer; 
	
	private Comparator<Shop> shopComparator = (o1, o2) -> (o2.getNeedPall() - o1.getNeedPall()); //сортирует от большей потребности к меньшей
	
	public void run(JSONObject jsonMainObject, List<Integer> shopList, List<Integer> pallHasShops, Integer stock, Double koeff) {
		Map <Integer,Shop> allShop = shopService.getShopMap();
		Shop targetStock = allShop.get(stock);
		this.jsonMainObject = jsonMainObject;		
		List<VehicleWay> whiteWay = new ArrayList<VehicleWay>();
		//блок подготовки
		matrixMachine.createMatrixHasList(shopList, stock); // заполняем static матрицу. Пусть хранится там
		List<Vehicle> trucks = null;
		try {
			trucks = vehicleMachine.prepareVehicleList(jsonMainObject);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/**
		 * магазины для распределения выстроены в порядке убывания потребностей
		 */
		List <Shop> shopsForOptimization = shopMachine.prepareShopList(shopList, pallHasShops); // магазины для распределения выстроены в порядке убывания потребностей
		//конец блока подготовки
		
		//тут пойдёт логика самого маршрутизатора
		int i = 0; // для тестов, чтобы ограничивать цикл while
		while (!trucks.isEmpty()) {
			if(i == 10) {
				break;
			}
			List <Shop> shopsForDelite = new ArrayList<Shop>();
			List <Shop> shopsForAddNewNeedPall = new ArrayList<Shop>();
			
			for (Shop shop : shopsForOptimization) {	
				if(trucks.isEmpty()) {
					System.err.println("Машины закончились!");
					i++;
					break;
				}
				
				//проверяем, поместится ли вся потребность магазина в одну машину
				Integer shopPall = shop.getNeedPall();
				Integer maxPallTruck = trucks.get(0).getPall();
				//1. определяем идеальные маршруты (одна точка)
				if(shopPall >= maxPallTruck) {
					//логика создания идеального маршрута
					if(shopPall == maxPallTruck) {
						Vehicle targetTruck = trucks.remove(0);
						List<Shop> points = new ArrayList<Shop>();
						points.add(targetStock);
						points.add(shop);
						points.add(targetStock);
						VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
						whiteWay.add(vehicleWay);
						shopsForDelite.add(shop);
					}
					if(shopPall > maxPallTruck) { // тут, если потребность магаза превышает загрузку максимально оставшейся машины - он загружает машину полностью и создаёт магаз с остатком от потребности
						Vehicle targetTruck = trucks.remove(0);
						Integer newNeedPallForShop = shopPall - targetTruck.getPall();
						Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(), shop.getLng());
						newShopHasPall.setNeedPall(newNeedPallForShop);
						List<Shop> points = new ArrayList<Shop>();
						points.add(targetStock);
						points.add(shop);
						points.add(targetStock);
						VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
						whiteWay.add(vehicleWay);
						shopsForDelite.add(shop);
						shopsForAddNewNeedPall.add(newShopHasPall);
						break;
					}
				}
				// 1. конец блока опредиления идеальных маршрутов
				i++;
				if(!shopsForDelite.isEmpty() || !shopsForAddNewNeedPall.isEmpty()) { // обновляемся после итерации
					break;
				}
				// 2. Блок построения типовых маршрутов
				if(shopPall < maxPallTruck) {
					Vehicle truck = trucks.remove(0);
					//виртуально загружаем машину магазином
					truck.setTargetPall(shop.getNeedPall());;
					
					//2.1 ищем самый близкий магазин к текущему магазину, и одновременно самый дальний от склада (поиск дальних магазинов)
					double minFromShop = 99999999.0;
					Integer hasNextShop = null;
					Integer doNotNexnShop = null; // ближайший магазин, но не подходящий по потребностям (т.е. больше чем можно догрузить в фуру)
					double maxFromStock = 0.0;
					Integer numShopTarget = shop.getNumshop();
					
					List<Shop> points = new ArrayList<Shop>();
					points.add(targetStock);
					points.add(shop);
					
					for (Shop shop2 : shopsForOptimization) {
								
						if(truck.getFreePall() == 0) {
							System.out.println("ColossusProcessor.run: Машина " + truck.getName() + " заполнена полностью на этапе 2.1");
							break;
						}
						
						Integer numShopTargetForTest = shop2.getNumshop();
						if(numShopTargetForTest == numShopTarget || numShopTargetForTest.equals(numShopTarget)) {
							continue;
						}
						//проверка для магаза						
						String keyForMatrixShop = numShopTarget+"-"+numShopTargetForTest;
						Double kmShopTest = matrixMachine.matrix.get(keyForMatrixShop);
						//проверка для склада
						String keyForMatrixStock = stock+"-"+numShopTargetForTest;
						Double kmStockTest = matrixMachine.matrix.get(keyForMatrixStock);
						
						//строим мнимый радиус 
						String keyForMatrixRad = stock+"-"+numShopTarget;
						Double rad = matrixMachine.matrix.get(keyForMatrixRad);
						
						if(rad>10000.0) { // тестовый метод, по сути мы уменьшаем мнимый радиус, проверяя сразу есть ли в пределах 10 км магазы
							rad = rad-10000.0; 
						}
						
						if(kmShopTest == null) {
							System.err.println("ColossusProcessor.run: Ошибка! в матрице нет расстояния для ключа " + keyForMatrixShop);							
						}						
						if(kmStockTest >= rad && maxFromStock < kmStockTest) { // ищем самый близкий магазин к текущему магазину, дальше радиуса и дальше всего от склада 
							// удалил из условий выборки minFromShop > kmShopTest && 
//							System.out.println("Смотрим самый близкий магазин к текущему магазину, дальше радиуса и дальше всего от склада: " + shop2);
							minFromShop = kmShopTest;	
							hasNextShop = numShopTargetForTest;
							
							if(shop2.getNeedPall()>truck.getFreePall()) {//если не хватает свободного места, то ищем дальше
								doNotNexnShop = numShopTargetForTest;
								hasNextShop = null;
								continue;
							}else{			//если хватает - добавляем, делаем червновой маршрут, проверяем на логичность
								//тут добавляем мазаз в точку point								
								points.add(shop2);
								points.add(targetStock);
								//проверяем является ли маршрут логичным!
								VehicleWay vehicleWayTest = new VehicleWay(points, 0.0, 30, truck);
								Double logicResult = logicAnalyzer.logicalСheck(vehicleWayTest, koeff);
								System.err.println(logicResult + " дальний радиус");
								if(logicResult>0) {
									truck.setTargetPall(truck.getTargetPall()+shop2.getNeedPall());
//									points.add(shop2);
									shopsForDelite.add(shop2);	
									points.remove(points.size()-1);
									continue;
								}else {
									points.remove(points.size()-1);
									points.remove(points.size()-1);
									continue;
								}
								
								
							}
						}						
									
						
					}
					
					// 2.2 Поиск самых близких к складу магазинов и самых близких от текущего магазина
					double minFromShop2 = 99999999.0;
					Integer hasNextShop2 = null;
					Integer doNotNexnShop2 = null; // ближайший магазин, но не подходящий по потребностям (т.е. больше чем можно догрузить в фуру)
					double maxFromStock2 = 0.0;
					for (Shop shop2 : shopsForOptimization) {
						if(truck.getFreePall() == 0) {
							System.out.println("ColossusProcessor.run: Машина " + truck.getName() + " заполнена полностью на этапе 2.2");
							break;
						}
						Integer numShopTargetForTest = shop2.getNumshop();
						if(numShopTargetForTest == numShopTarget || numShopTargetForTest.equals(numShopTarget)) {
							continue;
						}
						//проверка для магаза						
						String keyForMatrixShop = numShopTarget+"-"+numShopTargetForTest;
						Double kmShopTest = matrixMachine.matrix.get(keyForMatrixShop);
						//проверка для склада
						String keyForMatrixStock = stock+"-"+numShopTargetForTest;
						Double kmStockTest = matrixMachine.matrix.get(keyForMatrixStock);
						
						//строим мнимый радиус 
						String keyForMatrixRad = stock+"-"+numShopTarget;
						Double rad = matrixMachine.matrix.get(keyForMatrixRad);
						if(kmShopTest == null) {
							System.err.println("ColossusProcessor.run: Ошибка! в матрице нет расстояния для ключа " + keyForMatrixShop);							
						}
						
						if(kmStockTest <= rad && maxFromStock2 < kmStockTest) { // ищем самый близкий магазин к текущему магазину, внутри радиуса и дальше всего от склада
							//удалил из условий выборки minFromShop2 > kmShopTest &&
							minFromShop2 = kmShopTest;	
							hasNextShop2 = numShopTargetForTest;
							
							if(shop2.getNeedPall()>truck.getFreePall()) {//если не хватает свободного места, то ищем дальше
								doNotNexnShop2 = numShopTargetForTest;
								hasNextShop2 = null;
								continue;
							}else{			//если хватает - добавляем, делаем червновой маршрут, проверяем на логичность
								//тут добавляем мазаз в точку point								
								points.add(shop2);
								points.add(targetStock);
								//проверяем является ли маршрут логичным!
								VehicleWay vehicleWayTest = new VehicleWay(points, 0.0, 30, truck);
								Double logicResult = logicAnalyzer.logicalСheck(vehicleWayTest, 1.2);
								System.err.println(logicResult + " ближний радиус");
								if(logicResult>0) {
									truck.setTargetPall(truck.getTargetPall()+shop2.getNeedPall());
//									points.add(shop2);
									shopsForDelite.add(shop2);	
									points.remove(points.size()-1);
									continue;
								}else {
									points.remove(points.size()-1);
									points.remove(points.size()-1);
									continue;
								}
								
								
							}
						}
						
					}
					
					// 2.3 формирование маршрута второго этапа
					points.add(targetStock);
					VehicleWay vehicleWay = new VehicleWay(points, 0.0, 30, truck);
					whiteWay.add(vehicleWay);
					shopsForDelite.add(shop);					
				}
			}
			
			
			
			//после каждого цикла очищаем магазы
			if(!shopsForDelite.isEmpty()) {
				for (Shop shop : shopsForDelite) {
					shopsForOptimization.remove(shop);
				}
//				shopsForDelite.clear();
				shopsForOptimization.sort(shopComparator); // сортируем обновлённый список
			}
			
			//после каждого цикла добавляем новые магазы, если есть
			if(!shopsForAddNewNeedPall.isEmpty()) {
				for (Shop shop : shopsForAddNewNeedPall) {
					shopsForOptimization.add(shop);
				}
//				shopsForAddNewNeedPall.clear();
				shopsForOptimization.sort(shopComparator); // сортируем обновлённый список
			}
			
			
		}
		
		
		whiteWay.forEach(w-> System.out.println(w));
		System.out.println("===================");
		trucks.forEach(t-> System.out.println(t));
		System.out.println("++++++++++++++++++++");
		shopsForOptimization.forEach(s-> System.out.println(s));
		
	}
	
	private String idealWay() {
		return null;
		
	}

}
