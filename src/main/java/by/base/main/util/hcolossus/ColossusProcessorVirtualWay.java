package by.base.main.util.hcolossus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

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
public class ColossusProcessorVirtualWay {
	
	private JSONObject jsonMainObject;
	
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
	
	/**
	 * Основной метод расчёта первочной оптимизации
	 * @param jsonMainObject
	 * @param shopList
	 * @param pallHasShops
	 * @param stock
	 * @param koeff - коэфициент трубности маршрута, т.к. процент перебробега
	 * @param algoritm - название внутреннего алгоритма
	 *  <br>	* fullLoad - подбор машины так, чтобы сразу полностью загрузить
	 *  <br>	* noFullLoad - подбор машины так, чтобы не полностью загружать ее
	 * @return
	 */
	public Solution run(JSONObject jsonMainObject, List<Integer> shopList, List<Integer> pallHasShops, Integer stock, Double koeff, String algoritm) {
		String stackTrace;
		Map <Integer,Shop> allShop = shopService.getShopMap();
		Shop targetStock = allShop.get(stock);
		this.jsonMainObject = jsonMainObject;		
		List<VehicleWay> whiteWay = new ArrayList<VehicleWay>();
		List<Vehicle> vehicleForDelete = new ArrayList<Vehicle>();
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
			
			
			
			
			if(shopsForDelite.isEmpty() || shopsForAddNewNeedPall.isEmpty()) {// если в листе удаления магазина есть хоть один элемент то второй этап пропускаем
				//пункт 2 - ищем самый дальний и самый загруженный магазин а потом догружаем ближайшими к нему и т.д.
				//после, создаём виртуальный маршрут!
				//кладём его в максимально подходящую тачку
				shopsForOptimization.sort(shopComparator);
				
				//тест! При первом прогоне применяем рандомную сортировку
//				Collections.shuffle(shopsForOptimization);
				
				Shop firstShop = shopsForOptimization.remove(0);
				
				//создаём матрицу расстояний от первого магазина
				Map <Double, Shop> radiusMap = new TreeMap<Double, Shop>();			
				radiusMap = getDistanceMatrixHasMin(shopsForOptimization, firstShop);
				
				//создаём порядок точек
				List<Shop> points = new ArrayList<Shop>();
				points.add(targetStock);
				points.add(firstShop);
				
				stackTrace = stackTrace + "МАТРИЦА МАГАЗИНА " + firstShop.getNumshop()+ "\n";
				for (Map.Entry<Double, Shop> entry : radiusMap.entrySet()) {
					stackTrace = stackTrace + entry.getKey() + " - " + entry.getValue().getNumshop() + "\n";
				}
				stackTrace = stackTrace + "==============\n";
				
				for (Map.Entry<Double, Shop> entry : radiusMap.entrySet()) {
					Shop shop2 = entry.getValue();
					//тут добавляем мазаз в точку point								
					points.add(shop2);
					points.add(targetStock);
					//проверяем является ли маршрут логичным!
					VehicleWay vehicleWayTest = new VehicleWay(points, 0.0, 30, null);
					Double logicResult = logicAnalyzer.logicalСheck(vehicleWayTest, koeff);
//					System.err.println(logicResult + " логичность маршрута составила");
					if(logicResult>0) {
						//проверяем, в теории сможем ли мы положить в какую-нибудь машину такой маргрут
						Integer summPallHasPoints  = 0;
						boolean flag = false;
						for (Shop shop3 : points) {
							if(shop3.getNeedPall() == null) {
								continue;
							}
							summPallHasPoints = summPallHasPoints + shop3.getNeedPall();
						}
						for (Vehicle truck : trucks) {
							if(truck.getPall() >= summPallHasPoints) {
								flag=true;
							}
						}
						if(flag) {
//							points.add(shop2);
							shopsForDelite.add(shop2);	
							points.remove(points.size()-1);
//							System.out.println("кладём " + shop2);					
							continue;
						}else {
//							System.out.println("не кладём, т.к. нет такой большой машины " + shop2);
							points.remove(points.size()-1);
							points.remove(points.size()-1);
							continue;
						}
						
					}else {
//						System.out.println("не кладём, т.к. не логично " + shop2);
						points.remove(points.size()-1);
						points.remove(points.size()-1);
						continue;
					}
				}
				//создаём финальный, виртуальный маршрут
				points.add(targetStock);
				VehicleWay vehicleWayVirtual = new VehicleWay(points, 0.0, 30, null);
				
//				points.forEach(s -> System.out.println("Final -- "+s));
//				virtualWay.add(vehicleWayVirtual);
//				System.err.println(vehicleWayVirtual);
				
				//подбираем и ставим машину в виртуальный маршрут
				//используем разные алгоритмы
				switch (algoritm) {
				case "fullLoad":
					for (Vehicle truck : trucks) { // если удалось собрать сразу всю машину
						Integer pallHasWay = vehicleWayVirtual.getSummPall();
						if(truck.getPall() == pallHasWay) {
							truck.setTargetPall(pallHasWay);
							vehicleWayVirtual.setVehicle(truck);
							vehicleWayVirtual.setStatus(35);
							vehicleForDelete.add(truck);
							break;
						}
					}
					if(vehicleWayVirtual.getVehicle() == null) {
						//подбираем и ставим машину в виртуальный маршрут
						for (Vehicle truck : trucks) { // недогруженная машина
							Integer pallHasWay = vehicleWayVirtual.getSummPall();
							if(truck.getPall() > pallHasWay) {
								truck.setTargetPall(pallHasWay);
								vehicleWayVirtual.setVehicle(truck);
								vehicleWayVirtual.setStatus(30);
								vehicleForDelete.add(truck);
								break;
							}
						}
					}
					break;

				case "noFullLoad":
					if(vehicleWayVirtual.getVehicle() == null) {
						//подбираем и ставим машину в виртуальный маршрут
						for (Vehicle truck : trucks) { // недогруженная машина
							Integer pallHasWay = vehicleWayVirtual.getSummPall();
							if(truck.getPall() > pallHasWay) {
								truck.setTargetPall(pallHasWay);
								vehicleWayVirtual.setVehicle(truck);
								vehicleWayVirtual.setStatus(30);
								vehicleForDelete.add(truck);
								break;
							}
						}
					}
					for (Vehicle truck : trucks) { // если удалось собрать сразу всю машину
						Integer pallHasWay = vehicleWayVirtual.getSummPall();
						if(truck.getPall() == pallHasWay) {
							truck.setTargetPall(pallHasWay);
							vehicleWayVirtual.setVehicle(truck);
							vehicleWayVirtual.setStatus(35);
							vehicleForDelete.add(truck);
							break;
						}
					}
					break;
				}

				whiteWay.add(vehicleWayVirtual);
				
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
			
			//после каждого цикла удаляем загруженные авто
			if(!vehicleForDelete.isEmpty()) {
				for (Vehicle truck : vehicleForDelete) {
					trucks.remove(truck);
				}
			}
			
			stackTrace = stackTrace + "Итерация " + j + ": \n";
			j++;
			for (VehicleWay v : whiteWay) {
				stackTrace = stackTrace + v+"\n";
			}
			
			
		} // КОНЕЦ ОСНОВНОГО ЦИКЛА ДЛЯ ПУНКТОВ 1 И 2
		
		// 3. Алгоритм догруза

//				List <Shop> outsiderShops = new ArrayList<Shop>(shopsForOptimization); // магазины, которые остались после второго пункта, т.к. однозначно делающие маршрут нелогичным
//				stackTrace = stackTrace + "Недогруженные магазины: \n";
//				for (Shop v : outsiderShops) {
//					stackTrace = stackTrace + v.getNumshop() + " ("+v.getNeedPall()+") " +"\n";
//				}
//				List <Shop> shopForDeliteHasOutsiderShops = new ArrayList<Shop>();
//				List<VehicleWay> outsiderWay = new ArrayList<VehicleWay>();
//				whiteWay.forEach(w->{
//					if(!w.getVehicle().isFull()) {
//						outsiderWay.add(w);
//					}
//				});
//				int stop = 0;
//				int maxStop = outsiderShops.size()*2;
//				while (!outsiderShops.isEmpty() && stop != maxStop) {
//					
//					//меняем логику догруза: сначала берем недогруженные машруты и ищем самый ближайший неназначенные магазин.
//					Map <Double, VehicleWay> costOfOwerWay = new TreeMap<Double, VehicleWay>(); // мапа стоимостей перепробегов
//					for (VehicleWay vehicleWay : outsiderWay) {
//						
//						
//						Shop numShopTarget = vehicleWay.getWay().get(vehicleWay.getWay().size()-2);
//						Map <Double, Shop> radiusMap = new TreeMap<Double, Shop>();
//						
//						for (Shop shopHasRadius : outsiderShops) {
//							Integer numShopTargetForTest = shopHasRadius.getNumshop();
//							if(numShopTargetForTest == numShopTarget.getNumshop() || numShopTargetForTest.equals(numShopTarget.getNumshop())) {
//								continue;
//							}						
//							String keyForMatrixShop = numShopTarget.getNumshop()+"-"+numShopTargetForTest; //от таргетного магаза к потенциальному
//							Double kmShopTest = matrixMachine.matrix.get(keyForMatrixShop);
//							radiusMap.put(kmShopTest, shopHasRadius);
//						}
//						
//						for (Map.Entry<Double, Shop> entry: radiusMap.entrySet()) {
//							Shop shop = entry.getValue();
//							if(vehicleWay.getVehicle().getFreePall()>= shop.getNeedPall()) {
//								VehicleWay testWay = new VehicleWay(vehicleWay.getId().split("\\[")[0]+"[догруз]"); // создаём новый экземпляр маршрута догруза, чтобы не испортить старые маршруты
//								List <Shop> points = new ArrayList<Shop>(vehicleWay.getWay());
//								Vehicle truck = vehicleWay.getVehicle().getVirtualVehicle();
//								truck.setTargetPall(truck.getTargetPall()+shop.getNeedPall());
//								Shop lastPoint = points.remove(points.size()-1);
//								points.add(shop);
//								points.add(lastPoint);
//								testWay.setWay(points);
//								truck.setId(truck.getId()*-1);
//								testWay.setVehicle(truck);						
//								Double logicResult = logicAnalyzer.logicalСheck(testWay, koeff);
//								testWay.setOverrun(logicResult);
//								List <Shop> problemShop = new ArrayList<Shop>();
//								problemShop.add(shop);
//								testWay.setProblemShops(problemShop);
//								System.err.println(logicResult + " логичность маршрута составила после догруза составила");						
//								costOfOwerWay.put(logicResult, testWay);
//							}
//						}
//						
//					}
//							
//					
//					//блок принятие решения по догрузу
//					System.out.println("ПРИНИМАЕМ РЕШЕНИЕ ИЗ - маршрутов: " + costOfOwerWay.size());
//					if(costOfOwerWay.isEmpty()) {
//						stop++;
//						continue;
//					}
//					VehicleWay newWay = (VehicleWay) costOfOwerWay.values().toArray()[costOfOwerWay.size()-1];
////					System.out.println("Вытащил этот маршрут: "+newWay);
//					String idWayForReplace = newWay.getId().split("\\[")[0];
//					for (Iterator<VehicleWay> iterator = whiteWay.iterator(); iterator.hasNext();) {
//						VehicleWay value = iterator.next();
//						if(value.getId().equals(idWayForReplace)) {
//							iterator.remove();
//						}
//						if(value.getId().equals(newWay.getId())) {
//							iterator.remove();
//						}
//					}
//					whiteWay.add(newWay);
//					shopForDeliteHasOutsiderShops.addAll(newWay.getProblemShops());	
//					
//					//после каждого цикла очищаем магазы
//					if(!shopForDeliteHasOutsiderShops.isEmpty()) {
//						for (Shop shop : shopForDeliteHasOutsiderShops) {
//							outsiderShops.remove(shop);
//							shopsForOptimization.remove(shop);
//							costOfOwerWay.clear();
//						}
////						shopsForDelite.clear();
////						shopsForOptimization.sort(shopComparator); // сортируем обновлённый список
//					}
//					
//					//обновляем outsiderWay
//					outsiderWay.clear();
//					whiteWay.forEach(w->{
//						if(!w.getVehicle().isFull()) {
//							outsiderWay.add(w);
//						}
//					});
//					stop++;
//				}
		
		
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
	
	/**
	 * Метод принимает список нераспределенных магазинов и таргетный магазин.
	 * <br>Строит мапу, от меньшего расстояния к большему от таргетного магазина
	 * @param shopsForOptimization лист <b>нераспределённых магазинов<b>
	 * @param targetShop
	 * @return
	 */
	private Map <Double, Shop> getDistanceMatrixHasMin(List<Shop> shopsForOptimization, Shop targetShop) {
		Map <Double, Shop> radiusMap = new TreeMap<Double, Shop>();	
		Integer numShopTarget = targetShop.getNumshop();
		for (Shop shopHasRadius : shopsForOptimization) {
			Integer numShopTargetForTest = shopHasRadius.getNumshop();
			if(numShopTargetForTest == numShopTarget || numShopTargetForTest.equals(numShopTarget)) {
				continue;
			}						
			String keyForMatrixShop = numShopTarget+"-"+numShopTargetForTest; //от таргетного магаза к потенциальному
			Double kmShopTest = matrixMachine.matrix.get(keyForMatrixShop);
			radiusMap.put(kmShopTest, shopHasRadius);
		}
		return radiusMap;		
	}

}
