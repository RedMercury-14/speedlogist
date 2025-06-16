package by.base.main.dto.yard;

import by.base.main.model.yard.InternalDefectsQualityCard;
import by.base.main.model.yard.LightDefectsQualityCard;
import by.base.main.model.yard.TotalDefectQualityCard;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


public class AcceptanceQualityFoodCardDTO {

    private String firmNameAccept;

    private String carNumber;

    private String ttn;


    private Long idAcceptanceFoodQuality;
    private Long idAcceptanceQualityFoodCard;
    private Double cargoWeightCard;
    private Double sampleSize;
    private String productName;
    private String productType;
    private Integer classType;
    private Integer numberOfBrands;
    private String qualityOfProductPackaging;
    private Integer cardStatus;
    private String cardInfo;
    private String thermogram;
    private String bodyTemp;
    private String fruitTemp;
    private Integer appearanceEvaluation;
    private String appearanceDefects;
    private String maturityLevel;
    private String tasteQuality;
    private String caliber;
    private String stickerDescription;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp dateCard;
    
    private Boolean isImport;
    private String unit;
    private String managerPercent;
    private String type;
    private Long idMotherCard;
    
    private Double totalInternalDefectWeight;
    private Double totalInternalDefectPercentage;
    private Double totalDefectWeight;
    private Double totalDefectPercentage;
    private Double totalDefectPercentageWithPC;
    private Double totalLightDefectWeight;
    private Double totalLightDefectPercentage;
    private Double pcFactor;
    private Integer sampleSizeInternalDefect;
    


    private List<InternalDefectsQualityCard> internalDefectsQualityCardList;
    private List<LightDefectsQualityCard> lightDefectsQualityCardList;
    private List<TotalDefectQualityCard> totalDefectQualityCardList;

    private List<String> images;

    public Double getTotalInternalDefectWeight() {
		return totalInternalDefectWeight;
	}

	public void setTotalInternalDefectWeight(Double totalInternalDefectWeight) {
		this.totalInternalDefectWeight = totalInternalDefectWeight;
	}

	public Double getTotalInternalDefectPercentage() {
		return totalInternalDefectPercentage;
	}

	public void setTotalInternalDefectPercentage(Double totalInternalDefectPercentage) {
		this.totalInternalDefectPercentage = totalInternalDefectPercentage;
	}

	public Double getTotalDefectWeight() {
		return totalDefectWeight;
	}

	public void setTotalDefectWeight(Double totalDefectWeight) {
		this.totalDefectWeight = totalDefectWeight;
	}

	public Double getTotalDefectPercentage() {
		return totalDefectPercentage;
	}

	public void setTotalDefectPercentage(Double totalDefectPercentage) {
		this.totalDefectPercentage = totalDefectPercentage;
	}

	public Double getTotalDefectPercentageWithPC() {
		return totalDefectPercentageWithPC;
	}

	public void setTotalDefectPercentageWithPC(Double totalDefectPercentageWithPC) {
		this.totalDefectPercentageWithPC = totalDefectPercentageWithPC;
	}

	public Double getTotalLightDefectWeight() {
		return totalLightDefectWeight;
	}

	public void setTotalLightDefectWeight(Double totalLightDefectWeight) {
		this.totalLightDefectWeight = totalLightDefectWeight;
	}

	public Double getTotalLightDefectPercentage() {
		return totalLightDefectPercentage;
	}

	public void setTotalLightDefectPercentage(Double totalLightDefectPercentage) {
		this.totalLightDefectPercentage = totalLightDefectPercentage;
	}

	public Double getPcFactor() {
		return pcFactor;
	}

