package by.base.main.model.yard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

@Entity
@Table(name = "acceptance_quality_food_card_image_url")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcceptanceQualityFoodCardImageUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceptance_quality_food_card_image_url")
    private Long idAcceptanceQualityFoodCardImageUrl;

    @ManyToOne
    @JoinColumn(name = "id_acceptance_quality_food_card")
    @JsonIgnore
    private AcceptanceQualityFoodCard acceptanceQualityFoodCard;

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;


    public Long getIdAcceptanceQualityFoodCardImageUrl() {
        return idAcceptanceQualityFoodCardImageUrl;
    }

    public void setIdAcceptanceQualityFoodCardImageUrl(Long idAcceptanceQualityFoodCardImageUrl) {
        this.idAcceptanceQualityFoodCardImageUrl = idAcceptanceQualityFoodCardImageUrl;
    }

    public AcceptanceQualityFoodCard getAcceptanceQualityFoodCard() {
        return acceptanceQualityFoodCard;
    }

    public void setAcceptanceQualityFoodCard(AcceptanceQualityFoodCard acceptanceQualityFoodCard) {
        this.acceptanceQualityFoodCard = acceptanceQualityFoodCard;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

	@Override
	public String toString() {
		return "AcceptanceQualityFoodCardImageUrl [idAcceptanceQualityFoodCardImageUrl="
				+ idAcceptanceQualityFoodCardImageUrl + ", acceptanceQualityFoodCard=" + acceptanceQualityFoodCard
				+ ", url=" + url + "]";
	}
    
    
}
