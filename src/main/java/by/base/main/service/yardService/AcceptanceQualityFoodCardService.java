package by.base.main.service.yardService;

import by.base.main.dto.yard.AcceptanceQualityFoodCardDTO;
import by.base.main.model.yard.AcceptanceFoodQuality;
import by.base.main.model.yard.AcceptanceQualityFoodCard;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface AcceptanceQualityFoodCardService {

    List<AcceptanceQualityFoodCard> getAllByAcceptanceFoodQuality(AcceptanceFoodQuality acceptanceFoodQuality);

    AcceptanceQualityFoodCard getByIdAcceptanceQualityFoodCard(Long idAcceptanceQualityFoodCard);

    List<AcceptanceQualityFoodCardDTO> getAllAcceptanceQualityFoodCard(Long idAcceptanceFoodQuality, HttpServletRequest request);
    
    int save(AcceptanceQualityFoodCard acceptanceQualityFoodCard);
    
    void update(AcceptanceQualityFoodCard acceptanceQualityFoodCard);
    
    /**
     * отдаёт карточки товаров по id карточки машины
     * @param idAcceptanceFoodQuality
     * @return
     */
    List<AcceptanceQualityFoodCard> getFoodCardByIdFoodQuality (Long idAcceptanceFoodQuality);

}
