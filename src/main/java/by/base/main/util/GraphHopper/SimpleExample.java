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
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter.Print;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;

public class SimpleExample {	
	
	public static void main(String[] args) {
		/*
		 * создайте выходную папку
		 */
		File dir = new File("D:\\output\\");
		// if the directory does not exist, create it
		if (!dir.exists()){
			System.out.println("creating directory ./output");
			boolean result = dir.mkdir();  
			if(result) System.out.println("./output created");  
		}
		
		/*
		 * получите конструктор типов транспортных средств и создайте тип с typeId "vehicleType" и одним измерением вместимости, т. е. весом, и значением измерения вместимости, равным 2.
		 */
		final int WEIGHT_INDEX = 0;
		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(WEIGHT_INDEX, 10);
		VehicleTypeImpl vehicleType = vehicleTypeBuilder.build();
		
		/*
		 * возьмите конструктор транспортных средств и постройте транспортное средство, расположенное в (10,10), с типом «vehicleType»
		 */
		Builder vehicleBuilder = VehicleImpl.Builder.newInstance("Фура");
		vehicleBuilder.setStartLocation(Location.newInstance(53.808867, 27.775884));
		vehicleBuilder.setType(vehicleType);
		VehicleImpl vehicle = vehicleBuilder.build();
		
		/*
		 * построить службы в необходимых местах, каждая с потребностью в мощности 1..
		 */
		Service service1 = Service.Builder.newInstance("Магазин 1").addSizeDimension(WEIGHT_INDEX, 3).setLocation(Location.newInstance(52.422661, 31.314920)).build();
		Service service2 = Service.Builder.newInstance("Магазин 2").addSizeDimension(WEIGHT_INDEX, 5).setLocation(Location.newInstance(53.107514, 30.037660)).build();
		
		Service service3 = Service.Builder.newInstance("Магазин 3").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(52.806444, 29.422673)).build();
		Service service4 = Service.Builder.newInstance("Магазин 4").addSizeDimension(WEIGHT_INDEX, 6).setLocation(Location.newInstance(53.042774, 28.318940)).build();
		
		
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.addVehicle(vehicle);
		vrpBuilder.addJob(service4).addJob(service2).addJob(service3).addJob(service1);

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
		
		bestSolution.getRoutes().forEach(r-> {
			System.out.println(r.getTourActivities());
			r.getActivities().forEach(a-> System.out.println(a.getName() + " -- " + a.getLocation().getId() + "-id " + a.getLocation().getIndex() + "-index " + a.getLocation().getName() + "-name"));
		});
		
		new Plotter(problem, bestSolution).setLabel(com.graphhopper.jsprit.analysis.toolbox.Plotter.Label.ID).plot("D:\\output/solution.png", "изображенька");
		
		new GraphStreamViewer(problem, bestSolution).labelWith(Label.ID).setRenderDelay(400).display();
		
		

	}

}
