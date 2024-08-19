package by.base.main.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
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
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity(name = "Order")
@Table(name = "`order`")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idorder")
	private Integer idOrder;
	
	@Column(name = "counterparty")
	private String counterparty;
	
	@Column(name = "contact")
	private String contact;
	
	@Column(name = "cargo")
	private String cargo;
	
	@Column(name = "type_load")
	private String typeLoad;
	
	@Column(name = "method_load")
	private String methodLoad;
	
	@Column(name = "type_truck")
	private String typeTruck;
	
	@Column(name = "temperature")
	private String temperature;
	
	@Column(name = "`control`")
	private Boolean control;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "`status`")
	private Integer status;
	
	@Column(name = "date_create")
//	@JsonFormat(pattern = "yyyy-MM-dd", timezone="Europe/Moskow")
	private Date dateCreate;
	
	@Column(name = "date_delivery")
//	@JsonFormat(pattern = "yyyy-MM-dd", timezone="Europe/Moskow")
	private Date dateDelivery;
	
	@Column(name = "manager")
	private String manager;
	
	@Column(name = "telephone_manager")
	private String telephoneManager;
	
	@Column(name = "stacking")
	private Boolean stacking;
	
	@Column(name = "logist")
	private String logist;
	
	@Column(name = "logist_telephone")
	private String logistTelephone;
	
	@Column(name = "market_number")
	private String marketNumber;
	
	@Column(name = "onload_window_date")
	private Date onloadWindowDate;
	
	@Column(name = "onload_window_time")
	private Time onloadWindowTime;
	
	@Column(name = "load_number")
	private String loadNumber;
	
	@Column(name = "num_stock_delivery")
	private String numStockDelivery;
	
	@Column(name = "pall")
	private String pall;
	
	@OneToMany(fetch=FetchType.LAZY, orphanRemoval = true,
			   mappedBy="order",
			   cascade= {CascadeType.ALL})
	private Set<Address> addresses;
	
	@OneToMany(fetch=FetchType.LAZY, orphanRemoval = true,
			   mappedBy="order",
			   cascade= {CascadeType.ALL})
	@JsonIgnore
	private Set<OrderLine> orderLines;
	
	@ManyToMany(fetch = FetchType.LAZY, 
			cascade = { CascadeType.ALL })
	@JoinTable(name = "route_has_order", joinColumns = @JoinColumn(name = "order_idorder"), inverseJoinColumns = @JoinColumn(name = "route_idroute"))
