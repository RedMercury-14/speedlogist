package by.base.main.service.util;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.UserDAO;
import by.base.main.model.Route;
import by.base.main.model.User;
import by.base.main.service.RouteService;


public class TenderTimer {
		
	private Timer timer;
	private int routeId;
	private Route route;
	private int seconds;
	private RouteService routeService;
	

	public TenderTimer() {
    }
    public TenderTimer(int id) {
        this.routeId = id;
    }
    public TenderTimer(int seconds, Route route, RouteService routeService) {
        this.route = route;
        this.seconds = seconds;
        this.routeService = routeService;
        this.routeId = route.getIdRoute();
    }
    public void stop() {
    	timer.cancel();
    	System.out.println("таймер ID = "+ routeId+" остановлен!");
	}
    public void start() {
    	timer = new Timer();
    	StopTask stopTask = new StopTask(route);
        timer.schedule(stopTask, seconds * 1000);
	}
    
    
    public int getRouteId() {
		return route.getIdRoute();
	}
	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}
	
	public Route getRoute() {
		return route;
	}
	public void setRoute(Route route) {
		this.route = route;
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	@Override
	public int hashCode() {
		return Objects.hash(routeId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TenderTimer other = (TenderTimer) obj;
		return routeId == other.routeId;
	}


	@Override
	public String toString() {
		return "TenderTimer [routeId=" + routeId + ", seconds=" + seconds + "]";
	}
	
	class StopTask extends TimerTask {
		
		public StopTask(Route route) {
		}
	    public void run() {	    	
	    	routeService.saveOrUpdateRoute(addFinalCost(route));
	    	System.out.println("route ID = "+ route.getIdRoute() + "complited");
	        timer.cancel();
	        TimerList.stopAndRemoveTimer(routeId);
	    }
	    
	    private Route addFinalCost(Route route) {
	    	String casteHasUser = route.getUser().getRate();
	    	int startPrise = Integer.parseInt(route.getCost().get(casteHasUser).split("\\.")[0]);
	    	int finishPrisePercent = route.getFinishPrice();
	    	Integer finalPrice = startPrise*(100-finishPrisePercent)/100;
	    	route.setStartPrice(finalPrice);
			return route;
	    	
	    }
	}




}
