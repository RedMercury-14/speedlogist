package by.base.main.util.GraphHopper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.analysis.toolbox.StopWatch;
import com.graphhopper.jsprit.core.algorithm.PrettyAlgorithmBuilder;
import com.graphhopper.jsprit.core.algorithm.SearchStrategy;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.acceptor.GreedyAcceptance;
import com.graphhopper.jsprit.core.algorithm.listener.IterationStartsListener;
import com.graphhopper.jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListeners.Priority;
import com.graphhopper.jsprit.core.algorithm.module.RuinAndRecreateModule;
import com.graphhopper.jsprit.core.algorithm.recreate.AbstractInsertionStrategy;
import com.graphhopper.jsprit.core.algorithm.recreate.InsertionBuilder;
import com.graphhopper.jsprit.core.algorithm.recreate.InsertionData;
import com.graphhopper.jsprit.core.algorithm.recreate.JobInsertionCostsCalculatorLight;
import com.graphhopper.jsprit.core.algorithm.recreate.JobInsertionCostsCalculatorLightFactory;
import com.graphhopper.jsprit.core.algorithm.recreate.RegretInsertion;
import com.graphhopper.jsprit.core.algorithm.ruin.RandomRuinStrategyFactory;
import com.graphhopper.jsprit.core.algorithm.ruin.RuinStrategy;
import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.analysis.SolutionAnalyser;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.SolutionCostCalculator;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.FiniteFleetManagerFactory;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleFleetManager;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;

public class SimpleCustomStrategyExample {
	
