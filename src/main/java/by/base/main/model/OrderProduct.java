package by.base.main.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Объект, в который записывается количество заказанного товара
 */
@Entity
@Table(name = "order_product")
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idorder_product")
    private Integer idOrderProduct;

    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "code_product")
    private Integer codeProduct;

    @Column(name = "date_create")
    private Timestamp dateCreate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "name_product")
    private String nameProduct;
    
    @Column(name = "quantity1700")
    private Integer quantity1700;
    
    @Column(name = "quantity1800")
    private Integer quantity1800;
    
    @Column(name = "quantity1800_max")
    private Integer quantity1800Max;
    
    @Column(name = "quantity1700_max")
    private Integer quantity1700Max;
    
    @Column(name = "quantity_in_pallet")
    private Integer quantityInPallet;
    
    @Column(name = "market_contract_type")
    private String marketContractType;

	@Column(name = "market_contract_type")
	private String marketContractType;

    @ManyToOne
    @JoinColumn(name = "product_idproduct", nullable = false)
    @JsonBackReference
    private Product product;
    
    

	public String getMarketContractType() {
		return marketContractType;
	}

	public void setMarketContractType(String marketContractType) {
		this.marketContractType = marketContractType;
	}

	@ManyToOne (cascade= {CascadeType.MERGE,
			CascadeType.PERSIST})
	@JoinColumn(name = "order_calculation_idorder_calculation")
	@JsonBackReference
	private OrderCalculation orderCalculation;

	public OrderCalculation getOrderCalculation() {
		return orderCalculation;
	}

	public void setOrderCalculation(OrderCalculation orderCalculation) {
		this.orderCalculation = orderCalculation;
	}

	public Integer getIdOrderProduct() {
		return idOrderProduct;
	}

	public void setIdOrderProduct(Integer idOrderProduct) {
		this.idOrderProduct = idOrderProduct;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Timestamp getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Timestamp dateCreate) {
		this.dateCreate = dateCreate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getNameProduct() {
		return nameProduct;
	}

	public void setNameProduct(String nameProduct) {
		this.nameProduct = nameProduct;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}


	public Integer getCodeProduct() {
		return codeProduct;
	}

	public void setCodeProduct(Integer codeProduct) {
		this.codeProduct = codeProduct;
	}

	public Integer getQuantity1700() {
		return quantity1700;
	}

	public void setQuantity1700(Integer quantity1700) {
		this.quantity1700 = quantity1700;
	}

	public Integer getQuantity1800() {
		return quantity1800;
	}

	public void setQuantity1800(Integer quantity1800) {
		this.quantity1800 = quantity1800;
	}

	public Integer getQuantity1800Max() {
		return quantity1800Max;
	}

	public void setQuantity1800Max(Integer quantity1800Max) {
		this.quantity1800Max = quantity1800Max;
	}

	public Integer getQuantity1700Max() {
		return quantity1700Max;
	}

	public void setQuantity1700Max(Integer quantity1700Max) {
		this.quantity1700Max = quantity1700Max;
	}

	public Integer getQuantityInPallet() {
		return quantityInPallet;
	}

	public void setQuantityInPallet(Integer quantityInPallet) {
		this.quantityInPallet = quantityInPallet;
	}


	public String getMarketContractType() {
		return marketContractType;
	}

	public void setMarketContractType(String marketContractType) {
		this.marketContractType = marketContractType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idOrderProduct);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderProduct other = (OrderProduct) obj;
		return Objects.equals(idOrderProduct, other.idOrderProduct);
	}

	@Override
	public String toString() {
		return "OrderProduct [idOrderProduct=" + idOrderProduct + ", quantity=" + quantity + ", codeProduct="
				+ codeProduct + ", dateCreate=" + dateCreate + ", comment=" + comment + ", nameProduct=" + nameProduct
				+ ", product=" + product + "]";
	}
	
}
