package by.base.main.util.hcolossus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.CompareToBuilder;
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
import by.base.main.util.hcolossus.service.ComparatorShopsWhithRestrict;
import by.base.main.util.hcolossus.service.LogicAnalyzer;
import by.base.main.util.hcolossus.service.MatrixMachine;
import by.base.main.util.hcolossus.service.ShopMachine;
import by.base.main.util.hcolossus.service.VehicleMachine;

/**
 * Самый основной оптимизатор! БАЗА! Изменение запрещено!
 * Прямое продолжение 3 -й версии
 * Особенность метода в том, что он принимает еще потребность магазина по вывозу паллет и
 * подбирает машину с учётом этих паллет.
 * Этот метод полностью не протестирован.
 * Главное - не реализована ситуация, когда несколько потребностей попадает в машину (а должна ли?!)
 */
@Component
public class ColossusProcessorANDRestrictions4 {

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

//	private Comparator<Shop> shopComparatorPallOnly = (o1, o2) -> (o2.getNeedPall() - o1.getNeedPall()); // сортирует от большей потребности к меньшей
	private Comparator<Shop> shopComparatorPallOnly = (o1, o2) -> Double.compare(o2.getNeedPall(), o1.getNeedPall());// сортирует от большей потребности к меньшей. Переделка прошлго метода под double


//	private Comparator<Vehicle> vehicleComparatorFromMax = (o1, o2) -> (o2.getPall() - o1.getPall()); // сортирует от большей потребности к меньшей без учёта веса
	
//	private Comparator<Vehicle> vehicleComparatorFromMax = (o1, o2) -> { // этот метод сортирует от большей потребности к меньшей без учёта веса b отправляет вним списка машины помоченные на вторйо круг
//	    // Сначала проверяем isTwiceRound() и перемещаем такие элементы вниз всего списка
//	    if (o1.isTwiceRound() && !o2.isTwiceRound()) {
//	        return 1;  // o1 опускаем вниз
//	    } else if (!o1.isTwiceRound() && o2.isTwiceRound()) {
//	        return -1; // o2 опускаем вниз
//	    }
//
//	    // Если оба элемента имеют одинаковый статус isTwiceRound, сортируем по pall
//	    return Integer.compare(o2.getPall(), o1.getPall());
//	};
	
