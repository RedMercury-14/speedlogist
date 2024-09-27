package by.base.main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @ManyToOne
    @JoinColumn(name = "product_idproduct", nullable = false)
    @JsonBackReference
    private Product product;

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
