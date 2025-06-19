package by.base.main.util.hcolossus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
import com.graphhopper.jsprit.core.problem.job.Service;
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
import com.graphhopper.jsprit.core.util.Coordinate;
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
		List<Service> shopsService = createServicesFromShops(shopsForOptimization);

		// 1. Создаём маппинг координат → ID
		Map<Location, Integer> coordinateToId = new HashMap<>();

		// Для депо
		Location depotCoord = Location.newInstance(Double.parseDouble(targetStock.getLat()),
				Double.parseDouble(targetStock.getLng()));
		coordinateToId.put(depotCoord, stock); // stock = ID депо

		// Для магазинов
		for (Service shipment : shopsService) {
			Location shopCoord = shipment.getLocation();
			Integer shopId = Integer.parseInt(shipment.getName());
			coordinateToId.put(shopCoord, shopId);
		}

		// 2. При создании матрицы используем координаты
		VehicleRoutingTransportCostsMatrix.Builder matrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
				.newInstance(false); // если true - матрица симметричная. т.е. в одну и другую сторону одно и тоже
										// расстояние!!!

		for (Location fromCoord : coordinateToId.keySet()) {
			for (Location toCoord : coordinateToId.keySet()) {
				Integer fromId = coordinateToId.get(fromCoord);
				Integer toId = coordinateToId.get(toCoord);

				String matrixKey = fromId + "-" + toId;
				double distance = matrixMachine.matrix.getOrDefault(matrixKey, Double.POSITIVE_INFINITY);

				matrixBuilder.addTransportDistance(fromCoord.getCoordinate() + "", // Важно! Передаём координаты
						toCoord.getCoordinate() + "", distance / 1000.0);

				System.out.println(matrixKey + " === " + fromCoord.getCoordinate() + " - " + toCoord.getCoordinate()
						+ " - " + (distance / 1000.0));
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
		for (Service shipment : shopsService) {
			vrpBuilder.addJob(shipment);
		}

		// 4. Собираем задачу
		VehicleRoutingProblem problem = vrpBuilder.build();

		// 4 создаём кастомный наполнитель
		StateManager stateManager = new StateManager(problem);
		ConstraintManager constraintManager = new ConstraintManager(problem, stateManager);

		// 1. Обязательные ограничения грузоподъемности
		constraintManager.addLoadConstraint(); // Включает проверку загрузки по всем измерениям

		// 5. Настройка алгоритма с ограничением времени
		VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
				.setStateAndConstraintManager(stateManager, constraintManager) // Важно!
				.setProperty(Jsprit.Parameter.THREADS, "4").setProperty(Jsprit.Parameter.FAST_REGRET, "true")
				.setProperty(Jsprit.Parameter.CONSTRUCTION, "BEST_INSERTION") // Оптимальная вставка
				.setProperty(Jsprit.Parameter.ITERATIONS, "500").setProperty(Jsprit.Strategy.RADIAL_BEST, "0.4")
				.setProperty(Jsprit.Strategy.RANDOM_BEST, "0.3").setProperty(Jsprit.Strategy.WORST_BEST, "0.3")
				.buildAlgorithm();

		// 6. Запускаем расчёт
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		// 7. Выбираем лучшее решение
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		// 8. Печатаем отчёт
		SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
		new Plotter(problem, bestSolution).plot("D:\\result/route.png", "Route");
//		printSolutionVerbose(problem, bestSolution);
		exportSolutionToCSV(problem, bestSolution, shopsService, depotLocation, "D:\\result", targetStock);

		return null;

	}

	/**
	 * Экспорт решения маршрутизации в CSV файл
	 * 
	 * @param problem       объект задачи маршрутизации
	 * @param solution      найденное решение
	 * @param services      список сервисов (магазинов)
	 * @param depotLocation координаты депо
	 * @param filePath      путь для сохранения CSV файла
	 */
	private void exportSolutionToCSV(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution,
			List<Service> services, Location depotLocation, String filePath, Shop stock) {
		// Подготовка данных для CSV
		List<String[]> csvData = prepareCSVData(problem, solution, services, depotLocation, stock);

		// Запись в файл
		writeCSVFile(csvData, filePath);
	}

	/**
	 * Подготовка данных для CSV файла
	 */
	private List<String[]> prepareCSVData(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution,
			List<Service> services, Location depotLocation, Shop stock) {
		List<String[]> csvData = new ArrayList<>();

// Заголовок CSV
		csvData.add(new String[] { "Маршрут", "Транспорт", "ID точки", "Тип точки", "Вес, кг", "Паллеты", "Широта",
				"Долгота", "Расстояние до след. точки, км" });

// Собираем ID распределённых магазинов
		Set<String> assignedShopIds = new HashSet<>();
		for (VehicleRoute route : solution.getRoutes()) {
			for (TourActivity activity : route.getActivities()) {
				if (activity instanceof TourActivity.JobActivity) {
					assignedShopIds.add(((TourActivity.JobActivity) activity).getJob().getId());
				}
			}
		}

// Собираем ID использованных машин
		Set<String> usedVehicleIds = solution.getRoutes().stream().map(route -> route.getVehicle().getId())
				.collect(Collectors.toSet());

// Для каждого маршрута
		for (VehicleRoute route : solution.getRoutes()) {
			Vehicle vehicle = route.getVehicle();
			String vehicleId = vehicle.getId();

// Характеристики транспорта
			double maxLoadWeight = vehicle.getType().getCapacityDimensions().get(0);
			double maxPallets = vehicle.getType().getCapacityDimensions().get(1);
			double usedWeight = 0;
			double usedPallets = 0;

// Добавляем строку с информацией о транспорте
			csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, vehicleId, "Транспорт", "", "",
					String.valueOf(depotLocation.getCoordinate().getX()),
					String.valueOf(depotLocation.getCoordinate().getY()), "" });

// Стартовая точка - депо
			csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, String.valueOf(stock.getNumshop()),
					"Депо (старт)", "", "", String.valueOf(depotLocation.getCoordinate().getX()),
					String.valueOf(depotLocation.getCoordinate().getY()), "" });

			TourActivity prevActivity = route.getStart();
			double distanceFromDepot = 0;
			boolean firstActivity = true;

			for (TourActivity activity : route.getActivities()) {
				if (activity instanceof TourActivity.JobActivity) {
					Job job = ((TourActivity.JobActivity) activity).getJob();
					String shopId = job.getId();

// Находим сервис (магазин) по ID
					Service shopService = findServiceById(services, shopId);
					Location shopLocation = shopService != null ? shopService.getLocation() : null;

// Расчет расстояния
					double distance = problem.getTransportCosts().getTransportCost(prevActivity.getLocation(),
							activity.getLocation(), 0, null, null);

// Для первой активности добавляем расстояние от депо
					if (firstActivity) {
						distanceFromDepot = problem.getTransportCosts().getTransportCost(depotLocation,
								activity.getLocation(), 0, null, null);
						firstActivity = false;
					}

// Обновляем суммарные показатели
					int jobWeight = job.getSize().get(0);
					int jobPallets = job.getSize().get(1);
					usedWeight += jobWeight;
					usedPallets += jobPallets;

// Добавляем строку с магазином
					csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, shopId, "Магазин",
							String.valueOf(jobWeight), String.valueOf(jobPallets),
							shopLocation != null ? String.valueOf(shopLocation.getCoordinate().getX()) : "",
							shopLocation != null ? String.valueOf(shopLocation.getCoordinate().getY()) : "",
							firstActivity ? String.format("%.2f", distanceFromDepot)
									: String.format("%.2f", distance) });

					prevActivity = activity;
				}
			}

