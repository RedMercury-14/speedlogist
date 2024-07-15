package by.base.main.util.hcolossus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.log.SysoCounter;
import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

import by.base.main.model.Shop;
import by.base.main.service.ShopService;
import by.base.main.util.hcolossus.pojo.Solution;
import by.base.main.util.hcolossus.pojo.Vehicle;
import by.base.main.util.hcolossus.pojo.VehicleWay;
import by.base.main.util.hcolossus.service.ComparatorShops;
import by.base.main.util.hcolossus.service.ComparatorShopsDistanceMain;
import by.base.main.util.hcolossus.service.ComparatorShopsWhithRestrict;
import by.base.main.util.hcolossus.service.LogicAnalyzer;
import by.base.main.util.hcolossus.service.MatrixMachine;
import by.base.main.util.hcolossus.service.ShopMachine;
import by.base.main.util.hcolossus.service.VehicleMachine;

/**
 * Самый основной оптимизатор! БАЗА! Изменение запрещено!
 */
@Component
public class ColossusProcessorANDRestrictions3 {

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

	private Comparator<Shop> shopComparatorPallOnly = (o1, o2) -> (o2.getNeedPall() - o1.getNeedPall()); // сортирует от большей потребности к меньшей

	private Comparator<Vehicle> vehicleComparatorFromMax = (o1, o2) -> (o2.getPall() - o1.getPall()); // сортирует от большей потребности к меньшей без учёта веса
	
	private Comparator<Vehicle> vehicleComparatorFromMin = (o1, o2) -> (o1.getPall() - o2.getPall()); // сортирует от меньшей потребности к большей без учёта веса
	
	private Comparator<Vehicle> vehicleComparatorFromMinAndMass = (o1, o2) -> (o1.getWeigth() / o1.getPall() - o2.getWeigth() / o2.getPall());// сортирует от меньшей потребности к большей C УЧЁТОМ веса
	
	private Comparator<Vehicle> vehicleComparatorFromMaxAndMass = (o1, o2) -> (o2.getWeigth() / o2.getPall() - o1.getWeigth() / o1.getPall());// сортирует от большей потребности к меньшей C УЧЁТОМ веса
	
	private ComparatorShops shopComparatorForIdealWay = new ComparatorShops(); // сортирует от большей потребности к
																				// меньшей и от большего расстояния от
																				// склада к меньшему
	private ComparatorShopsDistanceMain shopComparatorDistanceMain = new ComparatorShopsDistanceMain(); // сортирует от
																										// большего
																										// расстояния от
																										// склада к
																										// меньшему и от
																										// большей
																										// потребности к
																										// меньшей
	private ComparatorShops shopComparator = new ComparatorShops(); // сортирует от большей потребности к меньшей и от
																	// большего расстояния от склада к меньшему
	
	private ComparatorShopsWhithRestrict comparatorShopsWhithRestrict = new ComparatorShopsWhithRestrict(); // 
	
	private List<Shop> shopsForOptimization;
	private List<Shop> shopsForDelite;
	private List<Vehicle> trucks;
	private List<Vehicle> vehicleForDelete;
	private String stackTrace;
	private List<VehicleWay> whiteWay;
	
	private Double maxDistanceInRoute = 100000.0;
	
