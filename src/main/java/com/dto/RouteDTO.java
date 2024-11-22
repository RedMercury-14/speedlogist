package com.dto;

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

public class RouteDTO {

	private Integer idRoute;

	private Integer numStock;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
	private LocalDate dateLoadPreviously;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime timeLoadPreviously;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime timeLoadPreviouslyStock;

	private LocalDateTime actualTimeArrival;

	private LocalDateTime startLoad;

	private LocalDateTime finishLoad;

	private LocalDateTime deliveryDocuments;

	private boolean isSanitization;

	private String temperature;

	private String nameLoader;

	private Integer ramp;

	private String totalLoadPall;

	private String totalCargoWeight;

	private String lines;

	private String comments;

	private String routeDirection;

	private Integer startPrice;

	private Integer finishPrice;

	@JsonFormat(pattern = "HH-mm")
	private LocalTime time;

	private String statusRoute;

	private String statusStock;

	private String typeTrailer;

	private String startCurrency;

	private String userComments;

	private String stepCost;

	private String optimalCost;

	private String customer;

	private String run;

	private String logistInfo;

	private String tnvd;

	private String way;

	private Integer expeditionCost;

	private String loadNumber;

	/**
	 * дата загрузки от перевозчика
	 */
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
	private LocalDate dateLoadActually;

	/**
	 * время загрузки от перевозчика
	 */
	@JsonFormat(pattern = "HH-mm")
	private LocalTime timeLoadActually;

	/**
	 * дата выгрузки от перевозчика
	 */
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
	private LocalDate dateUnloadActually;

	/**
	 * время выгрузки от перевозчика
	 */
	@JsonFormat(pattern = "HH-mm")
	private LocalTime timeUnloadActually;

	@JsonFormat(pattern = "HH-mm")
	private Time timeUnloadPreviouslyStock;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
	private Date dateUnloadPreviouslyStock;

	private Date createDate;

	private Time createTime;

	/**
	 * окно на выгрузку от Карины
	 */
	private Date onloadWindowDate;

	/**
	 * окно на выгрузку от Карины
	 */
	private Time onloadWindowTime;

	/**
	 * непосредственно сколько времени занимает сама выгрузка
	 */
	private Time onloadTime;

	private String logistComment;

	private String truckInfo;

	private Integer kmInfo;

	private String cargoInfo;

	private String routeDirectionInternational;

	private String typeLoad;

	private String methodLoad;

	// тут инфа по перевозчику
	private String nameOfСarrier;

	/*
	 * тут инфа по транспорту
	 */
	private String numOfTruck;
	private String modelOfTruck;
	private String brandOfTruck;
	private String typeOfTrailer;
	private String brandOfTrailer;
	/**
	 * владелец транспорта
	 */
	private String ownerOfTruck;
	private String cargoCapacityOfTruck;
	private String pallCapacityOfTruck;
	private String numTrailerOfTruck;
	/**
	 * тип сцепки: \nгрузовик\nполуприцеп\nсцепка
	 */
	private String hitchTypeofTruck;
	private String infoOfTruck;
	/**
	 * объем, м. куб
	 */
	private Integer volumeTrailerOfTruck;

	/**
	 * внутринние габариты кузова (Д/Ш/В), м
	 */
	private String dimensionsBodyOfTruck;

	/**
	 * Number of axes\n2\n3\n4+
	 */
	private String numberAxesOfTruck;

	/**
	 * Технический паспорт МАА 325845 РЭП ГАИ 02.02.2022\n
	 */
	private String technicalCertificateOfTruck;

	/*
	 * тут инфа по водителю
	 */
	private String numtelOfDriver;
	private String pasportOfDriver;
	private String nameOfDriver;
	private String surnameOfDriver;
	private String patronymicOfDriver;

	/**
	 * форматер для простого отображения дат во view
	 */
	private DateTimeFormatter simpleFormatterDate = DateTimeFormatter.ofPattern("dd.MM.yyy");
	
//	private Set<RouteHasShop> roteHasShop;

	private Set<OrderDTO> orders;

