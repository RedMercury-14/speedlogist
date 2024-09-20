package by.base.main.service.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
 * <br>а так же отдельные команды для крафика поставок.
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
	public DateRange getDateRange (Schedule schedule, List<Product> products) {
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
			return new DateRange(null, null, 0L, null);
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
								
			if(j<0) {
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
		
		
		//тут определения количество дней от заказа до поставки
//		String dayString1 = "SUNDAY";
//		LocalDate datePostav = LocalDate.of(2024, 7, DayOfWeek.valueOf(dayString1).getValue());
//		LocalDate dateOrder = LocalDate.of(2024, 7, RUSSIAN_DAYS.get("понедельник").getValue());
//		
//		int i = datePostav.getDayOfMonth() - dateOrder.getDayOfMonth();
//		System.out.println(i + " - " + dateOrder + " - " + datePostav);		
		
		return new DateRange(Date.valueOf(orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1)),
				Date.valueOf(orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(i+1)), i, dayOfPlanOrder);
	}
	
	
	
	/*
	 * описание метода getActualStock
	 * 1. проходимся по каждому скю и убираем скю, которых нет в потребности на день заказа
	 * принимая тот факт что это завоз их прошлого заказа
	 * 2. По оставшимся скю определяем дату заказа и дату первой поставки.
	 * 3. определяем дату второй поствки
	 * 4. разница между датой заказа из плана и датой второй поставки - есть искомый минимальынй сток
	 */
	/**
	 * Метод, котоырй определяет сколько дней стока долно быть минимально
	 * <br> для товара согласно графику поставок, до второй поставки
	 * <br> Отдаёт количество дней стока, начиная от заказа согласно графику поставок
	 * <br> т.е. Если заказ в понедельник а поставка в среду, то он берет плече с понедельника по среду
	 * <br> и до второй поставки, т.е. до сл. среды <b>(лог плечо + неделя)</b>
	 * @param order
	 * @return
	 */
	public Integer getActualStock(Order order) {
		String numContract = order.getMarketContractType();
		if(numContract == null) {
			 System.err.println("ReaderSchedulePlan.getActualStock: numContract = null");
			 return null;
		 }
		Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(numContract));
		if(schedule == null) {
			 System.err.println("ReaderSchedulePlan.getActualStock: schedule = null");
			 return null;
		 }
		List<OrderLine> orderProducts = order.getOrderLines().stream().collect(Collectors.toList());
		//тут находим продукты в бд
		List<Product> products = new ArrayList<Product>();
		for (OrderLine line : orderProducts) {
			 Product product = productService.getProductByCode(line.getGoodsId().intValue());
			 if(product != null) {
				 products.add(product);				 
			 }
        }
		
		//тут проходимся по потребностям и выносим в отдельный лист orderProductsHasMin ближайший расчёт потребностей по каждому продукту
		List<OrderProduct> orderProductsHasMin = new ArrayList<OrderProduct>();
		for (Product product2 : products) {
			List<OrderProduct> orderProductsHasNow = product2.getOrderProductsListHasDateTarget(Date.valueOf(order.getTimeDelivery().toLocalDateTime().toLocalDate())); // это реализация п.2 (взял +1 день, т.к. заказывают за день поставки)
			orderProductsHasMin.add(orderProductsHasNow.get(0));
		}
		orderProductsHasMin.sort((o1, o2) -> o2.getDateCreate().compareTo(o1.getDateCreate())); // сортируемся от самой ранней даты
		
		//от даты которую получим в orderProductsHasMin определяем количество дней до второй поставки
		Map<String, String> days = schedule.getDaysMap();
		Map<String, String> daysStep2 = days.entrySet().stream().filter(m->m.getValue().contains("понедельник")
				|| m.getValue().contains("вторник")
                || m.getValue().contains("среда")
                || m.getValue().contains("четверг")
                || m.getValue().contains("пятница")
                || m.getValue().contains("суббота")
                || m.getValue().contains("воскресенье")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		OrderProduct orderProductTarget = orderProductsHasMin.get(0);
		String dayOfPlanOrder = orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1).getDayOfWeek().toString(); // планируемый день заказа
		String targetKey = null;
		String targetValue = null;
		for (Entry<String, String> entry : daysStep2.entrySet()) {
			if(entry.getValue().contains(translateToRussianWeek(dayOfPlanOrder))) {
				targetKey = entry.getKey();
				targetValue = entry.getValue();
				break;
			}
		}
		long i = 0;
		
		i = parseWeekNumber(targetValue);
		LocalDate datePostavForCalc = LocalDate.of(2024, 7, DayOfWeek.valueOf(targetKey).getValue());
		
		if(targetValue.split("/").length>1) {
			targetValue = targetValue.split("/")[targetValue.split("/").length - 1];
		}
		
		LocalDate dateOrderCalc = LocalDate.of(2024, 7, RUSSIAN_DAYS.get(targetValue).getValue());
		
		int j = datePostavForCalc.getDayOfMonth() - dateOrderCalc.getDayOfMonth(); // лог плечо
							
		if(j<0) {
			j = j + 7;
		}
		if(j==0) {
			j=7;
		}
		i = i+j;
		
		int log = (int) Duration.between(orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(1).atStartOfDay(), 
				orderProductTarget.getDateCreate().toLocalDateTime().toLocalDate().plusDays(i+1).atStartOfDay()).toDays();
		return log +8;		
	}
	
	
	
	/**
	 * Главный метод проврки заказа по потребностям. Возвращает текстовую информацию
	 * @param order
	 * @return
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
			 Product product = productService.getProductByCode(line.getGoodsId().intValue());
			 if(product != null) {
				 products.add(product);				 
			 }
         }
		 
		 //определяем дату старта расчёта и лог плечо для всего заказа
		 Schedule schedule = scheduleService.getScheduleByNumContract(Long.parseLong(numContract));
		 if(products.isEmpty()) {
			 if(lines.size() == 0) {
				 return new PlanResponce(0, "Действие заблокировано!\nНе найдены товары в заказе.");
			 }else {
				 return new PlanResponce(200, "Не найдены товары в базе данных. " + infoRow);
				 
			 }
		 }
		 
//		 products.forEach(p-> System.out.println(p));
		 
		 
		 
		 DateRange dateRange = getDateRange(schedule, products); // тут раелизуются пункты 2 и 3
		 
		 System.out.println("dateRange = " + dateRange);
		 
		 if(dateRange == null) {
			 return new PlanResponce(0, "Действие заблокировано!\nПросчёт кол-ва товара на логистическое плечо невозможен, т.к. расчёт ОРЛ не совпадает с графиком поставок");
		 }
		 if(dateRange.start == null && dateRange.days == 0) {
			 return new PlanResponce(200, "Расчёта заказов по продукту: " + products.get(0).getName() + " ("+products.get(0).getCodeProduct()+") невозможен, т.к. нет в базе данных расчётов потребности");
		 }
		 
		 //проверка по стокам отностительно графика поставок
		 boolean isMistakeZAQ = false;
		 String dayStockMessage =  checkNumProductHasStock(order, dateRange);
		 if(dayStockMessage!= null) {
             result = dayStockMessage+"\n" + result;
             isMistakeZAQ = true;
         }
		// КОНЕЦ проверка по стокам отностительно графика поставок
		 
		 if(checkHasLog(dateRange, order)) {
			 //если входит в лог плече, то находим такие же заказы с такими же SKU
			 List<Order> orders = orderService.getOrderByTimeDelivery(dateRange.start, dateRange.end);
			 HashMap<Long, Double> map = calculateQuantityOrderSum(orders); // тут я получил мапу с кодами товаров и суммой заказа за период.
//			 map.forEach((k,v)->System.out.println(k + " -- " + v));
			
			 for (OrderLine orderLine : lines) {
				Double quantityOrderAll = map.get(orderLine.getGoodsId());
				Product product = productService.getProductByCode(orderLine.getGoodsId().intValue());
				if(product!=null) {
					List<OrderProduct> quantity = product.getOrderProductsListHasDateTarget(dateNow);
					if(quantity != null) {
						//тут происходит построчная оценка заказанного товара и принятие решения
						int zaq = quantityOrderAll.intValue();
						int orlZaq = quantity.get(0).getQuantity();
						if(zaq > orlZaq*1.1) {
							result = result +"<span style=\"color: red;\">"+orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + quantityOrderAll.intValue() + " шт. из " + quantity.get(0).getQuantity() + " шт.</span>\n";	
//							isMistakeZAQ = true;
						}else {
							result = result +orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - всего заказано " + quantityOrderAll.intValue() + " шт. из " + quantity.get(0).getQuantity() + " шт.\n";													
						}
					}else {
						result = result +orderLine.getGoodsName()+"("+orderLine.getGoodsId()+") - отсутствует в плане заказа (Заказы поставщика от ОРЛ)\n";
					}
				}
			}
			 
		 }else {
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
	 * @param orders
	 * @return
	 */
    public HashMap<Long, Double> calculateQuantityOrderSum(List<Order> orders) {
        // Используем HashMap для хранения результата
        HashMap<Long, Double> orderLineQuantityMap = new HashMap<>();
        
        // Проходим по каждому заказу
        for (Order order : orders) {
            // Получаем список строк заказа (OrderLine)
            Set<OrderLine> orderLines = order.getOrderLines();
            
            // Проходим по каждой строке заказа
            for (OrderLine orderLine : orderLines) {
            	if(!orderLineQuantityMap.containsKey(orderLine.getGoodsId())) {
            		orderLineQuantityMap.put(orderLine.getGoodsId(), orderLine.getQuantityOrder());
            	}else {
            		// Если строка заказа уже есть в HashMap, то добавляем к существующему значению
                    orderLineQuantityMap.put(orderLine.getGoodsId(), orderLineQuantityMap.get(orderLine.getGoodsId()) + orderLine.getQuantityOrder());
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
		
		        
        public DateRange(Date start, Date end, Long days, String dayOfWeekHasOrder) {
        	this.start = start;
            this.end = end;
            this.days = days;
            this.dayOfWeekHasOrder = dayOfWeekHasOrder;;
            this.stock = days + 7;
        }

		@Override
		public String toString() {
			return "DateRange [start=" + start + ", end=" + end + ", days=" + days + ", stock=" + stock
					+ ", dayOfWeekHasOrder=" + dayOfWeekHasOrder + "]";
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
	 * Метод для проверки кол-ва товара на текущий день
	 * если всё ок, возвращает null, если что то не то - сообщение
	 * @return
	 */
	private String checkNumProductHasStock(Order order, DateRange dateRange) {
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
		
		
		for (String string : numProductMass) {
			Product product = productService.getProductByCode(Integer.parseInt(string));
			
			if(product != null) {
				if(product.getBalanceStockAndReserves() == null) {
					continue;
				}
				if(product.getBalanceStockAndReserves() == 9999.0) {
					continue;
				}
//				if(product.getRemainderStockInPall() < 33.0) { //если в паллетах товара меньшге чем 33 - то пропускаем
//					continue;
//				}
				//считаем разницу в днях сегодняшнеего дня и непосредственно записи
				LocalDateTime start = order.getTimeDelivery().toLocalDateTime();
				LocalDateTime end = LocalDateTime.of(product.getDateUnload().toLocalDate(), LocalTime.now());

				Duration duration = Duration.between(start, end);
				Double currentDate = (double) duration.toDays();
				// считаем правильный остаток на текущий день
				Double trueBalance = roundВouble(product.getBalanceStockAndReserves() + currentDate, 0);
				
				//считаем разницу в днях между заказом и постановкой в слоты
//				LocalDateTime startOrder = LocalDateTime.of(dateRange.start.toLocalDate(), LocalTime.of(0, 0));
//				LocalDateTime endOrder = order.getTimeDelivery().toLocalDateTime();
//				Duration durationOrder = Duration.between(startOrder, endOrder);
//				Double currentDateOrder = (double) durationOrder.toDays();
//				// считаем правильный жопустимый сток на сегодняшний день
//				Double trueBalanceOrder = roundВouble(dateRange.stock - currentDateOrder, 0);
				
				System.out.println("Проверка по стокам!");
				
				if(!product.getIsException()) {
					System.out.println(trueBalance + " > " + dateRange.stock);
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
