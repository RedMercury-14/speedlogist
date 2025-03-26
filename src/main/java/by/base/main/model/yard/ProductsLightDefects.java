package by.base.main.model.yard;


import javax.persistence.*;

@Entity
@Table(name = "products_light_defects")
public class ProductsLightDefects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_products_light_defects")
    private Long idProductsLightDefects;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product_for_quality", nullable = false)
    private ProductForQuality productForQuality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quality_light_defect", nullable = false)
    private QualityLightDefect qualityLightDefect;

    public Long getIdProductsLightDefects() {
        return idProductsLightDefects;
    }

    public void setIdProductsLightDefects(Long idProductsLightDefects) {
        this.idProductsLightDefects = idProductsLightDefects;
    }

    public ProductForQuality getProductForQuality() {
        return productForQuality;
    }

    public void setProductForQuality(ProductForQuality productForQuality) {
        this.productForQuality = productForQuality;
    }

    public QualityLightDefect getQualityLightDefect() {
        return qualityLightDefect;
    }

    public void setQualityLightDefect(QualityLightDefect qualityLightDefect) {
        this.qualityLightDefect = qualityLightDefect;
    }
}
