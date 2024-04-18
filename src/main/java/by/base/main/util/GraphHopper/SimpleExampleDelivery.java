package by.base.main.util.GraphHopper;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.box.SchrimpfFactory;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter.Print;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;

public class SimpleExampleDelivery {

	public static void main(String[] args) {
		/*
		 * создайте выходную папку
		 */
		File dir = new File("D:\\output\\");
		// if the directory does not exist, create it
		if (!dir.exists()) {
			System.out.println("creating directory ./output");
			boolean result = dir.mkdir();
			if (result)
				System.out.println("./output created");
		}

		/*
		 * получите конструктор типов транспортных средств и создайте тип с typeId
		 * "vehicleType" и одним измерением вместимости, т. е. весом, и значением
		 * измерения вместимости, равным 2.
		 */
		final int WEIGHT_INDEX = 0;
		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
				.addCapacityDimension(WEIGHT_INDEX, 3);
		VehicleTypeImpl vehicleType = vehicleTypeBuilder.build();

		/*
		 * возьмите конструктор транспортных средств и постройте транспортное средство,
		 * расположенное в (10,10), с типом «vehicleType»
		 */
		Builder vehicleBuilder = VehicleImpl.Builder.newInstance("Фура");
		vehicleBuilder.setStartLocation(Location.newInstance(53.808867, 27.775884));
		vehicleBuilder.setType(vehicleType);
		VehicleImpl vehicle = vehicleBuilder.build();

		/*
		 * построить службы в необходимых местах, каждая с потребностью в мощности 1..
		 */
		Shipment shipment1 = Shipment.Builder.newInstance("1").addSizeDimension(0, 1)
				.setPickupLocation(Location.newInstance(5, 7)).setDeliveryLocation(Location.newInstance(6, 9)).build();
		Shipment shipment2 = Shipment.Builder.newInstance("2").addSizeDimension(0, 1)
				.setPickupLocation(Location.newInstance(5, 13)).setDeliveryLocation(Location.newInstance(6, 11))
				.build();
		Shipment shipment3 = Shipment.Builder.newInstance("3").addSizeDimension(0, 1)
				.setPickupLocation(Location.newInstance(15, 7)).setDeliveryLocation(Location.newInstance(14, 9))
				.build();
		Shipment shipment4 = Shipment.Builder.newInstance("4").addSizeDimension(0, 1)
				.setPickupLocation(Location.newInstance(15, 13)).setDeliveryLocation(Location.newInstance(14, 11))
				.build();

		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.addVehicle(vehicle);
		vrpBuilder.addJob(shipment1).addJob(shipment2).addJob(shipment3).addJob(shipment4);

		VehicleRoutingProblem problem = vrpBuilder.build();

		/*
		 * получить готовый алгоритм.
		 */
		VehicleRoutingAlgorithm algorithm = new SchrimpfFactory().createAlgorithm(problem);

		/*
		 * и найдите решение
		 */
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		/*
		 * получить лучшее
		 */
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

//		SolutionPlotter.plotSolutionAsPNG(problem, bestSolution, "output/solution.png", "solution");

		new Plotter(problem, bestSolution).plot("D:\\output/solution.png", "изображенька");

		new GraphStreamViewer(problem, bestSolution).labelWith(Label.ID).setRenderDelay(400).display();

	}

}
