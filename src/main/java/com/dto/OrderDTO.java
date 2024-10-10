package com.dto;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

import by.base.main.model.Address;


public class OrderDTO {

	private Integer idOrder;
	
	private String counterparty;
	
	private String contact;
	
	private String cargo;
	
	private String typeLoad;
	
	private String methodLoad;
	
	private String typeTruck;
	
	private String temperature;
	
	private Boolean control;
	
	private String comment;
	
	private Integer status;
	
	private Date dateCreate;
	
	private Date dateDelivery;
	
	private String manager;
	
	private String telephoneManager;
	
	private Boolean stacking;
	
	private String logist;
	
	private String logistTelephone;
	
	private String marketNumber;
	
	private Date onloadWindowDate;
	
	private Time onloadWindowTime;
	
	private String loadNumber;
	
	private String numStockDelivery;
	
	private String pall;

	private List<AddressDTO> addresses;

	private String way;
	
	private Time onloadTime;
	
	private String incoterms;
	
	private String changeStatus;
	
	private String needUnloadPoint;
	
	private Integer idRamp;
	
	private Timestamp timeDelivery;
	
	private Time timeUnload;
	
	private String loginManager;
	
	private Integer sku;
	
	private Integer monoPall;
	
	private Integer mixPall;
	
	private String isInternalMovement;
	
	private String mailInfo;
	
	private String slotInfo;
	
	private Date dateCreateMarket;
	
	private String marketInfo;
	
	private String marketContractType;
	
	private String marketContractGroupId;
	
	private String marketContractNumber;
	
	private String marketContractorId;
	
	private String numProduct;
	
	private Integer statusYard;
	
	private Timestamp unloadStartYard;
	
	private Timestamp unloadFinishYard;
	
	private Integer pallFactYard;
	
	private Double weightFactYard;
	
	private Double marketOrderSumFirst;
	
	private Double marketOrderSumFinal;
	
	private Timestamp arrivalFactYard;
	
	private Timestamp registrationFactYard;
	
	private String addressLoading;
	
	private Timestamp lastDatetimePointLoad;
	
	private Date dateOrderOrl;
	
	public OrderDTO() {
		
	}
	
		
	public OrderDTO(Integer idOrder, String counterparty, String contact, String cargo, String typeLoad,
			String methodLoad, String typeTruck, String temperature, Object control, String comment, Integer status,
			Object dateCreate, Object dateDelivery, String manager, String telephoneManager, Object stacking,
			String logist, String logistTelephone, String marketNumber, Object onloadWindowDate, Object onloadWindowTime,
			String loadNumber, String numStockDelivery, String pall, String way,
			Object onloadTime, String incoterms, String changeStatus, String needUnloadPoint, Integer idRamp,
			Object timeDelivery, Object timeUnload, String loginManager, Integer sku, Integer monoPall,
			Integer mixPall, String isInternalMovement, String mailInfo, String slotInfo, Object dateCreateMarket,
			String marketInfo, String marketContractType, String marketContractGroupId, String marketContractNumber,
			String marketContractorId, String numProduct, Integer statusYard, Object unloadStartYard,
			Object unloadFinishYard, Integer pallFactYard, Double weightFactYard, Double marketOrderSumFirst,
			Double marketOrderSumFinal, Object arrivalFactYard, Object registrationFactYard, String addressLoading, Object lastDatetimePointLoad, Object dateOrderOrl) {
		super();
		this.idOrder = idOrder;
		this.counterparty = counterparty;
		this.contact = contact;
		this.cargo = cargo;
		this.typeLoad = typeLoad;
		this.methodLoad = methodLoad;
		this.typeTruck = typeTruck;
		this.temperature = temperature;
		this.control = (Boolean) control;
		this.comment = comment;
		this.status = status;
		this.dateCreate = (Date) dateCreate;
		this.dateDelivery = (Date) dateDelivery;
		this.manager = manager;
		this.telephoneManager = telephoneManager;
		this.stacking = (Boolean) stacking;
		this.logist = logist;
		this.logistTelephone = logistTelephone;
		this.marketNumber = marketNumber;
		this.onloadWindowDate = (Date) onloadWindowDate;
		this.onloadWindowTime = (Time) onloadWindowTime;
		this.loadNumber = loadNumber;
		this.numStockDelivery = numStockDelivery;
		this.pall = pall;
		this.way = way;
		this.onloadTime = (Time) onloadTime;
		this.incoterms = incoterms;
		this.changeStatus = changeStatus;
		this.needUnloadPoint = needUnloadPoint;
		this.idRamp = idRamp;
		this.timeDelivery = (Timestamp) timeDelivery;
		this.timeUnload = (Time) timeUnload;
		this.loginManager = loginManager;
		this.sku = sku;
		this.monoPall = monoPall;
		this.mixPall = mixPall;
		this.isInternalMovement = isInternalMovement;
		this.mailInfo = mailInfo;
		this.slotInfo = slotInfo;
		this.dateCreateMarket = (Date) dateCreateMarket;
		this.marketInfo = marketInfo;
		this.marketContractType = marketContractType;
		this.marketContractGroupId = marketContractGroupId;
		this.marketContractNumber = marketContractNumber;
		this.marketContractorId = marketContractorId;
		this.numProduct = numProduct;
		this.statusYard = statusYard;
		this.unloadStartYard = (Timestamp) unloadStartYard;
		this.unloadFinishYard = (Timestamp) unloadFinishYard;
		this.pallFactYard = pallFactYard;
		this.weightFactYard = weightFactYard;
		this.marketOrderSumFirst = marketOrderSumFirst;
		this.marketOrderSumFinal = marketOrderSumFinal;
		this.arrivalFactYard = (Timestamp) arrivalFactYard;
		this.registrationFactYard = (Timestamp) registrationFactYard;
		this.addressLoading = addressLoading;
		this.lastDatetimePointLoad = (Timestamp) lastDatetimePointLoad;
		this.dateOrderOrl = (Date) dateOrderOrl;
	}
	
