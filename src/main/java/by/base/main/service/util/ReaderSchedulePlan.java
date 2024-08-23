package by.base.main.service.util;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.base.main.model.Order;
import by.base.main.model.OrderLine;
import by.base.main.model.OrderProduct;
import by.base.main.model.Product;
import by.base.main.model.Schedule;
import by.base.main.service.OrderProductService;
import by.base.main.service.ProductService;
import by.base.main.service.ScheduleService;

/**
 * Класс который реализует логику подсчётов / сведение
 * <br>а так же отдельные команды для крафика поставок.
 * <br>Осная цель этого класса - привести к датам и цифрам план
 */
@Component
public class ReaderSchedulePlan {
	
	/*
	 * 1. Получаем заказ, и дату постановки заказа в слоты
	 * 2. Определяем ближайший расчёт этого заказа согласно расчётам относительно текущей даты
	 * 3. определяем начальную дату заказа и ко-во дней лог плеча
	 * 4. определяем, входит ли текущая поставка в лог плечо. 
	 * 4.1 метод нахождения такихже заказов относительно лог плеча
	 * 5. если входит в лог прече - суммируем с другими. Если не входит - уже фатальная ошибка!
	 */
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private OrderProductService orderProductService;
	
	@Autowired
	private ProductService productService;
	
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
	
	public DateRange getDateRange (Schedule schedule, Product product) {
		Map<String, String> days = schedule.getDaysMap();
		Map<String, String> daysStep2 = days.entrySet().stream().filter(m->m.getValue().contains("понедельник")
				|| m.getValue().contains("вторник")
                || m.getValue().contains("среда")
                || m.getValue().contains("четверг")
                || m.getValue().contains("пятница")
                || m.getValue().contains("суббота")
                || m.getValue().contains("воскресенье")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
					
		List<OrderProduct> orderProductsHasNow = product.getOrderProductsListHasDateTarget(Date.valueOf(LocalDate.now()));
		OrderProduct orderProductTarget = orderProductsHasNow.get(0);
		String dayOfPlanOrder = orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1).getDayOfWeek().toString(); // планируемый день заказа
		System.err.println(orderProductTarget.getDateCreate());
		//проверяем, есть ли по плану заказ
		boolean flag = false;
		String targetKey = null;
		String targetValue = null;
		for (Entry<String, String> entry : daysStep2.entrySet()) {
			if(entry.getValue().contains(translateToRussianWeek(dayOfPlanOrder))) {
				flag = true;
				targetKey = entry.getKey();
				targetValue = entry.getValue();
				break;
			}
		}
		long i = 0;
		
		System.out.println("orderProductTarget = " + orderProductTarget);
		System.out.println("schedule = " + schedule);
		System.out.println("dayOfPlanOrder = "+dayOfPlanOrder);
		
		if(flag) {
			
			i = parseWeekNumber(targetValue);
			LocalDate datePostavForCalc = LocalDate.of(2024, 7, DayOfWeek.valueOf(targetKey).getValue());
			
			if(targetValue.split("/").length>1) {
				targetValue = targetValue.split("/")[targetValue.split("/").length - 1];
			}
			
			LocalDate dateOrderCalc = LocalDate.of(2024, 7, RUSSIAN_DAYS.get(targetValue).getValue());
			
			i = i + datePostavForCalc.getDayOfMonth() - dateOrderCalc.getDayOfMonth(); // лог плечо
			
		}else {
			System.err.println("план расчёта не совпадает с графиком поставок");
		}
		
		
		//тут определения количество дней от заказа до поставки
//		String dayString1 = "SUNDAY";
//		LocalDate datePostav = LocalDate.of(2024, 7, DayOfWeek.valueOf(dayString1).getValue());
//		LocalDate dateOrder = LocalDate.of(2024, 7, RUSSIAN_DAYS.get("понедельник").getValue());
//		
//		int i = datePostav.getDayOfMonth() - dateOrder.getDayOfMonth();
//		System.out.println(i + " - " + dateOrder + " - " + datePostav);		
		
		return new DateRange(Date.valueOf(orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate()),
				Date.valueOf(orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(i)), i, dayOfPlanOrder);
	}
	
	
//	public void name(Product product, Schedule schedule) {
//		List<OrderProduct> list = new ArrayList<OrderProduct>(product.getOrderProducts());
//		List<OrderProduct> list2 = findNearestFutureDate(list, Date.valueOf(LocalDate.now()));
//		OrderProduct orderProduct = list2.get(0);
//		System.out.println(orderProduct);
//		System.out.println(orderProduct.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1).getDayOfWeek());
////		orderProduct.getDateCreate().toLocalDateTime().toLocalDate().getDayOfWeek()
//	}
	
	public void process(Order order) {
		 Set<OrderLine> lines = order.getOrderLines(); // строки в заказе
		 List<Product> products = new ArrayList<Product>(); //
		 String numContract = order.getMarketContractType();
		 if(numContract == null) {
			 System.err.println("ReaderSchedulePlan.process: numContract = null");
			 return;
		 }
		 Date dateNow = Date.valueOf(LocalDate.now());
		 for (OrderLine line : lines) {
             products.add(productService.getProductByCode(line.getGoodsId().intValue()));
         }
		 
		 //определяем дату старта расчёта и лог плечо для всего заказа
		 Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(numContract));
		 DateRange dateRange = getDateRange(schedule, products.get(0));
		 System.out.println(dateRange);
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
		public Long days;
		public String dayOfWeekHasOrder;
		
		        
        public DateRange(Date start, Date end, Long days, String dayOfWeekHasOrder) {
        	this.start = start;
            this.end = end;
            this.days = days;
            this.dayOfWeekHasOrder = dayOfWeekHasOrder;;
        }


		@Override
		public String toString() {
			return "DateRange [start=" + start + ", end=" + end + ", days=" + days + ", dayOfWeekHasOrder="
					+ dayOfWeekHasOrder + "]";
		}
        
	}
	
	/**
	 * Метод принимает анг. значение дня недели и переводит на русский (MONDAY в понедельник)
	 */
	private String translateToRussianWeek(String englishDayOfWeek) {
        // Преобразуем строку в значение DayOfWeek
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(englishDayOfWeek.toUpperCase());

        // Переводим день недели на русский язык и приводим к нижнему регистру
        String russianDayOfWeek = dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, new Locale("ru")).toLowerCase();

        return russianDayOfWeek;
    }
	
	/**
	 * Переводим н3 в 21 день, принимает всю строку
	 * @param targetValue
	 * @return
	 */
    public int parseWeekNumber(String targetValue) {
        // Регулярное выражение для поиска "н" с числом от 1 до 9
        Pattern pattern = Pattern.compile("н(\\d)");
        Matcher matcher = pattern.matcher(targetValue);

        // Если найдена соответствующая подстрока, вычисляем значение i
        if (matcher.find()) {
            int weekNumber = Integer.parseInt(matcher.group(1));
            return weekNumber * 7;
        }

        // Если ничего не найдено, возвращаем 0 или другое значение по умолчанию
        return 0;
    }
}