// Добавляем расстояние от последней точки до депо
			double distanceToDepot = 0;
			if (prevActivity != null) {
				distanceToDepot = problem.getTransportCosts().getTransportCost(prevActivity.getLocation(),
						depotLocation, 0, null, null);
			}

// Депо как конечная точка маршрута
			csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, String.valueOf(stock.getNumshop()),
					"Депо (финиш)", "", "", String.valueOf(depotLocation.getCoordinate().getX()),
					String.valueOf(depotLocation.getCoordinate().getY()), String.format("%.2f", distanceToDepot) });

// Итоговая строка по маршруту
			csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, "ИТОГО", "",
					String.format("%.0f/%.0f", usedWeight, maxLoadWeight),
					String.format("%.0f/%.0f", usedPallets, maxPallets), "", "", "" });

// Пустая строка для разделения маршрутов
			csvData.add(new String[] { "", "", "", "", "", "", "", "", "" });
		}

// Добавляем информацию о нераспределённых магазинах
		List<Service> unassignedShops = services.stream().filter(service -> !assignedShopIds.contains(service.getId()))
				.collect(Collectors.toList());

		if (!unassignedShops.isEmpty()) {
			csvData.add(new String[] { "НЕРАСПРЕДЕЛЁННЫЕ МАГАЗИНЫ", "", "", "", "", "", "", "", "" });
			for (Service shop : unassignedShops) {
				csvData.add(new String[] { "Не распределён", "", shop.getId(), "Магазин",
						String.valueOf(shop.getSize().get(0)), String.valueOf(shop.getSize().get(1)),
						String.valueOf(shop.getLocation().getCoordinate().getX()),
						String.valueOf(shop.getLocation().getCoordinate().getY()), "" });
			}
			csvData.add(new String[] { "", "", "", "", "", "", "", "", "" });
		}

