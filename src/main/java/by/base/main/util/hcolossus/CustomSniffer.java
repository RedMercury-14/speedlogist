package by.base.main.util.hcolossus;

import java.util.Collection;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.listener.AlgorithmEndsListener;
import com.graphhopper.jsprit.core.algorithm.listener.AlgorithmStartsListener;
import com.graphhopper.jsprit.core.algorithm.listener.IterationEndsListener;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;

public class CustomSniffer implements AlgorithmStartsListener, AlgorithmEndsListener, IterationEndsListener {

	@Override
	public void informIterationEnds(int i, VehicleRoutingProblem problem,
			Collection<VehicleRoutingProblemSolution> solutions) {
		double bestCost = solutions.stream().mapToDouble(VehicleRoutingProblemSolution::getCost).min().orElse(Double.MAX_VALUE);
        System.out.println("Итерация " + i + " завершена. Лучшая стоимость: " + bestCost);
		
	}

	@Override
	public void informAlgorithmEnds(VehicleRoutingProblem problem,
			Collection<VehicleRoutingProblemSolution> solutions) {
		double bestCost = solutions.stream().mapToDouble(VehicleRoutingProblemSolution::getCost).min().orElse(Double.MAX_VALUE);
        System.out.println("Алгоритм завершён. Лучшая стоимость: " + bestCost);
		
	}

	@Override
	public void informAlgorithmStarts(VehicleRoutingProblem problem, VehicleRoutingAlgorithm algorithm,
			Collection<VehicleRoutingProblemSolution> solutions) {
		System.out.println("Алгоритм запущен");
		
	}
	
	

}
