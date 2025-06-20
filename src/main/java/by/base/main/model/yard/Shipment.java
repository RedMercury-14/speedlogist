package by.base.main.model.yard;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;


//Для экспорта во двор сущности Route
@Entity
@Table(name = "shipment")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_shipment")
    private Long idShipment;

    @Column(name = "route_value")
    private String routeValue; //RouteDirection

    @Column(name = "date_plan_ship")
    private Timestamp datePlanShip; //DateTask

    @Column(name = "firm_name")
    private String firmName; //Потому что не должено быть null

    public Long getIdShipment() {
        return idShipment;
    }

    public void setIdShipment(Long idShipment) {
        this.idShipment = idShipment;
    }

    public String getRouteValue() {
        return routeValue;
    }

    public void setRouteValue(String routeDirection) {
        this.routeValue = routeDirection;
    }

    public Timestamp getDatePlanShip() {
        return datePlanShip;
    }

    public void setDatePlanShip(Timestamp datePlanShip) {
        this.datePlanShip = datePlanShip;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "idShipment=" + idShipment +
                ", routeDirection='" + routeValue + '\'' +
                ", datePlanShip=" + datePlanShip +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Shipment other = (Shipment) obj;
        return Objects.equals(idShipment, other.idShipment);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idShipment);
    }
}
