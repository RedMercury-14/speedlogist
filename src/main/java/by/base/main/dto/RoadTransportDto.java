package by.base.main.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class RoadTransportDto {
    private Timestamp documentsArrived;
    private String importOrExport;
    private String routeId;
    private String requestId;
    private String supplier;
    private String requestInitiator;
    private String responsibleLogist;
    private Date dateRequestReceiving;
    private Timestamp cargoReadiness;
    private Timestamp loadingOnRequest;
    private LocalDate actualLoading;
    private String carrier;
    private String tenderParticipants;
    private String bid;
    private String bidCurrency;
    private String bidComment;
    private String truckNumber;
    private String truckType;
    private String temperature;
    private String UKZ;
    private String weight;
    private String unloadingWarehouse;

    public Timestamp getDocumentsArrived() {
        return documentsArrived;
    }

    public void setDocumentsArrived(Timestamp documentsArrived) {
        this.documentsArrived = documentsArrived;
    }

    public String getImportOrExport() {
        return importOrExport;
    }

    public void setImportOrExport(String importOrExport) {
        this.importOrExport = importOrExport;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getRequestInitiator() {
        return requestInitiator;
    }

    public void setRequestInitiator(String requestInitiator) {
        this.requestInitiator = requestInitiator;
    }

    public String getResponsibleLogist() {
        return responsibleLogist;
    }

    public void setResponsibleLogist(String responsibleLogist) {
        this.responsibleLogist = responsibleLogist;
    }

    public Date getDateRequestReceiving() {
        return dateRequestReceiving;
    }

    public void setDateRequestReceiving(Date dateRequestReceiving) {
        this.dateRequestReceiving = dateRequestReceiving;
    }

    public Timestamp getCargoReadiness() {
        return cargoReadiness;
    }

    public void setCargoReadiness(Timestamp cargoReadiness) {
        this.cargoReadiness = cargoReadiness;
    }

    public Timestamp getLoadingOnRequest() {
        return loadingOnRequest;
    }

    public void setLoadingOnRequest(Timestamp loadingOnRequest) {
        this.loadingOnRequest = loadingOnRequest;
    }

    public LocalDate getActualLoading() {
        return actualLoading;
    }

    public void setActualLoading(LocalDate actualLoading) {
        this.actualLoading = actualLoading;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTenderParticipants() {
        return tenderParticipants;
    }

    public void setTenderParticipants(String tenderParticipants) {
        this.tenderParticipants = tenderParticipants;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getBidCurrency() {
        return bidCurrency;
    }

    public void setBidCurrency(String bidCurrency) {
        this.bidCurrency = bidCurrency;
    }

    public String getBidComment() {
        return bidComment;
    }

    public void setBidComment(String bidComment) {
        this.bidComment = bidComment;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getUKZ() {
        return UKZ;
    }

    public void setUKZ(String UKZ) {
        this.UKZ = UKZ;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUnloadingWarehouse() {
        return unloadingWarehouse;
    }

    public void setUnloadingWarehouse(String unloadingWarehouse) {
        this.unloadingWarehouse = unloadingWarehouse;
    }
}