	@Deprecated
	public OrderDTO(Integer idOrder, String counterparty, String contact, String cargo, String typeLoad,
			String methodLoad, String typeTruck, String temperature, Object control, String comment, Integer status,
			Object dateCreate, Object dateDelivery, String manager, String telephoneManager, Object stacking,
			String logist, String logistTelephone, String marketNumber, Object onloadWindowDate, Object onloadWindowTime,
			String loadNumber, String numStockDelivery, String pall, String way,
			Object onloadTime, String incoterms, String changeStatus, String needUnloadPoint, Integer idRamp,
			Object timeDelivery, Object timeUnload, String loginManager, Integer sku, Integer monoPall,
			Integer mixPall, String isInternalMovement, String mailInfo, String slotInfo, Object dateCreateMarket,
			String marketInfo, String marketContractType, String marketContractGroupId, String marketContractNumber,
			String marketContractorId, String numProduct, Integer statusYard, Object unloadStartYard,
			Object unloadFinishYard, Integer pallFactYard, Double weightFactYard, Double marketOrderSumFirst,
			Double marketOrderSumFinal, Object arrivalFactYard, Object registrationFactYard, String addressLoading, Object lastDatetimePointLoad) {
		super();
		this.idOrder = idOrder;
		this.counterparty = counterparty;
		this.contact = contact;
		this.cargo = cargo;
		this.typeLoad = typeLoad;
		this.methodLoad = methodLoad;
		this.typeTruck = typeTruck;
		this.temperature = temperature;
		this.control = (Boolean) control;
		this.comment = comment;
		this.status = status;
		this.dateCreate = (Date) dateCreate;
		this.dateDelivery = (Date) dateDelivery;
		this.manager = manager;
		this.telephoneManager = telephoneManager;
		this.stacking = (Boolean) stacking;
		this.logist = logist;
		this.logistTelephone = logistTelephone;
		this.marketNumber = marketNumber;
		this.onloadWindowDate = (Date) onloadWindowDate;
		this.onloadWindowTime = (Time) onloadWindowTime;
		this.loadNumber = loadNumber;
		this.numStockDelivery = numStockDelivery;
		this.pall = pall;
		this.way = way;
		this.onloadTime = (Time) onloadTime;
		this.incoterms = incoterms;
		this.changeStatus = changeStatus;
		this.needUnloadPoint = needUnloadPoint;
		this.idRamp = idRamp;
		this.timeDelivery = (Timestamp) timeDelivery;
		this.timeUnload = (Time) timeUnload;
		this.loginManager = loginManager;
		this.sku = sku;
		this.monoPall = monoPall;
		this.mixPall = mixPall;
		this.isInternalMovement = isInternalMovement;
		this.mailInfo = mailInfo;
		this.slotInfo = slotInfo;
		this.dateCreateMarket = (Date) dateCreateMarket;
		this.marketInfo = marketInfo;
		this.marketContractType = marketContractType;
		this.marketContractGroupId = marketContractGroupId;
		this.marketContractNumber = marketContractNumber;
		this.marketContractorId = marketContractorId;
		this.numProduct = numProduct;
		this.statusYard = statusYard;
		this.unloadStartYard = (Timestamp) unloadStartYard;
		this.unloadFinishYard = (Timestamp) unloadFinishYard;
		this.pallFactYard = pallFactYard;
		this.weightFactYard = weightFactYard;
		this.marketOrderSumFirst = marketOrderSumFirst;
		this.marketOrderSumFinal = marketOrderSumFinal;
		this.arrivalFactYard = (Timestamp) arrivalFactYard;
		this.registrationFactYard = (Timestamp) registrationFactYard;
		this.addressLoading = addressLoading;
		this.lastDatetimePointLoad = (Timestamp) lastDatetimePointLoad;
	}




