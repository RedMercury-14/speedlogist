package by.base.main.util;

import java.util.ArrayList;
import java.util.List;

public class TESTTimerList  {
	
	private static List<TESTTenderTimer> tenderTimerList = new ArrayList<TESTTenderTimer>();

	public TESTTimerList() {		
	}
	
	public static void addAndStart(TESTTenderTimer timer) {
		timer.start();
		tenderTimerList.add(timer);
		
	}
	
	public static void replace(TESTTenderTimer timer) {
		tenderTimerList.stream().filter(t-> t.equals(timer)).forEach(t->t.stop());
		tenderTimerList.remove(timer);
		addAndStart(timer);
	}
	
	public static void stopAndRemoveTimer(int id) {
		tenderTimerList.stream().filter(t-> t.getRouteId() == id).forEach(t->t.stop());
		TESTTenderTimer target = new TESTTenderTimer(id);
		tenderTimerList.remove(target);
	}
	public static void stopAndClearAll(int id) {
		tenderTimerList.stream().forEach(t->t.stop());
		tenderTimerList.clear();
	}
	public static int cize() {
		return tenderTimerList.size();
	}
	public static boolean isEmpty() {
		return tenderTimerList.isEmpty();
	}
	

}
