package by.base.main.util.hcolossus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListener;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.problem.Capacity;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Activity;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.DeliveryActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.Start;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity.JobActivity;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;

import by.base.main.model.Shop;
import by.base.main.service.ShopService;
import by.base.main.util.hcolossus.exceptions.FatalInsufficientPalletTruckCapacityException;
import by.base.main.util.hcolossus.exceptions.InsufficientPalletTruckCapacityException;
import by.base.main.util.hcolossus.pojo.Solution;
import by.base.main.util.hcolossus.pojo.MyVehicle;
import by.base.main.util.hcolossus.pojo.VehicleWay;
import by.base.main.util.hcolossus.service.ComparatorShops;
import by.base.main.util.hcolossus.service.ComparatorShopsDistanceMain;
import by.base.main.util.hcolossus.service.ComparatorShopsWhithRestrict;
import by.base.main.util.hcolossus.service.LogicAnalyzer;
import by.base.main.util.hcolossus.service.MatrixMachine;
import by.base.main.util.hcolossus.service.ShopMachine;
import by.base.main.util.hcolossus.service.VehicleMachine;

/**
 * Самый основной оптимизатор! БАЗА! Изменение запрещено! Прямое продолжение 3
 * -й версии Особенность метода в том, что он принимает еще потребность магазина
 * по вывозу паллет и подбирает машину с учётом этих паллет. Этот метод
 * полностью не протестирован. Главное - не реализована ситуация, когда
 * несколько потребностей попадает в машину (а должна ли?!) Прямое продолжение 4
 * -й версии Особенность этого метода в том, что он оценивает оставшиеся машины
 * (суммарно паллеты) с суммой паллет потребностей магазинов. Если меньше -
 * останавливает итерацию на текущей трубе и переходит к следующей. Важно: метод
 * генерит исключения! Все сообщения будут передаваться через исключения
 */
@Component
public class ColossusProcessorJSpirit {

	private JSONObject jsonMainObject;

	@Autowired
	private VehicleMachine vehicleMachine;

	@Autowired
	private ShopMachine shopMachine;

	@Autowired
	private MatrixMachine matrixMachine;

	@Autowired
	private LogicAnalyzer logicAnalyzer;

//	private Comparator<Shop> shopComparatorPallOnly = (o1, o2) -> (o2.getNeedPall() - o1.getNeedPall()); // сортирует от большей потребности к меньшей
	private Comparator<Shop> shopComparatorPallOnly = (o1, o2) -> Double.compare(o2.getNeedPall(), o1.getNeedPall());// сортирует
																														// от
																														// большей
																														// потребности
																														// к
																														// меньшей.
																														// Переделка
																														// прошлго
																														// метода
																														// под
																														// double

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

	private Comparator<MyVehicle> vehicleComparatorFromMax = (o1, o2) -> { // этот метод сортирует от большей
																			// потребности к меньшей без учёта веса и
																			// отправляет вниз списка машины помоченные
																			// как КЛОНЫ!
		// Сначала проверяем isTwiceRound() и перемещаем такие элементы вниз всего
		// списка
		if (o1.isClone() && !o2.isClone()) {
			return 1; // o1 опускаем вниз
		} else if (!o1.isClone() && o2.isClone()) {
			return -1; // o2 опускаем вниз
		}

		// Если оба элемента имеют одинаковый статус isTwiceRound, сортируем по pall
		return Double.compare(o2.getPall(), o1.getPall());
	};

//	private Comparator<Vehicle> vehicleComparatorFromMin = (o1, o2) -> (o1.getPall() - o2.getPall()); // сортирует от меньшей потребности к большей без учёта веса
	private Comparator<MyVehicle> vehicleComparatorFromMin = (o1, o2) -> Double.compare(o1.getPall(), o2.getPall());// сортирует
																													// от
																													// меньшей
																													// потребности
																													// к
																													// большей
																													// без
																													// учёта
																													// веса
																													// Переделка
																													// прошлго
																													// метода
																													// под
																													// double
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
	private List<MyVehicle> trucks;
	/**
	 * Список тачек, которые подходят для вывоза товара из магазина
	 */
	private List<MyVehicle> trucksForShopReturn;
	private List<MyVehicle> vehicleForDelete;
	private String stackTrace;
	private List<VehicleWay> whiteWay;