	public void setPcFactor(Double pcFactor) {
		this.pcFactor = pcFactor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getIdMotherCard() {
		return idMotherCard;
	}

	public void setIdMotherCard(Long idMotherCard) {
		this.idMotherCard = idMotherCard;
	}

	public String getManagerPercent() {
		return managerPercent;
	}

	public void setManagerPercent(String managerPercent) {
		this.managerPercent = managerPercent;
	}

	public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Long getIdAcceptanceFoodQuality() {
        return idAcceptanceFoodQuality;
    }

    public void setIdAcceptanceFoodQuality(Long idAcceptanceFoodQuality) {
        this.idAcceptanceFoodQuality = idAcceptanceFoodQuality;
    }

    public String getFirmNameAccept() {
        return firmNameAccept;
    }

    public void setFirmNameAccept(String firmNameAccept) {
        this.firmNameAccept = firmNameAccept;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }


    public String getTtn() {
        return ttn;
    }

    public void setTtn(String ttn) {
        this.ttn = ttn;
    }

    public Long getIdAcceptanceQualityFoodCard() {
        return idAcceptanceQualityFoodCard;
    }

    public void setIdAcceptanceQualityFoodCard(Long idAcceptanceQualityFoodCard) {
        this.idAcceptanceQualityFoodCard = idAcceptanceQualityFoodCard;
    }

    public Double getCargoWeightCard() {
        return cargoWeightCard;
    }

    public void setCargoWeightCard(Double cargoWeightCard) {
        this.cargoWeightCard = cargoWeightCard;
    }

    public Double getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Double sampleSize) {
        this.sampleSize = sampleSize;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getClassType() {
        return classType;
    }

    public void setClassType(Integer classType) {
        this.classType = classType;
    }

    public Integer getNumberOfBrands() {
        return numberOfBrands;
    }

    public void setNumberOfBrands(Integer numberOfBrands) {
        this.numberOfBrands = numberOfBrands;
    }

    public String getQualityOfProductPackaging() {
        return qualityOfProductPackaging;
    }

    public void setQualityOfProductPackaging(String qualityOfProductPackaging) {
        this.qualityOfProductPackaging = qualityOfProductPackaging;
    }

    public Integer getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(Integer cardStatus) {
        this.cardStatus = cardStatus;
    }

    public String getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(String cardInfo) {
        this.cardInfo = cardInfo;
    }

    public String getThermogram() {
        return thermogram;
    }

    public void setThermogram(String thermogram) {
        this.thermogram = thermogram;
    }

    public String getBodyTemp() {
        return bodyTemp;
    }

    public void setBodyTemp(String bodyTemp) {
        this.bodyTemp = bodyTemp;
    }

    public String getFruitTemp() {
        return fruitTemp;
    }

    public void setFruitTemp(String fruitTemp) {
        this.fruitTemp = fruitTemp;
    }

    public Integer getAppearanceEvaluation() {
        return appearanceEvaluation;
    }

    public void setAppearanceEvaluation(Integer appearanceEvaluation) {
        this.appearanceEvaluation = appearanceEvaluation;
    }

    public String getAppearanceDefects() {
        return appearanceDefects;
    }

    public void setAppearanceDefects(String appearanceDefects) {
        this.appearanceDefects = appearanceDefects;
    }

    public String getMaturityLevel() {
        return maturityLevel;
    }

    public void setMaturityLevel(String maturityLevel) {
        this.maturityLevel = maturityLevel;
    }

    public String getTasteQuality() {
        return tasteQuality;
    }

    public void setTasteQuality(String tasteQuality) {
        this.tasteQuality = tasteQuality;
    }

    public String getCaliber() {
        return caliber;
    }

    public void setCaliber(String caliber) {
        this.caliber = caliber;
    }

    public String getStickerDescription() {
        return stickerDescription;
    }

    public void setStickerDescription(String stickerDescription) {
        this.stickerDescription = stickerDescription;
    }

    public Timestamp getDateCard() {
        return dateCard;
    }

    public void setDateCard(Timestamp dateCard) {
        this.dateCard = dateCard;
    }
    public void setDateCard(LocalDateTime dateCard) {
    	this.dateCard = Timestamp.valueOf(dateCard);
    }

    public List<InternalDefectsQualityCard> getInternalDefectsQualityCardList() {
        return internalDefectsQualityCardList;
    }

    public void setInternalDefectsQualityCardList(List<InternalDefectsQualityCard> internalDefectsQualityCardList) {
        this.internalDefectsQualityCardList = internalDefectsQualityCardList;
    }

    public List<LightDefectsQualityCard> getLightDefectsQualityCardList() {
        return lightDefectsQualityCardList;
    }

    public void setLightDefectsQualityCardList(List<LightDefectsQualityCard> lightDefectsQualityCardList) {
        this.lightDefectsQualityCardList = lightDefectsQualityCardList;
    }

    public List<TotalDefectQualityCard> getTotalDefectQualityCardList() {
        return totalDefectQualityCardList;
    }

    public void setTotalDefectQualityCardList(List<TotalDefectQualityCard> totalDefectQualityCardList) {
        this.totalDefectQualityCardList = totalDefectQualityCardList;
    }

	public Boolean getIsImport() {
		return isImport;
	}

	public void setIsImport(Boolean isImport) {
		this.isImport = isImport;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

    public Integer getSampleSizeInternalDefect() {
        return sampleSizeInternalDefect;
    }

    public void setSampleSizeInternalDefect(Integer sampleSizeInternalDefect) {
        this.sampleSizeInternalDefect = sampleSizeInternalDefect;
    }
}
