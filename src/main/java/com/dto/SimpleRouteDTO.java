package com.dto;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Упрощенный класс Route для отопражения графиков. Используется в REST контроллере
 * @author Dima HRushevski
 *
 */
public class SimpleRouteDTO {
	
	private Integer idRoute;
	private LocalDate dateLoadPreviously;
	private String routeDirection;
	private Integer finishPrice;
	private String startCurrency;
	
	
	
	public SimpleRouteDTO(Integer idRoute, LocalDate dateLoadPreviously, String routeDirection, Integer finishPrice,
			String startCurrency) {
		super();
		this.idRoute = idRoute;
		this.dateLoadPreviously = dateLoadPreviously;
		this.routeDirection = routeDirection;
		this.finishPrice = finishPrice;
		this.startCurrency = startCurrency;
	}
	public Integer getIdRoute() {
		return idRoute;
	}
	public void setIdRoute(Integer idRoute) {
		this.idRoute = idRoute;
	}
	public String getDateLoadPreviously() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return dateLoadPreviously.format(formatter);
	}
	public void setDateLoadPreviously(LocalDate dateLoadPreviously) {
		this.dateLoadPreviously = dateLoadPreviously;
	}
	public String getRouteDirection() {
		return routeDirection;
	}
	public void setRouteDirection(String routeDirection) {
		this.routeDirection = routeDirection;
	}
	public Integer getFinishPrice() {
		return finishPrice;
	}
	public void setFinishPrice(Integer finishPrice) {
		this.finishPrice = finishPrice;
	}
	public String getStartCurrency() {
		return startCurrency;
	}
	public void setStartCurrency(String startCurrency) {
		this.startCurrency = startCurrency;
	}
	@Override
	public String toString() {
		return "SimpleRoute [idRoute=" + idRoute + ", dateLoadPreviously=" + dateLoadPreviously + ", routeDirection="
				+ routeDirection + ", finishPrice=" + finishPrice + ", startCurrency=" + startCurrency + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(dateLoadPreviously, finishPrice, idRoute, routeDirection, startCurrency);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleRouteDTO other = (SimpleRouteDTO) obj;
		return Objects.equals(dateLoadPreviously, other.dateLoadPreviously)
				&& Objects.equals(finishPrice, other.finishPrice) && Objects.equals(idRoute, other.idRoute)
				&& Objects.equals(routeDirection, other.routeDirection)
				&& Objects.equals(startCurrency, other.startCurrency);
	}
	
	

}