	public Date getDateOrderOrl() {
		return dateOrderOrl;
	}


	public void setDateOrderOrl(Date dateOrderOrl) {
		this.dateOrderOrl = dateOrderOrl;
	}


	/**
	 * @param idOrder
	 * @param status
	 */
	public OrderDTO(Integer idOrder, Integer status, String con) {
		super();
		this.idOrder = idOrder;
		this.status = status;
		this.counterparty = con;
	}
	
	public OrderDTO(Integer idOrder, Integer status, String con, Object timeDelivery) {
		super();
		this.idOrder = idOrder;
		this.status = status;
		this.counterparty = con;
		this.timeDelivery = (Timestamp) timeDelivery;
	}
	
	public OrderDTO(Integer idOrder, Integer status) {
		super();
		this.idOrder = idOrder;
		this.status = status;
	}



	public Timestamp getLastDatetimePointLoad() {
		return lastDatetimePointLoad;
	}


	public void setLastDatetimePointLoad(Timestamp lastDatetimePointLoad) {
		this.lastDatetimePointLoad = lastDatetimePointLoad;
	}


	public String getAddressLoading() {
		return addressLoading;
	}


	public void setAddressLoading(String addressLoading) {
		this.addressLoading = addressLoading;
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

	public void setCounterparty(String counterparty) {
		this.counterparty = counterparty;
	}

	public String getContact() {
		return contact;
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

	public void setTypeLoad(String typeLoad) {
		this.typeLoad = typeLoad;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getStatus() {
		return status;
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

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getTelephoneManager() {
		return telephoneManager;
	}

	public void setTelephoneManager(String telephoneManager) {
		this.telephoneManager = telephoneManager;
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

	public String getLoadNumber() {
		return loadNumber;
	}

	public void setLoadNumber(String loadNumber) {
		this.loadNumber = loadNumber;
	}

	public String getNumStockDelivery() {
		return numStockDelivery;
	}

	public void setNumStockDelivery(String numStockDelivery) {
		this.numStockDelivery = numStockDelivery;
	}

	public String getPall() {
		return pall;
	}

	public void setPall(String pall) {
		this.pall = pall;
	}

	public List<AddressDTO> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressDTO> addresses) {
		this.addresses = addresses;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public Time getOnloadTime() {
		return onloadTime;
	}

	public void setOnloadTime(Time onloadTime) {
		this.onloadTime = onloadTime;
	}

	public String getIncoterms() {
		return incoterms;
	}

	public void setIncoterms(String incoterms) {
		this.incoterms = incoterms;
	}

	public String getChangeStatus() {
		return changeStatus;
	}

	public void setChangeStatus(String changeStatus) {
		this.changeStatus = changeStatus;
	}

	public String getNeedUnloadPoint() {
		return needUnloadPoint;
	}

	public void setNeedUnloadPoint(String needUnloadPoint) {
		this.needUnloadPoint = needUnloadPoint;
	}

	public Integer getIdRamp() {
		return idRamp;
	}

	public void setIdRamp(Integer idRamp) {
		this.idRamp = idRamp;
	}

	public Timestamp getTimeDelivery() {
		return timeDelivery;
	}

	public void setTimeDelivery(Timestamp timeDelivery) {
		this.timeDelivery = timeDelivery;
	}

	public Time getTimeUnload() {
		return timeUnload;
	}

	public void setTimeUnload(Time timeUnload) {
		this.timeUnload = timeUnload;
	}

	public String getLoginManager() {
		return loginManager;
	}

	public void setLoginManager(String loginManager) {
		this.loginManager = loginManager;
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

	public String getSlotInfo() {
		return slotInfo;
	}

	public void setSlotInfo(String slotInfo) {
		this.slotInfo = slotInfo;
	}

	public Date getDateCreateMarket() {
		return dateCreateMarket;
	}

	public void setDateCreateMarket(Date dateCreateMarket) {
		this.dateCreateMarket = dateCreateMarket;
	}

	public String getMarketInfo() {
		return marketInfo;
	}

	public void setMarketInfo(String marketInfo) {
		this.marketInfo = marketInfo;
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

	public String getNumProduct() {
		return numProduct;
	}

	public void setNumProduct(String numProduct) {
		this.numProduct = numProduct;
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
		OrderDTO other = (OrderDTO) obj;
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
