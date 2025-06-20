package by.base.main.util.hcolossus.algorithm;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint.ConstraintsStatus;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;

/**
 * Ограничени со строгим исключением магазинов превышающих определенную дистанцию
 * РАБОТАЕТ.
 * но с отключением разрушений!
 */
public class MaxDistanceConstraintCritical implements HardActivityConstraint{
	
	private final double maxDistanceMeters;
	private final VehicleRoutingTransportCosts costMatrix;
	private final Location depotLocation;
	
	
	
	public MaxDistanceConstraintCritical(double maxDistanceMeters, VehicleRoutingTransportCosts costMatrix,
			Location depotLocation) {
		super();
		this.maxDistanceMeters = maxDistanceMeters;
		this.costMatrix = costMatrix;
		this.depotLocation = depotLocation;
	}

	@Override
	public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct,
	                                   TourActivity nextAct, double prevActDepTime) {


	    Location depot = depotLocation;
	    Location prevLoc = prevAct.getLocation();
	    Location newLoc = newAct.getLocation();
	    Location nextLoc = nextAct != null ? nextAct.getLocation() : null;

	    // 1. Разрешаем, если вставляется сам склад
	    if (newLoc.equals(depot)) return ConstraintsStatus.FULFILLED;

	    // 2. Разрешаем, если маршрут выглядит как: склад → new → склад
	    boolean prevIsDepot = isDepotLocation(prevLoc);
	    boolean nextIsDepot = nextAct != null && isDepotLocation(nextLoc);
	    if (prevIsDepot && nextIsDepot) return ConstraintsStatus.FULFILLED;

	    // 3. Проверка: prev → new
	    double distPrevNew = costMatrix.getDistance(prevLoc, newLoc, 0.0, null);
	    if (distPrevNew > maxDistanceMeters) return ConstraintsStatus.NOT_FULFILLED;

	    // 4. Проверка: new → next (если next не склад)
	    if (nextAct != null && !nextIsDepot) {
	        double distNewNext = costMatrix.getDistance(newLoc, nextLoc, 0.0, null);
	        if (distNewNext > maxDistanceMeters) return ConstraintsStatus.NOT_FULFILLED;
	    }

	    return ConstraintsStatus.FULFILLED;
	}
	
	 private boolean isDepotLocation(Location location) {
	        return location.equals(depotLocation);
	    }

}
