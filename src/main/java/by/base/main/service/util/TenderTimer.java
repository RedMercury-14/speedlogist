package by.base.main.service.util;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import by.base.main.model.Message;
import by.base.main.model.Route;
import by.base.main.model.User;
import by.base.main.service.RouteService;
import by.base.main.util.ChatEnpoint;
import by.base.main.util.MainChat;


public class TenderTimer {
		
	private Timer timer;
	private int routeId;
	private Route route;
	private int seconds;
	private RouteService routeService;
	private ChatEnpoint chatEnpoint;
	private MainChat mainChat;
	
	
	

	public TenderTimer() {
    }
    public TenderTimer(int id) {
        this.routeId = id;
    }
    public TenderTimer(int seconds, Route route, RouteService routeService, ChatEnpoint chatEnpoint, MainChat mainChat) {
        this.route = route;
        this.seconds = seconds;
        this.routeService = routeService;
        this.routeId = route.getIdRoute();
        this.chatEnpoint = chatEnpoint;
        this.mainChat = mainChat;
    }
    public void stop() {
    	timer.cancel();
    	System.out.println("таймер ID = "+ routeId+" остановлен!");
	}
    public void start() {
    	timer = new Timer();
    	StopTask stopTask = new StopTask(route, chatEnpoint, mainChat);
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
		
		public StopTask(Route route, ChatEnpoint chatEnpoint, MainChat mainChat) {
			
		}
	    public void run() {	    
	    	routeService.saveOrUpdateRoute(addFinalCost(route));
	    	System.out.println("route ID = "+ route.getIdRoute() + "complited");
	        timer.cancel();
	        TimerList.stopAndRemoveTimer(routeId);
	        //далее отправка сообщений на js для юзера
	        Message message = new Message();
	        message.setFromUser("server");
	        User user = route.getUser();
	        message.setToUser(user.getLogin());
	        message.setText("Ваша скидка в "+ route.getFinishPrice()+"%, для маршрута " + route.getRouteDirection()+ " принята. ");
	        message.setYnp(user.getNumYNP());
	        message.setStatus("1");
	        message.setIdRoute(route.getIdRoute().toString());
	        mainChat.setMessageByTimer(message);
	        
	        //далее отправка сообщений на js для страницы менеджера
	        Message messageRouteManager = new Message();
	        messageRouteManager.setFromUser("server");
	        messageRouteManager.setToUser("allWorkers");
	        messageRouteManager.setText("Торги маршрута завершились");
	        messageRouteManager.setIdRoute(route.getIdRoute().toString());
	        chatEnpoint.setMessageByTimer(messageRouteManager);
	    }
	    
	    private Route addFinalCost(Route route) {
	    	String casteHasUser = route.getUser().getRate();
	    	System.out.println(casteHasUser);
	    	int startPrise = Integer.parseInt(route.getCost().get(casteHasUser).split("\\.")[0]);
	    	int finishPrisePercent = route.getFinishPrice();
	    	Integer finalPrice = startPrise*(100-finishPrisePercent)/100;
	    	route.setStartPrice(finalPrice);
			return route;
	    	
	    }
	}




}
