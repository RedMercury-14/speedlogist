package by.base.main.util.GraphHopper;

import java.util.List;

import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.jsprit.core.algorithm.ruin.distance.AvgServiceAndShipmentDistance;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Activity;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.util.CustomModel;
import com.graphhopper.util.shapes.GHPoint;


public class CustomAvgServiceAndShipmentDistance extends AvgServiceAndShipmentDistance {	
	
	private VehicleRoutingTransportCosts costs;
	
	public CustomAvgServiceAndShipmentDistance(VehicleRoutingTransportCosts costs) {
		super(costs);
		this.costs = costs;
	}

	@Override
	public double getDistance(Job i, Job j) {
		if (i.equals(j)) return 0.0;
        return calcDist(i, j);
	}
	
	private double calcDist(Job i, Job j) {
        double sum = 0;
        double iLat = i.getActivities().get(0).getLocation().getCoordinate().getX();
        double iLng = i.getActivities().get(0).getLocation().getCoordinate().getY();
        
        double jLat = j.getActivities().get(0).getLocation().getCoordinate().getX();
        double jLng = j.getActivities().get(0).getLocation().getCoordinate().getY();
        
        GHRequest request = GHRequestBilder(iLat, iLng, null, jLat, jLng);
//        GraphHopper hopper = routingMachine.getGraphHopper();
//        GHResponse rsp = hopper.route(request);
//        ResponsePath path = rsp.getBest();
//        sum = path.getDistance();
        return sum;
    }
	
	private GHRequest GHRequestBilder (double latFrom, double lonFrom, CustomModel model, double latTo, double lonTo) {
		GHRequest request= new GHRequest().addPoint(new GHPoint(latFrom, lonFrom))
				.addPoint(new GHPoint(latTo, lonTo))
//				.setAlgorithm(Parameters.Algorithms.ALT_ROUTE)
//				.putHint(Parameters.Algorithms.AltRoute.MAX_WEIGHT, 10)
//				.putHint(Parameters.Algorithms.AltRoute.MAX_PATHS, 10)
//				.putHint(Parameters.Algorithms.AltRoute.MAX_SHARE, 10)
//				.setAlgorithm(Parameters.Algorithms.DIJKSTRA)
//				.putHint(Parameters.CH.DISABLE, true)
				.setProfile("car_custom")
				.setCustomModel(model);
		return request;
		
	}

}
