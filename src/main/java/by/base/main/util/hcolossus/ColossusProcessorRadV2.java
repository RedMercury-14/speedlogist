package by.base.main.util.hcolossus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.base.main.model.Shop;
import by.base.main.service.ShopService;
import by.base.main.util.hcolossus.pojo.Solution;
import by.base.main.util.hcolossus.pojo.Vehicle;
import by.base.main.util.hcolossus.pojo.VehicleWay;
import by.base.main.util.hcolossus.service.ComparatorShops;
import by.base.main.util.hcolossus.service.ComparatorShopsDistanceMain;
import by.base.main.util.hcolossus.service.LogicAnalyzer;
import by.base.main.util.hcolossus.service.MatrixMachine;
import by.base.main.util.hcolossus.service.ShopMachine;
import by.base.main.util.hcolossus.service.VehicleMachine;

@Component
public class ColossusProcessorRadV2 {
	

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
	
//	private Comparator<Shop> shopComparator = (o1, o2) -> (o2.getNeedPall() - o1.getNeedPall()); //сортирует от большей потребности к меньшей
	
	private ComparatorShops shopComparatorForIdealWay = new ComparatorShops(); //сортирует от большей потребности к меньшей и от большего расстояния от склада к меньшему
	private ComparatorShopsDistanceMain shopComparatorDistanceMain = new ComparatorShopsDistanceMain(); //сортирует от большего расстояния от склада к меньшему и от большей потребности к меньшей
	private ComparatorShops shopComparator = new ComparatorShops(); //сортирует от большей потребности к меньшей  и от большего расстояния от склада к меньшему
	
