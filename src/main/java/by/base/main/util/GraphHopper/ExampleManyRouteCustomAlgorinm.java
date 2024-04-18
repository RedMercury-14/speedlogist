package by.base.main.util.GraphHopper;

import java.io.File;
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
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.box.SchrimpfFactory;
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
import com.graphhopper.jsprit.core.algorithm.ruin.distance.JobDistance;
import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.analysis.SolutionAnalyser;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.cost.TransportDistance;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.SolutionCostCalculator;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.FiniteFleetManagerFactory;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleFleetManager;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter.Print;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.instance.reader.CordeauReader;
import com.graphhopper.jsprit.io.algorithm.VehicleRoutingAlgorithms;
import com.graphhopper.jsprit.io.problem.VrpXMLReader;


public class ExampleManyRouteCustomAlgorinm {	
	
public static void main(String[] args) {
		
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
		Service test = Service.Builder.newInstance("test").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.808867, 26.775884)).build();
		
		vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4).addJob(service5)
			.addJob(service6).addJob(service7).addJob(service8).addJob(service9).addJob(service10)
			.addJob(service11).addJob(service12).addJob(service13).addJob(service14).addJob(service15)
			.addJob(service16).addJob(service17).addJob(service18).addJob(service19);
		
//		vrpBuilder.addJob(test);
		
		
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
		Coordinate firstDepotCoord = Coordinate.newInstance(53.808867, 27.775884);
