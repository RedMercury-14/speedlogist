package by.base.main.model;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	@JsonFormat(pattern = "HH-mm")
	private LocalTime timeLoadPreviously;
	
	@Column(name = "timeLoad_previouslyStock")
	@JsonFormat(pattern = "HH-mm")
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
	
	@ManyToOne(fetch = FetchType.EAGER, 
			cascade = { CascadeType.PERSIST, 
						CascadeType.MERGE, 
						CascadeType.DETACH,
						CascadeType.REFRESH })
	@JoinColumn(name = "truck_idtruck")
	//@JsonBackReference
	private Truck truck;
	
	@ManyToOne(fetch = FetchType.EAGER, 
			cascade = { CascadeType.PERSIST, 
						CascadeType.MERGE, 
						CascadeType.DETACH,
						CascadeType.REFRESH })
	@JoinColumn(name = "user_iduser_manager") // перевозчик
	//@JsonBackReference
	private User user;
	
	@OneToMany(fetch=FetchType.EAGER, orphanRemoval = true,
			   mappedBy="route",
			   cascade= {CascadeType.ALL})
	private Set<RouteHasShop> roteHasShop;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="user_iduser_driver")
	//@JsonBackReference
	private User driver;
	
	@Transient
	private Map<String, String> cost = new HashMap<String, String>();
	/**
	 * хранит в себе значения цен, где ключ - это номер касты, значение - цена для данной касты.
	 * для каждорого вызова маршрута, если нужна цена - она просчитывается. в бд записывается уже
	 * окончательная цена.
	 */
	
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
		return routeDirection;
	}

	public void setRouteDirection(String routeDirection) {
		this.routeDirection = routeDirection;
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
		return getRoteHasShop().size()+"";
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
				+ "]";
	}

	


	
	
	
}
