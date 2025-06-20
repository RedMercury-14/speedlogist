package by.base.main.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "warehouse_management_data")
public class WarehouseManagementData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idwarehouse_management_data")
    private Long idRouteSheet;

    @Column(name = "num_stock")
    private Integer numStock;

    @Column(name = "pallets")
    private Double pallets;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "warehouse")
    private Integer warehouse;

    @Column(name = "date_create")
    private Date dateCreate;

    @Column(name = "date_task")
    private Date dateTask;

    public Long getIdRouteSheet() {
        return idRouteSheet;
    }

    public void setIdRouteSheet(Long idRouteSheet) {
        this.idRouteSheet = idRouteSheet;
    }

    public Integer getNumStock() {
        return numStock;
    }

    public void setNumStock(Integer numStock) {
        this.numStock = numStock;
    }

    public Double getPallets() {
        return pallets;
    }

    public void setPallets(Double pallets) {
        this.pallets = pallets;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Integer warehouse) {
        this.warehouse = warehouse;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Date getDateTask() {
        return dateTask;
    }

    public void setDateTask(Date dateTask) {
        this.dateTask = dateTask;
    }
}
