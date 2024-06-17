package by.base.main.util.hcolossus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
public class ColossusProcessorANDRestrictions2 {

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

	private Comparator<Vehicle> vehicleComparatorFromMax = (o1, o2) -> (o2.getPall() - o1.getPall()); // сортирует от
																										// большей
																										// меньшей
																										// потребности к
																										// большей
	private Comparator<Vehicle> vehicleComparatorFromMin = (o1, o2) -> (o1.getPall() - o2.getPall()); // сортирует от
																										// большей
																										// большей
																										// потребности к
																										// меньшей

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
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public Solution run(JSONObject jsonMainObject, List<Integer> shopList, List<Integer> pallHasShops, List<Integer> tonnageHasShops, Integer stock,
			Double koeff, String algoritm) throws JsonMappingException, JsonProcessingException {
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
			System.out.println(
					"->" + s.getNumshop() + " - " + s.getDistanceFromStock() + " km - " + s.getNeedPall() + " pall");
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

			shopsForOptimization.sort(shopComparatorForIdealWay);
			

			// тут ставится блок ограничений, по дефолту все параметры равны null
			Integer pallRestrictionIdeal = null;

			for (Shop shop : shopsForOptimization) {
				//Блок распределения идеальных маршрутов
				// проверяем, поместится ли вся потребность магазина в одну машину
				Integer shopPall = shop.getNeedPall();
				Integer shopWeight = shop.getWeight();
				Integer maxPallTruck = trucks.get(0).getPall();
				Integer maxWeighTruck = trucks.get(0).getWeigth();
				Double distanceFromStock = shop.getDistanceFromStock();

				// проверяем, есть ли ограничения и записываем
				pallRestrictionIdeal = shop.getMaxPall() != null ? shop.getMaxPall() : null;

				// 1. определяем идеальные маршруты (одна точка)
				if (shopPall >= maxPallTruck && pallRestrictionIdeal == null) {
					// логика создания идеального маршрута если нет ограничений
					createIdealWay(shop, targetStock);
					break;
				}

				// логика создания идеального маршрута если ЕСТЬ ограниченя
				if (shopPall >= maxPallTruck && pallRestrictionIdeal != null) {
					createIdealWayAndPallRestriction(shop, targetStock);
					break;
				}

				// 1. конец блока опредиления идеальных маршрутов
				
				if (!shopsForDelite.isEmpty() || !shopsForAddNewNeedPall.isEmpty()) { // обновляемся после итерации
					break;
				}
			}
			

			stackTrace = stackTrace + "Магазины после первого пункта: \n";
			for (Shop shop : shopsForOptimization) {
				stackTrace = stackTrace + shop.getNumshop() + " (" + shop.getNeedPall() + ") \n";
			}
			if (!shopsForDelite.isEmpty()) {
				stackTrace = stackTrace + "На удаление: \n";
				for (Shop shop : shopsForDelite) {
					stackTrace = stackTrace + shop.getNumshop() + " (" + shop.getNeedPall() + ") \n";
				}
			}
			if (!shopsForAddNewNeedPall.isEmpty()) {
				stackTrace = stackTrace + "На обновление: \n";
				for (Shop shop : shopsForAddNewNeedPall) {
					stackTrace = stackTrace + shop.getNumshop() + " (" + shop.getNeedPall() + ") \n";
				}
			}

			// тут ставится блок ограничений, по дефолту все параметры равны null
			Integer pallRestriction = null;
			List<Vehicle> trucksBeforeRestr = null;

			if (shopsForDelite.isEmpty()) {// если в листе удаления магазина есть
																				// хоть один элемент то второй этап
																				// пропускаем
				// пункт 2 - ищем самый дальний и самый загруженный магазин а потом догружаем
				// ближайшими к нему и т.д.
				// после, создаём виртуальный маршрут!
				// кладём его в максимально подходящую тачку
//				shopsForOptimization.sort(shopComparatorDistanceMain);
				shopsForOptimization.sort(comparatorShopsWhithRestrict);
				
//				stackTrace = stackTrace + "Магазины перед вторым пунктом: \n";
//				for (Shop shop : shopsForOptimization) {
//					stackTrace = stackTrace + shop.getNumshop() + " (" + shop.getNeedPall() + ") \n";
//				}

				Shop firstShop = shopsForOptimization.remove(0);
				// проверяем, имеется ли на этом магазине ограничения! ВАЖНО МАГАЗЫ С
				// ОГРАНИЧЕНИЯМИ МЫ НЕ ВСТАВЛЯЕМ ПОСЕРЕДИНЕ СОСТАВЛЕНИЯ МАРШРУТА!
				pallRestriction = firstShop.getMaxPall() != null ? firstShop.getMaxPall() : null;
				// отсюда идёт разбиение на 2 независимых блока. Блока где нет ограничений и
				// блок где они есть.
				if (pallRestriction != null) {
					System.err.println("Блок распределения с ограничениями подъездов");
					trucksBeforeRestr = new ArrayList<Vehicle>();
					//формируем новый список машин не больше указанного ограничения по найденному магазину
					for (Vehicle truck : trucks) {
						if(truck.getPall()<=pallRestriction) {
							trucksBeforeRestr.add(truck);
						}
					}
					trucksBeforeRestr.sort(vehicleComparatorFromMax);
					stackTrace = stackTrace + "Новый список машин, с учётом ограничения первого магазина в "+ pallRestriction +" такой: \n";
					for (Vehicle vehicle : trucksBeforeRestr) {
						stackTrace = stackTrace + vehicle.toString() + "\n";
					}
					// создаём матрицу расстояний от первого магазина
					Map<Double, Shop> radiusMap = new TreeMap<Double, Shop>();
					
					// создаём порядок точек
					List<Shop> points = new ArrayList<Shop>();
					points.add(targetStock);
					points.add(firstShop);
					
					
					//тут возможен вариант того, что потребность магаза будет превышеть возможности допустимых авто! поэтому делаем обработку как в первом пункте
					if(trucksBeforeRestr.isEmpty()) {
						stackTrace = stackTrace + "Закончились машины для чернового распределения!\n";
						System.err.println("ColossusProcessorANDRestrictions2.run: Закончились машины для чернового распределения!");
					}
//					System.out.println("-----> "+ firstShop);
					if(firstShop.getNeedPall() >= trucksBeforeRestr.get(0).getPall()) {//логика для разбиений магазинов когда паллет изначально больше!
						double oneWidthPall = (double) (firstShop.getWeight()/firstShop.getNeedPall());
						double widthNewShop = oneWidthPall*trucksBeforeRestr.get(0).getPall();
						if (firstShop.getNeedPall() == trucksBeforeRestr.get(0).getPall()) { 
							if(firstShop.getWeight() <= trucksBeforeRestr.get(0).getWeigth()) {
								System.err.println("Распределяем магазин БЕЗ ограничений по весу, при том что паллет больше, после идеальных маршрутов (доп идеальные), когда паллет изначально больше!");
								Vehicle targetTruck = trucksBeforeRestr.remove(0);
								trucks.remove(targetTruck);
								targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
								targetTruck.setTargetWeigth(firstShop.getWeight()); // загружаем машину по весу
								points.add(targetStock);
								VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
								shopsForOptimization.remove(firstShop);
								optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
								changeTruckHasSmall(vehicleWay, targetStock);
								whiteWay.add(vehicleWay);							
								continue;
							}else {
								System.err.println("Распределяем магазин c ограничениями по весу, при том что паллеты входят ровно, после идеальных маршрутов (доп идеальные)!");
								Vehicle targetTruck = trucksBeforeRestr.remove(0);
								trucks.remove(targetTruck);
								int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
								targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
								Integer newNeedPallForShop = firstShop.getNeedPall() - newNeedPall;
								Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
								targetTruck.setTargetWeigth(finalWidthFOrTruck);
								Integer newNeedWeigthForShop = firstShop.getWeight() - finalWidthFOrTruck;
								Shop newShopHasPall = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
										firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
								newShopHasPall.setWeight(newNeedWeigthForShop);
								newShopHasPall.setNeedWeigth(Double.parseDouble(newNeedWeigthForShop.toString()));
								newShopHasPall.setNeedPall(newNeedPallForShop);
								newShopHasPall.setDistanceFromStock(firstShop.getDistanceFromStock());
								List<Shop> points1 = new ArrayList<Shop>();
								shopsForDelite.add(firstShop);
								shopsForAddNewNeedPall.add(newShopHasPall);
								firstShop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
								points1.add(targetStock);
								points1.add(firstShop);
								points1.add(targetStock);
								VehicleWay vehicleWay = new VehicleWay(points1, 0.0, 40, targetTruck);
								optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
								changeTruckHasSmall(vehicleWay, targetStock);
								whiteWay.add(vehicleWay);
								continue;
							}
							
						}
						if (firstShop.getNeedPall() > trucksBeforeRestr.get(0).getPall()) {
							if(firstShop.getWeight() <= trucksBeforeRestr.get(0).getWeigth()) {
								System.err.println("Распределяем магазин БЕЗ ограничений по весу, при том что паллет больше, после идеальных маршрутов (доп идеальные)!");
								Vehicle targetTruck = trucksBeforeRestr.remove(0);
								trucks.remove(targetTruck);
								targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
								Integer newNeedPallForShop = firstShop.getNeedPall() - targetTruck.getPall();
								Shop newShopHasPall = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
										firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
								newShopHasPall.setNeedPall(newNeedPallForShop);
								newShopHasPall.setDistanceFromStock(firstShop.getDistanceFromStock());
								double doubleWeigth = firstShop.getWeight() - oneWidthPall * targetTruck.getPall();
								newShopHasPall.setWeight((int) doubleWeigth);
								shopsForOptimization.remove(firstShop);
								shopsForOptimization.add(newShopHasPall);
								shopsForOptimization.sort(shopComparatorDistanceMain);
								Shop newFirstShop = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
										firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
								newFirstShop.setNeedPall(targetTruck.getPall());								
								points.remove(points.size()-1);
								points.add(newFirstShop);
								points.add(targetStock);
								VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
								optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
								changeTruckHasSmall(vehicleWay, targetStock);
								whiteWay.add(vehicleWay);
								if (!shopsForAddNewNeedPall.isEmpty()) {
									for (Shop shop : shopsForAddNewNeedPall) {
										if (!shopsForOptimization.contains(shop)) {
											shopsForOptimization.add(shop);
										}
									}
//									shopsForAddNewNeedPall.clear();
									shopsForOptimization.sort(shopComparatorDistanceMain); // сортируем обновлённый список
								}
								continue;
							}else {
								System.err.println("Распределяем магазин c ограничениями по весу, при том что паллет больше, после идеальных маршрутов (доп идеальные)!");
								Vehicle targetTruck = trucksBeforeRestr.remove(0);
								trucks.remove(targetTruck);
								int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
								targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
								Integer newNeedPallForShop = firstShop.getNeedPall() - newNeedPall;
								Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
								targetTruck.setTargetWeigth(finalWidthFOrTruck);
								Integer newNeedWeigthForShop = firstShop.getWeight() - finalWidthFOrTruck;
								Shop newShopHasPall = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
										firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
								newShopHasPall.setWeight(newNeedWeigthForShop);
								newShopHasPall.setNeedPall(newNeedPallForShop);
								newShopHasPall.setDistanceFromStock(firstShop.getDistanceFromStock());
								List<Shop> points1 = new ArrayList<Shop>();
								shopsForDelite.add(firstShop);
								shopsForAddNewNeedPall.add(newShopHasPall);
								firstShop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
								points1.add(targetStock);
								points1.add(firstShop);
								points1.add(targetStock);
								VehicleWay vehicleWay = new VehicleWay(points1, 0.0, 40, targetTruck);
								optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
								changeTruckHasSmall(vehicleWay, targetStock);
								whiteWay.add(vehicleWay);
								// после каждого цикла добавляем новые магазы, если есть
								if (!shopsForAddNewNeedPall.isEmpty()) {
									for (Shop shop : shopsForAddNewNeedPall) {
										if (!shopsForOptimization.contains(shop)) {
											shopsForOptimization.add(shop);
										}
									}
//									shopsForAddNewNeedPall.clear();
									shopsForOptimization.sort(shopComparatorDistanceMain); // сортируем обновлённый список
								}
								continue;
							}
						}
					}else { // тут мы знаем что паллет меньше. Далее проверям по весу
						double oneWidthPall = (double) (firstShop.getWeight()/firstShop.getNeedPall());
						double widthNewShop = oneWidthPall*trucksBeforeRestr.get(0).getPall();
						if(firstShop.getWeight() <= trucksBeforeRestr.get(0).getWeigth()) {
							System.err.println("Распределяем магазин БЕЗ ограничений по весу, при том что паллет МЕНЬШЕ чем может поместиться в одну машину, после идеальных маршрутов (доп идеальные)!");
							Vehicle targetTruck = trucksBeforeRestr.remove(0);
							trucks.remove(targetTruck);
							targetTruck.setTargetPall(firstShop.getNeedPall()); // загружаем машину полностью
							targetTruck.setTargetWeigth(firstShop.getWeight()); // загружаем машину по весу
							points.add(targetStock);
							VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
							shopsForOptimization.remove(firstShop);
							optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
							changeTruckHasSmall(vehicleWay, targetStock);
							whiteWay.add(vehicleWay);	
							continue;
						}else {
							System.err.println("Распределяем магазин c ограничениями по весу, при том что паллет МЕНЬШЕ чем может поместиться в одну машину, после идеальных маршрутов (доп идеальные)!");
							Vehicle targetTruck = trucksBeforeRestr.remove(0);
							trucks.remove(targetTruck);
							int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
							targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
							Integer newNeedPallForShop = firstShop.getNeedPall() - newNeedPall;
							Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
							targetTruck.setTargetWeigth(finalWidthFOrTruck);
							Integer newNeedWeigthForShop = firstShop.getWeight() - finalWidthFOrTruck;
							Shop newShopHasPall = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
									firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
							newShopHasPall.setWeight(newNeedWeigthForShop);
							newShopHasPall.setNeedPall(newNeedPallForShop);
							newShopHasPall.setDistanceFromStock(firstShop.getDistanceFromStock());
							List<Shop> points1 = new ArrayList<Shop>();
							shopsForDelite.add(firstShop);
							shopsForAddNewNeedPall.add(newShopHasPall);
							firstShop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
							points1.add(targetStock);
							points1.add(firstShop);
							points1.add(targetStock);
							VehicleWay vehicleWay = new VehicleWay(points1, 0.0, 40, targetTruck);
							optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
							changeTruckHasSmall(vehicleWay, targetStock);
							whiteWay.add(vehicleWay);
							// после каждого цикла добавляем новые магазы, если есть
							if (!shopsForAddNewNeedPall.isEmpty()) {
								for (Shop shop : shopsForAddNewNeedPall) {
									if (!shopsForOptimization.contains(shop)) {
										shopsForOptimization.add(shop);
									}
								}
//								shopsForAddNewNeedPall.clear();
								shopsForOptimization.sort(shopComparatorDistanceMain); // сортируем обновлённый список
							}
							continue;
						}
						
					}
					//остановился тут! сюда ни один из предыдущих методов не проходит ВРОДЕ ПРОБЛЕМА РЕШЕНА
					//обавляет в последние точки два склада, например 1700-1700
					
					radiusMap = getDistanceMatrixHasMin(shopsForOptimization, firstShop);
					stackTrace = stackTrace + "МАТРИЦА МАГАЗИНА " + firstShop.getNumshop() + "\n";
					for (Map.Entry<Double, Shop> entry : radiusMap.entrySet()) {
						stackTrace = stackTrace + entry.getKey() + " - " + entry.getValue().getNumshop() + "\n";
//						System.out.println(entry.getKey() + " - " + entry.getValue().getNumshop());
					}
					stackTrace = stackTrace + "==============\n";
					
					
					
					for (Map.Entry<Double, Shop> entry : radiusMap.entrySet()) {
						Shop shop2 = entry.getValue();
						if(shop2.getMaxPall() != null && shop2.getMaxPall() > pallRestriction) {
							//это условие говорит, что если ограничения второго магазина больше чем первого - магаз не рассматриваем
//							System.out.println("POINT 2");
							continue;
						}

						// тут добавляем мазаз в точку point
						points.add(shop2);
						points.add(targetStock);
						
						

						// проверяем является ли маршрут логичным!
						VehicleWay vehicleWayTest = new VehicleWay(points, 0.0, 30, null);

						Double logicResult = logicAnalyzer.logicalСheck(vehicleWayTest, koeff);
//						System.err.println(logicResult + " логичность маршрута составила");
						if (logicResult > 0) {
							// проверяем, в теории сможем ли мы положить в какую-нибудь машину такой маргрут
//							Integer summPallHasPoints  = 0;
							boolean flag = false;
							Integer summPallHasPoints = calcPallHashHsop(points, targetStock);
							// ВАЖНО! ТУТ ПРОХОДИМ ПО НОВОМУ СПИСКУ!
							for (Vehicle truck : trucksBeforeRestr) {
								if (truck.getPall() >= summPallHasPoints) {
									flag = true;
									break;
								}
							}

							if (flag) {
								shopsForDelite.add(shop2);
								points.remove(points.size() - 1);
//								System.out.println("кладём " + shop2);					
								continue;

							} else {
//								System.out.println("не кладём, т.к. нет такой большой машины " + shop2);
								points.remove(points.size() - 1);
								points.remove(points.size() - 1);
								continue;
							}

						} else {
//							System.out.println("не кладём, т.к. не логично " + shop2);
							points.remove(points.size() - 1);
							points.remove(points.size() - 1);
							continue;
						}
					}
					// создаём финальный, виртуальный маршрут
					points.add(targetStock);
					VehicleWay vehicleWayVirtual = new VehicleWay(points, 0.0, 30, null);

//					points.forEach(s -> System.out.println("Final -- "+s));
//					virtualWay.add(vehicleWayVirtual);
//					System.err.println(vehicleWayVirtual);
					
					// подбираем и ставим машину в виртуальный маршрут
					for (Vehicle truck : trucksBeforeRestr) { // если удалось собрать сразу всю машину
						Integer pallHasWay = calcPallHashHsop(vehicleWayVirtual.getWay(), targetStock);
						Integer weightHasWay= calcWeightHashHsop(vehicleWayVirtual.getWay(), targetStock);
						if (pallHasWay > truck.getPall()) {
							continue;
						} else if (truck.getPall() == pallHasWay) {
							if(weightHasWay <= truck.getWeigth()) {
								truck.setTargetPall(pallHasWay);
								vehicleWayVirtual.setVehicle(truck);
								vehicleWayVirtual.setStatus(35);
								vehicleForDelete.add(truck);
								break;
							}else {
								System.err.println("нужна машина побольше вер 1");
							}
							
						}
					}
					
					if (vehicleWayVirtual.getVehicle() == null) {
						// подбираем и ставим машину в виртуальный маршрут
						System.out.println(trucksBeforeRestr.size() + " <---------------------------");
						for (Vehicle truck : trucksBeforeRestr) { // недогруженная машина
							Integer pallHasWay = calcPallHashHsop(vehicleWayVirtual.getWay(), targetStock);
							Integer weightHasWay= calcWeightHashHsop(vehicleWayVirtual.getWay(), targetStock);
							System.out.print(pallHasWay + " pallHasWay ");
							System.out.println(weightHasWay + " weightHasWay ");
							if (truck.getPall() > pallHasWay) {
								if(weightHasWay <= truck.getWeigth()) {
									truck.setTargetPall(pallHasWay);
									vehicleWayVirtual.setVehicle(truck);
									vehicleWayVirtual.setStatus(30);
									vehicleForDelete.add(truck);
									break;
								}else {
									System.err.println("нужна машина побольше вер 2");
								}
								
							}
						}
					}
					

					// очищаем тачки
					if (!vehicleForDelete.isEmpty()) {
						for (Vehicle tr : vehicleForDelete) {
							trucks.remove(tr);
							trucksBeforeRestr.remove(tr);
						}
					}

					
					
					// блок комбинатрики

					// говорим, что если больше 4-х точек, то включаем блок
					if (vehicleWayVirtual.getWay().size() > 4) {
						List<Shop> newPoints = logicAnalyzer.correctRouteMaker(vehicleWayVirtual.getWay());
						vehicleWayVirtual.setWay(newPoints);
						stackTrace = stackTrace + "KOMBINATOR\n";

						int countShopHasOldWay = vehicleWayVirtual.getWay().size(); // колличество магазинов в базовом
																					// маршруте
						// мы говорим что нашли оптимальное количество магазинов, и единственный способ
						// сократить расстояние - брать часть магазинов и магрута (2 магаза заменить 1)
						Double maxDIstanceHasWay = 0.0;

						for (int n = 0; n < vehicleWayVirtual.getWay().size() - 1; n++) {
							String key = vehicleWayVirtual.getWay().get(n).getNumshop() + "-"
									+ vehicleWayVirtual.getWay().get(n + 1).getNumshop();
							maxDIstanceHasWay = maxDIstanceHasWay + matrixMachine.matrix.get(key);
						}

						stackTrace = stackTrace + "Первоначальный пробег маршрута = " + maxDIstanceHasWay + "\n";
//						vehicleWayVirtual.getWay().remove(vehicleWayVirtual.getWay().size()-1); // удаляем последний склад
//						vehicleWayVirtual.getWay().remove(0); // удаляем первый склад

						stackTrace = stackTrace + "Маршрут для рассмотрения\n";
						for (Shop shop : vehicleWayVirtual.getWay()) {
							stackTrace = stackTrace + shop.getNumshop() + " (" + shop.getNeedPall() + "); ->";
						}
						stackTrace = stackTrace + "\n";

						int pallForReplase = vehicleWayVirtual.getWay().get(1).getNeedPall()
								+ vehicleWayVirtual.getWay().get(2).getNeedPall();
						stackTrace = stackTrace + "Ищем один магазин с потребностью " + pallForReplase + " паллет\n";

						List<Shop> goodChoiceForWay = new ArrayList<Shop>();
						for (Shop shop : shopsForOptimization) {
							if(shop.getMaxPall() != null && shop.getMaxPall() > pallRestriction) {
								//это условие говорит, что если ограничения второго магазина больше чем первого - магаз не рассматриваем
								System.out.println("POINT 2");
								continue;
							}
							if (shop.getNeedPall() == pallForReplase && !vehicleWayVirtual.getWay().contains(shop)) {
								goodChoiceForWay.add(shop);
							}
						}

						stackTrace = stackTrace + "Подходящие магазы\n";
						for (Shop shop : goodChoiceForWay) {
							stackTrace = stackTrace + shop.getNumshop() + "\n";
						}
						stackTrace = stackTrace + "\n";

						if (!goodChoiceForWay.isEmpty()) {
							for (Shop shop : goodChoiceForWay) {
								Double distanceTest = 0.0;
								List<Shop> pointsTest = new ArrayList<Shop>();
								pointsTest.add(targetStock);
								pointsTest.add(shop);

								if (vehicleWayVirtual.getWay().size() == countShopHasOldWay) {
									for (int l = 3; l < vehicleWayVirtual.getWay().size(); l++) {
										pointsTest.add(vehicleWayVirtual.getWay().get(l));
									}
								} else {
									for (int l = 1; l < vehicleWayVirtual.getWay().size(); l++) {
										pointsTest.add(vehicleWayVirtual.getWay().get(l));
									}
								}

								if (pointsTest.get(pointsTest.size() - 1).getNumshop() != targetStock.getNumshop()) {
									pointsTest.add(targetStock);
								}

								// определяем дистанцию нового маршрута
								for (int q = 0; q < pointsTest.size() - 1; q++) {
									String key = pointsTest.get(q).getNumshop() + "-"
											+ pointsTest.get(q + 1).getNumshop();
//									System.out.println(key +"   "+ vehicleWayVirtual.getId());
									distanceTest = distanceTest + matrixMachine.matrix.get(key);
								}

								if (distanceTest < maxDIstanceHasWay && calcPallHashHsop(pointsTest,
										targetStock) <= vehicleWayVirtual.getVehicle().getPall()) {
									maxDIstanceHasWay = distanceTest;
									if (vehicleWayVirtual.getWay().size() == countShopHasOldWay) {
										Shop shop1 = vehicleWayVirtual.getWay().get(1);
										Shop shop2 = vehicleWayVirtual.getWay().get(2);

										shopsForDelite.remove(shop1);
										shopsForDelite.remove(shop2);

										if (!shopsForOptimization.contains(shop1)) {
											shopsForOptimization.add(shop1);
										}
										if (!shopsForOptimization.contains(shop2)) {
											shopsForOptimization.add(shop2);
										}

									} else {
										Shop shop2 = vehicleWayVirtual.getWay().get(1);
										shopsForDelite.remove(shop2);
										if (!shopsForOptimization.contains(shop2)) {
											shopsForOptimization.add(shop2);
										}
									}

									shopsForDelite.add(shop);
									vehicleWayVirtual.setWay(pointsTest);
									stackTrace = stackTrace + "Подбор показал сл результат: \n";
									stackTrace = stackTrace + "Новый маршрут: \n";
									for (Shop shop11 : pointsTest) {
										stackTrace = stackTrace + shop11.getNumshop() + " -> ";
									}
									stackTrace = stackTrace + "\n его пробег составляет " + distanceTest + " против "
											+ maxDIstanceHasWay + " \n";
								} else {
									stackTrace = stackTrace + "Магазин " + shop.getNumshop() + " не подходит т.к. ";
									stackTrace = stackTrace + distanceTest + " (пробег с новым магазином) > "
											+ maxDIstanceHasWay + " (дистанция виртуального маршрута)\n";
								}
							}

						} else {
							List<Shop> pointsOld = new ArrayList<Shop>();
//							pointsOld.add(targetStock);
							for (Shop shop : vehicleWayVirtual.getWay()) {
								pointsOld.add(shop);
							}
//							pointsOld.add(targetStock);
							vehicleWayVirtual.setWay(pointsOld);
						}
					}
					
					// блок оптимизации виртуальных маршрутов по последним точкам
					optimizationOfVirtualRoutesType1(vehicleWayVirtual, pallRestriction, targetStock);
					changeTruckHasSmall(vehicleWayVirtual, targetStock);

					whiteWay.add(vehicleWayVirtual);
				} else { // блок где нет ограничений! остановился тут!
					System.err.println("Большой блок распределения БЕЗ ограничений подъездов");
					
					// создаём матрицу расстояний от первого магазина
					Map<Double, Shop> radiusMap = new TreeMap<Double, Shop>();
					radiusMap = getDistanceMatrixHasMin(shopsForOptimization, firstShop);

					// создаём порядок точек
					List<Shop> points = new ArrayList<Shop>();
					points.add(targetStock);
					points.add(firstShop);
					
					if(firstShop.getNeedPall() >= trucks.get(0).getPall()) {//логика для разбиений магазинов когда паллет изначально больше!
						double oneWidthPall = (double) (firstShop.getWeight()/firstShop.getNeedPall());
						double widthNewShop = oneWidthPall*trucks.get(0).getPall();
						if (firstShop.getNeedPall() == trucks.get(0).getPall()) { 
							if(firstShop.getWeight() <= trucks.get(0).getWeigth()) {
								System.err.println("Распределяем магазин БЕЗ ограничений по весу, в блоке распределения БЕЗ ограничений подъездов, при том что паллет больше, после идеальных маршрутов (доп идеальные), когда паллет изначально больше!");
								Vehicle targetTruck = trucks.remove(0);
								targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
								targetTruck.setTargetWeigth(firstShop.getWeight()); // загружаем машину по весу
								points.add(targetStock);
								VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
								shopsForOptimization.remove(firstShop);
								optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
								changeTruckHasSmall(vehicleWay, targetStock);
								whiteWay.add(vehicleWay);							
								continue;
							}else {
								System.err.println("Распределяем магазин c ограничениями по весу, в блоке распределения БЕЗ ограничений подъездов, при том что паллеты входят ровно, после идеальных маршрутов (доп идеальные)!");
								Vehicle targetTruck = trucks.remove(0);
								int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
								targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
								Integer newNeedPallForShop = firstShop.getNeedPall() - newNeedPall;
								Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
								targetTruck.setTargetWeigth(finalWidthFOrTruck);
								Integer newNeedWeigthForShop = firstShop.getWeight() - finalWidthFOrTruck;
								Shop newShopHasPall = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
										firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
								newShopHasPall.setWeight(newNeedWeigthForShop);
								newShopHasPall.setNeedWeigth(Double.parseDouble(newNeedWeigthForShop.toString()));
								newShopHasPall.setNeedPall(newNeedPallForShop);
								newShopHasPall.setDistanceFromStock(firstShop.getDistanceFromStock());
								List<Shop> points1 = new ArrayList<Shop>();
								shopsForDelite.add(firstShop);
								shopsForAddNewNeedPall.add(newShopHasPall);
								firstShop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
								points1.add(targetStock);
								points1.add(firstShop);
								points1.add(targetStock);
								VehicleWay vehicleWay = new VehicleWay(points1, 0.0, 40, targetTruck);
								optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
								changeTruckHasSmall(vehicleWay, targetStock);
								whiteWay.add(vehicleWay);
								continue;
							}
							
						}
						if (firstShop.getNeedPall() > trucks.get(0).getPall()) {
							if(firstShop.getWeight() <= trucks.get(0).getWeigth()) {
								System.err.println("Распределяем магазин БЕЗ ограничений по весу, в блоке распределения БЕЗ ограничений подъездов, при том что паллет больше, после идеальных маршрутов (доп идеальные)!");
								Vehicle targetTruck = trucks.remove(0);
								targetTruck.setTargetPall(targetTruck.getPall()); // загружаем машину полностью
								Integer newNeedPallForShop = firstShop.getNeedPall() - targetTruck.getPall();
								Shop newShopHasPall = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
										firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
								newShopHasPall.setNeedPall(newNeedPallForShop);
								newShopHasPall.setDistanceFromStock(firstShop.getDistanceFromStock());
								double doubleWeigth = firstShop.getWeight() - oneWidthPall * targetTruck.getPall();
								newShopHasPall.setWeight((int) doubleWeigth);
								shopsForOptimization.remove(firstShop);
								shopsForOptimization.add(newShopHasPall);
								shopsForOptimization.sort(shopComparatorDistanceMain);
								Shop newFirstShop = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
										firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
								newFirstShop.setNeedPall(targetTruck.getPall());								
								points.remove(points.size()-1);
								points.add(newFirstShop);
								points.add(targetStock);
								VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
								optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
								changeTruckHasSmall(vehicleWay, targetStock);
								whiteWay.add(vehicleWay);
								if (!shopsForAddNewNeedPall.isEmpty()) {
									for (Shop shop : shopsForAddNewNeedPall) {
										if (!shopsForOptimization.contains(shop)) {
											shopsForOptimization.add(shop);
										}
									}
//									shopsForAddNewNeedPall.clear();
									shopsForOptimization.sort(shopComparatorDistanceMain); // сортируем обновлённый список
								}
								continue;
							}else {
								System.err.println("Распределяем магазин c ограничениями по весу, в блоке распределения БЕЗ ограничений подъездов, при том что паллет больше, после идеальных маршрутов (доп идеальные)!");
								Vehicle targetTruck = trucks.remove(0);
								int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
								targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
								Integer newNeedPallForShop = firstShop.getNeedPall() - newNeedPall;
								Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
								targetTruck.setTargetWeigth(finalWidthFOrTruck);
								Integer newNeedWeigthForShop = firstShop.getWeight() - finalWidthFOrTruck;
								Shop newShopHasPall = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
										firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
								newShopHasPall.setWeight(newNeedWeigthForShop);
								newShopHasPall.setNeedPall(newNeedPallForShop);
								newShopHasPall.setDistanceFromStock(firstShop.getDistanceFromStock());
								List<Shop> points1 = new ArrayList<Shop>();
								shopsForDelite.add(firstShop);
								shopsForAddNewNeedPall.add(newShopHasPall);
								firstShop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
								points1.add(targetStock);
								points1.add(firstShop);
								points1.add(targetStock);
								VehicleWay vehicleWay = new VehicleWay(points1, 0.0, 40, targetTruck);
								optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
								changeTruckHasSmall(vehicleWay, targetStock);
								whiteWay.add(vehicleWay);
								// после каждого цикла добавляем новые магазы, если есть
								if (!shopsForAddNewNeedPall.isEmpty()) {
									for (Shop shop : shopsForAddNewNeedPall) {
										if (!shopsForOptimization.contains(shop)) {
											shopsForOptimization.add(shop);
										}
									}
//									shopsForAddNewNeedPall.clear();
									shopsForOptimization.sort(shopComparatorDistanceMain); // сортируем обновлённый список
								}
								continue;
							}
						}
					}else { // тут мы знаем что паллет меньше. Далее проверям по весу
						System.out.println(firstShop.toAllString());
						double oneWidthPall = (double) (firstShop.getWeight()/firstShop.getNeedPall());
						double widthNewShop = oneWidthPall*trucks.get(0).getPall();
						if(firstShop.getWeight() > trucks.get(0).getWeigth()) {
							System.err.println("Распределяем магазин c ограничениями по весу, в блоке распределения БЕЗ ограничений подъездов, при том что паллет МЕНЬШЕ чем может поместиться в одну машину, после идеальных маршрутов (доп идеальные)!");
							Vehicle targetTruck = trucks.remove(0);
							int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
							targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
							Integer newNeedPallForShop = firstShop.getNeedPall() - newNeedPall;
							Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
							targetTruck.setTargetWeigth(finalWidthFOrTruck);
							Integer newNeedWeigthForShop = firstShop.getWeight() - finalWidthFOrTruck;
							Shop newShopHasPall = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
									firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
							newShopHasPall.setWeight(newNeedWeigthForShop);
							newShopHasPall.setNeedPall(newNeedPallForShop);
							newShopHasPall.setDistanceFromStock(firstShop.getDistanceFromStock());
							List<Shop> points1 = new ArrayList<Shop>();
							shopsForDelite.add(firstShop);
							shopsForAddNewNeedPall.add(newShopHasPall);
							firstShop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
							points1.add(targetStock);
							points1.add(firstShop);
							points1.add(targetStock);
							VehicleWay vehicleWay = new VehicleWay(points1, 0.0, 40, targetTruck);
							optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
							changeTruckHasSmall(vehicleWay, targetStock);
							whiteWay.add(vehicleWay);
							// после каждого цикла добавляем новые магазы, если есть
							if (!shopsForAddNewNeedPall.isEmpty()) {
								for (Shop shop : shopsForAddNewNeedPall) {
									if (!shopsForOptimization.contains(shop)) {
										shopsForOptimization.add(shop);
									}
								}
//								shopsForAddNewNeedPall.clear();
								shopsForOptimization.sort(shopComparatorDistanceMain); // сортируем обновлённый список
							}
							continue;
						}
//						if(firstShop.getWeight() <= trucks.get(0).getWeigth()) {
//							System.err.println("Распределяем магазин БЕЗ ограничений по весу, в блоке распределения БЕЗ ограничений подъездов, при том что паллет МЕНЬШЕ чем может поместиться в одну машину, после идеальных маршрутов (доп идеальные)!");
//							Vehicle targetTruck = trucks.remove(0);
//							targetTruck.setTargetPall(firstShop.getNeedPall()); // загружаем машину полностью
//							targetTruck.setTargetWeigth(firstShop.getWeight()); // загружаем машину по весу
//							points.add(targetStock);
//							VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
//							shopsForOptimization.remove(firstShop);
//							optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
//							changeTruckHasSmall(vehicleWay, targetStock);
//							whiteWay.add(vehicleWay);	
//							continue;
//						}else {
//							System.err.println("Распределяем магазин c ограничениями по весу, в блоке распределения БЕЗ ограничений подъездов, при том что паллет МЕНЬШЕ чем может поместиться в одну машину, после идеальных маршрутов (доп идеальные)!");
//							Vehicle targetTruck = trucks.remove(0);
//							int newNeedPall = targetTruck.getWeigth()/(int) oneWidthPall; // определяем сколько паллет (по ср. весу) поместится в эту машину
//							targetTruck.setTargetPall(newNeedPall); // загружаем этим колличеством паллет
//							Integer newNeedPallForShop = firstShop.getNeedPall() - newNeedPall;
//							Integer finalWidthFOrTruck = (int) ((int) newNeedPall*oneWidthPall);
//							targetTruck.setTargetWeigth(finalWidthFOrTruck);
//							Integer newNeedWeigthForShop = firstShop.getWeight() - finalWidthFOrTruck;
//							Shop newShopHasPall = new Shop(firstShop.getNumshop(), firstShop.getAddress(), firstShop.getLat(),
//									firstShop.getLng(), firstShop.getLength(), firstShop.getWidth(), firstShop.getHeight(), firstShop.getMaxPall());
//							newShopHasPall.setWeight(newNeedWeigthForShop);
//							newShopHasPall.setNeedPall(newNeedPallForShop);
//							newShopHasPall.setDistanceFromStock(firstShop.getDistanceFromStock());
//							List<Shop> points1 = new ArrayList<Shop>();
//							shopsForDelite.add(firstShop);
//							shopsForAddNewNeedPall.add(newShopHasPall);
//							firstShop.setNeedPall(targetTruck.getTargetPall()); // указываем текущую потребность магазина для этой фуры
//							points1.add(targetStock);
//							points1.add(firstShop);
//							points1.add(targetStock);
//							VehicleWay vehicleWay = new VehicleWay(points1, 0.0, 40, targetTruck);
//							optimizationOfVirtualRoutesType1(vehicleWay, pallRestriction, targetStock);
//							changeTruckHasSmall(vehicleWay, targetStock);
//							whiteWay.add(vehicleWay);
//							// после каждого цикла добавляем новые магазы, если есть
//							if (!shopsForAddNewNeedPall.isEmpty()) {
//								for (Shop shop : shopsForAddNewNeedPall) {
//									if (!shopsForOptimization.contains(shop)) {
//										shopsForOptimization.add(shop);
//									}
//								}
////								shopsForAddNewNeedPall.clear();
//								shopsForOptimization.sort(shopComparatorDistanceMain); // сортируем обновлённый список
//							}
//							continue;
//						}
						
					}

					stackTrace = stackTrace + "МАТРИЦА МАГАЗИНА " + firstShop.getNumshop() + "\n";
					for (Map.Entry<Double, Shop> entry : radiusMap.entrySet()) {
						stackTrace = stackTrace + entry.getKey() + " - " + entry.getValue().getNumshop() + "\n";
					}
					stackTrace = stackTrace + "==============\n";

					for (Map.Entry<Double, Shop> entry : radiusMap.entrySet()) {
						Shop shop2 = entry.getValue();
						if(firstShop.getMaxPall() == null && shop2.getMaxPall() != null) {
							//это условие говорит, что если первый магазин без ограничений, то в машину ограничения вообще не вставляются.
							System.out.println("POINT 1");
							continue;
						}

						// тут добавляем мазаз в точку point
						points.add(shop2);
						points.add(targetStock);

						// проверяем является ли маршрут логичным!
						VehicleWay vehicleWayTest = vehicleWayTest = new VehicleWay(points, 0.0, 30, null);

						Double logicResult = logicAnalyzer.logicalСheck(vehicleWayTest, koeff);
//						System.err.println(logicResult + " логичность маршрута составила");
						if (logicResult > 0) {
							// проверяем, в теории сможем ли мы положить в какую-нибудь машину такой маргрут
//							Integer summPallHasPoints  = 0;
							boolean flag = false;
							Integer summPallHasPoints = calcPallHashHsop(points, targetStock);
							// ВАЖНО! ТУТ ПРОХОДИМ ПО НОВОМУ СПИСКУ!
							for (Vehicle truck : trucks) {
								if (truck.getPall() >= summPallHasPoints) {
									flag = true;
									break;
								}
							}

							if (flag) {
								shopsForDelite.add(shop2);
								points.remove(points.size() - 1);
//								System.out.println("кладём " + shop2);					
								continue;

							} else {
//								System.out.println("не кладём, т.к. нет такой большой машины " + shop2);
								points.remove(points.size() - 1);
								points.remove(points.size() - 1);
								continue;
							}

						} else {
//							System.out.println("не кладём, т.к. не логично " + shop2);
							points.remove(points.size() - 1);
							points.remove(points.size() - 1);
							continue;
						}
					}
					// создаём финальный, виртуальный маршрут
					points.add(targetStock);
					VehicleWay vehicleWayVirtual = new VehicleWay(points, 0.0, 30, null);

//					points.forEach(s -> System.out.println("Final -- "+s));
//					virtualWay.add(vehicleWayVirtual);
//					System.err.println(vehicleWayVirtual);

					// подбираем и ставим машину в виртуальный маршрут
					for (Vehicle truck : trucks) { // если удалось собрать сразу всю машину
						Integer pallHasWay = calcPallHashHsop(vehicleWayVirtual.getWay(), targetStock);
						Integer weightHasWay= calcWeightHashHsop(vehicleWayVirtual.getWay(), targetStock);
						if (pallHasWay > truck.getPall()) {
							continue;
						} else if (truck.getPall() == pallHasWay) {
							if(weightHasWay <= truck.getWeigth()) {
								truck.setTargetPall(pallHasWay);
								truck.setTargetWeigth(weightHasWay);
								vehicleWayVirtual.setVehicle(truck);
								vehicleWayVirtual.setStatus(35);
								vehicleForDelete.add(truck);
								break;
							}else {
								System.err.println("нужна машина побольше вер 3");
							}
							
						}
					}
					
					if (vehicleWayVirtual.getVehicle() == null) {
						// подбираем и ставим машину в виртуальный маршрут
						for (Vehicle truck : trucks) { // недогруженная машина
							Integer pallHasWay = calcPallHashHsop(vehicleWayVirtual.getWay(), targetStock);
							Integer weightHasWay= calcWeightHashHsop(vehicleWayVirtual.getWay(), targetStock);
							if (truck.getPall() > pallHasWay) {
								if(weightHasWay <= truck.getWeigth()) {
									truck.setTargetPall(pallHasWay);
									truck.setTargetWeigth(weightHasWay);
									vehicleWayVirtual.setVehicle(truck);
									vehicleWayVirtual.setStatus(30);
									vehicleForDelete.add(truck);
									break;
								}else {
									System.err.println("нужна машина побольше вер 4");
								}
								
							}
						}
					}

					// очищаем тачки
					if (!vehicleForDelete.isEmpty()) {
						for (Vehicle tr : vehicleForDelete) {
							trucks.remove(tr);
						}
					}
					
					// блок комбинатрики

					// говорим, что если больше 4-х точек, то включаем блок
					if (vehicleWayVirtual.getWay().size() > 4) {
						List<Shop> newPoints = logicAnalyzer.correctRouteMaker(vehicleWayVirtual.getWay());
						vehicleWayVirtual.setWay(newPoints);
						stackTrace = stackTrace + "KOMBINATOR\n";

						int countShopHasOldWay = vehicleWayVirtual.getWay().size(); // колличество магазинов в базовом
																					// маршруте
						// мы говорим что нашли оптимальное количество магазинов, и единственный способ
						// сократить расстояние - брать часть магазинов и магрута (2 магаза заменить 1)
						Double maxDIstanceHasWay = 0.0;

						for (int n = 0; n < vehicleWayVirtual.getWay().size() - 1; n++) {
							String key = vehicleWayVirtual.getWay().get(n).getNumshop() + "-"
									+ vehicleWayVirtual.getWay().get(n + 1).getNumshop();
							maxDIstanceHasWay = maxDIstanceHasWay + matrixMachine.matrix.get(key);
						}

						stackTrace = stackTrace + "Первоначальный пробеег маршрута = " + maxDIstanceHasWay + "\n";
//						vehicleWayVirtual.getWay().remove(vehicleWayVirtual.getWay().size()-1); // удаляем последний склад
//						vehicleWayVirtual.getWay().remove(0); // удаляем первый склад

						stackTrace = stackTrace + "Маршрут для рассмотрения\n";
						for (Shop shop : vehicleWayVirtual.getWay()) {
							stackTrace = stackTrace + shop.getNumshop() + " (" + shop.getNeedPall() + "); ->";
						}
						stackTrace = stackTrace + "\n";

						int pallForReplase = vehicleWayVirtual.getWay().get(1).getNeedPall()
								+ vehicleWayVirtual.getWay().get(2).getNeedPall();
						stackTrace = stackTrace + "Ищем один магазин с потребностью " + pallForReplase + " паллет\n";

						List<Shop> goodChoiceForWay = new ArrayList<Shop>();
						for (Shop shop : shopsForOptimization) {
							if(firstShop.getMaxPall() == null && shop.getMaxPall() != null) {
								//это условие говорит, что если первый магазин без ограничений, то в машину ограничения вообще не вставляются.
								System.out.println("POINT 1");
								continue;
							}
							if (shop.getNeedPall() == pallForReplase && !vehicleWayVirtual.getWay().contains(shop)) {
								goodChoiceForWay.add(shop);
							}
						}

						stackTrace = stackTrace + "Подходящие магазы\n";
						for (Shop shop : goodChoiceForWay) {
							stackTrace = stackTrace + shop.getNumshop() + "\n";
						}
						stackTrace = stackTrace + "\n";

						if (!goodChoiceForWay.isEmpty()) {
							for (Shop shop : goodChoiceForWay) {
								Double distanceTest = 0.0;
								List<Shop> pointsTest = new ArrayList<Shop>();
								pointsTest.add(targetStock);
								pointsTest.add(shop);

								if (vehicleWayVirtual.getWay().size() == countShopHasOldWay) {
									for (int l = 3; l < vehicleWayVirtual.getWay().size(); l++) {
										pointsTest.add(vehicleWayVirtual.getWay().get(l));
									}
								} else {
									for (int l = 1; l < vehicleWayVirtual.getWay().size(); l++) {
										pointsTest.add(vehicleWayVirtual.getWay().get(l));
									}
								}

								if (pointsTest.get(pointsTest.size() - 1).getNumshop() != targetStock.getNumshop()) {
									pointsTest.add(targetStock);
								}

								// определяем дистанцию нового маршрута
								for (int q = 0; q < pointsTest.size() - 1; q++) {
									String key = pointsTest.get(q).getNumshop() + "-"
											+ pointsTest.get(q + 1).getNumshop();
//									System.out.println(key +"   "+ vehicleWayVirtual.getId());
									distanceTest = distanceTest + matrixMachine.matrix.get(key);
								}

								if (distanceTest < maxDIstanceHasWay && calcPallHashHsop(pointsTest,
										targetStock) <= vehicleWayVirtual.getVehicle().getPall()) {
									maxDIstanceHasWay = distanceTest;
									if (vehicleWayVirtual.getWay().size() == countShopHasOldWay) {
										Shop shop1 = vehicleWayVirtual.getWay().get(1);
										Shop shop2 = vehicleWayVirtual.getWay().get(2);

										shopsForDelite.remove(shop1);
										shopsForDelite.remove(shop2);

										if (!shopsForOptimization.contains(shop1)) {
											shopsForOptimization.add(shop1);
										}
										if (!shopsForOptimization.contains(shop2)) {
											shopsForOptimization.add(shop2);
										}

									} else {
										Shop shop2 = vehicleWayVirtual.getWay().get(1);
										shopsForDelite.remove(shop2);
										if (!shopsForOptimization.contains(shop2)) {
											shopsForOptimization.add(shop2);
										}
									}

									shopsForDelite.add(shop);
									vehicleWayVirtual.setWay(pointsTest);
									stackTrace = stackTrace + "Подбор показал сл результат: \n";
									stackTrace = stackTrace + "Новый маршрут: \n";
									for (Shop shop11 : pointsTest) {
										stackTrace = stackTrace + shop11.getNumshop() + " -> ";
									}
									stackTrace = stackTrace + "\n его пробег составляет " + distanceTest + " против "
											+ maxDIstanceHasWay + " \n";
								} else {
									stackTrace = stackTrace + "Магазин " + shop.getNumshop() + " не подходит т.к. ";
									stackTrace = stackTrace + distanceTest + " (пробег с новым магазином) > "
											+ maxDIstanceHasWay + " (дистанция виртуального маршрута)\n";
								}
							}

						} else {
							List<Shop> pointsOld = new ArrayList<Shop>();
//							pointsOld.add(targetStock);
							for (Shop shop : vehicleWayVirtual.getWay()) {
								pointsOld.add(shop);
							}
//							pointsOld.add(targetStock);
							vehicleWayVirtual.setWay(pointsOld);
						}
					}
					
					// блок оптимизации виртуальных маршрутов
					// блок оптимизации виртуальных маршрутов по последним точкам
					optimizationOfVirtualRoutesType1(vehicleWayVirtual, pallRestriction, targetStock);
					changeTruckHasSmall(vehicleWayVirtual, targetStock);

					whiteWay.add(vehicleWayVirtual);

				}
			}

			// после каждого цикла очищаем магазы
			if (!shopsForDelite.isEmpty()) {
				for (Shop shop : shopsForDelite) {
					shopsForOptimization.remove(shop);
				}
//				shopsForDelite.clear();
				shopsForOptimization.sort(shopComparatorDistanceMain); // сортируем обновлённый список
			}

			// после каждого цикла добавляем новые магазы, если есть
			if (!shopsForAddNewNeedPall.isEmpty()) {
				for (Shop shop : shopsForAddNewNeedPall) {
					if (!shopsForOptimization.contains(shop)) {
						shopsForOptimization.add(shop);
					}
				}
//				shopsForAddNewNeedPall.clear();
				shopsForOptimization.sort(shopComparatorDistanceMain); // сортируем обновлённый список
			}

			// после каждого цикла удаляем загруженные авто
			if (!vehicleForDelete.isEmpty()) {
				for (Vehicle truck : vehicleForDelete) {
					trucks.remove(truck);
				}
			}
			i++;
		} // КОНЕЦ ОСНОВНОГО ЦИКЛА ДЛЯ ПУНКТОВ 1 И 2


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
	 * Блок, отвечающий за замену машины, когда маршрут окончательно построен. Должен быть всегда самым последним!
	 * @param vehicleWayVirtual
	 */
	private void changeTruckHasSmall(VehicleWay vehicleWayVirtual, Shop targetStock) {		
		System.out.println("-->>> changeTruckHasSmall START : " + vehicleWayVirtual);
		if (!vehicleWayVirtual.getVehicle().isFull()) {
			// проверяем, есть ли, гипотетически меньшая машина, для того чтобы сохранить
			// большую
			Integer pallHasWay = calcPallHashHsop(vehicleWayVirtual.getWay(), targetStock);
			Vehicle oldTruck = vehicleWayVirtual.getVehicle();
			Integer pallHasOldTruck = vehicleWayVirtual.getVehicle().getPall();
			trucks.sort(vehicleComparatorFromMin);
			for (Vehicle truck : trucks) {
				System.err.println(truck + " <----> " + oldTruck);
				if(truck.getWeigth() >= oldTruck.getTargetWeigth()) {
					if (truck.getPall() >= oldTruck.getTargetPall() && truck.getPall() < pallHasOldTruck) {
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
	private void optimizationOfVirtualRoutesType1(VehicleWay vehicleWayVirtual, Integer pallRestriction, Shop targetStock) {		
		if (!vehicleWayVirtual.getVehicle().isFull()) {
			// если тачка недогружена, то возможны 2 варианта:
			// берем последнюю точку, прокладываем радиус 10 км, убераем её и ищем магаз
			// (магазы) которые могут догрузить машину
			// второй варик тот же самый, но без убирание точки
			List<Shop> newPoints = logicAnalyzer.correctRouteMaker(vehicleWayVirtual.getWay());
			Shop lastShop = newPoints.get(newPoints.size() - 2);
			List<Shop> processWay = new ArrayList<Shop>(newPoints);
			int size = processWay.size();

			Map<Double, Shop> radiusFromLastShop = getDistanceMatrixHasMinLimitParameter(shopsForOptimization, shopsForDelite, lastShop, 10000.0);
			for (Map.Entry<Double, Shop> e : radiusFromLastShop.entrySet()) {
				if(e.getValue().getMaxPall() != null && e.getValue().getMaxPall() > pallRestriction) {
					//это условие говорит, что если ограничения второго магазина больше чем первого - магаз не рассматриваем
					System.out.println("POINT 2");
					continue;
				}
				if (e.getValue().getNeedPall() <= vehicleWayVirtual.getFreePallInVehicle()) {
					if (e.getValue().getNumshop() != lastShop.getNumshop() && !shopsForDelite.contains(e.getValue())) {
						processWay.remove(size - 1);
						processWay.add(e.getValue());
						processWay.add(targetStock);
						Vehicle truck = vehicleWayVirtual.getVehicle();
						truck.setTargetPall(calcPallHashHsop(processWay, targetStock));
						truck.setTargetWeigth(truck.getTargetWeigth() + e.getValue().getWeight());
						vehicleWayVirtual.setVehicle(truck);
						vehicleWayVirtual.setWay(processWay);			
						shopsForDelite.add(e.getValue());
						shopsForOptimization.remove(e.getValue());
						size = processWay.size();
						System.err.println(	"~~~~~~~~~~~~~~~~~ БЛОК догруза машины по последней точке СРАБОТАЛ ~~~~~~~~~~~~~~~~~~~");
//						break;
					}
				}
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
	 */
	private void createIdealWay(Shop shop, Shop targetStock) {
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
			VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
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
			VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
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
				points.add(targetStock);
				points.add(shop);
				points.add(targetStock);
				VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
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
				points.add(targetStock);
				points.add(shop);
				points.add(targetStock);
				VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
				changeTruckHasSmall(vehicleWay, targetStock);
				whiteWay.add(vehicleWay);
			}
		}
	}
	
	private void createIdealWayAndPallRestriction (Shop shop, Shop targetStock) {
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
			VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
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
			VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
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
				VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
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
				VehicleWay vehicleWay = new VehicleWay(points, 0.0, 40, targetTruck);
				changeTruckHasSmall(vehicleWay, targetStock);
				whiteWay.add(vehicleWay);
			}						
		}
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
