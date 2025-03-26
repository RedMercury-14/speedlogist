package by.base.main.dto.yard;

import by.base.main.model.yard.*;


import java.util.List;

public class ProductsDefectsAndPartsDTO {
    private List<ProductsLightDefects> productsLightDefectsList;
    private List<ProductsRotDefects> productsRotDefectsList;
    private List<ProductForQuality> productForQualityList;
    private List<QualityLightDefect> qualityLightDefectList;
    private List<QualityRotDefect> qualityRotDefectList;

    public List<QualityRotDefect> getQualityRotDefectList() {
        return qualityRotDefectList;
    }

    public void setQualityRotDefectList(List<QualityRotDefect> qualityRotDefectList) {
        this.qualityRotDefectList = qualityRotDefectList;
    }

    public List<ProductsRotDefects> getProductsRotDefectsList() {
        return productsRotDefectsList;
    }

    public void setProductsRotDefectsList(List<ProductsRotDefects> productsRotDefectsList) {
        this.productsRotDefectsList = productsRotDefectsList;
    }

    public List<ProductsLightDefects> getProductsLightDefectsList() {
        return productsLightDefectsList;
    }

    public void setProductsLightDefectsList(List<ProductsLightDefects> productsLightDefectsList) {
        this.productsLightDefectsList = productsLightDefectsList;
    }

    public List<ProductForQuality> getProductForQualityList() {
        return productForQualityList;
    }

    public void setProductForQualityList(List<ProductForQuality> productForQualityList) {
        this.productForQualityList = productForQualityList;
    }

    public List<QualityLightDefect> getQualityLightDefectList() {
        return qualityLightDefectList;
    }

    public void setQualityLightDefectList(List<QualityLightDefect> qualityLightDefectList) {
        this.qualityLightDefectList = qualityLightDefectList;
    }
}
