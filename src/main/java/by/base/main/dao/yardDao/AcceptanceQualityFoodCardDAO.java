package by.base.main.dao.yardDao;

import by.base.main.model.yard.AcceptanceFoodQuality;
import by.base.main.model.yard.AcceptanceQualityFoodCard;

import java.util.List;

public interface AcceptanceQualityFoodCardDAO {

    List<AcceptanceQualityFoodCard> getAllByAcceptanceFoodQuality(AcceptanceFoodQuality acceptanceFoodQuality);

    AcceptanceQualityFoodCard getByIdAcceptanceQualityFoodCard(Long idAcceptanceQualityFoodCard);
    
    int save(AcceptanceQualityFoodCard acceptanceQualityFoodCard);
    
    void update(AcceptanceQualityFoodCard acceptanceQualityFoodCard);
    
    /**
     * отдаёт карточки товаров по id карточки машины
     * @param idAcceptanceFoodQuality
     * @return
     */
    List<AcceptanceQualityFoodCard> getFoodCardByIdFoodQuality (Long idAcceptanceFoodQuality);

}
