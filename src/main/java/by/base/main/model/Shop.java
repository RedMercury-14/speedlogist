package by.base.main.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "shop")
public class Shop implements Serializable{

	/**
	 * @author Dima Hrushevski
	 */
	private static final long serialVersionUID = -6656810088253113516L;
	
	
	
	public Shop() {}
	public Shop(int numshop, String address) {
		super();
		this.numshop = numshop;
		this.address = address;
	}

	@Id
	@Column(name="numshop")
	private int numshop;
	
	@Column(name="address")
	private String address;
			
	@Column(name = "telephone")
	private String telephone;
	
	@Column(name = "workingHours_start")
	private LocalTime workStart;
	
	@Column(name = "workingHours_finish")
	private LocalTime workfinish;
	
	@Column(name = "debt")
	private String debt;
	
	@Column(name = "commercial")
	private String commercial;
	
	@Column(name = "package")
	private String packageShop;
	
	@Column(name = "`return`", nullable = true)
	private String returnShop;
	
	@OneToMany(fetch=FetchType.EAGER,
			   mappedBy="shop",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	private Set<RouteHasShop> roteHasShopList;
	
	@OneToOne(mappedBy="shop",
			cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
						CascadeType.REFRESH})
	private User director;
	
	@OneToMany(fetch=FetchType.EAGER,
			   mappedBy="shop",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	private Set<Feedback> feedbackList;

	public int getNumshop() {
		return numshop;
	}

	public void setNumshop(int numshop) {
		this.numshop = numshop;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public LocalTime getWorkStart() {
		return workStart;
	}

	public void setWorkStart(LocalTime workStart) {
		this.workStart = workStart;
	}

	public LocalTime getWorkfinish() {
		return workfinish;
	}

	public void setWorkfinish(LocalTime workfinish) {
		this.workfinish = workfinish;
	}

	public String getDebt() {
		return debt;
	}

	public void setDebt(String debt) {
		this.debt = debt;
	}

	public String getCommercial() {
		return commercial;
	}

	public void setCommercial(String commercial) {
		this.commercial = commercial;
	}

	public String getPackageShop() {
		return packageShop;
	}

	public void setPackageShop(String packageShop) {
		this.packageShop = packageShop;
	}

	public String getReturnShop() {
		return returnShop;
	}

	public void setReturnShop(String returnShop) {
		this.returnShop = returnShop;
	}

	public Set<RouteHasShop> getRoteHasShopList() {
		return roteHasShopList;
	}

	public void setRoteHasShopList(Set<RouteHasShop> roteHasShopList) {
		this.roteHasShopList = roteHasShopList;
	}
	
	public User getDirector() {
		return director;
	}
	public void setDirector(User director) {
		this.director = director;
	}
	
	public Set<Feedback> getFeedbackList() {
		return feedbackList;
	}
	public void setFeedbackList(Set<Feedback> feedbackList) {
		this.feedbackList = feedbackList;
	}
	@Override
	public int hashCode() {
		return Objects.hash(numshop);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Shop other = (Shop) obj;
		return numshop == other.numshop;
	}
	@Override
	public String toString() {
		return "Shop [numshop=" + numshop + ", address=" + address + "]";
	}


	
	

}
