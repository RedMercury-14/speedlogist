package by.base.main.model.yard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Таблица приёмки
 */
@Entity
@Table(name = "acceptance")
public class Acceptance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceptance")
    private Integer idAcceptance;

    @Column(name = "id_order", nullable = false)
    private String idOrder;

    @Column(name = "id_sklad", nullable = false)
    private Integer idSklad;

    @Column(name = "firm_name_accept", nullable = false)
    private String firmNameAccept;

    @Column(name = "date_plan_accept", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datePlanAccept;

    @Column(name = "car_number")
    private String carNumber;

    @Column(name = "number_rampe_accept")
    private Integer numberRampeAccept;

    @Column(name = "date_upload_acceptance")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateUploadAcceptance;

    @Column(name = "time_registration_accept")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeRegistrationAccept;

    @Column(name = "time_start_accept")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeStartAccept;

    @Column(name = "time_end_accept")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeEndAccept;

    @Column(name = "user_start")
    private String userStart;

    @Column(name = "user_stop")
    private String userStop;

    @Column(name = "type_sklad", nullable = false)
    private String typeSklad;

    @OneToMany(mappedBy = "acceptance", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<TtnIn> ttnInList;

    @Column(name = "sku")
    private Integer sku;

    @Column(name = "pallets_was")
    private Integer palletsWas;

    @Column(name = "pallets_in_work")
    private Integer palletsInWork;

    @Column(name = "pallets_accepted")
    private Integer palletsAccepted;

    @Column(name = "pallets_return")
    private Integer palletsReturn;

    @Column(name = "act")
    private Integer act;

    @Column(name = "act_number")
    private Integer actNumber;

    @Column(name = "cargo_weight")
    private String cargoWeight;

    @Column(name = "temperature")
    private String temperature;

    @Column(name = "info_skan")
    private Integer infoSkan;

    @Column(name = "stm")
    private Integer stm;

    @Column(name = "ukz")
    private Integer ukz;

    @Column(name = "order_error")
    private Integer orderError;

    @Column(name = "info_acceptance", columnDefinition = "TEXT")
    private String infoAcceptance;

    @Column(name = "reason_for_lateness")
    private String reasonForLateness;

    @Column(name = "unloading_type")
    private Byte unloadingType;

    @Column(name = "method_load")
    private String methodLoad;

    @Column(name = "reason_of_registration_lateness")
    private String reasonOfRegistrationLateness;

    @Column(name = "reason_of_early_start_acceptance")
    private String reasonOfEarlyStartAcceptance;
    
    @Column(name = "is_import")
    private Boolean isImport;

    public Boolean getIsImport() {
		return isImport;
	}

	public void setIsImport(Boolean isImport) {
		this.isImport = isImport;
	}

	public Integer getIdAcceptance() {
        return idAcceptance;
    }

    public void setIdAcceptance(Integer idAcceptance) {
        this.idAcceptance = idAcceptance;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public Integer getIdSklad() {
        return idSklad;
    }

    public void setIdSklad(Integer idSklad) {
        this.idSklad = idSklad;
    }

    public String getFirmNameAccept() {
        return firmNameAccept;
    }

    public void setFirmNameAccept(String firmNameAccept) {
        this.firmNameAccept = firmNameAccept;
    }

    public LocalDateTime getDatePlanAccept() {
        return datePlanAccept;
    }

    public void setDatePlanAccept(LocalDateTime datePlanAccept) {
        this.datePlanAccept = datePlanAccept;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public Integer getNumberRampeAccept() {
        return numberRampeAccept;
    }

    public void setNumberRampeAccept(Integer numberRampeAccept) {
        this.numberRampeAccept = numberRampeAccept;
    }

    public LocalDateTime getDateUploadAcceptance() {
        return dateUploadAcceptance;
    }

    public void setDateUploadAcceptance(LocalDateTime dateUploadAcceptance) {
        this.dateUploadAcceptance = dateUploadAcceptance;
    }

    public LocalDateTime getTimeRegistrationAccept() {
        return timeRegistrationAccept;
    }

    public void setTimeRegistrationAccept(LocalDateTime timeRegistrationAccept) {
        this.timeRegistrationAccept = timeRegistrationAccept;
    }

    public LocalDateTime getTimeStartAccept() {
        return timeStartAccept;
    }

    public void setTimeStartAccept(LocalDateTime timeStartAccept) {
        this.timeStartAccept = timeStartAccept;
    }

    public LocalDateTime getTimeEndAccept() {
        return timeEndAccept;
    }

    public void setTimeEndAccept(LocalDateTime timeEndAccept) {
        this.timeEndAccept = timeEndAccept;
    }

    public String getUserStart() {
        return userStart;
    }

    public void setUserStart(String userStart) {
        this.userStart = userStart;
    }

    public String getUserStop() {
        return userStop;
    }

    public void setUserStop(String userStop) {
        this.userStop = userStop;
    }

    public String getTypeSklad() {
        return typeSklad;
    }

    public void setTypeSklad(String typeSklad) {
        this.typeSklad = typeSklad;
    }

    public Set<TtnIn> getTtnInList() {
        return ttnInList;
    }

    public void setTtnInList(Set<TtnIn> ttnInList) {
        this.ttnInList = ttnInList;
    }

    public Integer getSku() {
        return sku;
    }

    public void setSku(Integer sku) {
        this.sku = sku;
    }

    public Integer getPalletsWas() {
        return palletsWas;
    }

    public void setPalletsWas(Integer palletsWas) {
        this.palletsWas = palletsWas;
    }

    public Integer getPalletsInWork() {
        return palletsInWork;
    }

    public void setPalletsInWork(Integer palletsInWork) {
        this.palletsInWork = palletsInWork;
    }

    public Integer getPalletsAccepted() {
        return palletsAccepted;
    }

    public void setPalletsAccepted(Integer palletsAccepted) {
        this.palletsAccepted = palletsAccepted;
    }

    public Integer getPalletsReturn() {
        return palletsReturn;
    }

    public void setPalletsReturn(Integer palletsReturn) {
        this.palletsReturn = palletsReturn;
    }

    public Integer getAct() {
        return act;
    }

    public void setAct(Integer act) {
        this.act = act;
    }

    public Integer getActNumber() {
        return actNumber;
    }

    public void setActNumber(Integer actNumber) {
        this.actNumber = actNumber;
    }

    public String getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(String cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public Integer getInfoSkan() {
        return infoSkan;
    }

    public void setInfoSkan(Integer infoSkan) {
        this.infoSkan = infoSkan;
    }

    public Integer getStm() {
        return stm;
    }

    public void setStm(Integer stm) {
        this.stm = stm;
    }

    public Integer getUkz() {
        return ukz;
    }

    public void setUkz(Integer ukz) {
        this.ukz = ukz;
    }

    public Integer getOrderError() {
        return orderError;
    }

    public void setOrderError(Integer orderError) {
        this.orderError = orderError;
    }

    public String getInfoAcceptance() {
        return infoAcceptance;
    }

    public void setInfoAcceptance(String infoAcceptance) {
        this.infoAcceptance = infoAcceptance;
    }

    public String getReasonForLateness() {
        return reasonForLateness;
    }

    public void setReasonForLateness(String reasonForLateness) {
        this.reasonForLateness = reasonForLateness;
    }

    public Byte getUnloadingType() {
        return unloadingType;
    }

    public void setUnloadingType(Byte unloadingType) {
        this.unloadingType = unloadingType;
    }

    public String getMethodLoad() {
        return methodLoad;
    }

    public void setMethodLoad(String methodLoad) {
        this.methodLoad = methodLoad;
    }

    public String getReasonOfRegistrationLateness() {
        return reasonOfRegistrationLateness;
    }

    public void setReasonOfRegistrationLateness(String reasonOfRegistrationLateness) {
        this.reasonOfRegistrationLateness = reasonOfRegistrationLateness;
    }

    public String getReasonOfEarlyStartAcceptance() {
        return reasonOfEarlyStartAcceptance;
    }

    public void setReasonOfEarlyStartAcceptance(String reasonOfEarlyStartAcceptance) {
        this.reasonOfEarlyStartAcceptance = reasonOfEarlyStartAcceptance;
    }
}