	public Solution run(JSONObject jsonMainObject, List<Integer> shopList, List<Integer> pallHasShops, Integer stock, Double koeff) {
		String stackTrace;
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
//		List <Shop> shopsForOptimization = shopMachine.prepareShopList(shopList, pallHasShops); // магазины для распределения выстроены в порядке убывания потребностей
		List <Shop> shopsForOptimization = shopMachine.prepareShopList2Parameters(shopList, pallHasShops, stock); // магазины для распределения выстроены в порядке убывания потребностей TEST
//		List <Shop> shopsForOptimization = shopMachine.prepareShopList2ParametersDistanceMain(shopList, pallHasShops, stock); // магазины для распределения выстроены в порядке убывания потребностей TEST
//		List <Shop> shopsForOptimization = shopMachine.prepareShopList2ParametersTEST(shopList, pallHasShops, stock); // магазины для распределения выстроены в порядке убывания потребностей TEST
		
		System.out.println("Начальное распределение магазинов слеюущее: ");
		stackTrace = "Начальное распределение магазинов слеюущее: \n";
		for (Shop s : shopsForOptimization) {
			System.out.println("->"+s.getNumshop() + " - " + s.getDistanceFromStock()+" km - " + s.getNeedPall() + " pall");
			stackTrace = stackTrace + "магазин : "+s.getNumshop() + " - " + s.getDistanceFromStock()+" км - " + s.getNeedPall() + " паллет\n";
		}
		System.out.println("===========");
		stackTrace = stackTrace + "===========\n";
		
		//конец блока подготовки
		
		//тут пойдёт логика самого маршрутизатора
		int i = 0; // для тестов, чтобы ограничивать цикл while
		int iMax = shopsForOptimization.size()*shopsForOptimization.size();
		int j = 0; // итерация
		while (!trucks.isEmpty()) {
			if(i == iMax) {
				break;
			}
			if(shopsForOptimization.isEmpty()) {
				System.err.println("Магазины закончились!");
				i++;
				break;
			}
			
			List <Shop> shopsForDelite = new ArrayList<Shop>();
			List <Shop> shopsForAddNewNeedPall = new ArrayList<Shop>();
			
			List <Shop> shopsForOptimizationHasIdealWay = new ArrayList<Shop>(shopsForOptimization);
			shopsForOptimizationHasIdealWay.sort(shopComparatorForIdealWay);
			
			for (Shop shop : shopsForOptimizationHasIdealWay) {
				//проверяем, поместится ли вся потребность магазина в одну машину
				Integer shopPall = shop.getNeedPall();
				Integer maxPallTruck = trucks.get(0).getPall();
				//1. определяем идеальные маршруты (одна точка)
				if(shopPall >= maxPallTruck) {
					//логика создания идеального маршрута
					if(shopPall == maxPallTruck) {
						Vehicle targetTruck = trucks.remove(0);
						targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
						List<Shop> points = new ArrayList<Shop>();
						points.add(targetStock);
						shop.setNeedPall(targetTruck.getPall()); // указываем текущую потребность магазина для этой фуры
						points.add(shop);
						points.add(targetStock);
						VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
						whiteWay.add(vehicleWay);
						shopsForDelite.add(shop);
					}
					if(shopPall > maxPallTruck) { // тут, если потребность магаза превышает загрузку максимально оставшейся машины - он загружает машину полностью и создаёт магаз с остатком от потребности
						Vehicle targetTruck = trucks.remove(0);
						targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
						Integer newNeedPallForShop = shopPall - targetTruck.getPall();
						Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(), shop.getLng());
						newShopHasPall.setNeedPall(newNeedPallForShop);
						List<Shop> points = new ArrayList<Shop>();
						shopsForDelite.add(shop);
						shopsForAddNewNeedPall.add(newShopHasPall);
						shop.setNeedPall(targetTruck.getPall()); // указываем текущую потребность магазина для этой фуры
						points.add(targetStock);
						points.add(shop);
						points.add(targetStock);
						VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
						whiteWay.add(vehicleWay);
						break;
					}
				}
				
				// 1. конец блока опредиления идеальных маршрутов
				i++;
				if(!shopsForDelite.isEmpty() || !shopsForAddNewNeedPall.isEmpty()) { // обновляемся после итерации
					break;
				}
			}
			
			if(shopsForDelite.isEmpty() || shopsForAddNewNeedPall.isEmpty()) { // если в листе удаления магазина есть хоть один элемент то второй этап пропускаем
				//сортируем в зависимости от логики
				shopsForOptimization.sort(shopComparator);
				
				for (Shop shop : shopsForOptimization) {	
					if(trucks.isEmpty()) {
						System.err.println("Машины закончились!");
//						i++;
						break;
					}
					
					Integer shopPall = shop.getNeedPall();
					Integer maxPallTruck = trucks.get(0).getPall();
					
					//дополнительно проверяем нет ли этого магазина в списке на удаление
					if(shopsForDelite.contains(shop)) {
						continue;
					}
					
					// 2. Блок построения типовых маршрутов
					if(shopPall < maxPallTruck) {
						Vehicle truck = trucks.remove(0);
						//виртуально загружаем машину магазином
						truck.setTargetPall(shop.getNeedPall());
						
						//2.1 ищем самый близкий магазин к текущему магазину (по радиусу)
						double minFromShop = 99999999.0;
						Integer numShopTarget = shop.getNumshop();
						
						List<Shop> points = new ArrayList<Shop>();
						points.add(targetStock);
						points.add(shop);
						
						//тут необходимо создать новый лист и выстраить магазины по расстояниям
						Map <Double, Shop> radiusMap = new TreeMap<Double, Shop>();
						
						for (Shop shopHasRadius : shopsForOptimization) {
							Integer numShopTargetForTest = shopHasRadius.getNumshop();
							if(numShopTargetForTest == numShopTarget || numShopTargetForTest.equals(numShopTarget)) {
								continue;
							}						
							String keyForMatrixShop = numShopTarget+"-"+numShopTargetForTest; //от таргетного магаза к потенциальному
							Double kmShopTest = matrixMachine.matrix.get(keyForMatrixShop);
							radiusMap.put(kmShopTest, shopHasRadius);
						}
//						System.out.println("=====Самые близкие магазы к магазину: "+shop.getNumshop()+" =====");
//						radiusMap.forEach((k,v) -> System.out.println(k + "    " + v));
//						System.out.println("=====TEST=====");
						//закончили выборку
						
						VehicleWay vehicleWay;
						
						for (Map.Entry<Double, Shop> entry : radiusMap.entrySet()) {
							
							Shop shop2 = entry.getValue();
							
							//дополнительно проверяем нет ли этого магазина в списке на удаление
							if(shopsForDelite.contains(shop2)) {
								continue;
							}
							if(truck.getFreePall() == 0) {
								System.out.println("ColossusProcessor.run: Машина " + truck.getName() + " заполнена полностью на этапе 2.1");
								break;
							}
							
							Integer numShopTargetForTest = shop2.getNumshop();
							if(numShopTargetForTest == numShopTarget || numShopTargetForTest.equals(numShopTarget)) {
								continue;
							}
							
							
							//проверка для магаза						
							String keyForMatrixShop = numShopTarget+"-"+numShopTargetForTest; //от таргетного магаза к потенциальному
							Double kmShopTest = matrixMachine.matrix.get(keyForMatrixShop);
							
							if(kmShopTest == null) {
								System.err.println("ColossusProcessor.run: Ошибка! в матрице нет расстояния для ключа " + keyForMatrixShop);							
							}	
								
								if(shop2.getNeedPall()>truck.getFreePall()) {//если не хватает свободного места, то ищем дальше
									continue;
								}else{			
									//если хватает - добавляем, делаем червновой маршрут, проверяем на логичность
									//тут добавляем мазаз в точку point								
									points.add(shop2);
									points.add(targetStock);
									//проверяем является ли маршрут логичным!
									VehicleWay vehicleWayTest = new VehicleWay(points, 0.0, 30, truck);
									Double logicResult = logicAnalyzer.logicalСheck(vehicleWayTest, koeff);
//									System.err.println(logicResult + " логичность маршрута составила");
									if(logicResult>0) {
										truck.setTargetPall(truck.getTargetPall()+shop2.getNeedPall());
//										points.add(shop2);
										shopsForDelite.add(shop2);	
										points.remove(points.size()-1);
										System.out.println("кладём " + shop2);
										vehicleWay = vehicleWayTest;
//										continue;
										break; // находим самую ближайшую и логичную точку от таргетного магазина
									}else {
										System.out.println("не кладём, т.к. не логично " + shop2);
										points.remove(points.size()-1);
										points.remove(points.size()-1);
										continue;
									}
									
									
								}							
						}
						
						
						// 2.3 формирование маршрута второго этапа
						points.add(targetStock);
						
						if(truck.isFull()) { // если машина загружена полностью, то 35 статус
							vehicleWay = new VehicleWay(points, 0.0, 35, truck);
						}else { // если машина загружена не полностью, то 30 статус
							vehicleWay = new VehicleWay(points, 0.0, 30, truck);
						}
						whiteWay.add(vehicleWay);
						shopsForDelite.add(shop);					
					}
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
			stackTrace = stackTrace + "Итерация " + j + ": \n";
			j++;
			for (VehicleWay v : whiteWay) {
				stackTrace = stackTrace + v+"\n";
			}
			
		} // КОНЕЦ ОСНОВНОГО ЦИКЛА ДЛЯ ПУНКТОВ 1 И 2
		
		
		
		System.out.println("======Догружаем остатки======");
		stackTrace = stackTrace + "======Догружаем остатки======\n";
		
		System.out.println("Оставшиеся магазы:");
		shopsForOptimization.forEach(s-> System.out.println(s));
		System.out.println("КОНЕЦ Оставшиеся магазы:");
		
		// 3. Алгоритм догруза

		List <Shop> outsiderShops = new ArrayList<Shop>(shopsForOptimization); // магазины, которые остались после второго пункта, т.к. однозначно делающие маршрут нелогичным
		stackTrace = stackTrace + "Недогруженные магазины: \n";
		for (Shop v : outsiderShops) {
			stackTrace = stackTrace + v.getNumshop() + " ("+v.getNeedPall()+") " +"\n";
		}
		List <Shop> shopForDeliteHasOutsiderShops = new ArrayList<Shop>();
		List<VehicleWay> outsiderWay = new ArrayList<VehicleWay>();
		whiteWay.forEach(w->{
			if(!w.getVehicle().isFull()) {
				outsiderWay.add(w);
			}
		});
		int stop = 0;
		int maxStop = outsiderShops.size()*2;
		while (!outsiderShops.isEmpty() && stop != maxStop) {
			
			//меняем логику догруза: сначала берем недогруженные машруты и ищем самый ближайший неназначенные магазин.
			Map <Double, VehicleWay> costOfOwerWay = new TreeMap<Double, VehicleWay>(); // мапа стоимостей перепробегов
			for (VehicleWay vehicleWay : outsiderWay) {
				
				
				Shop numShopTarget = vehicleWay.getWay().get(vehicleWay.getWay().size()-2);
				Map <Double, Shop> radiusMap = new TreeMap<Double, Shop>();
				
				
				
				for (Shop shopHasRadius : outsiderShops) {
					Integer numShopTargetForTest = shopHasRadius.getNumshop();
					if(numShopTargetForTest == numShopTarget.getNumshop() || numShopTargetForTest.equals(numShopTarget.getNumshop())) {
						continue;
					}						
					String keyForMatrixShop = numShopTarget.getNumshop()+"-"+numShopTargetForTest; //от таргетного магаза к потенциальному
					Double kmShopTest = matrixMachine.matrix.get(keyForMatrixShop);
					radiusMap.put(kmShopTest, shopHasRadius);
				}
				
				for (Map.Entry<Double, Shop> entry: radiusMap.entrySet()) {
					Shop shop = entry.getValue();
					if(vehicleWay.getVehicle().getFreePall()>= shop.getNeedPall()) {
						VehicleWay testWay = new VehicleWay(vehicleWay.getId().split("\\[")[0]+"[догруз]"); // создаём новый экземпляр маршрута догруза, чтобы не испортить старые маршруты
						List <Shop> points = new ArrayList<Shop>(vehicleWay.getWay());
						Vehicle truck = vehicleWay.getVehicle().getVirtualVehicle();
						truck.setTargetPall(truck.getTargetPall()+shop.getNeedPall());
						Shop lastPoint = points.remove(points.size()-1);
						points.add(shop);
						points.add(lastPoint);
						testWay.setWay(points);
						truck.setId(truck.getId()*-1);
						testWay.setVehicle(truck);						
						Double logicResult = logicAnalyzer.logicalСheck(testWay, koeff);
						testWay.setOverrun(logicResult);
						List <Shop> problemShop = new ArrayList<Shop>();
						problemShop.add(shop);
						testWay.setProblemShops(problemShop);
//						System.err.println(logicResult + " логичность маршрута составила после догруза составила");						
						costOfOwerWay.put(logicResult, testWay);
					}
				}
				
			}
					
			
			//блок принятие решения по догрузу
			System.out.println("ПРИНИМАЕМ РЕШЕНИЕ ИЗ - маршрутов: ");
			costOfOwerWay.forEach((k,v) -> System.out.println(k + "     " + v));
			if(costOfOwerWay.isEmpty()) {
				stop++;
				continue;
			}
			VehicleWay newWay = (VehicleWay) costOfOwerWay.values().toArray()[costOfOwerWay.size()-1];
//			System.out.println("Вытащил этот маршрут: "+newWay);
			String idWayForReplace = newWay.getId().split("\\[")[0];
			for (Iterator<VehicleWay> iterator = whiteWay.iterator(); iterator.hasNext();) {
				VehicleWay value = iterator.next();
				if(value.getId().equals(idWayForReplace)) {
					iterator.remove();
				}
				if(value.getId().equals(newWay.getId())) {
					iterator.remove();
				}
			}
			whiteWay.add(newWay);
			shopForDeliteHasOutsiderShops.addAll(newWay.getProblemShops());	
			
			//после каждого цикла очищаем магазы
			if(!shopForDeliteHasOutsiderShops.isEmpty()) {
				for (Shop shop : shopForDeliteHasOutsiderShops) {
					outsiderShops.remove(shop);
					shopsForOptimization.remove(shop);
					costOfOwerWay.clear();
				}
//				shopsForDelite.clear();
//				shopsForOptimization.sort(shopComparator); // сортируем обновлённый список
			}
			
			//обновляем outsiderWay
			outsiderWay.clear();
			whiteWay.forEach(w->{
				if(!w.getVehicle().isFull()) {
					outsiderWay.add(w);
				}
			});
			stop++;
		}
		
		//4 этап. Проверка вариация проблемных магазинов
		List <VehicleWay> wayWhisProblemShop = new ArrayList<VehicleWay>();
		
		//заполняем лист с проблемными маршрутами
		for (VehicleWay vehicleWay : whiteWay) {
			if(vehicleWay.getProblemShops() != null && !vehicleWay.getProblemShops().isEmpty()) {
				wayWhisProblemShop.add(vehicleWay);
			}
		}
		
		for (VehicleWay problemWay : wayWhisProblemShop) {
			Shop problemShop = problemWay.getProblemShops().get(problemWay.getProblemShops().size()-1);
			Double oldOverWay = problemWay.getOverrun();
//			System.err.println("problemShop -> " + problemShop.getNumshop() + "   перепробег: " + oldOverWay);
			for (VehicleWay vehicleWa22y : whiteWay) {
				if(problemWay.equals(vehicleWa22y)) {
					continue;
				}
				Shop replaseShop = vehicleWa22y.getWay().get(vehicleWa22y.getWay().size()-2);
				VehicleWay vehicleWay = new VehicleWay(problemWay.getId()+"[проверка]");
				vehicleWay.setFreePallInVehicle(problemWay.getFreePallInVehicle());
				vehicleWay.setOverrun(problemWay.getOverrun());
				vehicleWay.setProblemShops(problemWay.getProblemShops());
				vehicleWay.setStatus(problemWay.getStatus());
				vehicleWay.setVehicle(problemWay.getVehicle());
				vehicleWay.setWay(problemWay.getWay());
				List<Shop> points = new ArrayList<Shop>(vehicleWay.getWay());
				Vehicle truck = vehicleWay.getVehicle().getVirtualVehicle();
				
				Shop shopForReplace = points.remove(points.size()-2);
				truck.setTargetPall(truck.getTargetPall() - shopForReplace.getNeedPall());
				if(truck.getFreePall() >= replaseShop.getNeedPall()) {
					points.add(points.size()-1, replaseShop);
					vehicleWay.setWay(points);
					Double newOverWay = logicAnalyzer.logicalСheck(vehicleWay, koeff);		
					vehicleWay.setOverrun(newOverWay);
//					System.out.println("Проверяем такой маршрут: " + vehicleWay);
					if(newOverWay>oldOverWay) {
						System.out.println("я бы поменял местами магазы");
					}
				}else {
					continue;
				}
				
				
				
			}
			
		}
		
		
		
		
		stackTrace = stackTrace + "Результат после третьего этапа: \n";
		for (VehicleWay v : whiteWay) {
			stackTrace = stackTrace + v+"\n";
		}
		stackTrace = stackTrace + "========= Свободные авто ==========\n";
		for (Vehicle v : trucks) {
			stackTrace = stackTrace + v+"\n";
		}
		stackTrace = stackTrace + "+++++++++ Оставшиеся магазины +++++++++++\n";
		for (Shop v : shopsForOptimization) {
			stackTrace = stackTrace +v.getNumshop() + " ("+v.getNeedPall()+") " +"\n";
		}
		
		whiteWay.forEach(w-> System.out.println(w));
		System.out.println("========= Свободные авто ==========");
		trucks.forEach(t-> System.out.println(t));
		System.out.println("+++++++++ Оставшиеся магазины +++++++++++");
		shopsForOptimization.forEach(s-> System.out.println(s));
		
		Solution solution = new Solution();
		solution.setEmptyShop(shopsForOptimization);
		solution.setEmptyTrucks(trucks);
		solution.setWhiteWay(whiteWay);
		solution.setStackTrace(stackTrace);
		
		
		return solution;
		
	}
	
	private String idealWay() {
		return null;
		
	}

}