	private Double maxDistanceInRoute = 100000.0;
	private Double minimumPercentageOfCarFilling = 95.0;

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
	public Solution run(JSONObject jsonMainObject, List<Integer> shopList, List<Double> pallHasShops,
			List<Integer> tonnageHasShops, Integer stock, Double koeff, String algoritm,
			Map<Integer, String> shopsWithCrossDockingMap, Integer maxShopInWay, List<Double> pallReturn,
			List<Integer> weightDistributionList, Map<Integer, Shop> allShop) throws Exception {
		Logger.getLogger("jsprit").setLevel(Level.FINE);
		// блок подготовки
		// заполняем static матрицу. Пусть хранится там
		matrixMachine.createMatrixHasList(shopList, stock, allShop);

		// 1. Получаем targetStock и готовим данные
		Shop targetStock = allShop.get(stock);
		List<Shop> shops = new ArrayList<>(allShop.values());
		List<Shop> shopsForOptimization = shopMachine.prepareShopList5Parameters(shopList, pallHasShops,
				tonnageHasShops, stock, shopsWithCrossDockingMap, pallReturn, weightDistributionList, shops);
		Location depotLocation = Location.newInstance(Double.parseDouble(targetStock.getLat()),
				Double.parseDouble(targetStock.getLng()));
		List<Shipment> shipments = createShipmentsFromShops(shopsForOptimization, depotLocation);

		// 1. Создаём маппинг координат → ID
		Map<Location, Integer> coordinateToId = new HashMap<>();

		// Для депо
		Location depotCoord = Location.newInstance(53.892622, 27.345616);
		coordinateToId.put(depotCoord, stock); // stock = ID депо

		// Для магазинов
		for (Shipment shipment : shipments) {
			Location shopCoord = shipment.getDeliveryLocation();
			Integer shopId = Integer.parseInt(shipment.getName());
			coordinateToId.put(shopCoord, shopId);
		}

		// 2. При создании матрицы используем координаты
		VehicleRoutingTransportCostsMatrix.Builder matrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
				.newInstance(true);

		for (Location fromCoord : coordinateToId.keySet()) {
			for (Location toCoord : coordinateToId.keySet()) {
				Integer fromId = coordinateToId.get(fromCoord);
				Integer toId = coordinateToId.get(toCoord);

				String matrixKey = fromId + "-" + toId;
				double distance = matrixMachine.matrix.getOrDefault(matrixKey, Double.POSITIVE_INFINITY);

				matrixBuilder.addTransportDistance(fromCoord.getCoordinate() + "", // Важно! Передаём координаты
						toCoord.getCoordinate() + "", distance / 1000.0);
			}
		}

		VehicleRoutingTransportCosts costMatrix = matrixBuilder.build();

		List<VehicleImpl> vehicleImpls = createVehiclesFromJson(jsonMainObject, depotLocation);

		// 1. Создаём объект задачи VRP
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
		vrpBuilder.setRoutingCost(costMatrix); // Указываем кастомную матрицу

		// 2. Добавляем все машины
		for (VehicleImpl vehicle : vehicleImpls) {
			vrpBuilder.addVehicle(vehicle);
		}

		// 3. Добавляем все заказы (магазины)
		for (Shipment shipment : shipments) {
			System.out.println(shipment);
			vrpBuilder.addJob(shipment);
		}

		// 4. Собираем задачу
		VehicleRoutingProblem problem = vrpBuilder.build();
		
		//4 создаём кастомный наполнитель
		StateManager stateManager = new StateManager(problem);
		ConstraintManager constraintManager = new ConstraintManager(problem, stateManager);

		// 1. Обязательные ограничения грузоподъемности
		constraintManager.addLoadConstraint(); // Включает проверку загрузки по всем измерениям

		// 5. Настройка алгоритма с ограничением времени
		VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
			    .setStateAndConstraintManager(stateManager, constraintManager) // Важно!
			    .setProperty(Jsprit.Parameter.THREADS, "4")
			    .setProperty(Jsprit.Parameter.FAST_REGRET, "true")
			    .setProperty(Jsprit.Parameter.CONSTRUCTION, "BEST_INSERTION") // Оптимальная вставка
			    .setProperty(Jsprit.Parameter.ITERATIONS, "500")
			    .setProperty(Jsprit.Strategy.RADIAL_BEST, "0.4")
			    .setProperty(Jsprit.Strategy.RANDOM_BEST, "0.3")
			    .setProperty(Jsprit.Strategy.WORST_BEST, "0.3")
			    .buildAlgorithm();

		

		// 6. Запускаем расчёт
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		// 7. Выбираем лучшее решение
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
		
		// 8. Печатаем отчёт
		SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
		new Plotter(problem, bestSolution).plot("D:\\result/route.png", "Route");
		printSolutionVerbose(problem, bestSolution);

		return null;

	}
	
