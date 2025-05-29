package by.base.main.model;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "info_carrier")
public class InfoCarrier {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idinfo_carrier")
    private Integer id; // PRIMARY KEY

    @Column(name = "date_time_create")
    private Timestamp dateTimeCreate; // дата и время поступления заявки

    @Column(name = "cargo_transport_market", columnDefinition = "TEXT")
    private String cargoTransportMarket; // РЫНОК ГРУЗОПЕРЕВОЗОК

    @Column(name = "ownership_type", columnDefinition = "TEXT")
    private String ownershipType; // Форма собственности

    @Column(name = "carrier_name", columnDefinition = "TEXT")
    private String carrierName; // Название организации

    @Column(name = "offered_vehicle_count")
    private String offeredVehicleCount; // Кол-во предлагаемых авто

    @Column(name = "body_type", columnDefinition = "TEXT")
    private String bodyType; // Тип кузова

    @Column(name = "has_tail_lift", columnDefinition = "TEXT")
    private String hasTailLift; // Наличие гидроборта

    @Column(name = "has_navigation", columnDefinition = "TEXT")
    private String hasNavigation; // Наличие навигации

    @Column(name = "vehicle_location_city", columnDefinition = "TEXT")
    private String vehicleLocationCity; // Город в котором расположен Ваш транспорт

    @Column(name = "contact_phone", columnDefinition = "TEXT")
    private String contactPhone; // Телефон для связи

    @Column(name = "email_address")
    private String emailAddress; // Адрес эл. почты

    @Column(name = "offered_rate", columnDefinition = "TEXT")
    private String offeredRate; // предлагаемый нам тариф

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // примечание (тарификация и прочая важная информация)

    @Column(name = "application_status", columnDefinition = "TEXT")
    private String applicationStatus; // статус заявки

    @Column(name = "carrier_contact_date")
    private Timestamp carrierContactDate; // дата связи с перевозчиком

    @Column(name = "otl_responsible_specialist", columnDefinition = "TEXT")
    private String otlResponsibleSpecialist; // ответственный специалист ОТЛ

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment; // внутренний комментарий
    
    @Column(name = "contact_carrier", columnDefinition = "TEXT")
    private String contactCarrier; // ФИО контакта перевозчика
    
    @Column(name = "vehicle_capacity", columnDefinition = "TEXT")
    private String vehicleCapacity; // грузоподъемность
    
    @Column(name = "pallet_capacity", columnDefinition = "TEXT")
    private String palletCapacity; // паллетовместимость
    
    @Column(name = "status", columnDefinition = "TEXT")
    private Integer status; // паллетовместимость

    
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getVehicleCapacity() {
		return vehicleCapacity;
	}

	public void setVehicleCapacity(String vehicleCapacity) {
		this.vehicleCapacity = vehicleCapacity;
	}

	public String getPalletCapacity() {
		return palletCapacity;
	}

	public void setPalletCapacity(String palletCapacity) {
		this.palletCapacity = palletCapacity;
	}

	public String getContactCarrier() {
		return contactCarrier;
	}

	public void setContactCarrier(String contactCarrier) {
		this.contactCarrier = contactCarrier;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getDateTimeCreate() {
		return dateTimeCreate;
	}

	public void setDateTimeCreate(Timestamp dateTimeCreate) {
		this.dateTimeCreate = dateTimeCreate;
	}

	public String getCargoTransportMarket() {
		return cargoTransportMarket;
	}

	public void setCargoTransportMarket(String cargoTransportMarket) {
		this.cargoTransportMarket = cargoTransportMarket;
	}

	public String getOwnershipType() {
		return ownershipType;
	}

	public void setOwnershipType(String ownershipType) {
		this.ownershipType = ownershipType;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getOfferedVehicleCount() {
		return offeredVehicleCount;
	}

	public void setOfferedVehicleCount(String offeredVehicleCount) {
		this.offeredVehicleCount = offeredVehicleCount;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public String getHasTailLift() {
		return hasTailLift;
	}

	public void setHasTailLift(String hasTailLift) {
		this.hasTailLift = hasTailLift;
	}

	public String getHasNavigation() {
		return hasNavigation;
	}

	public void setHasNavigation(String hasNavigation) {
		this.hasNavigation = hasNavigation;
	}

	public String getVehicleLocationCity() {
		return vehicleLocationCity;
	}

	public void setVehicleLocationCity(String vehicleLocationCity) {
		this.vehicleLocationCity = vehicleLocationCity;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getOfferedRate() {
		return offeredRate;
	}

	public void setOfferedRate(String offeredRate) {
		this.offeredRate = offeredRate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getApplicationStatus() {
		return applicationStatus;
	}

	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}

	public Timestamp getCarrierContactDate() {
		return carrierContactDate;
	}

	public void setCarrierContactDate(Timestamp carrierContactDate) {
		this.carrierContactDate = carrierContactDate;
	}

	public String getOtlResponsibleSpecialist() {
		return otlResponsibleSpecialist;
	}

	public void setOtlResponsibleSpecialist(String otlResponsibleSpecialist) {
		this.otlResponsibleSpecialist = otlResponsibleSpecialist;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(applicationStatus, bodyType, cargoTransportMarket, carrierContactDate, carrierName, comment,
				contactPhone, dateTimeCreate, emailAddress, hasNavigation, hasTailLift, id, notes, offeredRate,
				offeredVehicleCount, otlResponsibleSpecialist, ownershipType, vehicleLocationCity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InfoCarrier other = (InfoCarrier) obj;
		return Objects.equals(applicationStatus, other.applicationStatus) && Objects.equals(bodyType, other.bodyType)
				&& Objects.equals(cargoTransportMarket, other.cargoTransportMarket)
				&& Objects.equals(carrierContactDate, other.carrierContactDate)
				&& Objects.equals(carrierName, other.carrierName) && Objects.equals(comment, other.comment)
				&& Objects.equals(contactPhone, other.contactPhone)
				&& Objects.equals(dateTimeCreate, other.dateTimeCreate)
				&& Objects.equals(emailAddress, other.emailAddress)
				&& Objects.equals(hasNavigation, other.hasNavigation) && Objects.equals(hasTailLift, other.hasTailLift)
				&& Objects.equals(id, other.id) && Objects.equals(notes, other.notes)
				&& Objects.equals(offeredRate, other.offeredRate)
				&& Objects.equals(offeredVehicleCount, other.offeredVehicleCount)
				&& Objects.equals(otlResponsibleSpecialist, other.otlResponsibleSpecialist)
				&& Objects.equals(ownershipType, other.ownershipType)
				&& Objects.equals(vehicleLocationCity, other.vehicleLocationCity);
	}

	@Override
	public String toString() {
		return "InfoCarrier [id=" + id + ", dateTimeCreate=" + dateTimeCreate + ", cargoTransportMarket="
				+ cargoTransportMarket + ", ownershipType=" + ownershipType + ", carrierName=" + carrierName
				+ ", offeredVehicleCount=" + offeredVehicleCount + ", bodyType=" + bodyType + ", hasTailLift="
				+ hasTailLift + ", hasNavigation=" + hasNavigation + ", vehicleLocationCity=" + vehicleLocationCity
				+ ", contactPhone=" + contactPhone + ", emailAddress=" + emailAddress + ", offeredRate=" + offeredRate
				+ ", notes=" + notes + ", applicationStatus=" + applicationStatus + ", carrierContactDate="
				+ carrierContactDate + ", otlResponsibleSpecialist=" + otlResponsibleSpecialist + ", comment=" + comment
				+ "]";
	}
}
