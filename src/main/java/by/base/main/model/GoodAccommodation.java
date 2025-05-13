package by.base.main.model;

import java.sql.Date;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "good_accommodation")
public class GoodAccommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idgood_accommodation")
    private Long idGoodAccommodation;

    @Column(name = "product_code")
    private Long productCode;

    @Column(name = "stocks")
    private String stocks;

    @Column(name = "status")
    private Integer status;
    
    @Column(name = "initiator_name")
    private String initiatorName;
    
    @Column(name = "initiator_email")
    private String initiatorEmail;
    
    @Column(name = "date_create")
    private Date dateCreate;
    
    @Column(name = "good_name")
    private String goodName;
    
    public GoodAccommodation() {};

    public GoodAccommodation(Long productCode, String stocks, Integer status,
			String initiatorName, String initiatorEmail, Date dateCreate, String goodName) {
		super();
		this.productCode = productCode;
		this.stocks = ";"+stocks;
		this.status = status;
		this.initiatorName = initiatorName;
		this.initiatorEmail = initiatorEmail;
		this.dateCreate = dateCreate;
		this.goodName = goodName;
	}

	public Long getIdGoodAccommodation() {
        return idGoodAccommodation;
    }

    public void setIdGoodAccommodation(Long idGoodAccommodation) {
        this.idGoodAccommodation = idGoodAccommodation;
    }

    public String getStocks() {
        return stocks;
    }

    public void setStocks(String stocks) {
        this.stocks = stocks;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public Long getProductCode() {
		return productCode;
	}

	public void setProductCode(Long productCode) {
		this.productCode = productCode;
	}

	public String getInitiatorName() {
		return initiatorName;
	}

	public void setInitiatorName(String initiatorName) {
		this.initiatorName = initiatorName;
	}

	public String getInitiatorEmail() {
		return initiatorEmail;
	}

	public void setInitiatorEmail(String initiatorEmail) {
		this.initiatorEmail = initiatorEmail;
	}

	public Date getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateCreate, goodName, idGoodAccommodation, initiatorEmail, initiatorName, productCode,
				status, stocks);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoodAccommodation other = (GoodAccommodation) obj;
		return Objects.equals(dateCreate, other.dateCreate) && Objects.equals(goodName, other.goodName)
				&& Objects.equals(idGoodAccommodation, other.idGoodAccommodation)
				&& Objects.equals(initiatorEmail, other.initiatorEmail)
				&& Objects.equals(initiatorName, other.initiatorName) && Objects.equals(productCode, other.productCode)
				&& Objects.equals(status, other.status) && Objects.equals(stocks, other.stocks);
	}

	@Override
	public String toString() {
		return "GoodAccommodation [idGoodAccommodation=" + idGoodAccommodation + ", productCode=" + productCode
				+ ", stocks=" + stocks + ", status=" + status + ", initiatorName=" + initiatorName + ", initiatorEmail="
				+ initiatorEmail + ", dateCreate=" + dateCreate + ", goodName=" + goodName + "]";
	}
    
}
