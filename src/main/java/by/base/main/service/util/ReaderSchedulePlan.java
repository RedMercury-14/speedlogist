package by.base.main.service.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
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
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import com.dto.PlanResponce;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import by.base.main.aspect.TimedExecution;
import by.base.main.controller.ajax.MainRestController;
import by.base.main.dto.MarketDataFor325Request;
import by.base.main.dto.MarketDataFor325Responce;
import by.base.main.dto.MarketPacketDto;
import by.base.main.dto.MarketRequestDto;
import by.base.main.model.Order;
import by.base.main.model.OrderLine;
import by.base.main.model.OrderProduct;
import by.base.main.model.Permission;
import by.base.main.model.Product;
import by.base.main.model.Role;
import by.base.main.model.Schedule;
import by.base.main.model.User;
import by.base.main.service.OrderProductService;
import by.base.main.service.OrderService;
import by.base.main.service.PermissionService;
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
	
	
	private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
				@Override
				public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					return context.serialize(src.getTime());  // Сериализация даты в миллисекундах
				}
            })
            .create();
	
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
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
    private ServletContext servletContext;
	
	@Autowired
	private MainRestController mainRestController;
	
	private static final Map<String, DayOfWeek> RUSSIAN_DAYS = new HashMap<>();
	/*
	 * здесь хранятся параметры, для запроса по складам по 325 отчёту
	 */
	private static Map<Integer, String> stockParameters = new HashMap<Integer, String>();
	private static Properties properties = null;

    static {
        RUSSIAN_DAYS.put("понедельник", DayOfWeek.MONDAY);
        RUSSIAN_DAYS.put("вторник", DayOfWeek.TUESDAY);
        RUSSIAN_DAYS.put("среда", DayOfWeek.WEDNESDAY);
        RUSSIAN_DAYS.put("четверг", DayOfWeek.THURSDAY);
        RUSSIAN_DAYS.put("пятница", DayOfWeek.FRIDAY);
        RUSSIAN_DAYS.put("суббота", DayOfWeek.SATURDAY);
        RUSSIAN_DAYS.put("воскресенье", DayOfWeek.SUNDAY);
    }
    
    private void initStockParameters() {
    	//инициализируем параметры складов 
        if(stockParameters.isEmpty()) {
			String appPath = servletContext.getRealPath("/");
			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(appPath + "resources/properties/stocksFor325.properties");
				properties = new Properties();
				properties.load(fileInputStream);
				
				properties.entrySet().forEach(e->{
					stockParameters.put(Integer.parseInt(e.getKey().toString()), e.getValue().toString());
				});
				
//				stockParameters.forEach((k,v) -> System.err.println(k + " = " + v));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
    }
    
    private String getTrueStock(Order order) {
		String numStock = null;
		if(order.getIdRamp().toString().length() < 5) {
			System.err.println("Ошибка в названии склада. Склад не может быть двухзначным");
		}
		if(order.getIdRamp().toString().length() < 6) { // проверка на будующее если будет учавстовать склад с трехзначным индексом
			numStock = order.getIdRamp().toString().substring(0, 3);
		}else {
			numStock = order.getIdRamp().toString().substring(0, 4);
		}
		return numStock;		
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
		Integer factStock = Integer.parseInt(getTrueStock(order)); //фактическое значение склада взятое из номера рампы 
		
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
    @TimedExecution
	public PlanResponce getPlanResponce(Order order) {
		String numContract = order.getMarketContractType();
		if(numContract == null) {
			 System.err.println("ReaderSchedulePlan.process: numContract = null");
			 return new PlanResponce(0, "Действие заблокировано!\nНе найден номер контракта в заказе");
		 }
		Date dateNow = Date.valueOf(LocalDate.now());
		Date dateOld2Week = Date.valueOf(LocalDate.now().minusDays(14));
//		List <Order> orders = orderService.getOrderByPeriodDeliveryAndCodeContract(dateNow, dateOld2Week, numContract);
		List <Order> orders = orderService.getOrderByPeriodDeliveryAndCodeContractNotJOIN(dateNow, dateOld2Week, numContract); // заменил метод, на метод без join.
		
		
		if(!orders.contains(order)) {
			orders.add(order);
		}
		
//		Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(order.getMarketContractType())); за ненадобностью
		
		//берем по первой строке заказа и делаем запрос в бд потребности с выгрузкой пяти заказов с совпадениями		
		Set<OrderLine> lines = order.getOrderLines(); // каждая первая строка в заказе
		for (Order orderI : orders) {
			lines.add(orderI.getOrderLines().stream().findFirst().get());
		}
		
		Date dateNowOrderProducts = Date.valueOf(order.getTimeDelivery().toLocalDateTime().toLocalDate());
		Date dateOld3WeekOrderProducts = Date.valueOf(order.getTimeDelivery().toLocalDateTime().toLocalDate().minusDays(30));
		List<OrderProduct> orderProducts = orderProductService.getOrderProductListHasCodeProductGroupAndPeriod(new ArrayList<OrderLine>(lines), dateOld3WeekOrderProducts, dateNowOrderProducts);
//		List<OrderProduct> orderProducts2 = new ArrayList<OrderProduct>();
		
//		for (OrderLine orderLine : lines) { //заменил \тот неэффективный блок на клмплексный запрос getOrderProductListHasCodeProductGroupAndPeriod
//			List<OrderProduct> orderProductsTarget = orderProductService.getOrderProductListHasCodeProductAndPeriod(orderLine, dateOld3WeekOrderProducts, dateNowOrderProducts);
//			if(orderProductsTarget != null && !orderProductsTarget.isEmpty()) {
//				orderProducts2.addAll(orderProductsTarget);				
//			}
//		}		
		
		orderProducts.sort((o1, o2) -> o2.getDateCreate().compareTo(o1.getDateCreate()));// сортируемся от самой ранней даты
		
		Set<Date> dates = new HashSet<Date>();
		for (OrderProduct orderProduct : orderProducts) {
			dates.add(Date.valueOf(orderProduct.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1)));
		}		
		List<Date> result = new ArrayList<Date>(dates);
		result.sort((o1, o2) -> o2.compareTo(o1));// сортируемся от самой ранней даты
		
		return new PlanResponce(200, "Информация о датах заказа", result, null);		
	}
	
	
	private static final int targetDayForBalance = 20;
	/**
	 * метод проверки балансов на складах
	 * где больше товара, где меньше
	 * @return
	 */
	public List<Product> checkBalanceBetweenStock(Order order) {
		Integer targetStock = Integer.parseInt(getTrueStock(order));
		if(targetStock != 1700 && targetStock != 1800) {
			return null;
		}
		
		Map<Long, Double> orderProducts = order.getOrderLinesMap(); //заказанные строки
		List<Long> goodsIds = orderProducts.entrySet().stream()
			    .map(en -> en.getKey())
			    .collect(Collectors.toList()); // отдельный лист с кодами товаров заказанных в заказе
		List<Integer> goodsIdsIntegers = orderProducts.entrySet().stream()
				.map(en -> en.getKey().intValue())
				.collect(Collectors.toList()); // отдельный лист с кодами товаров заказанных в заказе
		Map<Integer, Product> productsMap = productService.getProductMapHasGroupByCode(goodsIdsIntegers);
		List<Product> productsHasBalance = new ArrayList<Product>(); // результирующий лист с продуктами и балансами

		List<Order> orders = null;

//		productsMap.forEach((k,v) -> System.out.println(k + " -- " + v));
		/*
		 * Далее по каждому товару проверяем его остаток с учётом установленных слотов.
		 * !Всегда ратгетимся по дефицитному товару!
		 */
		for (Entry<Long, Double> entry : orderProducts.entrySet()) {
			if(productsMap == null) {
				break;
			}			
			Product generalProduct = productsMap.get(entry.getKey().intValue());
			
//			System.err.println("--> "+generalProduct);

			if(generalProduct == null) {
				continue;
			}
			
			//считаем разницу в днях сегодняшнеего дня и непосредственно записи
			LocalDateTime startLocalDateTime = order.getTimeDelivery().toLocalDateTime();
			LocalDateTime endLocalDateTime;
			if(generalProduct.getDateUnload() != null) {
				endLocalDateTime = LocalDateTime.of(generalProduct.getDateUnload().toLocalDate(), LocalTime.now());
			}else {
				endLocalDateTime = order.getTimeDelivery().toLocalDateTime();
			}
			
			Duration duration = Duration.between(startLocalDateTime, endLocalDateTime);
			Double currentDate = (double) duration.toDays();

			Double remainderInDay1700 = generalProduct.getBalanceStockAndReserves1700() !=null ? roundВouble(generalProduct.getBalanceStockAndReserves1700() +currentDate, 0) : null; // записываем остаток в днях по записи для 1700.
			Double remainderInDay1800 = generalProduct.getBalanceStockAndReserves1800() !=null ? roundВouble(generalProduct.getBalanceStockAndReserves1800() +currentDate, 0) : null;// записываем остаток в днях по записи для 1800.
			Double calculatedPerDay1700 = generalProduct.getCalculatedPerDay1700();  //расчётная реализация в день для 1700 склада
			Double calculatedPerDay1800 = generalProduct.getCalculatedPerDay1800();  //расчётная реализация в день для 1700 склада

			if(generalProduct.getNumStock() == null) {
				continue;
			}

			if(remainderInDay1700 == null || remainderInDay1800 == null || remainderInDay1700 == 9999 || remainderInDay1800 == 9999) {
				System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv ");
				System.out.println("Для продукта " + generalProduct.getName() + " (" + generalProduct.getCodeProduct()+"):");
				System.out.println("Oстаток в днях по записи для 1700 = " + remainderInDay1700);
				System.out.println("Oстаток в днях по записи для 1800 = " + remainderInDay1800);
				System.out.println("Реализация в день для 1700 склада = " + calculatedPerDay1700);
				System.out.println("Реализация в день для 1800 склада = " + calculatedPerDay1800);
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ");
				continue;
			}
			//далее просматриваем что стоит в слотах (должно приехать) и переводем в дни, для суммирования
			Date start;
//			if(generalProduct.getDateUnload() == null) {
//				start = generalProduct.getDateUnload();
//			}else {
//				start = Date.valueOf(generalProduct.getDateCreate().toLocalDateTime().toLocalDate());
//			}
			start = Date.valueOf(order.getTimeDelivery().toLocalDateTime().toLocalDate());
			Date finish = Date.valueOf(LocalDate.now().plusDays(30));
			Double quantityOrderSum1700 = 0.0; // сумма всех заказов товара за заданный период
			Double quantityOrderSum1800 = 0.0; // сумма всех заказов товара за заданный период
			
			if(orders == null) {
				orders = orderService.getOrderGroupByPeriodSlotsAndProductNotJOIN(start, finish, goodsIds);
			}
			
			for (Order order2 : orders) {
				//не учитываем таргетный заказ
				if(order2.getIdOrder().intValue() == order.getIdOrder().intValue()) {
					continue;
				}
				if(order2.getOrderLinesMap().get(Long.parseLong(generalProduct.getCodeProduct()+"")) != null) {
					if(getTrueStock(order2).equals("1700")) {
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
//			System.err.println(generalProduct.getCodeProduct() + " 1700 -- " + remainderInDay1700 + " + " + expectedDays1700 + " ("+quantityOrderSum1700 + "/" +calculatedPerDay1700+")");
//			System.err.println(generalProduct.getCodeProduct() + " 1800 -- " + remainderInDay1800 + " + " + expectedDays1800 + " ("+quantityOrderSum1800 + "/" +calculatedPerDay1800+")");
			
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
	/*
	 * иерархия ошибок такая:
	 * проверки идут как и раньше, однако если будет замечена ошибка балансира - объект с разрешением создаваться не будет (т.к. не решен конфликт заказа и балансира)
	 * когда конфликт балансира решен - если есть проблемы со стоками заказа - создаётся объект разрешения и высылается письмо.
	 * даже если вне графика поставок
	 */
	public PlanResponce process(Order order) {
		initStockParameters();
		 Set<OrderLine> lines = order.getOrderLines(); // строки в заказе
//		 List<Product> products = new ArrayList<Product>(); 
		 Map<Integer,Product> products = new HashMap<Integer, Product>(); 
		 String numContract = order.getMarketContractType();
		 String result = "";
		 if(numContract == null) {
			 System.err.println("ReaderSchedulePlan.process: numContract = null");
			 return new PlanResponce(0, "Действие заблокировано!\nНе найден номер контракта в заказе");
		 }
		 Date dateNow = Date.valueOf(LocalDate.now());
		 String infoRow = "Строк в заказе: " + lines.size();
		 Integer factStock = Integer.parseInt(getTrueStock(order)); //фактическое значение склада взятое из номера рампы
		 for (OrderLine line : lines) {// переделать используя список!
			 Product product = productService.getProductByCode(line.getGoodsId().intValue());
			 if(product != null) {
				 products.put(product.getCodeProduct(),product);				 
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
		 
		 DateRange dateRange = getDateRange(schedule, new ArrayList<>(products.values()), order); // тут раелизуются пункты 2 и 3
		 if(dateRange == null) {
			 System.err.println("dateRange = " + dateRange);
		 }else {
			 System.out.println("dateRange = " + dateRange);			 
		 }
		 
		 if(dateRange == null) {
//			 return new PlanResponce(0, "Действие заблокировано!\nПросчёт кол-ва товара на логистическое плечо невозможен, т.к. расчёт ОРЛ не совпадает с графиком поставок");
			 return new PlanResponce(200, "Просчёт кол-ва товара на логистическое плечо невозможен, т.к. расчёт ОРЛ не совпадает с графиком поставок");
		 }
		 if(dateRange.start == null && dateRange.days == 0) {
			 //тут мы говорим что расчёты ОРЛ по товару отсутствуют но есть липовый график поставок и есть сток в днях. Всё равно проверяем по стоку!
			 
//			 return new PlanResponce(200, "Расчёта заказов по продукту: " + products.get(0).getName() + " ("+products.get(0).getCodeProduct()+") невозможен, т.к. нет в базе данных расчётов потребности");
			 return new PlanResponce(0, "Действие заблокировано!\nРасчёта заказов по продукту: " + products.get(0).getName() + " ("+products.get(0).getCodeProduct()+") невозможен, т.к. нет в базе данных расчётов потребности");
		 }
		 
		 boolean isBalanceMistake = false; //ошибка баланса
		 boolean isMistakeZAQ = false; // ошибка заказа, которую стоит подтвердить
		 
		 List<Product> balance = checkBalanceBetweenStock(order); // проерка балансов
		 
		 if(!checkHasLog(dateRange, order)) {
			 result = result + "<b>Данный заказ " + order.getMarketNumber() + " " + order.getCounterparty() + " установлен не по графику поставок. Он должен быть установлен в диапазоне: с "
					 +dateRange.start.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ", по " + dateRange.end.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"</b>\n";
		 }
		 
		//если входит в лог плече, то находим такие же заказы с такими же SKU
		 List<Order> orders = orderService.getOrderByDateOrderORLAndNumStock(order.getDateOrderOrl(), factStock); // новый метод. Теперь я беду ордеры тупо за указанную дату поставок
		 if(!orders.contains(order)) {
			 orders.add(order);
		 }
		 
		 HashMap<Long, ProductDouble> map = calculateQuantityOrderSum(orders); // тут я получил мапу с кодами товаров и суммой заказа за период.
//		 map.forEach((k,v)->System.out.println(k + " -- " + v));
		
		 for (OrderLine orderLine : lines) {
			Double quantityOrderAll = map.get(orderLine.getGoodsId()).num;
			Product product = products.get(orderLine.getGoodsId().intValue());
							
			if(product!=null) {
				//старый метод
				List<OrderProduct> orderProductORL = new ArrayList<OrderProduct>();
				//берем только за день, который укажут в ОРЛ
				if(order.getDateOrderOrl() != null) {
					orderProductORL.add(product.getOrderProductsHasDateTarget(Date.valueOf(order.getDateOrderOrl().toLocalDate().minusDays(1))));						
				}

				if(orderProductORL != null && !orderProductORL.isEmpty() && orderProductORL.get(0) !=null) {//если есть заказ ОРЛ
					//проверяем на какой склад хотят поставить заказ и берем данные именно этого склада
					int zaq = quantityOrderAll.intValue(); // СУММА заказов по периоду
					int singleZaq = orderLine.getQuantityOrder().intValue(); //Заказ по ордеру (не суммированный)
					int orlZaq;
					int orlZaqMax;
					OrderProduct orderProductTarget = orderProductORL.get(0);
					switch (factStock) {
				    case 1700:
				        orlZaq = orderProductTarget.getQuantity1700() != null ? orderProductORL.get(0).getQuantity1700() : 0;
				        orlZaqMax = orderProductTarget.getQuantity1700Max() != null ? orderProductORL.get(0).getQuantity1700Max() : 0;
				        break;
				    case 1800:
				        orlZaq = orderProductTarget.getQuantity1800() != null ? orderProductORL.get(0).getQuantity1800() : 0;
				        orlZaqMax = orderProductTarget.getQuantity1800Max() != null ? orderProductORL.get(0).getQuantity1800Max() : 0;
				        break;
				    default:
				        orlZaq = orderProductTarget.getQuantity() != null ? orderProductORL.get(0).getQuantity() : 0;
				        orlZaqMax = orderProductTarget.getQuantityMax() != null ? orderProductORL.get(0).getQuantityMax() : 0;
				        break;
					}

					
					// реализация специальной логики: если заказ от менеджера больше или равен заказу от ОРЛ - берем только его заказ
					// если заказ меньше 80% от того что заказал ОРЛ - проверяем другие заказы
					if (singleZaq < 0.8 * orlZaq) {
						//далее проверяем суммарный заказ. А суммарный звказ - это сумма заказов относящихся к дню расчётов и складу.
//						if(zaq > orlZaq*1.1) {
						if(zaq > orlZaqMax) {
							result = result +"<span style=\"color: red;\">"+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + zaq + " шт. ("+map.get(orderLine.getGoodsId()).orderHistory+") из " + orlZaq + " шт.</span>\n";	
							ResultMethod dayStockMessage =  checkNumProductHasStockFromDay(order, product, dateRange); // проверяем по стокам относительно одного продукта (дни остатка)
							if(dayStockMessage.getStatus().intValue() == 200) {
								result = result + dayStockMessage.getMessage() + "\n";
							}
							if(dayStockMessage.getStatus().intValue() == 100) {
								result = result + dayStockMessage.getMessage();
							}
							//пошла проверка балансов
							
							Product balanceProduct;
							if(balance == null || balance.isEmpty()) {
								balanceProduct = null;
							}else {
								balanceProduct = balance.stream().filter(b-> b.getCodeProduct().equals(product.getCodeProduct())).findFirst().orElse(null); // считаем текущий баланс на складе
							}								
							if(balanceProduct != null) {
								result = result +"<span style=\"color: red;\"> Запасы на складах "+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") с учётом слотов : 1700 = "+balanceProduct.getCalculatedDayStock1700()
									+" дн; 1800 = "+ balanceProduct.getCalculatedDayStock1800() +" дн;</span>\n("+balanceProduct.getCalculatedHistory()+")\n";
								if(order.getIdRamp().toString().substring(0, 4).equals("1700")) {
									if(balanceProduct.getCalculatedDayStock1700() > balanceProduct.getCalculatedDayMax()) {
										if(balanceProduct.getCalculatedDayStock1700() > balanceProduct.getCalculatedDayStock1800()) {
											result = result +"Необходимо доставить товар "+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") на <b>1800 склад</b>, т.к. остаток товаров в днях меньше чем на текущем складе;\n\n";
											isBalanceMistake = true;
										}
									}
								}else {
									if(balanceProduct.getCalculatedDayStock1800() > balanceProduct.getCalculatedDayMax()) {
										if(balanceProduct.getCalculatedDayStock1800() > balanceProduct.getCalculatedDayStock1700()) {
											result = result +"Необходимо доставить товар "+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") на <b>1700 склад</b>, т.к. остаток товаров в днях меньше чем на текущем складе;\n\n";
											isBalanceMistake = true;
										}
									}
								}
							}
							if(isBalanceMistake) {
								isMistakeZAQ = true;
							}else {
								//закончилась проверка балансов
								 if(dayStockMessage.getStatus().intValue() == 0) {
						             result = dayStockMessage.getMessage()+"\n" + result;
						             createPermission(order, dayStockMessage.getMessage());					             
						             isMistakeZAQ = true;						             
						         }
							}
						}else if(zaq > orlZaq*1.1 && zaq < orlZaqMax) {//больше чем заказали ОРЛ но меньше чем макс значение заказа #bbaa00
							result = result +"<span style=\"color: #bbaa00;\">"+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + zaq + " шт. ("+map.get(orderLine.getGoodsId()).orderHistory+") из " + orlZaq + " шт. Максимальное значение: "+orlZaqMax+" шт.</span>\n";
						}else {//всё хорошо, в пределах нормы
							result = result +orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + zaq + " шт. ("+map.get(orderLine.getGoodsId()).orderHistory+") из " + orlZaq + " шт.\n";													
						}
					}else {	// если заказ БОЛЬШЕ чем 80% от того что заказал ОРЛ		
//						if(singleZaq > orlZaq*1.1) {
						if(zaq > orlZaqMax) {
							result = result +"<span style=\"color: red;\">"+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + singleZaq + " шт. из " + orlZaq + " шт.</span>\n";	
							ResultMethod dayStockMessage =  checkNumProductHasStockFromDay(order, product, dateRange); // проверяем по стокам относительно одного продукта
							if(dayStockMessage.getStatus().intValue() == 200) {
								result = result + dayStockMessage.getMessage() + "\n";
							}
							if(dayStockMessage.getStatus().intValue() == 100) {
								result = result + dayStockMessage.getMessage();
							}
							//пошла проверка балансов
							if(getTrueStock(order).equals("1700") || getTrueStock(order).equals("1800")) {
								Product balanceProduct;
								if(balance == null || balance.isEmpty()) {
									balanceProduct = null;
								}else {
									balanceProduct = balance.stream().filter(b-> b.getCodeProduct().equals(product.getCodeProduct())).findFirst().orElse(null);
								};
								if(balanceProduct != null) {
									result = result +"<span style=\"color: red;\"> Запасы на складах "+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") с учётом слотов : 1700 = "+balanceProduct.getCalculatedDayStock1700()
										+" дн; 1800 = "+ balanceProduct.getCalculatedDayStock1800() +" дн;</span>\n("+balanceProduct.getCalculatedHistory()+")\n";
									if(getTrueStock(order).equals("1700")) {
										if(balanceProduct.getCalculatedDayStock1700() > balanceProduct.getCalculatedDayMax()) {
											if(balanceProduct.getCalculatedDayStock1700() > balanceProduct.getCalculatedDayStock1800()) {
												result = result +"Необходимо доставить товар "+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") на <b>1800 склад</b>, т.к. остаток товаров в днях меньше чем на текущем складе;\n\n";
												isBalanceMistake = true;
											}
										}
									}else {
										if(balanceProduct.getCalculatedDayStock1800() > balanceProduct.getCalculatedDayMax()) {
											if(balanceProduct.getCalculatedDayStock1800() > balanceProduct.getCalculatedDayStock1700()) {
												result = result +"Необходимо доставить товар "+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") на <b>1700 склад</b>, т.к. остаток товаров в днях меньше чем на текущем складе;\n\n";
												isBalanceMistake = true;
											}
										}
									}
								}
							}
							if(isBalanceMistake) {
								isMistakeZAQ = true;
							}else {
								//закончилась проверка балансов
								 if(dayStockMessage.getStatus().intValue() == 0) {
						             result = dayStockMessage.getMessage()+"\n" + result;
						             createPermission(order, dayStockMessage.getMessage());
						             isMistakeZAQ = true;						             
						         }
							}
						}else if(zaq > orlZaq*1.1 && zaq < orlZaqMax) {//больше чем заказали ОРЛ но меньше чем макс значение заказа #bbaa00
							result = result +"<span style=\"color: #bbaa00;\">"+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + zaq + " шт. ("+map.get(orderLine.getGoodsId()).orderHistory+") из " + orlZaq + " шт. Максимальное значение: "+orlZaqMax+" шт.</span>\n";	
						}else {
							result = result +orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + singleZaq + " шт. из " + orlZaq + " шт.\n";													
						}
					}
					
				}else { // если отсутствует заказ ОРЛ
					ResultMethod dayStockMessage =  checkNumProductHasStockFromDay(order, product, dateRange); // проверяем по стокам относительно одного продукта
					if(dayStockMessage.getStatus().intValue() == 200) {
						result = result + dayStockMessage.getMessage() + "\n";
					}
					if(dayStockMessage.getStatus().intValue() == 100) {
						result = result + dayStockMessage.getMessage();
					}
					 
					result = result +orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - отсутствует в плане заказа (Заказы поставщика от ОРЛ)\n";
					//пошла проверка балансов
					if(getTrueStock(order).equals("1700") || getTrueStock(order).equals("1800")) {
//						Product balanceProduct = balance.stream().filter(b-> b.getCodeProduct().equals(product.getCodeProduct())).findFirst().orElse(null);
						Product balanceProduct;
						if(balance == null || balance.isEmpty()) {
							balanceProduct = null;
						}else {
							balanceProduct = balance.stream().filter(b-> b.getCodeProduct().equals(product.getCodeProduct())).findFirst().orElse(null);
						}
						if(balanceProduct != null) {
							result = result +"<span style=\"color: red;\"> Запасы на складах "+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") с учётом слотов : 1700 = "+balanceProduct.getCalculatedDayStock1700()
								+" дн; 1800 = "+ balanceProduct.getCalculatedDayStock1800() +" дн;</span>\n("+balanceProduct.getCalculatedHistory()+")\n";
							if(getTrueStock(order).equals("1700")) {
								if(balanceProduct.getCalculatedDayStock1700() > balanceProduct.getCalculatedDayMax()) {
									if(balanceProduct.getCalculatedDayStock1700() > balanceProduct.getCalculatedDayStock1800()) {
										result = result +"Необходимо доставить товар "+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") на <b>1800 склад</b>, т.к. остаток товаров в днях меньше чем на текущем складе;\n\n";
										isBalanceMistake = true;
									}
								}
							}else {
								if(balanceProduct.getCalculatedDayStock1800() > balanceProduct.getCalculatedDayMax()) {
									if(balanceProduct.getCalculatedDayStock1800() > balanceProduct.getCalculatedDayStock1700()) {
										result = result +"Необходимо доставить товар "+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") на <b>1700 склад</b>, т.к. остаток товаров в днях меньше чем на текущем складе;\n\n";
										isBalanceMistake = true;
									}
								}
							}
						}
					}
					//закончилась проверка балансов
					if(isBalanceMistake) {
						isMistakeZAQ = true;
					}else {
						//закончилась проверка балансов
						 if(dayStockMessage.getStatus().intValue() == 0) {
				             result = dayStockMessage.getMessage()+"\n" + result;
				             createPermission(order, dayStockMessage.getMessage());
				             isMistakeZAQ = true;						             
				         }
					}
				}
			}
			result = result + "______________________________\n";
		} // ===========================конец цикла
		 
		 if(isMistakeZAQ) {
			 return new PlanResponce(0, "<b>Действие заблокировано!</b>\n"+result);
		 }else {
			 return new PlanResponce(200, result);
		 }		 
	}
	
	/**
	 * Главный метод создания объекта разрешения
	 * @param order
	 * @param dayStockMessage
	 */
	private void createPermission(Order order, String dayStockMessage) {
		User user = getThisUser();
		Permission permission = new Permission();
        permission.setIdObjectApprover(order.getIdOrder());
        permission.setDateValid(Date.valueOf(order.getTimeDelivery().toLocalDateTime().toLocalDate()));
        permission.setUserInitiator(user.getLogin());
        permission.setDateTimeInitiations(Timestamp.valueOf(LocalDateTime.now()));
        permission.setIdUserInitiator(user.getIdUser());
        permission.setNameUserInitiator(user.getSurname() + " " + user.getName());
        permission.setEmailUserInitiator(user.geteMail());
        permission.setTelUserInitiator(user.getTelephone());
        permission.setCommentUserInitiator(dayStockMessage);
        if(!permissionService.checkPermission(permission)) {
       	 permissionService.savePermission(permission);
       	 // сюда вставляем отправку сообщения
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
	 * <br> По сути проверка по дням!
	 * @return
	 */
	private ResultMethod checkNumProductHasStockFromDay(Order order, Product product, DateRange dateRange) {
		
		String message = null;
		User user = getThisUser();
		Role role = user.getRoles().stream().findFirst().get();			
		Double balanceStockAndReserves = null; // остаток в днях на таргетном складе
		
		
//		if(role.getIdRole() == 1 || role.getIdRole() == 2 || role.getIdRole() == 3) { // тут мы говорим что если это логист или админ - в проверке не нуждаемся
//			return null;
//		}
		if(order.getIsInternalMovement() != null && order.getIsInternalMovement().equals("true")) {
			return new ResultMethod("Заказ на внутреннее перемещение. Проверки по остаткам не проводилось.", 200);
//			return null;
		}
		if(order.getNumProduct() == null) {
//			message = "Данные по заказу " + order.getMarketNumber() + " устарели! Обновите заказ."; 
//			return message; временно отключил
			return new ResultMethod("Данные по заказу " + order.getMarketNumber() + " устарели! Обновите заказ.", 200);
//			return null;
		}
		
		String [] numProductMass = order.getNumProduct().split("\\^");
		Integer numStock = Integer.parseInt(getTrueStock(order));
		String referenceInformation = "";
		
		
		//реализация проверки, когда нужно проверить только один продукт
		if(product != null) {
			if(product.getDateUnload() == null) {
				return new ResultMethod("<span style=\"color: #bbaa00;\">Данные по потребностям " + product.getName() + " (" + product.getCodeProduct()+") не прогружены!.", 200);
			}
			LocalDate dateNow = LocalDate.now();
			Period period = Period.between(product.getDateUnload().toLocalDate(), dateNow);
			if(product.getDateUnload() != null && period.getDays()<2) {
				
				if(product.getСalculatedPerDay() == null || product.getСalculatedPerDay() == 0.0) {
					return new ResultMethod("<span style=\"color: #bbaa00;\"><strong>Проверка по стокам не проводилась!</strong> Расчётная реализация товара " + product.getName() + " (" + product.getCodeProduct()+") равна 0.0 !.</span>", 200);
				}
				
				Date date = Date.valueOf(LocalDate.now());
				MarketDataFor325Responce dataFor325Responce = null;
				Double quantityFrom325 = 0.0;
				Double calculatedPerDay = 0.0;
				
				try {
					switch (getTrueStock(order)) {
					case "1700":
						dataFor325Responce = get325AndParam(date.toString(), stockParameters.get(1700), product.getCodeProduct().toString());
						if(dataFor325Responce != null) {
							quantityFrom325 = dataFor325Responce.getRestWithOrderSale();
							calculatedPerDay = product.getСalculatedPerDay() != null ? product.getСalculatedPerDay() : 0.0;
							balanceStockAndReserves = roundВouble(quantityFrom325 / calculatedPerDay, 0);
						}						
						break;
					case "1800":
						dataFor325Responce = get325AndParam(date.toString(), stockParameters.get(1800), product.getCodeProduct().toString());
						if(dataFor325Responce != null) {
							quantityFrom325 = dataFor325Responce.getRestWithOrderSale();
							calculatedPerDay = product.getСalculatedPerDay() != null ? product.getСalculatedPerDay() : 0.0;
							balanceStockAndReserves = roundВouble(quantityFrom325 / calculatedPerDay, 0);
						}						
						break;
					case "1200":
						dataFor325Responce = get325AndParam(date.toString(), stockParameters.get(1200), product.getCodeProduct().toString());
						if(dataFor325Responce != null) {
							quantityFrom325 = dataFor325Responce.getRestWithOrderSale();
							calculatedPerDay = product.getСalculatedPerDay() != null ? product.getСalculatedPerDay() : 0.0;
							balanceStockAndReserves = roundВouble(quantityFrom325 / calculatedPerDay, 0);
						}						
						break;
					case "1250":
						dataFor325Responce = get325AndParam(date.toString(), stockParameters.get(1250), product.getCodeProduct().toString());
						if(dataFor325Responce != null) {
							quantityFrom325 = dataFor325Responce.getRestWithOrderSale();
							calculatedPerDay = product.getСalculatedPerDay() != null ? product.getСalculatedPerDay() : 0.0;
							balanceStockAndReserves = roundВouble(quantityFrom325 / calculatedPerDay, 0);
						}						
						break;	
					}
					
					if(user.getLogin().equals("zaq") || user.getIdUser() == 1) {
						System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv ");
						System.out.println(product.getCodeProduct());
						System.out.println(getTrueStock(order) + " <- stock");
						System.out.println(quantityFrom325 + " <-quantityFrom325");
						System.out.println(calculatedPerDay + " <-calculatedPerDay -> " + product.getСalculatedPerDay());
						System.out.println(balanceStockAndReserves + " <-balanceStockAndReserves ("+quantityFrom325+"/"+calculatedPerDay+")");
						System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ");
					}
					
				
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(dataFor325Responce == null) {
					return new ResultMethod("<span style=\"color: #bbaa00;\"><strong>Проверка по стокам не проводилась!</strong> Данные по потребностям " + product.getName() + " (" + product.getCodeProduct()+") не найдены в 325 отчёте!.</span>", 200);
//					return null;
				}
				if(balanceStockAndReserves <= 0.0) {
					return new ResultMethod("<span style=\"color: #bbaa00;\">Данные по статкам на складах " 
							+ product.getName() + " (" + product.getCodeProduct()+") равны "+balanceStockAndReserves+". Данные по 325 отчёту :" + quantityFrom325 + " шт, расчётная реализация в сутки: " + calculatedPerDay + " шт.</span>", 200);
				}
				
				
				//считаем разницу в днях сегодняшнеего дня и непосредственно записи
				LocalDateTime start = order.getTimeDelivery().toLocalDateTime();
				LocalDateTime end = LocalDateTime.of(product.getDateUnload().toLocalDate(), LocalTime.now());
				
				Duration duration = Duration.between(start, end);
				Double currentDate = (double) duration.toDays();
				// считаем правильный остаток на текущий день
//				System.out.println("Остаток ("+product.getCodeProduct()+") в паллетах на 1700 складе: "+product.getOstInPallets1700());
//				System.out.println("Остаток ("+product.getCodeProduct()+") в паллетах на 1800 складе: "+product.getOstInPallets1800());
//				System.out.println();
				Double summOstPallets = null;
				if(numStock.intValue() == 1700 || numStock.intValue() == 1800) {
					summOstPallets = product.getOstInPallets1700() + product.getOstInPallets1800();
				}else {
					summOstPallets = product.getOstInPallets();
				}
				referenceInformation = "Стоки " + product.getCodeProduct() + " ("+product.getName()+")" + ": " + quantityFrom325 + " (из 325) / " + calculatedPerDay + " (из файла потребности) = "
						+balanceStockAndReserves+ " дн;\n";
				
				
				switch (numStock) {
				case 1700:
					if(product.getOstInPallets1700() == null || product.getBalanceStockAndReserves1700() == null) {
						return new ResultMethod("В файле потребности, по 1700 складу, отсутствуют данные по товару " + product.getName() + " (" + product.getCodeProduct()+").  Проверки по остаткам не проводилось.", 200);
//						return null;
					}
					System.err.println("("+product.getOstInPallets1700() + " + " + product.getOstInPallets1800() + ") < " + "4");
					if(summOstPallets < 4.0) {
						return new ResultMethod("Остаток на 1700 и 1800 складах, в паллетах, суммарно составляет меньше чем 4 паллеты (" + roundВouble(summOstPallets, 0) + ").  Проверки по остаткам не проводилось.", 200);
					}
					
//					System.err.println("Код продукта: " + product.getCodeProduct() + " -> " + balanceStockAndReserves + "(trueBalance1700)" + " > " + dateRange.stock + "(dateRange.stock)");
					if(!product.getIsException()) {
//						System.out.println(balanceStockAndReserves + " > " + dateRange.stock);
						if(balanceStockAndReserves > dateRange.stock) {
							//считаем сколько дней нужно прибавить, чтобы заказать товар
							Long deltDate = (long) (balanceStockAndReserves - dateRange.stock );
							if(message == null) {
								message = "Товара " + product.getCodeProduct() + " ("+product.getName()+")" + " суммарно на 1700 и 1800 хранится на <strong>" + balanceStockAndReserves + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней."
										+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy")) + ";</strong>\n" + referenceInformation;
							}else {
								message = message + "\nТовара " + product.getCodeProduct() + " ("+product.getName()+")" + " суммарно на 1700 и 1800 хранится на <strong>" + balanceStockAndReserves + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней. "
										+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy"))+ ";</strong>\n" + referenceInformation;
							}							 
						}
					}
					break;
					
				case 1800:
					if(product.getOstInPallets1800() == null || product.getBalanceStockAndReserves1800() == null) {
						return new ResultMethod("В файле потребности, по 1800 складу, отсутствуют данные по товару " + product.getName() + " (" + product.getCodeProduct()+").  Проверки по остаткам не проводилось.", 200);
//						return null;
					}
					
//					System.err.println("("+product.getOstInPallets1700() + " + " + product.getOstInPallets1800() + ") < " + "4");
					if(summOstPallets < 4.0) {
						return new ResultMethod("Остаток на 1700 и 1800 складах, в паллетах, суммарно составляет меньше чем 4 паллеты (" + roundВouble(summOstPallets, 0) + ").  Проверки по остаткам не проводилось.", 200);
					}
//					System.err.println("Код продукта: " + product.getCodeProduct() + " -> " + balanceStockAndReserves + "(trueBalance1700)" + " > " + dateRange.stock + "(dateRange.stock)");
					if(!product.getIsException()) {
						if(balanceStockAndReserves > dateRange.stock) {
							//считаем сколько дней нужно прибавить, чтобы заказать товар
							Long deltDate = (long) (balanceStockAndReserves - dateRange.stock );
							if(message == null) {
								message = "Товара " + product.getCodeProduct() + " ("+product.getName()+")" + " суммарно на 1700 и 1800 хранится на <strong>" + balanceStockAndReserves + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней."
										+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy")) + ";</strong>\n" + referenceInformation;
							}else {
								message = message + "\nТовара " + product.getCodeProduct() + " ("+product.getName()+")" + " суммарно на 1700 и 1800 хранится на <strong>" + balanceStockAndReserves + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней. "
										+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy"))+ ";</strong>\n" + referenceInformation;
							}
							 
						}
					}
					break;

				default:
					summOstPallets = product.getOstInPallets();
					if(product.getOstInPallets() == null || product.getBalanceStockAndReserves() == null) {
						return new ResultMethod("В файле потребности, по "+numStock+" складу, отсутствуют данные по товару " + product.getName() + " (" + product.getCodeProduct()+").  Проверки по остаткам не проводилось.", 200);
//						return null;
					}
					if(product.getOstInPallets() < 4.0) { //если в паллетах товара меньшге чем 33 - то пропускаем
						return new ResultMethod("Остаток на "+numStock+" складе , в паллетах, суммарно составляет меньше чем 4 паллеты (" + roundВouble(summOstPallets, 0) + ").  Проверки по остаткам не проводилось.", 200);
//						return null;
					}
					if(!product.getIsException()) {
//						System.out.println(trueBalance + " > " + dateRange.stock);
						if(balanceStockAndReserves > dateRange.stock) {
							//считаем сколько дней нужно прибавить, чтобы заказать товар
							Long deltDate = (long) (balanceStockAndReserves - dateRange.stock );
							if(message == null) {
								message = "Товара " + product.getCodeProduct() + " ("+product.getName()+")" + " на складе хранится на <strong>" + balanceStockAndReserves + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней."
										+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy")) + ";</strong>\n" + referenceInformation;
							}else {
								message = message + "\nТовара " + product.getCodeProduct() + " ("+product.getName()+")" + " на складе хранится на <strong>" + balanceStockAndReserves + "</strong> дней. Ограничение стока (<u>на дату постановки слота</u>), по данному товару: <strong>" + dateRange.stock + "</strong> дней. "
										+" Ближайшая дата на которую можно доставить данный товар: <strong>" + start.toLocalDate().plusDays(deltDate).format(DateTimeFormatter.ofPattern("dd.MM.yyy"))+ ";</strong>\n" + referenceInformation;
							}
							 
						}
					}
					break;
				}
			}else {
				return new ResultMethod("<span style=\"color: #bbaa00;\">Данные по потребностям " + product.getName() + " (" + product.getCodeProduct()+") устаревшие!.  Дата последней прогрузки: " + product.getDateUnload().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "</span>", 200);
			}
		}
		if(message!=null){
			return new ResultMethod(message, 0);
		}else {
			return new ResultMethod(referenceInformation, 100);
		}
		
//		return message;
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
		
		
		/**
		 * Метод, котвечает за запрос и получение 325 отчёта по конкретному коду товара. Отдаёт суммированный результат по заданным складам
		 * @param date
		 * @param stock
		 * @return
		 * @throws ParseException
		 */
		@TimedExecution
		public MarketDataFor325Responce get325AndParam(String date, String stock, String code) throws ParseException {
			String whatBaseStr = "11,21";
			String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.Report325Get\", \"Data\": "
					+ "{\"DateFrom\": \""+date+"\", "
					+ "\"DateTo\": \""+date+"\", "
					+ "\"WarehouseId\": ["+stock+"], "
					+ "\"WhatBase\": ["+whatBaseStr+"], "
					+ "\"GoodsId\": ["+code+"]}}}";
			Map<String, Object> response = new HashMap<>();
			List<MarketDataFor325Responce> marketDataFor325Responcies = new ArrayList<MarketDataFor325Responce>();
			try {
				mainRestController.checkJWT(mainRestController.marketUrl);
			} catch (Exception e) {
				System.err.println("Ошибка получения jwt токена");
			}
			JSONParser parser = new JSONParser();
			JSONObject jsonMainObject = (JSONObject) parser.parse(str);
			String marketPacketDtoStr = jsonMainObject.get("Packet") != null ? jsonMainObject.get("Packet").toString() : null;
			JSONObject jsonMainObject2 = (JSONObject) parser.parse(marketPacketDtoStr);
			String marketDataFor398RequestStr = jsonMainObject2.get("Data") != null ? jsonMainObject2.get("Data").toString() : null;
			JSONObject jsonMainObjectTarget = (JSONObject) parser.parse(marketDataFor398RequestStr);

			JSONArray warehouseIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WarehouseId").toString());
			JSONArray goodsIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("GoodsId").toString());
			JSONArray whatBaseArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WhatBase").toString());

			String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
			String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
			Object[] warehouseId = warehouseIdArray.toArray();
			Object[] goodsId = goodsIdArray.toArray();
			Object[] whatBase = whatBaseArray.toArray();

			MarketDataFor325Request for325Request = new MarketDataFor325Request(dateForm, dateTo, warehouseId, goodsId, whatBase);
			MarketPacketDto marketPacketDto = new MarketPacketDto(mainRestController.marketJWT, "SpeedLogist.Report325Get", mainRestController.serviceNumber, for325Request);
			MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);

			java.util.Date t1 = new java.util.Date();
			String marketOrder2 = mainRestController.postRequest(mainRestController.marketUrl, gson.toJson(requestDto));
			java.util.Date t2 = new java.util.Date();
			
//			System.err.println(marketOrder2);

			if(marketOrder2.equals("503")) { // означает что связь с маркетом потеряна
				//в этом случае проверяем бд
				System.err.println("Связь с маркетом потеряна");
				response.put("status", "503");
				response.put("payload responce", marketOrder2);
				response.put("message", "Связь с маркетом потеряна");
				return null;

			}else{//если есть связь с маркетом
				JSONObject jsonResponceMainObject = (JSONObject) parser.parse(marketOrder2);
				JSONArray jsonResponceTable = (JSONArray) parser.parse(jsonResponceMainObject.get("Table").toString());
				for (Object obj : jsonResponceTable) {
		        	marketDataFor325Responcies.add(new MarketDataFor325Responce(obj.toString())); // парсин json засунул в конструктор
		        }

			}
			
			if(marketDataFor325Responcies.isEmpty()) {
				return null;
			}
			
			if(marketDataFor325Responcies.size() > 1) {
				System.out.println("Пришло " + marketDataFor325Responcies.size() + " объектов");
				MarketDataFor325Responce summDataFor325Responce = new MarketDataFor325Responce();
				Double summ = 0.0;
				for (MarketDataFor325Responce dataFor325Responce : marketDataFor325Responcies) {
					summ = summ + dataFor325Responce.getRestWithOrderSale();
				}
				summDataFor325Responce.setGoodsId(marketDataFor325Responcies.get(0).getGoodsId());
				summDataFor325Responce.setGoodsName(marketDataFor325Responcies.get(0).getGoodsName());
				summDataFor325Responce.setRestWithOrderSale(summ);
				System.out.println("time get325AndParam ---->  " + (t2.getTime() - t1.getTime()) + " ms");
				return summDataFor325Responce;
			}else {
				System.out.println("Пришел 1 объект");
				System.out.println("time get325AndParam ---->" + (t2.getTime() - t1.getTime()) + " ms");
				return marketDataFor325Responcies.get(0);
			}
					
		}
		
		/**
		 * Класс ответов для разных методов.
		 * Со статусом:
		 * 200 - всё гуд
		 * 100 - просто пропускаем
		 * 0 - ошибка/запрет.
		 */
		public class ResultMethod{
			
			/**
			 * @param message
			 * @param status
			 */
			public ResultMethod(String message, Integer status) {
				super();
				this.message = message;
				this.status = status;
			}
			public ResultMethod() {
				super();
			}
			private String message;
			private Integer status;
			public String getMessage() {
				return message;
			}
			public void setMessage(String message) {
				this.message = message;
			}
			public Integer getStatus() {
				return status;
			}
			public void setStatus(Integer status) {
				this.status = status;
			}
			
		}
}