	private List<Shop> shopsForAddNewNeedPall;
	/**
	 * Основной метод расчёта первочной оптимизации
	 * 
	 * @param jsonMainObject
	 * @param shopList
	 * @param pallHasShops
	 * @param stock
	 * @param koeff          - коэфициент трубности маршрута, т.к. процент
	 *                       перебробега
	 * @param algoritm       - название внутреннего алгоритма <br>
	 *                       * fullLoad - подбор машины так, чтобы сразу полностью
	 *                       загрузить <br>
	 *                       * noFullLoad - подбор машины так, чтобы не полностью
	 *                       загружать ее
	 * @return
	 * @throws Exception 
	 */
	public Solution run(JSONObject jsonMainObject, List<Integer> shopList, List<Integer> pallHasShops, List<Integer> tonnageHasShops, Integer stock,
			Double koeff, String algoritm) throws Exception {
		if(tonnageHasShops == null) {
			System.err.println("Используется старый метод! Нужно использовать /map/myoptimization3");
			return null;
		}
		stackTrace = "";
		Map<Integer, Shop> allShop = shopService.getShopMap();
		Shop targetStock = allShop.get(stock);
		this.jsonMainObject = jsonMainObject;
		whiteWay = new ArrayList<VehicleWay>();
		vehicleForDelete = new ArrayList<Vehicle>();
		// блок подготовки
		// заполняем static матрицу. Пусть хранится там
		matrixMachine.createMatrixHasList(shopList, stock);
		trucks = null;
		try {
			trucks = vehicleMachine.prepareVehicleListVersion2(jsonMainObject);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * магазины для распределения выстроены в порядке убывания потребностей
		 */
		shopsForOptimization = shopMachine.prepareShopList3Parameters(shopList, pallHasShops,tonnageHasShops, stock); // магазины для распределения выстроены в порядке убывания потребностей TEST

		shopsForOptimization.sort(comparatorShopsWhithRestrict);
//		System.out.println("Начальное распределение магазинов слеюущее: ");
		stackTrace = "Начальное распределение магазинов слеюущее: \n";
		for (Shop s : shopsForOptimization) {
//			System.out.println("->" + s.getNumshop() + " - " + s.getDistanceFromStock() + " km - " + s.getNeedPall() + " pall");
			stackTrace = stackTrace + "магазин : " + s.getNumshop() + " - " + s.getDistanceFromStock() + " км - "
					+ s.getNeedPall() + " паллет\n";
		}
		System.out.println("===========");
		stackTrace = stackTrace + "===========\n";

//		Map<String, Double> forJa = matrixMachine.createMatrixHasListTEXT(shopList, stock);
//		stackTrace = stackTrace + "=======================MATRIX============================\n";
//		for (Map.Entry<String, Double> entry : forJa.entrySet()) {
//			stackTrace = stackTrace + entry.getKey() + " --- " + entry.getValue() + "\n";
//		}
//		stackTrace = stackTrace + "==================MATRIX==================\n";
		// конец блока подготовки

		// тут пойдёт логика самого маршрутизатора
		int i = 0; // для тестов, чтобы ограничивать цикл while
//		int iMax = shopsForOptimization.size() * shopsForOptimization.size() * 3;
		int iMax = shopsForOptimization.size() * shopsForOptimization.size() * 10;
		int j = 0; // итерация
		
		trucks.sort(vehicleComparatorFromMax); // сортируем от больших паллет к маньшим
		
		while (!trucks.isEmpty()) {
			if (i == iMax) {
				stackTrace = stackTrace + "Задействован лимит итераций \n";
				break;
			}
			if (shopsForOptimization.isEmpty()) {
				System.err.println("Магазины закончились!");
				i++;
				break;
			}

			shopsForDelite = new ArrayList<Shop>();
			shopsForAddNewNeedPall = new ArrayList<Shop>();

			
			shopsForOptimization.sort(shopComparatorDistanceMain);
			
			Integer pallRestriction = null;
			
			//берем самый дальний магазин
			Shop firstShop = shopsForOptimization.remove(0);
			pallRestriction = firstShop.getMaxPall() != null ? firstShop.getMaxPall() : null;
			boolean isRestrictionsFirst = false;
			
			//Проверяем, загрузится ли этот магазин в самую большую машину (проверка на идеальные маршруты)
			
			Integer shopPall = firstShop.getNeedPall();
			Integer maxPallTruck = trucks.get(0).getPall();
			// проверяем, есть ли ограничения и записываем
			Integer pallRestrictionIdeal = firstShop.getMaxPall() != null ? firstShop.getMaxPall() : null;
			if (shopPall >= maxPallTruck && pallRestrictionIdeal == null) {
			// логика создания идеального маршрута если нет ограничений
				createIdealWay(i+"", firstShop, targetStock);
				i++;
				continue;
			}
	
			// логика создания идеального маршрута если ЕСТЬ ограниченя
			if (shopPall >= maxPallTruck && pallRestrictionIdeal != null) {
				createIdealWayAndPallRestriction(i+"", firstShop, targetStock);
				i++;
				continue;
			}
			isRestrictionsFirst = firstShop.getMaxPall() != null ? true : false; // тут определяем есть ли ограничения в текущем задании
			// это не идеальный маршрут, пожтому догружаем по обычному алгоритму
			// создаём порядок точек
			List<Shop> points = new ArrayList<Shop>();
			points.add(targetStock);
			points.add(firstShop);
			
			// создаём матрицу расстояний от первого магазина
			firstShop.setDistanceFromStock(matrixMachine.matrix.get(targetStock.getNumshop()+"-"+firstShop.getNumshop()));
			Map<Double, Shop> radiusMap = new TreeMap<Double, Shop>();
			radiusMap = getDistanceMatrixHasMin(shopsForOptimization, firstShop);
			// создаём виртуальную машину
			Vehicle virtualTruck = new Vehicle();
			virtualTruck = trucks.get(0);
			int countRadiusMap = 0;
			int maxCountRadiusMap = radiusMap.entrySet().size()-1;
			boolean isRestrictions = false;
			for (Map.Entry<Double, Shop> entry : radiusMap.entrySet()) {
				Shop shop2 = entry.getValue();	
				isRestrictions = shop2.getMaxPall() != null ? true : false; // тут определяем есть ли ограничения в текущем задании
				// тут добавляем мазаз в точку point
				points.add(shop2);
				points.add(targetStock);
				
				shop2.setDistanceFromStock(matrixMachine.matrix.get(targetStock.getNumshop()+"-"+shop2.getNumshop()));
				
							
				
				
				// проверяем является ли маршрут логичным!
				VehicleWay vehicleWayTest = new VehicleWay(points, 0.0, 30, null);

				Double logicResult = logicAnalyzer.logicalСheck(vehicleWayTest, koeff);
//				System.err.println(logicResult + " логичность маршрута составила");
				
				/**
				 * Тут решаем, в зависимости от логичтности - кладём магазин в точки, или нет.
				 * Если нет, то идём дальше
				 */
				double distanceBetween = matrixMachine.matrix.get(points.get(points.size() - 3).getNumshop()+"-"+shop2.getNumshop());
				if (logicResult > 0 && distanceBetween <= maxDistanceInRoute) {
					shopsForOptimization.remove(shop2);
					points.remove(points.size() - 1);
					if(isRestrictions || isRestrictionsFirst) break;
				} else {
//					System.out.println("не кладём, т.к. не логично " + shop2);
					points.remove(points.size() - 1);
					points.remove(points.size() - 1);
					countRadiusMap++;
					continue;
				}	
				
				/**
				 * Далее идут проверки на вместимость авто
				 * именно тут мы проверяем на вместимость машины
				 */
				
				int totalPall = calcPallHashHsop(points, targetStock);
				int totalWeigth = calcWeightHashHsop(points, targetStock);
				
				/**
				 * В этом условии проверяем, если текущие точки под завязку грузят машину. По весу или по паллетам
				 * И магазины без ограничений
				 * 
				 */
				if(totalPall == virtualTruck.getPall().intValue() && totalWeigth <= virtualTruck.getWeigth().intValue() && !isRestrictions || totalWeigth == virtualTruck.getWeigth().intValue() && totalPall <= virtualTruck.getPall().intValue() && !isRestrictions) {
//					trucks.remove(virtualTruck);
//					virtualTruck.setTargetWeigth(totalWeigth);
//					virtualTruck.setTargetPall(totalPall);	
					break;
				}
				
				
				/**
				 * В этом условии проверяем, если текущие точки не проходят в самую большую машину по весу и паллетам!
				 * В этом случае принимаем решение положить этот магазин обратно в общий список
				 * ВАЖНО! УСЛОВИЕ РАБОТАЕТ ЕСЛ/И НЕТ МАГАЗИНОВ С ОГРАНИЧЕНИЯМИ
				 */
				if(totalPall > virtualTruck.getPall().intValue() && !isRestrictions || totalWeigth > virtualTruck.getWeigth().intValue() && !isRestrictions) {
					shopsForOptimization.add(shop2);
					points.remove(points.size() - 1);	
				}
				
				
			}
			
			
			
			/**
			 * Большой блок подбора и обработки магазинов если есть ограничения 
			 */		
			for (Shop shop : points) {
				isRestrictions = shop.getMaxPall() != null ? true : false;
			}			
			if(isRestrictions || isRestrictionsFirst) {
				boolean flag = false;
				do {
					Shop specialShop = null;
					/**
					 * Данный блок отвечает за первичный и вотричный поиск магазина с ограничениями
					 * если flag true это значит что в списке присутствует 2 магазина с ограничениями, притом один из них меньше чем другой.
					 * блок с true как раз отвечает за поиск самого маленького ограничения и магазина.
					 */
					if(flag) {
						Integer extraPall = null;
						//тут определяем минимальное оганичение по паллетам, если таковое имеется
						for (Shop shop : points) {
							if(shop.getMaxPall() != null) {
								if (extraPall != null && extraPall > shop.getMaxPall()) {
									extraPall = shop.getMaxPall();
									specialShop = shop;
								}else {
									extraPall = shop.getMaxPall();
									specialShop = shop;
								}
							}
						}
					}else {
						specialShop = points.stream().filter(s-> s.getMaxPall() != null).findFirst().get();
					}
					
					flag = false;
					
					Integer specialPall = specialShop.getMaxPall();
					List<Vehicle> specialTrucks = new ArrayList<Vehicle>();
					trucks.stream().filter(t-> t.getPall()<=specialPall).forEach(t->specialTrucks.add(t));
					if(specialTrucks.isEmpty()) {
						System.err.println("Отсутствуют машины с паллетовместимостью " + specialPall + " и ниже!");
						break;
					}
					Map<Double, Shop> radiusMapSpecial = new TreeMap<Double, Shop>();
					radiusMapSpecial = getDistanceMatrixHasMin(shopsForOptimization, specialShop);
					virtualTruck = specialTrucks.remove(0);
					/**
					 * Сначала проверяем поместится ли уже текущие точки в данную машину
					 * если входит, то продолжаем искать по СТАРОМУ списку.
					 * Если не водит, избавляемся от лишних точке начиная с последней
					 */
					int totalPall = calcPallHashHsop(points, targetStock);
					int totalWeigth = calcWeightHashHsop(points, targetStock);
					
					if(totalPall > virtualTruck.getPall().intValue() || totalWeigth > virtualTruck.getWeigth().intValue()) {
						List<Shop> pointsNew = new ArrayList<Shop>();
						pointsNew.add(targetStock);
						pointsNew.add(specialShop);
						for (Shop shop : points) {
							if(shop.equals(targetStock) || shop.equals(specialShop)) continue;
							pointsNew.add(shop);
							int totalPallNew = calcPallHashHsop(pointsNew, targetStock);
							int totalWeigthNew = calcWeightHashHsop(pointsNew, targetStock);
							if(totalPallNew <= virtualTruck.getPall().intValue() && totalWeigthNew <= virtualTruck.getWeigth().intValue()) {
								continue;
							}else {
								pointsNew.remove(pointsNew.size()-1);
								continue;
							}
							
						}
						List<Shop> correctRoute = correctRouteMaker(pointsNew, targetStock);

						List<Shop> extraShop = new ArrayList<Shop>(points);
						extraShop.removeAll(correctRoute);
						shopsForOptimization.addAll(extraShop);	
						points = correctRoute;
					}
					/**
					 * продолжаем догружать машину дальше
					 */
					for (Map.Entry<Double, Shop> entry : radiusMapSpecial.entrySet()) {
						Shop shop2 = entry.getValue();	
						Integer specialPallNew = shop2.getMaxPall() != null ? shop2.getMaxPall() : null; // тут определяем есть ли ограничения в текущем задании
						
						// тут добавляем мазаз в точку point
						points.add(shop2);
						points.add(targetStock);
						
						shop2.setDistanceFromStock(matrixMachine.matrix.get(targetStock.getNumshop()+"-"+shop2.getNumshop()));
						
						// проверяем является ли маршрут логичным!
						VehicleWay vehicleWayTest = new VehicleWay(points, 0.0, 30, null);

						Double logicResult = logicAnalyzer.logicalСheck(vehicleWayTest, koeff);
//						System.err.println(logicResult + " логичность маршрута составила");
						
						/**
						 * Тут решаем, в зависимости от логичтности - кладём магазин в точки, или нет.
						 * Если нет, то идём дальше
						 */
						double distanceBetween = matrixMachine.matrix.get(points.get(points.size() - 3).getNumshop()+"-"+shop2.getNumshop());
						if (logicResult > 0 && distanceBetween <= maxDistanceInRoute) {
							shopsForOptimization.remove(shop2);
							points.remove(points.size() - 1);
							if(specialPallNew != null && specialPallNew < specialPall) {
								System.err.println("ИСКЛЮЧЕНИЕ ЕСЛИ СЛЕДУЮЩИЙ МАГАЗИН НАКЛАДЫВАЕТ БОЛЕЕ ЖЕСТКОЕ ОГРАНИЧЕНИЕ ЧЕМ ПРОШЛЫЙ");
								points.forEach(p-> System.out.println(p.toAllString()));
								flag = true;
								break;
							}
						} else {
//							System.out.println("не кладём, т.к. не логично " + shop2);
							points.remove(points.size() - 1);
							points.remove(points.size() - 1);
							countRadiusMap++;
							continue;
						}	
						
						/**
						 * Далее идут проверки на вместимость авто
						 * именно тут мы проверяем на вместимость машины
						 */
						
						totalPall = calcPallHashHsop(points, targetStock);
						totalWeigth = calcWeightHashHsop(points, targetStock);
						
						/**
						 * В этом условии проверяем, если текущие точки под завязку грузят машину. По весу или по паллетам
						 * И магазины без ограничений
						 * 
						 */
						if(totalPall == virtualTruck.getPall().intValue() && totalWeigth <= virtualTruck.getWeigth().intValue() || totalWeigth == virtualTruck.getWeigth().intValue() && totalPall <= virtualTruck.getPall().intValue()) {
//							trucks.remove(virtualTruck);
//							virtualTruck.setTargetWeigth(totalWeigth);
//							virtualTruck.setTargetPall(totalPall);	
							break;
						}
						
						
						/**
						 * В этом условии проверяем, если текущие точки не проходят в самую большую машину по весу и паллетам!
						 * В этом случае принимаем решение положить этот магазин обратно в общий список
						 * ВАЖНО! УСЛОВИЕ РАБОТАЕТ ЕСЛ/И НЕТ МАГАЗИНОВ С ОГРАНИЧЕНИЯМИ
						 */
						if(totalPall > virtualTruck.getPall().intValue() || totalWeigth > virtualTruck.getWeigth().intValue()) {
							shopsForOptimization.add(shop2);
							points.remove(points.size() - 1);	
						}
						
						
					}
				} while (flag);

			}
			
			
			trucks.remove(virtualTruck);
			virtualTruck.setTargetWeigth(calcWeightHashHsop(points, targetStock));
			virtualTruck.setTargetPall(calcPallHashHsop(points, targetStock));	
						
			
			// создаём финальный, виртуальный маршрут
			points.add(targetStock);

			VehicleWay vehicleWayVirtual = new VehicleWay(i+ "", points, 0.0, 30, virtualTruck);
			vehicleWayVirtual.setDistanceFromStock(firstShop.getDistanceFromStock());	
			
			//методы постобработки маршрутов
						
//			superProcessingWay(vehicleWayVirtual, targetStock); // метод попутноо подбора авто
			
			changeTruckHasSmall(vehicleWayVirtual, targetStock); // метод замены авто на меньшее
			
			optimizePoints(vehicleWayVirtual);//метод оптимизации точек маршрута
;
			whiteWay.add(vehicleWayVirtual);
			i++;			
		}


		stackTrace = stackTrace + "-->Остановлен  на итерации " + i + ". Максимальное значение итераций в данном задании: "+iMax+".<-- \n";
		
		
		stackTrace = stackTrace + "Результат после третьего этапа: \n";
		for (VehicleWay v : whiteWay) {
			stackTrace = stackTrace + v + "\n";
		}
		stackTrace = stackTrace + "========= Свободные авто ==========\n";
		for (Vehicle v : trucks) {
			stackTrace = stackTrace + v + "\n";
		}
		stackTrace = stackTrace + "+++++++++ Оставшиеся магазины +++++++++++\n";
		for (Shop v : shopsForOptimization) {
			stackTrace = stackTrace + v.getNumshop() + " (" + v.getNeedPall() + ") " + "\n";
		}

		whiteWay.forEach(w -> System.out.println(w));
		System.out.println("========= Свободные авто ==========");
		trucks.forEach(t -> System.out.println(t));
		System.out.println("+++++++++ Оставшиеся магазины +++++++++++");
		shopsForOptimization.forEach(s -> System.out.println(s.toAllString()));

		Solution solution = new Solution();

		solution.setEmptyShop(shopsForOptimization);
		solution.setEmptyTrucks(trucks);
		solution.setWhiteWay(whiteWay);
		// определяем и записываем суммарный пробег маршрута
		Double totalRunHasMatrix = 0.0;
		for (VehicleWay way : solution.getWhiteWay()) {
			// заменяем просчёт расстояний из GH на матричный метод
			for (int l = 0; l < way.getWay().size() - 1; l++) {
				String key = way.getWay().get(l).getNumshop() + "-" + way.getWay().get(l + 1).getNumshop();
				totalRunHasMatrix = totalRunHasMatrix + matrixMachine.matrix.get(key);
			}
		}
		solution.setTotalRunKM(totalRunHasMatrix);
		stackTrace = stackTrace + "Суммарный пробег маршрута: " + totalRunHasMatrix + " м.\n";
		solution.setStackTrace(stackTrace);
		return solution;

	}
	
	/**
	 * Оптимизирует точки по простому алгоритму: от крайней точке к самой ближайшей и так далее.
	 * <br> Так строятся красивые логичные маршруты (не всегда короткие)
	 * @param vehicleWayVirtual
	 */
	private void optimizePoints(VehicleWay vehicleWayVirtual) {
		Shop targetStock = vehicleWayVirtual.getWay().remove(0);
		vehicleWayVirtual.getWay().remove(vehicleWayVirtual.getWay().size()-1);
		List<Shop> points = vehicleWayVirtual.getWay();
		List<Shop> pointsNew = new ArrayList<Shop>();
		//определяем самый дальний магазин от склада		
		Shop furtherShop = getDistanceMatrixHasMin(vehicleWayVirtual.getWay(), targetStock).entrySet().stream().reduce((a, b) -> b).orElse(null).getValue(); // получаем последний элемент
		pointsNew.add(targetStock);
		pointsNew.add(furtherShop);
		points.remove(furtherShop);
		
		for (Shop shop : points) {
			Shop backShop = null;
			if(pointsNew.size() == 2) {
				backShop = furtherShop;
			}else {
				backShop = pointsNew.get(pointsNew.size()-1);
			}
			Map<Double, Shop> distanceMap = getDistanceMatrixHasMin(vehicleWayVirtual.getWay(), backShop);
			for (Map.Entry<Double, Shop> entry : distanceMap.entrySet()) {
				Shop targetShop = entry.getValue();
				if(!pointsNew.contains(targetShop)) {
					pointsNew.add(targetShop);
					break;
				}else {
					continue;
				}
			}
			
		}
		
		pointsNew.add(targetStock);
		vehicleWayVirtual.setWay(pointsNew);		
	}
	
	/**
	 * Метод кластерного догруза авто.
	 * <br> Берется точка, от неё прокладывается радиус 10 км,
	 * <br> все магазины в этом радиусе кладутся в эту точку. и так далее по каждой точке исходного маршрута.
	 * <br> Важно что в качестве точек, по которым идём ьерется начальный маршрут.
	 * @param vehicleWayVirtual
	 * @param targetStock
	 */
	private void superProcessingWay(VehicleWay vehicleWayVirtual, Shop targetStock) {
		Vehicle truck = vehicleWayVirtual.getVehicle();
		List<Shop> mainPoints = new ArrayList<Shop>(vehicleWayVirtual.getWay());
		if(mainPoints.get(0).equals(targetStock)) mainPoints.remove(0);
		if(mainPoints.get(mainPoints.size()-1).equals(targetStock)) mainPoints.remove(mainPoints.size()-1);
		List<Shop> newPoints = new ArrayList<Shop>();
		newPoints.add(targetStock);
		for (Shop shop : mainPoints) {
			int allPall = calcPallHashHsop(newPoints, targetStock) + shop.getNeedPall();
			int allWeigth = calcWeightHashHsop(newPoints, targetStock) + shop.getWeight();
			if(truck.getWeigth() >= allWeigth && truck.getPall() >= allPall && !newPoints.contains(shop)) {
				newPoints.add(shop);				
			}
			Map<Double, Shop> radiusFromShop = getDistanceMatrixHasMinLimitParameter(shopsForOptimization, null, shop, 10000.0);
			for (Entry<Double, Shop> entry : radiusFromShop.entrySet()) {
				Shop nextShop = entry.getValue();
				
				if(nextShop.getMaxPall()!=null && nextShop.getMaxPall() < truck.getPall()) {
					continue;
				}
				allPall = calcPallHashHsop(newPoints, targetStock) + nextShop.getNeedPall();
				allWeigth = calcWeightHashHsop(newPoints, targetStock) + nextShop.getWeight();
				if(truck.getWeigth() >= allWeigth && truck.getPall() >= allPall && !newPoints.contains(nextShop)) {
					newPoints.add(nextShop);
					shopsForOptimization.remove(nextShop);
				}
			}
//			System.err.println(radiusFromShop.size() + "  " + allPall + "  " + allWeigth);
//			radiusFromShop.entrySet().forEach(e -> System.out.println("++++++>> " + e.getKey() + " - " + e.getValue().toString()));			
		}
		mainPoints.removeAll(newPoints);
		shopsForOptimization.addAll(mainPoints);
		
//		newPoints.forEach(s->System.out.println("-------->>>> " + s));
		truck.setTargetWeigth(calcWeightHashHsop(newPoints, targetStock));
		truck.setTargetPall(calcPallHashHsop(newPoints, targetStock));	
					
		
		newPoints.add(targetStock);
		vehicleWayVirtual.setVehicle(truck);
		vehicleWayVirtual.setWay(newPoints);
		
	}
	
	
	/**
	 * Метод преобразует любой маршрут в правильный, т.е. точки выстраиваются по отдалению от склада, то меньшего к большему расстоянию
	 * <br>Сама сортировка прохордит с помощью помещения расстояний в TreeMap
	 * <br>не добавляет склад в конце
	 * @param points
	 * @return
	 */
	public List<Shop> correctRouteMaker(List<Shop> points, Shop targetStock){
		List<Shop> mainList = new ArrayList<Shop>(points);
//		System.out.println("получаем на вход:");
//		mainList.forEach(s-> System.out.println(s));

		mainList.remove(targetStock);
		if(mainList.get(mainList.size()-1).equals(targetStock)) {
			mainList.remove(mainList.size()-1);
		}
		
		
//		System.out.println("после удаления слкада:");
//		mainList.forEach(s-> System.out.println(s));
		
		Map<Double, Shop> map = new TreeMap<Double, Shop>(Collections.reverseOrder()); // с обратной сортировкой
		
		for (Shop shop : mainList) {
			String keyForMatrix = targetStock.getNumshop()+"-"+shop.getNumshop();
			Double km = matrixMachine.matrix.get(keyForMatrix);
			if(km == null) {
				System.err.println("LogicAnalyzer.correctRouteMaker: Расстояние " + keyForMatrix + " не найдено в матрице!");
				//генерим exception
			}else {
				map.put(km, shop);
			}			
		}
		
//		System.out.println("После обработки");
//		map.forEach((k,v) -> System.out.println(k + "  " + v));
		
		List<Shop> result = new ArrayList<Shop>();
		result.add(targetStock);
		map.forEach((k,v) -> result.add(v));
		
//		System.out.println("реузльтат:");
//		result.forEach(s-> System.out.println(s));
		
		return result;		
	}
	
	/**
	 * Блок, отвечающий за замену машины, когда маршрут окончательно построен. Должен быть всегда самым последним!
	 * @param vehicleWayVirtual
	 * @throws Exception 
	 */
	private void changeTruckHasSmall(VehicleWay vehicleWayVirtual, Shop targetStock) throws Exception {		
		System.out.println("-->>> changeTruckHasSmall START : " + vehicleWayVirtual);
		if(vehicleWayVirtual.getVehicle().getTargetWeigth() == null) {
			throw new Exception();
		}
		if (!vehicleWayVirtual.getVehicle().isFull()) {
			// проверяем, есть ли, гипотетически меньшая машина, для того чтобы сохранить
			// большую
			Integer pallHasWay = calcPallHashHsop(vehicleWayVirtual.getWay(), targetStock);
			Integer extraPall = 999;
			//тут определяем минимальное оганичение по паллетам, если таковое имеется
			for (Shop shop : vehicleWayVirtual.getWay()) {
				if(shop.getMaxPall() != null) {
					if (extraPall > shop.getMaxPall()) {
						extraPall = shop.getMaxPall();
					}
				}
			}
			
			Vehicle oldTruck = vehicleWayVirtual.getVehicle();
			Integer pallHasOldTruck = vehicleWayVirtual.getVehicle().getPall();
			trucks.sort(vehicleComparatorFromMin);
			for (Vehicle truck : trucks) {
//				System.err.println(truck + " <----> " + oldTruck);
				if(truck.getWeigth() >= oldTruck.getTargetWeigth()) {
					if (truck.getPall() >= oldTruck.getTargetPall() && truck.getPall() < pallHasOldTruck && extraPall > truck.getPall()) {
						truck.setTargetPall(pallHasWay);
						truck.setTargetWeigth(oldTruck.getTargetWeigth());
						trucks.add(oldTruck);
						trucks.remove(truck);
						vehicleForDelete.add(truck);
						vehicleForDelete.remove(oldTruck);
						vehicleWayVirtual.setVehicle(truck);
						stackTrace = stackTrace + "Меняем тачку с " + oldTruck.getPall() + " на "
								+ truck.getPall() + "\n";
						break;
					}
				}
				
			}
			System.out.println("-->>> changeTruckHasSmall FINISH : " + vehicleWayVirtual);
			trucks.sort(vehicleComparatorFromMax);
		}
	}
	
	/**
	 * Блок догруза машины по последней точке
	 * если тачка недогружена, то возможны 2 варианта:
	 * берем последнюю точку, прокладываем радиус 10 км, убераем её и ищем магаз.
	 * (магазы) которые могут догрузить машину
	 * второй варик тот же самый, но без убирание точки
	 * @param vehicleWayVirtual
	 * @param pallRestriction
	 * @param targetStock
	 */
	private VehicleWay optimizationOfVirtualRoutesType1(VehicleWay vehicleWayVirtual, Integer pallRestriction, Shop targetStock) {
		if (!vehicleWayVirtual.getVehicle().isFull() || vehicleWayVirtual.getWay().size() > 3) {
			// если тачка недогружена, то возможны 2 варианта:
			// берем последнюю точку, прокладываем радиус 10 км, убераем её и ищем магаз
			// (магазы) которые могут догрузить машину
			// второй варик тот же самый, но без убирание точки
			List<Shop> newPointsStart = logicAnalyzer.correctRouteMaker(vehicleWayVirtual.getWay());
			Shop lastShopStart = newPointsStart.get(1);
//			List<Shop> processWay = new ArrayList<Shop>(newPoints);
//			int size = processWay.size();

			Map<Double, Shop> radiusFromLastShop = getDistanceMatrixHasMinLimitParameter(shopsForOptimization, shopsForDelite, lastShopStart, 10000.0);
			for (Map.Entry<Double, Shop> e : radiusFromLastShop.entrySet()) {
				List<Shop> newPoints = logicAnalyzer.correctRouteMaker(vehicleWayVirtual.getWay());
				Shop lastShop = newPoints.get(1);
				List<Shop> processWay = new ArrayList<Shop>(newPoints);
				if(e.getValue().getMaxPall() != null && e.getValue().getMaxPall() > pallRestriction) {
					//это условие говорит, что если ограничения второго магазина больше чем первого - магаз не рассматриваем
					System.out.println("POINT 2");
					continue;
				}
				if (e.getValue().getNeedPall() <= vehicleWayVirtual.getFreePallInVehicle()) {
					Integer maxLim = getMaxLimitPallHasVehicleWay(vehicleWayVirtual, targetStock); 
					if(maxLim != null && maxLim <= vehicleWayVirtual.getVehicle().getPall()) { //здесь говорим что если машина больше чем ограничения, то не догружаем этот магазин
						continue;
					}
					System.out.println("<><><><><> 11");
					if (e.getValue().getNumshop() != lastShop.getNumshop() && !shopsForDelite.contains(e.getValue())) {
						System.out.println("<><><><><> 12");
						if(e.getValue().getWeight() < vehicleWayVirtual.getFreeWeigthInVehicle()) {
							System.out.println("<><><><><> 13");
							processWay.forEach(s-> System.out.println(s.toString())); //aaaaaa
							processWay.remove(0);
							List<Shop> processWayNew = new ArrayList<Shop>();
							processWayNew.add(targetStock);
							processWayNew.add(e.getValue());
							processWayNew.addAll(processWay);							
							Vehicle truck = vehicleWayVirtual.getVehicle();
							truck.setTargetPall(calcPallHashHsop(processWayNew, targetStock));
							truck.setTargetWeigth(truck.getTargetWeigth() + e.getValue().getWeight());
							vehicleWayVirtual.setVehicle(truck);
							vehicleWayVirtual.setWay(processWayNew);			
							shopsForDelite.add(e.getValue());
							shopsForOptimization.remove(e.getValue());
//							size = processWayNew.size();
							System.err.println(	"~~~~~~~~~~~~~~~~~ БЛОК догруза машины по последней точке СРАБОТАЛ ~~~~~~~~~~~~~~~~~~~");
//							break;
						}
						
					}
				}
//				if (e.getValue().getNeedPall() <= vehicleWayVirtual.getFreePallInVehicle()+lastShop.getNeedPall() || !vehicleWayVirtual.getVehicle().isFull()) {
//					System.out.println("<><><><><> 21");
//					if (e.getValue().getNumshop() != lastShop.getNumshop() && !shopsForDelite.contains(e.getValue())) {
//						System.out.println("<><><><><> 22");
//						if(e.getValue().getWeight() < vehicleWayVirtual.getFreeWeigthInVehicle()+lastShop.getWeight()) {
//							System.out.println("<><><><><> 23");
//							System.out.println(e.getValue().toAllString());
//							processWay.forEach(s-> System.err.println(s.toAllString()));
//							processWay.remove(0);
//							Shop oldShopForReplace = processWay.remove(0);
//							List<Shop> processWayNew = new ArrayList<Shop>();
//							processWayNew.add(targetStock);
//							processWayNew.add(e.getValue());
//							processWayNew.addAll(processWay);
//							Vehicle truck = vehicleWayVirtual.getVehicle();							
//							truck.setTargetPall(calcPallHashHsop(processWayNew, targetStock));
//							truck.setTargetWeigth(truck.getTargetWeigth() + e.getValue().getWeight());
//							vehicleWayVirtual.setVehicle(truck);
//							vehicleWayVirtual.setWay(processWayNew);			
//							shopsForDelite.add(e.getValue());
//							shopsForOptimization.remove(e.getValue());
//							shopsForOptimization.add(oldShopForReplace);
//							size = processWayNew.size();
//							System.err.println(	"~~~~~~~~~~~~~~~~~ БЛОК догруза машины с заменой последней СРАБОТАЛ ~~~~~~~~~~~~~~~~~~~");
////							break;
//						}
//						
//					}
//				}
			}

//			if(!vehicleWayVirtual.getVehicle().isFull()) {
//				for (Map.Entry<Double, Shop> e : radiusFromLastShop.entrySet()) {
//					if(e.getValue().getNeedPall()-lastShop.getNeedPall() == vehicleWayVirtual.getFreePallInVehicle()) {
//						if(e.getValue().getNumshop() != lastShop.getNumshop() && !shopsForDelite.contains(e.getValue())) {
//							processWay.remove(size-1);
//							processWay.remove(lastShop);
//							
//							shopsForDelite.remove(lastShop);
//							if(!shopsForOptimization.contains(lastShop)) {
//								shopsForOptimization.add(lastShop);
//							}
//							
//							processWay.add(e.getValue());
//							processWay.add(targetStock);
//							Vehicle truck = vehicleWayVirtual.getVehicle();
//							truck.setTargetPall(calcPallHashHsop(processWay, targetStock));
//							vehicleWayVirtual.setVehicle(truck);
//							vehicleWayVirtual.setWay(processWay);
//							shopsForDelite.add(e.getValue());
//							System.err.println("~~~~~~~~~~~~~~~~~СРАБОТАЛ МАКСИМАЛЬНЫЙ ДОГРУЗ С УДАЛЕНИЕМ ПОСЛЕДНЕГО МАГАЗИНА~~~~~~~~~~~~~~~~~~~");
//							break;
//						}							
//					}
//				}
//			}

		}
		return vehicleWayVirtual;
	}

	/**
	 * Метод принимает список нераспределенных магазинов и таргетный магазин. <br>
	 * Строит мапу, от меньшего расстояния к большему от таргетного магазина
	 * 
	 * @param shopsForOptimization лист <b>нераспределённых магазинов<b>
	 * @param targetShop
	 * @return
	 */
	private Map<Double, Shop> getDistanceMatrixHasMin(List<Shop> shopsForOptimization, Shop targetShop) {
		Map<Double, Shop> radiusMap = new TreeMap<Double, Shop>();
		Integer numShopTarget = targetShop.getNumshop();
		for (Shop shopHasRadius : shopsForOptimization) {
			Integer numShopTargetForTest = shopHasRadius.getNumshop();
			if (numShopTargetForTest == numShopTarget || numShopTargetForTest.equals(numShopTarget)) {
				continue;
			}
			String keyForMatrixShop = numShopTarget + "-" + numShopTargetForTest; // от таргетного магаза к
																					// потенциальному
			Double kmShopTest = matrixMachine.matrix.get(keyForMatrixShop);
			radiusMap.put(kmShopTest, shopHasRadius);
		}
		return radiusMap;
	}

	/**
	 * Mетод принимает список нераспределенных магазинов и таргетный магазин. <br>
	 * Строит мапу, от меньшей потребности к большей от таргетного магазина в рамках
	 * заданного парамерта
	 * 
	 * @param shopsForOptimization лист <b>нераспределённых магазинов<b>
	 * @param targetShop
	 * @param limit                <b>ограничение по поиску<b>
	 * @return
	 */
	private Map<Double, Shop> getDistanceMatrixHasMinLimitParameter(List<Shop> shopsForOptimization,
			List<Shop> shopsForDelete, Shop targetShop, Double limit) {
		Map<Double, Shop> radiusMap = new TreeMap<Double, Shop>();
		Integer numShopTarget = targetShop.getNumshop();
		for (Shop shopHasRadius : shopsForOptimization) {
			Integer numShopTargetForTest = shopHasRadius.getNumshop();
			if (numShopTargetForTest == numShopTarget || numShopTargetForTest.equals(numShopTarget)) {
				continue;
			}
			String keyForMatrixShop = numShopTarget + "-" + numShopTargetForTest; // от таргетного магаза к
																					// потенциальному
			Double kmShopTest = matrixMachine.matrix.get(keyForMatrixShop);
			if (shopsForDelete != null && shopsForDelete.contains(shopHasRadius)) {
				continue;
			}
			if (kmShopTest > limit) {
				continue;
			} else {
				radiusMap.put(kmShopTest, shopHasRadius);
			}
		}
		return radiusMap;
	}
	
	/**
	 * Метод создаёт идеальныу маршруты, когда нет ограничений. Создаёт в зависимости от паллет и веса.
	 * ПОСЛЕ ЭТОГО МЕТОДА ВСЕГДА ДОЛЖНА БЫТЬ КОМАНДА break;
	 * @param shop
	 * @param targetStock
	 * @throws Exception 
	 */
	private void createIdealWay(String id, Shop shop, Shop targetStock) throws Exception {
		Integer shopPall = shop.getNeedPall();
		Integer shopWeight = shop.getWeight();
		Integer maxPallTruck = trucks.get(0).getPall();
		Integer maxWeighTruck = trucks.get(0).getWeigth();
		Double distanceFromStock = shop.getDistanceFromStock();
		// логика создания идеального маршрута если нет ограничений
		//&& shopWeight < maxWeighTruck
		if (shopPall == maxPallTruck && maxWeighTruck >= shopWeight) {
			System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин по паллетам, при том что паллеты проходят ровно!");
			Vehicle targetTruck = trucks.remove(0);
			targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
			targetTruck.setTargetWeigth(shop.getWeight()); //загружаем машину по весу
			List<Shop> points = new ArrayList<Shop>();
			points.add(targetStock);
			shop.setNeedPall(targetTruck.getPall()); // указываем текущую потребность магазина для этой фуры
			points.add(shop);
			points.add(targetStock);
			VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
			changeTruckHasSmall(vehicleWay, targetStock);
			whiteWay.add(vehicleWay);
			shopsForDelite.add(shop);
		}else if(shopPall == maxPallTruck && maxWeighTruck < shopWeight) {
			System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин по весу, при том что паллеты проходят ровно!");
			//делим магаз по весу
			Vehicle targetTruck = trucks.remove(0);
			double oneWidthPall = (double) (shop.getWeight()/shop.getNeedPall());
			double widthNewShop = oneWidthPall*targetTruck.getPall();						
			int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
			targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
			targetTruck.setTargetWeigth((int)widthNewShop);
			Integer newNeedPallForShop = shopPall - newNeedPall;
			Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
			targetTruck.setTargetWeigth(finalWidthFOrTruck);
			Integer newNeedWeigthForShop = shopWeight - finalWidthFOrTruck;
			Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(),
					shop.getLng(), shop.getLength(), shop.getWidth(), shop.getHeight(), shop.getMaxPall());
			newShopHasPall.setWeight(newNeedWeigthForShop);
			newShopHasPall.setNeedPall(newNeedPallForShop);
			newShopHasPall.setDistanceFromStock(distanceFromStock);
			List<Shop> points = new ArrayList<Shop>();
			shopsForDelite.add(shop);
			shopsForAddNewNeedPall.add(newShopHasPall);
			shop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
			points.add(targetStock);
			points.add(shop);
			points.add(targetStock);
			VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
			changeTruckHasSmall(vehicleWay, targetStock);
			whiteWay.add(vehicleWay);
		}
		if (shopPall > maxPallTruck) { // тут, если потребность магаза
																		// превышает загрузку максимально
																		// оставшейся машины - он загружает
																		// машину полностью и создаёт магаз
																		// с остатком от потребности
			Vehicle targetTruck = trucks.remove(0);
			
			//проверяем, поместится ли данный магазин в фуру по весу!
			//делим паллеты на вес, находим приблизительный вес паллеты и ссумируем по паллетам в машине
			double oneWidthPall = (double) (shop.getWeight()/shop.getNeedPall());
			double widthNewShop = oneWidthPall*targetTruck.getPall();
//			System.err.println("Вес одной паллеты: " + oneWidthPall);
//			System.err.println("Потребность нового магазина с учётом максимальной паллетовместимости машины: " + widthNewShop);
//			System.err.println("Грузоподъемность машины: " + targetTruck.getWeigth());						
			if(widthNewShop > targetTruck.getWeigth()) {
				//если по весу не проходит, то алгоритм разбивает магаз не по паллетам, а по весу
				System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин по весу!");
				int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
				targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
				Integer newNeedPallForShop = shopPall - newNeedPall;
				Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
				targetTruck.setTargetWeigth(finalWidthFOrTruck);
				Integer newNeedWeigthForShop = shopWeight - finalWidthFOrTruck;
				Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(),
						shop.getLng(), shop.getLength(), shop.getWidth(), shop.getHeight(), shop.getMaxPall());
				newShopHasPall.setWeight(newNeedWeigthForShop);
				newShopHasPall.setNeedPall(newNeedPallForShop);
				newShopHasPall.setDistanceFromStock(distanceFromStock);				
				List<Shop> points = new ArrayList<Shop>();
				shopsForDelite.add(shop);
				shopsForAddNewNeedPall.add(newShopHasPall);
				shop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
				shop.setWeight(targetTruck.getTargetPall() * oneWidthPall);
				points.add(targetStock);
				points.add(shop);
				points.add(targetStock);
				VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
				changeTruckHasSmall(vehicleWay, targetStock);
				whiteWay.add(vehicleWay);
			}else {
				System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин по паллетам!");
				int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
				if(newNeedPall>= targetTruck.getPall()) {
					targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью		
				}else {
					System.err.println("обработать вариант! По идее такого быть не должно");
					targetTruck.setTargetPall(newNeedPall);
				}								
				Integer newNeedPallForShop = shopPall - targetTruck.getPall();
				Integer finalWidthFOrTruck = (int) ((int) targetTruck.getTargetPall()*oneWidthPall);
				targetTruck.setTargetWeigth(finalWidthFOrTruck);
				Integer newNeedWeigthForShop = shopWeight - finalWidthFOrTruck;
				Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(),
						shop.getLng(), shop.getLength(), shop.getWidth(), shop.getHeight(), shop.getMaxPall());
				newShopHasPall.setWeight(newNeedWeigthForShop);
				newShopHasPall.setNeedPall(newNeedPallForShop);
				newShopHasPall.setDistanceFromStock(distanceFromStock);
				List<Shop> points = new ArrayList<Shop>();				
				shopsForDelite.add(shop);
				shopsForAddNewNeedPall.add(newShopHasPall);
				shop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
				shop.setWeight(targetTruck.getTargetPall() * oneWidthPall);
				points.add(targetStock);
				points.add(shop);
				points.add(targetStock);
				VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
				changeTruckHasSmall(vehicleWay, targetStock);
				whiteWay.add(vehicleWay);
			}
		}
		shopsForOptimization.addAll(shopsForAddNewNeedPall);
	}
	
	/**
	 * Возвращает максимальное ограничение на виртуально маршруте
	 * @param vehicleWay
	 * @param targetStock
	 * @return
	 */
	private Integer getMaxLimitPallHasVehicleWay (VehicleWay vehicleWay, Shop targetStock) {
		Integer maxLim = null;
		for (Shop shop : vehicleWay.getWay()) {
			if(shop.getNumshop() == targetStock.getNumshop()) {
				continue;
			}
			if(shop.getMaxPall() != null) {
				if(maxLim == null) {
					maxLim = shop.getMaxPall();
				}else {
					if(maxLim > shop.getMaxPall()) {
						maxLim = shop.getMaxPall();
					}
				}
			}
		}
		return maxLim;		
	}
	
	private void createIdealWayAndPallRestriction (String id, Shop shop, Shop targetStock) throws Exception {
		Integer shopPall = shop.getNeedPall();
		Integer shopWeight = shop.getWeight();
		Integer maxPallTruck = trucks.get(0).getPall();
		Integer maxWeighTruck = trucks.get(0).getWeigth();
		Double distanceFromStock = shop.getDistanceFromStock();

		// проверяем, есть ли ограничения и записываем
		Integer pallRestrictionIdeal = shop.getMaxPall() != null ? shop.getMaxPall() : null;
		
		if (shopPall <= pallRestrictionIdeal && shopPall == maxPallTruck && maxWeighTruck >= shopWeight) {
			System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин БЕЗ ограничениями по весу, при том что паллеты проходят ровно!");
			Vehicle targetTruck = trucks.remove(0);
			targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
			targetTruck.setTargetWeigth(shopWeight); // загружаем авто по весу
			List<Shop> points = new ArrayList<Shop>();
			points.add(targetStock);
			shop.setNeedPall(targetTruck.getPall()); // указываем текущую потребность магазина для этой фуры
			points.add(shop);
			points.add(targetStock);
			VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
			changeTruckHasSmall(vehicleWay, targetStock);
			whiteWay.add(vehicleWay);
			shopsForDelite.add(shop);
		}else if(shopPall <= pallRestrictionIdeal && shopPall == maxPallTruck && maxWeighTruck < shopWeight) {
			System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин с ограничениями по весу, при том что паллеты проходят ровно!");
			//делим магаз по весу
			Vehicle targetTruck = trucks.remove(0);
			double oneWidthPall = (double) (shop.getWeight()/shop.getNeedPall());
			double widthNewShop = oneWidthPall*targetTruck.getPall();						
			int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
			targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
			targetTruck.setTargetWeigth((int)widthNewShop);
			Integer newNeedPallForShop = shopPall - newNeedPall;
			Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
			targetTruck.setTargetWeigth(finalWidthFOrTruck);
			Integer newNeedWeigthForShop = shopWeight - finalWidthFOrTruck;
			Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(),
					shop.getLng(), shop.getLength(), shop.getWidth(), shop.getHeight(), shop.getMaxPall());
			newShopHasPall.setWeight(newNeedWeigthForShop);
			newShopHasPall.setNeedPall(newNeedPallForShop);
			newShopHasPall.setDistanceFromStock(distanceFromStock);
			List<Shop> points = new ArrayList<Shop>();
			shopsForDelite.add(shop);
			shopsForAddNewNeedPall.add(newShopHasPall);
			shop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
			points.add(targetStock);
			points.add(shop);
			points.add(targetStock);
			VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
			changeTruckHasSmall(vehicleWay, targetStock);
			whiteWay.add(vehicleWay);
		}
		if (shopPall >= pallRestrictionIdeal) {
			Vehicle targetTruck = null;
			for (Vehicle truck : trucks) {
				if (truck.getPall() <= pallRestrictionIdeal) {
					targetTruck = truck;
					break;
				}
			}
			if(targetTruck == null) {
				stackTrace = stackTrace + "Ограничения на магазин следующие: не более " + shop.getMaxPall() + "паллет! Машин равных или меньше данному значению не найдено!\n";
				System.err.println("ColossusProcessorANDRestrictions2.run: Ограничения на магазин следующие: не более" + shop.getMaxPall() + "паллет! Машин равных или меньше данному значению не найдено!");
				//Нужно придумать специальную обработку
//				Solution solution = new Solution();
//				solution.setStackTrace(stackTrace);
//				return solution;
			}
			double oneWidthPall = (double) (shop.getWeight()/shop.getNeedPall());
			double widthNewShop = oneWidthPall*targetTruck.getPall();
			if(widthNewShop > targetTruck.getWeigth()) {
				//если по весу не проходит, то алгоритм разбивает магаз не по паллетам, а по весу
				System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин c ограничениями по весу!");
				trucks.remove(targetTruck);
				int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
				targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
				Integer newNeedPallForShop = shopPall - newNeedPall;
				Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
				targetTruck.setTargetWeigth(finalWidthFOrTruck);
				Integer newNeedWeigthForShop = shopWeight - finalWidthFOrTruck;
				Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(),
						shop.getLng(), shop.getLength(), shop.getWidth(), shop.getHeight(), shop.getMaxPall());
				newShopHasPall.setWeight(newNeedWeigthForShop);
				newShopHasPall.setNeedPall(newNeedPallForShop);
				newShopHasPall.setDistanceFromStock(distanceFromStock);
				List<Shop> points = new ArrayList<Shop>();
				shopsForDelite.add(shop);
				shopsForAddNewNeedPall.add(newShopHasPall);
				shop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
				points.add(targetStock);
				points.add(shop);
				points.add(targetStock);
				VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
				changeTruckHasSmall(vehicleWay, targetStock);
				whiteWay.add(vehicleWay);
			}else {
				System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин БЕЗ ограничениями по весу!");
				trucks.remove(targetTruck);
				int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
				targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
				Integer newNeedPallForShop = shopPall - targetTruck.getPall();
				Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
				targetTruck.setTargetWeigth(finalWidthFOrTruck);
				Integer newNeedWeigthForShop = shopWeight - finalWidthFOrTruck;
				Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(),
						shop.getLng(), shop.getLength(), shop.getWidth(), shop.getHeight(), shop.getMaxPall());
				newShopHasPall.setNeedPall(newNeedPallForShop);
				newShopHasPall.setWeight(newNeedWeigthForShop);
				newShopHasPall.setDistanceFromStock(distanceFromStock);
				List<Shop> points = new ArrayList<Shop>();
				shopsForDelite.add(shop);
				shopsForAddNewNeedPall.add(newShopHasPall);
				shop.setNeedPall(targetTruck.getPall()); // указываем текущую потребность магазина для этой фуры
				points.add(targetStock);
				points.add(shop);
				points.add(targetStock);
				VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
				changeTruckHasSmall(vehicleWay, targetStock);
				whiteWay.add(vehicleWay);
			}						
		}
		shopsForOptimization.addAll(shopsForAddNewNeedPall);
	}
	/**
	 * Метод раздеяет число на слогаемые в разных вариациях
	 * 
	 * @param n - число которое нужно разложить
	 * @return - Массив массивов числа
	 */
	public List<List<Integer>> splitPall(int n) {
		List<Integer> temp = new ArrayList<>();
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		for (int i = 0; i < n; ++i) {
			temp.add(1);
		}
		while (temp.get(0) != n) {
//			System.out.println(temp);
			List<Integer> temp2 = new ArrayList<Integer>(temp);
			result.add(temp2);
			int min = temp.get(0);
			int minIndex = 0;
			int sum = temp.get(0);
			int tempSum = temp.get(0);
			for (int j = 1; j < temp.size() - 1; ++j) {
				tempSum += temp.get(j);
				if (min > temp.get(j)) {
					min = temp.get(j);
					minIndex = j;
					sum = tempSum;
				}
			}
			temp.set(minIndex, temp.get(minIndex) + 1);
			sum += 1;
			temp.subList(minIndex + 1, temp.size()).clear();
			for (int k = 0; k < n - sum; ++k) {
				temp.add(1);
			}
		}
		return result;
	}

	/**
	 * Отдаёт колличество паллет загруженных в машину
	 * 
	 * @param shops
	 * @param targetStock
	 * @return
	 */
	public Integer calcPallHashHsop(List<Shop> shops, Shop targetStock) {
		Integer summ = 0;
		for (Shop shop : shops) {
			if (!targetStock.equals(shop)) {
				summ = summ + shop.getNeedPall();
			}
		}
		return summ;
	}
	
	public Integer calcWeightHashHsop(List<Shop> shops, Shop targetStock) {
		Integer summ = 0;
		for (Shop shop : shops) {
			if (!targetStock.equals(shop)) {
				summ = summ + shop.getWeight();
			}
		}
		return summ;
	}

}
