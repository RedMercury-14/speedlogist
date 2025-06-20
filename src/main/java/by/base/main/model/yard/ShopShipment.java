package by.base.main.model.yard;

import javax.persistence.*;
import java.util.Objects;

//Для экспорта во двор сущности RouteHasShop
@Entity
@Table(name = "shop_shipment")
public class ShopShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_shop_shipment")
    private Long idShopShipment;

    @Column(name = "id_shop")
    private Integer idShop; //Shop

    @Column(name = "id_shipment")
    private Long idShipment; //Route

    @Column (name = "pallets_to_shop")
    private Integer palletsToShop; //pall

    @Column(name = "cargo_weight")
    private Double cargoWeight; //weight

    @Column(name = "ordinal_number")
    private Integer ordinalNumber;

    public Long getIdShopShipment() {
        return idShopShipment;
    }

    public void setIdShopShipment(Long idShopShipment) {
        this.idShopShipment = idShopShipment;
    }

    public Integer getIdShop() {
        return idShop;
    }

    public void setIdShop(Integer idShop) {
        this.idShop = idShop;
    }

    public Long getIdShipment() {
        return idShipment;
    }

    public void setIdShipment(Long idShipment) {
        this.idShipment = idShipment;
    }

    public Integer getPalletsToShop() {
        return palletsToShop;
    }

    public void setPalletsToShop(Integer palletsToShop) {
        this.palletsToShop = palletsToShop;
    }

    public Double getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(Double cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public Integer getOrdinalNumber() {
        return ordinalNumber;
    }

    public void setOrdinalNumber(Integer ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }


    @Override
    public String toString() {
        return "ShopShipment{" +
                "idShopShipment=" + idShopShipment +
                ", idShop=" + idShop +
                ", idShipment=" + idShipment +
                ", palletsToShop=" + palletsToShop +
                ", cargoWeight=" + cargoWeight +
                ", ordinalNumber=" + ordinalNumber +
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
        ShopShipment other = (ShopShipment) obj;
        return Objects.equals(idShopShipment, other.idShopShipment);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idShopShipment);
    }
}
