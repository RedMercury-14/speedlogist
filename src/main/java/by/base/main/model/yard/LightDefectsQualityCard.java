package by.base.main.model.yard;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

@Entity
@Table(name = "light_defects_quality_card")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LightDefectsQualityCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_light_defects_quality_card")
    private Long idLightDefectsQualityCard;

    @ManyToOne
    @JoinColumn(name = "id_acceptance_quality_food_card", updatable = false) //  запрет изменения карточки
    @JsonBackReference
    private AcceptanceQualityFoodCard acceptanceQualityFoodCard;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    public AcceptanceQualityFoodCard getAcceptanceQualityFoodCard() {
        return acceptanceQualityFoodCard;
    }

    public void setAcceptanceQualityFoodCard(AcceptanceQualityFoodCard acceptanceQualityFoodCard) {
        this.acceptanceQualityFoodCard = acceptanceQualityFoodCard;
    }

    public Long getIdLightDefectsQualityCard() {
        return idLightDefectsQualityCard;
    }

    public void setIdLightDefectsQualityCard(Long idLightDefectsQualityCard) {
        this.idLightDefectsQualityCard = idLightDefectsQualityCard;
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
