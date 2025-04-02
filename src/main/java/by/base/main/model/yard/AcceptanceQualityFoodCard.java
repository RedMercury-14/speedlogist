package by.base.main.model.yard;

import by.base.main.model.yard.AcceptanceFoodQuality;
import com.fasterxml.jackson.annotation.*;
import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "acceptance_quality_food_card")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcceptanceQualityFoodCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceptance_quality_food_card")
    private Long idAcceptanceQualityFoodCard;

    @ManyToOne
    @JoinColumn(name = "id_acceptance_food_quality")
//    @JsonBackReference
    @JsonIgnore
    private AcceptanceFoodQuality acceptanceFoodQuality;

    @Column(name = "cargo_weight_card")
    private Double cargoWeightCard;

    @Column(name = "sample_size")
    private Double sampleSize;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "class_type")
    private Integer classType;

    @OneToMany(mappedBy = "acceptanceQualityFoodCard", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference
    Set<InternalDefectsQualityCard> internalDefectsQualityCardList;

    @OneToMany(mappedBy = "acceptanceQualityFoodCard", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference
    Set<LightDefectsQualityCard> lightDefectsQualityCardList;

    @OneToMany(mappedBy = "acceptanceQualityFoodCard", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference
    Set<TotalDefectQualityCard> totalDefectQualityCardList;
    
    @OneToMany(mappedBy = "acceptanceQualityFoodCard", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference
    Set<AcceptanceQualityFoodCardImageUrl> acceptanceQualityFoodCardImageUrls;

    @Column(name = "number_of_brands")
    private Integer numberOfBrands;

    @Column(name = "quality_of_product_packaging", columnDefinition = "TEXT")
    private String qualityOfProductPackaging;

    @Column(name = "card_status")
    private Integer cardStatus;

    @Column(name = "card_info")
    private String cardInfo;

    @Column(name = "thermogram", columnDefinition = "TEXT")
    private String thermogram;

    @Column(name = "body_temp")
    private Double bodyTemp;

    @Column(name = "fruit_temp")
    private Double fruitTemp;

    @Column(name = "appearance_evaluation")
    private Integer appearanceEvaluation;

    @Column(name = "appearance_defects", columnDefinition = "TEXT")
    private String appearanceDefects;

    @Column(name = "maturity_level", columnDefinition = "TEXT")
    private String maturityLevel;

    @Column(name = "taste_quality", columnDefinition = "TEXT")
    private String tasteQuality;

    @Column(name = "caliber", columnDefinition = "TEXT")
    private String caliber;

    @Column(name = "sticker_description", columnDefinition = "TEXT")
    private String stickerDescription;

    @Column(name = "date_card")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCard;
    
    @Column(name = "login_manager_aproof", columnDefinition = "TEXT")
    private String loginManagerAproof;
    
    @Column(name = "fullname_manager_aproof", columnDefinition = "TEXT")
    private String fullnameManagerAproof;
    
    @Column(name = "id_manager_aproof")
    private Integer idManagerAproof;
    
    @Column(name = "date_time_aproof")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTimeAproof;
    
    @Column(name = "date_time_end_card")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTimeEndCard;
    
    @Column(name = "comment_aproof", columnDefinition = "TEXT")
    private String commentAproof;       

    // Getters and Setters


    public LocalDateTime getDateCard() {
        return dateCard;
    }

    public void setDateCard(LocalDateTime dateCard) {
        this.dateCard = dateCard;
    }

    public Long getIdAcceptanceQualityFoodCard() {
        return idAcceptanceQualityFoodCard;
    }

    public void setIdAcceptanceQualityFoodCard(Long idAcceptanceQualityFoodCard) {
        this.idAcceptanceQualityFoodCard = idAcceptanceQualityFoodCard;
    }

    public AcceptanceFoodQuality getAcceptanceFoodQuality() {
        return acceptanceFoodQuality;
    }

    public void setAcceptanceFoodQuality(AcceptanceFoodQuality acceptanceFoodQuality) {
        this.acceptanceFoodQuality = acceptanceFoodQuality;
    }

    public Set<InternalDefectsQualityCard> getInternalDefectsQualityCardList() {
        return internalDefectsQualityCardList;
    }

    public void setInternalDefectsQualityCardList(Set<InternalDefectsQualityCard> internalDefectsQualityCardList) {
        this.internalDefectsQualityCardList = internalDefectsQualityCardList;
    }

    public Set<LightDefectsQualityCard> getLightDefectsQualityCardList() {
        return lightDefectsQualityCardList;
    }

    public void setLightDefectsQualityCardList(Set<LightDefectsQualityCard> lightDefectsQualityCardList) {
        this.lightDefectsQualityCardList = lightDefectsQualityCardList;
    }

    public Set<TotalDefectQualityCard> getTotalDefectQualityCardList() {
        return totalDefectQualityCardList;
    }

    public Set<AcceptanceQualityFoodCardImageUrl> getAcceptanceQualityFoodCardImageUrls() {
		return acceptanceQualityFoodCardImageUrls;
	}

	public void setAcceptanceQualityFoodCardImageUrls(
			Set<AcceptanceQualityFoodCardImageUrl> acceptanceQualityFoodCardImageUrls) {
		this.acceptanceQualityFoodCardImageUrls = acceptanceQualityFoodCardImageUrls;
	}

	public void setTotalDefectQualityCardList(Set<TotalDefectQualityCard> totalDefectQualityCardList) {
        this.totalDefectQualityCardList = totalDefectQualityCardList;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
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

    public Double getBodyTemp() {
        return bodyTemp;
    }

    public void setBodyTemp(Double bodyTemp) {
        this.bodyTemp = bodyTemp;
    }

    public Double getFruitTemp() {
        return fruitTemp;
    }

    public void setFruitTemp(Double fruitTemp) {
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

    public String getLoginManagerAproof() {
		return loginManagerAproof;
	}

	public void setLoginManagerAproof(String loginManagerAproof) {
		this.loginManagerAproof = loginManagerAproof;
	}

	public String getFullnameManagerAproof() {
		return fullnameManagerAproof;
	}

	public void setFullnameManagerAproof(String fullnameManagerAproof) {
		this.fullnameManagerAproof = fullnameManagerAproof;
	}

	public Integer getIdManagerAproof() {
		return idManagerAproof;
	}

	public void setIdManagerAproof(Integer idManagerAproof) {
		this.idManagerAproof = idManagerAproof;
	}

	public LocalDateTime getDateTimeAproof() {
		return dateTimeAproof;
	}

	public void setDateTimeAproof(LocalDateTime dateTimeAproof) {
		this.dateTimeAproof = dateTimeAproof;
	}

	public LocalDateTime getDateTimeEndCard() {
		return dateTimeEndCard;
	}

	public void setDateTimeEndCard(LocalDateTime dateTimeEndCard) {
		this.dateTimeEndCard = dateTimeEndCard;
	}
	
	

	public String getCommentAproof() {
		return commentAproof;
	}

	public void setCommentAproof(String commentAproof) {
		this.commentAproof = commentAproof;
	}

	@Override
	public String toString() {
		return "AcceptanceQualityFoodCard [idAcceptanceQualityFoodCard=" + idAcceptanceQualityFoodCard
				+ ", productType=" + productType + ", cardStatus=" + cardStatus + ", cardInfo=" + cardInfo + "]";
	}

	
}
