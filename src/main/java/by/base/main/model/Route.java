package by.base.main.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@DynamicUpdate
@DynamicInsert
@Entity(name = "Route")
@Table(name = "route")
public class Route implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -650717876587103650L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idroute")
	private Integer idRoute;
	
	@Column(name = "numStock")
	private Integer numStock;
	
	@Column(name = "dateLoad_previously")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateLoadPreviously;
	
	@Column(name = "timeLoad_previously")
	@JsonFormat(pattern = "HH:mm")
	private LocalTime timeLoadPreviously;
	
	@Column(name = "timeLoad_previouslyStock")
	@JsonFormat(pattern = "HH:mm")
	private LocalTime timeLoadPreviouslyStock;
	
	@Column(name = "actualTimeArrival")
	private LocalDateTime actualTimeArrival;
	
	@Column(name = "startLoad")
	private LocalDateTime startLoad;
	
	@Column(name = "finishLoad")
	private LocalDateTime finishLoad;
	
	@Column(name = "deliveryDocuments")
	private LocalDateTime deliveryDocuments;
	
	@Column(name = "sanitization")
	private boolean isSanitization;
	
	@Column(name = "temperature")
	private String temperature;
	
	@Column(name = "nameloader")
	private String nameLoader;
	
	@Column(name = "rump")
	private Integer ramp;
	
	@Column(name = "loadPall_total")
	private String totalLoadPall;
	
	@Column(name = "cargoWeight_total")
	private String totalCargoWeight;
	
	@Column(name = "`lines`")
	private String lines;
	
	@Column(name = "comments")
	private String comments;
	
	@Column(name = "routeDirection")
	private String routeDirection;
	
	@Column(name = "startPrice")
	private Integer startPrice;
	
	@Column(name = "finishPrice")
	private Integer finishPrice;
	
	@Column(name = "time")
	@JsonFormat(pattern = "HH-mm")
	private LocalTime time;
	
	@Column(name = "statusRoute")
	private String statusRoute;
	
	@Column(name = "statusStock")
	private String statusStock;
	
	@Column(name = "typeTrailer")
	private String typeTrailer;
	
	@Column(name = "startCurrency")
	private String startCurrency;
	
	@Column(name = "userComments")
	private String userComments;
	
	@Column(name = "stepCost")
	private String stepCost;
	
	@Column(name = "optimalCost")
	private String optimalCost;
	
	@Column(name = "customer")
	private String customer;
	
	@Column(name = "run")
	private String run;
	
	@Column(name = "logist_info")
	private String logistInfo;
	
	@Column(name = "tnvd")
	private String tnvd;
	
	@Column(name="way")
	private String way;
	
	@Column(name = "load_number")
	private String loadNumber;
	
	/**
	 * дата загрузки от перевозчика
	 */
	@Column(name = "dateLoad_actually")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateLoadActually;
	
	/**
	 * время загрузки от перевозчика
	 */
	@Column(name = "timeLoad_actually")
	@JsonFormat(pattern = "HH-mm")
	private LocalTime timeLoadActually;
	
	/**
	 * дата выгрузки от перевозчика
	 */
	@Column(name = "dateUnload_actually")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateUnloadActually;
	
	/**
	 * время выгрузки от перевозчика
	 */
	@Column(name = "timeUnload_actually")
	@JsonFormat(pattern = "HH-mm")
	private LocalTime timeUnloadActually;
	
	@ManyToOne(fetch = FetchType.LAZY, 
			cascade = { CascadeType.PERSIST, 
						CascadeType.MERGE, 
						CascadeType.DETACH,
						CascadeType.REFRESH })
	@JoinColumn(name = "truck_idtruck")
	//@JsonBackReference
	private Truck truck;
	
	@ManyToOne(fetch = FetchType.LAZY, 
			cascade = { CascadeType.PERSIST, 
						CascadeType.MERGE, 
						CascadeType.DETACH,
						CascadeType.REFRESH })
	@JoinColumn(name = "user_iduser_manager") // перевозчик
//	@JsonBackReference
//	@JsonManagedReference
	@JsonIgnore
	private User user;
	
	@OneToMany(fetch=FetchType.LAZY, orphanRemoval = true,
			   mappedBy="route",
			   cascade= {CascadeType.ALL})
	private Set<RouteHasShop> roteHasShop;
	
	@OneToOne(cascade=CascadeType.ALL) // не просто так не стоит fetch=FetchType.LAZY
	@JoinColumn(name="user_iduser_driver")