	public RouteDTO() {
		Date dateNow = Date.valueOf(LocalDate.now());
		Time timeNow = Time.valueOf(LocalTime.now());
		if (createDate == null) {
			this.createDate = dateNow;
		}
		if (createTime == null) {
			this.createTime = timeNow;
		}
	}
	
	
	public RouteDTO(Integer idRoute, Integer numStock, Object dateLoadPreviously, Object timeLoadPreviously,
			Object timeLoadPreviouslyStock, Object actualTimeArrival, Object startLoad,
			Object finishLoad, Object deliveryDocuments, boolean isSanitization, String temperature,
			String nameLoader, Integer ramp, String totalLoadPall, String totalCargoWeight, String lines,
			String comments, String routeDirection, Integer startPrice, Integer finishPrice, Object time,
			String statusRoute, String statusStock, String typeTrailer, String startCurrency, String userComments,
			String stepCost, String optimalCost, String customer, String run, String logistInfo, String tnvd,
			String way, Integer expeditionCost, String loadNumber, Object dateLoadActually,
			Object timeLoadActually, Object dateUnloadActually, Object timeUnloadActually,
			Object timeUnloadPreviouslyStock, Object dateUnloadPreviouslyStock, Object createDate,
			Object createTime, Object onloadWindowDate, Object onloadWindowTime, Object onloadTime, String logistComment,
			String truckInfo, Integer kmInfo, String cargoInfo, String routeDirectionInternational, String typeLoad,
			String methodLoad, String nameOfСarrier,
			String numOfTruck, String modelOfTruck, String brandOfTruck, String typeOfTrailer, String brandOfTrailer,
			String ownerOfTruck, String cargoCapacityOfTruck, String pallCapacityOfTruck, String numTrailerOfTruck,
			String hitchTypeofTruck, String infoOfTruck, Integer volumeTrailerOfTruck, String dimensionsBodyOfTruck,
			String numberAxesOfTruck, String technicalCertificateOfTruck, String numtelOfDriver,
			String pasportOfDriver, String nameOfDriver, String surnameOfDriver, String patronymicOfDriver) {
		super();
		this.idRoute = idRoute;
		this.numStock = numStock;
		this.dateLoadPreviously = (LocalDate) dateLoadPreviously;
		this.timeLoadPreviously = (LocalTime) timeLoadPreviously;
		this.timeLoadPreviouslyStock = (LocalTime) timeLoadPreviouslyStock;
		this.actualTimeArrival = (LocalDateTime) actualTimeArrival;
		this.startLoad = (LocalDateTime) startLoad;
		this.finishLoad = (LocalDateTime) finishLoad;
		this.deliveryDocuments = (LocalDateTime) deliveryDocuments;
		this.isSanitization = isSanitization;
		this.temperature = temperature;
		this.nameLoader = nameLoader;
		this.ramp = ramp;
		this.totalLoadPall = totalLoadPall;
		this.totalCargoWeight = totalCargoWeight;
		this.lines = lines;
		this.comments = comments;
		this.routeDirection = routeDirection;
		this.startPrice = startPrice;
		this.finishPrice = finishPrice;
		this.time = (LocalTime) time;
		this.statusRoute = statusRoute;
		this.statusStock = statusStock;
		this.typeTrailer = typeTrailer;
		this.startCurrency = startCurrency;
		this.userComments = userComments;
		this.stepCost = stepCost;
		this.optimalCost = optimalCost;
		this.customer = customer;
		this.run = run;
		this.logistInfo = logistInfo;
		this.tnvd = tnvd;
		this.way = way;
		this.expeditionCost = expeditionCost;
		this.loadNumber = loadNumber;
		this.dateLoadActually = (LocalDate) dateLoadActually;
		this.timeLoadActually = (LocalTime) timeLoadActually;
		this.dateUnloadActually = (LocalDate) dateUnloadActually;
		this.timeUnloadActually = (LocalTime) timeUnloadActually;
		this.timeUnloadPreviouslyStock = (Time) timeUnloadPreviouslyStock;
		this.dateUnloadPreviouslyStock = (Date) dateUnloadPreviouslyStock;
		this.createDate = (Date) createDate;
		this.createTime = (Time) createTime;
		this.onloadWindowDate = (Date) onloadWindowDate;
		this.onloadWindowTime = (Time) onloadWindowTime;
		this.onloadTime = (Time) onloadTime;
		this.logistComment = logistComment;
		this.truckInfo = truckInfo;
		this.kmInfo = kmInfo;
		this.cargoInfo = cargoInfo;
		this.routeDirectionInternational = routeDirectionInternational;
		this.typeLoad = typeLoad;
		this.methodLoad = methodLoad;
		/*
		 * фирма
		 */
		this.nameOfСarrier = nameOfСarrier;
		/*
		 * машина
		 */
		this.numOfTruck = numOfTruck;
		this.modelOfTruck = modelOfTruck;
		this.brandOfTruck = brandOfTruck;
		this.typeOfTrailer = typeOfTrailer;
		this.brandOfTrailer = brandOfTrailer;
		this.ownerOfTruck = ownerOfTruck;
		this.cargoCapacityOfTruck = cargoCapacityOfTruck;
		this.pallCapacityOfTruck = pallCapacityOfTruck;
		this.numTrailerOfTruck = numTrailerOfTruck;
		this.hitchTypeofTruck = hitchTypeofTruck;
		this.infoOfTruck = infoOfTruck;
		this.volumeTrailerOfTruck = volumeTrailerOfTruck;
		this.dimensionsBodyOfTruck = dimensionsBodyOfTruck;
		this.numberAxesOfTruck = numberAxesOfTruck;
		this.technicalCertificateOfTruck = technicalCertificateOfTruck;
		/*
		 * водила
		 */
		this.numtelOfDriver = numtelOfDriver;
		this.pasportOfDriver = pasportOfDriver;
		this.nameOfDriver = nameOfDriver;
		this.surnameOfDriver = surnameOfDriver;
		this.patronymicOfDriver = patronymicOfDriver;
	}
	
