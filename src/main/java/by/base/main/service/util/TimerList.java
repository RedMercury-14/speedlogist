package by.base.main.service.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
@Service
public class TimerList  {
	
	private static List<TenderTimer> tenderTimerList = new ArrayList<TenderTimer>();

	public TimerList() {		
	}
	
	public static void addAndStart(TenderTimer timer) {
		timer.start();
		tenderTimerList.add(timer);
		
	}
	
	public static void replace(TenderTimer timer) {
		tenderTimerList.stream().filter(t-> t.equals(timer)).forEach(t->t.stop());
		tenderTimerList.remove(timer);
		addAndStart(timer);
	}
	
	public static void stopAndRemoveTimer(int id) {
		tenderTimerList.stream().filter(t-> t.getRouteId() == id).forEach(t->t.stop());
		TenderTimer target = new TenderTimer(id);
		tenderTimerList.remove(target);
	}
	public static void stopAndClearAll(int id) {
		tenderTimerList.stream().forEach(t->t.stop());
		tenderTimerList.clear();
	}
	public static int size() {
		return tenderTimerList.size();
	}
	public static boolean isEmpty() {
		return tenderTimerList.isEmpty();
	}
	

}
