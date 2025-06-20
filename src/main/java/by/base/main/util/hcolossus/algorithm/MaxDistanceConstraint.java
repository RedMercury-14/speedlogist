package by.base.main.util.hcolossus.algorithm;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint.ConstraintsStatus;
import com.graphhopper.jsprit.core.problem.constraint.SoftActivityConstraint;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;

/**
 * Мягкое ограничение, которое накладывает при превышении определенной дистанции
 */
public class MaxDistanceConstraint implements SoftActivityConstraint {

    private final double maxDistanceMeters;
    private final double penaltyFactor; // штраф на 1 метр превышения
    private final VehicleRoutingTransportCosts costMatrix;
    private final Location depotLocation;



    public MaxDistanceConstraint(double maxDistanceMeters, double penaltyFactor,
			VehicleRoutingTransportCosts costMatrix, Location depotLocation) {
		super();
		this.maxDistanceMeters = maxDistanceMeters;
		this.penaltyFactor = penaltyFactor;
		this.costMatrix = costMatrix;
		this.depotLocation = depotLocation;
	}

	@Override
    public double getCosts(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct,
                           TourActivity nextAct, double prevActDepTime) {

        boolean prevIsDepot = isDepotLocation(prevAct.getLocation());
        boolean nextIsDepot = nextAct != null && isDepotLocation(nextAct.getLocation());

        // исключаем выезд со склада и вставку в конец
        if (nextAct == null || (prevIsDepot && !nextIsDepot)) {
            return 0.0;
        }

        // считаем расстояние от prev -> new
        double distance = costMatrix.getDistance(
                prevAct.getLocation(),
                newAct.getLocation(),
                0.0,
                null
        );

        // если превышает — добавляем штраф
        if (distance > maxDistanceMeters) {
            double excess = distance - maxDistanceMeters;
            return excess * penaltyFactor;
        }

        return 0.0;
    }

    private boolean isDepotLocation(Location location) {
        return location.equals(depotLocation);
    }
}