	public RouteDTO(Integer idRoute, Integer numStock, Object dateLoadPreviously, Object timeLoadPreviously,
			Object timeLoadPreviouslyStock, Object actualTimeArrival, Object startLoad,
			Object finishLoad, Object deliveryDocuments, Object isSanitization, String temperature,
			String nameLoader, Integer ramp, String totalLoadPall, String totalCargoWeight, String lines,
			String comments, String routeDirection, Integer startPrice, Integer finishPrice, Object time,
			String statusRoute, String statusStock, String typeTrailer, String startCurrency, String userComments,
			String stepCost, String optimalCost, String customer, String run, String logistInfo, String tnvd,
			String way, Integer expeditionCost, String loadNumber, Object dateLoadActually,
			Object timeLoadActually, Object dateUnloadActually, Object timeUnloadActually,
			Object timeUnloadPreviouslyStock, Object dateUnloadPreviouslyStock, Object createDate,
			Object createTime, Object onloadWindowDate, Object onloadWindowTime, Object onloadTime, String logistComment,
			String truckInfo, Integer kmInfo, String cargoInfo, String routeDirectionInternational, String typeLoad,
			String methodLoad) {
		super();
		this.idRoute = idRoute;
		this.numStock = numStock;
		this.dateLoadPreviously = (LocalDate) dateLoadPreviously;
		this.timeLoadPreviously = (LocalTime) timeLoadPreviously;
		this.timeLoadPreviouslyStock = (LocalTime) timeLoadPreviouslyStock;
		this.actualTimeArrival = (LocalDateTime) actualTimeArrival;
		this.startLoad = (LocalDateTime) startLoad;
		this.finishLoad = (LocalDateTime) finishLoad;
		this.deliveryDocuments = (LocalDateTime) deliveryDocuments;
		this.isSanitization = (boolean) isSanitization;
		this.temperature = temperature;
		this.nameLoader = nameLoader;
		this.ramp = ramp;
		this.totalLoadPall = totalLoadPall;
		this.totalCargoWeight = totalCargoWeight;
		this.lines = lines;
		this.comments = comments;
		this.routeDirection = routeDirection;
		this.startPrice = startPrice;
		this.finishPrice = finishPrice;
		this.time = (LocalTime) time;
		this.statusRoute = statusRoute;
		this.statusStock = statusStock;
		this.typeTrailer = typeTrailer;
		this.startCurrency = startCurrency;
		this.userComments = userComments;
		this.stepCost = stepCost;
		this.optimalCost = optimalCost;
		this.customer = customer;
		this.run = run;
		this.logistInfo = logistInfo;
		this.tnvd = tnvd;
		this.way = way;
		this.expeditionCost = expeditionCost;
		this.loadNumber = loadNumber;
		this.dateLoadActually = (LocalDate) dateLoadActually;
		this.timeLoadActually = (LocalTime) timeLoadActually;
		this.dateUnloadActually = (LocalDate) dateUnloadActually;
		this.timeUnloadActually = (LocalTime) timeUnloadActually;
		this.timeUnloadPreviouslyStock = (Time) timeUnloadPreviouslyStock;
		this.dateUnloadPreviouslyStock = (Date) dateUnloadPreviouslyStock;
		this.createDate = (Date) createDate;
		this.createTime = (Time) createTime;
		this.onloadWindowDate = (Date) onloadWindowDate;
		this.onloadWindowTime = (Time) onloadWindowTime;
		this.onloadTime = (Time) onloadTime;
		this.logistComment = logistComment;
		this.truckInfo = truckInfo;
		this.kmInfo = kmInfo;
		this.cargoInfo = cargoInfo;
		this.routeDirectionInternational = routeDirectionInternational;
		this.typeLoad = typeLoad;
		this.methodLoad = methodLoad;
		
	}



