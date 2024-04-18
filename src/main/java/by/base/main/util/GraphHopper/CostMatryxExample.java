package by.base.main.util.GraphHopper;

import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.graphhopper.jsprit.io.algorithm.VehicleRoutingAlgorithms;

import java.util.Collection;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;

public class CostMatryxExample {
	
	public static void main(String[] args) {
		
		VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, 2).setCostPerDistance(1).build();
		VehicleImpl vehicle = VehicleImpl.Builder.newInstance("vehicle").setStartLocation(Location.Builder.newInstance().setId("0").build()).setType(type).build();
		
		Service s1 = Service.Builder.newInstance("1").setLocation(Location.Builder.newInstance().setId("1").build()).addSizeDimension(0, 1).build();
		Service s2 = Service.Builder.newInstance("2").setLocation(Location.Builder.newInstance().setId("2").build()).addSizeDimension(0, 1).build();
		Service s3 = Service.Builder.newInstance("3").setLocation(Location.Builder.newInstance().setId("3").build()).addSizeDimension(0, 1).build();
		
		
		/*
		 * Assume the following symmetric distance-matrix
		 * from,to,distance
		 * 0,1,10.0
		 * 0,2,20.0
		 * 0,3,5.0
		 * 1,2,4.0
		 * 1,3,1.0
		 * 2,3,2.0
		 * 
		 * and this time-matrix
		 * 0,1,5.0
		 * 0,2,10.0
		 * 0,3,2.5
		 * 1,2,2.0
		 * 1,3,0.5
		 * 2,3,1.0
		 */
		//define a matrix-builder building a symmetric matrix
		VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
		costMatrixBuilder.addTransportDistance("0", "1", 10.0);
		costMatrixBuilder.addTransportDistance("0", "2", 20.0);
		costMatrixBuilder.addTransportDistance("0", "3", 5.0);
		costMatrixBuilder.addTransportDistance("1", "2", 4.0);
		costMatrixBuilder.addTransportDistance("1", "3", 1.0);
		costMatrixBuilder.addTransportDistance("2", "3", 2.0);
		
//		costMatrixBuilder.addTransportTime("0", "1", 10.0);
//		costMatrixBuilder.addTransportTime("0", "2", 20.0);
//		costMatrixBuilder.addTransportTime("0", "3", 5.0);
//		costMatrixBuilder.addTransportTime("1", "2", 4.0);
//		costMatrixBuilder.addTransportTime("1", "3", 1.0);
//		costMatrixBuilder.addTransportTime("2", "3", 2.0);
		
		VehicleRoutingTransportCostsMatrix costMatrix = costMatrixBuilder.build();
		
		VehicleRoutingProblem vrp = VehicleRoutingProblem.Builder.newInstance().setFleetSize(FleetSize.INFINITE).setRoutingCost(costMatrix)
				.addVehicle(vehicle).addJob(s1).addJob(s2).addJob(s3).build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "D:\\output/input/algorithmConfig_fix.xml");
		
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);
		
//		new GraphStreamViewer(vrp, Solutions.bestOf(solutions)).labelWith(Label.ID).setRenderDelay(400).display();
		
//		new Plotter(vrp, Solutions.bestOf(solutions)).plot("D:\\output/yo.png", "po");
		
		new Plotter(vrp, Solutions.bestOf(solutions)).setLabel(com.graphhopper.jsprit.analysis.toolbox.Plotter.Label.ID).plot("D:\\output/solution.png", "изображенька");
		
	}

}
