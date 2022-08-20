package by.base.main.util;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class TESTTenderTimer {
	
	private Timer timer;
	private int routeId;
	private String message;
	private int seconds;

    public TESTTenderTimer(int id) {
        this.routeId = id;
    }
    public TESTTenderTimer(int seconds, String message, int id) {
        this.routeId = id;
        this.message = message;
        this.seconds = seconds;
    }
    public void stop() {
    	timer.cancel();
    	System.out.println("таймер ID = "+ routeId+" остановлен!");
	}
    public void start() {
    	timer = new Timer();
        timer.schedule(new StopTask(message), seconds * 1000);
	}
    
    
    public int getRouteId() {
		return routeId;
	}
	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
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
		TESTTenderTimer other = (TESTTenderTimer) obj;
		return routeId == other.routeId;
	}


	@Override
	public String toString() {
		return "TenderTimer [routeId=" + routeId + ", seconds=" + seconds + "]";
	}


	class StopTask extends TimerTask {
    	public StopTask(String message) {
    	}
        public void run() {
        	
            System.out.println(message); // тут будет запись в БД
            timer.cancel();
            TESTTimerList.stopAndRemoveTimer(routeId);
        }
    }

}
