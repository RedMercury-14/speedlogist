package by.base.main.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "order_calculation")
public class OrderCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idorder_calculation")
    private Integer id;

    @Column(name = "goods_id")
    private Long goodsId;

    @Column(name = "good_name")
    private String goodName;

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "counterparty_code")
    private Long counterpartyCode;

    @Column(name = "counterparty_name")
    private String counterpartyName;

    @Column(name = "counterparty_contract_code")
    private Long counterpartyContractCode;

    @Column(name = "num_stock")
    private Integer numStock;

    @Column(name = "quantity_order")
    private Double quantityOrder;

    @Column(name = "quantity_in_pallet")
    private Double quantityInPallet;

    @Column(name = "quantity_of_pallets")
    private Double quantityOfPallets;

    @Column(name = "good_group")
    private String goodGroup;

    public String getGoodGroup() {
        return goodGroup;
    }

    public void setGoodGroup(String goodGroup) {
        this.goodGroup = goodGroup;
    }

    @Column(name = "status")
    private Integer status;

    @Column(name = "history")
    private String history;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    public Set<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Set<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    @OneToMany (fetch=FetchType.LAZY, orphanRemoval = true,
            mappedBy="orderCalculation",
            cascade= {CascadeType.MERGE,
                    CascadeType.PERSIST})
    @JsonBackReference
    private Set<OrderProduct> orderProducts;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getCounterpartyCode() {
        return counterpartyCode;
    }

    public void setCounterpartyCode(Long counterpartyCode) {
        this.counterpartyCode = counterpartyCode;
    }

    public Long getCounterpartyContractCode() {
        return counterpartyContractCode;
    }

    public void setCounterpartyContractCode(Long counterpartyContractCode) {
        this.counterpartyContractCode = counterpartyContractCode;
    }

    public Integer getNumStock() {
        return numStock;
    }

    public void setNumStock(Integer numStock) {
        this.numStock = numStock;
    }

    public Double getQuantityOrder() {
        return quantityOrder;
    }

    public void setQuantityOrder(Double quantityOrder) {
        this.quantityOrder = quantityOrder;
    }

    public Double getQuantityInPallet() {
        return quantityInPallet;
    }

    public void setQuantityInPallet(Double quantityInPallet) {
        this.quantityInPallet = quantityInPallet;
    }

    public Double getQuantityOfPallets() {
        return quantityOfPallets;
    }

    public void setQuantityOfPallets(Double quantityOfPallets) {
        this.quantityOfPallets = quantityOfPallets;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }
}
