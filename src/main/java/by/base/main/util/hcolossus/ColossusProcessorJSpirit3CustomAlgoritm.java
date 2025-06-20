package by.base.main.util.hcolossus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphhopper.jsprit.core.algorithm.PrettyAlgorithmBuilder;
import com.graphhopper.jsprit.core.algorithm.SearchStrategy;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.acceptor.GreedyAcceptance;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.module.RuinAndRecreateModule;
import com.graphhopper.jsprit.core.algorithm.recreate.AbstractInsertionStrategy;
import com.graphhopper.jsprit.core.algorithm.recreate.BestInsertion;
import com.graphhopper.jsprit.core.algorithm.recreate.InsertionBuilder;
import com.graphhopper.jsprit.core.algorithm.recreate.InsertionStrategyBuilder;
import com.graphhopper.jsprit.core.algorithm.recreate.RegretInsertion;
import com.graphhopper.jsprit.core.algorithm.ruin.RuinStrategy;
import com.graphhopper.jsprit.core.algorithm.ruin.listener.RuinListener;
import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.analysis.SolutionAnalyser;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.cost.TransportDistance;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.SolutionCostCalculator;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.FiniteFleetManagerFactory;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleFleetManager;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import by.base.main.model.Shop;
import by.base.main.util.GraphHopper.ExampleManyRouteCustomAlgorinm.MyBestStrategy;
import by.base.main.util.hcolossus.algorithm.ClusterConstraint;
import by.base.main.util.hcolossus.algorithm.MaxDistanceConstraint;
import by.base.main.util.hcolossus.algorithm.MaxDistanceConstraintCritical;
import by.base.main.util.hcolossus.pojo.Solution;
import by.base.main.util.hcolossus.service.MatrixMachine;
import by.base.main.util.hcolossus.service.ShopMachine;
import smile.clustering.KMeans;

/**
 * Версия оптимизатора Jspirit с собственным алгоритмом вставки
 * 
 */
@Component
public class ColossusProcessorJSpirit3CustomAlgoritm {

	@Autowired
	private ShopMachine shopMachine;

	@Autowired
	private MatrixMachine matrixMachine;
	
	private static final double MAX_JUMP_DISTANCE_KM = 40.0;   // порог "нормального" скачка
    private static final double PENALTY_MULTIPLIER = 40.0;      // множитель штрафа


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

		VehicleRoutingTransportCosts costMatrix = prepairMatrixForJspirit(stock, targetStock, shopsService);

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
		
//		constraintManager.addConstraint(new MaxDistanceConstraintCritical(40.0, costMatrix, depotLocation), ConstraintManager.Priority.CRITICAL);	
//		constraintManager.addConstraint(new MaxDistanceConstraint(40.0, 15.0, costMatrix, depotLocation));

		// 1. Обязательные ограничения грузоподъемности
		constraintManager.addLoadConstraint(); // Включает проверку загрузки по всем измерениям
		
//		VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
//			    .setStateAndConstraintManager(stateManager, constraintManager)
//			    .setProperty(Jsprit.Parameter.THREADS, "4")
//			    .setProperty(Jsprit.Parameter.CONSTRUCTION, "FARTHEST_INSERTION") // Начнёт с самых дальних точек
//			    .setProperty(Jsprit.Parameter.ITERATIONS, "4000")
//			    
//			    .setProperty(Jsprit.Strategy.RADIAL_BEST, "0.5")  // Локальная оптимизация внутри кластеров
//			    .setProperty(Jsprit.Strategy.CLUSTER_BEST, "0.5")  // Основная стратегия — кластеры
////			    .setProperty(Jsprit.Strategy.RANDOM_BEST, "0.1")  // Добавляем случайность
//			    .buildAlgorithm();
		VehicleRoutingAlgorithm algorithm = createAlgorithm(problem, costMatrix, depotLocation);

		// 6. Запускаем расчёт
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		// 7. Выбираем лучшее решение
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		// 8. Печатаем отчёт
		SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
//		new Plotter(problem, bestSolution).plot("D:\\result/route.png", "Route");
		exportSolutionToCSV(problem, bestSolution, shopsService, depotLocation, "D:\\result", targetStock);

