package by.base.main.model.yard;

import javax.persistence.*;

@Entity
@Table(name = "products_rot_defects")
public class ProductsRotDefects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_products_rot_defects")
    private Long idProductsRotDefects;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product_for_quality", nullable = false)
    private ProductForQuality productForQuality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quality_rot_defect", nullable = false)
    private QualityRotDefect qualityRotDefect;

    public Long getIdProductsRotDefects() {
        return idProductsRotDefects;
    }

    public void setIdProductsRotDefects(Long idProductsRotDefects) {
        this.idProductsRotDefects = idProductsRotDefects;
    }

    public ProductForQuality getProductForQuality() {
        return productForQuality;
    }

    public void setProductForQuality(ProductForQuality productForQuality) {
        this.productForQuality = productForQuality;
    }

    public QualityRotDefect getQualityRotDefect() {
        return qualityRotDefect;
    }

    public void setQualityRotDefect(QualityRotDefect qualityRotDefect) {
        this.qualityRotDefect = qualityRotDefect;
    }
}
