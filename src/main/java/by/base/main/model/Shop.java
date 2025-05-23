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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

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
	

	/**
	 * @param numshop
	 * @param address
	 * @param lat
	 * @param lng
	 */
	public Shop(int numshop, String address, String lat, String lng) {
		super();
		this.numshop = numshop;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
	}
	
	


	/**
	 * @param numshop
	 * @param address
	 * @param lat
	 * @param lng
	 * @param length
	 * @param width
	 * @param height
	 * @param maxPall
	 */
	public Shop(int numshop, String address, String lat, String lng, Double length, Double width, Double height,
			Integer maxPall) {
		super();
		this.numshop = numshop;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
		this.length = length;
		this.width = width;
		this.height = height;
		this.maxPall = maxPall;
	}




	@Id
	@Column(name="numshop")
	private int numshop;
	
	/**
	 * внутренний id магазина для маршрутизатора
	 */
	@Transient
	private int idShop;
	
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
	
	@Column(name = "pall_return")
	private Double pallReturn;
	
	@Column(name = "lat")
	private String lat;
	
	@Column(name = "lng")
	private String lng;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "length")
	private Double length;
	
	@Column(name = "width")
	private Double width;
	
	@Column(name = "height")
	private Double height;
	
	@Column(name = "max_pall")
	private Integer maxPall;
	
	@Column(name = "is_tail_lift")
	private Boolean isTailLift;
	
	@Column(name = "is_internal_movement")
	private Boolean isInternalMovement;
	
	/**
	 * потребность магазина по весу в кг
	 */
	@Transient
	private Integer weight;
	
	@Transient
	/**
	 * это поля отвечает за то что маршрут подходит по паллетам догруза хоть к одной машине 
	 */
	private Boolean IsFit;
	
	@Transient
	private Double needPall; // потребность магазина в паллетах

	@Transient
	private Double distanceFromStock; // расстояние от заданного склада
	
	@Transient
	private Double distanceForFilter; // расстояние для фильтраций
	
	@Transient
	private String krossPolugonName; // название кроссовой площадки
	
	@Transient
	private Boolean specialWeightDistribution; // расчитывать ли от веса магазин
	
	@OneToMany(fetch=FetchType.EAGER,
			   mappedBy="shop",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	@JsonBackReference
	private Set<RouteHasShop> roteHasShopList;
	
	@OneToOne(mappedBy="shop",
			cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
						CascadeType.REFRESH})
	@JsonBackReference
	private User director;
	
	@OneToMany(fetch=FetchType.EAGER,
			   mappedBy="shop",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	@JsonBackReference
	private Set<Feedback> feedbackList;

	public int getNumshop() {
		return numshop;
	}

	/**
	 * @return the krossPolugonName
	 */
	public String getKrossPolugonName() {
		return krossPolugonName;
	}
	/**
	 * @param krossPolugonName the krossPolugonName to set
	 */
	public void setKrossPolugonName(String krossPolugonName) {
		this.krossPolugonName = krossPolugonName;
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

	public Double getDistanceForFilter() {
		return distanceForFilter;
	}
	public void setDistanceForFilter(Double distanceForFilter) {
		this.distanceForFilter = distanceForFilter;
	}
	public LocalTime getWorkStart() {
		return workStart;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getSpecialWeightDistribution() {
		if(specialWeightDistribution!=null) {
			return specialWeightDistribution;			
		}else {
			return false;
		}
	}
	public void setSpecialWeightDistribution(Boolean specialWeightDistribution) {
		this.specialWeightDistribution = specialWeightDistribution;
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

	/**
	 * Возващает потребность магазина по весу
	 * @return
	 */
	public Integer getWeight() {
		return weight;
	}
	/**
	 * Задаёт потребность магазина по весу
	 * @param weight
	 */
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	public void setWeight(Double weight) {
		this.weight = weight.intValue();
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



	public Double getPallReturn() {
		return pallReturn;
	}
	public void setPallReturn(Double pallReturn) {
		this.pallReturn = pallReturn;
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
	
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Double getNeedPall() {
		return needPall;
	}
	public void setNeedPall(Double needPall) {
		this.needPall = needPall;
	}
	
	public Boolean getIsFit() {
		return IsFit;
	}
	public void setIsFit(Boolean isFit) {
		IsFit = isFit;
	}
	/**
	 * отдаёт расстояне от заданного склада
	 * @return
	 */
	public Double getDistanceFromStock() {
		return distanceFromStock;
	}
	
	/**
	 * задаёт расстояне от заданного склада
	 * @param distanceFrovStock
	 */
	public void setDistanceFromStock(Double distanceFrovStock) {
		this.distanceFromStock = distanceFrovStock;
	}
	
	public Double getLength() {
		return length;
	}
	public void setLength(Double length) {
		this.length = length;
	}
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	/**
	 * Возвращает ограничение по паллетам на данном магазине
	 * @return
	 */
	public Integer getMaxPall() {
		if(krossPolugonName!=null) {
			return null;			
		}else {
			return maxPall;
		}
	}
	/**
	 * Задаёт ограничение по паллетам на данном магазине
	 * @param maxPall
	 */
	public void setMaxPall(Integer maxPall) {
		this.maxPall = maxPall;
	}

	
	public int getIdShop() {
		return idShop;
	}
	public void setIdShop(int idShop) {
		this.idShop = idShop;
	}
	
	
	/**
	 * Обязательно ли наличие гидраборта для данного магазина
	 * @return
	 */
	public Boolean getIsTailLift() {
		return isTailLift;
	}
	/**
	 * Задаёт обязательно ли наличие гидраборта для данного магазина
	 * @param isTailLift
	 */
	public void setIsTailLift(Boolean isTailLift) {
		this.isTailLift = isTailLift;
	}
	/**
	 * ТО используется для внутреннего перемещения?
	 * @return
	 */
	public Boolean getIsInternalMovement() {
		return isInternalMovement;
	}
	/**
	 * задаёт ТО используется для внутреннего перемещения?
	 * @param isInternalMovement
	 */
	public void setIsInternalMovement(Boolean isInternalMovement) {
		this.isInternalMovement = isInternalMovement;
	}
	/**
	 * оздаёт id магазина для работы в оптимизаторе. На соновании номера магазина веса, и паллет
	 */
	public void createIdShop() {
		this.idShop = Objects.hash(needPall, numshop, weight);
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(needPall, numshop, weight);
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
		return Objects.equals(needPall, other.needPall) && numshop == other.numshop
				&& Objects.equals(weight, other.weight);
	}
	@Override
	public String toString() {
		return "Shop [numshop=" + numshop +  ", info=" + packageShop + ", address=" + address + ", lat=" + lat + ", lng=" + lng + ", needPall="
				+ needPall + "]";
	}
	
	
	public String toAllString() {
		return "Shop [numshop=" + numshop + ", address=" + address + ", telephone=" + telephone + ", workStart="
				+ workStart + ", workfinish=" + workfinish + ", debt=" + debt + ", commercial=" + commercial
				+ ", packageShop=" + packageShop + ", pallReturn=" + pallReturn + ", lat=" + lat + ", lng=" + lng
				+ ", type=" + type + ", length=" + length + ", width=" + width + ", height=" + height + ", maxPall="
				+ maxPall + ", needPall=" + needPall + ", weight=" + weight +"]";
	}
	
	

}