		return null;

	}
	
	public static VehicleRoutingAlgorithm createAlgorithm(final VehicleRoutingProblem vrp, VehicleRoutingTransportCosts costMatrix, Location depotLocation) {

		VehicleFleetManager fleetManager = new FiniteFleetManagerFactory(vrp.getVehicles()).createFleetManager();
	    StateManager stateManager = new StateManager(vrp);
	    ConstraintManager constraintManager = new ConstraintManager(vrp, stateManager);
	    
//	    constraintManager.addConstraint(new MaxDistanceConstraintCritical(40.0, costMatrix, depotLocation), ConstraintManager.Priority.CRITICAL);
//	    constraintManager.addConstraint(new MaxDistanceConstraint(40.0, 15.0, costMatrix, depotLocation));

	    MyBestStrategy myBestStrategy = new MyBestStrategy(vrp, fleetManager, stateManager, constraintManager);
	    SolutionCostCalculator objectiveFunction = getObjectiveFunction(vrp);

	    // 👇 создаём "RecreateModule", где ruin = пусто	    
	    RuinAndRecreateModule onlyInsertModule = new RuinAndRecreateModule("insertOnly", myBestStrategy, new RuinStrategy() {
			
	    	private final List<RuinListener> listeners = new ArrayList<>();
			@Override
			public Collection<Job> ruin(Collection<VehicleRoute> vehicleRoutes) {
				return Collections.emptyList();
			}
			
			@Override
			public void removeListener(RuinListener ruinListener) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Collection<RuinListener> getListeners() {
				// TODO Auto-generated method stub
				return listeners;
			}
			
			@Override
			public void addListener(RuinListener ruinListener) {
				// TODO Auto-generated method stub
				
			}
		});

	    SearchStrategy myOnlyStrategy = new SearchStrategy(
	        "onlyInsert",
	        new SelectBest(),
	        new GreedyAcceptance(1),
	        objectiveFunction
	    );

	    myOnlyStrategy.addModule(onlyInsertModule);
	    
	    BestInsertion bestInsertion = (BestInsertion) new InsertionBuilder(vrp, fleetManager, stateManager, constraintManager)
	    	    .setInsertionStrategy(InsertionBuilder.Strategy.BEST)
	    	    .considerFixedCosts(1000.0) // или твой параметр
	    	    .setAllowVehicleSwitch(true)
	    	    .build();
	    RegretInsertion regretInsertion = (RegretInsertion) new InsertionBuilder(vrp, fleetManager, stateManager, constraintManager)
	    	    .setInsertionStrategy(InsertionBuilder.Strategy.REGRET)
	    	    .considerFixedCosts(1000.0)  // если хочешь
	    	    .setAllowVehicleSwitch(true)
	    	    .build();
	    SearchStrategy bestStrategy = new SearchStrategy(
	    	    "myBestStrategy",
	    	    new SelectBest(),
	    	    new GreedyAcceptance(1),
	    	    getObjectiveFunction(vrp)  // твоя цель
	    	);
	    bestStrategy.addModule(new RuinAndRecreateModule("bestInsertionModule", regretInsertion, new RuinStrategy() {
	        @Override
	        public Collection<Job> ruin(Collection<VehicleRoute> vehicleRoutes) {
	            return Collections.emptyList(); // без разрушения
	        }
	        @Override public void removeListener(RuinListener l) {}
	        @Override public Collection<RuinListener> getListeners() { return new ArrayList<>(); }
	        @Override public void addListener(RuinListener l) {}
	    }));
	    

	    VehicleRoutingAlgorithm vra = PrettyAlgorithmBuilder
	    	.newInstance(vrp, fleetManager, stateManager, constraintManager)
	    	.addCoreStateAndConstraintStuff()
	        .withStrategy(myOnlyStrategy, 1.0)
//	        .constructInitialSolutionWith(myBestStrategy, objectiveFunction)
	        .constructInitialSolutionWith(bestInsertion, objectiveFunction)
	        .build();
	    
	    //constraintManager.addLoadConstraint();
	    //constraintManager.addTimeWindowConstraint();

	    return vra;
	}
	
	private static SolutionCostCalculator getObjectiveFunction(final VehicleRoutingProblem vrp) {
        return new SolutionCostCalculator() {


            @Override
            public double getCosts(VehicleRoutingProblemSolution solution) {
                SolutionAnalyser analyser = new SolutionAnalyser(vrp,solution,new TransportDistance() {
					@Override
					public double getDistance(Location from, Location to, double departureTime, Vehicle vehicle) {
						return vrp.getTransportCosts().getTransportCost(from, to,0.,null,null);
					}
                });
                return analyser.getVariableTransportCosts() + solution.getUnassignedJobs().size() * 500.;
            }

        };
    }

	/**
	 * Метод отвечает за подготовку матрицы для Jspirit
	 * @param stock
	 * @param targetStock
	 * @param shopsService
	 * @return
	 */
	private VehicleRoutingTransportCosts prepairMatrixForJspirit(Integer stock, Shop targetStock,
			List<Service> shopsService) {
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
				double time = matrixMachine.matrixTime.getOrDefault(matrixKey, Double.POSITIVE_INFINITY);
				matrixBuilder.addTransportDistance(fromCoord.getCoordinate() + "", // Важно! Передаём координаты
						toCoord.getCoordinate() + "", distance / 1000.0);
				double timeInSeconds = time / 1000.0;
				matrixBuilder.addTransportTime(fromCoord.getCoordinate() + "", // Важно! Передаём координаты
						toCoord.getCoordinate() + "", timeInSeconds);

				System.out.println(matrixKey + " === " + fromCoord.getCoordinate() + " - " + toCoord.getCoordinate()
						+ " - " + (distance / 1000.0) + " time = " + timeInSeconds + " сек");
			}
		}

		VehicleRoutingTransportCosts costMatrix = matrixBuilder.build();
		return costMatrix;
	}
	
	public Map<Integer, List<Integer>> clusterShopsKMeans(Map<Integer, Shop> allShops, List<Integer> shopList, int k) {
	    List<double[]> coords = new ArrayList<>();
	    List<Integer> ids = new ArrayList<>();

	    for (Integer id : shopList) {
	        Shop shop = allShops.get(id);
	        double lat = Double.parseDouble(shop.getLat());
	        double lng = Double.parseDouble(shop.getLng());
	        coords.add(new double[]{lat, lng});
	        ids.add(id);
	    }

	    double[][] data = coords.toArray(new double[0][]);
	    KMeans km = KMeans.fit(data, k); // k — число кластеров

	    Map<Integer, List<Integer>> result = new HashMap<>();
	    for (int i = 0; i < data.length; i++) {
	        int cluster = km.y[i];
	        result.computeIfAbsent(cluster, c -> new ArrayList<>()).add(ids.get(i));
	    }

	    return result;
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
		double totalDistance = 0.0;
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
					totalDistance = totalDistance + distance;

					// Для первой активности добавляем расстояние от депо
					if (firstActivity) {
						distanceFromDepot = problem.getTransportCosts().getTransportCost(depotLocation,
								activity.getLocation(), 0, null, null);
//						totalDistance = totalDistance + distanceFromDepot;
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
				totalDistance = totalDistance + distanceToDepot;
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
		addSummaryInfo(csvData, solution, totalDistance);

		return csvData;
	}


	/**
	 * Поиск сервиса по ID
	 */
	private Service findServiceById(List<Service> services, String id) {
		return services.stream().filter(s -> s.getName().equals(id)).findFirst().orElse(null);
	}

	/**
	 * Добавление сводной информации по решению
	 */
	private void addSummaryInfo(List<String[]> csvData, VehicleRoutingProblemSolution solution, double totalDistance) {
		int totalShops = solution.getRoutes().stream().mapToInt(
				r -> (int) r.getActivities().stream().filter(a -> a instanceof TourActivity.JobActivity).count()).sum();

		csvData.add(new String[] { "ОБЩИЕ ИТОГИ", "", "", "", "", "", "", "", "" });
		csvData.add(new String[] { "Всего маршрутов", String.valueOf(solution.getRoutes().size()), "", "", "", "", "",
				"", "" });
		csvData.add(new String[] { "Всего магазинов", String.valueOf(totalShops), "", "", "", "", "", "", "" });
		csvData.add(
				new String[] { "Общий пробег, км", String.format("%.2f", totalDistance), "", "", "", "", "", "", "" });
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
					.addCapacityDimension(1, maxPall) // кол-во паллет
					.setCostPerDistance(1.0) // Стоимость за каждый километр
					.setFixedCost(100) // Фиксированная стоимость использования машины (стартовая цена)
					// Стоимость за каждую секунду движения — включает фактор времени в оптимизацию
				    .setCostPerTransportTime(1.0);  // Важно для учёта времени маршрута!

			VehicleType vehicleType = vehicleTypeBuilder.build();

			// Создаем указанное количество транспортных средств этого типа
			for (int j = 1; j <= carCount; j++) {
				String vehicleId = String.format("%s_%d", carName, j);

				VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId)
						.setReturnToDepot(true)
						.setEarliestStart(0) // Устанавливаем допустимое время начала работы — с 0 секунд (00:00)
						 // Устанавливаем последнее возможное время возвращения — через 10 часов
					    .setLatestArrival(15 * 3600)  // 15 ч * 3600 = 36000 секунд
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
						.setServiceTime(900)  // 15 минут
						.build();

				services.add(service);
			} catch (NumberFormatException e) {
				System.err.println("Ошибка парсинга координат магазина " + shop.getNumshop());
			}
		}

		return services;
	}

}