//		Coordinate second = Coordinate.newInstance(33, -33);
		
		int depotCounter = 1;
			for(int i=0;i<nuOfVehicles;i++){
				VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
                        .addCapacityDimension(0, capacity).setCostPerDistance(1.0).build();
				String vehicleId = depotCounter + "_" + (i+1) + "_фура";
				VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
				vehicleBuilder.setStartLocation(Location.Builder.newInstance().setCoordinate(firstDepotCoord).build());
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
		vrpBuilder.setFleetSize(FleetSize.FINITE);
		
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
		VehicleRoutingAlgorithm vra = createAlgorithm(vrp);
		vra.setMaxIterations(10);
        vra.getAlgorithmListeners().addListener(new StopWatch(),Priority.HIGH);
		vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("D:\\output/progress.png"));
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		System.out.println("All solutions = " + solutions.size());
		
		SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);

		new Plotter(vrp, Solutions.bestOf(solutions)).setLabel(com.graphhopper.jsprit.analysis.toolbox.Plotter.Label.ID).plot("D:\\output/развоз.png", "Тест развоза");
		
		new GraphStreamViewer(vrp,Solutions.bestOf(solutions)).labelWith(Label.ID).setRenderDelay(50).display();
	}

	public static VehicleRoutingAlgorithm createAlgorithm(final VehicleRoutingProblem vrp){
	
	    VehicleFleetManager fleetManager = new FiniteFleetManagerFactory(vrp.getVehicles()).createFleetManager();
	    StateManager stateManager = new StateManager(vrp);
	    ConstraintManager constraintManager = new ConstraintManager(vrp,stateManager);
	
	    /*
	     * insertion strategies
	     */
	    //my custom best insertion
	    MyBestStrategy best = new MyBestStrategy(vrp,fleetManager,stateManager,constraintManager);
	
	    //regret insertion
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
	    RuinStrategy randomRuin = new RandomRuinStrategyFactory(0.5).createStrategy(vrp);
	    
	    
	    RuinStrategy radialRuin = new RadialRuinStrategyFactory(0.3, new AvgServiceAndShipmentDistance(vrp.getTransportCosts())).createStrategy(vrp);
	    
//	    RuinStrategy radialRuin = new RadialRuinStrategyFactory(0.3, new CustomAvgServiceAndShipmentDistance(vrp.getTransportCosts())).createStrategy(vrp);
	
		/*
		 * objective function
		 */
	    SolutionCostCalculator objectiveFunction = getObjectiveFunction(vrp);
	
	    SearchStrategy firstStrategy = new SearchStrategy("firstStrategy", new SelectBest(), new GreedyAcceptance(1), objectiveFunction);
	    firstStrategy.addModule(new RuinAndRecreateModule("randRuinRegretIns", regret, randomRuin));
	
	    SearchStrategy secondStrategy = new SearchStrategy("secondStrategy", new SelectBest(), new GreedyAcceptance(1), objectiveFunction);
	    secondStrategy.addModule(new RuinAndRecreateModule("radRuinRegretIns", regret, radialRuin));
	
	    SearchStrategy thirdStrategy = new SearchStrategy("thirdStrategy", new SelectBest(), new GreedyAcceptance(1), objectiveFunction);
	    secondStrategy.addModule(new RuinAndRecreateModule("radRuinBestIns", regret, radialRuin));
	
	    PrettyAlgorithmBuilder prettyAlgorithmBuilder = PrettyAlgorithmBuilder.newInstance(vrp,fleetManager,stateManager,constraintManager);
	    final VehicleRoutingAlgorithm vra = prettyAlgorithmBuilder
	            .withStrategy(firstStrategy, 0.1).withStrategy(secondStrategy, 0.5).withStrategy(thirdStrategy, 0.5)
	            .addCoreStateAndConstraintStuff()
	            .constructInitialSolutionWith(regret, objectiveFunction)
	            .build();
	
	    //if you want to switch on/off strategies or adapt their weight within the search, you can do the following
	    //e.g. from iteration 50 on, switch off first strategy
	    //switch on again at iteration 90 with slightly higher weight
	    IterationStartsListener strategyAdaptor = new IterationStartsListener() {
	        @Override
	        public void informIterationStarts(int i, VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> solutions) {
	            if(i == 50){
	                vra.getSearchStrategyManager().informStrategyWeightChanged("firstStrategy",0.0);
	                System.out.println("switched off firstStrategy");
	            }
	            if(i == 90){
	                vra.getSearchStrategyManager().informStrategyWeightChanged("firstStrategy",0.7);
	                System.out.println("switched on firstStrategy again with higher weight");
	            }
	        }
	    };
	    vra.addListener(strategyAdaptor);
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
	
	public static class MyBestStrategy extends AbstractInsertionStrategy {

        private JobInsertionCostsCalculatorLight insertionCalculator;


        public MyBestStrategy(VehicleRoutingProblem vrp, VehicleFleetManager fleetManager, StateManager stateManager, ConstraintManager constraintManager) {
            super(vrp);
            insertionCalculator = JobInsertionCostsCalculatorLightFactory.createStandardCalculator(vrp, fleetManager, stateManager, constraintManager);
        }

        @Override
        public Collection<Job> insertUnassignedJobs(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
            List<Job> badJobs = new ArrayList<Job>();
            List<Job> unassigned = new ArrayList<Job>(unassignedJobs);
            Collections.shuffle(unassigned, random);

            for(Job j : unassigned){

                InsertionData bestInsertionData = InsertionData.createEmptyInsertionData();
                VehicleRoute bestRoute = null;
                //look for inserting unassigned job into existing route
                for(VehicleRoute r : vehicleRoutes){
                    InsertionData insertionData = insertionCalculator.getInsertionData(j,r,bestInsertionData.getInsertionCost());
                    if(insertionData instanceof InsertionData.NoInsertionFound) continue;
                    if(insertionData.getInsertionCost() < bestInsertionData.getInsertionCost()){
                        bestInsertionData = insertionData;
                        bestRoute = r;
                    }
                }
                //try whole new route
                VehicleRoute empty = VehicleRoute.emptyRoute();
                InsertionData insertionData = insertionCalculator.getInsertionData(j, empty, bestInsertionData.getInsertionCost());
                if(!(insertionData instanceof InsertionData.NoInsertionFound)) {
                    if (insertionData.getInsertionCost() < bestInsertionData.getInsertionCost()) {
                        vehicleRoutes.add(empty);
                        insertJob(j, insertionData, empty);
                    }
                }
                else {
                    if(bestRoute != null) insertJob(j,bestInsertionData,bestRoute);
                    else badJobs.add(j);
                }
            }
            return badJobs;
        }


    }

}
