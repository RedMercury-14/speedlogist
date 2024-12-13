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
	private Integer numStock;
	
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

	public Product(Integer numStock, Double сalculatedPerDay, Double balanceStockAndReserves) {
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
	public Integer getNumStock() {
		return numStock;
	}

	/**
	 * Номер склада
	 */
	public void setNumStock(Integer numStock) {
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

	

	@Override
	public int hashCode() {
		return Objects.hash(codeProduct, numStock);
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
		return Objects.equals(codeProduct, other.codeProduct) && Objects.equals(numStock, other.numStock);
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
