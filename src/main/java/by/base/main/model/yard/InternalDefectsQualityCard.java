package by.base.main.model.yard;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

@Entity
@Table(name = "internal_defects_quality_card")
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalDefectsQualityCard implements DefectBase{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_internal_defects_quality_card")
    private Long idInternalDefectsQualityCard;

    @ManyToOne
    @JoinColumn(name = "id_acceptance_quality_food_card", updatable = false) //  запрет изменения карточки
    @JsonBackReference
    private AcceptanceQualityFoodCard acceptanceQualityFoodCard;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column (name = "percentage")
    private Double percentage;

    public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public AcceptanceQualityFoodCard getAcceptanceQualityFoodCard() {
        return acceptanceQualityFoodCard;
    }

    public void setAcceptanceQualityFoodCard(AcceptanceQualityFoodCard acceptanceQualityFoodCard) {
        this.acceptanceQualityFoodCard = acceptanceQualityFoodCard;
    }

    public Long getIdInternalDefectsQualityCard() {
        return idInternalDefectsQualityCard;
    }

    public void setIdInternalDefectsQualityCard(Long idInternalDefectsQualityCard) {
        this.idInternalDefectsQualityCard = idInternalDefectsQualityCard;
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