	public static void main(String[] args) {
        // ==============================================
        // ШАГ 1: Создание проблемы маршрутизации (VRP)
        // ==============================================

        // Создание билдера для Vehicle Routing Problem
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        final int WEIGHT_INDEX = 0; // Индекс для измерения веса/объема груза

        // Список сервисов (точек доставки) с координатами и объемом груза
        List<Service> services = Arrays.asList(
                Service.Builder.newInstance("2420 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.432760, 30.081273)).build(),
                Service.Builder.newInstance("2507 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.367933, 30.352870)).build(),
                Service.Builder.newInstance("2445 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.470185, 30.811819)).build(),
                Service.Builder.newInstance("2573 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.383926, 31.340801)).build(),
                Service.Builder.newInstance("2309 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(51.443972, 30.556509)).build(),
                Service.Builder.newInstance("2328 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.251612, 29.830131)).build(),
                Service.Builder.newInstance("2354 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.356054, 31.050825)).build(),
                Service.Builder.newInstance("2041 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.563911, 31.174559)).build(),
                Service.Builder.newInstance("926 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(53.393042, 29.008504)).build(),
                Service.Builder.newInstance("2396 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.536493, 24.989865)).build(),
                Service.Builder.newInstance("369 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.466430, 25.181534)).build(),
                Service.Builder.newInstance("2363 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.283385, 24.455367)).build(),
                Service.Builder.newInstance("379 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.367179, 23.383425)).build(),
                Service.Builder.newInstance("2350 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(51.931634, 23.662927)).build(),
                Service.Builder.newInstance("2443 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(51.783312, 24.054063)).build(),
                Service.Builder.newInstance("399 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.102650, 23.710870)).build(),
                Service.Builder.newInstance("2362 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.049800, 23.704681)).build(),
                Service.Builder.newInstance("2230 М").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(53.808057, 30.967973)).build(),
                Service.Builder.newInstance("283 Н").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(55.350986, 29.307491)).build()
            );

        // Добавление всех сервисов в проблему
        services.forEach(vrpBuilder::addJob);

        // Координаты депо (стартовая и конечная точка для транспортных средств)
        Coordinate depot = Coordinate.newInstance(53.808867, 27.775884);

        // ==============================================
        // ШАГ 2: Определение транспортных средств
        // ==============================================

        // Создание типа для большой машины (фуры) с грузоподъемностью 20 единиц
        VehicleTypeImpl bigType = VehicleTypeImpl.Builder.newInstance("big_type")
                .addCapacityDimension(0, 20).setCostPerDistance(1.0).build();
        // Создание самой фуры с начальной точкой в депо
        VehicleImpl bigTruck = VehicleImpl.Builder.newInstance("1_1_фура")
                .setStartLocation(Location.Builder.newInstance().setCoordinate(depot).build())
                .setType(bigType)
                .setLatestArrival(1000).build(); // Максимальное время возврата
        vrpBuilder.addVehicle(bigTruck);

        // Создание 4 малых машин с грузоподъемностью 10 единиц
        for (int i = 0; i < 4; i++) {
            VehicleTypeImpl smallType = VehicleTypeImpl.Builder.newInstance("small_type")
                    .addCapacityDimension(0, 10).setCostPerDistance(1.0).build();
            VehicleImpl smallTruck = VehicleImpl.Builder.newInstance("2_" + (i + 1) + "_малыш")
                    .setStartLocation(Location.newInstance(depot.getX(), depot.getY()))
                    .setType(smallType)
                    .setLatestArrival(8).build(); // Более строгое ограничение по времени
            vrpBuilder.addVehicle(smallTruck);
        }

        // Установка размера флота как конечного (FleetSize.FINITE)
        vrpBuilder.setFleetSize(FleetSize.FINITE);
        
        // Построение проблемы маршрутизации
        VehicleRoutingProblem vrp = vrpBuilder.build();

        // ==============================================
        // ШАГ 3: Настройка и запуск алгоритма
        // ==============================================

        // Создание алгоритма маршрутизации с кастомной стратегией
        VehicleRoutingAlgorithm vra = createAlgorithm(vrp);
        vra.setMaxIterations(1); // Ограничение количества итераций
        
        // Добавление слушателей для мониторинга работы алгоритма
        vra.getAlgorithmListeners().addListener(new StopWatch(), Priority.HIGH);
        vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("D:\\result\\progress.png"));

        // Запуск поиска решений
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
        System.out.println("All solutions = " + solutions.size());

        // Вывод и визуализация лучшего решения
        SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);
        new Plotter(vrp, Solutions.bestOf(solutions)).setLabel(Plotter.Label.ID).plot("D:\\result\\развоз.png", "Тест развоза");
        new GraphStreamViewer(vrp, Solutions.bestOf(solutions)).labelWith(Label.ID).setRenderDelay(50).display();
    }

    /**
     * Создает и настраивает алгоритм маршрутизации с кастомной стратегией
     */
    public static VehicleRoutingAlgorithm createAlgorithm(final VehicleRoutingProblem vrp) {
        // Менеджеры для управления состоянием и ограничениями
        VehicleFleetManager fleetManager = new FiniteFleetManagerFactory(vrp.getVehicles()).createFleetManager();
        StateManager stateManager = new StateManager(vrp);
        ConstraintManager constraintManager = new ConstraintManager(vrp, stateManager);

        // Кастомная стратегия вставки задач
        MyBestStrategy best = new MyBestStrategy(vrp, fleetManager, stateManager, constraintManager);

        // Настройка стратегии вставки (жадный алгоритм с "сожалением")
        InsertionBuilder builder = new InsertionBuilder(vrp, fleetManager, stateManager, constraintManager);
        builder.setInsertionStrategy(InsertionBuilder.Strategy.REGRET);
        RegretInsertion regret = (RegretInsertion) builder.build();

        // Стратегия "разрушения" (удаления части маршрута для улучшения)
        RuinStrategy ruin = new RandomRuinStrategyFactory(0.5).createStrategy(vrp);
        SolutionCostCalculator costCalculator = getObjectiveFunction(vrp);

        // Создание поисковой стратегии
        SearchStrategy strategy = new SearchStrategy("firstStrategy", new SelectBest(), new GreedyAcceptance(1), costCalculator);
        strategy.addModule(new RuinAndRecreateModule("ruin_recreate", best, ruin));

        // Построение алгоритма с использованием PrettyAlgorithmBuilder
        PrettyAlgorithmBuilder builderPretty = PrettyAlgorithmBuilder.newInstance(vrp, fleetManager, stateManager, constraintManager);
        VehicleRoutingAlgorithm vra = builderPretty
                .withStrategy(strategy, 1.0) // Вес стратегии
                .addCoreStateAndConstraintStuff() // Добавление базовых компонентов
                .constructInitialSolutionWith(regret, costCalculator) // Начальное решение
                .build();

        // Добавление слушателя для динамического изменения веса стратегии
//        vra.addListener((IterationStartsListener) (i, problem, solutions) -> {
//            if (i == 50) {
//                vra.getSearchStrategyManager().informStrategyWeightChanged("firstStrategy", 0.0);
//                System.out.println("switched off firstStrategy");
//            }
//            if (i == 90) {
//                vra.getSearchStrategyManager().informStrategyWeightChanged("firstStrategy", 0.7);
//                System.out.println("switched on firstStrategy again with higher weight");
//            }
//        });

        // Добавляем слушатель для логирования каждой итерации
        vra.addListener((IterationStartsListener) (iteration, problem, solutions) -> {
            System.out.println("\n\n=== Итерация " + iteration + " ===\n");
            
            VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
            System.out.println("! Лучшее решение !");
            System.out.println("Общая стоимость: " + bestSolution.getCost());
            System.out.println("Количество маршрутов: " + bestSolution.getRoutes().size());
            System.out.println("Нераспределенные задачи: " + bestSolution.getUnassignedJobs().size());
            
            final int WEIGHT_INDEX = 0;
            int routeNum = 1;
            
            // Детализация по каждому маршруту
            for (VehicleRoute route : bestSolution.getRoutes()) {
                System.out.println("\n  ░░░ Маршрут #" + routeNum++ + " ░░░");
                System.out.println("  Транспорт: " + route.getVehicle().getId() + 
                                 " (грузоподъемность: " + route.getVehicle().getType().getCapacityDimensions().get(WEIGHT_INDEX) + ")");
                System.out.println("  Текущая загрузка: " + route.getStart().getLocation().getCoordinate());
                System.out.println("  Количество точек доставки: " + route.getTourActivities().getActivities().size());
                
                // Рассчет расстояния
                double distance = 0;
                Location prev = route.getStart().getLocation();
                System.out.println("\n  Точки маршрута:");
                System.out.println("  1. Депо (" + prev + ")");
                
                int pointNum = 2;
                for (TourActivity act : route.getActivities()) {
                    distance += vrp.getTransportCosts().getTransportCost(prev, act.getLocation(), 0, null, null);
                    System.out.println("  " + pointNum++ + ". " + act.getName() + 
                                     " (" + act.getLocation() + ")" + 
                                     " Груз: " + act.getSize().get(WEIGHT_INDEX));
                    prev = act.getLocation();
                }
                distance += vrp.getTransportCosts().getTransportCost(prev, route.getEnd().getLocation(), 0, null, null);
                System.out.println("  " + pointNum + ". Депо (" + route.getEnd().getLocation() + ")");
                
                System.out.println("\n  Общая длина маршрута: " + String.format("%.2f", distance) + " усл.ед.");
            }
            
            // Логирование нераспределенных задач
            if (!bestSolution.getUnassignedJobs().isEmpty()) {
                System.out.println("\n░░ Нераспределенные задачи ░░");
                for (Job job : bestSolution.getUnassignedJobs()) {
                    if (job instanceof Service) {
                        Service service = (Service) job;
                        System.out.println("  " + service.getId() + " (" + service.getLocation() + ")");
                    }
                }
            }
        });
        return vra;
    }

    /**
     * Создает функцию оценки стоимости решения
     */
    private static SolutionCostCalculator getObjectiveFunction(VehicleRoutingProblem vrp) {
        return solution -> {
            // Анализ решения с учетом транспортных затрат
            SolutionAnalyser analyser = new SolutionAnalyser(vrp, solution, (from, to, time, vehicle) ->
                    vrp.getTransportCosts().getTransportCost(from, to, 0., null, null));
            // Общая стоимость = транспортные затраты + штраф за нераспределенные задачи
            return analyser.getVariableTransportCosts() + solution.getUnassignedJobs().size() * 500.;
        };
    }

    /**
     * Кастомная стратегия вставки задач в маршруты
     */
    public static class MyBestStrategy extends AbstractInsertionStrategy {
        private final JobInsertionCostsCalculatorLight insertionCalculator;

        public MyBestStrategy(VehicleRoutingProblem vrp, VehicleFleetManager fleetManager,
                              StateManager stateManager, ConstraintManager constraintManager) {
            super(vrp);
            // Создание калькулятора стоимости вставки
            this.insertionCalculator = JobInsertionCostsCalculatorLightFactory
                    .createStandardCalculator(vrp, fleetManager, stateManager, constraintManager);
        }

        //без логирования
//        @Override
//        public Collection<Job> insertUnassignedJobs(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
//            List<Job> badJobs = new ArrayList<>(); // Задачи, которые не удалось вставить
//            List<Job> shuffled = new ArrayList<>(unassignedJobs);
//            Collections.shuffle(shuffled, random); // Перемешивание для случайности
//
//            for (Job job : shuffled) {
//                InsertionData bestData = InsertionData.createEmptyInsertionData();
//                VehicleRoute bestRoute = null;
//
//                // Поиск лучшего маршрута для вставки задачи
//                for (VehicleRoute route : vehicleRoutes) {
//                    InsertionData data = insertionCalculator.getInsertionData(job, route, bestData.getInsertionCost());
//                    if (!(data instanceof InsertionData.NoInsertionFound) && data.getInsertionCost() < bestData.getInsertionCost()) {
//                        bestData = data;
//                        bestRoute = route;
//                    }
//                }
//
//                // Проверка возможности создания нового маршрута для задачи
//                VehicleRoute newRoute = VehicleRoute.emptyRoute();
//                InsertionData newData = insertionCalculator.getInsertionData(job, newRoute, bestData.getInsertionCost());
//
//                if (!(newData instanceof InsertionData.NoInsertionFound) && newData.getInsertionCost() < bestData.getInsertionCost()) {
//                    // Вставка в новый маршрут, если это выгоднее
//                    vehicleRoutes.add(newRoute);
//                    insertJob(job, newData, newRoute);
//                } else if (bestRoute != null) {
//                    // Вставка в существующий маршрут
//                    insertJob(job, bestData, bestRoute);
//                } else {
//                    // Задача не может быть вставлена
//                    badJobs.add(job);
//                }
//            }
//
//            return badJobs;
//        }
        // тот же метод но с логами
        @Override
        public Collection<Job> insertUnassignedJobs(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
            System.out.println("\n▓▓▓ Начало вставки " + unassignedJobs.size() + " нераспределенных задач ▓▓▓");
            
            List<Job> badJobs = new ArrayList<>();
            List<Job> shuffled = new ArrayList<>(unassignedJobs);
            Collections.shuffle(shuffled, random);

            for (Job job : shuffled) {
                System.out.println("\n░░ Обработка задачи: " + job.getId() + " ░░");
                
                InsertionData bestData = InsertionData.createEmptyInsertionData();
                VehicleRoute bestRoute = null;
                int evalCount = 0;

                // Поиск в существующих маршрутах
                for (VehicleRoute route : vehicleRoutes) {
                    System.out.println("  Проверка маршрута " + route.getVehicle().getId() + 
                                     " (загрузка: " + route.getStart().getName() + "/" + 
                                     route.getVehicle().getType().getCapacityDimensions().get(0) + ")");
                    
                    InsertionData data = insertionCalculator.getInsertionData(job, route, bestData.getInsertionCost());
                    evalCount++;
                    
                    if (!(data instanceof InsertionData.NoInsertionFound)) {
                        System.out.println("  ✓ Возможная вставка со стоимостью " + data.getInsertionCost());
//                        System.out.println("    Позиция: между " + 
//                                         (data.getPreviousActivity() != null ? data.getPreviousActivity().getName() : "START") + 
//                                         " и " + 
//                                         (data.getNextActivity() != null ? data.getNextActivity().getName() : "END"));
                        
                        if (data.getInsertionCost() < bestData.getInsertionCost()) {
                            bestData = data;
                            bestRoute = route;
                            System.out.println("    ★ Новый лучший вариант! Загружено :");
                        }
                    } else {
                        System.out.println("  ✗ Нельзя вставить (ограничения)");
                    }
                }

                // Проверка нового маршрута
                System.out.println("  Проверка создания нового маршрута...");
                VehicleRoute newRoute = VehicleRoute.emptyRoute();
                InsertionData newData = insertionCalculator.getInsertionData(job, newRoute, bestData.getInsertionCost());
                evalCount++;

                if (!(newData instanceof InsertionData.NoInsertionFound)) {
                    System.out.println("  ✓ Можно создать новый маршрут со стоимостью " + newData.getInsertionCost());
                    
                    if (newData.getInsertionCost() < bestData.getInsertionCost()) {
                        System.out.println("  ★ Создание нового маршрута выгоднее!");
                        vehicleRoutes.add(newRoute);
                        insertJob(job, newData, newRoute);
                        System.out.println("  ✔ Задача " + job.getId() + " добавлена в НОВЫЙ маршрут");
                    } else if (bestRoute != null) {
                        System.out.println("  ✔ Задача " + job.getId() + " добавлена в существующий маршрут " + bestRoute.getVehicle().getId());
                        insertJob(job, bestData, bestRoute);
                    }
                } else {
                    System.out.println("  ✗ Нельзя создать новый маршрут для этой задачи");
                    if (bestRoute != null) {
                        System.out.println("  ✔ Задача " + job.getId() + " добавлена в существующий маршрут " + bestRoute.getVehicle().getId());
                        insertJob(job, bestData, bestRoute);
                    } else {
                        System.out.println("  ✘ Задача " + job.getId() + " не может быть распределена");
                        badJobs.add(job);
                    }
                }
                System.out.println("  Всего оценок вставки: " + evalCount);
            }

            return badJobs;
        }
    }

}