// Добавляем информацию о неиспользованных машинах
		List<Vehicle> unusedVehicles = problem.getVehicles().stream()
				.filter(vehicle -> !usedVehicleIds.contains(vehicle.getId())).collect(Collectors.toList());

		if (!unusedVehicles.isEmpty()) {
			csvData.add(new String[] { "НЕИСПОЛЬЗОВАННЫЕ МАШИНЫ", "", "", "", "", "", "", "", "" });
			for (Vehicle vehicle : unusedVehicles) {
				csvData.add(new String[] { "Не использована", vehicle.getId(), "", "Транспорт", "", "",
						String.valueOf(depotLocation.getCoordinate().getX()),
						String.valueOf(depotLocation.getCoordinate().getY()), "" });
			}
			csvData.add(new String[] { "", "", "", "", "", "", "", "", "" });
		}

// Добавляем общие итоги
		addSummaryInfo(csvData, solution);

		return csvData;
	}

//	private List<String[]> prepareCSVData(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution,
//			List<Service> services, Location depotLocation, Shop stock) {
//		List<String[]> csvData = new ArrayList<>();
//
//		// Заголовок CSV
//		csvData.add(new String[] { "Маршрут", "Транспорт", "ID точки", "Тип точки", "Вес, кг", "Паллеты", "Широта",
//				"Долгота", "Расстояние до след. точки, км" });
//
//		// Для каждого маршрута
//		for (VehicleRoute route : solution.getRoutes()) {
//			Vehicle vehicle = route.getVehicle();
//			String vehicleId = vehicle.getId();
//
//			// Характеристики транспорта
//			double maxLoadWeight = vehicle.getType().getCapacityDimensions().get(0);
//			double maxPallets = vehicle.getType().getCapacityDimensions().get(1);
//			double usedWeight = 0;
//			double usedPallets = 0;
//
//			// Добавляем строку с информацией о транспорте
//			csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, vehicleId, "Транспорт", "", "",
//					String.valueOf(depotLocation.getCoordinate().getX()),
//					String.valueOf(depotLocation.getCoordinate().getY()), "" });
//
//			// Стартовая точка - депо
//			csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, String.valueOf(stock.getNumshop()),
//					"Депо (старт)", "", "", String.valueOf(depotLocation.getCoordinate().getX()),
//					String.valueOf(depotLocation.getCoordinate().getY()), "" });
//
//			TourActivity prevActivity = route.getStart();
//			double distanceFromDepot = 0;
//			boolean firstActivity = true;
//
//			for (TourActivity activity : route.getActivities()) {
//				if (activity instanceof TourActivity.JobActivity) {
//					Job job = ((TourActivity.JobActivity) activity).getJob();
//					String shopId = job.getId();
//
//					// Находим сервис (магазин) по ID
//					Service shopService = findServiceById(services, shopId);
//					Location shopLocation = shopService != null ? shopService.getLocation() : null;
//
//					// Расчет расстояния
//					double distance = problem.getTransportCosts().getTransportCost(prevActivity.getLocation(),
//							activity.getLocation(), 0, null, null);
//
//					// Для первой активности добавляем расстояние от депо
//					if (firstActivity) {
//						distanceFromDepot = problem.getTransportCosts().getTransportCost(depotLocation,
//								activity.getLocation(), 0, null, null);
//						firstActivity = false;
//					}
//
//					// Обновляем суммарные показатели
//					int jobWeight = job.getSize().get(0);
//					int jobPallets = job.getSize().get(1);
//					usedWeight += jobWeight;
//					usedPallets += jobPallets;
//
//					// Добавляем строку с магазином
//					csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, shopId, "Магазин",
//							String.valueOf(jobWeight), String.valueOf(jobPallets),
//							shopLocation != null ? String.valueOf(shopLocation.getCoordinate().getX()) : "",
//							shopLocation != null ? String.valueOf(shopLocation.getCoordinate().getY()) : "",
//							firstActivity ? String.format("%.2f", distanceFromDepot)
//									: String.format("%.2f", distance) });
//
//					prevActivity = activity;
//				}
//			}
//
//			// Добавляем расстояние от последней точки до депо
//			double distanceToDepot = 0;
//			if (prevActivity != null) {
//				distanceToDepot = problem.getTransportCosts().getTransportCost(prevActivity.getLocation(),
//						depotLocation, 0, null, null);
//			}
//
//			// Депо как конечная точка маршрута
//			csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, String.valueOf(stock.getNumshop()),
//					"Депо (финиш)", "", "", String.valueOf(depotLocation.getCoordinate().getX()),
//					String.valueOf(depotLocation.getCoordinate().getY()), String.format("%.2f", distanceToDepot) });
//
//			// Итоговая строка по маршруту
//			csvData.add(new String[] { "Маршрут " + vehicleId, vehicleId, "ИТОГО", "",
//					String.format("%.0f/%.0f", usedWeight, maxLoadWeight),
//					String.format("%.0f/%.0f", usedPallets, maxPallets), "", "", "" });
//
//			// Пустая строка для разделения маршрутов
//			csvData.add(new String[] { "", "", "", "", "", "", "", "", "" });
//		}
//
//		// Добавляем общие итоги
//		addSummaryInfo(csvData, solution);
//
//		return csvData;
//	}

	/**
	 * Поиск сервиса по ID
	 */
	private Service findServiceById(List<Service> services, String id) {
		return services.stream().filter(s -> s.getName().equals(id)).findFirst().orElse(null);
	}

	/**
	 * Добавление сводной информации по решению
	 */
	private void addSummaryInfo(List<String[]> csvData, VehicleRoutingProblemSolution solution) {
		int totalShops = solution.getRoutes().stream().mapToInt(
				r -> (int) r.getActivities().stream().filter(a -> a instanceof TourActivity.JobActivity).count()).sum();

		double totalDistance = solution.getRoutes().stream().mapToDouble(this::calculateRouteDistance).sum();

		csvData.add(new String[] { "ОБЩИЕ ИТОГИ", "", "", "", "", "", "", "", "" });
		csvData.add(new String[] { "Всего маршрутов", String.valueOf(solution.getRoutes().size()), "", "", "", "", "",
				"", "" });
		csvData.add(new String[] { "Всего магазинов", String.valueOf(totalShops), "", "", "", "", "", "", "" });
		csvData.add(
				new String[] { "Общий пробег, км", String.format("%.2f", totalDistance), "", "", "", "", "", "", "" });
	}

	private double calculateRouteDistance(VehicleRoute route) {
		double distance = 0;
		TourActivity prevActivity = route.getStart();

		for (TourActivity activity : route.getActivities()) {
			distance += getDistanceBetweenActivities(prevActivity, activity);
			prevActivity = activity;
		}

		// Добавляем расстояние от последней активности до конца маршрута
		distance += getDistanceBetweenActivities(prevActivity, route.getEnd());

		return distance;
	}

	private double getDistanceBetweenActivities(TourActivity from, TourActivity to) {
		// Здесь нужно использовать вашу матрицу расстояний или другой способ расчета
		// В простейшем случае можно использовать евклидово расстояние:
		Location fromLoc = from.getLocation();
		Location toLoc = to.getLocation();

		return Math.sqrt(Math.pow(fromLoc.getCoordinate().getX() - toLoc.getCoordinate().getX(), 2)
				+ Math.pow(fromLoc.getCoordinate().getY() - toLoc.getCoordinate().getY(), 2));
	}

	/**
	 * Запись данных в CSV файл
	 */
	private void writeCSVFile(List<String[]> data, String filePath) {
		File file = new File(filePath);

		try {
			// Создаем родительские директории, если их нет
			File parentDir = file.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				if (!parentDir.mkdirs()) {
					System.err.println("Не удалось создать директорию: " + parentDir.getAbsolutePath());
					// Пробуем сохранить в домашней директории пользователя
					filePath = System.getProperty("user.home") + File.separator + "routes_report.csv";
					file = new File(filePath);
				}
			}

			// Проверяем возможность записи
			if (file.exists() && !file.canWrite()) {
				System.err.println("Нет прав на запись в файл: " + filePath);
				filePath = System.getProperty("user.home") + File.separator + "routes_report.csv";
				file = new File(filePath);
			}

			// Записываем данные
			try (PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
				writer.write('\ufeff'); // BOM для UTF-8

				for (String[] row : data) {
					writer.println(String.join(";", row));
				}

				System.out.println("Отчет успешно сохранен: " + file.getAbsolutePath());
			}
		} catch (IOException e) {
			System.err.println("Ошибка при сохранении отчета: " + e.getMessage());

			// Последняя попытка - сохранить в временную директорию
			try {
				String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "routes_report.csv";
				try (PrintWriter writer = new PrintWriter(
						new OutputStreamWriter(new FileOutputStream(tempPath), StandardCharsets.UTF_8))) {
					writer.write('\ufeff');
					for (String[] row : data) {
						writer.println(String.join(";", row));
					}
					System.out.println("Отчет сохранен в временную директорию: " + tempPath);
				}
			} catch (IOException ex) {
				System.err.println("Не удалось сохранить отчет даже во временную директорию: " + ex.getMessage());
			}
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

				VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId).setReturnToDepot(true)
						.setStartLocation(depotLocation) // депо (координаты)
						.setType(vehicleType);

				vehicles.add(vehicleBuilder.build());
			}
		}

		return vehicles;
	}

	/**
	 * Создает список Shipment !!! (заказов) для магазинов. Важно что Shipment это
	 * ЗАБОР И ДОСТАВКА!!!!
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
				Shipment.Builder shipmentBuilder = Shipment.Builder.newInstance("shop_" + shop.getNumshop())
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

	/**
	 * Создает список Service !!! (заказов) для магазинов. Только доставка!
	 * 
	 * @param shopsForOptimization
	 * @return
	 */
	public static List<Service> createServicesFromShops(List<Shop> shopsForOptimization) {
		List<Service> services = new ArrayList<>();

		for (Shop shop : shopsForOptimization) {
			// Проверяем, что у магазина есть координаты и потребность
//	        if (shop.getLat() == null || shop.getLng() == null || shop.getWeight() == null
//	                || shop.getNeedPall() == null) {
//	            continue; // Пропускаем некорректные данные
//	        }

			try {
				// Координаты магазина (точка доставки)
				double lat = Double.parseDouble(shop.getLat());
				double lng = Double.parseDouble(shop.getLng());
				Location deliveryLocation = Location.newInstance(lat, lng);

				// Создаем Service (простая доставка)
				Service service = Service.Builder.newInstance(shop.getNumshop() + "").setName(shop.getNumshop() + "")
						.addSizeDimension(0, shop.getWeight()) // вес (кг)
						.addSizeDimension(1, shop.getNeedPall().intValue()) // паллеты
						.setLocation(deliveryLocation) // точка доставки (без забора!)
						.build();

				services.add(service);
			} catch (NumberFormatException e) {
				System.err.println("Ошибка парсинга координат магазина " + shop.getNumshop());
			}
		}

		return services;
	}

}