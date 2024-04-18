package by.base.main.util.GraphHopper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
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
import com.graphhopper.jsprit.core.algorithm.ruin.RadialRuinStrategyFactory;
import com.graphhopper.jsprit.core.algorithm.ruin.RandomRuinStrategyFactory;
import com.graphhopper.jsprit.core.algorithm.ruin.RuinStrategy;
import com.graphhopper.jsprit.core.algorithm.ruin.distance.AvgServiceAndShipmentDistance;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.analysis.SolutionAnalyser;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.cost.TransportDistance;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.SolutionCostCalculator;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.FiniteFleetManagerFactory;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleFleetManager;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.graphhopper.util.CustomModel;
import com.graphhopper.util.shapes.GHPoint;

import by.base.main.controller.ajax.MainRestController;
import by.base.main.model.Shop;
import by.base.main.service.ShopService;

@org.springframework.stereotype.Service
public class JSpiritMachine {

	@Autowired
	private RoutingMachine routingMachine;

	@Autowired
	private MainRestController mainRestController;
	
	@Autowired
	private ShopService shopService;
	
	private Map<String, Double> matrix = new HashMap<String, Double>();
	
	public String optimization50Points(List<Integer> shopList, List<Integer> pallHasShops, List<VehicleImpl> vehicleImpls, Integer stock, Integer iteration) {
		Map<Integer, Shop> allShop =  shopService.getShopMap();
		VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
		List<Integer> shopListForDIstance = shopList;
		shopListForDIstance.add(stock);
		for (Integer integer : shopListForDIstance) {
			for (int i = 0; i < shopListForDIstance.size(); i++) {				
				if(integer == shopListForDIstance.get(i)) {
					continue;
				}
				Integer integerTo = shopListForDIstance.get(i);
				double sum = 0;
				
//				System.out.println(integer + " --> " + integerTo);
				Shop from = allShop.get(integer);
				Shop to = allShop.get(integerTo);
				
				if(matrix.containsKey(from.getNumshop()+"-"+to.getNumshop())) {
					sum = matrix.get(from.getNumshop()+"-"+to.getNumshop());
				}else {
					double fromLat = Double.parseDouble(from.getLat());
			        double fromLng = Double.parseDouble(from.getLng());
			        
			        double toLat = Double.parseDouble(to.getLat());
			        double toLng = Double.parseDouble(to.getLng());
			        
			        CustomModel model = null;
					try {
						model = routingMachine.parseJSONFromClientCustomModel(null);
					} catch (ParseException e) {
						e.printStackTrace();
					}
			        
			        GHRequest req = routingMachine.GHRequestBilder(fromLat, fromLng, model, toLat, toLng);
			        GraphHopper hopper = routingMachine.getGraphHopper();
			        GHResponse rsp = hopper.route(req);
			        ResponsePath path = rsp.getBest();
			        sum = path.getDistance();
			        matrix.put(from.getNumshop()+"-"+to.getNumshop(), sum);
				}
		        System.out.println(integer + " --> " + integerTo + " --> " + sum);
		        costMatrixBuilder.addTransportDistance(integer.toString(), integerTo.toString(), roundВouble(sum/1000, 2));
		        costMatrixBuilder.addTransportTime(integer.toString(), integerTo.toString(), roundВouble(sum/1000, 2));
			}			
		}
		VehicleRoutingTransportCostsMatrix costMatrix = costMatrixBuilder.build();
		System.out.println("Матрица заполнена!");
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		
		//создаём объекты магазинов
		for (Integer integer : shopList) {
			int i = shopList.indexOf(integer);
			if(integer == stock) {
				continue;
			}
			int pall = pallHasShops.get(i);
			String nameIsCOstFromStock = matrix.get("1700-"+integer.toString())+""; // кладём в имя расстояние от склада то дочки, для последующего сортирования по методу Якубова
			Service point = Service.Builder.newInstance(integer.toString()).setLocation(Location.Builder.newInstance().setId(integer.toString()).build()).addSizeDimension(0, pall).setName(nameIsCOstFromStock).build();
			vrpBuilder.addJob(point);
		}
		
		
		
		//загружаем тачки
		vehicleImpls.forEach(v->{
			vrpBuilder.addVehicle(v);
		});
		
		//создаём проблему 
		vrpBuilder.setFleetSize(FleetSize.FINITE);
		VehicleRoutingProblem vrp = vrpBuilder.setRoutingCost(costMatrix).build();
		
		//пробуем по собственному алгоритму
		VehicleRoutingAlgorithm vra = createAlgorithm(vrp, routingMachine, costMatrixBuilder);
		
		vra.setMaxIterations(iteration);
        vra.getAlgorithmListeners().addListener(new StopWatch(),Priority.HIGH);
		vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("D:\\output/progress.png"));
		
		
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);
//		MyPrinter.print(vrp, Solutions.bestOf(solutions), MyPrinter.Print.VERBOSE);
		
		String result = "";
		Collection<VehicleRoute> routes = Solutions.bestOf(solutions).getRoutes();
		int i = 1;
		for (VehicleRoute vehicleRoute : routes) {
			result = result + "============Маршрут " + i+"============\n";
//			result = result + vehicleRoute.getVehicle().getId() + "(" + vehicleRoute.getVehicle().getEarliestDeparture()+")\n";
			List<TourActivity> a = vehicleRoute.getActivities();
			for (TourActivity ta : a) {				
				result = result+vehicleRoute.getVehicle().getId() + "(" + vehicleRoute.getVehicle().getType().getCapacityDimensions().get(0)+") --->" + ta.getLocation().getId() + "("+ta.getSize().get(0)+")"+"\n";
			}
			i++;
		}
		System.out.println("\n\n");
		for (VehicleRoute vehicleRoute : routes) {
			List<TourActivity> a = vehicleRoute.getActivities();
			for (TourActivity ta : a) {				
				result = result+ta.getLocation().getId()+"\n";
			}
			result = result+"--------\n";
		}
		
		return result;		
	}
	
	public void testMethod() {
		List<Integer> shopList = new ArrayList<Integer>(); //список магазов в фронта
		shopList.add(2420);
		shopList.add(2507);
		shopList.add(2445);
		shopList.add(2573);
		shopList.add(2309);
		shopList.add(2328);
		shopList.add(2354);
		shopList.add(2041);
		shopList.add(926);
		shopList.add(2396);
		shopList.add(369);
		shopList.add(2363);
		shopList.add(379);
		shopList.add(2350);
		shopList.add(2443);
		shopList.add(399);
		shopList.add(2362);
		shopList.add(2230);
		shopList.add(283);
//		shopList.add(2421);
		shopList.add(1700);
		
		Map<Integer, Shop> allShop =  shopService.getShopMap();
		
		VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
		//заполняем матрицу по магазинам
		System.out.println("Заполняем матрицу --> ");
		for (Integer integer : shopList) {
			for (int i = 0; i < shopList.size(); i++) {				
				if(integer == shopList.get(i)) {
					continue;
				}
				Integer integerTo = shopList.get(i);
				System.out.println(integer + " --> " + integerTo);
				Shop from = allShop.get(integer);
				Shop to = allShop.get(integerTo);
				
				double sum = 0;
		        double fromLat = Double.parseDouble(from.getLat());
		        double fromLng = Double.parseDouble(from.getLng());
		        
		        double toLat = Double.parseDouble(to.getLat());
		        double toLng = Double.parseDouble(to.getLng());
		        
		        CustomModel model = null;
				try {
					model = routingMachine.parseJSONFromClientCustomModel(null);
				} catch (ParseException e) {
					e.printStackTrace();
				}
		        
		        GHRequest req = routingMachine.GHRequestBilder(fromLat, fromLng, model, toLat, toLng);
		        GraphHopper hopper = routingMachine.getGraphHopper();
		        GHResponse rsp = hopper.route(req);
		        ResponsePath path = rsp.getBest();
		        sum = path.getDistance();
		        costMatrixBuilder.addTransportDistance(integer.toString(), integerTo.toString(), roundВouble(sum/1000, 2));
		        costMatrixBuilder.addTransportTime(integer.toString(), integerTo.toString(), roundВouble(sum/1000, 2));
			}
		}
		VehicleRoutingTransportCostsMatrix costMatrix = costMatrixBuilder.build();
		System.out.println("Матрица заполнена!");
		
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		
		//создаём объекты магазинов
		for (Integer integer : shopList) {
			if(integer == 1700) {
				continue;
			}
			Service point = Service.Builder.newInstance(integer.toString()).setLocation(Location.Builder.newInstance().setId(integer.toString()).build()).addSizeDimension(0, 3).build();
			vrpBuilder.addJob(point);
		}
		
		//обавляем разные типы машин 
		int nuOfVehicles = 2;
		int capacity = 20;
		double maxDuration = 1000;
		
		int depotCounter = 1;
		for(int i=0;i<nuOfVehicles;i++){
				VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
                        .addCapacityDimension(0, capacity).setCostPerDistance(1.0).build();
				String vehicleId = depotCounter + "_" + (i+1) + "_фура";
				VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
				vehicleBuilder.setStartLocation(Location.Builder.newInstance().setId("1700").build());
				vehicleBuilder.setType(vehicleType);
				vehicleBuilder.setLatestArrival(maxDuration);
				VehicleImpl vehicle = vehicleBuilder.build();
				vrpBuilder.addVehicle(vehicle);
		}
			
			
			
			int nuOfVehiclesSmall = 4;
			int capacitySmall = 10;
			double maxDurationSmall = 600;
			
				for(int i=0;i<nuOfVehiclesSmall;i++){
					VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter+1 + "_type")
	                        .addCapacityDimension(0, capacitySmall).setCostPerDistance(1.0).build();
					String vehicleId = depotCounter+1 + "_" + (i+1) + "_малыш";
					VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
					vehicleBuilder.setStartLocation(Location.Builder.newInstance().setId("1700").build());
					vehicleBuilder.setType(vehicleType);
					vehicleBuilder.setLatestArrival(maxDurationSmall);
					VehicleImpl vehicle = vehicleBuilder.build();
					vrpBuilder.addVehicle(vehicle);
				}
				
		//создаём проблему 
				vrpBuilder.setFleetSize(FleetSize.FINITE);
				VehicleRoutingProblem vrp = vrpBuilder.setRoutingCost(costMatrix).build();
				
				//пробуем по собственному алгоритму
				VehicleRoutingAlgorithm vra = createAlgorithm(vrp, routingMachine, costMatrixBuilder);
				
				
				Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
				
				SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);
				
				Solutions.bestOf(solutions).getRoutes().forEach(r-> {
					System.out.println(r.getTourActivities());
					r.getActivities().forEach(a-> System.out.println(a.getLocation().getId() + "-.getLocation().getId() "));
				});
	}
	
	@Deprecated
	public void oldTestMethod() {
		VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
		
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		
		
		
		final int WEIGHT_INDEX = 0;
		Service service1 = Service.Builder.newInstance("2420 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.432760, 30.081273)).build();
		Service service2 = Service.Builder.newInstance("2507 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.367933, 30.352870)).build();
		Service service3 = Service.Builder.newInstance("2445 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.470185, 30.811819)).build();
		Service service4 = Service.Builder.newInstance("2573 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.383926, 31.340801)).build();
		Service service5 = Service.Builder.newInstance("2309 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(51.443972, 30.556509)).build();
		Service service6 = Service.Builder.newInstance("2328 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.251612, 29.830131)).build();
		Service service7 = Service.Builder.newInstance("2354 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.356054, 31.050825)).build();
		Service service8 = Service.Builder.newInstance("2041 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.563911, 31.174559)).build();
		Service service9 = Service.Builder.newInstance("926 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(53.393042, 29.008504)).build();
		Service service10 = Service.Builder.newInstance("2396 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.536493, 24.989865)).build();
		Service service11 = Service.Builder.newInstance("369 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.466430, 25.181534)).build();
		Service service12 = Service.Builder.newInstance("2363 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.283385, 24.455367)).build();
		Service service13 = Service.Builder.newInstance("379 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.367179, 23.383425)).build();
		Service service14 = Service.Builder.newInstance("2350 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance( 51.931634, 23.662927)).build();
		Service service15 = Service.Builder.newInstance("2443 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(51.783312, 24.054063)).build();
		Service service16 = Service.Builder.newInstance("399 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.102650, 23.710870)).build();
		Service service17 = Service.Builder.newInstance("2362 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance( 52.049800, 23.704681)).build();
		Service service18 = Service.Builder.newInstance("2230 М").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance( 53.808057, 30.967973)).build();
		Service service19 = Service.Builder.newInstance("283 Н").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(55.350986, 29.307491)).build();
		Service service20 = Service.Builder.newInstance("2421 Х").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(55.394719, 26.630089)).build();
		
//		Service s1 = Service.Builder.newInstance("2420 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2420 Г").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.432760, 30.081273)).build()).build();
//		Service s2 = Service.Builder.newInstance("2507 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2507 Г").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.367933, 30.352870)).build()).build();
//		Service s3 = Service.Builder.newInstance("2445 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2445 Г").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.470185, 30.811819)).build()).build();
//		Service s4 = Service.Builder.newInstance("2573 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2573 Г").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.383926, 31.340801)).build()).build();
//		Service s5 = Service.Builder.newInstance("2309 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2309 Г").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(51.443972, 30.556509)).build()).build();
//		Service s6 = Service.Builder.newInstance("2328 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2328 Г").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.251612, 29.830131)).build()).build();
//		Service s7 = Service.Builder.newInstance("2354 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2354 Г").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.356054, 31.050825)).build()).build();
//		Service s8 = Service.Builder.newInstance("2041 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2041 Г").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.563911, 31.174559)).build()).build();
//		Service s9 = Service.Builder.newInstance("926 Г").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("926 Г").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(53.393042, 29.008504)).build()).build();
//		Service s10 = Service.Builder.newInstance("2396 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2396 Б").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.536493, 24.989865)).build()).build();
//		Service s11 = Service.Builder.newInstance("369 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("369 Б").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.466430, 25.181534)).build()).build();
//		Service s12 = Service.Builder.newInstance("2363 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2363 Б").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.283385, 24.455367)).build()).build();
//		Service s13 = Service.Builder.newInstance("379 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("379 Б").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.367179, 23.383425)).build()).build();
//		Service s14 = Service.Builder.newInstance("2350 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2350 Б").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(51.931634, 23.662927)).build()).build();
//		Service s15 = Service.Builder.newInstance("2443 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2443 Б").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(51.783312, 24.054063)).build()).build();
//		Service s16 = Service.Builder.newInstance("399 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("399 Б").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.102650, 23.710870)).build()).build();
//		Service s17 = Service.Builder.newInstance("2362 Б").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2362 Б").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(52.049800, 23.704681)).build()).build();
//		Service s18 = Service.Builder.newInstance("2230 М").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2230 М").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(53.808057, 30.967973)).build()).build();
//		Service s19 = Service.Builder.newInstance("283 Н").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("283 Н").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(55.350986, 29.307491)).build()).build();
//		Service s20 = Service.Builder.newInstance("2421 Х").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.Builder.newInstance().setId("2421 Х").setCoordinate(com.graphhopper.jsprit.core.util.Coordinate.newInstance(55.394719, 26.630089)).build()).build();
		
		
		
		vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4).addJob(service5)
			.addJob(service6).addJob(service7).addJob(service8).addJob(service9).addJob(service10)
			.addJob(service11).addJob(service12).addJob(service13).addJob(service14).addJob(service15)
			.addJob(service16).addJob(service17).addJob(service18).addJob(service19);
		
//		vrpBuilder.addJob(s1).addJob(s2).addJob(s3).addJob(s4).addJob(s5)
//		.addJob(s6).addJob(s7).addJob(s8).addJob(s9).addJob(s10)
//		.addJob(s11).addJob(s12).addJob(s13).addJob(s14).addJob(s15)
//		.addJob(s16).addJob(s17).addJob(s18).addJob(s19);
		
		
		/*
		 * add vehicles with its depots
		 * 2 depots:
		 * (-33,33)
		 * (33,-33)
		 * 
		 * each with 14 vehicles each with a capacity of 500 and a maximum duration of 310
		 */
		int nuOfVehicles = 1;
		int capacity = 20;
		double maxDuration = 1000;
		com.graphhopper.jsprit.core.util.Coordinate firstDepotCoord = com.graphhopper.jsprit.core.util.Coordinate.newInstance(53.808867, 27.775884);
//		Coordinate second = Coordinate.newInstance(33, -33);
		
		int depotCounter = 1;
			for(int i=0;i<nuOfVehicles;i++){
				VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
                        .addCapacityDimension(0, capacity).setCostPerDistance(1.0).build();
				String vehicleId = depotCounter + "_" + (i+1) + "_фура";
				VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
				vehicleBuilder.setStartLocation(Location.newInstance(firstDepotCoord.getX(), firstDepotCoord.getY()));
				vehicleBuilder.setType(vehicleType);
				vehicleBuilder.setLatestArrival(maxDuration);
				VehicleImpl vehicle = vehicleBuilder.build();
				vrpBuilder.addVehicle(vehicle);
			}
			
			
			
			int nuOfVehiclesSmall = 4;
			int capacitySmall = 10;
			double maxDurationSmall = 8;
			
				for(int i=0;i<nuOfVehiclesSmall;i++){
					VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter+1 + "_type")
	                        .addCapacityDimension(0, capacitySmall).setCostPerDistance(1.0).build();
					String vehicleId = depotCounter+1 + "_" + (i+1) + "_малыш";
					VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
					vehicleBuilder.setStartLocation(Location.newInstance(firstDepotCoord.getX(), firstDepotCoord.getY()));
					vehicleBuilder.setType(vehicleType);
					vehicleBuilder.setLatestArrival(maxDurationSmall);
					VehicleImpl vehicle = vehicleBuilder.build();
					vrpBuilder.addVehicle(vehicle);
				}
		/*
		 * define problem with finite fleet
		 */
		vrpBuilder.setFleetSize(FleetSize.INFINITE);
		
		/*
		 * build the problem
		 */
		VehicleRoutingProblem vrp = vrpBuilder.build();		
		
		
		/*
		 * plot to see how the problem looks like
		 */
//		SolutionPlotter.plotVrpAsPNG(vrp, "output/problem08.png", "p08");

		/*
		 * solve the problem
		 */
//		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp,12, "D:\\output/input/algorithmConfig.xml");
		
		//пробуем по собственному алгоритму
		VehicleRoutingAlgorithm vra = createAlgorithm(vrp, routingMachine, costMatrixBuilder);
		vra.setMaxIterations(10);
        vra.getAlgorithmListeners().addListener(new StopWatch(),Priority.HIGH);
		vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("D:\\output/progress.png"));
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
						
		
		System.out.println("All solutions = " + solutions.size());
		System.err.println(costMatrixBuilder.build().getDistance("2420 Г", "2507 Г"));
		
		SolutionPrinter pp = new SolutionPrinter();
		pp.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);
		
//		SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);

		new Plotter(vrp, Solutions.bestOf(solutions)).setLabel(com.graphhopper.jsprit.analysis.toolbox.Plotter.Label.ID).plot("D:\\output/развоз.png", "Тест развоза");
		
//		new GraphStreamViewer(vrp,Solutions.bestOf(solutions)).labelWith(Label.ID).setRenderDelay(50).display();
	}
	
	// округляем числа до 2-х знаков после запятой
	private static double roundВouble(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}

	/**
	 * Своя конфигурация алгоритма!
	 * 
	 * @param vrp
	 * @param routingMachine
	 * @param costMatrixBuilder
	 * @return
	 */
	private static VehicleRoutingAlgorithm createAlgorithm(final VehicleRoutingProblem vrp,
			RoutingMachine routingMachine, VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder) {

		VehicleFleetManager fleetManager = new FiniteFleetManagerFactory(vrp.getVehicles()).createFleetManager();
		StateManager stateManager = new StateManager(vrp);
		ConstraintManager constraintManager = new ConstraintManager(vrp, stateManager);

		/*
		 * insertion strategies
		 */
		// my custom best insertion
		MyStrategyV2 best = new MyStrategyV2(vrp, fleetManager, stateManager, constraintManager); //по весам
//		MyStrategyV1 best = new MyStrategyV1(vrp, fleetManager, stateManager, constraintManager); //якубова
		
		

		// regret insertion
		InsertionBuilder iBuilder = new InsertionBuilder(vrp, fleetManager, stateManager, constraintManager);
		iBuilder.setInsertionStrategy(InsertionBuilder.Strategy.REGRET);
		RegretInsertion regret = (RegretInsertion) iBuilder.build();
//	    RegretInsertion.DefaultScorer scoringFunction = new RegretInsertion.DefaultScorer(vrp);
//	    scoringFunction.setDepotDistanceParam(0.2);
//	    scoringFunction.setTimeWindowParam(-.2);
//	    regret.setScoringFunction(scoringFunction);

		/*
		 * ruin strategies
		 */
		RuinStrategy randomRuin = new RandomRuinStrategyFactory(0.5).createStrategy(vrp);//0.5

		//

//	    RuinStrategy radialRuin = new RadialRuinStrategyFactory(0.3, new AvgServiceAndShipmentDistance(vrp.getTransportCosts())).createStrategy(vrp);

		RuinStrategy radialRuin = new RadialRuinStrategyFactory(0.5, //0.3
				new CustomAvgServiceAndShipmentDistance(vrp.getTransportCosts(), routingMachine, costMatrixBuilder))
				.createStrategy(vrp);

		/*
		 * objective function
		 */
		SolutionCostCalculator objectiveFunction = getObjectiveFunction(vrp);

		SearchStrategy firstStrategy = new SearchStrategy("firstStrategy", new MySelector(), new GreedyAcceptance(1),
				objectiveFunction);
		firstStrategy.addModule(new RuinAndRecreateModule("randRuinRegretIns", best, randomRuin));

		SearchStrategy secondStrategy = new SearchStrategy("secondStrategy", new MySelector(), new GreedyAcceptance(1),
				objectiveFunction);
		secondStrategy.addModule(new RuinAndRecreateModule("radRuinRegretIns", best, radialRuin));

		SearchStrategy thirdStrategy = new SearchStrategy("thirdStrategy", new MySelector(), new GreedyAcceptance(1),
				objectiveFunction);
		secondStrategy.addModule(new RuinAndRecreateModule("radRuinBestIns", best, radialRuin));

		PrettyAlgorithmBuilder prettyAlgorithmBuilder = PrettyAlgorithmBuilder.newInstance(vrp, fleetManager,
				stateManager, constraintManager);
		
		//дефолтная стратегия
//		final VehicleRoutingAlgorithm vra = prettyAlgorithmBuilder.withStrategy(firstStrategy, 0.5)
//				.withStrategy(secondStrategy, 0.5).withStrategy(thirdStrategy, 0.5).addCoreStateAndConstraintStuff()
//				.constructInitialSolutionWith(best, objectiveFunction).build();
		
		//пробуем по одной стратегии!
		final VehicleRoutingAlgorithm vra = prettyAlgorithmBuilder.withStrategy(secondStrategy, 1).addCoreStateAndConstraintStuff()
				.constructInitialSolutionWith(best, objectiveFunction).build();

		// if you want to switch on/off strategies or adapt their weight within the
		// search, you can do the following
		// e.g. from iteration 50 on, switch off first strategy
		// switch on again at iteration 90 with slightly higher weight
		IterationStartsListener strategyAdaptor = new IterationStartsListener() {
			@Override
			public void informIterationStarts(int i, VehicleRoutingProblem problem,
					Collection<VehicleRoutingProblemSolution> solutions) {
//				problem.getJobsInclusiveInitialJobsInRoutes().forEach((k,v) -> System.out.println(k + "   --   " +v));
//				solutions.forEach(s-> {
//					System.out.print("Итерация " + i +" \n");
//					s.getRoutes().forEach(r-> {
//						r.getActivities().forEach(a-> System.out.println(a.getArrTime() + " | " + a.getLocation().getId() + "-магазин | " + a.getSize() + " | " + r.getVehicle().getId() + "- vehicle |"));
//						
//					});
//					System.out.println("====");
//				});
//				if (i == 50) {
//					vra.getSearchStrategyManager().informStrategyWeightChanged("firstStrategy", 0.0);
//					System.out.println("отключил первую стратегию");
//				}
//				if (i == 90) {
//					vra.getSearchStrategyManager().informStrategyWeightChanged("firstStrategy", 0.7);
//					System.out.println("снова включил первую стратегию с большим весом");
//				}
				vra.getSearchStrategyManager().getWeights().forEach(d-> System.err.println(d));
				vra.getSearchStrategyManager().getStrategies().forEach(s-> System.err.println(s.getId()));
			}
		};
		vra.addListener(strategyAdaptor);
		return vra;

	}

	private static SolutionCostCalculator getObjectiveFunction(final VehicleRoutingProblem vrp) {
		return new SolutionCostCalculator() {

			@Override
			public double getCosts(VehicleRoutingProblemSolution solution) {
				SolutionAnalyser analyser = new SolutionAnalyser(vrp, solution, new TransportDistance() {
					@Override
					public double getDistance(Location from, Location to, double departureTime, Vehicle vehicle) {
						return vrp.getTransportCosts().getTransportCost(from, to, 0., null, null);
					}
				});
				return analyser.getVariableTransportCosts() + solution.getUnassignedJobs().size() * 500.;
			}

		};
	}

	/**
	 * Стратегия заполнения: сначала идёт выборка по заполнению магазинов от потребностей
	 */
	private static class MyStrategyV2 extends AbstractInsertionStrategy {

		private JobInsertionCostsCalculatorLight insertionCalculator;
		
		private Comparator<Job> joComparator = (o1, o2) -> (int) (o2.getSize().get(0) - o1.getSize().get(0));
 
		public MyStrategyV2(VehicleRoutingProblem vrp, VehicleFleetManager fleetManager, StateManager stateManager,
				ConstraintManager constraintManager) {
			super(vrp);
			insertionCalculator = JobInsertionCostsCalculatorLightFactory.createStandardCalculator(vrp, fleetManager,
					stateManager, constraintManager);
		}

		@Override
		public Collection<Job> insertUnassignedJobs(Collection<VehicleRoute> vehicleRoutes,
				Collection<Job> unassignedJobs) {
			List<Job> badJobs = new ArrayList<Job>();
			List<Job> unassigned = new ArrayList<Job>(unassignedJobs); // неназначенные магазы
			unassigned.sort(joComparator);
			System.out.println(unassigned.size() + " - неназначенных магазинов:");
			unassigned.forEach(j-> System.out.println(j.getId() + "= номер магазина "+ j.getSize().get(0) + " потребность магазина " + j.getName() + " расстояние "));

			for (Job j : unassigned) {

				InsertionData bestInsertionData = InsertionData.createEmptyInsertionData();
				VehicleRoute bestRoute = null;
				
				
				// look for inserting unassigned job into existing route
				for (VehicleRoute r : vehicleRoutes) {
					InsertionData insertionData = insertionCalculator.getInsertionData(j, r,
							bestInsertionData.getInsertionCost());
					
					if (insertionData instanceof InsertionData.NoInsertionFound)
						continue;
					if (insertionData.getInsertionCost() < bestInsertionData.getInsertionCost()) {
						bestInsertionData = insertionData;
						bestRoute = r;
					}
				}
				// try whole new route
				VehicleRoute empty = VehicleRoute.emptyRoute();
				InsertionData insertionData = insertionCalculator.getInsertionData(j, empty,
						bestInsertionData.getInsertionCost());
				if (!(insertionData instanceof InsertionData.NoInsertionFound)) {
					if (insertionData.getInsertionCost() < bestInsertionData.getInsertionCost()) {
						vehicleRoutes.add(empty);
						insertJob(j, insertionData, empty);
					}
				} else {
					if (bestRoute != null)
						insertJob(j, bestInsertionData, bestRoute);
					else
						badJobs.add(j);
				}
			}
			
			System.out.println("после распределения неназначенных магазов осталось: " + badJobs.size());
			badJobs.forEach(j-> System.out.println(j));
			return badJobs;
		}

	}
	
	/**
	 * Стратегия по версии Якубова
	 */
	private static class MyStrategyV1 extends AbstractInsertionStrategy {

		private JobInsertionCostsCalculatorLight insertionCalculator;
		
		private Comparator<Job> joComparator = (o1, o2) -> (int) ( Double.parseDouble(o2.getName()) - Double.parseDouble(o1.getName()));

		public MyStrategyV1(VehicleRoutingProblem vrp, VehicleFleetManager fleetManager, StateManager stateManager,
				ConstraintManager constraintManager) {
			super(vrp);
			insertionCalculator = JobInsertionCostsCalculatorLightFactory.createStandardCalculator(vrp, fleetManager,
					stateManager, constraintManager);
		}

		@Override
		public Collection<Job> insertUnassignedJobs(Collection<VehicleRoute> vehicleRoutes,
				Collection<Job> unassignedJobs) {
			List<Job> badJobs = new ArrayList<Job>();
			List<Job> unassigned = new ArrayList<Job>(unassignedJobs); // неназначенные магазы
			unassigned.sort(joComparator);
			System.out.println(unassigned.size() + " - неназначенных магазинов:");
			unassigned.forEach(j-> System.out.println(j));

			for (Job j : unassigned) {

				InsertionData bestInsertionData = InsertionData.createEmptyInsertionData();
				VehicleRoute bestRoute = null;
				
				
				// look for inserting unassigned job into existing route
				for (VehicleRoute r : vehicleRoutes) {
					InsertionData insertionData = insertionCalculator.getInsertionData(j, r,
							bestInsertionData.getInsertionCost());
					
					if (insertionData instanceof InsertionData.NoInsertionFound)
						continue;
					if (insertionData.getInsertionCost() < bestInsertionData.getInsertionCost()) {
						bestInsertionData = insertionData;
						bestRoute = r;
					}
				}
				// try whole new route
				VehicleRoute empty = VehicleRoute.emptyRoute();
				InsertionData insertionData = insertionCalculator.getInsertionData(j, empty,
						bestInsertionData.getInsertionCost());
				if (!(insertionData instanceof InsertionData.NoInsertionFound)) {
					if (insertionData.getInsertionCost() < bestInsertionData.getInsertionCost()) {
						vehicleRoutes.add(empty);
						insertJob(j, insertionData, empty);
					}
				} else {
					if (bestRoute != null)
						insertJob(j, bestInsertionData, bestRoute);
					else
						badJobs.add(j);
				}
			}
			
			System.out.println("после распределения неназначенных магазов осталось: " + badJobs.size());
			badJobs.forEach(j-> System.out.println(j));
			return badJobs;
		}

	}
	
	static class CustomAvgServiceAndShipmentDistance extends AvgServiceAndShipmentDistance {		
		
		private VehicleRoutingTransportCosts costs;
		
		private RoutingMachine routingMachine;
		
		private VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder;
		
		public CustomAvgServiceAndShipmentDistance(VehicleRoutingTransportCosts costs, RoutingMachine routingMachine, VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder) {
			super(costs);
			this.costs = costs;
			this.routingMachine = routingMachine;
			this.costMatrixBuilder = costMatrixBuilder;
		}
		
		@Override
		public double getDistance(Job i, Job j) {
			if (i.equals(j)) return 0.0;			
			
	        return costMatrixBuilder.build().getDistance(i.getId(), j.getId());
		}
		
		private GHRequest GHRequestBilder (double latFrom, double lonFrom, CustomModel model, double latTo, double lonTo) {
			GHRequest request= new GHRequest().addPoint(new GHPoint(latFrom, lonFrom))
					.addPoint(new GHPoint(latTo, lonTo))
//					.setAlgorithm(Parameters.Algorithms.ALT_ROUTE)
//					.putHint(Parameters.Algorithms.AltRoute.MAX_WEIGHT, 10)
//					.putHint(Parameters.Algorithms.AltRoute.MAX_PATHS, 10)
//					.putHint(Parameters.Algorithms.AltRoute.MAX_SHARE, 10)
//					.setAlgorithm(Parameters.Algorithms.DIJKSTRA)
//					.putHint(Parameters.CH.DISABLE, true)
					.setProfile("car_custom")
					.setCustomModel(model);
			return request;
			
		}

	}

}



