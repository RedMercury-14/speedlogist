package by.base.main.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

public class Tender implements Serializable{

	/**
	 * @author Dima Hrushevski
	 */
	private static final long serialVersionUID = 2569126491208005767L;
	
	private int idRoute;
	private Integer numStock;
	private LocalDate dateLoadPreviously;
	private LocalTime timeLoadPreviously;
	private boolean isSanitization;
	private String temperature;
	private String totalLoadPall;
	private String totalCargoWeight;
	private String comments;
	private String routeDirection;
	private Integer startPrice;
	private Integer finishPrice;
	private LocalTime time;
	private String statusRoute;
	private String statusStock;
	private User user;
	private Truck truck;
	private Set<RouteHasShop> roteHasShop;
	private String numPoint;
	
	public Tender() {}
	
	public Tender(int idRoute, Integer numStock, LocalDate dateLoadPreviously, LocalTime timeLoadPreviously,
			boolean isSanitization, String temperature, String totalLoadPall, String totalCargoWeight, String comments,
			String routeDirection, Integer startPrice, LocalTime time, String statusRoute, String statusStock,
			User user, Truck truck, Set<RouteHasShop> roteHasShop) {
		super();
		this.idRoute = idRoute;
		this.numStock = numStock;
		this.dateLoadPreviously = dateLoadPreviously;
		this.timeLoadPreviously = timeLoadPreviously;
		this.isSanitization = isSanitization;
		this.temperature = temperature;
		this.totalLoadPall = totalLoadPall;
		this.totalCargoWeight = totalCargoWeight;
		this.comments = comments;
		this.routeDirection = routeDirection;
		this.startPrice = startPrice;
		this.time = time;
		this.statusRoute = statusRoute;
		this.statusStock = statusStock;
		this.user = user;
		this.truck = truck;
		this.roteHasShop = roteHasShop;
	}
	public int getIdRoute() {
		return idRoute;
	}
	public void setIdRoute(int idRoute) {
		this.idRoute = idRoute;
	}
	public Integer getNumStock() {
		return numStock;
	}
	public void setNumStock(Integer numStock) {
		this.numStock = numStock;
	}
	public LocalDate getDateLoadPreviously() {
		return dateLoadPreviously;
	}
	public void setDateLoadPreviously(LocalDate dateLoadPreviously) {
		this.dateLoadPreviously = dateLoadPreviously;
	}
	public LocalTime getTimeLoadPreviously() {
		return timeLoadPreviously;
	}
	public void setTimeLoadPreviously(LocalTime timeLoadPreviously) {
		this.timeLoadPreviously = timeLoadPreviously;
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
	public void setStartPrice(Integer startPrice) {
		this.startPrice = startPrice;
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
	public String getStatusStock() {
		return statusStock;
	}
	public void setStatusStock(String statusStock) {
		this.statusStock = statusStock;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Truck getTruck() {
		return truck;
	}
	public void setTruck(Truck truck) {
		this.truck = truck;
	}
	public Set<RouteHasShop> getRoteHasShop() {
		return roteHasShop;
	}
	public void setRoteHasShop(Set<RouteHasShop> roteHasShop) {
		this.roteHasShop = roteHasShop;
	}
	
	public String getNumPoint() {
		return getRoteHasShop().size()+"";
	}

	public void setNumPoint(String numPoint) {
		this.numPoint = numPoint;
	}
	

	public Integer getFinishPrice() {
		return finishPrice;
	}

	public void setFinishPrice(Integer finishPrice) {
		this.finishPrice = finishPrice;
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
		Tender other = (Tender) obj;
		return idRoute == other.idRoute;
	}
	@Override
	public String toString() {
		return "Tender [idRoute=" + idRoute + ", numStock=" + numStock + ", dateLoadPreviously=" + dateLoadPreviously
				+ ", timeLoadPreviously=" + timeLoadPreviously + ", isSanitization=" + isSanitization + ", temperature="
				+ temperature + ", totalLoadPall=" + totalLoadPall + ", totalCargoWeight=" + totalCargoWeight
				+ ", comments=" + comments + ", routeDirection=" + routeDirection + ", startPrice=" + startPrice
				+ ", time=" + time + ", statusRoute=" + statusRoute + ", statusStock=" + statusStock + ", user=" + user
				+ ", truck=" + truck + ", roteHasShop=" + roteHasShop + "]";
	}
	
	

}
