package by.base.main.util.hcolossus.pojo;

import java.util.Objects;

import by.base.main.model.Shop;

/**
 * Объект, описывающий магазин, который приносит перепробег
 */
public class ProblemShop {

	private Integer idVehicleWay;
	private Shop shop;
	
	/**
	 * перепробег с учётом того, если будет присутствовать в маршурте этот проблемый магазин<br>
	 * <b>именно это значение должно использоваться в принятии решения<b>
	 */
	private Double overrun;

	public Integer getIdVehicleWay() {
		return idVehicleWay;
	}

	public void setIdVehicleWay(Integer idVehicleWay) {
		this.idVehicleWay = idVehicleWay;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Double getOverrun() {
		return overrun;
	}

	public void setOverrun(Double overrun) {
		this.overrun = overrun;
	}

	/**
	 * @param idVehicleWay
	 * @param shop
	 * @param overrun
	 */
	public ProblemShop(Integer idVehicleWay, Shop shop, Double overrun) {
		super();
		this.idVehicleWay = idVehicleWay;
		this.shop = shop;
		this.overrun = overrun;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idVehicleWay, overrun, shop);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProblemShop other = (ProblemShop) obj;
		return Objects.equals(idVehicleWay, other.idVehicleWay) && Objects.equals(overrun, other.overrun)
				&& Objects.equals(shop, other.shop);
	}

	@Override
	public String toString() {
		return "ProblemShop [idVehicleWay=" + idVehicleWay + ", shop=" + shop + ", overrun=" + overrun + "]";
	} 
	
	
}
