package by.base.main.service.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.dto.PlanResponce;

import by.base.main.model.Order;
import by.base.main.model.OrderLine;
import by.base.main.model.OrderProduct;
import by.base.main.model.Product;
import by.base.main.model.Role;
import by.base.main.model.Schedule;
import by.base.main.model.User;
import by.base.main.service.OrderProductService;
import by.base.main.service.OrderService;
import by.base.main.service.ProductService;
import by.base.main.service.ScheduleService;
import by.base.main.service.UserService;

/**
 * Класс который реализует логику подсчётов / сведение
 * <br>а так же отдельные команды для графика поставок.
 * <br>Реализует подсчёт стоков, относительно графика поставок.
 * <br>Осная цель этого класса - привести к датам и цифрам план
 */
@Component
public class ReaderSchedulePlan {
	
	/*
	 * 1. Получаем заказ, и дату постановки заказа в слоты
	 * 2. Определяем ближайший расчёт этого заказа согласно расчётам относительно текущей даты (!!!)
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
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserService userService;
	
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
	
    /**
     * Метод отдаёт диапазон дат, когда можно ставить текущий заказ, <b>где график поставок подтягивает относительно сегодняшней даты</b>
     * @param schedule
     * @param product
     * @return
     */
	public DateRange getDateRange (Schedule schedule, List<Product> products, Order order) {
		if(schedule == null) {
			return null;
		}
		Map<String, String> days = schedule.getDaysMap();
		Map<String, String> daysStep2 = days.entrySet().stream().filter(m->m.getValue().contains("понедельник")
				|| m.getValue().contains("вторник")
                || m.getValue().contains("среда")
                || m.getValue().contains("четверг")
                || m.getValue().contains("пятница")
                || m.getValue().contains("суббота")
                || m.getValue().contains("воскресенье")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
					
//		List<OrderProduct> orderProductsHasNow = product.getOrderProductsListHasDateTarget(Date.valueOf(LocalDate.now().plusDays(1))); // это реализация п.2 (взял +1 день, т.к. заказывают за день поставки)
		
		//тут проходимся по потребностям и выносим в отдельный лист orderProductsHasMin ближайший расчёт потребностей по каждому продукту
				List<OrderProduct> orderProductsHasMin = new ArrayList<OrderProduct>();
				for (Product product2 : products) {
					List<OrderProduct> orderProductsHasNow = product2.getOrderProductsListHasDateTarget(Date.valueOf(LocalDate.now().plusDays(1))); // это реализация п.2 (взял +1 день, т.к. заказывают за день поставки)
					if(orderProductsHasNow != null) {
						orderProductsHasMin.add(orderProductsHasNow.get(0));						
					}
				}
				orderProductsHasMin.sort((o1, o2) -> o2.getDateCreate().compareTo(o1.getDateCreate())); // сортируемся от самой ранней даты
		
		if(orderProductsHasMin.isEmpty()) {
			System.err.println("Расчёта заказов по продуктам: невозможен, т.к. потребности нет в базе данных");
			//мы всё равно считаем график поставок, для определения лог плеча (days)
			DateRange extractDateRange = getDateRangeNoProducts(daysStep2, order);
			extractDateRange.numContruct = order.getMarketContractType();			
//			return new DateRange(null, null, 0L, null, null);
			return extractDateRange;
		}
		
		
		
		OrderProduct orderProductTarget = orderProductsHasMin.get(0);
		String dayOfPlanOrder = orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1).getDayOfWeek().toString(); // планируемый день заказа
		
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
		
//		System.out.println("orderProductTarget = " + orderProductTarget);
//		System.out.println("schedule = " + schedule);
//		System.out.println("dayOfPlanOrder = "+dayOfPlanOrder);
		
		if(flag) {
			
			i = parseWeekNumber(targetValue);
			
			LocalDate datePostavForCalc = LocalDate.of(2024, 7, DayOfWeek.valueOf(targetKey).getValue());
			
			if(targetValue.split("/").length>1) {
				targetValue = targetValue.split("/")[targetValue.split("/").length - 1];
			}
			
			LocalDate dateOrderCalc = LocalDate.of(2024, 7, RUSSIAN_DAYS.get(targetValue).getValue());
			
			int j = datePostavForCalc.getDayOfMonth() - dateOrderCalc.getDayOfMonth(); // лог плечо
							
			
			if(j < 0 && i == 0) {
				j = j + 7;
			}
			if(j==0) {
				j=7;
			}
			
			i = i+j;
			
		}else {
			System.err.println("план расчёта не совпадает с графиком поставок");
			return null;
		}
			
		return new DateRange(Date.valueOf(orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1)),
				Date.valueOf(orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(i+1)), i, dayOfPlanOrder, schedule.getCounterpartyContractCode().toString());
	}
	
	/**
	 * Метод отвечает за предоставления DateRange когда нету заказов со стороны ОРЛ 
	 * <br>или вообще не предусматривается проверка по заказам ОРЛ
	 * @param daysStep2
	 * @param order
	 * @return
	 */
	public DateRange getDateRangeNoProducts (Map<String, String> daysStep2, Order order) {
		LocalDate dateDeliveryHasSlot = order.getTimeDelivery().toLocalDateTime().toLocalDate();
		DayOfWeek dayOfWeek = dateDeliveryHasSlot.getDayOfWeek();
		String targetValue = null;
		String targetKey = null;
		
		LocalDate start = null;
		LocalDate finish = null;
		
		if(daysStep2.get(dayOfWeek.toString()) != null) {
			String dayOfDeliveryString = dayOfWeek.toString();
			String mass[] = daysStep2.get(dayOfWeek.toString()).split("/");
			targetValue = daysStep2.get(dayOfWeek.toString());
			targetKey = dayOfWeek.toString().trim();
			DayOfWeek dayOfOrderDayOfWeek = RUSSIAN_DAYS.get(mass[mass.length-1]);
			String week = Arrays.asList(mass).stream().filter(s -> s.matches("н\\d+")).findFirst().orElse(null);
			start = dateDeliveryHasSlot;
			//тут определяем дату финиша.
			//берем сначала день из текущей недели а потом применяем поправочный коэф (week) если он есть
	        // Получаем начало недели (понедельник)
	        LocalDate startOfWeek = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

	        // Получаем дату по названию дня (например, WEDNESDAY)
	        DayOfWeek targetDay = dayOfOrderDayOfWeek; // Укажи нужный день недели
	        LocalDate targetDate = startOfWeek.with(TemporalAdjusters.nextOrSame(targetDay));
	       
	        if(week != null) {
	        	int dayD = 7;
	        	int dayX = Integer.parseInt(week.replace("н", "").trim());
	        	finish = targetDate.minusDays(dayD*dayX);
	        }else {
	        	finish = targetDate;
	        }
	        
//			System.out.println("dayOfDeliveryString = " + dayOfDeliveryString);
//			System.out.println("dayOfOrderString = " + dayOfOrderDayOfWeek);
//			System.out.println("week = " + week);
//			System.out.println("start = " + start);
//			System.out.println("finish = " + finish);
			
		}else {			
			Entry<LocalDate, Entry<String, String>> entry = findNextAvailableDay(daysStep2, dateDeliveryHasSlot, dayOfWeek);
			String mass[] = entry.getValue().getValue().split("/");
			targetValue = entry.getValue().getValue();
			targetKey = entry.getValue().getKey();
			DayOfWeek dayOfOrderDayOfWeek = RUSSIAN_DAYS.get(mass[mass.length-1]);
			String week = Arrays.asList(mass).stream().filter(s -> s.matches("н\\d+")).findFirst().orElse(null);
			start = entry.getKey();
			//тут определяем дату финиша.
			//берем сначала день из текущей недели а потом применяем поправочный коэф (week) если он есть
	        // Получаем начало недели (понедельник)
	        LocalDate startOfWeek = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

	        // Получаем дату по названию дня (например, WEDNESDAY)
	        DayOfWeek targetDay = dayOfOrderDayOfWeek; // Укажи нужный день недели
	        LocalDate targetDate = startOfWeek.with(TemporalAdjusters.nextOrSame(targetDay));
	        if(week != null) {
	        	int dayD = 7;
	        	int dayX = Integer.parseInt(week.replace("н", "").trim());
	        	finish = targetDate.minusDays(dayD*dayX);
	        }else {
	        	finish = targetDate;
	        }
	        
			
//			System.err.println("dayOfOrderString = " + dayOfOrderDayOfWeek);
//			System.err.println("week = " + week);
//			System.err.println("start = " + start);
//			System.err.println("finish = " + finish);
		}
		//считаем дни
		long i = 0;
		i = parseWeekNumber(targetValue);
		
		LocalDate datePostavForCalc = LocalDate.of(2024, 7, DayOfWeek.valueOf(targetKey).getValue());
		
		if(targetValue.split("/").length>1) {
			targetValue = targetValue.split("/")[targetValue.split("/").length - 1];
		}
		
		LocalDate dateOrderCalc = LocalDate.of(2024, 7, RUSSIAN_DAYS.get(targetValue).getValue());
		
		int j = datePostavForCalc.getDayOfMonth() - dateOrderCalc.getDayOfMonth(); // лог плечо
						
		
		if(j < 0 && i == 0) {
			j = j + 7;
		}
		if(j==0) {
			j=7;
		}
		
		i = i+j;
		if(start.isBefore(finish)) {
			finish = finish.minusDays(7);
		}
		return new DateRange(Date.valueOf(finish),Date.valueOf(start), i, targetKey, null);
	}
	
	/**
	 * Ищет ближайший доступный день с значением в `Map`, начиная с заданной даты и дня недели.
	 * Возвращает дату найденного дня и соответствующее значение из `Map`.
	 * 
	 * @param dayMap   карта, где ключ — день недели, а значение — связанная с ним строка
	 * @param startDate начальная дата поиска
	 * @param startDay  день недели, с которого начинается поиск
	 * @return пара {@link Map.Entry}, содержащая дату найденного дня и значение из `Map`,
	 *         или `null`, если значение не найдено в течение недели
	 */
    public static Entry<LocalDate, Entry<String, String>> findNextAvailableDay(Map<String, String> dayMap, LocalDate startDate, DayOfWeek startDay) {
        LocalDate currentDate = startDate.with(startDay); // Дата, с которой начинаем поиск

        for (int i = 0; i < 7; i++) { // Максимум 7 итераций для обхода всей недели
            String dayOfWeekString = currentDate.getDayOfWeek().toString();
            String value = dayMap.get(dayOfWeekString);
            if (value != null) {
                return new SimpleEntry<>(currentDate, new SimpleEntry<>(dayOfWeekString, value)); // Возвращаем дату, ключ и значение
            }
            currentDate = currentDate.plusDays(1); // Переходим к следующему дню
        }
        return null; // Если ни одного значения не найдено
    }
	
	
	/**
	 * Метод, который определяет:
	 * <br>1)Текущий график поставок
	 * <br>2)Лог плечо
	 * <br>3)Даты ближайшего и прошлого расчёта
	 * @param order Заказ, для которого необходимо сгенерировать ответ плана.
	 * @return Объект {@link PlanResponce}, содержащий статус, сообщение, 
	 *         список соответствующих дат и связанное расписание.
	 *         Возвращает ответ с ошибкой, если номер контракта не найден.
	 *
	 * @throws NullPointerException если переданный заказ равен null.
	 * 
	 * <p>
	 * Метод выполняет следующие шаги:
	 * <ol>
	 *     <li>Проверяет, присутствует ли тип контракта на рынке в заказе.</li>
	 *     <li>Логирует ошибку и возвращает ответ с ошибкой, если тип контракта равен null.</li>
	 *     <li>Вызывает текущую дату и дату, которая была две недели назад.</li>
	 *     <li>Получает список заказов в заданном диапазоне дат и по номеру контракта.</li>
	 *     <li>Добавляет текущий заказ в список, если его там нет.</li>
	 *     <li>Извлекает расписание, связанное с данным номером контракта.</li>
	 *     <li>Собирает первые строки заказа из каждого заказа и объединяет их.</li>
	 *     <li>Извлекает продукты заказа на основе собранных строк заказа в заданном диапазоне дат (30 дней).</li>
	 *     <li>Сортирует продукты заказа по дате создания.</li>
	 *     <li>Извлекает уникальные даты из продуктов заказа и сортирует их.</li>
	 *     <li>Возвращает успешный ответ, содержащий отсортированные даты и расписание.</li>
	 * </ol>
	 * </p>
	 */
	public PlanResponce getPlanResponce(Order order) {
		String numContract = order.getMarketContractType();
		if(numContract == null) {
			 System.err.println("ReaderSchedulePlan.process: numContract = null");
			 return new PlanResponce(0, "Действие заблокировано!\nНе найден номер контракта в заказе");
		 }
		Date dateNow = Date.valueOf(LocalDate.now());
		Date dateOld2Week = Date.valueOf(LocalDate.now().minusDays(14));
		List <Order> orders = orderService.getOrderByPeriodDeliveryAndCodeContract(dateNow, dateOld2Week, numContract);
		
		if(!orders.contains(order)) {
			orders.add(order);
		}
		
		Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(order.getMarketContractType()));
		
		//берем по первой строке заказа и делаем запрос в бд потребности с выгрузкой пяти заказов с совпадениями		
		Set<OrderLine> lines = order.getOrderLines(); // каждая первая строка в заказе
		for (Order orderI : orders) {
			lines.add(orderI.getOrderLines().stream().findFirst().get());
		}
		
		List<OrderProduct> orderProducts = new ArrayList<OrderProduct>();
		Date dateNowOrderProducts = Date.valueOf(order.getTimeDelivery().toLocalDateTime().toLocalDate());
		Date dateOld3WeekOrderProducts = Date.valueOf(order.getTimeDelivery().toLocalDateTime().toLocalDate().minusDays(30));
		for (OrderLine orderLine : lines) {
			List<OrderProduct> orderProductsTarget = orderProductService.getOrderProductListHasCodeProductAndPeriod(orderLine, dateOld3WeekOrderProducts, dateNowOrderProducts);
			if(orderProductsTarget != null && !orderProductsTarget.isEmpty()) {
				orderProducts.addAll(orderProductsTarget);				
			}
		}		
		orderProducts.sort((o1, o2) -> o2.getDateCreate().compareTo(o1.getDateCreate()));// сортируемся от самой ранней даты
		
		Set<Date> dates = new HashSet<Date>();
		for (OrderProduct orderProduct : orderProducts) {
			dates.add(Date.valueOf(orderProduct.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1)));
		}		
		List<Date> result = new ArrayList<Date>(dates);
		result.sort((o1, o2) -> o2.compareTo(o1));// сортируемся от самой ранней даты
		
		return new PlanResponce(200, "Информация о датах заказа", result, schedule);		
	}
	
	
	private static final int targetDayForBalance = 20;
	/**
	 * метод проверки балансов на складах
	 * где больше товара, где меньше
	 * @return
	 */
	public List<Product> checkBalanceBetweenStock(Order order) {
		String stock = order.getNumStockDelivery();
		if(!stock.equals("1800") && !stock.equals("1700")) {
			return null;
		}
		
		Map<Long, Double> orderProducts = order.getOrderLinesMap(); //заказанные строки
		List<Long> goodsIds = orderProducts.entrySet().stream()
			    .map(en -> en.getKey())
			    .collect(Collectors.toList()); // отдельный лист с кодами товаров заказанных в заказе
		List<Integer> goodsIdsIntegers = orderProducts.entrySet().stream()
				.map(en -> en.getKey().intValue())
				.collect(Collectors.toList()); // отдельный лист с кодами товаров заказанных в заказе
		Map<String, Product> productsMap = productService.getProductMapHasGroupByCode(goodsIdsIntegers);
		List<Product> productsHasBalance = new ArrayList<Product>(); // результирующий лист с продуктами и балансами

		List<Order> orders = null;

//		productsMap.forEach((k,v) -> System.out.println(k + " -- " + v));
		/*
		 * Далее по каждому товару проверяем его остаток с учётом установленных слотов.
		 * !Всегда ратгетимся по дефицитному товару!
		 */
		for (Entry<Long, Double> entry : orderProducts.entrySet()) {
//			List<Product> products = productService.getProductByCode(entry.getKey().intValue());
			List<Product> products = new ArrayList<Product>();
			if(productsMap.get(entry.getKey()+"1700") != null) {
				products.add(productsMap.get(entry.getKey()+"1700"));
			}else {
				products.add(new Product(1700, 0.0, 0.0)); // заглушка, чтобы не ломать дальнейшую проверку
			}
			if(productsMap.get(entry.getKey()+"1800") != null) {
				products.add(productsMap.get(entry.getKey()+"1800"));
			}else {
				products.add(new Product(1800, 0.0, 0.0)); // заглушка, чтобы не ломать дальнейшую проверку
			}
			
			
			Product generalProduct = products.get(0);
			Double remainderInDay1700 = null;// записываем остаток в днях по записи для 1700.
			Double remainderInDay1800 = null;// записываем остаток в днях по записи для 1800.
			Double calculatedPerDay1700 = 0.0; 
			Double calculatedPerDay1800 = 0.0; 
			for (Product product : products) {
				
				if(product.getNumStock() == 1700) {
					remainderInDay1700 = product.getBalanceStockAndReserves();
					calculatedPerDay1700 = product.getСalculatedPerDay();
				}else {
					remainderInDay1800 = product.getBalanceStockAndReserves();
					calculatedPerDay1800 = product.getСalculatedPerDay();
				}
			}
			if(remainderInDay1700 == null || remainderInDay1800 == null || remainderInDay1700 == 9999 || remainderInDay1800 == 9999) {
				continue;
			}
			//далее просматриваем что стоит в слотах (должно приехать) и переводем в дни, для суммирования
			Date start = generalProduct.getDateUnload();
			Date finish = Date.valueOf(LocalDate.now().plusDays(30));
			Double quantityOrderSum1700 = 0.0; // сумма всех заказов товара за заданный период
			Double quantityOrderSum1800 = 0.0; // сумма всех заказов товара за заданный период
			
			if(orders == null) {
//				List<Long> goodsIds = orderProducts.entrySet().stream()
//					    .map(en -> en.getKey())
//					    .collect(Collectors.toList());
				orders = orderService.getOrderGroupByPeriodSlotsAndProductNotJOIN(start, finish, goodsIds);
			}
			
			for (Order order2 : orders) {
				if(order2.getOrderLinesMap().get(Long.parseLong(generalProduct.getCodeProduct()+"")) != null) {
					if(order2.getNumStockDelivery().equals("1700")) {
						quantityOrderSum1700 = quantityOrderSum1700 + order2.getOrderLinesMap().get(Long.parseLong(generalProduct.getCodeProduct()+""));
					}else {
						quantityOrderSum1800 = quantityOrderSum1800 + order2.getOrderLinesMap().get(Long.parseLong(generalProduct.getCodeProduct()+""));
					}
				}
				
			}
			
			
			Integer expectedDays1700 = 0; // ожидаемый приход в днях
			Integer expectedDays1800 = 0; // ожидаемый приход в днях
			if(calculatedPerDay1700 != 0 ) {
				expectedDays1700 = (int) roundВouble(quantityOrderSum1700/calculatedPerDay1700, 0);
			}
			if(calculatedPerDay1800 != 0 ) {
				expectedDays1800 = (int) roundВouble(quantityOrderSum1800/calculatedPerDay1800, 0);
			}
//			System.out.println(generalProduct.getCodeProduct() + " 1700 -- " + remainderInDay1700 + " + " + expectedDays1700 + " ("+quantityOrderSum1700 + "/" +calculatedPerDay1700+")");
//			System.out.println(generalProduct.getCodeProduct() + " 1800 -- " + remainderInDay1800 + " + " + expectedDays1800 + " ("+quantityOrderSum1800 + "/" +calculatedPerDay1800+")");
			
			Double finalDays1700 = remainderInDay1700 + expectedDays1700; // потом разделить на 1700 и 1800
			Double finalDays1800 = remainderInDay1800 + expectedDays1800; // потом разделить на 1700 и 1800
			
			generalProduct.setCalculatedDayStock1700(finalDays1700);
			generalProduct.setCalculatedDayStock1800(finalDays1800);
			generalProduct.setCalculatedDayMax(Double.parseDouble(targetDayForBalance+""));
			generalProduct.setOrderProducts(null);
			generalProduct.setCalculatedHistory(generalProduct.getCodeProduct() + " 1700 -- " + remainderInDay1700 + " + " + expectedDays1700 + " ("+quantityOrderSum1700 + "/" +calculatedPerDay1700+")\n"
					+ generalProduct.getCodeProduct() + " 1800 -- " + remainderInDay1800 + " + " + expectedDays1800 + " ("+quantityOrderSum1800 + "/" +calculatedPerDay1800+")");
			productsHasBalance.add(generalProduct);
		}
		
		
		return productsHasBalance;	
	}
	
	/**
	 * Главный метод проврки заказа по потребностям. Возвращает текстовую информацию
	 * <br>Обрабатывает заказ, проверяет наличие товаров, вычисляет количество и проверяет 
	 * <br>соответствие заказа графику поставок.
	 *
	 * @param order Заказ, который необходимо обработать.
	 * @return Объект {@link PlanResponce}, содержащий статус, сообщение о результатах обработки заказа.
	 *         Возвращает ответ с ошибкой, если номер контракта не найден или если 
	 *         имеются ошибки в количестве заказанных товаров.
	 *
	 * @throws NullPointerException если переданный заказ равен null.
	 * 
	 * <p>
	 * Метод выполняет следующие шаги:
	 * <ol>
	 *     <li>Извлекает строки заказа.</li>
	 *     <li>Получает номер контракта из заказа.</li>
	 *     <li>Проверяет наличие номера контракта и возвращает ошибку, если он отсутствует.</li>
	 *     <li>Определяет текущую дату и формирует сообщение о количестве строк в заказе.</li>
	 *     <li>Для каждой строки заказа получает соответствующий продукт по его коду.</li>
	 *     <li>Извлекает расписание по номеру контракта.</li>
	 *     <li>Возвращает ошибку, если товары отсутствуют в базе данных.</li>
	 *     <li>Получает диапазон дат для расчета логистического плеча.</li>
	 *     <li>Проверяет, возможно ли провести расчет по предоставленным данным.</li>
	 *     <li>Если расчеты возможны, проверяет наличие товаров в заказах, совпадающих с заданным логистическим плечом.</li>
	 *     <li>Оценивает каждую строку заказа и формирует сообщение о результатах.</li>
	 *     <li>Если заказ не соответствует графику поставок, возвращает сообщение об ошибке.</li>
	 *     <li>Возвращает успешный ответ с результатами обработки или ошибку, если были обнаружены несоответствия.</li>
	 * </ol>
	 * </p>
	 */
	public PlanResponce process(Order order) {
		 Set<OrderLine> lines = order.getOrderLines(); // строки в заказе
		 List<Product> products = new ArrayList<Product>(); //
		 String numContract = order.getMarketContractType();
		 String result = "";
		 if(numContract == null) {
			 System.err.println("ReaderSchedulePlan.process: numContract = null");
			 return new PlanResponce(0, "Действие заблокировано!\nНе найден номер контракта в заказе");
		 }
		 Date dateNow = Date.valueOf(LocalDate.now());
		 String infoRow = "Строк в заказе: " + lines.size();
		 for (OrderLine line : lines) {
			 Product product = productService.getProductByCodeAndStock(line.getGoodsId().intValue(), Integer.parseInt(order.getNumStockDelivery()));
			 if(product != null) {
				 products.add(product);				 
			 }
         }
		 
		 //определяем дату старта расчёта и лог плечо для всего заказа
		 Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(numContract));
		 if(schedule == null) {
			 return new PlanResponce(0, "Действие заблокировано!\nНе найден график поставок с кодом контракта: " + numContract);
		 }
		 
		 if(products.isEmpty()) {
			 if(lines.size() == 0) {
				 return new PlanResponce(0, "Действие заблокировано!\nНе найдены товары в заказе.");
			 }else {
				 return new PlanResponce(200, "Не найдены товары в базе данных. " + infoRow);
				 
			 }
		 }
		 
