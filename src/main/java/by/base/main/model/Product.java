package by.base.main.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;

import by.base.main.service.ProductService;

@Entity
@Table(name = "product")
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idproduct")
	private int idProduct;
	
	@Column(name = "code_product")
	private Integer codeProduct;
	
	@Column(name = "rating")
	private Integer rating;
	
	@Column(name = "num_stock")
	private String numStock;
	
	@Column(name = "`group`")
	private String group;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "сalculated_per_day")
	private Double сalculatedPerDay;
	
	@Column(name = "balance_stock")
	private Double balanceStock;
	
	@Column(name = "during_assembly")
	private Double duringAssembly;
	
	@Column(name = "need")
	private Double need;
	
	@Column(name = "remainder_network")
	private Double remainderNetwork;
	
	@Column(name = "remainder_stock_in_pall")
	private Double remainderStockInPall;
	
	@Column(name = "remainder_stock_in_day")
	private Double remainderStockInDay;
	
	@Column(name = "remainder_network_in_day")
	private Double remainderNetworkInDay;
	
	@Column(name = "amount_maintenance")
	private Double amountMaintenance;
	
	@Column(name = "to_with_leftovers_2days")
	private Double TOWithLeftovers2Days;
	
	@Column(name = "percent")
	private Double percent;
	
	@Column(name = "difference")
	private Double difference;
	
	@Column(name = "price_without_NDS")
	private Double priceWithoutNDS;
	
	@Column(name = "expected_arrival")
	private Double expectedArrival;
	
	@Column(name = "reserves")
	private Double reserves;
	
	@Column(name = "reserves_100_50")
	private Double reserves100And50;
	
	@Column(name = "balance_stock_and_reserves")
	private Double balanceStockAndReserves;
	
	@Column(name = "date_create")
	private Timestamp dateCreate;
	
	@Column(name = "date_unload")
	private Date dateUnload;
	
	@Column(name = "promotion_date_start")
	private Date promotionDateStart;
	
	@Column(name = "promotion_date_end")
	private Date promotionDateEnd;
	
	@Column(name = "day_max")
	private Integer dayMax;
	
	@Column(name = "is_exception")
	private Boolean isException;
	
	@Column(name = "sum_field_ost")
	private Double sumFieldOst;

	@Column(name = "sum_field_from_order")
	private Double sumFieldFromOrder;

	@Column(name = "max_otgruzim_in_two_stages")
	private Double maxOtgruzimInTwoStages;

	@Column(name = "max_ost_in_network")
	private Double maxOstInNetwork;

	@Column(name = "ost_in_pallets")
	private Double ostInPallets;

	@Column(name = "max_ost_network_in_days")
	private Double maxOstNetworkInDays;

	@Column(name = "kol")
	private Double kol;

	@Column(name = "summ")
	private Double summ;

	@Column(name = "report_380")
	private Double report380;

	@Column(name = "last_order")
	private Double lastOrder;

	@Column(name = "ost_dost")
	private Double ostDost;

	@Column(name = "one_day")
	private Double oneDay;

	@Column(name = "sum_ost")
	private Double sumOst;

	@Column(name = "сalculated_per_day_1700")
	private Double calculatedPerDay1700;

	@Column(name = "balance_stock_1700")
	private Double balanceStockInDay1700;

	@Column(name = "during_assembly_1700")
	private Double duringAssembly1700;

	@Column(name = "need_1700")
	private Double need1700;

	@Column(name = "remainder_network_1700")
	private Double remainderNetwork1700;

	@Column(name = "remainder_stock_in_pall_1700")
	private Double remainderStockInPall1700;

	@Column(name = "remainder_stock_in_day_1700")
	private Double remainderStockInDay1700;

	@Column(name = "remainder_network_in_day_1700")
	private Double remainderNetworkInDay1700;

	@Column(name = "amount_maintenance_1700")
	private Double amountMaintenance1700;

	@Column(name = "to_with_leftovers_2days_1700")
	private Double toWithLeftovers2Days1700;

	@Column(name = "percent_1700")
	private Double percent1700;

	@Column(name = "difference_1700")
	private Double difference1700;

	@Column(name = "price_without_NDS_1700")
	private Double priceWithoutNDS1700;

	@Column(name = "expected_arrival_1700")
	private Double expectedArrival1700;

	@Column(name = "reserves_1700")
	private Double reserves1700;

	@Column(name = "reserves_100_50_1700")
	private Double reserves10050_1700;

	@Column(name = "balance_stock_and_reserves_1700")
	private Double balanceStockAndReserves1700;

	@Column(name = "sum_field_ost_1700")
	private Double sumFieldOst1700;

	@Column(name = "sum_field_from_order_1700")
	private Double sumFieldFromOrder1700;

	@Column(name = "max_otgruzim_in_two_stages_1700")
	private Double maxOtgruzimInTwoStages1700;

	@Column(name = "max_ost_in_network_1700")
	private Double maxOstInNetwork1700;

	@Column(name = "ost_in_pallets_1700")
	private Double ostInPallets1700;

	@Column(name = "max_ost_network_in_days_1700")
	private Double maxOstNetworkInDays1700;

	@Column(name = "kol_1700")
	private Double kol1700;

	@Column(name = "summ_1700")
	private Double summ1700;

	@Column(name = "last_order_1700")
	private Double lastOrder1700;

	@Column(name = "ost_dost_1700")
	private Double ostDost1700;

	@Column(name = "one_day_1700")
	private Double oneDay1700;

	@Column(name = "sum_ost_1700")
	private Double sumOst1700;

	@Column(name = "сalculated_per_day_1800")
	private Double calculatedPerDay1800;

	@Column(name = "balance_stock_1800")
	private Double balanceStockInDay1800;

	@Column(name = "during_assembly_1800")
	private Double duringAssembly1800;

	@Column(name = "need_1800")
	private Double need1800;

	@Column(name = "remainder_network_1800")
	private Double remainderNetwork1800;

	@Column(name = "remainder_stock_in_pall_1800")
	private Double remainderStockInPall1800;

	@Column(name = "remainder_stock_in_day_1800")
	private Double remainderStockInDay1800;

	@Column(name = "remainder_network_in_day_1800")
	private Double remainderNetworkInDay1800;

	@Column(name = "amount_maintenance_1800")
	private Double amountMaintenance1800;

	@Column(name = "to_with_leftovers_2days_1800")
	private Double toWithLeftovers2Days1800;

	@Column(name = "percent_1800")
	private Double percent1800;

	@Column(name = "difference_1800")
	private Double difference1800;

	@Column(name = "price_without_NDS_1800")
	private Double priceWithoutNDS1800;

	@Column(name = "expected_arrival_1800")
	private Double expectedArrival1800;

	@Column(name = "reserves_1800")
	private Double reserves1800;

	@Column(name = "reserves_100_50_1800")
	private Double reserves10050_1800;

	@Column(name = "balance_stock_and_reserves_1800")
	private Double balanceStockAndReserves1800;

	@Column(name = "sum_field_ost_1800")
	private Double sumFieldOst1800;

	@Column(name = "sum_field_from_order_1800")
	private Double sumFieldFromOrder1800;

	@Column(name = "max_otgruzim_in_two_stages_1800")
	private Double maxOtgruzimInTwoStages1800;

	@Column(name = "max_ost_in_network_1800")
	private Double maxOstInNetwork1800;

	@Column(name = "ost_in_pallets_1800")
	private Double ostInPallets1800;

	@Column(name = "max_ost_network_in_days_1800")
	private Double maxOstNetworkInDays1800;

	@Column(name = "kol_1800")
	private Double kol1800;

	@Column(name = "summ_1800")
	private Double summ1800;

	@Column(name = "last_order_1800")
	private Double lastOrder1800;

	@Column(name = "ost_dost_1800")
	private Double ostDost1800;

	@Column(name = "one_day_1800")
	private Double oneDay1800;

	@Column(name = "sum_ost_1800")
	private Double sumOst1800;
	
	@Column(name = "report_380_1700")
	private Double report380_1700;
	
	@Column(name = "report_380_1800")
	private Double report380_1800;
	
	@Column(name = "moved_from_1800_to_1700")
	private Double movedFrom1800To1700;
	
	@Column(name = "moved_from_1700_to_1800")
	private Double movedFrom1700To1800;

	
	/**
	 * расчётный остаток для 1700 слкада в днях
	 */
	@Transient
	private Double calculatedDayStock1700;
	
	/**
	 * расчётный остаток для 1800 слкада в днях
	 */
	@Transient
	private Double calculatedDayStock1800;
	
	/**
	 * макс. кол-во дней для проверки
	 */
	@Transient
	private Double calculatedDayMax;
	
	/**
	 * Развертка расчётов
	 */
	@Transient
	private String calculatedHistory;
	
	@OneToMany(fetch=FetchType.LAZY, orphanRemoval = true,
			   mappedBy="product",
			   cascade= {CascadeType.ALL})
	private Set<OrderProduct> orderProducts;
	
	public Product() {
		super();
	}

	public Product(String numStock, Double сalculatedPerDay, Double balanceStockAndReserves) {
		super();
		this.numStock = numStock;
		this.сalculatedPerDay = сalculatedPerDay;
		this.balanceStockAndReserves = balanceStockAndReserves;
	}



	/**
	 * Развертка расчётов
	 */
	public String getCalculatedHistory() {
		return calculatedHistory;
	}
	/**
	 * Развертка расчётов
	 */
	public void setCalculatedHistory(String calculatedHistory) {
		this.calculatedHistory = calculatedHistory;
	}

	/**
	 * @return the idProduct
	 */
	public int getIdProduct() {
		return idProduct;
	}

	/**
	 * @param idProduct the idProduct to set
	 */
	public void setIdProduct(int idProduct) {
		this.idProduct = idProduct;
	}

	/**
	 * @return Код товара (уникальный)
	 */
	public Integer getCodeProduct() {
		return codeProduct;
	}

	/**
	 * Код товара (уникальный)
	 * @param codeProduct the codeProduct to set
	 */
	public void setCodeProduct(Integer codeProduct) {
		this.codeProduct = codeProduct;
	}

	/**
	 * рейтинг
	 */
	public Integer getRating() {
		return rating;
	}

	/**
	 * рейтинг
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * Номер склада
	 */
	public String getNumStock() {
		return numStock;
	}

	/**
	 * Номер склада
	 */
	public void setNumStock(String numStock) {
		this.numStock = numStock;
	}

	/**
	 * @return группа
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * группа
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Наиминование товара
	 */
	public String getName() {
		return name;
	}

	/**
	 * Наиминование товара
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Реализация расчётная в день
	 */
	public Double getСalculatedPerDay() {
		return сalculatedPerDay;
	}

	/**
	 * Реализация расчётная в день
	 */
	public void setСalculatedPerDay(Double сalculatedPerDay) {
		this.сalculatedPerDay = сalculatedPerDay;
	}

	/**
	 * Остаток на РЦ
	 */
	public Double getBalanceStock() {
		return balanceStock;
	}

	/**
	 * Остаток на РЦ
	 */
	public void setBalanceStock(Double balanceStock) {
		this.balanceStock = balanceStock;
	}

	/**
	 * В процессе сборки
	 */
	public Double getDuringAssembly() {
		return duringAssembly;
	}

	/**
	 * В процессе сборки
	 */
	public void setDuringAssembly(Double duringAssembly) {
		this.duringAssembly = duringAssembly;
	}

	/**
	 * Потребность
	 */
	public Double getNeed() {
		return need;
	}

	/**
	 * Потребность
	 */
	public void setNeed(Double need) {
		this.need = need;
	}

	/**
	 * Ост. в СЕТИ
	 */
	public Double getRemainderNetwork() {
		return remainderNetwork;
	}

	/**
	 * Ост. в СЕТИ
	 */
	public void setRemainderNetwork(Double remainderNetwork) {
		this.remainderNetwork = remainderNetwork;
	}

	/**
	 * Ост РЦ в поддонах
	 */
	public Double getRemainderStockInPall() {
		return remainderStockInPall;
	}

	/**
	 * Ост РЦ в поддонах
	 */
	public void setRemainderStockInPall(Double remainderStockInPall) {
		this.remainderStockInPall = remainderStockInPall;
	}

	/**
	 * Ост на РЦ в днях
	 */
	public Double getRemainderStockInDay() {
		return remainderStockInDay;
	}

	/**
	 * Ост на РЦ в днях
	 */
	public void setRemainderStockInDay(Double remainderStockInDay) {
		this.remainderStockInDay = remainderStockInDay;
	}

	/**
	 * Ост СЕТИ в днях
	 */
	public Double getRemainderNetworkInDay() {
		return remainderNetworkInDay;
	}

	/**
	 * Ост СЕТИ в днях
	 */
	public void setRemainderNetworkInDay(Double remainderNetworkInDay) {
		this.remainderNetworkInDay = remainderNetworkInDay;
	}

	/**
	 * колич ТО в рассчете'
	 */
	public Double getAmountMaintenance() {
		return amountMaintenance;
	}

	/**
	 * колич ТО в рассчете'
	 */
	public void setAmountMaintenance(Double amountMaintenance) {
		this.amountMaintenance = amountMaintenance;
	}

	/**
	 * ТО с остаками меньше, чем на 2 дня
	 */
	public Double getTOWithLeftovers2Days() {
		return TOWithLeftovers2Days;
	}

	/**
	 * ТО с остаками меньше, чем на 2 дня
	 */
	public void setTOWithLeftovers2Days(Double tOWithLeftovers2Days) {
		TOWithLeftovers2Days = tOWithLeftovers2Days;
	}

	/**
	 * % магазинов с остатками меньше,чем на 2 дня, к общему количеству магазинов в расчете
	 */
	public Double getPercent() {
		return percent;
	}

	/**
	 * % магазинов с остатками меньше,чем на 2 дня, к общему количеству магазинов в расчете
	 */
	public void setPercent(Double percent) {
		this.percent = percent;
	}

	/**
	 * Разница
	 */
	public Double getDifference() {
		return difference;
	}

	/**
	 * Разница
	 */
	public void setDifference(Double difference) {
		this.difference = difference;
	}

	/**
	 * Сумма без НДС
	 */
	public Double getPriceWithoutNDS() {
		return priceWithoutNDS;
	}

	/**
	 * Сумма без НДС
	 */
	public void setPriceWithoutNDS(Double priceWithoutNDS) {
		this.priceWithoutNDS = priceWithoutNDS;
	}

	/**
	 * Ожидается приход
	 */
	public Double getExpectedArrival() {
		return expectedArrival;
	}

	/**
	 * Ожидается приход
	 */
	public void setExpectedArrival(Double expectedArrival) {
		this.expectedArrival = expectedArrival;
	}

	/**
	 * Запасники  60,101,110,116,1101,1110,1114,1116,1119,1120,1150,1199,1201,1207,1210,1214,1231,1251,1700,1001700,1701,1714,1719
	 */
	public Double getReserves() {
		return reserves;
	}

	/**
	 * Запасники  60,101,110,116,1101,1110,1114,1116,1119,1120,1150,1199,1201,1207,1210,1214,1231,1251,1700,1001700,1701,1714,1719
	 */
	public void setReserves(Double reserves) {
		this.reserves = reserves;
	}

	/**
	 * Запасник   100, 50
	 */
	public Double getReserves100And50() {
		return reserves100And50;
	}

	/**
	 * Запасник   100, 50
	 */
	public void setReserves100And50(Double reserves100And50) {
		this.reserves100And50 = reserves100And50;
	}

	/**
	 * Ост на РЦ + запасники в днях
	 */
	public Double getBalanceStockAndReserves() {
		return balanceStockAndReserves;
	}

	/**
	 * Ост на РЦ + запасники в днях
	 */
	public void setBalanceStockAndReserves(Double balanceStockAndReserves) {
		this.balanceStockAndReserves = balanceStockAndReserves;
	}

	/**
	 * дата и время загрузки на сервер
	 */
	public Timestamp getDateCreate() {
		return dateCreate;
	}

	/**
	 * дата и время загрузки на сервер
	 */
	public void setDateCreate(Timestamp dateCreate) {
		this.dateCreate = dateCreate;
	}

	/**
	 * Дата остатков (непосредственно из файла)
	 */
	public Date getDateUnload() {
		return dateUnload;
	}

	/**
	 * Дата остатков (непосредственно из файла)
	 */
	public void setDateUnload(Date dateUnload) {
		this.dateUnload = dateUnload;
	}

	/**
	 * Дата начала акции
	 */
	public Date getPromotionDateStart() {
		return promotionDateStart;
	}

	/**
	 * Дата начала акции
	 */
	public void setPromotionDateStart(Date promotionDateStart) {
		this.promotionDateStart = promotionDateStart;
	}

	/**
	 * Дата окончания акции
	 */
	public Date getPromotionDateEnd() {
		return promotionDateEnd;
	}

	/**
	 * Дата окончания акции
	 */
	public void setPromotionDateEnd(Date promotionDateEnd) {
		this.promotionDateEnd = promotionDateEnd;
	}

	/**
	 * Максимальное кол-во дней остатка на складе по каждому коду товара
	 */
	public Integer getDayMax() {
		return dayMax;
	}

	/**
	 * Максимальное кол-во дней остатка на складе по каждому коду товара
	 */
	public void setDayMax(Integer dayMax) {
		this.dayMax = dayMax;
	}

	/**
	 * boolean значене. Если true - то исключается из проверок в слотах
	 */
	public Boolean getIsException() {
		return isException;
	}

	/**
	 * boolean значене. Если true - то исключается из проверок в слотах
	 */
	public void setIsException(Boolean isException) {
		this.isException = isException;
	}
	
	
	/**
	 * Возвращает заказы по данному продукту
	 * @return
	 */
	public Set<OrderProduct> getOrderProducts() {
		return orderProducts;
	}

	public void setOrderProducts(Set<OrderProduct> orderProducts) {
		this.orderProducts = orderProducts;
	}
	
	public void addOrderProducts(OrderProduct orderProduct) {
		if(orderProducts == null) {
			orderProducts = new HashSet<OrderProduct>();
			orderProducts.add(orderProduct);
		}else {
			orderProducts.add(orderProduct);
		}
	}
	
	
	/**
	 * Возвращает остортированный список с заказами <b>(потребностью)</b> продуктов начиная от самой раннего расчёта к targetDate
	 * <br> Если заказов вообще нет - то возвращает null
	 * @param targetDate
	 * @return
	 */
	public List<OrderProduct> getOrderProductsListHasDateTarget(Date targetDate) {		
		if(orderProducts != null && !orderProducts.isEmpty()) {
			return orderProducts.stream()
	                .filter(obj -> obj.getDateCreate().before(targetDate)) // Фильтруем только те, которые позднее targetDate
	                .sorted((obj1, obj2) -> Long.compare(
	                        obj2.getDateCreate().getTime() - targetDate.getTime(),
	                        obj1.getDateCreate().getTime() - targetDate.getTime()
	                ))
	                .collect(Collectors.toList());
		}else {
			return null;
		}
		
    }
	
	/**
	 * Возвращает заказ продукта, за таргетную дату
	 * @param targetDate
	 * @return
	 */
	public OrderProduct getOrderProductsHasDateTarget(Date targetDate) {		
		if(orderProducts != null && !orderProducts.isEmpty()) {
			return orderProducts.stream()
	                .filter(obj -> obj.getDateCreate().toLocalDateTime().toLocalDate().equals(targetDate.toLocalDate()))
	                .findFirst()
	                .orElse(null);
		}else {
			return null;
		}
		
    }
	
	
	/**
	 * расчётный остаток для 1700 слкада в днях
	 */
	public Double getCalculatedDayStock1700() {
		return calculatedDayStock1700;
	}

	/**
	 * расчётный остаток для 1700 слкада в днях
	 */
	public void setCalculatedDayStock1700(Double calculatedDayStock1700) {
		this.calculatedDayStock1700 = calculatedDayStock1700;
	}

	/**
	 * расчётный остаток для 1800 слкада в днях
	 */
	public Double getCalculatedDayStock1800() {
		return calculatedDayStock1800;
	}

	/**
	 * расчётный остаток для 1800 слкада в днях
	 */
	public void setCalculatedDayStock1800(Double calculatedDayStock1800) {
		this.calculatedDayStock1800 = calculatedDayStock1800;
	}

	/**
	 * макс. кол-во дней для проверки
	 */
	public Double getCalculatedDayMax() {
		return calculatedDayMax;
	}

	/**
	 * макс. кол-во дней для проверки
	 */
	public void setCalculatedDayMax(Double calculatedDayMax) {
		this.calculatedDayMax = calculatedDayMax;
	}

	

	public Double getSumFieldOst() {
		return sumFieldOst;
	}

	public void setSumFieldOst(Double sumFieldOst) {
		this.sumFieldOst = sumFieldOst;
	}

	public Double getSumFieldFromOrder() {
		return sumFieldFromOrder;
	}

	public void setSumFieldFromOrder(Double sumFieldFromOrder) {
		this.sumFieldFromOrder = sumFieldFromOrder;
	}

	public Double getMaxOtgruzimInTwoStages() {
		return maxOtgruzimInTwoStages;
	}

	public void setMaxOtgruzimInTwoStages(Double maxOtgruzimInTwoStages) {
		this.maxOtgruzimInTwoStages = maxOtgruzimInTwoStages;
	}

	public Double getMaxOstInNetwork() {
		return maxOstInNetwork;
	}

	public void setMaxOstInNetwork(Double maxOstInNetwork) {
		this.maxOstInNetwork = maxOstInNetwork;
	}

	public Double getOstInPallets() {
		return ostInPallets;
	}

	public void setOstInPallets(Double ostInPallets) {
		this.ostInPallets = ostInPallets;
	}

	public Double getMaxOstNetworkInDays() {
		return maxOstNetworkInDays;
	}

	public void setMaxOstNetworkInDays(Double maxOstNetworkInDays) {
		this.maxOstNetworkInDays = maxOstNetworkInDays;
	}

	public Double getKol() {
		return kol;
	}

	public void setKol(Double kol) {
		this.kol = kol;
	}

	public Double getSumm() {
		return summ;
	}

	public void setSumm(Double summ) {
		this.summ = summ;
	}

	public Double getReport380() {
		return report380;
	}

	public void setReport380(Double report380) {
		this.report380 = report380;
	}

	public Double getLastOrder() {
		return lastOrder;
	}

	public void setLastOrder(Double lastOrder) {
		this.lastOrder = lastOrder;
	}

	public Double getOstDost() {
		return ostDost;
	}

	public void setOstDost(Double ostDost) {
		this.ostDost = ostDost;
	}

	public Double getOneDay() {
		return oneDay;
	}

	public void setOneDay(Double oneDay) {
		this.oneDay = oneDay;
	}

	public Double getSumOst() {
		return sumOst;
	}

	public void setSumOst(Double sumOst) {
		this.sumOst = sumOst;
	}

	public Double getCalculatedPerDay1700() {
		return calculatedPerDay1700;
	}

	public void setCalculatedPerDay1700(Double calculatedPerDay1700) {
		this.calculatedPerDay1700 = calculatedPerDay1700;
	}

	public Double getBalanceStockInDay1700() {
		return balanceStockInDay1700;
	}

	public void setBalanceStockInDay1700(Double balanceStock1700) {
		this.balanceStockInDay1700 = balanceStock1700;
	}

	public Double getDuringAssembly1700() {
		return duringAssembly1700;
	}

	public void setDuringAssembly1700(Double duringAssembly1700) {
		this.duringAssembly1700 = duringAssembly1700;
	}

	public Double getNeed1700() {
		return need1700;
	}

	public void setNeed1700(Double need1700) {
		this.need1700 = need1700;
	}

	public Double getRemainderNetwork1700() {
		return remainderNetwork1700;
	}

	public void setRemainderNetwork1700(Double remainderNetwork1700) {
		this.remainderNetwork1700 = remainderNetwork1700;
	}

	public Double getRemainderStockInPall1700() {
		return remainderStockInPall1700;
	}

	public void setRemainderStockInPall1700(Double remainderStockInPall1700) {
		this.remainderStockInPall1700 = remainderStockInPall1700;
	}

	public Double getRemainderStockInDay1700() {
		return remainderStockInDay1700;
	}

	public void setRemainderStockInDay1700(Double remainderStockInDay1700) {
		this.remainderStockInDay1700 = remainderStockInDay1700;
	}

	public Double getRemainderNetworkInDay1700() {
		return remainderNetworkInDay1700;
	}

	public void setRemainderNetworkInDay1700(Double remainderNetworkInDay1700) {
		this.remainderNetworkInDay1700 = remainderNetworkInDay1700;
	}

	public Double getAmountMaintenance1700() {
		return amountMaintenance1700;
	}

	public void setAmountMaintenance1700(Double amountMaintenance1700) {
		this.amountMaintenance1700 = amountMaintenance1700;
	}

	public Double getToWithLeftovers2Days1700() {
		return toWithLeftovers2Days1700;
	}

	public void setToWithLeftovers2Days1700(Double toWithLeftovers2Days1700) {
		this.toWithLeftovers2Days1700 = toWithLeftovers2Days1700;
	}

	public Double getPercent1700() {
		return percent1700;
	}

	public void setPercent1700(Double percent1700) {
		this.percent1700 = percent1700;
	}

	public Double getDifference1700() {
		return difference1700;
	}

	public void setDifference1700(Double difference1700) {
		this.difference1700 = difference1700;
	}

	public Double getPriceWithoutNDS1700() {
		return priceWithoutNDS1700;
	}

	public void setPriceWithoutNDS1700(Double priceWithoutNDS1700) {
		this.priceWithoutNDS1700 = priceWithoutNDS1700;
	}

	public Double getExpectedArrival1700() {
		return expectedArrival1700;
	}

	public void setExpectedArrival1700(Double expectedArrival1700) {
		this.expectedArrival1700 = expectedArrival1700;
	}

	public Double getReserves1700() {
		return reserves1700;
	}

	public void setReserves1700(Double reserves1700) {
		this.reserves1700 = reserves1700;
	}

	public Double getReserves10050_1700() {
		return reserves10050_1700;
	}

	public void setReserves10050_1700(Double reserves10050_1700) {
		this.reserves10050_1700 = reserves10050_1700;
	}

	public Double getBalanceStockAndReserves1700() {
		return balanceStockAndReserves1700;
	}

	public void setBalanceStockAndReserves1700(Double balanceStockAndReserves1700) {
		this.balanceStockAndReserves1700 = balanceStockAndReserves1700;
	}

	public Double getSumFieldOst1700() {
		return sumFieldOst1700;
	}

	public void setSumFieldOst1700(Double sumFieldOst1700) {
		this.sumFieldOst1700 = sumFieldOst1700;
	}

	public Double getSumFieldFromOrder1700() {
		return sumFieldFromOrder1700;
	}

	public void setSumFieldFromOrder1700(Double sumFieldFromOrder1700) {
		this.sumFieldFromOrder1700 = sumFieldFromOrder1700;
	}

	public Double getMaxOtgruzimInTwoStages1700() {
		return maxOtgruzimInTwoStages1700;
	}

	public void setMaxOtgruzimInTwoStages1700(Double maxOtgruzimInTwoStages1700) {
		this.maxOtgruzimInTwoStages1700 = maxOtgruzimInTwoStages1700;
	}

	public Double getMaxOstInNetwork1700() {
		return maxOstInNetwork1700;
	}

	public void setMaxOstInNetwork1700(Double maxOstInNetwork1700) {
		this.maxOstInNetwork1700 = maxOstInNetwork1700;
	}

	public Double getOstInPallets1700() {
		return ostInPallets1700;
	}

	public void setOstInPallets1700(Double ostInPallets1700) {
		this.ostInPallets1700 = ostInPallets1700;
	}

	public Double getMaxOstNetworkInDays1700() {
		return maxOstNetworkInDays1700;
	}

	public void setMaxOstNetworkInDays1700(Double maxOstNetworkInDays1700) {
		this.maxOstNetworkInDays1700 = maxOstNetworkInDays1700;
	}

	public Double getKol1700() {
		return kol1700;
	}

	public void setKol1700(Double kol1700) {
		this.kol1700 = kol1700;
	}

	public Double getSumm1700() {
		return summ1700;
	}

	public void setSumm1700(Double summ1700) {
		this.summ1700 = summ1700;
	}

	public Double getLastOrder1700() {
		return lastOrder1700;
	}

	public void setLastOrder1700(Double lastOrder1700) {
		this.lastOrder1700 = lastOrder1700;
	}

	public Double getOstDost1700() {
		return ostDost1700;
	}

	public void setOstDost1700(Double ostDost1700) {
		this.ostDost1700 = ostDost1700;
	}

	public Double getOneDay1700() {
		return oneDay1700;
	}

	public void setOneDay1700(Double oneDay1700) {
		this.oneDay1700 = oneDay1700;
	}

	public Double getSumOst1700() {
		return sumOst1700;
	}

	public void setSumOst1700(Double sumOst1700) {
		this.sumOst1700 = sumOst1700;
	}

	public Double getCalculatedPerDay1800() {
		return calculatedPerDay1800;
	}

	public void setCalculatedPerDay1800(Double calculatedPerDay1800) {
		this.calculatedPerDay1800 = calculatedPerDay1800;
	}

	public Double getBalanceStockInDay1800() {
		return balanceStockInDay1800;
	}

	public void setBalanceStockInDay1800(Double balanceStock1800) {
		this.balanceStockInDay1800 = balanceStock1800;
	}

	public Double getDuringAssembly1800() {
		return duringAssembly1800;
	}

	public void setDuringAssembly1800(Double duringAssembly1800) {
		this.duringAssembly1800 = duringAssembly1800;
	}

	public Double getNeed1800() {
		return need1800;
	}

	public void setNeed1800(Double need1800) {
		this.need1800 = need1800;
	}

	public Double getRemainderNetwork1800() {
		return remainderNetwork1800;
	}

	public void setRemainderNetwork1800(Double remainderNetwork1800) {
		this.remainderNetwork1800 = remainderNetwork1800;
	}

	public Double getRemainderStockInPall1800() {
		return remainderStockInPall1800;
	}

	public void setRemainderStockInPall1800(Double remainderStockInPall1800) {
		this.remainderStockInPall1800 = remainderStockInPall1800;
	}

	public Double getRemainderStockInDay1800() {
		return remainderStockInDay1800;
	}

	public void setRemainderStockInDay1800(Double remainderStockInDay1800) {
		this.remainderStockInDay1800 = remainderStockInDay1800;
	}

	public Double getRemainderNetworkInDay1800() {
		return remainderNetworkInDay1800;
	}

	public void setRemainderNetworkInDay1800(Double remainderNetworkInDay1800) {
		this.remainderNetworkInDay1800 = remainderNetworkInDay1800;
	}

	public Double getAmountMaintenance1800() {
		return amountMaintenance1800;
	}

	public void setAmountMaintenance1800(Double amountMaintenance1800) {
		this.amountMaintenance1800 = amountMaintenance1800;
	}

	public Double getToWithLeftovers2Days1800() {
		return toWithLeftovers2Days1800;
	}

	public void setToWithLeftovers2Days1800(Double toWithLeftovers2Days1800) {
		this.toWithLeftovers2Days1800 = toWithLeftovers2Days1800;
	}

	public Double getPercent1800() {
		return percent1800;
	}

	public void setPercent1800(Double percent1800) {
		this.percent1800 = percent1800;
	}

	public Double getDifference1800() {
		return difference1800;
	}

	public void setDifference1800(Double difference1800) {
		this.difference1800 = difference1800;
	}

	public Double getPriceWithoutNDS1800() {
		return priceWithoutNDS1800;
	}

	public void setPriceWithoutNDS1800(Double priceWithoutNDS1800) {
		this.priceWithoutNDS1800 = priceWithoutNDS1800;
	}

	public Double getExpectedArrival1800() {
		return expectedArrival1800;
	}

	public void setExpectedArrival1800(Double expectedArrival1800) {
		this.expectedArrival1800 = expectedArrival1800;
	}

	public Double getReserves1800() {
		return reserves1800;
	}

	public void setReserves1800(Double reserves1800) {
		this.reserves1800 = reserves1800;
	}

	public Double getReserves10050_1800() {
		return reserves10050_1800;
	}

	public void setReserves10050_1800(Double reserves10050_1800) {
		this.reserves10050_1800 = reserves10050_1800;
	}

	public Double getBalanceStockAndReserves1800() {
		return balanceStockAndReserves1800;
	}

	public void setBalanceStockAndReserves1800(Double balanceStockAndReserves1800) {
		this.balanceStockAndReserves1800 = balanceStockAndReserves1800;
	}

	public Double getSumFieldOst1800() {
		return sumFieldOst1800;
	}

	public void setSumFieldOst1800(Double sumFieldOst1800) {
		this.sumFieldOst1800 = sumFieldOst1800;
	}

	public Double getSumFieldFromOrder1800() {
		return sumFieldFromOrder1800;
	}

	public void setSumFieldFromOrder1800(Double sumFieldFromOrder1800) {
		this.sumFieldFromOrder1800 = sumFieldFromOrder1800;
	}

	public Double getMaxOtgruzimInTwoStages1800() {
		return maxOtgruzimInTwoStages1800;
	}

	public void setMaxOtgruzimInTwoStages1800(Double maxOtgruzimInTwoStages1800) {
		this.maxOtgruzimInTwoStages1800 = maxOtgruzimInTwoStages1800;
	}

	public Double getMaxOstInNetwork1800() {
		return maxOstInNetwork1800;
	}

	public void setMaxOstInNetwork1800(Double maxOstInNetwork1800) {
		this.maxOstInNetwork1800 = maxOstInNetwork1800;
	}

	public Double getOstInPallets1800() {
		return ostInPallets1800;
	}

	public void setOstInPallets1800(Double ostInPallets1800) {
		this.ostInPallets1800 = ostInPallets1800;
	}

	public Double getMaxOstNetworkInDays1800() {
		return maxOstNetworkInDays1800;
	}

	public void setMaxOstNetworkInDays1800(Double maxOstNetworkInDays1800) {
		this.maxOstNetworkInDays1800 = maxOstNetworkInDays1800;
	}

	public Double getKol1800() {
		return kol1800;
	}

	public void setKol1800(Double kol1800) {
		this.kol1800 = kol1800;
	}

	public Double getSumm1800() {
		return summ1800;
	}

	public void setSumm1800(Double summ1800) {
		this.summ1800 = summ1800;
	}

	public Double getLastOrder1800() {
		return lastOrder1800;
	}

	public void setLastOrder1800(Double lastOrder1800) {
		this.lastOrder1800 = lastOrder1800;
	}

	public Double getOstDost1800() {
		return ostDost1800;
	}

	public void setOstDost1800(Double ostDost1800) {
		this.ostDost1800 = ostDost1800;
	}

	public Double getOneDay1800() {
		return oneDay1800;
	}

	public void setOneDay1800(Double oneDay1800) {
		this.oneDay1800 = oneDay1800;
	}

	public Double getSumOst1800() {
		return sumOst1800;
	}

	public void setSumOst1800(Double sumOst1800) {
		this.sumOst1800 = sumOst1800;
	}

	

	public Double getReport380_1700() {
		return report380_1700;
	}

	public void setReport380_1700(Double report380_1700) {
		this.report380_1700 = report380_1700;
	}

	public Double getReport380_1800() {
		return report380_1800;
	}

	public void setReport380_1800(Double report380_1800) {
		this.report380_1800 = report380_1800;
	}

	public Double getMovedFrom1800To1700() {
		return movedFrom1800To1700;
	}

	public void setMovedFrom1800To1700(Double movedFrom1800To1700) {
		this.movedFrom1800To1700 = movedFrom1800To1700;
	}

	public Double getMovedFrom1700To1800() {
		return movedFrom1700To1800;
	}

	public void setMovedFrom1700To1800(Double movedFrom1700To1800) {
		this.movedFrom1700To1800 = movedFrom1700To1800;
	}

	@Override
	public int hashCode() {
		return Objects.hash(codeProduct);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return Objects.equals(codeProduct, other.codeProduct);
	}

	@Override
	public String toString() {
		return "Product [idProduct=" + idProduct + ", codeProduct=" + codeProduct + ", rating=" + rating + ", numStock="
				+ numStock + ", group=" + group + ", name=" + name + ", сalculatedPerDay=" + сalculatedPerDay
				+ ", balanceStock=" + balanceStock + ", duringAssembly=" + duringAssembly + ", need=" + need
				+ ", remainderNetwork=" + remainderNetwork + ", remainderStockInPall=" + remainderStockInPall
				+ ", remainderStockInDay=" + remainderStockInDay + ", remainderNetworkInDay=" + remainderNetworkInDay
				+ ", amountMaintenance=" + amountMaintenance + ", TOWithLeftovers2Days=" + TOWithLeftovers2Days
				+ ", percent=" + percent + ", difference=" + difference + ", priceWithoutNDS=" + priceWithoutNDS
				+ ", expectedArrival=" + expectedArrival + ", reserves=" + reserves + ", reserves100And50="
				+ reserves100And50 + ", balanceStockAndReserves=" + balanceStockAndReserves + ", dateCreate="
				+ dateCreate + ", dateUnload=" + dateUnload + ", promotionDateStart=" + promotionDateStart
				+ ", promotionDateEnd=" + promotionDateEnd + ", dayMax=" + dayMax + ", isException=" + isException
				+ "]";
	}
	
	
	
	
}
