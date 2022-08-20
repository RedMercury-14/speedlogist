package by.base.main.util;

public class TestTimer {
	

	public TestTimer(long time) throws InterruptedException {
		timerGet(time);
	}

	public static void timerGet(long time) throws InterruptedException {
		for (int i = (int) time; i >= 0; i--) {
			System.out.println("Осталось: " + ((i > 4) ? i + " секунд": (i > 1) ? i + " секунды" : (i == 1) ? i + " секунда" : "менее секунды"));
			Thread.sleep(1000L);
		}
		System.out.println("Время истекло!");
	}

}
