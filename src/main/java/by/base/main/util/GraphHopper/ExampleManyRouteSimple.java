package by.base.main.util.GraphHopper;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.analysis.toolbox.StopWatch;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.box.SchrimpfFactory;
import com.graphhopper.jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListeners.Priority;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter.Print;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.instance.reader.CordeauReader;
import com.graphhopper.jsprit.io.algorithm.VehicleRoutingAlgorithms;

public class ExampleManyRouteSimple {	
	
	public static void main(String[] args) {
		
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		/*
		 * Read cordeau-instance p01, BUT only its services without any vehicles 
		 */
		new CordeauReader(vrpBuilder).read("D:\\output/input/p08");
		
		/*
		 * add vehicles with its depots
		 * 2 depots:
		 * (-33,33)
		 * (33,-33)
		 * 
		 * each with 14 vehicles each with a capacity of 500 and a maximum duration of 310
		 */
		int nuOfVehicles = 13;
		int capacity = 500;
		double maxDuration = 310;
		Coordinate firstDepotCoord = Coordinate.newInstance(-33, 33);
		Coordinate second = Coordinate.newInstance(33, -33);
		
		int depotCounter = 1;
		for(Coordinate depotCoord : Arrays.asList(firstDepotCoord,second)){
			for(int i=0;i<nuOfVehicles;i++){
				VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(depotCounter + "_type")
                        .addCapacityDimension(0, capacity).setCostPerDistance(1.0).build();
				String vehicleId = depotCounter + "_" + (i+1) + "_vehicle";
				VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
				vehicleBuilder.setStartLocation(Location.newInstance(depotCoord.getX(), depotCoord.getY()));
				vehicleBuilder.setType(vehicleType);
				vehicleBuilder.setLatestArrival(maxDuration);
				VehicleImpl vehicle = vehicleBuilder.build();
				vrpBuilder.addVehicle(vehicle);
			}
			depotCounter++;
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
		System.out.println("1");
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp,12, "D:\\output/input/algorithmConfig.xml");
		System.out.println(2);
		vra.setMaxIterations(10);
		System.out.println(3);
        vra.getAlgorithmListeners().addListener(new StopWatch(),Priority.HIGH);
        System.out.println(4);
		vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("D:\\output/progress.png"));
		System.out.println(5);
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		System.out.println("6");
		
		SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);

		new Plotter(vrp, Solutions.bestOf(solutions)).plot("D:\\output/p08_solution.png", "p08");
		
		new GraphStreamViewer(vrp,Solutions.bestOf(solutions)).setRenderDelay(50).display();
	}

}