//	@JsonBackReference // test!
	private Set<Route> routes; 
	
	
	/**
	 * cascade = { CascadeType.PERSIST, 
						CascadeType.MERGE, 
						CascadeType.DETACH,
						CascadeType.REFRESH }
	 */
	
	@Column(name="way")
	private String way;
	
	@Column(name="onload_time")
	private Time onloadTime;
	
	@Column(name="incoterms")
	private String incoterms;
	
	@Column(name="change_status")
	private String changeStatus;
	
	@Column(name="needUnloadPoint")
	private String needUnloadPoint;
	
	@Column(name="idRamp")
	private Integer idRamp;
	
	@Column(name="time_delivery")
	private Timestamp timeDelivery;
	
	@Column(name="time_unload")
	private Time timeUnload;
	
	@Column(name="login_manager")
	private String loginManager;
	
	@Column(name="sku")
	private Integer sku;
	
	@Column(name="mono_pall")
	private Integer monoPall;
	
	@Column(name="mix_pall")
	private Integer mixPall;
	
	@Column(name="is_internal_movement")
	private String isInternalMovement;
	
	@Column(name="mail_info")
	private String mailInfo;
	
	@Column(name="slot_info")
	private String slotInfo;
	
	@Column(name = "date_create_market")
	private Date dateCreateMarket;
	
	@Column(name = "market_info")
	private String marketInfo;
	
	@Column(name = "market_contract_type")
	private String marketContractType;
	
	@Column(name = "market_contract_group_id")
	private String marketContractGroupId;
	
	@Column(name = "market_contract_number")
	private String marketContractNumber;
	
	@Column(name = "market_contractor_id")
	private String marketContractorId;
	
	@Column(name = "num_product")
	private String numProduct;
	
	@Column(name = "status_yard")
	private Integer statusYard;
	
	@Column(name = "unload_start_yard")
	private Timestamp unloadStartYard;
	
	@Column(name = "unload_finish_yard")
	private Timestamp unloadFinishYard;
	
	@Column(name = "pall_fact_yard")
	private Integer pallFactYard;
	
	@Column(name = "weight_fact_yard")
	private Double weightFactYard;
	
	@Column(name = "market_order_sum_first")
	private Double marketOrderSumFirst;
	
	@Column(name = "market_order_sum_final")
	private Double marketOrderSumFinal;
	
	@Column(name = "arrival_fact_yard")
	private Timestamp arrivalFactYard;
	
	@Column(name = "registration_fact_yard")
	private Timestamp registrationFactYard;
	
	@Transient
	private List<Address> addressesSort;
	
	@Transient
	private List<Address> addressesToView;
	
	@Transient
	@JsonIgnore
	private String message;
	
	
	public Order() {
		super();
	}
	/**
	 * @param counterparty
	 * @param contact
	 * @param cargo
	 * @param typeLoad
	 * @param methodLoad
	 * @param typeTruck
	 * @param temperature
	 * @param control
	 * @param comment
	 * @param status
	 * @param dateCreate
	 * @param dateDelivery
	 */
	public Order(String counterparty, String contact, String cargo, String typeLoad, String methodLoad,
			String typeTruck, String temperature, Boolean control, String comment, Integer status, Date dateCreate,
			Date dateDelivery) {
		super();
		this.counterparty = counterparty;
		this.contact = contact;
		this.cargo = cargo;
		this.typeLoad = typeLoad;
		this.methodLoad = methodLoad;
		this.typeTruck = typeTruck;
		this.temperature = temperature;
		this.control = control;
		this.comment = comment;
		this.status = status;
		this.dateCreate = dateCreate;
		this.dateDelivery = dateDelivery;
	}

	
	
	public Set<OrderLine> getOrderLines() {
		return orderLines;
	}
	public void setOrderLines(Set<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}
	public Double getMarketOrderSumFirst() {
		return marketOrderSumFirst;
	}
	public void setMarketOrderSumFirst(Double marketOrderSumFirst) {
		this.marketOrderSumFirst = marketOrderSumFirst;
	}
	public Double getMarketOrderSumFinal() {
		return marketOrderSumFinal;
	}
	public void setMarketOrderSumFinal(Double marketOrderSumFinal) {
		this.marketOrderSumFinal = marketOrderSumFinal;
	}
	public String getMarketContractType() {
		return marketContractType;
	}
	public void setMarketContractType(String marketContractType) {
		this.marketContractType = marketContractType;
	}
	public String getMarketContractGroupId() {
		return marketContractGroupId;
	}
	public void setMarketContractGroupId(String marketContractGroupId) {
		this.marketContractGroupId = marketContractGroupId;
	}
	public String getMarketContractNumber() {
		return marketContractNumber;
	}
	public void setMarketContractNumber(String marketContractNumber) {
		this.marketContractNumber = marketContractNumber;
	}
	public String getMarketContractorId() {
		return marketContractorId;
	}
	public void setMarketContractorId(String marketContractorId) {
		this.marketContractorId = marketContractorId;
	}
	public Integer getIdOrder() {
		return idOrder;
	}
	
	public void setIdOrder(Integer idOrder) {
		this.idOrder = idOrder;
	}

	public String getCounterparty() {
		return counterparty;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setCounterparty(String counterparty) {
		this.counterparty = counterparty;
	}

	public String getContact() {
		return contact;
	}

	public Integer getStatusYard() {
		return statusYard;
	}
	public void setStatusYard(Integer statusYard) {
		this.statusYard = statusYard;
	}
	public Timestamp getUnloadStartYard() {
		return unloadStartYard;
	}
	public void setUnloadStartYard(Timestamp unloadStartYard) {
		this.unloadStartYard = unloadStartYard;
	}
	public Timestamp getUnloadFinishYard() {
		return unloadFinishYard;
	}
	public void setUnloadFinishYard(Timestamp unloadFinishYard) {
		this.unloadFinishYard = unloadFinishYard;
	}
	public Integer getPallFactYard() {
		return pallFactYard;
	}
	public void setPallFactYard(Integer pallFactYard) {
		this.pallFactYard = pallFactYard;
	}
	public Double getWeightFactYard() {
		return weightFactYard;
	}
	public void setWeightFactYard(Double weightFactYard) {
		this.weightFactYard = weightFactYard;
	}
	public Date getDateCreateMarket() {
		return dateCreateMarket;
	}
	public void setDateCreateMarket(Date dateCreateMarket) {
		this.dateCreateMarket = dateCreateMarket;
	}
	public String getSlotInfo() {
		return slotInfo;
	}
	public void setSlotInfo(String slotInfo) {
		this.slotInfo = slotInfo;
	}
	public Time getOnloadTime() {
		return onloadTime;
	}
	public void setOnloadTime(Time onloadTime) {
		this.onloadTime = onloadTime;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getTypeLoad() {
		return typeLoad;
	}

	public String getLoadNumber() {
		return loadNumber;
	}
	public void setLoadNumber(String loadNumber) {
		this.loadNumber = loadNumber;
	}
	public String getIncoterms() {
		return incoterms;
	}
	public void setIncoterms(String incoterms) {
		this.incoterms = incoterms;
	}
	public void setTypeLoad(String typeLoad) {
		this.typeLoad = typeLoad;
	}

	public String getMarketInfo() {
		return marketInfo;
	}
	public void setMarketInfo(String marketInfo) {
		this.marketInfo = marketInfo;
	}
	public String getPall() {
		return pall;
	}
	public void setPall(String pall) {
		this.pall = pall;
	}
	public String getNumStockDelivery() {
		return numStockDelivery;
	}
	public void setNumStockDelivery(String numStockDelivery) {
		this.numStockDelivery = numStockDelivery;
	}
	public String getMethodLoad() {
		return methodLoad;
	}

	public void setMethodLoad(String methodLoad) {
		this.methodLoad = methodLoad;
	}

	public String getTypeTruck() {
		return typeTruck;
	}

	public void setTypeTruck(String typeTruck) {
		this.typeTruck = typeTruck;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public Boolean getControl() {
		return control;
	}

	public void setControl(Boolean control) {
		this.control = control;
	}

	public String getChangeStatus() {
		return changeStatus;
	}
	public void setChangeStatus(String changeStatus) {
		this.changeStatus = changeStatus;
	}
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getStatus() {
		return status;
	}

	/**
	 * @return the numProduct
	 */
	public String getNumProduct() {
		return numProduct;
	}
	/**
	 * @param numProduct the numProduct to set
	 */
	public void setNumProduct(String numProduct) {
		this.numProduct = numProduct;
	}
	public String getMarketNumber() {
		return marketNumber;
	}
	public void setMarketNumber(String marketNumber) {
		this.marketNumber = marketNumber;
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
	public void setOnloadWindowTime(Time onloadWindowTime) {
		this.onloadWindowTime = onloadWindowTime;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	public Date getDateDelivery() {
		return dateDelivery;
	}

	public void setDateDelivery(Date dateDelivery) {
		this.dateDelivery = dateDelivery;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}


	public String getNeedUnloadPoint() {
		return needUnloadPoint;
	}
	public void setNeedUnloadPoint(String needUnloadPoint) {
		this.needUnloadPoint = needUnloadPoint;
	}
	public Set<Route> getRoutes() {
		return routes;
	}
	public void setRoutes(Set<Route> routes) {
		this.routes = routes;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	
	public Integer getSku() {
		return sku;
	}
	public void setSku(Integer sku) {
		this.sku = sku;
	}
	public Integer getMonoPall() {
		return monoPall;
	}
	public void setMonoPall(Integer monoPall) {
		this.monoPall = monoPall;
	}
	public Integer getMixPall() {
		return mixPall;
	}
	public void setMixPall(Integer mixPall) {
		this.mixPall = mixPall;
	}
	public Integer getIdRamp() {
		return idRamp;
	}
	public void setIdRamp(Integer idRamp) {
		this.idRamp = idRamp;
	}
	
	/**
	 * дата и Время начала выгрузки
	 * @return
	 */
	public Timestamp getTimeDelivery() {
		return timeDelivery;
	}
	
	/**
	 * дата и Время начала выгрузки
	 * @return
	 */
	public void setTimeDelivery(Timestamp timeDelivery) {
		this.timeDelivery = timeDelivery;
	}
	
	/**
	 * Продолжительность выгрузки
	 * @return
	 */
	public Time getTimeUnload() {
		return timeUnload;
	}
	
	/**
	 * Продолжительность выгрузки
	 * @return
	 */
	public void setTimeUnload(Time timeUnload) {
		this.timeUnload = timeUnload;
	}
	public String getTelephoneManager() {
		return telephoneManager;
	}
	public void setTelephoneManager(String telephoneManager) {
		this.telephoneManager = telephoneManager;
	}
	
	public List<Address> getAddressesToView() {
		return addressesToView;
	}
	public void setAddressesToView(List<Address> addressesToView) {
		this.addressesToView = addressesToView;
	}
	
	public List<Address> getAddressesSort() {
		return addressesSort;
	}
	public void setAddressesSort(List<Address> addressesSort) {
		this.addressesSort = addressesSort;
	}
	
	public String getWay() {
		return way;
	}
	public void setWay(String way) {
		this.way = way;
	}
	
	public Boolean getStacking() {
		return stacking;
	}
	public void setStacking(Boolean stacking) {
		this.stacking = stacking;
	}
	
	public String getLogist() {
		return logist;
	}
	public void setLogist(String logist) {
		this.logist = logist;
	}
	public String getLogistTelephone() {
		return logistTelephone;
	}
	public void setLogistTelephone(String logistTelephone) {
		this.logistTelephone = logistTelephone;
	}
	
	/**
	 * Логин менеджера, который имеет доступ к перетягиванию слота.
	 * возможно несколько менеджеров, через <b>;<b>
	 * @return
	 */
	public String getLoginManager() {
		return loginManager;
	}
	
	/**
	 * Логин менеджера, который имеет доступ к перетягиванию слота.
	 * возможно несколько менеджеров, через <b>;<b>
	 * @return
	 */
	public void setLoginManager(String loginManager) {
		this.loginManager = loginManager;
	}

	public String getIsInternalMovement() {
		return isInternalMovement;
	}
	public void setIsInternalMovement(String isInternalMovement) {
		this.isInternalMovement = isInternalMovement;
	}
	public String getMailInfo() {
		return mailInfo;
	}
	public void setMailInfo(String mailInfo) {
		this.mailInfo = mailInfo;
	}
	
	
	public Timestamp getArrivalFactYard() {
		return arrivalFactYard;
	}
	public void setArrivalFactYard(Timestamp arrivalFactYard) {
		this.arrivalFactYard = arrivalFactYard;
	}
	public Timestamp getRegistrationFactYard() {
		return registrationFactYard;
	}
	public void setRegistrationFactYard(Timestamp registrationFactYard) {
		this.registrationFactYard = registrationFactYard;
	}
	@Override
	public int hashCode() {
		return Objects.hash(idOrder);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(idOrder, other.idOrder);
	}
	@Override
	public String toString() {
		return "Order [idOrder=" + idOrder + ", counterparty=" + counterparty + ", contact=" + contact + ", cargo="
				+ cargo + ", typeLoad=" + typeLoad + ", methodLoad=" + methodLoad + ", typeTruck=" + typeTruck
				+ ", temperature=" + temperature + ", control=" + control + ", comment=" + comment + ", status="
				+ status + ", dateCreate=" + dateCreate + ", dateDelivery=" + dateDelivery +", timeDelivery="+ timeDelivery +", timeUnload="+ timeUnload+ ", sku=" +sku +", mixPall="+mixPall+", monoPall=" + monoPall +", MARKET="+marketNumber+"]";
	}

	public String toJsonForDelete() {
		return "{\"idOrder\":\"" + idOrder + "\", \"status\":\"" + status + "\", \"marketNumber\":\"" + marketNumber
				+ "\", \"numStockDelivery\":\"" + numStockDelivery + "\", \"pall\":\"" + pall
				+ "\", \"timeDelivery\":\"" + timeDelivery + "\", \"loginManager\":\"" + loginManager + "\"}";
	}
	
	public String toJsonForYard() {
		return "{\"idOrder\":\"" + idOrder + "\", \"control\":\"" + control
				+ "\", \"status\":\"" + status + "\", \"manager\":\"" + manager + "\", \"logist\":\"" + logist
				+ "\", \"marketNumber\":\"" + marketNumber + "\", \"onloadWindowDate\":\"" + onloadWindowDate
				+ "\", \"onloadWindowTime\":\"" + onloadWindowTime + "\", \"loadNumber\":\"" + loadNumber
				+ "\", \"numStockDelivery\":\"" + numStockDelivery + "\", \"pall\":\"" + pall + "\", \"way\":\"" + way
				+ "\", \"onloadTime\":\"" + onloadTime 
				+ "\", \"needUnloadPoint\":\"" + needUnloadPoint + "\", \"idRamp\":\"" + idRamp
				+ "\", \"timeDelivery\":\"" + timeDelivery + "\", \"timeUnload\":\"" + timeUnload
				+ "\", \"loginManager\":\"" + loginManager + "\", \"statusYard\":\"" + statusYard
				+ "\", \"arrivalFactYard\":\"" + arrivalFactYard + "\", \"registrationFactYard\":\"" + registrationFactYard
				+ "\", \"unloadStartYard\":\"" + unloadStartYard + "\", \"unloadFinishYard\":\"" + unloadFinishYard
				+ "\", \"pallFactYard\":\"" + pallFactYard + "\", \"weightFactYard\":\"" + weightFactYard + "\"}";
	}
	
	
		
	
	

	
	
}
