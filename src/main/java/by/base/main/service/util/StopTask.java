package by.base.main.service.util;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;

import by.base.main.dao.RouteDAO;
import by.base.main.model.Route;

class StopTask extends TimerTask {
	private Route route;
	@Autowired
	private RouteDAO routeDAO;
	private Timer timer;
	private int id;
	public StopTask() {
	}
	public StopTask(Route route, Timer timer, int id) {
		this.route = route;
		this.timer = timer;
		this.id = id;
	}
    public void run() {
    	routeDAO.saveOrUpdateRoute(route);
    	System.out.println("route ID = "+ route.getIdRoute() + "complited");
        timer.cancel();
        TimerList.stopAndRemoveTimer(id);
    }
}
