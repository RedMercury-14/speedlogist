package by.base.main.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;

@DynamicUpdate
@DynamicInsert
@Entity(name = "RouteHasShop")
@Table(name = "route_has_shop")
public class RouteHasShop implements Serializable{

	/**
	 * @author Dima Hrushevski
	 */
	private static final long serialVersionUID = -7198705473814115885L;
	
	
	public RouteHasShop() {}


	public RouteHasShop(Integer order, String pall, String weight, Shop shop) {
		super();
		this.order = order;
		this.pall = pall;
		this.weight = weight;
		this.shop = shop;
	}
	public RouteHasShop(Integer order, String pall, String weight, String address) {
		this.order = order;
		this.pall = pall;
		this.weight = weight;
		this.address = address;
	}


	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="idroute_has_shop")
	private Integer idRouteHasShop;
	
	@Column(name="`order`")
	private Integer order;
	
	@Column(name="pall")
	private String pall;
	
	@Column(name="weight")
	private String weight;
	
	@Column(name="status")
	private String status;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "cargo")
	private String cargo;
	
	@Column (name = "position")
	private String position;
	
	@Column(name = "volume")
	private String volume;
	
	@Column(name = "customs_address")
	private String customsAddress;	
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
			CascadeType.REFRESH })
	@JoinColumn(name = "shop_numshop")
	@JsonBackReference
	private Shop shop;
	
//	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL})
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE})
	@JsonBackReference
	private Route route;

	public Integer getIdRouteHasShop() {
		return idRouteHasShop;
	}


	public void setIdRouteHasShop(Integer idRouteHasShop) {
		this.idRouteHasShop = idRouteHasShop;
	}


	public Integer getOrder() {
		return order;
	}


	public void setOrder(Integer order) {
		this.order = order;
	}


	public String getPall() {
		return pall;
	}

	public void setPall(String pall) {
		this.pall = pall;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
	

	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	public String getAddress() {
		return address;
	}


	public void setAddress(String adress) {
		this.address = adress;
	}
	
	
	public String getCargo() {
		return cargo;
	}


	public void setCargo(String cargo) {
		this.cargo = cargo;
	}


	public String getPosition() {
		return position;
	}


	public void setPosition(String position) {
		this.position = position;
	}


	public String getVolume() {
		return volume;
	}


	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getCustomsAddress() {
		return customsAddress;
	}

	public void setCustomsAddress(String customsAddress) {
		this.customsAddress = customsAddress;
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(order);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteHasShop other = (RouteHasShop) obj;
		return Objects.equals(order, other.order);
	}


	@Override
	public String toString() {
		return "RouteHasShop [idRouteHasShop=" + idRouteHasShop + ", order=" + order + ", pall=" + pall + ", weight="
				+ weight + ", status=" + status + ", address=" + address + ", cargo=" + cargo + ", position=" + position
				+ ", shop=" + shop + "]";
	}
	
}