//		 products.forEach(p-> System.out.println(p));
		 
		 
		 
		 DateRange dateRange = getDateRange(schedule, products, order); // тут раелизуются пункты 2 и 3
		 
		 System.out.println("dateRange = " + dateRange);
		 
		 if(dateRange == null) {
//			 return new PlanResponce(0, "Действие заблокировано!\nПросчёт кол-ва товара на логистическое плечо невозможен, т.к. расчёт ОРЛ не совпадает с графиком поставок");
			 return new PlanResponce(200, "Просчёт кол-ва товара на логистическое плечо невозможен, т.к. расчёт ОРЛ не совпадает с графиком поставок");
		 }
		 if(dateRange.start == null && dateRange.days == 0) {
			 //тут мы говорим что расчёты ОРЛ по товару отсутствуют но есть липовый график поставок и есть сток в днях. Всё равно проверяем по стоку!
			 
			 return new PlanResponce(200, "Расчёта заказов по продукту: " + products.get(0).getName() + " ("+products.get(0).getCodeProduct()+") невозможен, т.к. нет в базе данных расчётов потребности");
		 }
		 
		 boolean isMistakeZAQ = false;
		 
		 if(checkHasLog(dateRange, order)) {
			 //если входит в лог плече, то находим такие же заказы с такими же SKU
			 List<Order> orders = orderService.getOrderByTimeDelivery(dateRange.start, dateRange.end);
			 if(!orders.contains(order)) {
				 orders.add(order);
			 }
			 
			 HashMap<Long, ProductDouble> map = calculateQuantityOrderSum(orders); // тут я получил мапу с кодами товаров и суммой заказа за период.
//			 map.forEach((k,v)->System.out.println(k + " -- " + v));
			
			 for (OrderLine orderLine : lines) {
				Double quantityOrderAll = map.get(orderLine.getGoodsId()).num;
				Product product = productService.getProductByCodeAndStock(orderLine.getGoodsId().intValue(), Integer.parseInt(order.getNumStockDelivery()));
				if(product!=null) {
					List<OrderProduct> quantity = null;
					if(order.getDateOrderOrl() != null) {
						quantity = product.getOrderProductsListHasDateTarget(order.getDateOrderOrl());
					}else {
						quantity = product.getOrderProductsListHasDateTarget(dateNow);
					}

					if(quantity != null) {
						//тут происходит построчная оценка заказанного товара и принятие решения
						int zaq = quantityOrderAll.intValue(); // СУММА заказов по периоду 
						int orlZaq = quantity.get(0).getQuantity(); // аказ от ОРЛ
						int singleZaq = orderLine.getQuantityOrder().intValue(); //Заказ по ордеру (не суммированный)
						// реализация специальной логики: если заказ от менеджера больше или равен заказу от ОРЛ - берем только его заказ
						// если заказ меньше 80% от того что заказал ОРЛ - проверяем другие заказы
						if (singleZaq < 0.8 * orlZaq) {
							if(zaq > orlZaq*1.1) {
								result = result +"<span style=\"color: red;\">"+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + zaq + " шт. ("+map.get(orderLine.getGoodsId()).orderHistory+") из " + quantity.get(0).getQuantity() + " шт.</span>\n";	
								String dayStockMessage =  checkNumProductHasStock(order, product, dateRange); // проверяем по стокам относительно одного продукта
								 if(dayStockMessage!= null) {
						             result = dayStockMessage+"\n" + result;
						             isMistakeZAQ = true;
						         }
//							isMistakeZAQ = true;
							}else {
								result = result +orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + zaq + " шт. ("+map.get(orderLine.getGoodsId()).orderHistory+") из " + quantity.get(0).getQuantity() + " шт.\n";													
							}
						}else {
							if(singleZaq > orlZaq*1.1) {
								result = result +"<span style=\"color: red;\">"+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + singleZaq + " шт. из " + quantity.get(0).getQuantity() + " шт.</span>\n";	
								String dayStockMessage =  checkNumProductHasStock(order, product, dateRange); // проверяем по стокам относительно одного продукта
								 if(dayStockMessage!= null) {
						             result = dayStockMessage+"\n" + result;
						             isMistakeZAQ = true;
						         }
//							isMistakeZAQ = true;
							}else {
								result = result +orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + singleZaq + " шт. из " + quantity.get(0).getQuantity() + " шт.\n";													
							}
						}
						
					}else {
						String dayStockMessage =  checkNumProductHasStock(order, product, dateRange); // проверяем по стокам относительно одного продукта
						 if(dayStockMessage!= null) {
				             result = dayStockMessage+"\n" + result;
				             isMistakeZAQ = true;
				         }
						result = result +orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - отсутствует в плане заказа (Заказы поставщика от ОРЛ)\n";
					}
				}
			}
			 
		 }else {
			 
			//проверка по стокам отностительно графика поставок
			 
			 String dayStockMessage =  checkNumProductHasStock(order, null,  dateRange);
			 if(dayStockMessage!= null) {
	             result = dayStockMessage+"\n" + result;
	             isMistakeZAQ = true;
	         }
			// КОНЕЦ проверка по стокам отностительно графика поставок
			 //если не входит, то сообщаем, в виде ошибки
			 System.err.println("false"); //остановился тут
			 result = result + "Данный заказ " + order.getMarketNumber() + " " + order.getCounterparty() + " установлен не по графику поставок. Он должен быть установлен в диапазоне: с "
					 +dateRange.start.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ", по " + dateRange.end.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		 }
		 
		 
		 
		 if(isMistakeZAQ) {
			 return new PlanResponce(0, "Действие заблокировано!\n"+result);
		 }else {
			 return new PlanResponce(200, result);
		 }		 
	}
	

	/**
	 * Принимает лист List<Order> orders
	 * <br>а возвращает HashMap<Long, Double>, где значение - это сумма по заказам за текущий период а ключ - это код товара.
	 * <br>Вычисляет сумму количеств товаров по строкам заказов и возвращает их в виде 
	 * <br>карты, где ключом является идентификатор товара, а значением — количество и 
	 * <br>история заказов.
	 *
	 * @param orders Список заказов, для которых необходимо вычислить суммы количеств товаров.
	 * @return Объект {@link HashMap} с идентификаторами товаров в качестве ключей и 
	 *         объектами {@link ProductDouble} в качестве значений, содержащими 
	 *         сумму количеств товаров и историю заказов.
	 *
	 * <p>
	 * Метод выполняет следующие шаги:
	 * <ol>
	 *     <li>Создает карту для хранения результатов.</li>
	 *     <li>Проходит по каждому заказу в списке.</li>
	 *     <li>Извлекает строки заказа (OrderLine) из каждого заказа.</li>
	 *     <li>Для каждой строки заказа проверяет, есть ли товар уже в карте:</li>
	 *     <ul>
	 *         <li>Если нет, добавляет новый товар с его количеством и идентификатором заказа.</li>
	 *         <li>Если да, обновляет количество товара и добавляет идентификатор заказа в историю.</li>
	 *     </ul>
	 *     <li>Возвращает заполненную карту.</li>
	 * </ol>
	 * </p>
	 */
    public HashMap<Long, ProductDouble> calculateQuantityOrderSum(List<Order> orders) {
        // Используем HashMap для хранения результата
        HashMap<Long, ProductDouble> orderLineQuantityMap = new HashMap<>();
        
        
        
        // Проходим по каждому заказу
        for (Order order : orders) {
            // Получаем список строк заказа (OrderLine)
            Set<OrderLine> orderLines = order.getOrderLines();
            
            // Проходим по каждой строке заказа
            for (OrderLine orderLine : orderLines) {
            	if(!orderLineQuantityMap.containsKey(orderLine.getGoodsId())) {
            		orderLineQuantityMap.put(orderLine.getGoodsId(), new ProductDouble(orderLine.getQuantityOrder(), order.getIdOrder().toString()));
            	}else {
            		// Если строка заказа уже есть в HashMap, то добавляем к существующему значению
                    orderLineQuantityMap.put(orderLine.getGoodsId(), new ProductDouble(orderLineQuantityMap.get(orderLine.getGoodsId()).num + orderLine.getQuantityOrder(),
                    		orderLineQuantityMap.get(orderLine.getGoodsId()).orderHistory + ";"+order.getIdOrder().toString()));
            	}
                
            }
        }
        
        return orderLineQuantityMap;
    }
	
	/**
	 * Проверяет, входит ли заказ в текущий DateRange
	 * реализация пункта 4
	 * @param dateRange
	 * @return
	 */
	public boolean checkHasLog(DateRange dateRange, Order order) {		
		LocalDate dateOrderTarget = order.getTimeDelivery().toLocalDateTime().toLocalDate();
		
		if(dateOrderTarget.isEqual(dateRange.start.toLocalDate()) 
				|| dateOrderTarget.isEqual(dateRange.end.toLocalDate())
				|| dateOrderTarget.isAfter(dateRange.start.toLocalDate()) && dateOrderTarget.isBefore(dateRange.end.toLocalDate())) {
			return true;
		}
		
		return false;		
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
	
	
	/**
	 *  Объект, котоырй определяет сколько дней стока долно быть минимально а так же лог плечо и диапазон дат поставок
	 * <br> Отдаёт количество дней стока (stock), начиная от заказа согласно графику поставок
	 * <br> т.е. Если заказ в понедельник а поставка в среду, то он берет плече с понедельника по среду
	 * <br> и до второй поставки, т.е. до сл. среды <b>(лог плечо + неделя)</b>
	 */
	class DateRange{
		/**
		 * Дата начала лог плеча
		 * <br>Она же дата заказа
		 */
		public Date start;
		
		/**
		 * Дата окончания лог плеча
		 * <br>Она же дата поставки
		 */
		public Date end;
		
		/**
		 * Непосредственно лог плечо
		 */
		public Long days;
		
		/**
		 * Жинамический сток для текущего лог плеча (запас товара в днях до второй поставки)
		 */
		public Long stock; // динамический сток от даты заказа
		
		/**
		 * Дата заказа согласно графику поставок
		 */
		public String dayOfWeekHasOrder;
		
		/**
		 * Номер контракта
		 */
		public String numContruct;
		
		        
        public DateRange(Date start, Date end, Long days, String dayOfWeekHasOrder, String numContruct) {
        	this.start = start;
            this.end = end;
            this.days = days;
            this.dayOfWeekHasOrder = dayOfWeekHasOrder;;
            this.stock = days + 8;
            this.numContruct = numContruct;
        }


		@Override
		public String toString() {
			return "DateRange [start=" + start + ", end=" + end + ", days=" + days + ", stock=" + stock
					+ ", dayOfWeekHasOrder=" + dayOfWeekHasOrder + ", numContruct=" + numContruct + "]";
		}
        
        
	}
	
	/**
	 * Класс реализхует сумму продуктов и историю с каких заказов суммируется строки продуктов
	 */
	class ProductDouble{
		public Double num;
		public String orderHistory;
		/**
		 * @param num
		 * @param orderHistory
		 */
		public ProductDouble(Double num, String orderHistory) {
			super();
			this.num = num;
			this.orderHistory = orderHistory;
		}
		@Override
		public String toString() {
			return "ProductDouble [num=" + num + ", orderHistory=" + orderHistory + "]";
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
    
    
    /**
	 * Метод для проверки кол-ва товара на текущий день <b> если Product != null то проверка происходит только по одному прдукту!</b>
	 * если всё ок, возвращает null, если что то не то - сообщение
	 * @return
	 */
	private String checkNumProductHasStock(Order order, Product product, DateRange dateRange) {
		String message = null;
		User user = getThisUser();
		Role role = user.getRoles().stream().findFirst().get();	
		
//		if(role.getIdRole() == 1 || role.getIdRole() == 2 || role.getIdRole() == 3) { // тут мы говорим что если это логист или админ - в проверке не нуждаемся
//			return null;
//		}
		if(order.getIsInternalMovement() != null && order.getIsInternalMovement().equals("true")) {
			return null;
		}
		if(order.getNumProduct() == null) {
			message = "Данные по заказу " + order.getMarketNumber() + " устарели! Обновите заказ."; 
//			return message; временно отключил
			return null;
		}
		
		String [] numProductMass = order.getNumProduct().split("\\^");
		
		//реализация проверки, когда нужно проверить только один продукт
		if(product != null) {
			if(product.getBalanceStockAndReserves() == null) {
				return null;
			}
//			if(product.getOrderProducts() != null && !product.getOrderProducts().isEmpty()) {
//				return null;
//			}
			if(product.getBalanceStockAndReserves() == 9999.0) {
				return null;
			}
			if(product.getRemainderStockInPall() < 15.0) { //если в паллетах товара меньшге чем 33 - то пропускаем
				return null;
			}
			//считаем разницу в днях сегодняшнеего дня и непосредственно записи
			LocalDateTime start = order.getTimeDelivery().toLocalDateTime();
			LocalDateTime end = LocalDateTime.of(product.getDateUnload().toLocalDate(), LocalTime.now());

			Duration duration = Duration.between(start, end);
			Double currentDate = (double) duration.toDays();
			// считаем правильный остаток на текущий день
			Double trueBalance = roundВouble(product.getBalanceStockAndReserves() + currentDate, 0);
			
			if(!product.getIsException()) {
//				System.out.println(trueBalance + " > " + dateRange.stock);
				if(trueBalance > dateRange.stock) {
					//считаем сколько дней нужно прибавить, чтобы заказать товар
					Long deltDate = (long) (trueBalance - dateRange.stock );
					if(message == null) {
						message = "Товара " + product.getCodeProduct() + " ("+product.getName()+")" + " на складе хранится на <strong>" + trueBalance + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней."
								+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy")) + ";</strong>\n";
					}else {
						message = message + "\nТовара " + product.getCodeProduct() + " ("+product.getName()+")" + " на складе хранится на <strong>" + trueBalance + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней. "
								+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy"))+ ";</strong>\n";
					}
					 
				}
			}
		}else { // реализация проверкиЮ когда нужно проверить все продукты, через цикл, которые указангы в заказе
			for (String string : numProductMass) {
				Product productTarget = productService.getProductByCodeAndStock(Integer.parseInt(string), Integer.parseInt(order.getNumStockDelivery()));
				
				if(productTarget != null) {
					if(productTarget.getBalanceStockAndReserves() == null) {
						continue;
					}
//					if(product.getOrderProducts() != null && !product.getOrderProducts().isEmpty()) {
//						continue;
//					}
					if(productTarget.getBalanceStockAndReserves() == 9999.0) {
						continue;
					}
					if(productTarget.getRemainderStockInPall() < 15.0) { //если в паллетах товара меньшге чем 33 - то пропускаем
						continue;
					}
					//считаем разницу в днях сегодняшнеего дня и непосредственно записи
					LocalDateTime start = order.getTimeDelivery().toLocalDateTime();
					LocalDateTime end = LocalDateTime.of(productTarget.getDateUnload().toLocalDate(), LocalTime.now());

					Duration duration = Duration.between(start, end);
					Double currentDate = (double) duration.toDays();
					// считаем правильный остаток на текущий день
					Double trueBalance = roundВouble(productTarget.getBalanceStockAndReserves() + currentDate, 0);
					
					if(!productTarget.getIsException()) {
//						System.out.println(trueBalance + " > " + dateRange.stock);
						if(trueBalance > dateRange.stock) {
							//считаем сколько дней нужно прибавить, чтобы заказать товар
							Long deltDate = (long) (trueBalance - dateRange.stock );
							if(message == null) {
								message = "Товара " + productTarget.getCodeProduct() + " ("+productTarget.getName()+")" + " на складе хранится на <strong>" + trueBalance + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней."
										+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy")) + ";</strong>\n";
							}else {
								message = message + "\nТовара " + productTarget.getCodeProduct() + " ("+productTarget.getName()+")" + " на складе хранится на <strong>" + trueBalance + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней. "
										+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy"))+ ";</strong>\n";
							}
							 
						}
					}
					
				}
			}
		}
		
		
		return message;
	}
	
	private User getThisUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByLogin(name);
		return user;
	}
	
	// округляем числа до 2-х знаков после запятой
		private static double roundВouble(double value, int places) {
			double scale = Math.pow(10, places);
			return Math.round(value * scale) / scale;
		}
}
