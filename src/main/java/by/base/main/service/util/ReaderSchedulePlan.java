package by.base.main.service.util;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.base.main.model.OrderProduct;
import by.base.main.model.Product;
import by.base.main.model.Schedule;
import by.base.main.service.OrderProductService;
import by.base.main.service.ScheduleService;

/**
 * Класс который реализует логику подсчётов / сведение
 * <br>а так же отдельные команды для крафика поставок.
 * <br>Осная цель этого класса - привести к датам и цифрам план
 */
@Component
public class ReaderSchedulePlan {
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private OrderProductService orderProductService;
	
	private static final Map<String, DayOfWeek> RUSSIAN_DAYS = new HashMap<>();

    static {
        RUSSIAN_DAYS.put("понедельник", DayOfWeek.MONDAY);
        RUSSIAN_DAYS.put("вторник", DayOfWeek.TUESDAY);
        RUSSIAN_DAYS.put("среда", DayOfWeek.WEDNESDAY);
        RUSSIAN_DAYS.put("четверг", DayOfWeek.THURSDAY);
        RUSSIAN_DAYS.put("пятница", DayOfWeek.FRIDAY);
        RUSSIAN_DAYS.put("суббота", DayOfWeek.SATURDAY);
        RUSSIAN_DAYS.put("воскресенье", DayOfWeek.SUNDAY);
    }
	
	public DateRange readSchedule (Schedule schedule) {
		Map<String, String> days = schedule.getDaysMap();
		Map<String, String> daysStep2 = days.entrySet().stream().filter(m->m.getValue().contains("понедельник")
				|| m.getValue().contains("вторник")
                || m.getValue().contains("среда")
                || m.getValue().contains("четверг")
                || m.getValue().contains("пятница")
                || m.getValue().contains("суббота")
                || m.getValue().contains("воскресенье")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
					
		
		
		//тут определения количество дней от заказа до поставки
//		String dayString1 = "SUNDAY";
//		LocalDate datePostav = LocalDate.of(2024, 7, DayOfWeek.valueOf(dayString1).getValue());
//		LocalDate dateOrder = LocalDate.of(2024, 7, RUSSIAN_DAYS.get("понедельник").getValue());
//		
//		int i = datePostav.getDayOfMonth() - dateOrder.getDayOfMonth();
//		System.out.println(i + " - " + dateOrder + " - " + datePostav);		
		
		return null;
	}
	
	public void name(Product product, Schedule schedule) {
		List<OrderProduct> list = new ArrayList<OrderProduct>(product.getOrderProducts());
		List<OrderProduct> list2 = findNearestFutureDate(list, Date.valueOf(LocalDate.now()));
		OrderProduct orderProduct = list2.get(0);
		System.out.println(orderProduct);
		System.out.println(orderProduct.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1).getDayOfWeek());
//		orderProduct.getDateCreate().toLocalDateTime().toLocalDate().getDayOfWeek()
	}
	
	
	/**
	 * Возвращает остортированный список с заказами продуктов начиная от самой раннего расчёта к targetDate
	 * @param objects
	 * @param targetDate
	 * @return
	 */
	public List<OrderProduct> findNearestFutureDate(List<OrderProduct> objects, Date targetDate) {
		return objects.stream()
                .filter(obj -> obj.getDateCreate().before(targetDate)) // Фильтруем только те, которые позднее targetDate
                .sorted((obj1, obj2) -> Long.compare(
                        obj2.getDateCreate().getTime() - targetDate.getTime(),
                        obj1.getDateCreate().getTime() - targetDate.getTime()
                ))
                .collect(Collectors.toList());
    }
	
	class DateRange{
		public Date start;
		public Date end;
		public Integer days;
		public String dayOfWeekHasOrder;
		
		        
        public DateRange(Date start, Date end, Integer days, String dayOfWeekHasOrder) {
        	this.start = start;
            this.end = end;
            this.days = days;
            this.dayOfWeekHasOrder = dayOfWeekHasOrder;;
        }
	}
	
}