//	@JsonBackReference
//	@JsonIgnore 
	// СТАЛ ПОКАЗЫВАТЬ DRIVER!!!!!!!!!!!!!!
	private User driver;
	
	@ManyToMany(fetch = FetchType.LAZY, 
			cascade = { CascadeType.ALL })
	@JoinTable(name = "route_has_order", joinColumns = @JoinColumn(name = "route_idroute"), inverseJoinColumns = @JoinColumn(name = "order_idorder"))
	@JsonBackReference //возможно зря ТУТ ФАТАЛКА
	private Set<Order> orders;
	
	@Column(name="timeUnload_previouslyStock")
	@JsonFormat(pattern = "HH-mm")
	private Time timeUnloadPreviouslyStock;
	
	@Column(name="dateUnload_previouslyStock")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dateUnloadPreviouslyStock;
	
	@Column(name="create_date")
	private Date createDate;
	
	@Column(name="create_time")
	private Time createTime;
	
	/**
	 * окно на выгрузку от Карины
	 */
	@Column(name = "onload_window_date")
	private Date onloadWindowDate;
	
	/**
	 * окно на выгрузку от Карины
	 */
	@Column(name = "onload_window_time")
	private Time onloadWindowTime;
	
	/**
	 * непосредственно сколько времени занимает сама выгрузка
	 */
	@Column(name = "onload_time")
	private Time onloadTime;
	
	@Transient
	private Map<String, String> cost = new HashMap<String, String>();
	/**
	 * хранит в себе значения цен, где ключ - это номер касты, значение - цена для данной касты.
	 * для каждорого вызова маршрута, если нужна цена - она просчитывается. в бд записывается уже
	 * окончательная цена.
	 */
	@Transient
	private Double nds;
	@Transient
	private String numWayList;
	@Transient
	private String numTruckAndTrailer;
	@Transient
	private String cmr;
	@Transient
	private String costWay;
	@Transient
	private String dateUnload;
	@Transient
	private String cargoWeightForAct;
	
	
	/**
	 * форматер для простого отображения дат во view
	 */
	@Transient
	private DateTimeFormatter simpleFormatterDate = DateTimeFormatter.ofPattern("dd.MM.yyy");
	@Transient
	private DateTimeFormatter mainFormatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	@Transient
	private DateTimeFormatter mainFormatterTime = DateTimeFormatter.ofPattern("HH-mm");
	
	
	
	/**
	 * 
	 */
	public Route() {
		Date dateNow = Date.valueOf(LocalDate.now());
		Time timeNow = Time.valueOf(LocalTime.now());
		if(createDate == null) {
			this.createDate = dateNow;
		}
		if(createTime == null) {
			this.createTime = timeNow;
		}
	}

	public Integer getIdRoute() {
		return idRoute;
	}

	public void setIdRoute(Integer idRoute) {
		this.idRoute = idRoute;
	}

	public Integer getNumStock() {
		return numStock;
	}

	public void setNumStock(int numStock) {
		this.numStock = numStock;
	}

	public LocalDate getDateLoadPreviously() {
		return dateLoadPreviously;
	}

	public Date getOnloadWindowDate() {
		return onloadWindowDate;
	}

	public void setOnloadWindowDate(Date onloadWindowDate) {
		this.onloadWindowDate = onloadWindowDate;
	}

	public Time getOnloadWindowTime() {
		return onloadWindowTime;
	}

	public String getLoadNumber() {
		return loadNumber;
	}

	public void setLoadNumber(String loadNumber) {
		this.loadNumber = loadNumber;
	}

	public void setOnloadWindowTime(Time onloadWindowTime) {
		this.onloadWindowTime = onloadWindowTime;
	}

	public void setDateLoadPreviously(LocalDate dateLoadPreviously) {		
		this.dateLoadPreviously = dateLoadPreviously;
	}
	public void setDateLoadPreviously(Date dateLoadPreviously) {		
		this.dateLoadPreviously = dateLoadPreviously.toLocalDate();
	}
	public void setDateLoadPreviously(String dateLoadPreviously) {		
		this.dateLoadPreviously = LocalDate.parse(dateLoadPreviously);
	}

	public LocalTime getTimeLoadPreviously() {
		return timeLoadPreviously;
	}

	public void setTimeLoadPreviously(LocalTime timeLoadPreviously) {
		this.timeLoadPreviously = timeLoadPreviously;
	}

	public LocalTime getTimeLoadPreviouslyStock() {
		return timeLoadPreviouslyStock;
	}

	/**
	 * непосредственно сколько времени занимает сама выгрузка
	 * @return
	 */
	public Time getOnloadTime() {
		return onloadTime;
	}

	/**
	 * непосредственно сколько времени занимает сама выгрузка
	 * @param onloadTime
	 */
	public void setOnloadTime(Time onloadTime) {
		this.onloadTime = onloadTime;
	}

	public void setTimeLoadPreviouslyStock(LocalTime timeLoadPreviouslyStock) {
		this.timeLoadPreviouslyStock = timeLoadPreviouslyStock;
	}

	public LocalDateTime getActualTimeArrival() {
		return actualTimeArrival;
	}

	public void setActualTimeArrival(LocalDateTime actualTimeArrival) {
		this.actualTimeArrival = actualTimeArrival;
	}

	public LocalDateTime getStartLoad() {
		return startLoad;
	}

	public void setStartLoad(LocalDateTime startLoad) {
		this.startLoad = startLoad;
	}

	public LocalDateTime getFinishLoad() {
		return finishLoad;
	}

	public void setFinishLoad(LocalDateTime finishLoad) {
		this.finishLoad = finishLoad;
	}

	public LocalDateTime getDeliveryDocuments() {
		return deliveryDocuments;
	}

	public void setDeliveryDocuments(LocalDateTime deliveryDocuments) {
		this.deliveryDocuments = deliveryDocuments;
	}

	public boolean isSanitization() {
		return isSanitization;
	}
	
	public boolean getIsSanitization() {
		return isSanitization;
	}

	public void setSanitization(boolean isSanitization) {
		this.isSanitization = isSanitization;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getNameLoader() {
		return nameLoader;
	}

	public void setNameLoader(String nameLoader) {
		this.nameLoader = nameLoader;
	}

	public String getTnvd() {
		return tnvd;
	}

	public void setTnvd(String tnvd) {
		this.tnvd = tnvd;
	}

	public Integer getRamp() {
		return ramp;
	}

	public void setRamp(int ramp) {
		this.ramp = ramp;
	}

	public String getTotalLoadPall() {
		return totalLoadPall;
	}

	public void setTotalLoadPall(String totalLoadPall) {
		this.totalLoadPall = totalLoadPall;
	}

	public String getTotalCargoWeight() {
		return totalCargoWeight;
	}

	public void setTotalCargoWeight(String totalCargoWeight) {
		this.totalCargoWeight = totalCargoWeight;
	}

	public String getLines() {
		return lines;
	}

	public void setLines(String lines) {
		this.lines = lines;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getRouteDirection() {
		return routeDirection.trim().replaceAll(" +", " ");
	}

	public void setRouteDirection(String routeDirection) {
		this.routeDirection = routeDirection.trim().replaceAll(" +", " ");
	}

	public Integer getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(int startPrice) {
		this.startPrice = startPrice;
	}

	public Integer getFinishPrice() {
		return finishPrice;
	}

	public void setFinishPrice(int finishPrice) {
		this.finishPrice = finishPrice;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public String getStatusRoute() {
		return statusRoute;
	}

	public void setStatusRoute(String statusRoute) {
		this.statusRoute = statusRoute;
	}

	public Truck getTruck() {
		return truck;
	}

	public void setTruck(Truck truck) {
		this.truck = truck;
	}

	public User getUser() {
		return user;
	}
	
	public String getNumPoint() {
		if(getRoteHasShop() !=null) {
			return getRoteHasShop().size()+"";
		}else {
			return null;
		}
		
	}

	public void setUser(User userManager) {
		this.user = userManager;
	}

	public Set<RouteHasShop> getRoteHasShop() {
		return roteHasShop;
	}

	public void setRoteHasShop(Set<RouteHasShop> roteHasShopList) {
		this.roteHasShop = roteHasShopList;
	}
	
	

	public String getStatusStock() {
		return statusStock;
	}

	public void setStatusStock(String statusStock) {
		this.statusStock = statusStock;
	}

	public void setNumStock(Integer numStock) {
		this.numStock = numStock;
	}

	public void setRamp(Integer ramp) {
		this.ramp = ramp;
	}

	public void setStartPrice(Integer startPrice) {
		this.startPrice = startPrice;
	}

	public void setFinishPrice(Integer finishPrice) {
		this.finishPrice = finishPrice;
	}
	

	public User getDriver() {
		return driver;
	}

	public void setDriver(User driver) {
		this.driver = driver;
	}
	
	public Map<String, String> getCost() {
		return cost;
	}

	public void setCost(Map<String, String> cost) {
		this.cost = cost;
	}

	public String getTypeTrailer() {
		return typeTrailer;
	}

	public void setTypeTrailer(String typeTrailer) {
		this.typeTrailer = typeTrailer;
	}
	
	public String getStartCurrency() {
		return startCurrency;
	}

	public void setStartCurrency(String startCurrency) {
		this.startCurrency = startCurrency;
	}
	
	public String getUserComments() {
		return userComments;
	}

	public void setUserComments(String userComments) {
		this.userComments = userComments;
	}

	public String getStepCost() {
		return stepCost;
	}

	public void setStepCost(String stepCost) {
		this.stepCost = stepCost;
	}

	public String getOptimalCost() {
		return optimalCost;
	}

	public void setOptimalCost(String optimalCost) {
		this.optimalCost = optimalCost;
	}

	public String getSimpleDateStart() { // отдаёт стринг даты в удобном формате
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return dateLoadPreviously.format(formatter);
	}
	public void setSimpleDateStart(String simpleDateStart) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.dateLoadPreviously = LocalDate.parse(simpleDateStart, formatter);
	}
	
	public Double getNds() {
		if (finishPrice != null) {
			nds = (double) (finishPrice*20.0/100.0);
			return nds;
		}else {
			return null;
		}		
	}	

	public String getNumWayList() {
		return numWayList;
	}

	public void setNumWayList(String numWayList) {
		this.numWayList = numWayList;
	}

	public String getNumTruckAndTrailer() {
		return numTruckAndTrailer;
	}

	public void setNumTruckAndTrailer(String numTruckAndTrailer) {
		this.numTruckAndTrailer = numTruckAndTrailer;
	}

	public String getCmr() {
		return cmr;
	}

	public void setCmr(String cmr) {
		this.cmr = cmr;
	}	

	public String getCostWay() {
		return costWay;
	}

	public void setCostWay(String costWay) {
		this.costWay = costWay;
	}	

	public String getDateUnload() {
		return dateUnload;
	}

	public void setDateUnload(String dateUnload) {
		this.dateUnload = dateUnload;
	}	

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	/**
	 * дата загрузки от перевозчика
	 */
	public LocalDate getDateLoadActually() {
		return dateLoadActually;
	}
	
	/**
	 * дата загрузки от перевозчика STRING
	 */
	public String getDateLoadActuallySimple() {
		if(dateLoadActually != null) {
			return dateLoadActually.format(simpleFormatterDate);
		}else {
			return null;
		}
		
	}

	/**
	 * дата загрузки от перевозчика
	 */
	public void setDateLoadActually(LocalDate dateLoadActually) {
		this.dateLoadActually = dateLoadActually;
	}
	
	public void setDateLoadActually(Date dateLoadActually) {
		this.dateLoadActually = dateLoadActually.toLocalDate();
	}
	
	public void setDateLoadActually(String dateLoadActually) {
		this.dateLoadActually = LocalDate.parse(dateLoadActually);
	}
	
	public String getSimpleDateActually() { // отдаёт стринг даты в удобном формате
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		if (dateLoadActually == null) {
			return null;
		}else {
			return dateLoadActually.format(formatter);
		}
		
	}
	public void setSimpleDateActually(String simpleDateActually) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.dateLoadActually = LocalDate.parse(simpleDateActually, formatter);
	}
	
	public String getWay() {
		if (way == null) {
			return null;
		}else {	
			return way;
		}
	}

	public void setWay(String way) {
		this.way = way;
	}
	
	public String getSimpleWay() {
		if (way == null) {
			return null;
		}else if(way.length()<3){	
			return way;
		}else {
			return way.substring(0, 3);
		}
	}
	
	public SimpleRoute getSimpleRoute() {
		return new SimpleRoute(idRoute, dateLoadPreviously, routeDirection, finishPrice, startCurrency);
	}

	/**
	 * время загрузки от перевозчика
	 */
	public LocalTime getTimeLoadActually() {
		return timeLoadActually;
	}

	/**
	 * время загрузки от перевозчика
	 */
	public void setTimeLoadActually(LocalTime timeLoadActually) {
		this.timeLoadActually = timeLoadActually;
	}

	/**
	 * дата выгрузки от перевозчика
	 */
	public LocalDate getDateUnloadActually() {
		return dateUnloadActually;
	}
	
	/**
	 * дата выгрузки от перевозчика STRING
	 */
	public String getDateUnloadActuallySimple() {
		if(dateUnloadActually != null) {
			return dateUnloadActually.format(simpleFormatterDate);
		}else {
			return null;
		}		
	}

	/**
	 * дата выгрузки от перевозчика
	 */
	public void setDateUnloadActually(LocalDate dateUnloadActually) {
		this.dateUnloadActually = dateUnloadActually;
	}

	/**
	 * время выгрузки от перевозчика
	 */
	public LocalTime getTimeUnloadActually() {
		return timeUnloadActually;
	}

	/**
	 * вреимя выгрузки от перевозчика
	 */
	public void setTimeUnloadActually(LocalTime timeUnloadActually) {
		this.timeUnloadActually = timeUnloadActually;
	}

	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	public Time getTimeUnloadPreviouslyStock() {
		return timeUnloadPreviouslyStock;
	}
	
	public String getCargoWeightForAct() {
		return cargoWeightForAct;
	}

	public void setCargoWeightForAct(String cargoWeightForAct) {
		this.cargoWeightForAct = cargoWeightForAct;
	}

	public void setTimeUnloadPreviouslyStock(String timeUnloadPreviouslyStock) {
		if(timeUnloadPreviouslyStock == null || timeUnloadPreviouslyStock.isEmpty()) {
			this.timeUnloadPreviouslyStock = null;
		}else {
			if(timeUnloadPreviouslyStock.split(":").length<3) {
				this.timeUnloadPreviouslyStock = Time.valueOf(timeUnloadPreviouslyStock+":00");
			}else {
				this.timeUnloadPreviouslyStock = Time.valueOf(timeUnloadPreviouslyStock);
			}
			
		}
		
	}

	public Date getDateUnloadPreviouslyStock() {
		return dateUnloadPreviouslyStock;
	}
	

//	public void setDateUnloadPreviouslyStock(Date dateUnloadPreviouslyStock) {
//		this.dateUnloadPreviouslyStock = dateUnloadPreviouslyStock;
//	}


	public void setDateUnloadPreviouslyStock(String dateUnloadPreviouslyStock) {
		if(dateUnloadPreviouslyStock == null || dateUnloadPreviouslyStock.isEmpty()) {
			this.dateUnloadPreviouslyStock = null;
		}else {
			this.dateUnloadPreviouslyStock = Date.valueOf(dateUnloadPreviouslyStock);
		}
		
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Time getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Time createTime) {
		this.createTime = createTime;
	}

	public String getLogistInfo() {
		return logistInfo;
	}

	public void setLogistInfo(String logistInfo) {
		this.logistInfo = logistInfo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idRoute);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		return idRoute == other.idRoute;
	}

	@Override
	public String toString() {
		return "Route [idRoute=" + idRoute + ", dateLoadPreviously=" + dateLoadPreviously + ", timeLoadPreviously="
				+ timeLoadPreviously + ", timeLoadPreviouslyStock=" + timeLoadPreviouslyStock + ", actualTimeArrival="
				+ actualTimeArrival + ", temperature=" + temperature + ", totalLoadPall=" + totalLoadPall
				+ ", totalCargoWeight=" + totalCargoWeight + ", comments=" + comments + ", routeDirection="
				+ routeDirection + ", startPrice=" + startPrice + ", finishPrice=" + finishPrice + ", time=" + time
				+ ", statusRoute=" + statusRoute + ", statusStock=" + statusStock + ", typeTrailer=" + typeTrailer
				+ "User="+user+"]";
	}

	


	
	
	
}
