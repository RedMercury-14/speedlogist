package by.base.main.model.yard;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "total_defect_quality_card")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TotalDefectQualityCard implements DefectBase{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_total_defect_quality_card")
    private Long idTotalDefectQualityCard;

    @ManyToOne
    @JoinColumn(name = "id_acceptance_quality_food_card", updatable = false) //  запрет изменения карточки
    @JsonBackReference
    private AcceptanceQualityFoodCard acceptanceQualityFoodCard;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "percentage_with_pc")
    private Double percentageWithPC;

    @Column (name = "pc_check")
    private Boolean pcCheck;

    

    public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public Double getPercentageWithPC() {
		return percentageWithPC;
	}

	public void setPercentageWithPC(Double percentageWithPC) {
		this.percentageWithPC = percentageWithPC;
	}

	public Boolean getPcCheck() {
		return pcCheck;
	}

	public void setPcCheck(Boolean pcCheck) {
		this.pcCheck = pcCheck;
	}

	public Long getIdTotalDefectQualityCard() {
        return idTotalDefectQualityCard;
    }

    public void setIdTotalDefectQualityCard(Long idTotalDefectQualityCard) {
        this.idTotalDefectQualityCard = idTotalDefectQualityCard;
    }

    public AcceptanceQualityFoodCard getAcceptanceQualityFoodCard() {
        return acceptanceQualityFoodCard;
    }

    public void setAcceptanceQualityFoodCard(AcceptanceQualityFoodCard acceptanceQualityFoodCard) {
        this.acceptanceQualityFoodCard = acceptanceQualityFoodCard;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
