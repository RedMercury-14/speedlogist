package by.base.main.dto.yard;


import by.base.main.model.yard.QualityLightDefect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsDefectsDTO {
    private Long idProductForQuality;
    private String productName;
    private List<QualityLightDefect> qualityLightDefectsList;

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

    public List<QualityLightDefect> getQualityLightDefectsList() {
        return qualityLightDefectsList;
    }

    public void setQualityLightDefectsList(List<QualityLightDefect> qualityLightDefectsList) {
        this.qualityLightDefectsList = qualityLightDefectsList;
    }
}
