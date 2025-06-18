package by.base.main.util.GraphHopper;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;

public class MultiStoreDelivery {
	
	public static void main(String[] args) {
        // 1. Создаем тип транспортного средства (грузоподъемность и объем)
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("truckType")
                .addCapacityDimension(0, 100)  // грузоподъемность (кг)
                .addCapacityDimension(1, 20);   // кол-во паллет

        VehicleType vehicleType = vehicleTypeBuilder.build();

        // 2. Создаем транспортные средства (можно несколько)
        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("truck1")
                .setStartLocation(Location.newInstance(0, 0)) // депо (координаты)
                .setType(vehicleType);

        VehicleImpl vehicle = vehicleBuilder.build();

        // 3. Создаем заказы (магазины)
        Shipment.Builder shipmentBuilder1 = Shipment.Builder.newInstance("shop1")
                .addSizeDimension(0, 30)  // вес (кг)
                .addSizeDimension(1, 5)   // паллеты
                .setPickupLocation(Location.newInstance(0, 0)) // забираем из депо
                .setDeliveryLocation(Location.newInstance(10, 10)); // доставляем в магазин

        Shipment shop1 = shipmentBuilder1.build();

        Shipment.Builder shipmentBuilder2 = Shipment.Builder.newInstance("shop2")
                .addSizeDimension(0, 50)  // вес (кг)
                .addSizeDimension(1, 8)   // паллеты
                .setPickupLocation(Location.newInstance(0, 0))
                .setDeliveryLocation(Location.newInstance(20, 5));

        Shipment shop2 = shipmentBuilder2.build();

        // 4. Создаем проблему (задачу маршрутизации)
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);
        vrpBuilder.addJob(shop1).addJob(shop2);

        VehicleRoutingProblem problem = vrpBuilder.build();

        // 5. Решаем задачу (используем стандартный алгоритм)
        com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm algorithm =
                com.graphhopper.jsprit.core.algorithm.box.Jsprit.createAlgorithm(problem);

        VehicleRoutingProblemSolution solution = Solutions.bestOf(algorithm.searchSolutions());

        // 6. Выводим результат
        SolutionPrinter.print(problem, solution, SolutionPrinter.Print.VERBOSE);

        // 7. Визуализация (опционально)
        new Plotter(problem, solution).plot("D:\\result/route.png", "Route");
        new GraphStreamViewer(problem, solution).setRenderDelay(200).display();
    }

}
