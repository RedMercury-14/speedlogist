package by.base.main.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "order_line")
public class OrderLine {
	
    // Уникальный идентификатор строки заказа (ID)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idorder_line")
    private Integer id;

    // Код товара (GoodsId)
    @Column(name = "goods_id")
    private Long goodsId;

    // Наименование товара (GoodsName)
    @Column(name = "goods_name", columnDefinition = "TEXT")
    private String goodsName;

    // Полное наименование товарной группы (GoodsGroupName)
    @Column(name = "goods_group_name", columnDefinition = "TEXT")
    private String goodsGroupName;

    // Штрих-код товара (Barcode)
    @Column(name = "barcode", columnDefinition = "TEXT")
    private String barcode;

    // Кол-во в упаковке (QuantityInPack)
    @Column(name = "quantity_pack")
    private Double quantityPack;

    // Кол-во в паллете (QuantityInPallet)
    @Column(name = "quantity_pallet")
    private Double quantityPallet;

    // Кол-во заказано (QuantityOrder)
    @Column(name = "quantity_order")
    private Double quantityOrder;

    // Связь с таблицей заказов (Order)
    @ManyToOne
    @JoinColumn(name = "order_idorder", nullable = false)
    @JsonBackReference
    private Order order;

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

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsGroupName() {
		return goodsGroupName;
	}

	public void setGoodsGroupName(String goodsGroupName) {
		this.goodsGroupName = goodsGroupName;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Double getQuantityPack() {
		return quantityPack;
	}

	public void setQuantityPack(Double quantityPack) {
		this.quantityPack = quantityPack;
	}

	public Double getQuantityPallet() {
		return quantityPallet;
	}

	public void setQuantityPallet(Double quantityPallet) {
		this.quantityPallet = quantityPallet;
	}

	public Double getQuantityOrder() {
		return quantityOrder;
	}

	public void setQuantityOrder(Double quantityOrder) {
		this.quantityOrder = quantityOrder;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderLine other = (OrderLine) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "OrderLine [id=" + id + ", goodsId=" + goodsId + ", goodsName=" + goodsName + ", goodsGroupName="
				+ goodsGroupName + ", barcode=" + barcode + ", quantityPack=" + quantityPack + ", quantityPallet="
				+ quantityPallet + ", quantityOrder=" + quantityOrder + ", order=" + order + "]";
	}
    
    
    

}
