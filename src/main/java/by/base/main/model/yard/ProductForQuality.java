package by.base.main.model.yard;


import javax.persistence.*;

@Entity
@Table(name = "product_for_quality")
public class ProductForQuality {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product_for_quality")
    private Long idProductForQuality;

    @Column(name = "product_name")
    private String productName;

    public Long getIdProductForQuality() {
        return idProductForQuality;
    }

    public void setIdProductForQuality(Long idProductForQuality) {
        this.idProductForQuality = idProductForQuality;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "ProductForQuality{" +
                "idProductForQuality=" + idProductForQuality +
                ", productName='" + productName + '\'' +
                '}';
    }
}
