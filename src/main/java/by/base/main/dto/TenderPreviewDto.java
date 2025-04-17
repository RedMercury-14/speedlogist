package by.base.main.dto;

import java.time.LocalDate;

public class TenderPreviewDto {

    private Integer tenderId;
    private LocalDate dateLoadActual;
    private String routeDirection;
    private String cargo;
    private String truckType;
    private String weight;
    private String loadType;
    private String loadMethod;
    private String pallets;
    private String temperature;

    public Integer getTenderId() {
        return tenderId;
    }

    public void setTenderId(Integer tenderId) {
        this.tenderId = tenderId;
    }

    public LocalDate getDateLoadActual() {
        return dateLoadActual;
    }

    public void setDateLoadActual(LocalDate dateLoadActual) {
        this.dateLoadActual = dateLoadActual;
    }

    public String getRouteDirection() {
        return routeDirection;
    }

    public void setRouteDirection(String routeDirection) {
        this.routeDirection = routeDirection;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public String getPallets() {
        return pallets;
    }

    public void setPallets(String pallets) {
        this.pallets = pallets;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getLoadMethod() {
        return loadMethod;
    }

    public void setLoadMethod(String loadMethod) {
        this.loadMethod = loadMethod;
    }

    @Override
    public String toString() {
        return "TenderPreviewDto{" +
                "dateLoadActual=" + dateLoadActual +
                ", routeDirection='" + routeDirection + '\'' +
                ", cargo='" + cargo + '\'' +
                ", truckType='" + truckType + '\'' +
                ", weight='" + weight + '\'' +
                ", loadType='" + loadType + '\'' +
                ", loadMethod='" + loadMethod + '\'' +
                ", pallets='" + pallets + '\'' +
                ", temperature='" + temperature + '\'' +
                '}';
    }
}
