package by.base.main.util;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.stereotype.Component;

//@Component
public class TenderCloseProcessor {
	
	
	private static Timer timer;
	
	private static TimerTask timeListner;
	
	public TenderCloseProcessor() {
		System.out.println("TenderCloseProcessor is running");
		if(timer == null) {
			timer = new Timer();	
			timeListner = new TimeListner();
			timer.schedule(timeListner, 0, 1000);
		}			
	}
	
	public void stopTimer() {
		timer.cancel();
	}
	
	
	class TimeListner extends TimerTask{
				
		private LocalTime timeNow;

		@Override
		public void run() {
			timeNow = LocalTime.now();
			System.out.println(timeNow);
		}
		@Override
		public boolean cancel() {
			System.out.println("Timer is Close");
			return super.cancel();
		}
		
	}

}