	public static void printSolutionVerbose(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution) {
	    System.out.println("РЕШЕНИЕ ПО МАРШРУТАМ:\n");

	    for (VehicleRoute route : solution.getRoutes()) {
	        String vehicleId = route.getVehicle().getId();
	        VehicleType vehicleType = route.getVehicle().getType();
	        int maxWeight = vehicleType.getCapacityDimensions().get(0);
	        int maxPallets = vehicleType.getCapacityDimensions().get(1);

	        int totalWeight = 0;
	        int totalPallets = 0;

	        System.out.println("Маршрут для машины: " + vehicleId);
	        System.out.println("  Стартовая точка: " + route.getStart().getLocation().getCoordinate());

	        for (TourActivity act : route.getActivities()) {
	            if (act instanceof JobActivity) {
	                JobActivity jobActivity = (JobActivity) act;
	                Job job = jobActivity.getJob();
	                int jobWeight = job.getSize().get(0);
	                int jobPallets = job.getSize().get(1);
	                String activityType = act.getName(); // "pickupShipment" или "deliverShipment"

	                if ("pickupShipment".equals(activityType)) {
//	                    System.out.printf("  Погрузка заказа: %s, вес: %d кг, паллет: %d\n",
//	                            job.getId(), jobWeight, jobPallets);
	                    totalWeight += jobWeight;
	                    totalPallets += jobPallets;
	                } else if ("deliverShipment".equals(activityType)) {
	                    System.out.printf("  Доставка заказа: %s, вес: %d кг, паллет: %d\n",
	                            job.getId(), jobWeight, jobPallets);
	                }
	            }
	        }

	        System.out.printf("ИТОГО загрузка:\n  Вес: %d / %d кг\n  Паллеты: %d / %d\n",
	                totalWeight, maxWeight, totalPallets, maxPallets);

	        if (totalWeight > maxWeight || totalPallets > maxPallets) {
	            System.err.println("⚠️ ВНИМАНИЕ: ПЕРЕГРУЗКА!!!");
	        }

	        System.out.println("------------------------------------------------------------\n");
	    }
	}

	

	/**
	 * СОздаём авто по json
	 * 
	 * @param jsonMainObject
	 * @return
	 */
	public static List<VehicleImpl> createVehiclesFromJson(JSONObject jsonMainObject, Location depotLocation) {
		List<VehicleImpl> vehicles = new ArrayList<>();

		// Парсим JSON
		JSONArray carsArray = (JSONArray) jsonMainObject.get("cars");

		for (int i = 0; i < carsArray.size(); i++) {
			JSONObject carJson = (JSONObject) carsArray.get(i);

			// Получаем данные из JSON
			String carName = carJson.get("carName").toString();
			int carCount = Integer.parseInt(carJson.get("carCount").toString());
			int maxTonnage = Integer.parseInt(carJson.get("maxTonnage").toString());
			int maxPall = Integer.parseInt(carJson.get("maxPall").toString());

			// Создаем тип транспортного средства
			VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance(carName)
					.addCapacityDimension(0, maxTonnage) // грузоподъемность (кг)
					.addCapacityDimension(1, maxPall); // кол-во паллет

			VehicleType vehicleType = vehicleTypeBuilder.build();

			// Создаем указанное количество транспортных средств этого типа
			for (int j = 1; j <= carCount; j++) {
				String vehicleId = String.format("%s_%d", carName, j);

				VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId)
						.setReturnToDepot(true)
						.setStartLocation(depotLocation) // депо (координаты)
						.setType(vehicleType);

				vehicles.add(vehicleBuilder.build());
			}
		}

		return vehicles;
	}

	/**
	 * Создает список Shipment (заказов) для магазинов.
	 * 
	 * @param shopsForOptimization список магазинов
	 * @param depotLocation        координаты депо (откуда забираем груз)
	 * @return список Shipment для VRP
	 */
	public static List<Shipment> createShipmentsFromShops(List<Shop> shopsForOptimization, Location depotLocation) {
		List<Shipment> shipments = new ArrayList<>();
		int currentIndex = 1;
		for (Shop shop : shopsForOptimization) {
			// Проверяем, что у магазина есть координаты и потребность
			if (shop.getLat() == null || shop.getLng() == null || shop.getWeight() == null
					|| shop.getNeedPall() == null) {
				continue; // Пропускаем некорректные данные
			}

			try {
				// Координаты магазина
				double lat = Double.parseDouble(shop.getLat());
				double lng = Double.parseDouble(shop.getLng());
				Location deliveryLocation = Location.newInstance(lat, lng);
				
				Integer str = currentIndex++;

				// Создаем Shipment (заказ)
				Shipment.Builder shipmentBuilder = Shipment.Builder.newInstance("shop_"+shop.getNumshop())
						.setName(shop.getNumshop() + "").addSizeDimension(0, shop.getWeight()) // вес (кг)
						.addSizeDimension(1, shop.getNeedPall().intValue()) // паллеты (целое число)
						.setPickupLocation(depotLocation) // забираем из депо
						.setDeliveryLocation(deliveryLocation); // доставляем в магазин

				// Можно добавить временные окна, если они есть:
				// shipmentBuilder.setDeliveryTimeWindow(TimeWindow.newInstance(9*3600,
				// 18*3600));

				shipments.add(shipmentBuilder.build());
			} catch (NumberFormatException e) {
				System.err.println("Ошибка парсинга координат магазина " + shop.getNumshop());
			}
		}

		return shipments;
	}

}