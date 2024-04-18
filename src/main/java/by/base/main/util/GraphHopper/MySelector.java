package by.base.main.util.GraphHopper;

import java.util.Collection;

import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.algorithm.selector.SolutionSelector;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;

public class MySelector implements SolutionSelector{

	private static SelectBest selector = null;

    public static SelectBest getInstance() {
        if (selector == null) {
            selector = new SelectBest();
            return selector;
        }
        return selector;
    }

    @Override
    public VehicleRoutingProblemSolution selectSolution(Collection<VehicleRoutingProblemSolution> solutions) {
        double minCost = Double.MAX_VALUE;
        VehicleRoutingProblemSolution bestSolution = null;
        for (VehicleRoutingProblemSolution sol : solutions) {
        	System.err.println(sol.getCost() + "=sol.getCost()   " + minCost+"=minCost");
            if (bestSolution == null) {
                bestSolution = sol;
                minCost = sol.getCost();
            } else if (sol.getCost() < minCost) {
                bestSolution = sol;
                minCost = sol.getCost();
            }
        }
        return bestSolution;
    }

    @Override
    public String toString() {
        return "[name=mySelect]";
    }

}