	private Comparator<Vehicle> vehicleComparatorFromMax = (o1, o2) -> { // этот метод сортирует от большей потребности к меньшей без учёта веса и отправляет вниз списка машины помоченные как КЛОНЫ!
    // Сначала проверяем isTwiceRound() и перемещаем такие элементы вниз всего списка
    if (o1.isClone() && !o2.isClone()) {
        return 1;  // o1 опускаем вниз
    } else if (!o1.isClone() && o2.isClone()) {
        return -1; // o2 опускаем вниз
    }

    // Если оба элемента имеют одинаковый статус isTwiceRound, сортируем по pall
    return Double.compare(o2.getPall(), o1.getPall());
};
	
//	private Comparator<Vehicle> vehicleComparatorFromMin = (o1, o2) -> (o1.getPall() - o2.getPall()); // сортирует от меньшей потребности к большей без учёта веса
	private Comparator<Vehicle> vehicleComparatorFromMin = (o1, o2) -> Double.compare(o1.getPall(), o2.getPall());//сортирует от меньшей потребности к большей без учёта веса Переделка прошлго метода под double
//	
//	private Comparator<Vehicle> vehicleComparatorFromMinAndMass = (o1, o2) -> (o1.getWeigth() / o1.getPall() - o2.getWeigth() / o2.getPall());// сортирует от меньшей потребности к большей C УЧЁТОМ веса
//	
//	private Comparator<Vehicle> vehicleComparatorFromMaxAndMass = (o1, o2) -> (o2.getWeigth() / o2.getPall() - o1.getWeigth() / o1.getPall());// сортирует от большей потребности к меньшей C УЧЁТОМ веса
	
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
	/**
	 * Список тачек, которые подходят для вывоза товара из магазина
	 */
	private List<Vehicle> trucksForShopReturn;
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
	public Solution run(JSONObject jsonMainObject, List<Integer> shopList, List<Double> pallHasShops, List<Integer> tonnageHasShops, Integer stock,
			Double koeff, String algoritm, Map<Integer, String> shopsWithCrossDockingMap, Integer maxShopInWay, List<Double> pallReturn) throws Exception {
		if(tonnageHasShops == null) {
			System.err.println("Используется старый метод! Нужно использовать /map/myoptimization3");
			return null;
		}
		List<Shop> problemShops = new ArrayList<Shop>(); // проблемные магазины
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
		shopsForOptimization = shopMachine.prepareShopList4Parameters(shopList, pallHasShops,tonnageHasShops, stock, shopsWithCrossDockingMap, pallReturn); // магазины для распределения выстроены в порядке убывания потребностей TEST

		sortedShopsHasKrossingAndReturnPall(); // Делает так, чтобы магазины, которые входят в кроссы были сверху списка

		stackTrace = "Начальное распределение магазинов слеюущее: \n";
		for (Shop s : shopsForOptimization) {
//			System.out.println("->" + s.getNumshop() + " - " + s.getDistanceFromStock() + " km - " + s.getNeedPall() + " pall");
			String answer = s.getKrossPolugonName() == null ? "НЕТ" : "ДА";
			String answer2 = s.getPallReturn() == null ? "НИЧЕГО" : s.getPallReturn() + " паллет";
			stackTrace = stackTrace + "магазин : " + s.getNumshop() + "- входит в кросс "+ answer +" - " + s.getDistanceFromStock() + " км - "
					+ s.getNeedPall() + " паллет; Забрать нужно: "+answer2+"\n";
		}
		
		System.out.println("===========");
		stackTrace = stackTrace + "===========\n";

		// тут пойдёт логика самого маршрутизатора
		int i = 0; // для тестов, чтобы ограничивать цикл while
//		int iMax = shopsForOptimization.size() * shopsForOptimization.size() * 3;
		int iMax = shopsForOptimization.size() * shopsForOptimization.size() * 10;
		int j = 0; // итерация
		
		trucks.sort(vehicleComparatorFromMax); // сортируем от больших паллет к маньшим
		
		/*
		 * тут получаем лист с магазами в кроссах и делаем мапу где ключ - название полигона - значение - лист магазов
		 */
		
		String nameKrosPolygon = null; // имя полигона для определения сл. полигона
		Double pallReturnInWay = null; //
		
		stackTrace = stackTrace +  "Начальное распределение машин слеюущее: \n";
		for (Vehicle v : trucks) {
			String answer = !v.isTwiceRound() ? "НЕТ" : "ДА";
			String answer2 = !v.isClone() ? "НЕТ" : "ДА";
			stackTrace = stackTrace + "Машина : " + v.getId()+"/"+v.getName() + ", паллеты : "+ v.getPall() +", второй круг : " + answer + ", клон : " + answer2 +"!\n";
		}
		
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
			sortedShopsHasKrossingAndReturnPall(); // Делает так, чтобы магазины, которые входят в кроссы были сверху списка
			
			//берем самый дальний магазин
			Shop firstShop = shopsForOptimization.remove(0);
			boolean isRestrictionsFirst = false;
			
			nameKrosPolygon = firstShop.getKrossPolugonName();
			pallReturnInWay = firstShop.getPallReturn();
			
			//Проверяем, загрузится ли этот магазин в самую большую машину (проверка на идеальные маршруты)
			Integer pallRestrictionIdeal = firstShop.getMaxPall() != null ? firstShop.getMaxPall() : null;
			Double shopPall = firstShop.getNeedPall();
			Double maxPallTruck = trucks.get(0).getPall();
			Double maxPallTruckRestriction = null;
			if(pallRestrictionIdeal != null) {
				for (Vehicle truck : trucks) {
					if (truck.getPall() <= pallRestrictionIdeal) {
						maxPallTruckRestriction = truck.getPall();
						break;
					}
				}	
			}
			
			
			
			// проверяем, есть ли ограничения и записываем
			
			if (shopPall >= maxPallTruck && pallRestrictionIdeal == null) {
			// логика создания идеального маршрута если нет ограничений
				createIdealWay(i+"", firstShop, targetStock);
				i++;
				continue;
			}
	
			// логика создания идеального маршрута если ЕСТЬ ограниченя
			if (pallRestrictionIdeal != null && maxPallTruckRestriction != null  && shopPall >= maxPallTruckRestriction) {
				createIdealWayAndPallRestriction(i+"", firstShop, targetStock);
				i++;
				continue;
			}
			isRestrictionsFirst = firstShop.getMaxPall() != null ? true : false; // тут определяем есть ли ограничения в текущем задании
			/*
			 * это значение означает что попался магазин с потребностью вернуть паллеты!
			 */
			
			
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
			if(pallReturnInWay!= null && virtualTruck.getPall() < pallReturnInWay) {
				System.err.println("Ошибка. Нет машины способной забрать " + pallReturnInWay + " паллет из магазина " + firstShop.getNumshop());
				//остановился тут
			}
			
			if(pallReturnInWay!= null && firstShop.getMaxPall() != null && pallReturnInWay > firstShop.getMaxPall()) {
				System.err.println("FATAL ERROR!. Ограничение на подъезд к магазину " + firstShop.getNumshop() + " составляет " + firstShop.getMaxPall() + ". А потребность в машине составляет: "  + pallReturnInWay + " паллет минимум");
			}
			
			//тут формируем новый список машин, которые в теории могут забрать посылку из магаза
//			trucksForShopReturn = new ArrayList<Vehicle>();
//			if(pallReturnInWay!= null) {
//				for (Vehicle truck : trucks) {
//					if(truck.getPall() >= pallReturnInWay) {
//						trucksForShopReturn.add(truck);				
//					}
//				}			
//			}
			
			int countRadiusMap = 0;
			int maxCountRadiusMap = radiusMap.entrySet().size()-1;
			boolean isRestrictions = false;
			for (Map.Entry<Double, Shop> entry : radiusMap.entrySet()) {
				Shop shop2 = entry.getValue();
				
				//test
				
				
				if(firstShop.getKrossPolugonName()!=null && shop2.getKrossPolugonName() == null || nameKrosPolygon != null && !shop2.getKrossPolugonName().equals(nameKrosPolygon)) { // если первый магаз входит в крос а второй нет - пропускаем!
					continue;
				}
				
				//тут проверяем если магазинов в маршруте больше 22 - останавливаемся!
				if(points.size()>= maxShopInWay+1) {
					points.add(targetStock);
					break;
				}
				//тут проверяем, если магаз с ограничением меньше чем паллеты которые нужно забрать в первом магазине - не добавляем магаз
				if(pallReturnInWay != null && shop2.getMaxPall()!= null && shop2.getMaxPall() < pallReturnInWay) {
					continue;
				}
				
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
				
				double totalPall = calcPallHashHsop(points, targetStock);
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
						points.remove(points.size()-1);
						specialShop.setPackageShop("Отсутствуют машины с паллетовместимостью " + specialPall + " и ниже!");
						problemShops.add(specialShop);						
//						shopsForOptimization.add(specialShop);
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
					double totalPall = calcPallHashHsop(points, targetStock);
					int totalWeigth = calcWeightHashHsop(points, targetStock);
					
					if(totalPall > virtualTruck.getPall().intValue() || totalWeigth > virtualTruck.getWeigth().intValue()) {
						List<Shop> pointsNew = new ArrayList<Shop>();
						pointsNew.add(targetStock);
						pointsNew.add(specialShop);
						for (Shop shop : points) {
							if(shop.equals(targetStock) || shop.equals(specialShop)) continue;
							pointsNew.add(shop);
							double totalPallNew = calcPallHashHsop(pointsNew, targetStock);
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
						if(firstShop.getKrossPolugonName()!=null && shop2.getKrossPolugonName() == null || nameKrosPolygon != null && !shop2.getKrossPolugonName().equals(nameKrosPolygon)) { // если первый магаз входит в крос а второй нет - пропускаем!
							continue;
						}
						Integer specialPallNew = shop2.getMaxPall() != null ? shop2.getMaxPall() : null; // тут определяем есть ли ограничения в текущем задании
						
						//тут проверяем если магазинов в маршруте больше 22 - останавливаемся!
						if(points.size()>=maxShopInWay+1) {
							points.add(targetStock);
							break;
						}
						//тут проверяем, если магаз с ограничением меньше чем паллеты которые нужно забрать в первом магазине - не добавляем магаз
						if(pallReturnInWay != null && shop2.getMaxPall()!= null && shop2.getMaxPall() < pallReturnInWay) {
							continue;
						}
						
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
//								points.forEach(p-> System.out.println(p.toAllString()));
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
			
			//тут проверка, если есть только склад и магазин - добавляет последний склад
			if(points.size() == 2 && points.get(0).equals(targetStock) && points.get(1).getNumshop() != points.get(0).getNumshop()) {
				points.add(targetStock);
			}
			
			if(points.size() >= 3) {//тут делаем проверку на то что не ломаный ли маршрут (типо 1700-1700)
				
				trucks.remove(virtualTruck);
				trucks.remove(virtualTruck);
				//если тачка помечена готовой для второго круга - создаём её клон
				
				
				virtualTruck.setTargetWeigth(calcWeightHashHsop(points, targetStock));
				virtualTruck.setTargetPall(calcPallHashHsop(points, targetStock));	
				
				// создаём финальный, виртуальный маршрут
				points.add(targetStock);
				
				String idStr = virtualTruck.isClone() ? i+ "/2 круг" : i+ "";
				
				VehicleWay vehicleWayVirtual = new VehicleWay(idStr, points, 0.0, 30, virtualTruck);
				vehicleWayVirtual.setDistanceFromStock(firstShop.getDistanceFromStock());	
				
				
				//методы постобработки маршрутов
				
//			superProcessingWay(vehicleWayVirtual, targetStock); // метод попутноо подбора авто
				
				changeTruckHasSmall(vehicleWayVirtual, targetStock); // метод замены авто на меньшее
				
//			optimizePoints(vehicleWayVirtual);//метод оптимизации точек маршрута
				
				optimizePointsAndLastPoint(vehicleWayVirtual);//метод оптимизации точек маршрута
				
				
				if(vehicleWayVirtual.getVehicle().isTwiceRound() && !vehicleWayVirtual.getVehicle().isClone() && vehicleWayVirtual.calcTotalRun(matrixMachine) > 250000.0 ) {//если тачка помечена готовой для второго круга - создаём её клон
					Vehicle cloneTruck = vehicleWayVirtual.getVehicle().cloneForSecondRound();
					cloneTruck.setClone(true);
					cloneTruck.setTwiceRound(false);
					cloneTruck.setId(cloneTruck.getId()*(-1));
					trucks.remove(cloneTruck);					
				}
				
				whiteWay.add(vehicleWayVirtual);
				i++;			
			}else {
				System.err.println("СРАБОТАЛ МЕТОД ОТСЕЧКИ ЛОМАННЫХ МАРШРУТОВ!");
			}
			
		}


		stackTrace = stackTrace + "-->Остановлен  на итерации " + i + ". Максимальное значение итераций в данном задании: "+iMax+".<-- \n";
		
		//добавляем проблемные магазины
		shopsForOptimization.addAll(problemShops);
		
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
			stackTrace = stackTrace + v.getNumshop() + " (" + v.getNeedPall() + ") info = "+v.getPackageShop() + "\n";
		}
		

		whiteWay.forEach(w -> System.out.println(w));
		System.out.println("========= Свободные авто ==========");
		trucks.forEach(t -> System.out.println(t));
		System.out.println("+++++++++ Оставшиеся магазины +++++++++++");
//		shopsForOptimization.forEach(s -> System.out.println(s.toAllString()));
		

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
//		stackTrace = stackTrace + "Суммарный пробег маршрута: " + totalRunHasMatrix + " м.\n";
		solution.setStackTrace(stackTrace);
		return solution;

	}

	
	/**
	 * Оптимизирует точки по простому алгоритму: от крайней точке к самой ближайшей и так далее.
	 * <br> Так строятся красивые логичные маршруты (не всегда короткие)
	 * <br> Важно. Тут мы фиксируем самую ближнюю точку к складу. 
	 * @param vehicleWayVirtual
	 */
	private void optimizePointsAndLastPoint(VehicleWay vehicleWayVirtual) {
		if(vehicleWayVirtual.getWay() == null || vehicleWayVirtual.getWay().size()<3) {
			return;
		}
		List<Shop> points = new ArrayList<Shop>(vehicleWayVirtual.getWay());
		Double totalRunHasMatrix = 0.0;
		List<Shop> points2 = new ArrayList<Shop>(vehicleWayVirtual.getWay());
		Double totalRunHasMatrix2 = 0.0;
		List<Shop> points3 = new ArrayList<Shop>(vehicleWayVirtual.getWay());
		Double totalRunHasMatrix3 = 0.0;
		
		vehicleWayVirtual.getWay().remove(vehicleWayVirtual.getWay().size()-1);
		Shop targetStock = points.remove(0);
		
		List<Shop> pointsNew = new ArrayList<Shop>();
		List<Shop> pointsNew2 = new ArrayList<Shop>();
		List<Shop> pointsNew3 = new ArrayList<Shop>();
		//определяем самый дальний магазин от склада	
		Map<Double, Shop> startMatrix = getDistanceMatrixHasMin(vehicleWayVirtual.getWay(), targetStock);
		Shop furtherShop = startMatrix.entrySet().stream().reduce((a, b) -> b).orElse(null).getValue(); // получаем последний элемент
		pointsNew.add(targetStock);
		pointsNew.add(furtherShop);
		points.remove(furtherShop);
		
		//определяем самый ближний магазин от склада
		Shop lastShop = startMatrix.entrySet().stream().findFirst().get().getValue();
		if(pointsNew.contains(lastShop)) {
			lastShop = null;
		}else {
			points.remove(lastShop);
		}
		
		
		for (Shop shop : points) {
			Shop backShop = null;
			if(pointsNew.size() == 2) {
				backShop = furtherShop;
			}else {
				backShop = pointsNew.get(pointsNew.size()-1);
			}
			Map<Double, Shop> distanceMap = getDistanceMatrixHasMin(points, backShop);
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
		
		if(lastShop != null) {
			pointsNew.add(lastShop);
		}
		pointsNew.add(targetStock);
//		vehicleWayVirtual.setWay(pointsNew);	
		for (int l = 0; l < pointsNew.size() - 1; l++) {
			String key = pointsNew.get(l).getNumshop() + "-" + pointsNew.get(l + 1).getNumshop();
			totalRunHasMatrix = totalRunHasMatrix + matrixMachine.matrix.get(key);
		}
		
		
		//тут пробуем строить без фиксации последней точки
		//определяем самый дальний магазин от склада	
				Map<Double, Shop> startMatrix2 = getDistanceMatrixHasMin(vehicleWayVirtual.getWay(), targetStock);
				Shop furtherShop2 = startMatrix2.entrySet().stream().reduce((a, b) -> b).orElse(null).getValue(); // получаем последний элемент
				pointsNew2.add(targetStock);
				pointsNew2.add(furtherShop2);
				points2.remove(furtherShop2);
				
				for (Shop shop : points2) {
					Shop backShop = null;
					if(pointsNew2.size() == 2) {
						backShop = furtherShop2;
					}else {
						backShop = pointsNew2.get(pointsNew2.size()-1);
					}
					Map<Double, Shop> distanceMap = getDistanceMatrixHasMin(points2, backShop);
					for (Map.Entry<Double, Shop> entry : distanceMap.entrySet()) {
						Shop targetShop = entry.getValue();
						if(!pointsNew2.contains(targetShop)) {
							pointsNew2.add(targetShop);
							break;
						}else {
							continue;
						}
					}
					
				}
				
				pointsNew2.add(targetStock);
//				vehicleWayVirtual.setWay(pointsNew);		
				for (int l = 0; l < pointsNew2.size() - 1; l++) {
					String key = pointsNew2.get(l).getNumshop() + "-" + pointsNew2.get(l + 1).getNumshop();
					totalRunHasMatrix2 = totalRunHasMatrix2 + matrixMachine.matrix.get(key);
				}
				
				//от обратного
				//определяем самый дальний магазин от склада	
				Map<Double, Shop> startMatrix3 = getDistanceMatrixHasMin(vehicleWayVirtual.getWay(), targetStock);
				Shop furtherShop3 = startMatrix2.entrySet().stream().findFirst().get().getValue(); // получаем первый
				pointsNew3.add(targetStock);
				pointsNew3.add(furtherShop3);
				points3.remove(furtherShop3);
				
				for (Shop shop : points3) {
					Shop backShop = null;
					if(pointsNew3.size() == 2) {
						backShop = furtherShop3;
					}else {
						backShop = pointsNew3.get(pointsNew3.size()-1);
					}
					Map<Double, Shop> distanceMap = getDistanceMatrixHasMin(points3, backShop);
					for (Map.Entry<Double, Shop> entry : distanceMap.entrySet()) {
						Shop targetShop = entry.getValue();
						if(!pointsNew3.contains(targetShop)) {
							pointsNew3.add(targetShop);
							break;
						}else {
							continue;
						}
					}
					
				}
				
				pointsNew3.add(targetStock);
//				vehicleWayVirtual.setWay(pointsNew);		
				for (int l = 0; l < pointsNew2.size() - 1; l++) {
					String key = pointsNew2.get(l).getNumshop() + "-" + pointsNew2.get(l + 1).getNumshop();
					totalRunHasMatrix2 = totalRunHasMatrix2 + matrixMachine.matrix.get(key);
				}
		
				if(totalRunHasMatrix > totalRunHasMatrix2 && totalRunHasMatrix3 > totalRunHasMatrix2) {
					vehicleWayVirtual.setWay(pointsNew2);
//					vehicleWayVirtual.setTotalRun(totalRunHasMatrix2);
				}else if(totalRunHasMatrix2 > totalRunHasMatrix3 && totalRunHasMatrix > totalRunHasMatrix3) {
					vehicleWayVirtual.setWay(pointsNew3);
//					vehicleWayVirtual.setTotalRun(totalRunHasMatrix);
				}else if(totalRunHasMatrix2 > totalRunHasMatrix && totalRunHasMatrix3 > totalRunHasMatrix) {
					vehicleWayVirtual.setWay(pointsNew);
				}
		
		
	}
	
	/**
	 * Оптимизирует точки по простому алгоритму: от крайней точке к самой ближайшей и так далее.
	 * <br> Так строятся красивые логичные маршруты (не всегда короткие)
	 * @param vehicleWayVirtual
	 */
	private void optimizePoints(VehicleWay vehicleWayVirtual) {
		List<Shop> points = vehicleWayVirtual.getWay();
		vehicleWayVirtual.getWay().remove(vehicleWayVirtual.getWay().size()-1);
		Shop targetStock = points.remove(0);
		
		List<Shop> pointsNew = new ArrayList<Shop>();
		//определяем самый дальний магазин от склада	
		Map<Double, Shop> startMatrix = getDistanceMatrixHasMin(vehicleWayVirtual.getWay(), targetStock);
		Shop furtherShop = startMatrix.entrySet().stream().reduce((a, b) -> b).orElse(null).getValue(); // получаем последний элемент
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
			Double allPall = calcPallHashHsop(newPoints, targetStock) + shop.getNeedPall();
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
//		System.out.println("-->>> changeTruckHasSmall START : " + vehicleWayVirtual);
		Double pallReturnInWay = 0.0;
//		.stream().filter(s-> s.getPallReturn()!=null).filter(s-> pallReturnInWay!= null && pallReturnInWay < s.getPallReturn()).forEach(s-> pallReturnInWay = s.getPallReturn());
		for (Shop shop : vehicleWayVirtual.getWay()) {
			if(shop.getPallReturn()!=null) {
				if(pallReturnInWay < shop.getPallReturn()) {
					pallReturnInWay = shop.getPallReturn();
				}else if(pallReturnInWay == 0.0){
					pallReturnInWay = shop.getPallReturn();
				}
			}
		}
		
		
		if(vehicleWayVirtual.getVehicle().getTargetWeigth() == null) {
			throw new Exception();
		}
		if (!vehicleWayVirtual.getVehicle().isFull()) {
			// проверяем, есть ли, гипотетически меньшая машина, для того чтобы сохранить
			// большую
			Double pallHasWay = calcPallHashHsop(vehicleWayVirtual.getWay(), targetStock);
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
			Double pallHasOldTruck = vehicleWayVirtual.getVehicle().getPall();
			trucks.sort(vehicleComparatorFromMin);
			for (Vehicle truck : trucks) {
//				System.err.println(truck + " <----> " + oldTruck);
				if(truck.getWeigth() >= oldTruck.getTargetWeigth()) {
					if (truck.getPall() >= oldTruck.getTargetPall() && truck.getPall() < pallHasOldTruck && extraPall > truck.getPall() && truck.getPall() >= pallReturnInWay) {
						truck.setTargetPall(pallHasWay);
						truck.setTargetWeigth(oldTruck.getTargetWeigth());
						trucks.add(oldTruck);
						trucks.remove(truck);
						vehicleForDelete.add(truck);
						vehicleForDelete.remove(oldTruck);
						vehicleWayVirtual.setVehicle(truck);
						stackTrace = stackTrace + "Меняем тачку с " + oldTruck.getPall() + " на "
								+ truck.getPall() + " на маршруте с id = "+vehicleWayVirtual.getId()+"\n";
						break;
					}
				}
				
			}
//			System.out.println("-->>> changeTruckHasSmall FINISH : " + vehicleWayVirtual);
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
		Double shopPall = shop.getNeedPall();
		Integer shopWeight = shop.getWeight();
		Double maxPallTruck = trucks.get(0).getPall();
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
			shop.setNeedPall(targetTruck.getPall().doubleValue()); // указываем текущую потребность магазина для этой фуры
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
			double newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
			targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
			targetTruck.setTargetWeigth((int)widthNewShop);
			Double newNeedPallForShop = shopPall - newNeedPall;
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
			if(newShopHasPall.getNeedPall()>0) {
				shopsForAddNewNeedPall.add(newShopHasPall);
			}
			shop.setNeedPall(targetTruck.getTargetPall().doubleValue()); // указываем текущую потребность магазина для этой фуры
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
				double newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
				targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
				Double newNeedPallForShop = shopPall - newNeedPall;
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
				if(newShopHasPall.getNeedPall()>0) {
					shopsForAddNewNeedPall.add(newShopHasPall);
				}
				shop.setNeedPall(targetTruck.getTargetPall().doubleValue()); // указываем текущую потребность магазина для этой фуры
				shop.setWeight(targetTruck.getTargetPall() * oneWidthPall);
				points.add(targetStock);
				points.add(shop);
				points.add(targetStock);
				VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
				changeTruckHasSmall(vehicleWay, targetStock);
				whiteWay.add(vehicleWay);
			}else {
				System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин по паллетам!");
				double newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
				if(newNeedPall>= targetTruck.getPall()) {
					targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью		
				}else {
					System.err.println("обработать вариант! По идее такого быть не должно");
					targetTruck.setTargetPall(newNeedPall);
				}								
				Double newNeedPallForShop = shopPall - targetTruck.getPall();
				Double finalWidthFOrTruck = targetTruck.getTargetPall()*oneWidthPall;
				targetTruck.setTargetWeigth(roundВouble(finalWidthFOrTruck, 0).intValue());
				Double newNeedWeigthForShop = shopWeight - finalWidthFOrTruck;
				Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(),
						shop.getLng(), shop.getLength(), shop.getWidth(), shop.getHeight(), shop.getMaxPall());
				newShopHasPall.setWeight(newNeedWeigthForShop);
				newShopHasPall.setNeedPall(newNeedPallForShop);
				newShopHasPall.setDistanceFromStock(distanceFromStock);
				List<Shop> points = new ArrayList<Shop>();				
				shopsForDelite.add(shop);
				if(newShopHasPall.getNeedPall()>0) {
					shopsForAddNewNeedPall.add(newShopHasPall);
				}
				shop.setNeedPall(targetTruck.getTargetPall().doubleValue()); // указываем текущую потребность магазина для этой фуры
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
		Double shopPall = shop.getNeedPall();
		Integer shopWeight = shop.getWeight();
		Double maxPallTruck = trucks.get(0).getPall();
		Integer maxWeighTruck = trucks.get(0).getWeigth();
		Double distanceFromStock = shop.getDistanceFromStock();
		
		System.out.println("пришел магаз: " + shop);

		// проверяем, есть ли ограничения и записываем
		Integer pallRestrictionIdeal = shop.getMaxPall() != null ? shop.getMaxPall() : null;
		
		if (shopPall <= pallRestrictionIdeal && shopPall == maxPallTruck && maxWeighTruck >= shopWeight) {
			System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин БЕЗ ограничениями по весу, при том что паллеты проходят ровно!");
			Vehicle targetTruck = trucks.remove(0);
			targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
			targetTruck.setTargetWeigth(shopWeight); // загружаем авто по весу
			List<Shop> points = new ArrayList<Shop>();
			points.add(targetStock);
			shop.setNeedPall(targetTruck.getPall().doubleValue()); // указываем текущую потребность магазина для этой фуры
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
			double newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
			targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
			targetTruck.setTargetWeigth((int)widthNewShop);
			Double newNeedPallForShop = shopPall - newNeedPall;
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
			if(newShopHasPall.getNeedPall()>0) {
				shopsForAddNewNeedPall.add(newShopHasPall);
			}
			shop.setNeedPall(targetTruck.getTargetPall().doubleValue()); // указываем текущую потребность магазина для этой фуры
			points.add(targetStock);
			points.add(shop);
			points.add(targetStock);
			VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
			changeTruckHasSmall(vehicleWay, targetStock);
			whiteWay.add(vehicleWay);
		}
		if (pallRestrictionIdeal != null && shopPall >= pallRestrictionIdeal || pallRestrictionIdeal != null && pallRestrictionIdeal < maxPallTruck) {
			Vehicle targetTruck = null;
			for (Vehicle truck : trucks) {
				if (truck.getPall() <= pallRestrictionIdeal) {
					targetTruck = truck;
					break;
				}
			}
			if(targetTruck == null) {
				stackTrace = stackTrace + "Ограничения на магазин следующие: не более " + shop.getMaxPall() + "паллет! Машин равных или меньше данному значению не найдено!\n";
				System.err.println("ColossusProcessorANDRestrictions3.run: Ограничения на магазин следующие: не более " + shop.getMaxPall() + " паллет! Машин равных или меньше данному значению не найдено!");
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
				double newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
				targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
				Double newNeedPallForShop = shopPall - newNeedPall;
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
				if(newShopHasPall.getNeedPall()>0) {
					shopsForAddNewNeedPall.add(newShopHasPall);
				}
				shop.setNeedPall(targetTruck.getTargetPall().doubleValue()); // указываем текущую потребность магазина для этой фуры
				points.add(targetStock);
				points.add(shop);
				points.add(targetStock);
				VehicleWay vehicleWay = new VehicleWay(id, points, 0.0, 40, targetTruck);
				changeTruckHasSmall(vehicleWay, targetStock);
				whiteWay.add(vehicleWay);
			}else {
				System.err.println("Блок распределения идеальных маршрутов: Распределяем магазин БЕЗ ограничениями по весу!");				
				trucks.remove(targetTruck);				
				targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
				Double newNeedPallForShop = shopPall - targetTruck.getPall();
				Double finalWidthFOrTruck = targetTruck.getPall()*oneWidthPall;
				targetTruck.setTargetWeigth(roundВouble(finalWidthFOrTruck, 0).intValue());
				Double newNeedWeigthForShop = shopWeight - finalWidthFOrTruck;
				Shop newShopHasPall = new Shop(shop.getNumshop(), shop.getAddress(), shop.getLat(),
						shop.getLng(), shop.getLength(), shop.getWidth(), shop.getHeight(), shop.getMaxPall());
				newShopHasPall.setNeedPall(newNeedPallForShop);
				newShopHasPall.setWeight(newNeedWeigthForShop);
				newShopHasPall.setDistanceFromStock(distanceFromStock);
				List<Shop> points = new ArrayList<Shop>();
				shopsForDelite.add(shop);
				if(newShopHasPall.getNeedPall()>0) {
					shopsForAddNewNeedPall.add(newShopHasPall);
				}				
				shop.setNeedPall(targetTruck.getPall().doubleValue()); // указываем текущую потребность магазина для этой фуры
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
	 * Делает так, чтобы магазины, которые взодят в кроссы были сверху списка
	 */
	@Deprecated
	public void sortedShopsHasKrossing() {
		// тут берем и сначала поднимае вверх магазы которые учавствуют в кроссах
		// Сортируем объекты с ненулевыми значениями
        List<Shop> sortedShops = shopsForOptimization.stream()
            .filter(s -> s.getKrossPolugonName() != null)
            .sorted(Comparator.comparing(Shop::getKrossPolugonName))
            .collect(Collectors.toList());

        // Добавляем к ним объекты с null в исходном порядке
        sortedShops.addAll(shopsForOptimization.stream()
            .filter(s -> s.getKrossPolugonName() == null)
            .collect(Collectors.toList()));

        // Перезаписываем исходный список
        shopsForOptimization.clear();
        shopsForOptimization.addAll(sortedShops);
	}
	
//	/**
//	 * Сортирует список магазинов по следующим критериям:
//	 * 1. Магазины с ненулевым значением getKrossPolugonName поднимаются вверх.
//	 * 2. Далее магазины с ненулевым значением getPallReturn поднимаются выше магазинов с null,
//	 *    но остаются ниже магазинов с ненулевым getKrossPolugonName.
//	 * 3. Магазины с ненулевым getPallReturn сортируются между собой по значению getDistanceFromStock.
//	 * 
//	 * После сортировки исходный список перезаписывается отсортированными элементами.
//	 */
//	public void sortedShopsHasKrossingAndReturnPall() {
//	    // Сортируем магазины по условиям:
//	    // 1. Сначала магазины с ненулевым getKrossPolugonName
//	    // 2. Затем магазины с ненулевым getPallReturn
//	    // 3. Магазины с getPallReturn сортируются по getDistanceFromStock
//	    List<Shop> sortedShops = shopsForOptimization.stream()
//	        .sorted(Comparator.comparing((Shop s) -> s.getKrossPolugonName() != null ? 0 : 1)
//	            .thenComparing((Shop s) -> s.getPallReturn() != null ? 0 : 1)
//	            .thenComparing(Shop::getDistanceFromStock, Comparator.nullsLast(Comparator.naturalOrder())))
//	        .collect(Collectors.toList());
//
//	    // Перезаписываем исходный список
//	    shopsForOptimization.clear();
//	    shopsForOptimization.addAll(sortedShops);
//	}
	
	public void sortedShopsHasKrossingAndReturnPall() {
	    // Фильтруем магазины по условиям:
	    // 1. Сначала магазины с ненулевым getKrossPolugonName
	    // 2. Затем магазины с ненулевым getPallReturn
	    // 3. Магазины с getPallReturn сортируются по getDistanceFromStock
	    List<Shop> filteredShops = shopsForOptimization.stream()
	        .filter(s -> s.getKrossPolugonName() != null || s.getPallReturn() != null)
	        .sorted(Comparator.comparing((Shop s) -> s.getKrossPolugonName() != null ? 0 : 1)
	            .thenComparing((Shop s) -> s.getPallReturn() != null ? 0 : 1)
	            .thenComparing(Shop::getDistanceFromStock, Comparator.nullsLast(Comparator.naturalOrder())))
	        .collect(Collectors.toList());

	    // Удаляем отфильтрованные элементы из исходного списка
	    shopsForOptimization.removeAll(filteredShops);

	    // Добавляем оставшиеся элементы в конец отфильтрованных
	    filteredShops.addAll(shopsForOptimization);

	    // Обновляем исходный список магазинов
	    shopsForOptimization.clear();
	    shopsForOptimization.addAll(filteredShops);
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
	public Double calcPallHashHsop(List<Shop> shops, Shop targetStock) {
		Double summ = 0.0;
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
	
	// округляем числа до 2-х знаков после запятой
		private static Double roundВouble(double value, int places) {
			double scale = Math.pow(10, places);
			return Math.round(value * scale) / scale;
		}

}