	public String getSurnameOfDriver() {
		return surnameOfDriver;
	}


	public void setSurnameOfDriver(String surnameOfDriver) {
		this.surnameOfDriver = surnameOfDriver;
	}


	public String getPatronymicOfDriver() {
		return patronymicOfDriver;
	}


	public void setPatronymicOfDriver(String patronymicOfDriver) {
		this.patronymicOfDriver = patronymicOfDriver;
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

	public String getRouteDirectionInternational() {
		return routeDirectionInternational;
	}

	public void setRouteDirectionInternational(String routeDirectionInternational) {
		this.routeDirectionInternational = routeDirectionInternational;
	}

	public String getLogistComment() {
		return logistComment;
	}

	public String getCargoInfo() {
		return cargoInfo;
	}

	public void setCargoInfo(String cargoInfo) {
		this.cargoInfo = cargoInfo;
	}

	public Integer getKmInfo() {
		return kmInfo;
	}

	public void setKmInfo(Integer kmInfo) {
		this.kmInfo = kmInfo;
	}

	public void setLogistComment(String logistComment) {
		this.logistComment = logistComment;
	}

	public String getTruckInfo() {
		return truckInfo;
	}

	public void setTruckInfo(String truckInfo) {
		this.truckInfo = truckInfo;
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

	public Integer getExpeditionCost() {
		return expeditionCost;
	}

	public void setExpeditionCost(Integer expeditionCost) {
		this.expeditionCost = expeditionCost;
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
	 * 
	 * @return
	 */
	public Time getOnloadTime() {
		return onloadTime;
	}

	/**
	 * непосредственно сколько времени занимает сама выгрузка
	 * 
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
		if (way != null && routeDirectionInternational != null && way.equals("Импорт")) {
			return routeDirectionInternational.trim();
		}
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
		if (dateLoadActually != null) {
			return dateLoadActually.format(simpleFormatterDate);
		} else {
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
		} else {
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
		} else {
			return way;
		}
	}

	public void setWay(String way) {
		this.way = way;
	}

	public String getSimpleWay() {
		if (way == null) {
			return null;
		} else if (way.length() < 3) {
			return way;
		} else {
			return way.substring(0, 3);
		}
	}

	public SimpleRouteDTO getSimpleRoute() {
		return new SimpleRouteDTO(idRoute, dateLoadPreviously, routeDirection, finishPrice, startCurrency);
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
		if (dateUnloadActually != null) {
			return dateUnloadActually.format(simpleFormatterDate);
		} else {
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

	public Time getTimeUnloadPreviouslyStock() {
		return timeUnloadPreviouslyStock;
	}

	public void setTimeUnloadPreviouslyStock(String timeUnloadPreviouslyStock) {
		if (timeUnloadPreviouslyStock == null || timeUnloadPreviouslyStock.isEmpty()) {
			this.timeUnloadPreviouslyStock = null;
		} else {
			if (timeUnloadPreviouslyStock.split(":").length < 3) {
				this.timeUnloadPreviouslyStock = Time.valueOf(timeUnloadPreviouslyStock + ":00");
			} else {
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
		if (dateUnloadPreviouslyStock == null || dateUnloadPreviouslyStock.isEmpty()) {
			this.dateUnloadPreviouslyStock = null;
		} else {
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

	// ------------------------------

	@Override
	public int hashCode() {
		return Objects.hash(idRoute);
	}

	public Set<OrderDTO> getOrders() {
		return orders;
	}

	public void setOrders(Set<OrderDTO> orders) {
		this.orders = orders;
	}

	public String getNameOfСarrier() {
		return nameOfСarrier;
	}

	public void setNameOfСarrier(String nameOfСarrier) {
		this.nameOfСarrier = nameOfСarrier;
	}

	public String getNumOfTruck() {
		return numOfTruck;
	}

	public void setNumOfTruck(String numOfTruck) {
		this.numOfTruck = numOfTruck;
	}

	public String getModelOfTruck() {
		return modelOfTruck;
	}

	public void setModelOfTruck(String modelOfTruck) {
		this.modelOfTruck = modelOfTruck;
	}

	public String getBrandOfTruck() {
		return brandOfTruck;
	}

	public void setBrandOfTruck(String brandOfTruck) {
		this.brandOfTruck = brandOfTruck;
	}

	public String getTypeOfTrailer() {
		return typeOfTrailer;
	}

	public void setTypeOfTrailer(String typeOfTrailer) {
		this.typeOfTrailer = typeOfTrailer;
	}

	public String getBrandOfTrailer() {
		return brandOfTrailer;
	}

	public void setBrandOfTrailer(String brandOfTrailer) {
		this.brandOfTrailer = brandOfTrailer;
	}

	public String getOwnerOfTruck() {
		return ownerOfTruck;
	}

	public void setOwnerOfTruck(String ownerOfTruck) {
		this.ownerOfTruck = ownerOfTruck;
	}

	public String getCargoCapacityOfTruck() {
		return cargoCapacityOfTruck;
	}

	public void setCargoCapacityOfTruck(String cargoCapacityOfTruck) {
		this.cargoCapacityOfTruck = cargoCapacityOfTruck;
	}

	public String getPallCapacityOfTruck() {
		return pallCapacityOfTruck;
	}

	public void setPallCapacityOfTruck(String pallCapacityOfTruck) {
		this.pallCapacityOfTruck = pallCapacityOfTruck;
	}

	public String getNumTrailerOfTruck() {
		return numTrailerOfTruck;
	}

	public void setNumTrailerOfTruck(String numTrailerOfTruck) {
		this.numTrailerOfTruck = numTrailerOfTruck;
	}

	public String getHitchTypeofTruck() {
		return hitchTypeofTruck;
	}

	public void setHitchTypeofTruck(String hitchTypeofTruck) {
		this.hitchTypeofTruck = hitchTypeofTruck;
	}

	public String getInfoOfTruck() {
		return infoOfTruck;
	}

	public void setInfoOfTruck(String infoOfTruck) {
		this.infoOfTruck = infoOfTruck;
	}

	public Integer getVolumeTrailerOfTruck() {
		return volumeTrailerOfTruck;
	}

	public void setVolumeTrailerOfTruck(Integer volumeTrailerOfTruck) {
		this.volumeTrailerOfTruck = volumeTrailerOfTruck;
	}

	public String getDimensionsBodyOfTruck() {
		return dimensionsBodyOfTruck;
	}

	public void setDimensionsBodyOfTruck(String dimensionsBodyOfTruck) {
		this.dimensionsBodyOfTruck = dimensionsBodyOfTruck;
	}

	public String getNumberAxesOfTruck() {
		return numberAxesOfTruck;
	}

	public void setNumberAxesOfTruck(String numberAxesOfTruck) {
		this.numberAxesOfTruck = numberAxesOfTruck;
	}

	public String getTechnicalCertificateOfTruck() {
		return technicalCertificateOfTruck;
	}

	public void setTechnicalCertificateOfTruck(String technicalCertificateOfTruck) {
		this.technicalCertificateOfTruck = technicalCertificateOfTruck;
	}

	public String getNameOfDriver() {
		return nameOfDriver;
	}

	public void setNameOfDriver(String nameOfDriver) {
		this.nameOfDriver = nameOfDriver;
	}

	public String getNumtelOfDriver() {
		return numtelOfDriver;
	}

	public void setNumtelOfDriver(String numtelOfDriver) {
		this.numtelOfDriver = numtelOfDriver;
	}

	public String getPasportOfDriver() {
		return pasportOfDriver;
	}

	public void setPasportOfDriver(String pasportOfDriver) {
		this.pasportOfDriver = pasportOfDriver;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteDTO other = (RouteDTO) obj;
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
